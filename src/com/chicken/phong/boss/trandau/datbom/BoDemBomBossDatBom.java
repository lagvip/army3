package com.chicken.phong.boss.trandau.datbom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Server-authoritative schedule for map 53 timed bombs.
 *
 * <p>A bomb is created at the start of turns 6, 12, 18, ... and expires after
 * ten completed turns, including the turn on which it was created. More than
 * one bomb may therefore be active at the same time.</p>
 */
public final class BoDemBomBossDatBom {
    public static final int CHU_KY_DAT_BOM = 6;
    public static final int SO_LUOT_TRUOC_KHI_NO = 10;

    private final ArrayList<Bom> bomDangHoatDong = new ArrayList<>();
    private int idTiepTheo;

    /**
     * Processes the completion of the preceding turn (if any), then starts the
     * supplied one-based turn number.
     */
    public synchronized KetQuaBatDauLuot batDauLuot(long soThuTuLuot) {
        if (soThuTuLuot <= 0) {
            throw new IllegalArgumentException("soThuTuLuot must be positive");
        }

        ArrayList<Bom> daNo = new ArrayList<>();
        ArrayList<Bom> daCapNhat = new ArrayList<>();
        if (soThuTuLuot > 1) {
            for (int i = this.bomDangHoatDong.size() - 1; i >= 0; i--) {
                Bom bom = this.bomDangHoatDong.get(i);
                bom.luotConLai--;
                if (bom.luotConLai <= 0) {
                    this.bomDangHoatDong.remove(i);
                    daNo.add(0, bom.chup());
                } else {
                    daCapNhat.add(0, bom.chup());
                }
            }
        }

        Bom bomMoi = null;
        if (soThuTuLuot % CHU_KY_DAT_BOM == 0) {
            bomMoi = new Bom(this.layIdTrong(), SO_LUOT_TRUOC_KHI_NO);
            this.bomDangHoatDong.add(bomMoi);
            bomMoi = bomMoi.chup();
        }
        return new KetQuaBatDauLuot(daCapNhat, daNo, bomMoi);
    }

    public synchronized List<Bom> chupBomDangHoatDong() {
        ArrayList<Bom> ketQua = new ArrayList<>(this.bomDangHoatDong.size());
        for (Bom bom : this.bomDangHoatDong) {
            ketQua.add(bom.chup());
        }
        return Collections.unmodifiableList(ketQua);
    }

    public synchronized boolean xoaBom(byte id) {
        for (int i = 0; i < this.bomDangHoatDong.size(); i++) {
            if (this.bomDangHoatDong.get(i).id == id) {
                this.bomDangHoatDong.remove(i);
                return true;
            }
        }
        return false;
    }

    public synchronized void xoaTatCa() {
        this.bomDangHoatDong.clear();
    }

    private byte layIdTrong() {
        for (int thu = 0; thu < 256; thu++) {
            byte ungVien = (byte) (this.idTiepTheo++ & 0xFF);
            boolean daDung = false;
            for (Bom bom : this.bomDangHoatDong) {
                if (bom.id == ungVien) {
                    daDung = true;
                    break;
                }
            }
            if (!daDung) {
                return ungVien;
            }
        }
        throw new IllegalStateException("khong con ID bom trong");
    }

    public static final class Bom {
        private final byte id;
        private int luotConLai;

        private Bom(byte id, int luotConLai) {
            this.id = id;
            this.luotConLai = luotConLai;
        }

        public byte getId() {
            return this.id;
        }

        public int getLuotConLai() {
            return this.luotConLai;
        }

        private Bom chup() {
            return new Bom(this.id, this.luotConLai);
        }
    }

    public static final class KetQuaBatDauLuot {
        private final List<Bom> bomDaCapNhat;
        private final List<Bom> bomDaNo;
        private final Bom bomMoi;

        private KetQuaBatDauLuot(
                List<Bom> bomDaCapNhat,
                List<Bom> bomDaNo,
                Bom bomMoi
        ) {
            this.bomDaCapNhat = Collections.unmodifiableList(bomDaCapNhat);
            this.bomDaNo = Collections.unmodifiableList(bomDaNo);
            this.bomMoi = bomMoi;
        }

        public List<Bom> getBomDaCapNhat() {
            return this.bomDaCapNhat;
        }

        public List<Bom> getBomDaNo() {
            return this.bomDaNo;
        }

        public Bom getBomMoi() {
            return this.bomMoi;
        }
    }
}
