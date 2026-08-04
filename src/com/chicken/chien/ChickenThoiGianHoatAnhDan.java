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
    /** Du phong thoi gian may bay B52 bay tu mep man hinh den diem tha. */
    public static final long B52_TRE_MAY_BAY_MS = 500L;
    /** Bullet 14 cho 20 frame truoc khi client kich hoat tia type 15. */
    public static final long LAZER_TRE_KICH_HOAT_MS = 20L * MOI_DIEM_MS;
    /** Bullet 15 giu hieu ung tia laser native trong 26 frame. */
    public static final long LAZER_HIEU_UNG_MS = 26L * MOI_DIEM_MS;

    private ChickenThoiGianHoatAnhDan() {
    }

    public static long tinh(ChickenKetQuaDan ketQua) {
        if (ketQua == null) {
            return HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        }
        int loaiDan = ketQua.loaiDan & 0xFF;
        if (loaiDan == 4) {
            return tinhBomB52(ketQua.cacDuongX, ketQua.cacDuongY);
        }
        if (loaiDan == 14) {
            return tinhDanLazer(ketQua.cacDuongX, ketQua.cacDuongY);
        }
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
     * Type 4 chay duong danh dau, cho may bay toi, sau do moi chay duong bom.
     * Hai duong noi tiep nhau nen khong duoc lay max nhu loat dan song song.
     */
    private static long tinhBomB52(
            short[][] cacDuongX,
            short[][] cacDuongY
    ) {
        int soDuong = soDuong(cacDuongX, cacDuongY);
        if (soDuong < 2) {
            return HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        }
        int tongDiem = soDiem(cacDuongX, cacDuongY, 0)
                + soDiem(cacDuongX, cacDuongY, 1);
        return kepThoiGian(
                (long) Math.max(1, tongDiem) * MOI_DIEM_MS
                + B52_TRE_MAY_BAY_MS
                + DEM_KET_THUC_MS);
    }

    /**
     * Type 14 chay duong danh dau, cho 20 frame roi moi ve tia type 15.
     * Path 1 la diem neo cua tia, khong phai mot quy dao chay song song.
     */
    private static long tinhDanLazer(
            short[][] cacDuongX,
            short[][] cacDuongY
    ) {
        if (soDuong(cacDuongX, cacDuongY) < 2) {
            return HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        }
        int soDiemDanhDau = soDiem(cacDuongX, cacDuongY, 0);
        return kepThoiGian(
                (long) Math.max(1, soDiemDanhDau) * MOI_DIEM_MS
                + LAZER_TRE_KICH_HOAT_MS
                + LAZER_HIEU_UNG_MS
                + DEM_KET_THUC_MS);
    }

    /**
     * Timeout du phong cho vien POW. Mot so bullet type native tao them Bullet
     * theo tung nhip khi critical=1, nen can cong ca thoi gian chen cac duong
     * hien thi ma server da bo sung cho client.
     */
    public static long tinh(ChickenKetQuaDan ketQua, boolean critical) {
        long coBan = tinh(ketQua);
        if (!critical || ketQua == null) {
            return coBan;
        }
        int soDuongGoc = Math.min(
                ketQua.cacDuongX.length, ketQua.cacDuongY.length);
        ChickenQuyDaoPowClient.DuLieu hienThi =
                ChickenQuyDaoPowClient.tao(
                        ketQua.loaiDan,
                        ketQua.cacDuongX,
                        ketQua.cacDuongY,
                        true);
        int soDuongHienThi = Math.min(
                hienThi.getCacDuongX().length,
                hienThi.getCacDuongY().length);
        int soDuongChen = Math.max(0, soDuongHienThi - soDuongGoc);
        return kepThoiGian(
                coBan + soDuongChen * 10L * MOI_DIEM_MS);
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
