package com.chicken.chien;

import java.util.Arrays;

/**
 * Tao toan bo quy dao cua mot lan ban tu du lieu sung tren server.
 *
 * Client chi gui goc/luc theo protocol cu. Loai dan, so vien, do lech goc va
 * cac quy dao phu cua Ga/Riu deu duoc suy ra tu sung dang trang bi.
 */
public final class ChickenLoatDanServer {
    private static final int SO_DUONG_TOI_DA = 16;

    private ChickenLoatDanServer() {
    }

    public static KetQua tao(
            short dauNongX,
            short dauNongY,
            short nguoiBanX,
            short nguoiBanY,
            short goc,
            byte luc,
            byte lucPhu,
            ChickenQuanLyDanSung.DuLieuSung sung,
            byte windX,
            byte windY,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        int idSung = sung == null ? -1 : sung.getIdSung();
        byte loaiDan = sung == null ? (byte) 0 : sung.getLoaiDan();
        byte nhomSung = sung == null ? (byte) 0 : sung.getNhomSung();
        int soVien = sung == null
                ? 1
                : Math.max(1, Math.min(
                        SO_DUONG_TOI_DA,
                        sung.getSoVienMoiLoat() & 0xFF
                ));
        boolean laDanGa = nhomSung == 6 && loaiDan == 19;
        boolean laDanRiu = nhomSung == 8 && loaiDan == 17;
        int soDuong = laDanRiu ? 4 : (laDanGa ? 2 : soVien);
        short[][] cacX = new short[soDuong][];
        short[][] cacY = new short[soDuong][];

        ChickenQuanLyCongThucSung.KetQuaQuyDao danChinh =
                ChickenQuanLyCongThucSung.taoQuyDaoTheoIdSung(
                        dauNongX,
                        dauNongY,
                        chuanHoaGoc(goc),
                        luc,
                        idSung,
                        windX,
                        windY,
                        banDo
                );
        cacX[0] = danChinh.getHienThiX();
        cacY[0] = danChinh.getHienThiY();

        int lucPhuHopLe = Math.max(1, Math.min(30, lucPhu & 0xFF));
        if (laDanGa) {
            int soDiemChinh = soDiem(cacX[0], cacY[0]);
            int buocTha = Math.max(1, Math.min(
                    lucPhuHopLe,
                    Math.max(1, soDiemChinh)
            ));
            lucPhuHopLe = buocTha;
            int chiSoTha = buocTha - 1;
            short thaX = soDiemChinh > 0 ? cacX[0][chiSoTha] : dauNongX;
            short thaY = soDiemChinh > 0 ? cacY[0][chiSoTha] : dauNongY;
            short batDauRoiY = kepShort(thaY + 8);
            ChickenQuanLyCongThucSung.KetQuaQuyDao danRoi =
                    ChickenQuanLyCongThucSung.taoQuyDaoDanGaRoi(
                            thaX,
                            batDauRoiY,
                            windX,
                            windY,
                            banDo
                    );
            cacX[1] = danRoi.getHienThiX();
            cacY[1] = danRoi.getHienThiY();
            datDiemDau(cacX[0], cacY[0], dauNongX, dauNongY);
        } else if (laDanRiu) {
            int soDiemChinh = soDiem(cacX[0], cacY[0]);
            int buocTach = Math.max(1, Math.min(
                    lucPhuHopLe,
                    Math.max(1, soDiemChinh)
            ));
            lucPhuHopLe = buocTach;
            if (soDiemChinh > 0) {
                cacX[0] = Arrays.copyOf(cacX[0], buocTach);
                cacY[0] = Arrays.copyOf(cacY[0], buocTach);
            } else {
                cacX[0] = new short[]{dauNongX};
                cacY[0] = new short[]{dauNongY};
            }
            short diemTachX = cacX[0][cacX[0].length - 1];
            short diemTachY = cacY[0][cacY[0].length - 1];
            for (int i = 0; i < 3; i++) {
                ChickenQuanLyCongThucSung.KetQuaQuyDao danCon =
                        ChickenQuanLyCongThucSung.taoQuyDaoConRiu(
                                diemTachX,
                                diemTachY,
                                nguoiBanX,
                                nguoiBanY,
                                goc,
                                luc,
                                i,
                                windX,
                                windY,
                                banDo
                        );
                cacX[i + 1] = danCon.getHienThiX();
                cacY[i + 1] = danCon.getHienThiY();
            }
            datDiemDau(cacX[0], cacY[0], dauNongX, dauNongY);
        } else {
            for (int i = 1; i < soDuong; i++) {
                short gocVien = chuanHoaGoc((short) (
                        goc + layDoLechGoc(nhomSung, soVien, i)
                ));
                ChickenQuanLyCongThucSung.KetQuaQuyDao quyDao =
                        ChickenQuanLyCongThucSung.taoQuyDaoTheoIdSung(
                                dauNongX,
                                dauNongY,
                                gocVien,
                                luc,
                                idSung,
                                windX,
                                windY,
                                banDo
                        );
                cacX[i] = quyDao.getHienThiX();
                cacY[i] = quyDao.getHienThiY();
            }
            if (soDuong > 1) {
                short gocVienDau = chuanHoaGoc((short) (
                        goc + layDoLechGoc(nhomSung, soVien, 0)
                ));
                ChickenQuanLyCongThucSung.KetQuaQuyDao quyDaoDau =
                        ChickenQuanLyCongThucSung.taoQuyDaoTheoIdSung(
                                dauNongX,
                                dauNongY,
                                gocVienDau,
                                luc,
                                idSung,
                                windX,
                                windY,
                                banDo
                        );
                cacX[0] = quyDaoDau.getHienThiX();
                cacY[0] = quyDaoDau.getHienThiY();
            }
            for (int i = 0; i < soDuong; i++) {
                datDiemDau(cacX[i], cacY[i], dauNongX, dauNongY);
            }
        }

        return new KetQua(
                loaiDan,
                (byte) lucPhuHopLe,
                cacX,
                cacY
        );
    }

