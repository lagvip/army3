package com.chicken.phong.boss.trandau.rua;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chiso.ChickenKichThuocNhanVat;

/**
 * Cơ chế di chuyển riêng của Boss Rùa.
 * Y là tọa độ chân. Ở map 54, mép phải ngoài cùng được chặn để Rùa không lao
 * xuống vực; phía trái vẫn giữ đà và rơi xuống chuỗi bệ thấp của bản đồ.
 */
public final class DiChuyenBossRua {
    public static final int QUANG_DUONG_MOI_LUOT = 170;
    public static final int TRE_MOI_BUOC_MS = 85;
    public static final long TRE_HOAT_ANH_TOI_THIEU_MS = 350L;
    public static final long TRE_HOAT_ANH_TOI_DA_MS = 3_200L;

    private static final int TOC_DO_CHAY = 9;
    private static final int TOC_DO_ROI = 12;
    private static final int BUOC_LEN_TOI_DA = 12;
    private static final int BUOC_XUONG_TOI_DA = 12;

    /** Vùng thân dùng cho va chạm đạn và chạm người chơi. */
    public static final int NUA_RONG_HITBOX = 58;
    public static final int CHIEU_CAO_HITBOX = 108;

    /** Vùng chân dùng để dò nền, hẹp hơn sprite để không mắc cạnh trang trí. */
    private static final int NUA_RONG_CHAN = 27;
    /** Rùa chỉ đứng vững khi ít nhất quá nửa bề rộng chân còn được đỡ. */
    private static final int SO_DIEM_NEN_TOI_THIEU = 4;
    private static final int NUA_RONG_THAN_DI_CHUYEN = 30;
    private static final int CHIEU_CAO_THAN_DI_CHUYEN = 72;

    private DiChuyenBossRua() {
    }

    /**
     * Uoc tinh thoi gian BigBoss native chay tu toa do cu toi dich CMD21.
     * Server chot toa do ngay nhung phai doi client ve xong moi tung chieu.
     */
    public static long tinhThoiGianHoatAnhMs(
            short xCu,
            short yCu,
            short xMoi,
            short yMoi
    ) {
        int dx = Math.abs(xMoi - xCu);
        int dy = Math.abs(yMoi - yCu);
        if (dx == 0 && dy == 0) {
            return 0L;
        }
        int soBuocNgang = (dx + TOC_DO_CHAY - 1) / TOC_DO_CHAY;
        int soBuocDoc = (dy + TOC_DO_ROI - 1) / TOC_DO_ROI;
        int soBuoc = Math.max(1, Math.max(soBuocNgang, soBuocDoc));
        long uocTinh = soBuoc * (long) TRE_MOI_BUOC_MS + 150L;
        return Math.max(
                TRE_HOAT_ANH_TOI_THIEU_MS,
                Math.min(TRE_HOAT_ANH_TOI_DA_MS, uocTinh)
        );
    }

    public static ChickenChienBinh timNguoiSongGanNhat(
            ChickenChienBinh boss,
            ChickenChienBinh[] chienBinhs
    ) {
        if (boss == null || chienBinhs == null) {
            return null;
        }
        ChickenChienBinh ganNhat = null;
        long khoangCachNhoNhat = Long.MAX_VALUE;
        for (int i = 0; i < 8 && i < chienBinhs.length; i++) {
            ChickenChienBinh nguoi = chienBinhs[i];
            if (nguoi == null || nguoi.chet || nguoi.hp <= 0 || !nguoi.coPhien()) {
                continue;
            }
            long dx = (long) nguoi.x - boss.x;
            long dy = (long) ChickenKichThuocNhanVat.layTamThanNguoiChoiY(nguoi.y)
                    - (boss.y - CHIEU_CAO_HITBOX / 2L);
            long kc = dx * dx + dy * dy;
            if (kc < khoangCachNhoNhat) {
                khoangCachNhoNhat = kc;
                ganNhat = nguoi;
            }
        }
        return ganNhat;
    }

    public static int layHuongX(ChickenChienBinh boss, ChickenChienBinh mucTieu) {
        if (boss == null || mucTieu == null) {
            return 0;
        }
        int dx = mucTieu.x - boss.x;
        if (Math.abs(dx) <= 3) {
            return 0;
        }
        return dx < 0 ? -1 : 1;
    }

    public static boolean daChamNguoiChoi(
            ChickenChienBinh boss,
            ChickenChienBinh nguoi
    ) {
        if (boss == null || nguoi == null || nguoi.chet || nguoi.hp <= 0) {
            return false;
        }
        int bossTrai = boss.x - NUA_RONG_HITBOX;
        int bossPhai = boss.x + NUA_RONG_HITBOX;
        int bossTren = boss.y - CHIEU_CAO_HITBOX;
        int bossDuoi = boss.y;

        int nguoiTrai = nguoi.x - ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
        int nguoiPhai = nguoi.x + ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
        int nguoiTren = nguoi.y - ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_TREN;
        int nguoiDuoi = nguoi.y - ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_DUOI;

        return bossTrai <= nguoiPhai && bossPhai >= nguoiTrai
                && bossTren <= nguoiDuoi && bossDuoi >= nguoiTren;
    }

