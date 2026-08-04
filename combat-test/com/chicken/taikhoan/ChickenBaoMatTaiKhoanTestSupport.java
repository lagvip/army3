package com.chicken.taikhoan;

import java.lang.reflect.Method;

public final class ChickenBaoMatTaiKhoanTestSupport {
    private ChickenBaoMatTaiKhoanTestSupport() {
    }

    public static void chay() throws Exception {
        bang("chicken_admin",
                ChickenBaoMatTaiKhoan
                        .chuanHoaTenDangNhap(" Chicken_Admin "));
        rong(ChickenBaoMatTaiKhoan
                .chuanHoaTenDangNhap("nvn_x"));
        rong(ChickenBaoMatTaiKhoan
                .chuanHoaTenDangNhap("admin<script>"));

        bang("owner@gmail.com",
                ChickenBaoMatTaiKhoan
                        .chuanHoaEmail(" Owner@Gmail.com "));
        rong(ChickenBaoMatTaiKhoan
                .chuanHoaEmail("owner@gmail"));
        bang("+84912345678",
                ChickenBaoMatTaiKhoan
                        .chuanHoaSoDienThoai("0912 345 678"));
        bang("+12025550123",
                ChickenBaoMatTaiKhoan
                        .chuanHoaSoDienThoai("+1 202-555-0123"));
        rong(ChickenBaoMatTaiKhoan
                .chuanHoaSoDienThoai("1234"));

        bang(null, ChickenBaoMatTaiKhoan
                .loiMatKhau("Chicken123"));
        khacRong(ChickenBaoMatTaiKhoan
                .loiMatKhau("chicken123"));
        khacRong(ChickenBaoMatTaiKhoan
                .loiMatKhau("Chicken"));

        String hash = ChickenBaoMatTaiKhoan
                .bamMatKhau("Chicken123");
        dung(ChickenBaoMatTaiKhoan.laBcrypt(hash));
        dung(ChickenBaoMatTaiKhoan
                .khopMatKhau("Chicken123", hash));
        sai(ChickenBaoMatTaiKhoan
                .khopMatKhau("Chicken124", hash));
        dung(ChickenBaoMatTaiKhoan
                .khopMatKhau("legacy", "legacy"));
        sai(ChickenBaoMatTaiKhoan
                .khopMatKhau("wrong", "legacy"));

        int tenDaQuet = 0;
        for (int doDai = 0; doDai <= 40; doDai++) {
            String ten = "a".repeat(doDai);
            String ketQua = ChickenBaoMatTaiKhoan
                    .chuanHoaTenDangNhap(ten);
            boolean hopLe = doDai >= 5 && doDai <= 24;
            bang(hopLe ? ten : null, ketQua);
            tenDaQuet++;
        }

        int matKhauDaQuet = 0;
        for (int doDai = 0; doDai <= 80; doDai++) {
            String matKhau;
            if (doDai == 0) {
                matKhau = "";
            } else if (doDai == 1) {
                matKhau = "A";
            } else {
                matKhau = "A1" + "a".repeat(doDai - 2);
            }
            boolean hopLe = doDai >= 8 && doDai <= 72;
            bang(hopLe, ChickenBaoMatTaiKhoan
                    .loiMatKhau(matKhau) == null);
            matKhauDaQuet++;
        }

        int soDienThoaiDaQuet = 0;
        for (int soChuSo = 1; soChuSo <= 20; soChuSo++) {
            String so = "+1" + "0".repeat(soChuSo - 1);
            boolean hopLe = soChuSo >= 8 && soChuSo <= 15;
            bang(hopLe, ChickenBaoMatTaiKhoan
                    .chuanHoaSoDienThoai(so) != null);
            soDienThoaiDaQuet++;
        }

        Method maOtpHopLe = ChickenXacMinhTaiKhoan.class
                .getDeclaredMethod("maOtpHopLe", String.class);
        maOtpHopLe.setAccessible(true);
        String[] otpHopLe = {"000000", "000001", "123456", "999999"};
        String[] otpSai = {null, "", "0", "12345", "1234567",
            "abcdef", "12 456", "１２３４５６", "-12345"};
        int otpDaQuet = 0;
        for (String otp : otpHopLe) {
            dung((boolean) maOtpHopLe.invoke(null, otp));
            otpDaQuet++;
        }
        for (String otp : otpSai) {
            sai((boolean) maOtpHopLe.invoke(null, otp));
            otpDaQuet++;
        }
        System.out.println(
                "ACCOUNT_SECURITY_OK normalization=ok bcrypt=ok "
                + "legacyMigration=ok usernameCases=" + tenDaQuet
                + " passwordCases=" + matKhauDaQuet
                + " phoneCases=" + soDienThoaiDaQuet
                + " otpCases=" + otpDaQuet);
    }

    private static void bang(Object mongDoi, Object thucTe) {
        if (mongDoi == null ? thucTe != null : !mongDoi.equals(thucTe)) {
            throw new AssertionError(
                    "Mong doi=" + mongDoi + " thuc te=" + thucTe);
        }
    }

    private static void rong(Object giaTri) {
        bang(null, giaTri);
    }

    private static void khacRong(Object giaTri) {
        if (giaTri == null) {
            throw new AssertionError("Gia tri khong duoc null");
        }
    }

    private static void dung(boolean dieuKien) {
        if (!dieuKien) {
            throw new AssertionError("Dieu kien phai dung");
        }
    }

    private static void sai(boolean dieuKien) {
        if (dieuKien) {
            throw new AssertionError("Dieu kien phai sai");
        }
    }
}
