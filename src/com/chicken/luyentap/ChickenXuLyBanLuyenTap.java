package com.chicken.luyentap;

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
        if (duLieu == null || duLieu.length < 8) {
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

        if (danHaiLuc) {
            if (viTri >= duLieu.length) {
                return null;
            }
            viTri++; // lực phụ: cơ chế hiện tại chưa dùng trong luyện tập
        }

        int soPhatKhongDau = viTri < duLieu.length ? duLieu[viTri] & 0xFF : 1;
        byte loaiDanHopLe = loaiDanTheoSungHopLe ? loaiDanTheoSung : 0;
        byte lucHopLe = (byte) kep(lucKhongDau, 1, 30);
        byte soPhatHopLe = (byte) kep(soPhatKhongDau, 1, 8);

        return new ChickenDuLieuPhatBanLuyenTap(
                loaiDanHopLe,
                x,
                y,
                chuanHoaGoc(goc),
                lucHopLe,
                soPhatHopLe
        );
    }

    /** Độ lệch từng viên; MG và AK dùng cùng một quỹ đạo. */
    public static int layDoLechGoc(int soVienMoiLoat, int chiSoVien) {
        if (soVienMoiLoat == 5 || soVienMoiLoat == 2) {
            return 0;
        }
        if (soVienMoiLoat == 3) {
            int[] doLech = {-5, 0, 5};
            return doLech[kep(chiSoVien, 0, doLech.length - 1)];
        }
        if (soVienMoiLoat == 7) {
            return (chiSoVien - 3) * 2;
        }
        if (soVienMoiLoat == 4) {
            int[] doLech = {-6, -2, 2, 6};
            return doLech[kep(chiSoVien, 0, doLech.length - 1)];
        }
        return 0;
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
