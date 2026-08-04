package com.chicken.kiemthu;

import com.chicken.avg.ChickenKyNangDacBietHawk;
import com.chicken.avg.ChickenKyNangDacBietIronMan;
import com.chicken.avg.ChickenKyNangDacBietLoki;
import com.chicken.avg.ChickenKyNangDacBietThor;
import com.chicken.avg.ChickenKyNangDacBietUltron;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenNapDanServer;
import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.chien.ChickenTrongLucDiaHinhServer;
import com.chicken.chiso.ChickenChiSoNguoiChoi;
import com.chicken.bando.ChickenDuLieuBanDo;
import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.sanhcho.ThanhVienBoss;
import com.chicken.phong.boss.trandau.baovay.BossBaoVay;
import com.chicken.phong.boss.trandau.datbom.BossDatBom;
import com.chicken.phong.boss.trandau.haitoathap.BossHaiToaThap;
import com.chicken.phong.boss.trandau.khicau.BossKhiCau;
import com.chicken.phong.boss.trandau.rong.BossRong;
import com.chicken.phong.boss.trandau.rua.BossRua;
import com.chicken.phong.boss.trandau.ruarong.BossRuaRong;
import com.chicken.vatpham.ChickenMauThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.tienich.ChickenTienIch;
import com.chicken.taikhoan.ChickenBaoMatTaiKhoanTestSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Entry point kiem thu chung toan game.
 *
 * <p>File nay gom cac bo hoi quy hien co va ma tran bat buoc cho tat ca che do
 * chien dau. Moi loi tung gap chi can them vao entry point nay de Gradle
 * {@code check} tu dong chay lai tren PvP, luyen tap va tat ca map boss.</p>
 */
public final class ChickenKiemThuToanGame {
    private static final int[] MAP_BOSS = {50, 51, 52, 53, 54, 55, 58};
    private static int soMaTranDaChay;

    private ChickenKiemThuToanGame() {
    }

    public static void main(String[] args) throws Exception {
        /*
         * Bo cu da gom parser packet, PvP, luyen tap, RPG, kinh te boss,
         * cong thuc 111 sung/AVG va cac hoi quy chuyen sau cua Rua.
         */
        ChickenKiemThuBan.main(args);
        ChickenKiemThuPow.main(args);
        ChickenBaoMatTaiKhoanTestSupport.chay();

        chay("7 boss: moi luot chi nhan mot phat ban",
                ChickenKiemThuToanGame::kiemTraMotPhatMoiLuotTatCaBoss);
        chay("7 boss: ma tran bo luot hop le va bi chan",
                ChickenKiemThuToanGame::kiemTraBoLuotTatCaBoss);
        chay("7 boss: het gio nguoi choi luon la bo luot 250",
                ChickenKiemThuToanGame::kiemTraHetGioLaBoLuotTatCaBoss);
        chay("map 51: server luu dung Y sau khi pha nen duoi Phien quan",
                ChickenKiemThuToanGame::kiemTraDongBoYPhienQuanMap51);
        chay("7 boss: chi mo GameScr sau ACK tai anh dan",
                ChickenKiemThuToanGame::kiemTraKhongGuiGameScrSom);
        chay("7 boss: doi sung Balo dung snapshot va chan packet sai",
                ChickenKiemThuToanGame::kiemTraDoiSungTatCaBoss);
        System.out.println(
                "WEAPON_SWAP_MATRIX_OK modes=9"
                + " weaponSequence=110>120>160>110>130>133>130>110"
                + " clientSlots=0,1,0,2,3,3,2 sameFamilyMG=ok"
                + " bossMaps=" + MAP_BOSS.length
                + " inventoryMutation=0");
        System.out.println("GAME_TEST_OK matrices=" + soMaTranDaChay
                + " bossMaps=" + MAP_BOSS.length);
    }

    private static void kiemTraDongBoYPhienQuanMap51() throws Exception {
        ArrayList<ChickenDuLieuBanDo.MapDataEntry> entrysCu =
                ChickenDuLieuBanDo.entrys;
        ArrayList<ChickenDuLieuBanDo.MapBrickEntry> bricksCu =
                ChickenDuLieuBanDo.brickEntrys;
        try {
            byte[] duLieu = ChickenTienIch.layTep("res/map/51");
            dung(duLieu != null && duLieu.length > 5,
                    "thieu res/map/51 cho test trong luc");
            ChickenDuLieuBanDo.entrys = new ArrayList<>();
            ChickenDuLieuBanDo.brickEntrys = new ArrayList<>();
            ChickenDuLieuBanDo.entrys.add(
                    new ChickenDuLieuBanDo.MapDataEntry(
                            duLieu, (byte) 51, "Hai toa thap gravity",
                            (short) 0, (byte) 15));

            ChickenQuanLyBanDo map = new ChickenQuanLyBanDo(51);
            ChickenChienBinh phienQuan = new ChickenChienBinh(
                    (byte) 8, -51_008, (short) 422, (short) 125,
                    "Phien quan gravity", (short) 57,
                    1_050, 100, 0);
            map.phaDiaHinh(424, 125, (byte) 21);
            int soCapNhat =
                    ChickenTrongLucDiaHinhServer.dongBoYSauPhaDiaHinh(
                            map, new ChickenChienBinh[]{phienQuan},
                            chienBinh -> false);

            bang(1, soCapNhat,
                    "server khong cap nhat Y Phien quan sau pha nen");
            bang(134, phienQuan.y,
                    "Y server khong khop log LAND client map 51");
        } finally {
            ChickenDuLieuBanDo.entrys = entrysCu;
            ChickenDuLieuBanDo.brickEntrys = bricksCu;
        }
        System.out.println("MAP51_TERRAIN_FALL_SYNC_OK slot=8 y=125>134");
    }

