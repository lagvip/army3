package com.chicken.phong.boss.trandau;

import com.chicken.chien.ChickenChienBinh;

/**
 * Luật chọn mục tiêu chung cho đạn trong phòng boss.
 *
 * Luật này dùng cho toàn bộ đạn boss ở cả 7 map, không phân biệt boss thường,
 * Rùa hay Rồng. Phòng boss không miễn sát thương theo phe: người chơi, đồng
 * đội, boss khác và chính người/boss bắn đều là mục tiêu hợp lệ. Hình học
 * đường đạn và hitbox vẫn phải thực sự va chạm thì server mới tính sát thương.
 */
public final class ChickenLuatVaChamPhongBoss {

    private ChickenLuatVaChamPhongBoss() {
    }

    public static boolean chapNhan(
            ChickenChienBinh nguoiBan,
            ChickenChienBinh mucTieu
    ) {
        return nguoiBan != null && mucTieu != null;
    }
}
