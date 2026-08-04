package com.chicken.taikhoan;

import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mohinh.ChickenNguoiDung;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** OTP mot lan cho xac minh email, khoi phuc mat khau va MFA admin. */
public final class ChickenXacMinhTaiKhoan {
    public static final String XAC_MINH_EMAIL = "EMAIL_VERIFY";
    public static final String DAT_LAI_MAT_KHAU = "PASSWORD_RESET";
    public static final String DANG_NHAP_ADMIN = "ADMIN_LOGIN";

    private static final SecureRandom NGAU_NHIEN = new SecureRandom();
    private static final int THOI_HAN_OTP_PHUT = 10;
    private static final int SO_LAN_THU = 5;
    private static byte[] pepper;

    private ChickenXacMinhTaiKhoan() {
    }

    public static synchronized void khoiTao(boolean batBuoc) {
        String giaTri = System.getenv("CHICKEN_OTP_PEPPER");
        if (giaTri == null
                || giaTri.getBytes(StandardCharsets.UTF_8).length < 32) {
            pepper = null;
            if (batBuoc) {
                throw new IllegalStateException(
                        "CHICKEN_OTP_PEPPER phai co it nhat 32 byte");
            }
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT] OTP pepper chua cau hinh; OTP dang tat");
            return;
        }
        pepper = giaTri.getBytes(StandardCharsets.UTF_8);
    }

    public static boolean sanSang() {
        return pepper != null && ChickenGuiEmail.sanSang();
    }

    public static void guiMaXacMinhEmail(int accountId, String email) {
        phatHanhVaGui(accountId, email, XAC_MINH_EMAIL,
                "Xac minh email Chicken LT");
    }

    public static void guiMaMfaAdmin(int accountId, String email) {
        phatHanhVaGui(accountId, email, DANG_NHAP_ADMIN,
                "Ma dang nhap quan tri Chicken LT");
    }

    public static void yeuCauDatLaiMatKhau(String danhTinh) {
        if (!sanSang()) {
            return;
        }
        TaiKhoanLienHe taiKhoan = timTaiKhoanLienHe(danhTinh, true);
        if (taiKhoan != null) {
            phatHanhVaGui(taiKhoan.id(), taiKhoan.email(),
                    DAT_LAI_MAT_KHAU,
                    "Dat lai mat khau Chicken LT");
        }
    }

    public static boolean xacMinhEmail(
            String tenDangNhap,
            String maOtp
    ) {
        String ten = ChickenBaoMatTaiKhoan
                .chuanHoaTenDangNhap(tenDangNhap);
        if (ten == null || !maOtpHopLe(maOtp) || pepper == null) {
            return false;
        }
        try (Connection conn = ChickenCoSoDuLieu.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int accountId = timIdTheoTen(conn, ten);
                if (accountId <= 0
                        || !tieuThuMa(conn, accountId,
                                XAC_MINH_EMAIL, maOtp)) {
                    // Giu lai viec tru attempts_left khi nhap sai OTP.
                    conn.commit();
                    return false;
                }
                try (PreparedStatement capNhat = conn.prepareStatement(
                        "UPDATE `accounts` SET `email_verified` = 1 "
                        + "WHERE `id` = ? LIMIT 1;")) {
                    capNhat.setInt(1, accountId);
                    if (capNhat.executeUpdate() != 1) {
                        throw new SQLException("Khong xac minh duoc email");
                    }
                }
                conn.commit();
                ChickenQuanLyMayChu.log(
                        "[BAO_MAT] Xac minh email account_id=" + accountId);
                return true;
            } catch (SQLException | RuntimeException ex) {
                rollback(conn, ex);
                throw ex;
            }
        } catch (SQLException | RuntimeException ex) {
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT] Loi xac minh email="
                    + ex.getClass().getSimpleName());
            return false;
        }
    }

    public static boolean datLaiMatKhau(
            String danhTinh,
            String maOtp,
            String matKhauMoi
    ) {
        if (!maOtpHopLe(maOtp) || pepper == null
                || ChickenBaoMatTaiKhoan.loiMatKhau(matKhauMoi) != null) {
            return false;
        }
        TaiKhoanLienHe taiKhoan = timTaiKhoanLienHe(danhTinh, true);
        if (taiKhoan == null) {
            return false;
        }
        try (Connection conn = ChickenCoSoDuLieu.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (!tieuThuMa(conn, taiKhoan.id(),
                        DAT_LAI_MAT_KHAU, maOtp)) {
                    // Giu lai viec tru attempts_left khi nhap sai OTP.
                    conn.commit();
                    return false;
                }
                String hash = ChickenBaoMatTaiKhoan.bamMatKhau(matKhauMoi);
                try (PreparedStatement capNhat = conn.prepareStatement(
                        "UPDATE `accounts` SET `password_hash` = ?, "
                        + "`failed_login_attempts` = 0, "
                        + "`locked_until` = NULL, "
                        + "`password_changed_at` = CURRENT_TIMESTAMP "
                        + "WHERE `id` = ? LIMIT 1;")) {
                    capNhat.setString(1, hash);
                    capNhat.setInt(2, taiKhoan.id());
                    if (capNhat.executeUpdate() != 1) {
                        throw new SQLException("Khong dat lai duoc mat khau");
                    }
                }
                huyMoiMaBaoMat(conn, taiKhoan.id());
                conn.commit();
                ChickenNguoiDung.ngatKetNoiSauKhiDatLaiMatKhau(
                        taiKhoan.tenDangNhap());
                ChickenQuanLyMayChu.log(
                        "[BAO_MAT] Dat lai mat khau account_id="
                        + taiKhoan.id());
                return true;
            } catch (SQLException | RuntimeException ex) {
                rollback(conn, ex);
                throw ex;
            }
        } catch (SQLException | RuntimeException ex) {
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT] Loi dat lai mat khau="
                    + ex.getClass().getSimpleName());
            return false;
        }
    }

    public static boolean xacMinhMfaAdmin(int accountId, String maOtp) {
        if (!maOtpHopLe(maOtp) || pepper == null) {
            return false;
        }
        try (Connection conn = ChickenCoSoDuLieu.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean hopLe = tieuThuMa(
                        conn, accountId, DANG_NHAP_ADMIN, maOtp);
                // Ca dung va sai deu commit: sai phai bi tru attempts_left.
                conn.commit();
                return hopLe;
            } catch (SQLException | RuntimeException ex) {
                rollback(conn, ex);
                throw ex;
            }
        } catch (SQLException | RuntimeException ex) {
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT] Loi MFA admin account_id=" + accountId);
            return false;
        }
    }

    private static void phatHanhVaGui(
            int accountId,
            String email,
            String mucDich,
            String tieuDe
    ) {
        if (!sanSang() || accountId <= 0 || email == null) {
            return;
        }
        String maOtp = String.format(Locale.ROOT, "%06d",
                NGAU_NHIEN.nextInt(1_000_000));
        long tokenId = luuMa(accountId, mucDich, maOtp);
        if (tokenId <= 0) {
            return;
        }
        String noiDung = "Ma xac minh Chicken LT cua ban la: " + maOtp
                + "\nMa co hieu luc " + THOI_HAN_OTP_PHUT
                + " phut va chi dung mot lan."
                + "\nNeu ban khong yeu cau, hay bo qua email nay.";
        ChickenGuiEmail.guiMa(accountId, email, tieuDe, noiDung)
                .thenAccept(thanhCong -> {
                    if (!thanhCong) {
                        huyMa(tokenId);
                    }
                });
    }

    private static long luuMa(
            int accountId,
            String mucDich,
            String maOtp
    ) {
        try (Connection conn = ChickenCoSoDuLieu.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ganDay = conn.prepareStatement(
                        "SELECT `created_at` FROM `account_security_tokens` "
                        + "WHERE `account_id` = ? AND `purpose` = ? "
                        + "AND `consumed_at` IS NULL "
                        + "ORDER BY `id` DESC LIMIT 1 FOR UPDATE;")) {
                    ganDay.setInt(1, accountId);
                    ganDay.setString(2, mucDich);
                    try (ResultSet ketQua = ganDay.executeQuery()) {
                        if (ketQua.next()
                                && System.currentTimeMillis()
                                - ketQua.getTimestamp(1).getTime()
                                < 60_000L) {
                            conn.rollback();
                            return -1L;
                        }
                    }
                }
                try (PreparedStatement huyCu = conn.prepareStatement(
                        "UPDATE `account_security_tokens` "
                        + "SET `consumed_at` = CURRENT_TIMESTAMP "
                        + "WHERE `account_id` = ? AND `purpose` = ? "
                        + "AND `consumed_at` IS NULL;")) {
                    huyCu.setInt(1, accountId);
                    huyCu.setString(2, mucDich);
                    huyCu.executeUpdate();
                }
                long id;
                try (PreparedStatement tao = conn.prepareStatement(
                        "INSERT INTO `account_security_tokens` "
                        + "(`account_id`, `purpose`, `token_hash`, "
                        + "`attempts_left`, `expires_at`) "
                        + "VALUES (?, ?, ?, ?, DATE_ADD(CURRENT_TIMESTAMP, "
                        + "INTERVAL ? MINUTE));",
                        Statement.RETURN_GENERATED_KEYS)) {
                    tao.setInt(1, accountId);
                    tao.setString(2, mucDich);
                    tao.setBytes(3, bamMa(accountId, mucDich, maOtp));
                    tao.setInt(4, SO_LAN_THU);
                    tao.setInt(5, THOI_HAN_OTP_PHUT);
                    tao.executeUpdate();
                    try (ResultSet khoa = tao.getGeneratedKeys()) {
                        if (!khoa.next()) {
                            throw new SQLException("Khong lay duoc id OTP");
                        }
                        id = khoa.getLong(1);
                    }
                }
                conn.commit();
                return id;
            } catch (SQLException | RuntimeException ex) {
                rollback(conn, ex);
                throw ex;
            }
        } catch (SQLException | RuntimeException ex) {
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT] Khong tao duoc OTP account_id="
                    + accountId);
            return -1L;
        }
    }

    private static boolean tieuThuMa(
            Connection conn,
            int accountId,
            String mucDich,
            String maOtp
    ) throws SQLException {
        long id;
        byte[] hashDaLuu;
        int soLanCon;
        try (PreparedStatement doc = conn.prepareStatement(
                "SELECT `id`, `token_hash`, `attempts_left` "
                + "FROM `account_security_tokens` "
                + "WHERE `account_id` = ? AND `purpose` = ? "
                + "AND `consumed_at` IS NULL "
                + "AND `expires_at` > CURRENT_TIMESTAMP "
                + "ORDER BY `id` DESC LIMIT 1 FOR UPDATE;")) {
            doc.setInt(1, accountId);
            doc.setString(2, mucDich);
            try (ResultSet ketQua = doc.executeQuery()) {
                if (!ketQua.next()) {
                    return false;
                }
                id = ketQua.getLong("id");
                hashDaLuu = ketQua.getBytes("token_hash");
                soLanCon = ketQua.getInt("attempts_left");
            }
        }
        byte[] hashNhan = bamMa(accountId, mucDich, maOtp);
        if (!MessageDigest.isEqual(hashDaLuu, hashNhan)) {
            int soLanConSauKhiSai = Math.max(0, soLanCon - 1);
            try (PreparedStatement tru = conn.prepareStatement(
                    "UPDATE `account_security_tokens` SET "
                    + "`attempts_left` = ?, "
                    + "`consumed_at` = CASE WHEN ? = 0 "
                    + "THEN CURRENT_TIMESTAMP ELSE `consumed_at` END "
                    + "WHERE `id` = ? LIMIT 1;")) {
                tru.setInt(1, soLanConSauKhiSai);
                tru.setInt(2, soLanConSauKhiSai);
                tru.setLong(3, id);
                tru.executeUpdate();
            }
            return false;
        }
        if (soLanCon <= 0) {
            return false;
        }
        try (PreparedStatement dung = conn.prepareStatement(
                "UPDATE `account_security_tokens` "
                + "SET `consumed_at` = CURRENT_TIMESTAMP "
                + "WHERE `id` = ? AND `consumed_at` IS NULL LIMIT 1;")) {
            dung.setLong(1, id);
            return dung.executeUpdate() == 1;
        }
    }

    private static TaiKhoanLienHe timTaiKhoanLienHe(
            String danhTinh,
            boolean canEmailDaXacMinh
    ) {
        if (danhTinh == null || danhTinh.length() > 254) {
            return null;
        }
        String giaTri = danhTinh.trim().toLowerCase(Locale.ROOT);
        String cot;
        String daChuanHoa;
        if (giaTri.contains("@")) {
            cot = "email";
            daChuanHoa = ChickenBaoMatTaiKhoan.chuanHoaEmail(giaTri);
        } else if (giaTri.startsWith("+")
                || giaTri.matches("^0[0-9].*")) {
            cot = "phone";
            daChuanHoa = ChickenBaoMatTaiKhoan
                    .chuanHoaSoDienThoai(giaTri);
        } else {
            cot = "username";
            daChuanHoa = ChickenBaoMatTaiKhoan
                    .chuanHoaTenDangNhap(giaTri);
        }
        if (daChuanHoa == null) {
            return null;
        }
        String sql = "SELECT `id`, `username`, `email`, `email_verified` "
                + "FROM `accounts` WHERE `" + cot + "` = ? LIMIT 1;";
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement doc = conn.prepareStatement(sql)) {
            doc.setString(1, daChuanHoa);
            try (ResultSet ketQua = doc.executeQuery()) {
                if (!ketQua.next()
                        || (canEmailDaXacMinh
                        && !ketQua.getBoolean("email_verified"))) {
                    return null;
                }
                return new TaiKhoanLienHe(
                        ketQua.getInt("id"),
                        ketQua.getString("username"),
                        ketQua.getString("email"));
            }
        } catch (SQLException ex) {
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT] Loi tim lien he khoi phuc");
            return null;
        }
    }

    private static int timIdTheoTen(Connection conn, String ten)
            throws SQLException {
        try (PreparedStatement doc = conn.prepareStatement(
                "SELECT `id` FROM `accounts` "
                + "WHERE `username` = ? LIMIT 1 FOR UPDATE;")) {
            doc.setString(1, ten);
            try (ResultSet ketQua = doc.executeQuery()) {
                return ketQua.next() ? ketQua.getInt(1) : -1;
            }
        }
    }

    private static byte[] bamMa(
            int accountId,
            String mucDich,
            String maOtp
    ) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(pepper, "HmacSHA256"));
            String noiDung = accountId + "|" + mucDich + "|" + maOtp;
            return hmac.doFinal(noiDung.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Khong tao duoc HMAC OTP", ex);
        }
    }

    private static boolean maOtpHopLe(String maOtp) {
        return maOtp != null && maOtp.matches("^[0-9]{6}$");
    }

    private static void huyMa(long tokenId) {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement huy = conn.prepareStatement(
                     "UPDATE `account_security_tokens` "
                     + "SET `consumed_at` = CURRENT_TIMESTAMP "
                     + "WHERE `id` = ? LIMIT 1;")) {
            huy.setLong(1, tokenId);
            huy.executeUpdate();
        } catch (SQLException ex) {
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT] Khong huy duoc OTP gui that bai");
        }
    }

    private static void huyMoiMaBaoMat(Connection conn, int accountId)
            throws SQLException {
        try (PreparedStatement huy = conn.prepareStatement(
                "UPDATE `account_security_tokens` "
                + "SET `consumed_at` = CURRENT_TIMESTAMP "
                + "WHERE `account_id` = ? AND `consumed_at` IS NULL;")) {
            huy.setInt(1, accountId);
            huy.executeUpdate();
        }
    }

    private static void rollback(Connection conn, Throwable loi) {
        try {
            conn.rollback();
        } catch (SQLException ex) {
            loi.addSuppressed(ex);
        }
    }

    private record TaiKhoanLienHe(
            int id,
            String tenDangNhap,
            String email
    ) {
    }
}
