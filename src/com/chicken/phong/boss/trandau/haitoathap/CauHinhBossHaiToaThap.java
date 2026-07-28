package com.chicken.phong.boss.trandau.haitoathap;

import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.phong.boss.trandau.ChickenHoatAnhNoCamTu;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import java.util.Arrays;

/** Dữ liệu cố định của boss trên map 51 - Boss Hai tòa tháp. */
public final class CauHinhBossHaiToaThap {
    public static final int MAP_ID = 51;
    public static final int MAU_BOSS = 1_050;
    public static final int SAT_THUONG_CAM_TU = MAU_BOSS / 2;
    /** GUN_BOMB_BIG cua client; packet phai co du bay duong dan. */
    public static final byte LOAI_DAN_HIEU_UNG_NO_CAM_TU =
            ChickenHoatAnhNoCamTu.LOAI_DAN;
    /** Dong bo tam no Cam tu o cac map boss khac. */
    public static final int BAN_KINH_NO_CAM_TU = 90;
    /** Tai ria vu no van con 20% damage, cung muc voi nhom dan no lon. */
    public static final int PHAN_TRAM_DAMAGE_RIA_CAM_TU = 20;
    public static final int SLOT_BOSS_DAU = 8;
    public static final int SLOT_BOSS_CUOI = 27;
    public static final int SLOT_CAM_TU_DAU = 10;
    public static final int SLOT_CAM_TU_CUOI = 17;
    public static final int SLOT_CAM_TU_DU_BI_DAU = 20;
    public static final int SLOT_CAM_TU_DU_BI_CUOI = 27;

    public enum LoaiBoss {
        BAN_SUNG,
        CAM_TU
    }

    public static final class CauHinh {
        private final byte slot;
        private final int id;
        private final String ten;
        private final short x;
        private final short y;
        private final short head;
        private final short hat;
        private final short body;
        private final short leg;
        private final short wing;
        private final short vuKhi;
        private final byte huong;
        private final LoaiBoss loai;

        private CauHinh(byte slot, int id, String ten, short x, short y,
                short head, short hat, short body, short leg, short wing,
                short vuKhi, byte huong, LoaiBoss loai) {
            this.slot = slot;
            this.id = id;
            this.ten = ten;
            this.x = x;
            this.y = y;
            this.head = head;
            this.hat = hat;
            this.body = body;
            this.leg = leg;
            this.wing = wing;
            this.vuKhi = vuKhi;
            this.huong = huong;
            this.loai = loai;
        }

        public byte getSlot() { return this.slot; }
        public int getId() { return this.id; }
        public String getTen() { return this.ten; }
        public short getX() { return this.x; }
        public short getY() { return this.y; }
        public short getHead() { return this.head; }
        public short getHat() { return this.hat; }
        public short getBody() { return this.body; }
        public short getLeg() { return this.leg; }
        public short getWing() { return this.wing; }
        public short getVuKhi() { return this.vuKhi; }
        public byte getHuong() { return this.huong; }
        public LoaiBoss getLoai() { return this.loai; }
        public boolean laBossBanSung() { return this.loai == LoaiBoss.BAN_SUNG; }
        public boolean laCamTu() { return this.loai == LoaiBoss.CAM_TU; }
    }

