package com.chicken.chien;

import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenVatPham;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Nguồn dữ liệu duy nhất quản lý ID súng và đạn của toàn bộ game.
 *
 * Quy trình:
 * 1. Server đọc súng thật đang trang bị ở itemBody[5].
 * 2. Lấy ID template của súng và gọi theoIdSung(id).
 * 3. Dữ liệu trả về quyết định loại đạn, ảnh đạn và số viên.
 * 4. Client chỉ gửi góc/lực; client không được tự quyết định loại đạn/số viên.
 */
public final class ChickenQuanLyDanSung {
    /*
     * Bảng súng chỉ lưu ID sprite. Đường dẫn 1/2/3/4 luôn được tạo tập trung
     * theo mức phóng đã xác thực của từng client.
     */
    private static final Map<Integer, DuLieuSung> THEO_ID_SUNG = new LinkedHashMap<>();

    static {
        dangKy(110, "AT4", (byte) 0, (short) 57, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 57, 839);
        dangKy(111, "M72 LAW", (byte) 0, (short) 31, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 31, 840);
        dangKy(112, "RPG-7", (byte) 0, (short) 5, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 5, 841);
        dangKy(113, "Javelin", (byte) 0, (short) 134, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 134, 842);
        dangKy(114, "FIM-92 Stinger", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, 843);
        dangKy(115, "[AT6]", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, 844);
        dangKy(116, "[AT7]", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, 135);
        dangKy(117, "[AT8]", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, 135);
        dangKy(118, "[AT9]", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, 135);
        dangKy(119, "[AT10]", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, 135);
        dangKy(120, "K98", (byte) 1, (short) 27, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 27, 848);
        dangKy(121, "M1 Garand", (byte) 1, (short) 37, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 37, 849);
        dangKy(122, "M1 Carbine", (byte) 1, (short) 156, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 156, 850);
        dangKy(123, "Dragunov SVU", (byte) 1, (short) 132, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 132, 851);
        dangKy(124, "MG36", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, 855);
        dangKy(125, "[AR6]", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, 133);
        dangKy(126, "[AR7]", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, 133);
        dangKy(127, "[AR8]", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, 133);
        dangKy(128, "[AR9]", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, 133);
        dangKy(129, "[AR10]", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, 133);
        dangKy(130, "MG42", (byte) 5, (short) 54, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 54, 931);
        dangKy(131, "MG60", (byte) 5, (short) 28, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 28, 932);
        dangKy(132, "M134 Mini", (byte) 5, (short) 143, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 143, 933);
        dangKy(133, "M61 Vulcan", (byte) 5, (short) 145, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 145, 934);
        dangKy(134, "XM196", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, 935);
        dangKy(135, "[MG6]", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, 936);
        dangKy(136, "[MG7]", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, 937);
        dangKy(137, "[MG8]", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, 938);
        dangKy(138, "[MG9]", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, 939);
        dangKy(139, "[MG10]", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, 939);
        dangKy(140, "Súng chuối cau", (byte) 3, (short) 58, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 58, 877);
        dangKy(141, "Súng chuối xanh", (byte) 3, (short) 140, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 140, 878);
        dangKy(142, "Súng chuối sáp", (byte) 3, (short) 32, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 32, 879);
        dangKy(143, "Súng chuối sứ", (byte) 3, (short) 141, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 141, 1968);
        dangKy(144, "Súng chuối 5", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, 142);
        dangKy(145, "Súng chuối 6", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, 142);
        dangKy(146, "Súng chuối 7", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, 142);
        dangKy(147, "Súng chuối 8", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, 142);
        dangKy(148, "Súng chuối 9", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, 142);
        dangKy(149, "Súng chuối 10", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, 142);
        dangKy(150, "Flint locks 1612", (byte) 2, (short) 123, (byte) 2, (byte) 2, (byte) 3, (short) 0, (short) 123, 857);
        dangKy(151, "RM-870", (byte) 2, (short) 124, (byte) 2, (byte) 2, (byte) 3, (short) 0, (short) 124, 858);
        dangKy(152, "Leone 12 Gauge", (byte) 2, (short) 125, (byte) 2, (byte) 2, (byte) 3, (short) 0, (short) 125, 859);
        dangKy(153, "GP-30 40 mm", (byte) 2, (short) 126, (byte) 2, (byte) 2, (byte) 3, (short) 0, (short) 126, 860);
        dangKy(154, "M1216 SA 12 Gauge", (byte) 2, (short) 127, (byte) 2, (byte) 2, (byte) 3, (short) 0, (short) 127, 861);
        dangKy(155, "[SG6]", (byte) 2, (short) 127, (byte) 2, (byte) 2, (byte) 3, (short) 0, (short) 127, 127);
        dangKy(156, "[SG7]", (byte) 2, (short) 127, (byte) 2, (byte) 2, (byte) 3, (short) 0, (short) 127, 127);
        dangKy(157, "[SG8]", (byte) 2, (short) 127, (byte) 2, (byte) 2, (byte) 3, (short) 0, (short) 127, 127);
        dangKy(158, "[SG9]", (byte) 2, (short) 127, (byte) 2, (byte) 2, (byte) 3, (short) 0, (short) 127, 127);
        dangKy(159, "[SG10]", (byte) 2, (short) 127, (byte) 2, (byte) 2, (byte) 3, (short) 0, (short) 127, 127);
        // Nhóm cối/rocket dùng type 10: ba viên nối tiếp trên cùng góc và lực.
        // Type 11 thuộc nhóm MG năm viên, không được dùng chung cho nhóm này.
        dangKy(160, "Súng cối 60mm", (byte) 4, (short) 56, (byte) 4, (byte) 10, (byte) 3, (short) 0, (short) 56, 886);
        dangKy(161, "Súng cối 70mm", (byte) 4, (short) 30, (byte) 4, (byte) 10, (byte) 3, (short) 0, (short) 30, 887);
        dangKy(162, "Súng cối 80mm", (byte) 4, (short) 146, (byte) 4, (byte) 10, (byte) 3, (short) 0, (short) 146, 888);
        dangKy(163, "Súng cối 90mm", (byte) 4, (short) 147, (byte) 4, (byte) 10, (byte) 3, (short) 0, (short) 147, 889);
        dangKy(164, "Rocket 5", (byte) 4, (short) 148, (byte) 4, (byte) 10, (byte) 3, (short) 0, (short) 148, 890);
        dangKy(165, "Rocket 6", (byte) 4, (short) 148, (byte) 4, (byte) 10, (byte) 3, (short) 0, (short) 148, 148);
        dangKy(166, "Rocket 7", (byte) 4, (short) 148, (byte) 4, (byte) 10, (byte) 3, (short) 0, (short) 148, 148);
        dangKy(167, "Rocket 8", (byte) 4, (short) 148, (byte) 4, (byte) 10, (byte) 3, (short) 0, (short) 148, 148);
        dangKy(168, "Rocket 9", (byte) 4, (short) 148, (byte) 4, (byte) 10, (byte) 3, (short) 0, (short) 148, 148);
        dangKy(169, "Rocket 10", (byte) 4, (short) 148, (byte) 4, (byte) 10, (byte) 3, (short) 0, (short) 148, 148);
        dangKy(170, "Gà con", (byte) 6, (short) 55, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 55, 895);
        dangKy(171, "Gà choai", (byte) 6, (short) 29, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 29, 896);
        dangKy(172, "Gà chọi", (byte) 6, (short) 153, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 153, 897);
        dangKy(173, "Gà sắt", (byte) 6, (short) 154, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 154, 898);
        dangKy(174, "Gà 5", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, 155);
        dangKy(175, "Gà 6", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, 155);
        dangKy(176, "Gà 7", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, 155);
        dangKy(177, "Gà 8", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, 155);
        dangKy(178, "Gà 9", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, 155);
        dangKy(179, "Gà 10", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, 155);
        dangKy(180, "Rìu gỗ", (byte) 8, (short) 121, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 121, 913);
        dangKy(181, "Rìu đồng", (byte) 8, (short) 128, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 128, 914);
        dangKy(182, "Rìu sắt", (byte) 8, (short) 129, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 129, 915);
        dangKy(183, "Rìu bạc", (byte) 8, (short) 130, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 130, 916);
        dangKy(184, "Apache 5", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, 131);
        dangKy(185, "Apache 6", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, 131);
        dangKy(186, "Apache 7", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, 131);
        dangKy(187, "Apache 8", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, 131);
        dangKy(188, "Apache 9", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, 131);
        dangKy(189, "Apache 10", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, 131);
        dangKy(190, "Boomerang gỗ", (byte) 7, (short) 120, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 120, 904);
        dangKy(191, "Boomerang đồng", (byte) 7, (short) 136, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 136, 905);
        dangKy(192, "Boomerang sắt", (byte) 7, (short) 137, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 137, 906);
        dangKy(193, "Boomerang vàng", (byte) 7, (short) 138, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 138, 907);
        dangKy(194, "Boomerang 5", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, 908);
        dangKy(195, "Boomerang 6", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, 909);
        dangKy(196, "Boomerang 7", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, 910);
        dangKy(197, "Boomerang 8", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, 911);
        dangKy(198, "Boomerang 9", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, 912);
        dangKy(199, "Boomerang 10", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, 912);
        dangKy(200, "Lazer Alpha", (byte) 9, (short) 122, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 122, 922);
        dangKy(201, "Lazer Beta", (byte) 9, (short) 149, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 149, 923);
        dangKy(202, "Lazer Gamma", (byte) 9, (short) 150, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 150, 926);
        dangKy(203, "Lazer Delta", (byte) 9, (short) 151, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 151, 930);
        dangKy(204, "Lazer 5", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, 927);
        dangKy(205, "Lazer 6", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, 152);
        dangKy(206, "Lazer 7", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, 152);
        dangKy(207, "Lazer 8", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, 152);
        dangKy(208, "Lazer 9", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, 152);
        dangKy(209, "Laze 10", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, 152);
        dangKy(295, "Bom", (byte) 10, (short) 193, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 193, 193);
        dangKy(391, "Iron Man", (byte) 1, (short) 223, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 223, 1883);
        // Client dung gun 12 chi de ve duong ngam cho Hulk. Packet ket qua
        // van phai la bullet type 0; Bullet.update() tu nhan avenger=2 va keo
        // Hulk di theo quy dao. Gui type 12 se vao nhanh grenade va treo client.
        dangKy(392, "Hulk", (byte) 0, (short) 224, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 224, 224);
        dangKy(393, "Thor", (byte) 0, (short) 225, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 225, 1880);
        dangKy(394, "Loki", (byte) 1, (short) 226, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 226, 226);
        dangKy(395, "Captain", (byte) 7, (short) 227, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 227, 227);
        dangKy(396, "Winter Soldier", (byte) 1, (short) 228, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 228, 1881);
        dangKy(397, "Hawk Eyes", (byte) 3, (short) 229, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 229, 1879);
        dangKy(398, "Ultron", (byte) 0, (short) 230, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 1888, 1888);
        dangKy(400, "Khỉ đỏ", (byte) 3, (short) 249, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 249, 249);
        dangKy(401, "Khỉ vàng", (byte) 3, (short) 249, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 249, 249);
    }

