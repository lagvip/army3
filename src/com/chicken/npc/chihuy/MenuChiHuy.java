package com.chicken.npc.chihuy;

import com.chicken.mohinh.ChickenNguoiChoi;
import java.util.Vector;

public final class MenuChiHuy {
    static final int MENU_CHINH = 1;
    static final int MENU_CHIEN_DAU = 2;

    private MenuChiHuy() {
    }

    public static void moMenuChinh(ChickenNguoiChoi nguoiChoi) {
        Vector<String> danhSach = new Vector<>();
        danhSach.add("Chiến đấu");
        danhSach.add("Shop đặc biệt");
        nguoiChoi.dichVu.moDanhSach("Đồng chí cần thông tin gì?", danhSach);
        XuLyMenuChiHuy.ghiNhanMenu(nguoiChoi, MENU_CHINH);
    }

    static void moMenuChienDau(ChickenNguoiChoi nguoiChoi) {
        Vector<String> danhSach = new Vector<>();
        danhSach.add("Chọn khu vực");
        danhSach.add("1 VS 1");
        danhSach.add("2 VS 2");
        danhSach.add("3 VS 3");
        nguoiChoi.dichVu.moDanhSach("Chọn hình thức chiến đấu", danhSach);
        XuLyMenuChiHuy.ghiNhanMenu(nguoiChoi, MENU_CHIEN_DAU);
    }
}
