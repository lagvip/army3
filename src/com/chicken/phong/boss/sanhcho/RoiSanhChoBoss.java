package com.chicken.phong.boss.sanhcho;

import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.nhapvai.ChickenBanDoRPG;
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

    private static void xuLyNoiBo(ChickenNguoiChoi nguoiChoi, boolean traVeSanhRpg) {
        if (nguoiChoi == null) {
            return;
        }
        SanhChoBoss sanh = QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
        if (sanh == null) {
            return;
        }

        ChickenQuanLyChien tranDangChien =
                ChickenQuanLyChien.timTranDauCuaNguoiChoi(nguoiChoi);

        boolean canChuyenChu;
        boolean phongDaRong;
        int maNguoiRoi = nguoiChoi.ma;
        synchronized (sanh) {
            ThanhVienBoss thanhVien = sanh.timThanhVien(nguoiChoi);
            if (thanhVien == null) {
                QuanLySanhChoBoss.boGanNguoiChoi(nguoiChoi);
                return;
            }
            boolean giuVeDenKetQua = !traVeSanhRpg
                    && tranDangChien != null
                    && (sanh.getTrangThai() == SanhChoBoss.TrangThai.DANG_BAT_DAU
                    || sanh.getTrangThai() == SanhChoBoss.TrangThai.DANG_CHIEN);
            canChuyenChu = !giuVeDenKetQua && thanhVien.isChuPhong();
            if (giuVeDenKetQua) {
                thanhVien.danhDauNgatKetNoi();
            } else {
                sanh.xoaThanhVien(nguoiChoi);
            }
            QuanLySanhChoBoss.boGanNguoiChoi(nguoiChoi);
            nguoiChoi.isReady = false;
            nguoiChoi.chiSo = -1;
            nguoiChoi.pointSeat = 0;
            phongDaRong = sanh.getSoNguoi() == 0;
            if (phongDaRong) {
                canChuyenChu = false;
            }
        }

        /*
         * Gỡ trạng thái phòng trước rồi mới báo cho trận đấu. Từ thời điểm này
         * combatant được đánh dấu daRoiTran, nên các task boss chạy chậm không
         * còn gửi packet chiến đấu kéo client trở lại scene cũ.
         */
        if (tranDangChien != null) {
            tranDangChien.khiNguoiChoiRoi(nguoiChoi);
        }
        /*
         * Callback trên có thể đặt phòng thành DA_KET_THUC khi người cuối cùng
         * rời trận. Vì vậy chỉ reset phòng rỗng sau callback; làm ngược thứ tự
         * sẽ khiến callback ghi đè DANG_CHO và phòng biến mất khỏi danh sách.
         */
        if (phongDaRong) {
            synchronized (sanh) {
                if (sanh.getSoNguoi() == 0) {
                    sanh.reset();
                }
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
            if (thanhVien == null || thanhVien.isDaNgatKetNoi()) {
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
        if (traVeSanhRpg) {
            traVeSanhRpg(nguoiChoi);
        }
    }

    private static void traVeSanhRpg(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null || nguoiChoi.dichVu == null) {
            return;
        }
        try {
            /*
             * CMD 3 phải đi trước CMD -98 của chính người chơi. CMD -98 là gói
             * cuối gọi GameScrRPG.show() và đóng màn hình chờ phía client.
             */
            ChickenBanDoRPG.roi(nguoiChoi);
            nguoiChoi.isReady = false;
            nguoiChoi.chiSo = -1;
            nguoiChoi.pointSeat = 0;
            nguoiChoi.x = 100;
            nguoiChoi.y = 360;
            nguoiChoi.dichVu.guiThongTin();
            ChickenBanDoRPG.vao(nguoiChoi);
        } catch (Exception ex) {
            DebugSanhBoss.log("LOI_TRA_VE_SANH_RPG", nguoiChoi,
                    "loi=" + ex.getClass().getSimpleName()
                    + ":" + ex.getMessage());
        }
    }
}
