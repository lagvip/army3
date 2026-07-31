package com.chicken.mohinh;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenMayMan;
import com.chicken.gio.ChickenHeThongGio;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

/**
 * Giữ toàn bộ trạng thái thay đổi trong một phiên luyện tập.
 *
 * Class này chưa chứa logic chiến đấu. ChickenNguoiChoi vẫn điều khiển hành vi
 * như trước, nhưng dữ liệu phiên không còn nằm rải rác trong dữ liệu nhân vật.
 */
final class ChickenPhienLuyenTap {
    int trainingDummyHp;
    int trainingPlayerMaxHp;
    int trainingPlayerHp;
    short trainingPlayerX = 220;
    short trainingPlayerY = 300;
    short trainingDummyX = 600;
    short trainingDummyY = 300;
    int trainingBossMaxHp;
    int trainingWins;

    final transient ChickenQuanLyBanDo trainingMap;
    final int[] trainingBotHp;
    final short[] trainingBotX;
    final short[] trainingBotY;
    final boolean[] trainingBotDead;
    final int[] trainingPendingBotHitCounts;
    final int[] trainingPendingBotDamages;

    int trainingBotTurn = -1;
    boolean trainingWaitingShotEnd;
    int trainingPendingSelfHitCount;
    int trainingPendingSelfDamage;
    /** Lần quay May mắn đã chốt trước packet đạn của phát hiện tại. */
    transient ChickenChienBinh[] trainingPendingMayManTargets;
    transient ChickenMayMan.PhienTanCong trainingPendingMayMan;
    /**
     * Snapshot server cua sung/vat pham trong Balo luc bat dau phien.
     * Dung rieng cho viec xac thuc CMD 26; khong dung lam nguon HP/toa do.
     */
    transient ChickenChienBinh trainingVatPhamChienBinh;
    boolean trainingFirstTurnSent;
    boolean trainingBotAnimating;
    boolean trainingBossShield;
    boolean trainingBossPowerShot;
    int trainingBotTurnCount;
    ChickenNguoiChoi.TrainingBossState trainingBossState = ChickenNguoiChoi.TrainingBossState.IDLE;
    byte trainingCurrentTurn;
    int trainingPlayerReload;
    int trainingBossReload;
    long trainingPlayerReloadOrder;
    long trainingBossReloadOrder;
    long trainingReloadOrderCounter;
    int trainingPlayerReloadTime;
    /** Quãng đường chủ động còn lại của người chơi trong lượt. */
    int trainingMoveRemaining;
    int trainingBossReloadTime;
    /** ID súng shop được server chốt một lần khi bắt đầu phiên. */
    int trainingBossWeaponId = -1;
    /** Part hiển thị tương ứng đúng với ID súng đã chốt. */
    short trainingBossWeaponPart = -1;
    /** Damage gốc lấy từ option tấn công của chính template súng shop. */
    int trainingBossAttack;
    int trainingPendingDamagePerBullet;
    int trainingActiveBotIndex = -1;

    ScheduledFuture<?> trainingBotTask;
    ScheduledFuture<?> trainingBotReturnTask;
    ScheduledFuture<?> trainingPlayerResolveTask;
    ScheduledFuture<?> trainingGravityTask;
    /** CMD79 X3 den som duoc hen xu ly, khong bi bo mat. */
    ScheduledFuture<?> trainingUltronX3XacNhanTask;

    long trainingSessionId;
    /** Khóa idempotency server phát riêng cho phần thưởng của phiên hiện tại. */
    String trainingRewardOperationKey;
    long trainingTurnId;
    long trainingLastShotTurnId = -1L;
    long trainingActiveShotId;
    boolean trainingActiveShotResolved = true;
    /** Moc server som nhat chap nhan CMD 79 cho packet dan hien tai. */
    long trainingXacNhanDanSomNhatMs;
    ChickenHeThongGio.TrangThaiGio trainingWind = ChickenHeThongGio.khongGio();
    long trainingMgBurstEndAt;
    long trainingMgBurstShotId = -1L;
    int trainingMgBurstGunId = -1;

    // Trạng thái riêng của skill Hawk trong luyện tập.
    long trainingHawkMenuTurnId = -1L;
    long trainingHawkSkillId;
    boolean trainingHawkSkillActive;

    // Trạng thái riêng của skill Thor trong luyện tập.
    long trainingThorMenuTurnId = -1L;
    long trainingThorSkillId;
    boolean trainingThorSkillActive;

    // Trạng thái riêng của skill Loki trong luyện tập.
    long trainingLokiMenuTurnId = -1L;
    long trainingLokiSkillId;
    boolean trainingLokiSkillActive;
    boolean trainingLokiDangChoChonMucTieu;
    boolean trainingLokiDaDungKyNang;

    // Trạng thái riêng của skill Ultron trong luyện tập.
    long trainingUltronMenuTurnId = -1L;
    boolean trainingUltronDaDungKyNang;
    boolean trainingUltronDangBanX3;
    short trainingUltronGocNgamHienTai = 45;
    byte trainingUltronLucNgamHienTai = 30;
    boolean trainingUltronDaCoGocNgam;
    /** CMD 79 được client gửi sau từng viên của Ultron X3, không chỉ cuối loạt. */
    long trainingUltronX3ShotId = -1L;
    int trainingUltronX3VaChamDaNhan;
    int trainingUltronX3PhatDaGui;
    byte trainingUltronX3LoaiDan;
    short trainingUltronX3Goc;
    byte trainingUltronX3Luc;
    /** Vị trí vật lý server dùng làm anchor cho cả ba packet X3. */
    short trainingUltronX3ShooterX;
    short trainingUltronX3ShooterY;
    short[][] trainingUltronX3DuongX;
    short[][] trainingUltronX3DuongY;

    // Trang thai skill laser nguc Iron Man trong luyen tap.
    long trainingIronManMenuTurnId = -1L;
    boolean trainingIronManDaDungKyNang;
    boolean trainingIronManLaserSanSang;

    int trainingMgBurstTotal;
    int trainingMgBurstSent;
    final transient ArrayList<ScheduledFuture<?>> trainingMgBurstTasks = new ArrayList<>();

    ChickenPhienLuyenTap(byte mapId, int botCount, int playerMaxHp, int bossBaseHp, byte playerIndex) {
        this.trainingMap = new ChickenQuanLyBanDo(mapId);
        this.trainingPlayerMaxHp = playerMaxHp;
        this.trainingPlayerHp = playerMaxHp;
        this.trainingDummyHp = bossBaseHp;
        this.trainingBossMaxHp = bossBaseHp;
        this.trainingCurrentTurn = playerIndex;

        this.trainingBotHp = new int[botCount];
        this.trainingBotX = new short[botCount];
        this.trainingBotY = new short[botCount];
        this.trainingBotDead = new boolean[botCount];
        this.trainingPendingBotHitCounts = new int[botCount];
        this.trainingPendingBotDamages = new int[botCount];
    }
}
