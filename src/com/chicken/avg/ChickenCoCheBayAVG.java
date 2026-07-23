package com.chicken.avg;

/**
 * Quy tắc dùng chung cho các AVG có khả năng bay native trên client.
 *
 * Client hiện nhận AVG 1 và AVG 8 là nhân vật bay. Server chỉ cần:
 * - chấp nhận tọa độ X/Y tự do trong phạm vi bản đồ;
 * - không ép nhân vật xuống nền;
 * - không áp trọng lực;
 * - không xử lý chết do rơi khỏi nền;
 * - không kiểm tra va chạm địa hình khi di chuyển.
 */
public final class ChickenCoCheBayAVG {

    public static final byte AVG_BAY_1 = 1;
    public static final byte AVG_ULTRON = 8;

    private ChickenCoCheBayAVG() {
    }

    /** Trùng đúng danh sách isFlyAvenger native của client. */
    public static boolean coTheBay(byte avenger) {
        return avenger == AVG_BAY_1 || avenger == AVG_ULTRON;
    }

    /** AVG bay được di chuyển xuyên qua pixel địa hình trong phạm vi map. */
    public static boolean boQuaVaChamDiaHinh(byte avenger) {
        return coTheBay(avenger);
    }

    /** AVG bay không chịu trọng lực và không chết chỉ vì không có nền bên dưới. */
    public static boolean mienTrongLucVaRoiMap(byte avenger) {
        return coTheBay(avenger);
    }

    /**
     * Khi địa hình bị phá, AVG bay phải giữ nguyên độ cao hiện tại.
     * Không được gọi hàm tìm mặt đất rồi ghi đè Y như nhân vật đi bộ.
     */
    public static boolean giuNguyenDoCaoKhiDiaHinhThayDoi(byte avenger) {
        return coTheBay(avenger);
    }

    /**
     * Packet bắn của client tạm chuyển AVG bay sang trạng thái bắn/đứng ngắm.
     * Server gửi lại đúng X/Y đã chốt để tránh client giữ một tọa độ tụt tạm
     * thời sau khi nhận kết quả phát bắn hoặc kết thúc animation.
     */
    public static boolean canDongBoToaDoSauKhiBan(byte avenger) {
        return coTheBay(avenger);
    }

    /** Giữ tọa độ trong vùng an toàn mà không kéo nhân vật về mặt đất. */
    public static short gioiHanToaDoTrongMap(short toaDo, int gioiHan) {
        int max = Math.max(0, gioiHan);
        return (short) Math.max(0, Math.min(max, toaDo));
    }

    /**
     * Lấy đúng tọa độ hiện tại mà client gửi kèm phát bắn của AVG bay.
     *
     * Client có thể gửi lệnh bắn ngay sau lệnh bay. Nếu server tiếp tục dùng
     * tọa độ cũ đang lưu, packet kết quả bắn sẽ kéo nhân vật trở lại vị trí cũ.
     * Chỉ AVG bay được đồng bộ X/Y theo packet bắn; AVG đi bộ vẫn giữ luồng cũ.
     */
    public static short[] toaDoKhiBan(
            byte avenger,
            short xClient,
            short yClient,
            int rongMap,
            int caoMap
    ) {
        if (!coTheBay(avenger)) {
            return null;
        }
        return new short[]{
            gioiHanToaDoTrongMap(xClient, rongMap),
            gioiHanToaDoTrongMap(yClient, caoMap)
        };
    }
}
