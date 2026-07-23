package com.chicken.phong.boss.trandau.khicau;

import com.chicken.bando.ChickenQuanLyBanDo;
import java.util.concurrent.ThreadLocalRandom;

/** Di chuyển bay tự do của khí cầu; không áp dụng trọng lực hoặc địa hình. */
public final class DiChuyenBossKhiCau {
    private static final int BUOC_TOI_DA = 55;
    private static final int KHOANG_CACH_DA_DEN = 18;

    private DiChuyenBossKhiCau() {
    }

    public static short[] chonDiemBay(short xHienTai, short yHienTai,
            ChickenQuanLyBanDo banDo) {
        int rong = Math.max(1, banDo.getWidth());
        int cao = Math.max(1, banDo.getHeight());
        int bienX = Math.min(120, Math.max(25, rong / 10));
        int yMin = Math.min(cao - 1, 205);
        int yMax = Math.min(cao - 1, 340);
        if (yMax < yMin) {
            yMin = Math.max(0, cao / 4);
            yMax = Math.max(yMin, cao * 2 / 3);
        }

        int dichX;
        if (xHienTai > rong * 2 / 3) {
            dichX = ThreadLocalRandom.current().nextInt(
                    bienX, Math.max(bienX + 1, rong / 2));
        } else if (xHienTai < rong / 3) {
            dichX = ThreadLocalRandom.current().nextInt(
                    Math.min(rong - 1, rong / 2), Math.max(rong / 2 + 1, rong - bienX));
        } else {
            dichX = ThreadLocalRandom.current().nextBoolean()
                    ? bienX : Math.max(bienX, rong - bienX - 1);
        }
        int dichY = ThreadLocalRandom.current().nextInt(yMin, yMax + 1);
        return new short[]{kep(dichX, 0, rong - 1), kep(dichY, 0, cao - 1)};
    }

    public static boolean daDenGan(short x, short y, short dichX, short dichY) {
        return Math.abs(dichX - x) <= KHOANG_CACH_DA_DEN
                && Math.abs(dichY - y) <= KHOANG_CACH_DA_DEN;
    }

    public static short[] tinhBuoc(short x, short y, short dichX, short dichY,
            ChickenQuanLyBanDo banDo) {
        double dx = dichX - x;
        double dy = dichY - y;
        double doDai = Math.max(1.0D, Math.hypot(dx, dy));
        double tiLe = Math.min(1.0D, BUOC_TOI_DA / doDai);
        int xMoi = (int) Math.round(x + dx * tiLe);
        int yMoi = (int) Math.round(y + dy * tiLe);
        return new short[]{
            kep(xMoi, 0, banDo.getWidth() - 1),
            kep(yMoi, 0, banDo.getHeight() - 1)
        };
    }

    private static short kep(int giaTri, int nhoNhat, int lonNhat) {
        return (short) Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }
}
