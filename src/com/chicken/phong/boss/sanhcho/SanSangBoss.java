package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;
import java.io.IOException;

public final class SanSangBoss {
    private SanSangBoss() {
    }

    public static void xuLy(ChickenNguoiChoi nguoiChoi, boolean sanSang)
            throws IOException {
        SanhChoBoss sanh = QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
        if (sanh == null) {
            return;
        }
        ThanhVienBoss thanhVien;
        synchronized (sanh) {
            if (!sanh.isDangCho()) {
                return;
            }
            thanhVien = sanh.timThanhVien(nguoiChoi);
            if (thanhVien == null || thanhVien.isChuPhong()) {
                return;
            }
            thanhVien.setSanSang(sanSang);
            nguoiChoi.isReady = thanhVien.isSanSang();
        }
        GoiTinSanhChoBoss.guiCapNhatSanSang(sanh, thanhVien);
    }
}
