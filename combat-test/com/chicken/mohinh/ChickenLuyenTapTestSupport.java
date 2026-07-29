package com.chicken.mohinh;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.phong.boss.trandau.ChickenSungShopBoss;
import com.chicken.vatpham.ChickenMauThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenVatPham;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        int expThuongCu = ChickenQuanLyMayChu.trainingExpReward;
        int vangThuongCu = ChickenQuanLyMayChu.trainingGoldReward;

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
            mauSung.buyGold = 2_000;
            ChickenQuanLyMayChu.itemTemplates.put(110, mauSung);

            ChickenVatPham sung = new ChickenVatPham(110);
            sung.chiSo = 5;
            nguoiChoi.itemBody[5] = sung;
            nguoiChoi.avenger = 0;
            nguoiChoi.inTraining = true;

            ChickenPhienLuyenTap phien = layPhien(nguoiChoi);
            ChickenQuanLyDanSung.DuLieuSung sungBot =
                    ChickenSungShopBoss.chonNgauNhienKhongAvg();
            dung(sungBot != null,
                    "bot luyen tap random ID sung khong co mapping");
            ChickenMauVatPham mauSungBot =
                    ChickenQuanLyMayChu.itemTemplates.get(
                            sungBot.getIdSung());
            dung(ChickenSungShopBoss.laSungShopKhongAvg(mauSungBot),
                    "bot luyen tap random ngoai shop hoac trung AVG");
            int tanCongBot = ChickenSungShopBoss.layTanCongTheoId(
                    sungBot.getIdSung(), 0);
            int napDanBot = ChickenSungShopBoss.layNapDanTheoId(
                    sungBot.getIdSung(), 0);
            ChickenQuanLyPhienLuyenTap.batDauPhien(
                    phien,
                    100,
                    napDanBot,
                    sungBot.getIdSung(),
                    sungBot.getPartSung(),
                    tanCongBot,
                    10_000,
                    (byte) 0);
            bang(sungBot.getPartSung(),
                    phien.trainingBossWeaponPart,
                    "part bot luyen tap khong khop ID sung random");
            bang(ChickenSungShopBoss.layTanCongTheoId(
                            sungBot.getIdSung(), 0),
                    phien.trainingBossAttack,
                    "bot luyen tap khong lay dung damage sung shop");
            bang(ChickenSungShopBoss.layNapDanTheoId(
                            sungBot.getIdSung(), 0),
                    phien.trainingBossReloadTime,
                    "bot luyen tap khong lay dung nap dan sung shop");
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

            kiemTraBotDungCongThucSungShop();
            kiemTraUltronX3KhongMatXacNhanSom(
                    nguoiChoi, phien, dichVu);
            kiemTraThuongVaBangKetQua(
                    nguoiChoi, phien, dichVu);
        } finally {
            ChickenKinhTeLuyenTap.datKhoChoKiemThu(null);
            ChickenQuanLyMayChu.trainingExpReward = expThuongCu;
            ChickenQuanLyMayChu.trainingGoldReward = vangThuongCu;
            nguoiChoi.roiLuyenTap();
            khoiPhuc(ChickenQuanLyMayChu.itemTemplates, 110, mauSungCu);
            khoiPhuc(ChickenQuanLyMayChu.iOptionTemplates, 1, tanCongCu);
            khoiPhuc(ChickenQuanLyMayChu.iOptionTemplates, 14, napDanCu);
        }
    }

    private static void kiemTraBotDungCongThucSungShop()
            throws Exception {
        ChickenQuanLyDanSung.DuLieuSung sungChuoi =
                ChickenQuanLyDanSung.theoIdSung(140);
        dung(sungChuoi != null,
                "thieu mapping sung chuoi de test bot luyen tap");

        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 94_002;
        nguoiChoi.ten = "TrainingRandomGunFormula";
        nguoiChoi.inTraining = true;
        ChickenPhienLuyenTap phien = layPhien(nguoiChoi);
        khoiTaoPhien(phien);
        phien.trainingBossWeaponId = sungChuoi.getIdSung();
        phien.trainingBossWeaponPart = sungChuoi.getPartSung();
        phien.trainingBossAttack = 320;
        phien.trainingBossReloadTime = 320;

        Method banTra = ChickenNguoiChoi.class.getDeclaredMethod(
                "botLuyenTapBanTra", int.class);
        banTra.setAccessible(true);
        long thoiGianHoatAnh = (Long) banTra.invoke(nguoiChoi, 0);
        dung(thoiGianHoatAnh > 0,
                "bot luyen tap khong tinh thoi gian animation dan");
        bang(1, dichVu.demLenh(84),
                "bot luyen tap khong gui dung mot packet ban");

        DataInputStream in = new ChickenTinNhan(
                (byte) 84, dichVu.layTinCuoi(84).layDuLieu()).boDoc();
        bang(1, in.readUnsignedByte(),
                "sung chuoi bot gui sai shoot type");
        bang(0, in.readUnsignedByte(),
                "packet bot sai byte danh dau");
        bang(1, in.readUnsignedByte(),
                "packet bot sai slot nguoi ban");
        bang(sungChuoi.getLoaiDan() & 0xFF,
                in.readUnsignedByte(),
                "bot luyen tap khong dung loai dan theo ID sung");
        in.readShort();
        in.readShort();
        in.readShort();
        bang(1, in.readUnsignedByte(),
                "bot luyen tap fake so lan ban");
        bang(sungChuoi.getSoVienMoiLoat() & 0xFF,
                in.readUnsignedByte(),
                "bot luyen tap khong dung so vien theo cong thuc sung");
        nguoiChoi.roiLuyenTap();
    }

    private static void kiemTraUltronX3KhongMatXacNhanSom(
            ChickenNguoiChoi nguoiChoi,
            ChickenPhienLuyenTap phien,
            DichVuBatPacket dichVu
    ) throws Exception {
        nguoiChoi.avenger = 8;
        phien.trainingBossState =
                ChickenNguoiChoi.TrainingBossState.IDLE;
        phien.trainingCurrentTurn = 0;
        phien.trainingWaitingShotEnd = false;
        phien.trainingTurnId = 81L;
        phien.trainingLastShotTurnId = -1L;
        phien.trainingUltronMenuTurnId = phien.trainingTurnId;
        phien.trainingUltronDaDungKyNang = false;
        phien.trainingUltronDangBanX3 = false;
        for (int i = 0; i < phien.trainingBotHp.length; i++) {
            phien.trainingBotHp[i] = 1_000_000;
            phien.trainingBotDead[i] = false;
        }
        datLongRieng(nguoiChoi, "lastTrainingFire", 0L);
        dung(nguoiChoi.kichHoatKyNangUltronLuyenTap(),
                "khong kich hoat duoc Ultron X3 luyen tap");

        int packetTruoc = dichVu.demLenh(84);
        nguoiChoi.xuLyBanLuyenTap(new ChickenTinNhan(
                (byte) 84,
                taoPacketBan(
                        (byte) 0,
                        phien.trainingPlayerX,
                        phien.trainingPlayerY,
                        (short) 45,
                        30,
                        30)));
        bang(packetTruoc + 1, dichVu.demLenh(84),
                "Ultron X3 luyen tap khong gui vien dau");

        for (int phatDaGui = 1; phatDaGui <= 3; phatDaGui++) {
            phien.trainingXacNhanDanSomNhatMs =
                    System.currentTimeMillis() + 80L;
            nguoiChoi.xuLyVaChamLuyenTap(new ChickenTinNhan(
                    (byte) 79, new byte[]{0}));
            if (phatDaGui < 3) {
                bang(packetTruoc + phatDaGui, dichVu.demLenh(84),
                        "CMD79 som da tua nhanh Ultron X3 luyen tap");
            }
            Thread.sleep(140L);
            if (phatDaGui < 3) {
                bang(packetTruoc + phatDaGui + 1,
                        dichVu.demLenh(84),
                        "CMD79 som bi mat, Ultron X3 luyen tap thieu vien");
            }
        }
        dung(!phien.trainingWaitingShotEnd,
                "Ultron X3 luyen tap khong dong loat sau ba vien");
        nguoiChoi.avenger = 0;
    }

    private static void kiemTraThuongVaBangKetQua(
            ChickenNguoiChoi nguoiChoi,
            ChickenPhienLuyenTap phien,
            DichVuBatPacket dichVu
    ) throws Exception {
        bang(0, ChickenKinhTeLuyenTap.gioiHanExpPacket(-1),
                "EXP luyen tap am khong bi chan");
        bang((int) Short.MAX_VALUE,
                ChickenKinhTeLuyenTap.gioiHanExpPacket(
                        Integer.MAX_VALUE),
                "EXP luyen tap vuot gioi han packet");
        nguoiChoi.datKinhNghiemVaCanBangTrongBoNho(1_000);
        nguoiChoi.vang = 2_000;
        nguoiChoi.ngoc = 30;
        nguoiChoi.cap = 0;
        nguoiChoi.capCaoNhatDaNhanThuong = 0;
        nguoiChoi.point = 0;
        nguoiChoi.powerAvenger = 100;
        nguoiChoi.datSoTranThangLuyenTap(0);
        nguoiChoi.inTraining = true;
        phien.trainingBossState =
                ChickenNguoiChoi.TrainingBossState.IDLE;
        phien.trainingRewardOperationKey =
                "training-win:test-result-94001";
        ChickenQuanLyMayChu.trainingExpReward = 1_000;
        ChickenQuanLyMayChu.trainingGoldReward = 5_000;

        KhoThuongRam kho = new KhoThuongRam(nguoiChoi);
        ChickenKinhTeLuyenTap.datKhoChoKiemThu(kho);
        dichVu.xoaTin();

        Method xuLyThang = ChickenNguoiChoi.class
                .getDeclaredMethod("xuLyNguoiChoiThangLuyenTap");
        xuLyThang.setAccessible(true);
        xuLyThang.invoke(nguoiChoi);

        bang(1, kho.soGiaoDich(),
                "thang luyen tap khong ghi dung mot giao dich");
        bang(2_000, nguoiChoi.layKinhNghiem(),
                "EXP luyen tap khong dong bo sau commit");
        bang(7_000, nguoiChoi.vang,
                "vang luyen tap khong dong bo sau commit");
        bang(1, nguoiChoi.laySoTranThangLuyenTap(),
                "so tran thang khong nam trong transaction");
        bang(1, dichVu.demLenh(50),
                "ket qua luyen tap khong gui dung mot CMD 50");
        bang(0, dichVu.demLenh(3),
                "CMD 3 dong bang ket qua luyen tap ngay lap tuc");
        dung(nguoiChoi.inTraining,
                "server roi luyen tap truoc khi nguoi choi bam OK");

        DataInputStream ketQua = new ChickenTinNhan(
                (byte) 50,
                dichVu.layTinCuoi(50).layDuLieu()).boDoc();
        bang(1, ketQua.readUnsignedByte(),
                "bang ket qua khong hien Thang");
        bang(1_000, (int) ketQua.readShort(),
                "bang ket qua hien sai EXP da commit");
        bang(5_000, ketQua.readInt(),
                "bang ket qua hien sai vang da commit");
        bang(0, (int) ketQua.readShort(),
                "bang ket qua hien sai ngoc tang cap");
        bang(0, ketQua.readUnsignedByte(),
                "packet ket qua thieu byte gift");
        bang(0, ketQua.available(),
                "packet ket qua co byte thua");

        xuLyThang.invoke(nguoiChoi);
        bang(1, kho.soGiaoDich(),
                "goi ket thuc lap tao them giao dich");
        bang(2_000, nguoiChoi.layKinhNghiem(),
                "goi ket thuc lap cong EXP lan hai");
        bang(7_000, nguoiChoi.vang,
                "goi ket thuc lap cong vang lan hai");
        bang(1, dichVu.demLenh(50),
                "goi ket thuc lap gui hai bang ket qua");

        ChickenNguoiChoi loiDb =
                new ChickenNguoiChoi(new ChickenDichVuGame(null));
        loiDb.ma = 94_002;
        loiDb.datKinhNghiemVaCanBangTrongBoNho(3_000);
        loiDb.vang = 4_000;
        loiDb.ngoc = 50;
        ChickenKinhTeLuyenTap.KhoThuong khoLoi =
                (ma, giaoDich, exp, vang) -> {
                    throw new SQLException("loi DB chu dong");
                };
        ChickenKinhTeLuyenTap.KetQua thatBai =
                ChickenKinhTeLuyenTap.traoThuong(
                        loiDb,
                        "training-win:test-db-error",
                        1_000,
                        5_000,
                        khoLoi);
        dung(!thatBai.thanhCong,
                "DB loi nhung server van bao thuong thanh cong");
        bang(3_000, loiDb.layKinhNghiem(),
                "DB rollback nhung RAM van bi cong EXP");
        bang(4_000, loiDb.vang,
                "DB rollback nhung RAM van bi cong vang");
        bang(50, loiDb.ngoc,
                "DB rollback nhung RAM van bi cong ngoc");
    }

    private static final class KhoThuongRam
            implements ChickenKinhTeLuyenTap.KhoThuong {
        private final Set<String> giaoDichs = new HashSet<>();
        private int exp;
        private int vang;
        private int ngoc;
        private int cap;
        private int mocCap;
        private short diem;
        private int wins;
        private int nangLuong;

        KhoThuongRam(ChickenNguoiChoi nguoiChoi) {
            this.exp = nguoiChoi.layKinhNghiem();
            this.vang = nguoiChoi.vang;
            this.ngoc = nguoiChoi.ngoc;
            this.cap = nguoiChoi.cap;
            this.mocCap = nguoiChoi.capCaoNhatDaNhanThuong;
            this.diem = nguoiChoi.point;
            this.wins = nguoiChoi.laySoTranThangLuyenTap();
            this.nangLuong = nguoiChoi.powerAvenger & 0xFF;
        }

        @Override
        public ChickenKinhTeLuyenTap.KetQua apDung(
                int maNguoiChoi,
                String maGiaoDich,
                int expThuong,
                int vangThuong
        ) {
            if (!this.giaoDichs.add(maGiaoDich)) {
                return ChickenKinhTeLuyenTap.KetQua.thanhCong(
                        true, 0, 0, 0, this.exp, this.vang,
                        this.cap, this.diem, this.mocCap, this.ngoc,
                        this.wins, this.nangLuong);
            }
            int expCu = this.exp;
            int vangCu = this.vang;
            this.exp = (int) Math.min(
                    Integer.MAX_VALUE,
                    (long) this.exp + Math.max(0, expThuong));
            this.vang = (int) Math.min(
                    Integer.MAX_VALUE,
                    (long) this.vang + Math.max(0, vangThuong));
            this.cap = com.chicken.tienich.ChickenTienIch
                    .layCap(this.exp);
            this.mocCap = Math.max(this.mocCap, this.cap);
            this.wins++;
            this.nangLuong = 100;
            return ChickenKinhTeLuyenTap.KetQua.thanhCong(
                    false,
                    this.exp - expCu,
                    this.vang - vangCu,
                    0,
                    this.exp,
                    this.vang,
                    this.cap,
                    this.diem,
                    this.mocCap,
                    this.ngoc,
                    this.wins,
                    this.nangLuong);
        }

        int soGiaoDich() {
            return this.giaoDichs.size();
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

        private synchronized void xoaTin() {
            this.tins.clear();
        }
    }
}
