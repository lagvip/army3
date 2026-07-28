package com.chicken.phong.boss.trandau.rong;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chiso.ChickenKichThuocNhanVat;
import java.util.concurrent.ThreadLocalRandom;

/** Di chuyển bay và tọa độ kẹp/thả riêng của BigBoss Rồng. */
public final class DiChuyenBossRong {
    /*
     * Bullet client native mo rong pW=20, pH=35 cua BigBoss them 15 px moi
     * canh: Rectangle(x - 25, y - 50, 50, 65). CRes.inRect dung canh phai
     * va canh duoi dang exclusive, nen pixel cuoi la x+24 va y+14.
     */
    private static final int NUA_RONG_NGUOI_CHOI =
            ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
    private static final int CAO_THAN_NGUOI_CHOI =
            ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_TREN;

    private DiChuyenBossRong() {
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
                    - (boss.y
                            + (CauHinhBossRong.HITBOX_LECH_DUOI
                                    - CauHinhBossRong.HITBOX_LECH_TREN) / 2L);
            long kc = dx * dx + dy * dy;
            if (kc < khoangCachNhoNhat) {
                khoangCachNhoNhat = kc;
                ganNhat = nguoi;
            }
        }
        return ganNhat;
    }

    /** Nội suy một bước bay thẳng, không dùng trọng lực và không bám địa hình. */
    public static short[] tinhBuocBay(
            int tuX,
            int tuY,
            int denX,
            int denY,
            ChickenQuanLyBanDo banDo
    ) {
        if (banDo == null) {
            return new short[]{(short) tuX, (short) tuY};
        }
        double dx = denX - tuX;
        double dy = denY - tuY;
        double kc = Math.hypot(dx, dy);
        if (kc <= 1.0D) {
            return kepTrongMap(denX, denY, banDo);
        }
        double tiLe = Math.min(
                1.0D,
                CauHinhBossRong.BUOC_BAY_TOI_DA / kc);
        int x = (int) Math.round(tuX + dx * tiLe);
        int y = (int) Math.round(tuY + dy * tiLe);
        return kepTrongMap(x, y, banDo);
    }

    public static boolean daDenGan(int x, int y, int denX, int denY) {
        return Math.hypot(denX - x, denY - y)
                <= CauHinhBossRong.BUOC_BAY_TOI_DA;
    }

    /** Tra ve mot dich bay hop le de server chi can gui mot lenh di chuyen. */
    public static short[] chuanHoaDiemBay(
            int x,
            int y,
            ChickenQuanLyBanDo banDo
    ) {
        if (banDo == null) {
            return new short[]{(short) x, (short) y};
        }
        return kepTrongMap(x, y, banDo);
    }

    /**
     * Uoc luong thoi gian client native noi suy toi dich. Server khong gui
     * them diem trung gian trong khoang nay de tranh ghi de xToNow lien tuc.
     */
    public static long tinhThoiGianBayMs(
            int tuX,
            int tuY,
            int denX,
            int denY
    ) {
        double khoangCach = Math.hypot(denX - tuX, denY - tuY);
        long soFrame = Math.max(
                1L,
                (long) Math.ceil(
                        khoangCach
                                / CauHinhBossRong.TOC_DO_BAY_CLIENT_MOI_FRAME)
        );
        return Math.max(
                CauHinhBossRong.THOI_GIAN_BAY_TOI_THIEU_MS,
                Math.min(
                        CauHinhBossRong.THOI_GIAN_BAY_TOI_DA_MS,
                        soFrame * CauHinhBossRong.THOI_GIAN_FRAME_CLIENT_MS
                )
        );
    }

    /**
     * Chon mot diem trong khong gian quanh nguoi choi de Rong bay toi truoc
     * khi ban. Diem cuoi do server chon, duoc kep trong map va khong nam trong
     * dia hinh; client chi nhan dich bay de ve animation.
     */
    public static short[] chonDiemBayQuanhNguoi(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenQuanLyBanDo banDo
    ) {
        if (boss == null || mucTieu == null || banDo == null) {
            return new short[]{
                (short) (boss == null ? 0 : boss.x),
                (short) (boss == null ? 0 : boss.y)
            };
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int lan = 0; lan < CauHinhBossRong.SO_LAN_THU_DIEM_BAY_QUANH;
                lan++) {
            int khoangX = random.nextInt(
                    CauHinhBossRong.KHOANG_X_BAY_QUANH_TOI_THIEU,
                    CauHinhBossRong.KHOANG_X_BAY_QUANH_TOI_DA + 1);
            int huong = random.nextBoolean() ? 1 : -1;
            int doCao = random.nextInt(
                    CauHinhBossRong.DO_CAO_BAY_QUANH_TOI_THIEU,
                    CauHinhBossRong.DO_CAO_BAY_QUANH_TOI_DA + 1);
            short[] diem = kepTrongMap(
                    mucTieu.x + huong * khoangX,
                    mucTieu.y - doCao,
                    banDo);
            if (Math.hypot(diem[0] - boss.x, diem[1] - boss.y)
                            >= CauHinhBossRong.BUOC_BAY_TOI_DA
                    && laVungBayTrong(diem[0], diem[1], banDo)) {
                return diem;
            }
        }

        // O ria map, uu tien phia con nhieu khoang trong hon va nang Rong cao
        // them. Day la diem du phong xac dinh, tranh dung im roi ban tai cho.
        int huongVaoMap = mucTieu.x < banDo.getWidth() / 2 ? 1 : -1;
        for (int doCao = CauHinhBossRong.DO_CAO_BAY_QUANH_TOI_THIEU;
                doCao <= CauHinhBossRong.DO_CAO_BAY_QUANH_TOI_DA + 120;
                doCao += 12) {
            short[] diem = kepTrongMap(
                    mucTieu.x + huongVaoMap
                            * CauHinhBossRong.KHOANG_X_BAY_QUANH_TOI_THIEU,
                    mucTieu.y - doCao,
                    banDo);
            if (laVungBayTrong(diem[0], diem[1], banDo)) {
                return diem;
            }
        }
        return kepTrongMap(
                mucTieu.x + huongVaoMap
                        * CauHinhBossRong.KHOANG_X_BAY_QUANH_TOI_THIEU,
                mucTieu.y - CauHinhBossRong.DO_CAO_BAY_QUANH_TOI_DA,
                banDo);
    }

    /** Toan bo hitbox native cua Rong tai diem dung phai nam ngoai dia hinh. */
    public static boolean laVungBayTrong(
            int x,
            int y,
            ChickenQuanLyBanDo banDo
    ) {
        if (banDo == null) {
            return false;
        }
        for (int px = x - CauHinhBossRong.HITBOX_LECH_TRAI;
                px <= x + CauHinhBossRong.HITBOX_LECH_PHAI; px++) {
            for (int py = y - CauHinhBossRong.HITBOX_LECH_TREN;
                    py <= y + CauHinhBossRong.HITBOX_LECH_DUOI; py++) {
                if (banDo.coVaCham((short) px, (short) py)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Điểm trung chuyển: bay sang nửa map đối diện và nâng mục tiêu lên cao. */
    public static short[] chonDiemMangQua(
            ChickenChienBinh mucTieu,
            ChickenQuanLyBanDo banDo
    ) {
        int nuaMap = banDo.getWidth() / 2;
        int x = mucTieu.x < nuaMap
                ? Math.max(
                        nuaMap + CauHinhBossRong.LECH_NUA_MAP_DIEM_MANG,
                        banDo.getWidth()
                                - CauHinhBossRong.CACH_RIA_MAP_DIEM_MANG)
                : Math.min(
                        nuaMap - CauHinhBossRong.LECH_NUA_MAP_DIEM_MANG,
                        CauHinhBossRong.CACH_RIA_MAP_DIEM_MANG);
        int y = Math.max(
                CauHinhBossRong.Y_DIEM_MANG_TOI_THIEU,
                Math.min(
                        CauHinhBossRong.Y_DIEM_MANG_TOI_DA,
                        mucTieu.y
                                - CauHinhBossRong.DO_CAO_NANG_NGUOI_KHI_MANG));
        return kepTrongMap(x, y, banDo);
    }

    /** Tìm mặt đất đầu tiên bên dưới chân người chơi. */
    public static int timChanDatBenDuoi(
            int x,
            int batDauY,
            ChickenQuanLyBanDo banDo
    ) {
        int start = Math.max(0, batDauY);
        int max = banDo.getHeight() - 2;
        for (int y = start; y <= max; y++) {
            if (coNenNguoiChoi(x, y, banDo)) {
                return y;
            }
        }
        return -1;
    }

    /**
     * Tim diem tha gan X uu tien nhat ma toan bo than nguoi choi khong nam
     * trong dia hinh. Neu cot uu tien bi mot tang da bit kin, tim dan sang hai
     * ben; khong co nen hop le thi tra ve null de xu ly roi xuong vuc.
     */
    public static short[] chonDiemThaAnToan(
            int xUuTien,
            int batDauY,
            ChickenQuanLyBanDo banDo
    ) {
        if (banDo == null || banDo.getWidth() <= NUA_RONG_NGUOI_CHOI * 2
                || banDo.getHeight() <= CAO_THAN_NGUOI_CHOI + 1) {
            return null;
        }
        int xMin = NUA_RONG_NGUOI_CHOI;
        int xMax = banDo.getWidth() - 1 - NUA_RONG_NGUOI_CHOI;
        int xGiua = Math.max(xMin, Math.min(xMax, xUuTien));
        int gioiHan = Math.max(xGiua - xMin, xMax - xGiua);
        for (int doLech = 0; doLech <= gioiHan;
                doLech += CauHinhBossRong.BUOC_TIM_X_THA_AN_TOAN) {
            int xPhai = xGiua + doLech;
            if (xPhai <= xMax) {
                int y = timChanDatAnToanTaiX(xPhai, batDauY, banDo);
                if (y >= 0) {
                    return new short[]{(short) xPhai, (short) y};
                }
            }
            if (doLech == 0) {
                continue;
            }
            int xTrai = xGiua - doLech;
            if (xTrai >= xMin) {
                int y = timChanDatAnToanTaiX(xTrai, batDauY, banDo);
                if (y >= 0) {
                    return new short[]{(short) xTrai, (short) y};
                }
            }
        }
        return null;
    }

    public static boolean laDiemThaAnToan(
            int x,
            int chanY,
            ChickenQuanLyBanDo banDo
    ) {
        if (banDo == null
                || x - NUA_RONG_NGUOI_CHOI < 0
                || x + NUA_RONG_NGUOI_CHOI >= banDo.getWidth()
                || chanY - CAO_THAN_NGUOI_CHOI < 0
                || chanY + 1 >= banDo.getHeight()
                || !coNenNguoiChoi(x, chanY, banDo)) {
            return false;
        }
        for (int px = x - NUA_RONG_NGUOI_CHOI;
                px <= x + NUA_RONG_NGUOI_CHOI; px++) {
            for (int py = chanY - CAO_THAN_NGUOI_CHOI;
                    py <= chanY; py++) {
                if (banDo.coVaCham((short) px, (short) py)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean trungBossRong(int danX, int danY, int bossX, int bossY) {
        return danX >= bossX - CauHinhBossRong.HITBOX_LECH_TRAI
                && danX <= bossX + CauHinhBossRong.HITBOX_LECH_PHAI
                && danY >= bossY - CauHinhBossRong.HITBOX_LECH_TREN
                && danY <= bossY + CauHinhBossRong.HITBOX_LECH_DUOI;
    }

    private static int timChanDatAnToanTaiX(
            int x,
            int batDauY,
            ChickenQuanLyBanDo banDo
    ) {
        int start = Math.max(CAO_THAN_NGUOI_CHOI, batDauY);
        int max = banDo.getHeight() - 2;
        for (int y = start; y <= max; y++) {
            if (coNenNguoiChoi(x, y, banDo)
                    && laDiemThaAnToan(x, y, banDo)) {
                return y;
            }
        }
        return -1;
    }

    private static boolean coNenNguoiChoi(
            int x,
            int chanY,
            ChickenQuanLyBanDo banDo
    ) {
        int y = chanY + 1;
        if (y < 0 || y >= banDo.getHeight()) {
            return false;
        }
        int[] lechX = new int[]{-5, 0, 5};
        for (int lech : lechX) {
            int px = x + lech;
            if (px >= 0 && px < banDo.getWidth()
                    && y >= 0
                    && banDo.coVaCham((short) px, (short) y)) {
                return true;
            }
        }
        return false;
    }

    private static short[] kepTrongMap(int x, int y, ChickenQuanLyBanDo banDo) {
        int xMin = CauHinhBossRong.HITBOX_LECH_TRAI;
        int xMax = Math.max(
                xMin,
                banDo.getWidth() - 1
                        - CauHinhBossRong.HITBOX_LECH_TRAI);
        int yMin = CauHinhBossRong.HITBOX_LECH_TREN;
        int yMax = Math.max(
                yMin,
                banDo.getHeight() - 1
                        - CauHinhBossRong.HITBOX_LECH_DUOI);
        x = Math.max(xMin, Math.min(xMax, x));
        y = Math.max(yMin, Math.min(yMax, y));
        return new short[]{(short) x, (short) y};
    }
}
