package com.chicken.kiemthu;

import com.chicken.avg.ChickenHoatAnhHawk;
import com.chicken.avg.ChickenCoCheHulk;
import com.chicken.avg.ChickenCoCheBayAVG;
import com.chicken.avg.ChickenCongThucBanUltron;
import com.chicken.avg.ChickenKyNangDacBietHawk;
import com.chicken.avg.ChickenKyNangDacBietIronMan;
import com.chicken.avg.ChickenKyNangDacBietLoki;
import com.chicken.avg.ChickenKyNangDacBietThor;
import com.chicken.avg.ChickenKyNangDacBietUltron;
import com.chicken.avg.ChickenSatThuongLanKyNang;
import com.chicken.avg.ChickenTiaLaserIronMan;
import com.chicken.avg.ChickenThanhDiChuyenAVG;
import com.chicken.chien.ChickenCauHinhSatThuongSung;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenDiChuyenServer;
import com.chicken.chien.ChickenLoatDanServer;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.chien.ChickenPhatBanServer;
import com.chicken.chien.ChickenQuanLyCongThucSung;
import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.chien.ChickenTinhSatThuongNo;
import com.chicken.chien.ChickenSieuCao;
import com.chicken.chien.ChickenYeuCauBanServer;
import com.chicken.chien.ChickenYeuCauToaDoServer;
import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.luyentap.ChickenDuLieuPhatBanLuyenTap;
import com.chicken.luyentap.ChickenXuLyBanLuyenTap;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.vatpham.ChickenMauThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenVatPham;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

/** Bo kiem thu khong can database, client hay server dang chay. */
public final class ChickenKiemThuBan {
    private static int daChay;

    private ChickenKiemThuBan() {
    }

    public static void main(String[] args) throws Exception {
        chay("packet gia bi bo qua", ChickenKiemThuBan::kiemTraPacketGia);
        chay("packet loi bi tu choi", ChickenKiemThuBan::kiemTraPacketLoi);
        chay("CMD 53 khong tin toa do client", ChickenKiemThuBan::kiemTraCmd53KhongTinClient);
        chay("chi Iron Man va Ultron co quyen bay", ChickenKiemThuBan::kiemTraQuyenBayServer);
        chay("toan bo mapping sung", ChickenKiemThuBan::kiemTraToanBoMappingSung);
        chay("so duong dan dac biet", ChickenKiemThuBan::kiemTraSoDuongDanDacBiet);
        chay("va cham va damage sung thuong", ChickenKiemThuBan::kiemTraVaChamDamage);
        chay("cong thuc duong dan AVG", ChickenKiemThuBan::kiemTraCongThucAvg);
        chay("no lan skill Thor va Hawk", ChickenKiemThuBan::kiemTraNoLanSkillAvg);
        chay("the luc di chuyen theo option 26", ChickenKiemThuBan::kiemTraTheLucDiChuyen);
        chay("anh dan ton tai", ChickenKiemThuBan::kiemTraAnhDan);
        chay("skill khong the dung cheo AVG", ChickenKiemThuBan::kiemTraSkillCheoAvg);
        chay("skill Hawk khong the dung sai luot", ChickenKiemThuBan::kiemTraHawkSaiLuot);
        chay("Loki copy nguoi choi va chan bot boss", ChickenKiemThuBan::kiemTraLokiSaoChepNguoiChoi);
        chay("damage no va tuong che", ChickenTinhSatThuongNo::tuKiemTra);
        chay("sieu cao phai trung hitbox du bo qua tuong",
                ChickenKiemThuBan::kiemTraSieuCaoTrungHitbox);
        System.out.println("COMBAT_TEST_OK tests=" + daChay
                + " weapons=" + ChickenQuanLyDanSung.layTatCa().size());
    }

    private static void kiemTraSieuCaoTrungHitbox() {
        short[] xs = {0, 50, 100};
        short[] ys = {0, -400, 0};

        dung(ChickenSieuCao.laPhatSieuCaoTrungMucTieu(
                        (byte) 0, xs, ys, 100, 0, false),
                "quy dao roi 400 px va cat hitbox lai khong duoc tinh sieu cao");
        dung(!ChickenSieuCao.laPhatSieuCaoTrungMucTieu(
                        (byte) 0, xs, ys, 160, 0, false),
                "chi bay cao nhung khong trung hitbox van duoc tinh sieu cao");
        dung(!ChickenSieuCao.laPhatSieuCaoTrungMucTieu(
                        (byte) 0, xs, ys, 75, -200, false),
                "trung hitbox truoc khi roi du 350 px van duoc tinh sieu cao");
        dung(!ChickenSieuCao.laPhatSieuCaoTrungMucTieu(
                        (byte) 17, xs, ys, 100, 0, false),
                "loai dan khong ho tro lai duoc tinh sieu cao");
        bang(4_560, ChickenSieuCao.tangSatThuong(3_800),
                "he so sieu cao khong con la 120 phan tram");
    }

    private static void kiemTraPacketGia() throws Exception {
        int[] idMau = {110, 130, 140, 150, 160, 170, 180, 200, 391, 398};
        for (int idSung : idMau) {
            ChickenQuanLyDanSung.DuLieuSung sung =
                    batBuocCoSung(idSung);
            boolean haiLuc = sung.getLoaiDan() == 17 || sung.getLoaiDan() == 19;
            byte avenger = idSung >= 391 && idSung <= 398
                    ? (byte) (idSung - 390) : 0;
            byte[] packetGia = taoPacket(
                    (byte) 127,
                    (short) 30_000,
                    (short) -30_000,
                    (short) 721,
                    255,
                    0,
                    255,
                    haiLuc
            );
            ChickenYeuCauBanServer.KetQua ketQua =
                    ChickenYeuCauBanServer.doc(
                            new ChickenTinNhan((byte) 22, packetGia), sung, avenger);
            khacNull(ketQua, "server tu choi packet hop le ID=" + idSung);
            bang(sung.getLoaiDan(), ketQua.getLoaiDan(),
                    "client doi duoc loai dan ID=" + idSung);
            bang(sung.getSoVienMoiLoat(), ketQua.getSoVienMoiLoat(),
                    "client doi duoc so vien ID=" + idSung);
            bang(30, ketQua.getLuc() & 0xFF, "luc khong duoc kep");
            bang(haiLuc ? 1 : 30, ketQua.getLucPhu() & 0xFF,
                    "luc phu khong duoc kep");

            ChickenDuLieuPhatBanLuyenTap luyenTap =
                    ChickenXuLyBanLuyenTap.docPhatBan(
                            new ChickenTinNhan((byte) 84, packetGia),
                            sung.getLoaiDan(),
                            true,
                            haiLuc
                    );
            khacNull(luyenTap, "luyen tap tu choi packet hop le ID=" + idSung);
            bang(sung.getLoaiDan(), luyenTap.loaiDan,
                    "luyen tap tin loai dan client ID=" + idSung);
        }
    }