    public static boolean trungBossRua(int danX, int danY, int bossX, int bossY) {
        return danX >= bossX - NUA_RONG_HITBOX
                && danX <= bossX + NUA_RONG_HITBOX
                && danY >= bossY - CHIEU_CAO_HITBOX
                && danY <= bossY;
    }

    /** Trả về tọa độ bước kế tiếp nhưng không cho Rùa bước khỏi mép nền. */
    public static short[] tinhBuocTiepTheo(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int quangDuongConLai,
            int huongXKhoa,
            ChickenQuanLyBanDo banDo
    ) {
        if (boss == null || banDo == null || quangDuongConLai <= 0) {
            return new short[]{boss == null ? 0 : boss.x, boss == null ? 0 : boss.y};
        }

        // Rua dang bi luoi phu thi dung tai cho va van tung chieu. Khong gui
        // dich CMD21 ma BigBoss client khong the toi, neu khong isMove cua
        // client se bi giu va packet ban tiep theo khong chay.
        if (banDo.coMangNhenTrongVung(
                boss.x - NUA_RONG_HITBOX,
                boss.y - CHIEU_CAO_HITBOX,
                boss.x + NUA_RONG_HITBOX,
                boss.y)) {
            return new short[]{boss.x, boss.y};
        }

        int huong = huongXKhoa;
        if (huong == 0 && mucTieu != null) {
            huong = layHuongX(boss, mucTieu);
        }
        if (huong == 0) {
            return new short[]{boss.x, boss.y};
        }

        if (!coNenBenDuoi(boss.x, boss.y, banDo)) {
            return phaiDungTaiMepPhai(huong, banDo)
                    ? new short[]{boss.x, boss.y}
                    : roiTheoTrongLuc(
                            boss.x, boss.y, huong,
                            quangDuongConLai, banDo);
        }

        int buocX = Math.min(TOC_DO_CHAY, quangDuongConLai) * huong;
        int xMoi = boss.x + buocX;
        int gioiHanTrai = NUA_RONG_HITBOX;
        int gioiHanPhai = Math.max(gioiHanTrai, banDo.getWidth() - 1 - NUA_RONG_HITBOX);
        xMoi = Math.max(gioiHanTrai, Math.min(gioiHanPhai, xMoi));

        if (xMoi == boss.x) {
            return new short[]{boss.x, boss.y};
        }

        if (!thanBiChan(xMoi, boss.y, huong, banDo)
                && coNenBenDuoi(xMoi, boss.y, banDo)) {
            return new short[]{(short) xMoi, boss.y};
        }

        // Chỉ bước qua bậc thấp; không cho boss bật bay lên cao.
        for (int nang = 1; nang <= BUOC_LEN_TOI_DA; nang++) {
            int yMoi = boss.y - nang;
            if (!thanBiChan(xMoi, yMoi, huong, banDo)
                    && coNenBenDuoi(xMoi, yMoi, banDo)) {
                return new short[]{(short) xMoi, (short) yMoi};
            }
        }

        // Cho phép đi xuống dốc thấp, nhưng không biến một mép vực thành cú rơi.
        for (int ha = 1; ha <= BUOC_XUONG_TOI_DA; ha++) {
            int yMoi = boss.y + ha;
            if (!thanBiChan(xMoi, yMoi, huong, banDo)
                    && coNenBenDuoi(xMoi, yMoi, banDo)) {
                return new short[]{(short) xMoi, (short) yMoi};
            }
        }

        // Gặp tường thật thì dừng; chỉ dùng trọng lực khi phía trước là mép trống.
        if (thanBiChan(xMoi, boss.y, huong, banDo)) {
            return new short[]{boss.x, boss.y};
        }

        return phaiDungTaiMepPhai(huong, banDo)
                ? new short[]{boss.x, boss.y}
                : roiTheoTrongLuc(
                        boss.x, boss.y, huong,
                        quangDuongConLai, banDo);
    }

    public static boolean daRoiKhoiMap(
            ChickenChienBinh boss,
            ChickenQuanLyBanDo banDo
    ) {
        return boss != null && banDo != null && boss.y > banDo.getHeight() + 24;
    }

