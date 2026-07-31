package com.chicken.mang;

import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenCheDoTestChienDau;
import com.chicken.chien.ChickenNapDanServer;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mohinh.ChickenNguoiDung;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.sanhcho.VaoSanhChoBoss;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import com.chicken.vatpham.ChickenMauThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenVatPham;

/** Hoi quy: packet scene tre khong duoc day client ra khoi tran boss. */
public final class ChickenXuLyTinTestSupport {
    private static final int[] LENH_CHUYEN_SCENE = {
        -98, -28, 6, 7, 8, 16, 18, 20, 71, 75, 83
    };

    private ChickenXuLyTinTestSupport() {
    }

    public static void tuKiemTra() throws Exception {
        for (int cmd : LENH_CHUYEN_SCENE) {
            dung(ChickenXuLyTin.laLenhChuyenScenePhong(cmd),
                    "thieu lenh scene cmd=" + cmd);
        }
        dung(!ChickenXuLyTin.laLenhChuyenScenePhong(15),
                "chan nham nut thoat hop le");
        dung(!ChickenXuLyTin.laLenhChuyenScenePhong(21),
                "chan nham lenh di chuyen");
        dung(!ChickenXuLyTin.laLenhChuyenScenePhong(22),
                "chan nham lenh ban");
        dung(ChickenXuLyTin.laLenhBoLuot(49, 0),
                "CMD49 rong cua nut native khong duoc nhan la bo luot");
        dung(!ChickenXuLyTin.laLenhBoLuot(49, 1),
                "CMD49 menu mot byte bi nhan nham la bo luot");
        dung(!ChickenXuLyTin.laLenhBoLuot(49, 2),
                "CMD49 menu hai byte bi nhan nham la bo luot");
        dung(!ChickenXuLyTin.laLenhBoLuot(49, 3),
                "CMD49 malformed bi nhan nham la bo luot");
        dung(!ChickenXuLyTin.laLenhBoLuot(-47, 0),
                "CMD-47 rong bi nhan nham la bo luot");
        int soToHopBoLuotDaQuet = 0;
        for (int cmd = Byte.MIN_VALUE; cmd <= Byte.MAX_VALUE; cmd++) {
            for (int soBytePayload = 0; soBytePayload <= 255;
                    soBytePayload++) {
                boolean mongDoi = cmd == 49 && soBytePayload == 0;
                dung(ChickenXuLyTin.laLenhBoLuot(cmd, soBytePayload)
                                == mongDoi,
                        "phan loai sai lenh bo luot cmd=" + cmd
                                + " payload=" + soBytePayload);
                soToHopBoLuotDaQuet++;
            }
        }
        dung(soToHopBoLuotDaQuet == 65_536,
                "chua quet du bang CMD/payload bo luot");
        kiemTraCmd49DiQuaRouterPvp();
        kiemTraCmd26MotByteDoiSungPvp();
        kiemTraCmd26PowPvp();
        kiemTraCoDanDoPowNative();
        kiemTraCmd20NapTruocSungTrongBalo();

        QuanLySanhChoBoss.khoiTao();
        PhienKhongDuocNgat phien =
                new PhienKhongDuocNgat(97_006);
        DichVuBatPacket dichVu = new DichVuBatPacket(phien);
        ChickenNguoiDung user = new ChickenNguoiDung(phien, dichVu);
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 97_006;
        nguoiChoi.ten = "BossLateScene";
        dichVu.datNguoiChoi(nguoiChoi);
        user.nguoiChoi = nguoiChoi;
        phien.user = user;

        try {
            dung(VaoSanhChoBoss.xuLy(nguoiChoi, 0, ""),
                    "khong tao duoc sanh boss test");
            SanhChoBoss sanh =
                    QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
            dung(sanh != null, "khong tim thay sanh boss test");
            sanh.setTrangThai(SanhChoBoss.TrangThai.DANG_CHIEN);
            dung(ChickenXuLyTin.dangTrongTranBoss(sanh),
                    "khong nhan trang thai boss dang chien");

            dichVu.xoaPacket();
            new ChickenXuLyTin(phien).khiCoTin(
                    new ChickenTinNhan((byte) 6, new byte[0]));
            dung(!phien.daBiNgat,
                    "packet scene tre lam ngat ket noi");
            dung(dichVu.cacLenh.isEmpty(),
                    "packet scene tre van gui UI sanh 8 nguoi");

            /*
             * Client gui -67 sau khi da tai du anh dan. Server boss phai tra
             * dung mot -67 de mo GameScr; khong duoc gui som trong batDau().
             */
            dichVu.xoaPacket();
            new ChickenXuLyTin(phien).khiCoTin(
                    new ChickenTinNhan((byte) -67, new byte[0]));
            dung(!phien.daBiNgat,
                    "ACK vao tran boss lam ngat ket noi");
            dung(dichVu.cacLenh.size() == 1
                            && dichVu.cacLenh.get(0) == -67,
                    "ACK vao tran boss khong mo GameScr dung mot lan");

            /*
             * Neu terrain CMD 126 con trong hang doi, ACK -67 phai duoc giu
             * lai den resource cuoi thay vi mo GameScr som.
             */
            dichVu.xoaPacket();
            dung(phien.datLichGuiNguyenLieuBoss(
                            System.currentTimeMillis()) >= 0L,
                    "khong tao duoc resource boss dang cho");
            new ChickenXuLyTin(phien).khiCoTin(
                    new ChickenTinNhan((byte) -67, new byte[0]));
            dung(dichVu.cacLenh.isEmpty(),
                    "ACK mo GameScr khi terrain con dang cho");
            dung(phien.hoanTatGuiNguyenLieuBoss(),
                    "resource cuoi khong danh thuc ACK GameScr");

            /*
             * Ngoai tran boss van giu nguyen handshake cua luyen tap.
             */
            QuanLySanhChoBoss.khoiTao();
            dichVu.xoaPacket();
            new ChickenXuLyTin(phien).khiCoTin(
                    new ChickenTinNhan((byte) -67, new byte[0]));
            dung(dichVu.cacLenh.size() == 1
                            && dichVu.cacLenh.get(0) == -67,
                    "lam hong handshake -67 cua luyen tap");
        } finally {
            QuanLySanhChoBoss.khoiTao();
        }
    }

