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
    private static final Map<Integer, HoSo> THEO_ID =
            new LinkedHashMap<Integer, HoSo>();

    static {
        // Luu dan: suc huy diet cao, no rong hon AT thuong mot chut.
        dangKy(new HoSo(
                227,
                125,
                ChickenCauHinhSatThuongSung.taoHoSoNoRieng(
                        20_227,
                        10,
                        52,
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
        private final ChickenCauHinhSatThuongSung.HoSoSatThuong hoSoNo;

        private HoSo(
                int idVatPham,
                int phanTramTanCong,
                ChickenCauHinhSatThuongSung.HoSoSatThuong hoSoNo
        ) {
            if (idVatPham < 0 || phanTramTanCong <= 0 || hoSoNo == null) {
                throw new IllegalArgumentException(
                        "Cau hinh damage vat pham khong hop le");
            }
            this.idVatPham = idVatPham;
            this.phanTramTanCong = phanTramTanCong;
            this.hoSoNo = hoSoNo;
        }

        public int getIdVatPham() {
            return this.idVatPham;
        }

        public int getPhanTramTanCong() {
            return this.phanTramTanCong;
        }

        public ChickenCauHinhSatThuongSung.HoSoSatThuong getHoSoNo() {
            return this.hoSoNo;
        }
    }
}
