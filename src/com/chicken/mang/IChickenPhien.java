package com.chicken.mang;

import com.chicken.mang.IChickenDichVuGame;
import com.chicken.mang.IChickenXuLyTin;
import com.chicken.mang.ChickenTinNhan;

public interface IChickenPhien {
    public boolean dangKetNoi();

    public void datBoXuLy(IChickenXuLyTin var1);

    public void guiTin(ChickenTinNhan var1);

    public void datDichVu(IChickenDichVuGame var1);

    public void close();
}

