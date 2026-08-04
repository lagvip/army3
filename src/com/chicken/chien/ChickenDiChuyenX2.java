package com.chicken.chien;

/** Luat server-authoritative cua item Di chuyen x2 (ID 223). */
public final class ChickenDiChuyenX2 {
    public static final int ID_VAT_PHAM = 223;
    public static final int HE_SO_DI_CHUYEN = 2;

    private ChickenDiChuyenX2() {
    }

    public static boolean coTheDung(ChickenChienBinh nguoiDung) {
        return nguoiDung != null
                && nguoiDung.laNguoiChoiThat()
                && !nguoiDung.daRoiTran
                && !nguoiDung.chet
                && nguoiDung.hp > 0
                && !nguoiDung.coDiChuyenX2();
    }

    public static int nhanDoiAnToan(int quangDuong) {
        long giaTri = (long) Math.max(0, quangDuong)
                * HE_SO_DI_CHUYEN;
        return (int) Math.min(Integer.MAX_VALUE, giaTri);
    }

    /** Danh dau buff va nhan doi phan quang duong con lai trong luot hien tai. */
    public static boolean apDung(ChickenChienBinh nguoiDung) {
        if (!coTheDung(nguoiDung)) {
            return false;
        }
        nguoiDung.kichHoatDiChuyenX2();
        return true;
    }
}
