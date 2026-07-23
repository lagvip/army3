package com.chicken.npc.tinhbao;

import com.chicken.dichvu.ChickenQuanLyBietDoi;
import com.chicken.mohinh.ChickenNguoiChoi;
import java.io.IOException;

public final class ChickenNpcTinhBao {
    private ChickenNpcTinhBao() {
    }

    public static void mo(ChickenNguoiChoi nguoiChoi) throws IOException {
        ChickenQuanLyBietDoi.topClan(nguoiChoi);
    }
}
