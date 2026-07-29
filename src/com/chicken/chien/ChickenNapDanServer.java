package com.chicken.chien;

import com.chicken.chiso.ChickenChiSoNguoiChoi;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenVatPham;
import java.util.Vector;

/**
 * Nguon duy nhat tinh toc do nap dan cua nguoi choi tren server.
 *
 * Client khong duoc gui gia tri nay. Item dang trang bi, option vat pham va
 * diem tiem nang deu duoc lay tu trang thai da xac thuc cua server.
 */
public final class ChickenNapDanServer {

    /** Không người chơi nào được nạp nhanh hơn mốc này. */
    public static final int TOI_THIEU = 250;
    public static final int MAC_DINH = TOI_THIEU;
    public static final int TOI_DA = 65_535;
    private static final int SLOT_SUNG = 5;
    private static final int OPTION_NAP_DAN = 14;

    private ChickenNapDanServer() {
    }

    public static int layChoChienBinh(ChickenChienBinh chienBinh) {
        if (chienBinh == null || chienBinh.nguoiChoi == null) {
            return MAC_DINH;
        }
        return layChoNguoiChoi(chienBinh.nguoiChoi);
    }

    public static int layChoNguoiChoi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return MAC_DINH;
        }
        ChickenVatPham sung = nguoiChoi.itemBody != null
                && nguoiChoi.itemBody.length > SLOT_SUNG
                ? nguoiChoi.itemBody[SLOT_SUNG] : null;
        int napDanGoc = layNapDanGoc(sung);
        int giamNapDan =
                ChickenChiSoNguoiChoi.tinhGiamNapDanTuTiemNang(nguoiChoi);
        long ketQua = (long) napDanGoc - Math.max(0, giamNapDan);
        return kep(ketQua);
    }

    /**
     * Option instance hop le duoc uu tien. Neu item bi thieu/loi option 14 thi
     * quay ve template server cua chinh khau sung, khong dung mac dinh 100 de
     * tranh bien item loi thanh sung nap sieu nhanh.
     */
    static int layNapDanGoc(ChickenVatPham sung) {
        if (sung == null) {
            return MAC_DINH;
        }
        int tuItem = layOptionDuong(sung.itemOptions, OPTION_NAP_DAN);
        if (tuItem > 0) {
            return kep(tuItem);
        }
        ChickenMauVatPham mau = sung.mau;
        int tuMau = mau == null
                ? -1 : layOptionDuong(mau.thuocTinhs, OPTION_NAP_DAN);
        return tuMau > 0 ? kep(tuMau) : MAC_DINH;
    }

    private static int layOptionDuong(Vector danhSach, int maOption) {
        if (danhSach == null) {
            return -1;
        }
        for (Object doiTuong : danhSach) {
            if (!(doiTuong instanceof ChickenThuocTinhVatPham)) {
                continue;
            }
            ChickenThuocTinhVatPham option =
                    (ChickenThuocTinhVatPham) doiTuong;
            if (option.optionTemplate != null
                    && option.optionTemplate.ma == maOption
                    && option.thamSo > 0) {
                return option.thamSo;
            }
        }
        return -1;
    }

    private static int kep(long giaTri) {
        return (int) Math.max(TOI_THIEU, Math.min(TOI_DA, giaTri));
    }
}