    private static void kiemTraCmd49DiQuaRouterPvp() throws Exception {
        PhienKhongDuocNgat phien = new PhienKhongDuocNgat(97_049);
        DichVuBatPacket dichVu = new DichVuBatPacket(phien);
        ChickenNguoiDung user = new ChickenNguoiDung(phien, dichVu);
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 97_049;
        nguoiChoi.ten = "SkipRouterP0";
        nguoiChoi.wp = 57;
        dichVu.datNguoiChoi(nguoiChoi);
        user.nguoiChoi = nguoiChoi;
        phien.user = user;

        ChickenNguoiChoi doiThu = new ChickenNguoiChoi(dichVu);
        doiThu.ma = 97_050;
        doiThu.ten = "SkipRouterP1";
        doiThu.wp = 57;
        ChickenQuanLyChien tran = new ChickenQuanLyChien(
                null, new ChickenNguoiChoi[]{nguoiChoi, doiThu}, (byte) 0);
        try {
            Field luot = ChickenQuanLyChien.class.getDeclaredField(
                    "luotHienTai");
            luot.setAccessible(true);
            Field napDan = ChickenQuanLyChien.class.getDeclaredField(
                    "napDan");
            napDan.setAccessible(true);
            luot.setByte(tran, (byte) 0);

            new ChickenXuLyTin(phien).khiCoTin(
                    new ChickenTinNhan((byte) 49, new byte[0]));
            dung(luot.getByte(tran) == 1,
                    "CMD49 rong khong di qua router toi tran PvP");
            dung(((int[]) napDan.get(tran))[0] == 250,
                    "CMD49 qua router khong gan nap dan 250");
            dung(!phien.daBiNgat,
                    "CMD49 hop le lam ngat ket noi");
        } finally {
            tran.dungBot();
            tran.khiNguoiChoiRoi(nguoiChoi);
            tran.khiNguoiChoiRoi(doiThu);
        }
    }

