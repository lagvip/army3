package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;

public final class ThanhVienBoss {
    private final ChickenNguoiChoi nguoiChoi;
    private byte ghe;
    private final long thuTuVao;
    private final long thoiDiemVao;
    private boolean chuPhong;
    private boolean sanSang;

    public ThanhVienBoss(
            ChickenNguoiChoi nguoiChoi,
            byte ghe,
            long thuTuVao,
            boolean chuPhong
    ) {
        this.nguoiChoi = nguoiChoi;
        this.ghe = ghe;
        this.thuTuVao = thuTuVao;
        this.thoiDiemVao = System.currentTimeMillis();
        this.chuPhong = chuPhong;
    }

    public ChickenNguoiChoi getNguoiChoi() {
        return this.nguoiChoi;
    }

    public byte getGhe() {
        return this.ghe;
    }

    void setGhe(byte ghe) {
        this.ghe = ghe;
    }

    public long getThuTuVao() {
        return this.thuTuVao;
    }

    public long getThoiDiemVao() {
        return this.thoiDiemVao;
    }

    public boolean isChuPhong() {
        return this.chuPhong;
    }

    public void setChuPhong(boolean chuPhong) {
        this.chuPhong = chuPhong;
        if (chuPhong) {
            this.sanSang = false;
        }
    }

    public boolean isSanSang() {
        return this.sanSang;
    }

    public void setSanSang(boolean sanSang) {
        this.sanSang = !this.chuPhong && sanSang;
    }
}