    private static void kiemTraPacketLoi() throws Exception {
        ChickenQuanLyDanSung.DuLieuSung at4 = batBuocCoSung(110);
        byte[] dung = taoPacket((byte) 0, (short) 0, (short) 0,
                (short) 45, 20, 20, 1, false);
        for (int doDai = 0; doDai < dung.length; doDai++) {
            byte[] thieu = Arrays.copyOf(dung, doDai);
            laNull(ChickenYeuCauBanServer.doc(
                    new ChickenTinNhan((byte) 22, thieu), at4, (byte) 0),
                    "nhan packet thieu " + doDai + " byte");
        }
        byte[] thua = Arrays.copyOf(dung, dung.length + 1);
        laNull(ChickenYeuCauBanServer.doc(
                new ChickenTinNhan((byte) 22, thua), at4, (byte) 0),
                "nhan packet co byte thua");
        laNull(ChickenYeuCauBanServer.doc(
                new ChickenTinNhan((byte) 22, dung), null, (byte) 0),
                "sung khong ton tai lai tao duoc dan mac dinh");
        laNull(ChickenXuLyBanLuyenTap.docPhatBan(
                new ChickenTinNhan((byte) 84, dung), (byte) 99, false, false),
                "luyen tap tao dan mac dinh cho mapping sai");
    }

    private static void kiemTraCmd53KhongTinClient() throws Exception {
        byte[] packetGia = taoPacketToaDo((short) 30_000, (short) -30_000);
        ChickenTinNhan tinGia = new ChickenTinNhan((byte) 53, packetGia);
        ChickenYeuCauToaDoServer.ToaDo daDoc = ChickenYeuCauToaDoServer.doc(tinGia);
        khacNull(daDoc, "CMD 53 dung 4 byte lai bi tu choi");
        bang(30_000, daDoc.getX(), "doc sai X signed short");
        bang(-30_000, daDoc.getY(), "doc sai Y signed short");

        for (int doDai = 0; doDai < packetGia.length; doDai++) {
            laNull(ChickenYeuCauToaDoServer.doc(new ChickenTinNhan(
                    (byte) 53, Arrays.copyOf(packetGia, doDai))),
                    "CMD 53 nhan packet thieu " + doDai + " byte");
        }
        laNull(ChickenYeuCauToaDoServer.doc(new ChickenTinNhan(
                (byte) 53, Arrays.copyOf(packetGia, packetGia.length + 1))),
                "CMD 53 nhan packet co byte thua");

        ChickenQuanLyBanDo banDoCoNen = new ChickenQuanLyBanDo(0) {
            @Override
            public int getWidth() { return 500; }

            @Override
            public int getHeight() { return 400; }

            @Override
            public synchronized boolean coVaCham(short x, short y) {
                return x >= 0 && x < 500 && y >= 300;
            }
        };
        ChickenYeuCauToaDoServer.KetQuaDongBo diBo =
                ChickenYeuCauToaDoServer.dongBoThuDong(
                        tinGia, banDoCoNen, (short) 120, (short) 100, false);
        khacNull(diBo, "CMD 53 hop le khong tao duoc ket qua server");
        bang(120, diBo.getX(), "CMD 53 gia da doi duoc X server");
        bang(300, diBo.getY(), "server khong tu tim mat dat ben duoi");
        dung(diBo.isDaRoi(), "khong danh dau ket qua roi do server tinh");

        ChickenYeuCauToaDoServer.KetQuaDongBo avgBay =
                ChickenYeuCauToaDoServer.dongBoThuDong(
                        tinGia, banDoCoNen, (short) 120, (short) 100, true);
        khacNull(avgBay, "AVG bay bi tu choi CMD 53 hop le");
        bang(120, avgBay.getX(), "AVG bay bi doi X theo client");
        bang(100, avgBay.getY(), "AVG bay bi ep xuong nen");
        dung(!avgBay.isDaRoi(), "AVG bay bi ap trong luc");

        ChickenQuanLyBanDo banDoKhongNen = new ChickenQuanLyBanDo(0) {
            @Override
            public int getWidth() { return 500; }

            @Override
            public int getHeight() { return 400; }

            @Override
            public synchronized boolean coVaCham(short x, short y) { return false; }
        };
        ChickenYeuCauToaDoServer.KetQuaDongBo khongNen =
                ChickenYeuCauToaDoServer.dongBoThuDong(
                        tinGia, banDoKhongNen, (short) 120, (short) 100, false);
        khacNull(khongNen, "map khong nen lam hong parser CMD 53");
        bang(120, khongNen.getX(), "map khong nen lai tin X client");
        bang(100, khongNen.getY(), "map khong nen lai tin Y client");
        dung(khongNen.isKhongCoNen(), "server khong danh dau truong hop roi khoi map");

        ChickenChienBinh gioiHanTanSuat = new ChickenChienBinh(
                (byte) 0, (short) 120, (short) 100, "P0", (short) 110, (byte) 0);
        dung(ChickenYeuCauToaDoServer.choPhepDongBo(gioiHanTanSuat, 1_000L),
                "CMD 53 dau tien bi rate-limit nham");
        dung(!ChickenYeuCauToaDoServer.choPhepDongBo(gioiHanTanSuat, 1_050L),
                "CMD 53 spam duoi 100ms van duoc nhan");
        dung(ChickenYeuCauToaDoServer.choPhepDongBo(gioiHanTanSuat, 1_100L),
                "CMD 53 hop le sau 100ms van bi chan");
    }