    private ChickenQuanLyDanSung() {
    }

    private static void dangKy(
            int idSung,
            String tenSung,
            byte nhomSung,
            short partSung,
            byte loaiHinhDan,
            byte loaiDan,
            byte soVienMoiLoat,
            short khoangCachVienMs,
            short idAnhDan,
            int idAnhChinh
    ) {
        THEO_ID_SUNG.put(idSung, new DuLieuSung(
                idSung,
                tenSung,
                nhomSung,
                partSung,
                loaiHinhDan,
                loaiDan,
                soVienMoiLoat,
                khoangCachVienMs,
                idAnhDan,
                idAnhChinh
        ));
    }

    public static DuLieuSung theoIdSung(int idSung) {
        return THEO_ID_SUNG.get(idSung);
    }

    public static DuLieuSung theoMauSung(ChickenMauVatPham mauSung) {
        if (mauSung == null || mauSung.loai != 5) {
            return null;
        }
        return theoIdSung(mauSung.ma & 0xFFFF);
    }

    public static DuLieuSung theoSungDangTrangBi(ChickenVatPham sung) {
        if (sung == null || sung.mau == null || sung.mau.loai != 5 || sung.HP <= 0) {
            return null;
        }
        return theoIdSung(sung.mau.ma & 0xFFFF);
    }

