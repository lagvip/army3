package com.chicken.kiemthu;

import com.chicken.avg.ChickenKyNangDacBietHawk;
import com.chicken.avg.ChickenKyNangDacBietIronMan;
import com.chicken.avg.ChickenKyNangDacBietLoki;
import com.chicken.avg.ChickenKyNangDacBietThor;
import com.chicken.avg.ChickenKyNangDacBietUltron;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenNapDanServer;
import com.chicken.chien.ChickenQuanLyChien;
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
import java.io.ByteArrayOutputStream;
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

        chay("7 boss: moi luot chi nhan mot phat ban",
                ChickenKiemThuToanGame::kiemTraMotPhatMoiLuotTatCaBoss);
        chay("7 boss: ma tran bo luot hop le va bi chan",
                ChickenKiemThuToanGame::kiemTraBoLuotTatCaBoss);
        chay("7 boss: chi mo GameScr sau ACK tai anh dan",
                ChickenKiemThuToanGame::kiemTraKhongGuiGameScrSom);
        System.out.println("GAME_TEST_OK matrices=" + soMaTranDaChay
                + " bossMaps=" + MAP_BOSS.length);
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
    }
}
