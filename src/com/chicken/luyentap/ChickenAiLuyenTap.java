package com.chicken.luyentap;

/** Quyet dinh AI gan nguoi choi, tach khoi packet va vat ly de de kiem thu. */
public final class ChickenAiLuyenTap {
    public enum HanhDongGan {
        DICH_CHUYEN,
        BAN
    }

    private ChickenAiLuyenTap() {
    }

    public static HanhDongGan chonHanhDongGan(
            int khoangCach
    ) {
        if (khoangCach >= ChickenCauHinhLuyenTap
                .TRAINING_BOSS_DANGER_DISTANCE) {
            return HanhDongGan.BAN;
        }
        return HanhDongGan.DICH_CHUYEN;
    }
}