    /**
     * Dùng khi client yêu cầu ảnh theo part súng. Ưu tiên đúng súng đang trang bị;
     * nếu không có thì lấy bản ghi đầu tiên có cùng part.
     */
    public static DuLieuSung theoPartSung(short partSung) {
        for (DuLieuSung duLieu : THEO_ID_SUNG.values()) {
            if (duLieu.getPartSung() == partSung) {
                return duLieu;
            }
        }
        return null;
    }

    /**
     * Danh sách ảnh đạn/vật thể bay lấy nguyên bản từ client JAR.
     * Danh sách này chỉ để kiểm tra tài nguyên; mapping từng súng vẫn lấy từ
     * DuLieuSung trong THEO_ID_SUNG.
     */
    public static String[] layDanhSachAnhDanTuClientJar() {
        ArrayList<String> danhSach = new ArrayList<>();
        for (DuLieuSung duLieu : THEO_ID_SUNG.values()) {
            String duongDan = taoDuongDanAnhItem(1, duLieu.getIdAnhChinh());
            if (!danhSach.contains(duongDan)) {
                danhSach.add(duongDan);
            }
        }
        return danhSach.toArray(new String[0]);
    }

    public static Map<Integer, DuLieuSung> layTatCa() {
        return Collections.unmodifiableMap(THEO_ID_SUNG);
    }