    private static void kiemTraCmd26MotByteDoiSungPvp()
            throws Exception {
        PhienKhongDuocNgat phien = new PhienKhongDuocNgat(97_026);
        DichVuBatPacket dichVu = new DichVuBatPacket(phien);
        ChickenNguoiDung user = new ChickenNguoiDung(phien, dichVu);
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 97_026;
        nguoiChoi.ten = "WeaponSwapRouter";
        ChickenVatPham sungMac = sung(110, 5, 57, 100, 700, 839);
        ChickenVatPham sungBalo = sung(120, 3, 27, 250, 430, 848);
        nguoiChoi.itemBody[5] = sungMac;
        nguoiChoi.itemBag[3] = sungBalo;
        nguoiChoi.itemBalo = new int[]{3};
        dichVu.datNguoiChoi(nguoiChoi);
        user.nguoiChoi = nguoiChoi;
        phien.user = user;

        ChickenQuanLyChien tran = new ChickenQuanLyChien(
                null, new ChickenNguoiChoi[]{nguoiChoi}, (byte) 0);
        try {
            Field luot = ChickenQuanLyChien.class.getDeclaredField(
                    "luotHienTai");
            luot.setAccessible(true);
            luot.setByte(tran, (byte) 0);
            Field danhSach = ChickenQuanLyChien.class.getDeclaredField(
                    "chienBinhs");
            danhSach.setAccessible(true);
            ChickenChienBinh chienBinh =
                    ((ChickenChienBinh[]) danhSach.get(tran))[0];

            dichVu.xoaPacket();
            new ChickenXuLyTin(phien).khiCoTin(
                    new ChickenTinNhan((byte) 26, new byte[]{0}));

            dung(chienBinh.laySungDangCamTrongTran() == sungBalo,
                    "CMD26 mot byte khong doi sang sung Balo");
            dung(chienBinh.maVuKhi == 27,
                    "CMD26 khong doi part sung authoritative");
            dung(ChickenNapDanServer.layChoChienBinh(chienBinh) == 430,
                    "CMD26 khong doi nap dan theo sung moi");
            dung(nguoiChoi.itemBody[5] == sungMac
                            && nguoiChoi.itemBag[3] == sungBalo,
                    "CMD26 sua inventory that trong tran");
            dung(dichVu.cacLenh.size() == 2
                            && dichVu.cacLenh.get(0) == -45
                            && dichVu.cacLenh.get(1) == -42,
                    "CMD26 khong tra -45 va dong bo lai Balo runtime");
            DataInputStream in = new DataInputStream(
                    new ByteArrayInputStream(dichVu.cacDuLieu.get(0)));
            dung(in.readUnsignedByte() == 0
                            && in.readShort() == 27
                            && in.readShort() == 839
                            && in.available() == 0,
                    "payload -45 doi sung sai giao thuc client");
            DataInputStream balo = new DataInputStream(
                    new ByteArrayInputStream(dichVu.cacDuLieu.get(1)));
            dung(balo.readUnsignedByte() == 0
                            && balo.readUnsignedByte() == 1
                            && balo.readShort() == 110,
                    "Balo runtime khong chua sung cu sau khi doi");
            balo.readByte();
            balo.readByte();
            balo.readUTF();
            balo.readUTF();
            int soOption = balo.readUnsignedByte();
            for (int i = 0; i < soOption; i++) {
                balo.readByte();
                balo.readShort();
            }
            dung(balo.readUnsignedByte() == 3 && balo.available() == 0,
                    "Balo runtime lam mat index tui de doi sung lai");
            dung(!phien.daBiNgat,
                    "CMD26 doi sung hop le lam ngat ket noi");

            // Tu day goi thang manager PvP de tach rate-limit router khoi
            // ma tran state. Moi lenh bi chan phai la no-op, khong packet.
            dichVu.xoaPacket();
            dung(!tran.doiSungTrongTran(nguoiChoi, 255),
                    "PvP index 255 van doi duoc sung");
            dung(chienBinh.laySungDangCamTrongTran() == sungBalo
                            && dichVu.cacLenh.isEmpty(),
                    "PvP index gia lam doi state/gui packet");

            luot.setByte(tran, (byte) 1);
            dung(!tran.doiSungTrongTran(nguoiChoi, 0),
                    "PvP sai luot van doi duoc sung");
            dung(chienBinh.laySungDangCamTrongTran() == sungBalo
                            && dichVu.cacLenh.isEmpty(),
                    "PvP sai luot lam doi state/gui packet");
            luot.setByte(tran, (byte) 0);

            chienBinh.chet = true;
            dung(!tran.doiSungTrongTran(nguoiChoi, 0),
                    "PvP da chet van doi duoc sung");
            dung(chienBinh.laySungDangCamTrongTran() == sungBalo
                            && dichVu.cacLenh.isEmpty(),
                    "PvP da chet lam doi state/gui packet");
            chienBinh.chet = false;

            chienBinh.avenger = 1;
            dung(!tran.doiSungTrongTran(nguoiChoi, 0),
                    "PvP AVG van doi duoc sung Balo");
            dung(chienBinh.laySungDangCamTrongTran() == sungBalo
                            && dichVu.cacLenh.isEmpty(),
                    "PvP AVG lam doi state/gui packet");
            chienBinh.avenger = 0;

            sungMac.HP = 0;
            dung(!tran.doiSungTrongTran(nguoiChoi, 0),
                    "PvP sung runtime hong van doi duoc");
            dung(chienBinh.laySungDangCamTrongTran() == sungBalo
                            && dichVu.cacLenh.isEmpty(),
                    "PvP sung runtime hong lam doi state/gui packet");
            sungMac.HP = ChickenVatPham.DO_BEN_TOI_DA;

            Field ketThuc = ChickenQuanLyChien.class.getDeclaredField(
                    "daKetThuc");
            ketThuc.setAccessible(true);
            ketThuc.setBoolean(tran, true);
            dung(!tran.doiSungTrongTran(nguoiChoi, 0),
                    "PvP het tran van doi duoc sung");
            dung(chienBinh.laySungDangCamTrongTran() == sungBalo
                            && dichVu.cacLenh.isEmpty(),
                    "PvP het tran lam doi state/gui packet");
            ketThuc.setBoolean(tran, false);

            dung(tran.doiSungTrongTran(nguoiChoi, 0),
                    "PvP khong doi lai duoc sung ban dau");
            dung(chienBinh.laySungDangCamTrongTran() == sungMac
                            && chienBinh.maVuKhi == 57
                            && ChickenNapDanServer.layChoChienBinh(
                                    chienBinh) == 700,
                    "PvP doi lai khong khoi phuc cong thuc/nap dan");
            dung(dichVu.cacLenh.size() == 2
                            && dichVu.cacLenh.get(0) == -45
                            && dichVu.cacLenh.get(1) == -42,
                    "PvP doi lai khong dong bo client dung thu tu");
            dung(nguoiChoi.itemBody[5] == sungMac
                            && nguoiChoi.itemBag[3] == sungBalo
                            && nguoiChoi.itemBalo[0] == 3,
                    "PvP doi qua lai lam sua inventory that");
        } finally {
            tran.dungBot();
            tran.khiNguoiChoiRoi(nguoiChoi);
        }
    }

