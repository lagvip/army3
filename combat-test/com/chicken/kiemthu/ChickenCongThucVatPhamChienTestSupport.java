package com.chicken.kiemthu;

import com.chicken.chien.ChickenCongThucVatPhamChien;
import com.chicken.chien.ChickenCongThucVatPhamChien.CauHinh;
import com.chicken.chien.ChickenCongThucVatPhamChien.KieuGoc;
import com.chicken.chien.ChickenCongThucVatPhamChien.KieuQuyDao;
import com.chicken.chien.ChickenCauHinhSatThuongVatPham;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.chien.ChickenLoaiDanPhaDiaHinhClient;
import com.chicken.chien.ChickenMayMan;
import com.chicken.chien.ChickenPhatBanServer;
import com.chicken.chien.ChickenPhatBanVatPhamServer;
import com.chicken.chien.ChickenHieuUngVatPhamChien;
import com.chicken.chien.ChickenDiChuyenServer;
import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.chien.ChickenQuanLyCongThucSung;
import com.chicken.chien.ChickenTrangThaiHanhDongLuot;
import com.chicken.chien.ChickenThoiGianHoatAnhDan;
import com.chicken.chien.ChickenCuuThuongBanThan;
import com.chicken.chien.ChickenCuuThuongDongDoi;
import com.chicken.chien.ChickenDiChuyenX2;
import com.chicken.chien.ChickenNgungGio;
import com.chicken.chien.ChickenYeuCauBanServer;
import com.chicken.gio.ChickenHeThongGio;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.phong.boss.trandau.rong.DiChuyenBossRong;
import com.chicken.phong.boss.trandau.rua.DiChuyenBossRua;
import com.chicken.luyentap.ChickenAiLuyenTap;
import com.chicken.luyentap.ChickenCauHinhLuyenTap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/** Doi chieu bang vat pham chien dau server voi CPlayer cua client goc. */
public final class ChickenCongThucVatPhamChienTestSupport {

    private ChickenCongThucVatPhamChienTestSupport() {
    }

    public static void tuKiemTra() {
        // id, itemUsed, bulletType, coHeSoCoDinh, heSoGio, trongLuc.
        int[][] mongDoi = {
            {220, 0, -1, 0, 0, 0},
            {221, 1, 5, 1, 0, 80},
            {222, 2, -1, 0, 0, 0},
            {223, 3, -1, 0, 0, 0},
            {225, 5, -1, 0, 0, 0},
            {226, 6, 6, 1, 70, 90},
            {227, 7, 7, 1, 70, 80},
            {228, 8, 4, 1, 0, 80},
            {229, 9, 8, 1, 70, 70},
            {230, 10, -1, 0, 0, 0},
            {231, 11, 16, 1, 0, 100},
            {235, 16, 14, 1, 10, 50},
            {236, 17, 13, 1, 50, 120},
            {237, 18, 22, 0, 0, 0},
            {238, 19, 26, 1, 30, 60},
            {239, 20, 25, 1, 0, -50},
            {240, 21, 23, 1, 20, 100},
            {241, 22, 28, 1, 0, 20},
            {242, 23, 30, 0, 0, 0},
            {258, 24, 50, 0, 0, 0},
            {244, 25, 51, 1, 5, 60},
            {245, 26, 52, 1, 10, 100},
            {246, 27, 53, 0, 0, 0},
            {247, 28, 54, 1, 0, 80},
            {243, 29, 55, 1, 6, 60},
            {248, 29, 55, 1, 6, 60},
            {249, 30, 56, 1, 70, 70},
            {250, 31, 57, 1, 0, 120},
            {388, 42, 58, 0, 0, 0}
        };

        bang(mongDoi.length,
                ChickenCongThucVatPhamChien.layTatCa().size(),
                "so cau hinh vat pham chien dau");

        Set<Integer> idDaGap = new HashSet<>();
        for (int[] dong : mongDoi) {
            int id = dong[0];
            dung(idDaGap.add(id), "trung ID trong bang test: " + id);
            CauHinh cauHinh =
                    ChickenCongThucVatPhamChien.theoIdVatPham(id);
            khacNull(cauHinh, "thieu cau hinh ID=" + id);
            bang(id, cauHinh.getIdVatPham(), "sai ID vat pham");
            bang(dong[1], cauHinh.getMaSuDung(),
                    "sai itemUsed ID=" + id);
            bang(dong[2], (int) cauHinh.getLoaiDan(),
                    "sai bulletType ID=" + id);
            bang(dong[3] == 1, cauHinh.coHeSoCoDinh(),
                    "sai che do he so ID=" + id);
            bang(dong[4], cauHinh.getHeSoGio(),
                    "sai he so gio ID=" + id);
            bang(dong[5], cauHinh.getTrongLuc(),
                    "sai trong luc ID=" + id);
            dung(cauHinh.getLechGoc().length > 0,
                    "thieu duong can goc ID=" + id);
        }

        int[] idDangBanCoDuongNgam = {
            221, 226, 227, 228, 229, 231,
            235, 236, 237, 238, 239, 240, 241,
            243, 244, 245, 247, 249, 250, 258, 388
        };
        for (int id : idDangBanCoDuongNgam) {
            khacNull(ChickenCongThucVatPhamChien.theoIdVatPham(id),
                    "item shop chua co cong thuc ID=" + id);
        }

        CauHinh toNhanX3 =
                ChickenCongThucVatPhamChien.theoIdVatPham(249);
        dung(Arrays.equals(new int[]{-5, 0, 5},
                        toNhanX3.getLechGoc()),
                "To nhan x3 sai ba tia client");

        CauHinh xuyenDat =
                ChickenCongThucVatPhamChien.theoIdVatPham(239);
        bang(KieuQuyDao.XUYEN_DAT, xuyenDat.getKieuQuyDao(),
                "Dan xuyen dat roi ve parabol thuong");
        bang(KieuGoc.XUYEN_DAT, xuyenDat.getKieuGoc(),
                "Dan xuyen dat sai mien goc");

        CauHinh muaDan =
                ChickenCongThucVatPhamChien.theoIdVatPham(241);
        bang(KieuGoc.THANG_DUNG, muaDan.getKieuGoc(),
                "Mua dan khong khoa goc doc");

        CauHinh bomTuSat =
                ChickenCongThucVatPhamChien.theoIdVatPham(258);
        bang(KieuGoc.KHONG_CAN_GOC, bomTuSat.getKieuGoc(),
                "Bom tu sat khong duoc tao duong ngam");

        bang(2,
                ChickenCongThucVatPhamChien.layTheoMaSuDung(29).size(),
                "action 29 phai ho tro Bom doc va Khi doc");
        dung(ChickenCongThucVatPhamChien.layTheoMaSuDung(99).isEmpty(),
                "action la bi roi ve cong thuc mac dinh");
        dung(ChickenCongThucVatPhamChien.theoIdVatPham(9999) == null,
                "ID la bi roi ve cong thuc mac dinh");

        ChickenMauVatPham luuDan = new ChickenMauVatPham(
                (short) 227, (byte) 10, (byte) 7,
                "Luu dan", "", (byte) 1, 0,
                (short) 0, (short) 0, false);
        dung(ChickenCongThucVatPhamChien.khopMauVatPham(luuDan),
                "template Luu dan dung bi tu choi");
        luuDan.gioiTinh = 8;
        dung(!ChickenCongThucVatPhamChien.khopMauVatPham(luuDan),
                "template sai itemUsed van duoc chap nhan");

        kiemTraPacketLuuDan();
        kiemTraSnapshotVaTieuHaoLuuDan();
        kiemTraQuyDaoVaSatThuongLuuDan();
        kiemTraBomPhaDat();
        kiemTraDanTraiPha();
        kiemTraDanLazer();
        kiemTraDanVoiRong();
        kiemTraChuotGanBom();
        kiemTraTenLuaX4();
        kiemTraDanXuyenDat();
        kiemTraCuuThuongBanThan();
        kiemTraDichChuyenTucThoi();
        kiemTraCuuThuongDongDoi();
        kiemTraDiChuyenX2();
        kiemTraNgungGio();
        kiemTraMotTrangThaiGioChoTatCaBoss();

        System.out.println(
                "ITEM_AIM_MATRIX_OK mappings=" + mongDoi.length
                + " shopAimedItems=" + idDangBanCoDuongNgam.length
                + " duplicateAction29=2 grenadeRuntime=ok"
                + " terrainBombRuntime=ok mortarRuntime=ok"
                + " laserRuntime=ok tornadoRuntime=ok mouseBombRuntime=ok"
                + " fourMissileRuntime=ok undergroundRuntime=ok");
    }

    private static void kiemTraDanXuyenDat() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(239);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(239);
        khacNull(cauHinh, "Dan xuyen dat thieu cong thuc client");
        khacNull(hoSo, "Dan xuyen dat thieu ho so runtime");
        bang(KieuQuyDao.XUYEN_DAT, cauHinh.getKieuQuyDao(),
                "Dan xuyen dat sai kieu quy dao");
        bang(KieuGoc.XUYEN_DAT, cauHinh.getKieuGoc(),
                "Dan xuyen dat sai mien goc");
        bang(20, cauHinh.getMaSuDung(),
                "Dan xuyen dat sai CPlayer.itemUsed");
        bang(25, cauHinh.getLoaiDan() & 255,
                "Dan xuyen dat sai bullet type");
        bang(150, hoSo.getPhanTramTanCong(),
                "Dan xuyen dat sai damage cao");
        bang(300, hoSo.getNapDanSauDung(),
                "Dan xuyen dat sai nap dan");
        bang(1, hoSo.getSoLanToiDaMoiTran(),
                "Dan xuyen dat sai quota moi tran");
        bang(2,
                com.chicken.vatpham.ChickenGoiMuaVatPham
                        .soLuongNhanMoiGoi(239),
                "Dan xuyen dat mua mot khong nhan du hai");
        bang(25,
                ChickenLoaiDanPhaDiaHinhClient.layLoaiDanTaoLo(
                        (byte) 25, 0),
                "Dan xuyen dat khong dung mask type 25");

