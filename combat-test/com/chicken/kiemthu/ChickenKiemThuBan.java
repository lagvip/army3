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
import com.chicken.chien.ChickenLoatBanUltronServer;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.chien.ChickenPhatBanServer;
import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.chien.ChickenQuanLyCongThucSung;
import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.chien.ChickenTinhSatThuongNo;
import com.chicken.chien.ChickenSieuCao;
import com.chicken.chien.ChickenThoiGianHoatAnhDan;
import com.chicken.chien.ChickenYeuCauBanServer;
import com.chicken.chien.ChickenYeuCauToaDoServer;
import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.bando.ChickenDuLieuBanDo;
import com.chicken.luyentap.ChickenDuLieuPhatBanLuyenTap;
import com.chicken.luyentap.ChickenXuLyBanLuyenTap;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenPhien;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mang.ChickenXuLyTinTestSupport;
import com.chicken.mohinh.ChickenLuyenTapTestSupport;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.nhapvai.ChickenKhu;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.sanhcho.ThanhVienBoss;
import com.chicken.phong.boss.sanhcho.ChickenKinhTeBossTestSupport;
import com.chicken.phong.boss.trandau.ChickenKetQuaTranBoss;
import com.chicken.phong.boss.trandau.baovay.BossBaoVay;
import com.chicken.phong.boss.trandau.datbom.BossDatBom;
import com.chicken.phong.boss.trandau.haitoathap.BossHaiToaThap;
import com.chicken.phong.boss.trandau.khicau.BossKhiCau;
import com.chicken.phong.boss.trandau.rong.BossRong;
import com.chicken.phong.boss.trandau.rua.BossRua;
import com.chicken.phong.boss.trandau.rua.CauHinhBossRua;
import com.chicken.phong.boss.trandau.rua.ChieuBossRua;
import com.chicken.phong.boss.trandau.rua.DiChuyenBossRua;
import com.chicken.phong.boss.trandau.rua.DocBossRua;
import com.chicken.phong.boss.trandau.ruarong.BossRuaRong;
import com.chicken.phong.boss.trandau.ruarong.CauHinhBossRuaRong;
import com.chicken.phong.boss.trandau.ruarong.BossRuaRongTanCong;
import com.chicken.vatpham.ChickenMauThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.tienich.ChickenTienIch;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** Bo kiem thu khong can database, client hay server dang chay. */
public final class ChickenKiemThuBan {
    private static int daChay;

    private ChickenKiemThuBan() {
    }

    public static void main(String[] args) throws Exception {
        chay("phien gioi han spam, di chuyen, skill va resource",
                ChickenKiemThuBan::kiemTraGioiHanPhien);
        chay("dang danh boss thi cam chuyen phong va tao tran khac",
                ChickenXuLyTinTestSupport::tuKiemTra);
        chay("PvP khong tin toa do, dan va luot tu client",
                ChickenKiemThuBan::kiemTraPvPTheoTrangThaiServer);
        chay("luyen tap khong tin toa do, dan va luot tu client",
                ChickenLuyenTapTestSupport::tuKiemTra);
        chay("toan bo 7 map boss khoa action truoc khi bat dau",
                ChickenKiemThuBan::kiemTraTatCaMapBossKhoaActionSom);
        chay("khong doi trang bi trong tran",
                ChickenKiemThuBan::kiemTraKhoaTrangBiTrongTran);
        chay("packet gia bi bo qua", ChickenKiemThuBan::kiemTraPacketGia);
        chay("packet loi bi tu choi", ChickenKiemThuBan::kiemTraPacketLoi);
        chay("CMD 53 khong tin toa do client", ChickenKiemThuBan::kiemTraCmd53KhongTinClient);
        chay("chi Iron Man va Ultron co quyen bay", ChickenKiemThuBan::kiemTraQuyenBayServer);
        chay("toan bo mapping sung", ChickenKiemThuBan::kiemTraToanBoMappingSung);
        chay("so duong dan dac biet", ChickenKiemThuBan::kiemTraSoDuongDanDacBiet);
        chay("va cham va damage sung thuong", ChickenKiemThuBan::kiemTraVaChamDamage);
        chay("phong boss cho ban ban than va dong doi",
                ChickenKiemThuBan::kiemTraFriendlyFirePhongBoss);
        chay("cong thuc duong dan AVG", ChickenKiemThuBan::kiemTraCongThucAvg);
        chay("Ultron x3 co ba va cham doc lap",
                ChickenKiemThuBan::kiemTraLoatUltronDocLap);
        chay("no lan skill Thor va Hawk", ChickenKiemThuBan::kiemTraNoLanSkillAvg);
        chay("the luc di chuyen theo option 26", ChickenKiemThuBan::kiemTraTheLucDiChuyen);
        chay("anh dan ton tai", ChickenKiemThuBan::kiemTraAnhDan);
        chay("skill khong the dung cheo AVG", ChickenKiemThuBan::kiemTraSkillCheoAvg);
        chay("skill Hawk khong the dung sai luot", ChickenKiemThuBan::kiemTraHawkSaiLuot);
        chay("Loki copy nguoi choi va chan bot boss", ChickenKiemThuBan::kiemTraLokiSaoChepNguoiChoi);
        chay("damage no va tuong che", ChickenTinhSatThuongNo::tuKiemTra);
        chay("sieu cao phai trung hitbox du bo qua tuong",
                ChickenKiemThuBan::kiemTraSieuCaoTrungHitbox);
        chay("nguoi roi tran khong con nhan packet chien dau",
                ChickenKiemThuBan::kiemTraNguoiRoiTranKhongConPhien);
        chay("roi khu RPG khong tin chiSo phong boss",
                ChickenKiemThuBan::kiemTraRoiKhuRpgKhongTinChiSo);
        chay("Rua ban dan doc va server tinh damage",
                ChickenKiemThuBan::kiemTraDanRiengRuaMap54);
        chay("Rua map 54 gui packet ban that",
                ChickenKiemThuBan::kiemTraBossRuaGuiPacketBan);
        chay("Rua giu da ngang de vuot khoi mep dia hinh",
                ChickenKiemThuBan::kiemTraRuaVuotMepDiaHinh);
        chay("Rua map 54 tu di chuyen xong phai ban",
                ChickenKiemThuBan::kiemTraBossRuaDiChuyenXongPhaiBan);
        chay("Rua map 58 tu di chuyen xong phai ban",
                ChickenKiemThuBan::kiemTraBossRuaRongDiChuyenXongPhaiBan);
        chay("Rua chon dung chu ky ban va dam da",
                ChickenKiemThuBan::kiemTraChuKyChieuRua);
        chay("da Rua ghim den khi bi pha trung loi",
                ChickenKiemThuBan::kiemTraVongDoiGhimDaRua);
        chay("Iron Man va Ultron bay qua da Rua",
                ChickenKiemThuBan::kiemTraAvgBayQuaDaRua);
        chay("boss khong hoa va cung chet van thua",
                ChickenKiemThuBan::kiemTraLuatKetThucBoss);
        chay("tran boss chi cong bo sau khi khoi tao xong",
                ChickenKiemThuBan::kiemTraCongBoTranSauKhoiTao);
        chay("mot client loi khong chan ket qua dong doi",
                ChickenKiemThuBan::kiemTraLoiGuiKetQuaDuocCoLap);
        chay("kinh te boss chi thu khi bat dau, rollback va idempotent",
                ChickenKinhTeBossTestSupport::tuKiemTra);
        chay("Rua giet het team gui ket qua thua",
                ChickenKiemThuBan::kiemTraRuaGietTeamKhongHoa);
        chay("boss chet truoc team chet sau cung don van thua co EXP",
                ChickenKiemThuBan::kiemTraCungDonCungChetVanThuaCoExp);
        chay("Rua map 54 luot hai di chuyen roi dam da",
                ChickenKiemThuBan::kiemTraBossRuaLuotHaiDamDa);
        chay("Rua map 58 luot hai di chuyen roi dam da",
                ChickenKiemThuBan::kiemTraBossRuaRongLuotHaiDamDa);
        chay("thanh HP dau dung thang 25 nac cua client",
                ChickenKiemThuBan::kiemTraThanhMauDau25Nac);
        chay("thoi gian animation dan theo quy dao server",
                ChickenKiemThuBan::kiemTraThoiGianHoatAnhDan);
        chay("map 54 server tu chuyen luot, CMD23 khong tua nhanh",
                ChickenKiemThuBan::kiemTraDongBoCmd23BossRua);
        chay("map 58 server tu chuyen luot, CMD23 khong tua nhanh",
                ChickenKiemThuBan::kiemTraDongBoCmd23BossRuaRong);
        chay("gui CMD24 de client doi sang luot boss",
                ChickenKiemThuBan::kiemTraGuiLuotClientChoBoss);
        chay("Rua map 54 thieu nua mat chan phai roi ve phia nguoi",
                ChickenKiemThuBan::kiemTraRuaRoiKhoiMepMap54);
        chay("Rua map 54 moi luot chi chay 130 va phai dap dat",
                ChickenKiemThuBan::kiemTraRuaKhongDiXaVaKhongTreo);
        chay("be va gach duong chay Boss Rua khong the bi pha",
                ChickenKiemThuBan::kiemTraDiaHinhCoDinhBossRua);
        System.out.println("COMBAT_TEST_OK tests=" + daChay
                + " weapons=" + ChickenQuanLyDanSung.layTatCa().size());
    }

