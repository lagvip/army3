package com.chicken.chien;

import com.chicken.chiso.ChickenKichThuocNhanVat;

/**
 * Quy tac sieu cao dung chung cho ca sat thuong phia server va hieu ung client.
 */
public final class ChickenSieuCao {

    public static final int DO_ROI_TOI_THIEU = 350;
    public static final int PHAN_TRAM_SAT_THUONG = 120;

    private ChickenSieuCao() {
    }

    @FunctionalInterface
    public interface KiemTraHitbox {
        boolean trung(int danX, int danY);
    }

    /**
     * Danh gia sieu cao tren quy dao hinh hoc khong bi dia hinh cat ngan.
     * Duong dan phai cat dung hitbox muc tieu sau khi da roi hon 350 px tu
     * dinh; chi bay cao hoac no gan muc tieu khong duoc tinh.
     */
    public static boolean laPhatSieuCaoTrungMucTieu(
            byte loaiDan,
            short[] duongXKhongDiaHinh,
            short[] duongYKhongDiaHinh,
            int mucTieuX,
            int mucTieuY,
            boolean mucTieuLaBoss
    ) {
        return laPhatSieuCaoTrungMucTieu(
                loaiDan,
                duongXKhongDiaHinh,
                duongYKhongDiaHinh,
                mucTieuX,
                mucTieuY,
                (danX, danY) -> mucTieuLaBoss
                        ? ChickenKichThuocNhanVat.trungBoss(
                                danX, danY, mucTieuX, mucTieuY)
                        : ChickenKichThuocNhanVat.trungNguoiChoi(
                                danX, danY, mucTieuX, mucTieuY)
        );
    }

    public static boolean laPhatSieuCaoTrungMucTieu(
            byte loaiDan,
            short[] duongXKhongDiaHinh,
            short[] duongYKhongDiaHinh,
            int mucTieuX,
            int mucTieuY,
            KiemTraHitbox kiemTraHitbox
    ) {
        int chiSoDinh = timChiSoDinhHinhHoc(
                loaiDan, duongXKhongDiaHinh, duongYKhongDiaHinh);
        if (chiSoDinh < 0 || kiemTraHitbox == null) {
            return false;
        }

        int soDiem = Math.min(
                duongXKhongDiaHinh.length,
                duongYKhongDiaHinh.length
        );
        int yDinh = duongYKhongDiaHinh[chiSoDinh];
        for (int i = chiSoDinh + 1; i < soDiem; i++) {
            int x1 = duongXKhongDiaHinh[i - 1];
            int y1 = duongYKhongDiaHinh[i - 1];
            int x2 = duongXKhongDiaHinh[i];
            int y2 = duongYKhongDiaHinh[i];
            int soBuoc = Math.max(
                    1,
                    Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1))
            );
            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                double tiLe = (double) buoc / (double) soBuoc;
                int danX = (int) Math.round(x1 + (x2 - x1) * tiLe);
                int danY = (int) Math.round(y1 + (y2 - y1) * tiLe);
                if (danY - yDinh <= DO_ROI_TOI_THIEU) {
                    continue;
                }
                if (kiemTraHitbox.trung(danX, danY)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int tangSatThuong(int satThuong) {
        if (satThuong <= 0) {
            return 0;
        }
        long daTang = ((long) satThuong * PHAN_TRAM_SAT_THUONG + 50L) / 100L;
        return (int) Math.min(Integer.MAX_VALUE, Math.max(satThuong, daTang));
    }

    /** Tim dinh de dat hieu ung; khong tu ket luan day la phat sieu cao. */
    public static int timChiSoDinhHinhHoc(
            byte loaiDan,
            short[] duongX,
            short[] duongY
    ) {
        if (!laLoaiDanSieuCao(loaiDan) || duongX == null || duongY == null) {
            return -1;
        }
        int soDiem = Math.min(duongX.length, duongY.length);
        if (soDiem < 3) {
            return -1;
        }

        int chiSoDinh = 0;
        int yDinh = duongY[0];
        for (int i = 1; i < soDiem; i++) {
            if (duongY[i] < yDinh) {
                yDinh = duongY[i];
                chiSoDinh = i;
            }
        }
        return chiSoDinh <= 0 || chiSoDinh >= soDiem - 1
                ? -1
                : chiSoDinh;
    }

    private static boolean laLoaiDanSieuCao(byte loaiDan) {
        switch (loaiDan) {
            case 0:
            case 1:
            case 2:
            case 9:
            case 10:
            case 11:
            case 19:
                return true;
            default:
                return false;
        }
    }
}
