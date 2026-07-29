package com.chicken.luyentap;

/**
 * Cấu hình tập trung cho chế độ luyện tập.
 *
 * Chỉ chứa các giá trị có thể cân chỉnh. Không đặt logic chiến đấu hoặc packet
 * trong file này để việc đổi máu, phần thưởng, trang phục, va chạm và tốc độ AI
 * không phải sửa trực tiếp ChickenNguoiChoi.
 */
public final class ChickenCauHinhLuyenTap {

    private ChickenCauHinhLuyenTap() {
    }

    // Phiên và bản đồ
    public static final int TRAINING_BOT_COUNT = 1;
    public static final byte TRAINING_MAP_ID = 0;
    public static final byte TRAINING_PLAYER_INDEX = 0;
    public static final byte TRAINING_BOSS_INDEX = 1;

    // Máu, sát thương và phần thưởng
    public static final int TRAINING_PLAYER_MAX_HP = 1000;
    public static final int TRAINING_BOSS_BASE_HP = 1000;
    public static final int TRAINING_BOSS_HP_STEP = 50;
    public static final int TRAINING_BOSS_FALLBACK_DAMAGE = 100;
    /*
     * CMD 50 của client chỉ chứa signed short cho EXP. Mức mặc định phải nằm
     * trong 0..32767 để bảng kết quả luôn hiện đúng số server đã cộng.
     */
    public static final int TRAINING_WIN_EXP_REWARD = 1_000;
    public static final int TRAINING_WIN_GOLD_REWARD = 5_000;
    public static final int TRAINING_MAX_PACKET_HP = 65535;

    // Thời gian và chuyển trạng thái
    public static final long TRAINING_RETURN_LOBBY_DELAY_MS = 1800L;
    public static final long TRAINING_BOSS_RETREAT_DELAY_MS = 120L;
    public static final long TRAINING_BOSS_AIM_DELAY_MS = 280L;
    public static final long TRAINING_BOSS_FALLBACK_SHOT_DELAY_MS = 1300L;

    // Di chuyển boss và rơi bản đồ
    public static final int TRAINING_FALL_DISTANCE = 140;
    public static final int TRAINING_BOSS_DANGER_DISTANCE = 150;
    public static final int TRAINING_BOSS_SAFE_DISTANCE = 240;
    public static final int TRAINING_BOSS_RETREAT_STEP = 18;
    public static final int TRAINING_BOSS_MAX_GROUND_STEP = 28;
    public static final int TRAINING_BOSS_MAX_RETREAT_STEPS = 40;

    // Điểm xoay và chiều dài nòng súng
    // Khớp công thức CPlayer của client: sourceY - 12, đầu nòng cách 40 px.
    public static final int TRAINING_PLAYER_GUN_PIVOT_Y = 12;
    public static final int TRAINING_PLAYER_BARREL_LENGTH = 40;

    // Vùng va chạm nhân vật
    public static final int TRAINING_CHARACTER_COLLISION_MIN_DISTANCE = 12;
    public static final int TRAINING_PLAYER_BODY_HALF_WIDTH = 8;
    public static final int TRAINING_PLAYER_BODY_TOP_OFFSET = 30;
    public static final int TRAINING_PLAYER_BODY_BOTTOM_OFFSET = 2;
    public static final int TRAINING_BOSS_BODY_HALF_WIDTH = 13;
    public static final int TRAINING_BOSS_BODY_TOP_OFFSET = 40;
    public static final int TRAINING_BOSS_BODY_BOTTOM_OFFSET = 8;
    public static final int TRAINING_MULTI_BULLET_DELAY_POINTS = 2;

    // Súng và ngoại hình boss. Súng dùng ID template; các món còn lại dùng part.
    // Bo IS I mau den: de head=-1 de client ve du part non 159
    // (Small1323/1324). Neu gui ca toc, client cu se bo qua lop non.
    public static final byte[] TRAINING_BOT_AVENGERS = new byte[]{0};
    public static final short[] TRAINING_BOT_DISPLAY_WEAPONS = new short[]{-1};
    public static final short[] TRAINING_BOT_HEADS = new short[]{-1};
    public static final short[] TRAINING_BOT_LEGS = new short[]{157};
    public static final short[] TRAINING_BOT_BODIES = new short[]{158};
    public static final short[] TRAINING_BOT_HATS = new short[]{159};
    public static final short[] TRAINING_BOT_WINGS = new short[]{160};
}
