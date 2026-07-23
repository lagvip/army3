package com.chicken.chien;

import com.chicken.vatpham.ChickenMauVatPham;

/**
 * Dữ liệu súng/đạn tương ứng với protocol của client Mobi Army 3.7.1.
 *
 * bullType là giá trị gửi trong packet -40 (BulletForGun). Client đổi bullType
 * sang bulletType chiến đấu theo bảng trong CPlayer.u(). soVienMoiLoat là số
 * mảng quỹ đạo client cần cho một lần bắn khi packet 84 dùng typeShoot = 1.
 * Trong bản client đang sử dụng, nhóm MG hiển thị 5 viên cho mỗi lần bắn.
 * Server phải gửi đủ 5 quỹ đạo và xử lý va chạm riêng cho từng viên.
 */
public final class ChickenDuLieuDanSung {
    private static final ChickenDuLieuDanSung AT =
            new ChickenDuLieuDanSung((byte) 0, (byte) 0, (byte) 1);
    private static final ChickenDuLieuDanSung AK =
            new ChickenDuLieuDanSung((byte) 1, (byte) 1, (byte) 2);
    private static final ChickenDuLieuDanSung MG =
            new ChickenDuLieuDanSung((byte) 5, (byte) 11, (byte) 5);
    private static final ChickenDuLieuDanSung BANANA =
            new ChickenDuLieuDanSung((byte) 3, (byte) 9, (byte) 4);
    private static final ChickenDuLieuDanSung SHOTGUN =
            new ChickenDuLieuDanSung((byte) 4, (byte) 10, (byte) 1);
    private static final ChickenDuLieuDanSung MORTAR =
            new ChickenDuLieuDanSung((byte) 5, (byte) 11, (byte) 1);
    private static final ChickenDuLieuDanSung CHICKEN =
            new ChickenDuLieuDanSung((byte) 6, (byte) 19, (byte) 1);
    private static final ChickenDuLieuDanSung BOOMERANG =
            new ChickenDuLieuDanSung((byte) 7, (byte) 21, (byte) 1);
    private static final ChickenDuLieuDanSung APACHE =
            new ChickenDuLieuDanSung((byte) 8, (byte) 17, (byte) 1);
    private static final ChickenDuLieuDanSung LASER =
            new ChickenDuLieuDanSung((byte) 9, (byte) 49, (byte) 1);

    private final byte loaiHinhDan;
    private final byte loaiDan;
    private final byte soVienMoiLoat;

    private ChickenDuLieuDanSung(byte loaiHinhDan, byte loaiDan, byte soVienMoiLoat) {
        this.loaiHinhDan = loaiHinhDan;
        this.loaiDan = loaiDan;
        this.soVienMoiLoat = soVienMoiLoat;
    }

    public byte getLoaiHinhDan() {
        return this.loaiHinhDan;
    }

    public byte getLoaiDan() {
        return this.loaiDan;
    }

    public byte getSoVienMoiLoat() {
        return this.soVienMoiLoat;
    }

    public static ChickenDuLieuDanSung theoMauSung(ChickenMauVatPham mauSung) {
        if (mauSung == null || mauSung.loai != 5) {
            return AT;
        }
        return theoNhomSung(mauSung.gioiTinh);
    }

    /**
     * Trường gender của item súng trong database chính là nhóm vũ khí:
     * 0 AT, 1 AK, 2 Shotgun, 3 Banana, 4 Mortar, 5 MG,
     * 6 Chicken, 7 Boomerang, 8 Apache/Rìu, 9 Laser.
     */
    public static ChickenDuLieuDanSung theoNhomSung(byte nhomSung) {
        switch (nhomSung) {
            case 0:
                return AT;
            case 1:
                return AK;
            case 2:
                return SHOTGUN;
            case 3:
                return BANANA;
            case 4:
                return MORTAR;
            case 5:
                return MG;
            case 6:
                return CHICKEN;
            case 7:
                return BOOMERANG;
            case 8:
                return APACHE;
            case 9:
                return LASER;
            default:
                return AT;
        }
    }
}
