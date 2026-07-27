package com.chicken.avg;

import com.chicken.chien.ChickenChienBinh;
import com.chicken.chiso.ChickenKichThuocNhanVat;
import java.io.IOException;

/**
 * Hinh hoc va sat thuong authoritative cua tia laser nguc Iron Man.
 *
 * Client chi gui goc ngam. Server tu tao tia, bo qua moi pixel dia hinh va
 * dung tai nhan vat song dau tien nam tren tia.
 */
public final class ChickenTiaLaserIronMan {

    public static final byte AVG_IRON_MAN = 1;
    /** Tin hieu server xac nhan client PC duoc phep hien thi ngam laser cho phat ke tiep. */
    public static final byte LENH_TRANG_THAI_NGAM = 124;
    public static final byte PHIEN_BAN_TRANG_THAI_NGAM = 1;
    /** CMD 125 cu chi doc mot byte tren client goc, nen van an toan voi client cu. */
    public static final byte LENH_HIEU_UNG_RIENG = 125;
    public static final byte PHIEN_BAN_HIEU_UNG = 1;
    public static final int THOI_GIAN_HIEU_UNG_MS = 650;
    public static final int LECH_NGUC_SO_VOI_CHAN = 18;
    private static final int BUOC_QUET_PX = 2;
    private static final int SO_MUI_TEN_HAWK = 4;

    @FunctionalInterface
    public interface KiemTraHitbox {
        boolean trung(ChickenChienBinh mucTieu, int tiaX, int tiaY);
    }

    public static final class KetQua {
        private final short batDauX;
        private final short batDauY;
        private final short ketThucX;
        private final short ketThucY;
        private final int chiSoMucTieu;

        private KetQua(short batDauX, short batDauY,
                short ketThucX, short ketThucY, int chiSoMucTieu) {
            this.batDauX = batDauX;
            this.batDauY = batDauY;
            this.ketThucX = ketThucX;
            this.ketThucY = ketThucY;
            this.chiSoMucTieu = chiSoMucTieu;
        }

        public short getBatDauX() {
            return this.batDauX;
        }

        public short getBatDauY() {
            return this.batDauY;
        }

        public short getKetThucX() {
            return this.ketThucX;
        }

        public short getKetThucY() {
            return this.ketThucY;
        }

        public int getChiSoMucTieu() {
            return this.chiSoMucTieu;
        }
    }

    private ChickenTiaLaserIronMan() {
    }

    public static short chuanHoaGoc(short gocClient) {
        int goc = gocClient % 360;
        if (goc < 0) {
            goc += 360;
        }
        return (short) goc;
    }

    public static KetQua taoTrongTran(
            ChickenChienBinh ironMan,
            ChickenChienBinh[] chienBinhs,
            short goc,
            int mapWidth,
            int mapHeight
    ) {
        return taoTrongTran(
                ironMan, chienBinhs, goc, mapWidth, mapHeight, null);
    }

    public static KetQua taoTrongTran(
            ChickenChienBinh ironMan,
            ChickenChienBinh[] chienBinhs,
            short goc,
            int mapWidth,
            int mapHeight,
            KiemTraHitbox kiemTraHitbox
    ) {
        if (ironMan == null) {
            return tao((short) 0, (short) 0, goc, mapWidth, mapHeight,
                    null, null, null);
        }
        short batDauX = ironMan.x;
        short batDauY = (short) (ironMan.y - LECH_NGUC_SO_VOI_CHAN);
        int rong = Math.max(1, mapWidth);
        int cao = Math.max(1, mapHeight);
        double rad = Math.toRadians(chuanHoaGoc(goc));
        double huongX = Math.cos(rad);
        double huongY = -Math.sin(rad);
        int gioiHanBuoc = Math.max(rong, cao) * 2 + 64;
        int xCuoi = batDauX;
        int yCuoi = batDauY;
        for (int buoc = BUOC_QUET_PX; buoc <= gioiHanBuoc;
                buoc += BUOC_QUET_PX) {
            int x = (int) Math.round(batDauX + huongX * buoc);
            int y = (int) Math.round(batDauY + huongY * buoc);
            if (x < 0 || x >= rong || y < 0 || y >= cao) {
                return new KetQua(
                        batDauX, batDauY,
                        kepShort(xCuoi), kepShort(yCuoi), -1);
            }
            xCuoi = x;
            yCuoi = y;
            if (chienBinhs == null) {
                continue;
            }
            for (int i = 0; i < chienBinhs.length; i++) {
                ChickenChienBinh mucTieu = chienBinhs[i];
                if (mucTieu == null || mucTieu == ironMan || mucTieu.chet
                        || mucTieu.hp <= 0) {
                    continue;
                }
                boolean trung = kiemTraHitbox != null
                        ? kiemTraHitbox.trung(mucTieu, x, y)
                        : mucTieu.bot
                                ? ChickenKichThuocNhanVat.trungBoss(
                                        x, y, mucTieu.x, mucTieu.y)
                                : ChickenKichThuocNhanVat.trungNguoiChoi(
                                        x, y, mucTieu.x, mucTieu.y);
                if (trung) {
                    return new KetQua(
                            batDauX, batDauY,
                            kepShort(x), kepShort(y), i);
                }
            }
        }
        return new KetQua(
                batDauX, batDauY,
                kepShort(xCuoi), kepShort(yCuoi), -1);
    }

