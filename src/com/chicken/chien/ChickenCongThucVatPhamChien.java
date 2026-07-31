package com.chicken.chien;

import com.chicken.vatpham.ChickenMauVatPham;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bang authoritative cho cac vat pham tao dan/duong ngam trong chien dau.
 *
 * Du lieu duoc doi chieu tu hai nhanh goc cua client:
 * - CPlayer.shoot(): ma su dung -> bulletType;
 * - CPlayer.drawBullLine(): he so gio, trong luc va dang duong can goc.
 *
 * Ma su dung trong packet chi la intent. Noi xu ly tran dau van phai tim dung
 * vat pham server dang giu, kiem tra so luong/cooldown/luot va tru kho truoc
 * khi dung cau hinh nay. Tuyet doi khong tra cuu bang ma client gui roi tu dong
 * tao dan.
 */
public final class ChickenCongThucVatPhamChien {

    public enum KieuQuyDao {
        PARABOL,
        THEO_SUNG_DANG_CAM,
        NHAN_DOI_SO_PHAT_SUNG,
        LAZER,
        VOI_RONG,
        CHUOT_GAN_BOM,
        TEN_LUA_X4,
        XUYEN_DAT,
        MUA_SAO_BANG,
        MUA_DAN,
        KHOAN_DAT,
        TU_NO,
        UFO_HO_TRO,
        LECH_DUONG_DAN
    }

    public enum KieuGoc {
        TU_DO,
        THEO_SUNG_DANG_CAM,
        XUYEN_DAT,
        THANG_DUNG,
        KHONG_CAN_GOC
    }

    public static final class CauHinh {
        private final int idVatPham;
        private final int maSuDung;
        private final byte loaiDan;
        private final KieuQuyDao kieuQuyDao;
        private final KieuGoc kieuGoc;
        private final boolean coHeSoCoDinh;
        private final int heSoGio;
        private final int trongLuc;
        private final int[] lechGoc;

        private CauHinh(
                int idVatPham,
                int maSuDung,
                int loaiDan,
                KieuQuyDao kieuQuyDao,
                KieuGoc kieuGoc,
                boolean coHeSoCoDinh,
                int heSoGio,
                int trongLuc,
                int[] lechGoc
        ) {
            this.idVatPham = idVatPham;
            this.maSuDung = maSuDung;
            this.loaiDan = (byte) loaiDan;
            this.kieuQuyDao = kieuQuyDao;
            this.kieuGoc = kieuGoc;
            this.coHeSoCoDinh = coHeSoCoDinh;
            this.heSoGio = heSoGio;
            this.trongLuc = trongLuc;
            this.lechGoc = lechGoc.clone();
        }

        public int getIdVatPham() {
            return this.idVatPham;
        }

        /** Ma CPlayer.itemUsed cua client goc. */
        public int getMaSuDung() {
            return this.maSuDung;
        }

        /**
         * Bullet.type native cua client. Gia tri -1 nghia la vat pham chi sua
         * phat ban hien tai va van dung loai dan cua sung server dang cam.
         */
        public byte getLoaiDan() {
            return this.loaiDan;
        }

        public KieuQuyDao getKieuQuyDao() {
            return this.kieuQuyDao;
        }

        public KieuGoc getKieuGoc() {
            return this.kieuGoc;
        }

        public boolean coHeSoCoDinh() {
            return this.coHeSoCoDinh;
        }

        public int getHeSoGio() {
            return this.heSoGio;
        }

        public int getTrongLuc() {
            return this.trongLuc;
        }

        /**
         * Cac tia duong ngam so voi goc chinh. Phan lon la {0}; To nhan x3
         * dung {-5, 0, 5} dung nhu CPlayer.drawBullLine().
         */
        public int[] getLechGoc() {
            return this.lechGoc.clone();
        }
    }

    private static final Map<Integer, CauHinh> THEO_ID_VAT_PHAM =
            new LinkedHashMap<>();
    private static final Map<Integer, List<CauHinh>> THEO_MA_SU_DUNG =
            new LinkedHashMap<>();

