package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;
import java.io.IOException;

/** Xử lý nút Đặt mật khẩu native CMD 18 trong sảnh boss. */
public final class MatKhauBoss {
    private static final int DO_DAI_TOI_DA = 20;

    private MatKhauBoss() {
    }

    public static boolean xuLy(ChickenNguoiChoi nguoiChoi, String matKhauMoi)
            throws IOException {
        SanhChoBoss sanh = QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
        if (sanh == null || nguoiChoi == null) {
            return false;
        }

        String matKhau = matKhauMoi == null ? "" : matKhauMoi.trim();
        if (matKhau.length() > DO_DAI_TOI_DA) {
            nguoiChoi.startOKDlg2("Mật khẩu phòng tối đa 20 ký tự.");
            return false;
        }

        boolean daDat;
        synchronized (sanh) {
            ThanhVienBoss thanhVien = sanh.timThanhVien(nguoiChoi);
            if (thanhVien == null || !thanhVien.isChuPhong()) {
                DebugSanhBoss.log("TU_CHOI_DAT_MAT_KHAU", nguoiChoi,
                        "P4-" + (sanh.getMaBan() & 0xFF)
                        + " lyDo=khongPhaiChuPhong");
                nguoiChoi.startOKDlg2("Chỉ chủ phòng mới được đặt mật khẩu.");
                return false;
            }
            if (!sanh.isDangCho()) {
                nguoiChoi.startOKDlg2("Phòng boss đã bắt đầu.");
                return false;
            }
            sanh.setMatKhau(matKhau);
            daDat = sanh.coMatKhau();
        }

        DebugSanhBoss.log(daDat ? "DAT_MAT_KHAU_THANH_CONG" : "XOA_MAT_KHAU_THANH_CONG",
                nguoiChoi,
                "P4-" + (sanh.getMaBan() & 0xFF)
                + " doDai=" + matKhau.length());
        nguoiChoi.startOKDlg2(daDat
                ? "Đã đặt mật khẩu phòng boss."
                : "Đã xóa mật khẩu phòng boss.");
        return true;
    }
}
