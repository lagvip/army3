package com.chicken.phong.boss.trandau.baovay;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chiso.ChickenKichThuocNhanVat;
import com.chicken.luyentap.ChickenLuyenTapToaDo;

/** Logic riêng của ba boss Cảm tử. */
public final class BossCamTu {
    public static final int QUANG_DUONG_MOI_LUOT = 230;
    public static final int BUOC_DI_CHUYEN = 18;
    public static final int TRE_MOI_BUOC_MS = 70;
    /** Sai số nhỏ quanh trục X không được dùng để đảo hướng liên tục. */
    private static final int NGUONG_DOI_HUONG_X = 4;
    /** Nạp đạn tạm thời sau một lượt di chuyển chưa chạm mục tiêu. */
    public static final int NAP_DAN_SAU_DI_CHUYEN = 300;

    private BossCamTu() {
    }

    public static ChickenChienBinh timNguoiSongGanNhat(
            ChickenChienBinh camTu,
            ChickenChienBinh[] chienBinhs
    ) {
        ChickenChienBinh ganNhat = null;
        long khoangCachNhoNhat = Long.MAX_VALUE;
        for (int i = 0; i < 8 && i < chienBinhs.length; i++) {
            ChickenChienBinh nguoiChoi = chienBinhs[i];
            if (nguoiChoi == null || nguoiChoi.chet || nguoiChoi.hp <= 0 || !nguoiChoi.coPhien()) {
                continue;
            }
            long dx = (long) nguoiChoi.x - camTu.x;
            long dy = (long) nguoiChoi.y - camTu.y;
            long khoangCach = dx * dx + dy * dy;
            if (khoangCach < khoangCachNhoNhat
                    || (khoangCach == khoangCachNhoNhat
                    && (ganNhat == null || (nguoiChoi.chiSo & 0xFF) < (ganNhat.chiSo & 0xFF)))) {
                ganNhat = nguoiChoi;
                khoangCachNhoNhat = khoangCach;
            }
        }
        return ganNhat;
    }

    public static boolean daChamNguoiChoi(ChickenChienBinh camTu, ChickenChienBinh nguoiChoi) {
        int camTuTrai = camTu.x - ChickenKichThuocNhanVat.BOSS_NUA_RONG;
        int camTuPhai = camTu.x + ChickenKichThuocNhanVat.BOSS_NUA_RONG;
        int camTuTren = camTu.y - ChickenKichThuocNhanVat.BOSS_LECH_TREN;
        int camTuDuoi = camTu.y - ChickenKichThuocNhanVat.BOSS_LECH_DUOI;

        int nguoiTrai = nguoiChoi.x - ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
        int nguoiPhai = nguoiChoi.x + ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
        int nguoiTren = nguoiChoi.y - ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_TREN;
        int nguoiDuoi = nguoiChoi.y - ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_DUOI;

        return camTuTrai <= nguoiPhai && camTuPhai >= nguoiTrai
                && camTuTren <= nguoiDuoi && camTuDuoi >= nguoiTren;
    }

    /**
     * Tính một bước đi có va chạm và trọng lực.
     *
     * Cảm tử chỉ tự điều khiển theo trục X. Trục Y tuyệt đối không đuổi thẳng
     * theo mục tiêu vì cách cũ làm boss rơi xuống rồi bị kéo bay ngược lên.
     * Khi mất nền, Y chỉ tăng theo từng bước trọng lực cho tới khi chạm nền.
     */
    public static short[] tinhBuocTiepTheo(
            ChickenChienBinh camTu,
            ChickenChienBinh mucTieu,
            int quangDuongConLai,
            int huongXKhoa,
            ChickenQuanLyBanDo map
    ) {
        if (camTu == null || mucTieu == null || map == null
                || quangDuongConLai <= 0) {
            return new short[]{
                camTu == null ? 0 : camTu.x,
                camTu == null ? 0 : camTu.y
            };
        }

        int huongX = huongXKhoa;
        if (huongX == 0) {
            huongX = layHuongX(camTu, mucTieu);
        }

        boolean dangCoNen = ChickenLuyenTapToaDo.coNenDoDuoiHaiChan(
                map, camTu.x, camTu.y);
        int khoangX = Math.abs(mucTieu.x - camTu.x);
        int buocNgang = dangCoNen
                ? Math.min(
                        Math.min(BUOC_DI_CHUYEN, quangDuongConLai),
                        khoangX)
                : 0;
        // Khi đang rơi chỉ hạ theo trọng lực, không chạy ngang giữa không trung.
        int xDuKien = camTu.x + huongX * buocNgang;
        xDuKien = Math.max(
                ChickenKichThuocNhanVat.BOSS_NUA_RONG,
                Math.min(
                        map.getWidth() - 1 - ChickenKichThuocNhanVat.BOSS_NUA_RONG,
                        xDuKien
                )
        );

        // Quét từng pixel ngang để không xuyên qua tường hoặc khối map mỏng.
        int xMoi = timXDiChuyenAnToan(map, camTu.x, xDuKien, camTu.y);
        int yMoi = camTu.y;

        if (ChickenLuyenTapToaDo.coNenDoDuoiHaiChan(
                map, (short) xMoi, (short) yMoi)
                && thanBossThongThoang(map, (short) xMoi, (short) yMoi)) {
            return new short[]{(short) xMoi, (short) yMoi};
        }

        short nenGan = timNenOnDinhGan(map, (short) xMoi, (short) yMoi);
        if (nenGan != Short.MIN_VALUE && nenGan >= yMoi) {
            yMoi = nenGan;
        } else {
            // Không tìm thấy nền gần: rơi đúng một bước, không được bật lên.
            yMoi = Math.min(map.getHeight() + 32, yMoi + BUOC_TRONG_LUC);
        }

        return new short[]{(short) xMoi, (short) yMoi};
    }