    private static void kiemTraCmd26PowPvp() throws Exception {
        PhienKhongDuocNgat phien = new PhienKhongDuocNgat(97_126);
        DichVuBatPacket dichVu = new DichVuBatPacket(phien);
        ChickenNguoiDung user = new ChickenNguoiDung(phien, dichVu);
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 97_126;
        nguoiChoi.ten = "PowRouter";
        nguoiChoi.wp = 57;
        dichVu.datNguoiChoi(nguoiChoi);
        user.nguoiChoi = nguoiChoi;
        phien.user = user;

        ChickenQuanLyChien tran = new ChickenQuanLyChien(
                null, new ChickenNguoiChoi[]{nguoiChoi}, (byte) 0);
        try {
            Field luot = ChickenQuanLyChien.class.getDeclaredField(
                    "luotHienTai");
            luot.setAccessible(true);
            luot.setByte(tran, (byte) 0);
            Field danhSach = ChickenQuanLyChien.class.getDeclaredField(
                    "chienBinhs");
            danhSach.setAccessible(true);
            ChickenChienBinh chienBinh =
                    ((ChickenChienBinh[]) danhSach.get(tran))[0];
            nguoiChoi.ghiNhanSatThuongChoPow(
                    (chienBinh.mauToiDa + 1) / 2,
                    chienBinh.mauToiDa);
            dung(nguoiChoi.layPowTrongTran() == 100,
                    "khong nap day POW truoc CMD26");

            dichVu.xoaPacket();
            new ChickenXuLyTin(phien).khiCoTin(
                    new ChickenTinNhan((byte) 26, new byte[]{100}));
            int powSauKichHoat = ChickenCheDoTestChienDau.POW_LUON_DAY
                    ? 100 : 0;
            dung(nguoiChoi.layPowTrongTran() == powSauKichHoat,
                    "CMD26 value=100 cap nhat POW sai che do");
            dung(dichVu.cacLenh.size() == 2
                            && dichVu.cacLenh.get(0) == 113
                            && dichVu.cacLenh.get(1) == 26,
                    "CMD26 POW khong phat giao thuc animation native");
            DataInputStream pow = new DataInputStream(
                    new ByteArrayInputStream(dichVu.cacDuLieu.get(0)));
            dung(pow.readUnsignedByte() == 0
                            && pow.readUnsignedByte() == 0
                            && pow.available() == 0,
                    "payload ha thanh POW sai");
            DataInputStream hieuUng = new DataInputStream(
                    new ByteArrayInputStream(dichVu.cacDuLieu.get(1)));
            dung(hieuUng.readUnsignedByte() == 0
                            && hieuUng.readUnsignedByte() == 100
                            && hieuUng.readUnsignedShort() == 1009
                            && hieuUng.available() == 0,
                    "payload animation POW native sai");

            new ChickenXuLyTin(phien).khiCoTin(
                    new ChickenTinNhan((byte) 26, new byte[]{100}));
            dung(dichVu.cacLenh.size() == 2,
                    "packet POW lap lai van tao tac dung");
            dung(!phien.daBiNgat,
                    "CMD26 POW hop le lam ngat ket noi");

            Method hetLuot = ChickenQuanLyChien.class.getDeclaredMethod(
                    "huyPowSauLuot",
                    ChickenChienBinh.class,
                    ChickenChienBinh[].class);
            hetLuot.setAccessible(true);
            hetLuot.invoke(tran, chienBinh,
                    (ChickenChienBinh[]) danhSach.get(tran));
            dung(dichVu.cacLenh.size() == 3
                            && dichVu.cacLenh.get(2) == 113,
                    "het luot khong tat VFX POW");
            DataInputStream powHetLuot = new DataInputStream(
                    new ByteArrayInputStream(dichVu.cacDuLieu.get(2)));
            dung(powHetLuot.readUnsignedByte() == 0
                            && powHetLuot.readUnsignedByte()
                                == powSauKichHoat
                            && powHetLuot.available() == 0,
                    "het luot dong bo sai thanh POW authoritative");
        } finally {
            tran.dungBot();
            tran.khiNguoiChoiRoi(nguoiChoi);
        }
    }

