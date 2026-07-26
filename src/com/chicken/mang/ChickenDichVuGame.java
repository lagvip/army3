package com.chicken.mang;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.chien.ChickenQuanLyDanSung.DuLieuSung;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.chien.ChickenSieuCao;
import com.chicken.avg.ChickenThanhDiChuyenAVG;
import com.chicken.avg.ChickenQuanLyNangLuongAVG;
import com.chicken.avg.ChickenTiaLaserIronMan;
import com.chicken.bando.ChickenDuLieuBanDo;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mang.IChickenDichVuGame;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mang.ChickenPhien;
import com.chicken.nhapvai.ChickenBanDoRPG;
import com.chicken.nhapvai.ChickenNhanVatPhu;
import com.chicken.cuahang.ChickenTrang;
import com.chicken.cuahang.ChickenCuaHang;
import com.chicken.tienich.ChickenTienIch;
import com.chicken.npc.chihuy.XuLyMenuChiHuy;
import com.chicken.phong.boss.sanhcho.DebugSanhBoss;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChickenDichVuGame
implements IChickenDichVuGame {
    /** Độ rơi từ đỉnh quỹ đạo để client bật hiệu ứng đạn siêu cao màu đỏ. */

    private ChickenPhien khach;
    private ChickenNguoiChoi nguoiChoi;

    public ChickenDichVuGame(ChickenPhien khach) {
        this.khach = khach;
    }

    public void datNguoiChoi(ChickenNguoiChoi nguoiChoi) {
        this.nguoiChoi = nguoiChoi;
    }

    public void ping() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-102);
        this.guiTin(ms);
    }

    public void hienTaiXuong() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-60);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(ChickenQuanLyMayChu.vBig);
        ds.writeUTF(ChickenQuanLyMayChu.dataSize[this.khach.mucPhong - 1]);
        ds.flush();
        this.guiTin(ms);
    }

    public void taiXuong() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-60);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(1);
        ds.writeByte(ChickenQuanLyMayChu.vBig);
        ds.writeShort(ChickenQuanLyMayChu.nBig[this.khach.mucPhong - 1]);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiPhienBan() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-30);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(ChickenQuanLyMayChu.vData);
        ds.writeByte(ChickenQuanLyMayChu.vItem);
        ds.writeByte(ChickenQuanLyMayChu.vMap);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiVatPham() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-32);
        DataOutputStream ds = ms.boGhi();
        ds.write(ChickenTienIch.layTep("cache/dataItem"));
        ds.flush();
        this.guiTin(ms);
    }

    public void guiBanDo() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-38);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(ChickenQuanLyMayChu.vMap);
        byte[] map = ChickenTienIch.layTep("cache/dataMap");
        ds.writeInt(map.length);
        ds.write(map);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiDuLieu() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-31);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(ChickenQuanLyMayChu.vData);
        byte[] anh = ChickenTienIch.layTep("cache/dataImage");
        ds.writeInt(anh.length);
        ds.write(anh);
        byte[] part = ChickenTienIch.layTep("cache/dataPart");
        ds.writeInt(part.length);
        ds.write(part);
        byte[] cap = ChickenTienIch.layTep("cache/dataLevel");
        ds.writeInt(cap.length);
        ds.write(cap);
        ds.flush();
        this.guiTin(ms);
    }

    public void choDangNhap(short second) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(122);
        DataOutputStream ds = ms.boGhi();
        ds.writeShort(second);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiCapNhatCup(byte loai, int cup) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-24);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(loai);
        ds.writeInt(cup);
        ds.flush();
        this.guiTin(ms);
    }

    public void moHopThoaiOK(String noiDung) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(45);
        DataOutputStream ds = ms.boGhi();
        ds.writeUTF(noiDung);
        ds.flush();
        this.guiTin(ms);
    }

    public void baoLoiTien(String noiDung) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(10);
        DataOutputStream ds = ms.boGhi();
        ds.writeUTF(noiDung);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiThongTin() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(3);
        DataOutputStream ds = ms.boGhi();
        ds.writeInt(this.nguoiChoi.ma);
        ds.writeUTF(this.nguoiChoi.ten);
        ds.writeInt(this.nguoiChoi.vang);
        ds.writeInt(this.nguoiChoi.ngoc);
        ds.writeInt(this.nguoiChoi.kinhNghiem);
        ds.writeInt(this.nguoiChoi.cup);
        ds.writeShort(this.nguoiChoi.clan);
        ds.writeInt(0);
        ds.writeInt(0);
        ds.writeInt(0);

        /*
         * Client không lưu trực tiếp trainingSuccess. Byte này bị đảo:
         *   0 -> coreLG.MyMidlet.a = true  -> cho phép tự mở phòng chờ
         *   khác 0 -> coreLG.MyMidlet.a = false -> giữ luồng luyện tập
         *
         * Dữ liệu server dùng 1 = đã hoàn thành luyện tập, vì vậy phải đảo
         * trước khi gửi. Bản cũ gửi thẳng giá trị 1 làm client luôn chặn
         * dh.a() sau khi tải đủ ảnh map, nên server nhận đủ CMD 8/CMD 126
         * nhưng màn hình phòng chờ không bao giờ xuất hiện.
         */
        byte coTrangThaiClient = (byte) (this.nguoiChoi.trainingSuccess == 1 ? 0 : 1);
        DebugSanhBoss.log("GUI_CMD_3_CO_MO_PHONG_CHO", this.nguoiChoi,
                "trainingSuccessServer=" + this.nguoiChoi.trainingSuccess
                + " byteGuiClient=" + coTrangThaiClient
                + " MyMidlet.aDuKien=" + (coTrangThaiClient == 0));
        ds.writeByte(coTrangThaiClient);
        ds.writeByte(this.nguoiChoi.busyHammer);
        ds.writeByte(this.nguoiChoi.nHammer);
        ds.flush();
        this.guiTin(ms);
    }

    public void vaoCho(ChickenNguoiChoi nguoiChoi) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-98);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(nguoiChoi.chiSo);
        ds.writeInt(nguoiChoi.ma);
        ds.writeUTF(nguoiChoi.ten);
        ds.writeShort(nguoiChoi.clan);
        ds.writeInt(nguoiChoi.kinhNghiem);
        ds.writeShort(nguoiChoi.head);
        ds.writeShort(nguoiChoi.leg);
        ds.writeShort(nguoiChoi.body);
        ds.writeShort(nguoiChoi.hat);
        ds.writeShort(nguoiChoi.wing);
        ds.writeShort(nguoiChoi.wp);
        ds.writeByte(nguoiChoi.avenger);
        ds.writeByte(nguoiChoi.isReady ? 1 : 0);
        ds.writeByte(nguoiChoi.zoneId);
        ds.writeInt(nguoiChoi.clan);
        if (nguoiChoi.clan != -1) {
            ds.writeShort(0);
        }
        ds.writeByte(nguoiChoi.pointSeat);
        ds.writeShort(nguoiChoi.x);
        ds.writeShort(nguoiChoi.y);
        ds.flush();
        this.guiTin(ms);
    }

    public void yeuCauIcon(ChickenTinNhan ms) throws IOException {
        short ma = ms.boDoc().readShort();
        ChickenTinNhan mss = new ChickenTinNhan(-41);
        DataOutputStream ds = mss.boGhi();
        ds.writeShort(ma);
        byte[] ab = ChickenTienIch.layTep("res/icon/item/" + this.khach.mucPhong + "/Small" + ma + ".png");
        ds.writeInt(ab.length);
        ds.write(ab);
        ds.flush();
        this.guiTin(mss);
    }

    public void yeuCauNguyenLieu(short ma) throws IOException {
        int maAnhGoc = ChickenDuLieuBanDo.layMaAnhMapGoc(ma & 0xFFFF);
        String duongDan = "res/icon/map/" + maAnhGoc + ".png";
        byte[] ab = ChickenTienIch.layTep(duongDan);
        if (ab == null) {
            ab = new byte[0];
        }

        if (this.nguoiChoi != null
                && QuanLySanhChoBoss.timSanhCuaNguoiChoi(this.nguoiChoi) != null) {
            DebugSanhBoss.log("GUI_CMD_126", this.nguoiChoi,
                    "materialId=" + (ma & 0xFFFF)
                    + " path=" + duongDan
                    + " bytes=" + ab.length
                    + " tonTai=" + (ab.length > 0));
        }

        ChickenTinNhan mss = new ChickenTinNhan(126);
        DataOutputStream ds = mss.boGhi();
        ds.writeShort(ma);
        ds.writeShort(ab.length);
        ds.write(ab);
        ds.flush();
        this.guiTin(mss);
    }

    public void xemCuaHang(ChickenCuaHang store) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(103);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(store.typeShop);
        int tabNumber = store.shopTabName.size();
        ds.writeByte(tabNumber);
        for (int i = 0; i < tabNumber; ++i) {
            ArrayList<ChickenTrang> pages = store.tabs.get(i);
            ds.writeByte(i);
            ds.writeUTF(store.shopTabName.get(i));
            ds.writeByte(pages.size());
            ArrayList<ChickenMauVatPham> vatPhams = pages.get((int)0).vatPhams;
            int numberItem = vatPhams.size();
            ds.writeByte(numberItem);
            for (int a = 0; a < numberItem; ++a) {
                ChickenMauVatPham vatPham = vatPhams.get(a);
                ds.writeShort(vatPham.ma);
                ds.writeInt(vatPham.buyGold);
                ds.writeInt(vatPham.buyGem);
                int numberOption = vatPham.thuocTinhs.size();
                ds.writeByte(numberOption);
                for (int b = 0; b < numberOption; ++b) {
                    ChickenThuocTinhVatPham option = (ChickenThuocTinhVatPham)vatPham.thuocTinhs.get(b);
                    ds.writeByte(option.optionTemplate.ma);
                    ds.writeShort(option.thamSo);
                }
            }
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiDoTrenNguoi() throws IOException {
        ChickenVatPham[] vatPhams = this.nguoiChoi.itemBody;
        ChickenTinNhan ms = new ChickenTinNhan(-34);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(vatPhams.length);
        for (ChickenVatPham vatPham : vatPhams) {
            if (vatPham != null) {
                ds.writeShort(vatPham.ma);
                ds.writeByte(vatPham.soLuong);
                ds.writeByte(vatPham.HP);
                ds.writeUTF("");
                ds.writeUTF("");
                int len = vatPham.itemOptions.size();
                ds.writeByte(len);
                for (int i = 0; i < len; ++i) {
                    ChickenThuocTinhVatPham option = (ChickenThuocTinhVatPham)vatPham.itemOptions.get(i);
                    ds.writeByte(option.optionTemplate.ma);
                    if (option.optionTemplate.ma == 15) {
                        int thamSo = (int)(((long)option.thamSo - System.currentTimeMillis() / 1000L) / 60L / 60L);
                        ds.writeShort(thamSo);
                        continue;
                    }
                    ds.writeShort(option.thamSo);
                }
                continue;
            }
            ds.writeShort(-1);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiRuongDo() throws IOException {
        ChickenVatPham[] vatPhams = this.nguoiChoi.itemBox;
        ChickenTinNhan ms = new ChickenTinNhan(-36);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(vatPhams.length);
        for (ChickenVatPham vatPham : vatPhams) {
            if (vatPham != null) {
                ds.writeShort(vatPham.ma);
                ds.writeByte(vatPham.soLuong);
                ds.writeByte(vatPham.HP);
                ds.writeUTF("");
                ds.writeUTF("");
                int len = vatPham.itemOptions.size();
                ds.writeByte(len);
                for (int i = 0; i < len; ++i) {
                    ChickenThuocTinhVatPham option = (ChickenThuocTinhVatPham)vatPham.itemOptions.get(i);
                    ds.writeByte(option.optionTemplate.ma);
                    if (option.optionTemplate.ma == 15) {
                        int thamSo = (int)(((long)option.thamSo - System.currentTimeMillis() / 1000L) / 60L / 60L);
                        ds.writeShort(thamSo);
                        continue;
                    }
                    ds.writeShort(option.thamSo);
                }
                continue;
            }
            ds.writeShort(-1);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiTuiDo() throws IOException {
        ChickenVatPham[] vatPhams = this.nguoiChoi.itemBag;
        ChickenTinNhan ms = new ChickenTinNhan(-35);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(vatPhams.length);
        for (ChickenVatPham vatPham : vatPhams) {
            if (vatPham != null) {
                ds.writeShort(vatPham.ma);
                ds.writeByte(vatPham.soLuong);
                ds.writeByte(vatPham.HP);
                ds.writeUTF("");
                ds.writeUTF("");
                int len = vatPham.itemOptions.size();
                ds.writeByte(len);
                for (int i = 0; i < len; ++i) {
                    ChickenThuocTinhVatPham option = (ChickenThuocTinhVatPham)vatPham.itemOptions.get(i);
                    ds.writeByte(option.optionTemplate.ma);
                    if (option.optionTemplate.ma == 15) {
                        int thamSo = (int)(((long)option.thamSo - System.currentTimeMillis() / 1000L) / 60L / 60L);
                        ds.writeShort(thamSo);
                        continue;
                    }
                    ds.writeShort(option.thamSo);
                }
                continue;
            }
            ds.writeShort(-1);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiBalo() throws IOException {
        int[] vatPhams = this.nguoiChoi.itemBalo;
        ChickenTinNhan ms = new ChickenTinNhan(-42);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(vatPhams.length);
        for (int chiSo : vatPhams) {
            ChickenVatPham vatPham = null;
            if (chiSo != -1) {
                vatPham = this.nguoiChoi.itemBag[chiSo];
            }
            if (vatPham != null) {
                ds.writeShort(vatPham.ma);
                ds.writeByte(vatPham.soLuong);
                ds.writeByte(vatPham.HP);
                ds.writeUTF("");
                ds.writeUTF("");
                int len = vatPham.itemOptions.size();
                ds.writeByte(len);
                for (int i = 0; i < len; ++i) {
                    ChickenThuocTinhVatPham option = (ChickenThuocTinhVatPham)vatPham.itemOptions.get(i);
                    ds.writeByte(option.optionTemplate.ma);
                    if (option.optionTemplate.ma == 15) {
                        int thamSo = (int)(((long)option.thamSo - System.currentTimeMillis() / 1000L) / 60L / 60L);
                        ds.writeShort(thamSo);
                        continue;
                    }
                    ds.writeShort(option.thamSo);
                }
                ds.writeByte(vatPham.chiSo);
                continue;
            }
            ds.writeShort(-1);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void capNhatTuiDo(int chiSo, int soLuong) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-35);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(2);
        ds.writeByte(chiSo);
        ds.writeByte(soLuong);
        ds.flush();
        this.guiTin(ms);
    }

    public void capNhatRuongDo(int chiSo, int soLuong) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-36);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(2);
        ds.writeByte(chiSo);
        ds.writeByte(soLuong);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiTep(String tenTep, byte[] duLieu) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-60);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(2);
        ds.writeUTF(tenTep);
        ds.writeInt(duLieu.length);
        ds.write(duLieu);
        ds.flush();
        this.guiTin(ms);
    }

    public void doiTrangBi() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-90);
        DataOutputStream ds = ms.boGhi();
        ds.writeInt(this.nguoiChoi.ma);
        ds.writeShort(this.nguoiChoi.head);
        ds.writeShort(this.nguoiChoi.leg);
        ds.writeShort(this.nguoiChoi.body);
        ds.writeShort(this.nguoiChoi.hat);
        ds.writeShort(this.nguoiChoi.wing);
        ds.writeShort(this.nguoiChoi.wp);
        ds.writeByte(this.nguoiChoi.avenger);
        ds.flush();
        this.guiTin(ms);
    }

    public void roi(int chiSo) {
        try {
            ChickenTinNhan ms = new ChickenTinNhan(-98);
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(1);
            ds.writeByte(chiSo);
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guiNhanVatPhu() {
        try {
            ChickenTinNhan ms = new ChickenTinNhan(-98);
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(10);
            ds.writeByte(ChickenBanDoRPG.npcs.size());
            for (ChickenNhanVatPhu npc : ChickenBanDoRPG.npcs) {
                ds.writeByte(npc.trangThai);
                ds.writeShort(npc.x);
                ds.writeShort(npc.y);
                ds.writeByte(npc.templateId);
                ds.writeShort(npc.anhDaiDien);
                ds.writeShort(npc.head);
                ds.writeShort(npc.body);
                ds.writeShort(npc.leg);
            }
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void capNhatKDVaKDA() {
        try {
            ChickenTinNhan ms = new ChickenTinNhan(-59);
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(0);
            ds.writeUTF(String.format("%.1f", Float.valueOf(this.nguoiChoi.layKD())));
            ds.writeUTF(String.format("%.1f", Float.valueOf(this.nguoiChoi.layKDA())));
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void capNhatAvenger() {
        try {
            ChickenTinNhan ms = new ChickenTinNhan(-59);
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(1);
            ds.writeByte(ChickenQuanLyNangLuongAVG.layNangLuong(this.nguoiChoi));
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void taoNguoiDungAo(String user) {
        try {
            ChickenTinNhan ms = new ChickenTinNhan(-58);
            DataOutputStream ds = ms.boGhi();
            ds.writeUTF(user);
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void taoNhanVat() {
        this.guiTin(new ChickenTinNhan(-99));
    }

    public void moDanhSach(String tieuDe, Vector v) {
        XuLyMenuChiHuy.xoaTrangThai(this.nguoiChoi);
        try {
            ChickenTinNhan ms = new ChickenTinNhan(-47);
            DataOutputStream ds = ms.boGhi();
            ds.writeUTF(tieuDe);
            int kichThuoc = v.size();
            ds.writeByte(kichThuoc);
            for (int i = 0; i < kichThuoc; ++i) {
                ds.writeUTF((String)v.get(i));
            }
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void capNhatSucManh() {
        try {
            ChickenTinNhan ms = new ChickenTinNhan(-59);
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(2);
            ds.writeByte(this.nguoiChoi.power);
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void capNhat() {
        try {
            ChickenTinNhan ms = new ChickenTinNhan(105);
            DataOutputStream ds = ms.boGhi();
            ds.writeInt(this.nguoiChoi.vang);
            ds.writeInt(this.nguoiChoi.ngoc);
            ds.writeByte(this.nguoiChoi.busyHammer);
            ds.writeByte(this.nguoiChoi.nHammer);
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guiTieuDePhongDau() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(88);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(1);
        ds.writeUTF("Phòng đấu");
        ds.flush();
        this.guiTin(ms);
    }

    public void guiThongTinChoDau(byte maPhong, byte maBan, String boardName, byte cap) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(76);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(maPhong);
        ds.writeByte(maBan);
        ds.writeUTF(boardName);
        ds.writeByte(cap);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiChonBanDoDau(byte maBanDo) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(75);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(maBanDo);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiNguoiChoiVaoDau(ChickenNguoiChoi joined, ChickenNguoiChoi chuPhong, byte maPhong, byte maBan) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(8);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(joined.chiSo);
        ds.writeInt(joined.ma);
        ds.writeUTF(joined.ten);
        ds.writeShort(joined.clan);
        ds.writeInt(joined.kinhNghiem);
        ds.writeShort(joined.head);
        ds.writeShort(joined.leg);
        ds.writeShort(joined.body);
        ds.writeShort(joined.hat);
        ds.writeShort(joined.wing);
        ds.writeShort(joined.wp);
        ds.writeByte(joined.avenger);
        ds.writeInt(chuPhong != null ? chuPhong.ma : joined.ma);
        ds.writeByte(maPhong);
        ds.writeByte(maBan);
        ds.writeInt(joined.clan);
        if (joined.clan != -1) {
            ds.writeShort(0);
        }
        ds.writeByte(joined.pointSeat);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiRoiDau(int playerId, int ownerId) {
        try {
            ChickenTinNhan ms = new ChickenTinNhan(14);
            DataOutputStream ds = ms.boGhi();
            ds.writeInt(playerId);
            ds.writeInt(ownerId);
            ds.flush();
            this.guiTin(ms);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guiSanSangDau(int playerId, boolean sanSang) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(16);
        DataOutputStream ds = ms.boGhi();
        ds.writeInt(playerId);
        ds.writeBoolean(sanSang);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiTienDau(int tien, byte cap) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(19);
        DataOutputStream ds = ms.boGhi();
        ds.writeShort(0);
        ds.writeInt(tien);
        ds.writeByte(cap);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiBatDauDau(byte maBanDo, ChickenChienBinh[] chienBinhs, byte maNen) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(20);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(maBanDo);
        ds.writeByte(25);
        for (int i = 0; i < 8; i++) {
            ChickenChienBinh chienBinh = i < chienBinhs.length ? chienBinhs[i] : null;
            if (chienBinh == null) {
                ds.writeShort(-1);
                continue;
            }
            ds.writeShort(chienBinh.x);
            ds.writeShort(chienBinh.y);
            ds.writeShort(chienBinh.hp);
            // Client đọc short này vào thể lực/cự ly di chuyển tối đa (CPlayer.bx).
            ds.writeShort(chienBinh.theLucDiChuyenToiDa);
        }
        ds.writeByte(maNen);
        ds.writeByte(this.demSungDau(chienBinhs));
        for (short maVuKhi : this.gomSungDau(chienBinhs)) {
            if (maVuKhi > 0) {
                ds.writeShort(maVuKhi);
            }
        }
        ds.flush();
        this.guiTin(ms);
    }

    /** Tạo chiến binh boss động tại slot 8-12 bằng packet native CMD -63. */
    public void guiTaoBossBaoVay(
            byte slot,
            int id,
            String ten,
            short head,
            short leg,
            short body,
            short hat,
            short wing,
            short vuKhi,
            short x,
            short y,
            int mauToiDa
    ) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-63);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(slot);
        ds.writeInt(id);
        ds.writeUTF(ten == null ? "Boss" : ten);
        ds.writeShort(head);
        ds.writeShort(leg);
        ds.writeShort(body);
        ds.writeShort(hat);
        ds.writeShort(wing);
        ds.writeShort(vuKhi);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.writeShort(Math.max(1, Math.min(65535, mauToiDa)));
        ds.writeByte(0);
        ds.flush();
        this.guiTin(ms);
    }

    /**
     * Tạo Boss Khí cầu map 52 bằng CMD -63.
     *
     * Byte loại cuối phải bằng 3. Client sẽ gọi setBaloonBoss(), nạp các ảnh
     * /boss/than.png, /boss/treo.png, /boss/quat1.png, /boss/quat2.png và giữ
     * thực thể ở cơ chế bay. ID -52 vẫn là CPlayer đặc biệt, không phải BigBoss.
     */
    public void guiTaoBossKhiCau(
            byte slot,
            int id,
            String ten,
            short x,
            short y,
            int mauToiDa
    ) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-63);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0); // client bỏ qua byte hành động này khi tạo thực thể
        ds.writeByte(slot);
        ds.writeInt(id);
        ds.writeUTF(ten == null ? "Boss Khí cầu" : ten);
        ds.writeShort(-1); // head
        ds.writeShort(-1); // leg
        ds.writeShort(-1); // body
        ds.writeShort(-1); // hat
        ds.writeShort(-1); // wing
        ds.writeShort(-1); // vũ khí của thân khí cầu
        ds.writeShort(x);
        ds.writeShort(y);
        ds.writeShort(Math.max(1, Math.min(65535, mauToiDa)));
        ds.writeByte(3); // native BALLOON_BOSS -> setBaloonBoss(), fh=3, bay=true
        ds.flush();
        this.guiTin(ms);
    }

    /**
     * Tạo một Phiến quân treo dưới Boss Khí cầu.
     * Byte loại cuối bằng 4 khiến client gọi g(), đặt fh=4 và tự khóa slot
     * kế tiếp vào đúng hai dây của khí cầu (slot khí cầu +1 và +2).
     */
    public void guiTaoBossDayKhiCau(
            byte slot,
            int id,
            String ten,
            short head,
            short leg,
            short body,
            short hat,
            short wing,
            short vuKhi,
            short x,
            short y,
            int mauToiDa
    ) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-63);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(slot);
        ds.writeInt(id);
        ds.writeUTF(ten == null ? "Phiến quân dây treo" : ten);
        ds.writeShort(head);
        ds.writeShort(leg);
        ds.writeShort(body);
        ds.writeShort(hat);
        ds.writeShort(wing);
        ds.writeShort(vuKhi);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.writeShort(Math.max(1, Math.min(65535, mauToiDa)));
        ds.writeByte(4); // native BALLOON_GUNNER -> g(), fh=4, bay=true
        ds.flush();
        this.guiTin(ms);
    }

    /** Tạo BigBoss Rùa tại slot 8 bằng packet native CMD -63. */
    public void guiTaoBossRua(
            byte slot,
            int id,
            String ten,
            short head,
            short leg,
            short body,
            short hat,
            short wing,
            short vuKhi,
            short x,
            short y,
            int mauToiDa
    ) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-63);
        DataOutputStream ds = ms.boGhi();
        // Byte đầu hiện bị client bỏ qua; ID -54 khiến client tạo BigBoss Rùa.
        ds.writeByte(0);
        ds.writeByte(slot);
        ds.writeInt(id);
        ds.writeUTF(ten == null ? "Boss Rùa" : ten);
        ds.writeShort(head);
        ds.writeShort(leg);
        ds.writeShort(body);
        ds.writeShort(hat);
        ds.writeShort(wing);
        ds.writeShort(vuKhi);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.writeShort(Math.max(1, Math.min(65535, mauToiDa)));
        // Client dùng byte cuối làm loại boss; 1 là BigBoss Rùa.
        ds.writeByte(1);
        ds.flush();
        this.guiTin(ms);
    }

    /** Tạo BigBoss Rồng tại slot 8 bằng packet native CMD -63. */
    public void guiTaoBossRong(
            byte slot,
            int id,
            String ten,
            short head,
            short leg,
            short body,
            short hat,
            short wing,
            short vuKhi,
            short x,
            short y,
            int mauToiDa
    ) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-63);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(slot);
        // ID -55 và type cuối = 2 khiến client tạo đúng BigBoss Rồng native.
        ds.writeInt(id);
        ds.writeUTF(ten == null ? "Boss Rồng" : ten);
        ds.writeShort(head);
        ds.writeShort(leg);
        ds.writeShort(body);
        ds.writeShort(hat);
        ds.writeShort(wing);
        ds.writeShort(vuKhi);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.writeShort(Math.max(1, Math.min(65535, mauToiDa)));
        ds.writeByte(2);
        ds.flush();
        this.guiTin(ms);
    }

    /** Di chuyển boss động bằng packet native CMD -64. */
    public void guiDiChuyenBossBaoVay(byte slot, short x, short y) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-64);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(slot);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiDiChuyenDau(byte chiSo, short x, short y) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(21);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.flush();
        this.guiTin(ms);
    }

    /**
     * Packet CMD 22 riêng cho trận Boss Bao vây. Hai tọa độ neo là vị trí
     * chân thật của người/boss bắn; quỹ đạo vẫn bắt đầu tại đầu nòng trong
     * ketQua. Cách này tránh client kéo nhân vật tới đầu nòng sau mỗi phát.
     */
    /**
     * Hoạt ảnh native thứ hai của BigBoss Rùa:
     * nhảy/dậm (action 0), rung màn hình và tạo Bullet type 61 tại điểm va
     * chạm. Tọa độ người bị đá nâng lên do server quyết định và gửi kèm.
     */
    public void guiDamDaBossRua(
            byte slotRua,
            short xVaCham,
            short yVaCham,
            byte slotMucTieu,
            short xMucTieuMoi,
            short yMucTieuMoi
    ) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-68);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(slotRua);
        ds.writeByte(0); // BigBoss action 0: nhảy rồi dậm.
        ds.writeByte(1); // Một điểm đá rơi.
        ds.writeByte(1); // Có Bullet type 61 tại điểm này.
        ds.writeShort(xVaCham);
        ds.writeShort(yVaCham);
        ds.writeByte(1); // Một người chơi được chốt lại tọa độ.
        ds.writeByte(slotMucTieu);
        ds.writeShort(xMucTieuMoi);
        ds.writeShort(yMucTieuMoi);
        ds.flush();
        this.guiTin(ms);
    }

    /**
     * Packet độc native của client: byte đầu là người bắn, byte sau là người
     * trúng độc. Packet chỉ bật hình ảnh; HP vẫn do server cập nhật bằng CMD 51.
     */
    public void guiTrungDoc(byte slotNguon, byte slotMucTieu) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(96);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(slotNguon);
        ds.writeByte(slotMucTieu);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiKetQuaBanBossBaoVay(
            byte whoShoot,
            short shooterX,
            short shooterY,
            ChickenKetQuaDan ketQua,
            byte numShoot
    ) throws IOException {
        this.guiKetQuaBanNoiBo(
                whoShoot, shooterX, shooterY, ketQua, numShoot, false);
    }

    /**
     * Gui ket qua tran thuong voi vi tri nguoi ban do server quan ly.
     * neoDiemDauQuyDao chi anh huong ban sao hien thi gui cho client.
     */
    public void guiKetQuaBanDau(
            byte whoShoot,
            short shooterX,
            short shooterY,
            ChickenKetQuaDan ketQua,
            byte numShoot,
            boolean neoDiemDauQuyDao
    ) throws IOException {
        this.guiKetQuaBanNoiBo(
                whoShoot,
                shooterX,
                shooterY,
                ketQua,
                numShoot,
                neoDiemDauQuyDao
        );
    }

    private void guiKetQuaBanNoiBo(
            byte whoShoot,
            short shooterX,
            short shooterY,
            ChickenKetQuaDan ketQua,
            byte numShoot,
            boolean neoDiemDauQuyDao
    ) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(22);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(ketQua.loaiDan == ChickenDuongDanLaserClient.LOAI_DAN_LASER
                ? 0 : 1);
        ds.writeByte(0);
        ds.writeByte(whoShoot);
        ds.writeByte(ketQua.loaiDan);
        ds.writeShort(shooterX);
        ds.writeShort(shooterY);
        ds.writeShort(ketQua.goc);
        if (ketQua.loaiDan == 17 || ketQua.loaiDan == 19) {
            ds.writeByte(ketQua.lucPhu);
        }
        ds.writeByte(numShoot <= 0 ? 1 : numShoot);
        int soDuong = Math.max(1, Math.min(255, Math.min(
                ketQua.cacDuongX.length, ketQua.cacDuongY.length)));
        ds.writeByte(soDuong);
        for (int i = 0; i < soDuong; i++) {
            this.ghiMotDuongDan(
                    ds,
                    ketQua.loaiDan,
                    i < ketQua.cacDuongX.length ? ketQua.cacDuongX[i] : null,
                    i < ketQua.cacDuongY.length ? ketQua.cacDuongY[i] : null,
                    shooterX,
                    shooterY,
                    neoDiemDauQuyDao
            );
        }
        this.ghiDuLieuSieuCao(
                ds,
                ketQua.loaiDan,
                ketQua.duongX,
                ketQua.duongY,
                ketQua.sieuCao
        );
        ds.flush();
        this.guiTin(ms);
    }

    private void ghiMotDuongDan(
            DataOutputStream ds,
            byte loaiDan,
            short[] duongX,
            short[] duongY,
            short xMacDinh,
            short yMacDinh
    ) throws IOException {
        this.ghiMotDuongDan(
                ds, loaiDan, duongX, duongY, xMacDinh, yMacDinh, false);
    }

    private void ghiMotDuongDan(
            DataOutputStream ds,
            byte loaiDan,
            short[] duongX,
            short[] duongY,
            short xMacDinh,
            short yMacDinh,
            boolean neoDiemDauQuyDao
    ) throws IOException {
        if (loaiDan == ChickenDuongDanLaserClient.LOAI_DAN_LASER) {
            ChickenDuongDanLaserClient.DuLieu laser =
                    ChickenDuongDanLaserClient.tao(
                            duongX, duongY, xMacDinh, yMacDinh);
            ChickenDuongDanLaserClient.ghiNen(ds, laser);
            // Bullet type 49 đọc bắt buộc hai byte này ngay sau từng quỹ đạo.
            return;
        }

        int soDiem = Math.min(
                duongX == null ? 0 : duongX.length,
                duongY == null ? 0 : duongY.length
        );
        if (soDiem <= 0) {
            ds.writeShort(1);
            ds.writeShort(xMacDinh);
            ds.writeShort(yMacDinh);
            return;
        }
        soDiem = Math.min(Short.MAX_VALUE, soDiem);
        ds.writeShort(soDiem);
        for (int i = 0; i < soDiem; i++) {
            ds.writeShort(neoDiemDauQuyDao && i == 0
                    ? xMacDinh : duongX[i]);
            ds.writeShort(neoDiemDauQuyDao && i == 0
                    ? yMacDinh : duongY[i]);
        }
    }

    /**
     * Gửi riêng hoạt ảnh skill Hawk trong trận. Bullet type 37 của client vẽ
     * sprite /eff/muiten.png cho bốn quỹ đạo, vì vậy soPhat phải là 1 để loạt
     * dừng đúng sau bốn mũi, không lặp vô hạn.
     */
    public void guiLoatMuiTenHawkDau(
            byte whoShoot,
            byte loaiDan,
            short shooterX,
            short shooterY,
            short goc,
            byte luc,
            short[][] cacDuongX,
            short[][] cacDuongY
    ) throws IOException {
        int soVien = Math.min(
                cacDuongX == null ? 0 : cacDuongX.length,
                cacDuongY == null ? 0 : cacDuongY.length
        );
        if (soVien <= 0) {
            return;
        }
        soVien = Math.min(255, soVien);

        ChickenTinNhan ms = new ChickenTinNhan(22);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(1);
        ds.writeByte(0);
        ds.writeByte(whoShoot);
        ds.writeByte(loaiDan);
        ds.writeShort(shooterX);
        ds.writeShort(shooterY);
        ds.writeShort(goc);
        if (loaiDan == 17 || loaiDan == 19) {
            ds.writeByte(luc);
        }

        // Một lần kích hoạt duy nhất. Bốn mũi nằm ở bốn quỹ đạo phía sau.
        ds.writeByte(1);
        ds.writeByte(soVien);
        for (int vien = 0; vien < soVien; vien++) {
            short[] duongX = cacDuongX[vien];
            short[] duongY = cacDuongY[vien];
            int soDiem = Math.min(
                    duongX == null ? 0 : duongX.length,
                    duongY == null ? 0 : duongY.length
            );
            if (soDiem <= 0) {
                ds.writeShort(1);
                ds.writeShort(shooterX);
                ds.writeShort(shooterY);
                continue;
            }
            soDiem = Math.min(Short.MAX_VALUE, soDiem);
            ds.writeShort(soDiem);
            for (int i = 0; i < soDiem; i++) {
                ds.writeShort(duongX[i]);
                ds.writeShort(duongY[i]);
            }
        }
        ds.writeByte(0);
        ds.flush();
        this.guiTin(ms);
    }

    /**
     * Ghi phần cuối packet CMD 22/84 mà client dùng để bật sprite /eff/no.png.
     * Tọa độ phải trùng chính xác một điểm trên quỹ đạo, nên dùng điểm cao nhất.
     */
    private void ghiDuLieuSieuCao(
            DataOutputStream ds,
            byte loaiDan,
            short[] duongX,
            short[] duongY,
            boolean sieuCao
    ) throws IOException {
        if (!sieuCao) {
            ds.writeByte(0);
            return;
        }
        int chiSoDinh = ChickenSieuCao.timChiSoDinhHinhHoc(
                loaiDan, duongX, duongY);
        if (chiSoDinh < 0) {
            ds.writeByte(0);
            return;
        }
        ds.writeByte(1);
        ds.writeShort(duongX[chiSoDinh]);
        ds.writeShort(duongY[chiSoDinh]);
    }

    /**
     * Quy tắc gốc: chỉ các loại đạn đạo thông thường được tính siêu cao; sau
     * khi qua đỉnh, viên đạn phải rơi xuống hơn 350 px.
     */
    /** Gửi ba viên Ultron độc lập trong một loạt native giống súng cối. */
    public void guiLoatLaserUltronDau(
            byte whoShoot,
            short shooterX,
            short shooterY,
            short goc,
            byte luc,
            short[][] cacDuongX,
            short[][] cacDuongY
    ) throws IOException {
        int soTia = Math.min(
                cacDuongX == null ? 0 : cacDuongX.length,
                cacDuongY == null ? 0 : cacDuongY.length
        );
        if (soTia <= 0) {
            return;
        }
        ChickenKetQuaDan ketQua = new ChickenKetQuaDan(
                (byte) 0,
                shooterX,
                shooterY,
                goc,
                luc,
                luc,
                cacDuongX,
                cacDuongY,
                null
        );
        this.guiKetQuaBanDau(
                whoShoot, shooterX, shooterY, ketQua, (byte) 1, true);
    }

    /** Gửi hiệu ứng Bắn x3 của Ultron trong luyện tập bằng CMD 84. */
    public void guiLoatLaserUltronLuyenTap(
            byte whoShoot,
            short shooterX,
            short shooterY,
            short goc,
            byte luc,
            short[][] cacDuongX,
            short[][] cacDuongY
    ) throws IOException {
        this.guiLoatLaserUltron(
                (byte) 84,
                whoShoot,
                shooterX,
                shooterY,
                goc,
                luc,
                cacDuongX,
                cacDuongY
        );
    }

    /**
     * Packet native tạo đúng ba tia đồng thời của client:
     * typeShoot=1      : mỗi điểm quỹ đạo là short X/Y;
     * critical=0       : nhánh bulletType 2 tạo đúng 3 Bullet (không phải 7);
     * bulletType=2     : client lấy đồng thời ba quỹ đạo trong cùng một nhịp;
     * soPhat=1         : chỉ có một lần bắn, tránh lặp hoặc kẹt lượt;
     * NBULL=3          : tia trái, tia giữa thật và tia phải.
     *
     * CPlayer của client luôn gán vị trí nhân vật bằng shooterX/shooterY trong
     * packet. Hai giá trị này bắt buộc là vị trí đứng thật của nhân vật, tuyệt
     * đối không truyền tọa độ đầu nòng; nếu truyền đầu nòng AVG sẽ bị kéo bay.
     */
    private void guiLoatLaserUltron(
            byte lenh,
            byte whoShoot,
            short shooterX,
            short shooterY,
            short goc,
            byte luc,
            short[][] cacDuongX,
            short[][] cacDuongY
    ) throws IOException {
        int soTia = Math.min(
                cacDuongX == null ? 0 : cacDuongX.length,
                cacDuongY == null ? 0 : cacDuongY.length
        );
        if (soTia < 3) {
            return;
        }
        soTia = 3;

        final byte bulletTypeBanX3 = 2;
        ChickenTinNhan ms = new ChickenTinNhan(lenh);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(1); // typeShoot: đọc toàn bộ tọa độ bằng short.
        ds.writeByte(0); // critical=0: phát bắn thường.
        ds.writeByte(whoShoot);
        ds.writeByte(bulletTypeBanX3);
        ds.writeShort(shooterX);
        ds.writeShort(shooterY);
        ds.writeShort(goc);
        // bulletType 2 không có trường lực trong packet CMD 22/84.
        ds.writeByte(1);     // soPhat: một lần bắn duy nhất.
        ds.writeByte(soTia); // NBULL: đúng ba quỹ đạo được tạo đồng thời.

        for (int tia = 0; tia < soTia; tia++) {
            // Giữ thứ tự hình học: trái, giữa, phải. Tia giữa (index 1) là tia
            // duy nhất đã được server dùng để tính va chạm và sát thương.
            short[] xs = cacDuongX[tia];
            short[] ys = cacDuongY[tia];
            int soDiem = Math.min(
                    xs == null ? 0 : xs.length,
                    ys == null ? 0 : ys.length
            );
            if (soDiem <= 0) {
                ds.writeShort(1);
                ds.writeShort(shooterX);
                ds.writeShort(shooterY);
                continue;
            }
            soDiem = Math.min(Short.MAX_VALUE, soDiem);
            ds.writeShort(soDiem);
            for (int i = 0; i < soDiem; i++) {
                ds.writeShort(i == 0 ? shooterX : xs[i]);
                ds.writeShort(i == 0 ? shooterY : ys[i]);
            }
        }

        ds.writeByte(0); // không dùng đạn siêu cao.
        ds.flush();
        this.guiTin(ms);
    }

    public void guiCapNhatMauDau(byte chiSo, int hp, byte phanTram, byte trangThaiChet) throws IOException {
        if (hp < 0) {
            hp = 0;
        }
        if (hp > 65535) {
            hp = 65535;
        }
        ChickenTinNhan ms = new ChickenTinNhan(51);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeShort(hp);
        // Client vẽ thanh HP theo 25 nấc, còn ChickenChienBinh trả phần trăm 0..100.
        int phanTram100 = Math.max(0, Math.min(100, phanTram & 0xFF));
        ds.writeByte(Math.max(0, Math.min(25, phanTram100 * 25 / 100)));
        ds.writeByte(trangThaiChet);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiGio(byte windX, byte windY) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(25);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(windX);
        ds.writeByte(windY);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiLuotDauTiep(byte whoNext, short x, short y, ChickenChienBinh[] chienBinhs, byte giay) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(24);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(whoNext);
        ds.writeShort(x);
        ds.writeShort(y);
        int alive = 0;
        for (ChickenChienBinh chienBinh : chienBinhs) {
            if (chienBinh != null && !chienBinh.chet) {
                alive++;
            }
        }
        ds.writeByte(alive);
        for (ChickenChienBinh chienBinh : chienBinhs) {
            if (chienBinh != null && !chienBinh.chet) {
                ds.writeByte(chienBinh.chiSo);
                ds.writeShort(100);
            }
        }
        ds.writeByte(giay);
        ds.flush();
        this.guiTin(ms);
    }

    /**
     * Packet lượt riêng cho Boss Bao vây.
     * Client hiển thị tên theo thứ tự các cặp slot/nạp đạn trong CMD 24,
     * vì vậy luôn ghi người đang có lượt lên đầu rồi mới tới các slot còn lại.
     */
    public void guiLuotBossBaoVayTiep(
            byte whoNext,
            short x,
            short y,
            ChickenChienBinh[] chienBinhs,
            int[] napDan,
            byte giay
    ) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(24);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(whoNext);
        ds.writeShort(x);
        ds.writeShort(y);

        int alive = 0;
        for (ChickenChienBinh chienBinh : chienBinhs) {
            if (chienBinh != null && !chienBinh.chet && chienBinh.hp > 0) {
                alive++;
            }
        }
        ds.writeByte(alive);

        boolean[] daGhi = new boolean[chienBinhs.length];
        int current = whoNext & 0xFF;
        if (current >= 0 && current < chienBinhs.length) {
            ChickenChienBinh hienTai = chienBinhs[current];
            if (hienTai != null && !hienTai.chet && hienTai.hp > 0) {
                ds.writeByte(hienTai.chiSo);
                ds.writeShort(layNapDanAnToan(napDan, current));
                daGhi[current] = true;
            }
        }

        // Các tên còn lại xếp theo nạp đạn tăng dần; nếu bằng nhau giữ thứ tự slot.
        for (int lan = 0; lan < alive; lan++) {
            int chon = -1;
            int napNhoNhat = Integer.MAX_VALUE;
            for (int slot = 0; slot < chienBinhs.length; slot++) {
                ChickenChienBinh chienBinh = chienBinhs[slot];
                if (daGhi[slot] || chienBinh == null || chienBinh.chet
                        || chienBinh.hp <= 0) {
                    continue;
                }
                int nap = layNapDanAnToan(napDan, slot);
                if (nap < napNhoNhat) {
                    napNhoNhat = nap;
                    chon = slot;
                }
            }
            if (chon < 0) {
                break;
            }
            ChickenChienBinh chienBinh = chienBinhs[chon];
            ds.writeByte(chienBinh.chiSo);
            ds.writeShort(napNhoNhat);
            daGhi[chon] = true;
        }

        ds.writeByte(giay);
        ds.flush();
        this.guiTin(ms);
    }

    private static int layNapDanAnToan(int[] napDan, int slot) {
        if (napDan == null || slot < 0 || slot >= napDan.length) {
            return 0;
        }
        return Math.max(0, Math.min(65_535, napDan[slot]));
    }

    public void guiKetThucDau(byte pheThang, int kinhNghiem, int vang, int ngoc) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(50);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(pheThang);
        ds.writeShort(kinhNghiem);
        ds.writeInt(vang);
        ds.writeShort(ngoc);
        ds.writeByte(0);
        ds.flush();
        this.guiTin(ms);
    }

    public void capNhatCup(byte loai, int cup) throws IOException {
        this.guiCapNhatCup(loai, cup);
    }

    private byte demSungDau(ChickenChienBinh[] chienBinhs) {
        byte dem = 0;
        for (short maVuKhi : this.gomSungDau(chienBinhs)) {
            if (maVuKhi > 0) {
                dem++;
            }
        }
        return dem;
    }

    private short[] gomSungDau(ChickenChienBinh[] chienBinhs) {
        short[] weapons = new short[chienBinhs.length];
        int kichThuoc = 0;
        for (ChickenChienBinh chienBinh : chienBinhs) {
            if (chienBinh == null || chienBinh.maVuKhi <= 0) {
                continue;
            }
            boolean exists = false;
            for (int i = 0; i < kichThuoc; i++) {
                if (weapons[i] == chienBinh.maVuKhi) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                weapons[kichThuoc++] = chienBinh.maVuKhi;
            }
        }
        return weapons;
    }

    public void guiTin(ChickenTinNhan ms) {
        this.khach.guiTin(ms);
    }

    public void guiDuLieuBanDo(int maBanDo) throws IOException {
        ChickenTinNhan msg = new ChickenTinNhan(-6);
        DataOutputStream ds = msg.boGhi();
        byte[] mapData = ChickenTienIch.layTep("cache/dataMap");
        ds.writeByte(ChickenQuanLyMayChu.vMap);
        ds.writeShort(mapData.length);
        ds.write(mapData);
        ds.flush();
        this.guiTin(msg);
    }

    public void guiChonBanDoLuyenTap(byte maBanDo) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(75);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(maBanDo);
        ds.flush();
        this.guiTin(ms);
    }

    /** Xóa các bot luyện tập cũ mà client có thể còn giữ trong danh sách hiển thị. */
    public void guiDonNhanVatAoLuyenTap(int ownerId) {
        for (int i = 0; i < 8; i++) {
            this.guiRoiDau(-9999 - i, ownerId);
        }
    }

    public void guiThongTinLuyenTap() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(76);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(0);
        ds.writeByte(0);
        ds.writeUTF("Luyện tập");
        ds.writeByte(0);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiNguoiChoiLuyenTap(byte chiSo, int ma, String ten, short head, short leg, short body,
            short hat, short wing, short wp, byte avenger, int ownerId, int clan, int kinhNghiem) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(8);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeInt(ma);
        ds.writeUTF(ten);
        ds.writeShort(clan);
        ds.writeInt(Math.max(0, kinhNghiem));
        ds.writeShort(head);
        ds.writeShort(leg);
        ds.writeShort(body);
        ds.writeShort(hat);
        ds.writeShort(wing);
        ds.writeShort(wp);
        ds.writeByte(avenger);
        ds.writeInt(ownerId);
        ds.writeByte(0);
        ds.writeByte(0);
        ds.writeInt(clan);
        if (clan != -1) {
            ds.writeShort(0);
        }
        ds.writeByte(chiSo);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiBatDauLuyenTap(byte maBanDo, byte maNen, short maVuKhi, short playerX, short playerY, int playerHp, int playerMaxHp,
            short[] botX, short[] botY, int[] botHp, int botMaxHp, short[] botWeapons,
            byte playerAvenger, byte[] botAvengers) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(20);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(maBanDo);
        ds.writeByte(25);
        for (int i = 0; i < 8; ++i) {
            if (i == 0) {
                ds.writeShort(playerX);
                ds.writeShort(playerY);
                ds.writeShort(Math.min(65535, Math.max(0, playerHp)));
                ds.writeShort(ChickenThanhDiChuyenAVG.quangDuongToiDa(this.nguoiChoi));
            } else if (i - 1 >= 0 && i - 1 < botX.length) {
                int botIndex = i - 1;
                ds.writeShort(botX[botIndex]);
                ds.writeShort(botY[botIndex]);
                ds.writeShort(Math.min(65535, Math.max(0, botHp[botIndex])));
                byte botAvenger = botAvengers != null && botIndex < botAvengers.length
                        ? botAvengers[botIndex] : 0;
                ds.writeShort(ChickenThanhDiChuyenAVG.quangDuongToiDaTheoAvenger(
                        botAvenger));
            } else {
                ds.writeShort(-1);
            }
        }
        ds.writeByte(maNen);
        java.util.LinkedHashSet<Short> weapons = new java.util.LinkedHashSet<>();
        if (maVuKhi > 0) {
            weapons.add(maVuKhi);
        }
        if (botWeapons != null) {
            for (short botWeapon : botWeapons) {
                if (botWeapon > 0) {
                    weapons.add(botWeapon);
                }
            }
        }
        ds.writeByte(weapons.size());
        for (short weapon : weapons) {
            ds.writeShort(weapon);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void guiHienManHinhGameLuyenTap() {
        this.guiTin(new ChickenTinNhan(-67));
    }

    public void yeuCauDanLuyenTap(ChickenTinNhan ms) throws IOException {
        short maHinhVuKhi = ms.boDoc().readShort();
        ChickenMauVatPham mauVuKhi = this.timMauVuKhiLuyenTap(maHinhVuKhi);
        DuLieuSung duLieuDan = ChickenQuanLyDanSung.theoMauSung(mauVuKhi);
        byte loaiHinhDan = duLieuDan == null ? (byte) 0 : duLieuDan.getLoaiHinhDan();
        ChickenTinNhan mss = new ChickenTinNhan(-40);
        DataOutputStream ds = mss.boGhi();
        byte[] img = this.layAnhDanLuyenTap(mauVuKhi, maHinhVuKhi);
        ds.writeShort(maHinhVuKhi);
        ds.writeByte(loaiHinhDan);
        ds.writeShort(img.length);
        ds.write(img);
        ds.flush();
        this.guiTin(mss);
    }

    private ChickenMauVatPham timMauVuKhiLuyenTap(short maHinhVuKhi) {
        if (this.nguoiChoi != null && this.nguoiChoi.itemBody != null && this.nguoiChoi.itemBody.length > 5) {
            ChickenVatPham dangTrangBi = this.nguoiChoi.itemBody[5];
            if (dangTrangBi != null && dangTrangBi.mau != null && dangTrangBi.mau.loai == 5
                    && dangTrangBi.mau.part == maHinhVuKhi) {
                return dangTrangBi.mau;
            }
        }
        for (ChickenMauVatPham mau : ChickenQuanLyMayChu.itemTemplates.values()) {
            if (mau != null && mau.loai == 5 && mau.part == maHinhVuKhi) {
                return mau;
            }
        }
        return null;
    }

    private byte[] layAnhDanLuyenTap(ChickenMauVatPham mauVuKhi, short maHinhVuKhi) {
        DuLieuSung duLieuDan = ChickenQuanLyDanSung.theoMauSung(mauVuKhi);
        if (duLieuDan == null) {
            duLieuDan = ChickenQuanLyDanSung.theoPartSung(maHinhVuKhi);
        }
        for (String duongDan : ChickenQuanLyDanSung.layThuTuDuongDanAnh(
                duLieuDan, mauVuKhi, maHinhVuKhi, this.khach.mucPhong)) {
            byte[] duLieu = this.docTepNeuCo(duongDan);
            if (duLieu != null && duLieu.length > 0) {
                return duLieu;
            }
        }
        return this.layPngDanLuyenTapDuPhong();
    }

    private byte[] layPngDanLuyenTapDuPhong() {
        return new byte[]{
            -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
            0, 0, 0, 8, 0, 0, 0, 8, 8, 6, 0, 0, 0, -60, 15, -66, -117,
            0, 0, 0, 25, 73, 68, 65, 84, 120, -100, 99, -4, -49, -64, -16,
            -97, -127, -127, -127, 33, 48, 50, 50, -4, 79, 6, 0, 39, 79,
            4, 2, 74, 56, 83, 55, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66,
            96, -126
        };
    }

    private byte[] docTepNeuCo(String duongDan) {
        File file = new File(duongDan);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            return bos.toByteArray();
        } catch (Exception ignored) {
            return null;
        }
    }

    public void guiKetQuaBanLuyenTap(byte whoShoot, byte loaiDan,
            short shooterX, short shooterY, short goc,
            byte luc, byte soPhat, short[][] cacDuongX, short[][] cacDuongY) throws IOException {
        this.guiKetQuaBanLuyenTap(
                whoShoot, loaiDan, shooterX, shooterY, goc,
                luc, luc, soPhat, cacDuongX, cacDuongY, false);
    }

    public void guiKetQuaBanLuyenTap(byte whoShoot, byte loaiDan,
            short shooterX, short shooterY, short goc,
            byte luc, byte soPhat, short[][] cacDuongX, short[][] cacDuongY,
            boolean sieuCao) throws IOException {
        this.guiKetQuaBanLuyenTap(
                whoShoot, loaiDan, shooterX, shooterY, goc,
                luc, luc, soPhat, cacDuongX, cacDuongY, sieuCao);
    }

    public void guiKetQuaBanLuyenTap(byte whoShoot, byte loaiDan,
            short shooterX, short shooterY, short goc,
            byte luc, byte lucPhu, byte soPhat,
            short[][] cacDuongX, short[][] cacDuongY) throws IOException {
        this.guiKetQuaBanLuyenTap(
                whoShoot, loaiDan, shooterX, shooterY, goc,
                luc, lucPhu, soPhat, cacDuongX, cacDuongY, false);
    }

    public void guiKetQuaBanLuyenTap(byte whoShoot, byte loaiDan,
            short shooterX, short shooterY, short goc,
            byte luc, byte lucPhu, byte soPhat,
            short[][] cacDuongX, short[][] cacDuongY,
            boolean sieuCao) throws IOException {
        int soVien = Math.min(cacDuongX == null ? 0 : cacDuongX.length,
                cacDuongY == null ? 0 : cacDuongY.length);
        if (soVien <= 0) {
            return;
        }
        soVien = Math.min(255, soVien);
        ChickenTinNhan ms = new ChickenTinNhan(84);
        DataOutputStream ds = ms.boGhi();
        // Bullet 49 dùng TYPE SHOOT = 0 để client đọc thêm dXLaser/dYLaser.
        ds.writeByte(loaiDan == ChickenDuongDanLaserClient.LOAI_DAN_LASER
                ? 0 : 1);
        ds.writeByte(0);
        ds.writeByte(whoShoot);
        ds.writeByte(loaiDan);
        // Hai trường này là neo vị trí thực của người bắn trên client, không phải
        // tọa độ đầu nòng. Điểm đầu nòng nằm ở phần tử đầu của mỗi quỹ đạo.
        ds.writeShort(shooterX);
        ds.writeShort(shooterY);
        ds.writeShort(goc);
        if (loaiDan == 17 || loaiDan == 19) {
            ds.writeByte(lucPhu);
        }
        ds.writeByte(soPhat <= 0 ? 1 : soPhat);
        ds.writeByte(soVien);
        for (int vien = 0; vien < soVien; vien++) {
            this.ghiMotDuongDan(
                    ds,
                    loaiDan,
                    cacDuongX[vien],
                    cacDuongY[vien],
                    shooterX,
                    shooterY
            );
        }
        this.ghiDuLieuSieuCao(
                ds, loaiDan, cacDuongX[0], cacDuongY[0], sieuCao);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiCapNhatMauLuyenTap(byte chiSo, int hp, int maxHp, byte trangThaiChet) throws IOException {
        if (hp < 0) {
            hp = 0;
        }
        if (hp > 65535) {
            hp = 65535;
        }
        if (maxHp <= 0) {
            maxHp = Math.max(1, hp);
        }
        byte phanTram = (byte)Math.max(0, Math.min(25, hp * 25 / Math.max(1, maxHp)));
        ChickenTinNhan ms = new ChickenTinNhan(51);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeShort(hp);
        ds.writeByte(phanTram);
        ds.writeByte(trangThaiChet);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiLuotLuyenTapTiep(byte whoNext, short x, short y, int napDanNguoiChoi, int napDanBoss) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(24);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(whoNext);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.writeByte(2);

        // Client hiển thị danh sách theo đúng thứ tự packet.
        // Đưa bên đang có lượt lên phần tử đầu tiên.
        if (whoNext == 1) {
            ds.writeByte(1);
            ds.writeShort(Math.max(0, Math.min(65535, napDanBoss)));
            ds.writeByte(0);
            ds.writeShort(Math.max(0, Math.min(65535, napDanNguoiChoi)));
        } else {
            ds.writeByte(0);
            ds.writeShort(Math.max(0, Math.min(65535, napDanNguoiChoi)));
            ds.writeByte(1);
            ds.writeShort(Math.max(0, Math.min(65535, napDanBoss)));
        }
        ds.writeByte(25);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiDungVatPhamLuyenTap(byte whoUse, byte itemId, short iconUse) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(26);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(whoUse);
        ds.writeByte(itemId);
        ds.writeShort(iconUse);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiDiChuyenLuyenTap(byte chiSo, short x, short y) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(21);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiCapNhatXYLuyenTap(byte chiSo, short x, short y) throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(53);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeShort(x);
        ds.writeShort(y);
        ds.flush();
        this.guiTin(ms);
    }

    public void guiDatLaiHoLuyenTap() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(-92);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(1);
        ds.flush();
        this.guiTin(ms);
    }
    /** Mở đúng một lựa chọn Bắn x3; client trả CMD -47 khi người chơi chọn. */
    public void guiChonKyNangUltron() {
        Vector danhSach = new Vector();
        danhSach.addElement("Bắn x3");
        this.moDanhSach("Kỹ năng đặc biệt", danhSach);
        System.out.println("[ULTRON] GUI_MENU label=Ban_x3 cmd=-47");
    }

    /** Mo menu generic de Iron Man bat laser, sau do client gui phat ban that. */
    public void guiChonKyNangIronMan() {
        Vector danhSach = new Vector();
        danhSach.addElement("Laser nguc");
        this.moDanhSach("Ky nang dac biet", danhSach);
        System.out.println("[IRON_MAN] GUI_MENU label=Laser_nguc cmd=-47");
    }

    /**
     * Xac nhan trang thai ngam skill cho client PC. Day chi la trang thai hien thi;
     * goc, tia, va cham va damage van do server tu tinh lai khi nhan phat ban.
     */
    public void guiTrangThaiNgamLaserIronMan(boolean sanSang) {
        ChickenTinNhan ms = new ChickenTinNhan(ChickenTiaLaserIronMan.LENH_TRANG_THAI_NGAM);
        try {
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(ChickenTiaLaserIronMan.PHIEN_BAN_TRANG_THAI_NGAM);
            ds.writeBoolean(sanSang);
            ds.flush();
            this.guiTin(ms);
        } catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ms.donDep();
        }
    }

    public void guiTiaLaserIronManDau(
            byte whoShoot,
            short shooterX,
            short shooterY,
            short goc,
            short batDauX,
            short batDauY,
            short ketThucX,
            short ketThucY
    ) throws IOException {
        this.guiTiaLaserIronMan(
                (byte) 22, whoShoot, shooterX, shooterY, goc,
                batDauX, batDauY, ketThucX, ketThucY);
    }

    public void guiTiaLaserIronManLuyenTap(
            byte whoShoot,
            short shooterX,
            short shooterY,
            short goc,
            short batDauX,
            short batDauY,
            short ketThucX,
            short ketThucY
    ) throws IOException {
        this.guiTiaLaserIronMan(
                (byte) 84, whoShoot, shooterX, shooterY, goc,
                batDauX, batDauY, ketThucX, ketThucY);
    }

    private void guiTiaLaserIronMan(
            byte lenh,
            byte whoShoot,
            short shooterX,
            short shooterY,
            short goc,
            short batDauX,
            short batDauY,
            short ketThucX,
            short ketThucY
    ) throws IOException {
        /*
         * Iron Man dung renderer rieng qua CMD 125. Khong gui bullet 49 nua:
         * Laser Girl giu nguyen vong doi dan cua no, con client moi chi ve
         * hieu ung dua tren diem dau/cuoi authoritative do server tinh.
         *
         * Tham so lenh/shooterX/shooterY duoc giu trong API de hai nhanh dau
         * va luyen tap khong phai doi chu ky goi. Chung khong duoc tin de tinh
         * va cham hay damage.
         */
        ChickenTinNhan ms = new ChickenTinNhan(
                ChickenTiaLaserIronMan.LENH_HIEU_UNG_RIENG);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(ChickenTiaLaserIronMan.PHIEN_BAN_HIEU_UNG);
        ds.writeByte(whoShoot);
        ds.writeShort(batDauX);
        ds.writeShort(batDauY);
        ds.writeShort(ketThucX);
        ds.writeShort(ketThucY);
        ds.writeShort(ChickenTiaLaserIronMan.THOI_GIAN_HIEU_UNG_MS);
        ds.flush();
        this.guiTin(ms);
    }

    /**
     * Đóng InfoDlg sau khi chọn Bắn x3 mà không gọi lại GameScr.show().
     * Client luôn gọi InfoDlg.hide() khi nhận CMD -91; action 127 không thuộc
     * action skill native 0..5 nên không mở thêm menu hoặc thay đổi nhân vật.
     */
    /*
     * Luu y: day la lenh dong InfoDlg dung chung cho moi AVG, khong chi
     * Ultron. Action 127 khong trung cac action skill native 0..5.
     */
    public void guiDongMenuKyNangDacBiet() {
        ChickenTinNhan ms = new ChickenTinNhan((byte)-91);
        try {
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(127);
            ds.flush();
            this.guiTin(ms);
            System.out.println("[SKILL] DONG_MENU cmd=-91 action=127");
        } catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ms.donDep();
        }
    }

    /** Ten cu duoc giu lai de cac nhanh Ultron/Iron Man hien tai tuong thich. */
    public void guiDongChoKyNangUltron() {
        this.guiDongMenuKyNangDacBiet();
    }

    /** Gửi tín hiệu native để client Loki mở menu Giả dạng. */
    public void guiChonKyNangLoki() {
        ChickenTinNhan ms = new ChickenTinNhan((byte)-91);
        try {
            DataOutputStream ds = ms.boGhi();
            // Client native dùng action 5 để hiện menu có mục "Giả dạng".
            // Khi bấm Giả dạng, client tự mở danh sách nhân vật trong map.
            ds.writeByte(5);
            ds.flush();
            this.guiTin(ms);
            System.out.println("[LOKI] GUI_MENU_NATIVE cmd=-91 action=5 label=Gia_dang");
        } catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ms.donDep();
        }
    }

    /** Luồng tương thích cũ: mở danh sách mục tiêu Loki bằng action 1. */
    public void guiChonMucTieuLoki() {
        ChickenTinNhan ms = new ChickenTinNhan((byte)-91);
        try {
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(1);
            ds.flush();
            this.guiTin(ms);
            System.out.println("[LOKI] GUI_CHON_MUC_TIEU cmd=-91 action=1");
        } catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ms.donDep();
        }
    }

    /**
     * Client native action 0 tự copy tên hiển thị, ngoại hình, AVG, hình súng, hp và max hp
     * từ targetIndex sang lokiIndex.
     */
    public void guiBienHinhLoki(byte lokiIndex, byte targetIndex) {
        ChickenTinNhan ms = new ChickenTinNhan((byte)-91);
        try {
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(0);
            ds.writeByte(lokiIndex);
            ds.writeByte(targetIndex);
            ds.flush();
            this.guiTin(ms);
            System.out.println("[LOKI] GUI_BIEN_HINH action=0 loki="
                    + (lokiIndex & 0xFF) + " target=" + (targetIndex & 0xFF));
        } catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ms.donDep();
        }
    }

    /** Gửi tín hiệu native để client Thor mở lựa chọn Sấm sét. */
    public void guiChonKyNangThor() {
        ChickenTinNhan ms = new ChickenTinNhan((byte)-91);
        try {
            DataOutputStream ds = ms.boGhi();
            // Client native dùng action 3 cho menu Sấm sét của Thor.
            ds.writeByte(3);
            ds.flush();
            this.guiTin(ms);
            System.out.println("[THOR] GUI_MENU_NATIVE cmd=-91 action=3");
        } catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ms.donDep();
        }
    }

    /** Gửi tín hiệu native để client Hawk mở giao diện chọn mục tiêu. */
    public void guiChonMucTieuHawk() {
        ChickenTinNhan ms = new ChickenTinNhan((byte)-91);
        try {
            DataOutputStream ds = ms.boGhi();
            // Action 1 mới là lệnh mở menu chọn mục tiêu Hawk của client.
            ds.writeByte(1);
            ds.flush();
            this.guiTin(ms);
            System.out.println("[HAWK] GUI_MENU_NATIVE cmd=-91 action=1");
        } catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ms.donDep();
        }
    }

    /**
     * Gửi một mũi tên Hawk rơi xuống một tọa độ.
     * Client đọc CMD -91 theo dạng: action=4, shooterIndex, bulletType,
     * count, rồi các cặp x/y. Gửi count=1 mỗi lần để các mũi tên nối đuôi.
     */
    public void guiMuiTenHawk(byte shooterIndex, byte bulletType, short x, short y) {
        ChickenTinNhan ms = new ChickenTinNhan((byte)-91);
        try {
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(4);
            ds.writeByte(shooterIndex);
            ds.writeByte(bulletType);
            ds.writeByte(1);
            ds.writeShort(x);
            ds.writeShort(y);
            ds.flush();
            this.guiTin(ms);
            System.out.println("[HAWK] GUI_MUI_TEN action=4 shooter="
                    + (shooterIndex & 0xFF) + " x=" + x + " y=" + y);
        } catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ms.donDep();
        }
    }

    /**
     * Phát hiệu ứng sét Thor tại nhiều điểm trong cùng một packet native.
     * Cấu trúc action 4 được lấy đúng từ bản cũ từng làm Hawk hiện nhầm sấm sét.
     */
    public void guiTiaSetThor(
            byte shooterIndex,
            byte loaiHieuUng,
            short[] cacX,
            short[] cacY
    ) {
        int soTia = Math.min(
                cacX == null ? 0 : cacX.length,
                cacY == null ? 0 : cacY.length
        );
        if (soTia <= 0) {
            return;
        }
        soTia = Math.min(255, soTia);

        ChickenTinNhan ms = new ChickenTinNhan((byte)-91);
        try {
            DataOutputStream ds = ms.boGhi();
            ds.writeByte(4);
            ds.writeByte(shooterIndex);
            ds.writeByte(loaiHieuUng);
            ds.writeByte(soTia);
            for (int i = 0; i < soTia; i++) {
                ds.writeShort(cacX[i]);
                ds.writeShort(cacY[i]);
            }
            ds.flush();
            this.guiTin(ms);
            System.out.println("[THOR] GUI_TIA_SET action=4 shooter="
                    + (shooterIndex & 0xFF) + " count=" + soTia);
        } catch (IOException ex) {
            Logger.getLogger(ChickenDichVuGame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ms.donDep();
        }
    }

}
