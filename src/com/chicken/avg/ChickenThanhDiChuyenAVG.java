package com.chicken.avg;

import com.chicken.chiso.ChickenChiSoNguoiChoi;
import com.chicken.mohinh.ChickenNguoiChoi;

/**
 * Quản lý giới hạn di chuyển trong một lượt chiến đấu.
 *
 * Client gốc đã có sẵn thanh di chuyển. Giá trị tối đa được gửi trong packet
 * bắt đầu trận (CMD 20). Server vẫn tự tính lại quãng đường để không phụ thuộc
 * hoàn toàn vào phần hiển thị hoặc giới hạn phía client.
 *
 * Quy ước hiện tại:
 * - mọi nhân vật có 100 px thể lực cơ bản;
 * - option 26 trên bộ trang bị/AVG cộng phần trăm cự ly tương ứng;
 * - chỉ packet di chuyển chủ động mới trừ thanh;
 * - rơi, bị đẩy, dịch chuyển skill và đồng bộ tọa độ không trừ thanh.
 */
public final class ChickenThanhDiChuyenAVG {

    public static final int QUANG_DUONG_CO_BAN_PX = 100;

    private ChickenThanhDiChuyenAVG() {
    }

    /** Chuyển option 26 thành giới hạn thể lực, có chặn tràn số. */
    public static int quangDuongToiDaTuPhanTram(int phanTramCongThem) {
        long phanTram = Math.max(0L, phanTramCongThem);
        long congThem = (long) QUANG_DUONG_CO_BAN_PX * phanTram / 100L;
        // CMD 20 truyền giá trị này bằng signed short; không để tràn thành số âm ở client.
        return (int) Math.min(Short.MAX_VALUE,
                (long) QUANG_DUONG_CO_BAN_PX + congThem);
    }

    /** Tính từ bộ trang bị thật; client không được khai option hoặc thể lực. */
    public static int quangDuongToiDa(ChickenNguoiChoi nguoiChoi) {
        return quangDuongToiDaTuPhanTram(
                ChickenChiSoNguoiChoi.tinhPhanTramCuLyDiChuyen(nguoiChoi));
    }

    /** Dành cho bot không có inventory nhưng có bộ AVG do server cấp. */
    public static int quangDuongToiDaTheoAvenger(byte avenger) {
        return quangDuongToiDaTuPhanTram(
                ChickenChiSoNguoiChoi.tinhPhanTramCuLyDiChuyenTheoAvenger(avenger));
    }

    /** Hồi đầy theo mức tối đa đã chốt lúc bắt đầu trận. */
    public static int hoiDay(int quangDuongToiDa) {
        return Math.max(0, quangDuongToiDa);
    }

    /**
     * Client tính thanh theo số bước điều khiển, không cộng đôi khi đi chéo.
     * Khoảng cách Chebyshev khớp cách đó: mỗi bước có thể đổi cả X và Y nhưng
     * chỉ tiêu hao một đơn vị.
     */
    public static int tinhQuangDuong(
            short xCu,
            short yCu,
            short xMoi,
            short yMoi
    ) {
        int dx = Math.abs((int) xMoi - xCu);
        int dy = Math.abs((int) yMoi - yCu);
        return Math.max(dx, dy);
    }

    /**
     * Chấp nhận toàn bộ tọa độ mới khi còn đủ thanh; nếu không, cắt điểm đến
     * tại đúng phần quãng đường còn lại trên đoạn từ vị trí cũ tới vị trí mới.
     */
    public static KetQuaDiChuyen gioiHan(
            short xCu,
            short yCu,
            short xYeuCau,
            short yYeuCau,
            int quangDuongConLai
    ) {
        int conLai = Math.max(0, quangDuongConLai);
        int canDi = tinhQuangDuong(xCu, yCu, xYeuCau, yYeuCau);

        if (canDi <= 0) {
            return new KetQuaDiChuyen(xCu, yCu, conLai, 0, true);
        }
        if (conLai <= 0) {
            return new KetQuaDiChuyen(xCu, yCu, 0, 0, false);
        }
        if (canDi <= conLai) {
            return new KetQuaDiChuyen(
                    xYeuCau,
                    yYeuCau,
                    conLai - canDi,
                    canDi,
                    true
            );
        }

        int dx = (int) xYeuCau - xCu;
        int dy = (int) yYeuCau - yCu;
        short xDuocPhep = (short) (xCu
                + Math.round((float) dx * conLai / canDi));
        short yDuocPhep = (short) (yCu
                + Math.round((float) dy * conLai / canDi));

        int daDung = Math.min(
                conLai,
                tinhQuangDuong(xCu, yCu, xDuocPhep, yDuocPhep)
        );
        return new KetQuaDiChuyen(
                xDuocPhep,
                yDuocPhep,
                Math.max(0, conLai - daDung),
                daDung,
                false
        );
    }

    public static final class KetQuaDiChuyen {
        private final short x;
        private final short y;
        private final int conLai;
        private final int daDung;
        private final boolean chapNhanToanBo;

        private KetQuaDiChuyen(
                short x,
                short y,
                int conLai,
                int daDung,
                boolean chapNhanToanBo
        ) {
            this.x = x;
            this.y = y;
            this.conLai = conLai;
            this.daDung = daDung;
            this.chapNhanToanBo = chapNhanToanBo;
        }

        public short getX() {
            return this.x;
        }

        public short getY() {
            return this.y;
        }

        public int getConLai() {
            return this.conLai;
        }

        public int getDaDung() {
            return this.daDung;
        }

        public boolean isChapNhanToanBo() {
            return this.chapNhanToanBo;
        }
    }
}