    /**
     * CMD22/CMD84 byte thu hai la critical native cua client. POW chi duoc
     * phep bat co hinh anh nay sau khi server da xac nhan kich hoat; overload
     * cu phai van mac dinh tat de dan boss/bot khong bi gia POW.
     */
    private static void kiemTraCoDanDoPowNative() throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket(
                new PhienKhongDuocNgat(97_127));
        short[][] duongX = {new short[]{100, 110, 120}};
        short[][] duongY = {new short[]{200, 190, 200}};
        ChickenKetQuaDan ketQua = new ChickenKetQuaDan(
                (byte) 1,
                (short) 100,
                (short) 200,
                (short) 45,
                (byte) 30,
                (byte) 30,
                duongX,
                duongY,
                java.util.Collections.emptyMap());

        dichVu.guiKetQuaBanDau(
                (byte) 0, (short) 100, (short) 200,
                ketQua, (byte) 1, false, true);
        xacNhanCriticalVaSoDuong(dichVu, 0, 22, 1, 6,
                "CMD22 POW khong bat dan do native");

        dichVu.guiKetQuaBanBossBaoVay(
                (byte) 0, (short) 100, (short) 200,
                ketQua, (byte) 1, false);
        xacNhanCriticalVaSoDuong(dichVu, 1, 22, 0, 1,
                "overload boss/bot mac dinh gia critical POW");

