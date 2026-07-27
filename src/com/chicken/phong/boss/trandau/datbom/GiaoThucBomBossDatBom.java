package com.chicken.phong.boss.trandau.datbom;

import com.chicken.mang.ChickenTinNhan;
import java.io.IOException;

/** Encodes the native client's CMD 109 timed-bomb protocol. */
public final class GiaoThucBomBossDatBom {
    public static final int CMD_BOM = 109;
    public static final byte HANH_DONG_TAO = 0;
    public static final byte HANH_DONG_NO = 1;
    public static final byte HANH_DONG_TIEN_DO_GO = 2;
    public static final byte HANH_DONG_XOA = 3;
    public static final byte HANH_DONG_CAP_NHAT_LUOT = 4;
    public static final byte KIEU_NO_MOT_QUA = 0;

    private GiaoThucBomBossDatBom() {
    }

    public static ChickenTinNhan taoBom(
            byte id,
            int x,
            int y,
            int luotConLai
    ) throws IOException {
        ChickenTinNhan tin = new ChickenTinNhan(CMD_BOM);
        tin.boGhi().writeByte(HANH_DONG_TAO);
        tin.boGhi().writeByte(id);
        tin.boGhi().writeInt(x);
        tin.boGhi().writeInt(y);
        tin.boGhi().writeByte(kiemTraLuot(luotConLai));
        return tin;
    }

    public static ChickenTinNhan capNhatLuot(
            byte id,
            int luotConLai
    ) throws IOException {
        ChickenTinNhan tin = new ChickenTinNhan(CMD_BOM);
        tin.boGhi().writeByte(HANH_DONG_CAP_NHAT_LUOT);
        tin.boGhi().writeByte(id);
        tin.boGhi().writeByte(kiemTraLuot(luotConLai));
        return tin;
    }

    public static ChickenTinNhan noBom(byte id) throws IOException {
        ChickenTinNhan tin = new ChickenTinNhan(CMD_BOM);
        tin.boGhi().writeByte(HANH_DONG_NO);
        tin.boGhi().writeByte(id);
        tin.boGhi().writeByte(KIEU_NO_MOT_QUA);
        return tin;
    }

    public static ChickenTinNhan capNhatTienDoGo(
            byte id,
            int phanTram
    ) throws IOException {
        if (phanTram < 0 || phanTram > 100) {
            throw new IllegalArgumentException(
                    "phanTram go bom nam ngoai 0..100: " + phanTram);
        }
        ChickenTinNhan tin = new ChickenTinNhan(CMD_BOM);
        tin.boGhi().writeByte(HANH_DONG_TIEN_DO_GO);
        tin.boGhi().writeByte(id);
        tin.boGhi().writeByte(phanTram);
        return tin;
    }

    public static ChickenTinNhan xoaBom(byte id) throws IOException {
        ChickenTinNhan tin = new ChickenTinNhan(CMD_BOM);
        tin.boGhi().writeByte(HANH_DONG_XOA);
        tin.boGhi().writeByte(id);
        return tin;
    }

    private static int kiemTraLuot(int luotConLai) {
        if (luotConLai < 0 || luotConLai > 127) {
            throw new IllegalArgumentException(
                    "luotConLai nam ngoai byte duong: " + luotConLai);
        }
        return luotConLai;
    }
}
