package com.chicken.avg;

import java.util.ArrayList;
import java.util.List;

/**
 * Công thức bắn riêng của AVG Ultron.
 *
 * - Tia laze đi thẳng từ đầu nòng tới biên bản đồ.
 * - Không chịu ảnh hưởng của gió, trọng lực hoặc lực bắn.
 * - Công thức chỉ tạo đường thẳng; nơi điều khiển trận sẽ cắt tia tại
 *   địa hình hoặc nhân vật đầu tiên.
 * - Kỹ năng Bắn x3 tạo ba tia cùng xuất phát tại đầu nòng, tách nhẹ rồi
 *   hội tụ tại cùng một điểm ở biên map theo góc người chơi đã chọn.
 */
public final class ChickenCongThucBanUltron {

    public static final byte AVG_ULTRON = 8;
    public static final short ID_ANH_DAN_ULTRON = 1888;
    private static final double BUOC_TIA = 6.0D;
    private static final double DO_DAI_TACH_TIA = 30.0D;
    private static final double DO_LECH_TIA = 15.0D;

    private ChickenCongThucBanUltron() {
    }

    public static final class DuongTia {
        private final short[] x;
        private final short[] y;

        private DuongTia(short[] x, short[] y) {
            this.x = x;
            this.y = y;
        }

        public short[] getX() {
            return this.x;
        }

        public short[] getY() {
            return this.y;
        }
    }

    public static final class LoatBaTia {
        private final short[][] x;
        private final short[][] y;
        private final short dichX;
        private final short dichY;

        private LoatBaTia(short[][] x, short[][] y, short dichX, short dichY) {
            this.x = x;
            this.y = y;
            this.dichX = dichX;
            this.dichY = dichY;
        }

        public short[][] getX() {
            return this.x;
        }

        public short[][] getY() {
            return this.y;
        }

        public short getDichX() {
            return this.dichX;
        }

        public short getDichY() {
            return this.dichY;
        }
    }

    public static DuongTia taoTiaThang(
            short dauNongX,
            short dauNongY,
            short goc,
            int mapWidth,
            int mapHeight
    ) {
        short[] dich = timDiemBien(dauNongX, dauNongY, goc, mapWidth, mapHeight);
        return taoDoanThang(dauNongX, dauNongY, dich[0], dich[1]);
    }

    public static LoatBaTia taoBaTiaHoiTu(
            short dauNongX,
            short dauNongY,
            short goc,
            int mapWidth,
            int mapHeight
    ) {
        short[] dich = timDiemBien(dauNongX, dauNongY, goc, mapWidth, mapHeight);
        return taoBaTiaHoiTuTaiDiemCuoi(
                dauNongX,
                dauNongY,
                goc,
                dich[0],
                dich[1],
                mapWidth,
                mapHeight
        );
    }

    /**
     * Tạo ba tia cùng hội tụ về đúng một điểm cuối.
     *
     * - Tia giữa đi thẳng từ đầu nòng tới điểm cuối.
     * - Hai tia ngoài chỉ là hiệu ứng: xuất phát lệch nhẹ hai bên và cùng
     *   chụm về một điểm cuối duy nhất.
     *
     * Nơi gọi có thể thay riêng quỹ đạo tia giữa bằng quỹ đạo thật đã cắt map /
     * nhân vật để giữ va chạm chính xác, trong khi hai tia ngoài vẫn chỉ làm
     * hiệu ứng theo đúng kiểu "3 tia chụm 1 điểm" mà người dùng yêu cầu.
     */
    public static LoatBaTia taoBaTiaHoiTuTaiDiemCuoi(
            short dauNongX,
            short dauNongY,
            short goc,
            short dichX,
            short dichY,
            int mapWidth,
            int mapHeight
    ) {
        double rad = Math.toRadians(chuanHoaGoc(goc));
        double huongX = Math.cos(rad);
        double huongY = -Math.sin(rad);
        double vuongX = -huongY;
        double vuongY = huongX;
        int maxX = Math.max(0, mapWidth - 1);
        int maxY = Math.max(0, mapHeight - 1);

        short[][] cacX = new short[3][];
        short[][] cacY = new short[3][];
        double[] lech = new double[]{-DO_LECH_TIA, 0.0D, DO_LECH_TIA};

        for (int i = 0; i < 3; i++) {
            short batDauX;
            short batDauY;
            if (i == 1) {
                batDauX = dauNongX;
                batDauY = dauNongY;
            } else {
                batDauX = kepShort(Math.round(
                        dauNongX + huongX * DO_DAI_TACH_TIA + vuongX * lech[i]
                ), 0, maxX);
                batDauY = kepShort(Math.round(
                        dauNongY + huongY * DO_DAI_TACH_TIA + vuongY * lech[i]
                ), 0, maxY);
            }
            DuongTia tia = taoDoanThang(batDauX, batDauY, dichX, dichY);
            cacX[i] = tia.getX();
            cacY[i] = tia.getY();
        }
        return new LoatBaTia(cacX, cacY, dichX, dichY);
    }

