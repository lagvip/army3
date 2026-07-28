package com.chicken.phong.boss.trandau.baovay;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chiso.ChickenKichThuocNhanVat;

/**
 * Cong thuc Cam tu cua map Bao vay.
 *
 * Lop nay uy quyen truc tiep sang cong thuc Hai toa thap de hai che do
 * khong the lech quang duong, va cham, thoi gian animation hay damage no
 * trong nhung lan sua sau.
 */
public final class BossCamTu {
    private static final int SAI_SO_NEO_CHAN_Y = 4;
    /** Mat nen day map 50 lien tuc tu X=97 den X=994. */
    private static final int MEP_NEN_TRAI_MAP_50 = 97;
    private static final int MEP_NEN_PHAI_MAP_50 = 994;
    /**
     * Tam hitbox phai cach mep nen it nhat nua chieu rong boss. Vi vay Cam
     * tu co the tien den ria, nhung toan than khong bao gio vuot ra ngoai.
     */
    public static final int MEP_TRAI_AN_TOAN_MAP_50 =
            MEP_NEN_TRAI_MAP_50 + ChickenKichThuocNhanVat.BOSS_NUA_RONG;
    public static final int MEP_PHAI_AN_TOAN_MAP_50 =
            MEP_NEN_PHAI_MAP_50 - ChickenKichThuocNhanVat.BOSS_NUA_RONG;
    public static final int QUANG_DUONG_MOI_LUOT =
            com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                    .QUANG_DUONG_MOI_LUOT;
    public static final int BUOC_DI_CHUYEN =
            com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                    .BUOC_DI_CHUYEN;
    public static final int TRE_MOI_BUOC_MS =
            com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                    .TRE_MOI_BUOC_MS;
    public static final int NAP_DAN_SAU_DI_CHUYEN =
            com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                    .NAP_DAN_SAU_DI_CHUYEN;

    private BossCamTu() {
    }

    public static ChickenChienBinh timNguoiSongGanNhat(
            ChickenChienBinh camTu,
            ChickenChienBinh[] chienBinhs
    ) {
        return com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                .timNguoiSongGanNhat(camTu, chienBinhs);
    }

    public static boolean daChamNguoiChoi(
            ChickenChienBinh camTu,
            ChickenChienBinh nguoiChoi
    ) {
        return com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                .daChamNguoiChoi(camTu, nguoiChoi);
    }

    public static boolean trongPhamViKichNo(
            ChickenChienBinh camTu,
            ChickenChienBinh nguoiChoi,
            int banKinh
    ) {
        return com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                .trongPhamViKichNo(camTu, nguoiChoi, banKinh);
    }

    public static int tinhSatThuongNoTheoKhoangCach(
            ChickenChienBinh camTu,
            ChickenChienBinh nguoiChoi,
            int satThuongToiDa,
            int banKinh,
            int phanTramTaiRia
    ) {
        return com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                .tinhSatThuongNoTheoKhoangCach(
                        camTu,
                        nguoiChoi,
                        satThuongToiDa,
                        banKinh,
                        phanTramTaiRia);
    }

