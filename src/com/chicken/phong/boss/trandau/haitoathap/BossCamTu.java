package com.chicken.phong.boss.trandau.haitoathap;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chiso.ChickenKichThuocNhanVat;
import com.chicken.luyentap.ChickenLuyenTapToaDo;

/** Logic riêng của tám boss Cảm tử. */
public final class BossCamTu {
    /** Log client goc map 51 cho thay moi luot Cam tu tien dung 36 px. */
    public static final int QUANG_DUONG_MOI_LUOT = 36;
    /** Mot luot chi gui mot dich di chuyen day du 36 px cho client. */
    public static final int BUOC_DI_CHUYEN = QUANG_DUONG_MOI_LUOT;
    /**
     * Nen duoi cung map 51 trai tu X=103 den X=1025. Tru nua rong hitbox
     * 12 px, tam Cam tu chi duoc di trong [115, 1013] de khong lao xuong
     * bien khi muc tieu dung hoac bay ra ngoai ria.
     */
    public static final int MEP_TRAI_AN_TOAN_MAP_51 = 115;
    public static final int MEP_PHAI_AN_TOAN_MAP_51 = 1013;
    /**
     * CMD -64 cho CPlayer chay 36 px mat khoang 300 ms. Server cho du 650 ms
     * de client native hoan tat noi suy va tu mo khoa luong nhan packet truoc
     * khi phat hanh dong tiep theo.
     */
    public static final int TRE_MOI_BUOC_MS = 650;
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
     * Kiem tra pham vi no bang toa do server. Client khong duoc gui hay chon
     * muc tieu nam trong vu no.
     */
    public static boolean trongPhamViKichNo(
            ChickenChienBinh camTu,
            ChickenChienBinh nguoiChoi,
            int banKinh
    ) {
        if (camTu == null || nguoiChoi == null || banKinh < 0) {
            return false;
        }
        return khoangCachDenHitboxNguoiChoi(camTu, nguoiChoi) <= banKinh;
    }

    /**
     * Damage giam tuyen tinh tu 100% tai hitbox toi muc toi thieu o ria no.
     */
    public static int tinhSatThuongNoTheoKhoangCach(
            ChickenChienBinh camTu,
            ChickenChienBinh nguoiChoi,
            int satThuongToiDa,
            int banKinh,
            int phanTramTaiRia
    ) {
        if (camTu == null || nguoiChoi == null || satThuongToiDa <= 0
                || banKinh < 0 || phanTramTaiRia < 0
                || phanTramTaiRia > 100) {
            return 0;
        }
        double khoangCach = khoangCachDenHitboxNguoiChoi(
                camTu, nguoiChoi);
        if (khoangCach > banKinh) {
            return 0;
        }
        if (banKinh == 0 || khoangCach <= 0.0D) {
            return satThuongToiDa;
        }
        double tiLeConLai = (banKinh - khoangCach) / banKinh;
        double phanTram = phanTramTaiRia
                + (100 - phanTramTaiRia) * tiLeConLai;
        return Math.max(1, (int) Math.round(
                satThuongToiDa * phanTram / 100.0D));
    }