        dichVu.guiKetQuaBanLuyenTap(
                (byte) 0, (byte) 1,
                (short) 100, (short) 200, (short) 45,
                (byte) 30, (byte) 30, (byte) 1,
                duongX, duongY, false, true);
        xacNhanCriticalVaSoDuong(dichVu, 2, 84, 1, 6,
                "CMD84 POW khong bat dan do native");

        dichVu.guiKetQuaBanLuyenTap(
                (byte) 1, (byte) 1,
                (short) 100, (short) 200, (short) 45,
                (byte) 30, (byte) 1, duongX, duongY);
        xacNhanCriticalVaSoDuong(dichVu, 3, 84, 0, 1,
                "dan bot luyen tap bi gia critical POW");

        dichVu.guiLoatMuiTenHawkDau(
                (byte) 0, (byte) 37,
                (short) 100, (short) 200, (short) 90,
                (byte) 30, duongX, duongY, true);
        xacNhanCriticalVaSoDuong(dichVu, 4, 22, 1, 1,
                "Hawk POW khong bat dan do native");

        dichVu.guiLoatLaserUltronDau(
                (byte) 0, (short) 100, (short) 200, (short) 45,
                (byte) 30, duongX, duongY, true);
        xacNhanCriticalVaSoDuong(dichVu, 5, 22, 1, 1,
                "Ultron x3 POW trong PvP/boss khong bat dan do native");

