package com.chicken.chien;

/**
 * Chuan hoa so quỹ đạo hiển thị theo quy tắc critical native của client cũ.
 *
 * <p>Đây chỉ là bản sao dùng để ghi packet. Kết quả va chạm và damage vẫn
 * dùng {@link ChickenKetQuaDan} gốc, nên POW không thể nhân số lần damage chỉ
 * vì client cần nhiều Bullet hơn để chạy hết animation.</p>
 */
public final class ChickenQuyDaoPowClient {

    private ChickenQuyDaoPowClient() {
    }

    public static DuLieu tao(
            byte loaiDan,
            short[][] cacDuongX,
            short[][] cacDuongY,
            boolean critical
    ) {
        int soDuongGoc = Math.min(
                cacDuongX == null ? 0 : cacDuongX.length,
                cacDuongY == null ? 0 : cacDuongY.length);
        if (soDuongGoc <= 0) {
            return new DuLieu(new short[0][], new short[0][]);
        }

        int soDuongHienThi = critical
                ? Math.max(soDuongGoc, soDanCriticalNative(loaiDan))
                : soDuongGoc;
        soDuongHienThi = Math.min(255, soDuongHienThi);
        if (soDuongHienThi == soDuongGoc) {
            return new DuLieu(cacDuongX, cacDuongY);
        }

        short[][] hienThiX = new short[soDuongHienThi][];
        short[][] hienThiY = new short[soDuongHienThi][];
        for (int i = 0; i < soDuongHienThi; i++) {
            int nguon = i % soDuongGoc;
            hienThiX[i] = cacDuongX[nguon];
            hienThiY[i] = cacDuongY[nguon];
        }
        return new DuLieu(hienThiX, hienThiY);
    }

    private static int soDanCriticalNative(byte loaiDan) {
        switch (loaiDan & 0xFF) {
            case 1:
                return 6;
            case 2:
                return 7;
            case 11:
                return 10;
            default:
                return 0;
        }
    }

    public static final class DuLieu {
        private final short[][] cacDuongX;
        private final short[][] cacDuongY;

        private DuLieu(short[][] cacDuongX, short[][] cacDuongY) {
            this.cacDuongX = cacDuongX;
            this.cacDuongY = cacDuongY;
        }

        public short[][] getCacDuongX() {
            return this.cacDuongX;
        }

        public short[][] getCacDuongY() {
            return this.cacDuongY;
        }
    }
}
