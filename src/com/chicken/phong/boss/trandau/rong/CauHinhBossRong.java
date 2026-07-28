package com.chicken.phong.boss.trandau.rong;

/**
 * Toàn bộ thông số có thể cân bằng của Boss Rồng map 55.
 *
 * <p>Khi chỉnh boss, chỉ sửa các hằng số trong file này. Các class
 * {@link BossRong}, {@link BossRongTanCong} và {@link DiChuyenBossRong}
 * chỉ chứa luồng xử lý và đọc lại cấu hình tại đây.</p>
 */
public final class CauHinhBossRong {
    // ==================== CHỈ SỐ CHÍNH ====================
    public static final int MAP_ID = 55;
    public static final int MAU_BOSS = 1;
    public static final int GIAP_BOSS = 0;
    /** Damage gốc của từng viên trong loạt bắn xa, trước khi trừ giáp. */
    public static final int TAN_CONG_BAN_TU_XA = 250;
    /** Damage gốc khi bị kẹp rồi thả xuống nền, trước khi trừ giáp. */
    public static final int TAN_CONG_THA_ROI = 500;
    /** Rơi xuống vực vẫn chết ngay, không dùng TAN_CONG_THA_ROI. */
    public static final int SO_VIEN_DAN_DAC_BIET = 2;
    public static final int NAP_DAN_SAU_HANH_DONG = 300;
    public static final int GIAY_MOI_LUOT = 25;
    /** Mỗi lượt có 25% dùng skill gắp-thả, 75% bay quanh rồi bắn loạt. */
    public static final int TY_LE_GAP_NGUOI_PHAN_TRAM = 25;

    // ==================== ĐẠN BẮN XA ====================
    /** Một lần tung cho cả loạt: 50% aim, 50% bắn vào điểm ngẫu nhiên. */
    public static final int TY_LE_AIM_PHAN_TRAM = 50;
    public static final int LE_DIEM_NGAU_NHIEN_X = 20;
    public static final int LE_DIEM_NGAU_NHIEN_Y = 40;
    public static final int KHOANG_CACH_DIEM_NGAU_NHIEN_TOI_THIEU = 160;
    /**
     * Phat ban truot phai ket thuc ngoai map de client khong tao hieu ung
     * no type 60 ngay trong mot khoi dia hinh.
     */
    public static final int LE_KET_THUC_NGOAI_BAN_DO = 32;
    public static final byte LOAI_DAN_DAC_BIET = 1;
    public static final byte LUC_HIEN_THI_DAN = 30;
    public static final int LECH_X_NO_DAN = 62;
    public static final int LECH_Y_NO_DAN = 70;
    public static final int SO_DIEM_DUONG_DAN_TOI_THIEU = 12;
    public static final int SO_DIEM_DUONG_DAN_TOI_DA = 96;
    public static final int KHOANG_CACH_MOI_DIEM_DAN = 12;
    public static final double HE_SO_GIO_DUONG_DAN = 0.65D;
    public static final double DO_CONG_DUONG_DAN = 18.0D;

    // ==================== HITBOX RỒNG ====================
    public static final int HITBOX_LECH_TRAI = 25;
    public static final int HITBOX_LECH_PHAI = 24;
    public static final int HITBOX_LECH_TREN = 50;
    public static final int HITBOX_LECH_DUOI = 14;

    // ==================== DI CHUYỂN / GẮP THẢ ====================
    public static final int BUOC_BAY_TOI_DA = 50;
    public static final int TOC_DO_BAY_CLIENT_MOI_FRAME = 12;
    public static final int TOC_DO_THA_NGUOI_MOI_FRAME = 40;
    public static final int THOI_GIAN_FRAME_CLIENT_MS = 17;
    public static final int THOI_GIAN_BAY_TOI_THIEU_MS = 100;
    public static final int THOI_GIAN_BAY_TOI_DA_MS = 3_500;
    public static final int LECH_Y_NGUOI_BI_KEP = 47;
    public static final int BUOC_TIM_X_THA_AN_TOAN = 4;
    public static final int LECH_NUA_MAP_DIEM_MANG = 120;
    public static final int CACH_RIA_MAP_DIEM_MANG = 200;
    public static final int Y_DIEM_MANG_TOI_THIEU = 130;
    public static final int Y_DIEM_MANG_TOI_DA = 230;
    public static final int DO_CAO_NANG_NGUOI_KHI_MANG = 170;
    /** Khoang ngang ngau nhien de Rong bay quanh muc tieu truoc khi ban. */
    public static final int KHOANG_X_BAY_QUANH_TOI_THIEU = 110;
    public static final int KHOANG_X_BAY_QUANH_TOI_DA = 190;
    /** Rong dung cao hon chan nguoi choi de dau dan khong nam trong dia hinh. */
    public static final int DO_CAO_BAY_QUANH_TOI_THIEU = 85;
    public static final int DO_CAO_BAY_QUANH_TOI_DA = 145;
    public static final int SO_LAN_THU_DIEM_BAY_QUANH = 12;

    // ==================== THỜI GIAN ANIMATION ====================
    public static final int TRE_BOSS_BAT_DAU_MS = 550;
    /** Cho client chạy xong cả hai viên native trước khi phát lượt tiếp theo. */
    public static final int TRE_KET_THUC_LOAT_DAN_MS = 650;
    /**
     * BigBoss native giữ mỗi frame fCarry khoảng 3 tick ở 60 FPS
     * (hoặc 6 tick ở high-frame-rate), tổng 8 frame xấp xỉ 408 ms.
     */
    public static final int TRE_HOAT_ANH_KEP_MANG_MS = 420;
    public static final int TRE_DU_PHONG_GAP_THA_MS = 120;
    /** Đủ để client vẽ impact/HP trước khi nhận CMD 24 của lượt kế tiếp. */
    public static final int TRE_SAU_DAMAGE_GAP_THA_MS = 120;
    /** Dem nho sau khi client bay toi dich roi moi tao va phat duong dan. */
    public static final int TRE_DU_PHONG_BAY_QUANH_TRUOC_BAN_MS = 70;

    // ==================== VỊ TRÍ / NHẬN DIỆN ====================
    public static final int SLOT_BOSS_DAU = 8;
    public static final int SLOT_BOSS_CUOI = 8;

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
