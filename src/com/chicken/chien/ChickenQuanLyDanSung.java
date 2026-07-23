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
     * Ảnh đạn chính lấy theo part ảnh của từng súng trong res/icon/item/1/.
     * Tên file có dạng Small<idAnhDan>.png để khớp đúng dữ liệu client.
     */
    private static final Map<Integer, DuLieuSung> THEO_ID_SUNG = new LinkedHashMap<>();

    static {
        dangKy(110, "AT4", (byte) 0, (short) 57, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 57, "res/icon/item/1/Small839.png");
        dangKy(111, "M72 LAW", (byte) 0, (short) 31, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 31, "res/icon/item/1/Small840.png");
        dangKy(112, "RPG-7", (byte) 0, (short) 5, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 5, "res/icon/item/1/Small841.png");
        dangKy(113, "Javelin", (byte) 0, (short) 134, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 134, "res/icon/item/1/Small842.png");
        dangKy(114, "FIM-92 Stinger", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, "res/icon/item/1/Small843.png");
        dangKy(115, "[AT6]", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, "res/icon/item/1/Small844.png");
        dangKy(116, "[AT7]", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, "res/icon/item/1/Small135.png");
        dangKy(117, "[AT8]", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, "res/icon/item/1/Small135.png");
        dangKy(118, "[AT9]", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, "res/icon/item/1/Small135.png");
        dangKy(119, "[AT10]", (byte) 0, (short) 135, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 135, "res/icon/item/1/Small135.png");
        dangKy(120, "K98", (byte) 1, (short) 27, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 27, "res/icon/item/1/Small848.png");
        dangKy(121, "M1 Garand", (byte) 1, (short) 37, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 37, "res/icon/item/1/Small849.png");
        dangKy(122, "M1 Carbine", (byte) 1, (short) 156, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 156, "res/icon/item/1/Small156.png");
        dangKy(123, "Dragunov SVU", (byte) 1, (short) 132, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 132, "res/icon/item/1/Small850.png");
        dangKy(124, "MG36", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, "res/icon/item/1/Small855.png");
        dangKy(125, "[AR6]", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, "res/icon/item/1/Small133.png");
        dangKy(126, "[AR7]", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, "res/icon/item/1/Small133.png");
        dangKy(127, "[AR8]", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, "res/icon/item/1/Small133.png");
        dangKy(128, "[AR9]", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, "res/icon/item/1/Small133.png");
        dangKy(129, "[AR10]", (byte) 1, (short) 133, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 133, "res/icon/item/1/Small133.png");
        dangKy(130, "MG42", (byte) 5, (short) 54, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 54, "res/icon/item/1/Small931.png");
        dangKy(131, "MG60", (byte) 5, (short) 28, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 28, "res/icon/item/1/Small932.png");
        dangKy(132, "M134 Mini", (byte) 5, (short) 143, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 143, "res/icon/item/1/Small933.png");
        dangKy(133, "M61 Vulcan", (byte) 5, (short) 145, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 145, "res/icon/item/1/Small934.png");
        dangKy(134, "XM196", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, "res/icon/item/1/Small935.png");
        dangKy(135, "[MG6]", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, "res/icon/item/1/Small936.png");
        dangKy(136, "[MG7]", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, "res/icon/item/1/Small937.png");
        dangKy(137, "[MG8]", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, "res/icon/item/1/Small938.png");
        dangKy(138, "[MG9]", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, "res/icon/item/1/Small939.png");
        dangKy(139, "[MG10]", (byte) 5, (short) 144, (byte) 5, (byte) 11, (byte) 5, (short) 10, (short) 144, "res/icon/item/1/Small939.png");
        dangKy(140, "Súng chuối cau", (byte) 3, (short) 58, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 58, "res/icon/item/1/Small58.png");
        dangKy(141, "Súng chuối xanh", (byte) 3, (short) 140, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 140, "res/icon/item/1/Small140.png");
        dangKy(142, "Súng chuối sáp", (byte) 3, (short) 32, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 32, "res/icon/item/1/Small32.png");
        dangKy(143, "Súng chuối sứ", (byte) 3, (short) 141, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 141, "res/icon/item/1/Small141.png");
        dangKy(144, "Súng chuối 5", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, "res/icon/item/1/Small142.png");
        dangKy(145, "Súng chuối 6", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, "res/icon/item/1/Small142.png");
        dangKy(146, "Súng chuối 7", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, "res/icon/item/1/Small142.png");
        dangKy(147, "Súng chuối 8", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, "res/icon/item/1/Small142.png");
        dangKy(148, "Súng chuối 9", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, "res/icon/item/1/Small142.png");
        dangKy(149, "Súng chuối 10", (byte) 3, (short) 142, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 142, "res/icon/item/1/Small142.png");
        dangKy(150, "Flint locks 1612", (byte) 2, (short) 123, (byte) 4, (byte) 10, (byte) 3, (short) 0, (short) 123, "res/icon/item/1/Small857.png");
        dangKy(151, "RM-870", (byte) 2, (short) 124, (byte) 4, (byte) 10, (byte) 1, (short) 0, (short) 124, "res/icon/item/1/Small858.png");
        dangKy(152, "Leone 12 Gauge", (byte) 2, (short) 125, (byte) 4, (byte) 10, (byte) 1, (short) 0, (short) 125, "res/icon/item/1/Small859.png");
        dangKy(153, "GP-30 40 mm", (byte) 2, (short) 126, (byte) 4, (byte) 10, (byte) 1, (short) 0, (short) 126, "res/icon/item/1/Small860.png");
        dangKy(154, "M1216 SA 12 Gauge", (byte) 2, (short) 127, (byte) 4, (byte) 10, (byte) 1, (short) 0, (short) 127, "res/icon/item/1/Small861.png");
        dangKy(155, "[SG6]", (byte) 2, (short) 127, (byte) 4, (byte) 10, (byte) 1, (short) 0, (short) 127, "res/icon/item/1/Small127.png");
        dangKy(156, "[SG7]", (byte) 2, (short) 127, (byte) 4, (byte) 10, (byte) 1, (short) 0, (short) 127, "res/icon/item/1/Small127.png");
        dangKy(157, "[SG8]", (byte) 2, (short) 127, (byte) 4, (byte) 10, (byte) 1, (short) 0, (short) 127, "res/icon/item/1/Small127.png");
        dangKy(158, "[SG9]", (byte) 2, (short) 127, (byte) 4, (byte) 10, (byte) 1, (short) 0, (short) 127, "res/icon/item/1/Small127.png");
        dangKy(159, "[SG10]", (byte) 2, (short) 127, (byte) 4, (byte) 10, (byte) 1, (short) 0, (short) 127, "res/icon/item/1/Small127.png");
        dangKy(160, "Súng cối 60mm", (byte) 4, (short) 56, (byte) 5, (byte) 11, (byte) 1, (short) 0, (short) 56, "res/icon/item/1/Small886.png");
        dangKy(161, "Súng cối 70mm", (byte) 4, (short) 30, (byte) 5, (byte) 11, (byte) 1, (short) 0, (short) 30, "res/icon/item/1/Small887.png");
        dangKy(162, "Súng cối 80mm", (byte) 4, (short) 146, (byte) 5, (byte) 11, (byte) 1, (short) 0, (short) 146, "res/icon/item/1/Small888.png");
        dangKy(163, "Súng cối 90mm", (byte) 4, (short) 147, (byte) 5, (byte) 11, (byte) 1, (short) 0, (short) 147, "res/icon/item/1/Small889.png");
        dangKy(164, "Rocket 5", (byte) 4, (short) 148, (byte) 5, (byte) 11, (byte) 1, (short) 0, (short) 148, "res/icon/item/1/Small890.png");
        dangKy(165, "Rocket 6", (byte) 4, (short) 148, (byte) 5, (byte) 11, (byte) 1, (short) 0, (short) 148, "res/icon/item/1/Small148.png");
        dangKy(166, "Rocket 7", (byte) 4, (short) 148, (byte) 5, (byte) 11, (byte) 1, (short) 0, (short) 148, "res/icon/item/1/Small148.png");
        dangKy(167, "Rocket 8", (byte) 4, (short) 148, (byte) 5, (byte) 11, (byte) 1, (short) 0, (short) 148, "res/icon/item/1/Small148.png");
        dangKy(168, "Rocket 9", (byte) 4, (short) 148, (byte) 5, (byte) 11, (byte) 1, (short) 0, (short) 148, "res/icon/item/1/Small148.png");
        dangKy(169, "Rocket 10", (byte) 4, (short) 148, (byte) 5, (byte) 11, (byte) 1, (short) 0, (short) 148, "res/icon/item/1/Small148.png");
        dangKy(170, "Gà con", (byte) 6, (short) 55, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 55, "res/icon/item/1/Small895.png");
        dangKy(171, "Gà choai", (byte) 6, (short) 29, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 29, "res/icon/item/1/Small896.png");
        dangKy(172, "Gà chọi", (byte) 6, (short) 153, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 153, "res/icon/item/1/Small897.png");
        dangKy(173, "Gà sắt", (byte) 6, (short) 154, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 154, "res/icon/item/1/Small898.png");
        dangKy(174, "Gà 5", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, "res/icon/item/1/Small155.png");
        dangKy(175, "Gà 6", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, "res/icon/item/1/Small155.png");
        dangKy(176, "Gà 7", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, "res/icon/item/1/Small155.png");
        dangKy(177, "Gà 8", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, "res/icon/item/1/Small155.png");
        dangKy(178, "Gà 9", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, "res/icon/item/1/Small155.png");
        dangKy(179, "Gà 10", (byte) 6, (short) 155, (byte) 6, (byte) 19, (byte) 1, (short) 0, (short) 155, "res/icon/item/1/Small155.png");
        dangKy(180, "Rìu gỗ", (byte) 8, (short) 121, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 121, "res/icon/item/1/Small121.png");
        dangKy(181, "Rìu đồng", (byte) 8, (short) 128, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 128, "res/icon/item/1/Small128.png");
        dangKy(182, "Rìu sắt", (byte) 8, (short) 129, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 129, "res/icon/item/1/Small129.png");
        dangKy(183, "Rìu bạc", (byte) 8, (short) 130, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 130, "res/icon/item/1/Small130.png");
        dangKy(184, "Apache 5", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, "res/icon/item/1/Small131.png");
        dangKy(185, "Apache 6", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, "res/icon/item/1/Small131.png");
        dangKy(186, "Apache 7", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, "res/icon/item/1/Small131.png");
        dangKy(187, "Apache 8", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, "res/icon/item/1/Small131.png");
        dangKy(188, "Apache 9", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, "res/icon/item/1/Small131.png");
        dangKy(189, "Apache 10", (byte) 8, (short) 131, (byte) 8, (byte) 17, (byte) 1, (short) 0, (short) 131, "res/icon/item/1/Small131.png");
        dangKy(190, "Boomerang gỗ", (byte) 7, (short) 120, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 120, "res/icon/item/1/Small904.png");
        dangKy(191, "Boomerang đồng", (byte) 7, (short) 136, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 136, "res/icon/item/1/Small905.png");
        dangKy(192, "Boomerang sắt", (byte) 7, (short) 137, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 137, "res/icon/item/1/Small906.png");
        dangKy(193, "Boomerang vàng", (byte) 7, (short) 138, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 138, "res/icon/item/1/Small907.png");
        dangKy(194, "Boomerang 5", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, "res/icon/item/1/Small908.png");
        dangKy(195, "Boomerang 6", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, "res/icon/item/1/Small909.png");
        dangKy(196, "Boomerang 7", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, "res/icon/item/1/Small910.png");
        dangKy(197, "Boomerang 8", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, "res/icon/item/1/Small911.png");
        dangKy(198, "Boomerang 9", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, "res/icon/item/1/Small912.png");
        dangKy(199, "Boomerang 10", (byte) 7, (short) 139, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 139, "res/icon/item/1/Small912.png");
        dangKy(200, "Lazer Alpha", (byte) 9, (short) 122, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 122, "res/icon/item/1/Small922.png");
        dangKy(201, "Lazer Beta", (byte) 9, (short) 149, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 149, "res/icon/item/1/Small923.png");
        dangKy(202, "Lazer Gamma", (byte) 9, (short) 150, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 150, "res/icon/item/1/Small926.png");
        dangKy(203, "Lazer Delta", (byte) 9, (short) 151, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 151, "res/icon/item/1/Small930.png");
        dangKy(204, "Lazer 5", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, "res/icon/item/1/Small927.png");
        dangKy(205, "Lazer 6", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, "res/icon/item/1/Small152.png");
        dangKy(206, "Lazer 7", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, "res/icon/item/1/Small152.png");
        dangKy(207, "Lazer 8", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, "res/icon/item/1/Small152.png");
        dangKy(208, "Lazer 9", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, "res/icon/item/1/Small152.png");
        dangKy(209, "Laze 10", (byte) 9, (short) 152, (byte) 9, (byte) 49, (byte) 1, (short) 0, (short) 152, "res/icon/item/1/Small152.png");
        dangKy(295, "Bom", (byte) 10, (short) 193, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 193, "res/icon/item/1/Small193.png");
        dangKy(391, "Iron Man", (byte) 1, (short) 223, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 223, "res/icon/item/1/Small1883.png");
        dangKy(392, "Hulk", (byte) 0, (short) 224, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 224, "res/icon/item/1/Small224.png");
        dangKy(393, "Thor", (byte) 0, (short) 225, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 225, "res/icon/item/1/Small1880.png");
        dangKy(394, "Loki", (byte) 1, (short) 226, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 226, "res/icon/item/1/Small226.png");
        dangKy(395, "Captain", (byte) 7, (short) 227, (byte) 7, (byte) 21, (byte) 1, (short) 0, (short) 227, "res/icon/item/1/Small227.png");
        dangKy(396, "Winter Soldier", (byte) 1, (short) 228, (byte) 1, (byte) 1, (byte) 2, (short) 100, (short) 228, "res/icon/item/1/Small1881.png");
        dangKy(397, "Hawk Eyes", (byte) 3, (short) 229, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 229, "res/icon/item/1/Small1879.png");
        dangKy(398, "Ultron", (byte) 0, (short) 230, (byte) 0, (byte) 0, (byte) 1, (short) 0, (short) 1888, "res/icon/item/1/Small1888.png");
        dangKy(400, "Khỉ đỏ", (byte) 3, (short) 249, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 249, "res/icon/item/1/Small249.png");
        dangKy(401, "Khỉ vàng", (byte) 3, (short) 249, (byte) 3, (byte) 9, (byte) 4, (short) 100, (short) 249, "res/icon/item/1/Small249.png");
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
            String duongDanAnhChinh
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
                duongDanAnhChinh
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
            if (!danhSach.contains(duLieu.getDuongDanAnhChinh())) {
                danhSach.add(duLieu.getDuongDanAnhChinh());
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
    public static String layDuongDanAnhDanTheoIdSung(int idSung) {
        DuLieuSung duLieu = theoIdSung(idSung);
        return duLieu == null ? null : duLieu.getDuongDanAnhChinh();
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
            short partClientYeuCau
    ) {
        ArrayList<String> ketQua = new ArrayList<>();
        if (duLieu != null) {
            themNeuChuaCo(ketQua, duLieu.getDuongDanAnhChinh());
            themNeuChuaCo(ketQua, "res/icon/item/1/Small" + duLieu.getIdAnhDan() + ".png");
            themNeuChuaCo(ketQua, "res/training/bullet/" + duLieu.getIdSung() + ".png");
            themNeuChuaCo(ketQua, "res/training/bullet/" + duLieu.getIdAnhDan() + ".png");
            themNeuChuaCo(ketQua, "res/training/bullet/gun_" + duLieu.getNhomSung() + ".png");
        }
        short part = mauSung != null ? mauSung.part : partClientYeuCau;
        themNeuChuaCo(ketQua, "res/icon/item/1/Small" + part + ".png");
        themNeuChuaCo(ketQua, "res/training/bullet/" + part + ".png");
        themNeuChuaCo(ketQua, "res/icon/bullet/" + part + ".png");
        themNeuChuaCo(ketQua, "res/bullet/" + part + ".png");
        return ketQua;
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
        private final String duongDanAnhChinh;

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
                String duongDanAnhChinh
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
            this.duongDanAnhChinh = duongDanAnhChinh;
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
        public String getDuongDanAnhChinh() { return this.duongDanAnhChinh; }
    }
}
