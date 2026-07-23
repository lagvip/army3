package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.phong.boss.trandau.CauHinhMapBoss;
import java.io.IOException;

/** Xử lý riêng việc chủ phòng đổi map trong sảnh boss. */
public final class DoiMapBoss {
    private DoiMapBoss() {
    }

    public static boolean xuLy(ChickenNguoiChoi nguoiChoi, int maBanDoMoi)
            throws IOException {
        SanhChoBoss sanh = QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
        if (sanh == null || nguoiChoi == null) {
            return false;
        }

        DebugSanhBoss.log("NHAN_YEU_CAU_DOI_MAP_BOSS", nguoiChoi,
                "P4-" + (sanh.getMaBan() & 0xFF)
                + " mapYeuCau=" + maBanDoMoi);

        int mapHienTai;
        String lyDoTuChoi = null;
        boolean doiThanhCong = false;

        synchronized (sanh) {
            mapHienTai = sanh.getMaBanDo() & 0xFF;
            ThanhVienBoss thanhVien = sanh.timThanhVien(nguoiChoi);

            if (thanhVien == null) {
                lyDoTuChoi = "khongPhaiThanhVien";
            } else if (!thanhVien.isChuPhong()) {
                lyDoTuChoi = "khongPhaiChuPhong";
            } else if (!sanh.isDangCho()) {
                lyDoTuChoi = "phongDaBatDau";
            } else if (!CauHinhMapBoss.laMapBossHopLe(maBanDoMoi)) {
                lyDoTuChoi = "mapNgoaiDanhSachBoss";
            } else if (maBanDoMoi == mapHienTai) {
                DebugSanhBoss.log("GIU_NGUYEN_MAP_BOSS", nguoiChoi,
                        "P4-" + (sanh.getMaBan() & 0xFF)
                        + " map=" + mapHienTai
                        + " ten=" + CauHinhMapBoss.layTenMap(mapHienTai));
            } else {
                sanh.setMaBanDo((byte) maBanDoMoi);
                doiThanhCong = true;
            }
        }

        if (lyDoTuChoi != null) {
            DebugSanhBoss.log("TU_CHOI_DOI_MAP_BOSS", nguoiChoi,
                    "P4-" + (sanh.getMaBan() & 0xFF)
                    + " mapYeuCau=" + maBanDoMoi
                    + " mapHienTai=" + mapHienTai
                    + " lyDo=" + lyDoTuChoi);

            // Ép giao diện quay lại map hợp lệ hiện tại nếu client vừa chọn map khác.
            GoiTinSanhChoBoss.guiBanDoSanh(nguoiChoi, sanh);
            if ("khongPhaiChuPhong".equals(lyDoTuChoi)) {
                nguoiChoi.startOKDlg2("Chỉ chủ phòng mới được đổi map boss.");
            } else if ("mapNgoaiDanhSachBoss".equals(lyDoTuChoi)) {
                nguoiChoi.startOKDlg2("Phòng boss chỉ được chọn map 50-55 hoặc 58.");
            } else if ("phongDaBatDau".equals(lyDoTuChoi)) {
                nguoiChoi.startOKDlg2("Phòng boss đã bắt đầu.");
            }
            return false;
        }

        if (!doiThanhCong) {
            GoiTinSanhChoBoss.guiBanDoSanh(nguoiChoi, sanh);
            return true;
        }

        DebugSanhBoss.log("DOI_MAP_BOSS_THANH_CONG", nguoiChoi,
                "P4-" + (sanh.getMaBan() & 0xFF)
                + " mapCu=" + mapHienTai
                + " mapMoi=" + maBanDoMoi
                + " ten=" + CauHinhMapBoss.layTenMap(maBanDoMoi));
        GoiTinSanhChoBoss.guiCapNhatBanDo(sanh);
        return true;
    }
}
