package com.chicken.mohinh;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.vatpham.ChickenMauThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenVatPham;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Integration test cho che do luyen tap. Dat trong cung package de doc duoc
 * trang thai phien ma khong mo public API chi phuc vu test trong production.
 */
public final class ChickenLuyenTapTestSupport {
    private ChickenLuyenTapTestSupport() {
    }

    public static void tuKiemTra() throws Exception {
        ChickenMauVatPham mauSungCu =
                ChickenQuanLyMayChu.itemTemplates.get(110);
        ChickenMauThuocTinhVatPham tanCongCu =
                ChickenQuanLyMayChu.iOptionTemplates.get(1);
        ChickenMauThuocTinhVatPham napDanCu =
                ChickenQuanLyMayChu.iOptionTemplates.get(14);

        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 94_001;
        nguoiChoi.ten = "TrainingModeSecurity";
        try {
            datOption(1, "Tan cong");
            datOption(14, "Nap dan");
            ChickenMauVatPham mauSung = new ChickenMauVatPham(
                    (short) 110, (byte) 5, (byte) 0,
                    "AT4 test", "", (byte) 1, 0,
                    (short) 1, (short) 57, false);
            mauSung.thuocTinhs.add(new ChickenThuocTinhVatPham(1, 500));
            mauSung.thuocTinhs.add(new ChickenThuocTinhVatPham(14, 100));
            ChickenQuanLyMayChu.itemTemplates.put(110, mauSung);

            ChickenVatPham sung = new ChickenVatPham(110);
            sung.chiSo = 5;
            nguoiChoi.itemBody[5] = sung;
            nguoiChoi.avenger = 0;
            nguoiChoi.inTraining = true;

            ChickenPhienLuyenTap phien = layPhien(nguoiChoi);
            khoiTaoPhien(phien);
            long maPhienTruocLenhLap = phien.trainingSessionId;
            int soTinTruocLenhLap = dichVu.tongSoTin();
            nguoiChoi.vaoLuyenTap();
            dung(nguoiChoi.inTraining,
                    "CMD83 lap lai lam roi phien luyen tap dang chay");
            bang(maPhienTruocLenhLap, phien.trainingSessionId,
                    "CMD83 lap lai tao ma phien luyen tap moi");
            bang(soTinTruocLenhLap, dichVu.tongSoTin(),
                    "CMD83 lap lai gui them packet tao bot/map");

            short xBanDau = phien.trainingPlayerX;
            short yBanDau = phien.trainingPlayerY;
            phien.trainingMoveRemaining = 40;
            nguoiChoi.handleTrainingMove(new ChickenTinNhan(
                    (byte) 21, taoPacketToaDo(
                            (short) 30_000, (short) -30_000)));
            int daDi = Math.abs(phien.trainingPlayerX - xBanDau);
            dung(daDi <= 40,
                    "luyen tap tin toa do client va teleport qua the luc");
            dung(phien.trainingPlayerY >= 0
                            && phien.trainingPlayerY
                            <= phien.trainingMap.getHeight(),
                    "luyen tap nhan Y client ra ngoai map");

            phien.trainingWaitingShotEnd = true;
            short xDangCho = phien.trainingPlayerX;
            short yDangCho = phien.trainingPlayerY;
            nguoiChoi.handleTrainingMove(new ChickenTinNhan(
                    (byte) 21, taoPacketToaDo((short) 0, (short) 0)));
            bang(xDangCho, phien.trainingPlayerX,
                    "dang cho dan ma luyen tap van cho di chuyen X");
            bang(yDangCho, phien.trainingPlayerY,
                    "dang cho dan ma luyen tap van cho di chuyen Y");

            phien.trainingWaitingShotEnd = false;
            phien.trainingCurrentTurn = 0;
            phien.trainingTurnId = 7L;
            phien.trainingLastShotTurnId = -1L;
            datLongRieng(nguoiChoi, "lastTrainingFire", 0L);
            short xServer = phien.trainingPlayerX;
            short yServer = phien.trainingPlayerY;

            ChickenTinNhan packetGia = new ChickenTinNhan(
                    (byte) 84,
                    taoPacketBan(
                            (byte) 127,
                            (short) 30_000,
                            (short) -30_000,
                            (short) 721,
                            255,
                            255));
            nguoiChoi.xuLyBanLuyenTap(packetGia);

            bang(1, dichVu.demLenh(84),
                    "luyen tap khong gui dung mot ket qua ban");
            bang(xServer, phien.trainingPlayerX,
                    "packet ban luyen tap ghi de X server");
            bang(yServer, phien.trainingPlayerY,
                    "packet ban luyen tap ghi de Y server");
            dung(phien.trainingWaitingShotEnd,
                    "luyen tap khong khoa action khi dan dang bay");
            bang(phien.trainingTurnId, phien.trainingLastShotTurnId,
                    "luyen tap khong danh dau mot phat moi turn");
            kiemTraPacketBanServer(dichVu.layTinCuoi(84), xServer, yServer);

            nguoiChoi.xuLyBanLuyenTap(new ChickenTinNhan(
                    (byte) 84, taoPacketBan(
                            (byte) 127, (short) 1, (short) 1,
                            (short) 90, 30, 8)));
            bang(1, dichVu.demLenh(84),
                    "luyen tap nhan phat ban lap khi dan cu chua xong");

            phien.trainingWaitingShotEnd = false;
            phien.trainingCurrentTurn = 1;
            phien.trainingLastShotTurnId = -1L;
            datLongRieng(nguoiChoi, "lastTrainingFire", 0L);
            nguoiChoi.xuLyBanLuyenTap(new ChickenTinNhan(
                    (byte) 84, taoPacketBan(
                            (byte) 0, xServer, yServer,
                            (short) 45, 20, 1)));
            bang(1, dichVu.demLenh(84),
                    "luyen tap cho nguoi choi ban sai luot bot");
        } finally {
            nguoiChoi.roiLuyenTap();
            khoiPhuc(ChickenQuanLyMayChu.itemTemplates, 110, mauSungCu);
            khoiPhuc(ChickenQuanLyMayChu.iOptionTemplates, 1, tanCongCu);
            khoiPhuc(ChickenQuanLyMayChu.iOptionTemplates, 14, napDanCu);
        }
    }