    public static short[] tinhBuocTiepTheo(
            ChickenChienBinh camTu,
            ChickenChienBinh mucTieu,
            int quangDuongConLai,
            int huongXKhoa,
            ChickenQuanLyBanDo map
    ) {
        int huongDuoi = Integer.signum(huongXKhoa);
        if (huongDuoi == 0 && camTu != null && mucTieu != null) {
            huongDuoi = Integer.signum(mucTieu.x - camTu.x);
        }
        if (camTu != null
                && ((camTu.x <= MEP_TRAI_AN_TOAN_MAP_50 && huongDuoi < 0)
                || (camTu.x >= MEP_PHAI_AN_TOAN_MAP_50 && huongDuoi > 0))) {
            return new short[]{camTu.x, camTu.y};
        }
        short[] buoc = com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                .tinhBuocTiepTheo(
                        camTu,
                        mucTieu,
                        quangDuongConLai,
                        huongXKhoa,
                        map);
        if (buoc != null && buoc.length >= 2) {
            buoc = chotDiemDungTrongMepAnToan(camTu, buoc, map);
            if (buoc[0] == MEP_TRAI_AN_TOAN_MAP_50
                    || buoc[0] == MEP_PHAI_AN_TOAN_MAP_50) {
                short matNenGan = timMatNenGanCaoDo(
                        map, buoc[0], buoc[1]);
                if (matNenGan != Short.MIN_VALUE) {
                    buoc[1] = matNenGan;
                }
            }
        }
        if (buoc == null || buoc.length < 2
                || coMatNenThatSuTaiTam(map, buoc[0], buoc[1])) {
            return buoc;
        }

        /*
         * Map Bao vay gom nhieu buc gach phang tach roi. Cong thuc dung chung
         * cho map 51 cho phep mot ban chan bam mai doc; tren map 50 dieu do co
         * the nham canh doc cua vien gach la nen va giu Cam tu lo lung o khe.
         * Neu tam chan da hut mat tren, khong duoc roi ngay tai diem mot ban
         * chan van con mac vao mep gach. Client se co gang noi suy cheo qua
         * canh gach, dung o do va lat sprite qua lai. Hay dung het phan quang
         * duong ngang con lai de toan hitbox thoat buc, sau do moi neo xuong
         * nen hop le tiep theo.
         */
        short[] diemRoi = timDiemRoiDaThoatKhoiBuc(
                map,
                camTu,
                mucTieu,
                quangDuongConLai,
                huongXKhoa,
                buoc[0]);
        if (diemRoi != null) {
            return chotDiemDungTrongMepAnToan(camTu, diemRoi, map);
        }

        /*
         * Chua thoat het mep buc trong luot nay: chi gui dich ngang ma cong
         * thuc dung chung da tim duoc. Luot sau Cam tu se tiep tuc tien ra
         * khoi mep. Tuyet doi khong gui dich roi cheo ngay tai canh gach.
         */
        if (buoc[0] != camTu.x) {
            return chotDiemDungTrongMepAnToan(camTu, buoc, map);
        }

        short yNen = timMatNenThapHon(map, buoc[0], buoc[1]);
        if (yNen != Short.MIN_VALUE) {
            return chotDiemDungTrongMepAnToan(
                    camTu, new short[]{buoc[0], yNen}, map);
        }
        return chotDiemDungTrongMepAnToan(camTu, new short[]{
            buoc[0],
            (short) Math.min(map.getHeight() + 32, buoc[1] + 12)
        }, map);
    }

    private static short[] timDiemRoiDaThoatKhoiBuc(
            ChickenQuanLyBanDo map,
            ChickenChienBinh camTu,
            ChickenChienBinh mucTieu,
            int quangDuongConLai,
            int huongXKhoa,
            short xBuocAnToan
    ) {
        if (map == null || camTu == null || mucTieu == null
                || quangDuongConLai <= 0) {
            return null;
        }
        int huong = Integer.signum(huongXKhoa);
        if (huong == 0) {
            huong = Integer.signum(mucTieu.x - camTu.x);
        }
        if (huong == 0) {
            return null;
        }

        int quangDuongX = Math.min(
                quangDuongConLai,
                Math.abs(mucTieu.x - camTu.x));
        int gioiHanTrai = MEP_TRAI_AN_TOAN_MAP_50;
        int gioiHanPhai = Math.min(
                MEP_PHAI_AN_TOAN_MAP_50,
                map.getWidth() - 1
                        - ChickenKichThuocNhanVat.BOSS_NUA_RONG);
        int xDayDu = Math.max(
                gioiHanTrai,
                Math.min(gioiHanPhai, camTu.x + huong * quangDuongX));

        for (int x = xDayDu;
                huong > 0 ? x >= xBuocAnToan : x <= xBuocAnToan;
                x -= huong) {
            short xKiemTra = (short) x;
            if (!daThoatKhoiMatBucCu(map, xKiemTra, camTu.y)) {
                continue;
            }
            short yNen = timMatNenThapHon(map, xKiemTra, camTu.y);
            if (yNen != Short.MIN_VALUE) {
                return new short[]{xKiemTra, yNen};
            }
            return new short[]{
                xKiemTra,
                (short) Math.min(map.getHeight() + 32, camTu.y + 12)
            };
        }
        return null;
    }

    private static short kepXTrongMepAnToan(short x) {
        return (short) Math.max(
                MEP_TRAI_AN_TOAN_MAP_50,
                Math.min(MEP_PHAI_AN_TOAN_MAP_50, x));
    }