    /**
     * Het dong ho khi nguoi choi chua ban la bo luot, khong phai mot phat ban.
     * Vi vay ket qua khong duoc phu thuoc sung/AVG dang cam.
     */
    private static void kiemTraHetGioLaBoLuotTatCaBoss() throws Exception {
        for (int mapId : MAP_BOSS) {
            DichVuBatPacket dichVu = new DichVuBatPacket();
            ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
            nguoiChoi.ma = 95_000 + mapId;
            nguoiChoi.ten = "BossTimeout" + mapId;
            nguoiChoi.wp = 57;

            SanhChoBoss sanh = new SanhChoBoss(
                    (byte) 4, (byte) (mapId - 50), (byte) mapId,
                    (byte) 8, 1_000);
            dung(sanh.themThanhVien(new ThanhVienBoss(
                            nguoiChoi, (byte) 0, mapId, true)),
                    "khong them duoc player timeout map=" + mapId);

            ChickenQuanLyChien tran = taoTranBoss(mapId, sanh);
            khacNull(tran, "khong tao duoc boss timeout map=" + mapId);
            try {
                ChickenChienBinh nguoi = chupChienBinh(tran)[0];
                khacNull(nguoi, "thieu chien binh timeout map=" + mapId);
                // Dung mot ma sung co nap dan khac 250 de bat hoi quy ro rang.
                nguoi.maVuKhi = 57;
                Method tinhNap = tran.getClass().getDeclaredMethod(
                        "layNapDanKhiHetThoiGian",
                        int.class, ChickenChienBinh.class);
                tinhNap.setAccessible(true);
                int napDan = (Integer) tinhNap.invoke(tran, 0, nguoi);
                bang(ChickenNapDanServer.TOI_THIEU, napDan,
                        "het gio van lay nap dan sung map=" + mapId);
            } finally {
                tran.dungBot();
            }
        }
        System.out.println("BOSS_TIMEOUT_SKIP_OK maps=" + MAP_BOSS.length
                + " reload=" + ChickenNapDanServer.TOI_THIEU);
    }

