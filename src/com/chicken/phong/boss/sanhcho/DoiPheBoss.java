package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;
import java.io.IOException;

/** Xử lý nút Đổi phe native CMD 71 trong sảnh boss. */
public final class DoiPheBoss {
    private DoiPheBoss() {
    }

    public static boolean xuLy(ChickenNguoiChoi nguoiChoi) throws IOException {
        SanhChoBoss sanh = QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
        if (sanh == null || nguoiChoi == null) {
            return false;
        }

        int gheCu;
        int gheMoi;
        boolean canGuiHuySanSang;
        synchronized (sanh) {
            ThanhVienBoss thanhVien = sanh.timThanhVien(nguoiChoi);
            if (thanhVien == null) {
                return false;
            }
            if (!sanh.isDangCho()) {
                nguoiChoi.startOKDlg2("Phòng boss đã bắt đầu.");
                return false;
            }

            gheCu = thanhVien.getGhe() & 0xFF;
            int pheMoi = (gheCu & 1) ^ 1;
            int gheDoiDien = gheCu ^ 1;
            gheMoi = sanh.layGheTrongTheoPhe(pheMoi, gheDoiDien);
            if (gheMoi < 0) {
                DebugSanhBoss.log("TU_CHOI_DOI_PHE", nguoiChoi,
                        "P4-" + (sanh.getMaBan() & 0xFF)
                        + " gheCu=" + gheCu
                        + " lyDo=pheBenKiaDaDay");
                nguoiChoi.startOKDlg2("Phe bên kia đã đủ người.");
                return false;
            }

            canGuiHuySanSang = !thanhVien.isChuPhong() && thanhVien.isSanSang();
            thanhVien.setSanSang(false);
            nguoiChoi.isReady = false;
            if (!sanh.chuyenGhe(thanhVien, gheMoi)) {
                DebugSanhBoss.log("TU_CHOI_DOI_PHE", nguoiChoi,
                        "P4-" + (sanh.getMaBan() & 0xFF)
                        + " gheCu=" + gheCu
                        + " gheMoi=" + gheMoi
                        + " lyDo=chuyenGheThatBai");
                return false;
            }
            nguoiChoi.chiSo = gheMoi;
            nguoiChoi.pointSeat = (byte) gheMoi;
        }

        DebugSanhBoss.log("DOI_PHE_THANH_CONG", nguoiChoi,
                "P4-" + (sanh.getMaBan() & 0xFF)
                + " gheCu=" + gheCu
                + " gheMoi=" + gheMoi
                + " pheCu=" + (gheCu & 1)
                + " pheMoi=" + (gheMoi & 1));

        for (ThanhVienBoss nguoiNhan : sanh.chupThanhVien()) {
            if (nguoiNhan != null && nguoiNhan.getNguoiChoi() != null) {
                GoiTinSanhChoBoss.guiDoiPhe(
                        nguoiNhan.getNguoiChoi(),
                        nguoiChoi.ma,
                        (byte) gheMoi
                );
            }
        }
        if (canGuiHuySanSang) {
            ThanhVienBoss thanhVien = sanh.timThanhVien(nguoiChoi);
            if (thanhVien != null) {
                GoiTinSanhChoBoss.guiCapNhatSanSang(sanh, thanhVien);
            }
        }
        return true;
    }
}
