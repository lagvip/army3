package com.chicken.kiemthu;

import com.chicken.chien.ChickenCongThucVatPhamChien;
import com.chicken.chien.ChickenCongThucVatPhamChien.CauHinh;
import com.chicken.chien.ChickenCongThucVatPhamChien.KieuGoc;
import com.chicken.chien.ChickenCongThucVatPhamChien.KieuQuyDao;
import com.chicken.chien.ChickenCauHinhSatThuongVatPham;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.chien.ChickenPhatBanServer;
import com.chicken.chien.ChickenPhatBanVatPhamServer;
import com.chicken.chien.ChickenQuanLyCongThucSung;
import com.chicken.chien.ChickenYeuCauBanServer;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenVatPham;

import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** Doi chieu bang vat pham chien dau server voi CPlayer cua client goc. */
public final class ChickenCongThucVatPhamChienTestSupport {

    private ChickenCongThucVatPhamChienTestSupport() {
    }

    public static void tuKiemTra() {
        // id, itemUsed, bulletType, coHeSoCoDinh, heSoGio, trongLuc.
        int[][] mongDoi = {
            {221, 1, 5, 1, 0, 80},
            {222, 2, -1, 0, 0, 0},
            {226, 6, 6, 1, 70, 90},
            {227, 7, 7, 1, 70, 80},
            {228, 8, 4, 1, 0, 80},
            {229, 9, 8, 1, 70, 70},
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

        System.out.println(
                "ITEM_AIM_MATRIX_OK mappings=" + mongDoi.length
                + " shopAimedItems=" + idDangBanCoDuongNgam.length
                + " duplicateAction29=2 grenadeRuntime=ok");
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
    }

    private static void kiemTraSnapshotVaTieuHaoLuuDan() {
        DichVuImLang dichVu = new DichVuImLang();
        NguoiChoiKiemThu nguoiChoi =
                new NguoiChoiKiemThu(dichVu, true);
        dichVu.datNguoiChoi(nguoiChoi);
        nguoiChoi.ma = 227_001;
        nguoiChoi.ten = "GrenadeInventoryTest";
        ChickenVatPham luuDan = taoLuuDan(17, 2);
        nguoiChoi.itemBag[17] = luuDan;
        nguoiChoi.itemBalo = new int[]{17, -1, -1, -1, -1};

        ChickenChienBinh chienBinh =
                new ChickenChienBinh(
                        nguoiChoi, (byte) 0, (short) 100, (short) 300);
        ChickenChienBinh.VatPhamChienTrongTran snapshot =
                chienBinh.layVatPhamChienTrongOTrongBalo(0);
        khacNull(snapshot,
                "snapshot khong khoa Luu dan theo o hien thi Balo");
        dung(chienBinh.layVatPhamChienTrongOTrongBalo(17) == null,
                "snapshot dung index itemBag thay vi o Balo");
        dung(chienBinh.chonVatPhamChienTrongTran(0),
                "khong chon duoc Luu dan da trang bi Balo");
        bang(2, luuDan.soLuong,
                "chon item da tru kho truoc khi ban");
        dung(nguoiChoi.tieuThuMotVatPhamChien(snapshot),
                "khong tru duoc Luu dan sau khi server chap nhan");
        bang(1, luuDan.soLuong,
                "moi phat khong tru dung mot Luu dan");
        bang(1, nguoiChoi.soLanLuu,
                "tieu hao Luu dan khong ghi kho dung mot lan");

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
    }

    private static void kiemTraQuyDaoVaSatThuongLuuDan() {
        CauHinh luuDan =
                ChickenCongThucVatPhamChien.theoIdVatPham(227);
        khacNull(ChickenCauHinhSatThuongVatPham
                        .theoIdVatPham(227),
                "Luu dan thieu ho so damage runtime");
        dung(ChickenCauHinhSatThuongVatPham
                        .theoIdVatPham(226) == null,
                "item chua lam damage lai tu dong vao runtime");

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
        private DichVuImLang() {
            super(null);
        }

        @Override
        public void guiTin(ChickenTinNhan tinNhan) {
            // Test chi can state authoritative cua server.
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
