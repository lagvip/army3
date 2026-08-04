package com.chicken.vatpham;

/**
 * Kiem tra cap su dung vat pham tai server.
 *
 * <p>{@link ChickenMauVatPham#cap} la bac/cap cua ban than mon do.
 * Cap nhan vat toi thieu duoc luu trong {@link ChickenMauVatPham#strRequire}.
 * Client chi duoc hien thi hai gia tri nay, khong duoc tu quyet dinh quyen
 * mua, trang bi hay su dung.
 */
public final class ChickenYeuCauCapVatPham {
    /**
     * Giao thuc dataItem cua client doc cap yeu cau bang mot signed byte.
     * Gioi han nay giu du lieu server va client khong bi lech/am cap.
     */
    public static final int CAP_YEU_CAU_TOI_DA = Byte.MAX_VALUE;

    private ChickenYeuCauCapVatPham() {
    }

    public static boolean cauHinhHopLe(ChickenMauVatPham mau) {
        return mau != null
                && mau.strRequire >= 0
                && mau.strRequire <= CAP_YEU_CAU_TOI_DA;
    }

    public static boolean datYeuCau(
            int capNguoiChoi,
            ChickenMauVatPham mau
    ) {
        return capNguoiChoi >= 0
                && cauHinhHopLe(mau)
                && capNguoiChoi >= mau.strRequire;
    }

    public static boolean datYeuCau(
            int capNguoiChoi,
            int capYeuCau
    ) {
        return capNguoiChoi >= 0
                && capYeuCau >= 0
                && capYeuCau <= CAP_YEU_CAU_TOI_DA
                && capNguoiChoi >= capYeuCau;
    }

    public static boolean datYeuCau(
            int capNguoiChoi,
            ChickenVatPham vatPham
    ) {
        return vatPham != null
                && vatPham.mau != null
                && datYeuCau(capNguoiChoi, vatPham.mau);
    }
}
