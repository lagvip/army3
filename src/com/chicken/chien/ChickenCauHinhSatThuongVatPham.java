package com.chicken.chien;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Can bang damage vat pham chien dau nam hoan toan o server.
 *
 * Moi item chi duoc dua vao runtime sau khi co ho so tai day; co cong thuc
 * bay nhung thieu damage se bi tu choi thay vi roi ve mot gia tri ngam dinh.
 */
public final class ChickenCauHinhSatThuongVatPham {
    /** Item thay the phat ban deu nap lai co dinh 300, khong theo sung. */
    public static final int NAP_DAN_VAT_PHAM_TAO_DAN = 300;

    public enum HieuUngDacBiet {
        KHONG,
        DICH_CHUYEN_TUC_THOI,
        TAO_MANG_NHEN,
        HOI_MAU_BAN_THAN,
        HOI_MAU_DONG_DOI,
        NHAN_DOI_DI_CHUYEN,
        NGUNG_GIO,
        PHA_DIA_HINH,
        TAO_VOI_RONG
    }

    private static final Map<Integer, HoSo> THEO_ID =
            new LinkedHashMap<Integer, HoSo>();

    static {
        // Cuu thuong ca nhan: dung xong van ban sung, toi da hai lan/tran.
        dangKy(new HoSo(
                220,
                0,
                -1,
                2,
                null,
                HieuUngDacBiet.HOI_MAU_BAN_THAN
        ));

        // Dich chuyen tuc thoi: vien chi diem type 5 do server mo phong;
        // client chi ve duong bay, toa do dich chuyen lay tu diem cuoi server.
        dangKy(new HoSo(
                221,
                0,
                NAP_DAN_VAT_PHAM_TAO_DAN,
                2,
                null,
                HieuUngDacBiet.DICH_CHUYEN_TUC_THOI
        ));

        // Di chuyen x2 ton tai suot tran. Dung xong van duoc ban sung va
        // khong ghi de toc do nap dan cua khau dang cam.
        dangKy(new HoSo(
                223,
                0,
                -1,
                1,
                null,
                HieuUngDacBiet.NHAN_DOI_DI_CHUYEN
        ));

        // Ngung gio tac dong ngay len luot hien tai, sau do nguoi choi van
        // duoc ban sung. Moi lan kich hoat tru mot item, toi da ba lan/tran.
        dangKy(new HoSo(
                225,
                0,
                -1,
                3,
                null,
                HieuUngDacBiet.NGUNG_GIO
        ));

        // Bom pha dat: ba path chong khit de hien thi thanh mot vien khoan.
        // Chi dia hinh pha duoc moi cho no tiep lan 2/3; moi muc tieu van chi
        // nhan damage theo diem no manh nhat, khong cong don ca ba lan.
        dangKy(new HoSo(
                226,
                80,
                NAP_DAN_VAT_PHAM_TAO_DAN,
                1,
                ChickenCauHinhSatThuongSung.taoHoSoNoRieng(
                        20_226,
                        8,
                        48,
                        15,
                        true
                ),
                HieuUngDacBiet.PHA_DIA_HINH
        ));

        // Luu dan: suc huy diet cao, no rong hon AT thuong mot chut.
        dangKy(new HoSo(
                227,
                125,
                NAP_DAN_VAT_PHAM_TAO_DAN,
                1,
                ChickenCauHinhSatThuongSung.taoHoSoNoRieng(
                        20_227,
                        10,
                        52,
                        20,
                        true
                )
        ));

        // Bom B52: damage moi muc tieu thap hon Luu dan mot chut, doi lai
        // vung no rong. Day la cau hinh can bang server, khong phai client.
        dangKy(new HoSo(
                228,
                115,
                NAP_DAN_VAT_PHAM_TAO_DAN,
                1,
                ChickenCauHinhSatThuongSung.taoHoSoNoRieng(
                        20_228,
                        18,
                        78,
                        15,
                        true
                )
        ));

        // To nhan khong gay damage. Client ve mang nhan tai diem roi;
        // server them cung mat na va cham de dia hinh van authoritative.
        dangKy(new HoSo(
                229,
                0,
                NAP_DAN_VAT_PHAM_TAO_DAN,
                1,
                null,
                HieuUngDacBiet.TAO_MANG_NHEN
        ));

        // Cuu thuong khong tao dan. Hieu ung va danh sach dong doi deu do
        // server tinh ngay khi chap nhan CMD 26; van dung chung quota item.
        dangKy(new HoSo(
                230,
                0,
                -1,
                1,
                null,
                HieuUngDacBiet.HOI_MAU_DONG_DOI
        ));

        // Dan trai pha: vien dau chi danh dau vi tri, sau do client tao sau
        // vien coi type 12 roi tu tren cao. Moi muc tieu chi nhan damage cua
        // diem no manh nhat de khong bi cong chong damage x6.
        dangKy(new HoSo(
                231,
                90,
                NAP_DAN_VAT_PHAM_TAO_DAN,
                1,
                ChickenCauHinhSatThuongSung.taoHoSoNoRieng(
                        20_231,
                        8,
                        44,
                        15,
                        true
                ),
                HieuUngDacBiet.PHA_DIA_HINH
        ));

        // Dan Lazer: vien danh dau type 14 bay den diem no, sau do client
        // native tao tia type 15 tai chinh diem server da xac nhan. Damage,
        // va cham va pha dia hinh van hoan toan do server tinh.
        dangKy(new HoSo(
                235,
                110,
                NAP_DAN_VAT_PHAM_TAO_DAN,
                1,
                ChickenCauHinhSatThuongSung.taoHoSoNoRieng(
                        20_235,
                        10,
                        58,
                        20,
                        true
                ),
                HieuUngDacBiet.PHA_DIA_HINH
        ));

        // Dan voi rong khong gay damage va khong pha dia hinh. Diem roi
        // authoritative tao cot loc lam lech quy dao trong ba luot tiep theo.
        dangKy(new HoSo(
                236,
                0,
                NAP_DAN_VAT_PHAM_TAO_DAN,
                1,
                null,
                HieuUngDacBiet.TAO_VOI_RONG
        ));

        // Chuot gan bom: client type 22 ve mot con chuot chay theo path
        // server, het duong se bung ba cum no quanh tam. Damage va diem no
        // van do server tinh; vung no lon de dung voi mo ta "suc huy diet cao".
        dangKy(new HoSo(
                237,
                150,
                NAP_DAN_VAT_PHAM_TAO_DAN,
                1,
                ChickenCauHinhSatThuongSung.taoHoSoNoRieng(
                        20_237,
                        20,
                        90,
                        20,
                        true
                )
        ));

        // Ten lua x4: type 26 la vien mo dau, sau do client native tao bon
        // ten lua type 27 tu path 1..4. 180% la tong damage khi ca bon vien
        // no sat muc tieu; server chia deu damage nay cho tung duong dan.
        dangKy(new HoSo(
                238,
                180,
                NAP_DAN_VAT_PHAM_TAO_DAN,
                1,
                ChickenCauHinhSatThuongSung.taoHoSoNoRieng(
                        20_238,
                        10,
                        52,
                        20,
                        true
                )
        ));

        // Dan xuyen dat: type 25 dao mot duong xuyen qua terrain va chi no
        // khi cham hitbox. Damage cao hon Luu dan, ban kinh no o muc vua de
        // khong bien kha nang xuyen map thanh damage toan khu vuc.
        dangKy(new HoSo(
                239,
                150,
                NAP_DAN_VAT_PHAM_TAO_DAN,
                1,
                ChickenCauHinhSatThuongSung.taoHoSoNoRieng(
                        20_239,
                        10,
                        58,
                        20,
                        true
                )
        ));
    }

