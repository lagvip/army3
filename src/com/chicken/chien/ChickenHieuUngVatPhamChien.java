package com.chicken.chien;

import com.chicken.bando.ChickenQuanLyBanDo;

/** Ap dung hieu ung gameplay cua item sau khi kho da luu thanh cong. */
public final class ChickenHieuUngVatPhamChien {
    private ChickenHieuUngVatPhamChien() {
    }

    /**
     * Kiem tra truoc giao dich de item khong bi tru neu resource/hit point
     * cua hieu ung khong hop le.
     */
    public static boolean coTheApDung(
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            ChickenKetQuaDan ketQua,
            ChickenQuanLyBanDo banDo
    ) {
        ChickenCauHinhSatThuongVatPham.HoSo hoSo = layHoSo(vatPham);
        if (hoSo == null || ketQua == null) {
            return false;
        }
        switch (hoSo.getHieuUngDacBiet()) {
            case KHONG:
            case PHA_DIA_HINH:
                return true;
            case DICH_CHUYEN_TUC_THOI:
                int[] dich = layDiemCuoi(ketQua);
                return dich != null && banDo != null
                        && dich[0] >= 0 && dich[0] < banDo.getWidth()
                        && dich[1] >= 0 && dich[1] < banDo.getHeight();
            case TAO_MANG_NHEN:
                int[] diem = layDiemCuoi(ketQua);
                return banDo != null
                        && ChickenQuanLyBanDo.coTheThemMangNhen()
                        && diem != null;
            case TAO_VOI_RONG:
                return banDo != null && layDiemCuoi(ketQua) != null;
            default:
                return false;
        }
    }

    /** Goi dung mot lan, ngay sau khi server tru va luu item thanh cong. */
    public static boolean apDung(
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            ChickenKetQuaDan ketQua,
            ChickenQuanLyBanDo banDo
    ) {
        if (!coTheApDung(vatPham, ketQua, banDo)) {
            return false;
        }
        ChickenCauHinhSatThuongVatPham.HoSo hoSo = layHoSo(vatPham);
        switch (hoSo.getHieuUngDacBiet()) {
            case KHONG:
            case PHA_DIA_HINH:
                return true;
            case DICH_CHUYEN_TUC_THOI:
                // Toa do chi duoc chot sau khi packet duong bay da gui, de
                // client bat dau animation tai vi tri cu.
                return true;
            case TAO_MANG_NHEN:
                int[] diem = layDiemCuoi(ketQua);
                // Ban ra ngoai map la mot phat truot hop le: van phai tra
                // animation/ket thuc luot cho client, nhung khong tao va cham
                // ngoai bien. Neu tra hop thoai o day, client dang cho CMD 22
                // se giu khoa input vinh vien.
                if (diem[0] < 0 || diem[0] >= banDo.getWidth()
                        || diem[1] < 0 || diem[1] >= banDo.getHeight()) {
                    return true;
                }
                if (!banDo.themMangNhen(diem[0], diem[1])) {
                    return false;
                }
                return true;
            case TAO_VOI_RONG:
                int[] tamVoiRong = layDiemCuoi(ketQua);
                // Roi ngoai map van la mot phat truot hop le. Client ket
                // thuc animation type 13, server khong tao cot loc ngoai bien.
                if (tamVoiRong[0] < 0
                        || tamVoiRong[0] >= banDo.getWidth()
                        || tamVoiRong[1] < 0
                        || tamVoiRong[1] >= banDo.getHeight()) {
                    return true;
                }
                return banDo.themVoiRong(
                        tamVoiRong[0], tamVoiRong[1]);
            default:
                return false;
        }
    }

    /** Chot vi tri authoritative sau khi client da nhan duong bay type 5. */
    public static boolean apDungDichChuyenSauKhiGuiPhatBan(
            ChickenChienBinh nguoiDung,
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            ChickenKetQuaDan ketQua,
            ChickenQuanLyBanDo banDo
    ) {
        ChickenCauHinhSatThuongVatPham.HoSo hoSo = layHoSo(vatPham);
        if (nguoiDung == null || nguoiDung.chet || hoSo == null
                || hoSo.getHieuUngDacBiet()
                        != ChickenCauHinhSatThuongVatPham.HieuUngDacBiet
                                .DICH_CHUYEN_TUC_THOI
                || !coTheApDung(vatPham, ketQua, banDo)) {
            return false;
        }
        int[] dich = layDiemCuoi(ketQua);
        nguoiDung.x = (short) dich[0];
        nguoiDung.y = (short) dich[1];
        return true;
    }

    private static ChickenCauHinhSatThuongVatPham.HoSo layHoSo(
            ChickenChienBinh.VatPhamChienTrongTran vatPham
    ) {
        return vatPham == null ? null
                : ChickenCauHinhSatThuongVatPham.theoIdVatPham(
                        vatPham.getIdVatPham());
    }

    private static int[] layDiemCuoi(ChickenKetQuaDan ketQua) {
        if (ketQua == null || ketQua.cacDuongX.length == 0
                || ketQua.cacDuongY.length == 0
                || ketQua.cacDuongX[0] == null
                || ketQua.cacDuongY[0] == null) {
            return null;
        }
        int soDiem = Math.min(
                ketQua.cacDuongX[0].length,
                ketQua.cacDuongY[0].length);
        if (soDiem <= 0) {
            return null;
        }
        return new int[]{
            ketQua.cacDuongX[0][soDiem - 1],
            ketQua.cacDuongY[0][soDiem - 1]
        };
    }
}
