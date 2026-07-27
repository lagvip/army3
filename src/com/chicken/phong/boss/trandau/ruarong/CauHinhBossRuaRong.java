package com.chicken.phong.boss.trandau.ruarong;

import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;

/** Cấu hình map 58: 2 Rùa máy, 1 Rồng máy và 2 Phiến quân hỗ trợ. */
public final class CauHinhBossRuaRong {
    public static final int MAP_ID = 58;
    public static final int SLOT_BOSS_DAU = 8;
    public static final int SLOT_BOSS_CUOI = 12;

    public static final int MAU_RUA = 9_853;
    public static final int MAU_RONG = 10_560;
    public static final int MAU_PHIEN_QUAN = 2_875;

    public static final int SAT_THUONG_RUA = 900;
    /** Damage gốc mỗi viên của loạt hai viên Rồng. */
    public static final int SAT_THUONG_RONG = 980;
    /** Damage gốc khi Rồng gắp rồi thả trúng nền. */
    public static final int SAT_THUONG_GAP_THA_RONG = SAT_THUONG_RONG;
    public static final int MAU_BOSS = MAU_PHIEN_QUAN;
    public static final int SAT_THUONG_CAM_TU = SAT_THUONG_RUA;
    public static final int BAN_KINH_KICH_NO = 90;
    public static final int NAP_DAN_SAU_HANH_DONG = 300;

    /**
     * Part ảnh đạn Rùa do client yêu cầu qua CMD -40. Server trả đúng file
     * res/icon/item/1/Small1563.png; không cần và không được sửa JAR client.
     */
    public static final short PART_ANH_DAN_RUA = 1563;

    public enum LoaiBoss {
        RUA,
        RONG,
        BAN_SUNG
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
        private final int mau;
        private final LoaiBoss loai;

        private CauHinh(byte slot, int id, String ten, short x, short y,
                short head, short hat, short body, short leg, short wing,
                short vuKhi, byte huong, int mau, LoaiBoss loai) {
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
            this.mau = mau;
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
        public int getMau() { return this.mau; }
        public LoaiBoss getLoai() { return this.loai; }
        public boolean laRua() { return this.loai == LoaiBoss.RUA; }
        public boolean laRong() { return this.loai == LoaiBoss.RONG; }
        public boolean laBossBanSung() { return this.loai == LoaiBoss.BAN_SUNG; }
        public boolean laCamTu() { return false; }
    }

    private static final CauHinh[] DANH_SACH = new CauHinh[]{
        new CauHinh((byte) 8, -54, "Rùa Máy 1", (short) 31, (short) 265,
                (short) -1, (short) -1, (short) -1, (short) -1, (short) -1,
                PART_ANH_DAN_RUA, (byte) 0, MAU_RUA, LoaiBoss.RUA),
        new CauHinh((byte) 9, -54, "Rùa Máy 2", (short) 928, (short) 269,
                (short) -1, (short) -1, (short) -1, (short) -1, (short) -1,
                PART_ANH_DAN_RUA, (byte) 2, MAU_RUA, LoaiBoss.RUA),
        new CauHinh((byte) 10, -55, "Rồng máy", (short) 425, (short) 229,
                (short) -1, (short) -1, (short) -1, (short) -1, (short) -1,
                (short) -1, (byte) 0, MAU_RONG, LoaiBoss.RONG),
        new CauHinh((byte) 11, -301, "Phiến Quân 1", (short) 261, (short) 221,
                (short) -1, (short) 159, (short) 158, (short) 157, (short) 160,
                (short) 57, (byte) 2, MAU_PHIEN_QUAN, LoaiBoss.BAN_SUNG),
        new CauHinh((byte) 12, -401, "Phiến Quân 2", (short) 731, (short) 219,
                (short) -1, (short) 159, (short) 158, (short) 157, (short) 160,
                (short) 27, (byte) 0, MAU_PHIEN_QUAN, LoaiBoss.BAN_SUNG)
    };

    private CauHinhBossRuaRong() {
    }

    public static CauHinh[] layTatCa() {
        return DANH_SACH.clone();
    }

    public static CauHinh layTheoSlot(int slot) {
        for (CauHinh cauHinh : DANH_SACH) {
            if ((cauHinh.slot & 0xFF) == slot) {
                return cauHinh;
            }
        }
        return null;
    }

    public static int layTanCongTheoSung(short partSung) {
        ChickenQuanLyDanSung.DuLieuSung duLieu = ChickenQuanLyDanSung.theoPartSung(partSung);
        if (duLieu != null && ChickenQuanLyMayChu.itemTemplates != null) {
            ChickenMauVatPham mau = ChickenQuanLyMayChu.itemTemplates.get(duLieu.getIdSung());
            int tanCong = layOption(mau, 1);
            if (tanCong > 0) {
                return tanCong;
            }
        }
        return 350;
    }

    public static int layNapDanTheoSung(short partSung) {
        ChickenQuanLyDanSung.DuLieuSung duLieu = ChickenQuanLyDanSung.theoPartSung(partSung);
        if (duLieu != null && ChickenQuanLyMayChu.itemTemplates != null) {
            ChickenMauVatPham mau = ChickenQuanLyMayChu.itemTemplates.get(duLieu.getIdSung());
            int napDan = layOption(mau, 14);
            if (napDan > 0) {
                return napDan;
            }
        }
        return NAP_DAN_SAU_HANH_DONG;
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
