package com.chicken.kiemthu;

import com.chicken.avg.ChickenKyNangDacBietHawk;
import com.chicken.chien.ChickenChienBinh;
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
        System.out.println("GAME_TEST_OK matrices=" + soMaTranDaChay
                + " bossMaps=" + MAP_BOSS.length);
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