    private static void kiemTraQuyenBayServer() {
        ChickenChienBinh ironMan = chienBinh((byte) 0, (short) 223, (byte) 1);
        ChickenChienBinh ultron = chienBinh((byte) 1, (short) 230, (byte) 8);
        ChickenChienBinh hulk = chienBinh((byte) 2, (short) 224, (byte) 2);
        dung(ChickenCoCheBayAVG.coTheBay(ironMan), "Iron Man server khong bay duoc");
        dung(ChickenCoCheBayAVG.coTheBay(ultron), "Ultron server khong bay duoc");
        dung(!ChickenCoCheBayAVG.coTheBay(hulk), "AVG thuong lai co quyen bay");

        ChickenChienBinh giaIronMan = chienBinh((byte) 3, (short) 224, (byte) 2);
        giaIronMan.avenger = 1;
        dung(!ChickenCoCheBayAVG.coTheBay(giaIronMan),
                "chi sua ID avenger da gia duoc quyen bay Iron Man");
        ChickenChienBinh giaUltron = chienBinh((byte) 4, (short) 224, (byte) 2);
        giaUltron.avenger = 8;
        dung(!ChickenCoCheBayAVG.coTheBay(giaUltron),
                "chi sua ID avenger da gia duoc quyen bay Ultron");

        ChickenQuanLyBanDo banDoPhang = new ChickenQuanLyBanDo(0) {
            @Override
            public int getWidth() { return 500; }

            @Override
            public int getHeight() { return 400; }

            @Override
            public synchronized boolean coVaCham(short x, short y) {
                return x >= 0 && x < 500 && y >= 300;
            }
        };
        ChickenDiChuyenServer.KetQua diBoGiaY = ChickenDiChuyenServer.xuLy(
                banDoPhang,
                (short) 100, (short) 300,
                (short) 140, (short) 40,
                100,
                ChickenCoCheBayAVG.coTheBay(giaIronMan));
        bang(140, diBoGiaY.getX(), "nhan vat di bo khong di duoc theo X hop le");
        bang(300, diBoGiaY.getY(), "nhan vat gia Iron Man da bay bang Y client");
        bang(60, diBoGiaY.getConLai(), "server tru sai thanh di chuyen tren mat dat");

        ChickenDiChuyenServer.KetQua dichChuyenXa = ChickenDiChuyenServer.xuLy(
                banDoPhang,
                (short) 100, (short) 300,
                (short) 30_000, (short) -30_000,
                100,
                false);
        bang(200, dichChuyenXa.getX(), "packet X cuc lon da teleport qua thanh di chuyen");
        bang(300, dichChuyenXa.getY(), "packet Y cuc lon da dua nguoi di bo len troi");
        bang(0, dichChuyenXa.getConLai(), "teleport bi chan nhung khong tru dung gioi han");

        ChickenQuanLyBanDo banDoCoTuongMong = new ChickenQuanLyBanDo(0) {
            @Override
            public int getWidth() { return 500; }

            @Override
            public int getHeight() { return 400; }

            @Override
            public synchronized boolean coVaCham(short x, short y) {
                return (x >= 0 && x < 500 && y >= 300)
                        || (x == 150 && y == 285);
            }
        };
        ChickenDiChuyenServer.KetQua biTuongMongChan =
                ChickenDiChuyenServer.xuLy(
                        banDoCoTuongMong,
                        (short) 100, (short) 300,
                        (short) 200, (short) 300,
                        100,
                        false);
        bang(137, biTuongMongChan.getX(),
                "nhan vat da di xuyen vach dia hinh mong 1 px");
        bang(300, biTuongMongChan.getY(),
                "bi tuong chan nhung server lam lech Y");
        bang(63, biTuongMongChan.getConLai(),
                "buoc bi tuong chan van bi tru the luc");
        dung(biTuongMongChan.isBiDiaHinhChan(),
                "server khong danh dau duong di bi dia hinh chan");

        ChickenQuanLyBanDo banDoDoc = new ChickenQuanLyBanDo(0) {
            @Override
            public int getWidth() { return 500; }

            @Override
            public int getHeight() { return 400; }

            @Override
            public synchronized boolean coVaCham(short x, short y) {
                if (x < 0 || x >= 500 || y < 0) {
                    return false;
                }
                int matDoc = 400 - x;
                return y >= matDoc;
            }
        };
        ChickenDiChuyenServer.KetQua lenDoc = ChickenDiChuyenServer.xuLy(
                banDoDoc,
                (short) 100, (short) 300,
                (short) 120, (short) 20,
                100,
                false);
        bang(120, lenDoc.getX(), "di len doc bi server keo lui");
        dung(lenDoc.getY() >= 278 && lenDoc.getY() <= 280,
                "server tinh sai do cao khi di len doc y=" + lenDoc.getY());
        bang(80, lenDoc.getConLai(), "di len doc bi tru sai the luc");
        dung(!lenDoc.isBiDiaHinhChan(), "mat doc bi nhan nham thanh tuong");

        ChickenDiChuyenServer.KetQua xuongDoc = ChickenDiChuyenServer.xuLy(
                banDoDoc,
                (short) 120, (short) 280,
                (short) 100, (short) 390,
                100,
                false);
        bang(100, xuongDoc.getX(), "di xuong doc bi server keo lui");
        dung(xuongDoc.getY() >= 298 && xuongDoc.getY() <= 300,
                "server tinh sai do cao khi di xuong doc y=" + xuongDoc.getY());
        bang(80, xuongDoc.getConLai(), "di xuong doc bi tru sai the luc");
        dung(!xuongDoc.isBiDiaHinhChan(), "xuong doc bi nhan nham thanh tuong");

        ChickenDiChuyenServer.KetQua ironManXuyenTuong =
                ChickenDiChuyenServer.xuLy(
                        banDoCoTuongMong,
                        (short) 100, (short) 300,
                        (short) 180, (short) 240,
                        100,
                        ChickenCoCheBayAVG.coTheBay(ironMan));
        bang(180, ironManXuyenTuong.getX(), "Iron Man bi tuong chan khi dang bay");
        bang(240, ironManXuyenTuong.getY(), "Iron Man bi ep theo dia hinh khi dang bay");
        dung(!ironManXuyenTuong.isBiDiaHinhChan(),
                "Iron Man bay bi danh dau va cham dia hinh");

        ChickenDiChuyenServer.KetQua ultronXuyenTuong =
                ChickenDiChuyenServer.xuLy(
                        banDoCoTuongMong,
                        (short) 100, (short) 300,
                        (short) 180, (short) 240,
                        100,
                        ChickenCoCheBayAVG.coTheBay(ultron));
        bang(180, ultronXuyenTuong.getX(), "Ultron bi tuong chan khi dang bay");
        bang(240, ultronXuyenTuong.getY(), "Ultron bi ep theo dia hinh khi dang bay");
        dung(!ultronXuyenTuong.isBiDiaHinhChan(),
                "Ultron bay bi danh dau va cham dia hinh");

        ChickenDiChuyenServer.KetQua ironManBay = ChickenDiChuyenServer.xuLy(
                banDoPhang,
                (short) 100, (short) 300,
                (short) 140, (short) 240,
                100,
                ChickenCoCheBayAVG.coTheBay(ironMan));
        bang(140, ironManBay.getX(), "Iron Man bay sai X");
        bang(240, ironManBay.getY(), "Iron Man hop le bi ep xuong dat");
        bang(40, ironManBay.getConLai(), "Iron Man bay khong bi tru dung the luc");

        ChickenDiChuyenServer.KetQua ironManDichXa = ChickenDiChuyenServer.xuLy(
                banDoPhang,
                (short) 100, (short) 300,
                (short) 499, (short) 0,
                100,
                true);
        dung(ChickenThanhDiChuyenAVG.tinhQuangDuong(
                        (short) 100, (short) 300,
                        ironManDichXa.getX(), ironManDichXa.getY()) <= 100,
                "Iron Man hop le van teleport vuot thanh di chuyen");

        ChickenMauVatPham ironManCu = ChickenQuanLyMayChu.itemTemplates.get(391);
        ChickenMauVatPham hulkCu = ChickenQuanLyMayChu.itemTemplates.get(392);
        try {
            ChickenQuanLyMayChu.itemTemplates.put(391, mauSungThuNghiem(391));
            ChickenQuanLyMayChu.itemTemplates.put(392, mauSungThuNghiem(392));

            ChickenNguoiChoi nguoiIronMan = new ChickenNguoiChoi(null);
            nguoiIronMan.avenger = 8;
            nguoiIronMan.itemBody[5] = new ChickenVatPham(391);
            bang(1, ChickenCoCheBayAVG.layAvengerTuTrangBi(nguoiIronMan),
                    "server khong suy ra Iron Man tu trang bi that");
            dung(ChickenCoCheBayAVG.coTrangBiBayHopLe(nguoiIronMan),
                    "trang bi Iron Man that lai khong co quyen bay");

            ChickenNguoiChoi nguoiGia = new ChickenNguoiChoi(null);
            nguoiGia.avenger = 1;
            nguoiGia.itemBody[5] = new ChickenVatPham(392);
            dung(!ChickenCoCheBayAVG.coTrangBiBayHopLe(nguoiGia),
                    "Hulk sua bien avenger da gia duoc trang bi bay");
        } finally {
            khoiPhucMap(ChickenQuanLyMayChu.itemTemplates, 391, ironManCu);
            khoiPhucMap(ChickenQuanLyMayChu.itemTemplates, 392, hulkCu);
        }
    }

