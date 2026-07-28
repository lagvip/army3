package com.chicken.chien;

/**
 * Danh dau doan xu ly mot yeu cau ban hop le cua nguoi choi.
 *
 * <p>Client cu gui y dinh ban ngay khi vua bat dau animation lay sung. Neu
 * server local tra CMD 22/84 trong cung nhip, CPlayer co the chuyen sang
 * PSTATE_SHOOT trong khi {@code isGetGun} van con bat va Bullet se dung mai.
 * Ngu canh nay chi la tin hieu noi bo server de lop mang chen khoang cho hien
 * thi; no khong nhan hay tin bat ky trang thai animation nao tu client.</p>
 */
public final class ChickenNguCanhLaySung {
    /**
     * Bon frame lay sung cua client cu can xap xi 360-400 ms tuy game loop.
     * Them mot bien nho de PC/JAR deu ket thuc frame truoc khi nhan ket qua.
     */
    public static final long THOI_GIAN_TOI_THIEU_MS = 420L;

    private static final ThreadLocal<Integer> DO_SAU =
            ThreadLocal.withInitial(() -> 0);

    private ChickenNguCanhLaySung() {
    }

    public static Phien batDauPhatBanNguoiChoi() {
        DO_SAU.set(DO_SAU.get() + 1);
        return new Phien();
    }

    public static boolean dangChoLaySung() {
        return DO_SAU.get() > 0;
    }

    public static final class Phien implements AutoCloseable {
        private boolean daDong;

        private Phien() {
        }

        @Override
        public void close() {
            if (this.daDong) {
                return;
            }
            this.daDong = true;
            int doSauMoi = DO_SAU.get() - 1;
            if (doSauMoi <= 0) {
                DO_SAU.remove();
            } else {
                DO_SAU.set(doSauMoi);
            }
        }
    }
}