    private static void kiemTraPvPTheoTrangThaiServer() throws Exception {
        DichVuBatPacket dichVuMot = new DichVuBatPacket();
        DichVuBatPacket dichVuHai = new DichVuBatPacket();
        ChickenNguoiChoi nguoiMot = new ChickenNguoiChoi(dichVuMot);
        nguoiMot.ma = 93_001;
        nguoiMot.ten = "PvPAuthoritativeP0";
        nguoiMot.wp = 57;
        ChickenNguoiChoi nguoiHai = new ChickenNguoiChoi(dichVuHai);
        nguoiHai.ma = 93_002;
        nguoiHai.ten = "PvPAuthoritativeP1";
        nguoiHai.wp = 57;

        ChickenQuanLyChien tran = new ChickenQuanLyChien(
                null, new ChickenNguoiChoi[]{nguoiMot, nguoiHai}, (byte) 0);
        try {
            Field danhSach = ChickenQuanLyChien.class.getDeclaredField(
                    "chienBinhs");
            danhSach.setAccessible(true);
            ChickenChienBinh[] chienBinhs =
                    (ChickenChienBinh[]) danhSach.get(tran);
            ChickenChienBinh p0 = chienBinhs[0];
            ChickenChienBinh p1 = chienBinhs[1];
            khacNull(p0, "PvP khong tao chien binh P0");
            khacNull(p1, "PvP khong tao chien binh P1");
            p0.maVuKhi = 57;
            p1.maVuKhi = 57;
            p0.hp = p0.mauToiDa = 10_000;
            p1.hp = p1.mauToiDa = 10_000;
            p0.quangDuongDiChuyenConLai = 40;
            p1.quangDuongDiChuyenConLai = 40;

            Field luot = ChickenQuanLyChien.class.getDeclaredField(
                    "luotHienTai");
            luot.setAccessible(true);
            luot.setByte(tran, (byte) 0);

            short xMotBanDau = p0.x;
            short yMotBanDau = p0.y;
            short xHaiBanDau = p1.x;
            short yHaiBanDau = p1.y;

            tran.diChuyen(nguoiHai, new ChickenTinNhan(
                    (byte) 21,
                    taoPacketToaDo((short) 30_000, (short) -30_000)));
            bang(xHaiBanDau, p1.x,
                    "PvP cho nguoi choi di chuyen sai luot X");
            bang(yHaiBanDau, p1.y,
                    "PvP cho nguoi choi di chuyen sai luot Y");

            tran.ban(nguoiHai, new ChickenTinNhan(
                    (byte) 22,
                    taoPacket((byte) 127, (short) 30_000,
                            (short) -30_000, (short) 721,
                            255, 0, 255, false)));
            bang(0, dichVuMot.demLenh(22),
                    "PvP phat dan tu nguoi choi sai luot");

            p1.avenger = ChickenKyNangDacBietHawk.AVG_HAWK;
            tran.nhanLenhKyNangDacBiet(
                    nguoiHai, tinSkill(0, 0));
            dung(!p1.hawkDaGuiChonMucTieu
                            && !p1.hawkDaDungKyNang,
                    "PvP mo skill cho nguoi choi sai luot");

            tran.diChuyen(nguoiMot, new ChickenTinNhan(
                    (byte) 21,
                    taoPacketToaDo((short) 30_000, (short) -30_000)));
            int daDi = Math.abs(p0.x - xMotBanDau);
            dung(daDi <= 40,
                    "PvP teleport xa hon the luc server");
            dung(p0.y >= 0,
                    "PvP tin Y am tu client");

            short xServer = p0.x;
            short yServer = p0.y;
            int truocPhatBan = dichVuMot.demLenh(22);
            tran.ban(nguoiMot, new ChickenTinNhan(
                    (byte) 22,
                    taoPacket((byte) 127, (short) 30_000,
                            (short) -30_000, (short) 721,
                            255, 0, 255, false)));
            bang(truocPhatBan + 1, dichVuMot.demLenh(22),
                    "PvP khong phat dung mot packet ban hop le");
            bang(truocPhatBan + 1, dichVuHai.demLenh(22),
                    "PvP khong dong bo phat ban cho doi thu");
            bang(xServer, p0.x,
                    "packet ban PvP ghi de X server");
            bang(yServer, p0.y,
                    "packet ban PvP ghi de Y server");
            kiemTraPacketBanPvP(
                    dichVuMot.layTinCuoi(22), xServer, yServer);
            dung(luot.getByte(tran) != 0,
                    "PvP khong chuyen luot sau phat ban");

            tran.ban(nguoiMot, new ChickenTinNhan(
                    (byte) 22,
                    taoPacket((byte) 0, xServer, yServer,
                            (short) 45, 20, 0, 1, false)));
            bang(truocPhatBan + 1, dichVuMot.demLenh(22),
                    "PvP nhan phat ban lap sau khi da het luot");

            ChickenNguoiChoi nguoiNgoai =
                    new ChickenNguoiChoi(new DichVuBatPacket());
            nguoiNgoai.ma = 93_099;
            nguoiNgoai.ten = "PvPNotInMatch";
            tran.diChuyen(nguoiNgoai, new ChickenTinNhan(
                    (byte) 21, taoPacketToaDo((short) 0, (short) 0)));
            tran.ban(nguoiNgoai, new ChickenTinNhan(
                    (byte) 22,
                    taoPacket((byte) 0, (short) 0, (short) 0,
                            (short) 0, 1, 0, 1, false)));
            bang(truocPhatBan + 1, dichVuMot.demLenh(22),
                    "nguoi ngoai tran gui duoc packet ban PvP");
        } finally {
            tran.dungBot();
            tran.khiNguoiChoiRoi(nguoiMot);
            tran.khiNguoiChoiRoi(nguoiHai);
        }
    }

    private static void kiemTraPacketBanPvP(
            ChickenTinNhan tin,
            short xServer,
            short yServer
    ) throws Exception {
        khacNull(tin, "khong bat duoc CMD22 PvP");
        DataInputStream in = new ChickenTinNhan(
                (byte) 22, tin.layDuLieu()).boDoc();
        bang(1, in.readUnsignedByte(), "AT4 PvP sai shoot type");
        bang(0, in.readUnsignedByte(), "CMD22 PvP sai byte danh dau");
        bang(0, in.readUnsignedByte(), "CMD22 PvP sai slot nguoi ban");
        bang(0, in.readUnsignedByte(),
                "client fake duoc loai dan PvP");
        bang(xServer, in.readShort(),
                "CMD22 PvP gui X client thay vi X server");
        bang(yServer, in.readShort(),
                "CMD22 PvP gui Y client thay vi Y server");
        bang(1, in.readUnsignedShort(),
                "goc PvP 721 khong duoc chuan hoa ve 1");
        bang(1, in.readUnsignedByte(),
                "client fake duoc so loat AT4 PvP");
        bang(1, in.readUnsignedByte(),
                "client fake duoc so vien AT4 PvP");
    }

    private static void kiemTraTatCaMapBossKhoaActionSom()
            throws Exception {
        int[] mapIds = {50, 51, 52, 53, 54, 55, 58};
        for (int mapId : mapIds) {
            DichVuBatPacket dichVu = new DichVuBatPacket();
            ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
            nguoiChoi.ma = 95_000 + mapId;
            nguoiChoi.ten = "BossModeSecurity" + mapId;
            nguoiChoi.wp = 57;
            SanhChoBoss sanh = new SanhChoBoss(
                    (byte) 4, (byte) (mapId - 50), (byte) mapId,
                    (byte) 8, 1_000);
            dung(sanh.themThanhVien(new ThanhVienBoss(
                            nguoiChoi, (byte) 0, mapId, true)),
                    "khong them duoc nguoi vao sanh boss map=" + mapId);

            ChickenQuanLyChien tran = taoTranBossTest(mapId, sanh);
            khacNull(tran, "khong tao duoc tran boss map=" + mapId);
            try {
                dung(ChickenQuanLyChien.timTranDauCuaNguoiChoi(nguoiChoi)
                                == tran,
                        "tran boss khong dang ky router map=" + mapId);
                Method chup = tran.getClass().getMethod("chupChienBinh");
                ChickenChienBinh[] chienBinhs =
                        (ChickenChienBinh[]) chup.invoke(tran);
                ChickenChienBinh nguoiTrongTran = chienBinhs[0];
                khacNull(nguoiTrongTran,
                        "boss khong tao nguoi choi map=" + mapId);
                nguoiTrongTran.maVuKhi = 57;
                nguoiTrongTran.avenger =
                        ChickenKyNangDacBietHawk.AVG_HAWK;
                nguoiTrongTran.quangDuongDiChuyenConLai = 100;

                short xCu = nguoiTrongTran.x;
                short yCu = nguoiTrongTran.y;
                int hpCu = nguoiTrongTran.hp;
                int soDiChuyenCu = dichVu.demLenh(21);
                int soBanCu = dichVu.demLenh(22);

                tran.diChuyen(nguoiChoi, new ChickenTinNhan(
                        (byte) 21,
                        taoPacketToaDo(
                                (short) 30_000, (short) -30_000)));
                tran.ban(nguoiChoi, new ChickenTinNhan(
                        (byte) 22,
                        taoPacket((byte) 127, (short) 30_000,
                                (short) -30_000, (short) 721,
                                255, 0, 255, false)));
                tran.nhanLenhKyNangDacBiet(
                        nguoiChoi, tinSkill(0, 0));

                bang(xCu, nguoiTrongTran.x,
                        "boss cho di chuyen truoc tran map=" + mapId);
                bang(yCu, nguoiTrongTran.y,
                        "boss doi Y truoc tran map=" + mapId);
                bang(hpCu, nguoiTrongTran.hp,
                        "boss doi HP truoc tran map=" + mapId);
                bang(soDiChuyenCu, dichVu.demLenh(21),
                        "boss broadcast di chuyen som map=" + mapId);
                bang(soBanCu, dichVu.demLenh(22),
                        "boss broadcast dan som map=" + mapId);
                dung(!nguoiTrongTran.hawkDaGuiChonMucTieu
                                && !nguoiTrongTran.hawkDaDungKyNang,
                        "boss mo skill truoc tran map=" + mapId);
            } finally {
                tran.dungBot();
            }
            laNull(ChickenQuanLyChien.timTranDauCuaNguoiChoi(nguoiChoi),
                    "boss khong go router khi dung map=" + mapId);
        }
    }

    private static ChickenQuanLyChien taoTranBossTest(
            int mapId,
            SanhChoBoss sanh
    ) {
        return switch (mapId) {
            case 50 -> new BossBaoVay(sanh);
            case 51 -> new BossHaiToaThap(sanh);
            case 52 -> new BossKhiCau(sanh);
            case 53 -> new BossDatBom(sanh);
            case 54 -> new BossRua(sanh);
            case 55 -> new BossRong(sanh);
            case 58 -> new BossRuaRong(sanh);
            default -> null;
        };
    }

    private static void kiemTraGioiHanPhien() {
        ChickenPhien phienDiChuyen = new ChickenPhien(null, 90_001);
        dung(phienDiChuyen.choPhepXuLyLenh(21, 1_000L),
                "packet di chuyen dau tien bi chan");
        dung(!phienDiChuyen.choPhepXuLyLenh(21, 1_010L),
                "client spam CMD21 duoi 30ms van duoc xu ly");
        dung(phienDiChuyen.choPhepXuLyLenh(21, 1_030L),
                "CMD21 dung nhip lai bi chan");

        ChickenPhien phienSkill = new ChickenPhien(null, 90_002);
        dung(phienSkill.choPhepXuLyLenh(-91, 1_000L),
                "packet skill dau tien bi chan");
        dung(!phienSkill.choPhepXuLyLenh(-91, 1_050L),
                "client spam skill duoi 75ms van duoc xu ly");
        dung(phienSkill.choPhepXuLyLenh(-91, 1_075L),
                "skill dung nhip lai bi chan");

        ChickenPhien phienFlood = new ChickenPhien(null, 90_004);
        for (int i = 0; i < 300; i++) {
            dung(phienFlood.choPhepXuLyLenh(5, 2_000L),
                    "phien bi dong truoc nguong packet i=" + i);
        }
        dung(!phienFlood.choPhepXuLyLenh(5, 2_000L),
                "phien vuot 300 packet/giay van con hoat dong");
        dung(!phienFlood.conKichHoat(),
                "phien flood bi tu choi nhung khong bi dong");

        ChickenPhien phienResource = new ChickenPhien(null, 90_003);
        for (int i = 0; i < 64; i++) {
            dung(phienResource.datLichGuiNguyenLieuBoss(1_000L) >= 0L,
                    "hang resource boss day som tai i=" + i);
        }
        bang(-1L, phienResource.datLichGuiNguyenLieuBoss(1_000L),
                "hang resource boss khong co gioi han");
        phienResource.hoanTatGuiNguyenLieuBoss();
        dung(phienResource.datLichGuiNguyenLieuBoss(1_000L) >= 0L,
                "resource hoan tat khong tra lai slot hang doi");
    }