    /**
     * Lấy loại đạn của đúng ID template súng.
     * Trả về -1 khi ID súng chưa được đăng ký.
     */
    public static byte layLoaiDanTheoIdSung(int idSung) {
        DuLieuSung duLieu = theoIdSung(idSung);
        return duLieu == null ? (byte) -1 : duLieu.getLoaiDan();
    }

    /**
     * Lấy ID ảnh đạn đã gắn với đúng ID súng.
     * Trả về -1 khi ID súng chưa được đăng ký.
     */
    public static short layIdAnhDanTheoIdSung(int idSung) {
        DuLieuSung duLieu = theoIdSung(idSung);
        return duLieu == null ? (short) -1 : duLieu.getIdAnhDan();
    }

    /**
     * Lấy file PNG chính của viên đạn theo đúng ID súng.
     */
    public static String layDuongDanAnhDanTheoIdSung(int idSung, int mucPhong) {
        DuLieuSung duLieu = theoIdSung(idSung);
        return duLieu == null
                ? null
                : taoDuongDanAnhItem(mucPhong, duLieu.getIdAnhChinh());
    }

    /**
     * Lấy số viên được tạo trong một lần bắn.
     * ID chưa đăng ký trả về 0 để nơi gọi không được tự đoán số viên.
     */
    public static byte laySoVienMoiLoatTheoIdSung(int idSung) {
        DuLieuSung duLieu = theoIdSung(idSung);
        return duLieu == null ? (byte) 0 : duLieu.getSoVienMoiLoat();
    }

