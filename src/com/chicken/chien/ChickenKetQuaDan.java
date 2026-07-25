package com.chicken.chien;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChickenKetQuaDan {
    public final byte loaiDan;
    public final short batDauX;
    public final short batDauY;
    public final short goc;
    public final byte luc;
    public final byte lucPhu;
    public final short[] duongX;
    public final short[] duongY;
    public final short[] duongPhuX;
    public final short[] duongPhuY;
    public final short[][] cacDuongX;
    public final short[][] cacDuongY;
    public final ChickenChienBinh mucTieu;
    public final int satThuong;
    /** Server da xac nhan quy dao dau tien roi dung vao hitbox sau do roi. */
    public final boolean sieuCao;
    /**
     * Kết quả damage do server tính cho từng mục tiêu. Map này cho phép các loại
     * đạn xuyên người gây damage lên nhiều nhân vật mà không tin hit do client báo.
     */
    public final Map<ChickenChienBinh, Integer> satThuongTheoMucTieu;

    public ChickenKetQuaDan(byte loaiDan, short batDauX, short batDauY, short goc, byte luc,
            short[] duongX, short[] duongY, ChickenChienBinh mucTieu, int satThuong) {
        this(loaiDan, batDauX, batDauY, goc, luc, luc,
                new short[][]{duongX}, new short[][]{duongY},
                taoSatThuongMotMucTieu(mucTieu, satThuong));
    }

    public ChickenKetQuaDan(byte loaiDan, short batDauX, short batDauY, short goc, byte luc,
            short[] duongX, short[] duongY,
            Map<ChickenChienBinh, Integer> satThuongTheoMucTieu) {
        this(loaiDan, batDauX, batDauY, goc, luc, luc,
                new short[][]{duongX}, new short[][]{duongY},
                satThuongTheoMucTieu);
    }

    public ChickenKetQuaDan(byte loaiDan, short batDauX, short batDauY,
            short goc, byte luc, byte lucPhu,
            short[] duongX, short[] duongY,
            short[] duongPhuX, short[] duongPhuY,
            Map<ChickenChienBinh, Integer> satThuongTheoMucTieu) {
        this(loaiDan, batDauX, batDauY, goc, luc, lucPhu,
                new short[][]{duongX, duongPhuX},
                new short[][]{duongY, duongPhuY},
                satThuongTheoMucTieu);
    }

    public ChickenKetQuaDan(byte loaiDan, short batDauX, short batDauY,
            short goc, byte luc, byte lucPhu,
            short[][] cacDuongX, short[][] cacDuongY,
            Map<ChickenChienBinh, Integer> satThuongTheoMucTieu) {
        this(loaiDan, batDauX, batDauY, goc, luc, lucPhu,
                cacDuongX, cacDuongY, satThuongTheoMucTieu, false);
    }

    public ChickenKetQuaDan(byte loaiDan, short batDauX, short batDauY,
            short goc, byte luc, byte lucPhu,
            short[][] cacDuongX, short[][] cacDuongY,
            Map<ChickenChienBinh, Integer> satThuongTheoMucTieu,
            boolean sieuCao) {
        this.loaiDan = loaiDan;
        this.batDauX = batDauX;
        this.batDauY = batDauY;
        this.goc = goc;
        this.luc = luc;
        this.lucPhu = lucPhu;
        this.cacDuongX = cacDuongX == null ? new short[0][] : cacDuongX;
        this.cacDuongY = cacDuongY == null ? new short[0][] : cacDuongY;
        int soDuong = Math.min(this.cacDuongX.length, this.cacDuongY.length);
        this.duongX = soDuong > 0 ? this.cacDuongX[0] : new short[0];
        this.duongY = soDuong > 0 ? this.cacDuongY[0] : new short[0];
        this.duongPhuX = soDuong > 1 ? this.cacDuongX[1] : null;
        this.duongPhuY = soDuong > 1 ? this.cacDuongY[1] : null;
        this.sieuCao = sieuCao;

        LinkedHashMap<ChickenChienBinh, Integer> damage = new LinkedHashMap<>();
        if (satThuongTheoMucTieu != null) {
            for (Map.Entry<ChickenChienBinh, Integer> entry
                    : satThuongTheoMucTieu.entrySet()) {
                if (entry.getKey() != null
                        && entry.getValue() != null
                        && entry.getValue() > 0) {
                    damage.put(entry.getKey(), entry.getValue());
                }
            }
        }
        this.satThuongTheoMucTieu = Collections.unmodifiableMap(damage);
        if (damage.isEmpty()) {
            this.mucTieu = null;
            this.satThuong = 0;
        } else {
            Map.Entry<ChickenChienBinh, Integer> dauTien =
                    damage.entrySet().iterator().next();
            this.mucTieu = dauTien.getKey();
            this.satThuong = dauTien.getValue();
        }
    }

    private static Map<ChickenChienBinh, Integer> taoSatThuongMotMucTieu(
            ChickenChienBinh mucTieu,
            int satThuong
    ) {
        LinkedHashMap<ChickenChienBinh, Integer> ketQua = new LinkedHashMap<>();
        if (mucTieu != null && satThuong > 0) {
            ketQua.put(mucTieu, satThuong);
        }
        return ketQua;
    }
}