    private static short[] chotDiemDungTrongMepAnToan(
            ChickenChienBinh camTu,
            short[] diem,
            ChickenQuanLyBanDo map
    ) {
        if (camTu == null || diem == null || diem.length < 2) {
            return diem;
        }
        short xAnToan = kepXTrongMepAnToan(diem[0]);
        if (((xAnToan - camTu.x) & 1) != 0) {
            xAnToan = (short) (xAnToan
                    - Integer.signum(xAnToan - camTu.x));
        }
        short[] daChot =
                com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                        .chotDiemDungChoClient(
                                camTu,
                                new short[]{xAnToan, diem[1]},
                                map);
        if (daChot == null || daChot.length < 2) {
            return daChot;
        }
        short xDaChot = kepXTrongMepAnToan(daChot[0]);
        if (((xDaChot - camTu.x) & 1) != 0) {
            xDaChot = (short) (xDaChot
                    - Integer.signum(xDaChot - camTu.x));
        }
        daChot[0] = xDaChot;
        return daChot;
    }

    private static short timMatNenGanCaoDo(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        if (map == null) {
            return Short.MIN_VALUE;
        }
        for (int doLech = 0; doLech <= SAI_SO_NEO_CHAN_Y; doLech++) {
            int yTren = footY - doLech;
            if (laMatTren(map, x, yTren)) {
                return (short) yTren;
            }
            int yDuoi = footY + doLech;
            if (doLech > 0 && laMatTren(map, x, yDuoi)) {
                return (short) yDuoi;
            }
        }
        return Short.MIN_VALUE;
    }

    private static boolean daThoatKhoiMatBucCu(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        int trai = Math.max(
                0, x - ChickenKichThuocNhanVat.BOSS_NUA_RONG);
        int phai = Math.min(
                map.getWidth() - 1,
                x + ChickenKichThuocNhanVat.BOSS_NUA_RONG);
        int tuY = Math.max(1, footY - SAI_SO_NEO_CHAN_Y);
        int denY = Math.min(
                map.getHeight() - 1,
                footY + SAI_SO_NEO_CHAN_Y);
        for (int px = trai; px <= phai; px++) {
            for (int y = tuY; y <= denY; y++) {
                if (laMatTren(map, (short) px, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean coMatNenThatSuTaiTam(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        if (map == null) {
            return false;
        }
        int tuY = Math.max(1, footY - SAI_SO_NEO_CHAN_Y);
        int denY = Math.min(
                map.getHeight() - 1,
                footY + SAI_SO_NEO_CHAN_Y);
        for (int y = tuY; y <= denY; y++) {
            if (laMatTren(map, x, y)) {
                return true;
            }
        }
        return false;
    }

    private static short timMatNenThapHon(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        int batDauY = Math.max(1, footY + 1);
        for (int y = batDauY; y < map.getHeight(); y++) {
            if (laMatTren(map, x, y)
                    && thanBossThongThoang(map, x, (short) y)) {
                return (short) y;
            }
        }
        return Short.MIN_VALUE;
    }

    private static boolean laMatTren(
            ChickenQuanLyBanDo map,
            short x,
            int y
    ) {
        return y > 0 && y < map.getHeight()
                && map.coVaCham(x, (short) y)
                && !map.coVaCham(x, (short) (y - 1));
    }

    public static boolean thanBossThongThoang(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        return com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                .thanBossThongThoang(map, x, footY);
    }

    public static boolean daRoiKhoiMap(
            ChickenChienBinh camTu,
            ChickenQuanLyBanDo map
    ) {
        return com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                .daRoiKhoiMap(camTu, map);
    }

    public static int layHuongX(
            ChickenChienBinh camTu,
            ChickenChienBinh mucTieu
    ) {
        return com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                .layHuongX(camTu, mucTieu);
    }

    public static boolean daCanTrucX(
            ChickenChienBinh camTu,
            ChickenChienBinh mucTieu
    ) {
        return com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                .daCanTrucX(camTu, mucTieu);
    }

    public static boolean laDiemDenClientCoTheKetThuc(
            ChickenChienBinh camTu,
            ChickenQuanLyBanDo map,
            short x,
            short y
    ) {
        return com.chicken.phong.boss.trandau.haitoathap.BossCamTu
                .laDiemDenClientCoTheKetThuc(camTu, map, x, y);
    }
}
