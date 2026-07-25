package com.chicken.chien;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cau hinh sat thuong cua tung ID sung. Bang nay chi nam o server; client
 * khong duoc gui ban kinh no, he so giam damage hay quy tac che dia hinh.
 */
public final class ChickenCauHinhSatThuongSung {
    public enum KieuDuongCong {
        TUYEN_TINH,
        GIU_DAMAGE_GAN_TAM
    }

    private static final Map<Integer, HoSoSatThuong> THEO_ID_SUNG =
            new LinkedHashMap<Integer, HoSoSatThuong>();

    static {
        // AT: mot vien, vung no lon.
        dangKyKhoangNo(110, 119, 7, 40, 20, true);
        // Rifle/AK: vung no nho, uu tien trung truc tiep.
        dangKyKhoangNo(120, 129, 2, 10, 10, true);
        // MG: nam vien trong mot loat, moi vien chi co vung anh huong rat nho.
        dangKyKhoangNo(130, 139, 2, 8, 10, true);
        // Chuoi: bon vien chum.
        dangKyKhoangNo(140, 149, 4, 24, 15, true);
        // Flint/shotgun: ba vien, tranh cong splash qua lon.
        dangKyKhoangNo(150, 159, 2, 12, 10, true);
        // Coi/rocket: ba vien noi tiep, damage goc duoc chia cho tung vien.
        dangKyKhoangNo(160, 169, 7, 42, 20, true);
        // Ga co duong chinh va vien roi.
        dangKyKhoangNo(170, 179, 5, 32, 15, true);
        // Riu tach bon nhanh.
        dangKyKhoangNo(180, 189, 3, 18, 10, true);
        // Boomerang van co no diem cuoi nho; Captain ghi de o ben duoi.
        dangKyKhoangNo(190, 199, 4, 20, 15, true);
        // Laser la tia va cham, khong no theo khoang cach.
        dangKyKhoangTrucTiep(200, 209);

        dangKyNo(295, 8, 48, 20, true);

        // Vu khi thuong cua AVG. Skill rieng (Hawk/Iron Man/Ultron...) co bo
        // xu ly rieng va khong duoc tu dong bien thanh vu no.
        dangKyNo(391, 2, 10, 10, true);   // Iron Man thuong
        dangKyTrucTiep(392);              // Hulk la vien dan
        dangKyNo(393, 7, 40, 20, true);   // Thor thuong
        dangKyNo(394, 2, 10, 10, true);   // Loki thuong
        dangKyNo(395, 6, 24, 20, true);   // Captain: no diem cuoi + xuyen nguoi rieng
        dangKyTrucTiep(396);              // Winter Soldier xuyen map
        dangKyNo(397, 4, 24, 15, true);   // Hawk thuong
        dangKyTrucTiep(398);              // Ultron laze thang
        dangKyNo(400, 4, 24, 15, true);
        dangKyNo(401, 4, 24, 15, true);
    }

    private ChickenCauHinhSatThuongSung() {
    }

    private static void dangKyKhoangNo(
            int tuId,
            int denId,
            int banKinhDayDu,
            int banKinhNo,
            int phanTramToiThieu,
            boolean biDiaHinhChe
    ) {
        for (int id = tuId; id <= denId; id++) {
            dangKyNo(id, banKinhDayDu, banKinhNo, phanTramToiThieu, biDiaHinhChe);
        }
    }

    private static void dangKyKhoangTrucTiep(int tuId, int denId) {
        for (int id = tuId; id <= denId; id++) {
            dangKyTrucTiep(id);
        }
    }

    private static void dangKyNo(
            int idSung,
            int banKinhDayDu,
            int banKinhNo,
            int phanTramToiThieu,
            boolean biDiaHinhChe
    ) {
        dangKy(new HoSoSatThuong(
                idSung,
                true,
                banKinhDayDu,
                banKinhNo,
                phanTramToiThieu,
                KieuDuongCong.GIU_DAMAGE_GAN_TAM,
                biDiaHinhChe,
                6,
                14,
                70,
                35
        ));
    }

    private static void dangKyTrucTiep(int idSung) {
        dangKy(new HoSoSatThuong(
                idSung,
                false,
                0,
                0,
                100,
                KieuDuongCong.TUYEN_TINH,
                false,
                0,
                0,
                100,
                100
        ));
    }

