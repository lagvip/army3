package com.chicken.chien;

/** Luat server-authoritative cua item Cuu thuong ca nhan (ID 220). */
public final class ChickenCuuThuongBanThan {
    public static final int ID_VAT_PHAM = 220;
    public static final int SO_MAU_HOI = 300;

    private ChickenCuuThuongBanThan() {
    }

    public static boolean coTheDung(ChickenChienBinh nguoiDung) {
        return nguoiDung != null
                && nguoiDung.laNguoiChoiThat()
                && !nguoiDung.daRoiTran
                && !nguoiDung.chet
                && nguoiDung.hp > 0
                && nguoiDung.hp < nguoiDung.mauToiDa;
    }

    /** Cong co dinh 300 HP va kep tai HP toi da. */
    public static int tinhMauSauCuuThuong(int hp, int mauToiDa) {
        int maxHp = Math.max(1, mauToiDa);
        int hpHienTai = Math.max(0, Math.min(maxHp, hp));
        return (int) Math.min((long) maxHp, (long) hpHienTai + SO_MAU_HOI);
    }

    /** Tra ve so HP thuc te da hoi de ghi log/kiem thu. */
    public static int apDung(ChickenChienBinh nguoiDung) {
        if (!coTheDung(nguoiDung)) {
            return 0;
        }
        int hpCu = nguoiDung.hp;
        nguoiDung.hp = tinhMauSauCuuThuong(
                nguoiDung.hp, nguoiDung.mauToiDa);
        return nguoiDung.hp - hpCu;
    }
}