    private static void kiemTraToanBoMappingSung() {
        Map<Integer, ChickenQuanLyDanSung.DuLieuSung> tatCa =
                ChickenQuanLyDanSung.layTatCa();
        bang(111, tatCa.size(), "so mapping sung thay doi ngoai du kien");
        for (Map.Entry<Integer, ChickenQuanLyDanSung.DuLieuSung> entry
                : tatCa.entrySet()) {
            int id = entry.getKey();
            ChickenQuanLyDanSung.DuLieuSung sung = entry.getValue();
            bang(id, sung.getIdSung(), "ID key va value lech");
            dung(sung.getSoVienMoiLoat() > 0, "so vien <= 0 ID=" + id);
            dung((sung.getSoVienMoiLoat() & 0xFF) <= 16,
                    "so vien vuot gioi han ID=" + id);
            khacNull(ChickenCauHinhSatThuongSung.theoIdSung(id),
                    "thieu profile damage ID=" + id);

            ChickenLoatDanServer.KetQua loat = ChickenLoatDanServer.tao(
                    (short) 200, (short) 500,
                    (short) 180, (short) 520,
                    (short) 45, (byte) 20, (byte) 12,
                    sung, (byte) 0, (byte) 0, BAN_DO_TRONG
            );
            bang(sung.getLoaiDan(), loat.getLoaiDan(),
                    "loat dan dung nham type ID=" + id);
            int soDuongMongDoi = soDuongMongDoi(sung);
            bang(soDuongMongDoi, loat.getCacDuongX().length,
                    "sai so duong X ID=" + id);
            bang(soDuongMongDoi, loat.getCacDuongY().length,
                    "sai so duong Y ID=" + id);
            for (int i = 0; i < soDuongMongDoi; i++) {
                short[] xs = loat.getCacDuongX()[i];
                short[] ys = loat.getCacDuongY()[i];
                khacNull(xs, "X null ID=" + id + " vien=" + i);
                khacNull(ys, "Y null ID=" + id + " vien=" + i);
                dung(xs.length > 0 && xs.length == ys.length,
                        "quy dao hong ID=" + id + " vien=" + i);
                boolean duongPhuBatDauTaiDiemTach = i > 0
                        && ((sung.getNhomSung() == 6 && sung.getLoaiDan() == 19)
                        || (sung.getNhomSung() == 8 && sung.getLoaiDan() == 17));
                if (!duongPhuBatDauTaiDiemTach) {
                    bang(200, xs[0], "sai dau nong X ID=" + id);
                    bang(500, ys[0], "sai dau nong Y ID=" + id);
                }
            }
        }
    }

    private static void kiemTraSoDuongDanDacBiet() {
        bang(5, taoLoat(130).getCacDuongX().length, "MG42 khong du 5 vien");
        bang(4, taoLoat(140).getCacDuongX().length, "chuoi khong du 4 vien");
        bang(3, taoLoat(150).getCacDuongX().length, "shotgun khong du 3 vien");
        bang(3, taoLoat(160).getCacDuongX().length, "coi khong du 3 vien");
        bang(2, taoLoat(170).getCacDuongX().length, "ga khong du 2 duong");
        bang(4, taoLoat(180).getCacDuongX().length, "riu khong du 4 nhanh");
        bang(1, taoLoat(200).getCacDuongX().length, "laser thuong sai so vien");

        int[] soVienAvg = {2, 1, 1, 2, 1, 2, 4, 1};
        for (int i = 0; i < soVienAvg.length; i++) {
            int id = 391 + i;
            bang(soVienAvg[i], taoLoat(id).getCacDuongX().length,
                    "AVG ID=" + id + " dung so vien cua AVG khac");
        }
    }

    private static void kiemTraAnhDan() {
        for (ChickenQuanLyDanSung.DuLieuSung sung
                : ChickenQuanLyDanSung.layTatCa().values()) {
            String duongDan = ChickenQuanLyDanSung.layDuongDanAnhDanTheoIdSung(
                    sung.getIdSung(), 1);
            dung(duongDan != null && Files.isRegularFile(Path.of(duongDan)),
                    "thieu anh dan ID=" + sung.getIdSung() + " path=" + duongDan);
        }
    }