    private static double khoangCachDenHitboxNguoiChoi(
            ChickenChienBinh camTu,
            ChickenChienBinh nguoiChoi
    ) {
        int trai = nguoiChoi.x
                - ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
        int phai = nguoiChoi.x
                + ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
        int tren = nguoiChoi.y
                - ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_TREN;
        int duoi = nguoiChoi.y
                - ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_DUOI;
        long dx = camTu.x < trai
                ? (long) trai - camTu.x
                : camTu.x > phai ? (long) camTu.x - phai : 0L;
        long dy = camTu.y < tren
                ? (long) tren - camTu.y
                : camTu.y > duoi ? (long) camTu.y - duoi : 0L;
        return Math.hypot(dx, dy);
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

        boolean dangCoNen = coDiemTuaTrenMatNen(
                map, camTu.x, camTu.y)
                && thanBossThongThoang(map, camTu.x, camTu.y);
        int gioiHanTrai = Math.max(
                ChickenKichThuocNhanVat.BOSS_NUA_RONG,
                MEP_TRAI_AN_TOAN_MAP_51);
        int gioiHanPhai = Math.min(
                map.getWidth() - 1 - ChickenKichThuocNhanVat.BOSS_NUA_RONG,
                MEP_PHAI_AN_TOAN_MAP_51);
        if (gioiHanTrai > gioiHanPhai) {
            gioiHanTrai = ChickenKichThuocNhanVat.BOSS_NUA_RONG;
            gioiHanPhai = map.getWidth() - 1
                    - ChickenKichThuocNhanVat.BOSS_NUA_RONG;
        }
        if (!dangCoNen && (camTu.x <= gioiHanTrai
                || camTu.x >= gioiHanPhai)) {
            // O hai ria ngoai, giu nguyen diem dung ke ca khi pixel nen bi
            // pha. Khong gui mot packet chi doi Y lam client lat huong sprite.
            return new short[]{camTu.x, camTu.y};
        }
        int khoangX = Math.abs(mucTieu.x - camTu.x);
        int buocNgang = dangCoNen
                ? Math.min(
                        Math.min(BUOC_DI_CHUYEN, quangDuongConLai),
                        khoangX)
                : 0;
        // Khi đang rơi chỉ hạ theo trọng lực, không chạy ngang giữa không trung.
        int xDuKien = camTu.x + huongX * buocNgang;
        xDuKien = Math.max(gioiHanTrai, Math.min(gioiHanPhai, xDuKien));

        // Quét từng pixel ngang để không xuyên qua tường hoặc khối map mỏng.
        short[] toaDoSauBuocNgang = timBuocNgangAnToan(
                map, camTu.x, xDuKien, camTu.y);
        int xMoi = toaDoSauBuocNgang[0];
        int yMoi = toaDoSauBuocNgang[1];

        if (coDiemTuaTrenMatNen(map, (short) xMoi, (short) yMoi)
                && thanBossThongThoang(map, (short) xMoi, (short) yMoi)) {
            return chotDiemDungChoClient(
                    camTu,
                    new short[]{(short) xMoi, (short) yMoi},
                    map);
        }

        short nenGan = timNenOnDinhGan(map, (short) xMoi, (short) yMoi);
        if (nenGan != Short.MIN_VALUE && nenGan >= yMoi) {
            yMoi = nenGan;
        } else {
            // Không tìm thấy nền gần: rơi đúng một bước, không được bật lên.
            yMoi = Math.min(map.getHeight() + 32, yMoi + BUOC_TRONG_LUC);
        }

        return chotDiemDungChoClient(
                camTu,
                new short[]{(short) xMoi, (short) yMoi},
                map);
    }

    private static final int BUOC_TRONG_LUC = 12;
    private static final int DO_ROI_GAN_TOI_DA = 12;
    /**
     * Map 51 co hai tile mai doc 45 do (121/124). Moi pixel di ngang chi duoc
     * thay doi do cao mot khoang rat nho: du de buoc theo mai, nhung khong the
     * leo qua mat ben cua mot khoi tuong dung.
     */
    private static final int DO_BUOC_DOC_MOI_PIXEL_TOI_DA = 2;
    /** Chenh cao toi da giua tam than va ban chan tren mai doc 45 do. */
    private static final int DO_TIM_NEN_DUOI_CHAN = 8;
    private static final int LECH_BAN_CHAN_X = 8;
    private static final int BAN_KINH_BAN_CHAN_X = 2;
    /** Client Java ME lay mau dia hinh tai hai chan x +/- 6. */
    private static final int LECH_CHAN_CLIENT_X = 6;
    private static final int BAN_KINH_MAU_CHAN_CLIENT_X = 1;
    /** Du cho mai doc 45 do cua map 51 giua tam va hai chan. */
    private static final int LECH_Y_CHAN_CLIENT_TOI_DA = 16;
    /** Tim diem dung gan raw endpoint, nhung khong vuot quota 36 px. */
    private static final int TIM_X_DIEM_DUNG_TOI_DA = 16;
    /**
     * Client goc neo Y theo tam chan. Phan 10 px duoi cua hinh chu nhat boss
     * la vung chan co the chong len mat doc; neu tinh no nhu than ran, boss se
     * bi nang som 9-10 px khi leo mai map 51.
     */
    private static final int VUNG_CHAN_BAM_DOC = 10;

