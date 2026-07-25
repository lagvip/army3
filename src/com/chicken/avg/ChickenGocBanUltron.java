package com.chicken.avg;

/**
 * Quy tắc góc bắn riêng của AVG Ultron.
 *
 * Client dùng hai miền góc:
 * - quay phải: -45 tới 90 độ;
 * - quay trái: 90 tới 225 độ.
 *
 * Khi đổi hướng, client đôi lúc gửi góc tương đương ở dạng âm, ví dụ -161
 * thay cho 199. Lớp này đưa mọi giá trị về đúng miền client hiển thị và cung
 * cấp điểm bắt đầu trùng với đường căn góc native của client Ultron.
 */
public final class ChickenGocBanUltron {

    public static final int GOC_PHAI_THAP_NHAT = -45;
    public static final int GOC_PHAI_CAO_NHAT = 90;
    public static final int GOC_TRAI_THAP_NHAT = 90;
    public static final int GOC_TRAI_CAO_NHAT = 225;

    /* Bytecode KeGocFunctions của client Ultron dùng đúng hai offset này. */
    private static final int KHOANG_CACH_DUONG_CAN = 40;
    private static final int TRUC_DUONG_CAN_CACH_CHAN = 12;

    private ChickenGocBanUltron() {
    }

    /**
     * Đưa góc client về một trong hai miền hợp lệ.
     *
     * Ví dụ:
     * -161 -> 199;
     * 359 -> -1;
     * 300 -> -45;
     * 250 -> 225.
     */
    public static short chuanHoa(short gocClient) {
        int goc = gocClient % 360;
        if (goc < 0) {
            goc += 360;
        }

        if (goc <= GOC_TRAI_CAO_NHAT) {
            return (short) goc;
        }
        if (goc >= 315) {
            return (short) (goc - 360);
        }

        /* Miền 226..314 không hợp lệ. Ép về biên gần nhất. */
        return goc <= 270
                ? (short) GOC_TRAI_CAO_NHAT
                : (short) GOC_PHAI_THAP_NHAT;
    }

    /** Đổi góc hợp lệ sang 0..359 để dùng sin/cos. */
    public static int gocLuongGiac(short goc) {
        int ketQua = chuanHoa(goc);
        return ketQua < 0 ? ketQua + 360 : ketQua;
    }

    /**
     * Điểm bắt đầu của tia trùng với đường căn native client Ultron:
     * x + 40*cos(góc), y - 12 - 40*sin(góc).
     */
    public static short[] layDiemBatDauDuongCan(
            short nhanVatX,
            short nhanVatY,
            short goc,
            int mapWidth,
            int mapHeight
    ) {
        int gocChuan = gocLuongGiac(goc);
        double rad = Math.toRadians(gocChuan);
        int cos1024 = (int) Math.round(Math.cos(rad) * 1024.0D);
        int sin1024 = (int) Math.round(Math.sin(rad) * 1024.0D);
        int x = nhanVatX + ((KHOANG_CACH_DUONG_CAN * cos1024) >> 10);
        int y = nhanVatY - TRUC_DUONG_CAN_CACH_CHAN
                - ((KHOANG_CACH_DUONG_CAN * sin1024) >> 10);

        /*
         * Client không kẹp điểm đầu nòng vào map: khi Ultron bay sát mép, tia
         * vẫn bắt đầu ở đúng đầu nòng kể cả khi điểm đó nằm hơi ngoài khung.
         * Phần mô phỏng/cắt va chạm phía server đã bỏ qua các pixel ngoài map
         * an toàn. Kẹp ở đây làm tia server bắt đầu lệch so với đường ngắm.
         */
        return new short[]{
            (short) x,
            (short) y
        };
    }
}
