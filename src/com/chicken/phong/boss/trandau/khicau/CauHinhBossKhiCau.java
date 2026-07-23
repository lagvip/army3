package com.chicken.phong.boss.trandau.khicau;

import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;

/** Cấu hình map 52: một khí cầu và hai Phiến quân bám tại hai dây treo. */
public final class CauHinhBossKhiCau {
    public static final int MAP_ID = 52;
    public static final int SLOT_KHI_CAU = 8;
    public static final int SLOT_BOSS_DAU = 8;
    public static final int SLOT_BOSS_CUOI = 10;
    public static final int MAU_KHI_CAU = 8_527;
    public static final int MAU_PHIEN_QUAN = 1_000;
    /** Chỉ giữ để tương thích phần điều phối dùng chung; map 52 không tạo Cảm tử. */
    public static final int SAT_THUONG_CAM_TU = MAU_PHIEN_QUAN / 2;
    public static final int NAP_DAN_KHI_CAU_SAU_DI_CHUYEN = 300;
    public static final int TRE_MOI_BUOC_BAY_MS = 70;

    /** Tọa độ hai Phiến quân so với tâm neo của khí cầu. */
    // Đúng tọa độ native mà CPlayer fh=3 tự khóa hai slot fh=4 vào dây treo.
    public static final int LECH_X_TRAI = -20;
    public static final int LECH_X_PHAI = 20;
    public static final int LECH_Y_DAY_TREO = 50;

    public enum LoaiBoss {
        KHI_CAU,
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
        public boolean laKhiCau() { return this.loai == LoaiBoss.KHI_CAU; }
        public boolean laBossBanSung() { return this.loai == LoaiBoss.BAN_SUNG; }
        public boolean laCamTu() { return this.loai == LoaiBoss.CAM_TU; }
    }

    private static final CauHinh[] DANH_SACH = new CauHinh[]{
        new CauHinh((byte) 8, -52, "Boss Khí cầu 8", (short) 900, (short) 260,
                (short) -1, (short) -1, (short) -1, (short) -1, (short) -1,
                (short) -1, (byte) 0, MAU_KHI_CAU, LoaiBoss.KHI_CAU),
        new CauHinh((byte) 9, -152, "Phiến quân dây trái", (short) 880, (short) 310,
                (short) -1, (short) 159, (short) 158, (short) 157, (short) 160,
                (short) 54, (byte) 2, MAU_PHIEN_QUAN, LoaiBoss.BAN_SUNG),
        new CauHinh((byte) 10, -252, "Phiến quân dây phải", (short) 920, (short) 310,
                (short) -1, (short) 159, (short) 158, (short) 157, (short) 160,
                (short) 56, (byte) 2, MAU_PHIEN_QUAN, LoaiBoss.BAN_SUNG)
    };

    private CauHinhBossKhiCau() {
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
            ChickenMauVatPham mau = ChickenQuanLyMayChu.itemTemplates.get(duLieu.getIdSung());
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