    private static DuongTia taoDoanThang(short x1, short y1, short x2, short y2) {
        double dx = (double) x2 - x1;
        double dy = (double) y2 - y1;
        double doDai = Math.hypot(dx, dy);
        if (doDai < 0.001D) {
            return new DuongTia(new short[]{x1}, new short[]{y1});
        }

        int soBuoc = Math.max(1, (int) Math.ceil(doDai / BUOC_TIA));
        List<Short> xs = new ArrayList<Short>(soBuoc + 1);
        List<Short> ys = new ArrayList<Short>(soBuoc + 1);
        for (int i = 0; i <= soBuoc; i++) {
            double tiLe = (double) i / (double) soBuoc;
            xs.add((short) Math.round(x1 + dx * tiLe));
            ys.add((short) Math.round(y1 + dy * tiLe));
        }
        return new DuongTia(doiMang(xs), doiMang(ys));
    }

    private static short[] timDiemBien(
            short x,
            short y,
            short goc,
            int mapWidth,
            int mapHeight
    ) {
        int width = Math.max(1, mapWidth - 1);
        int height = Math.max(1, mapHeight - 1);
        double rad = Math.toRadians(chuanHoaGoc(goc));
        double dx = Math.cos(rad);
        double dy = -Math.sin(rad);

        double t = Double.POSITIVE_INFINITY;
        if (dx > 0.000001D) {
            t = Math.min(t, (width - x) / dx);
        } else if (dx < -0.000001D) {
            t = Math.min(t, (0 - x) / dx);
        }
        if (dy > 0.000001D) {
            t = Math.min(t, (height - y) / dy);
        } else if (dy < -0.000001D) {
            t = Math.min(t, (0 - y) / dy);
        }
        if (!Double.isFinite(t) || t < 0.0D) {
            t = 0.0D;
        }

        short dichX = kepShort(Math.round(x + dx * t), 0, width);
        short dichY = kepShort(Math.round(y + dy * t), 0, height);
        return new short[]{dichX, dichY};
    }

    private static int chuanHoaGoc(short goc) {
        int value = goc % 360;
        return value < 0 ? value + 360 : value;
    }

    private static short kepShort(long giaTri, int min, int max) {
        long value = Math.max(min, Math.min(max, giaTri));
        return (short) value;
    }

    private static short[] noiKhongLap(short[] dau, short[] sau) {
        if (dau == null || dau.length == 0) {
            return sau == null ? new short[0] : sau;
        }
        if (sau == null || sau.length == 0) {
            return dau;
        }
        int boQua = dau[dau.length - 1] == sau[0] ? 1 : 0;
        short[] ketQua = new short[dau.length + sau.length - boQua];
        System.arraycopy(dau, 0, ketQua, 0, dau.length);
        System.arraycopy(sau, boQua, ketQua, dau.length, sau.length - boQua);
        return ketQua;
    }

    private static short[] doiMang(List<Short> danhSach) {
        short[] ketQua = new short[danhSach.size()];
        for (int i = 0; i < danhSach.size(); i++) {
            ketQua[i] = danhSach.get(i);
        }
        return ketQua;
    }
}
