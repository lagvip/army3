package com.chicken.phong.boss.trandau;

public final class CauHinhMapBoss {
    public static final int MAP_BOSS_MAC_DINH = 50;
    public static final int MAP_BOSS_NHO_NHAT = 50;
    public static final int MAP_BOSS_LON_NHAT = 58;

    private CauHinhMapBoss() {
    }

    /** Mỗi sảnh boss mới đều bắt đầu ở map 50 - Boss Bao vây. */
    public static int layMapTheoBan(int maBan) {
        return maBan >= 0 && maBan < 5 ? MAP_BOSS_MAC_DINH : -1;
    }

    /** Cho phép các map boss 50..55 và thêm map 58 - Rùa x Rồng. */
    public static boolean laMapBossHopLe(int maBanDo) {
        return (maBanDo >= 50 && maBanDo <= 55) || maBanDo == 58;
    }

    public static String layTenMap(int maBanDo) {
        return switch (maBanDo) {
            case 50 -> "Boss Bao vây";
            case 51 -> "Boss Hai tòa tháp";
            case 52 -> "Boss Khí cầu";
            case 53 -> "Boss Đặt bom";
            case 54 -> "Boss Rùa";
            case 55 -> "Boss Rồng";
            case 58 -> "Boss Rùa x Boss Rồng";
            default -> "Map không hợp lệ";
        };
    }
}