    /**
     * Ma tran doi sung tren tat ca boss. Moi map phai dung cung mot invariant:
     * inventory that bat bien, chi snapshot tran duoc hoan doi, cong thuc sung
     * va nap dan doi theo sung dang cam, client nhan -45 roi -42. Moi yeu cau
     * sai trang thai phai la no-op.
     */
    private static void kiemTraDoiSungTatCaBoss() throws Exception {
        for (int mapId : MAP_BOSS) {
            DichVuBatPacket dichVu = new DichVuBatPacket();
            ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
            nguoiChoi.ma = 98_000 + mapId;
            nguoiChoi.ten = "BossWeaponSwap" + mapId;
            nguoiChoi.avenger = 0;
            dichVu.datNguoiChoi(nguoiChoi);

            ChickenVatPham at4 = taoSung(
                    110, 5, 57, 100, 700, 839);
            ChickenVatPham k98 = taoSung(
                    120, 3, 27, 250, 430, 848);
            ChickenVatPham coi = taoSung(
                    160, 8, 56, 400, 620, 886);
            ChickenVatPham mg42 = taoSung(
                    130, 5, 54, 310, 300, 931);
            ChickenVatPham m61 = taoSung(
                    133, 5, 145, 360, 340, 934);
            nguoiChoi.itemBody[5] = at4;
            nguoiChoi.itemBag[3] = k98;
            nguoiChoi.itemBag[8] = coi;
            // Co y dat index itemBag khac xa vi tri Balo 0..3. Day moi la
            // payload CMD26 client that gui va la hoi quy cho loi MG42.
            nguoiChoi.itemBag[27] = mg42;
            nguoiChoi.itemBag[64] = m61;
            nguoiChoi.itemBalo = new int[]{3, 8, 27, 64};

            SanhChoBoss sanh = new SanhChoBoss(
                    (byte) 4,
                    (byte) (mapId - 50),
                    (byte) mapId,
                    (byte) 8,
                    1_000
            );
            dung(sanh.themThanhVien(new ThanhVienBoss(
                            nguoiChoi, (byte) 0, mapId, true)),
                    "khong them duoc player doi sung map=" + mapId);

            ChickenQuanLyChien tran = taoTranBoss(mapId, sanh);
            khacNull(tran, "khong tao duoc boss doi sung map=" + mapId);
            try {
                datBoolean(tran, "daBatDau", true);
                datByte(tran, "luotHienTai", (byte) 0);
                ChickenChienBinh nguoi = chupChienBinh(tran)[0];
                khacNull(nguoi,
                        "khong co snapshot player doi sung map=" + mapId);
                nguoi.hp = nguoi.mauToiDa = 10_000;
                nguoi.chet = false;
                nguoi.avenger = 0;

                dichVu.xoaTin();
                dung(tran.doiSungTrongTran(nguoiChoi, 0),
                        "khong doi duoc sang K98 map=" + mapId);
                xacNhanSungDangCam(nguoiChoi, nguoi, k98,
                        120, 27, 430, mapId, "K98");
                bang(1, dichVu.demLenh(-45),
                        "doi K98 khong gui dung mot -45 map=" + mapId);
                bang(1, dichVu.demLenh(-42),
                        "doi K98 khong gui dung mot -42 map=" + mapId);
                xacNhanPacketDoiSung(dichVu.layTinCuoi(-45),
                        0, 27, 839, mapId);

                dung(tran.doiSungTrongTran(nguoiChoi, 1),
                        "khong doi duoc sang coi map=" + mapId);
                xacNhanSungDangCam(nguoiChoi, nguoi, coi,
                        160, 56, 620, mapId, "coi");
                dung(tran.doiSungTrongTran(nguoiChoi, 0),
                        "khong doi duoc lai AT4 map=" + mapId);
                xacNhanSungDangCam(nguoiChoi, nguoi, at4,
                        110, 57, 700, mapId, "AT4");

                dung(tran.doiSungTrongTran(nguoiChoi, 2),
                        "khong doi duoc sang MG42 map=" + mapId);
                xacNhanSungDangCam(nguoiChoi, nguoi, mg42,
                        130, 54, 300, mapId, "MG42");
                dung(tran.doiSungTrongTran(nguoiChoi, 3),
                        "khong doi duoc sang M61 map=" + mapId);
                xacNhanSungDangCam(nguoiChoi, nguoi, m61,
                        133, 145, 340, mapId, "M61");
                dung(tran.doiSungTrongTran(nguoiChoi, 3),
                        "khong doi lai MG42 cung nhom map=" + mapId);
                xacNhanSungDangCam(nguoiChoi, nguoi, mg42,
                        130, 54, 300, mapId, "MG42 cung nhom");
                dung(tran.doiSungTrongTran(nguoiChoi, 2),
                        "khong doi lai AT4 sau nhom MG map=" + mapId);
                xacNhanSungDangCam(nguoiChoi, nguoi, at4,
                        110, 57, 700, mapId, "AT4 sau MG");

                // Inventory/persistence that khong duoc phep bi hoan doi.
                cung(at4, nguoiChoi.itemBody[5],
                        "doi sung sua itemBody map=" + mapId);
                cung(k98, nguoiChoi.itemBag[3],
                        "doi sung sua itemBag[3] map=" + mapId);
                cung(coi, nguoiChoi.itemBag[8],
                        "doi sung sua itemBag[8] map=" + mapId);
                cung(mg42, nguoiChoi.itemBag[27],
                        "doi sung sua itemBag[27] map=" + mapId);
                cung(m61, nguoiChoi.itemBag[64],
                        "doi sung sua itemBag[64] map=" + mapId);
                bang(3, nguoiChoi.itemBalo[0],
                        "doi sung sua ref Balo 0 map=" + mapId);
                bang(8, nguoiChoi.itemBalo[1],
                        "doi sung sua ref Balo 1 map=" + mapId);
                bang(27, nguoiChoi.itemBalo[2],
                        "doi sung sua ref Balo 2 map=" + mapId);
                bang(64, nguoiChoi.itemBalo[3],
                        "doi sung sua ref Balo 3 map=" + mapId);

                int packetTruoc = dichVu.tongSoTin();
                ChickenVatPham sungTruoc = nguoi.laySungDangCamTrongTran();
                dung(!tran.doiSungTrongTran(nguoiChoi, 255),
                        "index 255 van doi duoc sung map=" + mapId);
                xacNhanBiChan(nguoi, sungTruoc, dichVu, packetTruoc,
                        mapId, "index ngoai bien");

                datByte(tran, "luotHienTai", (byte) 8);
                dung(!tran.doiSungTrongTran(nguoiChoi, 1),
                        "sai luot van doi duoc sung map=" + mapId);
                xacNhanBiChan(nguoi, sungTruoc, dichVu, packetTruoc,
                        mapId, "sai luot");
                datByte(tran, "luotHienTai", (byte) 0);

                nguoi.chet = true;
                dung(!tran.doiSungTrongTran(nguoiChoi, 1),
                        "da chet van doi duoc sung map=" + mapId);
                xacNhanBiChan(nguoi, sungTruoc, dichVu, packetTruoc,
                        mapId, "da chet");
                nguoi.chet = false;

                nguoi.avenger = 1;
                dung(!tran.doiSungTrongTran(nguoiChoi, 1),
                        "AVG van doi duoc sung Balo map=" + mapId);
                xacNhanBiChan(nguoi, sungTruoc, dichVu, packetTruoc,
                        mapId, "AVG");
                nguoi.avenger = 0;

                coi.HP = 0;
                dung(!tran.doiSungTrongTran(nguoiChoi, 0),
                        "sung het do ben van doi duoc map=" + mapId);
                xacNhanBiChan(nguoi, sungTruoc, dichVu, packetTruoc,
                        mapId, "sung khong hop le");
                coi.HP = ChickenVatPham.DO_BEN_TOI_DA;

                Field dangCho = fieldNeuCo(
                        tran, "slotDangChoKetThucBan");
                if (dangCho != null) {
                    dangCho.setInt(tran, 0);
                    dung(!tran.doiSungTrongTran(nguoiChoi, 1),
                            "dang xu ly dan van doi sung map=" + mapId);
                    xacNhanBiChan(nguoi, sungTruoc, dichVu, packetTruoc,
                            mapId, "dang xu ly dan");
                    dangCho.setInt(tran, -1);
                }

                datBoolean(tran, "daKetThuc", true);
                dung(!tran.doiSungTrongTran(nguoiChoi, 1),
                        "het tran van doi duoc sung map=" + mapId);
                xacNhanBiChan(nguoi, sungTruoc, dichVu, packetTruoc,
                        mapId, "da ket thuc");
            } finally {
                tran.dungBot();
            }
        }
    }

