package com.chicken.mohinh;

import com.chicken.chien.ChickenNapDanServer;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenPhien;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.vatpham.ChickenMauThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenVatPham;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

/** Hoi quy cho invariant trang bi, balo, do ben va luu kho item. */
public final class ChickenTrangBiTestSupport {
    private ChickenTrangBiTestSupport() {
    }

    public static void tuKiemTra() throws Exception {
        doiBaloLonSangNhoKhiDuCho();
        tuChoiBaloNhoKhiKhongDuCho();
        tuChoiBaloVuotNamO();
        khongChoMotVatPhamChiemNhieuOBalo();
        khongChoThuocTayDiemVaoBalo();
        khongChoAVGTrongBalo();
        doiSungTrongTranKhongSuaKhoVaDoiDungChiSo();
        quetDoiSungMotTramSungThuongTheoViTriBalo();
        themBaloDongThoiKhongTaoBanSao();
        rollbackKhiLuuKhoThatBai();
        rollbackTatCaNhanhChuyenKhoKhiDbLoi();
        stressTrangThaiKhoKhongMatNhanVatPham();
        tuChoiPacketThuaVaSungThieuCauHinh();
        doBenLuonToiDa();
        duLieuInstanceKhongDuocFakeChiSo();
        quetToanBoPayloadVaTanSuatKho();
    }

