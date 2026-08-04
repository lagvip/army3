package com.chicken.chien;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mot phat item tao dan do server mo phong. Client chi ve bulletType va
 * cac diem duong bay server tra ve.
 */
public final class ChickenPhatBanVatPhamServer {
    private static final int ID_DICH_CHUYEN_TUC_THOI = 221;
    private static final int ID_BOM_PHA_DAT = 226;
    private static final int ID_BOM_B52 = 228;
    private static final int ID_DAN_TRAI_PHA = 231;
    private static final int ID_DAN_LAZER = 235;
    private static final int ID_CHUOT_GAN_BOM = 237;
    private static final int ID_TEN_LUA_X4 = 238;
    private static final int ID_DAN_XUYEN_DAT = 239;
    private static final int BOM_PHA_DAT_KHOANG_CACH_NO = 36;
    private static final int BOM_PHA_DAT_SO_DIEM_MOI_CHANG = 8;
    private static final int B52_LECH_TRAI_THA_BOM = 165;
    private static final int B52_DO_CAO_THA_BOM = 320;
    private static final int B52_SO_DIEM_ROI = 25;
    private static final int TRAI_PHA_DO_CAO_ROI = 500;
    private static final int TRAI_PHA_SO_DIEM_ROI = 32;
    private static final int[] TRAI_PHA_LECH_X = {
        -75, -45, -15, 15, 45, 75
    };
    private static final int TEN_LUA_X4_SO_TEN_LUA = 4;
    private static final int TEN_LUA_X4_SO_DIEM = 25;
    private static final int TEN_LUA_X4_SO_DOAN_CONG = 4;
    private static final int TEN_LUA_X4_SO_DIEM_MOI_DOAN = 6;

    private ChickenPhatBanVatPhamServer() {
    }

