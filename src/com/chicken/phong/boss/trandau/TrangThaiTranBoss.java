package com.chicken.phong.boss.trandau;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chiso.ChickenHieuUngDongDoi;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.sanhcho.ThanhVienBoss;

public final class TrangThaiTranBoss {
    private final byte maBanDo;
    private final byte maNen;
    private final ChickenChienBinh[] chienBinhs;

    private TrangThaiTranBoss(byte maBanDo, byte maNen, ChickenChienBinh[] chienBinhs) {
        this.maBanDo = maBanDo;
        this.maNen = maNen;
        this.chienBinhs = chienBinhs;
    }

    public static TrangThaiTranBoss tao(SanhChoBoss sanh) {
        if (sanh == null) {
            return null;
        }
        ChickenQuanLyBanDo banDo = new ChickenQuanLyBanDo(sanh.getMaBanDo() & 0xFF);
        ChickenChienBinh[] chienBinhs = new ChickenChienBinh[8];
        for (ThanhVienBoss thanhVien : sanh.chupThanhVien()) {
            if (thanhVien == null || thanhVien.getNguoiChoi() == null) {
                continue;
            }
            int ghe = thanhVien.getGhe() & 0xFF;
            if (ghe >= chienBinhs.length) {
                continue;
            }
            chienBinhs[ghe] = new ChickenChienBinh(
                    thanhVien.getNguoiChoi(),
                    thanhVien.getGhe(),
                    banDo.laySinhX(ghe),
                    banDo.laySinhY(ghe)
            );
        }
        ChickenHieuUngDongDoi.apDungChoNhomDongMinh(chienBinhs);
        return new TrangThaiTranBoss(
                banDo.layMaBanDo(),
                banDo.layMaNen(),
                chienBinhs
        );
    }

    public byte getMaBanDo() {
        return this.maBanDo;
    }

    public byte getMaNen() {
        return this.maNen;
    }

    public ChickenChienBinh[] chupChienBinh() {
        return this.chienBinhs.clone();
    }
}
