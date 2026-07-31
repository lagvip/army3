package com.chicken.mohinh;

import com.chicken.cuahang.ChickenCuaHang;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenPhien;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mang.ChickenXuLyTin;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenVatPham;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Ma tran hoi quy shop server-authoritative:
 * packet, danh muc, tien, tui, ban vat pham, rollback va tranh chap.
 */
public final class ChickenCuaHangTestSupport {
    private static final int STACK_GOLD = 32_000;
    private static final int STACK_GEM = 32_001;
    private static final int EQUIP_GOLD = 32_002;
    private static final int UNSOLD = 32_003;
    private static final int OVERFLOW_PRICE = 32_004;
    private static final int BOTH_CURRENCIES = 32_005;
    private static final int FREE_ITEM = 32_006;
    private static final int SO_TO_HOP_PACKET_MUA = 65_536;
    private static final int SO_ID_VAT_PHAM_WIRE = 65_536;
    private static final int SO_TO_HOP_PACKET_BAN = 65_536;

    private ChickenCuaHangTestSupport() {
    }

    public static void tuKiemTra() throws Exception {
        Map<Integer, ChickenMauVatPham> banSaoMau = new HashMap<>();
        int soTabItemCu = ChickenCuaHang.SHOP_ITEM.tabs.size();
        int soTenTabItemCu =
                ChickenCuaHang.SHOP_ITEM.shopTabName.size();
        int soTabEquipCu = ChickenCuaHang.SHOP_EQUIP.tabs.size();
        int soTenTabEquipCu =
                ChickenCuaHang.SHOP_EQUIP.shopTabName.size();
        for (int ma = STACK_GOLD; ma <= FREE_ITEM; ma++) {
            banSaoMau.put(
                    ma, ChickenQuanLyMayChu.itemTemplates.get(ma));
        }

        try {
            ChickenMauVatPham stackGold =
                    mau(STACK_GOLD, 6, 100, 0);
            ChickenMauVatPham stackGem =
                    mau(STACK_GEM, 10, 0, 4);
            ChickenMauVatPham equipGold =
                    mau(EQUIP_GOLD, 5, 200, 0);
            ChickenMauVatPham unsold =
                    mau(UNSOLD, 6, 50, 0);
            ChickenMauVatPham overflow =
                    mau(OVERFLOW_PRICE, 6, Integer.MAX_VALUE, 0);
            ChickenMauVatPham both =
                    mau(BOTH_CURRENCIES, 6, 10, 2);
            ChickenMauVatPham free =
                    mau(FREE_ITEM, 6, 0, 0);
            for (ChickenMauVatPham mau : List.of(
                    stackGold, stackGem, equipGold, unsold,
                    overflow, both, free)) {
                ChickenQuanLyMayChu.itemTemplates.put(
                        mau.ma & 0xFFFF, mau);
            }

            ChickenCuaHang.SHOP_ITEM.themTab(
                    "Shop test item",
                    new ArrayList<>(List.of(
                            stackGold, stackGem, overflow, both)));
            ChickenCuaHang.SHOP_EQUIP.themTab(
                    "Shop test equip",
                    new ArrayList<>(List.of(equipGold)));

            kiemTraPhanTrangVaDanhMuc();
            kiemTraToanBoDoDaiVaGiaTriPacketMua();
            kiemTraMuaBangVangVaNgoc();
            kiemTraXepChongVaSucChuaTui();
            kiemTraGiaTienVaQuyenMua();
            kiemTraRollbackVaTrangThaiChienDau();
            kiemTraMuaDongThoi();
            kiemTraMoShopVaChuyenTrang();
            kiemTraBanVatPham();
            kiemTraBanDongThoiVaRollback();
            kiemTraRouterShopThat();

            System.out.println(
                    "SHOP_MATRIX_OK buyPacketCombinations="
                    + SO_TO_HOP_PACKET_MUA
                    + " itemIds=" + SO_ID_VAT_PHAM_WIRE
                    + " sellPacketCombinations="
                    + SO_TO_HOP_PACKET_BAN
                    + " currencies=256 quantities=256"
                    + " concurrentBuy=32 concurrentSell=32");
        } finally {
            catDanhSach(
                    ChickenCuaHang.SHOP_ITEM.tabs, soTabItemCu);
            catDanhSach(
                    ChickenCuaHang.SHOP_ITEM.shopTabName,
                    soTenTabItemCu);
            catDanhSach(
                    ChickenCuaHang.SHOP_EQUIP.tabs, soTabEquipCu);
            catDanhSach(
                    ChickenCuaHang.SHOP_EQUIP.shopTabName,
                    soTenTabEquipCu);
            for (Map.Entry<Integer, ChickenMauVatPham> entry
                    : banSaoMau.entrySet()) {
                if (entry.getValue() == null) {
                    ChickenQuanLyMayChu.itemTemplates.remove(
                            entry.getKey());
                } else {
                    ChickenQuanLyMayChu.itemTemplates.put(
                            entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private static void kiemTraPhanTrangVaDanhMuc() {
        ChickenCuaHang rong = new ChickenCuaHang();
        rong.themTab("Rong", new ArrayList<>());
        dung(rong.tabs.size() == 1
                        && rong.tabs.get(0).size() == 1
                        && rong.tabs.get(0).get(0).vatPhams.isEmpty(),
                "tab rong khong co trang 0 an toan");

        ArrayList<ChickenMauVatPham> haiMuoiMot =
                new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            haiMuoiMot.add(mau(10_000 + i, 6, i + 1, 0));
        }
        ChickenCuaHang phanTrang = new ChickenCuaHang();
        phanTrang.themTab("21", haiMuoiMot);
        dung(phanTrang.tabs.get(0).size() == 2,
                "21 item khong tach thanh hai trang");
        dung(phanTrang.tabs.get(0).get(0).vatPhams.size() == 20,
                "trang dau shop khong dung 20 item");
        dung(phanTrang.tabs.get(0).get(1).vatPhams.size() == 1,
                "trang hai shop khong dung mot item");
        dung(phanTrang.coBanVatPham(10_000, (byte) 0),
                "danh muc khong nhan item vang");
        dung(!phanTrang.coBanVatPham(10_000, (byte) 1),
                "danh muc cho mua sai loai tien");
        dung(!phanTrang.coBanVatPham(65_535, (byte) 0),
                "danh muc nhan ID khong ton tai");
        for (int loai = Byte.MIN_VALUE;
                loai <= Byte.MAX_VALUE; loai++) {
            boolean mongDoi = loai == 0;
            dung(phanTrang.coBanVatPham(
                            10_000, (byte) loai) == mongDoi,
                    "phan loai tien shop sai loai=" + loai);
        }
    }

    private static void kiemTraToanBoDoDaiVaGiaTriPacketMua()
            throws Exception {
        for (int doDai = 0; doDai <= 255; doDai++) {
            if (doDai == 4) {
                continue;
            }
            BoTest bo = boTest();
            bo.nguoiChoi.vang = 10_000;
            bo.nguoiChoi.yeuCauMuaVatPham(new ChickenTinNhan(
                    (byte) 72, new byte[doDai]));
            dung(bo.nguoiChoi.vang == 10_000
                            && demVatPham(bo.nguoiChoi) == 0,
                    "packet mua sai do dai van sua kinh te len="
                            + doDai);
            dung(bo.dichVu.coLenh(10),
                    "packet mua sai do dai khong dong InfoDlg len="
                            + doDai);
        }

        int soToHop = 0;
        for (int loai = Byte.MIN_VALUE;
                loai <= Byte.MAX_VALUE; loai++) {
            for (int soLuong = 0; soLuong <= 255; soLuong++) {
                BoTest bo = boTest();
                /*
                 * Dung ID khong nam trong shop de toan bo ma tran chi test
                 * parser/range, khong tao giao dich that.
                 */
                bo.nguoiChoi.vang = Integer.MAX_VALUE;
                bo.nguoiChoi.ngoc = Integer.MAX_VALUE;
                bo.nguoiChoi.yeuCauMuaVatPham(
                        tinMua((byte) loai, UNSOLD, soLuong));
                dung(bo.nguoiChoi.vang == Integer.MAX_VALUE
                                && bo.nguoiChoi.ngoc
                                        == Integer.MAX_VALUE
                                && demVatPham(bo.nguoiChoi) == 0,
                        "ma tran packet mua sua kinh te loai="
                                + loai + " quantity=" + soLuong);
                soToHop++;
            }
        }
        dung(soToHop == SO_TO_HOP_PACKET_MUA,
                "chua quet du loai tien x so luong");

        BoTest idWire = boTest();
        idWire.nguoiChoi.vang = Integer.MAX_VALUE;
        idWire.nguoiChoi.ngoc = Integer.MAX_VALUE;
        int soId = 0;
        for (int ma = 0; ma <= 65_535; ma++) {
            idWire.dichVu.xoaPacket();
            idWire.nguoiChoi.yeuCauMuaVatPham(
                    tinMua((byte) -1, ma, 1));
            dung(idWire.nguoiChoi.vang == Integer.MAX_VALUE
                            && idWire.nguoiChoi.ngoc
                                    == Integer.MAX_VALUE
                            && demVatPham(idWire.nguoiChoi) == 0
                            && idWire.nguoiChoi.soLanLuu == 0,
                    "ID wire mua ngoai quyen sua kinh te id=" + ma);
            soId++;
        }
        dung(soId == SO_ID_VAT_PHAM_WIRE,
                "chua quet du 65536 ID vat pham tren wire");
    }

    private static void kiemTraMuaBangVangVaNgoc()
            throws Exception {
        BoTest vang = boTest();
        vang.nguoiChoi.vang = 1_000;
        vang.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, STACK_GOLD, 3));
        dung(vang.nguoiChoi.vang == 700,
                "server khong tu tinh tong gia vang");
        dung(vang.nguoiChoi.itemBag[0] != null
                        && vang.nguoiChoi.itemBag[0].soLuong == 3,
                "mua stack vang khong dung");
        dung(vang.nguoiChoi.soLanLuu == 1,
                "mua vang khong commit dung mot lan");
        dung(vang.dichVu.coLenh(105)
                        && vang.dichVu.coLenh(-35)
                        && vang.dichVu.coLenh(45),
                "mua thanh cong khong cap nhat UI");

        BoTest ngoc = boTest();
        ngoc.nguoiChoi.ngoc = 20;
        ngoc.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 1, STACK_GEM, 4));
        dung(ngoc.nguoiChoi.ngoc == 4
                        && ngoc.nguoiChoi.itemBag[0].soLuong == 4,
                "mua bang ngoc sai tien hoac so luong");

        BoTest trangBi = boTest();
        trangBi.nguoiChoi.vang = 1_000;
        trangBi.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, EQUIP_GOLD, 3));
        dung(trangBi.nguoiChoi.vang == 400,
                "mua trang bi khong tinh dung gia");
        for (int i = 0; i < 3; i++) {
            dung(trangBi.nguoiChoi.itemBag[i] != null
                            && trangBi.nguoiChoi.itemBag[i].ma
                                    == EQUIP_GOLD
                            && trangBi.nguoiChoi.itemBag[i].soLuong == 1
                            && trangBi.nguoiChoi.itemBag[i].chiSo == i,
                    "trang bi mua nhieu khong tach o i=" + i);
        }
    }

    private static void kiemTraXepChongVaSucChuaTui()
            throws Exception {
        BoTest chong = boTest();
        chong.nguoiChoi.vang = 10_000;
        chong.nguoiChoi.itemBag[7] =
                vatPham(STACK_GOLD, 90, 7);
        chong.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, STACK_GOLD, 9));
        dung(chong.nguoiChoi.itemBag[7].soLuong == 99,
                "khong cong vao stack dang co");
        int vangTruoc = chong.nguoiChoi.vang;
        int soLanLuuTruoc = chong.nguoiChoi.soLanLuu;
        chong.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, STACK_GOLD, 1));
        dung(chong.nguoiChoi.itemBag[7].soLuong == 99
                        && chong.nguoiChoi.vang == vangTruoc
                        && chong.nguoiChoi.soLanLuu
                                == soLanLuuTruoc,
                "stack vuot 99 van tru tien/commit");

        BoTest dayNhungCoStack = boTest();
        dayNhungCoStack.nguoiChoi.vang = 10_000;
        lapDayTui(dayNhungCoStack.nguoiChoi, EQUIP_GOLD);
        dayNhungCoStack.nguoiChoi.itemBag[50] =
                vatPham(STACK_GOLD, 1, 50);
        dayNhungCoStack.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, STACK_GOLD, 1));
        dung(dayNhungCoStack.nguoiChoi.itemBag[50].soLuong == 2,
                "tui day khong cho cong stack co san");

        BoTest day = boTest();
        day.nguoiChoi.vang = 10_000;
        lapDayTui(day.nguoiChoi, EQUIP_GOLD);
        int vangDay = day.nguoiChoi.vang;
        day.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, STACK_GOLD, 1));
        dung(day.nguoiChoi.vang == vangDay
                        && day.nguoiChoi.soLanLuu == 0,
                "tui day van tru tien");
    }

    private static void kiemTraGiaTienVaQuyenMua()
            throws Exception {
        BoTest duTien = boTest();
        duTien.nguoiChoi.vang = 100;
        duTien.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, STACK_GOLD, 1));
        dung(duTien.nguoiChoi.vang == 0
                        && demVatPham(duTien.nguoiChoi) == 1,
                "so du bang dung gia khong mua duoc");

        BoTest thieuTien = boTest();
        thieuTien.nguoiChoi.vang = 99;
        thieuTien.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, STACK_GOLD, 1));
        dung(thieuTien.nguoiChoi.vang == 99
                        && demVatPham(thieuTien.nguoiChoi) == 0,
                "thieu mot vang van mua duoc");

        BoTest saiTien = boTest();
        saiTien.nguoiChoi.ngoc = 1_000;
        saiTien.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 1, STACK_GOLD, 1));
        dung(saiTien.nguoiChoi.ngoc == 1_000
                        && demVatPham(saiTien.nguoiChoi) == 0,
                "client ep item vang mua bang ngoc");

        BoTest khongBan = boTest();
        khongBan.nguoiChoi.vang = 1_000;
        khongBan.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, UNSOLD, 1));
        dung(khongBan.nguoiChoi.vang == 1_000
                        && demVatPham(khongBan.nguoiChoi) == 0,
                "client mua duoc template ngoai shop");

        BoTest tranGia = boTest();
        tranGia.nguoiChoi.vang = Integer.MAX_VALUE;
        tranGia.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, OVERFLOW_PRICE, 2));
        dung(tranGia.nguoiChoi.vang == Integer.MAX_VALUE
                        && demVatPham(tranGia.nguoiChoi) == 0,
                "tong gia vuot int van giao dich");

        BoTest haiLoaiTien = boTest();
        haiLoaiTien.nguoiChoi.vang = 10;
        haiLoaiTien.nguoiChoi.ngoc = 2;
        haiLoaiTien.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 1, BOTH_CURRENCIES, 1));
        dung(haiLoaiTien.nguoiChoi.vang == 10
                        && haiLoaiTien.nguoiChoi.ngoc == 0,
                "item hai gia khong dung loai tien client chon");
    }

    private static void kiemTraRollbackVaTrangThaiChienDau()
            throws Exception {
        BoTest loiLuu = boTest();
        loiLuu.nguoiChoi.vang = 1_000;
        loiLuu.nguoiChoi.choPhepLuu = false;
        loiLuu.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, STACK_GOLD, 2));
        dung(loiLuu.nguoiChoi.vang == 1_000
                        && demVatPham(loiLuu.nguoiChoi) == 0,
                "loi DB khong rollback mua item moi");
        dung(!loiLuu.dichVu.coLenh(45)
                        && loiLuu.dichVu.coLenh(10),
                "loi DB van gui thong bao mua thanh cong");

        BoTest loiStack = boTest();
        loiStack.nguoiChoi.vang = 1_000;
        loiStack.nguoiChoi.itemBag[4] =
                vatPham(STACK_GOLD, 8, 4);
        loiStack.nguoiChoi.choPhepLuu = false;
        loiStack.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, STACK_GOLD, 2));
        dung(loiStack.nguoiChoi.vang == 1_000
                        && loiStack.nguoiChoi.itemBag[4].soLuong == 8,
                "loi DB khong rollback stack");

        BoTest trongTran = boTest();
        trongTran.nguoiChoi.vang = 1_000;
        trongTran.nguoiChoi.inTraining = true;
        trongTran.nguoiChoi.yeuCauMuaVatPham(
                tinMua((byte) 0, STACK_GOLD, 1));
        dung(trongTran.nguoiChoi.vang == 1_000
                        && demVatPham(trongTran.nguoiChoi) == 0
                        && trongTran.nguoiChoi.soLanLuu == 0,
                "dang chien dau van mua duoc");
    }

    private static void kiemTraMuaDongThoi() throws Exception {
        BoTest bo = boTest();
        bo.nguoiChoi.vang = 100;
        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Future<?>> ketQua = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                ketQua.add(pool.submit(() -> {
                    try {
                        bo.nguoiChoi.yeuCauMuaVatPham(
                                tinMua((byte) 0, STACK_GOLD, 1));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }));
            }
            for (Future<?> future : ketQua) {
                future.get();
            }
        } finally {
            pool.shutdownNow();
        }
        dung(bo.nguoiChoi.vang == 0
                        && tongSoLuong(bo.nguoiChoi, STACK_GOLD) == 1
                        && bo.nguoiChoi.soLanLuu == 1,
                "32 lenh mua dong thoi tao tien am/nhan ban item");
    }

    private static void kiemTraMoShopVaChuyenTrang()
            throws Exception {
        BoTest bo = boTest();
        ChickenCuaHang cuaHang = new ChickenCuaHang();
        cuaHang.datLoaiCuaHang((byte) 7);
        ArrayList<ChickenMauVatPham> danhSach = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            danhSach.add(mau(11_000 + i, 6, i + 1, 0));
        }
        cuaHang.themTab("Trang", danhSach);

        bo.nguoiChoi.requestTab(new ChickenTinNhan(
                (byte) -43, new byte[]{0, 0}));
        dung(bo.dichVu.coLenh(10),
                "request trang truoc khi mo shop khong phan hoi");

        bo.dichVu.xoaPacket();
        bo.nguoiChoi.xemCuaHang(cuaHang);
        dung(bo.dichVu.coLenh(103),
                "mo shop khong gui CMD103");
        ChickenTinNhan moShop = bo.dichVu.tinCuoi(103);
        try (DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(moShop.layDuLieu()))) {
            dung(in.readByte() == 7
                            && in.readUnsignedByte() == 1,
                    "CMD103 sai type/tab count");
        }

        bo.dichVu.xoaPacket();
        bo.nguoiChoi.requestTab(new ChickenTinNhan(
                (byte) -43, new byte[]{0, 1}));
        ChickenTinNhan trangHai = bo.dichVu.tinCuoi(-43);
        dung(trangHai != null,
                "request trang hop le khong gui CMD-43");
        try (DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(trangHai.layDuLieu()))) {
            dung(in.readUnsignedByte() == 0
                            && in.readUnsignedByte() == 1
                            && in.readUnsignedByte() == 2
                            && in.readUnsignedByte() == 1,
                    "CMD-43 sai metadata trang hai");
        }

        for (byte[] payload : List.of(
                new byte[0],
                new byte[]{0},
                new byte[]{0, 0, 0},
                new byte[]{-1, 0},
                new byte[]{0, 2})) {
            bo.dichVu.xoaPacket();
            bo.nguoiChoi.requestTab(
                    new ChickenTinNhan((byte) -43, payload));
            dung(bo.dichVu.coLenh(10)
                            || bo.dichVu.coLenh(45),
                    "request trang loi khong co phan hoi payload="
                            + Arrays.toString(payload));
        }

        BoTest rong = boTest();
        ChickenCuaHang shopRong = new ChickenCuaHang();
        shopRong.themTab("Rong", new ArrayList<>());
        rong.nguoiChoi.xemCuaHang(shopRong);
        dung(rong.dichVu.coLenh(103),
                "shop co tab rong lam hong CMD103");
    }

    private static void kiemTraBanVatPham() throws Exception {
        BoTest prompt = boTest();
        prompt.nguoiChoi.itemBag[9] =
                vatPham(STACK_GOLD, 2, 9);
        prompt.nguoiChoi.yeuCauBanVatPham(
                new ChickenTinNhan((byte) -48, new byte[]{9}));
        ChickenTinNhan xacNhan = prompt.dichVu.tinCuoi(-25);
        dung(xacNhan != null,
                "ban item hop le khong gui hop xac nhan");
        try (DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(xacNhan.layDuLieu()))) {
            dung(in.readInt() == 11_009,
                    "token xac nhan ban sai slot");
            dung(!in.readUTF().isBlank(),
                    "hop xac nhan ban rong noi dung");
        }

        BoTest banVang = boTest();
        banVang.nguoiChoi.vang = 10;
        banVang.nguoiChoi.itemBag[3] =
                vatPham(STACK_GOLD, 2, 3);
        banVang.nguoiChoi.thucHien(tinXacNhanBan(0, 3));
        dung(banVang.nguoiChoi.vang == 110
                        && banVang.nguoiChoi.itemBag[3] == null
                        && banVang.nguoiChoi.soLanLuu == 1,
                "ban item vang sai gia/khong xoa item");

        BoTest banNgoc = boTest();
        banNgoc.nguoiChoi.itemBag[2] =
                vatPham(STACK_GEM, 2, 2);
        banNgoc.nguoiChoi.thucHien(tinXacNhanBan(1, 2));
        dung(banNgoc.nguoiChoi.vang == 800,
                "quy doi gia ban item ngoc sai");

        BoTest banDoKhongGia = boTest();
        banDoKhongGia.nguoiChoi.itemBag[1] =
                vatPham(FREE_ITEM, 2, 1);
        banDoKhongGia.nguoiChoi.thucHien(
                tinXacNhanBan(0, 1));
        dung(banDoKhongGia.nguoiChoi.vang == 2,
                "item khong gia khong dung gia san 1 vang");

        BoTest balo = boTest();
        balo.nguoiChoi.itemBag[6] =
                vatPham(STACK_GOLD, 1, 6);
        balo.nguoiChoi.itemBalo = new int[]{6};
        balo.nguoiChoi.yeuCauBanVatPham(
                new ChickenTinNhan((byte) -48, new byte[]{6}));
        dung(!balo.dichVu.coLenh(-25),
                "item trong balo van mo xac nhan ban");
        balo.nguoiChoi.thucHien(tinXacNhanBan(0, 6));
        dung(balo.nguoiChoi.itemBag[6] != null
                        && balo.nguoiChoi.vang == 0,
                "client bo qua prompt ban duoc item trong balo");

        kiemTraPacketBanLoi();
    }

    private static void kiemTraPacketBanLoi() throws Exception {
        for (int doDai = 0; doDai <= 16; doDai++) {
            if (doDai == 1) {
                continue;
            }
            BoTest bo = boTest();
            bo.nguoiChoi.itemBag[0] =
                    vatPham(STACK_GOLD, 1, 0);
            bo.nguoiChoi.yeuCauBanVatPham(new ChickenTinNhan(
                    (byte) -48, new byte[doDai]));
            dung(bo.nguoiChoi.itemBag[0] != null
                            && bo.nguoiChoi.vang == 0,
                    "packet prompt ban sai do dai van giao dich");
        }
        for (int doDai = 0; doDai <= 16; doDai++) {
            if (doDai == 5) {
                continue;
            }
            BoTest bo = boTest();
            bo.nguoiChoi.itemBag[0] =
                    vatPham(STACK_GOLD, 1, 0);
            bo.nguoiChoi.thucHien(new ChickenTinNhan(
                    (byte) -25, new byte[doDai]));
            dung(bo.nguoiChoi.itemBag[0] != null
                            && bo.nguoiChoi.vang == 0
                            && bo.nguoiChoi.soLanLuu == 0,
                    "packet confirm ban sai do dai van giao dich");
        }
        for (int chiSo = 100; chiSo <= 255; chiSo++) {
            BoTest bo = boTest();
            bo.nguoiChoi.yeuCauBanVatPham(new ChickenTinNhan(
                    (byte) -48, new byte[]{(byte) chiSo}));
            dung(bo.nguoiChoi.soLanLuu == 0,
                    "slot ban ngoai tui van commit slot=" + chiSo);
        }

        BoTest maTranXacNhan = boTest();
        int soToHop = 0;
        for (int action = 0; action <= 255; action++) {
            for (int chiSo = 0; chiSo <= 255; chiSo++) {
                maTranXacNhan.dichVu.xoaPacket();
                maTranXacNhan.nguoiChoi.thucHien(
                        tinXacNhanBan(action, chiSo));
                dung(maTranXacNhan.nguoiChoi.vang == 0
                                && demVatPham(
                                        maTranXacNhan.nguoiChoi) == 0
                                && maTranXacNhan.nguoiChoi.soLanLuu == 0,
                        "action/slot ban rong sua kinh te action="
                                + action + " slot=" + chiSo);
                soToHop++;
            }
        }
        dung(soToHop == SO_TO_HOP_PACKET_BAN,
                "chua quet du action x slot xac nhan ban");
    }

    private static void kiemTraBanDongThoiVaRollback()
            throws Exception {
        BoTest dongThoi = boTest();
        dongThoi.nguoiChoi.itemBag[0] =
                vatPham(STACK_GOLD, 2, 0);
        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Future<?>> ketQua = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                int action = i;
                ketQua.add(pool.submit(() -> {
                    try {
                        dongThoi.nguoiChoi.thucHien(
                                tinXacNhanBan(action, 0));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }));
            }
            for (Future<?> future : ketQua) {
                future.get();
            }
        } finally {
            pool.shutdownNow();
        }
        dung(dongThoi.nguoiChoi.vang == 100
                        && dongThoi.nguoiChoi.itemBag[0] == null
                        && dongThoi.nguoiChoi.soLanLuu == 1,
                "32 confirm ban dong thoi cong vang nhieu lan");

        BoTest loi = boTest();
        loi.nguoiChoi.vang = 7;
        loi.nguoiChoi.itemBag[5] =
                vatPham(STACK_GOLD, 2, 5);
        loi.nguoiChoi.choPhepLuu = false;
        loi.nguoiChoi.thucHien(tinXacNhanBan(0, 5));
        dung(loi.nguoiChoi.vang == 7
                        && loi.nguoiChoi.itemBag[5] != null
                        && loi.nguoiChoi.itemBag[5].soLuong == 2,
                "loi DB khong rollback ban vat pham");

        BoTest trongTran = boTest();
        trongTran.nguoiChoi.itemBag[0] =
                vatPham(STACK_GOLD, 1, 0);
        trongTran.nguoiChoi.inTraining = true;
        trongTran.nguoiChoi.thucHien(tinXacNhanBan(0, 0));
        dung(trongTran.nguoiChoi.itemBag[0] != null
                        && trongTran.nguoiChoi.vang == 0,
                "dang trong tran van ban duoc item");
    }

    private static void kiemTraRouterShopThat() throws Exception {
        BoTest bo = boTest();
        ChickenNguoiDung user =
                new ChickenNguoiDung(bo.phien, bo.dichVu);
        user.nguoiChoi = bo.nguoiChoi;
        bo.phien.user = user;
        bo.nguoiChoi.vang = 100;

        new ChickenXuLyTin(bo.phien).khiCoTin(
                tinMua((byte) 0, STACK_GOLD, 1));
        dung(bo.nguoiChoi.vang == 0
                        && tongSoLuong(
                                bo.nguoiChoi, STACK_GOLD) == 1,
                "CMD72 khong di qua router shop that");

        bo.dichVu.xoaPacket();
        new ChickenXuLyTin(bo.phien).khiCoTin(
                new ChickenTinNhan((byte) -48, new byte[]{0}));
        dung(bo.dichVu.coLenh(-25),
                "CMD-48 khong di qua router mo xac nhan ban");
        bo.dichVu.xoaPacket();
        new ChickenXuLyTin(bo.phien).khiCoTin(
                tinXacNhanBan(0, 0));
        dung(bo.nguoiChoi.vang == 50
                        && bo.nguoiChoi.itemBag[0] == null,
                "CMD-25 khong di qua router ban vat pham");

        bo.dichVu.xoaPacket();
        new ChickenXuLyTin(bo.phien).khiCoTin(
                new ChickenTinNhan((byte) 103, new byte[0]));
        dung(bo.dichVu.coLenh(103),
                "CMD103 khong mo shop trang bi qua router");
        bo.dichVu.xoaPacket();
        new ChickenXuLyTin(bo.phien).khiCoTin(
                new ChickenTinNhan((byte) -33, new byte[0]));
        dung(bo.dichVu.coLenh(103),
                "CMD-33 khong mo shop item qua router");

        bo.dichVu.xoaPacket();
        new ChickenXuLyTin(bo.phien).khiCoTin(
                new ChickenTinNhan((byte) 103, new byte[]{1}));
        dung(!bo.dichVu.coLenh(103),
                "CMD103 payload thua van mo shop");
        new ChickenXuLyTin(bo.phien).khiCoTin(
                new ChickenTinNhan((byte) -33, new byte[]{1}));
        dung(!bo.dichVu.coLenh(103),
                "CMD-33 payload thua van mo shop");
    }

    private static BoTest boTest() {
        ChickenPhien phien = new ChickenPhien(null, 97_072);
        DichVuBatPacket dichVu = new DichVuBatPacket(phien);
        NguoiChoiShopTest nguoiChoi =
                new NguoiChoiShopTest(dichVu);
        nguoiChoi.ma = 97_072;
        nguoiChoi.ten = "ShopMatrix";
        dichVu.datNguoiChoi(nguoiChoi);
        return new BoTest(phien, dichVu, nguoiChoi);
    }

    private static ChickenMauVatPham mau(
            int ma,
            int loai,
            int giaVang,
            int giaNgoc
    ) {
        ChickenMauVatPham mau = new ChickenMauVatPham(
                (short) ma,
                (byte) loai,
                (byte) 0,
                "ShopTest" + ma,
                "",
                (byte) 0,
                0,
                (short) 0,
                (short) 0,
                false);
        mau.buyGold = giaVang;
        mau.buyGem = giaNgoc;
        return mau;
    }

    private static ChickenVatPham vatPham(
            int ma,
            int soLuong,
            int chiSo
    ) {
        ChickenVatPham vatPham = new ChickenVatPham(ma);
        vatPham.soLuong = soLuong;
        vatPham.chiSo = chiSo;
        return vatPham;
    }

    private static ChickenTinNhan tinMua(
            byte loai,
            int ma,
            int soLuong
    ) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(bo);
        ds.writeByte(loai);
        ds.writeShort(ma);
        ds.writeByte(soLuong);
        ds.flush();
        return new ChickenTinNhan((byte) 72, bo.toByteArray());
    }

    private static ChickenTinNhan tinXacNhanBan(
            int action,
            int chiSo
    ) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(bo);
        ds.writeByte(action);
        ds.writeInt(11_000 + chiSo);
        ds.flush();
        return new ChickenTinNhan((byte) -25, bo.toByteArray());
    }

    private static void lapDayTui(
            ChickenNguoiChoi nguoiChoi,
            int ma
    ) {
        for (int i = 0; i < nguoiChoi.itemBag.length; i++) {
            nguoiChoi.itemBag[i] = vatPham(ma, 1, i);
        }
    }

    private static int demVatPham(ChickenNguoiChoi nguoiChoi) {
        int tong = 0;
        for (ChickenVatPham vatPham : nguoiChoi.itemBag) {
            if (vatPham != null) {
                tong++;
            }
        }
        return tong;
    }

    private static int tongSoLuong(
            ChickenNguoiChoi nguoiChoi,
            int ma
    ) {
        int tong = 0;
        for (ChickenVatPham vatPham : nguoiChoi.itemBag) {
            if (vatPham != null && vatPham.ma == ma) {
                tong += vatPham.soLuong;
            }
        }
        return tong;
    }

    private static <T> void catDanhSach(
            List<T> danhSach,
            int kichThuoc
    ) {
        while (danhSach.size() > kichThuoc) {
            danhSach.remove(danhSach.size() - 1);
        }
    }

    private static void dung(
            boolean dieuKien,
            String thongBao
    ) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private record BoTest(
            ChickenPhien phien,
            DichVuBatPacket dichVu,
            NguoiChoiShopTest nguoiChoi
    ) {
    }

    private static final class NguoiChoiShopTest
            extends ChickenNguoiChoi {
        private volatile boolean choPhepLuu = true;
        private int soLanLuu;

        private NguoiChoiShopTest(ChickenDichVuGame dichVu) {
            super(dichVu);
        }

        @Override
        protected boolean luuGiaoDichShopCoKetQua(
                int vangCu,
                int ngocCu,
                String tuiCu
        ) {
            this.soLanLuu++;
            return this.choPhepLuu;
        }
    }

    private static final class DichVuBatPacket
            extends ChickenDichVuGame {
        private final List<ChickenTinNhan> cacTin =
                new ArrayList<>();

        private DichVuBatPacket(ChickenPhien phien) {
            super(phien);
        }

        @Override
        public synchronized void guiTin(ChickenTinNhan tin) {
            this.cacTin.add(tin);
        }

        private synchronized boolean coLenh(int lenh) {
            return this.cacTin.stream().anyMatch(
                    tin -> tin.layLenh() == (byte) lenh);
        }

        private synchronized ChickenTinNhan tinCuoi(int lenh) {
            for (int i = this.cacTin.size() - 1; i >= 0; i--) {
                ChickenTinNhan tin = this.cacTin.get(i);
                if (tin.layLenh() == (byte) lenh) {
                    return tin;
                }
            }
            return null;
        }

        private synchronized void xoaPacket() {
            this.cacTin.clear();
        }
    }
}
