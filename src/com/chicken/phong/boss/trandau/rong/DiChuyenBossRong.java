package com.chicken.phong.boss.trandau.rong;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chiso.ChickenKichThuocNhanVat;

/** Di chuyển bay và tọa độ kẹp/thả riêng của BigBoss Rồng. */
public final class DiChuyenBossRong {
    public static final int BUOC_BAY_TOI_DA = 50;
    public static final int BUOC_ROI = 14;
    public static final int LECH_Y_NGUOI_BI_KEP = 47;
    public static final int NUA_RONG_HITBOX = 72;
    public static final int CHIEU_CAO_HITBOX = 122;

    private DiChuyenBossRong() {
    }

    public static ChickenChienBinh timNguoiSongGanNhat(
            ChickenChienBinh boss,
            ChickenChienBinh[] chienBinhs
    ) {
        if (boss == null || chienBinhs == null) {
            return null;
        }
        ChickenChienBinh ganNhat = null;
        long khoangCachNhoNhat = Long.MAX_VALUE;
        for (int i = 0; i < 8 && i < chienBinhs.length; i++) {
            ChickenChienBinh nguoi = chienBinhs[i];
            if (nguoi == null || nguoi.chet || nguoi.hp <= 0 || !nguoi.coPhien()) {
                continue;
            }
            long dx = (long) nguoi.x - boss.x;
            long dy = (long) ChickenKichThuocNhanVat.layTamThanNguoiChoiY(nguoi.y)
                    - (boss.y - CHIEU_CAO_HITBOX / 2L);
            long kc = dx * dx + dy * dy;
            if (kc < khoangCachNhoNhat) {
                khoangCachNhoNhat = kc;
                ganNhat = nguoi;
            }
        }
        return ganNhat;
    }

    /** Nội suy một bước bay thẳng, không dùng trọng lực và không bám địa hình. */
    public static short[] tinhBuocBay(
            int tuX,
            int tuY,
            int denX,
            int denY,
            ChickenQuanLyBanDo banDo
    ) {
        if (banDo == null) {
            return new short[]{(short) tuX, (short) tuY};
        }
        double dx = denX - tuX;
        double dy = denY - tuY;
        double kc = Math.hypot(dx, dy);
        if (kc <= 1.0D) {
            return kepTrongMap(denX, denY, banDo);
        }
        double tiLe = Math.min(1.0D, BUOC_BAY_TOI_DA / kc);
        int x = (int) Math.round(tuX + dx * tiLe);
        int y = (int) Math.round(tuY + dy * tiLe);
        return kepTrongMap(x, y, banDo);
    }

    public static boolean daDenGan(int x, int y, int denX, int denY) {
        return Math.hypot(denX - x, denY - y) <= BUOC_BAY_TOI_DA;
    }

    /** Điểm trung chuyển: bay sang nửa map đối diện và nâng mục tiêu lên cao. */
    public static short[] chonDiemMangQua(
            ChickenChienBinh mucTieu,
            ChickenQuanLyBanDo banDo
    ) {
        int nuaMap = banDo.getWidth() / 2;
        int x = mucTieu.x < nuaMap
                ? Math.max(nuaMap + 120, banDo.getWidth() - 200)
                : Math.min(nuaMap - 120, 200);
        int y = Math.max(130, Math.min(230, mucTieu.y - 170));
        return kepTrongMap(x, y, banDo);
    }

    /** Tìm mặt đất đầu tiên bên dưới chân người chơi. */
    public static int timChanDatBenDuoi(
            int x,
            int batDauY,
            ChickenQuanLyBanDo banDo
    ) {
        int start = Math.max(0, batDauY);
        int max = banDo.getHeight() + 24;
        for (int y = start; y <= max; y++) {
            if (coNenNguoiChoi(x, y, banDo)) {
                return y;
            }
        }
        return -1;
    }

    public static boolean trungBossRong(int danX, int danY, int bossX, int bossY) {
        return danX >= bossX - NUA_RONG_HITBOX
                && danX <= bossX + NUA_RONG_HITBOX
                && danY >= bossY - CHIEU_CAO_HITBOX
                && danY <= bossY;
    }

    private static boolean coNenNguoiChoi(
            int x,
            int chanY,
            ChickenQuanLyBanDo banDo
    ) {
        int y = chanY + 1;
        int[] lechX = new int[]{-5, 0, 5};
        for (int lech : lechX) {
            int px = x + lech;
            if (px >= 0 && px < banDo.getWidth()
                    && y >= 0
                    && banDo.coVaCham((short) px, (short) y)) {
                return true;
            }
        }
        return false;
    }

    private static short[] kepTrongMap(int x, int y, ChickenQuanLyBanDo banDo) {
        int xMin = NUA_RONG_HITBOX;
        int xMax = Math.max(xMin, banDo.getWidth() - 1 - NUA_RONG_HITBOX);
        int yMin = 90;
        int yMax = Math.max(yMin, banDo.getHeight() - 35);
        x = Math.max(xMin, Math.min(xMax, x));
        y = Math.max(yMin, Math.min(yMax, y));
        return new short[]{(short) x, (short) y};
    }
}
