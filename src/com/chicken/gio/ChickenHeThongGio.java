package com.chicken.gio;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Quản lý duy nhất gió dùng trong mọi chế độ chiến đấu.
 *
 * Client Army3 nhận packet 25 gồm 2 byte windX/windY, sau đó tự tính góc
 * và lực để hiển thị. Mỗi lượt chỉ tạo một trạng thái gió và giữ nguyên
 * trạng thái đó cho toàn bộ phát bắn của lượt.
 */
public final class ChickenHeThongGio {
    public static final int GOC_NHO_NHAT = 0;
    public static final int GOC_LON_NHAT = 359;
    public static final int LUC_NHO_NHAT = 0;
    public static final int LUC_LON_NHAT = 100;

    /* Các AVG/súng/item đặc biệt không chịu gió được thêm vào đây. */
    private static final int[] ID_SUNG_KHONG_CHIU_GIO = {};
    private static final int[] ID_ITEM_KHONG_CHIU_GIO = {};

    public static final class TrangThaiGio {
        private final int goc;
        private final int luc;
        private final byte windX;
        private final byte windY;

        private TrangThaiGio(int goc, int luc, byte windX, byte windY) {
            this.goc = goc;
            this.luc = luc;
            this.windX = windX;
            this.windY = windY;
        }

        public int getGoc() { return this.goc; }
        public int getLuc() { return this.luc; }
        public byte getWindX() { return this.windX; }
        public byte getWindY() { return this.windY; }
    }

    private ChickenHeThongGio() {
    }

    public static TrangThaiGio khongGio() {
        return new TrangThaiGio(0, 0, (byte) 0, (byte) 0);
    }

    public static TrangThaiGio taoGioMoi() {
        int goc = ThreadLocalRandom.current().nextInt(GOC_NHO_NHAT, GOC_LON_NHAT + 1);
        int luc = ThreadLocalRandom.current().nextInt(LUC_NHO_NHAT, LUC_LON_NHAT + 1);
        return taoTheoGocVaLuc(goc, luc);
    }

    public static TrangThaiGio taoTheoGocVaLuc(int goc, int luc) {
        int gocChuan = ((goc % 360) + 360) % 360;
        int lucChuan = Math.max(LUC_NHO_NHAT, Math.min(LUC_LON_NHAT, luc));
        if (lucChuan == 0) {
            return khongGio();
        }
        double rad = Math.toRadians(gocChuan);
        int x = (int) Math.round(Math.cos(rad) * lucChuan);
        int y = (int) Math.round(-Math.sin(rad) * lucChuan);
        return new TrangThaiGio(
                gocChuan,
                lucChuan,
                (byte) Math.max(-127, Math.min(127, x)),
                (byte) Math.max(-127, Math.min(127, y))
        );
    }

    public static boolean sungChiuAnhHuongGio(int idSung) {
        return !coTrongMang(ID_SUNG_KHONG_CHIU_GIO, idSung);
    }

    public static boolean itemChiuAnhHuongGio(int idItem) {
        return !coTrongMang(ID_ITEM_KHONG_CHIU_GIO, idItem);
    }

    public static byte layWindXChoSung(TrangThaiGio gio, int idSung) {
        return gio != null && sungChiuAnhHuongGio(idSung) ? gio.getWindX() : 0;
    }

    public static byte layWindYChoSung(TrangThaiGio gio, int idSung) {
        return gio != null && sungChiuAnhHuongGio(idSung) ? gio.getWindY() : 0;
    }

    private static boolean coTrongMang(int[] mang, int giaTri) {
        for (int item : mang) {
            if (item == giaTri) {
                return true;
            }
        }
        return false;
    }
}
