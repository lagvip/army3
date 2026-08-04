package com.chicken.taikhoan;

import com.chicken.loi.ChickenCoSoDuLieu;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Quy tắc xác thực dùng chung. Mọi giá trị trả về từ client phải được chuẩn
 * hóa và kiểm tra tại server trước khi truy vấn hoặc ghi database.
 */
public final class ChickenBaoMatTaiKhoan {
    public static final int SO_LAN_DANG_NHAP_SAI_TOI_DA = 5;
    public static final int SO_PHUT_KHOA_DANG_NHAP = 15;

    private static final int DO_DAI_MAT_KHAU_TOI_DA = 72;
    private static final Pattern TEN_DANG_NHAP = Pattern.compile(
            "^[a-z0-9_]{5,24}$");
    private static final Pattern EMAIL = Pattern.compile(
            "^[a-z0-9.!#$%&'*+/=?^_`{|}~-]+"
            + "@[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?"
            + "(?:\\.[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?)+$");
    private static final Pattern SO_DIEN_THOAI_QUOC_TE =
            Pattern.compile("^\\+[1-9][0-9]{7,14}$");
    private static final Pattern SO_DIEN_THOAI_VIET_NAM =
            Pattern.compile("^0[35789][0-9]{8}$");
    private static final String HASH_GIA =
            BCrypt.hashpw("chicken-login-timing-placeholder",
                    BCrypt.gensalt(12));

    private ChickenBaoMatTaiKhoan() {
    }

    public static String chuanHoaTenDangNhap(String giaTri) {
        if (giaTri == null) {
            return null;
        }
        String ketQua = giaTri.trim().toLowerCase(Locale.ROOT);
        if (ketQua.startsWith("nvn_")
                || !TEN_DANG_NHAP.matcher(ketQua).matches()) {
            return null;
        }
        return ketQua;
    }

    public static String chuanHoaEmail(String giaTri) {
        if (giaTri == null) {
            return null;
        }
        String ketQua = giaTri.trim().toLowerCase(Locale.ROOT);
        if (ketQua.length() > 254 || !EMAIL.matcher(ketQua).matches()) {
            return null;
        }
        return ketQua;
    }

    /**
     * Lưu số điện thoại ở dạng E.164. Số Việt Nam 0xxxxxxxxx được đổi thành
     * +84xxxxxxxxx; số quốc tế phải có dấu +.
     */
    public static String chuanHoaSoDienThoai(String giaTri) {
        if (giaTri == null) {
            return null;
        }
        String ketQua = giaTri.trim()
                .replace(" ", "")
                .replace("-", "")
                .replace(".", "");
        if (SO_DIEN_THOAI_VIET_NAM.matcher(ketQua).matches()) {
            ketQua = "+84" + ketQua.substring(1);
        }
        return SO_DIEN_THOAI_QUOC_TE.matcher(ketQua).matches()
                ? ketQua : null;
    }

    public static String loiMatKhau(String matKhau) {
        if (matKhau == null
                || matKhau.length() < 8
                || matKhau.length() > DO_DAI_MAT_KHAU_TOI_DA
                || matKhau.getBytes(StandardCharsets.UTF_8).length
                > DO_DAI_MAT_KHAU_TOI_DA) {
            return "Mật khẩu phải dài từ 8 đến 72 ký tự.";
        }
        boolean coChuThuong = false;
        boolean coChuHoa = false;
        boolean coSo = false;
        for (int i = 0; i < matKhau.length(); i++) {
            char kyTu = matKhau.charAt(i);
            coChuThuong |= Character.isLowerCase(kyTu);
            coChuHoa |= Character.isUpperCase(kyTu);
            coSo |= Character.isDigit(kyTu);
            if (Character.isISOControl(kyTu)) {
                return "Mật khẩu chứa ký tự không hợp lệ.";
            }
        }
        if (!coChuThuong || !coChuHoa || !coSo) {
            return "Mật khẩu phải có chữ hoa, chữ thường và chữ số.";
        }
        return null;
    }

    public static String bamMatKhau(String matKhau) {
        return BCrypt.hashpw(matKhau, BCrypt.gensalt(12));
    }

    public static boolean laBcrypt(String giaTri) {
        return giaTri != null && (giaTri.startsWith("$2a$")
                || giaTri.startsWith("$2b$")
                || giaTri.startsWith("$2y$"));
    }

    public static boolean khopMatKhau(String matKhau, String giaTriDaLuu) {
        if (matKhau == null || giaTriDaLuu == null) {
            BCrypt.checkpw("", HASH_GIA);
            return false;
        }
        if (!laBcrypt(giaTriDaLuu)) {
            // Chỉ để di chuyển tài khoản cũ sau một lần đăng nhập thành công.
            return giaTriDaLuu.equals(matKhau);
        }
        try {
            return BCrypt.checkpw(matKhau, giaTriDaLuu);
        } catch (IllegalArgumentException loi) {
            BCrypt.checkpw("", HASH_GIA);
            return false;
        }
    }

    public static void taoDoTreKhiKhongCoTaiKhoan(String matKhau) {
        BCrypt.checkpw(matKhau == null ? "" : matKhau, HASH_GIA);
    }

    /**
     * Di chuyển một lần các mật khẩu plaintext của database cũ sang bcrypt.
     * UPDATE kèm giá trị cũ để không ghi đè nếu tài khoản vừa đổi mật khẩu.
     */
    public static void maHoaMatKhauCuTrongDatabase() {
        int daMaHoa = 0;
        try (java.sql.Connection conn =
                ChickenCoSoDuLieu.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement doc = conn.prepareStatement(
                    "SELECT `id`, `password_hash` FROM `accounts` "
                    + "FOR UPDATE;");
                 ResultSet ketQua = doc.executeQuery();
                 PreparedStatement ghi = conn.prepareStatement(
                         "UPDATE `accounts` SET `password_hash` = ? "
                         + "WHERE `id` = ? AND `password_hash` = ? "
                         + "LIMIT 1;")) {
                while (ketQua.next()) {
                    String giaTriCu = ketQua.getString("password_hash");
                    if (giaTriCu == null || giaTriCu.isEmpty()
                            || laBcrypt(giaTriCu)) {
                        continue;
                    }
                    ghi.setString(1, bamMatKhau(giaTriCu));
                    ghi.setInt(2, ketQua.getInt("id"));
                    ghi.setString(3, giaTriCu);
                    daMaHoa += ghi.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackLoi) {
                    ex.addSuppressed(rollbackLoi);
                }
                throw ex;
            }
        } catch (SQLException | RuntimeException ex) {
            Logger.getLogger(ChickenBaoMatTaiKhoan.class.getName()).log(
                    Level.SEVERE,
                    "Khong the ma hoa mat khau tai khoan cu", ex);
            throw new IllegalStateException(
                    "Khoi tao bao mat tai khoan that bai", ex);
        }
        if (daMaHoa > 0) {
            Logger.getLogger(ChickenBaoMatTaiKhoan.class.getName()).log(
                    Level.INFO,
                    "Da di chuyen {0} mat khau cu sang bcrypt",
                    daMaHoa);
        }
    }
}
