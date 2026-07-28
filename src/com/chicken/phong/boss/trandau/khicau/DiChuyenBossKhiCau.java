package com.chicken.phong.boss.trandau.khicau;

import com.chicken.bando.ChickenQuanLyBanDo;
import java.util.concurrent.ThreadLocalRandom;

/** Di chuyển bay tự do của khí cầu; không áp dụng trọng lực hoặc địa hình. */
public final class DiChuyenBossKhiCau {
    /**
     * Client native noi suy khi cau khoang 3 px moi frame. Lay moc 180 px/s
     * cho client PC 60 FPS va them du phong de server khong doi luot khi hinh
     * anh van con dang bay.
     */
    private static final int TOC_DO_HIEN_THI_PIXEL_GIAY = 180;
    private static final int TRE_DU_PHONG_MS = 250;
    private static final int THOI_GIAN_TOI_THIEU_MS = 350;
    private static final int THOI_GIAN_TOI_DA_MS = 7_000;

    private DiChuyenBossKhiCau() {
    }

    public static short[] chonDiemXuatHien(ChickenQuanLyBanDo banDo) {
        int rong = Math.max(1, banDo.getWidth());
        int cao = Math.max(1, banDo.getHeight());
        int bienX = Math.min(120, Math.max(25, rong / 10));
        int xMin = Math.min(rong - 1, bienX);
        int xMax = Math.max(xMin, rong - bienX - 1);
        int yMin = Math.min(
                cao - 1,
                CauHinhBossKhiCau.Y_BAY_CAO_NHAT);
        int yMax = Math.max(
                yMin,
                Math.min(cao - 1, CauHinhBossKhiCau.Y_BAY_THAP_NHAT));
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int x = random.nextInt(xMin, xMax + 1);
        int y = random.nextInt(yMin, yMax + 1);
        return new short[]{(short) x, (short) y};
    }

    public static short[] chonDiemBay(short xHienTai, short yHienTai,
            ChickenQuanLyBanDo banDo) {
        int rong = Math.max(1, banDo.getWidth());
        int cao = Math.max(1, banDo.getHeight());
        int bienX = Math.min(120, Math.max(25, rong / 10));
        int yMin = Math.min(
                cao - 1,
                CauHinhBossKhiCau.Y_BAY_CAO_NHAT);
        int yMax = Math.min(
                cao - 1,
                CauHinhBossKhiCau.Y_BAY_THAP_NHAT);
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

    public static long tinhThoiGianBayMs(
            short xCu,
            short yCu,
            short xMoi,
            short yMoi
    ) {
        double khoangCach = Math.hypot(xMoi - xCu, yMoi - yCu);
        long thoiGian = (long) Math.ceil(
                khoangCach * 1_000.0D / TOC_DO_HIEN_THI_PIXEL_GIAY)
                + TRE_DU_PHONG_MS;
        return Math.max(
                THOI_GIAN_TOI_THIEU_MS,
                Math.min(THOI_GIAN_TOI_DA_MS, thoiGian));
    }

    private static short kep(int giaTri, int nhoNhat, int lonNhat) {
        return (short) Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }
}
