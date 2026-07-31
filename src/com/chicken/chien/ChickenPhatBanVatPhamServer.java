package com.chicken.chien;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Mot phat item tao dan do server mo phong. Client chi ve bulletType va
 * cac diem duong bay server tra ve.
 */
public final class ChickenPhatBanVatPhamServer {
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
        datDiemDau(xs, ys, dauNongX, dauNongY);

        VaChamNhanVat vaCham = timVaChamDauTien(
                nguoiBan, dauNongX, dauNongY, xs, ys, cacMucTieu, boLoc);
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

        Map<ChickenChienBinh, Integer> damage =
                new LinkedHashMap<ChickenChienBinh, Integer>();
        boolean sieuCao = false;
        if (coDiemNo && cacMucTieu != null) {
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
                new short[][]{xs},
                new short[][]{ys},
                damage,
                sieuCao
        );
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
        };
    }

    private static VaChamNhanVat timVaChamDauTien(
            ChickenChienBinh nguoiBan,
            short dauNongX,
            short dauNongY,
            short[] xs,
            short[] ys,
            ChickenChienBinh[] cacMucTieu,
            ChickenPhatBanServer.BoLocMucTieu boLoc
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
}
