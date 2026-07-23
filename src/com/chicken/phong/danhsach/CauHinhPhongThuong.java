package com.chicken.phong.danhsach;

/** Cấu hình riêng cho P0-P3; không chứa logic phòng boss. */
public final class CauHinhPhongThuong {
    public static final int SO_NHOM_PHONG = 4;
    public static final int SO_BAN_MOI_NHOM = 5;
    public static final byte SO_NGUOI_TOI_DA = 8;
    public static final byte MAP_CAU_BANG = 0;

    private static final int[] TIEN_HIEN_THI = new int[]{100, 100, 0, 0};

    private CauHinhPhongThuong() {
    }

    public static int layTien(int maPhong) {
        if (maPhong < 0 || maPhong >= TIEN_HIEN_THI.length) {
            return 0;
        }
        return TIEN_HIEN_THI[maPhong];
    }
}
