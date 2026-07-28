package com.chicken.chien;

/**
 * Nguồn duy nhất tính điểm phát đạn ở đầu nòng súng.
 * Tọa độ nhân vật: x là tâm ngang, y là điểm chân.
 */
public final class ChickenToaDoDauNong {
    /** Khop pivot CPlayer va cong thuc dang dung trong luyen tap. */
    public static final int NGUOI_CHOI_TRUC_SUNG_CACH_CHAN = 12;
    public static final int BOSS_TRUC_SUNG_CACH_CHAN = 30;
    public static final int DO_DAI_NONG_NGUOI_CHOI = 40;
    public static final int DO_DAI_NONG_PROTON = 20;
    public static final int DO_DAI_NONG_BOSS = 18;

    private ChickenToaDoDauNong() {
    }

    public static short[] layChoNguoiChoi(short x, short y, short goc,
            short maVuKhi,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo) {
        return lay(x, (short)(y - NGUOI_CHOI_TRUC_SUNG_CACH_CHAN), goc,
                layDoDaiNongNguoiChoi(maVuKhi), banDo);
    }

    /**
     * Client dung nong 20 px cho nhom Proton (ID 150-159), cac sung con lai
     * dung 40 px. maVuKhi lay tu trang thai chien binh tren server.
     */
    public static int layDoDaiNongNguoiChoi(short maVuKhi) {
        ChickenQuanLyDanSung.DuLieuSung sung =
                ChickenQuanLyDanSung.theoPartSung(maVuKhi);
        if (sung != null && sung.getIdSung() >= 150 && sung.getIdSung() <= 159) {
            return DO_DAI_NONG_PROTON;
        }
        return DO_DAI_NONG_NGUOI_CHOI;
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
