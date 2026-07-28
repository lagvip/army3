package com.chicken.phong.boss.trandau;

import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenKetQuaDan;
import java.util.LinkedHashMap;
import java.util.Map;

/** Tao packet no Cam tu dung cau truc ma client goc mong doi. */
public final class ChickenHoatAnhNoCamTu {

    /** GUN_BOMB_BIG cua client. */
    public static final byte LOAI_DAN = 12;
    /**
     * BM.createBullet(12) cua client bo qua duong 0 va doc bat buoc cac
     * duong 1..6. Gui it hon bay duong lam vong update dan nem loi moi frame.
     */
    public static final int SO_DUONG = 7;

    private static final int[] LECH_X = {0, 16, 8, -8, -16, -8, 8};
    private static final int[] LECH_Y = {0, 0, -14, -14, 0, 14, 14};

    private ChickenHoatAnhNoCamTu() {
    }

    public static ChickenKetQuaDan tao(
            ChickenChienBinh camTu,
            ChickenChienBinh mucTieu,
            int satThuong
    ) {
        if (camTu == null) {
            throw new IllegalArgumentException("camTu");
        }
        short[][] cacDuongX = new short[SO_DUONG][];
        short[][] cacDuongY = new short[SO_DUONG][];
        for (int i = 0; i < SO_DUONG; i++) {
            cacDuongX[i] = new short[]{
                camTu.x,
                (short) (camTu.x + LECH_X[i])
            };
            cacDuongY[i] = new short[]{
                camTu.y,
                (short) (camTu.y + LECH_Y[i])
            };
        }
        Map<ChickenChienBinh, Integer> satThuongTheoMucTieu =
                new LinkedHashMap<>();
        if (mucTieu != null && satThuong > 0) {
            satThuongTheoMucTieu.put(mucTieu, satThuong);
        }
        return new ChickenKetQuaDan(
                LOAI_DAN,
                camTu.x,
                camTu.y,
                (short) 0,
                (byte) 1,
                (byte) 1,
                cacDuongX,
                cacDuongY,
                satThuongTheoMucTieu
        );
    }
}
