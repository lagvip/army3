package com.chicken.chien;

/**
 * Tinh thoi gian client can de chay het quy dao do server tao.
 *
 * Cong thuc nay dung cung nhip voi luyen tap: moi diem quy dao tuong ung
 * khoang 8 ms, nhung luon chua mot khoang toi thieu cho hieu ung no va
 * khong de mot quy dao bat thuong khoa luot qua lau.
 */
public final class ChickenThoiGianHoatAnhDan {
    public static final long TOI_THIEU_MS = 180L;
    public static final long TOI_DA_MS = 450L;
    public static final long MOI_DIEM_MS = 8L;
    public static final long HIEU_UNG_KHONG_CO_QUY_DAO_MS = 300L;

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
