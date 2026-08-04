package com.chicken.mohinh;

import com.chicken.dichvu.ChickenQuanLyBietDoi;
import com.chicken.avg.ChickenQuanLyNangLuongAVG;
import com.chicken.chien.ChickenNapDanServer;
import com.chicken.chien.ChickenQuanLyDanSung;

import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenYeuCauCapVatPham;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mang.ChickenPhien;
import com.chicken.tienich.ChickenDuLieuJson;
import com.chicken.tienich.ChickenTienIch;
import com.chicken.tiemnang.ChickenQuanLyTiemNang;
import com.chicken.taikhoan.ChickenBaoMatTaiKhoan;
import com.chicken.taikhoan.ChickenXacMinhTaiKhoan;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChickenNguoiDung {
    enum TrangThaiTrangBiDaLuu {
        HOP_LE,
        THIEU_CAP,
        LOI_DU_LIEU
    }

    public static final ConcurrentHashMap<String, ChickenNguoiDung> users = new ConcurrentHashMap<>();
    private ChickenPhien khach;
    public ChickenDichVuGame dichVu;
    private int user_id;

    public int layMaTaiKhoan() {
        return this.user_id;
    }
    private String tenDangNhap;
    private boolean ban;
    private long lanDoiMatKhauMs;
    public ChickenNguoiChoi nguoiChoi;
    private static final int[] ID_TEMPLATE_BALO = new int[]{85, 90, 95, 100, 105};
    private static final int[] ID_TEMPLATE_BODY = new int[]{35, 40, 45, 50, 55};
    private static final int[] ID_TEMPLATE_LEG = new int[]{10, 15, 20, 25, 30};
    private static final int[] ID_TEMPLATE_WEAPON = new int[]{110, 120, 130, 140, 150, 160, 190, 200};
    private static final int[] ID_TEMPLATE_HEAD = new int[]{0, 1, 2, 3, 4};
    private static final int[] ID_TEMPLATE_HAT = new int[]{60, 65, 70, 75, 80};
    private static final String DEFAULT_STATS_JSON = "{\"power\":100,\"avenger\":100,\"kill\":0,\"dead\":1,\"assist\":0,\"trainingSuccess\":1,\"trainingWins\":0,\"busyHammer\":0,\"nHammer\":2,\"exp\":1000,\"rewardedLevel\":2,\"point\":20,\"pointAdd\":[1000,0,0,0,0,0]}";

    public ChickenNguoiDung(ChickenPhien khach, ChickenDichVuGame dichVu) {
        this.khach = khach;
        this.dichVu = dichVu;
    }

    public static ChickenNguoiDung timNguoiDungTheoTen(String ten) {
        return users.get(ten);
    }

    public static void ngatKetNoiSauKhiDatLaiMatKhau(String tenDangNhap) {
        if (tenDangNhap == null) {
            return;
        }
        ChickenNguoiDung dangOnline = users.get(tenDangNhap);
        if (dangOnline != null && dangOnline.khach != null) {
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT] Thu hoi phien sau reset mat khau account_id="
                    + dangOnline.user_id);
            dangOnline.khach.dangXuat();
        }
    }

    public static ChickenNguoiDung dangNhap(
            ChickenPhien s,
            String tenDangNhap,
            String matKhau,
            String phienBan,
            byte loai,
            String maMfa
    ) {
        ChickenNguoiDung us = new ChickenNguoiDung(s, (ChickenDichVuGame)s.layDichVu());
        String tenDaChuanHoa =
                ChickenBaoMatTaiKhoan.chuanHoaTenDangNhap(tenDangNhap);
        try {
            if (tenDaChuanHoa == null) {
                ChickenBaoMatTaiKhoan.taoDoTreKhiKhongCoTaiKhoan(matKhau);
                us.dichVu.moHopThoaiOK(
                        "Tài khoản hoặc mật khẩu không chính xác.");
                return null;
            }
            try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT `id`, `username`, `password_hash`, "
                         + "`is_banned`, `failed_login_attempts`, "
                         + "`locked_until`, `email`, `email_verified`, "
                         + "`is_admin` FROM `accounts` "
                         + "WHERE `username` = ? LIMIT 1;")) {
                stmt.setString(1, tenDaChuanHoa);
                try (ResultSet res = stmt.executeQuery()) {
                    if (!res.next()) {
                        ChickenBaoMatTaiKhoan
                                .taoDoTreKhiKhongCoTaiKhoan(matKhau);
                        us.dichVu.moHopThoaiOK(
                                "Tài khoản hoặc mật khẩu không chính xác.");
                        return null;
                    }
                    us.user_id = res.getInt("id");
                    us.ban = res.getBoolean("is_banned");
                    if (us.ban) {
                        us.dichVu.moHopThoaiOK("Tài khoản đã bị khóa.");
                        return null;
                    }
                    Timestamp khoaDen = res.getTimestamp("locked_until");
                    if (khoaDen != null
                            && khoaDen.getTime() > System.currentTimeMillis()) {
                        us.dichVu.moHopThoaiOK(
                                "Đăng nhập tạm khóa do sai mật khẩu nhiều lần. "
                                + "Vui lòng thử lại sau.");
                        return null;
                    }
                    String matKhauDaLuu = res.getString("password_hash");
                    if (!ChickenBaoMatTaiKhoan
                            .khopMatKhau(matKhau, matKhauDaLuu)) {
                        try (PreparedStatement capNhat =
                                conn.prepareStatement(
                                        "UPDATE `accounts` SET "
                                        + "`locked_until` = CASE WHEN "
                                        + "`failed_login_attempts` + 1 >= ? "
                                        + "THEN DATE_ADD(CURRENT_TIMESTAMP, "
                                        + "INTERVAL ? MINUTE) "
                                        + "ELSE `locked_until` END, "
                                        + "`failed_login_attempts` = "
                                        + "`failed_login_attempts` + 1 "
                                        + "WHERE `id` = ? LIMIT 1;")) {
                            capNhat.setInt(1, ChickenBaoMatTaiKhoan
                                    .SO_LAN_DANG_NHAP_SAI_TOI_DA);
                            capNhat.setInt(2, ChickenBaoMatTaiKhoan
                                    .SO_PHUT_KHOA_DANG_NHAP);
                            capNhat.setInt(3, us.user_id);
                            capNhat.executeUpdate();
                        }
                        us.dichVu.moHopThoaiOK(
                                "Tài khoản hoặc mật khẩu không chính xác.");
                        return null;
                    }
                    us.tenDangNhap = res.getString("username");
                    boolean emailDaXacMinh =
                            res.getBoolean("email_verified");
                    boolean laQuanTri = res.getBoolean("is_admin");
                    String email = res.getString("email");
                    if (ChickenQuanLyMayChu.batBuocXacMinhEmail()
                            && !emailDaXacMinh) {
                        us.dichVu.moHopThoaiOK(
                                "Tai khoan chua xac minh email.");
                        return null;
                    }
                    if (ChickenQuanLyMayChu.batBuocMfaQuanTri()
                            && laQuanTri) {
                        if (!emailDaXacMinh || email == null
                                || !ChickenXacMinhTaiKhoan.sanSang()) {
                            ChickenQuanLyMayChu.log(
                                    "[BAO_MAT] MFA admin chua san sang account_id="
                                    + us.user_id);
                            us.dichVu.moHopThoaiOK(
                                    "Dang nhap quan tri tam thoi khong kha dung.");
                            return null;
                        }
                        if (maMfa == null || maMfa.isEmpty()) {
                            ChickenXacMinhTaiKhoan.guiMaMfaAdmin(
                                    us.user_id, email);
                            s.guiYeuCauMfaAdmin();
                            return null;
                        }
                        if (!ChickenXacMinhTaiKhoan.xacMinhMfaAdmin(
                                us.user_id, maMfa)) {
                            us.dichVu.moHopThoaiOK(
                                    "Ma xac minh khong hop le hoac da het han.");
                            return null;
                        }
                    }
                    String hashMoi = ChickenBaoMatTaiKhoan
                            .laBcrypt(matKhauDaLuu)
                                    ? matKhauDaLuu
                                    : ChickenBaoMatTaiKhoan
                                            .bamMatKhau(matKhau);
                    try (PreparedStatement capNhat =
                            conn.prepareStatement(
                                    "UPDATE `accounts` SET "
                                    + "`password_hash` = ?, "
                                    + "`failed_login_attempts` = 0, "
                                    + "`locked_until` = NULL, "
                                    + "`last_login_at` = CURRENT_TIMESTAMP "
                                    + "WHERE `id` = ? LIMIT 1;")) {
                        capNhat.setString(1, hashMoi);
                        capNhat.setInt(2, us.user_id);
                        capNhat.executeUpdate();
                    }
                    ChickenNguoiDung user = users.putIfAbsent(us.tenDangNhap, us);
                    if (user != null) {
                        us.dichVu.moHopThoaiOK(
                                "Tài khoản đang được sử dụng.");
                        user.khach.guiMaPhien(0);
                        return null;
                    }
                    return us;
                }
            }
        }
        catch (Exception ex) {
            try {
                us.dichVu.moHopThoaiOK("Lỗi đăng nhập.");
            }
            catch (Exception exception) {
                }
            Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                    Level.WARNING, "Loi xu ly dang nhap", ex);
        }
        return null;
    }

    public static void dangNhap2(ChickenPhien s, String tenDangNhap) {
        try {
            ChickenDichVuGame dichVu = (ChickenDichVuGame)s.layDichVu();
            dichVu.moHopThoaiOK(
                    "Chế độ tài khoản khách đã tắt. "
                    + "Vui lòng đăng ký hoặc đăng nhập.");
        } catch (Exception ex) {
            Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                    Level.FINE, "Khong gui duoc thong bao tat guest", ex);
        }
    }

    public static DangKyKetQua dangKyTaiKhoan(
            String tenDangNhap,
            String matKhau,
            String email,
            String soDienThoai
    ) {
        String tenDaChuanHoa =
                ChickenBaoMatTaiKhoan.chuanHoaTenDangNhap(tenDangNhap);
        if (tenDaChuanHoa == null) {
            return new DangKyKetQua(false,
                    "Tên đăng nhập phải có 5-24 ký tự, "
                    + "chỉ gồm chữ thường, số hoặc dấu gạch dưới.");
        }
        String emailDaChuanHoa =
                ChickenBaoMatTaiKhoan.chuanHoaEmail(email);
        if (emailDaChuanHoa == null) {
            return new DangKyKetQua(false, "Địa chỉ email không hợp lệ.");
        }
        String soDaChuanHoa =
                ChickenBaoMatTaiKhoan.chuanHoaSoDienThoai(soDienThoai);
        if (soDaChuanHoa == null) {
            return new DangKyKetQua(false, "Số điện thoại không hợp lệ.");
        }
        String loiMatKhau = ChickenBaoMatTaiKhoan.loiMatKhau(matKhau);
        if (loiMatKhau != null) {
            return new DangKyKetQua(false, loiMatKhau);
        }

        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection()) {
            String hash = ChickenBaoMatTaiKhoan.bamMatKhau(matKhau);
            int accountId;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO `accounts` "
                    + "(`username`, `password_hash`, `email`, `phone`, "
                    + "`is_banned`, `is_online`, `is_admin`) "
                    + "VALUES (?, ?, ?, ?, 0, 0, 0);",
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, tenDaChuanHoa);
                stmt.setString(2, hash);
                stmt.setString(3, emailDaChuanHoa);
                stmt.setString(4, soDaChuanHoa);
                stmt.executeUpdate();
                try (ResultSet khoa = stmt.getGeneratedKeys()) {
                    if (!khoa.next()) {
                        throw new SQLException(
                                "Khong lay duoc id tai khoan moi");
                    }
                    accountId = khoa.getInt(1);
                }
            }
            boolean canXacMinh =
                    ChickenQuanLyMayChu.batBuocXacMinhEmail();
            if (canXacMinh) {
                ChickenXacMinhTaiKhoan.guiMaXacMinhEmail(
                        accountId, emailDaChuanHoa);
            }
            return new DangKyKetQua(true, "", canXacMinh);
        } catch (SQLException ex) {
            if ("23000".equals(ex.getSQLState())) {
                return new DangKyKetQua(false,
                        "Tên đăng nhập, email hoặc số điện thoại "
                        + "đã được sử dụng.");
            }
            Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                    Level.SEVERE, "Khong the dang ky tai khoan", ex);
            return new DangKyKetQua(false,
                    "Không thể đăng ký lúc này. Vui lòng thử lại sau.");
        }
    }

    public record DangKyKetQua(
            boolean thanhCong,
            String thongBao,
            boolean canXacMinhEmail
    ) {
        public DangKyKetQua(boolean thanhCong, String thongBao) {
            this(thanhCong, thongBao, false);
        }
    }

    /**
     * Doi mat khau trong mot phien da xac thuc.
     *
     * Client chi gui y dinh gom mat khau cu va mat khau moi. Server tu khoa
     * dong tai khoan, kiem tra hash cu va ghi hash moi trong cung giao dich.
     */
    public synchronized void doiMatKhau(ChickenTinNhan ms) {
        long hienTaiMs = System.currentTimeMillis();
        if (hienTaiMs - this.lanDoiMatKhauMs < 2_000L) {
            this.thongBaoDoiMatKhau(
                    "Vui long cho mot chut roi thu lai.");
            return;
        }
        this.lanDoiMatKhauMs = hienTaiMs;

        String matKhauCu;
        String matKhauMoi;
        try {
            matKhauCu = ms.boDoc().readUTF();
            matKhauMoi = ms.boDoc().readUTF();
            if (ms.boDoc().available() != 0
                    || matKhauCu.isEmpty()
                    || matKhauCu.length() > 72) {
                throw new IllegalArgumentException(
                        "Du lieu doi mat khau khong hop le");
            }
        } catch (IOException | RuntimeException ex) {
            this.khach.ghiNhanPacketLoi(81, ex);
            this.thongBaoDoiMatKhau(
                    "Du lieu doi mat khau khong hop le.");
            return;
        }

        String loiMatKhau =
                ChickenBaoMatTaiKhoan.loiMatKhau(matKhauMoi);
        if (loiMatKhau != null) {
            this.thongBaoDoiMatKhau(loiMatKhau);
            return;
        }
        if (matKhauCu.equals(matKhauMoi)) {
            this.thongBaoDoiMatKhau(
                    "Mat khau moi phai khac mat khau hien tai.");
            return;
        }

        try (java.sql.Connection conn =
                ChickenCoSoDuLieu.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String hashCu;
                try (PreparedStatement doc = conn.prepareStatement(
                        "SELECT `password_hash` FROM `accounts` "
                        + "WHERE `id` = ? FOR UPDATE;")) {
                    doc.setInt(1, this.user_id);
                    try (ResultSet ketQua = doc.executeQuery()) {
                        if (!ketQua.next()) {
                            throw new SQLException(
                                    "Tai khoan dang nhap khong ton tai");
                        }
                        hashCu = ketQua.getString("password_hash");
                    }
                }
                if (!ChickenBaoMatTaiKhoan
                        .khopMatKhau(matKhauCu, hashCu)) {
                    conn.rollback();
                    ChickenQuanLyMayChu.log(
                            "[BAO_MAT] Doi mat khau that bai user_id="
                            + this.user_id);
                    this.thongBaoDoiMatKhau(
                            "Mat khau hien tai khong chinh xac.");
                    return;
                }
                String hashMoi =
                        ChickenBaoMatTaiKhoan.bamMatKhau(matKhauMoi);
                try (PreparedStatement ghi = conn.prepareStatement(
                        "UPDATE `accounts` SET `password_hash` = ?, "
                        + "`failed_login_attempts` = 0, "
                        + "`locked_until` = NULL, "
                        + "`password_changed_at` = CURRENT_TIMESTAMP "
                        + "WHERE `id` = ? LIMIT 1;")) {
                    ghi.setString(1, hashMoi);
                    ghi.setInt(2, this.user_id);
                    if (ghi.executeUpdate() != 1) {
                        throw new SQLException(
                                "Khong cap nhat duoc mat khau");
                    }
                }
                try (PreparedStatement huyOtp = conn.prepareStatement(
                        "UPDATE `account_security_tokens` "
                        + "SET `consumed_at` = CURRENT_TIMESTAMP "
                        + "WHERE `account_id` = ? "
                        + "AND `consumed_at` IS NULL;")) {
                    huyOtp.setInt(1, this.user_id);
                    huyOtp.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException loiRollback) {
                    ex.addSuppressed(loiRollback);
                }
                throw ex;
            }
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT] Doi mat khau thanh cong user_id="
                    + this.user_id);
            this.khach.voHieuKhoiPhucSauDoiMatKhau();
            this.thongBaoDoiMatKhau(
                    "Doi mat khau thanh cong.");
        } catch (SQLException | RuntimeException ex) {
            Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                    Level.WARNING,
                    "Khong the doi mat khau user_id=" + this.user_id,
                    ex);
            this.thongBaoDoiMatKhau(
                    "Khong the doi mat khau luc nay. Vui long thu lai.");
        }
    }

    private void thongBaoDoiMatKhau(String noiDung) {
        try {
            this.dichVu.moHopThoaiOK(noiDung);
        } catch (IOException ex) {
            Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                    Level.FINE,
                    "Khong gui duoc ket qua doi mat khau user_id="
                    + this.user_id,
                    ex);
        }
    }

    public void taoNhanVat(ChickenTinNhan ms) {
        if (this.nguoiChoi != null) {
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT] Chan tao lai nhan vat accountId="
                    + this.user_id + " playerId=" + this.nguoiChoi.ma);
            try {
                this.dichVu.moHopThoaiOK(
                        "Tài khoản đã có nhân vật.");
            } catch (IOException ex) {
                Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                        Level.FINE,
                        "Khong gui duoc thong bao tao lai nhan vat accountId="
                        + this.user_id,
                        ex);
            }
            return;
        }
        try {
            String ten = ms.boDoc().readUTF().trim();
            short head = ms.boDoc().readShort();
            short leg = ms.boDoc().readShort();
            short body = ms.boDoc().readShort();
            short wing = ms.boDoc().readShort();
            short weapon = ms.boDoc().readShort();
            short hat = ms.boDoc().readShort();
            if (ms.boDoc().available() != 0) {
                this.dichVu.moHopThoaiOK(
                        "Dữ liệu tạo nhân vật không hợp lệ.");
                return;
            }
            if (ten.length() < 3 || ten.length() > 20
                    || !ten.matches("^[\\p{L}\\p{N}_ ]+$")
                    || ten.isBlank()) {
                this.dichVu.moHopThoaiOK(
                        "Tên nhân vật phải có 3-20 ký tự hợp lệ.");
                return;
            }

            int headId = timMaMauTheoPart(ID_TEMPLATE_HEAD, head);
            int legId = timMaMauTheoPart(ID_TEMPLATE_LEG, leg);
            int bodyId = timMaMauTheoPart(ID_TEMPLATE_BODY, body);
            int wingId = timMaMauTheoPart(ID_TEMPLATE_BALO, wing);
            int maVuKhi = timMaMauTheoPart(ID_TEMPLATE_WEAPON, weapon);
            int hatId = timMaMauTheoPart(ID_TEMPLATE_HAT, hat);
            if (headId < 0 || legId < 0 || bodyId < 0 || wingId < 0
                    || maVuKhi < 0 || hatId < 0) {
                this.dichVu.moHopThoaiOK(
                        "Trang bị tân thủ không hợp lệ.");
                return;
            }

            JSONArray trangBi = new JSONArray();
            ChickenVatPham balo = null;
            int[] cacMa = new int[]{
                headId, legId, bodyId, wingId, maVuKhi, hatId
            };
            for (int ma : cacMa) {
                ChickenVatPham vatPham = taoVatPhamMacDinh(ma);
                if (vatPham == null) {
                    this.dichVu.moHopThoaiOK(
                            "Trang bị tân thủ chưa được cấu hình.");
                    return;
                }
                trangBi.add(vatPham.toJSONObject());
                if (vatPham.mau.loai == 4) {
                    balo = vatPham;
                }
            }
            int dungLuongBalo =
                    ChickenNguoiChoi.layDungLuongBaloHopLe(balo);
            if (dungLuongBalo < 0) {
                this.dichVu.moHopThoaiOK(
                        "Balô tân thủ chưa được cấu hình.");
                return;
            }
            JSONArray tuiChien = new JSONArray();
            for (int i = 0; i < dungLuongBalo; i++) {
                tuiChien.add(-1);
            }

            try (java.sql.Connection conn =
                    ChickenCoSoDuLieu.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    try (PreparedStatement khoaTaiKhoan =
                            conn.prepareStatement(
                                    "SELECT `id` FROM `accounts` "
                                    + "WHERE `id` = ? FOR UPDATE;")) {
                        khoaTaiKhoan.setInt(1, this.user_id);
                        try (ResultSet ketQua =
                                khoaTaiKhoan.executeQuery()) {
                            if (!ketQua.next()) {
                                throw new SQLException(
                                        "Tai khoan khong ton tai");
                            }
                        }
                    }
                    try (PreparedStatement stmt =
                            conn.prepareStatement(
                                    "INSERT INTO `players` "
                                    + "(`account_id`, `name`, `gold`, `cup`, "
                                    + "`gem`, `stats_json`, `inventory_json`, "
                                    + "`equipped_json`, `pocket_json`, "
                                    + "`storage_json`) "
                                    + "VALUES (?, ?, ?, 0, ?, ?, '[]', ?, ?, "
                                    + "'[]');")) {
                        stmt.setInt(1, this.user_id);
                        stmt.setString(2, ten);
                        stmt.setInt(3, 1_000_000);
                        stmt.setInt(4, 1_000);
                        stmt.setString(5, DEFAULT_STATS_JSON);
                        stmt.setString(6, trangBi.toJSONString());
                        stmt.setString(7, tuiChien.toJSONString());
                        stmt.executeUpdate();
                    }
                    conn.commit();
                } catch (SQLException | RuntimeException ex) {
                    try {
                        conn.rollback();
                    } catch (SQLException loiRollback) {
                        ex.addSuppressed(loiRollback);
                    }
                    throw ex;
                }
            }
            if (!this.taiDuLieuNguoiChoi()
                    || this.nguoiChoi == null) {
                throw new SQLException(
                        "Khong tai lai duoc nhan vat vua tao");
            }
            this.khach.guiThongTin();
        } catch (SQLException ex) {
            try {
                if ("23000".equals(ex.getSQLState())) {
                    this.dichVu.moHopThoaiOK(
                            "Tài khoản đã có nhân vật "
                            + "hoặc tên đã được sử dụng.");
                } else {
                    Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                            Level.SEVERE, "Loi tao nhan vat", ex);
                    this.dichVu.moHopThoaiOK(
                            "Không thể tạo nhân vật lúc này.");
                }
            } catch (Exception guiLoi) {
                ex.addSuppressed(guiLoi);
            }
        } catch (Exception ex) {
            Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                    Level.WARNING, "Packet tao nhan vat khong hop le", ex);
            try {
                this.dichVu.moHopThoaiOK(
                        "Dữ liệu tạo nhân vật không hợp lệ.");
            } catch (Exception boQua) {
                // Kết nối có thể đã đóng.
            }
        }
    }

    private static int timMaMauTheoPart(int[] cacMa, short part) {
        for (int ma : cacMa) {
            ChickenMauVatPham mau =
                    ChickenQuanLyMayChu.itemTemplates.get(ma);
            if (mau != null && mau.part == part) {
                return mau.ma;
            }
        }
        return -1;
    }

    private static ChickenVatPham taoVatPhamMacDinh(int ma) {
        ChickenVatPham vatPham = new ChickenVatPham(ma);
        if (vatPham.mau == null
                || vatPham.mau.loai < 0
                || vatPham.mau.loai >= 6) {
            return null;
        }
        vatPham.chiSo = vatPham.mau.loai;
        vatPham.itemOptions = vatPham.mau.thuocTinhs;
        return vatPham;
    }

    private void taoNhanVatCuKhongDung(ChickenTinNhan ms) {
        try {
            String ten = ms.boDoc().readUTF();
            short head = ms.boDoc().readShort();
            short leg = ms.boDoc().readShort();
            short body = ms.boDoc().readShort();
            short wing = ms.boDoc().readShort();
            short weapon = ms.boDoc().readShort();
            short hat = ms.boDoc().readShort();
            System.out.println("head: " + head);
            System.out.println("body: " + body);
            System.out.println("leg: " + leg);
            System.out.println("wing: " + wing);
            System.out.println("wp: " + weapon);
            System.out.println("hat: " + hat);
            if (ten.equals("")) {
                this.dichVu.moHopThoaiOK("Tên không hợp lệ!");
                return;
            }
            try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `name` = ?;")) {
                stmt.setString(1, ten);
                try (ResultSet res = stmt.executeQuery()) {
                    if (res.next()) {
                        this.dichVu.moHopThoaiOK("Tên nhân vật đã tồn tại.");
                        return;
                    }
                }
                try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO `players` (`account_id`, `name`, `gold`, `cup`, `gem`, `stats_json`, `inventory_json`, `equipped_json`, `pocket_json`, `storage_json`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                    stmt2.setInt(1, this.user_id);
                    stmt2.setString(2, ten);
                    stmt2.setInt(3, 1000000);
                    stmt2.setInt(4, 0);
                    stmt2.setInt(5, 1000);
                    stmt2.setString(6, DEFAULT_STATS_JSON);
                    stmt2.setString(7, "[]");
                    stmt2.setString(8, "[]");
                    stmt2.setString(9, "[]");
                    stmt2.setString(10, "[]");
                    stmt2.executeUpdate();
                }
                try (PreparedStatement stmt3 = conn.prepareStatement("SELECT * FROM `players` WHERE `account_id` = ? LIMIT 1;")) {
                    stmt3.setInt(1, this.user_id);
                    try (ResultSet res2 = stmt3.executeQuery()) {
                        if (res2.next()) {
                            this.nguoiChoi = new ChickenNguoiChoi(this.dichVu);
                            this.nguoiChoi.ma = res2.getInt("id");
                            this.nguoiChoi.ten = ten;
                            this.nguoiChoi.vang = res2.getInt("gold");
                            this.nguoiChoi.ngoc = res2.getInt("gem");
                            this.nguoiChoi.cup = res2.getInt("cup");
                            this.nguoiChoi.x = (short)70;
                            this.nguoiChoi.y = (short)360;
                            this.nguoiChoi.head = head;
                            this.nguoiChoi.hat = hat;
                            this.nguoiChoi.leg = leg;
                            this.nguoiChoi.body = body;
                            this.nguoiChoi.wing = wing;
                            this.nguoiChoi.wp = weapon;
                            JSONObject stats = (JSONObject)JSON.parse(res2.getString("stats_json"));
                            ChickenDuLieuJson p = new ChickenDuLieuJson(stats);
                            int expTai = p.getInt("exp");
                            int capTai = ChickenTienIch.layCap(expTai);
                            int mocTai = p.containsKey("rewardedLevel")
                                    ? Math.max(0, p.getInt("rewardedLevel"))
                                    : capTai;
                            JSONArray jArr = p.getJSONArray("pointAdd");
                            short[] diemTai = new short[6];
                            for (int i = 0; i < 6; ++i) {
                                diemTai[i] = Short.parseShort(
                                        jArr.get(i).toString());
                            }
                            this.nguoiChoi.napTrangThaiTiemNangTuKho(
                                    expTai, mocTai, p.getShort("point"),
                                    diemTai, res2.getLong("stats_revision"));
                            this.nguoiChoi.napPhienBanKhoVatPham(
                                    res2.getLong("inventory_revision"));
                            this.nguoiChoi.trainingSuccess = p.getByte("trainingSuccess");
                            this.nguoiChoi.datSoTranThangLuyenTap(p.containsKey("trainingWins") ? p.getInt("trainingWins") : 0);
                            this.nguoiChoi.busyHammer = p.getByte("busyHammer");
                            this.nguoiChoi.nHammer = p.getByte("nHammer");
                            this.nguoiChoi.kill = p.getInt("kill");
                            this.nguoiChoi.chet = p.getInt("dead");
                            this.nguoiChoi.assist = p.getInt("assist");
                this.nguoiChoi.daNhanThanhTich = p.containsKey("achievementClaims") ? p.getInt("achievementClaims") : 0;
                            this.nguoiChoi.powerAvenger = ChickenQuanLyNangLuongAVG.chuanHoaGiaTri(
                                    p.containsKey("avenger")
                                            ? p.getInt("avenger")
                                            : ChickenQuanLyNangLuongAVG.NANG_LUONG_TOI_DA
                            );
                            this.nguoiChoi.power = p.getByte("power");
                            int headId = -1;
                            int legId = -1;
                            int bodyId = -1;
                            int wingId = -1;
                            int maVuKhi = -1;
                            int hatId = -1;
                            for (int ma : ID_TEMPLATE_BODY) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == body) {
                                    bodyId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_LEG) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == leg) {
                                    legId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_WEAPON) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == weapon) {
                                    maVuKhi = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_BALO) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == wing) {
                                    wingId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_HEAD) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == head) {
                                    headId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_HAT) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == hat) {
                                    hatId = t.ma;
                                    break;
                                }
                            }
                            ChickenVatPham vatPham = new ChickenVatPham(headId);
                            vatPham.chiSo = vatPham.mau.loai;
                            vatPham.itemOptions = vatPham.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[vatPham.chiSo] = vatPham;
                            ChickenVatPham item2 = new ChickenVatPham(legId);
                            item2.chiSo = item2.mau.loai;
                            item2.itemOptions = item2.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item2.chiSo] = item2;
                            ChickenVatPham item3 = new ChickenVatPham(bodyId);
                            item3.chiSo = item3.mau.loai;
                            item3.itemOptions = item3.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item3.chiSo] = item3;
                            ChickenVatPham item4 = new ChickenVatPham(wingId);
                            item4.chiSo = item4.mau.loai;
                            item4.itemOptions = item4.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item4.chiSo] = item4;
                            int thamSo = ChickenNguoiChoi
                                    .layDungLuongBaloHopLe(item4);
                            if (thamSo < 0) {
                                throw new SQLException(
                                        "Template balo mac dinh khong hop le");
                            }
                            this.nguoiChoi.itemBalo = new int[thamSo];
                            for (int i = 0; i < thamSo; ++i) {
                                this.nguoiChoi.itemBalo[i] = -1;
                            }
                            ChickenVatPham item5 = new ChickenVatPham(maVuKhi);
                            item5.chiSo = item5.mau.loai;
                            item5.itemOptions = item5.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item5.chiSo] = item5;
                            ChickenVatPham item6 = new ChickenVatPham(hatId);
                            item6.chiSo = item6.mau.loai;
                            item6.itemOptions = item6.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item6.chiSo] = item6;
                            this.nguoiChoi.dichVu.datNguoiChoi(this.nguoiChoi);
                            this.khach.guiThongTin();
                        } else {
                            this.dichVu.moHopThoaiOK("Có lỗi xảy ra.");
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(ChickenNguoiDung.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean taiDuLieuNguoiChoi() {
        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `account_id` = ? LIMIT 1;")) {
            stmt.setInt(1, this.user_id);
            ResultSet res = stmt.executeQuery();
            if (res != null && res.next()) {
                this.nguoiChoi = new ChickenNguoiChoi(this.dichVu);
                this.nguoiChoi.ma = res.getInt("id");
                this.nguoiChoi.ten = res.getString("name");
                this.nguoiChoi.vang = res.getInt("gold");
                this.nguoiChoi.ngoc = res.getInt("gem");
                this.nguoiChoi.cup = res.getInt("cup");
                this.nguoiChoi.x = (short)70;
                this.nguoiChoi.y = (short)360;
                this.nguoiChoi.head = (short)-1;
                this.nguoiChoi.hat = (short)-1;
                this.nguoiChoi.leg = (short)-1;
                this.nguoiChoi.body = (short)-1;
                this.nguoiChoi.wing = (short)-1;
                this.nguoiChoi.wp = (short)-1;
                ChickenDuLieuJson p = new ChickenDuLieuJson((JSONObject)JSON.parse(res.getString("stats_json")));
                int expTai = p.getInt("exp");
                int capTai = ChickenTienIch.layCap(expTai);
                int mocTai = p.containsKey("rewardedLevel")
                        ? Math.max(0, p.getInt("rewardedLevel"))
                        : capTai;
                JSONArray jArr = p.getJSONArray("pointAdd");
                short[] diemTai = new short[6];
                for (int i = 0; i < 6; ++i) {
                    diemTai[i] = Short.parseShort(
                            jArr.get(i).toString());
                }
                this.nguoiChoi.napTrangThaiTiemNangTuKho(
                        expTai, mocTai, p.getShort("point"),
                        diemTai, res.getLong("stats_revision"));
                this.nguoiChoi.napPhienBanKhoVatPham(
                        res.getLong("inventory_revision"));
                this.nguoiChoi.trainingSuccess = p.getByte("trainingSuccess");
                this.nguoiChoi.datSoTranThangLuyenTap(p.containsKey("trainingWins") ? p.getInt("trainingWins") : 0);
                this.nguoiChoi.busyHammer = p.getByte("busyHammer");
                this.nguoiChoi.nHammer = p.getByte("nHammer");
                this.nguoiChoi.kill = p.getInt("kill");
                this.nguoiChoi.chet = p.getInt("dead");
                this.nguoiChoi.assist = p.getInt("assist");
                this.nguoiChoi.daNhanThanhTich = p.containsKey("achievementClaims") ? p.getInt("achievementClaims") : 0;
                this.nguoiChoi.powerAvenger = ChickenQuanLyNangLuongAVG.chuanHoaGiaTri(
                                    p.containsKey("avenger")
                                            ? p.getInt("avenger")
                                            : ChickenQuanLyNangLuongAVG.NANG_LUONG_TOI_DA
                            );
                this.nguoiChoi.power = p.getByte("power");
                boolean khoCanDongBo = false;
                java.util.ArrayList<ChickenVatPham> vatPhamCachLy =
                        new java.util.ArrayList<>();
                JSONArray bags = (JSONArray)JSON.parse(res.getString("inventory_json"));
                for (int i = 0; i < bags.size(); ++i) {
                    ChickenVatPham vatPham = new ChickenVatPham((JSONObject)bags.get(i));
                    if (vatPham.mau != null
                            && vatPham.soLuong > 0
                            && vatPham.chiSo >= 0
                            && vatPham.chiSo < this.nguoiChoi.itemBag.length
                            && this.nguoiChoi.itemBag[vatPham.chiSo] == null) {
                        this.nguoiChoi.itemBag[vatPham.chiSo] = vatPham;
                    } else {
                        khoCanDongBo = true;
                        if (vatPham.mau != null && vatPham.soLuong > 0) {
                            vatPhamCachLy.add(vatPham);
                        }
                        Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                                Level.WARNING,
                                "Cach ly vat pham tui co chi so khong hop le/bi trung: player={0}, index={1}",
                                new Object[]{this.nguoiChoi.ma, vatPham.chiSo}
                        );
                    }
                }
                JSONArray bodys = (JSONArray)JSON.parse(res.getString("equipped_json"));
                for (int i = 0; i < bodys.size(); ++i) {
                    ChickenVatPham vatPham = new ChickenVatPham((JSONObject)bodys.get(i));
                    TrangThaiTrangBiDaLuu trangThaiTrangBi =
                            danhGiaTrangBiDaLuu(
                                    this.nguoiChoi, vatPham);
                    if (trangThaiTrangBi
                            == TrangThaiTrangBiDaLuu.LOI_DU_LIEU) {
                        Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                                Level.WARNING,
                                "Chuyen trang bi loi ve tui: player={0}, item={1}, index={2}",
                                new Object[]{this.nguoiChoi.ma, vatPham.ma,
                                    vatPham.chiSo}
                        );
                        if (vatPham.mau != null && vatPham.soLuong > 0) {
                            vatPham.soLuong = 1;
                            vatPhamCachLy.add(vatPham);
                        }
                        khoCanDongBo = true;
                        continue;
                    }
                    this.nguoiChoi.itemBody[vatPham.chiSo] = vatPham;
                    if (trangThaiTrangBi
                            == TrangThaiTrangBiDaLuu.HOP_LE) {
                        this.nguoiChoi.datTrangBiChoNhanVat(vatPham);
                    } else {
                        /*
                         * Tut cap khong lam hong item va khong duoc bien
                         * thanh loi tai kho. Giu nguyen item trong equipped
                         * de khong can o trong tui/ruong; cac lop chi so,
                         * sung, AVG va vat pham chien deu kiem tra cap truoc
                         * khi cap quyen su dung.
                         */
                        Logger.getLogger(
                                ChickenNguoiDung.class.getName()).log(
                                Level.INFO,
                                "Giu trang bi thieu cap tren nguoi: player={0}, item={1}, required={2}, level={3}",
                                new Object[]{this.nguoiChoi.ma, vatPham.ma,
                                    vatPham.mau.strRequire,
                                    this.nguoiChoi.cap}
                        );
                    }
                    if (vatPham.mau.loai == 4) {
                        int thamSo = ChickenNguoiChoi
                                .layDungLuongBaloHopLe(vatPham);
                        this.nguoiChoi.itemBalo = new int[thamSo];
                        for (int a = 0; a < thamSo; ++a) {
                            this.nguoiChoi.itemBalo[a] = -1;
                        }
                    }
                }
                JSONArray balos = (JSONArray)JSON.parse(res.getString("pocket_json"));
                if (balos.size() != this.nguoiChoi.itemBalo.length) {
                    khoCanDongBo = true;
                }
                boolean[] vatPhamDaGanBalo =
                        new boolean[this.nguoiChoi.itemBag.length];
                for (int i = 0; i < balos.size() && i < this.nguoiChoi.itemBalo.length; ++i) {
                    int chiSo;
                    try {
                        chiSo = Integer.parseInt(balos.get(i).toString());
                    } catch (NumberFormatException ex) {
                        khoCanDongBo = true;
                        continue;
                    }
                    if (chiSo == -1) {
                        continue;
                    }
                    if (chiSo >= 0
                            && chiSo < this.nguoiChoi.itemBag.length
                            && this.nguoiChoi.itemBag[chiSo] != null
                            && ChickenNguoiChoi.laVatPhamDuocPhepTrongBalo(
                                    this.nguoiChoi.itemBag[chiSo])
                            && ChickenYeuCauCapVatPham.datYeuCau(
                                    this.nguoiChoi.cap,
                                    this.nguoiChoi.itemBag[chiSo])
                            && this.nguoiChoi.itemBag[chiSo].chiSo == chiSo
                            && !vatPhamDaGanBalo[chiSo]) {
                        this.nguoiChoi.itemBalo[i] = chiSo;
                        vatPhamDaGanBalo[chiSo] = true;
                    } else {
                        khoCanDongBo = true;
                    }
                }
                JSONArray box = (JSONArray)JSON.parse(res.getString("storage_json"));
                for (int i = 0; i < box.size(); ++i) {
                    ChickenVatPham vatPham = new ChickenVatPham((JSONObject)box.get(i));
                    if (vatPham.mau != null
                            && vatPham.soLuong > 0
                            && vatPham.chiSo >= 0
                            && vatPham.chiSo < this.nguoiChoi.itemBox.length
                            && this.nguoiChoi.itemBox[vatPham.chiSo] == null) {
                        this.nguoiChoi.itemBox[vatPham.chiSo] = vatPham;
                    } else {
                        khoCanDongBo = true;
                        if (vatPham.mau != null && vatPham.soLuong > 0) {
                            vatPhamCachLy.add(vatPham);
                        }
                        Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                                Level.WARNING,
                                "Cach ly vat pham ruong co chi so khong hop le/bi trung: player={0}, index={1}",
                                new Object[]{this.nguoiChoi.ma, vatPham.chiSo}
                        );
                    }
                }
                for (ChickenVatPham vatPham : vatPhamCachLy) {
                    if (!this.datVatPhamVaoKhoTrongKhiTai(vatPham)) {
                        throw new SQLException(
                                "Khong con cho cach ly trang bi loi player="
                                + this.nguoiChoi.ma + " item=" + vatPham.ma);
                    }
                }
                if (khoCanDongBo
                        && !this.nguoiChoi.luuKhoVatPhamCoKetQua()) {
                    throw new SQLException(
                            "Khong the dong bo kho vat pham player="
                            + this.nguoiChoi.ma);
                }
                int thayDoiDiem =
                        ChickenQuanLyTiemNang.dongBoQuyenLoiTheoCapHienTai(
                                this.nguoiChoi);
                if (thayDoiDiem
                        == ChickenQuanLyTiemNang.DONG_BO_THAT_BAI) {
                    int maNguoiChoiLoi = this.nguoiChoi.ma;
                    this.nguoiChoi = null;
                    throw new SQLException(
                            "Khong the dong bo diem tiem nang player="
                            + maNguoiChoiLoi);
                }
                res.close();
                ChickenQuanLyBietDoi.taiClan(this.nguoiChoi);
                this.nguoiChoi.dichVu.datNguoiChoi(this.nguoiChoi);
                return true;
            }
            res.close();
            return true;
        }
        catch (SQLException | RuntimeException ex) {
            int maNguoiChoiLoi = this.nguoiChoi == null
                    ? -1 : this.nguoiChoi.ma;
            this.nguoiChoi = null;
            Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                    Level.SEVERE,
                    "Khong the tai du lieu nguoi choi id=" + maNguoiChoiLoi,
                    ex);
            try {
                this.dichVu.moHopThoaiOK(
                        "Dữ liệu nhân vật không hợp lệ. Vui lòng liên hệ quản trị viên.");
            } catch (Exception guiLoi) {
                ex.addSuppressed(guiLoi);
            }
            return false;
        }
    }

    static TrangThaiTrangBiDaLuu danhGiaTrangBiDaLuu(
            ChickenNguoiChoi nguoiChoi,
            ChickenVatPham vatPham
    ) {
        if (nguoiChoi == null
                || vatPham == null
                || vatPham.mau == null
                || vatPham.soLuong != 1
                || vatPham.chiSo < 0
                || vatPham.chiSo >= nguoiChoi.itemBody.length
                || vatPham.mau.loai != vatPham.chiSo
                || nguoiChoi.itemBody[vatPham.chiSo] != null) {
            return TrangThaiTrangBiDaLuu.LOI_DU_LIEU;
        }
        if (vatPham.mau.loai == 4
                && ChickenNguoiChoi
                        .layDungLuongBaloHopLe(vatPham) < 0) {
            return TrangThaiTrangBiDaLuu.LOI_DU_LIEU;
        }
        if (vatPham.mau.loai == 5
                && (ChickenQuanLyDanSung
                        .theoMauSung(vatPham.mau) == null
                || !ChickenNapDanServer
                        .coCauHinhNapDanHopLe(vatPham))) {
            return TrangThaiTrangBiDaLuu.LOI_DU_LIEU;
        }
        return ChickenYeuCauCapVatPham.datYeuCau(
                nguoiChoi.cap, vatPham)
                        ? TrangThaiTrangBiDaLuu.HOP_LE
                        : TrangThaiTrangBiDaLuu.THIEU_CAP;
    }

    private boolean datVatPhamVaoKhoTrongKhiTai(ChickenVatPham vatPham) {
        if (vatPham == null || vatPham.mau == null) {
            return false;
        }
        for (int i = 0; i < this.nguoiChoi.itemBag.length; i++) {
            if (this.nguoiChoi.itemBag[i] != null) {
                continue;
            }
            vatPham.HP = ChickenVatPham.DO_BEN_TOI_DA;
            vatPham.chiSo = i;
            this.nguoiChoi.itemBag[i] = vatPham;
            return true;
        }
        for (int i = 0; i < this.nguoiChoi.itemBox.length; i++) {
            if (this.nguoiChoi.itemBox[i] != null) {
                continue;
            }
            vatPham.HP = ChickenVatPham.DO_BEN_TOI_DA;
            vatPham.chiSo = i;
            this.nguoiChoi.itemBox[i] = vatPham;
            return true;
        }
        Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                Level.SEVERE,
                "Khong con o tui/ruong de cach ly vat pham loi: player={0}, item={1}",
                new Object[]{this.nguoiChoi.ma, vatPham.ma});
        return false;
    }

    public void close() {
        if (this.tenDangNhap != null) {
            users.remove(this.tenDangNhap, this);
        }
        if (this.nguoiChoi != null) {
            this.nguoiChoi.close();
        }
    }

    public String toString() {
        return this.tenDangNhap;
    }
}