    private static void kiemTraVaChamDamage() {
        ChickenQuanLyDanSung.DuLieuSung at4 = batBuocCoSung(110);
        ChickenLoatDanServer.KetQua duongAt4 = taoLoat(110);
        ChickenChienBinh shooter = chienBinh((byte) 0, at4.getPartSung(), (byte) 0);
        shooter.x = 180;
        shooter.y = 520;
        ChickenChienBinh trungTam = mucTieuTaiDiem(
                (byte) 1, duongAt4.getCacDuongX()[0], duongAt4.getCacDuongY()[0], 12);
        ChickenChienBinh ganVuNo = chienBinh(
                (byte) 2, (short) 230, (byte) 8);
        ganVuNo.x = (short) (trungTam.x + 30);
        ganVuNo.y = trungTam.y;
        ChickenChienBinh oXa = chienBinh((byte) 3, (short) 230, (byte) 8);
        oXa.x = (short) (trungTam.x + 200);
        oXa.y = trungTam.y;
        ChickenKetQuaDan ketQuaAt4 = ChickenPhatBanServer.tao(
                shooter,
                (short) 200,
                (short) 500,
                (short) 45,
                (byte) 20,
                (byte) 12,
                at4,
                (byte) 0,
                (byte) 0,
                BAN_DO_TRONG,
                new ChickenChienBinh[]{shooter, trungTam, ganVuNo, oXa},
                (nguoiBan, mucTieu) -> true
        );
        dung(ketQuaAt4.satThuongTheoMucTieu.containsKey(trungTam),
                "AT4 trung truc tiep nhung khong co damage");
        dung(ketQuaAt4.satThuongTheoMucTieu.containsKey(ganVuNo),
                "AT4 khong gay damage lan trong ban kinh");
        dung(!ketQuaAt4.satThuongTheoMucTieu.containsKey(oXa),
                "AT4 gay damage ngoai ban kinh");

        ChickenQuanLyDanSung.DuLieuSung laser = batBuocCoSung(200);
        ChickenLoatDanServer.KetQua duongLaser = taoLoat(200);
        shooter = chienBinh((byte) 0, laser.getPartSung(), (byte) 0);
        shooter.x = 180;
        shooter.y = 520;
        ChickenChienBinh mucTieuLaser = mucTieuTaiDiem(
                (byte) 1, duongLaser.getCacDuongX()[0], duongLaser.getCacDuongY()[0], 12);
        ChickenChienBinh satBenLaser = chienBinh((byte) 2, (short) 230, (byte) 8);
        satBenLaser.x = (short) (mucTieuLaser.x + 30);
        satBenLaser.y = mucTieuLaser.y;
        ChickenKetQuaDan ketQuaLaser = ChickenPhatBanServer.tao(
                shooter,
                (short) 200,
                (short) 500,
                (short) 45,
                (byte) 20,
                (byte) 12,
                laser,
                (byte) 0,
                (byte) 0,
                BAN_DO_TRONG,
                new ChickenChienBinh[]{shooter, mucTieuLaser, satBenLaser},
                (nguoiBan, mucTieu) -> true
        );
        dung(ketQuaLaser.satThuongTheoMucTieu.containsKey(mucTieuLaser),
                "laser trung nguoi nhung khong co damage");
        dung(!ketQuaLaser.satThuongTheoMucTieu.containsKey(satBenLaser),
                "laser direct bi ro damage sang muc tieu ben canh");

        ChickenQuanLyDanSung.DuLieuSung captain = batBuocCoSung(395);
        ChickenLoatDanServer.KetQua duongCaptain = taoLoat(395);
        shooter = chienBinh((byte) 0, captain.getPartSung(), (byte) 5);
        shooter.x = 180;
        shooter.y = 520;
        ChickenChienBinh captainTarget1 = mucTieuTaiDiem(
                (byte) 1, duongCaptain.getCacDuongX()[0], duongCaptain.getCacDuongY()[0], 8);
        ChickenChienBinh captainTarget2 = mucTieuTaiDiem(
                (byte) 2, duongCaptain.getCacDuongX()[0], duongCaptain.getCacDuongY()[0], 20);
        ChickenKetQuaDan ketQuaCaptain = ChickenPhatBanServer.tao(
                shooter,
                (short) 200,
                (short) 500,
                (short) 45,
                (byte) 20,
                (byte) 12,
                captain,
                (byte) 0,
                (byte) 0,
                BAN_DO_TRONG,
                new ChickenChienBinh[]{shooter, captainTarget1, captainTarget2},
                (nguoiBan, mucTieu) -> true
        );
        dung(ketQuaCaptain.satThuongTheoMucTieu.containsKey(captainTarget1)
                        && ketQuaCaptain.satThuongTheoMucTieu.containsKey(captainTarget2),
                "Captain khong xuyen va tinh damage cho hai muc tieu");
    }

    private static void kiemTraCongThucAvg() {
        ChickenChienBinh ironMan = chienBinh((byte) 0, (short) 223, (byte) 1);
        ironMan.x = 100;
        ironMan.y = 500;
        ChickenChienBinh gan = chienBinh((byte) 1, (short) 230, (byte) 8);
        gan.x = 300;
        gan.y = 500;
        ChickenChienBinh xa = chienBinh((byte) 2, (short) 230, (byte) 8);
        xa.x = 500;
        xa.y = 500;
        ChickenTiaLaserIronMan.KetQua laser = ChickenTiaLaserIronMan.taoTrongTran(
                ironMan,
                new ChickenChienBinh[]{ironMan, gan, xa},
                (short) 0,
                800,
                600
        );
        bang(1, laser.getChiSoMucTieu(),
                "laser Iron Man khong dung o muc tieu dau tien");
        bang(100, laser.getBatDauX(), "laser Iron Man khong bat dau tu nguc");
        bang(482, laser.getBatDauY(), "laser Iron Man sai do cao nguc");

        ChickenCongThucBanUltron.DuongTia tia =
                ChickenCongThucBanUltron.taoTiaThang(
                        (short) 100, (short) 500, (short) 0, 800, 600);
        bang(51, tia.getX().length, "tia Ultron sai so nhip native");
        bang(tia.getX().length, tia.getY().length, "tia Ultron hong cap XY");
        ChickenCongThucBanUltron.LoatBaTia baTia =
                ChickenCongThucBanUltron.taoBaTiaHoiTu(
                        (short) 100, (short) 500, (short) 0, 800, 600);
        bang(3, baTia.getX().length, "skill Ultron khong tao du 3 tia");
        for (int i = 0; i < 3; i++) {
            int cuoi = baTia.getX()[i].length - 1;
            bang(baTia.getDichX(), baTia.getX()[i][cuoi],
                    "tia Ultron khong hoi tu X");
            bang(baTia.getDichY(), baTia.getY()[i][cuoi],
                    "tia Ultron khong hoi tu Y");
        }

        ChickenHoatAnhHawk.DuongDan muiTen =
                ChickenHoatAnhHawk.taoDuongBayLen((short) 100, (short) 480);
        ChickenHoatAnhHawk.LoatDuongDan bonMui =
                ChickenHoatAnhHawk.taoLoatBonMuiNoiDuoi(muiTen);
        bang(37, ChickenHoatAnhHawk.LOAI_DAN_MUI_TEN,
                "skill Hawk dung nham bullet type");
        bang(4, bonMui.getX().length, "skill Hawk khong du 4 mui ten");
        dung(bonMui.getX()[0].length < bonMui.getX()[3].length,
                "4 mui Hawk khong duoc noi duoi");

        dung(!ChickenCoCheHulk.daRaKhoiMap(
                        new short[]{100, 100}, new short[]{500, -200}, 800, 600),
                "Hulk bay len troi bi tinh la roi map");
        dung(ChickenCoCheHulk.daRaKhoiMap(
                        new short[]{100, -1}, new short[]{500, 500}, 800, 600),
                "Hulk bay qua canh map khong bi tinh la roi map");
    }

    private static void kiemTraNoLanSkillAvg() {
        int hawkTaiTam = ChickenSatThuongLanKyNang.tinhHawk(
                100, 0, 300, 482, 300, 500, false, null);
        int hawkGan = ChickenSatThuongLanKyNang.tinhHawk(
                100, 0, 300, 482, 330, 500, false, null);
        int hawkNgoai = ChickenSatThuongLanKyNang.tinhHawk(
                100, 0, 300, 482, 350, 500, false, null);
        bang(400, hawkTaiTam, "Hawk tai tam khong du damage 4 mui");
        dung(hawkGan > 0 && hawkGan < hawkTaiTam,
                "Hawk khong giam damage no theo khoang cach");
        bang(0, hawkNgoai, "Hawk gay damage ngoai ban kinh skill");
        bang(320, ChickenSatThuongLanKyNang.tinhHawk(
                        100, 20, 300, 482, 300, 500, false, null),
                "Hawk khong tru giap rieng cho muc tieu");

        short[] motTiaX = {(short) 300};
        short[] motTiaY = {(short) 500};
        int thorTaiTam = ChickenSatThuongLanKyNang.tinhThor(
                100, 0, motTiaX, motTiaY, 300, 500, false, null);
        int thorGan = ChickenSatThuongLanKyNang.tinhThor(
                100, 0, motTiaX, motTiaY, 330, 500, false, null);
        int thorNgoai = ChickenSatThuongLanKyNang.tinhThor(
                100, 0, motTiaX, motTiaY, 360, 500, false, null);
        bang(100, thorTaiTam, "Thor tai tam khong du damage mot tia");
        dung(thorGan > 0 && thorGan < thorTaiTam,
                "Thor khong giam damage no theo khoang cach");
        bang(0, thorNgoai, "Thor gay damage ngoai ban kinh skill");
        bang(200, ChickenSatThuongLanKyNang.tinhThor(
                        100, 0,
                        new short[]{300, 300},
                        new short[]{500, 500},
                        300, 500, false, null),
                "Thor khong cong damage khi trung vung no hai tia");
    }

