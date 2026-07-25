package com.chicken.chien;

import com.chicken.chien.ChickenCauHinhSatThuongSung.HoSoSatThuong;
import com.chicken.chiso.ChickenKichThuocNhanVat;

/** Bo tinh damage no dung chung cho PvP, luyen tap va boss. */
public final class ChickenTinhSatThuongNo {
    private ChickenTinhSatThuongNo() {
    }

    /**
     * Duong cong giam damage: day du trong vung gan, sau do giam theo
     * 1 - t^2. Tai dung mep van con phan tram toi thieu; ngoai mep bang 0.
     */
    public static int tinhPhanTramTheoKhoangCach(
            HoSoSatThuong hoSo,
            double khoangCach
    ) {
        if (hoSo == null || Double.isNaN(khoangCach) || khoangCach < 0.0D) {
            return 0;
        }
        if (!hoSo.coNoTheoKhoangCach()) {
            return khoangCach <= 0.0D ? 100 : 0;
        }
        if (khoangCach <= hoSo.getBanKinhDayDu()) {
            return 100;
        }
        if (khoangCach > hoSo.getBanKinhNo()) {
            return 0;
        }
        int doRongVungGiam = hoSo.getBanKinhNo() - hoSo.getBanKinhDayDu();
        if (doRongVungGiam <= 0) {
            return hoSo.getPhanTramToiThieu();
        }
        double t = (khoangCach - hoSo.getBanKinhDayDu()) / doRongVungGiam;
        t = Math.max(0.0D, Math.min(1.0D, t));
        double phanConLai = switch (hoSo.getKieuDuongCong()) {
            case TUYEN_TINH -> 1.0D - t;
            case GIU_DAMAGE_GAN_TAM -> 1.0D - t * t;
        };
        double tiLe = hoSo.getPhanTramToiThieu()
                + (100.0D - hoSo.getPhanTramToiThieu()) * phanConLai;
        return Math.max(
                hoSo.getPhanTramToiThieu(),
                Math.min(100, (int) Math.round(tiLe))
        );
    }

    /** Lam tron dung mot lan sau khi da nhan falloff va he so tuong che. */
    public static int tinhSatThuong(
            HoSoSatThuong hoSo,
            int satThuongGoc,
            double khoangCach,
            int phanTramQuaDiaHinh
    ) {
        if (satThuongGoc <= 0 || hoSo == null) {
            return 0;
        }
        int phanTramKhoangCach = tinhPhanTramTheoKhoangCach(hoSo, khoangCach);
        return tinhSatThuongTheoPhanTram(
                satThuongGoc,
                phanTramKhoangCach,
                phanTramQuaDiaHinh
        );
    }