    private static void doiBaloLonSangNhoKhiDuCho() throws Exception {
        NguoiChoiKiemThu p = nguoiChoi(true);
        ChickenVatPham baloLon = balo(90_001, 5);
        baloLon.chiSo = 4;
        p.itemBody[4] = baloLon;
        p.itemBalo = new int[5];
        java.util.Arrays.fill(p.itemBalo, -1);
        for (int i = 0; i < 2; i++) {
            int oTui = 10 + i;
            p.itemBag[oTui] = vatPhamThuong(91_000 + i, oTui);
            p.itemBalo[3 + i] = oTui;
        }
        ChickenVatPham baloNho = balo(90_002, 3);
        baloNho.chiSo = 0;
        baloNho.HP = 0;
        p.itemBag[0] = baloNho;

        p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{4, 0}));

        cung(baloNho, p.itemBody[4], "khong mac balo moi");
        cung(baloLon, p.itemBag[0], "balo cu khong ve dung o tui");
        bang(3, p.itemBalo.length, "sai dung luong balo moi");
        for (int i = 0; i < 2; i++) {
            bang(10 + i, p.itemBalo[i],
                    "khong compact vat pham balo i=" + i);
        }
        bang(ChickenVatPham.DO_BEN_TOI_DA, baloNho.HP,
                "trang bi moi khong duoc hoi day do ben");
        bang(1, p.soLanLuu, "doi balo khong luu kho ngay");
    }

    private static void tuChoiBaloNhoKhiKhongDuCho() throws Exception {
        NguoiChoiKiemThu p = nguoiChoi(true);
        ChickenVatPham baloLon = balo(90_011, 5);
        baloLon.chiSo = 4;
        p.itemBody[4] = baloLon;
        p.itemBalo = new int[5];
        java.util.Arrays.fill(p.itemBalo, -1);
        for (int i = 0; i < 3; i++) {
            int oTui = 20 + i;
            p.itemBag[oTui] = vatPhamThuong(92_000 + i, oTui);
            p.itemBalo[i] = oTui;
        }
        ChickenVatPham baloNho = balo(90_012, 2);
        baloNho.chiSo = 0;
        p.itemBag[0] = baloNho;

        p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{4, 0}));

        cung(baloLon, p.itemBody[4], "balo cu bi thay khi khong du cho");
        cung(baloNho, p.itemBag[0], "balo moi bi lay khoi tui");
        bang(5, p.itemBalo.length, "balo bi thu nho nua chung");
        bang(0, p.soLanLuu, "trang thai bi tu choi van ghi DB");
    }

    private static void tuChoiBaloVuotNamO() throws Exception {
        NguoiChoiKiemThu p = nguoiChoi(true);
        ChickenVatPham baloLoi = balo(90_013, 6);
        baloLoi.chiSo = 0;
        p.itemBag[0] = baloLoi;

        p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{4, 0}));

        cung(baloLoi, p.itemBag[0], "balo hon 5 o van duoc trang bi");
        bang(0, p.itemBalo.length, "balo hon 5 o lam thay doi suc chua");
        bang(0, p.soLanLuu, "balo hon 5 o van ghi DB");
    }

    private static void khongChoMotVatPhamChiemNhieuOBalo()
            throws Exception {
        NguoiChoiKiemThu p = nguoiChoi(true);
        ChickenVatPham balo = balo(90_014, 5);
        balo.chiSo = 4;
        p.itemBody[4] = balo;
        p.itemBalo = new int[]{-1, -1, -1, -1, -1};
        ChickenVatPham vatPham = vatPhamThuong(90_015, 0);
        p.itemBag[0] = vatPham;

        p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{6, 0}));
        p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{6, 0}));

        int soThamChieu = 0;
        for (int chiSo : p.itemBalo) {
            if (chiSo == 0) {
                soThamChieu++;
            }
        }
        bang(1, soThamChieu,
                "mot vat pham chiem nhieu o balo");
        bang(1, p.soLanLuu,
                "packet them trung balo van ghi DB");
    }

    private static void themBaloDongThoiKhongTaoBanSao()
            throws Exception {
        NguoiChoiKiemThu p = nguoiChoi(true);
        ChickenVatPham balo = balo(90_016, 5);
        balo.chiSo = 4;
        p.itemBody[4] = balo;
        p.itemBalo = new int[]{-1, -1, -1, -1, -1};
        p.itemBag[0] = vatPhamThuong(90_017, 0);

        java.util.concurrent.ExecutorService pool =
                java.util.concurrent.Executors.newFixedThreadPool(16);
        java.util.ArrayList<java.util.concurrent.Future<?>> futures =
                new java.util.ArrayList<>();
        try {
            for (int i = 0; i < 64; i++) {
                futures.add(pool.submit(() -> {
                    try {
                        p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                                new byte[]{6, 0}));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }));
            }
            for (java.util.concurrent.Future<?> future : futures) {
                future.get();
            }
        } finally {
            pool.shutdownNow();
        }
        int soThamChieu = 0;
        for (int chiSo : p.itemBalo) {
            if (chiSo == 0) {
                soThamChieu++;
            }
        }
        bang(1, soThamChieu,
                "spam dong thoi tao trung vat pham trong balo");
        bang(1, p.soLanLuu,
                "spam dong thoi ghi DB nhieu lan");
    }

    private static void khongChoThuocTayDiemVaoBalo()
            throws Exception {
        NguoiChoiKiemThu p = nguoiChoi(true);
        ChickenVatPham balo = balo(90_018, 5);
        balo.chiSo = 4;
        p.itemBody[4] = balo;
        p.itemBalo = new int[]{-1, -1, -1, -1, -1};
        ChickenVatPham tayDiem = vatPhamThuong(
                ChickenNguoiChoi.MA_THUOC_TAY_DIEM, 0);
        p.itemBag[0] = tayDiem;

        p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{6, 0}));

        dung(!ChickenNguoiChoi.laVatPhamDuocPhepTrongBalo(tayDiem),
                "thuoc tay diem duoc phep vao balo");
        bang(-1, p.itemBalo[0],
                "packet van gan thuoc tay diem vao balo");
        bang(0, p.soLanLuu,
                "packet thuoc tay diem bi tu choi van ghi DB");
    }

    private static void khongChoAVGTrongBalo() throws Exception {
        NguoiChoiKiemThu p = nguoiChoi(true);
        ChickenVatPham balo = balo(90_019, 5);
        balo.chiSo = 4;
        p.itemBody[4] = balo;
        p.itemBalo = new int[]{-1, -1, -1, -1, -1};
        ChickenVatPham avg = sung(391, 0, 223, 500, 600, 1883);
        p.itemBag[0] = avg;

        p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{6, 0}));

        dung(!ChickenNguoiChoi.laVatPhamDuocPhepTrongBalo(avg),
                "AVG duoc phep vao Balo");
        bang(-1, p.itemBalo[0], "packet van gan AVG vao Balo");
        bang(0, p.soLanLuu, "tu choi AVG van ghi kho");
    }

    private static void doiSungTrongTranKhongSuaKhoVaDoiDungChiSo() {
        NguoiChoiKiemThu p = nguoiChoi(true);
        ChickenVatPham sungMac = sung(110, 5, 57, 100, 700, 839);
        ChickenVatPham sungBalo = sung(120, 3, 27, 250, 430, 848);
        p.itemBody[5] = sungMac;
        p.itemBag[3] = sungBalo;
        p.itemBalo = new int[]{3};

        ChickenChienBinh chienBinh = new ChickenChienBinh(
                p, (byte) 0, (short) 50, (short) 50);
        int tanCongMac = chienBinh.tanCong;
        bang(700, ChickenNapDanServer.layChoChienBinh(chienBinh),
                "nap dan sung mac sai");

        ChickenVatPham daCat = chienBinh.doiSungTrongTran(0);
        cung(sungMac, daCat, "khong tra ve sung cu");
        cung(sungBalo, chienBinh.laySungDangCamTrongTran(),
                "khong cam sung Balo");
        bang(27, chienBinh.maVuKhi, "part sung khong doi");
        bang(430, ChickenNapDanServer.layChoChienBinh(chienBinh),
                "nap dan khong theo sung dang cam");
        dung(chienBinh.tanCong > tanCongMac,
                "tan cong khong tinh lai theo sung dang cam");
        cung(sungMac, p.itemBody[5], "doi sung sua itemBody that");
        cung(sungBalo, p.itemBag[3], "doi sung sua itemBag that");
        bang(3, p.itemBalo[0], "doi sung sua tham chieu Balo that");

        ChickenVatPham daCatLanHai = chienBinh.doiSungTrongTran(0);
        cung(sungBalo, daCatLanHai, "doi lai khong cat dung sung");
        cung(sungMac, chienBinh.laySungDangCamTrongTran(),
                "doi lai khong ve sung mac ban dau");

        dung(chienBinh.doiSungTrongTran(255) == null,
                "index Balo gia van doi duoc sung");
    }

    /**
     * CMD 26 gui o hien thi 0..4, khong gui itemBag index. Quet du 100 sung
     * thuong voi vat pham co y nam tai itemBag[73] de khong con test pass do
     * hai index vo tinh trung nhau nhu bo test cu.
     */
    private static void quetDoiSungMotTramSungThuongTheoViTriBalo() {
        int soSungDaQuet = 0;
        for (ChickenQuanLyDanSung.DuLieuSung duLieu
                : ChickenQuanLyDanSung.layTatCa().values()) {
            int idSung = duLieu.getIdSung();
            if (idSung < 110 || idSung > 209) {
                continue;
            }
            NguoiChoiKiemThu p = nguoiChoi(true);
            ChickenVatPham sungMac = sung(
                    110, 5, 57, 100, 700, 839);
            ChickenVatPham sungBalo = sung(
                    idSung,
                    73,
                    duLieu.getPartSung(),
                    200 + (idSung - 110),
                    300 + (idSung - 110),
                    duLieu.getIdAnhChinh());
            p.itemBody[5] = sungMac;
            p.itemBag[73] = sungBalo;
            p.itemBalo = new int[]{73};

            ChickenChienBinh chienBinh = new ChickenChienBinh(
                    p, (byte) 0, (short) 50, (short) 50);
            cung(sungBalo, chienBinh.laySungTrongOTrongBalo(0),
                    "snapshot khong map client slot 0 cho sung " + idSung);
            dung(chienBinh.laySungTrongOTrongBalo(73) == null,
                    "snapshot con map nham itemBag index cho sung " + idSung);
            cung(sungMac, chienBinh.doiSungTrongTran(0),
                    "khong doi duoc sung bang client slot cho ID " + idSung);
            cung(sungBalo, chienBinh.laySungDangCamTrongTran(),
                    "doi nham instance sung ID " + idSung);
            bang(duLieu.getPartSung(), chienBinh.maVuKhi,
                    "doi sai part sung ID " + idSung);
            ChickenQuanLyDanSung.DuLieuSung dangCam =
                    ChickenQuanLyDanSung.theoSungDangTrangBi(
                            chienBinh.laySungDangCamTrongTran());
            dung(dangCam != null && dangCam.getIdSung() == idSung,
                    "doi sai cong thuc/loai dan sung ID " + idSung);
            bang(300 + (idSung - 110),
                    ChickenNapDanServer.layChoChienBinh(chienBinh),
                    "doi sai nap dan sung ID " + idSung);
            cung(sungBalo, chienBinh.doiSungTrongTran(0),
                    "khong cat lai dung sung ID " + idSung);
            cung(sungMac, chienBinh.laySungDangCamTrongTran(),
                    "khong quay lai sung mac sau ID " + idSung);
            soSungDaQuet++;
        }
        bang(100, soSungDaQuet,
                "ma tran doi sung thuong khong du 100 ID");
    }

    private static void quetToanBoPayloadVaTanSuatKho()
            throws Exception {
        NguoiChoiKiemThu p = nguoiChoi(true);
        for (int byteDau = 0; byteDau <= 255; byteDau++) {
            for (int byteSau = 0; byteSau <= 255; byteSau++) {
                p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                        new byte[]{(byte) byteDau, (byte) byteSau}));
                p.dungVatPham(new ChickenTinNhan((byte) 26,
                        new byte[]{(byte) byteDau, (byte) byteSau}));
            }
        }
        bang(0, p.soLanLuu,
                "payload tren kho rong van tao thay doi");

        for (int doDai = 0; doDai <= 8; doDai++) {
            if (doDai == 2) {
                continue;
            }
            byte[] payload = new byte[doDai];
            p.chuyenVatPham(new ChickenTinNhan((byte) -44, payload));
            p.dungVatPham(new ChickenTinNhan((byte) 26, payload));
        }

        ChickenPhien phien = new ChickenPhien(null, 99_002);
        dung(phien.choPhepXuLyLenh(-44, 1_000L),
                "lenh kho dau tien bi chan");
        dung(!phien.choPhepXuLyLenh(-44, 1_079L),
                "CMD -44 khong co rate-limit rieng");
        dung(phien.choPhepXuLyLenh(-44, 1_080L),
                "CMD -44 bi khoa lau hon quy dinh");
        dung(phien.choPhepXuLyLenh(26, 2_000L),
                "lenh dung item dau tien bi chan");
        dung(!phien.choPhepXuLyLenh(26, 2_119L),
                "CMD 26 khong co rate-limit rieng");
        dung(phien.choPhepXuLyLenh(26, 2_120L),
                "CMD 26 bi khoa lau hon quy dinh");

        NguoiChoiKiemThu duLieuLoi = nguoiChoi(true);
        duLieuLoi.itemBalo = new int[]{255};
        duLieuLoi.dichVu.guiBalo();

        System.out.println(
                "EQUIP_MATRIX_OK transferPayloads=65536"
                + " usePayloads=65536 malformedLengths=16");
    }

    private static void rollbackKhiLuuKhoThatBai() throws Exception {
        NguoiChoiKiemThu p = nguoiChoi(false);
        ChickenVatPham baloCu = balo(90_021, 3);
        baloCu.chiSo = 4;
        ChickenVatPham baloMoi = balo(90_022, 5);
        baloMoi.chiSo = 0;
        p.itemBody[4] = baloCu;
        p.itemBag[0] = baloMoi;
        p.itemBalo = new int[3];
        java.util.Arrays.fill(p.itemBalo, -1);

        p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{4, 0}));

        cung(baloCu, p.itemBody[4], "DB loi nhung trang bi khong rollback");
        cung(baloMoi, p.itemBag[0], "DB loi nhung tui khong rollback");
        bang(3, p.itemBalo.length, "DB loi nhung balo khong rollback");
        bang(1, p.soLanLuu, "khong thu luu kho");
    }

    private static void rollbackTatCaNhanhChuyenKhoKhiDbLoi()
            throws Exception {
        NguoiChoiKiemThu tuiSangRuong = nguoiChoi(false);
        ChickenVatPham itemTui = vatPhamThuong(90_023, 0);
        tuiSangRuong.itemBag[0] = itemTui;
        tuiSangRuong.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{1, 0}));
        cung(itemTui, tuiSangRuong.itemBag[0],
                "DB loi lam mat item tui sang ruong");
        bang(0, demVatPham(tuiSangRuong.itemBox),
                "DB loi van de item trong ruong");

        NguoiChoiKiemThu ruongSangTui = nguoiChoi(false);
        ChickenVatPham itemRuong = vatPhamThuong(90_024, 0);
        ruongSangTui.itemBox[0] = itemRuong;
        ruongSangTui.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{0, 0}));
        cung(itemRuong, ruongSangTui.itemBox[0],
                "DB loi lam mat item ruong sang tui");
        bang(0, demVatPham(ruongSangTui.itemBag),
                "DB loi van de item trong tui");

        NguoiChoiKiemThu thaoTrangBi = nguoiChoi(false);
        ChickenVatPham mu = trangBi(90_025, 0, 0);
        thaoTrangBi.itemBody[0] = mu;
        thaoTrangBi.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{5, 0}));
        cung(mu, thaoTrangBi.itemBody[0],
                "DB loi lam mat trang bi khi thao");
        bang(0, demVatPham(thaoTrangBi.itemBag),
                "DB loi van de trang bi vao tui");

        NguoiChoiKiemThu themBalo = nguoiChoi(false);
        ChickenVatPham balo = balo(90_026, 5);
        balo.chiSo = 4;
        themBalo.itemBody[4] = balo;
        themBalo.itemBalo = new int[]{-1, -1, -1, -1, -1};
        ChickenVatPham itemGan = vatPhamThuong(90_027, 0);
        themBalo.itemBag[0] = itemGan;
        themBalo.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{6, 0}));
        bang(-1, themBalo.itemBalo[0],
                "DB loi van gan item vao balo");

        NguoiChoiKiemThu goBalo = nguoiChoi(false);
        ChickenVatPham balo2 = balo(90_028, 5);
        balo2.chiSo = 4;
        goBalo.itemBody[4] = balo2;
        goBalo.itemBag[0] = vatPhamThuong(90_029, 0);
        goBalo.itemBalo = new int[]{0, -1, -1, -1, -1};
        goBalo.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{7, 0}));
        bang(0, goBalo.itemBalo[0],
                "DB loi van go item khoi balo");
    }

    private static void stressTrangThaiKhoKhongMatNhanVatPham()
            throws Exception {
        NguoiChoiKiemThu p = nguoiChoi(true);
        ChickenVatPham baloDangMac = balo(90_050, 5);
        baloDangMac.chiSo = 4;
        p.itemBody[4] = baloDangMac;
        p.itemBody[0] = trangBi(90_051, 0, 0);
        p.itemBalo = new int[]{-1, -1, -1, -1, -1};
        for (int i = 0; i < 6; i++) {
            p.itemBag[i] = vatPhamThuong(90_060 + i, i);
        }
        p.itemBag[6] = trangBi(90_066, 0, 6);
        p.itemBag[7] = balo(90_067, 3);
        p.itemBag[7].chiSo = 7;
        for (int i = 0; i < 6; i++) {
            p.itemBox[i] = vatPhamThuong(90_070 + i, i);
        }
        int tongBanDau = demVatPham(p.itemBag)
                + demVatPham(p.itemBody) + demVatPham(p.itemBox);
        int[] hanhDong = {0, 1, 4, 5, 6, 7};
        java.util.Random random = new java.util.Random(0xC11C4EEL);
        for (int lan = 0; lan < 20_000; lan++) {
            int loai = hanhDong[random.nextInt(hanhDong.length)];
            int chiSo = random.nextInt(4) == 0
                    ? random.nextInt(256) : random.nextInt(10);
            p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                    new byte[]{(byte) loai, (byte) chiSo}));
            kiemTraInvariantKho(p, tongBanDau, lan);
        }
    }

    private static void kiemTraInvariantKho(ChickenNguoiChoi p,
            int tongBanDau, int lan) {
        bang(tongBanDau, demVatPham(p.itemBag)
                + demVatPham(p.itemBody) + demVatPham(p.itemBox),
                "stress lam mat/nhan item lan=" + lan);
        java.util.IdentityHashMap<ChickenVatPham, Boolean> daCo =
                new java.util.IdentityHashMap<>();
        kiemTraMangVatPham(p.itemBag, daCo, false, lan);
        kiemTraMangVatPham(p.itemBody, daCo, true, lan);
        kiemTraMangVatPham(p.itemBox, daCo, false, lan);
        boolean[] refDaCo = new boolean[p.itemBag.length];
        for (int ref : p.itemBalo) {
            if (ref == -1) {
                continue;
            }
            dung(ref >= 0 && ref < p.itemBag.length,
                    "stress tao ref balo ngoai bien lan=" + lan);
            dung(!refDaCo[ref],
                    "stress tao ref balo trung lan=" + lan);
            dung(ChickenNguoiChoi.laVatPhamDuocPhepTrongBalo(
                            p.itemBag[ref]),
                    "stress dua sai loai vao balo lan=" + lan);
            bang(ref, p.itemBag[ref].chiSo,
                    "stress ref balo sai index lan=" + lan);
            refDaCo[ref] = true;
        }
    }

    private static void kiemTraMangVatPham(ChickenVatPham[] danhSach,
            java.util.IdentityHashMap<ChickenVatPham, Boolean> daCo,
            boolean laTrangBi, int lan) {
        for (int i = 0; i < danhSach.length; i++) {
            ChickenVatPham vatPham = danhSach[i];
            if (vatPham == null) {
                continue;
            }
            dung(daCo.put(vatPham, Boolean.TRUE) == null,
                    "stress nhan ban cung object lan=" + lan);
            bang(i, vatPham.chiSo,
                    "stress item sai index lan=" + lan);
            if (laTrangBi) {
                bang(i, vatPham.mau.loai,
                        "stress trang bi sai slot lan=" + lan);
            }
        }
    }

    private static void tuChoiPacketThuaVaSungThieuCauHinh()
            throws Exception {
        NguoiChoiKiemThu p = nguoiChoi(true);
        ChickenVatPham balo = balo(90_031, 5);
        balo.chiSo = 0;
        p.itemBag[0] = balo;
        p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{4, 0, 99}));
        cung(balo, p.itemBag[0], "packet thua van doi trang bi");
        bang(0, p.soLanLuu, "packet thua van ghi DB");

        ChickenVatPham sung = new ChickenVatPham(110);
        sung.mau = new ChickenMauVatPham((short) 110, (byte) 5,
                (byte) 0, "GunMissingReload", "", (byte) 1,
                0, (short) 57, (short) 57, false);
        sung.chiSo = 1;
        p.itemBag[1] = sung;
        p.chuyenVatPham(new ChickenTinNhan((byte) -44,
                new byte[]{4, 1}));
        cung(sung, p.itemBag[1], "sung thieu nap dan van duoc mac");
        bang(0, p.soLanLuu, "sung thieu cau hinh van ghi DB");

        sung.chiSo = 5;
        p.itemBody[5] = sung;
        bang(ChickenNapDanServer.TOI_DA,
                ChickenNapDanServer.layChoNguoiChoi(p),
                "sung thieu option 14 roi ve nap nhanh nhat");
    }

    private static void doBenLuonToiDa() {
        ChickenVatPham vatPham = vatPhamThuong(90_041, 0);
        dung(ChickenNguoiChoi.laVatPhamDuocPhepTrongBalo(vatPham),
                "vat pham type 10 hop le bi chan khoi balo");
        ChickenVatPham trangBi = new ChickenVatPham(90_042);
        trangBi.mau = new ChickenMauVatPham((short) 90_042,
                (byte) 0, (byte) 0, "Head", "", (byte) 1,
                0, (short) 1, (short) 1, false);
        dung(!ChickenNguoiChoi.laVatPhamDuocPhepTrongBalo(trangBi),
                "trang bi sai loai van duoc dua vao balo");
        vatPham.HP = -5;
        int hpDaLuu = vatPham.toJSONObject().getIntValue("HP");
        bang(ChickenVatPham.DO_BEN_TOI_DA, hpDaLuu,
                "JSON van luu do ben hong");
        bang(ChickenVatPham.DO_BEN_TOI_DA, vatPham.HP,
                "toJSONObject khong chuan hoa do ben RAM");
    }

    private static void duLieuInstanceKhongDuocFakeChiSo() {
        final int maTrangBi = 30_000;
        final int maNgoc = 30_001;
        ChickenMauVatPham trangBiCu =
                ChickenQuanLyMayChu.itemTemplates.get(maTrangBi);
        ChickenMauVatPham ngocCu =
                ChickenQuanLyMayChu.itemTemplates.get(maNgoc);
        ChickenMauThuocTinhVatPham option1Cu =
                ChickenQuanLyMayChu.iOptionTemplates.get(1);
        ChickenMauThuocTinhVatPham option2Cu =
                ChickenQuanLyMayChu.iOptionTemplates.get(2);
        ChickenMauThuocTinhVatPham option16Cu =
                ChickenQuanLyMayChu.iOptionTemplates.get(16);
        try {
            ChickenMauThuocTinhVatPham option1 = optionMau(1);
            ChickenMauThuocTinhVatPham option2 = optionMau(2);
            ChickenMauThuocTinhVatPham option16 = optionMau(16);
            ChickenQuanLyMayChu.iOptionTemplates.put(1, option1);
            ChickenQuanLyMayChu.iOptionTemplates.put(2, option2);
            ChickenQuanLyMayChu.iOptionTemplates.put(16, option16);

            ChickenMauVatPham mauTrangBi = new ChickenMauVatPham(
                    (short) maTrangBi, (byte) 0, (byte) 0,
                    "StatTemplate", "", (byte) 1, 0,
                    (short) 1, (short) 1, false);
            ChickenThuocTinhVatPham tanCong =
                    new ChickenThuocTinhVatPham(1, 50);
            mauTrangBi.thuocTinhs.add(tanCong);
            ChickenMauVatPham mauNgoc = new ChickenMauVatPham(
                    (short) maNgoc, (byte) 12, (byte) 0,
                    "Gem", "", (byte) 1, 0,
                    (short) 1, (short) 1, false);
            ChickenQuanLyMayChu.itemTemplates.put(maTrangBi, mauTrangBi);
            ChickenQuanLyMayChu.itemTemplates.put(maNgoc, mauNgoc);

            JSONObject json = new JSONObject();
            json.put("id", maTrangBi);
            json.put("quantity", 1);
            json.put("HP", -999);
            json.put("index", 0);
            JSONArray options = new JSONArray();
            options.add(optionJson(1, Integer.MAX_VALUE));
            options.add(optionJson(2, Integer.MAX_VALUE));
            for (int i = 0; i < 4; i++) {
                options.add(optionJson(16, maNgoc));
            }
            json.put("options", options);

            ChickenVatPham daTai = new ChickenVatPham(json);
            bang(50, daTai.getParamById(1),
                    "instance JSON fake duoc param template");
            bang(-1, daTai.getParamById(2),
                    "instance JSON chen duoc option ngoai template");
            bang(ChickenVatPham.SO_SOCKET_TOI_DA, daTai.nSocket,
                    "instance JSON chen qua 3 socket");
            bang(ChickenVatPham.DO_BEN_TOI_DA, daTai.HP,
                    "instance JSON ha do ben");
        } finally {
            khoiPhuc(ChickenQuanLyMayChu.itemTemplates,
                    maTrangBi, trangBiCu);
            khoiPhuc(ChickenQuanLyMayChu.itemTemplates, maNgoc, ngocCu);
            khoiPhuc(ChickenQuanLyMayChu.iOptionTemplates, 1, option1Cu);
            khoiPhuc(ChickenQuanLyMayChu.iOptionTemplates, 2, option2Cu);
            khoiPhuc(ChickenQuanLyMayChu.iOptionTemplates, 16, option16Cu);
        }
    }

    private static ChickenMauThuocTinhVatPham optionMau(int ma) {
        ChickenMauThuocTinhVatPham option =
                new ChickenMauThuocTinhVatPham();
        option.ma = ma;
        return option;
    }

    private static JSONObject optionJson(int ma, int thamSo) {
        JSONObject option = new JSONObject();
        option.put("id", ma);
        option.put("param", thamSo);
        return option;
    }

    private static <T> void khoiPhuc(java.util.Map<Integer, T> map,
            int ma, T giaTriCu) {
        if (giaTriCu == null) {
            map.remove(ma);
        } else {
            map.put(ma, giaTriCu);
        }
    }

    private static NguoiChoiKiemThu nguoiChoi(boolean choLuu) {
        DichVuImLang dichVu = new DichVuImLang();
        NguoiChoiKiemThu p = new NguoiChoiKiemThu(dichVu, choLuu);
        p.ma = 99_001;
        p.ten = "EquipRegression";
        p.cap = 99;
        dichVu.datNguoiChoi(p);
        return p;
    }

    private static ChickenVatPham balo(int ma, int sucChua) {
        ChickenVatPham item = new ChickenVatPham(ma);
        item.ma = ma;
        item.mau = new ChickenMauVatPham((short) ma, (byte) 4,
                (byte) 0, "Balo" + ma, "", (byte) 1,
                0, (short) 1, (short) 1, false);
        ChickenMauThuocTinhVatPham mauOption =
                new ChickenMauThuocTinhVatPham();
        mauOption.ma = 13;
        ChickenThuocTinhVatPham option =
                new ChickenThuocTinhVatPham(13, sucChua);
        option.optionTemplate = mauOption;
        item.itemOptions.add(option);
        return item;
    }

    private static ChickenVatPham vatPhamThuong(int ma, int chiSo) {
        ChickenVatPham item = new ChickenVatPham(ma);
        item.ma = ma;
        item.mau = new ChickenMauVatPham((short) ma, (byte) 10,
                (byte) 0, "Item" + ma, "", (byte) 1,
                0, (short) 1, (short) 1, false);
        item.chiSo = chiSo;
        item.soLuong = 1;
        return item;
    }

    private static ChickenVatPham trangBi(int ma, int loai, int chiSo) {
        ChickenVatPham item = new ChickenVatPham(ma);
        item.ma = ma;
        item.mau = new ChickenMauVatPham((short) ma, (byte) loai,
                (byte) 0, "Equip" + ma, "", (byte) 1,
                0, (short) 1, (short) 1, false);
        item.chiSo = chiSo;
        item.soLuong = 1;
        return item;
    }

    private static ChickenVatPham sung(int ma, int chiSo, int part,
            int tanCong, int napDan, int icon) {
        ChickenVatPham item = new ChickenVatPham(ma);
        item.ma = ma;
        item.mau = new ChickenMauVatPham((short) ma, (byte) 5,
                (byte) 0, "Gun" + ma, "", (byte) 1,
                0, (short) icon, (short) part, false);
        item.chiSo = chiSo;
        item.soLuong = 1;
        item.HP = ChickenVatPham.DO_BEN_TOI_DA;
        ChickenMauThuocTinhVatPham mauTanCong = optionMau(1);
        ChickenThuocTinhVatPham optionTanCong =
                new ChickenThuocTinhVatPham(1, tanCong);
        optionTanCong.optionTemplate = mauTanCong;
        item.itemOptions.add(optionTanCong);
        ChickenMauThuocTinhVatPham mauNapDan = optionMau(14);
        ChickenThuocTinhVatPham optionNapDan =
                new ChickenThuocTinhVatPham(14, napDan);
        optionNapDan.optionTemplate = mauNapDan;
        item.itemOptions.add(optionNapDan);
        return item;
    }

    private static int demVatPham(ChickenVatPham[] danhSach) {
        int soLuong = 0;
        for (ChickenVatPham vatPham : danhSach) {
            if (vatPham != null) {
                soLuong++;
            }
        }
        return soLuong;
    }

    private static void bang(int mongDoi, int thucTe, String thongBao) {
        if (mongDoi != thucTe) {
            throw new AssertionError(thongBao + " expected="
                    + mongDoi + " actual=" + thucTe);
        }
    }

    private static void cung(Object mongDoi, Object thucTe, String thongBao) {
        if (mongDoi != thucTe) {
            throw new AssertionError(thongBao);
        }
    }

    private static void dung(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private static final class NguoiChoiKiemThu extends ChickenNguoiChoi {
        private final boolean choLuu;
        private int soLanLuu;

        private NguoiChoiKiemThu(ChickenDichVuGame dichVu,
                boolean choLuu) {
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
            // Test chi can quan sat state server.
        }
    }
}