    public static ChickenKetQuaDan tao(
            ChickenChienBinh nguoiBan,
            short dauNongX,
            short dauNongY,
            short goc,
            byte luc,
            ChickenCongThucVatPhamChien.CauHinh cauHinh,
            byte windX,
            byte windY,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc
    ) {
        if (nguoiBan == null || cauHinh == null || banDo == null
                || boLoc == null) {
            return null;
        }
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(
                        cauHinh.getIdVatPham());
        if (hoSo == null) {
            return null;
        }
        if (cauHinh.getIdVatPham() == ID_BOM_PHA_DAT) {
            return taoBomPhaDat(
                    nguoiBan, dauNongX, dauNongY, goc, luc, cauHinh,
                    windX, windY, banDo, cacMucTieu, boLoc, hoSo);
        }
        if (cauHinh.getIdVatPham() == ID_DAN_TRAI_PHA) {
            return taoDanTraiPha(
                    nguoiBan, dauNongX, dauNongY, goc, luc, cauHinh,
                    windX, windY, banDo, cacMucTieu, boLoc, hoSo);
        }
        if (cauHinh.getIdVatPham() == ID_TEN_LUA_X4) {
            return taoTenLuaX4(
                    nguoiBan, dauNongX, dauNongY, goc, luc, cauHinh,
                    windX, windY, banDo, cacMucTieu, boLoc, hoSo);
        }

        ChickenQuanLyCongThucSung.KetQuaQuyDao quyDao =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        dauNongX,
                        dauNongY,
                        goc,
                        luc,
                        cauHinh,
                        windX,
                        windY,
                        banDo
                );
        ChickenQuanLyCongThucSung.KetQuaQuyDao quyDaoKhongDiaHinh =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        dauNongX,
                        dauNongY,
                        goc,
                        luc,
                        cauHinh,
                        windX,
                        windY,
                        banDoKhongVaCham(banDo)
                );
        short[] xs = banSao(quyDao.getHienThiX());
        short[] ys = banSao(quyDao.getHienThiY());
        boolean dichChuyenCoDiemDen =
                cauHinh.getIdVatPham() != ID_DICH_CHUYEN_TUC_THOI
                || diemCuoiNamTrenDiaHinh(xs, ys, banDo);
        datDiemDau(xs, ys, dauNongX, dauNongY);

        if (cauHinh.getIdVatPham() == ID_DICH_CHUYEN_TUC_THOI) {
            if (!dichChuyenCoDiemDen) {
                // Quy dao da thoat map/het buoc ma khong cham dia hinh.
                // Type 5 se dat shooter tai diem cuoi, nen gui chinh toa do
                // cu de client khong roi ra ngoai map va bi xu chet.
                xs = new short[]{nguoiBan.x};
                ys = new short[]{nguoiBan.y};
            }
        }

        // Type 5 cho phep dich chuyen chong len nhan vat. Hitbox nhan vat
        // khong duoc cat hay be nguoc diem cuoi duong dan nay.
        VaChamNhanVat vaCham = cauHinh.getIdVatPham()
                == ID_DICH_CHUYEN_TUC_THOI
                        ? null : timVaChamDauTien(
                                nguoiBan, dauNongX, dauNongY,
                                xs, ys, cacMucTieu, boLoc, cauHinh);
        if (vaCham != null) {
            xs = Arrays.copyOf(xs, vaCham.chiSoDiem + 1);
            ys = Arrays.copyOf(ys, vaCham.chiSoDiem + 1);
            xs[xs.length - 1] = vaCham.x;
            ys[ys.length - 1] = vaCham.y;
        }

        int soDiem = Math.min(xs.length, ys.length);
        int xNo = soDiem == 0 ? dauNongX : xs[soDiem - 1];
        int yNo = soDiem == 0 ? dauNongY : ys[soDiem - 1];
        boolean coDiemNo = vaCham != null
                || (xNo >= 0 && xNo < banDo.getWidth()
                && yNo >= 0 && yNo < banDo.getHeight()
                && banDo.coVaCham((short) xNo, (short) yNo));
        if (cauHinh.getIdVatPham() == ID_DAN_XUYEN_DAT) {
            // Terrain chi bi dao tren duong di; type 25 chi no va gay damage
            // khi duong dan cham mot hitbox hop le.
            coDiemNo = vaCham != null;
        }
        if (cauHinh.getIdVatPham() == ID_CHUOT_GAN_BOM
                && soDiem > 0
                && xNo >= 0 && xNo < banDo.getWidth()
                && yNo >= 0 && yNo < banDo.getHeight()) {
            // Bullet 22 native luon no khi het path, ke ca khong cham vat.
            // Server cung chot diem no tai diem cuoi de hinh va damage khop.
            coDiemNo = true;
        }
        short[][] cacDuongX = new short[][]{xs};
        short[][] cacDuongY = new short[][]{ys};

        /*
         * Bullet type 4 cua client la mot chuoi hai giai doan:
         * 1) vien danh dau bay den diem goi may bay;
         * 2) khi may bay toi noi, BM tao bullet type 3 tu duong thu hai.
         * Neu server chi gui mot duong, createBullet(3) doc xPaint[1] va
         * nem IndexOutOfRangeException moi frame, lam client treo vinh vien.
         * Damage B52 phai lay diem roi cua qua bom, khong lay vien danh dau.
         */
        if (cauHinh.getIdVatPham() == ID_BOM_B52) {
            DuongRoi duongBom = taoDuongBomB52(
                    (short) xNo,
                    (short) yNo,
                    nguoiBan,
                    cacMucTieu,
                    boLoc,
                    banDo
            );
            cacDuongX = new short[][]{xs, duongBom.xs};
            cacDuongY = new short[][]{ys, duongBom.ys};
            int soDiemBom = Math.min(
                    duongBom.xs.length, duongBom.ys.length);
            xNo = soDiemBom == 0 ? xNo : duongBom.xs[soDiemBom - 1];
            yNo = soDiemBom == 0 ? yNo : duongBom.ys[soDiemBom - 1];
            coDiemNo = duongBom.coVaCham
                    || (xNo >= 0 && xNo < banDo.getWidth()
                    && yNo >= 0 && yNo < banDo.getHeight()
                    && banDo.coVaCham((short) xNo, (short) yNo));
        }

        /*
         * Bullet type 14 cua client la laser hai giai doan: duong 0 la vien
         * danh dau, sau do BM tao bullet type 15 tu xPaint[1]. Duong thu hai
         * chi can mot diem no authoritative; client tu ve tia laser doc tai
         * diem nay. Thieu path 1 se lam client doc ngoai mang va treo.
         */
        if (cauHinh.getIdVatPham() == ID_DAN_LAZER) {
            cacDuongX = new short[][]{
                xs, new short[]{(short) xNo}
            };
            cacDuongY = new short[][]{
                ys, new short[]{(short) yNo}
            };
        }

        Map<ChickenChienBinh, Integer> damage =
                new LinkedHashMap<ChickenChienBinh, Integer>();
        boolean sieuCao = false;
        if (hoSo.coSatThuongNo() && coDiemNo && cacMucTieu != null) {
            Set<ChickenChienBinh> mucTieuSieuCao =
                    timTatCaMucTieuTrung(
                            nguoiBan,
                            dauNongX,
                            dauNongY,
                            quyDaoKhongDiaHinh.getHienThiX(),
                            quyDaoKhongDiaHinh.getHienThiY(),
                            cacMucTieu,
                            boLoc
                    );
            for (ChickenChienBinh mucTieu : cacMucTieu) {
                if (!hopLe(nguoiBan, mucTieu, boLoc)) {
                    continue;
                }
                long tanCongNhan = (long) Math.max(1, nguoiBan.tanCong)
                        * hoSo.getPhanTramTanCong();
                int tanCong = (int) Math.min(
                        Integer.MAX_VALUE,
                        (tanCongNhan + 50L) / 100L
                );
                int satThuongGoc = Math.max(1, tanCong - mucTieu.giap);
                if (mucTieuSieuCao.contains(mucTieu)) {
                    satThuongGoc = ChickenSieuCao.tangSatThuong(
                            satThuongGoc);
                }
                int satThuong = tinhSatThuongNoChoMucTieu(
                        hoSo.getHoSoNo(),
                        satThuongGoc,
                        xNo,
                        yNo,
                        mucTieu,
                        boLoc,
                        banDo
                );
                if (satThuong > 0) {
                    damage.put(mucTieu, satThuong);
                    if (mucTieuSieuCao.contains(mucTieu)) {
                        sieuCao = true;
                    }
                }
            }
        }
        return new ChickenKetQuaDan(
                cauHinh.getLoaiDan(),
                dauNongX,
                dauNongY,
                goc,
                luc,
                luc,
                cacDuongX,
                cacDuongY,
                damage,
                sieuCao
        );
    }

    /**
     * Client type 6 bat buoc khoi tao ba Bullet cung luc. De nguoi choi chi
     * thay mot vien khoan lien tuc, ba path chong khit cung mot tien to:
     * path 0 no o va cham dau, path 1 di tiep thang va no, path 2 lai di tiep
     * va no. Neu tien to dau cham nhan vat, hai path sau thoat map va khong no.
     */
    private static ChickenKetQuaDan taoBomPhaDat(
            ChickenChienBinh nguoiBan,
            short dauNongX,
            short dauNongY,
            short goc,
            byte luc,
            ChickenCongThucVatPhamChien.CauHinh cauHinh,
            byte windX,
            byte windY,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc,
            ChickenCauHinhSatThuongVatPham.HoSo hoSo
    ) {
        ChickenQuanLyCongThucSung.KetQuaQuyDao quyDao =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        dauNongX, dauNongY, goc, luc, cauHinh,
                        windX, windY, banDo);
        short[] duongDauX = banSao(quyDao.getHienThiX());
        short[] duongDauY = banSao(quyDao.getHienThiY());
        if (duongDauX.length == 0 || duongDauY.length == 0) {
            duongDauX = new short[]{dauNongX};
            duongDauY = new short[]{dauNongY};
        }
        datDiemDau(duongDauX, duongDauY, dauNongX, dauNongY);

        VaChamNhanVat vaChamDau = timVaChamDauTien(
                nguoiBan, dauNongX, dauNongY,
                duongDauX, duongDauY, cacMucTieu, boLoc, cauHinh);
        if (vaChamDau != null) {
            duongDauX = Arrays.copyOf(
                    duongDauX, vaChamDau.chiSoDiem + 1);
            duongDauY = Arrays.copyOf(
                    duongDauY, vaChamDau.chiSoDiem + 1);
            duongDauX[duongDauX.length - 1] = vaChamDau.x;
            duongDauY[duongDauY.length - 1] = vaChamDau.y;
        }

        short[][] cacDuongX = new short[3][];
        short[][] cacDuongY = new short[3][];
        boolean[] coNo = new boolean[3];
        cacDuongX[0] = duongDauX;
        cacDuongY[0] = duongDauY;
        boolean chamDiaHinh =
                diemCuoiNamTrenDiaHinh(duongDauX, duongDauY, banDo);
        boolean diaHinhCoThePha = chamDiaHinh
                && banDo.coThePhaDiaHinh(
                        duongDauX[duongDauX.length - 1],
                        duongDauY[duongDauY.length - 1]);
        coNo[0] = vaChamDau != null || chamDiaHinh;

        if (vaChamDau != null || !diaHinhCoThePha) {
            short[] thoatX = themDiemThoatMapX(duongDauX);
            short[] thoatY = themDiemThoatMapY(duongDauY, banDo);
            cacDuongX[1] = thoatX;
            cacDuongY[1] = thoatY;
            cacDuongX[2] = thoatX.clone();
            cacDuongY[2] = thoatY.clone();
        } else {
            HuongThang huong = layHuongCuoi(
                    duongDauX, duongDauY, goc);
            ChangKhoan changHai = taoChangKhoan(
                    duongDauX, duongDauY, huong,
                    nguoiBan, cacMucTieu, boLoc, banDo);
            cacDuongX[1] = changHai.xs;
            cacDuongY[1] = changHai.ys;
            coNo[1] = changHai.coNo;
            if (changHai.chamNhanVat || !changHai.coNo) {
                cacDuongX[2] = themDiemThoatMapX(changHai.xs);
                cacDuongY[2] = themDiemThoatMapY(changHai.ys, banDo);
            } else {
                ChangKhoan changBa = taoChangKhoan(
                        changHai.xs, changHai.ys, huong,
                        nguoiBan, cacMucTieu, boLoc, banDo);
                cacDuongX[2] = changBa.xs;
                cacDuongY[2] = changBa.ys;
                coNo[2] = changBa.coNo;
            }
        }
        Map<ChickenChienBinh, Integer> damage =
                new LinkedHashMap<ChickenChienBinh, Integer>();
        if (cacMucTieu != null && hoSo.coSatThuongNo()) {
            long tanCongNhan = (long) Math.max(1, nguoiBan.tanCong)
                    * hoSo.getPhanTramTanCong();
            int tanCong = (int) Math.min(
                    Integer.MAX_VALUE, (tanCongNhan + 50L) / 100L);
            for (ChickenChienBinh mucTieu : cacMucTieu) {
                if (!hopLe(nguoiBan, mucTieu, boLoc)) {
                    continue;
                }
                int satThuongGoc = Math.max(1, tanCong - mucTieu.giap);
                int lonNhat = 0;
                for (int i = 0; i < 3; i++) {
                    if (!coNo[i]) {
                        continue;
                    }
                    int soDiem = Math.min(
                            cacDuongX[i].length, cacDuongY[i].length);
                    if (soDiem <= 0) {
                        continue;
                    }
                    int xNo = cacDuongX[i][soDiem - 1];
                    int yNo = cacDuongY[i][soDiem - 1];
                    lonNhat = Math.max(lonNhat,
                            tinhSatThuongNoChoMucTieu(
                                    hoSo.getHoSoNo(), satThuongGoc,
                                    xNo, yNo, mucTieu, boLoc, banDo));
                }
                if (lonNhat > 0) {
                    damage.put(mucTieu, lonNhat);
                }
            }
        }
        return new ChickenKetQuaDan(
                cauHinh.getLoaiDan(), dauNongX, dauNongY,
                goc, luc, luc, cacDuongX, cacDuongY,
                damage, false);
    }

    private static ChangKhoan taoChangKhoan(
            short[] tienToX,
            short[] tienToY,
            HuongThang huong,
            ChickenChienBinh nguoiBan,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        int soDiemTienTo = Math.min(tienToX.length, tienToY.length);
        int xDau = tienToX[soDiemTienTo - 1];
        int yDau = tienToY[soDiemTienTo - 1];
        List<Short> xs = new ArrayList<Short>();
        List<Short> ys = new ArrayList<Short>();
        for (int i = 0; i < soDiemTienTo; i++) {
            xs.add(tienToX[i]);
            ys.add(tienToY[i]);
        }

        int xCu = xDau;
        int yCu = yDau;
        for (int i = 1; i <= BOM_PHA_DAT_SO_DIEM_MOI_CHANG; i++) {
            double tiLe = (double) i / BOM_PHA_DAT_SO_DIEM_MOI_CHANG;
            int xMoi = xDau + (int) Math.round(
                    huong.dx * BOM_PHA_DAT_KHOANG_CACH_NO * tiLe);
            int yMoi = yDau + (int) Math.round(
                    huong.dy * BOM_PHA_DAT_KHOANG_CACH_NO * tiLe);
            VaChamDoan vaCham = timVaChamNhanVatTrenDoan(
                    nguoiBan, xCu, yCu, xMoi, yMoi,
                    cacMucTieu, boLoc);
            if (vaCham != null) {
                xs.add((short) vaCham.x);
                ys.add((short) vaCham.y);
                return new ChangKhoan(
                        doiMang(xs), doiMang(ys), true, true);
            }
            xs.add((short) xMoi);
            ys.add((short) yMoi);
            xCu = xMoi;
            yCu = yMoi;
        }
        boolean trongMap = xCu >= 0 && xCu < banDo.getWidth()
                && yCu >= 0 && yCu < banDo.getHeight();
        return new ChangKhoan(
                doiMang(xs), doiMang(ys), trongMap, false);
    }

    private static VaChamDoan timVaChamNhanVatTrenDoan(
            ChickenChienBinh nguoiBan,
            int xDau,
            int yDau,
            int xCuoi,
            int yCuoi,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc
    ) {
        if (cacMucTieu == null) {
            return null;
        }
        int dx = xCuoi - xDau;
        int dy = yCuoi - yDau;
        int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
        for (int buoc = 1; buoc <= soBuoc; buoc++) {
            int x = xDau + (int) Math.round((double) dx * buoc / soBuoc);
            int y = yDau + (int) Math.round((double) dy * buoc / soBuoc);
            for (ChickenChienBinh mucTieu : cacMucTieu) {
                if (hopLe(nguoiBan, mucTieu, boLoc)
                        && boLoc.trungHitbox(mucTieu, x, y)) {
                    return new VaChamDoan(x, y);
                }
            }
        }
        return null;
    }

    private static HuongThang layHuongCuoi(
            short[] xs,
            short[] ys,
            short goc
    ) {
        int soDiem = Math.min(xs.length, ys.length);
        int xCuoi = xs[soDiem - 1];
        int yCuoi = ys[soDiem - 1];
        for (int i = soDiem - 2; i >= 0; i--) {
            int dx = xCuoi - xs[i];
            int dy = yCuoi - ys[i];
            double doDai = Math.hypot(dx, dy);
            if (doDai >= 1.0D) {
                return new HuongThang(dx / doDai, dy / doDai);
            }
        }
        double radian = Math.toRadians(goc);
        return new HuongThang(Math.cos(radian), -Math.sin(radian));
    }

    private static short[] themDiemThoatMapX(short[] xs) {
        short[] ketQua = Arrays.copyOf(xs, xs.length + 1);
        ketQua[ketQua.length - 1] = xs[xs.length - 1];
        return ketQua;
    }

    private static short[] themDiemThoatMapY(
            short[] ys,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        short[] ketQua = Arrays.copyOf(ys, ys.length + 1);
        ketQua[ketQua.length - 1] = (short) Math.min(
                Short.MAX_VALUE, banDo.getHeight() + 101);
        return ketQua;
    }

    /**
     * Type 26 la vien danh dau. Neu no trung hitbox, client thay nOrbit=5
     * se cho no bung thanh bon type 27: cac vien con bat dau tai diem trung,
     * bay vong xuyen dia hinh va quay lai ghim vao cung muc tieu. Neu vien
     * danh dau truot, server chi gui path 0 de client khong tao ten lua con.
     */
    private static ChickenKetQuaDan taoTenLuaX4(
            ChickenChienBinh nguoiBan,
            short dauNongX,
            short dauNongY,
            short goc,
            byte luc,
            ChickenCongThucVatPhamChien.CauHinh cauHinh,
            byte windX,
            byte windY,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc,
            ChickenCauHinhSatThuongVatPham.HoSo hoSo
    ) {
        ChickenQuanLyCongThucSung.KetQuaQuyDao quyDao =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        dauNongX, dauNongY, goc, luc, cauHinh,
                        windX, windY, banDo);
        ChickenQuanLyCongThucSung.KetQuaQuyDao quyDaoKhongDiaHinh =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        dauNongX, dauNongY, goc, luc, cauHinh,
                        windX, windY, banDoKhongVaCham(banDo));
        short[] markerX = banSao(quyDao.getHienThiX());
        short[] markerY = banSao(quyDao.getHienThiY());
        if (markerX.length == 0 || markerY.length == 0) {
            markerX = new short[]{dauNongX};
            markerY = new short[]{dauNongY};
        }
        datDiemDau(markerX, markerY, dauNongX, dauNongY);

        VaChamNhanVat vaChamMarker = timVaChamDauTien(
                nguoiBan, dauNongX, dauNongY,
                markerX, markerY, cacMucTieu, boLoc, cauHinh);
        if (vaChamMarker != null) {
            markerX = Arrays.copyOf(markerX, vaChamMarker.chiSoDiem + 1);
            markerY = Arrays.copyOf(markerY, vaChamMarker.chiSoDiem + 1);
            markerX[markerX.length - 1] = vaChamMarker.x;
            markerY[markerY.length - 1] = vaChamMarker.y;
        }

        if (vaChamMarker == null) {
            return new ChickenKetQuaDan(
                    cauHinh.getLoaiDan(), dauNongX, dauNongY,
                    goc, luc, luc,
                    new short[][]{markerX}, new short[][]{markerY},
                    new LinkedHashMap<ChickenChienBinh, Integer>(), false);
        }

        int diemTrungX = vaChamMarker.x;
        int diemTrungY = vaChamMarker.y;
        short[][] cacDuongX = new short[TEN_LUA_X4_SO_TEN_LUA + 1][];
        short[][] cacDuongY = new short[TEN_LUA_X4_SO_TEN_LUA + 1][];
        cacDuongX[0] = markerX;
        cacDuongY[0] = markerY;
        double phaBungNgauNhien = ThreadLocalRandom.current().nextDouble(
                0.0D, Math.PI * 2.0D);
        for (int i = 0; i < TEN_LUA_X4_SO_TEN_LUA; i++) {
            DuongRoi tenLua = taoDuongTenLuaGhimMucTieu(
                    diemTrungX, diemTrungY, i, phaBungNgauNhien);
            cacDuongX[i + 1] = tenLua.xs;
            cacDuongY[i + 1] = tenLua.ys;
        }

        Set<ChickenChienBinh> mucTieuSieuCao =
                timTatCaMucTieuTrung(
                        nguoiBan, dauNongX, dauNongY,
                        quyDaoKhongDiaHinh.getHienThiX(),
                        quyDaoKhongDiaHinh.getHienThiY(),
                        cacMucTieu, boLoc);
        Map<ChickenChienBinh, Integer> damage =
                new LinkedHashMap<ChickenChienBinh, Integer>();
        boolean sieuCao = false;
        if (cacMucTieu != null && hoSo.coSatThuongNo()) {
            for (ChickenChienBinh mucTieu : cacMucTieu) {
                if (!hopLe(nguoiBan, mucTieu, boLoc)) {
                    continue;
                }
                long tanCongNhan = (long) Math.max(1, nguoiBan.tanCong)
                        * hoSo.getPhanTramTanCong();
                int tanCongTong = (int) Math.min(
                        Integer.MAX_VALUE, (tanCongNhan + 50L) / 100L);
                int satThuongTong = Math.max(1, tanCongTong - mucTieu.giap);
                boolean laSieuCao = mucTieuSieuCao.contains(mucTieu);
                if (laSieuCao) {
                    satThuongTong = ChickenSieuCao.tangSatThuong(
                            satThuongTong);
                }
                int satThuongDaNhan = 0;
                for (int i = 0; i < TEN_LUA_X4_SO_TEN_LUA; i++) {
                    int soDiem = Math.min(
                            cacDuongX[i + 1].length,
                            cacDuongY[i + 1].length);
                    if (soDiem <= 0) {
                        continue;
                    }
                    int satThuongVien = satThuongTong
                            / TEN_LUA_X4_SO_TEN_LUA;
                    if (i < satThuongTong % TEN_LUA_X4_SO_TEN_LUA) {
                        satThuongVien++;
                    }
                    int satThuongNo = tinhSatThuongNoChoMucTieu(
                            hoSo.getHoSoNo(), satThuongVien,
                            cacDuongX[i + 1][soDiem - 1],
                            cacDuongY[i + 1][soDiem - 1],
                            mucTieu, boLoc, banDo);
                    long tongMoi = (long) satThuongDaNhan + satThuongNo;
                    satThuongDaNhan = tongMoi > Integer.MAX_VALUE
                            ? Integer.MAX_VALUE : (int) tongMoi;
                }
                if (satThuongDaNhan > 0) {
                    damage.put(mucTieu, satThuongDaNhan);
                    sieuCao |= laSieuCao;
                }
            }
        }
        return new ChickenKetQuaDan(
                cauHinh.getLoaiDan(), dauNongX, dauNongY,
                goc, luc, luc, cacDuongX, cacDuongY,
                damage, sieuCao);
    }

    private static DuongRoi taoDuongTenLuaGhimMucTieu(
            int diemTrungX,
            int diemTrungY,
            int chiSoTenLua,
            double phaBungNgauNhien
    ) {
        ThreadLocalRandom ngauNhien = ThreadLocalRandom.current();
        double[] mocX = new double[TEN_LUA_X4_SO_DOAN_CONG + 1];
        double[] mocY = new double[TEN_LUA_X4_SO_DOAN_CONG + 1];
        mocX[0] = diemTrungX;
        mocY[0] = diemTrungY;
        mocX[TEN_LUA_X4_SO_DOAN_CONG] = diemTrungX;
        mocY[TEN_LUA_X4_SO_DOAN_CONG] = diemTrungY;

        // Moi vien bung ve mot phia khac nhau, sau do doi huong ngau nhien
        // qua ba moc. Vi tri moc thay doi moi phat nen khong lap mot vong
        // tron co dinh, nhung Catmull-Rom van giu chuyen dong lien tuc.
        double goc = phaBungNgauNhien
                + chiSoTenLua * Math.PI * 2.0D / TEN_LUA_X4_SO_TEN_LUA
                + ngauNhien.nextDouble(-Math.PI / 12.0D, Math.PI / 12.0D);
        for (int moc = 1; moc < TEN_LUA_X4_SO_DOAN_CONG; moc++) {
            int banKinh = ngauNhien.nextInt(
                    moc == 2 ? 85 : 60,
                    moc == 2 ? 151 : 121);
            mocX[moc] = diemTrungX + Math.cos(goc) * banKinh;
            mocY[moc] = diemTrungY + Math.sin(goc)
                    * banKinh * ngauNhien.nextDouble(0.60D, 0.90D);
            goc += ngauNhien.nextDouble(
                    Math.toRadians(75.0D), Math.toRadians(165.0D));
        }

        short[] xs = new short[TEN_LUA_X4_SO_DIEM];
        short[] ys = new short[TEN_LUA_X4_SO_DIEM];
        for (int doan = 0; doan < TEN_LUA_X4_SO_DOAN_CONG; doan++) {
            int truoc = Math.max(0, doan - 1);
            int dau = doan;
            int cuoi = doan + 1;
            int sau = Math.min(TEN_LUA_X4_SO_DOAN_CONG, doan + 2);
            for (int buoc = 0; buoc < TEN_LUA_X4_SO_DIEM_MOI_DOAN;
                    buoc++) {
                double t = (double) buoc / TEN_LUA_X4_SO_DIEM_MOI_DOAN;
                int chiSo = doan * TEN_LUA_X4_SO_DIEM_MOI_DOAN + buoc;
                xs[chiSo] = gioiHanShort(catmullRom(
                        mocX[truoc], mocX[dau], mocX[cuoi], mocX[sau], t));
                ys[chiSo] = gioiHanShort(catmullRom(
                        mocY[truoc], mocY[dau], mocY[cuoi], mocY[sau], t));
            }
        }
        // Diem dau/cuoi phai trung tuyet doi de client thay ten lua bung
        // tai hitbox va ghim tro lai dung muc tieu, khong giat ve dau nong.
        xs[0] = (short) diemTrungX;
        ys[0] = (short) diemTrungY;
        xs[xs.length - 1] = (short) diemTrungX;
        ys[ys.length - 1] = (short) diemTrungY;
        // Type 27 xuyen dia hinh: khong cat path theo terrain/nhan vat phu.
        return new DuongRoi(xs, ys, true);
    }

    private static double catmullRom(
            double p0,
            double p1,
            double p2,
            double p3,
            double t
    ) {
        double t2 = t * t;
        double t3 = t2 * t;
        return 0.5D * ((2.0D * p1)
                + (-p0 + p2) * t
                + (2.0D * p0 - 5.0D * p1 + 4.0D * p2 - p3) * t2
                + (-p0 + 3.0D * p1 - 3.0D * p2 + p3) * t3);
    }

    private static short gioiHanShort(double giaTri) {
        long lamTron = Math.round(giaTri);
        return (short) Math.max(
                Short.MIN_VALUE, Math.min(Short.MAX_VALUE, lamTron));
    }

    /**
     * Bullet type 16 cua client la chuoi hai giai doan bat buoc:
     * duong 0 la vien danh dau, duong 1..6 la sau vien coi type 12 roi xuong.
     * Gui thieu mot duong se lam client doc vuot mang khi activeMortarBum().
     */
    private static ChickenKetQuaDan taoDanTraiPha(
            ChickenChienBinh nguoiBan,
            short dauNongX,
            short dauNongY,
            short goc,
            byte luc,
            ChickenCongThucVatPhamChien.CauHinh cauHinh,
            byte windX,
            byte windY,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc,
            ChickenCauHinhSatThuongVatPham.HoSo hoSo
    ) {
        ChickenQuanLyCongThucSung.KetQuaQuyDao quyDao =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        dauNongX, dauNongY, goc, luc, cauHinh,
                        windX, windY, banDo);
        short[] markerX = banSao(quyDao.getHienThiX());
        short[] markerY = banSao(quyDao.getHienThiY());
        if (markerX.length == 0 || markerY.length == 0) {
            markerX = new short[]{dauNongX};
            markerY = new short[]{dauNongY};
        }
        datDiemDau(markerX, markerY, dauNongX, dauNongY);

        VaChamNhanVat vaChamMarker = timVaChamDauTien(
                nguoiBan, dauNongX, dauNongY,
                markerX, markerY, cacMucTieu, boLoc, cauHinh);
        if (vaChamMarker != null) {
            markerX = Arrays.copyOf(
                    markerX, vaChamMarker.chiSoDiem + 1);
            markerY = Arrays.copyOf(
                    markerY, vaChamMarker.chiSoDiem + 1);
            markerX[markerX.length - 1] = vaChamMarker.x;
            markerY[markerY.length - 1] = vaChamMarker.y;
        }

        int chiSoMarker = Math.min(markerX.length, markerY.length) - 1;
        int xMarker = markerX[chiSoMarker];
        int yMarker = markerY[chiSoMarker];
        short[][] cacDuongX = new short[1 + TRAI_PHA_LECH_X.length][];
        short[][] cacDuongY = new short[1 + TRAI_PHA_LECH_X.length][];
        boolean[] coVaCham = new boolean[cacDuongX.length];
        cacDuongX[0] = markerX;
        cacDuongY[0] = markerY;

        for (int i = 0; i < TRAI_PHA_LECH_X.length; i++) {
            DuongRoi duongRoi = taoDuongTraiPhaRoi(
                    xMarker + TRAI_PHA_LECH_X[i],
                    yMarker,
                    nguoiBan,
                    cacMucTieu,
                    boLoc,
                    banDo);
            cacDuongX[i + 1] = duongRoi.xs;
            cacDuongY[i + 1] = duongRoi.ys;
            coVaCham[i + 1] = duongRoi.coVaCham;
        }

        Map<ChickenChienBinh, Integer> damage =
                new LinkedHashMap<ChickenChienBinh, Integer>();
        if (cacMucTieu != null && hoSo.coSatThuongNo()) {
            long tanCongNhan = (long) Math.max(1, nguoiBan.tanCong)
                    * hoSo.getPhanTramTanCong();
            int tanCong = (int) Math.min(
                    Integer.MAX_VALUE, (tanCongNhan + 50L) / 100L);
            for (ChickenChienBinh mucTieu : cacMucTieu) {
                if (!hopLe(nguoiBan, mucTieu, boLoc)) {
                    continue;
                }
                int satThuongGoc = Math.max(1, tanCong - mucTieu.giap);
                int lonNhat = 0;
                for (int i = 1; i < cacDuongX.length; i++) {
                    if (!coVaCham[i]) {
                        continue;
                    }
                    int soDiem = Math.min(
                            cacDuongX[i].length, cacDuongY[i].length);
                    if (soDiem <= 0) {
                        continue;
                    }
                    lonNhat = Math.max(lonNhat,
                            tinhSatThuongNoChoMucTieu(
                                    hoSo.getHoSoNo(), satThuongGoc,
                                    cacDuongX[i][soDiem - 1],
                                    cacDuongY[i][soDiem - 1],
                                    mucTieu, boLoc, banDo));
                }
                if (lonNhat > 0) {
                    damage.put(mucTieu, lonNhat);
                }
            }
        }
        return new ChickenKetQuaDan(
                cauHinh.getLoaiDan(), dauNongX, dauNongY,
                goc, luc, luc, cacDuongX, cacDuongY,
                damage, false);
    }

    private static DuongRoi taoDuongTraiPhaRoi(
            int dichX,
            int yMarker,
            ChickenChienBinh nguoiBan,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        int batDauY = yMarker - TRAI_PHA_DO_CAO_ROI;
        int ketThucY = banDo.getHeight() + 64;
        List<Short> xs = new ArrayList<Short>();
        List<Short> ys = new ArrayList<Short>();
        xs.add((short) dichX);
        ys.add((short) batDauY);

        int yCu = batDauY;
        for (int diem = 1; diem < TRAI_PHA_SO_DIEM_ROI; diem++) {
            double tiLe = (double) diem / (TRAI_PHA_SO_DIEM_ROI - 1);
            int yMoi = (int) Math.round(
                    batDauY + (ketThucY - batDauY) * tiLe * tiLe);
            VaChamDoan vaCham = timVaChamDauTienTrenDoan(
                    nguoiBan, dichX, yCu, dichX, yMoi,
                    cacMucTieu, boLoc, banDo);
            if (vaCham != null) {
                xs.add((short) vaCham.x);
                ys.add((short) vaCham.y);
                return new DuongRoi(
                        doiMang(xs), doiMang(ys), true);
            }
            xs.add((short) dichX);
            ys.add((short) yMoi);
            yCu = yMoi;
        }
        return new DuongRoi(doiMang(xs), doiMang(ys), false);
    }

    private static DuongRoi taoDuongBomB52(
            short dichX,
            short dichY,
            ChickenChienBinh nguoiBan,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        int batDauX = dichX - B52_LECH_TRAI_THA_BOM;
        int batDauY = dichY - B52_DO_CAO_THA_BOM;
        List<Short> xs = new ArrayList<Short>();
        List<Short> ys = new ArrayList<Short>();
        xs.add((short) batDauX);
        ys.add((short) batDauY);

        int xCu = batDauX;
        int yCu = batDauY;
        for (int diem = 1; diem < B52_SO_DIEM_ROI; diem++) {
            double tiLe = (double) diem / (B52_SO_DIEM_ROI - 1);
            int xMoi = (int) Math.round(
                    batDauX + (dichX - batDauX) * tiLe);
            int yMoi = (int) Math.round(
                    batDauY + (dichY - batDauY) * tiLe * tiLe);
            VaChamDoan vaCham = timVaChamDauTienTrenDoan(
                    nguoiBan, xCu, yCu, xMoi, yMoi,
                    cacMucTieu, boLoc, banDo);
            if (vaCham != null) {
                xs.add((short) vaCham.x);
                ys.add((short) vaCham.y);
                return new DuongRoi(
                        doiMang(xs), doiMang(ys), true);
            }
            xs.add((short) xMoi);
            ys.add((short) yMoi);
            xCu = xMoi;
            yCu = yMoi;
        }
        return new DuongRoi(doiMang(xs), doiMang(ys), false);
    }

    private static VaChamDoan timVaChamDauTienTrenDoan(
            ChickenChienBinh nguoiBan,
            int xDau,
            int yDau,
            int xCuoi,
            int yCuoi,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        int dx = xCuoi - xDau;
        int dy = yCuoi - yDau;
        int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
        for (int buoc = 1; buoc <= soBuoc; buoc++) {
            int x = xDau + (int) Math.round((double) dx * buoc / soBuoc);
            int y = yDau + (int) Math.round((double) dy * buoc / soBuoc);
            if (cacMucTieu != null) {
                for (ChickenChienBinh mucTieu : cacMucTieu) {
                    if (hopLe(nguoiBan, mucTieu, boLoc)
                            && boLoc.trungHitbox(mucTieu, x, y)) {
                        return new VaChamDoan(x, y);
                    }
                }
            }
            if (x >= 0 && x < banDo.getWidth()
                    && y >= 0 && y < banDo.getHeight()
                    && banDo.coVaCham((short) x, (short) y)) {
                return new VaChamDoan(x, y);
            }
        }
        return null;
    }

    private static short[] doiMang(List<Short> giaTri) {
        short[] ketQua = new short[giaTri.size()];
        for (int i = 0; i < giaTri.size(); i++) {
            ketQua[i] = giaTri.get(i);
        }
        return ketQua;
    }

    private static int tinhSatThuongNoChoMucTieu(
            ChickenCauHinhSatThuongSung.HoSoSatThuong hoSo,
            int satThuongGoc,
            int xNo,
            int yNo,
            ChickenChienBinh mucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        int nuaRong = boLoc.nuaRongHitbox(mucTieu);
        int lechTren = boLoc.lechTrenHitbox(mucTieu);
        int lechDuoi = boLoc.lechDuoiHitbox(mucTieu);
        int dichX = Math.max(
                mucTieu.x - nuaRong,
                Math.min(mucTieu.x + nuaRong, xNo));
        int dichY = Math.max(
                mucTieu.y - lechTren,
                Math.min(mucTieu.y - lechDuoi, yNo));
        double khoangCach = Math.hypot(xNo - dichX, yNo - dichY);
        int phanTramQuaDiaHinh =
                ChickenTinhSatThuongNo.tinhPhanTramQuaDiaHinh(
                        hoSo,
                        xNo,
                        yNo,
                        mucTieu.x,
                        mucTieu.y,
                        nuaRong,
                        lechTren,
                        lechDuoi,
                        banDo,
                        khoangCach
                );
        return ChickenTinhSatThuongNo.tinhSatThuong(
                hoSo, satThuongGoc, khoangCach, phanTramQuaDiaHinh);
    }

    private static ChickenQuanLyCongThucSung.KiemTraBanDo banDoKhongVaCham(
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        return new ChickenQuanLyCongThucSung.KiemTraBanDo() {
            @Override
            public int getWidth() {
                return banDo.getWidth();
            }

            @Override
            public int getHeight() {
                return banDo.getHeight();
            }

            @Override
            public boolean coVaCham(short x, short y) {
                return false;
            }

            @Override
            public short[][] layCacVoiRong() {
                return banDo.layCacVoiRong();
            }
        };
    }

    private static VaChamNhanVat timVaChamDauTien(
            ChickenChienBinh nguoiBan,
            short dauNongX,
            short dauNongY,
            short[] xs,
            short[] ys,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc,
            ChickenCongThucVatPhamChien.CauHinh cauHinh
    ) {
        if (cacMucTieu == null) {
            return null;
        }
        int soDiem = Math.min(xs.length, ys.length);
        int xCu = dauNongX;
        int yCu = dauNongY;
        for (int i = 0; i < soDiem; i++) {
            int dx = xs[i] - xCu;
            int dy = ys[i] - yCu;
            int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                int x = xCu + (int) Math.round((double) dx * buoc / soBuoc);
                int y = yCu + (int) Math.round((double) dy * buoc / soBuoc);
                for (ChickenChienBinh mucTieu : cacMucTieu) {
                    if (hopLe(nguoiBan, mucTieu, boLoc)
                            && boLoc.trungHitbox(mucTieu, x, y)) {
                        return new VaChamNhanVat(
                                mucTieu, i, (short) x, (short) y);
                    }
                }
            }
            xCu = xs[i];
            yCu = ys[i];
        }
        return null;
    }

    private static Set<ChickenChienBinh> timTatCaMucTieuTrung(
            ChickenChienBinh nguoiBan,
            short dauNongX,
            short dauNongY,
            short[] xs,
            short[] ys,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc
    ) {
        Set<ChickenChienBinh> ketQua =
                new LinkedHashSet<ChickenChienBinh>();
        if (cacMucTieu == null || xs == null || ys == null) {
            return ketQua;
        }
        int soDiem = Math.min(xs.length, ys.length);
        int xCu = dauNongX;
        int yCu = dauNongY;
        for (int i = 0; i < soDiem; i++) {
            int dx = xs[i] - xCu;
            int dy = ys[i] - yCu;
            int soBuoc = Math.max(1, Math.max(Math.abs(dx), Math.abs(dy)));
            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                int x = xCu + (int) Math.round((double) dx * buoc / soBuoc);
                int y = yCu + (int) Math.round((double) dy * buoc / soBuoc);
                for (ChickenChienBinh mucTieu : cacMucTieu) {
                    if (!ketQua.contains(mucTieu)
                            && hopLe(nguoiBan, mucTieu, boLoc)
                            && boLoc.trungHitbox(mucTieu, x, y)) {
                        ketQua.add(mucTieu);
                    }
                }
            }
            xCu = xs[i];
            yCu = ys[i];
        }
        return ketQua;
    }

    private static boolean hopLe(
            ChickenChienBinh nguoiBan,
            ChickenChienBinh mucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc
    ) {
        return mucTieu != null && !mucTieu.chet && !mucTieu.daRoiTran
                && boLoc.chapNhan(nguoiBan, mucTieu);
    }

    private static short[] banSao(short[] mang) {
        return mang == null ? new short[0] : mang.clone();
    }

    private static boolean diemCuoiNamTrenDiaHinh(
            short[] xs,
            short[] ys,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        int soDiem = Math.min(xs.length, ys.length);
        if (soDiem <= 0) {
            return false;
        }
        short x = xs[soDiem - 1];
        short y = ys[soDiem - 1];
        return x >= 0 && x < banDo.getWidth()
                && y >= 0 && y < banDo.getHeight()
                && banDo.coVaCham(x, y);
    }

    private static void datDiemDau(
            short[] xs,
            short[] ys,
            short x,
            short y
    ) {
        if (xs.length > 0 && ys.length > 0) {
            xs[0] = x;
            ys[0] = y;
        }
    }

    private static final class VaChamNhanVat {
        private final ChickenChienBinh mucTieu;
        private final int chiSoDiem;
        private final short x;
        private final short y;

        private VaChamNhanVat(
                ChickenChienBinh mucTieu,
                int chiSoDiem,
                short x,
                short y
        ) {
            this.mucTieu = mucTieu;
            this.chiSoDiem = chiSoDiem;
            this.x = x;
            this.y = y;
        }
    }

    private static final class DuongRoi {
        private final short[] xs;
        private final short[] ys;
        private final boolean coVaCham;

        private DuongRoi(short[] xs, short[] ys, boolean coVaCham) {
            this.xs = xs;
            this.ys = ys;
            this.coVaCham = coVaCham;
        }
    }

    private static final class ChangKhoan {
        private final short[] xs;
        private final short[] ys;
        private final boolean coNo;
        private final boolean chamNhanVat;

        private ChangKhoan(
                short[] xs,
                short[] ys,
                boolean coNo,
                boolean chamNhanVat
        ) {
            this.xs = xs;
            this.ys = ys;
            this.coNo = coNo;
            this.chamNhanVat = chamNhanVat;
        }
    }

    private static final class HuongThang {
        private final double dx;
        private final double dy;

        private HuongThang(double dx, double dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    private static final class VaChamDoan {
        private final int x;
        private final int y;

        private VaChamDoan(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