    private static void khoiTaoPhien(ChickenPhienLuyenTap phien) {
        phien.trainingCurrentTurn = 0;
        phien.trainingWaitingShotEnd = false;
        phien.trainingBotAnimating = false;
        phien.trainingBossState = ChickenNguoiChoi.TrainingBossState.IDLE;
        phien.trainingTurnId = 1L;
        phien.trainingLastShotTurnId = -1L;
        phien.trainingActiveShotResolved = true;
        phien.trainingPlayerX = 220;
        phien.trainingPlayerY = 300;
        phien.trainingPlayerHp = 10_000;
        phien.trainingPlayerMaxHp = 10_000;
        phien.trainingBossMaxHp = 10_000;
        for (int i = 0; i < phien.trainingBotHp.length; i++) {
            phien.trainingBotHp[i] = i == 0 ? 10_000 : 0;
            phien.trainingBotDead[i] = i != 0;
            phien.trainingBotX[i] = (short) (600 + i * 20);
            phien.trainingBotY[i] = 300;
        }
    }

    private static void kiemTraPacketBanServer(
            ChickenTinNhan tin,
            short xServer,
            short yServer
    ) throws Exception {
        dung(tin != null, "khong bat duoc CMD84 cua server");
        DataInputStream in = new ChickenTinNhan(
                (byte) 84, tin.layDuLieu()).boDoc();
        bang(1, in.readUnsignedByte(), "AT4 bi gui sai shoot type");
        bang(0, in.readUnsignedByte(), "CMD84 sai byte danh dau");
        bang(0, in.readUnsignedByte(), "CMD84 sai slot nguoi ban");
        bang(0, in.readUnsignedByte(),
                "client fake duoc loai dan trong luyen tap");
        bang(xServer, in.readShort(),
                "CMD84 gui X client thay vi X authoritative");
        bang(yServer, in.readShort(),
                "CMD84 gui Y client thay vi Y authoritative");
        bang(1, in.readUnsignedShort(),
                "goc 721 khong duoc chuan hoa ve 1");
        bang(1, in.readUnsignedByte(),
                "client fake duoc so loat cua AT4");
        bang(1, in.readUnsignedByte(),
                "client fake duoc so vien cua AT4");
    }

    private static ChickenPhienLuyenTap layPhien(
            ChickenNguoiChoi nguoiChoi
    ) throws Exception {
        Field field = ChickenNguoiChoi.class.getDeclaredField(
                "trainingSession");
        field.setAccessible(true);
        return (ChickenPhienLuyenTap) field.get(nguoiChoi);
    }

    private static void datLongRieng(
            ChickenNguoiChoi nguoiChoi,
            String tenField,
            long giaTri
    ) throws Exception {
        Field field = ChickenNguoiChoi.class.getDeclaredField(tenField);
        field.setAccessible(true);
        field.setLong(nguoiChoi, giaTri);
    }

    private static byte[] taoPacketBan(
            byte loaiDan,
            short x,
            short y,
            short goc,
            int luc,
            int soPhat
    ) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(out);
        data.writeByte(loaiDan);
        data.writeShort(x);
        data.writeShort(y);
        data.writeShort(goc);
        data.writeByte(luc);
        data.writeByte(soPhat);
        data.flush();
        return out.toByteArray();
    }

    private static byte[] taoPacketToaDo(short x, short y)
            throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(out);
        data.writeShort(x);
        data.writeShort(y);
        data.flush();
        return out.toByteArray();
    }

    private static void datOption(int ma, String ten) {
        ChickenMauThuocTinhVatPham option =
                new ChickenMauThuocTinhVatPham();
        option.ma = ma;
        option.ten = ten;
        ChickenQuanLyMayChu.iOptionTemplates.put(ma, option);
    }

    private static <T> void khoiPhuc(
            java.util.Map<Integer, T> map,
            int khoa,
            T giaTriCu
    ) {
        if (giaTriCu == null) {
            map.remove(khoa);
        } else {
            map.put(khoa, giaTriCu);
        }
    }

    private static void dung(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private static void bang(Object mongDoi, Object thucTe, String thongBao) {
        if (mongDoi == null ? thucTe != null : !mongDoi.equals(thucTe)) {
            throw new AssertionError(
                    thongBao + " expected=" + mongDoi
                    + " actual=" + thucTe);
        }
    }

    private static final class DichVuBatPacket
            extends ChickenDichVuGame {
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

        private synchronized int tongSoTin() {
            return this.tins.size();
        }

        private synchronized ChickenTinNhan layTinCuoi(int lenh) {
            for (int i = this.tins.size() - 1; i >= 0; i--) {
                ChickenTinNhan tin = this.tins.get(i);
                if (tin.layLenh() == (byte) lenh) {
                    return tin;
                }
            }
            return null;
        }
    }
}
