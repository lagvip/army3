package com.chicken.phong.boss.trandau.rong;

/** Cấu hình cố định Boss Rồng map 55. */
public final class CauHinhBossRong {
    public static final int MAP_ID = 55;
    /** Giữ đúng lượng máu mặc định quan sát được từ BigBoss Rồng native. */
    public static final int MAU_BOSS = 10_471;
    /** Công cơ bản; sau khi trừ giáp thường còn khoảng 193 như log thử nghiệm. */
    public static final int TAN_CONG_BAN_TU_XA = 500;
    /** Lực va đập khi bị kẹp, mang đi rồi thả xuống. */
    public static final int TAN_CONG_THA_ROI = 700;
    public static final int SLOT_BOSS_DAU = 8;
    public static final int SLOT_BOSS_CUOI = 8;
    public static final int NAP_DAN_SAU_HANH_DONG = 300;
    public static final int TRE_MOI_BUOC_BAY_MS = 70;
    public static final int TRE_MOI_BUOC_ROI_MS = 55;

    public static final class CauHinh {
        private final byte slot;
        private final int id;
        private final String ten;
        private final short x;
        private final short y;

        private CauHinh(byte slot, int id, String ten, short x, short y) {
            this.slot = slot;
            this.id = id;
            this.ten = ten;
            this.x = x;
            this.y = y;
        }

        public byte getSlot() { return this.slot; }
        public int getId() { return this.id; }
        public String getTen() { return this.ten; }
        public short getX() { return this.x; }
        public short getY() { return this.y; }
        public short getHead() { return -1; }
        public short getHat() { return -1; }
        public short getBody() { return -1; }
        public short getLeg() { return -1; }
        public short getWing() { return -1; }
        public short getVuKhi() { return -1; }
        public byte getHuong() { return 0; }
        public String getLoai() { return "RONG"; }
        public boolean laBossBanSung() { return true; }
        public boolean laCamTu() { return false; }
    }

    private static final CauHinh[] DANH_SACH = new CauHinh[]{
        new CauHinh((byte) 8, -55, "Rồng máy 8", (short) 820, (short) 263)
    };

    private CauHinhBossRong() {}

    public static CauHinh[] layTatCa() { return DANH_SACH.clone(); }
    public static CauHinh layTheoSlot(int slot) {
        return slot == 8 ? DANH_SACH[0] : null;
    }
    public static int layTanCongTheoSung(short partSung) {
        return TAN_CONG_BAN_TU_XA;
    }
    public static int layNapDanTheoSung(short partSung) {
        return NAP_DAN_SAU_HANH_DONG;
    }
}
