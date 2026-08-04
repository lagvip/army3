package com.chicken.chien;

/**
 * Invariant state cua mot luot chien dau.
 * Item dang cho chi co hieu luc trong chinh luot da chon va khong duoc phep
 * chong len trang thai skill AVG.
 */
public final class ChickenTrangThaiHanhDongLuot {
    private ChickenTrangThaiHanhDongLuot() {
    }

    public static void ketThucLuot(ChickenChienBinh chienBinh) {
        if (chienBinh != null) {
            chienBinh.datLaiHanhDongDacBietTrongLuot();
        }
    }

    public static boolean coTheKichHoatKyNang(ChickenChienBinh chienBinh) {
        return chienBinh != null
                && chienBinh.layVatPhamChienDangCho() == null
                && !chienBinh.daChonVatPhamTrongLuot();
    }
}