    private static void dangKy(HoSoSatThuong hoSo) {
        if (THEO_ID_SUNG.put(hoSo.idSung, hoSo) != null) {
            throw new IllegalStateException("Trung cau hinh sat thuong sung ID=" + hoSo.idSung);
        }
    }

    public static HoSoSatThuong theoIdSung(int idSung) {
        return THEO_ID_SUNG.get(idSung);
    }

    public static Map<Integer, HoSoSatThuong> layTatCa() {
        return Collections.unmodifiableMap(THEO_ID_SUNG);
    }

    /** Goi luc khoi dong de sung moi khong bi roi ve cong thuc ngam dinh. */
    public static void kiemTraDayDu() {
        for (Integer idSung : ChickenQuanLyDanSung.layTatCa().keySet()) {
            if (!THEO_ID_SUNG.containsKey(idSung)) {
                throw new IllegalStateException(
                        "Thieu cau hinh sat thuong cho sung ID=" + idSung);
            }
        }
        for (Integer idSung : THEO_ID_SUNG.keySet()) {
            if (ChickenQuanLyDanSung.theoIdSung(idSung) == null) {
                throw new IllegalStateException(
                        "Cau hinh sat thuong tham chieu sung khong ton tai ID=" + idSung);
            }
        }
    }

    public static final class HoSoSatThuong {
        private final int idSung;
        private final boolean noTheoKhoangCach;
        private final int banKinhDayDu;
        private final int banKinhNo;
        private final int phanTramToiThieu;
        private final KieuDuongCong kieuDuongCong;
        private final boolean biDiaHinhChe;
        private final int doDayMongToiDa;
        private final int doDayVuaToiDa;
        private final int phanTramQuaTuongMong;
        private final int phanTramQuaTuongVua;

        private HoSoSatThuong(
                int idSung,
                boolean noTheoKhoangCach,
                int banKinhDayDu,
                int banKinhNo,
                int phanTramToiThieu,
                KieuDuongCong kieuDuongCong,
                boolean biDiaHinhChe,
                int doDayMongToiDa,
                int doDayVuaToiDa,
                int phanTramQuaTuongMong,
                int phanTramQuaTuongVua
        ) {
            if (idSung < 0
                    || banKinhDayDu < 0
                    || banKinhNo < banKinhDayDu
                    || phanTramToiThieu < 0
                    || phanTramToiThieu > 100
                    || kieuDuongCong == null
                    || doDayMongToiDa < 0
                    || doDayVuaToiDa < doDayMongToiDa
                    || phanTramQuaTuongMong < 0
                    || phanTramQuaTuongMong > 100
                    || phanTramQuaTuongVua < 0
                    || phanTramQuaTuongVua > phanTramQuaTuongMong) {
                throw new IllegalArgumentException("Cau hinh sat thuong sung khong hop le ID=" + idSung);
            }
            this.idSung = idSung;
            this.noTheoKhoangCach = noTheoKhoangCach;
            this.banKinhDayDu = banKinhDayDu;
            this.banKinhNo = banKinhNo;
            this.phanTramToiThieu = phanTramToiThieu;
            this.kieuDuongCong = kieuDuongCong;
            this.biDiaHinhChe = biDiaHinhChe;
            this.doDayMongToiDa = doDayMongToiDa;
            this.doDayVuaToiDa = doDayVuaToiDa;
            this.phanTramQuaTuongMong = phanTramQuaTuongMong;
            this.phanTramQuaTuongVua = phanTramQuaTuongVua;
        }

        public int getIdSung() { return this.idSung; }
        public boolean coNoTheoKhoangCach() { return this.noTheoKhoangCach; }
        public int getBanKinhDayDu() { return this.banKinhDayDu; }
        public int getBanKinhNo() { return this.banKinhNo; }
        public int getPhanTramToiThieu() { return this.phanTramToiThieu; }
        public KieuDuongCong getKieuDuongCong() { return this.kieuDuongCong; }
        public boolean biDiaHinhChe() { return this.biDiaHinhChe; }
        public int getDoDayMongToiDa() { return this.doDayMongToiDa; }
        public int getDoDayVuaToiDa() { return this.doDayVuaToiDa; }
        public int getPhanTramQuaTuongMong() { return this.phanTramQuaTuongMong; }
        public int getPhanTramQuaTuongVua() { return this.phanTramQuaTuongVua; }
    }
}
