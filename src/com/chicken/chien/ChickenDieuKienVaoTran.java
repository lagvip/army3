package com.chicken.chien;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.vatpham.ChickenVatPham;

/**
 * Điều kiện tiền kiểm dùng chung trước khi tạo mới một trận đấu.
 *
 * <p>Không đọc súng, cấp hay chỉ số từ packet. Tất cả dữ liệu được lấy từ
 * trạng thái người chơi mà server đang giữ.</p>
 */
public final class ChickenDieuKienVaoTran {
    public static final String LOI_SUNG =
            "Bạn cần trang bị một khẩu súng hợp lệ và đủ cấp trước khi chiến đấu.";
    public static final String LOI_DU_LIEU_SUNG =
            "Súng đang trang bị thiếu dữ liệu chiến đấu, vui lòng đổi sang súng khác.";

    private ChickenDieuKienVaoTran() {
    }

    /**
     * @return {@code null} nếu đủ điều kiện; ngược lại là thông báo an toàn
     *         để gửi cho client.
     */
    public static String layLoi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return LOI_SUNG;
        }
        ChickenVatPham sung = nguoiChoi.laySungTrangBiMayChu();
        if (sung == null) {
            return LOI_SUNG;
        }
        if (ChickenQuanLyDanSung.theoSungDangTrangBi(sung) == null
                || !ChickenNapDanServer.coCauHinhNapDanHopLe(sung)
                || nguoiChoi.layTongTanCongHienTai() <= 0) {
            return LOI_DU_LIEU_SUNG;
        }
        return null;
    }
}
