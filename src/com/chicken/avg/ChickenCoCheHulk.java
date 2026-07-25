package com.chicken.avg;

import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenKetQuaDan;

/**
 * Hulk khong ban ra mot vat the doc lap: client di chuyen chinh Hulk theo
 * tung diem cua quy dao. Server phai chot cung vi tri nay de lan sau khong
 * keo Hulk ve diem ban cu va de xu ly roi khoi map mot cach authoritative.
 */
public final class ChickenCoCheHulk {
    public static final byte AVG_HULK = 2;

    private ChickenCoCheHulk() {
    }

    public static boolean laHulk(byte avenger) {
        return avenger == AVG_HULK;
    }

    /** Chot toa do server cua Hulk tai diem cuoi duong dan chinh. */
    public static boolean apDungViTriCuoi(
            ChickenChienBinh hulk,
            ChickenKetQuaDan ketQua,
            int rongMap,
            int caoMap
    ) {
        if (hulk == null || !laHulk(hulk.avenger) || ketQua == null) {
            return false;
        }
        short[] xs = ketQua.duongX;
        short[] ys = ketQua.duongY;
        int soDiem = Math.min(xs == null ? 0 : xs.length, ys == null ? 0 : ys.length);
        if (soDiem <= 0) {
            return false;
        }
        hulk.x = xs[soDiem - 1];
        hulk.y = ys[soDiem - 1];
        return daRaKhoiMap(xs, ys, rongMap, caoMap);
    }

    public static boolean daRaKhoiMap(
            short[] xs,
            short[] ys,
            int rongMap,
            int caoMap
    ) {
        int soDiem = Math.min(xs == null ? 0 : xs.length, ys == null ? 0 : ys.length);
        for (int i = 0; i < soDiem; i++) {
            // Bay qua canh trai/phai hoac xuong day la Hulk roi khoi tran.
            // Vung troi phia tren map van hop le, nhu cac phat ban sieu cao.
            if (xs[i] < 0 || xs[i] >= rongMap || ys[i] >= caoMap) {
                return true;
            }
        }
        return false;
    }
}
