package com.chicken.luyentap;

import com.chicken.bando.ChickenQuanLyBanDo;

/**
 * Chỉ xử lý tọa độ luyện tập: đầu nòng, nền dưới chân và mặt đất thấp hơn.
 * Không xử lý bắn, ảnh đạn, máu hoặc chuyển lượt.
 */
public final class ChickenLuyenTapToaDo {
    private static final int LECH_CHAN_X = 8;
    private static final int BAN_KINH_CHAN_X = 2;
    private static final int DO_LECH_NEN_HAI_CHAN_TOI_DA = 8;

    @FunctionalInterface
    public interface KiemTraThanThongThoang {
        boolean kiemTra(short x, short footY);
    }

    private ChickenLuyenTapToaDo() {
    }

    public static short[] layDiemDauNong(
            ChickenQuanLyBanDo map,
            short trucSungX,
            short trucSungY,
            short goc,
            int doDaiNong
    ) {
        int gocChuan = goc % 360;
        if (gocChuan < 0) {
            gocChuan += 360;
        }
        double rad = Math.toRadians(gocChuan);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        int doDai = Math.max(0, doDaiNong);

        for (int khoangCach = doDai; khoangCach >= 0; khoangCach -= 2) {
            short x = kepShort(
                    (int)Math.round(trucSungX + cos * khoangCach),
                    0, map.getWidth());
            short y = kepShort(
                    (int)Math.round(trucSungY - sin * khoangCach),
                    0, map.getHeight());
            if (!map.coVaCham(x, y)) {
                return new short[]{x, y};
            }
        }
        return new short[]{
            kepShort(trucSungX, 0, map.getWidth()),
            kepShort(trucSungY, 0, map.getHeight())
        };
    }

    /** Ép điểm đầu của từng quỹ đạo trùng chính xác đầu nòng súng. */
    public static void datDiemDauTaiDauNong(
            short[][] cacDuongX,
            short[][] cacDuongY,
            short muzzleX,
            short muzzleY
    ) {
        int soVien = Math.min(
                cacDuongX == null ? 0 : cacDuongX.length,
                cacDuongY == null ? 0 : cacDuongY.length
        );
        for (int i = 0; i < soVien; i++) {
            if (cacDuongX[i] != null && cacDuongY[i] != null
                    && cacDuongX[i].length > 0 && cacDuongY[i].length > 0) {
                cacDuongX[i][0] = muzzleX;
                cacDuongY[i][0] = muzzleY;
            }
        }
    }

    public static boolean coNenDoDuoiHaiChan(
            ChickenQuanLyBanDo map,
            short x,
            short footY
    ) {
        int nenGiua = timPixelNenGanNhat(map, x, footY, footY + 4);
        int nenTrai = timPixelNenGanNhat(
                map, x - LECH_CHAN_X, footY, footY + 4);
        int nenPhai = timPixelNenGanNhat(
                map, x + LECH_CHAN_X, footY, footY + 4);

        // Mép map hai bên không được giữ boss lơ lửng trên lỗ. Phải còn nền
        // ngay dưới tâm chân và ít nhất một bên chân có nền cùng độ cao.
        if (nenGiua < 0) {
            return false;
        }
        boolean chanTraiHopLe = nenTrai >= 0
                && Math.abs(nenGiua - nenTrai) <= DO_LECH_NEN_HAI_CHAN_TOI_DA;
        boolean chanPhaiHopLe = nenPhai >= 0
                && Math.abs(nenGiua - nenPhai) <= DO_LECH_NEN_HAI_CHAN_TOI_DA;
        return chanTraiHopLe || chanPhaiHopLe;
    }

    /**
     * Tìm mặt nền thấp hơn gần nhất. Hai chân không cần chạm pixel map đúng
     * cùng một hàng; chấp nhận mặt nền lệch tối đa 8 px để boss vẫn đáp được
     * trên địa hình bị khoét hoặc hơi dốc.
     */
    public static short timMatDatTaiHoacThapHon(
            ChickenQuanLyBanDo map,
            short x,
            short footY,
            KiemTraThanThongThoang kiemTraThan
    ) {
        // Nếu Y hiện tại vẫn có nền thật sự dưới tâm chân thì giữ nguyên.
        if (coNenDoDuoiHaiChan(map, x, footY)
                && kiemTraThan.kiemTra(x, footY)) {
            return footY;
        }

        // Khi tâm chân đã thủng, tuyệt đối không dùng lại Y cũ. Chỉ quét các
        // mặt nền nằm thấp hơn và phải là mép trên của địa hình, không phải
        // pixel nằm bên trong khối băng.
        int batDauY = Math.max(0, footY + 1);
        for (int py = batDauY; py < map.getHeight(); py++) {
            if (!laMatNenHopLe(map, x, py)) {
                continue;
            }
            short yDung = (short) py;
            if (kiemTraThan.kiemTra(x, yDung)) {
                return yDung;
            }
        }
        return Short.MIN_VALUE;
    }

    private static boolean laMatNenHopLe(
            ChickenQuanLyBanDo map,
            short x,
            int footY
    ) {
        if (footY <= 0 || footY >= map.getHeight()) {
            return false;
        }

        int nenGiua = timPixelNenGanNhat(map, x, footY, footY);
        if (nenGiua < 0) {
            return false;
        }

        // Đây phải là mép trên của nền: pixel ngay phía trên tâm chân phải trống.
        if (map.coVaCham(x, (short) (footY - 1))) {
            return false;
        }

        int nenTrai = timPixelNenGanNhat(
                map, x - LECH_CHAN_X, footY,
                Math.min(map.getHeight() - 1, footY + DO_LECH_NEN_HAI_CHAN_TOI_DA));
        int nenPhai = timPixelNenGanNhat(
                map, x + LECH_CHAN_X, footY,
                Math.min(map.getHeight() - 1, footY + DO_LECH_NEN_HAI_CHAN_TOI_DA));

        boolean chanTraiHopLe = nenTrai >= 0
                && Math.abs(footY - nenTrai) <= DO_LECH_NEN_HAI_CHAN_TOI_DA;
        boolean chanPhaiHopLe = nenPhai >= 0
                && Math.abs(footY - nenPhai) <= DO_LECH_NEN_HAI_CHAN_TOI_DA;
        return chanTraiHopLe || chanPhaiHopLe;
    }



    /** Giữ tương thích cho các chỗ cũ chỉ muốn tìm nền thấp hơn. */
    public static short timMatDatThapHon(
            ChickenQuanLyBanDo map,
            short x,
            short footY,
            KiemTraThanThongThoang kiemTraThan
    ) {
        short y = timMatDatTaiHoacThapHon(map, x, footY, kiemTraThan);
        return y > footY ? y : Short.MIN_VALUE;
    }

    private static int timPixelNenGanNhat(
            ChickenQuanLyBanDo map,
            int tamChanX,
            int tuY,
            int denY
    ) {
        int batDauY = Math.max(0, tuY);
        int ketThucY = Math.min(map.getHeight() - 1, denY);
        int batDauX = Math.max(0, tamChanX - BAN_KINH_CHAN_X);
        int ketThucX = Math.min(map.getWidth() - 1, tamChanX + BAN_KINH_CHAN_X);
        for (int py = batDauY; py <= ketThucY; py++) {
            for (int px = batDauX; px <= ketThucX; px++) {
                if (map.coVaCham((short)px, (short)py)) {
                    return py;
                }
            }
        }
        return -1;
    }

    private static short kepShort(int giaTri, int min, int max) {
        return (short)Math.max(min, Math.min(max, giaTri));
    }
}
