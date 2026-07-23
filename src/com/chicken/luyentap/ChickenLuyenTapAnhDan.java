package com.chicken.luyentap;

import com.chicken.vatpham.ChickenMauVatPham;
import java.util.ArrayList;
import java.util.List;

/**
 * Chỉ quyết định thứ tự đường dẫn ảnh đạn luyện tập.
 * Không xử lý phát bắn, tọa độ hoặc sát thương.
 */
public final class ChickenLuyenTapAnhDan {
    private ChickenLuyenTapAnhDan() {
    }

    public static List<String> layThuTuDuongDan(
            ChickenMauVatPham mauVuKhi,
            short maHinhVuKhi
    ) {
        ArrayList<String> paths = new ArrayList<>();

        String anhTheoCap = layDuongDanAnhTheoCapSung(mauVuKhi);
        if (anhTheoCap != null) {
            paths.add(anhTheoCap);
        }

        paths.add("res/icon/item/1/Small" + maHinhVuKhi + ".png");
        paths.add("res/icon/bullet/" + maHinhVuKhi + ".png");
        paths.add("res/training/bullet/" + maHinhVuKhi + ".png");
        paths.add("res/bullet/" + maHinhVuKhi + ".png");
        return paths;
    }

    private static String layDuongDanAnhTheoCapSung(ChickenMauVatPham mauVuKhi) {
        if (mauVuKhi == null || mauVuKhi.loai != 5) {
            return null;
        }

        int capSung = Math.max(1, Math.min(5, mauVuKhi.cap & 255));
        int maAnh;
        switch (mauVuKhi.gioiTinh) {
            case 0:
                maAnh = new int[]{842, 843, 844, 845, 846}[capSung - 1];
                break;
            case 1:
                maAnh = new int[]{889, 890, 891, 892, 893}[capSung - 1];
                break;
            case 5:
                maAnh = new int[]{931, 932, 933, 934, 935}[capSung - 1];
                break;
            default:
                return null;
        }
        return "res/icon/item/1/Small" + maAnh + ".png";
    }
}