    private static void kiemTraTheLucDiChuyen() {
        ChickenMauThuocTinhVatPham optionCu =
                ChickenQuanLyMayChu.iOptionTemplates.get(26);
        ChickenMauVatPham ironManCu = ChickenQuanLyMayChu.itemTemplates.get(391);
        ChickenMauVatPham hulkCu = ChickenQuanLyMayChu.itemTemplates.get(392);
        ChickenMauVatPham thorCu = ChickenQuanLyMayChu.itemTemplates.get(393);
        try {
            ChickenMauThuocTinhVatPham optionCuLy =
                    new ChickenMauThuocTinhVatPham();
            optionCuLy.ma = 26;
            optionCuLy.ten = "Cu ly di chuyen +#%";
            ChickenQuanLyMayChu.iOptionTemplates.put(26, optionCuLy);

            ChickenMauVatPham ironMan = mauSungThuNghiem(391);
            ironMan.thuocTinhs.add(new ChickenThuocTinhVatPham(26, 90));
            ChickenQuanLyMayChu.itemTemplates.put(391, ironMan);

            ChickenMauVatPham hulk = mauSungThuNghiem(392);
            hulk.thuocTinhs.add(new ChickenThuocTinhVatPham(26, 100));
            ChickenQuanLyMayChu.itemTemplates.put(392, hulk);
            ChickenQuanLyMayChu.itemTemplates.put(393, mauSungThuNghiem(393));

            ChickenNguoiChoi thuong = new ChickenNguoiChoi(null);
            bang(100, ChickenThanhDiChuyenAVG.quangDuongToiDa(thuong),
                    "sung thuong khong co 100 the luc co ban");

            ChickenNguoiChoi nguoiIronMan = new ChickenNguoiChoi(null);
            ChickenVatPham sungIronMan = new ChickenVatPham(391);
            sungIronMan.chiSo = 5;
            nguoiIronMan.itemBody[5] = sungIronMan;
            bang(190, ChickenThanhDiChuyenAVG.quangDuongToiDa(nguoiIronMan),
                    "Iron Man khong doc +90% tu template");

            // Option instance phải thay thế đúng option template cùng ID, không cộng đôi.
            sungIronMan.itemOptions.add(new ChickenThuocTinhVatPham(26, 50));
            bang(150, ChickenThanhDiChuyenAVG.quangDuongToiDa(nguoiIronMan),
                    "option 26 instance bi cong doi voi template");

            bang(200, ChickenThanhDiChuyenAVG.quangDuongToiDaTheoAvenger((byte) 2),
                    "bot Hulk khong doc +100% tu template");
            bang(100, ChickenThanhDiChuyenAVG.quangDuongToiDaTheoAvenger((byte) 3),
                    "Thor khong option 26 lai bi tang the luc");
            bang(100, ChickenThanhDiChuyenAVG.tinhQuangDuong(
                    (short) 0, (short) 0, (short) 100, (short) 60),
                    "di cheo bi tru the luc hai lan");

            ChickenThanhDiChuyenAVG.KetQuaDiChuyen biCat =
                    ChickenThanhDiChuyenAVG.gioiHan(
                            (short) 0, (short) 0,
                            (short) 300, (short) 100,
                            190);
            bang(190, biCat.getX(), "server khong cat X theo the luc con lai");
            bang(63, biCat.getY(), "server khong cat Y theo cung ti le");
            bang(0, biCat.getConLai(), "di qua gioi han van con the luc");
            dung(!biCat.isChapNhanToanBo(), "server chap nhan dich vuot the luc");
        } finally {
            khoiPhucMap(ChickenQuanLyMayChu.iOptionTemplates, 26, optionCu);
            khoiPhucMap(ChickenQuanLyMayChu.itemTemplates, 391, ironManCu);
            khoiPhucMap(ChickenQuanLyMayChu.itemTemplates, 392, hulkCu);
            khoiPhucMap(ChickenQuanLyMayChu.itemTemplates, 393, thorCu);
        }
    }

    private static ChickenMauVatPham mauSungThuNghiem(int ma) {
        return new ChickenMauVatPham(
                (short) ma,
                (byte) 5,
                (byte) 0,
                "AVG " + ma,
                "",
                (byte) 1,
                0,
                (short) 0,
                (short) (ma - 168),
                false
        );
    }

    private static <T> void khoiPhucMap(Map<Integer, T> map, int khoa, T giaTriCu) {
        if (giaTriCu == null) {
            map.remove(khoa);
        } else {
            map.put(khoa, giaTriCu);
        }
    }

    private static ChickenChienBinh mucTieuTaiDiem(
            byte chiSo,
            short[] xs,
            short[] ys,
            int chiSoDiem
    ) {
        int soDiem = Math.min(xs.length, ys.length);
        int diem = Math.max(1, Math.min(soDiem - 1, chiSoDiem));
        ChickenChienBinh mucTieu = chienBinh(chiSo, (short) 230, (byte) 8);
        mucTieu.x = xs[diem];
        mucTieu.y = (short) (ys[diem] + 18);
        return mucTieu;
    }

