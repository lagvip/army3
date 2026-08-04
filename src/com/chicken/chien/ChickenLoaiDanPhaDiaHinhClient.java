package com.chicken.chien;

/**
 * Chuyển loại đạn trong packet CMD 22/84 thành loại Bullet thực sự tạo lỗ
 * trên client gốc. Một số item/súng là chuỗi nhiều giai đoạn: viên đầu chỉ
 * gọi hiệu ứng, các đường tiếp theo mới là đạn nổ và dùng mặt nạ khác.
 */
public final class ChickenLoaiDanPhaDiaHinhClient {

    public static final int KHONG_PHA_DIA_HINH = -1;

    private ChickenLoaiDanPhaDiaHinhClient() {
    }

    public static int layLoaiDanTaoLo(byte loaiDanPacket, int chiSoDuong) {
        int loai = loaiDanPacket & 0xFF;
        int duong = Math.max(0, chiSoDuong);
        switch (loai) {
            case 5:  // Dich chuyen: Bullet.explode chi doi X/Y, khong makeHole.
            case 36: // Bien the dich chuyen cua client cung khong pha dia hinh.
            case 13: // Voi rong chi tao Tornado, khong goi CMap.makeHole().
                return KHONG_PHA_DIA_HINH;
            case 4:  // B52: marker type 4 -> bom type 3.
                return duong == 0 ? KHONG_PHA_DIA_HINH : 3;
            case 14: // Laser: marker type 14 -> tia type 15.
                return duong == 0 ? KHONG_PHA_DIA_HINH : 15;
            case 16: // Cối: marker type 16 -> loạt rơi type 12.
                return duong == 0 ? KHONG_PHA_DIA_HINH : 12;
            case 17: // Rìu: viên đầu type 17 -> mảnh nổ type 18.
                return duong == 0 ? 17 : 18;
            case 19: // Gà: quả trứng type 19 -> gà con type 20.
                return duong == 0 ? 19 : 20;
            case 23: // Mưa sao băng: marker type 23 -> thiên thạch type 24.
                return duong == 0 ? KHONG_PHA_DIA_HINH : 24;
            case 26: // Tên lửa mẹ type 26 -> bốn tên lửa type 27.
                return duong == 0 ? 26 : 27;
            case 28: // Mưa tên lửa: marker type 28 -> đạn rơi type 29.
                return duong == 0 ? KHONG_PHA_DIA_HINH : 29;
            default:
                return loai;
        }
    }
}
