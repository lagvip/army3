package com.chicken.phong.boss.trandau.datbom;

import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.phong.boss.trandau.ChickenSungShopBoss;
import com.chicken.vatpham.ChickenMauVatPham;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Dữ liệu cố định của các Phiến quân đứng yên trên map 53. */
public final class CauHinhBossDatBom {
    public static final int MAP_ID = 53;
    public static final int MAU_BOSS = 1_100;
    /** Sát thương gốc trước khi trừ giáp của người chơi. */
    public static final int SAT_THUONG_CAM_TU = 1_500;
    /** Đủ gần người chơi thì Phiến quân kích bom ngay, không cần chạm sprite. */
    public static final int BAN_KINH_KICH_NO = 90;
    public static final int SLOT_BOSS_DAU = 8;
    public static final int SLOT_BOSS_CUOI = 15;
    public static final int SO_BOSS_MOT_NGUOI = 4;
    public static final int SO_BOSS_HAI_NGUOI = 6;
    public static final int SO_BOSS_BON_NGUOI = 7;
    public static final int SO_BOSS_SAU_NGUOI = 8;
    /** Damage shown for a timed bomb. Timed bombs are fatal and ignore armor. */
    public static final int SAT_THUONG_BOM_HEN_GIO = 6_000;

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
        /**
         * Map 53 dùng các Phiến quân cố định tại tọa độ spawn. Chúng vẫn tấn
         * công bằng súng đang cầm nhưng không được đi vào luồng Cảm tử đuổi
         * người chơi của map 50/51.
         */
        public boolean laBossBanSung() { return true; }
        public boolean laCamTu() { return false; }
    }

    /*
     * Tọa độ 8-15 lấy trực tiếp từ các spawn cùng chỉ số trong res/map/53.
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
                (short) 56, (byte) 2, LoaiBoss.DAT_BOM),
        new CauHinh((byte) 12, -401, "Phiến quân 4", (short) 243, (short) 416,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) 56, (byte) 2, LoaiBoss.DAT_BOM),
        new CauHinh((byte) 13, -501, "Phiến quân 5", (short) 335, (short) 416,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) 56, (byte) 2, LoaiBoss.DAT_BOM),
        new CauHinh((byte) 14, -601, "Phiến quân 6", (short) 120, (short) 175,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) 56, (byte) 2, LoaiBoss.DAT_BOM),
        new CauHinh((byte) 15, -701, "Phiến quân 7", (short) 169, (short) 175,
                (short) -1, (short) 179, (short) 178, (short) 177, (short) 180,
                (short) 56, (byte) 2, LoaiBoss.DAT_BOM)
    };

    private CauHinhBossDatBom() {
    }

    public static CauHinh[] layTatCa() {
        return DANH_SACH.clone();
    }

    public static CauHinh[] layChoSoNguoi(int soNguoi) {
        int soBoss;
        if (soNguoi <= 1) {
            soBoss = SO_BOSS_MOT_NGUOI;
        } else if (soNguoi < 4) {
            soBoss = SO_BOSS_HAI_NGUOI;
        } else if (soNguoi < 6) {
            soBoss = SO_BOSS_BON_NGUOI;
        } else {
            soBoss = SO_BOSS_SAU_NGUOI;
        }
        return Arrays.copyOf(DANH_SACH, soBoss);
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
     * Chọn đúng một súng đang bán trong shop cho một Phiến quân.
     * Súng được giữ nguyên suốt trận; AVG và item không bán bị loại.
     */
    public static ChickenQuanLyDanSung.DuLieuSung chonSungShopKhongAvg() {
        return ChickenSungShopBoss.chonNgauNhienKhongAvg();
    }

    /**
     * Tạo danh sách ứng viên từ cùng dữ liệu mà server dùng để mở tab Súng.
     * Chỉ item type 5, có giá shop, có mapping đạn và không phải ID AVG.
     */
    public static List<ChickenQuanLyDanSung.DuLieuSung>
            layDanhSachSungShopKhongAvg(
                    Map<Integer, ChickenMauVatPham> itemTemplates) {
        return ChickenSungShopBoss.layDanhSachKhongAvg(itemTemplates);
    }

    public static boolean laSungShopKhongAvg(ChickenMauVatPham mau) {
        return ChickenSungShopBoss.laSungShopKhongAvg(mau);
    }

    public static int layTanCongTheoIdSung(int idSung) {
        int duPhong = switch (idSung) {
            case 110 -> 280;
            case 120 -> 250;
            case 160 -> 330;
            default -> SAT_THUONG_CAM_TU;
        };
        return ChickenSungShopBoss.layTanCongTheoId(idSung, duPhong);
    }

    /** Option 14 là tốc độ nạp dùng để xếp lượt tiếp theo của chính khẩu súng. */
    public static int layNapDanTheoIdSung(int idSung) {
        int duPhong = switch (idSung) {
            case 110 -> 280;
            case 120 -> 250;
            case 160 -> 330;
            default -> 100;
        };
        return ChickenSungShopBoss.layNapDanTheoId(idSung, duPhong);
    }

}