    private static void kiemTraKhoaTrangBiTrongTran() throws Exception {
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(
                new DichVuBatPacket());
        nguoiChoi.ma = 90_010;
        nguoiChoi.ten = "EquipLock";
        nguoiChoi.cap = 99;
        ChickenVatPham vatPham = new ChickenVatPham(99_999);
        vatPham.mau = new ChickenMauVatPham(
                (short) 123, (byte) 5, (byte) 0,
                "AVG test", "", (byte) 1, 0,
                (short) 1, (short) 1, false);
        nguoiChoi.itemBag[0] = vatPham;

        ChickenQuanLyChien tran = new ChickenQuanLyChien(
                null, new ChickenNguoiChoi[]{nguoiChoi}, (byte) 0);
        try {
            nguoiChoi.chuyenVatPham(new ChickenTinNhan(
                    (byte) -44, new byte[]{4, 0}));
            dung(nguoiChoi.itemBag[0] == vatPham,
                    "item trong tui bi doi khi tran dang hoat dong");
            dung(nguoiChoi.itemBody[5] == null,
                    "AVG duoc thay giua tran de ghep chi so gia");
        } finally {
            tran.khiNguoiChoiRoi(nguoiChoi);
        }
    }

    private static void kiemTraDanRiengRuaMap54() {
        bang(1563, CauHinhBossRua.layTheoSlot(8).getVuKhi(),
                "Rua map 54 chua mang part anh dan rieng");

        ChickenChienBinh rua = chienBinh(
                (byte) 8,
                CauHinhBossRua.PART_ANH_DAN_RUA,
                (byte) 0
        );
        rua.x = 100;
        rua.y = 500;
        rua.tanCong = CauHinhBossRua.SAT_THUONG_CHAM;

        ChickenChienBinh mucTieu = chienBinh(
                (byte) 0,
                (short) 5,
                (byte) 0
        );
        mucTieu.x = 300;
        mucTieu.y = 500;
        mucTieu.giap = 100;

        ChickenQuanLyBanDo mapTrong = new ChickenQuanLyBanDo(0) {
            @Override
            public int getWidth() { return 2_000; }

            @Override
            public int getHeight() { return 1_000; }

            @Override
            public synchronized boolean coVaCham(short x, short y) {
                return false;
            }
        };
        ChickenKetQuaDan ketQua = BossRuaRongTanCong.taoPhatBanRua(
                rua, mucTieu, mapTrong, (byte) 0, (byte) 0);

        bang(DocBossRua.LOAI_DAN_DOC, ketQua.loaiDan,
                "dan Rua khong dung bullet type 55");
        dung(ketQua.cacDuongX.length == 1
                        && ketQua.cacDuongX[0].length >= 2,
                "dan Rua khong co duong bay hien thi");
        dung(ketQua.mucTieu == mucTieu,
                "dan Rua bay dung nguoi nhung server khong nhan va cham");
        bang(400, ketQua.satThuong,
                "dan Rua khong lay cong 500 tru giap muc tieu");
        dung(DocBossRua.apDung(rua, mucTieu, ketQua.satThuong),
                "server khong gan trang thai doc khi dan trung");
        bang(80, DocBossRua.laySatThuongDauLuot(mucTieu),
                "damage doc khong bang 20 phan tram damage truc tiep");
        DocBossRua.apDung(rua, mucTieu, 100);
        bang(80, DocBossRua.laySatThuongDauLuot(mucTieu),
                "doc yeu hon lai ghi de muc doc manh dang co");
    }

    private static void kiemTraBossRuaGuiPacketBan() throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 92_001;
        nguoiChoi.ten = "BossRuaPacketTarget";

        SanhChoBoss sanh = new SanhChoBoss(
                (byte) 4, (byte) 0, (byte) CauHinhBossRua.MAP_ID,
                (byte) 8, 1_000);
        dung(sanh.themThanhVien(new ThanhVienBoss(
                        nguoiChoi, (byte) 0, 1L, true)),
                "khong them duoc nguoi choi vao sanh test Rua");

        BossRua tran = new BossRua(sanh);
        Field luot = BossRua.class.getDeclaredField("luotHienTai");
        luot.setAccessible(true);
        luot.setByte(tran, (byte) 8);
        Field phien = BossRua.class.getDeclaredField("maPhienLuot");
        phien.setAccessible(true);
        phien.setLong(tran, 1L);

        ChickenChienBinh[] chienBinhs = tran.chupChienBinh();
        ChickenChienBinh boss = chienBinhs[8];
        ChickenChienBinh mucTieu = chienBinhs[0];
        mucTieu.x = boss.x;
        mucTieu.y = boss.y;
        mucTieu.hp = 2_000;
        mucTieu.mauToiDa = 2_000;

        Method thucHienRua = BossRua.class.getDeclaredMethod(
                "thucHienRua",
                ChickenChienBinh.class,
                int.class,
                long.class,
                int.class,
                int.class,
                int.class
        );
        thucHienRua.setAccessible(true);
        thucHienRua.invoke(tran, boss, 8, 1L, 260, 0, 0);