    private static void xacNhanSungDangCam(
            ChickenNguoiChoi nguoiChoi,
            ChickenChienBinh chienBinh,
            ChickenVatPham sung,
            int idSung,
            int part,
            int napDan,
            int mapId,
            String tenSung
    ) {
        cung(sung, chienBinh.laySungDangCamTrongTran(),
                "snapshot khong cam " + tenSung + " map=" + mapId);
        bang(part, chienBinh.maVuKhi,
                "sai part " + tenSung + " map=" + mapId);
        bang(napDan, ChickenNapDanServer.layChoChienBinh(chienBinh),
                "sai nap dan " + tenSung + " map=" + mapId);
        bang(ChickenChiSoNguoiChoi.tinhTanCongVoiSung(nguoiChoi, sung),
                chienBinh.tanCong,
                "sai tan cong " + tenSung + " map=" + mapId);
        ChickenQuanLyDanSung.DuLieuSung duLieu =
                ChickenQuanLyDanSung.theoSungDangTrangBi(sung);
        khacNull(duLieu,
                "thieu cong thuc " + tenSung + " map=" + mapId);
        bang(idSung, duLieu.getIdSung(),
                "sai mapping cong thuc " + tenSung + " map=" + mapId);
    }

    private static void xacNhanPacketDoiSung(
            ChickenTinNhan tin,
            int slot,
            int part,
            int iconCu,
            int mapId
    ) throws Exception {
        khacNull(tin, "khong co packet -45 map=" + mapId);
        DataInputStream in = new ChickenTinNhan(
                (byte) -45, tin.layDuLieu()).boDoc();
        bang(slot, in.readUnsignedByte(),
                "packet -45 sai slot map=" + mapId);
        bang(part, in.readShort(),
                "packet -45 sai part map=" + mapId);
        bang(iconCu, in.readShort(),
                "packet -45 sai icon sung cu map=" + mapId);
        bang(0, in.available(),
                "packet -45 con byte thua map=" + mapId);
    }

    private static void xacNhanBiChan(
            ChickenChienBinh chienBinh,
            ChickenVatPham sungTruoc,
            DichVuBatPacket dichVu,
            int packetTruoc,
            int mapId,
            String tinhHuong
    ) {
        cung(sungTruoc, chienBinh.laySungDangCamTrongTran(),
                tinhHuong + " lam doi snapshot map=" + mapId);
        bang(packetTruoc, dichVu.tongSoTin(),
                tinhHuong + " van gui packet map=" + mapId);
    }

    private static ChickenVatPham taoSung(
            int ma,
            int chiSo,
            int part,
            int tanCong,
            int napDan,
            int icon
    ) {
        ChickenVatPham item = new ChickenVatPham(ma);
        item.ma = ma;
        item.mau = new ChickenMauVatPham(
                (short) ma, (byte) 5, (byte) 0,
                "Gun" + ma, "", (byte) 1, 0,
                (short) icon, (short) part, false);
        item.chiSo = chiSo;
        item.HP = ChickenVatPham.DO_BEN_TOI_DA;
        item.itemOptions.add(taoOption(1, tanCong));
        item.itemOptions.add(taoOption(14, napDan));
        return item;
    }

    private static ChickenThuocTinhVatPham taoOption(int ma, int thamSo) {
        ChickenMauThuocTinhVatPham mau =
                new ChickenMauThuocTinhVatPham();
        mau.ma = ma;
        ChickenThuocTinhVatPham option =
                new ChickenThuocTinhVatPham(ma, thamSo);
        option.optionTemplate = mau;
        return option;
    }

