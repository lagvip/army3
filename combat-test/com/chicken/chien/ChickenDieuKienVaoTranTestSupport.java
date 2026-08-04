package com.chicken.chien;

import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.nhapvai.ChickenKhu;
import com.chicken.phong.ChickenChoDau;
import com.chicken.phong.ChickenPhong;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.sanhcho.VaoSanhChoBoss;
import com.chicken.phong.boss.trandau.VaoTranBoss;
import com.chicken.vatpham.ChickenMauThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenVatPham;
import java.util.ArrayList;
import java.util.List;

/** Hồi quy cho trường hợp tụt cấp làm súng đang mặc không còn dùng được. */
public final class ChickenDieuKienVaoTranTestSupport {
    private ChickenDieuKienVaoTranTestSupport() {
    }

    public static void tuKiemTra() throws Exception {
        kiemTraSungMatHieuLucKhiTutCap();
        kiemTraLuyenTapTraVeRpg();
        kiemTraPvpKhongTaoTran();
        kiemTraBossKhongTaoTranVaKhongTruPhi();
    }

    private static void kiemTraSungMatHieuLucKhiTutCap() {
        ChickenNguoiChoi nguoiChoi =
                new ChickenNguoiChoi(new DichVuBatPacket());
        ChickenVatPham sung = new ChickenVatPham(110);
        sung.ma = 110;
        sung.chiSo = 5;
        sung.mau = new ChickenMauVatPham(
                (short) 110, (byte) 5, (byte) 0,
                "AT4 cap 10", "", (byte) 1, 10,
                (short) 839, (short) 57, false);
        sung.itemOptions.add(taoOption(1, 500));
        sung.itemOptions.add(taoOption(14, 300));
        nguoiChoi.itemBody[5] = sung;

        nguoiChoi.cap = 9;
        bang(ChickenDieuKienVaoTran.LOI_SUNG,
                ChickenDieuKienVaoTran.layLoi(nguoiChoi),
                "tụt cấp vẫn được dùng súng vượt cấp");
        nguoiChoi.cap = 10;
        dung(nguoiChoi.laySungTrangBiMayChu() == sung,
                "đủ cấp nhưng server vẫn loại súng");
        dung(ChickenDieuKienVaoTran.layLoi(nguoiChoi) == null,
                "đủ cấp và đủ dữ liệu nhưng tiền kiểm vẫn chặn");
    }

    private static ChickenThuocTinhVatPham taoOption(int ma, int thamSo) {
        ChickenMauThuocTinhVatPham mau =
                new ChickenMauThuocTinhVatPham();
        mau.ma = ma;
        ChickenThuocTinhVatPham option =
                new ChickenThuocTinhVatPham(ma, thamSo);
        option.optionTemplate = mau;
        return option;
    }

    private static void kiemTraLuyenTapTraVeRpg() {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = taoNguoiChoi(96_101, dichVu);
        ChickenKhu khu = new ChickenKhu(7);
        dung(khu.vao(nguoiChoi), "không đưa được người test vào RPG");
        dichVu.xoaTin();

        nguoiChoi.vaoLuyenTap();

        dung(!nguoiChoi.inTraining,
                "không có súng vẫn tạo phiên luyện tập");
        dung(nguoiChoi.zone == khu,
                "luyện tập bị từ chối nhưng server lại xóa người khỏi RPG");
        bang(0, dichVu.demLenh(20),
                "luyện tập bị từ chối vẫn gửi CMD 20");
        bang(1, dichVu.demLenh(-98),
                "không gửi lại bản ghi RPG để thoát WaitingScr");
        bang(1, dichVu.demLenh(10),
                "luyện tập bị từ chối không báo lý do");
        dung(dichVu.viTriLenhDau(-98) < dichVu.viTriLenhDau(10),
                "hộp thoại được gửi trước packet khôi phục GameScrRPG");
    }

    private static void kiemTraPvpKhongTaoTran() throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = taoNguoiChoi(96_102, dichVu);
        ChickenPhong phong = new ChickenPhong(
                1, 1, (byte) 0, (byte) 2, (byte) 0);
        ChickenChoDau ban = phong.banChos[0];
        dung(ban.vao(nguoiChoi, ""),
                "không đưa được người test vào phòng PvP");
        dichVu.xoaTin();

        ban.batDau(nguoiChoi);

        dung(!ban.started && ban.layTranDau() == null,
                "không có súng vẫn tạo trận PvP");
        bang(0, dichVu.demLenh(20),
                "PvP bị từ chối vẫn gửi CMD 20");
        bang(1, dichVu.demLenh(10),
                "PvP bị từ chối không báo lý do");
        ban.roi(nguoiChoi);
    }

    private static void kiemTraBossKhongTaoTranVaKhongTruPhi()
            throws Exception {
        QuanLySanhChoBoss.khoiTao();
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = taoNguoiChoi(96_103, dichVu);
        nguoiChoi.vang = 10_000;
        dung(VaoSanhChoBoss.xuLy(nguoiChoi, 0, ""),
                "không đưa được người test vào sảnh boss");
        SanhChoBoss sanh =
                QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
        dung(sanh != null, "mất sảnh boss trước khi bắt đầu");
        int vangTruoc = nguoiChoi.vang;
        dichVu.xoaTin();

        VaoTranBoss.xuLy(nguoiChoi);

        bang(SanhChoBoss.TrangThai.DANG_CHO, sanh.getTrangThai(),
                "không có súng vẫn đổi trạng thái phòng boss");
        bang(vangTruoc, nguoiChoi.vang,
                "boss trừ phí trước khi kiểm tra trang bị");
        bang(0, dichVu.demLenh(20),
                "boss bị từ chối vẫn gửi CMD 20");
        bang(1, dichVu.demLenh(10),
                "boss bị từ chối không báo lý do");
        QuanLySanhChoBoss.khoiTao();
    }

    private static ChickenNguoiChoi taoNguoiChoi(
            int ma,
            DichVuBatPacket dichVu
    ) {
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = ma;
        nguoiChoi.ten = "Preflight" + ma;
        return nguoiChoi;
    }

    private static void dung(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private static void bang(Object mongDoi, Object thucTe,
            String thongBao) {
        if (mongDoi == null ? thucTe != null : !mongDoi.equals(thucTe)) {
            throw new AssertionError(thongBao
                    + " expected=" + mongDoi + " actual=" + thucTe);
        }
    }

    private static final class DichVuBatPacket extends ChickenDichVuGame {
        private final List<ChickenTinNhan> tins = new ArrayList<>();

        private DichVuBatPacket() {
            super(null);
        }

        @Override
        public synchronized void guiTin(ChickenTinNhan tin) {
            if (tin != null) {
                this.tins.add(tin);
            }
        }

        private synchronized int demLenh(int lenh) {
            int dem = 0;
            for (ChickenTinNhan tin : this.tins) {
                if (tin.layLenh() == (byte) lenh) {
                    dem++;
                }
            }
            return dem;
        }

        private synchronized int viTriLenhDau(int lenh) {
            for (int i = 0; i < this.tins.size(); i++) {
                if (this.tins.get(i).layLenh() == (byte) lenh) {
                    return i;
                }
            }
            return Integer.MAX_VALUE;
        }

        private synchronized void xoaTin() {
            this.tins.clear();
        }
    }
}
