package com.chicken.chiso;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.tiemnang.ChickenQuanLyTiemNang;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenVatPham;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Nơi duy nhất tính chỉ số thực tế của người chơi.
 *
 * Công thức chung:
 *   tổng cố định = tiềm năng + đồ mặc + ngọc/socket
 *   kết quả = tổng cố định + tổng cố định * tổng phần trăm / 100
 *
 * Thứ tự pointAdd theo packet/client: Máu, Giáp, Tấn công, May mắn, Đồng đội, Tốc độ.
 */
public final class ChickenChiSoNguoiChoi {
    private static final int OPTION_MAU = 0;
    private static final int OPTION_TAN_CONG = 1;
    private static final int OPTION_GIAP = 2;
    private static final int OPTION_MAY_MAN = 3;
    private static final int OPTION_DONG_DOI = 4;
    private static final int OPTION_TOC_DO = 5;

    private static final int OPTION_MAU_PHAN_TRAM = 6;
    private static final int OPTION_TAN_CONG_PHAN_TRAM = 7;
    private static final int OPTION_GIAP_PHAN_TRAM = 8;
    private static final int OPTION_MAY_MAN_PHAN_TRAM = 9;
    private static final int OPTION_DONG_DOI_PHAN_TRAM = 10;
    private static final int OPTION_TOC_DO_PHAN_TRAM = 11;

    private static final int OPTION_SOCKET = 16;
    private static final int OPTION_PHAN_TRAM_CHUNG = 18;

    private ChickenChiSoNguoiChoi() {
    }

    public static int tinhMau(ChickenNguoiChoi nguoiChoi) {
        return tinhChiSo(nguoiChoi, OPTION_MAU, OPTION_MAU_PHAN_TRAM,
                ChickenQuanLyTiemNang.MAU, -1);
    }

    public static int tinhTanCong(ChickenNguoiChoi nguoiChoi) {
        ChickenVatPham sung = laySungHopLe(nguoiChoi);
        int optionTheoLoaiSung = sung == null ? -1 : layOptionTanCongTheoLoaiSung(sung.mau);
        return tinhChiSo(nguoiChoi, OPTION_TAN_CONG, OPTION_TAN_CONG_PHAN_TRAM,
                ChickenQuanLyTiemNang.TAN_CONG, optionTheoLoaiSung);
    }

    public static int tinhGiap(ChickenNguoiChoi nguoiChoi) {
        return tinhChiSo(nguoiChoi, OPTION_GIAP, OPTION_GIAP_PHAN_TRAM,
                ChickenQuanLyTiemNang.GIAP, -1);
    }

    public static int tinhMayMan(ChickenNguoiChoi nguoiChoi) {
        return tinhChiSo(nguoiChoi, OPTION_MAY_MAN, OPTION_MAY_MAN_PHAN_TRAM,
                ChickenQuanLyTiemNang.MAY_MAN, -1);
    }

    public static int tinhDongDoi(ChickenNguoiChoi nguoiChoi) {
        return tinhChiSo(nguoiChoi, OPTION_DONG_DOI, OPTION_DONG_DOI_PHAN_TRAM,
                ChickenQuanLyTiemNang.DONG_DOI, -1);
    }

    public static int tinhTocDo(ChickenNguoiChoi nguoiChoi) {
        return tinhChiSo(nguoiChoi, OPTION_TOC_DO, OPTION_TOC_DO_PHAN_TRAM,
                ChickenQuanLyTiemNang.TOC_DO, -1);
    }

    /**
     * Chỉ tính phần giảm nạp đạn từ điểm tiềm năng Tốc độ đã cộng.
     * Cứ đủ 3 điểm Tốc độ thì giảm 1 đơn vị nạp đạn.
     * Trang bị/ngọc Tốc độ không đi vào công thức này.
     */
    public static int tinhGiamNapDanTuTiemNang(ChickenNguoiChoi nguoiChoi) {
        int diemTocDo = ChickenQuanLyTiemNang.layGiaTri(
                nguoiChoi, ChickenQuanLyTiemNang.TOC_DO);
        return Math.max(0, diemTocDo / 3);
    }

