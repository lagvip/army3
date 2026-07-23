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
    public final short[] duongX;
    public final short[] duongY;
    public final ChickenChienBinh mucTieu;
    public final int satThuong;
    /**
     * Kết quả damage do server tính cho từng mục tiêu. Map này cho phép các loại
     * đạn xuyên người gây damage lên nhiều nhân vật mà không tin hit do client báo.
     */
    public final Map<ChickenChienBinh, Integer> satThuongTheoMucTieu;

    public ChickenKetQuaDan(byte loaiDan, short batDauX, short batDauY, short goc, byte luc,
            short[] duongX, short[] duongY, ChickenChienBinh mucTieu, int satThuong) {
        this.loaiDan = loaiDan;
        this.batDauX = batDauX;
        this.batDauY = batDauY;
        this.goc = goc;
        this.luc = luc;
        this.duongX = duongX;
        this.duongY = duongY;
        this.mucTieu = mucTieu;
        this.satThuong = satThuong;
        LinkedHashMap<ChickenChienBinh, Integer> damage = new LinkedHashMap<>();
        if (mucTieu != null && satThuong > 0) {
            damage.put(mucTieu, satThuong);
        }
        this.satThuongTheoMucTieu = Collections.unmodifiableMap(damage);
    }

    public ChickenKetQuaDan(byte loaiDan, short batDauX, short batDauY, short goc, byte luc,
            short[] duongX, short[] duongY,
            Map<ChickenChienBinh, Integer> satThuongTheoMucTieu) {
        this.loaiDan = loaiDan;
        this.batDauX = batDauX;
        this.batDauY = batDauY;
        this.goc = goc;
        this.luc = luc;
        this.duongX = duongX;
        this.duongY = duongY;

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
}
