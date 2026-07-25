package com.chicken.avg;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenCauHinhSatThuongSung;
import com.chicken.chien.ChickenCauHinhSatThuongSung.HoSoSatThuong;
import com.chicken.chien.ChickenQuanLyCongThucSung;
import com.chicken.chien.ChickenTinhSatThuongNo;

/** Công thức nổ lan server-authoritative dùng riêng cho skill AVG. */
public final class ChickenSatThuongLanKyNang {
    public static final int ID_HO_SO_THOR = 393;
    public static final int ID_HO_SO_HAWK = 397;
    private static final HoSoSatThuong HO_SO_THOR =
            ChickenCauHinhSatThuongSung.theoIdSung(ID_HO_SO_THOR);
    private static final HoSoSatThuong HO_SO_HAWK =
            ChickenCauHinhSatThuongSung.theoIdSung(ID_HO_SO_HAWK);

    private ChickenSatThuongLanKyNang() {
    }

    /** Hawk dồn bốn mũi tại một tâm nổ; mỗi mục tiêu tự áp giáp và falloff. */
    public static int tinhHawk(
            int tanCong,
            int giapMucTieu,
            int tamNoX,
            int tamNoY,
            int mucTieuX,
            int mucTieuY,
            boolean laBoss,
            ChickenQuanLyBanDo banDo
    ) {
        int satThuongMoiMui = Math.max(1, tanCong - Math.max(0, giapMucTieu));
        int satThuongBonMui = nhanAnToan(satThuongMoiMui, ChickenHoatAnhHawk.SO_MUI_TEN);
        return tinhTheoHoSo(
                HO_SO_HAWK,
                satThuongBonMui,
                tamNoX,
                tamNoY,
                mucTieuX,
                mucTieuY,
                laBoss,
                banDo
        );
    }

    /** Thor cộng damage nổ của cả bốn điểm sét đối với từng mục tiêu. */
    public static int tinhThor(
            int tanCong,
            int giapMucTieu,
            short[] cacX,
            short[] cacY,
            int mucTieuX,
            int mucTieuY,
            boolean laBoss,
            ChickenQuanLyBanDo banDo
    ) {
        if (cacX == null || cacY == null) {
            return 0;
        }
        int satThuongMoiTia = Math.max(1, tanCong - Math.max(0, giapMucTieu));
        int tong = 0;
        int soTia = Math.min(cacX.length, cacY.length);
        for (int i = 0; i < soTia; i++) {
            int satThuong = tinhTheoHoSo(
                    HO_SO_THOR,
                    satThuongMoiTia,
                    cacX[i],
                    cacY[i],
                    mucTieuX,
                    mucTieuY,
                    laBoss,
                    banDo
            );
            tong = congAnToan(tong, satThuong);
        }
        return tong;
    }

    private static int tinhTheoHoSo(
            HoSoSatThuong hoSo,
            int satThuongGoc,
            int tamNoX,
            int tamNoY,
            int mucTieuX,
            int mucTieuY,
            boolean laBoss,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        return ChickenTinhSatThuongNo.tinhSatThuongChoNhanVat(
                hoSo,
                satThuongGoc,
                tamNoX,
                tamNoY,
                mucTieuX,
                mucTieuY,
                laBoss,
                banDo
        );
    }

    private static int nhanAnToan(int giaTri, int soLan) {
        long ketQua = (long) Math.max(0, giaTri) * Math.max(0, soLan);
        return ketQua > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) ketQua;
    }

    private static int congAnToan(int a, int b) {
        long ketQua = (long) Math.max(0, a) + Math.max(0, b);
        return ketQua > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) ketQua;
    }
}
