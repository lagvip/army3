package com.chicken.phong.boss.trandau;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.phong.boss.sanhcho.ChickenKinhTeBoss;
import com.chicken.phong.boss.sanhcho.DebugSanhBoss;
import com.chicken.phong.boss.sanhcho.ThanhVienBoss;
import java.io.IOException;

/**
 * Luật kết thúc và phát thưởng dùng chung cho mọi trận boss.
 *
 * Client hiểu CMD 50: 0 = thua, 1 = thắng, 2 = hòa. Trận boss không có hòa;
 * khi hai phe cùng hết HP thì phía người chơi luôn thua.
 */
public final class ChickenKetQuaTranBoss {
    public static final byte CLIENT_NGUOI_CHOI_THUA = 0;
    public static final byte CLIENT_NGUOI_CHOI_THANG = 1;

    /**
     * Cho server áp dụng hết sát thương của cùng một đòn nhiều va chạm trước
     * khi chốt người chơi thắng.
     */
    public static final long TRE_XAC_NHAN_THANG_MS = 250L;

    public enum KetQua {
        CHUA_KET_THUC,
        NGUOI_CHOI_THANG,
        NGUOI_CHOI_THUA
    }

    public static final class PhanThuong {
        public static final PhanThuong KHONG_CO =
                new PhanThuong(0, 0);
        public final int exp;
        public final int vang;

        private PhanThuong(int exp, int vang) {
            this.exp = Math.max(0, exp);
            this.vang = Math.max(0, vang);
        }
    }

    private ChickenKetQuaTranBoss() {
    }

    public static KetQua danhGia(int soNguoiChoiSong, int soBossSong) {
        // Ưu tiên người chơi: cùng chết luôn là thua, không phải hòa/thắng.
        if (soNguoiChoiSong <= 0) {
            return KetQua.NGUOI_CHOI_THUA;
        }
        if (soBossSong <= 0) {
            return KetQua.NGUOI_CHOI_THANG;
        }
        return KetQua.CHUA_KET_THUC;
    }

    public static int layExpHaBoss(int soBossSong) {
        if (soBossSong > 0) {
            return 0;
        }
        // CMD 50 dùng signed short cho EXP.
        return Math.max(0, Math.min(
                Short.MAX_VALUE, ChickenQuanLyMayChu.bossExpReward));
    }

    /**
     * Vé thành viên là nguồn xác thực cho cả EXP và vàng. Hai phần thưởng đều
     * được giao dịch phía server và không lấy giá trị từ packet client.
     */
    public static PhanThuong traoThuong(
            ThanhVienBoss thanhVien,
            int expHaBoss,
            boolean nguoiChoiThang
    ) {
        if (thanhVien == null || thanhVien.getNguoiChoi() == null
                || thanhVien.isDaNgatKetNoi()) {
            return PhanThuong.KHONG_CO;
        }
        int expNhan = ChickenKinhTeBoss.traoExpHaBoss(
                thanhVien, expHaBoss);
        int vangNhan = nguoiChoiThang
                ? ChickenKinhTeBoss.traoThuongThang(thanhVien)
                : 0;
        return new PhanThuong(expNhan, vangNhan);
    }

    /**
     * Cô lập toàn bộ lỗi theo từng người: một tài khoản/connection lỗi không
     * được phép chặn kết quả và phần thưởng của đồng đội phía sau.
     */
    public static PhanThuong traoThuongVaGuiKetQua(
            ThanhVienBoss thanhVien,
            int expHaBoss,
            boolean nguoiChoiThang
    ) {
        if (thanhVien == null || thanhVien.getNguoiChoi() == null) {
            return PhanThuong.KHONG_CO;
        }
        ChickenNguoiChoi nguoiChoi = thanhVien.getNguoiChoi();
        if (thanhVien.isDaNgatKetNoi()) {
            DebugSanhBoss.log("BO_THUONG_THANH_VIEN_NGAT_KET_NOI",
                    nguoiChoi, "phiDaThu=" + thanhVien.getPhiDaThu());
            return PhanThuong.KHONG_CO;
        }
        int expNhan = 0;
        int vangNhan = 0;
        try {
            expNhan = ChickenKinhTeBoss.traoExpHaBoss(
                    thanhVien, expHaBoss);
        } catch (RuntimeException loi) {
            DebugSanhBoss.log("LOI_THUONG_EXP_KET_THUC",
                    nguoiChoi, "loi=" + loi.getClass().getSimpleName());
        }
        if (nguoiChoiThang) {
            try {
                vangNhan = ChickenKinhTeBoss.traoThuongThang(
                        thanhVien);
            } catch (RuntimeException loi) {
                DebugSanhBoss.log("LOI_THUONG_VANG_KET_THUC",
                        nguoiChoi, "loi=" + loi.getClass().getSimpleName());
            }
        }

        if (nguoiChoi.dichVu != null) {
            try {
                int vangHienThi = nguoiChoiThang
                        ? vangNhan
                        : -Math.max(0, thanhVien.getPhiDaThu());
                /*
                 * InfoDlg cua bat ky skill AVG nao (menu generic CMD -47
                 * hoac skill native CMD -91) co the van mo khi don cuoi ket
                 * thuc tran. Dong no truoc CMD 50 de client khong mang hop
                 * "Ky nang dac biet" ve sanh cho boss, dong thoi khong dong
                 * nham bang ket qua vua duoc mo.
                 */
                nguoiChoi.dichVu.guiDongMenuKyNangDacBiet();
                nguoiChoi.dichVu.guiKetThucDau(
                        nguoiChoiThang
                                ? CLIENT_NGUOI_CHOI_THANG
                                : CLIENT_NGUOI_CHOI_THUA,
                        expNhan, vangHienThi, 0);
                /*
                 * Không gửi CMD 3 (guiThongTin) ngay sau CMD 50. Client PC dùng
                 * CMD 3 để khởi tạo lại người chơi/màn hình, nên nó sẽ đóng màn
                 * kết quả và chuyển thẳng sang loading trước khi bảng thưởng
                 * kịp hiện. Vàng/EXP đã được server cộng nguyên tử; CMD 50 dùng
                 * để hiển thị phần thưởng, còn CMD 3 sẽ được gửi khi người chơi
                 * thực sự rời phòng boss.
                 */
            } catch (IOException | RuntimeException loi) {
                DebugSanhBoss.log("LOI_GUI_KET_THUC",
                        nguoiChoi, "loi=" + loi.getClass().getSimpleName());
            }
        }
        return new PhanThuong(expNhan, vangNhan);
    }
}