    private static int tinhChiSo(ChickenNguoiChoi nguoiChoi,
            int optionCongThang, int optionPhanTram,
            int chiSoTiemNang, int optionTheoLoaiSung) {
        if (nguoiChoi == null) {
            return 0;
        }

        long coDinh = ChickenQuanLyTiemNang.layGiaTri(nguoiChoi, chiSoTiemNang);
        long phanTram = 0L;

        if (nguoiChoi.itemBody != null) {
            for (int i = 0; i < nguoiChoi.itemBody.length; i++) {
                ChickenVatPham trangBi = nguoiChoi.itemBody[i];
                if (!trangBiHopLe(trangBi, i)) {
                    continue;
                }
                long[] cong = layChiSoTuTrangBi(trangBi,
                        optionCongThang, optionPhanTram, optionTheoLoaiSung);
                coDinh += cong[0];
                phanTram += cong[1];
            }
        }

        coDinh = Math.max(0L, coDinh);
        long tong = coDinh + coDinh * Math.max(0L, phanTram) / 100L;
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, tong));
    }

    private static ChickenVatPham laySungHopLe(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null || nguoiChoi.itemBody == null || nguoiChoi.itemBody.length <= 5) {
            return null;
        }
        ChickenVatPham sung = nguoiChoi.itemBody[5];
        return trangBiHopLe(sung, 5) && sung.mau.loai == 5 ? sung : null;
    }

    private static boolean trangBiHopLe(ChickenVatPham vatPham, int viTri) {
        return vatPham != null && vatPham.mau != null
                && vatPham.chiSo == viTri && vatPham.mau.loai == viTri;
    }

    private static long[] layChiSoTuTrangBi(ChickenVatPham trangBi,
            int optionCongThang, int optionPhanTram, int optionTheoLoaiSung) {
        long coDinh = 0L;
        long phanTram = 0L;
        Set<Integer> optionDaCo = new HashSet<Integer>();

        if (trangBi.itemOptions != null) {
            long[] tuItem = layChiSoTuDanhSach(trangBi.itemOptions,
                    optionCongThang, optionPhanTram, optionTheoLoaiSung,
                    true, optionDaCo, false);
            coDinh += tuItem[0];
            phanTram += tuItem[1];
        }

        if (trangBi.mau != null && trangBi.mau.thuocTinhs != null) {
            long[] tuMau = layChiSoTuDanhSach(trangBi.mau.thuocTinhs,
                    optionCongThang, optionPhanTram, optionTheoLoaiSung,
                    false, optionDaCo, true);
            coDinh += tuMau[0];
            phanTram += tuMau[1];
        }
        return new long[]{coDinh, phanTram};
    }

    private static long[] layChiSoTuDanhSach(Vector danhSach,
            int optionCongThang, int optionPhanTram, int optionTheoLoaiSung,
            boolean docNgoc, Set<Integer> optionDaCo, boolean chiDocOptionConThieu) {
        long coDinh = 0L;
        long phanTram = 0L;
        if (danhSach == null) {
            return new long[]{0L, 0L};
        }

        for (Object doiTuong : danhSach) {
            if (!(doiTuong instanceof ChickenThuocTinhVatPham)) {
                continue;
            }
            ChickenThuocTinhVatPham thuocTinh = (ChickenThuocTinhVatPham) doiTuong;
            if (thuocTinh.optionTemplate == null) {
                continue;
            }

            int maThuocTinh = thuocTinh.optionTemplate.ma;
            if (chiDocOptionConThieu && optionDaCo != null && optionDaCo.contains(maThuocTinh)) {
                continue;
            }
            if (!chiDocOptionConThieu && optionDaCo != null && maThuocTinh != OPTION_SOCKET) {
                optionDaCo.add(maThuocTinh);
            }

            int thamSo = Math.max(0, thuocTinh.thamSo);
            if (maThuocTinh == optionCongThang) {
                coDinh += thamSo;
            } else if (maThuocTinh == optionPhanTram
                    || maThuocTinh == OPTION_PHAN_TRAM_CHUNG
                    || (optionTheoLoaiSung >= 0 && maThuocTinh == optionTheoLoaiSung)) {
                phanTram += thamSo;
            } else if (docNgoc && maThuocTinh == OPTION_SOCKET && thamSo > 0) {
                ChickenMauVatPham mauNgoc = ChickenQuanLyMayChu.itemTemplates.get(thamSo);
                if (mauNgoc != null && mauNgoc.loai == 12) {
                    long[] congNgoc = layChiSoTuDanhSach(mauNgoc.thuocTinhs,
                            optionCongThang, optionPhanTram, optionTheoLoaiSung,
                            false, null, false);
                    coDinh += congNgoc[0];
                    phanTram += congNgoc[1];
                }
            }
        }
        return new long[]{coDinh, phanTram};
    }

    private static int layOptionTanCongTheoLoaiSung(ChickenMauVatPham sung) {
        if (sung == null) {
            return -1;
        }
        switch (sung.gioiTinh) {
            case 0: return 21;
            case 1: return 22;
            case 5: return 23;
            case 3: return 24;
            case 2: return 25;
            default: return -1;
        }
    }
}