        short[][] baDuongX = {duongX[0], duongX[0], duongX[0]};
        short[][] baDuongY = {duongY[0], duongY[0], duongY[0]};
        dichVu.guiLoatLaserUltronLuyenTap(
                (byte) 0, (short) 100, (short) 200, (short) 45,
                (byte) 30, baDuongX, baDuongY);
        xacNhanCriticalVaSoDuong(dichVu, 6, 84, 0, 3,
                "packet Ultron type 2 phai giu critical=0 de khong sinh 7 vien");
    }

    private static void xacNhanCriticalVaSoDuong(
            DichVuBatPacket dichVu,
            int chiSoPacket,
            int lenh,
            int critical,
            int soDuong,
            String thongBao
    ) throws Exception {
        dung(dichVu.cacLenh.size() > chiSoPacket
                        && dichVu.cacLenh.get(chiSoPacket) == lenh,
                thongBao + " (sai lenh)");
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(
                dichVu.cacDuLieu.get(chiSoPacket)));
        in.readUnsignedByte();
        dung(in.readUnsignedByte() == critical,
                thongBao + " (sai critical)");
        in.readUnsignedByte();
        int loaiDan = in.readUnsignedByte();
        in.readShort();
        in.readShort();
        in.readShort();
        if (loaiDan == 17 || loaiDan == 19) {
            in.readUnsignedByte();
        }
        in.readUnsignedByte();
        dung(in.readUnsignedByte() == soDuong,
                thongBao + " (sai so duong hien thi)");
    }

    /**
     * Hoi quy loi client giu gun/bullet cu sau khi server da doi sung.
     * Moi part co the cam trong snapshot phai nam trong CMD20 de client tai
     * BulletForGun truoc khi cho vao tran.
     */
    private static void kiemTraCmd20NapTruocSungTrongBalo()
            throws Exception {
        PhienKhongDuocNgat phien = new PhienKhongDuocNgat(97_020);
        DichVuBatPacket dichVu = new DichVuBatPacket(phien);
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 97_020;
        nguoiChoi.ten = "WeaponPreload";
        nguoiChoi.itemBody[5] = sung(110, 5, 57, 100, 700, 839);
        nguoiChoi.itemBag[3] = sung(120, 3, 27, 250, 430, 848);
        nguoiChoi.itemBag[8] = sung(160, 8, 56, 400, 620, 886);
        nguoiChoi.itemBalo = new int[]{3, 8};
        dichVu.datNguoiChoi(nguoiChoi);

        ChickenChienBinh[] chienBinhs = new ChickenChienBinh[8];
        chienBinhs[0] = new ChickenChienBinh(
                nguoiChoi, (byte)0, (short)100, (short)300);
        dichVu.guiBatDauDau((byte)4, chienBinhs, (byte)1);

        dung(dichVu.cacLenh.size() == 1
                        && dichVu.cacLenh.get(0) == 20,
                "khong bat duoc CMD20 preload sung");
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(
                dichVu.cacDuLieu.get(0)));
        dung(in.readUnsignedByte() == 4
                        && in.readUnsignedByte() == 25,
                "header CMD20 sai");
        for (int i = 0; i < 8; i++) {
            short x = in.readShort();
            if (x >= 0) {
                in.readShort();
                in.readShort();
                in.readShort();
            }
        }
        in.readUnsignedByte();
        int soSung = in.readUnsignedByte();
        List<Short> cacSung = new ArrayList<>();
        for (int i = 0; i < soSung; i++) {
            cacSung.add(in.readShort());
        }
        dung(soSung == 3
                        && cacSung.contains((short)57)
                        && cacSung.contains((short)27)
                        && cacSung.contains((short)56)
                        && in.available() == 0,
                "CMD20 khong nap du sung dang cam va sung Balo: "
                        + cacSung);
    }

    private static ChickenVatPham sung(int ma, int chiSo, int part,
            int tanCong, int napDan, int icon) {
        ChickenVatPham item = new ChickenVatPham(ma);
        item.ma = ma;
        item.mau = new ChickenMauVatPham((short) ma, (byte) 5,
                (byte) 0, "Gun" + ma, "", (byte) 1, 0,
                (short) icon, (short) part, false);
        item.chiSo = chiSo;
        item.HP = ChickenVatPham.DO_BEN_TOI_DA;
        item.itemOptions.add(option(1, tanCong));
        item.itemOptions.add(option(14, napDan));
        return item;
    }

    private static ChickenThuocTinhVatPham option(int ma, int thamSo) {
        ChickenMauThuocTinhVatPham mau =
                new ChickenMauThuocTinhVatPham();
        mau.ma = ma;
        ChickenThuocTinhVatPham option =
                new ChickenThuocTinhVatPham(ma, thamSo);
        option.optionTemplate = mau;
        return option;
    }

    private static void dung(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private static final class PhienKhongDuocNgat extends ChickenPhien {
        private boolean daBiNgat;

        private PhienKhongDuocNgat(int ma) {
            super(null, ma);
        }

        @Override
        public void dongTin() {
            this.daBiNgat = true;
        }
    }

    private static final class DichVuBatPacket
            extends ChickenDichVuGame {
        private final List<Integer> cacLenh = new ArrayList<>();
        private final List<byte[]> cacDuLieu = new ArrayList<>();

        private DichVuBatPacket(ChickenPhien phien) {
            super(phien);
        }

        @Override
        public void guiTin(ChickenTinNhan tin) {
            this.cacLenh.add((int) tin.layLenh());
            this.cacDuLieu.add(tin.layDuLieu().clone());
        }

        private void xoaPacket() {
            this.cacLenh.clear();
            this.cacDuLieu.clear();
        }
    }
}