    /**
     * CMD 20 khoi tao tran va lam client tai anh dan. Client chi gui ACK -67
     * sau khi BulletForGun.nMustGet ve 0; server khong duoc gui -67 som trong
     * batDau(), neu khong client co the quay lai BoardScr khi tai nguyen den
     * lech thu tu.
     */
    private static void kiemTraKhongGuiGameScrSom() throws Exception {
        for (int mapId : MAP_BOSS) {
            DichVuBatPacket dichVu = new DichVuBatPacket();
            ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
            nguoiChoi.ma = 97_000 + mapId;
            nguoiChoi.ten = "BossSceneHandshake" + mapId;
            nguoiChoi.wp = 57;
            ChickenVatPham toNhen = new ChickenVatPham(229);
            toNhen.mau = new ChickenMauVatPham(
                    (short) 229, (byte) 10, (byte) 9,
                    "To nhen", "", (byte) 1, 0,
                    (short) 229, (short) 0, false);
            toNhen.chiSo = 0;
            toNhen.soLuong = 2;
            nguoiChoi.itemBag[0] = toNhen;
            nguoiChoi.itemBalo = new int[]{0, -1, -1, -1, -1};

            SanhChoBoss sanh = new SanhChoBoss(
                    (byte) 4,
                    (byte) (mapId - 50),
                    (byte) mapId,
                    (byte) 8,
                    1_000
            );
            dung(sanh.themThanhVien(new ThanhVienBoss(
                            nguoiChoi, (byte) 0, mapId, true)),
                    "khong them duoc player handshake map=" + mapId);

            ChickenQuanLyChien tran = taoTranBoss(mapId, sanh);
            khacNull(tran, "khong tao duoc boss handshake map=" + mapId);
            try {
                tran.batDau();
                bang(1, dichVu.demLenh(20),
                        "boss khong gui dung mot CMD20 map=" + mapId);
                bang(1, dichVu.demLenh(-42),
                        "boss khong gui lai balo chien dau map=" + mapId);
                ChickenTinNhan tinBalo = dichVu.layTinCuoi(-42);
                khacNull(tinBalo,
                        "boss thieu packet balo map=" + mapId);
                try (DataInputStream duLieu = new DataInputStream(
                        new ByteArrayInputStream(tinBalo.layDuLieu()))) {
                    bang(0, duLieu.readUnsignedByte(),
                            "sai mode balo boss map=" + mapId);
                    bang(5, duLieu.readUnsignedByte(),
                            "sai so o balo boss map=" + mapId);
                    bang(229, duLieu.readUnsignedShort(),
                            "To nhen bi mat khi vao boss map=" + mapId);
                }
                bang(2, toNhen.soLuong,
                        "vao boss tu tru To nhen trong kho map=" + mapId);
                bang(0, dichVu.demLenh(-67),
                        "boss gui -67 truoc ACK tai anh dan map=" + mapId);
            } finally {
                tran.dungBot();
            }
        }
    }