    public static List<String> layThuTuDuongDanAnh(
            DuLieuSung duLieu,
            ChickenMauVatPham mauSung,
            short partClientYeuCau,
            int mucPhong
    ) {
        ArrayList<String> ketQua = new ArrayList<>();
        int mucPhongHopLe = chuanHoaMucPhong(mucPhong);
        if (duLieu != null) {
            themNeuChuaCo(ketQua,
                    taoDuongDanAnhItem(mucPhongHopLe, duLieu.getIdAnhChinh()));
            themNeuChuaCo(ketQua,
                    taoDuongDanAnhItem(1, duLieu.getIdAnhChinh()));
            themNeuChuaCo(ketQua,
                    taoDuongDanAnhItem(mucPhongHopLe, duLieu.getIdAnhDan()));
            themNeuChuaCo(ketQua,
                    taoDuongDanAnhItem(1, duLieu.getIdAnhDan()));
            themNeuChuaCo(ketQua, "res/training/bullet/" + duLieu.getIdSung() + ".png");
            themNeuChuaCo(ketQua, "res/training/bullet/" + duLieu.getIdAnhDan() + ".png");
            themNeuChuaCo(ketQua, "res/training/bullet/gun_" + duLieu.getNhomSung() + ".png");
        }
        short part = mauSung != null ? mauSung.part : partClientYeuCau;
        themNeuChuaCo(ketQua,
                taoDuongDanAnhItem(mucPhongHopLe, part));
        themNeuChuaCo(ketQua, taoDuongDanAnhItem(1, part));
        themNeuChuaCo(ketQua, "res/training/bullet/" + part + ".png");
        themNeuChuaCo(ketQua, "res/icon/bullet/" + part + ".png");
        themNeuChuaCo(ketQua, "res/bullet/" + part + ".png");
        return ketQua;
    }

    private static String taoDuongDanAnhItem(int mucPhong, int idAnh) {
        return "res/icon/item/" + chuanHoaMucPhong(mucPhong) + "/Small" + idAnh + ".png";
    }

    private static int chuanHoaMucPhong(int mucPhong) {
        return mucPhong >= 1 && mucPhong <= 4 ? mucPhong : 1;
    }

    private static void themNeuChuaCo(List<String> danhSach, String giaTri) {
        if (giaTri != null && !giaTri.isEmpty() && !danhSach.contains(giaTri)) {
            danhSach.add(giaTri);
        }
    }

    /** Dữ liệu bất biến của đúng một ID súng. */
    public static final class DuLieuSung {
        private final int idSung;
        private final String tenSung;
        private final byte nhomSung;
        private final short partSung;
        private final byte loaiHinhDan;
        private final byte loaiDan;
        private final byte soVienMoiLoat;
        private final short khoangCachVienMs;
        private final short idAnhDan;
        private final int idAnhChinh;

        private DuLieuSung(
                int idSung,
                String tenSung,
                byte nhomSung,
                short partSung,
                byte loaiHinhDan,
                byte loaiDan,
                byte soVienMoiLoat,
                short khoangCachVienMs,
                short idAnhDan,
                int idAnhChinh
        ) {
            this.idSung = idSung;
            this.tenSung = tenSung;
            this.nhomSung = nhomSung;
            this.partSung = partSung;
            this.loaiHinhDan = loaiHinhDan;
            this.loaiDan = loaiDan;
            this.soVienMoiLoat = soVienMoiLoat;
            this.khoangCachVienMs = khoangCachVienMs;
            this.idAnhDan = idAnhDan;
            this.idAnhChinh = idAnhChinh;
        }

        public int getIdSung() { return this.idSung; }
        public String getTenSung() { return this.tenSung; }
        public byte getNhomSung() { return this.nhomSung; }
        public short getPartSung() { return this.partSung; }
        public byte getLoaiHinhDan() { return this.loaiHinhDan; }
        public byte getLoaiDan() { return this.loaiDan; }
        public byte getSoVienMoiLoat() { return this.soVienMoiLoat; }
        public short getKhoangCachVienMs() { return this.khoangCachVienMs; }
        public short getIdAnhDan() { return this.idAnhDan; }
        public int getIdAnhChinh() { return this.idAnhChinh; }
    }
}