        khacNull(ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(99, 32_000, -32_000, -30, 30, 99),
                        cauHinh),
                "goc xuyen dat hop le bi tu choi");
        dung(ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(25, 0, 0, 270, 30, 1), cauHinh) == null,
                "client sua packet ban xuyen dat theo goc cam van duoc nhan");
        khacNull(ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(25, 0, 0, 171, 30, 1), cauHinh),
                "goc 171 do nut aim cam tao ra bi server tu choi");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[8]),
                        cauHinh) == null,
                "packet xuyen dat thieu byte van duoc nhan");

        ChickenQuanLyCongThucSung.KiemTraBanDo datDay =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() { return 1_000; }

                    @Override
                    public int getHeight() { return 600; }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return x >= 0 && x < 1_000 && y >= 330;
                    }
                };
        ChickenQuanLyCongThucSung.KetQuaQuyDao khongGio =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 100, (short) 300,
                        (short) 330, (byte) 30, cauHinh,
                        (byte) 0, (byte) 0, datDay);
        ChickenQuanLyCongThucSung.KetQuaQuyDao gioManh =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 100, (short) 300,
                        (short) 330, (byte) 30, cauHinh,
                        (byte) 100, (byte) -100, datDay);
        dung(Arrays.equals(khongGio.getHienThiX(), gioManh.getHienThiX())
                        && Arrays.equals(
                                khongGio.getHienThiY(),
                                gioManh.getHienThiY()),
                "Dan xuyen dat bi gio lam lech");

        int diemTrongDat = -1;
        for (int i = 1; i < khongGio.getHienThiX().length; i++) {
            if (datDay.coVaCham(
                    khongGio.getHienThiX()[i],
                    khongGio.getHienThiY()[i])) {
                diemTrongDat = i;
            }
        }
        dung(diemTrongDat > 0
                        && diemTrongDat < khongGio.getHienThiX().length - 1,
                "Dan xuyen dat bi terrain cat hoac khong thoat khoi dat");

        final ChickenChienBinh shooter = new ChickenChienBinh(
                (byte) 0, (short) 100, (short) 300,
                "UndergroundShooter", (short) 57, (byte) 0);
        shooter.tanCong = 1_000;
        final ChickenChienBinh mucTieu = new ChickenChienBinh(
                (byte) 1,
                khongGio.getHienThiX()[diemTrongDat],
                khongGio.getHienThiY()[diemTrongDat],
                "UndergroundTarget", (short) 57, (byte) 0);
        ChickenPhatBanServer.BoLocMucTieu boLoc =
                new ChickenPhatBanServer.BoLocMucTieu() {
                    @Override
                    public boolean chapNhan(
                            ChickenChienBinh nguoiBan,
                            ChickenChienBinh candidate
                    ) {
                        return candidate == mucTieu;
                    }

                    @Override
                    public boolean trungHitbox(
                            ChickenChienBinh candidate,
                            int danX,
                            int danY
                    ) {
                        return candidate == mucTieu
                                && Math.abs(danX - mucTieu.x) <= 2
                                && Math.abs(danY - mucTieu.y) <= 2;
                    }
                };
        ChickenKetQuaDan trung = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 330, (byte) 30, cauHinh,
                (byte) 100, (byte) -100, datDay,
                new ChickenChienBinh[]{shooter, mucTieu}, boLoc);
        khacNull(trung, "engine Dan xuyen dat khong tao ket qua");
        khacNull(trung.satThuongTheoMucTieu.get(mucTieu),
                "Dan xuyen dat cham muc tieu trong terrain ma khong no");
        dung(trung.satThuongTheoMucTieu.get(mucTieu) > 0,
                "Dan xuyen dat khong gay damage server");

        ChickenKetQuaDan truot = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 330, (byte) 30, cauHinh,
                (byte) 0, (byte) 0, datDay,
                new ChickenChienBinh[]{shooter},
                (nguoiBan, candidate) -> false);
        khacNull(truot, "Dan xuyen dat ban truot khong ket thuc duoc");
        dung(truot.satThuongTheoMucTieu.isEmpty(),
                "Dan xuyen dat tu no khi khong cham muc tieu");
    }

    private static void kiemTraTenLuaX4() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(238);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(238);
        khacNull(cauHinh, "Ten lua x4 thieu cong thuc client");
        khacNull(hoSo, "Ten lua x4 thieu ho so runtime");
        bang(KieuQuyDao.TEN_LUA_X4, cauHinh.getKieuQuyDao(),
                "Ten lua x4 sai kieu quy dao");
        bang(KieuGoc.TU_DO, cauHinh.getKieuGoc(),
                "Ten lua x4 sai mien goc");
        bang(19, cauHinh.getMaSuDung(),
                "Ten lua x4 sai CPlayer.itemUsed");
        bang(26, cauHinh.getLoaiDan() & 255,
                "Ten lua x4 sai bullet type me");
        bang(30, cauHinh.getHeSoGio(),
                "Ten lua x4 sai he so gio client goc");
        bang(60, cauHinh.getTrongLuc(),
                "Ten lua x4 sai trong luc client goc");
        bang(180, hoSo.getPhanTramTanCong(),
                "Ten lua x4 sai tong damage bon vien");
        bang(300, hoSo.getNapDanSauDung(),
                "Ten lua x4 sai nap dan");
        bang(1, hoSo.getSoLanToiDaMoiTran(),
                "Ten lua x4 sai quota moi tran");
        bang(27,
                ChickenLoaiDanPhaDiaHinhClient.layLoaiDanTaoLo(
                        (byte) 26, 1),
                "Ten lua con khong dung mask dia hinh type 27");
        bang(5,
                com.chicken.vatpham.ChickenGoiMuaVatPham
                        .soLuongNhanMoiGoi(238),
                "Ten lua x4 mua mot khong nhan du nam");

        ChickenYeuCauBanServer.KetQua yeuCau =
                ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(99, 32_000, -32_000, 725, 255, 99),
                        cauHinh);
        khacNull(yeuCau, "packet Ten lua x4 hop le bi tu choi");
        bang(26, yeuCau.getLoaiDan() & 255,
                "Ten lua x4 tin bulletType gia tu client");
        bang(1, yeuCau.getSoVienMoiLoat() & 255,
                "Ten lua x4 tin so dan gia tu client");
        bang(5, yeuCau.getGoc() & 0xFFFF,
                "Ten lua x4 khong chuan hoa goc");
        bang(30, yeuCau.getLuc() & 255,
                "Ten lua x4 khong kep luc");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[8]),
                        cauHinh) == null,
                "packet Ten lua x4 thieu byte van duoc nhan");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[10]),
                        cauHinh) == null,
                "packet Ten lua x4 du byte van duoc nhan");

        ChickenQuanLyCongThucSung.KiemTraBanDo nenPhang =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() { return 1_200; }

                    @Override
                    public int getHeight() { return 600; }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return x >= 0 && x < 1_200 && y >= 350;
                    }
                };
        ChickenQuanLyCongThucSung.KetQuaQuyDao marker =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 100, (short) 300,
                        (short) 0, (byte) 30, cauHinh,
                        (byte) -100, (byte) 100, nenPhang);
        int cuoiMarker = marker.getHienThiX().length - 1;
        ChickenChienBinh shooter = new ChickenChienBinh(
                (byte) 0, (short) 100, (short) 350,
                "MissileShooter", (short) 57, (byte) 0);
        shooter.tanCong = 1_000;
        ChickenChienBinh mucTieu = new ChickenChienBinh(
                (byte) 1,
                marker.getHienThiX()[cuoiMarker],
                (short) 350,
                "MissileTarget", (short) 57, (byte) 0);
        ChickenChienBinh ngoaiBanKinh = new ChickenChienBinh(
                (byte) 2,
                (short) (mucTieu.x + 200),
                (short) 350,
                "MissileFar", (short) 57, (byte) 0);
        ChickenKetQuaDan ketQua = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 30, cauHinh,
                (byte) -100, (byte) 100, nenPhang,
                new ChickenChienBinh[]{shooter, mucTieu, ngoaiBanKinh},
                (nguoiBan, candidate) -> candidate != nguoiBan);
        khacNull(ketQua, "engine Ten lua x4 khong tao ket qua");
        bang(26, ketQua.loaiDan & 255,
                "engine Ten lua x4 gui sai bulletType");
        bang(5, ketQua.cacDuongX.length,
                "client type 26 khong nhan du path 0..4");
        bang(5, ketQua.cacDuongY.length,
                "Ten lua x4 lech so mang path X/Y");
        for (int i = 0; i < 5; i++) {
            dung(ketQua.cacDuongX[i] != null
                            && ketQua.cacDuongY[i] != null
                            && ketQua.cacDuongX[i].length >= 2
                            && ketQua.cacDuongX[i].length
                                    == ketQua.cacDuongY[i].length,
                    "Ten lua x4 path " + i + " rong hoac lech X/Y");
        }
        int diemCuoiPathMe = ketQua.cacDuongX[0].length - 1;
        short diemBungX = ketQua.cacDuongX[0][diemCuoiPathMe];
        short diemBungY = ketQua.cacDuongY[0][diemCuoiPathMe];
        boolean daXuyenQuaDiaHinh = false;
        for (int i = 1; i <= 4; i++) {
            int diemCuoi = ketQua.cacDuongX[i].length - 1;
            bang(25, ketQua.cacDuongX[i].length,
                    "Ten lua con " + i + " bi dia hinh cat path");
            bang(diemBungX, ketQua.cacDuongX[i][0],
                    "Ten lua con " + i + " khong bung tai diem trung X");
            bang(diemBungY, ketQua.cacDuongY[i][0],
                    "Ten lua con " + i + " khong bung tai diem trung Y");
            bang(diemBungX, ketQua.cacDuongX[i][diemCuoi],
                    "Ten lua con " + i + " khong ghim lai muc tieu X");
            bang(diemBungY, ketQua.cacDuongY[i][diemCuoi],
                    "Ten lua con " + i + " khong ghim lai muc tieu Y");
            boolean daBayRaKhoiDiemTrung = false;
            for (int diem = 1; diem < diemCuoi; diem++) {
                if (ketQua.cacDuongX[i][diem] != diemBungX
                        || ketQua.cacDuongY[i][diem] != diemBungY) {
                    daBayRaKhoiDiemTrung = true;
                }
                if (nenPhang.coVaCham(
                        ketQua.cacDuongX[i][diem],
                        ketQua.cacDuongY[i][diem])) {
                    daXuyenQuaDiaHinh = true;
                }
            }
            dung(daBayRaKhoiDiemTrung,
                    "Ten lua con " + i + " khong bay vong");
        }
        dung(daXuyenQuaDiaHinh,
                "Ten lua con chua duoc kiem chung xuyen dia hinh");
        Integer satThuong = ketQua.satThuongTheoMucTieu.get(mucTieu);
        khacNull(satThuong,
                "bon ten lua khong gay damage vao muc tieu da ngam");
        dung(satThuong > 0
                        && satThuong <= com.chicken.chien.ChickenSieuCao
                                .tangSatThuong(1_800),
                "Ten lua x4 cong damage vuot tong 180% + sieu cao server");
        dung(!ketQua.satThuongTheoMucTieu.containsKey(ngoaiBanKinh),
                "Ten lua x4 gay damage ngoai ban kinh no");
        dung(!ketQua.satThuongTheoMucTieu.containsKey(shooter),
                "Ten lua x4 bo qua bo loc tu ban");

        ChickenKetQuaDan lanBanKhac = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 30, cauHinh,
                (byte) -100, (byte) 100, nenPhang,
                new ChickenChienBinh[]{shooter, mucTieu, ngoaiBanKinh},
                (nguoiBan, candidate) -> candidate != nguoiBan);
        bang(5, lanBanKhac.cacDuongX.length,
                "Ten lua x4 lan hai khong bung du bon vien");
        boolean quyDaoDaRandom = false;
        for (int i = 1; i <= 4 && !quyDaoDaRandom; i++) {
            for (int diem = 1;
                    diem < ketQua.cacDuongX[i].length - 1; diem++) {
                if (ketQua.cacDuongX[i][diem]
                                != lanBanKhac.cacDuongX[i][diem]
                        || ketQua.cacDuongY[i][diem]
                                != lanBanKhac.cacDuongY[i][diem]) {
                    quyDaoDaRandom = true;
                    break;
                }
            }
        }
        dung(quyDaoDaRandom,
                "Ten lua x4 van lap cung mot quy dao moi lan ban");

        ChickenKetQuaDan banTruot = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 180, (byte) 30, cauHinh,
                (byte) 0, (byte) 0, nenPhang,
                new ChickenChienBinh[]{shooter, mucTieu},
                (nguoiBan, candidate) -> candidate != nguoiBan);
        khacNull(banTruot, "Ten lua x4 ban truot khong tao ket qua");
        bang(1, banTruot.cacDuongX.length,
                "Ten lua x4 ban truot van kich hoat bon vien con");
        dung(banTruot.satThuongTheoMucTieu.isEmpty(),
                "Ten lua x4 ban truot van gay damage");
    }

    private static void kiemTraChuotGanBom() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(237);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(237);
        khacNull(cauHinh, "Chuot gan bom thieu cong thuc client");
        khacNull(hoSo, "Chuot gan bom thieu ho so runtime");
        bang(KieuQuyDao.CHUOT_GAN_BOM, cauHinh.getKieuQuyDao(),
                "Chuot gan bom sai kieu quy dao");
        bang(KieuGoc.THEO_SUNG_DANG_CAM, cauHinh.getKieuGoc(),
                "Chuot gan bom sai kieu can goc");
        bang(18, cauHinh.getMaSuDung(),
                "Chuot gan bom sai CPlayer.itemUsed");
        bang(22, cauHinh.getLoaiDan() & 255,
                "Chuot gan bom sai bullet type");
        bang(150, hoSo.getPhanTramTanCong(),
                "Chuot gan bom sai he so damage cao");
        bang(300, hoSo.getNapDanSauDung(),
                "Chuot gan bom sai nap dan");
        bang(1, hoSo.getSoLanToiDaMoiTran(),
                "Chuot gan bom sai quota moi tran");
        bang(20, hoSo.getHoSoNo().getBanKinhDayDu(),
                "Chuot gan bom sai ban kinh damage day du");
        bang(90, hoSo.getHoSoNo().getBanKinhNo(),
                "Chuot gan bom sai ban kinh no");
        bang(22,
                ChickenLoaiDanPhaDiaHinhClient.layLoaiDanTaoLo(
                        (byte) 22, 0),
                "Chuot gan bom khong dung mask dia hinh type 22");
        bang(5,
                com.chicken.vatpham.ChickenGoiMuaVatPham
                        .soLuongNhanMoiGoi(237),
                "Chuot gan bom mua mot khong nhan du nam");

        ChickenYeuCauBanServer.KetQua yeuCau =
                ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(99, 32_000, -32_000, 725, 255, 99),
                        cauHinh);
        khacNull(yeuCau, "packet Chuot gan bom hop le bi tu choi");
        bang(22, yeuCau.getLoaiDan() & 255,
                "Chuot gan bom tin bulletType gia tu client");
        bang(1, yeuCau.getSoVienMoiLoat() & 255,
                "Chuot gan bom tin so dan gia tu client");
        bang(5, yeuCau.getGoc() & 0xFFFF,
                "Chuot gan bom khong chuan hoa goc");
        bang(30, yeuCau.getLuc() & 255,
                "Chuot gan bom khong kep luc");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[8]),
                        cauHinh) == null,
                "packet Chuot gan bom thieu byte van duoc nhan");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[10]),
                        cauHinh) == null,
                "packet Chuot gan bom du byte van duoc nhan");

        ChickenQuanLyCongThucSung.KiemTraBanDo nenPhang =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() { return 1_000; }

                    @Override
                    public int getHeight() { return 600; }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return x >= 0 && x < 1_000 && y >= 350;
                    }
                };
        ChickenQuanLyCongThucSung.KetQuaQuyDao lucMuoi =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 100, (short) 300,
                        (short) 0, (byte) 10, cauHinh,
                        (byte) 0, (byte) 0, nenPhang);
        ChickenQuanLyCongThucSung.KetQuaQuyDao gioManh =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 100, (short) 300,
                        (short) 0, (byte) 10, cauHinh,
                        (byte) 100, (byte) -100, nenPhang);
        ChickenQuanLyCongThucSung.KetQuaQuyDao lucBaMuoi =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 100, (short) 300,
                        (short) 0, (byte) 30, cauHinh,
                        (byte) 0, (byte) 0, nenPhang);
        dung(lucMuoi.getHienThiX().length > 30,
                "Chuot khong co chang roi xuong nen truoc khi chay");
        dung(lucMuoi.getHienThiY()[0] > 300,
                "Chuot lai bay len nhu vien dan");
        dung(Arrays.equals(lucMuoi.getHienThiX(), gioManh.getHienThiX())
                        && Arrays.equals(
                                lucMuoi.getHienThiY(),
                                gioManh.getHienThiY()),
                "Chuot gan bom bi gio lam lech");
        int cuoiLucMuoi = lucMuoi.getHienThiX().length - 1;
        int cuoiLucBaMuoi = lucBaMuoi.getHienThiX().length - 1;
        dung(lucMuoi.getHienThiX()[cuoiLucMuoi] > 100,
                "Chuot khong chay sang phai theo huong ngam");
        dung(lucBaMuoi.getHienThiX()[cuoiLucBaMuoi]
                        > lucMuoi.getHienThiX()[cuoiLucMuoi],
                "luc lon khong lam Chuot chay xa hon");
        dung(lucBaMuoi.getHienThiX()[cuoiLucBaMuoi] <= 100 + 30 * 6,
                "luc bi hieu sai thanh van toc dan bay");

        ChickenQuanLyCongThucSung.KetQuaQuyDao chayTrai =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 500, (short) 300,
                        (short) 180, (byte) 10, cauHinh,
                        (byte) 0, (byte) 0, nenPhang);
        int cuoiTrai = chayTrai.getHienThiX().length - 1;
        dung(chayTrai.getHienThiX()[cuoiTrai] < 500,
                "goc trai khong doi huong chay cua Chuot");

        ChickenQuanLyCongThucSung.KiemTraBanDo coTuong =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() { return 1_000; }

                    @Override
                    public int getHeight() { return 600; }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return y >= 350 || (x >= 145 && y >= 250);
                    }
                };
        ChickenQuanLyCongThucSung.KetQuaQuyDao dungTruocTuong =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 100, (short) 300,
                        (short) 0, (byte) 30, cauHinh,
                        (byte) 0, (byte) 0, coTuong);
        int cuoiTuong = dungTruocTuong.getHienThiX().length - 1;
        dung(dungTruocTuong.getHienThiX()[cuoiTuong] < 145,
                "Chuot chay xuyen tuong");

        ChickenQuanLyCongThucSung.KiemTraBanDo coVuc =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() { return 1_000; }

                    @Override
                    public int getHeight() { return 600; }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return x < 145 && y >= 350;
                    }
                };
        ChickenQuanLyCongThucSung.KetQuaQuyDao roiVuc =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 100, (short) 300,
                        (short) 0, (byte) 30, cauHinh,
                        (byte) 0, (byte) 0, coVuc);
        int cuoiVuc = roiVuc.getHienThiY().length - 1;
        dung(roiVuc.getHienThiY()[cuoiVuc] > 350,
                "Chuot ra khoi nen lai lo lung hoac bi keo len");

        ChickenChienBinh shooter = new ChickenChienBinh(
                (byte) 0, (short) 100, (short) 350,
                "MouseShooter", (short) 57, (byte) 0);
        shooter.tanCong = 1_000;
        int xHetTam = lucMuoi.getHienThiX()[cuoiLucMuoi];
        int yHetTam = lucMuoi.getHienThiY()[cuoiLucMuoi];
        ChickenChienBinh trungTrucTiep = new ChickenChienBinh(
                (byte) 1, (short) (xHetTam - 20), (short) (yHetTam + 18),
                "MouseDirect", (short) 57, (byte) 0);
        ChickenChienBinh trungLan = new ChickenChienBinh(
                (byte) 2, (short) (xHetTam + 65), (short) (yHetTam + 18),
                "MouseSplash", (short) 57, (byte) 0);
        ChickenChienBinh ngoaiBanKinh = new ChickenChienBinh(
                (byte) 3, (short) (xHetTam + 200), (short) (yHetTam + 18),
                "MouseFar", (short) 57, (byte) 0);
        ChickenKetQuaDan trungNguoi = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 10, cauHinh,
                (byte) 100, (byte) -100, nenPhang,
                new ChickenChienBinh[]{
                    shooter, trungTrucTiep, trungLan, ngoaiBanKinh
                },
                (nguoiBan, mucTieu) -> mucTieu != nguoiBan);
        khacNull(trungNguoi, "engine Chuot gan bom khong tao ket qua");
        bang(1, trungNguoi.cacDuongX.length,
                "Chuot gan bom khong phai mot vien");
        khacNull(trungNguoi.satThuongTheoMucTieu.get(trungTrucTiep),
                "Chuot gan bom cham nguoi ma khong no");
        khacNull(trungNguoi.satThuongTheoMucTieu.get(trungLan),
                "Chuot gan bom khong gay damage no lan");
        dung(trungNguoi.satThuongTheoMucTieu.get(trungTrucTiep)
                        > trungNguoi.satThuongTheoMucTieu.get(trungLan),
                "Chuot gan bom khong giam damage theo khoang cach");
        dung(!trungNguoi.satThuongTheoMucTieu.containsKey(ngoaiBanKinh),
                "Chuot gan bom gay damage ngoai ban kinh");
        dung(!trungNguoi.satThuongTheoMucTieu.containsKey(shooter),
                "bo loc tu ban bi Chuot gan bom bo qua");

        ChickenChienBinh mucTieuHetTam = new ChickenChienBinh(
                (byte) 4, (short) (xHetTam + 20),
                (short) (yHetTam + 18),
                "MouseTimeout", (short) 57, (byte) 0);
        ChickenKetQuaDan hetTam = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 10, cauHinh,
                (byte) 0, (byte) 0, nenPhang,
                new ChickenChienBinh[]{mucTieuHetTam},
                (nguoiBan, mucTieu) -> true);
        khacNull(hetTam.satThuongTheoMucTieu.get(mucTieuHetTam),
                "Chuot het path khong no tai diem cuoi server");
    }

    private static void kiemTraDanVoiRong() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(236);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(236);
        khacNull(cauHinh, "Dan voi rong thieu cong thuc client");
        khacNull(hoSo, "Dan voi rong thieu ho so runtime");
        bang(KieuQuyDao.VOI_RONG, cauHinh.getKieuQuyDao(),
                "Dan voi rong sai kieu quy dao");
        bang(17, cauHinh.getMaSuDung(),
                "Dan voi rong sai CPlayer.itemUsed");
        bang(13, cauHinh.getLoaiDan() & 255,
                "Dan voi rong sai bullet type");
        bang(0, hoSo.getPhanTramTanCong(),
                "Dan voi rong khong duoc gay damage");
        bang(300, hoSo.getNapDanSauDung(),
                "Dan voi rong sai nap dan");
        bang(1, hoSo.getSoLanToiDaMoiTran(),
                "Dan voi rong sai quota moi tran");
        bang(ChickenCauHinhSatThuongVatPham.HieuUngDacBiet.TAO_VOI_RONG,
                hoSo.getHieuUngDacBiet(),
                "Dan voi rong thieu hieu ung cot loc");
        bang(ChickenLoaiDanPhaDiaHinhClient.KHONG_PHA_DIA_HINH,
                ChickenLoaiDanPhaDiaHinhClient.layLoaiDanTaoLo(
                        (byte) 13, 0),
                "Dan voi rong lai pha dia hinh");

        ChickenYeuCauBanServer.KetQua yeuCau =
                ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(99, 32_000, -32_000, 725, 255, 99),
                        cauHinh);
        khacNull(yeuCau, "packet Dan voi rong hop le bi tu choi");
        bang(13, yeuCau.getLoaiDan() & 255,
                "Dan voi rong tin bulletType gia tu client");
        bang(1, yeuCau.getSoVienMoiLoat() & 255,
                "Dan voi rong tin so dan gia tu client");
        bang(5, yeuCau.getGoc() & 0xFFFF,
                "Dan voi rong khong chuan hoa goc");
        bang(30, yeuCau.getLuc() & 255,
                "Dan voi rong khong kep luc");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[8]),
                        cauHinh) == null,
                "packet Dan voi rong thieu byte van duoc nhan");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[10]),
                        cauHinh) == null,
                "packet Dan voi rong du byte van duoc nhan");

        ChickenQuanLyCongThucSung.KiemTraBanDo mapRong =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() { return 1_000; }

                    @Override
                    public int getHeight() { return 600; }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return false;
                    }
                };
        ChickenChienBinh shooter = new ChickenChienBinh(
                (byte) 0, (short) 100, (short) 300,
                "TornadoShooter", (short) 57, (byte) 0);
        ChickenKetQuaDan phatDatLoc = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 30, cauHinh,
                (byte) 0, (byte) 0, mapRong,
                new ChickenChienBinh[]{shooter},
                (nguoiBan, mucTieu) -> mucTieu != nguoiBan);
        khacNull(phatDatLoc, "engine Dan voi rong khong tao ket qua");
        bang(1, phatDatLoc.cacDuongX.length,
                "Dan voi rong khong phai mot vien");
        dung(phatDatLoc.satThuongTheoMucTieu.isEmpty(),
                "Dan voi rong van tao damage");

        ChickenQuanLyBanDo mapCoLoc = new ChickenQuanLyBanDo(-1);
        dung(mapCoLoc.themVoiRong(160, 400),
                "server khong tao duoc cot loc hop le");
        bang(1, mapCoLoc.laySoVoiRongDangHoatDong(),
                "cot loc khong duoc luu trong map");
        ChickenQuanLyCongThucSung.KetQuaQuyDao khongLoc =
                ChickenQuanLyCongThucSung.taoQuyDaoTheoIdSung(
                        (short) 100, (short) 300, (short) 0, (byte) 30,
                        110, (byte) 0, (byte) 0, mapRong);
        ChickenQuanLyCongThucSung.KetQuaQuyDao coLoc =
                ChickenQuanLyCongThucSung.taoQuyDaoTheoIdSung(
                        (short) 100, (short) 300, (short) 0, (byte) 30,
                        110, (byte) 0, (byte) 0, mapCoLoc);
        int chung = Math.min(
                khongLoc.getHienThiY().length,
                coLoc.getHienThiY().length);
        boolean daLech = false;
        for (int i = 0; i < chung; i++) {
            if (khongLoc.getHienThiY()[i] != coLoc.getHienThiY()[i]) {
                daLech = true;
                break;
            }
        }
        dung(daLech, "cot loc khong lam lech quy dao server");

        /*
         * AI khong duoc dung cot loc de tinh nguoc va ngam bu. Goc/luc phai
         * giong luc chua co voi rong, nhung phat dan authoritative sau do
         * van phai bi cot loc lam lech.
         */
        com.chicken.chien.ChickenQuanLyDanSung.DuLieuSung sungAT4 =
                com.chicken.chien.ChickenQuanLyDanSung.theoIdSung(110);
        khacNull(sungAT4, "thieu AT4 de test AI voi rong");
        ChickenChienBinh botBan = new ChickenChienBinh(
                (byte) 1, (short) 100, (short) 300,
                "BotBan", sungAT4.getPartSung(), (byte) 0);
        botBan.tanCong = 500;
        ChickenChienBinh mucTieuBot = new ChickenChienBinh(
                (byte) 2, (short) 600, (short) 300,
                "MucTieu", (short) 57, (byte) 0);
        mucTieuBot.mauToiDa = 10_000;
        mucTieuBot.hp = 10_000;
        ChickenQuanLyBanDo mapAiKhongLoc = taoMapTrongTestVoiRong();
        ChickenKetQuaDan aiKhongLoc =
                com.chicken.phong.boss.trandau.baovay.BossBanSung
                        .taoPhatBanTheoCongThucSung(
                                botBan, mucTieuBot,
                                new ChickenChienBinh[]{mucTieuBot},
                                sungAT4, mapAiKhongLoc,
                                (byte) 0, (byte) 0, true);
        khacNull(aiKhongLoc, "AI khong tao duoc phat ban doi chieu");
        int chiSoCotLoc = Math.max(
                1, aiKhongLoc.cacDuongX[0].length / 2);
        int xCotLoc = aiKhongLoc.cacDuongX[0][Math.min(
                chiSoCotLoc, aiKhongLoc.cacDuongX[0].length - 1)];
        ChickenQuanLyBanDo mapAiCoLoc = taoMapTrongTestVoiRong();
        dung(mapAiCoLoc.themVoiRong(xCotLoc, 599),
                "khong dat duoc cot loc tren duong dan AI");
        ChickenKetQuaDan aiCoLoc =
                com.chicken.phong.boss.trandau.baovay.BossBanSung
                        .taoPhatBanTheoCongThucSung(
                                botBan, mucTieuBot,
                                new ChickenChienBinh[]{mucTieuBot},
                                sungAT4, mapAiCoLoc,
                                (byte) 0, (byte) 0, true);
        khacNull(aiCoLoc, "AI khong tao duoc phat ban qua voi rong");
        bang(aiKhongLoc.goc, aiCoLoc.goc,
                "AI da ngam bu goc theo voi rong");
        bang(aiKhongLoc.luc, aiCoLoc.luc,
                "AI da ngam bu luc theo voi rong");
        bang(aiKhongLoc.lucPhu, aiCoLoc.lucPhu,
                "AI da ngam bu luc phu theo voi rong");
        dung(!Arrays.equals(
                        aiKhongLoc.cacDuongY[0], aiCoLoc.cacDuongY[0]),
                "phat dan that cua AI khong bi voi rong lam lech");

        // Luot dat loc khong nam trong ba luot hieu luc.
        mapCoLoc.ketThucLuotVoiRong(true);
        bang(1, mapCoLoc.laySoVoiRongDangHoatDong(),
                "luot dat da lam mat cot loc");

        // Luot bot/boss khong duoc lam giam bo dem.
        mapCoLoc.ketThucLuotVoiRong(false);
        bang(1, mapCoLoc.laySoVoiRongDangHoatDong(),
                "luot bot/boss da lam giam thoi gian cua cot loc");

        // Cot loc hoat dong trong tron ba luot nguoi choi tiep theo,
        // bat ke giua cac luot do co bao nhieu luot bot/boss.
        for (int i = 0; i < 3; i++) {
            bang(1, mapCoLoc.laySoVoiRongDangHoatDong(),
                    "cot loc khong hoat dong o luot nguoi choi thu " + (i + 1));
            mapCoLoc.ketThucLuotVoiRong(false);
            mapCoLoc.ketThucLuotVoiRong(true);
            if (i < 2) {
                bang(1, mapCoLoc.laySoVoiRongDangHoatDong(),
                        "cot loc het truoc ba luot nguoi choi");
            }
        }
        bang(0, mapCoLoc.laySoVoiRongDangHoatDong(),
                "cot loc khong het sau ba luot nguoi choi tiep theo");
    }

    private static ChickenQuanLyBanDo taoMapTrongTestVoiRong() {
        return new ChickenQuanLyBanDo(-1) {
            @Override
            public int getWidth() {
                return 1_000;
            }

            @Override
            public int getHeight() {
                return 600;
            }

            @Override
            public synchronized boolean coVaCham(short x, short y) {
                return false;
            }
        };
    }

    private static void kiemTraDanLazer() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(235);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(235);
        khacNull(cauHinh, "Dan Lazer thieu cong thuc client");
        khacNull(hoSo, "Dan Lazer thieu ho so runtime");
        bang(16, cauHinh.getMaSuDung(),
                "Dan Lazer sai CPlayer.itemUsed");
        bang(14, cauHinh.getLoaiDan() & 255,
                "Dan Lazer sai bullet type danh dau");
        bang(110, hoSo.getPhanTramTanCong(),
                "Dan Lazer sai he so damage");
        bang(300, hoSo.getNapDanSauDung(),
                "Dan Lazer sai nap dan");
        bang(1, hoSo.getSoLanToiDaMoiTran(),
                "Dan Lazer sai quota moi tran");
        bang(ChickenCauHinhSatThuongVatPham.HieuUngDacBiet.PHA_DIA_HINH,
                hoSo.getHieuUngDacBiet(),
                "Dan Lazer thieu hieu ung pha dia hinh");

        ChickenYeuCauBanServer.KetQua yeuCau =
                ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(99, 32_000, -32_000, 725, 255, 99),
                        cauHinh);
        khacNull(yeuCau, "packet Dan Lazer hop le bi tu choi");
        bang(14, yeuCau.getLoaiDan() & 255,
                "Dan Lazer tin bulletType gia tu client");
        bang(1, yeuCau.getSoVienMoiLoat() & 255,
                "Dan Lazer tin so dan gia tu client");
        bang(5, yeuCau.getGoc() & 0xFFFF,
                "Dan Lazer khong chuan hoa goc");
        bang(30, yeuCau.getLuc() & 255,
                "Dan Lazer khong kep luc");

        ChickenQuanLyCongThucSung.KiemTraBanDo nenPhang =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() {
                        return 1_000;
                    }

                    @Override
                    public int getHeight() {
                        return 600;
                    }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return x >= 0 && x < 1_000 && y >= 400;
                    }
                };
        ChickenChienBinh shooter = new ChickenChienBinh(
                (byte) 0, (short) 100, (short) 300,
                "LaserShooter", (short) 57, (byte) 0);
        shooter.tanCong = 1_000;
        ChickenKetQuaDan moc = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 30, cauHinh,
                (byte) 0, (byte) 0, nenPhang,
                new ChickenChienBinh[]{shooter},
                (nguoiBan, mucTieu) -> mucTieu != nguoiBan);
        khacNull(moc, "engine Dan Lazer khong tao ket qua");
        bang(2, moc.cacDuongX.length,
                "Dan Lazer thieu path type 15 tren truc X");
        bang(2, moc.cacDuongY.length,
                "Dan Lazer thieu path type 15 tren truc Y");
        dung(moc.cacDuongX[0].length > 0
                        && moc.cacDuongY[0].length > 0,
                "Dan Lazer co marker rong");
        bang(1, moc.cacDuongX[1].length,
                "path neo tia Lazer phai co dung mot diem X");
        bang(1, moc.cacDuongY[1].length,
                "path neo tia Lazer phai co dung mot diem Y");
        int cuoi = Math.min(
                moc.cacDuongX[0].length, moc.cacDuongY[0].length) - 1;
        bang(moc.cacDuongX[0][cuoi], moc.cacDuongX[1][0],
                "tia Lazer lech X so voi diem marker");
        bang(moc.cacDuongY[0][cuoi], moc.cacDuongY[1][0],
                "tia Lazer lech Y so voi diem marker");
        bang(ChickenLoaiDanPhaDiaHinhClient.KHONG_PHA_DIA_HINH,
                ChickenLoaiDanPhaDiaHinhClient.layLoaiDanTaoLo(
                        (byte) 14, 0),
                "marker Dan Lazer lai pha dia hinh");
        bang(15,
                ChickenLoaiDanPhaDiaHinhClient.layLoaiDanTaoLo(
                        (byte) 14, 1),
                "tia Dan Lazer khong dung mask type 15");

        ChickenChienBinh mucTieu = new ChickenChienBinh(
                (byte) 1, moc.cacDuongX[1][0],
                (short) (moc.cacDuongY[1][0] + 20),
                "LaserTarget", (short) 57, (byte) 0);
        ChickenKetQuaDan coDamage = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 30, cauHinh,
                (byte) 0, (byte) 0, nenPhang,
                new ChickenChienBinh[]{shooter, mucTieu},
                (nguoiBan, dich) -> dich != nguoiBan);
        Integer damage = coDamage.satThuongTheoMucTieu.get(mucTieu);
        khacNull(damage, "Dan Lazer no sat muc tieu nhung khong co damage");
        dung(damage > 0 && damage <= 1_320,
                "Dan Lazer cong don damage hai giai doan");

        long toiThieu = (long) Math.max(1, moc.cacDuongX[0].length)
                * ChickenThoiGianHoatAnhDan.MOI_DIEM_MS
                + ChickenThoiGianHoatAnhDan.LAZER_TRE_KICH_HOAT_MS
                + ChickenThoiGianHoatAnhDan.LAZER_HIEU_UNG_MS
                + ChickenThoiGianHoatAnhDan.DEM_KET_THUC_MS;
        long mongDoi = Math.max(
                ChickenThoiGianHoatAnhDan.TOI_THIEU_MS,
                Math.min(ChickenThoiGianHoatAnhDan.TOI_DA_MS, toiThieu));
        bang(mongDoi, ChickenThoiGianHoatAnhDan.tinh(moc),
                "watchdog khong cho client chay het hai giai doan Lazer");
    }

    private static void kiemTraBomPhaDat() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(226);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(226);
        khacNull(cauHinh, "Bom pha dat thieu cong thuc");
        khacNull(hoSo, "Bom pha dat thieu ho so runtime");
        bang(6, cauHinh.getLoaiDan() & 255,
                "Bom pha dat sai bulletType client");
        dung(Arrays.equals(new int[]{0}, cauHinh.getLechGoc()),
                "Bom pha dat van tach ba goc thay vi mot vien khoan");
        bang(80, hoSo.getPhanTramTanCong(),
                "Bom pha dat sai he so damage");
        bang(300, hoSo.getNapDanSauDung(),
                "Bom pha dat sai nap dan");
        bang(1, hoSo.getSoLanToiDaMoiTran(),
                "Bom pha dat sai quota moi tran");
        bang(ChickenCauHinhSatThuongVatPham.HieuUngDacBiet.PHA_DIA_HINH,
                hoSo.getHieuUngDacBiet(),
                "Bom pha dat thieu hieu ung pha dia hinh");

        ChickenYeuCauBanServer.KetQua yeuCau =
                ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(99, 32_000, -32_000, 725, 255, 99),
                        cauHinh);
        khacNull(yeuCau, "packet Bom pha dat hop le bi tu choi");
        bang(6, yeuCau.getLoaiDan() & 255,
                "Bom pha dat tin bulletType gia tu client");
        bang(1, yeuCau.getSoVienMoiLoat() & 255,
                "Bom pha dat tin so dan gia tu client");
        bang(5, yeuCau.getGoc() & 0xFFFF,
                "Bom pha dat khong chuan hoa goc");
        bang(30, yeuCau.getLuc() & 255,
                "Bom pha dat khong kep luc");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[8]),
                        cauHinh) == null,
                "packet Bom pha dat thieu byte van duoc nhan");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[10]),
                        cauHinh) == null,
                "packet Bom pha dat du byte van duoc nhan");

        ChickenQuanLyCongThucSung.KiemTraBanDo tuong =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() {
                        return 1_000;
                    }

                    @Override
                    public int getHeight() {
                        return 600;
                    }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return x >= 400;
                    }
                };
        ChickenChienBinh shooter = new ChickenChienBinh(
                (byte) 0, (short) 100, (short) 300,
                "TerrainBombShooter", (short) 57, (byte) 0);
        shooter.tanCong = 1_000;
        ChickenKetQuaDan moc = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 30, cauHinh,
                (byte) 0, (byte) 0, tuong,
                new ChickenChienBinh[]{shooter},
                (nguoiBan, mucTieu) -> mucTieu != nguoiBan);
        khacNull(moc, "engine Bom pha dat khong tao ket qua");
        bang(3, moc.cacDuongX.length,
                "client type 6 khong nhan du ba duong X");
        bang(3, moc.cacDuongY.length,
                "client type 6 khong nhan du ba duong Y");
        for (int i = 0; i < 3; i++) {
            dung(moc.cacDuongX[i] != null && moc.cacDuongX[i].length > 0,
                    "Bom pha dat co duong X rong: " + i);
            dung(moc.cacDuongY[i] != null && moc.cacDuongY[i].length > 0,
                    "Bom pha dat co duong Y rong: " + i);
        }
        dung(moc.cacDuongX[0].length < moc.cacDuongX[1].length
                        && moc.cacDuongX[1].length
                                < moc.cacDuongX[2].length,
                "Bom pha dat khong no lan luot theo do dai path");
        for (int i = 0; i < moc.cacDuongX[0].length; i++) {
            bang(moc.cacDuongX[0][i], moc.cacDuongX[1][i],
                    "path 2 khong chong khit vien khoan ban dau X");
            bang(moc.cacDuongY[0][i], moc.cacDuongY[1][i],
                    "path 2 khong chong khit vien khoan ban dau Y");
            bang(moc.cacDuongX[0][i], moc.cacDuongX[2][i],
                    "path 3 khong chong khit vien khoan ban dau X");
            bang(moc.cacDuongY[0][i], moc.cacDuongY[2][i],
                    "path 3 khong chong khit vien khoan ban dau Y");
        }
        int cuoiMot = moc.cacDuongX[0].length - 1;
        int cuoiHai = moc.cacDuongX[1].length - 1;
        int cuoiBa = moc.cacDuongX[2].length - 1;
        long dx1 = moc.cacDuongX[1][cuoiHai]
                - moc.cacDuongX[0][cuoiMot];
        long dy1 = moc.cacDuongY[1][cuoiHai]
                - moc.cacDuongY[0][cuoiMot];
        long dx2 = moc.cacDuongX[2][cuoiBa]
                - moc.cacDuongX[1][cuoiHai];
        long dy2 = moc.cacDuongY[2][cuoiBa]
                - moc.cacDuongY[1][cuoiHai];
        dung(Math.abs(dx1 * dy2 - dy1 * dx2) <= 2,
                "hai chang khoan sau khong nam tren mot duong thang");

        int cuoi = Math.min(moc.cacDuongX[1].length,
                moc.cacDuongY[1].length) - 1;
        ChickenChienBinh mucTieu = new ChickenChienBinh(
                (byte) 1, moc.cacDuongX[1][cuoi], moc.cacDuongY[1][cuoi],
                "TerrainBombTarget", (short) 57, (byte) 0);
        ChickenKetQuaDan coDamage = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 30, cauHinh,
                (byte) 0, (byte) 0, tuong,
                new ChickenChienBinh[]{shooter, mucTieu},
                (nguoiBan, dich) -> dich != nguoiBan);
        Integer damage = coDamage.satThuongTheoMucTieu.get(mucTieu);
        khacNull(damage, "Bom pha dat no sat muc tieu nhung khong co damage");
        dung(damage > 0 && damage <= 800,
                "Bom pha dat cong don damage cua ca ba qua");

        int diemNguoi = Math.max(1, moc.cacDuongX[0].length / 2);
        ChickenChienBinh trungNguoi = new ChickenChienBinh(
                (byte) 2,
                moc.cacDuongX[0][diemNguoi],
                (short) (moc.cacDuongY[0][diemNguoi] + 20),
                "TerrainBombDirectTarget", (short) 57, (byte) 0);
        ChickenKetQuaDan vaoNguoi = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 30, cauHinh,
                (byte) 0, (byte) 0, tuong,
                new ChickenChienBinh[]{shooter, trungNguoi},
                (nguoiBan, dich) -> dich != nguoiBan);
        int cuoiPathNguoi = vaoNguoi.cacDuongY[0].length - 1;
        dung(vaoNguoi.cacDuongY[1].length
                        == vaoNguoi.cacDuongY[0].length + 1
                        && vaoNguoi.cacDuongY[2].length
                                == vaoNguoi.cacDuongY[0].length + 1,
                "Bom pha dat trung nguoi van noi dai hai vu no sau");
        dung(vaoNguoi.cacDuongY[1][cuoiPathNguoi + 1] > tuong.getHeight()
                        && vaoNguoi.cacDuongY[2][cuoiPathNguoi + 1]
                                > tuong.getHeight(),
                "hai path huy sau khi trung nguoi khong thoat map");
        khacNull(vaoNguoi.satThuongTheoMucTieu.get(trungNguoi),
                "Bom pha dat cham nguoi khong gay damage mot lan");

        ChickenQuanLyCongThucSung.KiemTraBanDo tuongBatHoai =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() {
                        return 1_000;
                    }

                    @Override
                    public int getHeight() {
                        return 600;
                    }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return x >= 400;
                    }

                    @Override
                    public boolean coThePhaDiaHinh(short x, short y) {
                        return false;
                    }
                };
        ChickenKetQuaDan vaoTuongBatHoai =
                ChickenPhatBanVatPhamServer.tao(
                        shooter, (short) 100, (short) 300,
                        (short) 0, (byte) 30, cauHinh,
                        (byte) 0, (byte) 0, tuongBatHoai,
                        new ChickenChienBinh[]{shooter},
                        (nguoiBan, dich) -> dich != nguoiBan);
        dung(vaoTuongBatHoai.cacDuongY[1].length
                        == vaoTuongBatHoai.cacDuongY[0].length + 1
                        && vaoTuongBatHoai.cacDuongY[1][
                                vaoTuongBatHoai.cacDuongY[1].length - 1]
                                > tuongBatHoai.getHeight(),
                "dia hinh bat hoai van kich hoat chuoi ba vu no");
    }

    private static void kiemTraDanTraiPha() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(231);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(231);
        khacNull(cauHinh, "Dan trai pha thieu cong thuc");
        khacNull(hoSo, "Dan trai pha thieu ho so runtime");
        bang(11, cauHinh.getMaSuDung(),
                "Dan trai pha sai CPlayer.itemUsed");
        bang(16, cauHinh.getLoaiDan() & 255,
                "Dan trai pha sai bulletType client");
        bang(90, hoSo.getPhanTramTanCong(),
                "Dan trai pha sai he so damage");
        bang(300, hoSo.getNapDanSauDung(),
                "Dan trai pha sai nap dan");
        bang(1, hoSo.getSoLanToiDaMoiTran(),
                "Dan trai pha sai quota moi tran");
        bang(ChickenCauHinhSatThuongVatPham.HieuUngDacBiet.PHA_DIA_HINH,
                hoSo.getHieuUngDacBiet(),
                "Dan trai pha thieu hieu ung pha dia hinh");

        ChickenYeuCauBanServer.KetQua yeuCau =
                ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(99, 32_000, -32_000, 725, 255, 99),
                        cauHinh);
        khacNull(yeuCau, "packet Dan trai pha hop le bi tu choi");
        bang(16, yeuCau.getLoaiDan() & 255,
                "Dan trai pha tin bulletType gia tu client");
        bang(1, yeuCau.getSoVienMoiLoat() & 255,
                "Dan trai pha tin so dan gia tu client");
        bang(5, yeuCau.getGoc() & 0xFFFF,
                "Dan trai pha khong chuan hoa goc");
        bang(30, yeuCau.getLuc() & 255,
                "Dan trai pha khong kep luc");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[8]),
                        cauHinh) == null,
                "packet Dan trai pha thieu byte van duoc nhan");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[10]),
                        cauHinh) == null,
                "packet Dan trai pha du byte van duoc nhan");

        ChickenQuanLyCongThucSung.KiemTraBanDo nenPhang =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() {
                        return 1_000;
                    }

                    @Override
                    public int getHeight() {
                        return 600;
                    }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return x >= 0 && x < 1_000 && y >= 400;
                    }
                };
        ChickenChienBinh shooter = new ChickenChienBinh(
                (byte) 0, (short) 100, (short) 300,
                "MortarShooter", (short) 57, (byte) 0);
        shooter.tanCong = 1_000;
        ChickenKetQuaDan moc = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 30, cauHinh,
                (byte) 0, (byte) 0, nenPhang,
                new ChickenChienBinh[]{shooter},
                (nguoiBan, mucTieu) -> mucTieu != nguoiBan);
        khacNull(moc, "engine Dan trai pha khong tao ket qua");
        bang(7, moc.cacDuongX.length,
                "client type 16 khong nhan du 1 marker + 6 dan roi X");
        bang(7, moc.cacDuongY.length,
                "client type 16 khong nhan du 1 marker + 6 dan roi Y");
        bang(ChickenLoaiDanPhaDiaHinhClient.KHONG_PHA_DIA_HINH,
                ChickenLoaiDanPhaDiaHinhClient.layLoaiDanTaoLo(
                        (byte) 16, 0),
                "marker Dan trai pha lai pha dia hinh");
        for (int i = 0; i < 7; i++) {
            dung(moc.cacDuongX[i] != null
                            && moc.cacDuongX[i].length > 0,
                    "Dan trai pha co duong X rong: " + i);
            dung(moc.cacDuongY[i] != null
                            && moc.cacDuongY[i].length > 0,
                    "Dan trai pha co duong Y rong: " + i);
            if (i > 0) {
                bang(12,
                        ChickenLoaiDanPhaDiaHinhClient.layLoaiDanTaoLo(
                                (byte) 16, i),
                        "Dan trai pha roi khong dung mask type 12");
                dung(moc.cacDuongY[i][0]
                                < moc.cacDuongY[i][
                                        moc.cacDuongY[i].length - 1],
                        "vien Dan trai pha khong roi tu tren xuong: " + i);
            }
        }
        dung(moc.cacDuongX[1][0] != moc.cacDuongX[6][0],
                "sau vien Dan trai pha bi chong cung mot cot");

        int duongMucTieu = 3;
        int cuoi = Math.min(
                moc.cacDuongX[duongMucTieu].length,
                moc.cacDuongY[duongMucTieu].length) - 1;
        ChickenChienBinh mucTieu = new ChickenChienBinh(
                (byte) 1,
                moc.cacDuongX[duongMucTieu][cuoi],
                moc.cacDuongY[duongMucTieu][cuoi],
                "MortarTarget", (short) 57, (byte) 0);
        ChickenKetQuaDan coDamage = ChickenPhatBanVatPhamServer.tao(
                shooter, (short) 100, (short) 300,
                (short) 0, (byte) 30, cauHinh,
                (byte) 0, (byte) 0, nenPhang,
                new ChickenChienBinh[]{shooter, mucTieu},
                (nguoiBan, dich) -> dich != nguoiBan);
        Integer damage = coDamage.satThuongTheoMucTieu.get(mucTieu);
        khacNull(damage,
                "Dan trai pha roi sat muc tieu nhung khong co damage");
        dung(damage > 0 && damage <= 900,
                "Dan trai pha cong don damage cua sau vien");
    }

    private static void kiemTraCuuThuongBanThan() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(220);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(220);
        khacNull(cauHinh, "Cuu thuong ca nhan thieu mapping client");
        bang(0, cauHinh.getMaSuDung(),
                "Cuu thuong ca nhan sai CPlayer.itemUsed");
        bang(KieuQuyDao.KHONG_TAO_DAN, cauHinh.getKieuQuyDao(),
                "Cuu thuong ca nhan lai tao dan");
        khacNull(hoSo, "Cuu thuong ca nhan thieu ho so runtime");
        bang(-1, hoSo.getNapDanSauDung(),
                "Cuu thuong ca nhan ghi de nap dan sung");
        bang(2, hoSo.getSoLanToiDaMoiTran(),
                "Cuu thuong ca nhan sai gioi han hai goi moi tran");
        bang(ChickenCauHinhSatThuongVatPham.HieuUngDacBiet
                        .HOI_MAU_BAN_THAN,
                hoSo.getHieuUngDacBiet(),
                "Cuu thuong ca nhan sai hieu ung server");

        ChickenChienBinh nguoiDung =
                chienBinhNguoiThat((byte) 0, 1_000, 200);
        ChickenChienBinh dongDoi =
                chienBinhNguoiThat((byte) 2, 1_000, 200);
        bang(300, ChickenCuuThuongBanThan.apDung(nguoiDung),
                "Cuu thuong ca nhan khong hoi du 300 HP");
        bang(500, nguoiDung.hp,
                "Cuu thuong ca nhan sai HP nguoi dung");
        bang(200, dongDoi.hp,
                "Cuu thuong ca nhan hoi nham dong doi");

        nguoiDung.hp = 900;
        bang(100, ChickenCuuThuongBanThan.apDung(nguoiDung),
                "Cuu thuong ca nhan khong kep tai HP toi da");
        bang(1_000, nguoiDung.hp,
                "Cuu thuong ca nhan lam HP vuot tran");
        dung(!ChickenCuuThuongBanThan.coTheDung(nguoiDung),
                "Cuu thuong ca nhan van dung duoc khi day HP");
        nguoiDung.hp = 0;
        nguoiDung.chet = true;
        bang(0, ChickenCuuThuongBanThan.apDung(nguoiDung),
                "Cuu thuong ca nhan hoi sinh nguoi da chet");

        DichVuImLang dichVuQuota = new DichVuImLang();
        NguoiChoiKiemThu nguoiChoiQuota =
                new NguoiChoiKiemThu(dichVuQuota, true);
        dichVuQuota.datNguoiChoi(nguoiChoiQuota);
        ChickenVatPham cuuThuong = taoCuuThuongBanThan(20, 3);
        nguoiChoiQuota.itemBag[20] = cuuThuong;
        nguoiChoiQuota.itemBalo = new int[]{20, -1, -1, -1, -1};
        ChickenChienBinh chienBinhQuota = new ChickenChienBinh(
                nguoiChoiQuota, (byte) 0, (short) 100, (short) 300);
        ChickenChienBinh.VatPhamChienTrongTran snapshot =
                chienBinhQuota.layVatPhamChienTrongOTrongBalo(0);
        khacNull(snapshot,
                "Cuu thuong ca nhan khong vao snapshot Balo");

        dung(chienBinhQuota.chonVatPhamChienTrongTran(0),
                "khong chon duoc Cuu thuong lan dau");
        dung(chienBinhQuota.danhDauDaDungVatPhamChienTrongTran(snapshot),
                "khong ghi nhan Cuu thuong lan dau");
        chienBinhQuota.xoaVatPhamChienDangCho();
        dung(!chienBinhQuota.chonVatPhamChienTrongTran(0),
                "Cuu thuong dung duoc hai lan trong cung mot luot");

        ChickenTrangThaiHanhDongLuot.ketThucLuot(chienBinhQuota);
        try {
            dichVuQuota.guiBaloTrongTran(chienBinhQuota);
            bang(220, dichVuQuota.layIdOTrangBiDauTien(),
                    "Cuu thuong con quota khong hien lai o luot sau");
        } catch (Exception ex) {
            throw new AssertionError(
                    "khong kiem tra duoc packet Balo Cuu thuong lan hai",
                    ex);
        }
        dung(chienBinhQuota.chonVatPhamChienTrongTran(0),
                "Cuu thuong khong dung duoc lan hai o luot sau");
        dung(chienBinhQuota.danhDauDaDungVatPhamChienTrongTran(snapshot),
                "khong ghi nhan Cuu thuong lan hai");
        ChickenTrangThaiHanhDongLuot.ketThucLuot(chienBinhQuota);
        try {
            dichVuQuota.guiBaloTrongTran(chienBinhQuota);
            bang(-1, dichVuQuota.layIdOTrangBiDauTien(),
                    "Cuu thuong van hien sau khi dung du hai lan");
        } catch (Exception ex) {
            throw new AssertionError(
                    "khong kiem tra duoc packet Balo Cuu thuong het quota",
                    ex);
        }
        dung(!chienBinhQuota.chonVatPhamChienTrongTran(0),
                "Cuu thuong dung qua hai lan trong mot tran");
    }

    private static void kiemTraDiChuyenX2() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(223);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(223);
        khacNull(cauHinh, "Di chuyen x2 thieu mapping client");
        bang(3, cauHinh.getMaSuDung(),
                "Di chuyen x2 sai CPlayer.itemUsed");
        bang(KieuQuyDao.KHONG_TAO_DAN, cauHinh.getKieuQuyDao(),
                "Di chuyen x2 lai tao dan");
        bang(KieuGoc.KHONG_CAN_GOC, cauHinh.getKieuGoc(),
                "Di chuyen x2 lai doi packet ban");
        khacNull(hoSo, "Di chuyen x2 thieu ho so runtime");
        bang(-1, hoSo.getNapDanSauDung(),
                "Di chuyen x2 ghi de nap dan sung");
        bang(1, hoSo.getSoLanToiDaMoiTran(),
                "Di chuyen x2 sai quota moi tran");
        bang(ChickenCauHinhSatThuongVatPham.HieuUngDacBiet
                        .NHAN_DOI_DI_CHUYEN,
                hoSo.getHieuUngDacBiet(),
                "Di chuyen x2 sai hieu ung server");

        ChickenMauVatPham mauDung = new ChickenMauVatPham(
                (short) 223, (byte) 10, (byte) 3,
                "Di chuyen x2", "", (byte) 1, 0,
                (short) 972, (short) 0, false);
        dung(ChickenCongThucVatPhamChien.khopMauVatPham(mauDung),
                "template Di chuyen x2 dung bi tu choi");
        mauDung.gioiTinh = 2;
        dung(!ChickenCongThucVatPhamChien.khopMauVatPham(mauDung),
                "action gia cua Di chuyen x2 van duoc chap nhan");

        DichVuImLang dichVu = new DichVuImLang();
        NguoiChoiKiemThu nguoiChoi =
                new NguoiChoiKiemThu(dichVu, true);
        dichVu.datNguoiChoi(nguoiChoi);
        nguoiChoi.itemBag[19] = taoDiChuyenX2(19, 2);
        nguoiChoi.itemBalo = new int[]{19, -1, -1, -1, -1};
        ChickenChienBinh chienBinh = new ChickenChienBinh(
                nguoiChoi, (byte) 0, (short) 100, (short) 300);
        chienBinh.theLucDiChuyenToiDa = 100;
        chienBinh.quangDuongDiChuyenConLai = 35;
        ChickenChienBinh.VatPhamChienTrongTran snapshot =
                chienBinh.layVatPhamChienTrongOTrongBalo(0);
        khacNull(snapshot, "Di chuyen x2 khong vao snapshot Balo");
        dung(chienBinh.chonVatPhamChienTrongTran(0),
                "khong chon duoc Di chuyen x2 lan dau");
        dung(chienBinh.danhDauDaDungVatPhamChienTrongTran(snapshot),
                "khong ghi quota Di chuyen x2");
        dung(ChickenDiChuyenX2.apDung(chienBinh),
                "server khong kich hoat Di chuyen x2");
        bang(70, chienBinh.quangDuongDiChuyenConLai,
                "Di chuyen x2 hoan sai phan thanh da tieu");
        bang(200, chienBinh.layQuangDuongDiChuyenToiDaTrongLuot(),
                "Di chuyen x2 sai gioi han luot sau");
        dung(!ChickenDiChuyenX2.apDung(chienBinh),
                "Di chuyen x2 bi cong don khi gui packet lap");
        bang(70, chienBinh.quangDuongDiChuyenConLai,
                "packet lap lam nhan doi quang duong lan hai");

        chienBinh.xoaVatPhamChienDangCho();
        dung(!chienBinh.danhDauKichHoatPowTrongLuot(),
                "Di chuyen x2 van cho chen POW cung luot");
        ChickenTrangThaiHanhDongLuot.ketThucLuot(chienBinh);
        chienBinh.hoiDayQuangDuongDiChuyen();
        bang(200, chienBinh.quangDuongDiChuyenConLai,
                "Di chuyen x2 khong ton tai sang luot sau");
        dung(!chienBinh.chonVatPhamChienTrongTran(0),
                "Di chuyen x2 dung duoc lan hai trong cung tran");

        ChickenKetQuaDan ketQuaGia = new ChickenKetQuaDan(
                (byte) 0, (short) 100, (short) 300,
                (short) 45, (byte) 20,
                new short[]{100, 110}, new short[]{300, 290}, null, 0);
        dung(!ChickenHieuUngVatPhamChien.coTheApDung(
                        snapshot, ketQuaGia, new ChickenQuanLyBanDo(-1)),
                "Di chuyen x2 bi kich hoat qua CMD ban gia");

        bang(0, ChickenDiChuyenX2.nhanDoiAnToan(-10),
                "Di chuyen x2 bien state am thanh quang duong");
        bang(Integer.MAX_VALUE,
                ChickenDiChuyenX2.nhanDoiAnToan(Integer.MAX_VALUE),
                "Di chuyen x2 tran so nguyen");
    }

    private static void kiemTraNgungGio() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(225);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(225);
        khacNull(cauHinh, "Ngung gio thieu mapping client");
        bang(5, cauHinh.getMaSuDung(),
                "Ngung gio sai CPlayer.itemUsed");
        bang(-1, (int) cauHinh.getLoaiDan(),
                "Ngung gio bi gan bulletType gia");
        bang(KieuQuyDao.KHONG_TAO_DAN, cauHinh.getKieuQuyDao(),
                "Ngung gio lai tao dan");
        bang(KieuGoc.KHONG_CAN_GOC, cauHinh.getKieuGoc(),
                "Ngung gio lai yeu cau packet ban");
        khacNull(hoSo, "Ngung gio thieu ho so runtime");
        bang(-1, hoSo.getNapDanSauDung(),
                "Ngung gio ghi de nap dan sung");
        bang(3, hoSo.getSoLanToiDaMoiTran(),
                "Ngung gio sai quota ba lan moi tran");
        bang(ChickenCauHinhSatThuongVatPham.HieuUngDacBiet.NGUNG_GIO,
                hoSo.getHieuUngDacBiet(),
                "Ngung gio sai hieu ung server");

        ChickenHeThongGio.TrangThaiGio gioCu =
                ChickenHeThongGio.taoTheoGocVaLuc(45, 100);
        dung(gioCu.getWindX() != 0 || gioCu.getWindY() != 0,
                "fixture gio test lai khong co gio");
        ChickenHeThongGio.TrangThaiGio daNgung =
                ChickenNgungGio.taoTrangThaiKhongGio();
        bang(0, (int) daNgung.getWindX(),
                "Ngung gio khong xoa windX");
        bang(0, (int) daNgung.getWindY(),
                "Ngung gio khong xoa windY");

        ChickenMauVatPham mauDung = new ChickenMauVatPham(
                (short) 225, (byte) 10, (byte) 5,
                "Ngung gio", "", (byte) 1, 0,
                (short) 974, (short) 0, false);
        dung(ChickenCongThucVatPhamChien.khopMauVatPham(mauDung),
                "template Ngung gio dung bi tu choi");
        mauDung.gioiTinh = 6;
        dung(!ChickenCongThucVatPhamChien.khopMauVatPham(mauDung),
                "action gia cua Ngung gio van duoc chap nhan");

        DichVuImLang dichVu = new DichVuImLang();
        NguoiChoiKiemThu nguoiChoi =
                new NguoiChoiKiemThu(dichVu, true);
        dichVu.datNguoiChoi(nguoiChoi);
        nguoiChoi.itemBag[18] = taoNgungGio(18, 4);
        nguoiChoi.itemBalo = new int[]{18, -1, -1, -1, -1};
        ChickenChienBinh chienBinh = new ChickenChienBinh(
                nguoiChoi, (byte) 0, (short) 100, (short) 300);
        ChickenChienBinh.VatPhamChienTrongTran snapshot =
                chienBinh.layVatPhamChienTrongOTrongBalo(0);
        khacNull(snapshot, "Ngung gio khong vao snapshot Balo");
        dung(ChickenNgungGio.coTheDung(chienBinh),
                "nguoi choi song bi cam dung Ngung gio");

        for (int lan = 0; lan < 3; lan++) {
            dung(chienBinh.chonVatPhamChienTrongTran(0),
                    "khong chon duoc Ngung gio lan " + (lan + 1));
            dung(chienBinh.danhDauDaDungVatPhamChienTrongTran(snapshot),
                    "khong ghi quota Ngung gio lan " + (lan + 1));
            chienBinh.xoaVatPhamChienDangCho();
            dung(!chienBinh.danhDauKichHoatPowTrongLuot(),
                    "Ngung gio van cho chen POW cung luot");
            ChickenTrangThaiHanhDongLuot.ketThucLuot(chienBinh);
        }
        dung(!chienBinh.chonVatPhamChienTrongTran(0),
                "Ngung gio dung qua ba lan trong mot tran");
        chienBinh.chet = true;
        chienBinh.hp = 0;
        dung(!ChickenNgungGio.coTheDung(chienBinh),
                "nguoi da chet van dung duoc Ngung gio");
    }

    private static void kiemTraMotTrangThaiGioChoTatCaBoss() {
        try {
            Field field = ChickenQuanLyChien.class
                    .getDeclaredField("gioHienTai");
            dung(Modifier.isProtected(field.getModifiers()),
                    "trang thai gio lop cha khong cho boss dung chung");
            String[] cacLopBoss = {
                "com.chicken.phong.boss.trandau.baovay.BossBaoVay",
                "com.chicken.phong.boss.trandau.haitoathap.BossHaiToaThap",
                "com.chicken.phong.boss.trandau.khicau.BossKhiCau",
                "com.chicken.phong.boss.trandau.datbom.BossDatBom",
                "com.chicken.phong.boss.trandau.rua.BossRua",
                "com.chicken.phong.boss.trandau.rong.BossRong",
                "com.chicken.phong.boss.trandau.ruarong.BossRuaRong"
            };
            for (String tenLop : cacLopBoss) {
                try {
                    Class.forName(tenLop).getDeclaredField("gioHienTai");
                    throw new AssertionError(
                            tenLop + " shadow trang thai gio lop cha");
                } catch (NoSuchFieldException mongDoi) {
                    // Dung: boss su dung field authoritative cua lop cha.
                }
            }
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError(
                    "khong kiem tra duoc trang thai gio dung chung", ex);
        }
    }

    private static void kiemTraCuuThuongDongDoi() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(230);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(230);
        khacNull(cauHinh, "Cuu thuong thieu mapping client");
        bang(10, cauHinh.getMaSuDung(),
                "Cuu thuong sai CPlayer.itemUsed");
        bang(KieuQuyDao.KHONG_TAO_DAN, cauHinh.getKieuQuyDao(),
                "Cuu thuong lai tao duong dan");
        bang(KieuGoc.KHONG_CAN_GOC, cauHinh.getKieuGoc(),
                "Cuu thuong lai doi CMD 22");
        khacNull(hoSo, "Cuu thuong thieu ho so runtime");
        dung(!hoSo.coSatThuongNo(), "Cuu thuong bi gan damage gia");
        bang(-1,
                hoSo.getNapDanSauDung(),
                "Cuu thuong khong giu nap dan cua sung ban sau do");
        bang(1, hoSo.getSoLanToiDaMoiTran(),
                "Cuu thuong sai quota moi tran");
        bang(ChickenCauHinhSatThuongVatPham.HieuUngDacBiet
                        .HOI_MAU_DONG_DOI,
                hoSo.getHieuUngDacBiet(),
                "Cuu thuong sai hieu ung server");

        ChickenChienBinh nguoiDung = chienBinhNguoiThat((byte) 0, 100, 40);
        ChickenChienBinh dongDoi = chienBinhNguoiThat((byte) 2, 200, 150);
        ChickenChienBinh keDich = chienBinhNguoiThat((byte) 1, 200, 120);
        ChickenChienBinh dongDoiChet =
                chienBinhNguoiThat((byte) 4, 200, 0);
        dongDoiChet.chet = true;
        ChickenChienBinh boss = new ChickenChienBinh(
                (byte) 5, (short) 0, (short) 0,
                "Boss", (short) 57, (byte) 0);
        boss.mauToiDa = 500;
        boss.hp = 100;
        ChickenChienBinh[] danhSach = {
            nguoiDung, keDich, dongDoi, null, dongDoiChet, boss
        };

        java.util.List<ChickenChienBinh> mucTieuPvp =
                ChickenCuuThuongDongDoi.layMucTieu(
                        nguoiDung, danhSach, true);
        bang(2, mucTieuPvp.size(),
                "PvP cuu sai so nguoi cung phe");
        dung(mucTieuPvp.contains(nguoiDung)
                        && mucTieuPvp.contains(dongDoi),
                "PvP bo sot nguoi dung hoac dong doi");
        dung(!mucTieuPvp.contains(keDich)
                        && !mucTieuPvp.contains(dongDoiChet)
                        && !mucTieuPvp.contains(boss),
                "PvP hoi nham ke dich, nguoi chet hoac bot");
        ChickenCuuThuongDongDoi.apDung(mucTieuPvp);
        bang(70, nguoiDung.hp,
                "Cuu thuong khong hoi 50% HP da mat cua nguoi dung");
        bang(175, dongDoi.hp,
                "Cuu thuong khong hoi 50% HP da mat cua dong doi");
        bang(120, keDich.hp, "Cuu thuong hoi nham ke dich");

        nguoiDung.hp = 20;
        dongDoi.hp = 20;
        keDich.hp = 20;
        java.util.List<ChickenChienBinh> mucTieuBoss =
                ChickenCuuThuongDongDoi.layMucTieu(
                        nguoiDung, danhSach, false);
        bang(3, mucTieuBoss.size(),
                "phong boss khong hoi du nguoi choi con song");
        ChickenCuuThuongDongDoi.apDung(mucTieuBoss);
        bang(60, nguoiDung.hp,
                "phong boss sai 50% HP da mat nguoi dung");
        bang(110, dongDoi.hp,
                "phong boss sai 50% HP da mat dong doi");
        bang(110, keDich.hp,
                "phong boss khong coi player la dong doi");
        bang(100,
                ChickenCuuThuongDongDoi.tinhMauSauCuuThuong(99, 100),
                "Cuu thuong lam tron xuong khi chi thieu 1 HP");
        bang(100,
                ChickenCuuThuongDongDoi.tinhMauSauCuuThuong(100, 100),
                "Cuu thuong lam HP vuot tran");
        bang(50,
                ChickenCuuThuongDongDoi.tinhMauSauCuuThuong(-100, 100),
                "Cuu thuong khong kep HP am tu state loi");

    }

    private static void kiemTraDichChuyenTucThoi() {
        CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(221);
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(221);
        khacNull(cauHinh, "Dich chuyen thieu mapping client");
        bang(1, cauHinh.getMaSuDung(),
                "Dich chuyen sai CPlayer.itemUsed");
        bang((byte) 5, cauHinh.getLoaiDan(),
                "Dich chuyen sai bullet type client");
        bang(KieuQuyDao.PARABOL, cauHinh.getKieuQuyDao(),
                "Dich chuyen sai kieu quy dao");
        khacNull(hoSo, "Dich chuyen thieu ho so runtime");
        bang(0, hoSo.getPhanTramTanCong(),
                "Dich chuyen bi gan damage");
        dung(!hoSo.coSatThuongNo(),
                "Dich chuyen bi xu ly nhu bom no");
        bang(300, hoSo.getNapDanSauDung(),
                "Dich chuyen sai nap dan");
        bang(hoSo.getNapDanSauDung(),
                ChickenCauHinhLuyenTap.TRAINING_BOSS_TELEPORT_RELOAD,
                "Bot luyen tap Dich chuyen khong nap dan nhu nguoi choi");
        bang(2, hoSo.getSoLanToiDaMoiTran(),
                "Dich chuyen sai quota hai lan moi tran");
        bang(ChickenCauHinhSatThuongVatPham.HieuUngDacBiet
                        .DICH_CHUYEN_TUC_THOI,
                hoSo.getHieuUngDacBiet(),
                "Dich chuyen thieu hieu ung server");

        DichVuImLang dichVu = new DichVuImLang();
        NguoiChoiKiemThu nguoiChoi =
                new NguoiChoiKiemThu(dichVu, true);
        dichVu.datNguoiChoi(nguoiChoi);
        ChickenVatPham item = taoDichChuyenTucThoi(18, 3);
        nguoiChoi.itemBag[18] = item;
        nguoiChoi.itemBalo = new int[]{18, -1, -1, -1, -1};
        ChickenChienBinh nguoiDung = new ChickenChienBinh(
                nguoiChoi, (byte) 0, (short) 100, (short) 300);
        ChickenChienBinh.VatPhamChienTrongTran snapshot =
                nguoiDung.layVatPhamChienTrongOTrongBalo(0);
        ChickenChienBinh mucTieuChanDuong = new ChickenChienBinh(
                (byte) 1, (short) 140, (short) 300,
                "TeleportPassThrough", (short) 57, (byte) 0);
        ChickenQuanLyCongThucSung.KiemTraBanDo mapCoNen =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() {
                        return 320;
                    }

                    @Override
                    public int getHeight() {
                        return 400;
                    }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return y >= 320;
                    }
                };
        ChickenKetQuaDan ketQuaVatLy = ChickenPhatBanVatPhamServer.tao(
                nguoiDung, (short) 100, (short) 300,
                (short) 0, (byte) 20, cauHinh,
                (byte) 0, (byte) 0, mapCoNen,
                new ChickenChienBinh[]{nguoiDung, mucTieuChanDuong},
                (shooter, target) -> target != shooter);
        khacNull(ketQuaVatLy,
                "server khong tao duoc duong Dich chuyen");
        dung(ketQuaVatLy.satThuongTheoMucTieu.isEmpty(),
                "Dich chuyen gay damage vao nhan vat chan duong");
        int cuoiVatLy = ketQuaVatLy.duongX.length - 1;
        dung(cuoiVatLy >= 0
                        && ketQuaVatLy.duongX[cuoiVatLy] >= 0
                        && ketQuaVatLy.duongX[cuoiVatLy] < 320
                        && ketQuaVatLy.duongY[cuoiVatLy] >= 0
                        && ketQuaVatLy.duongY[cuoiVatLy] < 400,
                "Dich chuyen tra diem cuoi ngoai map");
        dung(ketQuaVatLy.duongX[cuoiVatLy]
                        > mucTieuChanDuong.x + 12,
                "Vien Dich chuyen bi hitbox nhan vat chan lai");

        ChickenQuanLyCongThucSung.KiemTraBanDo mapKhongNen =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() {
                        return 320;
                    }

                    @Override
                    public int getHeight() {
                        return 400;
                    }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return false;
                    }
                };
        ChickenKetQuaDan ketQuaRaNgoai =
                ChickenPhatBanVatPhamServer.tao(
                        nguoiDung, (short) 100, (short) 300,
                        (short) 0, (byte) 30, cauHinh,
                        (byte) 0, (byte) 0, mapKhongNen,
                        new ChickenChienBinh[]{mucTieuChanDuong},
                        (shooter, target) -> true);
        khacNull(ketQuaRaNgoai,
                "Dich chuyen ngoai map khong tra ket qua an toan");
        int cuoiNgoai = ketQuaRaNgoai.duongX.length - 1;
        bang((int) nguoiDung.x,
                (int) ketQuaRaNgoai.duongX[cuoiNgoai],
                "Dich chuyen ngoai map van doi X");
        bang((int) nguoiDung.y,
                (int) ketQuaRaNgoai.duongY[cuoiNgoai],
                "Dich chuyen ngoai map van doi Y");

        bang(ChickenAiLuyenTap.HanhDongGan.DICH_CHUYEN,
                ChickenAiLuyenTap.chonHanhDongGan(0),
                "AI luyen tap khong Dich chuyen khi trung toa do");
        bang(ChickenAiLuyenTap.HanhDongGan.DICH_CHUYEN,
                ChickenAiLuyenTap.chonHanhDongGan(149),
                "AI luyen tap khong Dich chuyen khi o gan");
        bang(ChickenAiLuyenTap.HanhDongGan.BAN,
                ChickenAiLuyenTap.chonHanhDongGan(150),
                "AI van Dich chuyen khi khong con o gan");

        ChickenQuanLyBanDo banDo = new ChickenQuanLyBanDo(-1);
        ChickenKetQuaDan ketQua = new ChickenKetQuaDan(
                (byte) 5, (short) 100, (short) 300,
                (short) 20, (byte) 20,
                new short[]{100, 160, 220},
                new short[]{300, 250, 280}, null, 0);
        dung(ChickenHieuUngVatPhamChien.coTheApDung(
                        snapshot, ketQua, banDo),
                "server tu choi diem Dich chuyen hop le");
        dung(ChickenHieuUngVatPhamChien.apDung(
                        snapshot, ketQua, banDo),
                "Dich chuyen khong qua duoc giao dich item");
        bang(100, (int) nguoiDung.x,
                "Dich chuyen doi toa do truoc khi gui animation");
        dung(ChickenHieuUngVatPhamChien
                        .apDungDichChuyenSauKhiGuiPhatBan(
                                nguoiDung, snapshot, ketQua, banDo),
                "server khong chot diem Dich chuyen");
        bang(220, (int) nguoiDung.x,
                "Dich chuyen sai X diem cuoi server");
        bang(280, (int) nguoiDung.y,
                "Dich chuyen sai Y diem cuoi server");

        ChickenKetQuaDan ngoaiBanDo = new ChickenKetQuaDan(
                (byte) 5, (short) 100, (short) 300,
                (short) 0, (byte) 1,
                new short[]{-10}, new short[]{100}, null, 0);
        dung(!ChickenHieuUngVatPhamChien.coTheApDung(
                        snapshot, ngoaiBanDo, banDo),
                "Dich chuyen chap nhan diem ngoai map");

        dung(nguoiDung.chonVatPhamChienTrongTran(0),
                "khong chon duoc Dich chuyen lan dau");
        dung(nguoiDung.danhDauDaDungVatPhamChienTrongTran(snapshot),
                "khong ghi quota Dich chuyen lan dau");
        ChickenTrangThaiHanhDongLuot.ketThucLuot(nguoiDung);
        dung(nguoiDung.chonVatPhamChienTrongTran(0),
                "khong chon duoc Dich chuyen lan hai");
        dung(nguoiDung.danhDauDaDungVatPhamChienTrongTran(snapshot),
                "khong ghi quota Dich chuyen lan hai");
        ChickenTrangThaiHanhDongLuot.ketThucLuot(nguoiDung);
        dung(!nguoiDung.chonVatPhamChienTrongTran(0),
                "Dich chuyen dung qua hai lan trong tran");
    }

    private static ChickenChienBinh chienBinhNguoiThat(
            byte chiSo, int mauToiDa, int hp
    ) {
        NguoiChoiKiemThu nguoiChoi =
                new NguoiChoiKiemThu(new DichVuImLang(), true);
        nguoiChoi.ma = 230_000 + (chiSo & 0xFF);
        nguoiChoi.ten = "Medic" + (chiSo & 0xFF);
        ChickenChienBinh chienBinh = new ChickenChienBinh(
                nguoiChoi, chiSo, (short) 100, (short) 300);
        chienBinh.mauToiDa = mauToiDa;
        chienBinh.hp = hp;
        return chienBinh;
    }

    private static void kiemTraPacketLuuDan() {
        CauHinh luuDan =
                ChickenCongThucVatPhamChien.theoIdVatPham(227);
        ChickenTinNhan packet = taoPacketBan(
                99, 32_000, -32_000, 725, 255, 99);
        ChickenYeuCauBanServer.KetQua ketQua =
                ChickenYeuCauBanServer.docVatPham(packet, luuDan);
        khacNull(ketQua, "packet Luu dan 9 byte bi tu choi");
        bang(7, ketQua.getLoaiDan() & 255,
                "server tin bulletType gia cua client");
        bang(1, ketQua.getSoVienMoiLoat() & 255,
                "server tin so vien gia cua client");
        bang(5, ketQua.getGoc() & 0xFFFF,
                "goc Luu dan khong duoc chuan hoa");
        bang(30, ketQua.getLuc() & 255,
                "luc Luu dan khong duoc kep");

        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan(
                                (byte) 22, new byte[8]), luuDan) == null,
                "packet Luu dan thieu byte van duoc nhan");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan(
                                (byte) 22, new byte[10]), luuDan) == null,
                "packet Luu dan du byte van duoc nhan");
        dung(ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(7, 0, 0, 45, 20, 1),
                        ChickenCongThucVatPhamChien
                                .theoIdVatPham(258)) == null,
                "item khong can goc lai nhan packet ban");

        CauHinh bomB52 =
                ChickenCongThucVatPhamChien.theoIdVatPham(228);
        ChickenYeuCauBanServer.KetQua ketQuaB52 =
                ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(99, -123, 32_000, 400, 255, 99),
                        bomB52);
        khacNull(ketQuaB52, "packet Bom B52 hop le bi tu choi");
        bang(4, ketQuaB52.getLoaiDan() & 255,
                "Bom B52 tin bulletType gia cua client");
        bang(1, ketQuaB52.getSoVienMoiLoat() & 255,
                "Bom B52 tin so vien gia cua client");

        CauHinh toNhen =
                ChickenCongThucVatPhamChien.theoIdVatPham(229);
        ChickenYeuCauBanServer.KetQua ketQuaToNhen =
                ChickenYeuCauBanServer.docVatPham(
                        taoPacketBan(99, 32_000, -32_000, 725, 255, 99),
                        toNhen);
        khacNull(ketQuaToNhen, "packet To nhan hop le bi tu choi");
        bang(8, ketQuaToNhen.getLoaiDan() & 255,
                "To nhan tin bulletType gia cua client");
        bang(1, ketQuaToNhen.getSoVienMoiLoat() & 255,
                "To nhan tin so vien gia cua client");
        dung(ChickenYeuCauBanServer.docVatPham(
                        new ChickenTinNhan((byte) 22, new byte[8]),
                        toNhen) == null,
                "packet To nhan thieu byte van duoc nhan");
    }

    private static void kiemTraSnapshotVaTieuHaoLuuDan() {
        DichVuImLang dichVu = new DichVuImLang();
        NguoiChoiKiemThu nguoiChoi =
                new NguoiChoiKiemThu(dichVu, true);
        dichVu.datNguoiChoi(nguoiChoi);
        nguoiChoi.ma = 227_001;
        nguoiChoi.ten = "GrenadeInventoryTest";
        ChickenVatPham luuDan = taoLuuDan(17, 2);
        ChickenVatPham bomB52 = taoBomB52(18, 2);
        ChickenVatPham toNhen = taoToNhen(19, 2);
        nguoiChoi.itemBag[17] = luuDan;
        nguoiChoi.itemBag[18] = bomB52;
        nguoiChoi.itemBag[19] = toNhen;
        nguoiChoi.itemBalo = new int[]{17, 18, 19, -1, -1};

        ChickenChienBinh chienBinh =
                new ChickenChienBinh(
                        nguoiChoi, (byte) 0, (short) 100, (short) 300);
        ChickenChienBinh.VatPhamChienTrongTran snapshot =
                chienBinh.layVatPhamChienTrongOTrongBalo(0);
        ChickenChienBinh.VatPhamChienTrongTran snapshotB52 =
                chienBinh.layVatPhamChienTrongOTrongBalo(1);
        ChickenChienBinh.VatPhamChienTrongTran snapshotToNhen =
                chienBinh.layVatPhamChienTrongOTrongBalo(2);
        khacNull(snapshot,
                "snapshot khong khoa Luu dan theo o hien thi Balo");
        khacNull(snapshotB52,
                "snapshot khong khoa Bom B52 theo o hien thi Balo");
        khacNull(snapshotToNhen,
                "snapshot khong khoa To nhan theo o hien thi Balo");
        dung(chienBinh.layVatPhamChienTrongOTrongBalo(17) == null,
                "snapshot dung index itemBag thay vi o Balo");
        dung(chienBinh.chonVatPhamChienTrongTran(0),
                "khong chon duoc Luu dan da trang bi Balo");
        dung(!chienBinh.chonVatPhamChienTrongTran(1),
                "mot luot van chon duoc ca Luu dan va Bom B52");
        dung(!chienBinh.danhDauKichHoatPowTrongLuot(),
                "chon Luu dan xong van kich hoat duoc POW");
        chienBinh.xoaVatPhamChienDangCho();
        dung(!chienBinh.chonVatPhamChienTrongTran(1),
                "xoa item dang cho da mo khoa item thu hai cung luot");
        dung(!ChickenTrangThaiHanhDongLuot.coTheKichHoatKyNang(chienBinh),
                "dang cam Luu dan van kich hoat chong ky nang AVG");
        ChickenTrangThaiHanhDongLuot.ketThucLuot(chienBinh);
        dung(chienBinh.layVatPhamChienDangCho() == null,
                "het luot khong xoa trang thai Luu dan dang cho");
        dung(chienBinh.danhDauKichHoatPowTrongLuot(),
                "luot moi khong khoa duoc quyen POW");
        dung(!chienBinh.chonVatPhamChienTrongTran(1),
                "kich hoat POW xong van chon duoc Bom B52");
        ChickenTrangThaiHanhDongLuot.ketThucLuot(chienBinh);
        dung(chienBinh.chonVatPhamChienTrongTran(0),
                "khong chon lai duoc Luu dan truoc khi da dung");
        bang(2, luuDan.soLuong,
                "chon item da tru kho truoc khi ban");
        dung(nguoiChoi.tieuThuMotVatPhamChien(snapshot),
                "khong tru duoc Luu dan sau khi server chap nhan");
        bang(1, luuDan.soLuong,
                "moi phat khong tru dung mot Luu dan");
        bang(1, nguoiChoi.soLanLuu,
                "tieu hao Luu dan khong ghi kho dung mot lan");
        bang(300, chienBinh.layNapDanSauKhiDungVatPham(snapshot),
                "Luu dan khong ap dung nap dan 300");
        dung(chienBinh.danhDauDaDungVatPhamChienTrongTran(snapshot),
                "server khong danh dau lan dung Luu dan dau tien");
        try {
            dichVu.guiBaloTrongTran(chienBinh);
            bang(-1, dichVu.layIdOTrangBiDauTien(),
                    "Balo trong tran van hien Luu dan da dung");
            dichVu.guiBalo();
            bang(227, dichVu.layIdOTrangBiDauTien(),
                    "Balo kho that lam mat Luu dan con lai");
        } catch (Exception ex) {
            throw new AssertionError(
                    "khong kiem tra duoc packet Balo sau khi dung item", ex);
        }
        ChickenTrangThaiHanhDongLuot.ketThucLuot(chienBinh);
        dung(!chienBinh.coTheDungVatPhamChienTrongTran(snapshot),
                "Luu dan van dung duoc lan hai trong cung tran");
        dung(!chienBinh.chonVatPhamChienTrongTran(0),
                "doi stack/o Balo da vuot gioi han mot lan moi tran");
        bang(1, luuDan.soLuong,
                "lan dung Luu dan thu hai bi tu choi nhung van tru kho");

        dung(chienBinh.chonVatPhamChienTrongTran(1),
                "dung Luu dan lai khoa nham quota Bom B52");
        dung(nguoiChoi.tieuThuMotVatPhamChien(snapshotB52),
                "khong tru duoc Bom B52 sau khi server chap nhan");
        dung(chienBinh.danhDauDaDungVatPhamChienTrongTran(snapshotB52),
                "server khong danh dau lan dung Bom B52 dau tien");
        bang(300, chienBinh.layNapDanSauKhiDungVatPham(snapshotB52),
                "Bom B52 khong ap dung luat nap dan item 300");
        ChickenTrangThaiHanhDongLuot.ketThucLuot(chienBinh);
        dung(!chienBinh.chonVatPhamChienTrongTran(1),
                "Bom B52 van dung duoc lan hai trong cung tran");
        bang(1, bomB52.soLuong,
                "Bom B52 bi tru kho khi server tu choi lan hai");
        bang(2, nguoiChoi.soLanLuu,
                "hai item hop le khong ghi kho dung hai giao dich");

        dung(chienBinh.chonVatPhamChienTrongTran(2),
                "khong chon duoc To nhan o luot moi");
        bang(300, chienBinh.layNapDanSauKhiDungVatPham(snapshotToNhen),
                "To nhan khong ap dung nap dan 300");
        dung(nguoiChoi.tieuThuMotVatPhamChien(snapshotToNhen),
                "khong tru duoc To nhan sau khi server chap nhan");
        dung(chienBinh.danhDauDaDungVatPhamChienTrongTran(snapshotToNhen),
                "server khong danh dau lan dung To nhan dau tien");
        ChickenTrangThaiHanhDongLuot.ketThucLuot(chienBinh);
        dung(!chienBinh.chonVatPhamChienTrongTran(2),
                "To nhan van dung duoc lan hai trong cung tran");
        bang(1, toNhen.soLuong,
                "To nhan bi tru sai so luong hoac bi tru khi vuot quota");

        DichVuImLang dichVuLoi = new DichVuImLang();
        NguoiChoiKiemThu nguoiChoiLoi =
                new NguoiChoiKiemThu(dichVuLoi, false);
        dichVuLoi.datNguoiChoi(nguoiChoiLoi);
        nguoiChoiLoi.ma = 227_002;
        ChickenVatPham luuDanLoi = taoLuuDan(41, 1);
        nguoiChoiLoi.itemBag[41] = luuDanLoi;
        nguoiChoiLoi.itemBalo =
                new int[]{41, -1, -1, -1, -1};
        ChickenChienBinh chienBinhLoi =
                new ChickenChienBinh(
                        nguoiChoiLoi,
                        (byte) 0,
                        (short) 100,
                        (short) 300);
        ChickenChienBinh.VatPhamChienTrongTran snapshotLoi =
                chienBinhLoi.layVatPhamChienTrongOTrongBalo(0);
        dung(!nguoiChoiLoi.tieuThuMotVatPhamChien(snapshotLoi),
                "DB loi van bao tieu hao thanh cong");
        dung(luuDanLoi == nguoiChoiLoi.itemBag[41],
                "DB loi lam mat stack Luu dan trong RAM");
        bang(1, luuDanLoi.soLuong,
                "DB loi khong rollback so luong Luu dan");
        bang(41, nguoiChoiLoi.itemBalo[0],
                "DB loi khong rollback tham chieu Balo");
        dung(chienBinhLoi.coTheDungVatPhamChienTrongTran(snapshotLoi),
                "DB loi lai danh dau oan Luu dan da dung trong tran");
    }

    private static void kiemTraQuyDaoVaSatThuongLuuDan() {
        CauHinh luuDan =
                ChickenCongThucVatPhamChien.theoIdVatPham(227);
        ChickenCauHinhSatThuongVatPham.HoSo hoSoLuuDan =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(227);
        CauHinh bomB52 =
                ChickenCongThucVatPhamChien.theoIdVatPham(228);
        ChickenCauHinhSatThuongVatPham.HoSo hoSoB52 =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(228);
        CauHinh toNhen =
                ChickenCongThucVatPhamChien.theoIdVatPham(229);
        ChickenCauHinhSatThuongVatPham.HoSo hoSoToNhen =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(229);
        khacNull(hoSoLuuDan,
                "Luu dan thieu ho so damage runtime");
        bang(300, hoSoLuuDan.getNapDanSauDung(),
                "ho so Luu dan sai toc do nap dan");
        bang(1, hoSoLuuDan.getSoLanToiDaMoiTran(),
                "ho so Luu dan sai gioi han moi tran");
        khacNull(hoSoB52, "Bom B52 thieu ho so damage runtime");
        bang(115, hoSoB52.getPhanTramTanCong(),
                "ho so Bom B52 sai he so tan cong");
        bang(300, hoSoB52.getNapDanSauDung(),
                "ho so Bom B52 sai toc do nap dan");
        bang(1, hoSoB52.getSoLanToiDaMoiTran(),
                "ho so Bom B52 sai gioi han moi tran");
        bang(18, hoSoB52.getHoSoNo().getBanKinhDayDu(),
                "Bom B52 sai ban kinh damage day du");
        bang(78, hoSoB52.getHoSoNo().getBanKinhNo(),
                "Bom B52 sai ban kinh no");
        khacNull(hoSoToNhen, "To nhan thieu ho so runtime");
        bang(0, hoSoToNhen.getPhanTramTanCong(),
                "To nhan bi gan damage gia");
        dung(!hoSoToNhen.coSatThuongNo(),
                "To nhan bi xu ly nhu bom no");
        bang(300, hoSoToNhen.getNapDanSauDung(),
                "To nhan sai toc do nap dan");
        bang(1, hoSoToNhen.getSoLanToiDaMoiTran(),
                "To nhan sai gioi han moi tran");
        bang(ChickenCauHinhSatThuongVatPham.HieuUngDacBiet.TAO_MANG_NHEN,
                hoSoToNhen.getHieuUngDacBiet(),
                "To nhan thieu hieu ung dia hinh");
        khacNull(ChickenCauHinhSatThuongVatPham.theoIdVatPham(226),
                "Bom pha dat chua vao runtime");
        for (ChickenCauHinhSatThuongVatPham.HoSo hoSo
                : ChickenCauHinhSatThuongVatPham.layTatCa().values()) {
            if (hoSo.getHieuUngDacBiet()
                    == ChickenCauHinhSatThuongVatPham.HieuUngDacBiet
                            .HOI_MAU_DONG_DOI
                    || hoSo.getHieuUngDacBiet()
                            == ChickenCauHinhSatThuongVatPham
                                    .HieuUngDacBiet.HOI_MAU_BAN_THAN
                    || hoSo.getHieuUngDacBiet()
                            == ChickenCauHinhSatThuongVatPham
                                    .HieuUngDacBiet.NHAN_DOI_DI_CHUYEN
                    || hoSo.getHieuUngDacBiet()
                            == ChickenCauHinhSatThuongVatPham
                                    .HieuUngDacBiet.NGUNG_GIO) {
                bang(-1, hoSo.getNapDanSauDung(),
                        "item tuc thoi lai ghi de nap dan sung");
            } else {
                bang(ChickenCauHinhSatThuongVatPham
                                .NAP_DAN_VAT_PHAM_TAO_DAN,
                        hoSo.getNapDanSauDung(),
                        "item " + hoSo.getIdVatPham()
                        + " khong dung nap dan chung 300");
            }
        }

        ChickenQuanLyCongThucSung.KiemTraBanDo tuong =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() {
                        return 1_000;
                    }

                    @Override
                    public int getHeight() {
                        return 600;
                    }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return x >= 400;
                    }
                };
        ChickenQuanLyCongThucSung.KetQuaQuyDao quyDaoA =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 100, (short) 300, (short) 0, (byte) 30,
                        luuDan, (byte) 20, (byte) 0, tuong);
        ChickenQuanLyCongThucSung.KetQuaQuyDao quyDaoB =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 100, (short) 300, (short) 0, (byte) 30,
                        luuDan, (byte) 20, (byte) 0, tuong);
        ChickenQuanLyCongThucSung.KetQuaQuyDao khongGio =
                ChickenQuanLyCongThucSung.taoQuyDaoVatPham(
                        (short) 100, (short) 300, (short) 0, (byte) 30,
                        luuDan, (byte) 0, (byte) 0, tuong);
        dung(Arrays.equals(
                        quyDaoA.getHienThiX(),
                        quyDaoB.getHienThiX())
                && Arrays.equals(
                        quyDaoA.getHienThiY(),
                        quyDaoB.getHienThiY()),
                "quy dao Luu dan khong lap lai cung input");
        dung(!Arrays.equals(
                        quyDaoA.getHienThiX(),
                        khongGio.getHienThiX()),
                "he so gio Luu dan khong tac dong quy dao");

        ChickenChienBinh shooter = new ChickenChienBinh(
                (byte) 0, (short) 100, (short) 300,
                "Shooter", (short) 57, (byte) 0);
        shooter.tanCong = 1_000;
        ChickenChienBinh trungTrucTiep = new ChickenChienBinh(
                (byte) 1, (short) 370, (short) 330,
                "Direct", (short) 57, (byte) 0);
        ChickenChienBinh trungLan = new ChickenChienBinh(
                (byte) 2, (short) 395, (short) 330,
                "Splash", (short) 57, (byte) 0);
        ChickenChienBinh ngoaiBanKinh = new ChickenChienBinh(
                (byte) 3, (short) 600, (short) 330,
                "Far", (short) 57, (byte) 0);
        ChickenKetQuaDan ketQua = ChickenPhatBanVatPhamServer.tao(
                shooter,
                (short) 100,
                (short) 300,
                (short) 0,
                (byte) 30,
                luuDan,
                (byte) 0,
                (byte) 0,
                tuong,
                new ChickenChienBinh[]{
                    shooter, trungTrucTiep, trungLan, ngoaiBanKinh
                },
                new ChickenPhatBanServer.BoLocMucTieu() {
                    @Override
                    public boolean chapNhan(
                            ChickenChienBinh nguoiBan,
                            ChickenChienBinh mucTieu
                    ) {
                        return mucTieu != nguoiBan;
                    }
                }
        );
        khacNull(ketQua, "engine Luu dan khong tao ket qua");
        bang(7, ketQua.loaiDan & 255,
                "engine Luu dan gui sai bulletType");
        Integer damageTrucTiep =
                ketQua.satThuongTheoMucTieu.get(trungTrucTiep);
        Integer damageLan =
                ketQua.satThuongTheoMucTieu.get(trungLan);
        khacNull(damageTrucTiep,
                "Luu dan khong gay damage trung truc tiep");
        khacNull(damageLan,
                "Luu dan khong co damage no lan");
        dung(damageTrucTiep > damageLan && damageLan > 0,
                "damage Luu dan khong giam theo khoang cach");
        dung(!ketQua.satThuongTheoMucTieu
                        .containsKey(ngoaiBanKinh),
                "Luu dan gay damage ngoai ban kinh");

        shooter.mayMan = 1_000;
        ChickenMayMan.PhienTanCong mayManVatPham =
                ChickenMayMan.batDauChoKiemThu(
                        shooter,
                        new ChickenChienBinh[]{
                            shooter, trungTrucTiep, trungLan, ngoaiBanKinh
                        },
                        gioiHan -> gioiHan - 1);
        bang(damageTrucTiep * 2,
                mayManVatPham.apDung(trungTrucTiep, damageTrucTiep),
                "May man tan cong khong x2 damage Luu dan");
        shooter.mayMan = 0;

        ChickenKetQuaDan ketQuaB52 = ChickenPhatBanVatPhamServer.tao(
                shooter,
                (short) 100,
                (short) 300,
                (short) 0,
                (byte) 30,
                bomB52,
                (byte) 0,
                (byte) 0,
                tuong,
                new ChickenChienBinh[]{
                    shooter, trungTrucTiep, trungLan, ngoaiBanKinh
                },
                new ChickenPhatBanServer.BoLocMucTieu() {
                    @Override
                    public boolean chapNhan(
                            ChickenChienBinh nguoiBan,
                            ChickenChienBinh mucTieu
                    ) {
                        return mucTieu != nguoiBan;
                    }
                }
        );
        khacNull(ketQuaB52, "engine Bom B52 khong tao ket qua");
        bang(4, ketQuaB52.loaiDan & 255,
                "engine Bom B52 gui sai bulletType");
        bang(2, ketQuaB52.cacDuongX.length,
                "Bom B52 thieu duong danh dau hoac duong bom roi");
        bang(2, ketQuaB52.cacDuongY.length,
                "Bom B52 lech so duong X/Y");
        dung(ketQuaB52.cacDuongX[1].length > 1
                        && ketQuaB52.cacDuongY[1].length > 1,
                "duong bom B52 rong lam client createBullet bi treo");
        dung(ketQuaB52.cacDuongY[1][0]
                        < ketQuaB52.cacDuongY[1][
                                ketQuaB52.cacDuongY[1].length - 1],
                "bom B52 khong roi tu may bay xuong muc tieu");
        khacNull(ketQuaB52.satThuongTheoMucTieu.get(trungTrucTiep),
                "Bom B52 khong gay damage trung truc tiep");
        khacNull(ketQuaB52.satThuongTheoMucTieu.get(trungLan),
                "Bom B52 khong co damage no lan");
        dung(!ketQuaB52.satThuongTheoMucTieu.containsKey(ngoaiBanKinh),
                "Bom B52 gay damage ngoai ban kinh");

        ChickenKetQuaDan ketQuaToNhen = ChickenPhatBanVatPhamServer.tao(
                shooter,
                (short) 100,
                (short) 300,
                (short) 0,
                (byte) 30,
                toNhen,
                (byte) 0,
                (byte) 0,
                tuong,
                new ChickenChienBinh[]{shooter},
                new ChickenPhatBanServer.BoLocMucTieu() {
                    @Override
                    public boolean chapNhan(
                            ChickenChienBinh nguoiBan,
                            ChickenChienBinh mucTieu
                    ) {
                        return mucTieu != nguoiBan;
                    }
                }
        );
        khacNull(ketQuaToNhen, "engine To nhan khong tao ket qua");
        bang(8, ketQuaToNhen.loaiDan & 255,
                "engine To nhan gui sai bulletType");
        dung(ketQuaToNhen.satThuongTheoMucTieu.isEmpty(),
                "To nhan gay damage ngoai mo ta item");
        kiemTraMangNhenServer(ketQuaToNhen);

        int chiSoDiemGhim = Math.max(
                1, Math.min(ketQuaToNhen.duongX.length - 2, 5));
        ChickenChienBinh nguoiBiGhim = new ChickenChienBinh(
                (byte) 4,
                ketQuaToNhen.duongX[chiSoDiemGhim],
                (short) (ketQuaToNhen.duongY[chiSoDiemGhim] + 36),
                "WebTarget", (short) 57, (byte) 0);
        ChickenKetQuaDan ketQuaGhimNguoi =
                ChickenPhatBanVatPhamServer.tao(
                        shooter,
                        (short) 100,
                        (short) 300,
                        (short) 0,
                        (byte) 30,
                        toNhen,
                        (byte) 0,
                        (byte) 0,
                        tuong,
                        new ChickenChienBinh[]{shooter, nguoiBiGhim},
                        new ChickenPhatBanServer.BoLocMucTieu() {
                            @Override
                            public boolean chapNhan(
                                    ChickenChienBinh nguoiBan,
                                    ChickenChienBinh mucTieu
                            ) {
                                return mucTieu != nguoiBan;
                            }
                        }
                );
        dung(ketQuaGhimNguoi.duongX.length
                        < ketQuaToNhen.duongX.length,
                "To nhan khong dung tai hitbox nhan vat");
        bang(ketQuaGhimNguoi.duongX.length,
                ketQuaGhimNguoi.duongY.length,
                "To nhan lech so diem X/Y khi trung nhan vat");
        int diemCuoiCoNguoi = ketQuaGhimNguoi.duongX.length - 1;
        dung(Math.abs(ketQuaGhimNguoi.duongX[diemCuoiCoNguoi]
                        - nguoiBiGhim.x) <= 12,
                "To nhan dung ngoai hitbox ngang cua muc tieu");
        dung(ketQuaGhimNguoi.duongY[diemCuoiCoNguoi]
                        < nguoiBiGhim.y,
                "To nhan bi keo xuong chan va day nguoi xuyen map");
        dung(ketQuaGhimNguoi.satThuongTheoMucTieu.isEmpty(),
                "To nhan trung nhan vat lai gay damage");

        ChickenChienBinh ironManBay = new ChickenChienBinh(
                (byte) 4, nguoiBiGhim.x, nguoiBiGhim.y,
                "IronManWebImmune", (short) 223, (byte) 1);
        ChickenChienBinh ultronBay = new ChickenChienBinh(
                (byte) 5, nguoiBiGhim.x, nguoiBiGhim.y,
                "UltronWebImmune", (short) 312, (byte) 8);
        ChickenChienBinh bossBay = new ChickenChienBinh(
                (byte) 8, -55, nguoiBiGhim.x, nguoiBiGhim.y,
                "FlyingBossWebHitNoRoot", (short) -1, 10_000, 100, 0);
        for (ChickenChienBinh mucTieuBay
                : new ChickenChienBinh[]{ironManBay, ultronBay, bossBay}) {
            ChickenKetQuaDan ketQuaBay = ChickenPhatBanVatPhamServer.tao(
                    shooter,
                    (short) 100,
                    (short) 300,
                    (short) 0,
                    (byte) 30,
                    toNhen,
                    (byte) 0,
                    (byte) 0,
                    tuong,
                    new ChickenChienBinh[]{shooter, mucTieuBay},
                    new ChickenPhatBanServer.BoLocMucTieu() {
                        @Override
                        public boolean chapNhan(
                                ChickenChienBinh nguoiBan,
                                ChickenChienBinh mucTieu
                        ) {
                            return mucTieu != nguoiBan;
                        }
                    }
            );
            bang(ketQuaGhimNguoi.duongX.length, ketQuaBay.duongX.length,
                    "To nhen khong dung tai hitbox thuc the bay "
                            + mucTieuBay.ten);
            dung(ketQuaBay.duongX.length < ketQuaToNhen.duongX.length,
                    "To nhen xuyen qua hitbox thuc the bay "
                            + mucTieuBay.ten);
            int diemCuoiBay = ketQuaBay.duongX.length - 1;
            dung(Math.abs(ketQuaBay.duongX[diemCuoiBay]
                            - mucTieuBay.x) <= 12,
                    "Diem tao To nhen lech hitbox thuc the bay "
                            + mucTieuBay.ten);
            dung(ketQuaBay.satThuongTheoMucTieu.isEmpty(),
                    "To nhen gay damage len thuc the bay " + mucTieuBay.ten);
        }
    }

    private static void kiemTraMangNhenServer(
            ChickenKetQuaDan ketQuaToNhen
    ) {
        DichVuImLang dichVu = new DichVuImLang();
        NguoiChoiKiemThu nguoiChoi =
                new NguoiChoiKiemThu(dichVu, true);
        dichVu.datNguoiChoi(nguoiChoi);
        ChickenVatPham toNhen = taoToNhen(19, 1);
        nguoiChoi.itemBag[19] = toNhen;
        nguoiChoi.itemBalo = new int[]{19, -1, -1, -1, -1};
        ChickenChienBinh chienBinh = new ChickenChienBinh(
                nguoiChoi, (byte) 0, (short) 100, (short) 300);
        ChickenChienBinh.VatPhamChienTrongTran snapshot =
                chienBinh.layVatPhamChienTrongOTrongBalo(0);
        ChickenQuanLyBanDo banDo = new ChickenQuanLyBanDo(-1);
        dung(ChickenHieuUngVatPhamChien.coTheApDung(
                        snapshot, ketQuaToNhen, banDo),
                "server tu choi diem roi To nhan hop le");
        dung(ChickenHieuUngVatPhamChien.apDung(
                        snapshot, ketQuaToNhen, banDo),
                "server khong them duoc mang nhan");

        int soDiem = Math.min(
                ketQuaToNhen.duongX.length,
                ketQuaToNhen.duongY.length);
        int x = ketQuaToNhen.duongX[soDiem - 1];
        int y = ketQuaToNhen.duongY[soDiem - 1];
        int soPixelVaCham = demVaCham(
                banDo, x - 21, y - 20, 41, 35);
        dung(soPixelVaCham > 0,
                "mang nhan server khong tao va cham theo anh client");
        dung(banDo.coMangNhenTrongVung(x - 58, y - 108, x + 58, y),
                "server khong nhan hitbox Rua dang nam trong mang nhen");

        ChickenChienBinh rua = new ChickenChienBinh(
                (byte) 8, -54, (short) x, (short) y,
                "RuaWebPinned", (short) 1563, 10_000, 100, 0);
        ChickenChienBinh mucTieuRua = new ChickenChienBinh(
                (byte) 0, (short) (x + 200), (short) y,
                "RuaWebTarget", (short) 57, (byte) 0);
        short[] buocRua = DiChuyenBossRua.tinhBuocTiepTheo(
                rua, mucTieuRua, DiChuyenBossRua.QUANG_DUONG_MOI_LUOT,
                1, banDo);
        bang(rua.x, buocRua[0],
                "Rua bi To nhen van nhan dich di chuyen X");
        bang(rua.y, buocRua[1],
                "Rua bi To nhen van nhan dich di chuyen Y");

        dung(DiChuyenBossRong.laVungBayTrong(x, y, banDo),
                "Rong bay van coi To nhen la tuong");
        ChickenDiChuyenServer.KetQua bayQuaMang =
                ChickenDiChuyenServer.xuLy(
                        banDo, (short) (x - 40), (short) y,
                        (short) (x + 40), (short) y,
                        100, true);
        bang(x + 40, (int) bayQuaMang.getX(),
                "AVG bay van bi To nhen chan tren server");
        banDo.phaDiaHinh(x, y, (byte) 8);
        bang(soPixelVaCham,
                demVaCham(banDo, x - 21, y - 20, 41, 35),
                "dan To nhan tu pha luoi vua tao");
        banDo.phaDiaHinh(x, y, (byte) 3);
        dung(demVaCham(banDo, x - 21, y - 20, 41, 35)
                        < soPixelVaCham,
                "mang nhan server khong bi dan khac pha");

        ChickenKetQuaDan ngoaiBanDo = new ChickenKetQuaDan(
                (byte) 8, (short) 0, (short) 0, (short) 0, (byte) 1,
                new short[]{-10}, new short[]{100}, null, 0);
        dung(ChickenHieuUngVatPhamChien.coTheApDung(
                        snapshot, ngoaiBanDo, banDo),
                "To nhan truot ngoai map bi tu choi va khoa input client");
        dung(ChickenHieuUngVatPhamChien.apDung(
                        snapshot, ngoaiBanDo, banDo),
                "To nhan truot ngoai map khong ket thuc duoc phat ban");
    }

    private static int demVaCham(
            ChickenQuanLyBanDo banDo,
            int trai,
            int tren,
            int rong,
            int cao
    ) {
        int dem = 0;
        for (int y = tren; y < tren + cao; y++) {
            for (int x = trai; x < trai + rong; x++) {
                if (banDo.coVaCham((short) x, (short) y)) {
                    dem++;
                }
            }
        }
        return dem;
    }

    private static ChickenTinNhan taoPacketBan(
            int loaiDan,
            int x,
            int y,
            int goc,
            int luc,
            int soPhat
    ) {
        try {
            ChickenTinNhan packet = new ChickenTinNhan(22);
            DataOutputStream out = packet.boGhi();
            out.writeByte(loaiDan);
            out.writeShort(x);
            out.writeShort(y);
            out.writeShort(goc);
            out.writeByte(luc);
            out.writeByte(soPhat);
            out.flush();
            return packet;
        } catch (Exception ex) {
            throw new AssertionError("khong tao duoc packet test", ex);
        }
    }

    private static ChickenVatPham taoLuuDan(int chiSo, int soLuong) {
        ChickenVatPham item = new ChickenVatPham(227);
        item.ma = 227;
        item.mau = new ChickenMauVatPham(
                (short) 227, (byte) 10, (byte) 7,
                "Luu dan", "", (byte) 1, 0,
                (short) 927, (short) 0, false);
        item.chiSo = chiSo;
        item.soLuong = soLuong;
        return item;
    }

    private static ChickenVatPham taoDichChuyenTucThoi(
            int chiSo, int soLuong
    ) {
        ChickenVatPham item = new ChickenVatPham(221);
        item.ma = 221;
        item.mau = new ChickenMauVatPham(
                (short) 221, (byte) 10, (byte) 1,
                "Dich chuyen tuc thoi", "", (byte) 1, 0,
                (short) 970, (short) 0, false);
        item.chiSo = chiSo;
        item.soLuong = soLuong;
        return item;
    }

    private static ChickenVatPham taoDiChuyenX2(int chiSo, int soLuong) {
        ChickenVatPham item = new ChickenVatPham(223);
        item.ma = 223;
        item.mau = new ChickenMauVatPham(
                (short) 223, (byte) 10, (byte) 3,
                "Di chuyen x2", "", (byte) 1, 0,
                (short) 972, (short) 0, false);
        item.chiSo = chiSo;
        item.soLuong = soLuong;
        return item;
    }

    private static ChickenVatPham taoNgungGio(int chiSo, int soLuong) {
        ChickenVatPham item = new ChickenVatPham(225);
        item.ma = 225;
        item.mau = new ChickenMauVatPham(
                (short) 225, (byte) 10, (byte) 5,
                "Ngung gio", "", (byte) 1, 0,
                (short) 974, (short) 0, false);
        item.chiSo = chiSo;
        item.soLuong = soLuong;
        return item;
    }

    private static ChickenVatPham taoCuuThuongBanThan(
            int chiSo, int soLuong
    ) {
        ChickenVatPham item = new ChickenVatPham(220);
        item.ma = 220;
        item.mau = new ChickenMauVatPham(
                (short) 220, (byte) 10, (byte) 0,
                "Cuu thuong", "", (byte) 1, 0,
                (short) 969, (short) 0, false);
        item.chiSo = chiSo;
        item.soLuong = soLuong;
        return item;
    }

    private static ChickenVatPham taoBomB52(int chiSo, int soLuong) {
        ChickenVatPham item = new ChickenVatPham(228);
        item.ma = 228;
        item.mau = new ChickenMauVatPham(
                (short) 228, (byte) 10, (byte) 8,
                "Bom B52", "", (byte) 1, 0,
                (short) 977, (short) 0, false);
        item.chiSo = chiSo;
        item.soLuong = soLuong;
        return item;
    }

    private static ChickenVatPham taoToNhen(int chiSo, int soLuong) {
        ChickenVatPham item = new ChickenVatPham(229);
        item.ma = 229;
        item.mau = new ChickenMauVatPham(
                (short) 229, (byte) 10, (byte) 9,
                "To nhan", "", (byte) 1, 0,
                (short) 978, (short) 0, false);
        item.chiSo = chiSo;
        item.soLuong = soLuong;
        return item;
    }

    private static final class NguoiChoiKiemThu
            extends ChickenNguoiChoi {
        private final boolean choLuu;
        private int soLanLuu;

        private NguoiChoiKiemThu(
                ChickenDichVuGame dichVu,
                boolean choLuu
        ) {
            super(dichVu);
            this.choLuu = choLuu;
        }

        @Override
        protected boolean luuKhoVatPhamCoKetQua() {
            this.soLanLuu++;
            return this.choLuu;
        }
    }

    private static final class DichVuImLang extends ChickenDichVuGame {
        private ChickenTinNhan baloCuoi;

        private DichVuImLang() {
            super(null);
        }

        @Override
        public void guiTin(ChickenTinNhan tinNhan) {
            if (tinNhan != null && tinNhan.layLenh() == (byte) -42) {
                this.baloCuoi = tinNhan;
            }
        }

        private int layIdOTrangBiDauTien() throws Exception {
            khacNull(this.baloCuoi, "server khong gui CMD -42");
            DataInputStream in = new DataInputStream(
                    new java.io.ByteArrayInputStream(
                            this.baloCuoi.layDuLieu()));
            bang(0, in.readUnsignedByte(), "CMD -42 sai che do Balo");
            dung(in.readUnsignedByte() > 0, "CMD -42 gui Balo rong");
            return in.readShort();
        }
    }

    private static void khacNull(Object giaTri, String thongBao) {
        if (giaTri == null) {
            throw new AssertionError(thongBao);
        }
    }

    private static void dung(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private static void bang(
            Object mongDoi,
            Object thucTe,
            String thongBao
    ) {
        if (mongDoi == null ? thucTe != null : !mongDoi.equals(thucTe)) {
            throw new AssertionError(
                    thongBao + ": expected=" + mongDoi
                    + " actual=" + thucTe);
        }
    }
}
