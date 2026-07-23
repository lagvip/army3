package com.chicken.chien;

/**
 * Quy tac sieu cao dung chung cho ca sat thuong phia server va hieu ung client.
 */
public final class ChickenSieuCao {

    public static final int DO_ROI_TOI_THIEU = 350;
    public static final int PHAN_TRAM_SAT_THUONG = 120;

    private ChickenSieuCao() {
    }

    public static boolean laPhatSieuCao(
            byte loaiDan,
            short[] duongX,
            short[] duongY
    ) {
        return timChiSoDinh(loaiDan, duongX, duongY) >= 0;
    }

    public static int tangSatThuong(int satThuong) {
        if (satThuong <= 0) {
            return 0;
        }
        long daTang = ((long) satThuong * PHAN_TRAM_SAT_THUONG + 50L) / 100L;
        return (int) Math.min(Integer.MAX_VALUE, Math.max(satThuong, daTang));
    }

    public static int tangNeuSieuCao(
            byte loaiDan,
            short[] duongX,
            short[] duongY,
            int satThuong
    ) {
        if (!laPhatSieuCao(loaiDan, duongX, duongY)) {
            return satThuong;
        }
        return tangSatThuong(satThuong);
    }

    public static int timChiSoDinh(
            byte loaiDan,
            short[] duongX,
            short[] duongY
    ) {
        if (!laLoaiDanSieuCao(loaiDan) || duongX == null || duongY == null) {
            return -1;
        }
        int soDiem = Math.min(duongX.length, duongY.length);
        if (soDiem < 3) {
            return -1;
        }

        int chiSoDinh = 0;
        int yDinh = duongY[0];
        for (int i = 1; i < soDiem; i++) {
            if (duongY[i] < yDinh) {
                yDinh = duongY[i];
                chiSoDinh = i;
            }
        }
        if (chiSoDinh <= 0 || chiSoDinh >= soDiem - 1) {
            return -1;
        }
        for (int i = chiSoDinh + 1; i < soDiem; i++) {
            if ((int) duongY[i] - yDinh > DO_ROI_TOI_THIEU) {
                return chiSoDinh;
            }
        }
        return -1;
    }

    private static boolean laLoaiDanSieuCao(byte loaiDan) {
        switch (loaiDan) {
            case 0:
            case 1:
            case 2:
            case 9:
            case 10:
            case 11:
            case 19:
                return true;
            default:
                return false;
        }
    }
}
