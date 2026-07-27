package com.chicken.chien;

/**
 * Tinh thoi gian client can de chay het quy dao do server tao.
 *
 * Client goc cap nhat moi diem quy dao theo frame, khong phai theo nhip
 * scheduler cua server. Timeout o day chi la duong lui khi client khong gui
 * CMD 79 bao animation da xong, vi vay phai du dai de khong ghi de BM dang
 * chay tren may nguoi choi.
 */
public final class ChickenThoiGianHoatAnhDan {
    public static final long TOI_THIEU_MS = 180L;
    public static final long TOI_DA_MS = 5_000L;
    public static final long MOI_DIEM_MS = 40L;
    public static final long HIEU_UNG_KHONG_CO_QUY_DAO_MS = 800L;

    private ChickenThoiGianHoatAnhDan() {
    }

    public static long tinh(ChickenKetQuaDan ketQua) {
        if (ketQua == null) {
            return HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        }
        return tinh(ketQua.cacDuongX, ketQua.cacDuongY);
    }

    public static long tinh(short[][] cacDuongX, short[][] cacDuongY) {
        if (cacDuongX == null || cacDuongY == null) {
            return HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        }
        int soDuong = Math.min(cacDuongX.length, cacDuongY.length);
        int doDaiLonNhat = 0;
        for (int i = 0; i < soDuong; i++) {
            short[] xs = cacDuongX[i];
            short[] ys = cacDuongY[i];
            doDaiLonNhat = Math.max(
                    doDaiLonNhat,
                    Math.min(xs == null ? 0 : xs.length,
                            ys == null ? 0 : ys.length)
            );
        }
        if (doDaiLonNhat <= 0) {
            return HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        }
        return Math.max(
                TOI_THIEU_MS,
                Math.min(TOI_DA_MS, doDaiLonNhat * MOI_DIEM_MS)
        );
    }
}