    private ChickenCauHinhSatThuongVatPham() {
    }

    private static void dangKy(HoSo hoSo) {
        if (hoSo == null || THEO_ID.putIfAbsent(
                hoSo.idVatPham, hoSo) != null) {
            throw new IllegalStateException("Trung cau hinh damage vat pham");
        }
    }

    public static HoSo theoIdVatPham(int idVatPham) {
        return THEO_ID.get(idVatPham);
    }

    public static Map<Integer, HoSo> layTatCa() {
        return Collections.unmodifiableMap(THEO_ID);
    }

    public static final class HoSo {
        private final int idVatPham;
        private final int phanTramTanCong;
        private final int napDanSauDung;
        private final int soLanToiDaMoiTran;
        private final ChickenCauHinhSatThuongSung.HoSoSatThuong hoSoNo;
        private final HieuUngDacBiet hieuUngDacBiet;

        private HoSo(
                int idVatPham,
                int phanTramTanCong,
                int napDanSauDung,
                int soLanToiDaMoiTran,
                ChickenCauHinhSatThuongSung.HoSoSatThuong hoSoNo
        ) {
            this(idVatPham, phanTramTanCong, napDanSauDung,
                    soLanToiDaMoiTran, hoSoNo, HieuUngDacBiet.KHONG);
        }

