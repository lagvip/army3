package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;
import java.util.UUID;

public final class ThanhVienBoss {
    private final ChickenNguoiChoi nguoiChoi;
    private byte ghe;
    private final long thuTuVao;
    private final long thoiDiemVao;
    private final String maGiaoDich;
    private boolean chuPhong;
    private boolean sanSang;
    private boolean daThuPhiVaoPhong;
    private int phiDaThu;
    private boolean daNhanExpHaBoss;
    private boolean daNhanThuongThang;
    private boolean daNgatKetNoi;

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
        this.maGiaoDich = UUID.randomUUID().toString();
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

    String getMaGiaoDichThuPhi() {
        return "boss-entry:" + this.maGiaoDich;
    }

    String getMaGiaoDichHoanPhi() {
        return "boss-entry-refund:" + this.maGiaoDich;
    }

    String getMaGiaoDichThuongThang() {
        return "boss-win:" + this.maGiaoDich;
    }

    String getMaGiaoDichExpHaBoss() {
        return "boss-exp:" + this.maGiaoDich;
    }

    public boolean isDaThuPhiVaoPhong() {
        return this.daThuPhiVaoPhong;
    }

    void danhDauDaThuPhiVaoPhong() {
        this.danhDauDaThuPhiVaoPhong(0);
    }

    void danhDauDaThuPhiVaoPhong(int phiDaThu) {
        this.daThuPhiVaoPhong = true;
        this.phiDaThu = Math.max(0, phiDaThu);
    }

    void xoaDanhDauDaThuPhiVaoPhong() {
        this.daThuPhiVaoPhong = false;
        this.phiDaThu = 0;
    }

    public int getPhiDaThu() {
        return this.phiDaThu;
    }

    boolean isDaNhanExpHaBoss() {
        return this.daNhanExpHaBoss;
    }

    void danhDauDaNhanExpHaBoss() {
        this.daNhanExpHaBoss = true;
    }

    boolean isDaNhanThuongThang() {
        return this.daNhanThuongThang;
    }

    void danhDauDaNhanThuongThang() {
        this.daNhanThuongThang = true;
    }

    public boolean isDaNgatKetNoi() {
        return this.daNgatKetNoi;
    }

    void danhDauNgatKetNoi() {
        this.daNgatKetNoi = true;
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