    private static short[] timBuocNgangAnToan(
            ChickenQuanLyBanDo map,
            int xHienTai,
            int xDuKien,
            int footY
    ) {
        if (xDuKien == xHienTai) {
            return new short[]{(short) xHienTai, (short) footY};
        }
        int huong = xDuKien > xHienTai ? 1 : -1;
        int xAnToan = xHienTai;
        int yAnToan = footY;
        for (int x = xHienTai + huong;
                huong > 0 ? x <= xDuKien : x >= xDuKien;
                x += huong) {
            short yTiepTheo = timYChoBuocNgang(
                    map, (short) x, (short) yAnToan);
            if (yTiepTheo == Short.MIN_VALUE) {
                break;
            }
            xAnToan = x;
            yAnToan = yTiepTheo;
        }
        return new short[]{(short) xAnToan, (short) yAnToan};
    }

    /**
     * Chon Y thap nhat van hop le trong mot buoc ngang. Quet ca len va xuong
     * giup boss bam sat hai nua cua mai tam giac, thay vi bi treo tren dinh roi
     * roi tung cuc. Moi vi tri bat buoc co than trong va mot ban chan tua vao
     * dung mep tren cua pixel dia hinh.
     */
    private static short timYChoBuocNgang(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        int yThapNhat = footY + DO_BUOC_DOC_MOI_PIXEL_TOI_DA;
        int yCaoNhat = footY - DO_BUOC_DOC_MOI_PIXEL_TOI_DA;
        // Client goc neo truc Y vao mep nen ngay duoi tam chan. Uu tien diem
        // nay truoc, neu khong chan truoc se nang boss som tren mai doc.
        for (int y = yThapNhat; y >= yCaoNhat; y--) {
            short yKiemTra = (short) y;
            if (laMatNenTaiTam(map, x, yKiemTra)
                    && thanBossThongThoang(map, x, yKiemTra)) {
                return yKiemTra;
            }
        }
        // O mep tile/lo nho co the khong co pixel ngay duoi tam; luc nay moi
        // cho mot trong hai chan lam diem tua de boss khong roi oan.
        for (int y = yThapNhat; y >= yCaoNhat; y--) {
            short yKiemTra = (short) y;
            if (thanBossThongThoang(map, x, yKiemTra)
                    && coDiemTuaTrenMatNen(map, x, yKiemTra)) {
                return yKiemTra;
            }
        }
        return Short.MIN_VALUE;
    }

    private static boolean laMatNenTaiTam(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        return footY > 0
                && footY < map.getHeight()
                && map.coVaCham(x, footY)
                && !map.coVaCham(x, (short) (footY - 1));
    }

    private static boolean coDiemTuaTrenMatNen(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        if (ChickenLuyenTapToaDo.coNenDoDuoiHaiChan(map, x, footY)) {
            return true;
        }
        return coMatNenTaiBanChan(
                map, x - LECH_BAN_CHAN_X, footY)
                || coMatNenTaiBanChan(
                        map, x + LECH_BAN_CHAN_X, footY);
    }

