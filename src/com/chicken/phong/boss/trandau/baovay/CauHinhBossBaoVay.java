package com.chicken.phong.boss.trandau.baovay;

import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;

/** Dữ liệu cố định của năm boss trên map 50 - Boss Bao vây. */
public final class CauHinhBossBaoVay {
    public static final int MAP_ID = 50;
    public static final int MAU_BOSS = 1_000;
    public static final int SAT_THUONG_CAM_TU = MAU_BOSS / 2;
    public static final int SLOT_BOSS_DAU = 8;
    public static final int SLOT_BOSS_CUOI = 12;

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
        new CauHinh((byte) 8, -1, "Phiến quân 0", (short) 133, (short) 334,
                (short) -1, (short) 159, (short) 158, (short) 157, (short) 160,
                (short) 54, (byte) 2, LoaiBoss.BAN_SUNG),
        new CauHinh((byte) 9, -101, "Phiến quân 1", (short) 154, (short) 446,
                (short) -1, (short) 159, (short) 158, (short) 157, (short) 160,
                (short) 27, (byte) 2, LoaiBoss.BAN_SUNG),
        new CauHinh((byte) 10, -201, "Cảm tử 3", (short) 760, (short) 155,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 11, -301, "Cảm tử 4", (short) 883, (short) 155,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU),
        new CauHinh((byte) 12, -401, "Cảm tử 5", (short) 649, (short) 248,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) -1, (byte) 0, LoaiBoss.CAM_TU)
    };

    private CauHinhBossBaoVay() {
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

    /**
     * Lấy đúng chỉ số tấn công cơ bản của template súng đang cầm.
     * Khi dữ liệu template chưa nạp, dùng giá trị an toàn theo đúng hai súng
     * của map 50 để trận vẫn chạy thay vì tạo boss sát thương bằng 0.
     */
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
        if (partSung == 54) {
            return 100;
        }
        if (partSung == 27) {
            return 100;
        }
        return 50;
    }

    /**
     * Lấy thời gian nạp đạn theo option 14 của đúng template súng.
     * Dùng 100 làm giá trị an toàn khi dữ liệu template chưa được nạp.
     */
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