    private static void kiemTraSkillCheoAvg() throws Exception {
        DieuKhienDonGian dieuKhien = new DieuKhienDonGian((byte) 0);
        ChickenChienBinh ironMan = chienBinh((byte) 0, (short) 223, (byte) 1);
        ChickenChienBinh ultron = chienBinh((byte) 0, (short) 230, (byte) 8);

        ChickenKyNangDacBietUltron skillUltron =
                new ChickenKyNangDacBietUltron(dieuKhien);
        ironMan.ultronDaGuiMenu = true;
        dung(!skillUltron.kichHoatBanX3(ironMan),
                "Iron Man kich hoat duoc skill Ultron");
        dung(!ironMan.ultronDangBanX3 && !ironMan.ultronDaDungKyNang,
                "skill Ultron lam ban trang thai AVG khac");
        ultron.ultronDaGuiMenu = true;
        dung(skillUltron.kichHoatBanX3(ultron),
                "Ultron dung AVG va dung luot lai khong kich hoat duoc");
        dung(skillUltron.dangBanX3(ultron), "Ultron khong vao trang thai x3");
        dung(!skillUltron.kichHoatBanX3(ultron), "Ultron kich hoat x3 lap");
        skillUltron.sauKhiDaBan(ultron);
        dung(!ultron.ultronDangBanX3, "x3 khong tat sau khi ban");

        ChickenKyNangDacBietIronMan skillIronMan =
                new ChickenKyNangDacBietIronMan(dieuKhien);
        ultron.ironManDaGuiMenu = true;
        dung(!skillIronMan.kichHoat(ultron),
                "Ultron kich hoat duoc laser Iron Man");
        ironMan.ironManDaGuiMenu = true;
        dung(skillIronMan.kichHoat(ironMan),
                "Iron Man dung AVG lai khong kich hoat duoc laser");
        dung(skillIronMan.dangChoBan(ironMan), "laser khong vao trang thai cho ban");
        skillIronMan.sauKhiBanHoacBoLuot(ironMan);
        dung(!ironMan.ironManLaserSanSang, "laser khong tat sau hanh dong");

        ChickenChienBinh hawk = chienBinh((byte) 0, (short) 229, (byte) 7);
        ChickenKyNangDacBietHawk skillHawk = new ChickenKyNangDacBietHawk(
                new ChickenChienBinh[]{hawk, ultron}, null, dieuKhien);
        skillHawk.sauKhiBanThuong(ironMan);
        bang(0, ironMan.hawkSoLuotBan, "Iron Man nap duoc skill Hawk");
        ironMan.hawkDaGuiChonMucTieu = true;
        skillHawk.nhanLenh(ironMan, tinSkill(1, 0));
        dung(!ironMan.hawkDaDungKyNang,
                "Iron Man gui packet Hawk va dung duoc skill Hawk");
        skillHawk.sauKhiBanThuong(hawk);
        bang(1, hawk.hawkSoLuotBan, "Hawk khong nap sau phat thuong");

        ChickenKyNangDacBietThor skillThor = new ChickenKyNangDacBietThor(
                new ChickenChienBinh[]{ironMan, ultron}, null, dieuKhien);
        ironMan.thorDaGuiMenu = true;
        skillThor.nhanLenh(ironMan, tinSkill(3, 0));
        dung(!ironMan.thorDaDungKyNang,
                "Iron Man gui packet Thor va dung duoc skill Thor");
        ChickenChienBinh thor = chienBinh((byte) 0, (short) 225, (byte) 3);
        skillThor = new ChickenKyNangDacBietThor(
                new ChickenChienBinh[]{thor, ultron}, null, dieuKhien);
        skillThor.nhanLenh(thor, tinSkill(3, 0));
        dung(!thor.thorDaDungKyNang,
                "Thor dung skill khi server chua phat menu/token");

        ChickenKyNangDacBietLoki skillLoki = new ChickenKyNangDacBietLoki(
                new ChickenChienBinh[]{ironMan, ultron}, dieuKhien);
        ironMan.lokiDaGuiMenu = true;
        skillLoki.nhanLenh(ironMan, tinSkill(0, 1));
        dung(!ironMan.lokiDaDungKyNang,
                "Iron Man gui packet Loki va dung duoc skill Loki");
        ChickenChienBinh loki = nguoiChoiThat((byte) 0, (short) 226, (byte) 4);
        skillLoki = new ChickenKyNangDacBietLoki(
                new ChickenChienBinh[]{loki, ultron}, dieuKhien);
        skillLoki.nhanLenh(loki, tinSkill(0, 1));
        dung(!loki.lokiDaDungKyNang,
                "Loki dung skill khi server chua phat menu/token");
    }

    private static void kiemTraHawkSaiLuot() throws Exception {
        ChickenChienBinh hawk = chienBinh((byte) 0, (short) 229, (byte) 7);
        ChickenChienBinh target = chienBinh((byte) 1, (short) 230, (byte) 8);
        DieuKhienDonGian saiLuot = new DieuKhienDonGian((byte) 1);
        ChickenKyNangDacBietHawk skill = new ChickenKyNangDacBietHawk(
                new ChickenChienBinh[]{hawk, target}, null, saiLuot);
        hawk.hawkDaGuiChonMucTieu = true;
        skill.nhanLenh(hawk, tinSkill(1, 1));
        dung(!hawk.hawkDaDungKyNang, "Hawk dung skill ngoai luot");

        DieuKhienDonGian dungLuot = new DieuKhienDonGian((byte) 0);
        skill = new ChickenKyNangDacBietHawk(
                new ChickenChienBinh[]{hawk, target}, null, dungLuot);
        hawk.hawkDaGuiChonMucTieu = false;
        skill.nhanLenh(hawk, tinSkill(1, 1));
        dung(!hawk.hawkDaDungKyNang,
                "Hawk dung skill khi server chua phat menu/token");
    }

    private static void kiemTraLokiSaoChepNguoiChoi() throws Exception {
        ChickenChienBinh loki = nguoiChoiThat((byte) 0, (short) 226, (byte) 4);
        ChickenChienBinh ultron = nguoiChoiThat((byte) 1, (short) 230, (byte) 8);
        // Mô phỏng quyền đã được server xác nhận từ trang bị Ultron thật.
        ultron.duocPhepBay = true;
        ultron.ten = "UltronTarget";
        ultron.mauToiDa = 999;
        ultron.hp = 777;
        ultron.tanCong = 321;
        ultron.giap = 123;
        ultron.mayMan = 45;
        ultron.tocDo = 67;
        ultron.theLucDiChuyenToiDa = 200;
        ultron.ultronDaDungKyNang = true;
        int maTaiKhoanLoki = loki.ma;
        byte chiSoLoki = loki.chiSo;
        short xLoki = loki.x;
        short yLoki = loki.y;
        DieuKhienDonGian dieuKhien = new DieuKhienDonGian((byte) 0);
        ChickenKyNangDacBietLoki skill = new ChickenKyNangDacBietLoki(
                new ChickenChienBinh[]{loki, ultron}, dieuKhien);

        loki.lokiDaGuiMenu = true;
        skill.nhanLenh(loki, tinSkill(0, 1));
        dung(loki.lokiDaDungKyNang, "Loki khong bien hinh");
        bang(230, loki.maVuKhi, "Loki khong copy sung cua Ultron");
        bang(8, loki.avenger, "Loki khong copy AVG cua Ultron");
        dung(ChickenCoCheBayAVG.coTheBay(loki),
                "Loki copy Ultron hop le nhung khong copy quyen bay server");
        bang(321, loki.tanCong, "Loki khong copy tan cong");
        bang(123, loki.giap, "Loki khong copy giap");
        bang(45, loki.mayMan, "Loki khong copy may man");
        bang(67, loki.tocDo, "Loki khong copy toc do");
        bang(200, loki.theLucDiChuyenToiDa,
                "Loki khong copy the luc di chuyen toi da");
        bang(999, loki.mauToiDa, "Loki khong copy mau toi da");
        bang(777, loki.hp, "Loki khong copy HP");
        dung(loki.ultronDaDungKyNang, "Loki khong copy cooldown skill muc tieu");
        bang(maTaiKhoanLoki, loki.ma, "Loki bi doi ma tai khoan");
        bang(chiSoLoki, loki.chiSo, "Loki bi doi battle index");
        bang(xLoki, loki.x, "Loki bi teleport X khi bien hinh");
        bang(yLoki, loki.y, "Loki bi teleport Y khi bien hinh");
        bang(1, dieuKhien.soLanBienHinh, "packet bien hinh khong gui dung mot lan");

        ChickenChienBinh lokiKhac = nguoiChoiThat(
                (byte) 2, (short) 226, (byte) 4);
        ChickenChienBinh boss = chienBinh((byte) 3, (short) 230, (byte) 8);
        skill = new ChickenKyNangDacBietLoki(
                new ChickenChienBinh[]{lokiKhac, boss},
                new DieuKhienDonGian((byte) 2));
        lokiKhac.lokiDaGuiMenu = true;
        skill.nhanLenh(lokiKhac, tinSkill(0, 3));
        dung(!lokiKhac.lokiDaDungKyNang, "Loki van sao chep duoc bot/boss");
        bang(4, lokiKhac.avenger, "Loki bi doi AVG khi chon bot/boss");
    }

