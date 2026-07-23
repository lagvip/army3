package com.chicken.avg;

import com.chicken.mohinh.ChickenNguoiChoi;

/**
 * Quản lý toàn bộ năng lượng AVG.
 *
 * Quy tắc hiện tại:
 * - Năng lượng tối đa: 100.
 * - Chỉ khi đang mặc AVG (avenger != 0) mới tiêu hao năng lượng.
 * - Tạm thời tắt giới hạn năng lượng: mỗi trận tiêu hao 0 điểm.
 * - Thắng một boss luyện tập hồi 5 điểm, kể cả khi trận đó không dùng AVG.
 * - Khi cần bật lại, đổi TIEU_HAO_MOI_TRAN từ 0 về 5.
 * - Hết năng lượng không tự tháo AVG ở sảnh.
 */
public final class ChickenQuanLyNangLuongAVG {

    public static final int NANG_LUONG_TOI_DA = 100;
    /** Bản local: thanh năng lượng AVG luôn đầy, không có cơ chế tiêu hao. */
    public static final boolean LUON_DAY_NANG_LUONG = true;
    // Tạm tắt chặn vào trận do hết năng lượng AVG. Đổi lại 5 để bật.
    public static final int TIEU_HAO_MOI_TRAN = 0;
    public static final int HOI_KHI_THANG_BOSS = 5;

    private ChickenQuanLyNangLuongAVG() {
    }

    public static int layNangLuong(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return 0;
        }
        if (LUON_DAY_NANG_LUONG) {
            nguoiChoi.powerAvenger = (byte) NANG_LUONG_TOI_DA;
            return NANG_LUONG_TOI_DA;
        }
        return gioiHan(nguoiChoi.powerAvenger & 0xFF);
    }

    public static byte chuanHoaGiaTri(int giaTri) {
        if (LUON_DAY_NANG_LUONG) {
            return (byte) NANG_LUONG_TOI_DA;
        }
        return (byte) gioiHan(giaTri);
    }

    public static void chuanHoaSauKhiTai(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return;
        }
        nguoiChoi.powerAvenger = chuanHoaGiaTri(nguoiChoi.powerAvenger & 0xFF);
    }

    public static boolean dangSuDungAVG(ChickenNguoiChoi nguoiChoi) {
        return nguoiChoi != null && nguoiChoi.avenger != 0;
    }

    /**
     * Không mặc AVG thì luôn được vào trận, kể cả năng lượng đang bằng 0.
     */
    public static boolean coDuNangLuongDeVaoTran(ChickenNguoiChoi nguoiChoi) {
        return !dangSuDungAVG(nguoiChoi)
                || layNangLuong(nguoiChoi) >= TIEU_HAO_MOI_TRAN;
    }

    public static boolean kiemTraChoVaoTran(
            ChickenNguoiChoi nguoiChoi,
            String tenCheDo
    ) {
        if (coDuNangLuongDeVaoTran(nguoiChoi)) {
            return true;
        }

        if (nguoiChoi != null) {
            nguoiChoi.startOKDlg2(
                    "Năng lượng AVG không đủ để vào " + tenCheDo
                    + ". Cần ít nhất " + TIEU_HAO_MOI_TRAN
                    + " điểm. Hãy tháo AVG và thắng boss để hồi năng lượng."
            );
        }
        return false;
    }

    /**
     * Trừ năng lượng cho một người chơi khi trận thật sự bắt đầu.
     * Không mặc AVG thì không bị trừ.
     */
    public static boolean tieuHaoKhiBatDauTran(
            ChickenNguoiChoi nguoiChoi,
            String tenCheDo
    ) {
        if (nguoiChoi == null || !dangSuDungAVG(nguoiChoi)) {
            return true;
        }

        synchronized (nguoiChoi) {
            int hienTai = layNangLuong(nguoiChoi);
            if (hienTai < TIEU_HAO_MOI_TRAN) {
                nguoiChoi.startOKDlg2(
                        "Năng lượng AVG không đủ để vào " + tenCheDo
                        + ". Cần ít nhất " + TIEU_HAO_MOI_TRAN + " điểm."
                );
                return false;
            }
            nguoiChoi.powerAvenger = chuanHoaGiaTri(
                    hienTai - TIEU_HAO_MOI_TRAN
            );
        }

        capNhatSauThayDoi(nguoiChoi);
        return true;
    }

    /**
     * Kiểm tra toàn bộ người chơi trước, sau đó mới trừ đồng loạt.
     * Nhờ vậy không có trường hợp người đầu đã bị trừ nhưng trận bị hủy
     * vì một người khác không đủ năng lượng.
     */
    public static boolean tieuHaoKhiBatDauTran(
            ChickenNguoiChoi[] danhSach,
            String tenCheDo
    ) {
        if (danhSach == null) {
            return true;
        }

        synchronized (ChickenQuanLyNangLuongAVG.class) {
            for (ChickenNguoiChoi nguoiChoi : danhSach) {
                if (nguoiChoi != null
                        && dangSuDungAVG(nguoiChoi)
                        && layNangLuong(nguoiChoi) < TIEU_HAO_MOI_TRAN) {
                    nguoiChoi.startOKDlg2(
                            "Năng lượng AVG không đủ để vào " + tenCheDo
                            + ". Cần ít nhất " + TIEU_HAO_MOI_TRAN + " điểm."
                    );
                    return false;
                }
            }

            for (ChickenNguoiChoi nguoiChoi : danhSach) {
                if (nguoiChoi == null || !dangSuDungAVG(nguoiChoi)) {
                    continue;
                }
                int hienTai = layNangLuong(nguoiChoi);
                nguoiChoi.powerAvenger = chuanHoaGiaTri(
                        hienTai - TIEU_HAO_MOI_TRAN
                );
            }
        }

        for (ChickenNguoiChoi nguoiChoi : danhSach) {
            if (nguoiChoi != null && dangSuDungAVG(nguoiChoi)) {
                capNhatSauThayDoi(nguoiChoi);
            }
        }
        return true;
    }

    /**
     * Thắng boss luôn hồi 5 điểm. Nếu trận dùng AVG thì trước đó đã trừ 5,
     * nên tổng thay đổi của trận là 0. Nếu chỉ dùng súng thường thì được +5.
     */
    public static int hoiKhiThangBoss(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return 0;
        }

        int sauKhiHoi;
        synchronized (nguoiChoi) {
            int hienTai = layNangLuong(nguoiChoi);
            sauKhiHoi = gioiHan(hienTai + HOI_KHI_THANG_BOSS);
            nguoiChoi.powerAvenger = (byte) sauKhiHoi;
        }

        capNhatSauThayDoi(nguoiChoi);
        return sauKhiHoi;
    }

    private static int gioiHan(int giaTri) {
        return Math.max(0, Math.min(NANG_LUONG_TOI_DA, giaTri));
    }

    private static void capNhatSauThayDoi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return;
        }
        if (nguoiChoi.dichVu != null) {
            nguoiChoi.dichVu.capNhatAvenger();
        }
        nguoiChoi.flushCache();
    }
}
