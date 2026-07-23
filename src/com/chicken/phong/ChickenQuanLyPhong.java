package com.chicken.phong;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.bando.ChickenDuLieuBanDo;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.phong.danhsach.CauHinhPhongThuong;
import com.chicken.phong.danhsach.DanhSachPhong;
import com.chicken.phong.boss.sanhcho.GoiTinSanhChoBoss;
import com.chicken.phong.boss.sanhcho.DebugSanhBoss;
import com.chicken.phong.boss.sanhcho.DoiMapBoss;
import com.chicken.phong.boss.sanhcho.DoiPheBoss;
import com.chicken.phong.boss.sanhcho.MatKhauBoss;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import com.chicken.phong.boss.sanhcho.SanSangBoss;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.sanhcho.VaoSanhChoBoss;
import com.chicken.phong.boss.trandau.VaoTranBoss;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChickenQuanLyPhong {
    private static final Map<Integer, ChickenChoDau> playerBoards = new HashMap<>();
    private static byte[] fightMaps = new byte[]{1};
    public static ChickenPhong[] phongs = new ChickenPhong[0];

    public static void khoiTao() {
        QuanLySanhChoBoss.khoiTao();
        fightMaps = taiBanDoDau();
        phongs = new ChickenPhong[CauHinhPhongThuong.SO_NHOM_PHONG];
        for (int i = 0; i < phongs.length; i++) {
            phongs[i] = new ChickenPhong(
                    i,
                    CauHinhPhongThuong.SO_BAN_MOI_NHOM,
                    (byte) 0,
                    CauHinhPhongThuong.SO_NGUOI_TOI_DA,
                    CauHinhPhongThuong.MAP_CAU_BANG
            );
            for (ChickenChoDau banCho : phongs[i].banChos) {
                banCho.maBanDo = CauHinhPhongThuong.MAP_CAU_BANG;
                banCho.tien = CauHinhPhongThuong.layTien(i);
            }
        }
    }

    public static void gan(ChickenNguoiChoi nguoiChoi, ChickenChoDau banCho) {
        synchronized (playerBoards) {
            playerBoards.put(nguoiChoi.ma, banCho);
        }
    }

    public static void boGan(ChickenNguoiChoi nguoiChoi) {
        synchronized (playerBoards) {
            playerBoards.remove(nguoiChoi.ma);
        }
    }

    public static ChickenChoDau layBanCho(ChickenNguoiChoi nguoiChoi) {
        synchronized (playerBoards) {
            return playerBoards.get(nguoiChoi.ma);
        }
    }

    public static void roiBanCho(ChickenNguoiChoi nguoiChoi) {
        ChickenChoDau banCho = layBanCho(nguoiChoi);
        if (banCho != null) {
            banCho.roi(nguoiChoi);
        }
    }

    public static void yeuCauDanhSachPhong(ChickenNguoiChoi nguoiChoi) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(6);
        DataOutputStream ds = ms.boGhi();
        for (ChickenPhong phong : phongs) {
            ds.writeByte(phong.ma);
            ds.writeByte(phong.layDoDay());
            ds.writeByte(0);
            ds.writeByte(phong.loai);
        }
        ds.flush();
        nguoiChoi.dichVu.guiTin(ms);
        nguoiChoi.dichVu.guiTieuDePhongDau();
        guiPhongTisEmpty(nguoiChoi);
    }

    public static void guiPhongTisEmpty(ChickenNguoiChoi nguoiChoi) throws IOException {
        DanhSachPhong.gui(nguoiChoi);
    }

    public static void yeuCauDanhSachBan(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        byte maPhong = ms.boDoc().readByte();
        if (maPhong == QuanLySanhChoBoss.MA_PHONG_BOSS) {
            DebugSanhBoss.log("NHAN_CMD_7_DANH_SACH_BAN", nguoiChoi,
                    "maPhong=4 soPhongBoss=" + QuanLySanhChoBoss.SO_SANH);
            guiDanhSachBanBoss(nguoiChoi);
            return;
        }
        ChickenPhong phong = layPhong(maPhong);
        if (phong == null) {
            nguoiChoi.startOKDlg2("Phòng không tồn tại.");
            return;
        }
        ChickenTinNhan out = new ChickenTinNhan(7);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(phong.ma);
        for (ChickenChoDau banCho : phong.banChos) {
            if (banCho.started || banCho.laySoNguoiChoi() >= banCho.maxPlayers) {
                continue;
            }
            ds.writeByte(banCho.ma);
            ds.writeByte(banCho.laySoNguoiChoi());
            ds.writeByte(banCho.maxPlayers);
            ds.writeBoolean(false);
            ds.writeInt(banCho.tien);
            ds.writeBoolean(true);
            ds.writeUTF(banCho.ten);
            ds.writeByte(0);
        }
        ds.flush();
        nguoiChoi.dichVu.guiTin(out);
    }

    public static void vaoBan(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        int soByteBanDau = ms.boDoc().available();
        DebugSanhBoss.log("NHAN_YEU_CAU_VAO_PHONG", nguoiChoi,
                "cmd=8 bytes=" + soByteBanDau);
        if (soByteBanDau < 2) {
            DebugSanhBoss.log("LOI_PACKET_CMD_8", nguoiChoi,
                    "thieuDuLieu can>=2 bytes thucTe=" + soByteBanDau);
            nguoiChoi.startOKDlg2("Dữ liệu vào phòng không hợp lệ.");
            return;
        }

        byte maPhong = ms.boDoc().readByte();
        byte maBan = ms.boDoc().readByte();
        int conLai = ms.boDoc().available();
        DebugSanhBoss.log("DA_CHON_PHONG", nguoiChoi,
                "maPhong=" + (maPhong & 0xFF)
                + " maBan=" + (maBan & 0xFF)
                + " ten=P" + (maPhong & 0xFF) + "-" + (maBan & 0xFF)
                + " bytesConLai=" + conLai);

        String matKhau = ms.boDoc().available() > 0 ? ms.boDoc().readUTF() : "";
        if (maPhong == QuanLySanhChoBoss.MA_PHONG_BOSS) {
            DebugSanhBoss.log("VAO_NHANH_PHONG_BOSS", nguoiChoi,
                    "P4-" + (maBan & 0xFF)
                    + " coNhapMatKhau=" + !matKhau.isEmpty());
            boolean thanhCong = VaoSanhChoBoss.xuLy(
                    nguoiChoi,
                    maBan & 0xFF,
                    matKhau
            );
            DebugSanhBoss.log("KET_QUA_VAO_PHONG_BOSS", nguoiChoi,
                    "P4-" + (maBan & 0xFF) + " thanhCong=" + thanhCong);
            return;
        }
        if (QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi) != null) {
            nguoiChoi.startOKDlg2("Bạn đang ở trong phòng boss khác.");
            return;
        }
        ChickenPhong phong = layPhong(maPhong);
        if (phong == null || maBan < 0 || maBan >= phong.banChos.length) {
            nguoiChoi.startOKDlg2("Khu vực không tồn tại.");
            return;
        }
        phong.banChos[maBan].vao(nguoiChoi, matKhau);
    }

    public static void sanSang(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        boolean giaTri = ms.boDoc().readBoolean();
        if (QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi) != null) {
            SanSangBoss.xuLy(nguoiChoi, giaTri);
            return;
        }
        ChickenChoDau banCho = layBanCho(nguoiChoi);
        if (banCho != null) {
            banCho.datSanSang(nguoiChoi, giaTri);
        }
    }

    public static void batDau(ChickenNguoiChoi nguoiChoi) throws IOException {
        if (QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi) != null) {
            VaoTranBoss.xuLy(nguoiChoi);
            return;
        }
        ChickenChoDau banCho = layBanCho(nguoiChoi);
        if (banCho != null) {
            banCho.batDau(nguoiChoi);
        }
    }

    public static void chonBanDo(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        byte maBanDo = ms.boDoc().readByte();
        SanhChoBoss sanhBoss = QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
        if (sanhBoss != null) {
            DoiMapBoss.xuLy(nguoiChoi, maBanDo & 0xFF);
            return;
        }
        ChickenChoDau banCho = layBanCho(nguoiChoi);
        if (banCho != null) {
            banCho.datBanDo(nguoiChoi, chuanHoaBanDo(maBanDo));
        }
    }

    public static void doiPhe(ChickenNguoiChoi nguoiChoi) throws IOException {
        if (QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi) != null) {
            DoiPheBoss.xuLy(nguoiChoi);
        }
    }

    public static void datMatKhau(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms)
            throws IOException {
        String matKhau = ms.boDoc().available() > 0 ? ms.boDoc().readUTF() : "";
        if (QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi) != null) {
            MatKhauBoss.xuLy(nguoiChoi, matKhau);
        }
    }

    private static void guiDanhSachBanBoss(ChickenNguoiChoi nguoiChoi) throws IOException {
        ChickenTinNhan out = new ChickenTinNhan(7);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(QuanLySanhChoBoss.MA_PHONG_BOSS);
        for (SanhChoBoss sanh : QuanLySanhChoBoss.layDanhSach()) {
            if (sanh == null || sanh.isDaBatDau()
                    || sanh.getSoNguoi() >= (sanh.getToiDa() & 0xFF)) {
                continue;
            }
            ds.writeByte(sanh.getMaBan());
            ds.writeByte(sanh.getSoNguoi());
            ds.writeByte(sanh.getToiDa());
            ds.writeBoolean(sanh.coMatKhau());
            ds.writeInt(sanh.getGiaHienThi());
            ds.writeBoolean(true);
            ds.writeUTF("Khu vực " + ((sanh.getMaBan() & 0xFF) + 1));
            ds.writeByte(0);
        }
        ds.flush();
        nguoiChoi.dichVu.guiTin(out);
    }

    public static void dauDiChuyen(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        ChickenQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.diChuyen(nguoiChoi, ms);
        }
    }

    public static void dauCapNhatXY(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        ChickenQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.capNhatXY(nguoiChoi, ms);
        }
    }

    public static void dauBan(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        ChickenQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.ban(nguoiChoi, ms);
        }
    }

    public static void dauKiemTraVaCham(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        ChickenQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.kiemTraVaCham(nguoiChoi, ms);
        }
    }

    public static void boLuot(ChickenNguoiChoi nguoiChoi) throws IOException {
        ChickenQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.boLuot(nguoiChoi);
        }
    }

    /** Chỉ chuyển CMD -91 vào trận; toàn bộ logic nằm trong ChickenKyNangDacBietHawk. */
    public static void dauKyNangHawk(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        ChickenQuanLyChien fight = layTranDau(nguoiChoi);
        if (fight != null) {
            fight.xuLyCmd91Hawk(nguoiChoi, ms);
        }
    }

    private static ChickenQuanLyChien layTranDau(ChickenNguoiChoi nguoiChoi) {
        ChickenQuanLyChien dangHoatDong = ChickenQuanLyChien.layTranDangHoatDong(nguoiChoi);
        if (dangHoatDong != null) {
            return dangHoatDong;
        }
        ChickenChoDau banCho = layBanCho(nguoiChoi);
        return banCho != null ? banCho.layTranDau() : null;
    }

    private static ChickenPhong layPhong(byte maPhong) {
        if (maPhong < 0 || maPhong >= phongs.length) {
            return null;
        }
        return phongs[maPhong];
    }

    private static byte[] taiBanDoDau() {
        ArrayList<Byte> maps = new ArrayList<>();
        if (ChickenDuLieuBanDo.entrys != null) {
            for (ChickenDuLieuBanDo.MapDataEntry muc : ChickenDuLieuBanDo.entrys) {
                maps.add(muc.mapID);
            }
        }
        if (maps.isEmpty()) {
            maps.add((byte)1);
        }
        byte[] ketQua = new byte[maps.size()];
        for (int i = 0; i < maps.size(); i++) {
            ketQua[i] = maps.get(i);
        }
        return ketQua;
    }

    private static byte chuanHoaBanDo(byte maBanDo) {
        if (fightMaps.length == 0) {
            return 1;
        }
        for (byte fightMap : fightMaps) {
            if (fightMap == maBanDo) {
                return maBanDo;
            }
        }
        if (maBanDo == 100) {
            return fightMaps[(int)(System.currentTimeMillis() % fightMaps.length)];
        }
        return fightMaps[0];
    }
}
