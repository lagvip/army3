package com.chicken.phong;

public class ChickenPhong {
    public final byte ma;
    public final byte loai;
    public final String ten;
    public final ChickenChoDau[] banChos;

    public ChickenPhong(int ma, int boardCount, byte loai, byte maxPlayers, byte maBanDo) {
        this.ma = (byte)ma;
        this.loai = loai;
        this.ten = "Phòng " + (ma + 1);
        this.banChos = new ChickenChoDau[boardCount];
        for (int i = 0; i < boardCount; i++) {
            this.banChos[i] = new ChickenChoDau(this, (byte)i, maxPlayers, maBanDo);
        }
    }

    public byte layDoDay() {
        int current = 0;
        int lonNhat = 0;
        for (ChickenChoDau banCho : this.banChos) {
            current += banCho.laySoNguoiChoi();
            lonNhat += banCho.maxPlayers;
        }
        if (lonNhat <= 0) {
            return 2;
        }
        int phanTram = current * 100 / lonNhat;
        if (phanTram < 50) {
            return 2;
        }
        return (byte)(phanTram < 75 ? 1 : 0);
    }
}
