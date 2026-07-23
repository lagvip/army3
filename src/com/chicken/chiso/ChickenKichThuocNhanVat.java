package com.chicken.chiso;

/**
 * Vùng va chạm nhân vật theo đúng cách bản Chicken_lt(106) đang dùng.
 * x là tâm ngang; y là tọa độ chân. Hitbox phủ toàn bộ thân người từ đầu
 * tới sát chân nhưng chỉ nằm trong bề rộng cơ thể, không tính phần súng chìa ra.
 * Tọa độ đầu được hạ xuống đúng vùng sprite cơ thể để đạn chưa chạm pixel người
 * thì không bị tính va chạm ở khoảng trống phía trên đầu.
 */
public final class ChickenKichThuocNhanVat {

    // CPlayer của client PC: Rectangle(x - 12, y - 36, 24, 36).
    // Giữ toàn bộ phía server cùng một hệ toạ độ để đạn, nổ và va chạm khớp nhau.
    public static final int NGUOI_CHOI_NUA_RONG = 12;
    public static final int NGUOI_CHOI_LECH_TREN = 36;
    public static final int NGUOI_CHOI_LECH_DUOI = 0;
    public static final int NGUOI_CHOI_RONG = NGUOI_CHOI_NUA_RONG * 2 + 1;
    public static final int NGUOI_CHOI_CAO = NGUOI_CHOI_LECH_TREN - NGUOI_CHOI_LECH_DUOI + 1;

    public static final int BOSS_NUA_RONG = 12;
    public static final int BOSS_LECH_TREN = 36;
    public static final int BOSS_LECH_DUOI = 0;
    public static final int BOSS_RONG = BOSS_NUA_RONG * 2 + 1;
    public static final int BOSS_CAO = BOSS_LECH_TREN - BOSS_LECH_DUOI + 1;

    private ChickenKichThuocNhanVat() {
    }

    public static boolean trungNguoiChoi(int danX, int danY, int nhanVatX, int nhanVatY) {
        return namTrongVungThan(
                danX,
                danY,
                nhanVatX,
                nhanVatY,
                NGUOI_CHOI_NUA_RONG,
                NGUOI_CHOI_LECH_TREN,
                NGUOI_CHOI_LECH_DUOI
        );
    }

    public static boolean trungBoss(int danX, int danY, int bossX, int bossY) {
        return namTrongVungThan(
                danX,
                danY,
                bossX,
                bossY,
                BOSS_NUA_RONG,
                BOSS_LECH_TREN,
                BOSS_LECH_DUOI
        );
    }

    public static int layTamThanNguoiChoiY(int chanY) {
        int tren = chanY - NGUOI_CHOI_LECH_TREN;
        int duoi = chanY - NGUOI_CHOI_LECH_DUOI;
        return (tren + duoi) / 2;
    }


    public static double khoangCachDenNguoiChoi(int diemX, int diemY, int nhanVatX, int nhanVatY) {
        return khoangCachDenVungThan(
                diemX, diemY, nhanVatX, nhanVatY,
                NGUOI_CHOI_NUA_RONG, NGUOI_CHOI_LECH_TREN, NGUOI_CHOI_LECH_DUOI
        );
    }

    public static double khoangCachDenBoss(int diemX, int diemY, int bossX, int bossY) {
        return khoangCachDenVungThan(
                diemX, diemY, bossX, bossY,
                BOSS_NUA_RONG, BOSS_LECH_TREN, BOSS_LECH_DUOI
        );
    }

    private static double khoangCachDenVungThan(
            int diemX, int diemY, int tamX, int chanY,
            int nuaRong, int lechTren, int lechDuoi
    ) {
        int trai = tamX - nuaRong;
        int phai = tamX + nuaRong;
        int tren = chanY - lechTren;
        int duoi = chanY - lechDuoi;
        int dx = diemX < trai ? trai - diemX : (diemX > phai ? diemX - phai : 0);
        int dy = diemY < tren ? tren - diemY : (diemY > duoi ? diemY - duoi : 0);
        return Math.hypot(dx, dy);
    }

    private static boolean namTrongVungThan(
            int diemX,
            int diemY,
            int tamX,
            int chanY,
            int nuaRong,
            int lechTren,
            int lechDuoi
    ) {
        int trai = tamX - nuaRong;
        int phai = tamX + nuaRong;
        int tren = chanY - lechTren;
        int duoi = chanY - lechDuoi;

        return diemX >= trai && diemX <= phai
                && diemY >= tren && diemY <= duoi;
    }
}
