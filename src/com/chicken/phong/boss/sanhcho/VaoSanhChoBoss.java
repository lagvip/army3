package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.nhapvai.ChickenBanDoRPG;
import com.chicken.phong.ChickenQuanLyPhong;
import java.io.IOException;

public final class VaoSanhChoBoss {
    private VaoSanhChoBoss() {
    }

    public static boolean xuLy(ChickenNguoiChoi nguoiChoi, int maBan, String matKhauNhap)
            throws IOException {
        if (nguoiChoi == null) {
            return false;
        }
        /*
         * Khóa theo account thay vì theo object session để hai kết nối trùng
         * tài khoản không thể đồng thời mua vé và chui vào hai phòng khác nhau.
         */
        synchronized (QuanLySanhChoBoss.layKhoaTaiKhoan(nguoiChoi.ma)) {
            return xuLyDaKhoaTaiKhoan(nguoiChoi, maBan, matKhauNhap);
        }
    }

    private static boolean xuLyDaKhoaTaiKhoan(
            ChickenNguoiChoi nguoiChoi,
            int maBan,
            String matKhauNhap
    ) throws IOException {
        DebugSanhBoss.log("BAT_DAU_XU_LY_VAO_SANH", nguoiChoi,
                "maBan=" + maBan);
        if (nguoiChoi.dichVu == null) {
            DebugSanhBoss.log("TU_CHOI_VAO_SANH", nguoiChoi,
                    "lyDo=nguoiChoi_hoac_dichVu_null");
            return false;
        }
        SanhChoBoss sanh = QuanLySanhChoBoss.laySanh(maBan);
        if (sanh == null) {
            DebugSanhBoss.log("TU_CHOI_VAO_SANH", nguoiChoi,
                    "lyDo=maBanKhongTonTai maBan=" + maBan
                    + " soSanh=" + QuanLySanhChoBoss.SO_SANH);
            nguoiChoi.startOKDlg2("Khu vực boss không tồn tại.");
            return false;
        }
        if (nguoiChoi.inTraining) {
            DebugSanhBoss.log("TU_CHOI_VAO_SANH", nguoiChoi, "lyDo=dangLuyenTap");
            nguoiChoi.startOKDlg2("Bạn đang ở trong luyện tập.");
            return false;
        }
        if (ChickenQuanLyPhong.layBanCho(nguoiChoi) != null) {
            DebugSanhBoss.log("TU_CHOI_VAO_SANH", nguoiChoi, "lyDo=dangTrongPvP");
            nguoiChoi.startOKDlg2("Bạn đang ở trong phòng PvP khác.");
            return false;
        }

        SanhChoBoss sanhHienTai = QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
        if (sanhHienTai != null) {
            if (sanhHienTai == sanh) {
                DebugSanhBoss.log("DA_O_TRONG_SANH", nguoiChoi,
                        "guiLaiGiaoDien P4-" + maBan);
                GoiTinSanhChoBoss.guiMoSanh(nguoiChoi, sanh);
                return true;
            }
            DebugSanhBoss.log("TU_CHOI_VAO_SANH", nguoiChoi,
                    "lyDo=dangTrongSanhBossKhac");
            nguoiChoi.startOKDlg2("Bạn đang ở trong phòng boss khác.");
            return false;
        }

        if (!sanh.kiemTraMatKhau(matKhauNhap)) {
            DebugSanhBoss.log("TU_CHOI_VAO_SANH", nguoiChoi,
                    "lyDo=saiMatKhau P4-" + maBan);
            GoiTinSanhChoBoss.guiYeuCauNhapMatKhau(nguoiChoi);
            return false;
        }

        ThanhVienBoss thanhVienMoi;
        synchronized (sanh) {
            if (!sanh.isDangCho()) {
                DebugSanhBoss.log("TU_CHOI_VAO_SANH", nguoiChoi,
                        "lyDo=phongDaBatDau trangThai=" + sanh.getTrangThai());
                nguoiChoi.startOKDlg2("Phòng boss đã bắt đầu.");
                return false;
            }
            if (sanh.getSoNguoi() >= (sanh.getToiDa() & 0xFF)) {
                DebugSanhBoss.log("TU_CHOI_VAO_SANH", nguoiChoi,
                        "lyDo=phongDay soNguoi=" + sanh.getSoNguoi());
                nguoiChoi.startOKDlg2("Phòng boss đã đủ người.");
                return false;
            }
            int ghe = sanh.layGheTrongDauTien();
            if (ghe < 0) {
                DebugSanhBoss.log("TU_CHOI_VAO_SANH", nguoiChoi,
                        "lyDo=khongConGheTrong");
                nguoiChoi.startOKDlg2("Phòng boss đã đủ người.");
                return false;
            }
            boolean laChuPhong = sanh.getChuPhong() == null;
            thanhVienMoi = new ThanhVienBoss(
                    nguoiChoi,
                    (byte) ghe,
                    QuanLySanhChoBoss.taoThuTuVao(),
                    laChuPhong
            );
            if (!sanh.themThanhVien(thanhVienMoi)) {
                DebugSanhBoss.log("TU_CHOI_VAO_SANH", nguoiChoi,
                        "lyDo=themThanhVienThatBai ghe=" + ghe);
                nguoiChoi.startOKDlg2("Không thể xếp ghế trong phòng boss.");
                return false;
            }
            /*
             * Phòng boss là một scene riêng. Tháo người chơi khỏi khu RPG trước
             * khi dùng chiSo làm số ghế; nếu giữ cả hai trạng thái thì lúc thoát
             * không thể dựng lại GameScrRPG và còn có nguy cơ xóa nhầm slot RPG.
             */
            ChickenBanDoRPG.roi(nguoiChoi);
            nguoiChoi.chiSo = ghe;
            nguoiChoi.pointSeat = (byte) ghe;
            nguoiChoi.isReady = false;
            QuanLySanhChoBoss.ganNguoiChoi(nguoiChoi, sanh);
            DebugSanhBoss.log("XEP_GHE_THANH_CONG", nguoiChoi,
                    "P4-" + maBan
                    + " ghe=" + ghe
                    + " laChuPhong=" + laChuPhong
                    + " soNguoi=" + sanh.getSoNguoi());
        }

        try {
            GoiTinSanhChoBoss.guiMoSanh(nguoiChoi, sanh);
            GoiTinSanhChoBoss.guiThanhVienMoiChoNguoiConLai(sanh, thanhVienMoi);
            DebugSanhBoss.log("VAO_SANH_THANH_CONG", nguoiChoi,
                    "P4-" + maBan + " ghe=" + (thanhVienMoi.getGhe() & 0xFF));
            return true;
        } catch (IOException ex) {
            DebugSanhBoss.log("LOI_GUI_PACKET_SANH", nguoiChoi,
                    "P4-" + maBan
                    + " loi=" + ex.getClass().getSimpleName()
                    + ":" + ex.getMessage());
            RoiSanhChoBoss.xuLyNgatKetNoi(nguoiChoi);
            throw ex;
        }
    }
}
