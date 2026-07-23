package com.chicken.nhapvai;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.nhapvai.ChickenNhanVatPhu;
import com.chicken.nhapvai.ChickenKhu;
import java.util.ArrayList;

public class ChickenBanDoRPG {
    public static ArrayList<ChickenKhu> zones = new ArrayList();
    public static ArrayList<ChickenNhanVatPhu> npcs = new ArrayList();

    public static void khoiTaoKhu() {
        for (int i = 0; i < 100; ++i) {
            zones.add(new ChickenKhu(i));
        }
        npcs.add(new ChickenNhanVatPhu(0, 0, (short) 100, (short) 360, (byte) 1, (short) 1900, (short) 237, (short) 238, (short) 239));
        npcs.add(new ChickenNhanVatPhu(1, 0, (short) 200, (short) 360, (byte) 2, (short) 1908, (short) 240, (short) 241, (short) 242));
        npcs.add(new ChickenNhanVatPhu(2, 0, (short) 300, (short) 360, (byte) 3, (short) 1907, (short) 243, (short) 244, (short) 245));
        npcs.add(new ChickenNhanVatPhu(3, 0, (short) 400, (short) 360, (byte) 0, (short) 1896, (short) 234, (short) 235, (short) 236));
    }

    public static void vao(int zoneId, ChickenNguoiChoi nguoiChoi) {
        ChickenKhu z = zones.get(zoneId);
        if (z != null) {
            if (!z.vao(nguoiChoi)) {
                nguoiChoi.moHopThoaiOK("Khu vực đã đầy.");
            }
        } else {
            nguoiChoi.moHopThoaiOK("Có lỗi xảy ra.");
        }
    }

    public static void vao(ChickenNguoiChoi nguoiChoi) {
        for (ChickenKhu z : zones) {
            if (z != null && z.vao(nguoiChoi)) break;
        }
    }

    public static void roi(ChickenNguoiChoi nguoiChoi) {
        ChickenKhu z = zones.get(nguoiChoi.zoneId);
        z.roi(nguoiChoi);
    }
}

