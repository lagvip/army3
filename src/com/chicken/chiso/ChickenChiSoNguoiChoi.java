package com.chicken.chiso;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.tiemnang.ChickenQuanLyTiemNang;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.vatpham.ChickenYeuCauCapVatPham;
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
    /** Cự ly/thể lực di chuyển cộng theo phần trăm trên trang bị. */
    private static final int OPTION_CU_LY_DI_CHUYEN_PHAN_TRAM = 26;

    private ChickenChiSoNguoiChoi() {
    }

    public static int tinhMau(ChickenNguoiChoi nguoiChoi) {
        return tinhChiSo(nguoiChoi, OPTION_MAU, OPTION_MAU_PHAN_TRAM,
                ChickenQuanLyTiemNang.MAU, -1);
    }

    public static int tinhTanCong(ChickenNguoiChoi nguoiChoi) {
        ChickenVatPham sung = laySungHopLe(nguoiChoi);
        return tinhTanCongVoiSung(nguoiChoi, sung);
    }

    /**
     * Tinh lai tan cong theo khau sung server dang cho phep cam trong tran.
     * Sung trong Balo khong co chiSo=5, vi vay khong duoc dung phep kiem tra
     * slot trang bi cho rieng khau sung thay the. Cac mon do con lai van phai
     * nam dung slot itemBody nhu binh thuong.
     */
    public static int tinhTanCongVoiSung(
            ChickenNguoiChoi nguoiChoi,
            ChickenVatPham sung
    ) {
        if (!laSungHopLe(sung)) {
            sung = null;
        }
        int optionTheoLoaiSung = sung == null ? -1 : layOptionTanCongTheoLoaiSung(sung.mau);
        return tinhChiSo(nguoiChoi, OPTION_TAN_CONG,
                OPTION_TAN_CONG_PHAN_TRAM,
                ChickenQuanLyTiemNang.TAN_CONG,
                optionTheoLoaiSung,
                sung);
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
     * Lấy tổng option 26 từ bộ trang bị thật của người chơi. Đây là phần trăm
     * cộng vào thể lực di chuyển cơ bản, không phải chỉ số Tốc độ tiềm năng.
     */
    public static int tinhPhanTramCuLyDiChuyen(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null || nguoiChoi.itemBody == null) {
            return 0;
        }
        long phanTram = 0L;
        for (int i = 0; i < nguoiChoi.itemBody.length; i++) {
            ChickenVatPham trangBi = nguoiChoi.itemBody[i];
            if (!trangBiHopLe(nguoiChoi, trangBi, i)) {
                continue;
            }
            long[] cong = layChiSoTuTrangBi(
                    trangBi,
                    OPTION_CU_LY_DI_CHUYEN_PHAN_TRAM,
                    -1,
                    -1
            );
            phanTram = congBaoHoa(phanTram, cong[0]);
        }
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, phanTram));
    }

    /**
     * Bot hiển thị bộ AVG nhưng không có inventory. Dùng template AVG tương
     * ứng (391..398) để lấy đúng option 26 thay vì mặc định mọi AVG đều +100%.
     */
    public static int tinhPhanTramCuLyDiChuyenTheoAvenger(byte avenger) {
        int chiSoAvenger = avenger & 0xFF;
        if (chiSoAvenger < 1 || chiSoAvenger > 8) {
            return 0;
        }
        ChickenMauVatPham mauAvenger = ChickenQuanLyMayChu.itemTemplates.get(
                390 + chiSoAvenger);
        if (mauAvenger == null) {
            return 0;
        }
        long[] cong = layChiSoTuDanhSach(
                mauAvenger.thuocTinhs,
                OPTION_CU_LY_DI_CHUYEN_PHAN_TRAM,
                -1,
                -1,
                false,
                null,
                false
        );
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, cong[0]));
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
        return tinhChiSo(nguoiChoi, optionCongThang, optionPhanTram,
                chiSoTiemNang, optionTheoLoaiSung, null);
    }

    private static int tinhChiSo(ChickenNguoiChoi nguoiChoi,
            int optionCongThang, int optionPhanTram,
            int chiSoTiemNang, int optionTheoLoaiSung,
            ChickenVatPham sungThayThe) {
        if (nguoiChoi == null) {
            return 0;
        }

        long coDinh = ChickenQuanLyTiemNang.layGiaTri(nguoiChoi, chiSoTiemNang);
        long phanTram = 0L;

        if (nguoiChoi.itemBody != null) {
            for (int i = 0; i < nguoiChoi.itemBody.length; i++) {
                ChickenVatPham trangBi = i == 5 && sungThayThe != null
                        ? sungThayThe : nguoiChoi.itemBody[i];
                boolean hopLe = i == 5 && sungThayThe != null
                        ? laSungHopLe(trangBi)
                                && ChickenYeuCauCapVatPham.datYeuCau(
                                        nguoiChoi.cap, trangBi)
                        : trangBiHopLe(nguoiChoi, trangBi, i);
                if (!hopLe) {
                    continue;
                }
                long[] cong = layChiSoTuTrangBi(trangBi,
                        optionCongThang, optionPhanTram, optionTheoLoaiSung);
                coDinh = congBaoHoa(coDinh, cong[0]);
                phanTram = congBaoHoa(phanTram, cong[1]);
            }
        }

        coDinh = Math.max(0L, coDinh);
        long phanThuong = nhanChiaTramBaoHoa(
                coDinh, Math.max(0L, phanTram));
        long tong = congBaoHoa(coDinh, phanThuong);
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, tong));
    }

    private static ChickenVatPham laySungHopLe(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null || nguoiChoi.itemBody == null || nguoiChoi.itemBody.length <= 5) {
            return null;
        }
        ChickenVatPham sung = nguoiChoi.itemBody[5];
        return trangBiHopLe(nguoiChoi, sung, 5)
                && laSungHopLe(sung) ? sung : null;
    }

    private static boolean laSungHopLe(ChickenVatPham sung) {
        return sung != null && sung.mau != null && sung.mau.loai == 5
                && sung.HP > 0;
    }

    private static boolean trangBiHopLe(
            ChickenNguoiChoi nguoiChoi,
            ChickenVatPham vatPham,
            int viTri
    ) {
        return vatPham != null && vatPham.mau != null
                && vatPham.chiSo == viTri
                && vatPham.mau.loai == viTri
                && nguoiChoi != null
                && ChickenYeuCauCapVatPham.datYeuCau(
                        nguoiChoi.cap, vatPham);
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
            coDinh = congBaoHoa(coDinh, tuItem[0]);
            phanTram = congBaoHoa(phanTram, tuItem[1]);
        }

        if (trangBi.mau != null && trangBi.mau.thuocTinhs != null) {
            long[] tuMau = layChiSoTuDanhSach(trangBi.mau.thuocTinhs,
                    optionCongThang, optionPhanTram, optionTheoLoaiSung,
                    false, optionDaCo, true);
            coDinh = congBaoHoa(coDinh, tuMau[0]);
            phanTram = congBaoHoa(phanTram, tuMau[1]);
        }
        return new long[]{coDinh, phanTram};
    }

    private static long[] layChiSoTuDanhSach(Vector danhSach,
            int optionCongThang, int optionPhanTram, int optionTheoLoaiSung,
            boolean docNgoc, Set<Integer> optionDaCo, boolean chiDocOptionConThieu) {
        long coDinh = 0L;
        long phanTram = 0L;
        int soSocketDaDoc = 0;
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
                coDinh = congBaoHoa(coDinh, thamSo);
            } else if (maThuocTinh == optionPhanTram
                    || maThuocTinh == OPTION_PHAN_TRAM_CHUNG
                    || (optionTheoLoaiSung >= 0 && maThuocTinh == optionTheoLoaiSung)) {
                phanTram = congBaoHoa(phanTram, thamSo);
            } else if (docNgoc && maThuocTinh == OPTION_SOCKET
                    && thamSo > 0
                    && soSocketDaDoc < ChickenVatPham.SO_SOCKET_TOI_DA) {
                soSocketDaDoc++;
                ChickenMauVatPham mauNgoc = ChickenQuanLyMayChu.itemTemplates.get(thamSo);
                if (mauNgoc != null && mauNgoc.loai == 12) {
                    long[] congNgoc = layChiSoTuDanhSach(mauNgoc.thuocTinhs,
                            optionCongThang, optionPhanTram, optionTheoLoaiSung,
                            false, null, false);
                    coDinh = congBaoHoa(coDinh, congNgoc[0]);
                    phanTram = congBaoHoa(phanTram, congNgoc[1]);
                }
            }
        }
        return new long[]{coDinh, phanTram};
    }

    private static long congBaoHoa(long a, long b) {
        if (a < 0L || b < 0L || Long.MAX_VALUE - a < b) {
            return Long.MAX_VALUE;
        }
        return a + b;
    }

    private static long nhanChiaTramBaoHoa(long giaTri, long phanTram) {
        if (giaTri <= 0L || phanTram <= 0L) {
            return 0L;
        }
        if (giaTri > Long.MAX_VALUE / phanTram) {
            return Long.MAX_VALUE;
        }
        return giaTri * phanTram / 100L;
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
