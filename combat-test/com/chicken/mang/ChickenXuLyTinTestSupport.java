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
    private static final int[] PART_TAO_NHAN_VAT = {
        0, 6, 11, 16, 21,
        8, 2, 17, 22, 12,
        57, 27, 54,
        7, 1, 18, 23, 13,
        10, 4, 20, 25, 15,
        9, 3, 19, 24, 14
    };

    private ChickenXuLyTinTestSupport() {
    }

    public static void tuKiemTra() throws Exception {
        kiemTraTaiKhoanMoiMoManTaoNhanVat();
        kiemTraMaTranBaoMatTaiNguyenTaoNhanVat();
        kiemTraGioiHanTaiNguyenVaTaoNhanVat();
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
        kiemTraPacketDanLazerHaiGiaiDoan();
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

    /**
     * Hoi quy luong dang ky -> dang nhap cua tai khoan chua co player.
     * CMD -37 chi can tai khoan da xac thuc; neu doi san player thi server
     * khong bao gio co co hoi gui CMD -99 mo man hinh tao nhan vat.
     */
    private static void kiemTraTaiKhoanMoiMoManTaoNhanVat()
            throws Exception {
        PhienKhongDuocNgat phien = new PhienKhongDuocNgat(97_037);
        DichVuBatPacket dichVu = new DichVuBatPacket(phien);
        ChickenNguoiDung user = new ChickenNguoiDung(phien, dichVu);
        datMaTaiKhoan(user, 97_037);
        phien.user = user;

        new ChickenXuLyTin(phien).khiCoTin(
                new ChickenTinNhan((byte) -37, new byte[0]));

        dung(!phien.daBiNgat,
                "CMD-37 cua tai khoan moi lam ngat ket noi");
        dung(dichVu.cacLenh.size() == 1
                        && dichVu.cacLenh.get(0) == -99,
                "tai khoan moi khong nhan dung mot CMD-99 tao nhan vat");

        PhienKhongDuocNgat chuaDangNhap =
                new PhienKhongDuocNgat(97_038);
        DichVuBatPacket dichVuChuaDangNhap =
                new DichVuBatPacket(chuaDangNhap);
        chuaDangNhap.datDichVu(dichVuChuaDangNhap);
        new ChickenXuLyTin(chuaDangNhap).khiCoTin(
                new ChickenTinNhan((byte) -37, new byte[0]));
        dung(dichVuChuaDangNhap.cacLenh.isEmpty(),
                "CMD-37 chua xac thuc van nhan phan hoi tao nhan vat");

        // Man tao nhan vat phai tai duoc tung Small image khi chua co player.
        phien.mucPhong = 2;
        dichVu.xoaPacket();
        new ChickenXuLyTin(phien).khiCoTin(
                new ChickenTinNhan((byte) -41,
                        new byte[]{0, (byte) 159}));
        dung(dichVu.cacLenh.size() == 1
                        && dichVu.cacLenh.get(0) == -41,
                "tai khoan moi bi chan CMD-41 tai sprite nhan vat");
        DataInputStream icon = new DataInputStream(
                new ByteArrayInputStream(dichVu.cacDuLieu.get(0)));
        dung(icon.readUnsignedShort() == 159,
                "phan hoi CMD-41 sai ID icon");
        int soByteAnh = icon.readInt();
        byte[] anh = icon.readNBytes(soByteAnh);
        dung(soByteAnh > 8 && anh.length == soByteAnh
                        && (anh[0] & 0xFF) == 0x89
                        && anh[1] == 'P' && anh[2] == 'N'
                        && anh[3] == 'G' && icon.available() == 0,
                "CMD-41 khong tra du PNG cua sprite nhan vat");

        dung(ChickenXuLyTin.chiCanTaiKhoanDaXacThuc(-41)
                        && ChickenXuLyTin.chiCanTaiKhoanDaXacThuc(-37)
                        && ChickenXuLyTin.chiCanTaiKhoanDaXacThuc(-99)
                        && !ChickenXuLyTin.chiCanTaiKhoanDaXacThuc(22),
                "phan tang xac thuc tai khoan/nhan vat bi sai");
    }

    /**
     * Ma tran bao mat cho dung loi vua sua: tai khoan da xac thuc nhung chua
     * co player. Quet 28 part tao nhan vat tren 4 muc phong = 112 yeu cau anh
     * that, toan bo mien byte command va cac packet CMD -41 sai kich thuoc.
     */
    private static void kiemTraMaTranBaoMatTaiNguyenTaoNhanVat()
            throws Exception {
        int soAnhDaQuet = 0;
        for (int mucPhong = 1; mucPhong <= 4; mucPhong++) {
            PhienKhongDuocNgat phien =
                    new PhienKhongDuocNgat(98_000 + mucPhong);
            DichVuBatPacket dichVu = new DichVuBatPacket(phien);
            phien.user = new ChickenNguoiDung(phien, dichVu);
            phien.mucPhong = (byte) mucPhong;
            ChickenXuLyTin router = new ChickenXuLyTin(phien);

            for (int id : PART_TAO_NHAN_VAT) {
                dichVu.xoaPacket();
                router.khiCoTin(new ChickenTinNhan((byte) -41,
                        new byte[]{(byte) (id >>> 8), (byte) id}));
                dung(!phien.daBiNgat,
                        "tai sprite hop le lam ngat phien x=" + mucPhong
                                + " id=" + id);
                dung(dichVu.cacLenh.size() == 1
                                && dichVu.cacLenh.get(0) == -41,
                        "sprite tao nhan vat khong duoc tra x=" + mucPhong
                                + " id=" + id);
                DataInputStream doc = new DataInputStream(
                        new ByteArrayInputStream(dichVu.cacDuLieu.get(0)));
                dung(doc.readUnsignedShort() == id,
                        "server tra nham ID sprite x=" + mucPhong
                                + " id=" + id);
                int doDai = doc.readInt();
                byte[] png = doc.readNBytes(doDai);
                dung(doDai > 8 && png.length == doDai
                                && (png[0] & 0xFF) == 0x89
                                && png[1] == 'P' && png[2] == 'N'
                                && png[3] == 'G' && doc.available() == 0,
                        "sprite khong phai PNG day du x=" + mucPhong
                                + " id=" + id);
                soAnhDaQuet++;
            }
        }
        dung(soAnhDaQuet == 112,
                "chua quet du 112 sprite man tao nhan vat");

        // Khong duoc vo tinh mo them command gameplay cho account chua co player.
        int soCommandDaQuet = 0;
        for (int cmd = Byte.MIN_VALUE; cmd <= Byte.MAX_VALUE; cmd++) {
            boolean mongDoi = switch (cmd) {
                case -99, -41, -38, -37, -32, -31 -> true;
                default -> false;
            };
            dung(ChickenXuLyTin.chiCanTaiKhoanDaXacThuc(cmd) == mongDoi,
                    "phan quyen sai command=" + cmd);
            soCommandDaQuet++;
        }
        dung(soCommandDaQuet == 256,
                "chua quet du mien byte command");

        // Packet chua dang nhap khong duoc lay mot icon hop le.
        PhienKhongDuocNgat voDanh = new PhienKhongDuocNgat(98_100);
        DichVuBatPacket dichVuVoDanh = new DichVuBatPacket(voDanh);
        voDanh.datDichVu(dichVuVoDanh);
        voDanh.mucPhong = 2;
        new ChickenXuLyTin(voDanh).khiCoTin(new ChickenTinNhan((byte) -41,
                new byte[]{0, (byte) 159}));
        dung(dichVuVoDanh.cacLenh.isEmpty(),
                "phien vo danh tai duoc sprite");

        // Moi do dai khac dung hai byte deu phai bi tu choi, khong co response.
        int soPayloadSaiDaQuet = 0;
        for (int doDai = 0; doDai <= 100; doDai++) {
            if (doDai == 2) {
                continue;
            }
            PhienKhongDuocNgat phien =
                    new PhienKhongDuocNgat(98_200 + doDai);
            DichVuBatPacket dichVu = new DichVuBatPacket(phien);
            phien.user = new ChickenNguoiDung(phien, dichVu);
            phien.mucPhong = 2;
            byte[] payload = new byte[doDai];
            new ChickenXuLyTin(phien).khiCoTin(
                    new ChickenTinNhan((byte) -41, payload));
            dung(dichVu.cacLenh.isEmpty(),
                    "CMD-41 sai kich thuoc van co response len=" + doDai);
            soPayloadSaiDaQuet++;
        }
        dung(soPayloadSaiDaQuet == 100,
                "chua quet du 100 payload CMD-41 sai kich thuoc");

        // Hai byte van phai bi chan neu ID/scale nam ngoai mien cho phep.
        int[] idNgoaiMien = {3000, 3001, 32767, 65535};
        int[] mucPhongNgoaiMien = {0, 5, 127, 255};
        int soBienDaQuet = 0;
        for (int mucPhong : mucPhongNgoaiMien) {
            for (int id : idNgoaiMien) {
                PhienKhongDuocNgat phien =
                        new PhienKhongDuocNgat(98_400 + soBienDaQuet);
                DichVuBatPacket dichVu = new DichVuBatPacket(phien);
                phien.user = new ChickenNguoiDung(phien, dichVu);
                phien.mucPhong = (byte) mucPhong;
                new ChickenXuLyTin(phien).khiCoTin(
                        new ChickenTinNhan((byte) -41,
                                new byte[]{(byte) (id >>> 8), (byte) id}));
                dung(dichVu.cacLenh.isEmpty(),
                        "CMD-41 ngoai bien van co response scale="
                                + mucPhong + " id=" + id);
                soBienDaQuet++;
            }
        }
        dung(soBienDaQuet == 16,
                "chua quet du ma tran ID/scale ngoai bien");

        System.out.println("ACCOUNT_RESOURCE_SECURITY_MATRIX_OK"
                + " validSprites=" + soAnhDaQuet
                + " commandDomain=" + soCommandDaQuet
                + " malformedPayloads=" + soPayloadSaiDaQuet
                + " invalidBounds=" + soBienDaQuet);
    }

    private static void kiemTraGioiHanTaiNguyenVaTaoNhanVat()
            throws Exception {
        int[] lenhTaiDuLieu = {-31, -32, -37, -38};
        int soPayloadDuDaChan = 0;
        for (int i = 0; i < lenhTaiDuLieu.length; i++) {
            int cmd = lenhTaiDuLieu[i];
            PhienKhongDuocNgat phien =
                    new PhienKhongDuocNgat(98_600 + i);
            DichVuBatPacket dichVu = new DichVuBatPacket(phien);
            ChickenNguoiDung user = new ChickenNguoiDung(phien, dichVu);
            datMaTaiKhoan(user, 98_600 + i);
            phien.user = user;

            new ChickenXuLyTin(phien).khiCoTin(
                    new ChickenTinNhan((byte) cmd, new byte[]{1}));
            dung(dichVu.cacLenh.isEmpty(),
                    "CMD tai du lieu co payload du van duoc xu ly cmd="
                            + cmd);
            dung(!phien.daBiNgat,
                    "mot payload du lam ngat phien cmd=" + cmd);
            soPayloadDuDaChan++;
        }

        PhienKhongDuocNgat phienLap =
                new PhienKhongDuocNgat(98_610);
        DichVuBatPacket dichVuLap = new DichVuBatPacket(phienLap);
        ChickenNguoiDung userLap =
                new ChickenNguoiDung(phienLap, dichVuLap);
        datMaTaiKhoan(userLap, 98_610);
        phienLap.user = userLap;
        ChickenXuLyTin routerLap = new ChickenXuLyTin(phienLap);
        routerLap.khiCoTin(new ChickenTinNhan((byte) -37, new byte[0]));
        dung(dichVuLap.cacLenh.size() == 1
                        && dichVuLap.cacLenh.get(0) == -99,
                "CMD-37 hop le khong mo man tao nhan vat");
        dichVuLap.xoaPacket();
        routerLap.khiCoTin(new ChickenTinNhan((byte) -37, new byte[0]));
        dung(dichVuLap.cacLenh.isEmpty(),
                "CMD-37 lap trong cung phien van tao response");

        int accountTaiLai = 98_620;
        PhienKhongDuocNgat taiLanMot = phienCoTaiKhoan(
                98_621, accountTaiLai);
        PhienKhongDuocNgat taiLanHai = phienCoTaiKhoan(
                98_622, accountTaiLai);
        PhienKhongDuocNgat taiLanBa = phienCoTaiKhoan(
                98_623, accountTaiLai);
        dung(taiLanMot.choPhepYeuCauTaiDuLieu(-31, 10_000L),
                "tai du lieu lan dau bi chan");
        dung(taiLanHai.choPhepYeuCauTaiDuLieu(-31, 10_001L),
                "mot lan reconnect that bi chan");
        dung(!taiLanBa.choPhepYeuCauTaiDuLieu(-31, 10_002L),
                "reconnect vuot quota van tai duoc blob lon");
        dung(!taiLanMot.choPhepYeuCauTaiDuLieu(-31, 70_001L),
                "cung phien tai lai du lieu lan hai");

        int accountMoi = 98_630;
        PhienKhongDuocNgat phienA = phienCoTaiKhoan(98_631, accountMoi);
        PhienKhongDuocNgat phienB = phienCoTaiKhoan(98_632, accountMoi);
        for (int i = 0; i < 225; i++) {
            dung(phienA.choPhepXuLyLenh(5, 20_000L),
                    "quota account chan som phien A i=" + i);
            dung(phienB.choPhepXuLyLenh(5, 20_000L),
                    "quota account chan som phien B i=" + i);
        }
        dung(!phienA.choPhepXuLyLenh(5, 20_000L),
                "tai khoan chua co player vuot 450 packet/giay");

        PhienKhongDuocNgat phienDaCoNhanVat =
                new PhienKhongDuocNgat(98_640);
        DichVuBatPacket dichVuDaCoNhanVat =
                new DichVuBatPacket(phienDaCoNhanVat);
        ChickenNguoiDung userDaCoNhanVat = new ChickenNguoiDung(
                phienDaCoNhanVat, dichVuDaCoNhanVat);
        datMaTaiKhoan(userDaCoNhanVat, 98_640);
        ChickenNguoiChoi nguoiChoi =
                new ChickenNguoiChoi(dichVuDaCoNhanVat);
        nguoiChoi.ma = 98_641;
        userDaCoNhanVat.nguoiChoi = nguoiChoi;
        phienDaCoNhanVat.user = userDaCoNhanVat;
        ChickenXuLyTin routerDaCoNhanVat =
                new ChickenXuLyTin(phienDaCoNhanVat);
        for (int i = 0; i < 8; i++) {
            routerDaCoNhanVat.khiCoTin(
                    new ChickenTinNhan((byte) -99, new byte[0]));
        }
        dung(dichVuDaCoNhanVat.cacLenh.isEmpty(),
                "CMD-99 tao lai van cham luong tao nhan vat");
        dung(phienDaCoNhanVat.daBiNgat,
                "spam CMD-99 tao lai khong bi ngat sau nguong loi");

        System.out.println("ACCOUNT_RESOURCE_RATE_LIMIT_OK"
                + " emptyPayloadCommands=4 malformedBlocked="
                + soPayloadDuDaChan
                + " sameSessionReplay=blocked reconnectQuota=2"
                + " accountWithoutPlayerRate=450"
                + " existingPlayerCreateSpam=blocked");
    }

    private static PhienKhongDuocNgat phienCoTaiKhoan(
            int maPhien,
            int maTaiKhoan
    ) throws Exception {
        PhienKhongDuocNgat phien = new PhienKhongDuocNgat(maPhien);
        DichVuBatPacket dichVu = new DichVuBatPacket(phien);
        ChickenNguoiDung user = new ChickenNguoiDung(phien, dichVu);
        datMaTaiKhoan(user, maTaiKhoan);
        phien.user = user;
        return phien;
    }

    private static void datMaTaiKhoan(
            ChickenNguoiDung user,
            int maTaiKhoan
    ) throws Exception {
        Field field = ChickenNguoiDung.class.getDeclaredField("user_id");
        field.setAccessible(true);
        field.setInt(user, maTaiKhoan);
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
        if (loaiDan == 14 || loaiDan == 40) {
            in.readUnsignedByte();
            in.readUnsignedByte();
        }
        in.readUnsignedByte();
        dung(in.readUnsignedByte() == soDuong,
                thongBao + " (sai so duong hien thi)");
    }

    /** Bullet 14 bat buoc co goc/luc byte va hai path o ca PvP lan tap. */
    private static void kiemTraPacketDanLazerHaiGiaiDoan()
            throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket(
                new PhienKhongDuocNgat(97_128));
        short[][] duongX = {
            new short[]{100, 110, 120}, new short[]{120}
        };
        short[][] duongY = {
            new short[]{200, 190, 210}, new short[]{210}
        };
        ChickenKetQuaDan ketQua = new ChickenKetQuaDan(
                (byte) 14, (short) 100, (short) 200,
                (short) 45, (byte) 30, (byte) 30,
                duongX, duongY, java.util.Collections.emptyMap());

        dichVu.guiKetQuaBanDau(
                (byte) 0, (short) 100, (short) 200,
                ketQua, (byte) 1, false, false);
        xacNhanPacketDanLazer(dichVu, 0, 22);

        dichVu.guiKetQuaBanLuyenTap(
                (byte) 0, (byte) 14,
                (short) 100, (short) 200, (short) 45,
                (byte) 30, (byte) 30, (byte) 1,
                duongX, duongY, false, false);
        xacNhanPacketDanLazer(dichVu, 1, 84);
    }

    private static void xacNhanPacketDanLazer(
            DichVuBatPacket dichVu,
            int chiSoPacket,
            int lenh
    ) throws Exception {
        dung(dichVu.cacLenh.get(chiSoPacket) == lenh,
                "Dan Lazer sai lenh packet " + lenh);
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(
                dichVu.cacDuLieu.get(chiSoPacket)));
        dung(in.readUnsignedByte() == 1,
                "Dan Lazer sai typeShoot");
        dung(in.readUnsignedByte() == 0,
                "Dan Lazer bi gia critical");
        dung(in.readUnsignedByte() == 0,
                "Dan Lazer sai whoShoot");
        dung(in.readUnsignedByte() == 14,
                "Dan Lazer sai bulletType");
        dung(in.readShort() == 100 && in.readShort() == 200,
                "Dan Lazer sai toa do shooter");
        dung(in.readUnsignedShort() == 45,
                "Dan Lazer sai goc short");
        dung(in.readUnsignedByte() == 45,
                "Dan Lazer thieu goc byte native");
        dung(in.readUnsignedByte() == 30,
                "Dan Lazer thieu luc byte native");
        dung(in.readUnsignedByte() == 1,
                "Dan Lazer sai numShoot");
        dung(in.readUnsignedByte() == 2,
                "Dan Lazer thieu path type 15");
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
