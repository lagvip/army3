package com.chicken.mang;

import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mohinh.ChickenNguoiDung;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.sanhcho.VaoSanhChoBoss;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

        private DichVuBatPacket(ChickenPhien phien) {
            super(phien);
        }

        @Override
        public void guiTin(ChickenTinNhan tin) {
            this.cacLenh.add((int) tin.layLenh());
        }

        private void xoaPacket() {
            this.cacLenh.clear();
        }
    }
}
