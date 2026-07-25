package com.chicken.luyentap;

import com.chicken.chien.ChickenLoatDanServer;
import com.chicken.mang.ChickenTinNhan;

/**
 * Các quy tắc độc lập của phát bắn luyện tập.
 * Không giữ trạng thái người chơi, không gửi packet và không tự đổi lượt.
 */
public final class ChickenXuLyBanLuyenTap {
    private ChickenXuLyBanLuyenTap() {
    }

    public static ChickenDuLieuPhatBanLuyenTap docPhatBan(
            ChickenTinNhan tinNhan,
            byte loaiDanTheoSung,
            boolean loaiDanTheoSungHopLe,
            boolean danHaiLuc) {
        byte[] duLieu = tinNhan == null ? null : tinNhan.layDuLieu();
        int doDaiChinhXac = danHaiLuc ? 10 : 9;
        if (!loaiDanTheoSungHopLe
                || duLieu == null
                || duLieu.length != doDaiChinhXac) {
            return null;
        }

        int viTri = 0;
        viTri++; // Loại đạn do client gửi không được dùng làm nguồn tin cậy.
        short x = docShort(duLieu, viTri);
        viTri += 2;
        short y = docShort(duLieu, viTri);
        viTri += 2;
        short goc = docShort(duLieu, viTri);
        viTri += 2;
        int lucKhongDau = duLieu[viTri++] & 0xFF;
        int lucPhuKhongDau = lucKhongDau;

        if (danHaiLuc) {
            if (viTri >= duLieu.length) {
                return null;
            }
            lucPhuKhongDau = duLieu[viTri++] & 0xFF;
        }

        int soPhatKhongDau = duLieu[viTri] & 0xFF;
        byte loaiDanHopLe = loaiDanTheoSung;
        byte lucHopLe = (byte) kep(lucKhongDau, 1, 30);
        byte lucPhuHopLe = (byte) kep(lucPhuKhongDau, 1, 30);
        byte soPhatHopLe = (byte) kep(soPhatKhongDau, 1, 8);

        return new ChickenDuLieuPhatBanLuyenTap(
                loaiDanHopLe,
                x,
                y,
                chuanHoaGoc(goc),
                lucHopLe,
                lucPhuHopLe,
                soPhatHopLe
        );
    }

    /**
     * Độ lệch từng viên theo nhóm súng do server xác định.
     * Proton bắn chùm ba hướng; cối cũng có ba viên nhưng là burst cùng hướng.
     */
    public static int layDoLechGoc(
            byte nhomSung,
            int soVienMoiLoat,
            int chiSoVien
    ) {
        return ChickenLoatDanServer.layDoLechGoc(
                nhomSung,
                soVienMoiLoat,
                chiSoVien
        );
    }

    private static short docShort(byte[] duLieu, int viTri) {
        return (short) (((duLieu[viTri] & 0xFF) << 8) | (duLieu[viTri + 1] & 0xFF));
    }

    private static short chuanHoaGoc(short goc) {
        int ketQua = goc % 360;
        if (ketQua < 0) {
            ketQua += 360;
        }
        return (short) ketQua;
    }

    private static int kep(int giaTri, int nhoNhat, int lonNhat) {
        return Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }
}
