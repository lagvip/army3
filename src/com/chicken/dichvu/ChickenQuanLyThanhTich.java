package com.chicken.dichvu;

import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mohinh.ChickenNguoiDung;
import com.chicken.tienich.ChickenTienIch;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ChickenQuanLyThanhTich {
    private static final String[][] THANH_TICH = new String[][]{
        {"0", "Tân binh", "Tham gia 1 trận đấu", "1000", "1"},
        {"1", "Chiến binh", "Hạ gục 10 đối thủ", "5000", "2"},
        {"2", "Cúp vàng", "Đạt 100 cúp", "10000", "3"},
        {"3", "Luyện tập", "Thắng luyện tập 1 lần", "2000", "1"},
        {"4", "Đồng đội", "Hỗ trợ hạ gục 5 lần", "3000", "2"},
        {"5", "Cao thủ", "Đạt cấp 50", "20000", "3"}
    };

    private ChickenQuanLyThanhTich() {
    }

    public static void xuLy(ChickenNguoiDung nguoiDung, ChickenTinNhan tinNhan) throws IOException {
        byte hanhDong = tinNhan.boDoc().available() > 0 ? tinNhan.boDoc().readByte() : 0;
        if (hanhDong == 1 && tinNhan.boDoc().available() > 0) {
            nhanThuong(nguoiDung, tinNhan.boDoc().readByte());
            return;
        }
        guiDanhSach(nguoiDung);
    }

    private static void guiDanhSach(ChickenNguoiDung nguoiDung) throws IOException {
        ChickenNguoiChoi nguoiChoi = nguoiDung.nguoiChoi;
        ChickenTinNhan ketQua = new ChickenTinNhan(88);
        DataOutputStream ghi = ketQua.boGhi();
        ghi.writeByte(0);
        ghi.writeByte(THANH_TICH.length);
        for (String[] thanhTich : THANH_TICH) {
            byte id = Byte.parseByte(thanhTich[0]);
            int tienDo = tinhTienDo(nguoiChoi, id);
            int mucTieu = layMucTieu(id);
            byte coTheNhan = (byte)(!daNhan(nguoiChoi, id) && tienDo >= mucTieu ? 1 : 0);
            ghi.writeByte(id);
            ghi.writeUTF(thanhTich[1]);
            ghi.writeUTF(thanhTich[2]);
            ghi.writeByte(coTheNhan);
            ghi.writeInt(tienDo);
            ghi.writeInt(mucTieu);
            ghi.writeInt(Integer.parseInt(thanhTich[3]));
            ghi.writeByte(Byte.parseByte(thanhTich[4]));
        }
        ghi.flush();
        nguoiDung.dichVu.guiTin(ketQua);
    }

    private static void nhanThuong(ChickenNguoiDung nguoiDung, byte id) throws IOException {
        ChickenNguoiChoi nguoiChoi = nguoiDung.nguoiChoi;
        if (id < 0 || id >= THANH_TICH.length) {
            return;
        }
        if (tinhTienDo(nguoiChoi, id) < layMucTieu(id)) {
            nguoiChoi.moHopThoaiOK("Chưa hoàn thành thành tích.");
            return;
        }
        if (daNhan(nguoiChoi, id)) {
            nguoiChoi.moHopThoaiOK("Bạn đã nhận thưởng thành tích này.");
            return;
        }
        nguoiChoi.daNhanThanhTich |= 1 << id;
        int vangThuong = Integer.parseInt(THANH_TICH[id][3]);
        nguoiChoi.updateGold(vangThuong);
        nguoiChoi.flushCache();
        nguoiChoi.moHopThoaiOK("Nhận " + ChickenTienIch.dinhDangTien(vangThuong) + " vàng.");
        guiDanhSach(nguoiDung);
    }

    private static int tinhTienDo(ChickenNguoiChoi nguoiChoi, byte id) {
        switch (id) {
            case 0:
                return nguoiChoi.kill + nguoiChoi.chet > 0 ? 1 : 0;
            case 1:
                return Math.min(10, Math.max(0, nguoiChoi.kill));
            case 2:
                return Math.min(100, Math.max(0, nguoiChoi.cup));
            case 3:
                return nguoiChoi.laySoTranThangLuyenTap() > 0 ? 1 : 0;
            case 4:
                return Math.min(5, Math.max(0, nguoiChoi.assist));
            case 5:
                return Math.min(50, Math.max(0, nguoiChoi.cap));
            default:
                return 0;
        }
    }

    private static int layMucTieu(byte id) {
        switch (id) {
            case 0:
            case 3:
                return 1;
            case 1:
                return 10;
            case 2:
                return 100;
            case 4:
                return 5;
            case 5:
                return 50;
            default:
                return 1;
        }
    }

    private static boolean daNhan(ChickenNguoiChoi nguoiChoi, byte id) {
        return (nguoiChoi.daNhanThanhTich & 1 << id) != 0;
    }
}
