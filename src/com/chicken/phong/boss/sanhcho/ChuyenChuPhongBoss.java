package com.chicken.phong.boss.sanhcho;

public final class ChuyenChuPhongBoss {
    private ChuyenChuPhongBoss() {
    }

    public static ThanhVienBoss chuyen(SanhChoBoss sanh) {
        if (sanh == null) {
            return null;
        }
        ThanhVienBoss chuMoi = null;
        synchronized (sanh) {
            for (ThanhVienBoss thanhVien : sanh.chupThanhVien()) {
                if (thanhVien == null) {
                    continue;
                }
                if (chuMoi == null
                        || thanhVien.getThuTuVao() < chuMoi.getThuTuVao()) {
                    chuMoi = thanhVien;
                }
            }
            if (chuMoi != null) {
                sanh.datChuPhong(chuMoi);
                chuMoi.getNguoiChoi().isReady = false;
            }
        }
        return chuMoi;
    }
}