    /**
     * Hoi quy chung cho bug spam tung xuat hien o Boss Rua:
     *
     * <ul>
     *   <li>packet ban dau tien hop le chi sinh dung mot CMD22;</li>
     *   <li>spam lai CMD22, CMD21, bo luot va skill khong tao action thu hai;</li>
     *   <li>CMD23/CMD79 cua client khong duoc tua nhanh ma phien/luot server;</li>
     *   <li>dung tran phai go router nguoi choi.</li>
     * </ul>
     *
     * <p>Map 54/58 khoa action trong khi cho server het animation. Cac map con
     * lai doi luot ngay sau phat ban. Hai cach noi bo khac nhau nhung cung phai
     * giu invariant mot action ban cho moi luot.</p>
     */
    private static void kiemTraMotPhatMoiLuotTatCaBoss() throws Exception {
        for (int mapId : MAP_BOSS) {
            DichVuBatPacket dichVu = new DichVuBatPacket();
            ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(dichVu);
            nguoiChoi.ma = 96_000 + mapId;
            nguoiChoi.ten = "GlobalBossRegression" + mapId;
            nguoiChoi.wp = 57;

            SanhChoBoss sanh = new SanhChoBoss(
                    (byte) 4,
                    (byte) (mapId - 50),
                    (byte) mapId,
                    (byte) 8,
                    1_000
            );
            dung(sanh.themThanhVien(new ThanhVienBoss(
                            nguoiChoi, (byte) 0, mapId, true)),
                    "khong them duoc player map=" + mapId);

            ChickenQuanLyChien tran = taoTranBoss(mapId, sanh);
            khacNull(tran, "khong tao duoc boss map=" + mapId);
            try {
                datBoolean(tran, "daBatDau", true);
                datByte(tran, "luotHienTai", (byte) 0);
                datLong(tran, "maPhienLuot", 10_000L + mapId);

                ChickenChienBinh[] chienBinhs = chupChienBinh(tran);
                ChickenChienBinh nguoiBan = chienBinhs[0];
                khacNull(nguoiBan,
                        "khong tao chien binh map=" + mapId);
                nguoiBan.maVuKhi = 57;
                nguoiBan.avenger = 0;
                nguoiBan.hp = nguoiBan.mauToiDa = 10_000;
                nguoiBan.quangDuongDiChuyenConLai = 100;

                long phienTruocBoLuot = layLong(tran, "maPhienLuot");
                tran.boLuot(nguoiChoi);
                dung(layByte(tran, "luotHienTai") != 0,
                        "bo luot hop le khong sang boss map=" + mapId);
                bang(ChickenNapDanServer.TOI_THIEU,
                        layMangInt(tran, "napDan")[0],
                        "bo luot boss khong gan nap dan 250 map=" + mapId);
                dung(layLong(tran, "maPhienLuot") != phienTruocBoLuot,
                        "bo luot hop le khong doi ma phien map=" + mapId);
                /*
                 * Vô hiệu hóa task boss vừa được xếp sau phép thử bỏ lượt rồi
                 * đưa trận về lượt người chơi để tiếp tục ma trận phát bắn.
                 */
                datLong(tran, "maPhienLuot",
                        layLong(tran, "maPhienLuot") + 1_000L);
                datByte(tran, "luotHienTai", (byte) 0);

                ChickenTinNhan packetGia = new ChickenTinNhan(
                        (byte) 22,
                        taoPacketBan(
                                (byte) 127,
                                (short) 30_000,
                                (short) -30_000,
                                (short) 721,
                                255,
                                255
                        )
                );
                tran.ban(nguoiChoi, packetGia);
                bang(1, dichVu.demLenh(22),
                        "phat dau khong sinh dung mot CMD22 map=" + mapId);

                byte luotSauPhat = layByte(tran, "luotHienTai");
                /*
                 * Vo hieu hoa task boss da dat lich de test khong phu thuoc toc
                 * do may. Task cu se tu bo qua vi ma phien khong con trung.
                 */
                long phienCoLap = layLong(tran, "maPhienLuot") + 1_000L;
                datLong(tran, "maPhienLuot", phienCoLap);
                short xSauPhat = nguoiBan.x;
                short ySauPhat = nguoiBan.y;
                nguoiBan.avenger =
                        ChickenKyNangDacBietHawk.AVG_HAWK;

                for (int i = 0; i < 20; i++) {
                    tran.ban(nguoiChoi, new ChickenTinNhan(
                            (byte) 22,
                            taoPacketBan(
                                    (byte) 0,
                                    xSauPhat,
                                    ySauPhat,
                                    (short) 45,
                                    30,
                                    1
                            )
                    ));
                    tran.diChuyen(nguoiChoi, new ChickenTinNhan(
                            (byte) 21,
                            taoPacketToaDo((short) 30_000, (short) -30_000)
                    ));
                    tran.boLuot(nguoiChoi);
                    tran.nhanLenhKyNangDacBiet(
                            nguoiChoi,
                            new ChickenTinNhan(
                                    (byte) -91, new byte[]{0, 0})
                    );
                    tran.kiemTraVaCham(
                            nguoiChoi,
                            new ChickenTinNhan(
                                    (byte) 23,
                                    i < 3 ? new byte[0] : new byte[]{1})
                    );
                    tran.kiemTraVaCham(
                            nguoiChoi,
                            new ChickenTinNhan(
                                    (byte) 79, new byte[]{127, 1, 2})
                    );
                }

                bang(1, dichVu.demLenh(22),
                        "spam tao them phat ban map=" + mapId);
                bang(xSauPhat, nguoiBan.x,
                        "spam di chuyen doi X map=" + mapId);
                bang(ySauPhat, nguoiBan.y,
                        "spam di chuyen doi Y map=" + mapId);
                bang(luotSauPhat, layByte(tran, "luotHienTai"),
                        "CMD23/CMD79 tua nhanh luot map=" + mapId);
                bang(phienCoLap, layLong(tran, "maPhienLuot"),
                        "CMD23/CMD79 tua nhanh ma phien map=" + mapId);
                dung(!nguoiBan.hawkDaDungKyNang
                                && !nguoiBan.hawkDaGuiChonMucTieu,
                        "spam kich hoat duoc skill map=" + mapId);
            } finally {
                tran.dungBot();
            }
            dung(ChickenQuanLyChien.timTranDauCuaNguoiChoi(nguoiChoi)
                            == null,
                    "dung boss khong go router map=" + mapId);
        }
    }