    private static final CauHinh[] DANH_SACH = new CauHinh[]{
        new CauHinh((byte) 8, -1, "Phiến quân 0", (short) 422, (short) 125,
                (short) -1, (short) 159, (short) 158, (short) 157, (short) 160,
                (short) 54, (byte) 2, LoaiBoss.BAN_SUNG),
        new CauHinh((byte) 9, -101, "Phiến quân 1", (short) 689, (short) 125,
                (short) -1, (short) 159, (short) 158, (short) 157, (short) 160,
                (short) 56, (byte) 2, LoaiBoss.BAN_SUNG),
        new CauHinh((byte) 10, -201, "Cảm tử 3", (short) 168, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 11, -301, "Cảm tử 4", (short) 881, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 12, -401, "Cảm tử 5", (short) 933, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 13, -501, "Cảm tử 6", (short) 959, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 14, -601, "Cảm tử 7", (short) 908, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 15, -701, "Cảm tử 8", (short) 219, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 16, -801, "Cảm tử 9", (short) 251, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 17, -901, "Cảm tử 10", (short) 185, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        /*
         * Hai vị trí này nằm trên đúng hai viên gạch đỉnh tháp trong
         * res/map/51: gạch (320,107) và (769,107), lệch tâm 16 px.
         * Giữ Cảm tử ở slot 10-17 để không phá luồng di chuyển/
         * animation hiện có; hai Phiến quân thử nghiệm dùng slot 18-19.
         */
        new CauHinh((byte) 18, -1001, "Phiến quân 2", (short) 336, (short) 107,
                (short) -1, (short) 159, (short) 158, (short) 157, (short) 160,
                (short) 54, (byte) 2, LoaiBoss.BAN_SUNG),
        new CauHinh((byte) 19, -1101, "Phiến quân 3", (short) 785, (short) 107,
                (short) -1, (short) 159, (short) 158, (short) 157, (short) 160,
                (short) 56, (byte) 2, LoaiBoss.BAN_SUNG)
    };

    /**
     * Mỗi Cảm tử ban đầu có đúng một quân dự bị tại cùng
     * điểm spawn. Các slot này không được tạo khi bắt đầu trận.
     */
    private static final CauHinh[] CAM_TU_DU_BI = new CauHinh[]{
        new CauHinh((byte) 20, -1201, "Cảm tử 11", (short) 168, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 21, -1301, "Cảm tử 12", (short) 881, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 22, -1401, "Cảm tử 13", (short) 933, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 23, -1501, "Cảm tử 14", (short) 959, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 24, -1601, "Cảm tử 15", (short) 908, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 25, -1701, "Cảm tử 16", (short) 219, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 26, -1801, "Cảm tử 17", (short) 251, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 27, -1901, "Cảm tử 18", (short) 185, (short) 508,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU)
    };

    private CauHinhBossHaiToaThap() {
    }

    public static CauHinh[] layTatCa() {
        return DANH_SACH.clone();
    }

    /** Hai Phiến quân tăng cường chỉ xuất hiện từ bốn người. */
    public static CauHinh[] layBanDauChoSoNguoi(int soNguoi) {
        int soLuong = soNguoi >= 4 ? DANH_SACH.length : 10;
        return Arrays.copyOf(DANH_SACH, soLuong);
    }

    public static CauHinh[] layCamTuDuBi() {
        return CAM_TU_DU_BI.clone();
    }

    /** Slot 10-17 ánh xạ một-một sang slot dự bị 20-27. */
    public static CauHinh layCamTuDuBiTheoSlotGoc(int slotGoc) {
        int chiSo = slotGoc - SLOT_CAM_TU_DAU;
        return chiSo >= 0 && chiSo < CAM_TU_DU_BI.length
                ? CAM_TU_DU_BI[chiSo]
                : null;
    }

    public static CauHinh layTheoSlot(int slot) {
        for (CauHinh cauHinh : DANH_SACH) {
            if ((cauHinh.slot & 0xFF) == slot) {
                return cauHinh;
            }
        }
        for (CauHinh cauHinh : CAM_TU_DU_BI) {
            if ((cauHinh.slot & 0xFF) == slot) {
                return cauHinh;
            }
        }
        return null;
    }

    public static int layTanCongTheoSung(short partSung) {
        ChickenQuanLyDanSung.DuLieuSung duLieu =
                ChickenQuanLyDanSung.theoPartSung(partSung);
        if (duLieu != null && ChickenQuanLyMayChu.itemTemplates != null) {
            ChickenMauVatPham mau = ChickenQuanLyMayChu.itemTemplates.get(duLieu.getIdSung());
            int tanCong = layOption(mau, 1);
            if (tanCong > 0) {
                return tanCong;
            }
        }
        return 100;
    }

    public static int layNapDanTheoSung(short partSung) {
        ChickenQuanLyDanSung.DuLieuSung duLieu =
                ChickenQuanLyDanSung.theoPartSung(partSung);
        if (duLieu != null && ChickenQuanLyMayChu.itemTemplates != null) {
            ChickenMauVatPham mau =
                    ChickenQuanLyMayChu.itemTemplates.get(duLieu.getIdSung());
            int napDan = layOption(mau, 14);
            if (napDan > 0) {
                return napDan;
            }
        }
        return 100;
    }

    private static int layOption(ChickenMauVatPham mau, int maOption) {
        if (mau == null || mau.thuocTinhs == null) {
            return 0;
        }
        int tong = 0;
        for (Object doiTuong : mau.thuocTinhs) {
            if (!(doiTuong instanceof ChickenThuocTinhVatPham)) {
                continue;
            }
            ChickenThuocTinhVatPham option = (ChickenThuocTinhVatPham) doiTuong;
            if (option.optionTemplate != null && option.optionTemplate.ma == maOption) {
                tong += Math.max(0, option.thamSo);
            }
        }
        return tong;
    }
}
