package com.chicken.npc.chihuy;

import com.chicken.mohinh.ChickenNguoiChoi;

public final class NpcChiHuy {
    private NpcChiHuy() {
    }

    public static void mo(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null || nguoiChoi.dichVu == null) {
            return;
        }
        MenuChiHuy.moMenuChinh(nguoiChoi);
    }
}
