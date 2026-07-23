package com.chicken.thoat;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.phong.ChickenQuanLyPhong;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import com.chicken.phong.boss.sanhcho.RoiSanhChoBoss;

public final class ChickenThoatTran {

    private ChickenThoatTran() {
    }

    public static void xuLy(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return;
        }
        if (QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi) != null) {
            RoiSanhChoBoss.xuLy(nguoiChoi);
            return;
        }
        if (nguoiChoi.inTraining) {
            nguoiChoi.thoatLuyenTapVeSanh();
            return;
        }
        ChickenQuanLyPhong.roiBanCho(nguoiChoi);
    }
}
