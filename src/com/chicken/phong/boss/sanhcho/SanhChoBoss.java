package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;

public final class SanhChoBoss {
    public enum TrangThai {
        DANG_CHO,
        DANG_BAT_DAU,
        DANG_CHIEN,
        DA_KET_THUC
    }

    private final byte maPhong;
    private final byte maBan;
    private final byte maBanDoMacDinh;
    private byte maBanDo;
    private final byte toiDa;
    private final int giaHienThi;
    private final long thoiDiemTao;
    private final ThanhVienBoss[] thanhViens;
    private ThanhVienBoss chuPhong;
    private String matKhau = "";
    private TrangThai trangThai = TrangThai.DANG_CHO;

    public SanhChoBoss(
            byte maPhong,
            byte maBan,
            byte maBanDo,
            byte toiDa,
            int giaHienThi
    ) {
        this.maPhong = maPhong;
        this.maBan = maBan;
        this.maBanDoMacDinh = maBanDo;
        this.maBanDo = maBanDo;
        this.toiDa = toiDa;
        this.giaHienThi = giaHienThi;
        this.thoiDiemTao = System.currentTimeMillis();
        this.thanhViens = new ThanhVienBoss[toiDa & 0xFF];
    }

    public byte getMaPhong() {
        return this.maPhong;
    }

    public byte getMaBan() {
        return this.maBan;
    }

    public synchronized byte getMaBanDo() {
        return this.maBanDo;
    }

    public synchronized void setMaBanDo(byte maBanDo) {
        this.maBanDo = maBanDo;
    }

    public byte getToiDa() {
        return this.toiDa;
    }

    public int getGiaHienThi() {
        return this.giaHienThi;
    }

    public long getThoiDiemTao() {
        return this.thoiDiemTao;
    }


    public synchronized boolean coMatKhau() {
        return this.matKhau != null && !this.matKhau.isEmpty();
    }

    public synchronized boolean kiemTraMatKhau(String matKhauNhap) {
        if (!this.coMatKhau()) {
            return true;
        }
        return this.matKhau.equals(matKhauNhap == null ? "" : matKhauNhap.trim());
    }

    public synchronized void setMatKhau(String matKhauMoi) {
        this.matKhau = matKhauMoi == null ? "" : matKhauMoi.trim();
    }

    public synchronized int layGheTrongTheoPhe(int phe, int gheUuTien) {
        int pheCanTim = phe & 1;
        if (gheUuTien >= 0
                && gheUuTien < this.thanhViens.length
                && (gheUuTien & 1) == pheCanTim
                && this.thanhViens[gheUuTien] == null) {
            return gheUuTien;
        }
        for (int i = 0; i < this.thanhViens.length; i++) {
            if ((i & 1) == pheCanTim && this.thanhViens[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public synchronized boolean chuyenGhe(ThanhVienBoss thanhVien, int gheMoi) {
        if (thanhVien == null || gheMoi < 0 || gheMoi >= this.thanhViens.length) {
            return false;
        }
        int gheCu = thanhVien.getGhe() & 0xFF;
        if (gheCu >= this.thanhViens.length
                || this.thanhViens[gheCu] != thanhVien
                || this.thanhViens[gheMoi] != null) {
            return false;
        }
        this.thanhViens[gheCu] = null;
        this.thanhViens[gheMoi] = thanhVien;
        thanhVien.setGhe((byte) gheMoi);
        return true;
    }

    public synchronized TrangThai getTrangThai() {
        return this.trangThai;
    }

    public synchronized void setTrangThai(TrangThai trangThai) {
        this.trangThai = trangThai == null ? TrangThai.DANG_CHO : trangThai;
    }

    public synchronized boolean isDangCho() {
        return this.trangThai == TrangThai.DANG_CHO;
    }

    public synchronized boolean isDaBatDau() {
        return this.trangThai != TrangThai.DANG_CHO;
    }

    public synchronized int getSoNguoi() {
        int soNguoi = 0;
        for (ThanhVienBoss thanhVien : this.thanhViens) {
            if (thanhVien != null) {
                soNguoi++;
            }
        }
        return soNguoi;
    }

    public synchronized int layGheTrongDauTien() {
        for (int i = 0; i < this.thanhViens.length; i++) {
            if (this.thanhViens[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public synchronized ThanhVienBoss getChuPhong() {
        return this.chuPhong;
    }

    public synchronized ThanhVienBoss timThanhVien(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return null;
        }
        for (ThanhVienBoss thanhVien : this.thanhViens) {
            if (thanhVien != null
                    && (thanhVien.getNguoiChoi() == nguoiChoi
                    || thanhVien.getNguoiChoi().ma == nguoiChoi.ma)) {
                return thanhVien;
            }
        }
        return null;
    }

    public synchronized ThanhVienBoss timThanhVienTheoGhe(int ghe) {
        if (ghe < 0 || ghe >= this.thanhViens.length) {
            return null;
        }
        return this.thanhViens[ghe];
    }

    public synchronized boolean themThanhVien(ThanhVienBoss thanhVien) {
        if (thanhVien == null) {
            return false;
        }
        int ghe = thanhVien.getGhe() & 0xFF;
        if (ghe >= this.thanhViens.length || this.thanhViens[ghe] != null) {
            return false;
        }
        this.thanhViens[ghe] = thanhVien;
        if (this.chuPhong == null || thanhVien.isChuPhong()) {
            this.datChuPhongNoiBo(thanhVien);
        }
        return true;
    }

    public synchronized ThanhVienBoss xoaThanhVien(ChickenNguoiChoi nguoiChoi) {
        ThanhVienBoss thanhVien = this.timThanhVien(nguoiChoi);
        if (thanhVien == null) {
            return null;
        }
        int ghe = thanhVien.getGhe() & 0xFF;
        if (ghe < this.thanhViens.length) {
            this.thanhViens[ghe] = null;
        }
        if (this.chuPhong == thanhVien) {
            this.chuPhong = null;
        }
        thanhVien.setChuPhong(false);
        thanhVien.setSanSang(false);
        return thanhVien;
    }

    public synchronized void datChuPhong(ThanhVienBoss thanhVien) {
        if (thanhVien == null || this.timThanhVien(thanhVien.getNguoiChoi()) == null) {
            return;
        }
        this.datChuPhongNoiBo(thanhVien);
    }

    private void datChuPhongNoiBo(ThanhVienBoss thanhVien) {
        for (ThanhVienBoss hienTai : this.thanhViens) {
            if (hienTai != null) {
                hienTai.setChuPhong(hienTai == thanhVien);
            }
        }
        this.chuPhong = thanhVien;
    }

    public synchronized ThanhVienBoss[] chupThanhVien() {
        return this.thanhViens.clone();
    }

    public synchronized boolean tatCaThanhVienDaSanSang() {
        for (ThanhVienBoss thanhVien : this.thanhViens) {
            if (thanhVien != null
                    && !thanhVien.isChuPhong()
                    && !thanhVien.isSanSang()) {
                return false;
            }
        }
        return true;
    }

    public synchronized void reset() {
        for (int i = 0; i < this.thanhViens.length; i++) {
            ThanhVienBoss thanhVien = this.thanhViens[i];
            if (thanhVien != null) {
                thanhVien.setChuPhong(false);
                thanhVien.setSanSang(false);
            }
            this.thanhViens[i] = null;
        }
        this.chuPhong = null;
        this.maBanDo = this.maBanDoMacDinh;
        this.matKhau = "";
        this.trangThai = TrangThai.DANG_CHO;
    }
}