    static {
        // Vat pham an/khong ban van duoc giu de bang server khop day du client.
        dangKyCoDinh(221, 1, 5, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 0, 80);
        dangKyTheoSung(222, 2, -1, KieuQuyDao.NHAN_DOI_SO_PHAT_SUNG);

        dangKyCoDinh(226, 6, 6, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 70, 90);
        dangKyCoDinh(227, 7, 7, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 70, 80);
        dangKyCoDinh(228, 8, 4, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 0, 80);
        dangKyCoDinh(229, 9, 8, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 70, 70);
        dangKyCoDinh(231, 11, 16, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 0, 100);

        dangKyCoDinh(235, 16, 14, KieuQuyDao.LAZER,
                KieuGoc.TU_DO, 10, 50);
        dangKyCoDinh(236, 17, 13, KieuQuyDao.VOI_RONG,
                KieuGoc.TU_DO, 50, 120);
        dangKyTheoSung(237, 18, 22, KieuQuyDao.CHUOT_GAN_BOM);
        dangKyCoDinh(238, 19, 26, KieuQuyDao.TEN_LUA_X4,
                KieuGoc.TU_DO, 30, 60);
        dangKyCoDinh(239, 20, 25, KieuQuyDao.XUYEN_DAT,
                KieuGoc.XUYEN_DAT, 0, -50);
        dangKyCoDinh(240, 21, 23, KieuQuyDao.MUA_SAO_BANG,
                KieuGoc.TU_DO, 20, 100);
        dangKyCoDinh(241, 22, 28, KieuQuyDao.MUA_DAN,
                KieuGoc.THANG_DUNG, 0, 20);
        dangKyTheoSung(242, 23, 30, KieuQuyDao.KHOAN_DAT);
        dangKyKhongCanGoc(258, 24, 50, KieuQuyDao.TU_NO);
        dangKyCoDinh(244, 25, 51, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 5, 60);
        dangKyCoDinh(245, 26, 52, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 10, 100);
        dangKyTheoSung(246, 27, 53, KieuQuyDao.UFO_HO_TRO);
        dangKyCoDinh(247, 28, 54, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 0, 80);
        dangKyCoDinh(243, 29, 55, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 6, 60);
        // Khi doc du lieu cu, Khi doc va Bom doc cung dung action 29/bullet 55.
        dangKyCoDinh(248, 29, 55, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 6, 60);
        dangKyCoDinh(249, 30, 56, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 70, 70, -5, 0, 5);
        dangKyCoDinh(250, 31, 57, KieuQuyDao.PARABOL,
                KieuGoc.TU_DO, 0, 120);

        // Item nay bien doi phat ban ke tiep; duong can goc van theo sung dang cam.
        dangKyTheoSung(388, 42, 58, KieuQuyDao.LECH_DUONG_DAN);
    }

    private ChickenCongThucVatPhamChien() {
    }

    private static void dangKyCoDinh(
            int idVatPham,
            int maSuDung,
            int loaiDan,
            KieuQuyDao kieuQuyDao,
            KieuGoc kieuGoc,
            int heSoGio,
            int trongLuc,
            int... lechGoc
    ) {
        int[] cacLechGoc = lechGoc == null || lechGoc.length == 0
                ? new int[]{0} : lechGoc;
        dangKy(new CauHinh(
                idVatPham, maSuDung, loaiDan, kieuQuyDao, kieuGoc,
                true, heSoGio, trongLuc, cacLechGoc));
    }

    private static void dangKyTheoSung(
            int idVatPham,
            int maSuDung,
            int loaiDan,
            KieuQuyDao kieuQuyDao
    ) {
        dangKy(new CauHinh(
                idVatPham, maSuDung, loaiDan, kieuQuyDao,
                KieuGoc.THEO_SUNG_DANG_CAM,
                false, 0, 0, new int[]{0}));
    }

    private static void dangKyKhongCanGoc(
            int idVatPham,
            int maSuDung,
            int loaiDan,
            KieuQuyDao kieuQuyDao
    ) {
        dangKy(new CauHinh(
                idVatPham, maSuDung, loaiDan, kieuQuyDao,
                KieuGoc.KHONG_CAN_GOC,
                false, 0, 0, new int[]{0}));
    }

    private static void dangKy(CauHinh cauHinh) {
        if (THEO_ID_VAT_PHAM.putIfAbsent(
                cauHinh.getIdVatPham(), cauHinh) != null) {
            throw new IllegalStateException(
                    "Trung ID vat pham chien dau: "
                    + cauHinh.getIdVatPham());
        }
        THEO_MA_SU_DUNG.computeIfAbsent(
                cauHinh.getMaSuDung(), ignored -> new ArrayList<>())
                .add(cauHinh);
    }

    public static CauHinh theoIdVatPham(int idVatPham) {
        return THEO_ID_VAT_PHAM.get(idVatPham);
    }

    /**
     * Chi dung de doi chieu packet voi vat pham server da chon. Khong duoc lay
     * phan tu dau tien roi coi ma client gui la bang chung so huu.
     */
    public static List<CauHinh> layTheoMaSuDung(int maSuDung) {
        List<CauHinh> ketQua = THEO_MA_SU_DUNG.get(maSuDung);
        return ketQua == null
                ? List.of()
                : Collections.unmodifiableList(ketQua);
    }

    public static Map<Integer, CauHinh> layTatCa() {
        return Collections.unmodifiableMap(THEO_ID_VAT_PHAM);
    }

    /**
     * Xac nhan template server van khop voi bang protocol client.
     */
    public static boolean khopMauVatPham(ChickenMauVatPham mauVatPham) {
        if (mauVatPham == null || mauVatPham.loai != 10) {
            return false;
        }
        CauHinh cauHinh = theoIdVatPham(mauVatPham.ma & 0xFFFF);
        return cauHinh != null
                && cauHinh.getMaSuDung() == (mauVatPham.gioiTinh & 0xFF);
    }
}
