package com.chicken.phong.boss.trandau.datbom;

import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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
    private static final int ID_AVG_DAU = 391;
    private static final int ID_AVG_CUOI = 398;
    private static final int[] SUNG_DU_PHONG = {110, 120, 160};

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
        List<ChickenQuanLyDanSung.DuLieuSung> ungViens =
                layDanhSachSungShopKhongAvg(
                        ChickenQuanLyMayChu.itemTemplates);
        if (ungViens.isEmpty()) {
            for (int idSung : SUNG_DU_PHONG) {
                ChickenQuanLyDanSung.DuLieuSung duLieu =
                        ChickenQuanLyDanSung.theoIdSung(idSung);
                if (duLieu != null) {
                    ungViens.add(duLieu);
                }
            }
        }
        if (ungViens.isEmpty()) {
            return null;
        }
        return ungViens.get(
                ThreadLocalRandom.current().nextInt(ungViens.size()));
    }

    /**
     * Tạo danh sách ứng viên từ cùng dữ liệu mà server dùng để mở tab Súng.
     * Chỉ item type 5, có giá shop, có mapping đạn và không phải ID AVG.
     */
    public static List<ChickenQuanLyDanSung.DuLieuSung>
            layDanhSachSungShopKhongAvg(
                    Map<Integer, ChickenMauVatPham> itemTemplates) {
        if (itemTemplates == null || itemTemplates.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<ChickenQuanLyDanSung.DuLieuSung> ketQua =
                new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>(itemTemplates.keySet());
        Collections.sort(ids);
        for (int idSung : ids) {
            ChickenMauVatPham mau = itemTemplates.get(idSung);
            if (!laSungShopKhongAvg(mau)) {
                continue;
            }
            ChickenQuanLyDanSung.DuLieuSung duLieu =
                    ChickenQuanLyDanSung.theoIdSung(idSung);
            if (duLieu != null) {
                ketQua.add(duLieu);
            }
        }
        return ketQua;
    }

    public static boolean laSungShopKhongAvg(ChickenMauVatPham mau) {
        if (mau == null || mau.loai != 5
                || (mau.buyGold <= 0 && mau.buyGem <= 0)) {
            return false;
        }
        int idSung = mau.ma & 0xFFFF;
        return idSung < ID_AVG_DAU || idSung > ID_AVG_CUOI;
    }

    public static int layTanCongTheoIdSung(int idSung) {
        int tanCong = layOption(layMauSung(idSung), 1);
        if (tanCong > 0) {
            return tanCong;
        }
        return switch (idSung) {
            case 110 -> 280;
            case 120 -> 250;
            case 160 -> 330;
            default -> SAT_THUONG_CAM_TU;
        };
    }

    /** Option 14 là tốc độ nạp dùng để xếp lượt tiếp theo của chính khẩu súng. */
    public static int layNapDanTheoIdSung(int idSung) {
        int napDan = layOption(layMauSung(idSung), 14);
        if (napDan > 0) {
            return napDan;
        }
        return switch (idSung) {
            case 110 -> 280;
            case 120 -> 250;
            case 160 -> 330;
            default -> 100;
        };
    }

    private static ChickenMauVatPham layMauSung(int idSung) {
        return ChickenQuanLyMayChu.itemTemplates == null
                ? null
                : ChickenQuanLyMayChu.itemTemplates.get(idSung);
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
            ChickenThuocTinhVatPham option =
                    (ChickenThuocTinhVatPham) doiTuong;
            if (option.optionTemplate != null
                    && option.optionTemplate.ma == maOption) {
                tong += Math.max(0, option.thamSo);
            }
        }
        return tong;
    }
}