        private HoSo(
                int idVatPham,
                int phanTramTanCong,
                int napDanSauDung,
                int soLanToiDaMoiTran,
                ChickenCauHinhSatThuongSung.HoSoSatThuong hoSoNo,
                HieuUngDacBiet hieuUngDacBiet
        ) {
            boolean coDamage = phanTramTanCong > 0 && hoSoNo != null;
            boolean coHieuUng = hieuUngDacBiet != null
                    && hieuUngDacBiet != HieuUngDacBiet.KHONG;
            boolean laItemTucThoi = hieuUngDacBiet
                    == HieuUngDacBiet.HOI_MAU_BAN_THAN
                    || hieuUngDacBiet
                            == HieuUngDacBiet.HOI_MAU_DONG_DOI
                    || hieuUngDacBiet
                            == HieuUngDacBiet.NHAN_DOI_DI_CHUYEN
                    || hieuUngDacBiet == HieuUngDacBiet.NGUNG_GIO;
            if (idVatPham < 0 || phanTramTanCong < 0
                    || (laItemTucThoi && napDanSauDung != -1)
                    || (!laItemTucThoi
                            && napDanSauDung != NAP_DAN_VAT_PHAM_TAO_DAN)
                    || soLanToiDaMoiTran <= 0
                    || (phanTramTanCong == 0) != (hoSoNo == null)
                    || (!coDamage && !coHieuUng)) {
                throw new IllegalArgumentException(
                        "Cau hinh damage vat pham khong hop le");
            }
            this.idVatPham = idVatPham;
            this.phanTramTanCong = phanTramTanCong;
            this.napDanSauDung = napDanSauDung;
            this.soLanToiDaMoiTran = soLanToiDaMoiTran;
            this.hoSoNo = hoSoNo;
            this.hieuUngDacBiet = hieuUngDacBiet;
        }

        public int getIdVatPham() {
            return this.idVatPham;
        }

        public int getPhanTramTanCong() {
            return this.phanTramTanCong;
        }

        public int getNapDanSauDung() {
            return this.napDanSauDung;
        }

        public int getSoLanToiDaMoiTran() {
            return this.soLanToiDaMoiTran;
        }

        public ChickenCauHinhSatThuongSung.HoSoSatThuong getHoSoNo() {
            return this.hoSoNo;
        }

        public boolean coSatThuongNo() {
            return this.phanTramTanCong > 0 && this.hoSoNo != null;
        }

        public HieuUngDacBiet getHieuUngDacBiet() {
            return this.hieuUngDacBiet;
        }
    }
}