    /**
     * Sau khi đã dùng hết quãng chạy ngang, Rùa vẫn phải rơi thẳng xuống cho
     * tới khi có nền đỡ. Không cho thêm đà ngang ở giai đoạn này để mỗi lượt
     * không đi xa hơn cấu hình.
     */
    public static short[] tinhBuocRoiThangDung(
            ChickenChienBinh boss,
            ChickenQuanLyBanDo banDo
    ) {
        if (boss == null || banDo == null
                || coNenBenDuoi(boss.x, boss.y, banDo)) {
            return new short[]{
                boss == null ? 0 : boss.x,
                boss == null ? 0 : boss.y
            };
        }
        int yMoi = boss.y;
        for (int i = 1; i <= TOC_DO_ROI; i++) {
            int thuY = boss.y + i;
            yMoi = thuY;
            if (coNenBenDuoi(boss.x, thuY, banDo)) {
                break;
            }
        }
        yMoi = Math.min(banDo.getHeight() + 40, yMoi);
        return new short[]{boss.x, (short) yMoi};
    }

    private static boolean phaiDungTaiMepPhai(
            int huong,
            ChickenQuanLyBanDo banDo
    ) {
        return huong > 0
                && banDo != null
                && (banDo.layMaBanDo() & 0xFF) == CauHinhBossRua.MAP_ID;
    }

    private static short[] roiTheoTrongLuc(
            int x,
            int y,
            int huong,
            int quangDuongConLai,
            ChickenQuanLyBanDo banDo
    ) {
        int buocX = Math.min(TOC_DO_CHAY, quangDuongConLai) * huong;
        int xMin = NUA_RONG_HITBOX;
        int xMax = Math.max(xMin, banDo.getWidth() - 1 - NUA_RONG_HITBOX);
        int xDuKien = Math.max(xMin, Math.min(xMax, x + buocX));
        int xMoi = thanBiChan(xDuKien, y, huong, banDo) ? x : xDuKien;
        int yMoi = y;
        for (int i = 1; i <= TOC_DO_ROI; i++) {
            int thuY = y + i;
            if (coNenBenDuoi(xMoi, thuY, banDo)) {
                yMoi = thuY;
                break;
            }
            yMoi = thuY;
        }
        yMoi = Math.min(banDo.getHeight() + 40, yMoi);
        return new short[]{(short) xMoi, (short) yMoi};
    }

    private static boolean coNenBenDuoi(int x, int chanY, ChickenQuanLyBanDo banDo) {
        int yKiemTra = chanY + 1;
        return demDiemNenTaiY(x, yKiemTra, banDo) >= SO_DIEM_NEN_TOI_THIEU;
    }

    /**
     * Đổi tọa độ vật lý server sang tọa độ chân native mà BigBoss client có
     * thể đạt chính xác. Vật lý vẫn giữ quy ước cũ để không đổi hành vi mép
     * bệ; chỉ khi y hiện tại chưa nằm trong nền nhưng y+1 có đủ nền mới cộng
     * đúng một pixel trước khi phát CMD21.
     */
    public static short chuanHoaYChanClient(
            int x,
            short chanY,
            ChickenQuanLyBanDo banDo
    ) {
        if (banDo == null || demDiemNenTaiY(x, chanY, banDo)
                >= SO_DIEM_NEN_TOI_THIEU) {
            return chanY;
        }
        return demDiemNenTaiY(x, chanY + 1, banDo)
                >= SO_DIEM_NEN_TOI_THIEU
                ? (short) (chanY + 1)
                : chanY;
    }

    private static int demDiemNenTaiY(
            int x,
            int yKiemTra,
            ChickenQuanLyBanDo banDo
    ) {
        int soDiemCoNen = 0;
        for (int lech = -NUA_RONG_CHAN; lech <= NUA_RONG_CHAN; lech += 9) {
            int px = x + lech;
            if (px >= 0 && px < banDo.getWidth()
                    && yKiemTra >= 0
                    && banDo.coVaCham((short) px, (short) yKiemTra)) {
                soDiemCoNen++;
            }
        }
        /*
         * Không dùng quy tắc "chỉ một pixel chân chạm nền là đứng". Ở mép
         * gạch map 54, đuôi Rùa còn chạm 2-3 điểm khiến cả thân treo lơ lửng
         * và AI chuyển thẳng sang bắn. Thiếu quá nửa mặt chân thì phải rơi.
         */
        return soDiemCoNen;
    }

    private static boolean thanBiChan(
            int x,
            int chanY,
            int huong,
            ChickenQuanLyBanDo banDo
    ) {
        int mepTruoc = x + Integer.signum(huong) * NUA_RONG_THAN_DI_CHUYEN;
        int[] cacX = new int[]{
            x,
            mepTruoc
        };
        int[] cacY = new int[]{
            chanY - 6,
            chanY - 24,
            chanY - 45,
            chanY - CHIEU_CAO_THAN_DI_CHUYEN
        };
        for (int px : cacX) {
            if (px < 0 || px >= banDo.getWidth()) {
                return true;
            }
            for (int py : cacY) {
                if (py >= 0 && py < banDo.getHeight()
                        && banDo.coVaCham((short) px, (short) py)) {
                    return true;
                }
            }
        }
        return false;
    }
}
