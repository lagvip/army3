package com.chicken.mohinh;

/**
 * Quản lý vòng đời và trạng thái dùng chung của một phiên luyện tập.
 *
 * Class này chỉ xử lý dữ liệu phiên. Logic packet, bản đồ, AI boss và công
 * thức đạn vẫn do ChickenNguoiChoi điều khiển để không thay đổi cơ chế hiện có.
 */
final class ChickenQuanLyPhienLuyenTap {

    private ChickenQuanLyPhienLuyenTap() {
    }

    static long batDauPhien(ChickenPhienLuyenTap phien,
            int thoiGianNapNguoiChoi,
            int thoiGianNapBoss,
            int mauToiDaBoss,
            byte chiSoNguoiChoi) {
        long maPhien = ++phien.trainingSessionId;
        phien.trainingPlayerReloadTime = thoiGianNapNguoiChoi;
        phien.trainingBossReloadTime = thoiGianNapBoss;
        phien.trainingBossMaxHp = mauToiDaBoss;
        phien.trainingFirstTurnSent = false;
        datTrangThaiChoVongMoi(phien, chiSoNguoiChoi);
        return maPhien;
    }

    static void ketThucPhien(ChickenPhienLuyenTap phien, byte chiSoNguoiChoi) {
        phien.trainingSessionId++;
        phien.trainingFirstTurnSent = false;
        phien.trainingBotAnimating = false;
        phien.trainingBossShield = false;
        phien.trainingBossPowerShot = false;
        phien.trainingBotTurn = -1;
        phien.trainingBotTurnCount = 0;
        phien.trainingActiveBotIndex = -1;
        phien.trainingBossState = ChickenNguoiChoi.TrainingBossState.IDLE;
        phien.trainingCurrentTurn = chiSoNguoiChoi;
        phien.trainingPlayerReload = 0;
        phien.trainingBossReload = 0;
        phien.trainingTurnId = 0L;
        phien.trainingLastShotTurnId = -1L;
        phien.trainingActiveShotId = 0L;
        phien.trainingActiveShotResolved = true;
        phien.trainingMgBurstEndAt = 0L;
        phien.trainingHawkMenuTurnId = -1L;
        phien.trainingHawkSkillId++;
        phien.trainingHawkSkillActive = false;
        phien.trainingThorMenuTurnId = -1L;
        phien.trainingThorSkillId++;
        phien.trainingThorSkillActive = false;
        phien.trainingLokiMenuTurnId = -1L;
        phien.trainingLokiSkillId++;
        phien.trainingLokiSkillActive = false;
        phien.trainingLokiDangChoChonMucTieu = false;
        phien.trainingLokiDaDungKyNang = false;
        phien.trainingUltronMenuTurnId = -1L;
        phien.trainingUltronDaDungKyNang = false;
        phien.trainingUltronDangBanX3 = false;
        phien.trainingUltronGocNgamHienTai = 45;
        phien.trainingUltronLucNgamHienTai = 30;
        phien.trainingUltronDaCoGocNgam = false;
        phien.trainingDummyHp = phien.trainingBossMaxHp;

        for (int i = 0; i < phien.trainingBotHp.length; i++) {
            phien.trainingBotHp[i] = 0;
            phien.trainingBotDead[i] = true;
            phien.trainingBotX[i] = 0;
            phien.trainingBotY[i] = 0;
            phien.trainingPendingBotHitCounts[i] = 0;
        }
    }

    static void datTrangThaiChoVongMoi(ChickenPhienLuyenTap phien, byte chiSoNguoiChoi) {
        phien.trainingBotAnimating = false;
        phien.trainingBossShield = false;
        phien.trainingBossPowerShot = false;
        phien.trainingBotTurn = -1;
        phien.trainingBotTurnCount = 0;
        phien.trainingActiveBotIndex = -1;
        phien.trainingBossState = ChickenNguoiChoi.TrainingBossState.IDLE;
        phien.trainingCurrentTurn = chiSoNguoiChoi;
        phien.trainingPlayerReload = 0;
        phien.trainingBossReload = 0;
        phien.trainingTurnId = 1L;
        phien.trainingLastShotTurnId = -1L;
        phien.trainingActiveShotId = 0L;
        phien.trainingActiveShotResolved = true;
        phien.trainingMgBurstEndAt = 0L;
        phien.trainingHawkMenuTurnId = -1L;
        phien.trainingHawkSkillId++;
        phien.trainingHawkSkillActive = false;
        phien.trainingThorMenuTurnId = -1L;
        phien.trainingThorSkillId++;
        phien.trainingThorSkillActive = false;
        phien.trainingLokiMenuTurnId = -1L;
        phien.trainingLokiSkillId++;
        phien.trainingLokiSkillActive = false;
        phien.trainingLokiDangChoChonMucTieu = false;
        phien.trainingLokiDaDungKyNang = false;
        phien.trainingUltronMenuTurnId = -1L;
        phien.trainingUltronDaDungKyNang = false;
        phien.trainingUltronDangBanX3 = false;
        phien.trainingUltronGocNgamHienTai = 45;
        phien.trainingUltronLucNgamHienTai = 30;
        phien.trainingUltronDaCoGocNgam = false;
        phien.trainingPendingSelfHitCount = 0;
        phien.trainingPendingDamagePerBullet = 0;

        for (int i = 0; i < phien.trainingPendingBotHitCounts.length; i++) {
            phien.trainingPendingBotHitCounts[i] = 0;
        }
    }
}
