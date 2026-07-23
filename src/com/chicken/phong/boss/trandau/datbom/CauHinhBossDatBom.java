package com.chicken.phong.boss.trandau.datbom;

/** Dữ liệu cố định của bốn Phiến quân tự kích nổ trên map 53. */
public final class CauHinhBossDatBom {
    public static final int MAP_ID = 53;
    public static final int MAU_BOSS = 1_100;
    /** Sát thương gốc trước khi trừ giáp của người chơi. */
    public static final int SAT_THUONG_CAM_TU = 1_500;
    /** Đủ gần người chơi thì Phiến quân kích bom ngay, không cần chạm sprite. */
    public static final int BAN_KINH_KICH_NO = 90;
    public static final int SLOT_BOSS_DAU = 8;
    public static final int SLOT_BOSS_CUOI = 11;

    public enum LoaiBoss {
        DAT_BOM
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
        public boolean laBossBanSung() { return false; }
        public boolean laCamTu() { return true; }
    }

    /*
     * Tọa độ 8-11 lấy trực tiếp từ spawn gốc của res/map/53.
     * Ngoại hình và part súng khớp CPlayerBoss native của client:
     * hat=179, body=178, leg=177, wing=180; wp=57,57,27,56.
     */
    private static final CauHinh[] DANH_SACH = new CauHinh[]{
        new CauHinh((byte) 8, -1, "Phiến quân 0", (short) 198, (short) 276,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) 57, (byte) 2, LoaiBoss.DAT_BOM),
        new CauHinh((byte) 9, -101, "Phiến quân 1", (short) 779, (short) 241,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) 57, (byte) 2, LoaiBoss.DAT_BOM),
        new CauHinh((byte) 10, -201, "Phiến quân 2", (short) 450, (short) 176,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) 27, (byte) 2, LoaiBoss.DAT_BOM),
        new CauHinh((byte) 11, -301, "Phiến quân 3", (short) 496, (short) 176,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) 56, (byte) 2, LoaiBoss.DAT_BOM)
    };

    private CauHinhBossDatBom() {
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
        return SAT_THUONG_CAM_TU;
    }

    public static int layNapDanTheoSung(short partSung) {
        return 300;
    }
}