    public static KetQua tao(
            short batDauX,
            short batDauY,
            short goc,
            int mapWidth,
            int mapHeight,
            short[] cacMucTieuX,
            short[] cacMucTieuY,
            boolean[] mucTieuHopLe
    ) {
        int rong = Math.max(1, mapWidth);
        int cao = Math.max(1, mapHeight);
        double rad = Math.toRadians(chuanHoaGoc(goc));
        double huongX = Math.cos(rad);
        double huongY = -Math.sin(rad);
        int gioiHanBuoc = Math.max(rong, cao) * 2 + 64;
        int xCuoi = batDauX;
        int yCuoi = batDauY;

        int soMucTieu = Math.min(
                cacMucTieuX == null ? 0 : cacMucTieuX.length,
                cacMucTieuY == null ? 0 : cacMucTieuY.length
        );
        if (mucTieuHopLe != null) {
            soMucTieu = Math.min(soMucTieu, mucTieuHopLe.length);
        } else {
            soMucTieu = 0;
        }

        for (int buoc = BUOC_QUET_PX; buoc <= gioiHanBuoc;
                buoc += BUOC_QUET_PX) {
            int x = (int) Math.round(batDauX + huongX * buoc);
            int y = (int) Math.round(batDauY + huongY * buoc);
            if (x < 0 || x >= rong || y < 0 || y >= cao) {
                return new KetQua(
                        batDauX,
                        batDauY,
                        kepShort(xCuoi),
                        kepShort(yCuoi),
                        -1
                );
            }
            xCuoi = x;
            yCuoi = y;
            for (int i = 0; i < soMucTieu; i++) {
                if (mucTieuHopLe[i]
                        && ChickenKichThuocNhanVat.trungNguoiChoi(
                                x, y, cacMucTieuX[i], cacMucTieuY[i])) {
                    return new KetQua(
                            batDauX,
                            batDauY,
                            kepShort(x),
                            kepShort(y),
                            i
                    );
                }
            }
        }
        return new KetQua(
                batDauX,
                batDauY,
                kepShort(xCuoi),
                kepShort(yCuoi),
                -1
        );
    }

    /** Dung dung cong thuc tong damage bon mui ten skill Hawk. */
    public static int tinhSatThuongNhuHawk(int tanCong, int giap) {
        long moiMui = Math.max(1L, (long) tanCong - Math.max(0, giap));
        long tong = moiMui * SO_MUI_TEN_HAWK;
        return tong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) tong;
    }

    public static void phatHienThiTrongTran(
            ChickenChienBinh shooter,
            ChickenChienBinh[] chienBinhs,
            short goc,
            KetQua ketQua
    ) throws IOException {
        if (shooter == null || chienBinhs == null || ketQua == null) {
            return;
        }
        for (ChickenChienBinh nguoiNhan : chienBinhs) {
            if (nguoiNhan != null && nguoiNhan.coPhien()) {
                nguoiNhan.nguoiChoi.dichVu.guiTiaLaserIronManDau(
                        shooter.chiSo,
                        shooter.x,
                        shooter.y,
                        goc,
                        ketQua.getBatDauX(),
                        ketQua.getBatDauY(),
                        ketQua.getKetThucX(),
                        ketQua.getKetThucY()
                );
            }
        }
    }

    private static short kepShort(int giaTri) {
        return (short) Math.max(
                Short.MIN_VALUE,
                Math.min(Short.MAX_VALUE, giaTri)
        );
    }
}
