package com.chicken.avg;

/**
 * Chỉ tạo quỹ đạo hiển thị cho kỹ năng Hawk.
 *
 * Skill dùng bulletType 37 để client vẽ sprite hai frame /eff/muiten.png
 * được đóng gói sẵn. Đạn bắn thường của Hawk vẫn dùng bulletType 9 và ảnh
 * Small1879 được server gửi riêng qua BulletForGun.
 */
public final class ChickenHoatAnhHawk {
    public static final byte LOAI_DAN_MUI_TEN = 37;
    public static final byte SO_MUI_TEN = 4;
    public static final byte SO_PHAT_MOT_LOAT = 1;
    public static final byte LUC_HIEN_THI = 30;
    public static final short GOC_BAY_LEN = 90;
    public static final short GOC_LAO_XUONG = 270;

    /** Đợi loạt cuối bay hẳn khỏi mép trên rồi mới tạo loạt rơi xuống. */
    public static final long THOI_GIAN_BAY_LEN_MS = 1650L;

    /** Thời gian từ lúc tạo loạt trên đầu mục tiêu tới lúc mũi đầu chạm thân. */
    public static final long THOI_GIAN_MUI_DAU_CHAM_MUC_TIEU_MS = 360L;

    /** Khoảng cách bốn lần sát thương, khớp nhịp bốn mũi tên nối đuôi. */
    public static final long KHOANG_CACH_MUI_TEN_MS = 110L;

    private static final int BUOC_LAO_XUONG_PX = 12;
    private static final int BUOC_BAY_LEN_PX = 24;

    /** Mép trên của hệ tọa độ map. Loạt rơi xuống bắt đầu từ đây. */
    private static final int Y_DINH_MAP = 0;

    /**
     * Điểm cuối phần quỹ đạo còn nhìn thấy. Sau điểm này tên đã vượt hẳn mép
     * trên màn hình, nhưng chưa kết thúc mảng quỹ đạo để client không tạo hiệu
     * ứng chạm ngay tại đỉnh map.
     */
    private static final int Y_RA_KHOI_MAN_HINH = -256;

    /**
     * Client luôn gọi hiệu ứng kết thúc khi đọc hết mảng quỹ đạo. Vì không có
     * cờ packet riêng để tắt hiệu ứng đó, điểm cuối bắt buộc được đặt sát giới
     * hạn short, hoàn toàn ngoài mọi map. Hiệu ứng kết thúc nếu client tạo ra
     * cũng nằm ở Y này và không thể xuất hiện tại mép trên map.
     */
    private static final short Y_KET_THUC_NGOAI_MAP = (short)-32760;

    /** Các điểm nhảy tiếp tục đi lên sau khi tên đã ra khỏi màn hình. */
    private static final short[] DUONG_THOAT_MAP_Y = new short[]{
        (short)-2048,
        (short)-8192,
        (short)-16384,
        Y_KET_THUC_NGOAI_MAP
    };

    /** Khoảng ba frame giữa hai mũi, gần với nhịp 110 ms của client. */
    private static final int SO_DIEM_CHO_GIUA_HAI_MUI = 3;

    public static final class DuongDan {
        private final short[] x;
        private final short[] y;