    private static boolean coMatNenTaiBanChan(
            ChickenQuanLyBanDo map,
            int tamChanX,
            int footY
    ) {
        int batDauX = Math.max(0, tamChanX - BAN_KINH_BAN_CHAN_X);
        int ketThucX = Math.min(
                map.getWidth() - 1,
                tamChanX + BAN_KINH_BAN_CHAN_X);
        int batDauY = Math.max(1, footY);
        int ketThucY = Math.min(
                map.getHeight() - 1,
                footY + DO_TIM_NEN_DUOI_CHAN);
        for (int py = batDauY; py <= ketThucY; py++) {
            for (int px = batDauX; px <= ketThucX; px++) {
                if (map.coVaCham((short) px, (short) py)
                        && !map.coVaCham((short) px, (short) (py - 1))) {
                    return true;
                }
            }
        }
        return false;
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

    /**
     * Chot waypoint ma vat ly CPlayer native co the dung on dinh.
     *
     * Client khong chi kiem tra tam nhan vat; no lay mau hai chan x +/- 6.
     * Neu server chon mot pixel o mep buc, client se roi tiep trong khi server
     * da luu Y cu. Luot sau CMD -64 keo nguoc client va tao vong xoay. Ham nay
     * bat buoc tam va ca hai chan co nen, neu can se dich vao trong buc hoac
     * chon mat nen thap hon gan nhat.
     */
    public static short[] chotDiemDungChoClient(
            ChickenChienBinh camTu,
            short[] diemDuKien,
            ChickenQuanLyBanDo map
    ) {
        if (camTu == null || diemDuKien == null || diemDuKien.length < 2
                || map == null) {
            return diemDuKien;
        }
        short xDuKien = canBangChanLeTheoBuocClient(
                camTu.x, diemDuKien[0]);
        short yDuKien = diemDuKien[1];
        if (laDiemDenClientCoTheKetThuc(
                camTu, map, xDuKien, yDuKien)) {
            return new short[]{xDuKien, yDuKien};
        }

        int huong = Integer.signum(xDuKien - camTu.x);
        short[] diemGan = timDiemDungGanCaoDo(
                camTu, map, xDuKien, yDuKien, huong);
        if (diemGan != null) {
            return diemGan;
        }

        short[] diemThapHon = timDiemDungThapHon(
                camTu, map, xDuKien, yDuKien, huong);
        if (diemThapHon != null) {
            return diemThapHon;
        }
        if (laDiemDungOnDinhChoClient(map, camTu.x, camTu.y)) {
            return new short[]{camTu.x, camTu.y};
        }
        /*
         * Khong bao gio tra ve mot dich ma CPlayer native khong the ket thuc.
         * CMD -64 khoa luong doc packet cho toi khi client cham dung dich; mot
         * waypoint ao se lam ca tran dung vo han. Giu nguyen toa do server de
         * caller bo qua packet di chuyen trong truong hop khong tim duoc diem
         * dung hop le.
         */
        return new short[]{camTu.x, camTu.y};
    }

    /**
     * CPlayer native tien moi truc theo buoc 2 px. Neu server gui dich le
     * parity, vi du 980 -> 999, client chi co the lap 998 <-> 1000 va lat
     * sprite vo han. Ngoai nen/hitbox, toa do dich X bat buoc cung chan-le
     * voi toa do bat dau. Truc Y van do vat ly dia hinh cua client neo lai,
     * nen ep parity Y se lam Cam tu khong leo duoc mai doc 45 do.
     */
    public static boolean laDiemDenClientCoTheKetThuc(
            ChickenChienBinh camTu,
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        return camTu != null
                && ((x - camTu.x) & 1) == 0
                && laDiemDungOnDinhChoClient(map, x, footY);
    }

    private static short canBangChanLeTheoBuocClient(
            short hienTai,
            short duKien
    ) {
        int khoang = duKien - hienTai;
        if ((khoang & 1) == 0) {
            return duKien;
        }
        return (short) (duKien - Integer.signum(khoang));
    }

    public static boolean laDiemDungOnDinhChoClient(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        if (map == null
                || !laMatNenTaiTam(map, x, footY)
                || !thanBossThongThoang(map, x, footY)) {
            return false;
        }
        return coNenGanChanClient(
                map, x - LECH_CHAN_CLIENT_X, footY)
                && coNenGanChanClient(
                        map, x + LECH_CHAN_CLIENT_X, footY);
    }

    private static short[] timDiemDungGanCaoDo(
            ChickenChienBinh camTu,
            ChickenQuanLyBanDo map,
            short xDuKien,
            short yDuKien,
            int huong
    ) {
        short[] totNhat = null;
        int diemTotNhat = Integer.MAX_VALUE;
        for (int lechX = 0; lechX <= TIM_X_DIEM_DUNG_TOI_DA; lechX++) {
            int soNhanh = lechX == 0 || huong != 0 ? 1 : 2;
            for (int nhanh = 0; nhanh < soNhanh; nhanh++) {
                int dau = huong != 0
                        ? huong
                        : (nhanh == 0 ? 1 : -1);
                int x = xDuKien + dau * lechX;
                if (!namTrongQuotaVaBanDo(camTu, map, x)) {
                    continue;
                }
                short y = timMatDungGanCaoDo(
                        camTu, map, (short) x, yDuKien);
                if (y == Short.MIN_VALUE) {
                    continue;
                }
                int diem = Math.abs(y - yDuKien) * 100 + lechX;
                if (diem < diemTotNhat) {
                    totNhat = new short[]{(short) x, y};
                    diemTotNhat = diem;
                }
            }
        }
        return totNhat;
    }

    private static short[] timDiemDungThapHon(
            ChickenChienBinh camTu,
            ChickenQuanLyBanDo map,
            short xDuKien,
            short yDuKien,
            int huong
    ) {
        int batDauY = Math.max(1, yDuKien + LECH_Y_CHAN_CLIENT_TOI_DA + 1);
        for (int y = batDauY; y < map.getHeight(); y++) {
            for (int lechX = 0; lechX <= TIM_X_DIEM_DUNG_TOI_DA; lechX++) {
                int soNhanh = lechX == 0 || huong != 0 ? 1 : 2;
                for (int nhanh = 0; nhanh < soNhanh; nhanh++) {
                    int dau = huong != 0
                            ? huong
                            : (nhanh == 0 ? 1 : -1);
                    int x = xDuKien + dau * lechX;
                    if (!namTrongQuotaVaBanDo(camTu, map, x)) {
                        continue;
                    }
                    if (laDiemDenClientCoTheKetThuc(
                            camTu, map, (short) x, (short) y)) {
                        return new short[]{(short) x, (short) y};
                    }
                }
            }
        }
        return null;
    }

    private static short timMatDungGanCaoDo(
            ChickenChienBinh camTu,
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        for (int doLech = 0;
                doLech <= LECH_Y_CHAN_CLIENT_TOI_DA;
                doLech++) {
            int yDuoi = footY + doLech;
            if (yDuoi < map.getHeight()
                    && laDiemDenClientCoTheKetThuc(
                            camTu, map, x, (short) yDuoi)) {
                return (short) yDuoi;
            }
            int yTren = footY - doLech;
            if (doLech > 0 && yTren > 0
                    && laDiemDenClientCoTheKetThuc(
                            camTu, map, x, (short) yTren)) {
                return (short) yTren;
            }
        }
        return Short.MIN_VALUE;
    }

    private static boolean namTrongQuotaVaBanDo(
            ChickenChienBinh camTu,
            ChickenQuanLyBanDo map,
            int x
    ) {
        return x >= ChickenKichThuocNhanVat.BOSS_NUA_RONG
                && x <= map.getWidth() - 1
                        - ChickenKichThuocNhanVat.BOSS_NUA_RONG
                && Math.abs(x - camTu.x) <= QUANG_DUONG_MOI_LUOT;
    }

    private static boolean coNenGanChanClient(
            ChickenQuanLyBanDo map,
            int tamChanX,
            int footY
    ) {
        int batDauX = Math.max(
                0, tamChanX - BAN_KINH_MAU_CHAN_CLIENT_X);
        int ketThucX = Math.min(
                map.getWidth() - 1,
                tamChanX + BAN_KINH_MAU_CHAN_CLIENT_X);
        int batDauY = Math.max(1, footY - LECH_Y_CHAN_CLIENT_TOI_DA);
        int ketThucY = Math.min(
                map.getHeight() - 1,
                footY + LECH_Y_CHAN_CLIENT_TOI_DA);
        for (int y = batDauY; y <= ketThucY; y++) {
            for (int x = batDauX; x <= ketThucX; x++) {
                if (laMatNenTaiTam(
                        map, (short) x, (short) y)) {
                    return true;
                }
            }
        }
        return false;
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
        int duoi = footY
                - ChickenKichThuocNhanVat.BOSS_LECH_DUOI
                - 1
                - VUNG_CHAN_BAM_DOC;
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

    /**
     * Khi Cảm tử đã nằm trên cùng trục X với mục tiêu nhưng bị
     * ngăn cách theo Y, nó phải đứng chờ thay vì gửi CMD -64 chỉ
     * thay đổi Y. Client gốc coi X đích bằng X hiện tại là hướng
     * trái, làm sprite đổi hướng qua lại giữa các lượt.
     */
    public static boolean daCanTrucX(
            ChickenChienBinh camTu,
            ChickenChienBinh mucTieu
    ) {
        return camTu != null && mucTieu != null
                && Math.abs(mucTieu.x - camTu.x) <= NGUONG_DOI_HUONG_X;
    }
}