    /**
     * Cung quy tac dang dung trong luyen tap: shotgun va chuoi tao chum, cac
     * nhom AK/MG/coi lap lai dung goc ngam.
     */
    public static int layDoLechGoc(
            byte nhomSung,
            int soVienMoiLoat,
            int chiSoVien
    ) {
        if (nhomSung == 2 && soVienMoiLoat == 3) {
            int[] doLech = {-5, 0, 5};
            return doLech[kep(chiSoVien, 0, doLech.length - 1)];
        }
        if (nhomSung == 3 && soVienMoiLoat == 4) {
            int[] doLech = {-6, -2, 2, 6};
            return doLech[kep(chiSoVien, 0, doLech.length - 1)];
        }
        return 0;
    }

    private static int soDiem(short[] xs, short[] ys) {
        return Math.min(xs == null ? 0 : xs.length, ys == null ? 0 : ys.length);
    }

    private static void datDiemDau(
            short[] xs,
            short[] ys,
            short dauNongX,
            short dauNongY
    ) {
        if (xs != null && ys != null && xs.length > 0 && ys.length > 0) {
            xs[0] = dauNongX;
            ys[0] = dauNongY;
        }
    }

    private static short chuanHoaGoc(short goc) {
        int ketQua = goc % 360;
        return (short) (ketQua < 0 ? ketQua + 360 : ketQua);
    }

    private static short kepShort(int giaTri) {
        return (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, giaTri));
    }

    private static int kep(int giaTri, int nhoNhat, int lonNhat) {
        return Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }

    public static final class KetQua {
        private final byte loaiDan;
        private final byte lucPhu;
        private final short[][] cacDuongX;
        private final short[][] cacDuongY;

        private KetQua(
                byte loaiDan,
                byte lucPhu,
                short[][] cacDuongX,
                short[][] cacDuongY
        ) {
            this.loaiDan = loaiDan;
            this.lucPhu = lucPhu;
            this.cacDuongX = cacDuongX;
            this.cacDuongY = cacDuongY;
        }

        public byte getLoaiDan() {
            return this.loaiDan;
        }

        public byte getLucPhu() {
            return this.lucPhu;
        }

        public short[][] getCacDuongX() {
            return this.cacDuongX;
        }

        public short[][] getCacDuongY() {
            return this.cacDuongY;
        }
    }
}