    private static ChickenLoatDanServer.KetQua taoLoat(int idSung) {
        return ChickenLoatDanServer.tao(
                (short) 200, (short) 500,
                (short) 180, (short) 520,
                (short) 45, (byte) 20, (byte) 12,
                batBuocCoSung(idSung), (byte) 0, (byte) 0, BAN_DO_TRONG
        );
    }

    private static int soDuongMongDoi(ChickenQuanLyDanSung.DuLieuSung sung) {
        if (sung.getNhomSung() == 6 && sung.getLoaiDan() == 19) {
            return 2;
        }
        if (sung.getNhomSung() == 8 && sung.getLoaiDan() == 17) {
            return 4;
        }
        return sung.getSoVienMoiLoat() & 0xFF;
    }

    private static ChickenQuanLyDanSung.DuLieuSung batBuocCoSung(int id) {
        ChickenQuanLyDanSung.DuLieuSung sung = ChickenQuanLyDanSung.theoIdSung(id);
        if (sung == null) {
            throw new AssertionError("khong co sung ID=" + id);
        }
        return sung;
    }

    private static ChickenChienBinh chienBinh(
            byte chiSo,
            short partSung,
            byte avenger
    ) {
        return new ChickenChienBinh(
                chiSo, (short) (100 + chiSo * 100), (short) 500,
                "P" + chiSo, partSung, avenger);
    }

    private static ChickenChienBinh nguoiChoiThat(
            byte chiSo,
            short partSung,
            byte avenger
    ) {
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(null);
        nguoiChoi.ma = 10_000 + (chiSo & 0xFF);
        nguoiChoi.ten = "RealP" + (chiSo & 0xFF);
        nguoiChoi.wp = partSung;
        nguoiChoi.avenger = avenger;
        ChickenChienBinh ketQua = new ChickenChienBinh(
                nguoiChoi,
                chiSo,
                (short) (100 + (chiSo & 0xFF) * 100),
                (short) 500
        );
        // Test không nạp DB/item template nên gán trực tiếp bộ chiến đấu mẫu.
        ketQua.maVuKhi = partSung;
        ketQua.avenger = avenger;
        return ketQua;
    }

    private static ChickenTinNhan tinSkill(int action, int index) {
        return new ChickenTinNhan((byte) -91,
                new byte[]{(byte) action, (byte) index});
    }

    private static byte[] taoPacket(
            byte loaiDanClient,
            short xClient,
            short yClient,
            short goc,
            int luc,
            int lucPhu,
            int soPhatClient,
            boolean haiLuc
    ) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(out);
        data.writeByte(loaiDanClient);
        data.writeShort(xClient);
        data.writeShort(yClient);
        data.writeShort(goc);
        data.writeByte(luc);
        if (haiLuc) {
            data.writeByte(lucPhu);
        }
        data.writeByte(soPhatClient);
        data.flush();
        return out.toByteArray();
    }

    private static byte[] taoPacketToaDo(short x, short y) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(out);
        data.writeShort(x);
        data.writeShort(y);
        data.flush();
        return out.toByteArray();
    }

    private static void chay(String ten, Viec viec) throws Exception {
        try {
            viec.chay();
            daChay++;
            System.out.println("[PASS] " + ten);
        } catch (Throwable loi) {
            System.out.println("[FAIL] " + ten + ": " + loi.getMessage());
            if (loi instanceof Exception exception) {
                throw exception;
            }
            throw new AssertionError(loi);
        }
    }

    private static void dung(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private static void bang(long mongDoi, long thucTe, String thongBao) {
        if (mongDoi != thucTe) {
            throw new AssertionError(thongBao
                    + " expected=" + mongDoi + " actual=" + thucTe);
        }
    }

    private static void khacNull(Object giaTri, String thongBao) {
        dung(giaTri != null, thongBao);
    }

    private static void laNull(Object giaTri, String thongBao) {
        dung(giaTri == null, thongBao);
    }

    @FunctionalInterface
    private interface Viec {
        void chay() throws Exception;
    }

    private static final ChickenQuanLyCongThucSung.KiemTraBanDo BAN_DO_TRONG =
            new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                @Override
                public int getWidth() { return 2_400; }

                @Override
                public int getHeight() { return 1_200; }

                @Override
                public boolean coVaCham(short x, short y) { return false; }
            };

    private static final class DieuKhienDonGian
            implements ChickenKyNangDacBietUltron.DieuKhienTranDau,
            ChickenKyNangDacBietIronMan.DieuKhienTranDau,
            ChickenKyNangDacBietHawk.DieuKhienTranDau,
            ChickenKyNangDacBietThor.DieuKhienTranDau,
            ChickenKyNangDacBietLoki.DieuKhienTranDau {
        private final byte luot;
        private int soLanBienHinh;

        private DieuKhienDonGian(byte luot) {
            this.luot = luot;
        }

        @Override
        public boolean daKetThuc() { return false; }

        @Override
        public byte luotHienTai() { return this.luot; }

        @Override
        public void guiMenuUltron(ChickenChienBinh ultron) { }

        @Override
        public void guiMenuIronMan(ChickenChienBinh ironMan) { }

        @Override
        public void guiHoatAnhMuiTen(
                ChickenChienBinh hawk,
                short goc,
                ChickenHoatAnhHawk.DuongDan duongDan
        ) { }

        @Override
        public void guiTiaSet(
                ChickenChienBinh thor,
                byte loaiHieuUng,
                short[] cacX,
                short[] cacY
        ) { }

        @Override
        public void gaySatThuong(ChickenChienBinh mucTieu, int satThuong) { }

        @Override
        public void sangLuot() { }

        @Override
        public void guiMenuLoki(ChickenChienBinh loki) { }

        @Override
        public void guiChonMucTieuLoki(ChickenChienBinh loki) { }

        @Override
        public void guiBienHinh(
                ChickenChienBinh loki,
                ChickenChienBinh mucTieu
        ) {
            this.soLanBienHinh++;
        }

        @Override
        public void capNhatMau(ChickenChienBinh loki) { }
    }
}