        private DuongDan(short[] x, short[] y) {
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

    public static final class LoatDuongDan {
        private final short[][] x;
        private final short[][] y;

        private LoatDuongDan(short[][] x, short[][] y) {
            this.x = x;
            this.y = y;
        }

        public short[][] getX() {
            return this.x;
        }

        public short[][] getY() {
            return this.y;
        }
    }

    private ChickenHoatAnhHawk() {
    }

    /**
     * Tạo quỹ đạo riêng cho pha bay lên, không kết thúc tại mép map.
     *
     * Phần đầu đi thẳng và mượt từ đầu nòng tới ngoài màn hình. Sau đó giữ
     * nguyên X, tiếp tục đẩy Y tới vùng cực xa ngoài map. Client chỉ phát hiệu
     * ứng kết thúc ở phần tử cuối, nên không còn điểm chạm hoặc hiệu ứng map ở
     * đỉnh màn hình.
     */
    public static DuongDan taoDuongBayLen(short dauNongX, short dauNongY) {
        DuongDan phanNhinThay = taoDuongThangDung(
                dauNongX,
                dauNongY,
                Math.min(Y_RA_KHOI_MAN_HINH, dauNongY - 256),
                BUOC_BAY_LEN_PX
        );

        int soDiemNhinThay = Math.min(
                phanNhinThay.x.length,
                phanNhinThay.y.length
        );
        short[] xs = new short[soDiemNhinThay + DUONG_THOAT_MAP_Y.length];
        short[] ys = new short[soDiemNhinThay + DUONG_THOAT_MAP_Y.length];

        System.arraycopy(phanNhinThay.x, 0, xs, 0, soDiemNhinThay);
        System.arraycopy(phanNhinThay.y, 0, ys, 0, soDiemNhinThay);

        for (int i = 0; i < DUONG_THOAT_MAP_Y.length; i++) {
            xs[soDiemNhinThay + i] = dauNongX;
            ys[soDiemNhinThay + i] = DUONG_THOAT_MAP_Y[i];
        }
        return new DuongDan(xs, ys);
    }

    /** Tạo đường từ đúng đỉnh map xuống vùng thân của mục tiêu. */
    public static DuongDan taoDuongLaoXuong(short mucTieuX, short tamThanY) {
        return taoDuongThangDung(
                mucTieuX,
                Y_DINH_MAP,
                tamThanY,
                BUOC_LAO_XUONG_PX
        );
    }

    /**
     * Packet chứa bốn quỹ đạo; client tạo một sprite /eff/muiten.png cho mỗi
     * đường. Mỗi đường sau được chèn thêm điểm đứng yên ở đầu để bốn mũi nối
     * đuôi, nhưng bộ đếm bắn của client chỉ chạy đúng một lần rồi dừng.
     */
    public static LoatDuongDan taoLoatBonMuiNoiDuoi(DuongDan duongDanGoc) {
        short[][] cacDuongX = new short[SO_MUI_TEN][];
        short[][] cacDuongY = new short[SO_MUI_TEN][];

        if (duongDanGoc == null
                || duongDanGoc.x == null
                || duongDanGoc.y == null
                || duongDanGoc.x.length == 0
                || duongDanGoc.y.length == 0) {
            return new LoatDuongDan(cacDuongX, cacDuongY);
        }

        int soDiemGoc = Math.min(duongDanGoc.x.length, duongDanGoc.y.length);
        short diemDauX = duongDanGoc.x[0];
        short diemDauY = duongDanGoc.y[0];

        for (int mui = 0; mui < SO_MUI_TEN; mui++) {
            int soDiemCho = mui * SO_DIEM_CHO_GIUA_HAI_MUI;
            short[] duongX = new short[soDiemCho + soDiemGoc];
            short[] duongY = new short[soDiemCho + soDiemGoc];

            for (int i = 0; i < soDiemCho; i++) {
                duongX[i] = diemDauX;
                duongY[i] = diemDauY;
            }
            System.arraycopy(duongDanGoc.x, 0, duongX, soDiemCho, soDiemGoc);
            System.arraycopy(duongDanGoc.y, 0, duongY, soDiemCho, soDiemGoc);

            cacDuongX[mui] = duongX;
            cacDuongY[mui] = duongY;
        }

        return new LoatDuongDan(cacDuongX, cacDuongY);
    }

    private static DuongDan taoDuongThangDung(
            int x,
            int yBatDau,
            int yKetThuc,
            int buocDiChuyen
    ) {
        int buoc = Math.max(1, buocDiChuyen);
        int khoangCach = Math.abs(yKetThuc - yBatDau);
        int soBuoc = Math.max(1, (khoangCach + buoc - 1) / buoc);
        short[] xs = new short[soBuoc + 1];
        short[] ys = new short[soBuoc + 1];

        for (int i = 0; i <= soBuoc; i++) {
            double tiLe = (double)i / (double)soBuoc;
            xs[i] = kepShort(x);
            ys[i] = kepShort((int)Math.round(
                    yBatDau + (yKetThuc - yBatDau) * tiLe
            ));
        }
        return new DuongDan(xs, ys);
    }

    private static short kepShort(int giaTri) {
        return (short)Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, giaTri));
    }
}
