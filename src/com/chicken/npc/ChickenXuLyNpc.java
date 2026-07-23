package com.chicken.npc;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.npc.tinhbao.ChickenNpcTinhBao;
import com.chicken.npc.quannhu.ChickenNpcQuanNhu;
import com.chicken.npc.doitruong.ChickenNpcDoiTruong;
import com.chicken.npc.chihuy.NpcChiHuy;
import java.io.IOException;

public final class ChickenXuLyNpc {
    private ChickenXuLyNpc() {
    }

    public static void mo(ChickenNguoiChoi nguoiChoi, short npcId) throws IOException {
        switch (npcId) {
            case 0:
                ChickenNpcQuanNhu.mo(nguoiChoi);
                break;
            case 1:
                ChickenNpcTinhBao.mo(nguoiChoi);
                break;
            case 2:
                ChickenNpcDoiTruong.mo(nguoiChoi);
                break;
            case 3:
                NpcChiHuy.mo(nguoiChoi);
                break;
            default:
                nguoiChoi.moHopThoaiOK("NPC này chưa có chức năng.");
                break;
        }
    }
}
