package com.chicken.mang;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mohinh.ChickenNguoiDung;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.sanhcho.VaoSanhChoBoss;

/**
 * Hoi quy cho cong trang thai packet cua phong boss.
 */
public final class ChickenXuLyTinTestSupport {
    private static final int[] LENH_CAM = {
        -98, -28, 6, 7, 8, 16, 18, 20, 71, 75, 83
    };
    private static final int[] LENH_HOP_LE_TRONG_TRAN = {
        5, 15, 21, 22, 23, 53, 79, 91, -91, 126
    };

    private ChickenXuLyTinTestSupport() {
    }

    public static void tuKiemTra() throws Exception {
        for (int cmd : LENH_CAM) {
            dung(ChickenXuLyTin.laLenhCamKhiDangDanhBoss(cmd),
                    "thieu lenh chuyen trang thai cmd=" + cmd);
        }
        for (int cmd : LENH_HOP_LE_TRONG_TRAN) {
            dung(!ChickenXuLyTin.laLenhCamKhiDangDanhBoss(cmd),
                    "chan nham lenh chien dau/thoat cmd=" + cmd);
        }

        QuanLySanhChoBoss.khoiTao();
        PhienBatNgatKetNoi phien =
                new PhienBatNgatKetNoi(97_083);
        ChickenDichVuGame dichVu =
                (ChickenDichVuGame) phien.layDichVu();
        ChickenNguoiDung user = new ChickenNguoiDung(phien, dichVu);
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 97_083;
        nguoiChoi.ten = "BossStateSecurity";
        user.nguoiChoi = nguoiChoi;
        phien.user = user;

        try {
            dung(VaoSanhChoBoss.xuLy(nguoiChoi, 0, ""),
                    "khong tao duoc sanh boss test");
            SanhChoBoss sanh =
                    QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
            dung(sanh != null, "khong tim thay sanh boss test");
            sanh.setTrangThai(SanhChoBoss.TrangThai.DANG_CHIEN);
            dung(ChickenXuLyTin.dangDanhBoss(sanh),
                    "khong nhan trang thai boss dang chien");

            ChickenXuLyTin router = new ChickenXuLyTin(phien);
            for (int cmd : LENH_CAM) {
                phien.datLaiDauNgat();
                router.khiCoTin(new ChickenTinNhan(
                        (byte) cmd, new byte[0]));
                dung(phien.daBiNgat(),
                        "lenh trai phep khong ngat ket noi cmd=" + cmd);
                dung(!nguoiChoi.inTraining,
                        "CMD trai phep tao duoc phien luyen tap cmd=" + cmd);
            }
        } finally {
            QuanLySanhChoBoss.khoiTao();
        }
    }

    private static void dung(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private static final class PhienBatNgatKetNoi extends ChickenPhien {
        private boolean daBiNgat;

        private PhienBatNgatKetNoi(int ma) {
            super(null, ma);
        }

        @Override
        public void dongTin() {
            this.daBiNgat = true;
        }

        private boolean daBiNgat() {
            return this.daBiNgat;
        }

        private void datLaiDauNgat() {
            this.daBiNgat = false;
        }
    }
}
