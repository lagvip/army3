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
    /**
     * Thoi gian cho client chay frame no, removeBullet va endShoot sau diem
     * quy dao cuoi. Khong co dem nay, CMD24 cua luot moi co the ghi de VFX cu.
     */
    public static final long DEM_KET_THUC_MS = 300L;

    private ChickenThoiGianHoatAnhDan() {
    }

    public static long tinh(ChickenKetQuaDan ketQua) {
        if (ketQua == null) {
            return HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        }
        int loaiDan = ketQua.loaiDan & 0xFF;
        if (loaiDan == 17) {
            return tinhDanRiu(ketQua.cacDuongX, ketQua.cacDuongY);
        }
        if (loaiDan == 19) {
            return tinhDanGa(
                    ketQua.cacDuongX,
                    ketQua.cacDuongY,
                    ketQua.lucPhu & 0xFF);
        }
        return tinh(ketQua.cacDuongX, ketQua.cacDuongY);
    }

    /**
     * Dan Riu chay het duong chinh roi client moi kich hoat dong thoi ba
     * duong con. Lay max don thuan se doi luot trong luc ba dau Riu van bay.
     */
    private static long tinhDanRiu(short[][] cacDuongX, short[][] cacDuongY) {
        int soDuong = soDuong(cacDuongX, cacDuongY);
        if (soDuong <= 0) {
            return HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        }
        int duongChinh = soDiem(cacDuongX, cacDuongY, 0);
        int duongConDaiNhat = 0;
        for (int i = 1; i < soDuong; i++) {
            duongConDaiNhat = Math.max(
                    duongConDaiNhat,
                    soDiem(cacDuongX, cacDuongY, i));
        }
        return kepThoiGian(
                ((long) duongChinh + duongConDaiNhat) * MOI_DIEM_MS
                + DEM_KET_THUC_MS);
    }

    /**
     * Dan Ga giu duong chinh, den moc luc phu moi tha duong roi. Hai nhanh
     * sau do chay dong thoi nen lay nhanh ket thuc muon hon.
     */
    private static long tinhDanGa(
            short[][] cacDuongX,
            short[][] cacDuongY,
            int mocTha
    ) {
        int soDuong = soDuong(cacDuongX, cacDuongY);
        if (soDuong <= 0) {
            return HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        }
        int duongChinh = soDiem(cacDuongX, cacDuongY, 0);
        int duongRoi = soDuong > 1
                ? soDiem(cacDuongX, cacDuongY, 1) : 0;
        int tongDiem = Math.max(duongChinh, Math.max(1, mocTha) + duongRoi);
        return kepThoiGian(
                (long) tongDiem * MOI_DIEM_MS
                + DEM_KET_THUC_MS);
    }

    public static long tinh(short[][] cacDuongX, short[][] cacDuongY) {
        if (cacDuongX == null || cacDuongY == null) {
            return HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        }
        int soDuong = soDuong(cacDuongX, cacDuongY);
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
        return kepThoiGian(
                (long) doDaiLonNhat * MOI_DIEM_MS + DEM_KET_THUC_MS);
    }

    private static int soDuong(short[][] cacDuongX, short[][] cacDuongY) {
        return cacDuongX == null || cacDuongY == null
                ? 0 : Math.min(cacDuongX.length, cacDuongY.length);
    }

    private static int soDiem(
            short[][] cacDuongX,
            short[][] cacDuongY,
            int chiSo
    ) {
        if (chiSo < 0 || chiSo >= soDuong(cacDuongX, cacDuongY)) {
            return 0;
        }
        short[] xs = cacDuongX[chiSo];
        short[] ys = cacDuongY[chiSo];
        return Math.min(xs == null ? 0 : xs.length, ys == null ? 0 : ys.length);
    }

    private static long kepThoiGian(long thoiGianMs) {
        return Math.max(TOI_THIEU_MS, Math.min(TOI_DA_MS, thoiGianMs));
    }
}
