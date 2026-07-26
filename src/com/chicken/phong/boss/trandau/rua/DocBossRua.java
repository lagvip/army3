package com.chicken.phong.boss.trandau.rua;

import com.chicken.chien.ChickenChienBinh;

/**
 * Trạng thái độc server-authoritative của đạn Rùa.
 *
 * Client chỉ nhận loại đạn và hiệu ứng hiển thị. Việc xác định trúng, nguồn
 * độc và sát thương mỗi lượt đều lấy từ kết quả va chạm do server tính.
 */
public final class DocBossRua {
    /** Bullet type độc native của client PC/JAR/APK. */
    public static final byte LOAI_DAN_DOC = 55;
    /** Mỗi đầu lượt chịu 20% sát thương trực tiếp đã được server chốt. */
    public static final int PHAN_TRAM_SAT_THUONG_MOI_LUOT = 20;

    private DocBossRua() {
    }

    public static int tinhSatThuongMoiLuot(int satThuongTrucTiep) {
        if (satThuongTrucTiep <= 0) {
            return 0;
        }
        return Math.max(
                1,
                satThuongTrucTiep * PHAN_TRAM_SAT_THUONG_MOI_LUOT / 100
        );
    }

    /**
     * Nhiễm lại không cộng dồn. Chỉ giữ mức độc mạnh hơn để tránh một Rùa
     * bắn liên tục tạo damage vô hạn ngoài kiểm soát.
     */
    public static boolean apDung(
            ChickenChienBinh nguon,
            ChickenChienBinh mucTieu,
            int satThuongTrucTiep
    ) {
        if (nguon == null || mucTieu == null || mucTieu.chet
                || mucTieu.hp <= 0 || satThuongTrucTiep <= 0) {
            return false;
        }
        int satThuongMoiLuot = tinhSatThuongMoiLuot(satThuongTrucTiep);
        if (satThuongMoiLuot <= 0) {
            return false;
        }
        mucTieu.biDocBossRua = true;
        if (satThuongMoiLuot >= mucTieu.satThuongDocBossRuaMoiLuot) {
            mucTieu.satThuongDocBossRuaMoiLuot = satThuongMoiLuot;
            mucTieu.slotGayDocBossRua = nguon.chiSo;
        }
        return true;
    }

    public static int laySatThuongDauLuot(ChickenChienBinh mucTieu) {
        if (mucTieu == null || mucTieu.chet || mucTieu.hp <= 0
                || !mucTieu.biDocBossRua) {
            return 0;
        }
        return Math.max(0, mucTieu.satThuongDocBossRuaMoiLuot);
    }
}
