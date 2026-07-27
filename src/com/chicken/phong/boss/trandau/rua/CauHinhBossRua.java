package com.chicken.phong.boss.trandau.rua;

/** Cấu hình cố định Boss Rùa map 54. */
public final class CauHinhBossRua {
    public static final int MAP_ID = 54;
    public static final int MAU_BOSS = 1;
    public static final int SAT_THUONG_CHAM = 500;
    /** Ảnh viên đạn riêng của Rùa, dùng chung tài nguyên chuẩn với map 58. */
    public static final short PART_ANH_DAN_RUA = 1563;
    public static final int SLOT_BOSS_DAU = 8;
    public static final int SLOT_BOSS_CUOI = 8;
    public static final int NAP_DAN_SAU_HANH_DONG = 300;
    /**
     * Quãng chạy ngang của Rùa map 54 trong một lượt.
     * Phần rơi theo trọng lực không được tính vào quãng này.
     */
    public static final int QUANG_DUONG_MOI_LUOT = 130;

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
        public short getVuKhi() { return PART_ANH_DAN_RUA; }
        public byte getHuong() { return 0; }
        public String getLoai() { return "RUA"; }
        public boolean laBossBanSung() { return false; }
        public boolean laCamTu() { return false; }
    }

    private static final CauHinh[] DANH_SACH = new CauHinh[]{
        new CauHinh((byte) 8, -54, "Boss Rùa 8", (short) 817, (short) 253)
    };

    private CauHinhBossRua() {}

    public static CauHinh[] layTatCa() { return DANH_SACH.clone(); }
    public static CauHinh layTheoSlot(int slot) {
        return slot == 8 ? DANH_SACH[0] : null;
    }
    public static int layTanCongTheoSung(short partSung) { return SAT_THUONG_CHAM; }
    public static int layNapDanTheoSung(short partSung) { return NAP_DAN_SAU_HANH_DONG; }
}
