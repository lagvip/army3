package com.chicken.taikhoan;

import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenPhien;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiDung;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Kiem thu tich hop co DB cho tron vong doi tai khoan. Chi tao ban ghi co
 * tien to codexsec va luon xoa trong finally; khong cham tai khoan that.
 */
public final class ChickenVongDoiTaiKhoanIntegrationTest {
    private static final byte[] PEPPER_TEST =
            "chicken-account-integration-pepper-2026"
                    .getBytes(StandardCharsets.UTF_8);

    private ChickenVongDoiTaiKhoanIntegrationTest() {
    }

    public static void main(String[] args) throws Exception {
        String pass = System.getenv("CHICKEN_DB_PASSWORD");
        if (pass == null || pass.isBlank()) {
            throw new IllegalStateException(
                    "Thieu CHICKEN_DB_PASSWORD cho accountIntegrationTest");
        }
        String host = bienMoiTruong("CHICKEN_DB_HOST", "127.0.0.1");
        String database = bienMoiTruong("CHICKEN_DB_NAME", "chicken3");
        String dbUser = bienMoiTruong("CHICKEN_DB_USER", "chicken");
        ChickenCoSoDuLieu.khoiTao(host, database, dbUser, pass);

        Field pepperField = ChickenXacMinhTaiKhoan.class
                .getDeclaredField("pepper");
        pepperField.setAccessible(true);
        byte[] pepperCu = (byte[]) pepperField.get(null);
        pepperField.set(null, PEPPER_TEST.clone());

        String hauTo = Long.toString(System.nanoTime(), 36)
                .toLowerCase(Locale.ROOT);
        String tenDangNhap = cat24("codexsec_" + hauTo);
        String email = tenDangNhap + "@example.invalid";
        String soDienThoai = "+1999" + chuSoCuoi(hauTo, 10);
        String matKhauCu = "ChickenA123";
        String matKhauMoi = "ChickenB456";
        int accountId = -1;
        ChickenNguoiDung userDangMo = null;
        try {
            napTemplateVatPham();

            ChickenNguoiDung.DangKyKetQua dangKy =
                    ChickenNguoiDung.dangKyTaiKhoan(
                            tenDangNhap, matKhauCu, email, soDienThoai);
            dung(dangKy.thanhCong(), "khong tao duoc tai khoan tam");
            accountId = layAccountId(tenDangNhap);
            dung(accountId > 0, "khong tim thay tai khoan vua tao");
            String hash = layHash(accountId);
            dung(ChickenBaoMatTaiKhoan.laBcrypt(hash)
                            && !matKhauCu.equals(hash),
                    "mat khau dang ky khong duoc bam bcrypt");

            dung(!ChickenNguoiDung.dangKyTaiKhoan(
                            tenDangNhap, matKhauCu,
                            "dup." + email, "+1888" + chuSoCuoi(hauTo, 10))
                            .thanhCong(),
                    "dang ky trung username van thanh cong");
            dung(!ChickenNguoiDung.dangKyTaiKhoan(
                            cat24("codexmail_" + hauTo), matKhauCu,
                            email, "+1777" + chuSoCuoi(hauTo, 10))
                            .thanhCong(),
                    "dang ky trung email van thanh cong");
            dung(!ChickenNguoiDung.dangKyTaiKhoan(
                            cat24("codexphone_" + hauTo), matKhauCu,
                            "phone." + email, soDienThoai)
                            .thanhCong(),
                    "dang ky trung so dien thoai van thanh cong");

            TrangThaiKhoa khoa = null;
            for (int lan = 1; lan <= 5; lan++) {
                PhienTest phienSai = new PhienTest(99_000 + lan);
                dung(ChickenNguoiDung.dangNhap(
                                phienSai, tenDangNhap, "SaiMatKhau9",
                                "3.7.3", (byte) 0, "") == null,
                        "dang nhap sai mat khau lai thanh cong lan=" + lan);
                khoa = layTrangThaiKhoa(accountId);
                dung(khoa.soLanSai() == lan,
                        "bo dem dang nhap sai=" + khoa.soLanSai()
                                + " sau lan=" + lan);
                if (lan < 5) {
                    dung(khoa.khoaDen() == null,
                            "tai khoan bi khoa som o lan sai=" + lan);
                } else {
                    dung(khoa.khoaDen() != null
                                    && khoa.khoaDen().getTime()
                                    > System.currentTimeMillis(),
                            "tai khoan khong khoa dung o lan sai thu 5");
                }
            }
            dung(ChickenNguoiDung.dangNhap(
                            new PhienTest(99_006), tenDangNhap, matKhauCu,
                            "3.7.3", (byte) 0, "") == null,
                    "mat khau dung vuot qua khoa tam thoi");
            moKhoaTaiKhoan(accountId);

            userDangMo = dangNhapThanhCong(
                    tenDangNhap, matKhauCu, 99_010);
            dung(userDangMo.nguoiChoi == null,
                    "tai khoan moi lai co player truoc khi tao");
            userDangMo.close();
            userDangMo = null;

            xacMinhEmailChoTest(accountId);
            themOtp(accountId, ChickenXacMinhTaiKhoan.DAT_LAI_MAT_KHAU,
                    "654321", 10);
            TrangThaiOtp otpKhoa = null;
            for (int lan = 1; lan <= 5; lan++) {
                dung(!ChickenXacMinhTaiKhoan.datLaiMatKhau(
                                tenDangNhap, "000000", matKhauMoi),
                        "OTP sai lai dat duoc mat khau lan=" + lan);
                otpKhoa = layTrangThaiOtp(accountId,
                        ChickenXacMinhTaiKhoan.DAT_LAI_MAT_KHAU);
                int soLanConMongDoi = 5 - lan;
                dung(otpKhoa.soLanCon() == soLanConMongDoi,
                        "OTP con=" + otpKhoa.soLanCon()
                                + " thay vi=" + soLanConMongDoi
                                + " sau lan sai=" + lan);
                if (lan < 5) {
                    dung(otpKhoa.daTieuThu() == null,
                            "OTP bi khoa som o lan sai=" + lan);
                } else {
                    dung(otpKhoa.daTieuThu() != null,
                            "OTP khong bi khoa dung o lan sai thu 5");
                }
            }

            themOtp(accountId, ChickenXacMinhTaiKhoan.DAT_LAI_MAT_KHAU,
                    "111111", -1);
            dung(!ChickenXacMinhTaiKhoan.datLaiMatKhau(
                            email, "111111", matKhauMoi),
                    "OTP het han van dat lai duoc mat khau");

            themOtp(accountId, ChickenXacMinhTaiKhoan.XAC_MINH_EMAIL,
                    "222222", 10);
            dung(!ChickenXacMinhTaiKhoan.datLaiMatKhau(
                            soDienThoai, "222222", matKhauMoi),
                    "OTP xac minh email bi dung sai muc dich reset");

            themOtp(accountId, ChickenXacMinhTaiKhoan.DAT_LAI_MAT_KHAU,
                    "333333", 10);
            dung(ChickenXacMinhTaiKhoan.datLaiMatKhau(
                            tenDangNhap, "333333", matKhauMoi),
                    "OTP dung khong dat lai duoc mat khau");
            dung(!ChickenXacMinhTaiKhoan.datLaiMatKhau(
                            tenDangNhap, "333333", "ChickenC789"),
                    "OTP da dung van replay duoc");
            dung(moiOtpDaTieuThu(accountId),
                    "reset mat khau khong huy cac OTP cu");

            dung(ChickenNguoiDung.dangNhap(
                            new PhienTest(99_020), tenDangNhap, matKhauCu,
                            "3.7.3", (byte) 0, "") == null,
                    "mat khau cu van dang nhap duoc sau reset");
            userDangMo = dangNhapThanhCong(
                    tenDangNhap, matKhauMoi, 99_021);

            PhienTest phienTao = ((DichVuTest) userDangMo.dichVu)
                    .layPhienChoTest();
            phienTao.user = userDangMo;
            userDangMo.taoNhanVat(goiTaoNhanVat(
                    "x');drop", (short) 0, (short) 7, (short) 8,
                    (short) 10, (short) 57, (short) 9));
            dung(demPlayer(accountId) == 0,
                    "ten nhan vat injection van duoc tao");
            userDangMo.taoNhanVat(goiTaoNhanVat(
                    "CodexSec" + hauTo.substring(0, Math.min(5, hauTo.length())),
                    (short) 9999, (short) 7, (short) 8,
                    (short) 10, (short) 57, (short) 9));
            dung(demPlayer(accountId) == 0,
                    "part tan thu gia van tao duoc nhan vat");
            String tenNhanVat = "Sec" + hauTo.substring(
                    0, Math.min(12, hauTo.length()));
            userDangMo.taoNhanVat(goiTaoNhanVat(
                    tenNhanVat, (short) 0, (short) 7, (short) 8,
                    (short) 10, (short) 57, (short) 9));
            dung(demPlayer(accountId) == 1
                            && userDangMo.nguoiChoi != null,
                    "khong tao dung mot nhan vat hop le");
            userDangMo.taoNhanVat(goiTaoNhanVat(
                    tenNhanVat + "X", (short) 0, (short) 7, (short) 8,
                    (short) 10, (short) 57, (short) 9));
            dung(demPlayer(accountId) == 1,
                    "replay CMD-99 tao trung nhan vat");

            userDangMo.close();
            userDangMo = null;
            themOtp(accountId, ChickenXacMinhTaiKhoan.DANG_NHAP_ADMIN,
                    "444444", 10);
            final int accountIdChoDongThoi = accountId;
            ExecutorService pool = Executors.newFixedThreadPool(8);
            int soLanOtpThanhCong = 0;
            try {
                List<Callable<Boolean>> viec = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    viec.add(() -> ChickenXacMinhTaiKhoan
                            .xacMinhMfaAdmin(
                                    accountIdChoDongThoi, "444444"));
                }
                for (Future<Boolean> ketQua : pool.invokeAll(viec)) {
                    if (ketQua.get()) {
                        soLanOtpThanhCong++;
                    }
                }
            } finally {
                pool.shutdownNow();
            }
            dung(soLanOtpThanhCong == 1,
                    "OTP dong thoi thanh cong " + soLanOtpThanhCong
                            + " lan thay vi mot");

            System.out.println("ACCOUNT_LIFECYCLE_INTEGRATION_OK"
                    + " register=ok duplicate=3 lockoutActual="
                    + khoa.soLanSai() + " lockoutExpected=5"
                    + " resetWrongActual=" + (5 - otpKhoa.soLanCon())
                    + " resetWrongExpected=5"
                    + " expired=1 purposeIsolation=1"
                    + " resetReplay=blocked otpConcurrency=8>1"
                    + " createCharacter=ok createReplay=blocked");
        } finally {
            if (userDangMo != null) {
                userDangMo.close();
            }
            ChickenNguoiDung.users.remove(tenDangNhap);
            if (accountId > 0) {
                xoaTaiKhoanTam(accountId);
            }
            pepperField.set(null, pepperCu);
            ChickenCoSoDuLieu.close();
        }
    }

    private static ChickenNguoiDung dangNhapThanhCong(
            String ten, String matKhau, int maPhien) {
        PhienTest phien = new PhienTest(maPhien);
        ChickenNguoiDung user = ChickenNguoiDung.dangNhap(
                phien, ten, matKhau, "3.7.3", (byte) 0, "");
        dung(user != null && user.taiDuLieuNguoiChoi(),
                "dang nhap hop le that bai");
        phien.dichVuTest.datUser(user);
        return user;
    }

    private static ChickenTinNhan goiTaoNhanVat(
            String ten, short head, short leg, short body,
            short wing, short weapon, short hat) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream ghi = new DataOutputStream(bytes)) {
            ghi.writeUTF(ten);
            ghi.writeShort(head);
            ghi.writeShort(leg);
            ghi.writeShort(body);
            ghi.writeShort(wing);
            ghi.writeShort(weapon);
            ghi.writeShort(hat);
        }
        return new ChickenTinNhan((byte) -99, bytes.toByteArray());
    }

    private static void napTemplateVatPham() throws Exception {
        Method load = ChickenQuanLyMayChu.class
                .getDeclaredMethod("loadDataItem");
        load.setAccessible(true);
        load.invoke(null);
        dung(!ChickenQuanLyMayChu.itemTemplates.isEmpty(),
                "khong nap duoc template item");
    }

    private static void themOtp(
            int accountId, String mucDich, String otp, int soPhut)
            throws Exception {
        byte[] hash = bamOtp(accountId, mucDich, otp);
        try (Connection conn = ChickenCoSoDuLieu.getConnection()) {
            try (PreparedStatement huy = conn.prepareStatement(
                    "UPDATE account_security_tokens SET consumed_at = NOW() "
                    + "WHERE account_id=? AND purpose=? "
                    + "AND consumed_at IS NULL")) {
                huy.setInt(1, accountId);
                huy.setString(2, mucDich);
                huy.executeUpdate();
            }
            try (PreparedStatement tao = conn.prepareStatement(
                    "INSERT INTO account_security_tokens "
                    + "(account_id,purpose,token_hash,attempts_left,expires_at) "
                    + "VALUES (?,?,?,?,DATE_ADD(NOW(), INTERVAL ? MINUTE))")) {
                tao.setInt(1, accountId);
                tao.setString(2, mucDich);
                tao.setBytes(3, hash);
                tao.setInt(4, 5);
                tao.setInt(5, soPhut);
                tao.executeUpdate();
            }
        }
    }

    private static byte[] bamOtp(int accountId, String mucDich, String otp)
            throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(PEPPER_TEST, "HmacSHA256"));
        return hmac.doFinal((accountId + "|" + mucDich + "|" + otp)
                .getBytes(StandardCharsets.UTF_8));
    }

    private static int layAccountId(String ten) throws Exception {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement doc = conn.prepareStatement(
                     "SELECT id FROM accounts WHERE username=?")) {
            doc.setString(1, ten);
            try (ResultSet ketQua = doc.executeQuery()) {
                return ketQua.next() ? ketQua.getInt(1) : -1;
            }
        }
    }

    private static String layHash(int accountId) throws Exception {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement doc = conn.prepareStatement(
                     "SELECT password_hash FROM accounts WHERE id=?")) {
            doc.setInt(1, accountId);
            try (ResultSet ketQua = doc.executeQuery()) {
                dung(ketQua.next(), "tai khoan bien mat khi doc hash");
                return ketQua.getString(1);
            }
        }
    }

    private static TrangThaiKhoa layTrangThaiKhoa(int accountId)
            throws Exception {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement doc = conn.prepareStatement(
                     "SELECT failed_login_attempts,locked_until "
                     + "FROM accounts WHERE id=?")) {
            doc.setInt(1, accountId);
            try (ResultSet ketQua = doc.executeQuery()) {
                dung(ketQua.next(), "tai khoan bien mat khi doc khoa");
                return new TrangThaiKhoa(
                        ketQua.getInt(1), ketQua.getTimestamp(2));
            }
        }
    }

    private static void moKhoaTaiKhoan(int accountId) throws Exception {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement sua = conn.prepareStatement(
                     "UPDATE accounts SET failed_login_attempts=0,"
                     + "locked_until=NULL WHERE id=?")) {
            sua.setInt(1, accountId);
            sua.executeUpdate();
        }
    }

    private static void xacMinhEmailChoTest(int accountId) throws Exception {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement sua = conn.prepareStatement(
                     "UPDATE accounts SET email_verified=1 WHERE id=?")) {
            sua.setInt(1, accountId);
            sua.executeUpdate();
        }
    }

    private static TrangThaiOtp layTrangThaiOtp(
            int accountId, String mucDich)
            throws Exception {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement doc = conn.prepareStatement(
                     "SELECT attempts_left,consumed_at "
                     + "FROM account_security_tokens "
                     + "WHERE account_id=? AND purpose=? "
                     + "ORDER BY id DESC LIMIT 1")) {
            doc.setInt(1, accountId);
            doc.setString(2, mucDich);
            try (ResultSet ketQua = doc.executeQuery()) {
                dung(ketQua.next(), "khong tim thay OTP de doc trang thai");
                return new TrangThaiOtp(
                        ketQua.getInt(1), ketQua.getTimestamp(2));
            }
        }
    }

    private static boolean moiOtpDaTieuThu(int accountId) throws Exception {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement doc = conn.prepareStatement(
                     "SELECT COUNT(*) FROM account_security_tokens "
                     + "WHERE account_id=? AND consumed_at IS NULL")) {
            doc.setInt(1, accountId);
            try (ResultSet ketQua = doc.executeQuery()) {
                return ketQua.next() && ketQua.getInt(1) == 0;
            }
        }
    }

    private static int demPlayer(int accountId) throws Exception {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement doc = conn.prepareStatement(
                     "SELECT COUNT(*) FROM players WHERE account_id=?")) {
            doc.setInt(1, accountId);
            try (ResultSet ketQua = doc.executeQuery()) {
                return ketQua.next() ? ketQua.getInt(1) : -1;
            }
        }
    }

    private static void xoaTaiKhoanTam(int accountId) throws Exception {
        try (Connection conn = ChickenCoSoDuLieu.getConnection()) {
            conn.setAutoCommit(false);
            try {
                xoaTheoAccount(conn, "account_security_tokens", accountId);
                xoaTheoAccount(conn, "players", accountId);
                xoaTheoId(conn, "accounts", accountId);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    private static void xoaTheoAccount(
            Connection conn, String bang, int accountId) throws Exception {
        try (PreparedStatement xoa = conn.prepareStatement(
                "DELETE FROM `" + bang + "` WHERE account_id=?")) {
            xoa.setInt(1, accountId);
            xoa.executeUpdate();
        }
    }

    private static void xoaTheoId(
            Connection conn, String bang, int id) throws Exception {
        try (PreparedStatement xoa = conn.prepareStatement(
                "DELETE FROM `" + bang + "` WHERE id=?")) {
            xoa.setInt(1, id);
            xoa.executeUpdate();
        }
    }

    private static String bienMoiTruong(String ten, String macDinh) {
        String giaTri = System.getenv(ten);
        return giaTri == null || giaTri.isBlank() ? macDinh : giaTri.trim();
    }

    private static String cat24(String giaTri) {
        return giaTri.substring(0, Math.min(24, giaTri.length()));
    }

    private static String chuSoCuoi(String giaTri, int soLuong) {
        StringBuilder ketQua = new StringBuilder();
        for (int i = 0; i < giaTri.length(); i++) {
            char c = giaTri.charAt(i);
            ketQua.append(Character.isDigit(c) ? c : (c - 'a' + 1) % 10);
        }
        while (ketQua.length() < soLuong) {
            ketQua.append('7');
        }
        return ketQua.substring(ketQua.length() - soLuong);
    }

    private static void dung(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private record TrangThaiKhoa(int soLanSai, Timestamp khoaDen) {
    }

    private record TrangThaiOtp(int soLanCon, Timestamp daTieuThu) {
    }

    private static final class PhienTest extends ChickenPhien {
        private final DichVuTest dichVuTest;

        private PhienTest(int ma) {
            super(null, ma);
            this.dichVuTest = new DichVuTest(this);
            this.datDichVu(this.dichVuTest);
        }

        @Override
        public void guiThongTin() {
            // Test DB tao nhan vat; khong dua player tam vao sanh RPG that.
        }

        @Override
        public void dongTin() {
            // Khong can kenh Netty trong integration test.
        }
    }

    private static final class DichVuTest extends ChickenDichVuGame {
        private final PhienTest phien;
        private final List<Integer> lenhDaGui = new ArrayList<>();

        private DichVuTest(PhienTest phien) {
            super(phien);
            this.phien = phien;
        }

        @Override
        public void guiTin(ChickenTinNhan tin) {
            this.lenhDaGui.add((int) tin.layLenh());
        }

        private void datUser(ChickenNguoiDung user) {
            this.phien.user = user;
        }

        private PhienTest layPhienChoTest() {
            return this.phien;
        }
    }
}
