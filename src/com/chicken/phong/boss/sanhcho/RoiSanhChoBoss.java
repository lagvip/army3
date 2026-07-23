package com.chicken.phong.boss.sanhcho;

import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.phong.ChickenQuanLyPhong;
import java.io.IOException;

public final class RoiSanhChoBoss {
    private RoiSanhChoBoss() {
    }

    public static void xuLy(ChickenNguoiChoi nguoiChoi) {
        xuLyNoiBo(nguoiChoi, true);
    }

    public static void xuLyNgatKetNoi(ChickenNguoiChoi nguoiChoi) {
        xuLyNoiBo(nguoiChoi, false);
    }

    private static void xuLyNoiBo(ChickenNguoiChoi nguoiChoi, boolean guiLaiDanhSach) {
        if (nguoiChoi == null) {
            return;
        }
        ChickenQuanLyChien tranDangChien =
                ChickenQuanLyChien.timTranDauCuaNguoiChoi(nguoiChoi);
        if (tranDangChien != null) {
            tranDangChien.khiNguoiChoiRoi(nguoiChoi);
        }
        SanhChoBoss sanh = QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
        if (sanh == null) {
            return;
        }

        boolean canChuyenChu;
        int maNguoiRoi = nguoiChoi.ma;
        synchronized (sanh) {
            ThanhVienBoss thanhVien = sanh.timThanhVien(nguoiChoi);
            if (thanhVien == null) {
                QuanLySanhChoBoss.boGanNguoiChoi(nguoiChoi);
                return;
            }
            canChuyenChu = thanhVien.isChuPhong();
            sanh.xoaThanhVien(nguoiChoi);
            QuanLySanhChoBoss.boGanNguoiChoi(nguoiChoi);
            nguoiChoi.isReady = false;
            nguoiChoi.chiSo = -1;
            nguoiChoi.pointSeat = 0;
            if (sanh.getSoNguoi() == 0) {
                sanh.reset();
                canChuyenChu = false;
            }
        }

        ThanhVienBoss chuMoi = null;
        if (canChuyenChu) {
            chuMoi = ChuyenChuPhongBoss.chuyen(sanh);
        }
        int maChuMoi = chuMoi != null && chuMoi.getNguoiChoi() != null
                ? chuMoi.getNguoiChoi().ma
                : sanh.getChuPhong() != null
                ? sanh.getChuPhong().getNguoiChoi().ma
                : -1;

        for (ThanhVienBoss thanhVien : sanh.chupThanhVien()) {
            if (thanhVien == null) {
                continue;
            }
            try {
                GoiTinSanhChoBoss.guiNguoiChoiRoi(
                        thanhVien.getNguoiChoi(),
                        maNguoiRoi,
                        maChuMoi
                );
            } catch (IOException ignored) {
            }
        }
        try {
            GoiTinSanhChoBoss.guiNguoiChoiRoi(nguoiChoi, maNguoiRoi, maChuMoi);
        } catch (IOException ignored) {
        }
        if (canChuyenChu && sanh.isDangCho()) {
            try {
                GoiTinSanhChoBoss.guiCapNhatChuPhong(sanh);
            } catch (IOException ignored) {
            }
        }
        if (guiLaiDanhSach && nguoiChoi.dichVu != null) {
            try {
                ChickenQuanLyPhong.yeuCauDanhSachPhong(nguoiChoi);
            } catch (IOException ignored) {
            }
        }
    }
}
