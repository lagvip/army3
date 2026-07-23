package com.chicken.phong.boss.trandau.rua;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chiso.ChickenKichThuocNhanVat;

/**
 * Cơ chế di chuyển riêng của Boss Rùa.
 * Y là tọa độ chân. Boss chỉ chạy khi đang có nền; mất nền thì dừng X và rơi
 * theo trọng lực cho tới khi chạm địa hình hoặc rơi khỏi map.
 */
public final class DiChuyenBossRua {
    public static final int QUANG_DUONG_MOI_LUOT = 170;
    public static final int TRE_MOI_BUOC_MS = 85;

    private static final int TOC_DO_CHAY = 9;
    private static final int TOC_DO_ROI = 12;
    private static final int BUOC_LEN_TOI_DA = 12;

    /** Vùng thân dùng cho va chạm đạn và chạm người chơi. */
    public static final int NUA_RONG_HITBOX = 58;
    public static final int CHIEU_CAO_HITBOX = 108;

    /** Vùng chân dùng để dò nền, hẹp hơn sprite để không mắc cạnh trang trí. */
    private static final int NUA_RONG_CHAN = 27;
    private static final int NUA_RONG_THAN_DI_CHUYEN = 30;
    private static final int CHIEU_CAO_THAN_DI_CHUYEN = 72;

    private DiChuyenBossRua() {
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

    /** Trả về tọa độ bước kế tiếp. Boss không thay đổi X trong lúc đang rơi. */
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

        if (!coNenBenDuoi(boss.x, boss.y, banDo)) {
            return roiTheoTrongLuc(boss.x, boss.y, banDo);
        }

        int huong = huongXKhoa;
        if (huong == 0 && mucTieu != null) {
            huong = layHuongX(boss, mucTieu);
        }
        if (huong == 0) {
            return new short[]{boss.x, boss.y};
        }

        int buocX = Math.min(TOC_DO_CHAY, quangDuongConLai) * huong;
        int xMoi = boss.x + buocX;
        int gioiHanTrai = NUA_RONG_HITBOX;
        int gioiHanPhai = Math.max(gioiHanTrai, banDo.getWidth() - 1 - NUA_RONG_HITBOX);
        xMoi = Math.max(gioiHanTrai, Math.min(gioiHanPhai, xMoi));

        if (xMoi == boss.x) {
            return new short[]{boss.x, boss.y};
        }

        if (!thanBiChan(xMoi, boss.y, banDo)) {
            return new short[]{(short) xMoi, boss.y};
        }

        // Chỉ bước qua bậc thấp; không cho boss bật bay lên cao.
        for (int nang = 1; nang <= BUOC_LEN_TOI_DA; nang++) {
            int yMoi = boss.y - nang;
            if (!thanBiChan(xMoi, yMoi, banDo)
                    && coNenBenDuoi(xMoi, yMoi, banDo)) {
                return new short[]{(short) xMoi, (short) yMoi};
            }
        }

        return new short[]{boss.x, boss.y};
    }

    public static boolean daRoiKhoiMap(
            ChickenChienBinh boss,
            ChickenQuanLyBanDo banDo
    ) {
        return boss != null && banDo != null && boss.y > banDo.getHeight() + 24;
    }

    private static short[] roiTheoTrongLuc(int x, int y, ChickenQuanLyBanDo banDo) {
        int yMoi = y;
        for (int i = 1; i <= TOC_DO_ROI; i++) {
            int thuY = y + i;
            if (coNenBenDuoi(x, thuY, banDo)) {
                yMoi = thuY;
                break;
            }
            yMoi = thuY;
        }
        yMoi = Math.min(banDo.getHeight() + 40, yMoi);
        return new short[]{(short) x, (short) yMoi};
    }

    private static boolean coNenBenDuoi(int x, int chanY, ChickenQuanLyBanDo banDo) {
        int yKiemTra = chanY + 1;
        for (int lech = -NUA_RONG_CHAN; lech <= NUA_RONG_CHAN; lech += 9) {
            int px = x + lech;
            if (px >= 0 && px < banDo.getWidth()
                    && yKiemTra >= 0
                    && banDo.coVaCham((short) px, (short) yKiemTra)) {
                return true;
            }
        }
        return false;
    }

    private static boolean thanBiChan(int x, int chanY, ChickenQuanLyBanDo banDo) {
        int[] cacX = new int[]{
            x - NUA_RONG_THAN_DI_CHUYEN,
            x,
            x + NUA_RONG_THAN_DI_CHUYEN
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