    private static int tinhSatThuongTheoPhanTram(
            int satThuongGoc,
            int phanTramKhoangCach,
            int phanTramQuaDiaHinh
    ) {
        int phanTramChe = Math.max(0, Math.min(100, phanTramQuaDiaHinh));
        if (satThuongGoc <= 0 || phanTramKhoangCach <= 0 || phanTramChe <= 0) {
            return 0;
        }
        long tuSo = (long) satThuongGoc * phanTramKhoangCach * phanTramChe;
        long ketQua = (tuSo + 5_000L) / 10_000L;
        if (ketQua <= 0L) {
            return 1;
        }
        return ketQua > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) ketQua;
    }

    public static int tinhSatThuongChoNhanVat(
            HoSoSatThuong hoSo,
            int satThuongGoc,
            int xNo,
            int yNo,
            int nhanVatX,
            int nhanVatY,
            boolean laBoss,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        if (hoSo == null || satThuongGoc <= 0) {
            return 0;
        }
        if (namNgoaiHopBaoBanKinh(
                hoSo,
                xNo,
                yNo,
                nhanVatX,
                nhanVatY,
                laBoss
        )) {
            return 0;
        }
        double khoangCach = laBoss
                ? ChickenKichThuocNhanVat.khoangCachDenBoss(
                        xNo, yNo, nhanVatX, nhanVatY)
                : ChickenKichThuocNhanVat.khoangCachDenNguoiChoi(
                        xNo, yNo, nhanVatX, nhanVatY);
        int phanTramKhoangCach = tinhPhanTramTheoKhoangCach(hoSo, khoangCach);
        if (phanTramKhoangCach <= 0) {
            return 0;
        }
        int phanTramQuaDiaHinh = tinhPhanTramQuaDiaHinh(
                hoSo,
                xNo,
                yNo,
                nhanVatX,
                nhanVatY,
                laBoss,
                banDo,
                khoangCach
        );
        return tinhSatThuongTheoPhanTram(
                satThuongGoc,
                phanTramKhoangCach,
                phanTramQuaDiaHinh
        );
    }

    /**
     * Broad phase re: chi dung phep so sanh hop, khong sqrt va khong doc tile map.
     * Hop duoc noi rong theo ban kinh no nen khong loai nham muc tieu hop le.
     */
    private static boolean namNgoaiHopBaoBanKinh(
            HoSoSatThuong hoSo,
            int xNo,
            int yNo,
            int nhanVatX,
            int nhanVatY,
            boolean laBoss
    ) {
        int nuaRong = laBoss
                ? ChickenKichThuocNhanVat.BOSS_NUA_RONG
                : ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
        int lechTren = laBoss
                ? ChickenKichThuocNhanVat.BOSS_LECH_TREN
                : ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_TREN;
        int lechDuoi = laBoss
                ? ChickenKichThuocNhanVat.BOSS_LECH_DUOI
                : ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_DUOI;
        int banKinh = hoSo.coNoTheoKhoangCach() ? hoSo.getBanKinhNo() : 0;

        long trai = (long) nhanVatX - nuaRong - banKinh;
        long phai = (long) nhanVatX + nuaRong + banKinh;
        long tren = (long) nhanVatY - lechTren - banKinh;
        long duoi = (long) nhanVatY - lechDuoi + banKinh;
        return xNo < trai || xNo > phai || yNo < tren || yNo > duoi;
    }

    public static int tinhPhanTramQuaDiaHinh(
            HoSoSatThuong hoSo,
            int xNo,
            int yNo,
            int nhanVatX,
            int nhanVatY,
            boolean laBoss,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo,
            double khoangCach
    ) {
        if (hoSo == null
                || !hoSo.biDiaHinhChe()
                || banDo == null
                || khoangCach <= hoSo.getBanKinhDayDu()) {
            return 100;
        }
        int nuaRong = laBoss
                ? ChickenKichThuocNhanVat.BOSS_NUA_RONG
                : ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
        int lechTren = laBoss
                ? ChickenKichThuocNhanVat.BOSS_LECH_TREN
                : ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_TREN;
        int lechDuoi = laBoss
                ? ChickenKichThuocNhanVat.BOSS_LECH_DUOI
                : ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_DUOI;

        int dichX = kep(xNo, nhanVatX - nuaRong, nhanVatX + nuaRong);
        int dichY = kep(yNo, nhanVatY - lechTren, nhanVatY - lechDuoi);
        int doDay = demPixelDiaHinhTrenDoan(
                xNo,
                yNo,
                dichX,
                dichY,
                hoSo.getBanKinhDayDu(),
                banDo,
                hoSo.getDoDayVuaToiDa() + 1
        );
        if (doDay <= 0) {
            return 100;
        }
        if (doDay <= hoSo.getDoDayMongToiDa()) {
            return hoSo.getPhanTramQuaTuongMong();
        }
        if (doDay <= hoSo.getDoDayVuaToiDa()) {
            return hoSo.getPhanTramQuaTuongVua();
        }
        return 0;
    }

    static int demPixelDiaHinhTrenDoan(
            int x1,
            int y1,
            int x2,
            int y2,
            int boQuaPixelDau,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo,
            int dungKhiDat
    ) {
        if (banDo == null) {
            return 0;
        }
        int dx = x2 - x1;
        int dy = y2 - y1;
        int soBuoc = Math.max(Math.abs(dx), Math.abs(dy));
        if (soBuoc <= 1) {
            return 0;
        }
        int doDay = 0;
        int xTruoc = Integer.MIN_VALUE;
        int yTruoc = Integer.MIN_VALUE;
        int boQua = Math.max(0, boQuaPixelDau);
        for (int buoc = 1; buoc < soBuoc; buoc++) {
            if (buoc <= boQua) {
                continue;
            }
            double tiLe = (double) buoc / (double) soBuoc;
            int x = (int) Math.round(x1 + dx * tiLe);
            int y = (int) Math.round(y1 + dy * tiLe);
            if (x == xTruoc && y == yTruoc) {
                continue;
            }
            xTruoc = x;
            yTruoc = y;
            if (x < 0 || y < 0 || x >= banDo.getWidth() || y >= banDo.getHeight()) {
                continue;
            }
            if (banDo.coVaCham((short) x, (short) y)) {
                doDay++;
                if (dungKhiDat > 0 && doDay >= dungKhiDat) {
                    break;
                }
            }
        }
        return doDay;
    }

    /** Kiem tra bat bien cong thuc ngay luc server khoi dong. */
    public static void tuKiemTra() {
        final int damageMau = 10_000;
        for (HoSoSatThuong hoSo : ChickenCauHinhSatThuongSung.layTatCa().values()) {
            int taiTam = tinhSatThuong(hoSo, damageMau, 0.0D, 100);
            if (taiTam != damageMau) {
                throw new IllegalStateException(
                        "Damage truc tiep khong dat 100% cho sung ID=" + hoSo.getIdSung());
            }
            if (!hoSo.coNoTheoKhoangCach()) {
                if (tinhSatThuong(hoSo, damageMau, 1.0D, 100) != 0) {
                    throw new IllegalStateException(
                            "Sung truc tiep bi ro damage no ID=" + hoSo.getIdSung());
                }
                continue;
            }
            int taiVungDayDu = tinhSatThuong(
                    hoSo, damageMau, hoSo.getBanKinhDayDu(), 100);
            int taiMep = tinhSatThuong(
                    hoSo, damageMau, hoSo.getBanKinhNo(), 100);
            int ngoaiMep = tinhSatThuong(
                    hoSo, damageMau, hoSo.getBanKinhNo() + 1.0D, 100);
            if (taiVungDayDu != damageMau
                    || taiMep != damageMau * hoSo.getPhanTramToiThieu() / 100
                    || ngoaiMep != 0) {
                throw new IllegalStateException(
                        "Sai bien damage no cho sung ID=" + hoSo.getIdSung());
            }
            int truoc = damageMau;
            for (int d = hoSo.getBanKinhDayDu(); d <= hoSo.getBanKinhNo(); d++) {
                int hienTai = tinhSatThuong(hoSo, damageMau, d, 100);
                if (hienTai > truoc || hienTai < 0) {
                    throw new IllegalStateException(
                            "Damage no khong giam don cho sung ID=" + hoSo.getIdSung());
                }
                truoc = hienTai;
            }
        }

        HoSoSatThuong hoSoAt = ChickenCauHinhSatThuongSung.theoIdSung(110);
        double khoangCachMau = ChickenKichThuocNhanVat.khoangCachDenNguoiChoi(
                0, 50, 40, 50);
        int khongTuong = tinhPhanTramQuaDiaHinh(
                hoSoAt, 0, 50, 40, 50, false,
                taoBanDoKiemTraTuong(0, -1), khoangCachMau);
        int tuongMong = tinhPhanTramQuaDiaHinh(
                hoSoAt, 0, 50, 40, 50, false,
                taoBanDoKiemTraTuong(10, 12), khoangCachMau);
        int tuongVua = tinhPhanTramQuaDiaHinh(
                hoSoAt, 0, 50, 40, 50, false,
                taoBanDoKiemTraTuong(10, 18), khoangCachMau);
        int tuongDay = tinhPhanTramQuaDiaHinh(
                hoSoAt, 0, 50, 40, 50, false,
                taoBanDoKiemTraTuong(8, 23), khoangCachMau);
        if (khongTuong != 100 || tuongMong != 70 || tuongVua != 35 || tuongDay != 0) {
            throw new IllegalStateException("Sai quy tac giam damage theo do day dia hinh");
        }

        final int[] soLanDocVaCham = {0};
        ChickenQuanLyCongThucSung.KiemTraBanDo banDoDem =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() { return 2_000; }

                    @Override
                    public int getHeight() { return 2_000; }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        soLanDocVaCham[0]++;
                        return false;
                    }
                };
        int damageNgoaiBanKinh = tinhSatThuongChoNhanVat(
                hoSoAt,
                damageMau,
                0,
                50,
                1_000,
                50,
                false,
                banDoDem
        );
        if (damageNgoaiBanKinh != 0 || soLanDocVaCham[0] != 0) {
            throw new IllegalStateException(
                    "Muc tieu ngoai ban kinh van bi do dia hinh");
        }
    }

    private static ChickenQuanLyCongThucSung.KiemTraBanDo taoBanDoKiemTraTuong(
            final int tuX,
            final int denX
    ) {
        return new ChickenQuanLyCongThucSung.KiemTraBanDo() {
            @Override
            public int getWidth() { return 100; }

            @Override
            public int getHeight() { return 100; }

            @Override
            public boolean coVaCham(short x, short y) {
                return x >= tuX && x <= denX;
            }
        };
    }

    private static int kep(int giaTri, int nhoNhat, int lonNhat) {
        return Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }
}
