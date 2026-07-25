package com.chicken.avg;

import com.chicken.chien.ChickenChienBinh;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.vatpham.ChickenVatPham;

/**
 * Quy tắc dùng chung cho các AVG có khả năng bay native trên client.
 *
 * Client hiện nhận AVG 1 và AVG 8 là nhân vật bay. Server chỉ cần:
 * - nhận X/Y như ý định di chuyển, sau đó server giới hạn theo thanh di chuyển;
 * - không ép nhân vật xuống nền;
 * - không áp trọng lực;
 * - không xử lý chết do rơi khỏi nền;
 * - không kiểm tra va chạm địa hình khi di chuyển.
 */
public final class ChickenCoCheBayAVG {

    public static final byte AVG_IRON_MAN = 1;
    public static final byte AVG_ULTRON = 8;
    private static final int VAT_PHAM_IRON_MAN = 391;
    private static final int VAT_PHAM_ULTRON = 398;

    private ChickenCoCheBayAVG() {
    }

    /** Whitelist ID; chưa đủ để cấp quyền bay cho một người chơi thật. */
    public static boolean laIdBayDuocPhep(byte avenger) {
        return avenger == AVG_IRON_MAN || avenger == AVG_ULTRON;
    }

    /** Suy ra bộ AVG từ đúng vật phẩm server đang giữ trong ô vũ khí. */
    public static byte layAvengerTuTrangBi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null || nguoiChoi.itemBody == null
                || nguoiChoi.itemBody.length <= 5) {
            return 0;
        }
        ChickenVatPham trangBi = nguoiChoi.itemBody[5];
        if (trangBi == null || trangBi.mau == null || trangBi.mau.loai != 5
                || trangBi.ma < VAT_PHAM_IRON_MAN || trangBi.ma > VAT_PHAM_ULTRON) {
            return 0;
        }
        return (byte) (trangBi.ma - 390);
    }

    /**
     * Quyền bay của người chơi thật phải khớp cả ID AVG lẫn vật phẩm đang được
     * server giữ ở ô vũ khí. Chỉ sửa trường avenger hoặc packet client là chưa đủ.
     */
    public static boolean coTrangBiBayHopLe(ChickenNguoiChoi nguoiChoi) {
        return laIdBayDuocPhep(layAvengerTuTrangBi(nguoiChoi));
    }

    /** Quyền đã được server chốt khi tạo chiến binh; Loki sao chép cùng quyền này. */
    public static boolean coTheBay(ChickenChienBinh chienBinh) {
        return chienBinh != null
                && chienBinh.duocPhepBay
                && laIdBayDuocPhep(chienBinh.avenger);
    }

    /**
     * Chỉ AVG bay thường cần một gói chốt X/Y sau phát bắn.
     *
     * Ultron dùng CMD 22/84 để client tự chuyển sang animation tia laser. Nếu
     * gửi tiếp CMD 53 ngay sau đó, client sẽ áp X/Y trong lúc animation còn
     * chạy và tạo ra hiện tượng giật/lùi vị trí. Tọa độ trong CMD 22/84 đã là
     * tọa độ server chốt, nên Ultron không được gửi gói chốt thứ hai.
     */
    public static boolean canDongBoToaDoSauKhiBan(byte avenger) {
        return avenger == AVG_IRON_MAN;
    }

    /** Giữ tọa độ trong vùng an toàn mà không kéo nhân vật về mặt đất. */
    public static short gioiHanToaDoTrongMap(short toaDo, int gioiHan) {
        int max = Math.max(0, gioiHan);
        return (short) Math.max(0, Math.min(max, toaDo));
    }

}