    private static void kiemTraBoLuotTatCaBoss() throws Exception {
        int soNhanhDaKiemTra = 0;
        for (int mapId : MAP_BOSS) {
            ChickenNguoiChoi nguoiChoi =
                    new ChickenNguoiChoi(new DichVuBatPacket());
            nguoiChoi.ma = 98_000 + mapId;
            nguoiChoi.ten = "SkipMatrix" + mapId;
            nguoiChoi.wp = 57;

            SanhChoBoss sanh = new SanhChoBoss(
                    (byte) 4,
                    (byte) (mapId - 50),
                    (byte) mapId,
                    (byte) 8,
                    1_000
            );
            dung(sanh.themThanhVien(new ThanhVienBoss(
                            nguoiChoi, (byte) 0, mapId, true)),
                    "khong them duoc player bo luot map=" + mapId);

            ChickenQuanLyChien tran = taoTranBoss(mapId, sanh);
            khacNull(tran, "khong tao duoc boss bo luot map=" + mapId);
            try {
                datBoolean(tran, "daBatDau", true);
                datByte(tran, "luotHienTai", (byte) 0);
                datLong(tran, "maPhienLuot", 20_000L + mapId);
                ChickenChienBinh nguoi =
                        chupChienBinh(tran)[0];
                khacNull(nguoi,
                        "khong co chien binh bo luot map=" + mapId);
                nguoi.hp = nguoi.mauToiDa = 10_000;
                nguoi.chet = false;

                ChickenNguoiChoi nguoiNgoai =
                        new ChickenNguoiChoi(new DichVuBatPacket());
                nguoiNgoai.ma = 99_000 + mapId;
                nguoiNgoai.ten = "SkipOutsider" + mapId;
                xacNhanBossKhongBoLuot(
                        tran, null, "null map=" + mapId);
                xacNhanBossKhongBoLuot(
                        tran, nguoiNgoai, "ngoai tran map=" + mapId);
                soNhanhDaKiemTra += 2;

                datByte(tran, "luotHienTai", (byte) 8);
                xacNhanBossKhongBoLuot(
                        tran, nguoiChoi, "sai luot map=" + mapId);
                datByte(tran, "luotHienTai", (byte) 0);
                soNhanhDaKiemTra++;

                nguoi.chet = true;
                xacNhanBossKhongBoLuot(
                        tran, nguoiChoi, "da chet map=" + mapId);
                nguoi.chet = false;
                soNhanhDaKiemTra++;

                datBoolean(tran, "daKetThuc", true);
                xacNhanBossKhongBoLuot(
                        tran, nguoiChoi, "da ket thuc map=" + mapId);
                datBoolean(tran, "daKetThuc", false);
                soNhanhDaKiemTra++;

                nguoi.avenger = ChickenKyNangDacBietThor.AVG_THOR;
                nguoi.thorDaDungKyNang = true;
                xacNhanBossKhongBoLuot(
                        tran, nguoiChoi, "Thor dang chay map=" + mapId);
                nguoi.thorDaDungKyNang = false;
                soNhanhDaKiemTra++;

                nguoi.avenger = ChickenKyNangDacBietLoki.AVG_LOKI;
                nguoi.lokiSkillActive = true;
                xacNhanBossKhongBoLuot(
                        tran, nguoiChoi, "Loki dang chay map=" + mapId);
                nguoi.lokiSkillActive = false;
                soNhanhDaKiemTra++;

                Field dangChoDan = fieldNeuCo(
                        tran, "slotDangChoKetThucBan");
                if (dangChoDan != null) {
                    dangChoDan.setInt(tran, 0);
                    xacNhanBossKhongBoLuot(
                            tran, nguoiChoi,
                            "dan dang bay map=" + mapId);
                    dangChoDan.setInt(tran, -1);
                    soNhanhDaKiemTra++;
                }

                chuanBiBoLuotBoss(tran, nguoi);
                nguoi.avenger = ChickenKyNangDacBietLoki.AVG_LOKI;
                nguoi.lokiDangChoChonMucTieu = true;
                nguoi.lokiDaGuiMenu = true;
                tran.boLuot(nguoiChoi);
                xacNhanBossBoLuotHopLe(tran, nguoi, mapId, "Loki");
                dung(!nguoi.lokiDangChoChonMucTieu
                                && !nguoi.lokiDaGuiMenu,
                        "bo luot khong huy menu Loki map=" + mapId);
                soNhanhDaKiemTra++;

                chuanBiBoLuotBoss(tran, nguoi);
                nguoi.avenger =
                        ChickenKyNangDacBietUltron.AVG_ULTRON;
                nguoi.ultronDangBanX3 = true;
                nguoi.ultronDaGuiMenu = true;
                tran.boLuot(nguoiChoi);
                xacNhanBossBoLuotHopLe(tran, nguoi, mapId, "Ultron");
                dung(!nguoi.ultronDangBanX3
                                && !nguoi.ultronDaGuiMenu,
                        "bo luot khong huy X3 Ultron map=" + mapId);
                soNhanhDaKiemTra++;

                chuanBiBoLuotBoss(tran, nguoi);
                nguoi.avenger =
                        ChickenKyNangDacBietIronMan.AVG_IRON_MAN;
                nguoi.ironManLaserSanSang = true;
                nguoi.ironManDaGuiMenu = true;
                tran.boLuot(nguoiChoi);
                xacNhanBossBoLuotHopLe(
                        tran, nguoi, mapId, "Iron Man");
                dung(!nguoi.ironManLaserSanSang
                                && !nguoi.ironManDaGuiMenu,
                        "bo luot khong huy laser Iron Man map=" + mapId);
                soNhanhDaKiemTra++;
            } finally {
                tran.dungBot();
            }
        }
        dung(soNhanhDaKiemTra >= 70,
                "ma tran bo luot boss thieu nhanh: "
                        + soNhanhDaKiemTra);
        System.out.println("SKIP_TURN_MATRIX_OK bossBranches="
                + soNhanhDaKiemTra + " packetCombinations=65536");
    }