    private static final int BUOC_TRONG_LUC = 12;
    private static final int DO_ROI_GAN_TOI_DA = 12;

    private static int timXDiChuyenAnToan(
            ChickenQuanLyBanDo map,
            int xHienTai,
            int xDuKien,
            int footY
    ) {
        if (xDuKien == xHienTai) {
            return xHienTai;
        }
        int huong = xDuKien > xHienTai ? 1 : -1;
        int xAnToan = xHienTai;
        for (int x = xHienTai + huong;
                huong > 0 ? x <= xDuKien : x >= xDuKien;
                x += huong) {
            if (!thanBossThongThoang(map, (short) x, (short) footY)) {
                break;
            }
            xAnToan = x;
        }
        return xAnToan;
    }

    private static short timNenOnDinhGan(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        int[] lechX = new int[]{0, -4, 4, -8, 8, -12, 12};
        short nenTotNhat = Short.MIN_VALUE;
        int doRoiNhoNhat = Integer.MAX_VALUE;
        for (int dx : lechX) {
            short xKiemTra = (short) Math.max(
                    0, Math.min(map.getWidth() - 1, x + dx));
            short nen = ChickenLuyenTapToaDo.timMatDatTaiHoacThapHon(
                    map,
                    xKiemTra,
                    footY,
                    (kiemTraX, kiemTraY) ->
                            thanBossThongThoang(map, kiemTraX, kiemTraY)
            );
            if (nen == Short.MIN_VALUE || nen < footY) {
                continue;
            }
            int doRoi = nen - footY;
            if (doRoi <= DO_ROI_GAN_TOI_DA && doRoi < doRoiNhoNhat) {
                nenTotNhat = nen;
                doRoiNhoNhat = doRoi;
            }
        }
        return nenTotNhat;
    }

    public static boolean thanBossThongThoang(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        if (map == null) {
            return false;
        }
        int trai = x - ChickenKichThuocNhanVat.BOSS_NUA_RONG + 2;
        int phai = x + ChickenKichThuocNhanVat.BOSS_NUA_RONG - 2;
        int tren = footY - ChickenKichThuocNhanVat.BOSS_LECH_TREN;
        int duoi = footY - ChickenKichThuocNhanVat.BOSS_LECH_DUOI - 1;
        for (int py = Math.max(0, tren);
                py <= Math.min(map.getHeight() - 1, duoi);
                py += 2) {
            for (int px = Math.max(0, trai);
                    px <= Math.min(map.getWidth() - 1, phai);
                    px += 2) {
                if (map.coVaCham((short) px, (short) py)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean daRoiKhoiMap(
            ChickenChienBinh camTu,
            ChickenQuanLyBanDo map
    ) {
        return camTu != null && map != null
                && camTu.y > map.getHeight() + 24;
    }

    public static int layHuongX(ChickenChienBinh camTu, ChickenChienBinh mucTieu) {
        if (camTu == null || mucTieu == null) {
            return 0;
        }
        int dx = mucTieu.x - camTu.x;
        if (Math.abs(dx) <= NGUONG_DOI_HUONG_X) {
            return 0;
        }
        return dx < 0 ? -1 : 1;
    }
}