        dung(dichVu.daNhan(22),
                "Boss Rua khong gui CMD 22 hien thi dan doc");
        dung(dichVu.daNhan(96),
                "dan Rua trung nguoi nhung khong gui CMD 96 hieu ung doc");
        ChickenTinNhan tinDoc = dichVu.layTinCuoi(96);
        DataInputStream doc = new ChickenTinNhan(
                (byte) 96, tinDoc.layDuLieu()).boDoc();
        bang(8, doc.readUnsignedByte(), "CMD 96 sai slot Rua gay doc");
        bang(0, doc.readUnsignedByte(), "CMD 96 sai slot nguoi bi doc");
        bang(0, doc.available(), "CMD 96 co byte du lieu thua");
        dung(mucTieu.biDocBossRua,
                "client co hieu ung nhung server khong luu trang thai doc");
        bang(1_500, mucTieu.hp,
                "damage truc tiep cua dan doc khong duoc server tru HP");
        bang(100, mucTieu.satThuongDocBossRuaMoiLuot,
                "server khong luu dung damage doc moi luot");
        tran.dungBot();
    }

    private static void kiemTraRuaVuotMepDiaHinh() {
        ChickenQuanLyBanDo mapCoMep = new ChickenQuanLyBanDo(0) {
            @Override
            public int getWidth() {
                return 500;
            }

            @Override
            public int getHeight() {
                return 400;
            }

            @Override
            public synchronized boolean coVaCham(short x, short y) {
                return x >= 200 && x < 500 && y >= 200;
            }
        };
        ChickenChienBinh rua = chienBinh((byte) 8, (short) 1563, (byte) 0);
        rua.x = 260;
        rua.y = 199;
        ChickenChienBinh mucTieu = chienBinh((byte) 0, (short) 110, (byte) 0);
        mucTieu.x = 60;
        mucTieu.y = 199;

        int conLai = DiChuyenBossRua.QUANG_DUONG_MOI_LUOT;
        while (conLai > 0) {
            short[] buoc = DiChuyenBossRua.tinhBuocTiepTheo(
                    rua, mucTieu, conLai, -1, mapCoMep);
            int daDi = Math.abs(buoc[0] - rua.x);
            boolean coDichChuyen = buoc[0] != rua.x || buoc[1] != rua.y;
            if (!coDichChuyen) {
                break;
            }
            rua.x = buoc[0];
            rua.y = buoc[1];
            conLai = Math.max(0, conLai - daDi);
        }

        dung(rua.x < 200 - DiChuyenBossRua.NUA_RONG_HITBOX,
                "Rua van dung giua hitbox tren mep nen va khoa animation client");
        dung(rua.y > 199,
                "Rua qua mep nhung khong roi theo trong luc server");
    }

    private static void kiemTraBossRuaDiChuyenXongPhaiBan() throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 92_002;
        nguoiChoi.ten = "BossRuaMoveTarget";

        SanhChoBoss sanh = new SanhChoBoss(
                (byte) 4, (byte) 0, (byte) CauHinhBossRua.MAP_ID,
                (byte) 8, 1_000);
        dung(sanh.themThanhVien(new ThanhVienBoss(
                        nguoiChoi, (byte) 0, 2L, true)),
                "khong them duoc nguoi choi vao sanh test Rua di chuyen");

        BossRua tran = new BossRua(sanh);
        Field luot = BossRua.class.getDeclaredField("luotHienTai");
        luot.setAccessible(true);
        luot.setByte(tran, (byte) 8);
        Field phien = BossRua.class.getDeclaredField("maPhienLuot");
        phien.setAccessible(true);
        phien.setLong(tran, 2L);

        ChickenChienBinh[] chienBinhs = tran.chupChienBinh();
        ChickenChienBinh boss = chienBinhs[8];
        ChickenChienBinh mucTieu = chienBinhs[0];
        // Tọa độ thật ghi nhận từ client map 54 sau khi nhân vật đã rơi xuống nền.
        mucTieu.x = 245;
        mucTieu.y = 481;
        mucTieu.hp = 10_000;
        mucTieu.mauToiDa = 10_000;

        Method thucHienLuotBoss = BossRua.class.getDeclaredMethod(
                "thucHienLuotBoss", int.class, long.class);
        thucHienLuotBoss.setAccessible(true);
        thucHienLuotBoss.invoke(tran, 8, 2L);

        dung(dichVu.choLenh(22, 5, TimeUnit.SECONDS),
                "Rua di chuyen het 260px nhung khong gui CMD 22");
        bang(1, dichVu.demLenh(21),
                "Rua map 54 phai gui dung mot dich di chuyen CMD21");
        bang(0, dichVu.demLenh(53),
                "Rua map 54 van teleport tung buoc bang CMD53");
        bang(0, dichVu.demLenh(-64),
                "Rua map 54 van gui CMD-64 khoa bo doc packet client");
        tran.kiemTraVaCham(
                nguoiChoi, new ChickenTinNhan((byte) 23, new byte[]{1}));
        bang(8, luot.getByte(tran),
                "CMD23 loi lai ket thuc som luot ban cua Rua");
        tran.kiemTraVaCham(
                nguoiChoi, new ChickenTinNhan((byte) 23, new byte[0]));
        bang(8, luot.getByte(tran),
                "CMD23 khong phai tin ket thuc dan cua boss");
        tran.kiemTraVaCham(
                nguoiChoi, new ChickenTinNhan((byte) 79, new byte[]{1}));
        bang(8, luot.getByte(tran),
                "CMD79 sai do dai lai ket thuc luot boss");
        tran.kiemTraVaCham(
                nguoiChoi, new ChickenTinNhan((byte) 79, new byte[]{0}));
        long hetHan = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        while (luot.getByte(tran) == (byte) 8 && System.nanoTime() < hetHan) {
            Thread.sleep(20L);
        }
        dung(luot.getByte(tran) != (byte) 8,
                "Rua da ban xong nhung khong giai phong luot");
        bang(9_400, mucTieu.hp,
                "server khong tru ca damage dan va damage doc dau luot");
        tran.dungBot();
    }

    private static void kiemTraBossRuaRongDiChuyenXongPhaiBan() throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 92_003;
        nguoiChoi.ten = "BossRuaRongMoveTarget";

        SanhChoBoss sanh = new SanhChoBoss(
                (byte) 4, (byte) 0, (byte) CauHinhBossRuaRong.MAP_ID,
                (byte) 8, 1_000);
        dung(sanh.themThanhVien(new ThanhVienBoss(
                        nguoiChoi, (byte) 0, 3L, true)),
                "khong them duoc nguoi choi vao sanh test Rua Rong");

        BossRuaRong tran = new BossRuaRong(sanh);
        Field luot = BossRuaRong.class.getDeclaredField("luotHienTai");
        luot.setAccessible(true);
        luot.setByte(tran, (byte) CauHinhBossRuaRong.SLOT_BOSS_DAU);
        Field phien = BossRuaRong.class.getDeclaredField("maPhienLuot");
        phien.setAccessible(true);
        phien.setLong(tran, 3L);

        ChickenChienBinh mucTieu = tran.chupChienBinh()[0];
        // Tọa độ ghi nhận từ phiên client thật ở map 58.
        mucTieu.x = 480;
        mucTieu.y = 248;
        mucTieu.hp = 10_000;
        mucTieu.mauToiDa = 10_000;

        Method thucHienLuotBoss = BossRuaRong.class.getDeclaredMethod(
                "thucHienLuotBoss", int.class, long.class);
        thucHienLuotBoss.setAccessible(true);
        thucHienLuotBoss.invoke(
                tran, CauHinhBossRuaRong.SLOT_BOSS_DAU, 3L);

        dung(dichVu.choLenh(22, 5, TimeUnit.SECONDS),
                "Rua map 58 di chuyen xong nhung khong gui CMD 22");
        bang(1, dichVu.demLenh(21),
                "Rua map 58 phai gui dung mot dich di chuyen CMD21");
        bang(0, dichVu.demLenh(53),
                "Rua map 58 van teleport tung buoc bang CMD53");
        bang(0, dichVu.demLenh(-64),
                "Rua map 58 van gui CMD-64 khoa bo doc packet client");
        tran.kiemTraVaCham(
                nguoiChoi, new ChickenTinNhan((byte) 23, new byte[0]));
        bang(CauHinhBossRuaRong.SLOT_BOSS_DAU, luot.getByte(tran),
                "CMD23 khong phai tin ket thuc dan cua boss map 58");
        tran.kiemTraVaCham(
                nguoiChoi, new ChickenTinNhan((byte) 79, new byte[]{0}));
        bang(CauHinhBossRuaRong.SLOT_BOSS_DAU, luot.getByte(tran),
                "CMD79 hop le van tua nhanh dong ho server map 58");
        long hetHan = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        while (luot.getByte(tran) == (byte) CauHinhBossRuaRong.SLOT_BOSS_DAU
                && System.nanoTime() < hetHan) {
            Thread.sleep(20L);
        }
        dung(luot.getByte(tran) != (byte) CauHinhBossRuaRong.SLOT_BOSS_DAU,
                "Rua map 58 da het animation nhung server khong giai phong luot");
        tran.dungBot();
    }

    private static void kiemTraChuKyChieuRua() {
        dung(ChieuBossRua.chonChoLuot(1, true)
                        == ChieuBossRua.LoaiChieu.BAN_THUONG,
                "luot dau cua Rua khong ban dan thuong");
        dung(ChieuBossRua.chonChoLuot(2, false)
                        == ChieuBossRua.LoaiChieu.DAM_DA,
                "luot hai cua Rua khong bat buoc dam da");
        dung(ChieuBossRua.chonChoLuot(3, false)
                        == ChieuBossRua.LoaiChieu.BAN_THUONG,
                "tu luot ba nhanh random ban thuong bi sai");
        dung(ChieuBossRua.chonChoLuot(3, true)
                        == ChieuBossRua.LoaiChieu.DAM_DA,
                "tu luot ba nhanh random dam da bi sai");
    }

    private static void kiemTraVongDoiGhimDaRua() {
        ChickenQuanLyBanDo banDo = new ChickenQuanLyBanDo(127);
        ChickenChienBinh mucTieu = chienBinh(
                (byte) 0, (short) 110, (byte) 0);
        short x = 200;
        short y = 300;
        dung(banDo.themDaRua(x, y), "khong them duoc anh da Rua vao map test");
        ChieuBossRua.ghimMucTieu(mucTieu, x, y);
        dung(ChieuBossRua.dangBiDaRuaGhim(mucTieu, banDo),
                "da con nguyen nhung server khong giu trang thai ghim");

        banDo.phaDiaHinh(x, y - 18, (byte) 0);
        dung(!ChieuBossRua.dangBiDaRuaGhim(mucTieu, banDo),
                "loi da da bi pha nhung nguoi choi van bi ghim vinh vien");
        dung(!mucTieu.biDaRuaGhim,
                "server khong xoa co ghim sau khi da bi pha");
    }

    private static void kiemTraAvgBayQuaDaRua() {
        ChickenQuanLyBanDo banDo = new ChickenQuanLyBanDo(127);
        short x = 220;
        short y = 320;
        dung(banDo.themDaRua(x, y),
                "khong them duoc da Rua de test AVG bay");

        ChickenChienBinh ironMan = chienBinh(
                (byte) 0, (short) 223, ChickenCoCheBayAVG.AVG_IRON_MAN);
        ChieuBossRua.ghimMucTieu(ironMan, x, y);
        dung(!ironMan.biDaRuaGhim
                        && !ChieuBossRua.dangBiDaRuaGhim(ironMan, banDo),
                "Iron Man co quyen bay server van bi da Rua ghim");

        ChickenChienBinh ultron = chienBinh(
                (byte) 1, (short) 230, ChickenCoCheBayAVG.AVG_ULTRON);
        ChieuBossRua.ghimMucTieu(ultron, x, y);
        dung(!ultron.biDaRuaGhim
                        && !ChieuBossRua.dangBiDaRuaGhim(ultron, banDo),
                "Ultron co quyen bay server van bi da Rua ghim");

        ChickenChienBinh fakeIronMan = chienBinh(
                (byte) 2, (short) 223, ChickenCoCheBayAVG.AVG_IRON_MAN);
        fakeIronMan.duocPhepBay = false;
        ChieuBossRua.ghimMucTieu(fakeIronMan, x, y);
        dung(ChieuBossRua.dangBiDaRuaGhim(fakeIronMan, banDo),
                "fake ID Iron Man khong co quyen bay lai qua duoc da Rua");
    }

    private static void kiemTraLuatKetThucBoss() {
        dung(ChickenKetQuaTranBoss.danhGia(1, 1)
                        == ChickenKetQuaTranBoss.KetQua.CHUA_KET_THUC,
                "hai phe con song lai ket thuc tran boss");
        dung(ChickenKetQuaTranBoss.danhGia(1, 0)
                        == ChickenKetQuaTranBoss.KetQua.NGUOI_CHOI_THANG,
                "boss het HP nhung nguoi choi khong thang");
        dung(ChickenKetQuaTranBoss.danhGia(0, 1)
                        == ChickenKetQuaTranBoss.KetQua.NGUOI_CHOI_THUA,
                "team het HP nhung boss khong thang");
        dung(ChickenKetQuaTranBoss.danhGia(0, 0)
                        == ChickenKetQuaTranBoss.KetQua.NGUOI_CHOI_THUA,
                "hai phe cung chet lai khong uu tien tinh nguoi choi thua");
        bang(1, ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THANG,
                "CMD 50 thang sai mapping client");
        bang(0, ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THUA,
                "CMD 50 thua sai mapping client");

        int expCu = ChickenQuanLyMayChu.bossExpReward;
        try {
            ChickenQuanLyMayChu.bossExpReward = 1_234;
            bang(1_234, ChickenKetQuaTranBoss.layExpHaBoss(0),
                    "boss chet nhung server khong tinh EXP");
            bang(0, ChickenKetQuaTranBoss.layExpHaBoss(1),
                    "boss con song lai duoc trao EXP ha boss");
        } finally {
            ChickenQuanLyMayChu.bossExpReward = expCu;
        }
    }

    private static void kiemTraCongBoTranSauKhoiTao() {
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(
                new DichVuBatPacket());
        nguoiChoi.ma = 92_050;
        nguoiChoi.ten = "BossLatePublish";

        final class TranCongBoTre extends ChickenQuanLyChien {
            private final boolean biCongBoSom;

            TranCongBoTre(ChickenNguoiChoi nguoiChoiTest) {
                super(null, new ChickenNguoiChoi[]{nguoiChoiTest},
                        (byte) CauHinhBossRua.MAP_ID, false);
                this.biCongBoSom =
                        ChickenQuanLyChien.timTranDauCuaNguoiChoi(
                                nguoiChoiTest) != null;
                this.dangKyNguoiChoiTrongTran();
            }
        }

        TranCongBoTre tran = new TranCongBoTre(nguoiChoi);
        dung(!tran.biCongBoSom,
                "router thay instance tran khi constructor chua hoan tat");
        dung(ChickenQuanLyChien.timTranDauCuaNguoiChoi(nguoiChoi) == tran,
                "tran hoan tat lai khong duoc cong bo cho router");
        tran.khiNguoiChoiRoi(nguoiChoi);
        dung(ChickenQuanLyChien.timTranDauCuaNguoiChoi(nguoiChoi) == null,
                "tran test ket thuc nhung con dang ky player");
    }

    private static void kiemTraLoiGuiKetQuaDuocCoLap() {
        ChickenNguoiChoi nguoiLoi = new ChickenNguoiChoi(
                new DichVuNemLoi());
        nguoiLoi.ma = 92_051;
        nguoiLoi.ten = "BossResultBrokenClient";
        ThanhVienBoss veLoi = new ThanhVienBoss(
                nguoiLoi, (byte) 0, 51L, true);
        ChickenKinhTeBossTestSupport.danhDauDaTraPhi(veLoi);

        DichVuBatPacket dichVuTot = new DichVuBatPacket();
        ChickenNguoiChoi nguoiTot = new ChickenNguoiChoi(dichVuTot);
        nguoiTot.ma = 92_052;
        nguoiTot.ten = "BossResultHealthyClient";
        ThanhVienBoss veTot = new ThanhVienBoss(
                nguoiTot, (byte) 1, 52L, false);
        ChickenKinhTeBossTestSupport.danhDauDaTraPhi(veTot);

        ChickenKinhTeBossTestSupport.batKhoExpRam(nguoiTot);
        try {
            ChickenKetQuaTranBoss.traoThuongVaGuiKetQua(
                    veLoi, 0, false);
            ChickenKetQuaTranBoss.traoThuongVaGuiKetQua(
                    veTot, 10, false);
            bang(1, dichVuTot.demLenh(50),
                    "client dau loi lam dong doi khong nhan CMD 50");
            bang(1, dichVuTot.demLenh(-91),
                    "ket thuc boss khong dong menu skill con sot");
            dung(dichVuTot.viTriLenhDau(-91) < dichVuTot.viTriLenhDau(50),
                    "menu skill bi dong sau CMD 50 lam an bang ket qua");
            bang(0, dichVuTot.demGuiThongTin(),
                    "CMD 3 gui ngay sau CMD 50 lam client bo bang ket qua");
        } finally {
            ChickenKinhTeBossTestSupport.khoiPhucKhoExpJdbc();
        }
    }

    private static void kiemTraRuaGietTeamKhongHoa() throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 92_006;
        nguoiChoi.ten = "BossRuaLoseTarget";

        SanhChoBoss sanh = new SanhChoBoss(
                (byte) 4, (byte) 0, (byte) CauHinhBossRua.MAP_ID,
                (byte) 8, 1_000);
        ThanhVienBoss thanhVien = new ThanhVienBoss(
                nguoiChoi, (byte) 0, 6L, true);
        ChickenKinhTeBossTestSupport.danhDauDaTraPhi(thanhVien);
        dung(sanh.themThanhVien(thanhVien),
                "khong them duoc nguoi choi vao test ket qua thua boss Rua");

        BossRua tran = new BossRua(sanh);
        Field luot = BossRua.class.getDeclaredField("luotHienTai");
        luot.setAccessible(true);
        luot.setByte(tran, (byte) 8);
        Field phien = BossRua.class.getDeclaredField("maPhienLuot");
        phien.setAccessible(true);
        phien.setLong(tran, 6L);

        ChickenChienBinh[] chienBinhs = tran.chupChienBinh();
        ChickenChienBinh boss = chienBinhs[8];
        ChickenChienBinh mucTieu = chienBinhs[0];
        mucTieu.x = boss.x;
        mucTieu.y = boss.y;
        mucTieu.hp = 1;
        mucTieu.mauToiDa = 1;

        Method thucHienRua = BossRua.class.getDeclaredMethod(
                "thucHienRua",
                ChickenChienBinh.class,
                int.class,
                long.class,
                int.class,
                int.class,
                int.class
        );
        thucHienRua.setAccessible(true);
        thucHienRua.invoke(tran, boss, 8, 6L, 260, 0, 0);

        ChickenTinNhan tinKetThuc = dichVu.layTinCuoi(50);
        dung(tinKetThuc != null,
                "Rua giet het team nhung server khong gui CMD 50");
        DataInputStream in = new ChickenTinNhan(
                (byte) 50, tinKetThuc.layDuLieu()).boDoc();
        bang(ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THUA,
                in.readUnsignedByte(),
                "Rua giet het team lai gui hoa/thang thay vi thua");
        bang(0, in.readShort(),
                "boss con song nhung packet lai co EXP ha boss");
        bang(-1_000, in.readInt(),
                "tran boss thua khong hien thi phi da mat");
        bang(0, in.readShort(), "tran boss thua lai co ngoc ngoai cau hinh");
        bang(0, in.readUnsignedByte(),
                "CMD 50 boss co danh sach vat pham ngoai du kien");
        bang(0, in.available(), "CMD 50 boss co byte du thua");
        dung(sanh.getTrangThai() == SanhChoBoss.TrangThai.DANG_CHO,
                "ket thuc boss Rua khong mo lai phong cho tai dau");
        dung(sanh.timThanhVien(nguoiChoi) != thanhVien,
                "ket thuc boss Rua van tai su dung ve tran cu");
        tran.dungBot();
    }

    private static void kiemTraCungDonCungChetVanThuaCoExp()
            throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu) {
            @Override
            public void flushCache() {
                // Test độc lập database; production vẫn lưu thưởng bằng flushCache thật.
            }
        };
        nguoiChoi.ma = 92_007;
        nguoiChoi.ten = "BossRuaDoubleDeathTarget";
        nguoiChoi.kinhNghiem = 10_000;

        SanhChoBoss sanh = new SanhChoBoss(
                (byte) 4, (byte) 0, (byte) CauHinhBossRua.MAP_ID,
                (byte) 8, 1_000);
        ThanhVienBoss thanhVien = new ThanhVienBoss(
                nguoiChoi, (byte) 0, 7L, true);
        ChickenKinhTeBossTestSupport.danhDauDaTraPhi(thanhVien);
        dung(sanh.themThanhVien(thanhVien),
                "khong them duoc nguoi choi vao test cung chet");

        ChickenKinhTeBossTestSupport.batKhoExpRam(nguoiChoi);
        BossRua tran = new BossRua(sanh);
        ChickenChienBinh[] chienBinhs = tran.chupChienBinh();
        ChickenChienBinh boss = chienBinhs[8];
        ChickenChienBinh mucTieu = chienBinhs[0];
        boss.hp = 1;
        boss.mauToiDa = 1;
        mucTieu.hp = 1;
        mucTieu.mauToiDa = 1;

        Method gaySatThuong = BossRua.class.getDeclaredMethod(
                "gaySatThuong", ChickenChienBinh.class, int.class);
        gaySatThuong.setAccessible(true);
        Method kiemTraKetThuc = BossRua.class.getDeclaredMethod(
                "kiemTraKetThuc");
        kiemTraKetThuc.setAccessible(true);

        int expCu = ChickenQuanLyMayChu.bossExpReward;
        try {
            ChickenQuanLyMayChu.bossExpReward = 1_234;
            int expBanDau = nguoiChoi.kinhNghiem;

            // Va chạm đầu của cùng đòn hạ boss nhưng chưa được phép chốt thắng.
            gaySatThuong.invoke(tran, boss, 1);
            dung(!dichVu.daNhan(50),
                    "server chot thang ngay giua don truoc khi ap dung het damage");

            // Phần sát thương còn lại của chính đòn đó hạ người chơi.
            mucTieu.hp = 0;
            mucTieu.chet = true;
            kiemTraKetThuc.invoke(tran);

            ChickenTinNhan tinKetThuc = dichVu.layTinCuoi(50);
            dung(tinKetThuc != null,
                    "cung chet nhung server khong gui ket qua");
            DataInputStream in = new ChickenTinNhan(
                    (byte) 50, tinKetThuc.layDuLieu()).boDoc();
            bang(ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THUA,
                    in.readUnsignedByte(),
                    "boss chet truoc roi team chet cung don lai bi tinh thang/hoa");
            bang(1_234, in.readShort(),
                    "cung chet da ha boss nhung packet khong co EXP");
            bang(-1_000, in.readInt(),
                    "cung chet bi tinh thua khong hien thi phi da mat");
            bang(0, in.readShort(),
                    "cung chet bi tinh thua nhung van duoc thuong ngoc");
            bang(expBanDau + 1_234, nguoiChoi.kinhNghiem,
                    "EXP ha boss chi hien thi ma khong cong vao state server");
            bang(1, dichVu.demLenh(50),
                    "mot tran bi gui ket qua nhieu lan");

            Thread.sleep(ChickenKetQuaTranBoss.TRE_XAC_NHAN_THANG_MS + 80L);
            bang(1, dichVu.demLenh(50),
                    "tac vu thang tre ghi de ket qua thua cung chet");
        } finally {
            ChickenQuanLyMayChu.bossExpReward = expCu;
            tran.dungBot();
            ChickenKinhTeBossTestSupport.khoiPhucKhoExpJdbc();
        }
    }

    private static void kiemTraRuaRoiKhoiMepMap54() {
        ArrayList<ChickenDuLieuBanDo.MapDataEntry> entrysCu =
                ChickenDuLieuBanDo.entrys;
        ArrayList<ChickenDuLieuBanDo.MapBrickEntry> bricksCu =
                ChickenDuLieuBanDo.brickEntrys;
        try {
            byte[] duLieu = ChickenTienIch.layTep("res/map/54");
            dung(duLieu != null && duLieu.length > 5,
                    "thieu res/map/54 de test dia hinh that");
            ChickenDuLieuBanDo.entrys = new ArrayList<>();
            ChickenDuLieuBanDo.brickEntrys = new ArrayList<>();
            ChickenDuLieuBanDo.entrys.add(
                    new ChickenDuLieuBanDo.MapDataEntry(
                            duLieu, (byte) 54, "Rua test",
                            (short) 0, (byte) 7));
            ChickenQuanLyBanDo banDo = new ChickenQuanLyBanDo(54);
            ChickenChienBinh rua = new ChickenChienBinh(
                    (byte) 8, -54, (short) 736, (short) 270,
                    "Rua", (short) 1563, 10_000, 500, 0);
            ChickenChienBinh mucTieu = chienBinh(
                    (byte) 0, (short) 110, (byte) 0);
            mucTieu.x = 145;
            mucTieu.y = 481;

            short[] buocVeNguoi = DiChuyenBossRua.tinhBuocTiepTheo(
                    rua, mucTieu, 260, -1, banDo);
            dung(buocVeNguoi[0] <= rua.x,
                    "Rua lai di nguoc, xa nguoi choi o ben trai");
            dung(buocVeNguoi[1] > rua.y,
                    "Rua con bam 2-3 diem chan o mep gach nen khong roi xuong");
        } finally {
            ChickenDuLieuBanDo.entrys = entrysCu;
            ChickenDuLieuBanDo.brickEntrys = bricksCu;
        }
    }

    private static void kiemTraRuaKhongDiXaVaKhongTreo() {
        ArrayList<ChickenDuLieuBanDo.MapDataEntry> entrysCu =
                ChickenDuLieuBanDo.entrys;
        ArrayList<ChickenDuLieuBanDo.MapBrickEntry> bricksCu =
                ChickenDuLieuBanDo.brickEntrys;
        try {
            bang(130, CauHinhBossRua.QUANG_DUONG_MOI_LUOT,
                    "Rua map 54 van con chay xa gap doi");
            byte[] duLieu = ChickenTienIch.layTep("res/map/54");
            dung(duLieu != null && duLieu.length > 5,
                    "thieu res/map/54 de test dia hinh that");
            ChickenDuLieuBanDo.entrys = new ArrayList<>();
            ChickenDuLieuBanDo.brickEntrys = new ArrayList<>();
            ChickenDuLieuBanDo.entrys.add(
                    new ChickenDuLieuBanDo.MapDataEntry(
                            duLieu, (byte) 54, "Rua test",
                            (short) 0, (byte) 7));
            ChickenQuanLyBanDo banDo = new ChickenQuanLyBanDo(54);
            ChickenChienBinh rua = new ChickenChienBinh(
                    (byte) 8, -54, (short) 736, (short) 270,
                    "Rua", (short) 1563, 10_000, 500, 0);
            ChickenChienBinh mucTieu = chienBinh(
                    (byte) 0, (short) 110, (byte) 0);
            mucTieu.x = 145;
            mucTieu.y = 481;

            int xBatDau = rua.x;
            int conLai = CauHinhBossRua.QUANG_DUONG_MOI_LUOT;
            int baoHiem = 0;
            while (conLai > 0 && baoHiem++ < 200) {
                short xCu = rua.x;
                short yCu = rua.y;
                short[] buoc = DiChuyenBossRua.tinhBuocTiepTheo(
                        rua, mucTieu, conLai, -1, banDo);
                if (buoc[0] == xCu && buoc[1] == yCu) {
                    break;
                }
                rua.x = buoc[0];
                rua.y = buoc[1];
                conLai = Math.max(0, conLai - Math.abs(rua.x - xCu));
            }
            dung(baoHiem < 200, "vong lap di chuyen Rua khong ket thuc");

            while (baoHiem++ < 300) {
                short[] buocRoi = DiChuyenBossRua.tinhBuocRoiThangDung(
                        rua, banDo);
                if (buocRoi[1] == rua.y) {
                    break;
                }
                rua.y = buocRoi[1];
            }
            dung(baoHiem < 300, "Rua roi mai ma khong cham nen");

            int daChayNgang = Math.abs(rua.x - xBatDau);
            dung(daChayNgang <= CauHinhBossRua.QUANG_DUONG_MOI_LUOT,
                    "Rua da chay qua quang duong server cho phep");
            dung(rua.x > mucTieu.x + DiChuyenBossRua.NUA_RONG_HITBOX,
                    "Rua van den sat nguoi chi trong mot luot");
            short[] kiemTraNen = DiChuyenBossRua.tinhBuocRoiThangDung(
                    rua, banDo);
            bang(rua.y, kiemTraNen[1],
                    "Rua ket thuc luot khi van dang lo lung");
        } finally {
            ChickenDuLieuBanDo.entrys = entrysCu;
            ChickenDuLieuBanDo.brickEntrys = bricksCu;
        }
    }

    private static void kiemTraDiaHinhCoDinhBossRua() {
        ArrayList<ChickenDuLieuBanDo.MapDataEntry> entrysCu =
                ChickenDuLieuBanDo.entrys;
        ArrayList<ChickenDuLieuBanDo.MapBrickEntry> bricksCu =
                ChickenDuLieuBanDo.brickEntrys;
        try {
            byte[] duLieu = ChickenTienIch.layTep("res/map/54");
            dung(duLieu != null && duLieu.length > 5,
                    "thieu res/map/54 de test gach co dinh");
            ChickenDuLieuBanDo.MapDataEntry entry =
                    new ChickenDuLieuBanDo.MapDataEntry(
                            duLieu, (byte) 54, "Rua test",
                            (short) 0, (byte) 7);
            bang(167, layIdBrickTai(entry.duLieu, 769, 252),
                    "be lon Boss Rua chua duoc doi sang tile bat tu");
            bang(ChickenDuLieuBanDo.ID_GACH_NHO_CO_DINH_BOSS_RUA,
                    layIdBrickTai(entry.duLieu, 714, 353),
                    "gach nho duong chay chua co ID bat tu rieng");
            bang(20, layIdBrickTai(entry.duLieu, 209, 352),
                    "gach 20 ngoai duong chay bi khoa nham");

            ChickenDuLieuBanDo.entrys = new ArrayList<>();
            ChickenDuLieuBanDo.brickEntrys = new ArrayList<>();
            ChickenDuLieuBanDo.entrys.add(entry);
            ChickenQuanLyBanDo banDo = new ChickenQuanLyBanDo(54);
            short beBossX = 785;
            short beBossY = 268;
            short gachNhoX = 730;
            short gachNhoY = 369;
            short gachThuongX = 225;
            short gachThuongY = 368;
            dung(banDo.coVaCham(beBossX, beBossY),
                    "be Boss Rua khong co va cham truoc khi test");
            dung(banDo.coVaCham(gachNhoX, gachNhoY),
                    "gach nho duong chay khong co va cham truoc khi test");
            dung(banDo.coVaCham(gachThuongX, gachThuongY),
                    "gach thuong khong co va cham truoc khi test");

            banDo.phaDiaHinh(beBossX, beBossY, (byte) 3);
            banDo.phaDiaHinh(gachNhoX, gachNhoY, (byte) 3);
            banDo.phaDiaHinh(gachThuongX, gachThuongY, (byte) 3);
            dung(banDo.coVaCham(beBossX, beBossY),
                    "be Boss Rua van bi dan pha");
            dung(banDo.coVaCham(gachNhoX, gachNhoY),
                    "gach nho duong chay van bi dan pha");
            dung(!banDo.coVaCham(gachThuongX, gachThuongY),
                    "khoa dia hinh lam gach ngoai pham vi cung bat tu");
        } finally {
            ChickenDuLieuBanDo.entrys = entrysCu;
            ChickenDuLieuBanDo.brickEntrys = bricksCu;
        }
    }

    private static int layIdBrickTai(byte[] duLieu, int xCanTim, int yCanTim) {
        int soMuc = duLieu == null || duLieu.length < 5
                ? 0 : duLieu[4] & 0xFF;
        int offset = 5;
        for (int i = 0; i < soMuc && offset + 4 < duLieu.length; i++) {
            int x = ChickenTienIch.getShort(offset + 1, duLieu);
            int y = ChickenTienIch.getShort(offset + 3, duLieu);
            if (x == xCanTim && y == yCanTim) {
                return duLieu[offset] & 0xFF;
            }
            offset += 5;
        }
        return -1;
    }

    private static void kiemTraBossRuaLuotHaiDamDa() throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 92_004;
        nguoiChoi.ten = "BossRuaStompTarget";

        SanhChoBoss sanh = new SanhChoBoss(
                (byte) 4, (byte) 0, (byte) CauHinhBossRua.MAP_ID,
                (byte) 8, 1_000);
        dung(sanh.themThanhVien(new ThanhVienBoss(
                        nguoiChoi, (byte) 0, 4L, true)),
                "khong them duoc nguoi choi vao sanh test Rua dam da");

        BossRua tran = new BossRua(sanh);
        datTrangThaiLuotThuHai(tran, BossRua.class, 8, 4L);
        ChickenChienBinh mucTieu = tran.chupChienBinh()[0];
        mucTieu.x = 245;
        mucTieu.y = 481;
        mucTieu.hp = 10_000;
        mucTieu.mauToiDa = 10_000;
        short yCu = mucTieu.y;
        int hpCu = mucTieu.hp;

        Method thucHienLuotBoss = BossRua.class.getDeclaredMethod(
                "thucHienLuotBoss", int.class, long.class);
        thucHienLuotBoss.setAccessible(true);
        thucHienLuotBoss.invoke(tran, 8, 4L);

        dung(dichVu.choLenh(-68, 5, TimeUnit.SECONDS),
                "Rua map 54 luot hai khong gui CMD -68 dam da");
        bang(1, dichVu.demLenh(21),
                "Rua map 54 luot hai khong di chuyen dung mot lan truoc khi dam");
        bang(0, dichVu.demLenh(22),
                "Rua map 54 luot hai lai ban dan thuong");
        bang(yCu, mucTieu.y,
                "skill Rua lai nang nguoi choi len tren tang da");
        dung(mucTieu.biDaRuaGhim,
                "server khong dat trang thai da Rua ghim muc tieu");
        dung(mucTieu.hp < hpCu,
                "dam da map 54 khong gay damage do server tinh");
        kiemTraPacketDamDa(dichVu.layTinCuoi(-68), 8, mucTieu.chiSo, 245, yCu);
        short xGhim = mucTieu.x;
        tran.diChuyen(nguoiChoi, new ChickenTinNhan(
                (byte) 21, taoPacketToaDo((short) (xGhim + 80), yCu)));
        bang(xGhim, mucTieu.x,
                "nguoi bi da ghim van di chuyen duoc tren server map 54");
        bang(yCu, mucTieu.y,
                "CMD di chuyen lam lech Y nguoi dang bi da ghim map 54");
        tran.dungBot();
    }

    private static void kiemTraBossRuaRongLuotHaiDamDa() throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 92_005;
        nguoiChoi.ten = "BossRuaRongStompTarget";

        SanhChoBoss sanh = new SanhChoBoss(
                (byte) 4, (byte) 0, (byte) CauHinhBossRuaRong.MAP_ID,
                (byte) 8, 1_000);
        dung(sanh.themThanhVien(new ThanhVienBoss(
                        nguoiChoi, (byte) 0, 5L, true)),
                "khong them duoc nguoi choi vao sanh test Rua Rong dam da");

        BossRuaRong tran = new BossRuaRong(sanh);
        int slotRua = CauHinhBossRuaRong.SLOT_BOSS_DAU;
        datTrangThaiLuotThuHai(tran, BossRuaRong.class, slotRua, 5L);
        ChickenChienBinh mucTieu = tran.chupChienBinh()[0];
        mucTieu.x = 480;
        mucTieu.y = 248;
        mucTieu.hp = 10_000;
        mucTieu.mauToiDa = 10_000;
        short yCu = mucTieu.y;
        int hpCu = mucTieu.hp;

        Method thucHienLuotBoss = BossRuaRong.class.getDeclaredMethod(
                "thucHienLuotBoss", int.class, long.class);
        thucHienLuotBoss.setAccessible(true);
        thucHienLuotBoss.invoke(tran, slotRua, 5L);

        dung(dichVu.choLenh(-68, 5, TimeUnit.SECONDS),
                "Rua map 58 luot hai khong gui CMD -68 dam da");
        bang(1, dichVu.demLenh(21),
                "Rua map 58 luot hai khong di chuyen dung mot lan truoc khi dam");
        bang(0, dichVu.demLenh(22),
                "Rua map 58 luot hai lai ban dan thuong");
        bang(yCu, mucTieu.y,
                "skill Rua map 58 lai nang nguoi choi len tren tang da");
        dung(mucTieu.biDaRuaGhim,
                "server map 58 khong dat trang thai da ghim");
        dung(mucTieu.hp < hpCu,
                "dam da map 58 khong gay damage do server tinh");
        kiemTraPacketDamDa(
                dichVu.layTinCuoi(-68), slotRua, mucTieu.chiSo, 480, yCu);
        short xGhim = mucTieu.x;
        tran.diChuyen(nguoiChoi, new ChickenTinNhan(
                (byte) 21, taoPacketToaDo((short) (xGhim - 80), yCu)));
        bang(xGhim, mucTieu.x,
                "nguoi bi da ghim van di chuyen duoc tren server map 58");
        bang(yCu, mucTieu.y,
                "CMD di chuyen lam lech Y nguoi dang bi da ghim map 58");
        tran.dungBot();
    }

    private static void kiemTraThanhMauDau25Nac() throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        dichVu.guiCapNhatMauDau((byte) 3, 1_000, (byte) 100, (byte) 0);
        ChickenTinNhan tinNhan = dichVu.layTinCuoi(51);
        dung(tinNhan != null, "khong bat duoc CMD 51 cap nhat HP");
        DataInputStream doc = new ChickenTinNhan(
                (byte) 51, tinNhan.layDuLieu()).boDoc();
        bang(3, doc.readUnsignedByte(), "CMD 51 sai slot");
        bang(1_000, doc.readUnsignedShort(), "CMD 51 sai HP");
        bang(25, doc.readUnsignedByte(),
                "CMD 51 gui 100 lam thanh HP client tran man hinh");
        bang(0, doc.readUnsignedByte(), "CMD 51 sai trang thai chet");
        bang(0, doc.available(), "CMD 51 co byte thua");
    }

    private static void datTrangThaiLuotThuHai(
            Object tran,
            Class<?> loaiTran,
            int slotRua,
            long maPhien
    ) throws Exception {
        Field luot = loaiTran.getDeclaredField("luotHienTai");
        luot.setAccessible(true);
        luot.setByte(tran, (byte) slotRua);
        Field phien = loaiTran.getDeclaredField("maPhienLuot");
        phien.setAccessible(true);
        phien.setLong(tran, maPhien);
        Field soLuotRua = loaiTran.getDeclaredField("soLuotRua");
        soLuotRua.setAccessible(true);
        ((int[]) soLuotRua.get(tran))[slotRua] = 1;
    }

    private static void kiemTraPacketDamDa(
            ChickenTinNhan tinNhan,
            int slotRua,
            int slotMucTieu,
            int xVaCham,
            int yVaCham
    ) throws Exception {
        dung(tinNhan != null, "khong bat duoc packet CMD -68");
        DataInputStream doc = new ChickenTinNhan(
                (byte) -68, tinNhan.layDuLieu()).boDoc();
        bang(slotRua, doc.readUnsignedByte(), "CMD -68 sai slot Rua");
        bang(0, doc.readUnsignedByte(), "CMD -68 sai action nhay/dam");
        bang(1, doc.readUnsignedByte(), "CMD -68 sai so diem da");
        bang(1, doc.readUnsignedByte(), "CMD -68 khong tao Bullet 61");
        bang(xVaCham, doc.readShort(), "CMD -68 sai X va cham");
        bang(yVaCham, doc.readShort(), "CMD -68 sai Y va cham");
        bang(1, doc.readUnsignedByte(), "CMD -68 sai so nguoi bi nang");
        bang(slotMucTieu, doc.readUnsignedByte(), "CMD -68 sai slot muc tieu");
        bang(xVaCham, doc.readShort(), "CMD -68 sai X moi cua muc tieu");
        bang(yVaCham, doc.readShort(),
                "CMD -68 nang nguoi len tren da thay vi ghim tai cho");
        bang(0, doc.available(), "CMD -68 co du lieu thua");
    }

    private static void kiemTraDongBoCmd23BossRua() throws Exception {
        ChickenNguoiChoi nguoiBan = taoNguoiChoiBossTest(92_101, "RuaShooter");
        ChickenNguoiChoi nguoiKhac = taoNguoiChoiBossTest(92_102, "RuaOther");
        SanhChoBoss sanh = taoSanhBossHaiNguoi(
                CauHinhBossRua.MAP_ID, nguoiBan, nguoiKhac, 101L);
        BossRua tran = new BossRua(sanh);
        kiemTraDongBoCmd23(
                tran, BossRua.class, nguoiBan, nguoiKhac, 101L);
        tran.dungBot();
    }

    private static void kiemTraThoiGianHoatAnhDan() {
        bang(ChickenThoiGianHoatAnhDan.TOI_THIEU_MS,
                ChickenThoiGianHoatAnhDan.tinh(
                        new short[][]{new short[1]},
                        new short[][]{new short[1]}),
                "quy dao ngan hon muc toi thieu");
        bang(240L,
                ChickenThoiGianHoatAnhDan.tinh(
                        new short[][]{new short[5], new short[30]},
                        new short[][]{new short[5], new short[30]}),
                "khong lay duong dan dai nhat");
        bang(ChickenThoiGianHoatAnhDan.TOI_DA_MS,
                ChickenThoiGianHoatAnhDan.tinh(
                        new short[][]{new short[100]},
                        new short[][]{new short[100]}),
                "quy dao dai vuot muc toi da");
        bang(ChickenThoiGianHoatAnhDan.HIEU_UNG_KHONG_CO_QUY_DAO_MS,
                ChickenThoiGianHoatAnhDan.tinh(null, null),
                "hieu ung khong co quy dao khong dung thoi gian mac dinh");
    }

    private static void kiemTraDongBoCmd23BossRuaRong() throws Exception {
        ChickenNguoiChoi nguoiBan = taoNguoiChoiBossTest(92_201, "RuaRongShooter");
        ChickenNguoiChoi nguoiKhac = taoNguoiChoiBossTest(92_202, "RuaRongOther");
        SanhChoBoss sanh = taoSanhBossHaiNguoi(
                CauHinhBossRuaRong.MAP_ID, nguoiBan, nguoiKhac, 201L);
        BossRuaRong tran = new BossRuaRong(sanh);
        kiemTraDongBoCmd23(
                tran, BossRuaRong.class, nguoiBan, nguoiKhac, 201L);
        tran.dungBot();
    }

    private static void kiemTraGuiLuotClientChoBoss() throws Exception {
        kiemTraGuiLuotClientChoBossRua();
        kiemTraGuiLuotClientChoBossRuaRong();
    }

    private static void kiemTraGuiLuotClientChoBossRua() throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 92_301;
        nguoiChoi.ten = "RuaTurnTarget";
        SanhChoBoss sanh = new SanhChoBoss(
                (byte) 4, (byte) 0, (byte) CauHinhBossRua.MAP_ID,
                (byte) 8, 1_000);
        dung(sanh.themThanhVien(new ThanhVienBoss(
                        nguoiChoi, (byte) 0, 301L, true)),
                "khong them duoc nguoi vao test luot boss Rua");
        BossRua tran = new BossRua(sanh);
        Method phatLuot = BossRua.class.getDeclaredMethod(
                "phatLuot", ChickenChienBinh.class);
        phatLuot.setAccessible(true);
        ChickenChienBinh[] chienBinhs = tran.chupChienBinh();
        ChickenChienBinh nguoiTrongTran = chienBinhs[0];
        nguoiTrongTran.hawkDaGuiChonMucTieu = true;
        nguoiTrongTran.thorDaGuiMenu = true;
        nguoiTrongTran.lokiDaGuiMenu = true;
        nguoiTrongTran.lokiDangChoChonMucTieu = true;
        nguoiTrongTran.ultronDaGuiMenu = true;
        nguoiTrongTran.ultronDangBanX3 = true;
        nguoiTrongTran.ironManDaGuiMenu = true;

        phatLuot.invoke(tran, chienBinhs[8]);
        bang(1, dichVu.demLenh(24), "map 54 khong gui CMD24 cho slot boss");
        bang(1, dichVu.demLenh(-91),
                "map 54 doi sang boss khong dong menu skill");
        dung(dichVu.viTriLenhDau(-91) < dichVu.viTriLenhDau(24),
                "map 54 dong menu skill sau khi da phat luot boss");
        dung(!nguoiTrongTran.hawkDaGuiChonMucTieu
                        && !nguoiTrongTran.thorDaGuiMenu
                        && !nguoiTrongTran.lokiDaGuiMenu
                        && !nguoiTrongTran.lokiDangChoChonMucTieu
                        && !nguoiTrongTran.ultronDaGuiMenu
                        && !nguoiTrongTran.ultronDangBanX3
                        && !nguoiTrongTran.ironManDaGuiMenu,
                "map 54 dong UI nhung khong reset co menu AVG");
        phatLuot.invoke(tran, chienBinhs[0]);
        bang(2, dichVu.demLenh(24), "map 54 khong gui CMD24 cho nguoi choi");
        tran.dungBot();
    }

    private static void kiemTraGuiLuotClientChoBossRuaRong() throws Exception {
        DichVuBatPacket dichVu = new DichVuBatPacket();
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
        nguoiChoi.ma = 92_302;
        nguoiChoi.ten = "RuaRongTurnTarget";
        SanhChoBoss sanh = new SanhChoBoss(
                (byte) 4, (byte) 0, (byte) CauHinhBossRuaRong.MAP_ID,
                (byte) 8, 1_000);
        dung(sanh.themThanhVien(new ThanhVienBoss(
                        nguoiChoi, (byte) 0, 302L, true)),
                "khong them duoc nguoi vao test luot boss Rua Rong");
        BossRuaRong tran = new BossRuaRong(sanh);
        Method phatLuot = BossRuaRong.class.getDeclaredMethod(
                "phatLuot", ChickenChienBinh.class);
        phatLuot.setAccessible(true);
        ChickenChienBinh[] chienBinhs = tran.chupChienBinh();

        phatLuot.invoke(tran, chienBinhs[CauHinhBossRuaRong.SLOT_BOSS_DAU]);
        bang(1, dichVu.demLenh(24), "map 58 khong gui CMD24 cho slot boss");
        phatLuot.invoke(tran, chienBinhs[0]);
        bang(2, dichVu.demLenh(24), "map 58 khong gui CMD24 cho nguoi choi");
        tran.dungBot();
    }

    private static void kiemTraDongBoCmd23(
            Object tran,
            Class<?> loaiTran,
            ChickenNguoiChoi nguoiBan,
            ChickenNguoiChoi nguoiKhac,
            long maPhien
    ) throws Exception {
        Field luot = loaiTran.getDeclaredField("luotHienTai");
        luot.setAccessible(true);
        luot.setByte(tran, (byte) 0);
        Field phien = loaiTran.getDeclaredField("maPhienLuot");
        phien.setAccessible(true);
        phien.setLong(tran, maPhien);

        Method choKetThuc = loaiTran.getDeclaredMethod(
                "choServerKetThucPhatBan",
                ChickenChienBinh.class, int.class, long.class);
        choKetThuc.setAccessible(true);
        Method chupChienBinh = loaiTran.getDeclaredMethod("chupChienBinh");
        chupChienBinh.setAccessible(true);
        ChickenChienBinh nguoiBanTrongTran =
                ((ChickenChienBinh[]) chupChienBinh.invoke(tran))[0];
        choKetThuc.invoke(
                tran,
                nguoiBanTrongTran,
                100,
                ChickenThoiGianHoatAnhDan.TOI_THIEU_MS);

        bang(0, luot.getByte(tran),
                "server chuyen luot truoc khi client ket thuc duong dan");
        Method coTheNhanLenh = loaiTran.getDeclaredMethod(
                "coTheNhanLenhNguoiChoi", ChickenChienBinh.class);
        coTheNhanLenh.setAccessible(true);
        dung(!(boolean) coTheNhanLenh.invoke(tran, nguoiBanTrongTran),
                "dang cho CMD23 ma client van co the ban lan hai");
        nguoiBanTrongTran.avenger = ChickenKyNangDacBietThor.AVG_THOR;
        nguoiBanTrongTran.thorDaDungKyNang = false;
        ((ChickenQuanLyChien) tran).nhanLenhKyNangDacBiet(
                nguoiBan, tinSkill(3, 0));
        dung(!nguoiBanTrongTran.thorDaDungKyNang,
                "dang cho dan ma client chen them skill Thor cung luot");
        ((ChickenQuanLyChien) tran).boLuot(nguoiBan);
        bang(0, luot.getByte(tran),
                "dang cho CMD23 ma client co the bo luot de mo khoa action");
        ((com.chicken.chien.ChickenQuanLyChien) tran).kiemTraVaCham(
                nguoiBan, new ChickenTinNhan((byte) 79, new byte[0]));
        bang(0, luot.getByte(tran), "CMD79 lai duoc dung de mo khoa luot");
        ((com.chicken.chien.ChickenQuanLyChien) tran).kiemTraVaCham(
                nguoiBan, new ChickenTinNhan((byte) 23, new byte[]{1}));
        bang(0, luot.getByte(tran), "CMD23 co byte thua lai mo duoc khoa luot");
        ((com.chicken.chien.ChickenQuanLyChien) tran).kiemTraVaCham(
                nguoiKhac, new ChickenTinNhan((byte) 23, new byte[0]));
        bang(0, luot.getByte(tran), "nguoi khac gui CMD23 mo duoc khoa luot");

        ((com.chicken.chien.ChickenQuanLyChien) tran).kiemTraVaCham(
                nguoiBan, new ChickenTinNhan((byte) 23, new byte[0]));
        bang(0, luot.getByte(tran),
                "CMD23 dung van tua nhanh dong ho server");

        long hanCho = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        while (luot.getByte(tran) == 0 && System.nanoTime() < hanCho) {
            Thread.sleep(10L);
        }
        byte luotSau = luot.getByte(tran);
        dung(luotSau != 0,
                "server khong tu chuyen luot sau khi animation ket thuc");
        long phienSau = phien.getLong(tran);

        ((com.chicken.chien.ChickenQuanLyChien) tran).kiemTraVaCham(
                nguoiBan, new ChickenTinNhan((byte) 23, new byte[0]));
        bang(luotSau, luot.getByte(tran), "CMD23 lap bi xu ly hai lan");
        bang(phienSau, phien.getLong(tran), "CMD23 lap lam tang ma phien");
    }

    private static ChickenNguoiChoi taoNguoiChoiBossTest(int ma, String ten) {
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(new DichVuBatPacket());
        nguoiChoi.ma = ma;
        nguoiChoi.ten = ten;
        return nguoiChoi;
    }

    private static SanhChoBoss taoSanhBossHaiNguoi(
            int mapId,
            ChickenNguoiChoi nguoiThuNhat,
            ChickenNguoiChoi nguoiThuHai,
            long maPhien
    ) {
        SanhChoBoss sanh = new SanhChoBoss(
                (byte) 4, (byte) 0, (byte) mapId, (byte) 8, 1_000);
        dung(sanh.themThanhVien(new ThanhVienBoss(
                        nguoiThuNhat, (byte) 0, maPhien, true)),
                "khong them duoc nguoi ban vao sanh test CMD23");
        dung(sanh.themThanhVien(new ThanhVienBoss(
                        nguoiThuHai, (byte) 1, maPhien + 1L, true)),
                "khong them duoc nguoi thu hai vao sanh test CMD23");
        return sanh;
    }

    private static void kiemTraNguoiRoiTranKhongConPhien() {
        ChickenChienBinh chienBinh = nguoiChoiThat(
                (byte) 0,
                (short) 5,
                (byte) 1
        );
        chienBinh.nguoiChoi.dichVu = new ChickenDichVuGame(null);
        dung(chienBinh.coPhien(), "chien binh dang ket noi phai co phien");

        chienBinh.daRoiTran = true;
        dung(!chienBinh.coPhien(),
                "chien binh da roi van bi tinh la nguoi nhan packet");
    }

    private static void kiemTraRoiKhuRpgKhongTinChiSo() {
        ChickenKhu khu = new ChickenKhu(7);
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(null);
        nguoiChoi.ma = 91_001;
        nguoiChoi.ten = "RpgBossExit";
        nguoiChoi.chiSo = 3; // Ghế boss đã ghi đè slot RPG thật.
        nguoiChoi.zoneId = 7;
        nguoiChoi.zone = khu;

        khu.players_index.put(11, nguoiChoi);
        khu.players_id.put(nguoiChoi.ma, nguoiChoi);
        khu.numPlayer = 1;

        dung(khu.roi(nguoiChoi), "khong go duoc nguoi choi khoi khu RPG");
        dung(khu.players_index.isEmpty(), "slot RPG that van con sau khi roi");
        dung(khu.players_id.isEmpty(), "player-id RPG van con sau khi roi");
        bang(0, khu.numPlayer, "so nguoi trong khu khong ve 0");
        bang(-1, nguoiChoi.chiSo, "chiSo khong duoc xoa khi roi khu");
        laNull(nguoiChoi.zone, "tham chieu khu RPG khong duoc xoa");
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

    private static void kiemTraFriendlyFirePhongBoss() {
        ChickenQuanLyDanSung.DuLieuSung at4 = batBuocCoSung(110);
        ChickenLoatDanServer.KetQua duongAt4 = taoLoat(110);
        ChickenChienBinh shooter =
                nguoiChoiThat((byte) 0, at4.getPartSung(), (byte) 0);
        shooter.x = 180;
        shooter.y = 520;
        ChickenChienBinh dongDoi = nguoiChoiThat(
                (byte) 1, (short) 230, (byte) 8);
        int diem = 0;
        dongDoi.x = duongAt4.getCacDuongX()[0][diem];
        dongDoi.y = (short) (duongAt4.getCacDuongY()[0][diem] + 18);

        ChickenKetQuaDan ketQua = ChickenPhatBanServer.tao(
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
                new ChickenChienBinh[]{shooter, dongDoi},
                (nguoiBan, mucTieu) -> !nguoiBan.bot || !mucTieu.bot
        );
        dung(ketQua.satThuongTheoMucTieu.containsKey(dongDoi),
                "phong boss van loai dong doi khoi va cham/damage");
        dung(ketQua.satThuongTheoMucTieu.containsKey(shooter),
                "vu no phong boss van loai chinh nguoi ban");
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

    private static void kiemTraLoatUltronDocLap() {
        ChickenChienBinh ultron = chienBinh((byte) 0, (short) 230, (byte) 8);
        ultron.x = 100;
        ultron.y = 500;
        ultron.tanCong = 100;
        ChickenChienBinh mucTieu = chienBinh((byte) 1, (short) 110, (byte) 0);
        mucTieu.x = 300;
        mucTieu.y = 500;
        mucTieu.giap = 20;

        ChickenQuanLyBanDo mapTrong = new ChickenQuanLyBanDo(0) {
            @Override
            public int getWidth() { return 2_000; }
            @Override
            public int getHeight() { return 1_000; }
            @Override
            public synchronized boolean coVaCham(short x, short y) {
                return false;
            }
        };
        ChickenKetQuaDan trungNguoi = ChickenLoatBanUltronServer.tao(
                ultron,
                (short) 100,
                (short) 482,
                (short) 0,
                (byte) 30,
                mapTrong,
                new ChickenChienBinh[]{ultron, mucTieu},
                (nguoiBan, dich) -> true
        );
        bang(3, trungNguoi.cacDuongX.length,
                "Ultron x3 khong co du ba quy dao");
        bang(240, trungNguoi.satThuongTheoMucTieu.get(mucTieu),
                "Ultron x3 khong cong damage cua ba vien trung that");
        dung(trungNguoi.cacDuongX[0] != trungNguoi.cacDuongX[1]
                        && trungNguoi.cacDuongX[1] != trungNguoi.cacDuongX[2],
                "Ultron x3 tai su dung cung mot object quy dao");

        final int[] vachX = {300};
        final int[] soLanPha = {0};
        ChickenQuanLyBanDo mapBaLop = new ChickenQuanLyBanDo(0) {
            @Override
            public int getWidth() { return 2_000; }
            @Override
            public int getHeight() { return 1_000; }
            @Override
            public synchronized boolean coVaCham(short x, short y) {
                return x >= vachX[0] && y >= 0 && y < 1_000;
            }
            @Override
            public synchronized void phaDiaHinh(int x, int y, byte loaiDan) {
                soLanPha[0]++;
                vachX[0] += 40;
            }
        };
        ChickenKetQuaDan trungMap = ChickenLoatBanUltronServer.tao(
                ultron,
                (short) 100,
                (short) 482,
                (short) 0,
                (byte) 30,
                mapBaLop,
                new ChickenChienBinh[]{ultron},
                (nguoiBan, dich) -> true
        );
        bang(3, soLanPha[0], "Ultron x3 chi pha dia hinh mot lan");
        int x1 = trungMap.cacDuongX[0][trungMap.cacDuongX[0].length - 1];
        int x2 = trungMap.cacDuongX[1][trungMap.cacDuongX[1].length - 1];
        int x3 = trungMap.cacDuongX[2][trungMap.cacDuongX[2].length - 1];
        dung(x1 < x2 && x2 < x3,
                "vien sau Ultron khong duoc tinh lai sau lo vien truoc");
        dung(trungMap.satThuongTheoMucTieu.isEmpty(),
                "ban vao dia hinh lai tao damage nhan vat gia");
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

    private static final class DichVuNemLoi extends ChickenDichVuGame {
        private DichVuNemLoi() {
            super(null);
        }

        @Override
        public void guiTin(ChickenTinNhan tinNhan) {
            throw new IllegalStateException("loi connection chu dong");
        }
    }

    private static final class DichVuBatPacket extends ChickenDichVuGame {
        private final List<Byte> lenhs = new ArrayList<>();
        private final List<ChickenTinNhan> tinNhans = new ArrayList<>();
        private int soLanGuiThongTin;

        private DichVuBatPacket() {
            super(null);
        }

        @Override
        public synchronized void guiTin(ChickenTinNhan tinNhan) {
            if (tinNhan != null) {
                this.lenhs.add(tinNhan.layLenh());
                this.tinNhans.add(tinNhan);
                this.notifyAll();
            }
        }

        @Override
        public synchronized void guiThongTin() {
            this.soLanGuiThongTin++;
            // Mock không có ChickenPhien nên không thể dùng implementation production.
        }

        private synchronized int demGuiThongTin() {
            return this.soLanGuiThongTin;
        }

        private synchronized boolean daNhan(int lenh) {
            return this.lenhs.contains((byte) lenh);
        }

        private synchronized int demLenh(int lenh) {
            int dem = 0;
            for (byte giaTri : this.lenhs) {
                if (giaTri == (byte) lenh) {
                    dem++;
                }
            }
            return dem;
        }

        private synchronized int viTriLenhDau(int lenh) {
            return this.lenhs.indexOf((byte) lenh);
        }

        private boolean choLenh(int lenh, long thoiGian, TimeUnit donVi)
                throws InterruptedException {
            long hetHan = System.nanoTime() + donVi.toNanos(thoiGian);
            synchronized (this) {
                while (!this.daNhan(lenh)) {
                    long conLai = hetHan - System.nanoTime();
                    if (conLai <= 0) {
                        return false;
                    }
                    TimeUnit.NANOSECONDS.timedWait(this, conLai);
                }
                return true;
            }
        }

        private synchronized ChickenTinNhan layTinCuoi(int lenh) {
            for (int i = this.tinNhans.size() - 1; i >= 0; i--) {
                ChickenTinNhan tinNhan = this.tinNhans.get(i);
                if (tinNhan.layLenh() == (byte) lenh) {
                    return tinNhan;
                }
            }
            return null;
        }
    }
}