    private static void chuanBiBoLuotBoss(
            ChickenQuanLyChien tran,
            ChickenChienBinh nguoi
    ) throws Exception {
        datLong(tran, "maPhienLuot",
                layLong(tran, "maPhienLuot") + 1_000L);
        datByte(tran, "luotHienTai", (byte) 0);
        datBoolean(tran, "daKetThuc", false);
        Arrays.fill(layMangInt(tran, "napDan"), 0);
        nguoi.chet = false;
        nguoi.hp = Math.max(1, nguoi.mauToiDa);
        nguoi.thorDaDungKyNang = false;
        nguoi.lokiSkillActive = false;
        Field dangChoDan = fieldNeuCo(tran, "slotDangChoKetThucBan");
        if (dangChoDan != null) {
            dangChoDan.setInt(tran, -1);
        }
    }

    private static void xacNhanBossKhongBoLuot(
            ChickenQuanLyChien tran,
            ChickenNguoiChoi nguoiChoi,
            String trangThai
    ) throws Exception {
        byte luotTruoc = layByte(tran, "luotHienTai");
        long phienTruoc = layLong(tran, "maPhienLuot");
        int[] napDanTruoc = Arrays.copyOf(
                layMangInt(tran, "napDan"),
                layMangInt(tran, "napDan").length);
        tran.boLuot(nguoiChoi);
        bang(luotTruoc, layByte(tran, "luotHienTai"),
                trangThai + " van doi luot boss");
        bang(phienTruoc, layLong(tran, "maPhienLuot"),
                trangThai + " van doi ma phien boss");
        dung(Arrays.equals(
                        napDanTruoc, layMangInt(tran, "napDan")),
                trangThai + " van sua nap dan boss");
    }

    private static void xacNhanBossBoLuotHopLe(
            ChickenQuanLyChien tran,
            ChickenChienBinh nguoi,
            int mapId,
            String loaiAvg
    ) throws Exception {
        bang(ChickenNapDanServer.TOI_THIEU,
                layMangInt(tran, "napDan")[nguoi.chiSo & 0xFF],
                "bo luot " + loaiAvg + " khong nap 250 map=" + mapId);
        dung(layByte(tran, "luotHienTai") != nguoi.chiSo,
                "bo luot " + loaiAvg + " khong doi luot map=" + mapId);
        /*
         * Hủy hiệu lực task boss vừa được xếp, tránh nó can thiệp trường hợp
         * AVG kế tiếp trong cùng ma trận.
         */
        datLong(tran, "maPhienLuot",
                layLong(tran, "maPhienLuot") + 1_000L);
    }

    private static ChickenQuanLyChien taoTranBoss(
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

    private static ChickenChienBinh[] chupChienBinh(
            ChickenQuanLyChien tran
    ) throws Exception {
        Method method = tran.getClass().getMethod("chupChienBinh");
        return (ChickenChienBinh[]) method.invoke(tran);
    }

    private static Field field(Object doiTuong, String ten)
            throws Exception {
        Field field = doiTuong.getClass().getDeclaredField(ten);
        field.setAccessible(true);
        return field;
    }

    private static Field fieldNeuCo(Object doiTuong, String ten)
            throws Exception {
        try {
            return field(doiTuong, ten);
        } catch (NoSuchFieldException ignored) {
            return null;
        }
    }

    private static void datBoolean(
            Object doiTuong,
            String ten,
            boolean giaTri
    ) throws Exception {
        field(doiTuong, ten).setBoolean(doiTuong, giaTri);
    }

    private static void datByte(
            Object doiTuong,
            String ten,
            byte giaTri
    ) throws Exception {
        field(doiTuong, ten).setByte(doiTuong, giaTri);
    }

    private static byte layByte(Object doiTuong, String ten)
            throws Exception {
        return field(doiTuong, ten).getByte(doiTuong);
    }

    private static void datLong(
            Object doiTuong,
            String ten,
            long giaTri
    ) throws Exception {
        field(doiTuong, ten).setLong(doiTuong, giaTri);
    }

    private static long layLong(Object doiTuong, String ten)
            throws Exception {
        return field(doiTuong, ten).getLong(doiTuong);
    }

    private static int[] layMangInt(Object doiTuong, String ten)
            throws Exception {
        return (int[]) field(doiTuong, ten).get(doiTuong);
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

    private static void chay(String ten, Viec viec) throws Exception {
        try {
            viec.chay();
            soMaTranDaChay++;
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

    private static void khacNull(Object giaTri, String thongBao) {
        dung(giaTri != null, thongBao);
    }

    private static void cung(Object mongDoi, Object thucTe,
            String thongBao) {
        if (mongDoi != thucTe) {
            throw new AssertionError(thongBao);
        }
    }

    private static void bang(long mongDoi, long thucTe, String thongBao) {
        if (mongDoi != thucTe) {
            throw new AssertionError(thongBao
                    + " expected=" + mongDoi
                    + " actual=" + thucTe);
        }
    }

    @FunctionalInterface
    private interface Viec {
        void chay() throws Exception;
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
