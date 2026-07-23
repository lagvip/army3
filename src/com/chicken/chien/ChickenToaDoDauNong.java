package com.chicken.chien;

/**
 * Nguồn duy nhất tính điểm phát đạn ở đầu nòng súng.
 * Tọa độ nhân vật: x là tâm ngang, y là điểm chân.
 */
public final class ChickenToaDoDauNong {
    public static final int NGUOI_CHOI_TRUC_SUNG_CACH_CHAN = 32;
    public static final int BOSS_TRUC_SUNG_CACH_CHAN = 30;
    public static final int DO_DAI_NONG_NGUOI_CHOI = 18;
    public static final int DO_DAI_NONG_BOSS = 18;

    private ChickenToaDoDauNong() {
    }

    public static short[] layChoNguoiChoi(short x, short y, short goc,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo) {
        return lay(x, (short)(y - NGUOI_CHOI_TRUC_SUNG_CACH_CHAN), goc,
                DO_DAI_NONG_NGUOI_CHOI, banDo);
    }

    public static short[] layChoBoss(short x, short y, short goc,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo) {
        return lay(x, (short)(y - BOSS_TRUC_SUNG_CACH_CHAN), goc,
                DO_DAI_NONG_BOSS, banDo);
    }

    public static short[] lay(short trucX, short trucY, short goc, int doDaiNong,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo) {
        int gocChuan = goc % 360;
        if (gocChuan < 0) {
            gocChuan += 360;
        }
        double rad = Math.toRadians(gocChuan);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        int doDai = Math.max(0, doDaiNong);

        // Lấy đúng điểm cuối nòng theo vector góc. Chỉ thu ngắn dọc theo chính
        // trục nòng khi đầu nòng nằm trong địa hình, không đổi sang offset cố định.
        for (int khoangCach = doDai; khoangCach >= 0; khoangCach--) {
            int px = (int)Math.round(trucX + cos * khoangCach);
            int py = (int)Math.round(trucY - sin * khoangCach);
            if (px < 0 || py < 0 || px >= banDo.getWidth() || py >= banDo.getHeight()) {
                continue;
            }
            if (!banDo.coVaCham((short)px, (short)py)) {
                return new short[]{(short)px, (short)py};
            }
        }
        return new short[]{trucX, trucY};
    }
}
