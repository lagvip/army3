package com.chicken.phong.boss.trandau.rong;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.chiso.ChickenKichThuocNhanVat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Tao loat hai vien dan dac biet cua BigBoss Rong.
 *
 * <p>Client native dung bullet type 1 cho loat hai vien: mot CMD22, hai
 * duong dan va soPhat=1. Bullet type 2 la loat ba vien; gui mot hoac hai
 * duong voi type 2 lam BM cua client cho du lieu con thieu va ket luot.
 * Dia hinh khong cat duong bay; server van tu xac nhan hitbox va damage.</p>
 */
public final class BossRongTanCong {
    private BossRongTanCong() {
    }

    /**
     * Gop damage cua ca loat de client chi nhan mot lan cap nhat HP.
     *
     * <p>Giap van duoc tinh rieng cho tung vien khi tao ket qua ban; ham
     * nay chi cong cac damage da duoc server xac nhan.</p>
     */
    public static int tinhTongSatThuongLoat(int satThuongMoiVien) {
        if (satThuongMoiVien <= 0
                || CauHinhBossRong.SO_VIEN_DAN_DAC_BIET <= 0) {
            return 0;
        }
        long tong = (long) satThuongMoiVien
                * CauHinhBossRong.SO_VIEN_DAN_DAC_BIET;
        return (int) Math.min(Integer.MAX_VALUE, tong);
    }

    public static boolean laCheDoAim(int giaTriNgauNhienPhanTram) {
        return giaTriNgauNhienPhanTram >= 0
                && giaTriNgauNhienPhanTram
                        < CauHinhBossRong.TY_LE_AIM_PHAN_TRAM;
    }

    /** Chỉ tung một lần cho cả loạt, không tung riêng từng viên. */
    public static boolean chonAimChoCaLoat() {
        return laCheDoAim(ThreadLocalRandom.current().nextInt(100));
    }

    public static ChickenKetQuaDan taoPhatBanLua(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY
    ) {
        return taoPhatBanDacBiet(boss, mucTieu, banDo, windX, windY);
    }

    public static ChickenKetQuaDan taoPhatBanLua(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenChienBinh[] cacNguoiChoi,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY,
            boolean aim
    ) {
        return taoPhatBanDacBiet(
                boss, mucTieu, cacNguoiChoi, banDo, windX, windY, aim);
    }

    public static ChickenKetQuaDan taoPhatBanDacBiet(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY
    ) {
        return taoPhatBanDacBiet(
                boss,
                mucTieu,
                new ChickenChienBinh[]{mucTieu},
                banDo,
                windX,
                windY,
                true
        );
    }

    public static ChickenKetQuaDan taoPhatBanDacBiet(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenChienBinh[] cacNguoiChoi,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY,
            boolean aim
    ) {
        if (boss == null || mucTieu == null) {
            throw new IllegalArgumentException("boss va mucTieu khong duoc null");
        }

        int huong = mucTieu.x < boss.x ? -1 : 1;
        short dauX = (short) (boss.x
                + huong * CauHinhBossRong.LECH_X_NO_DAN);
        short dauY = (short) (boss.y
                - CauHinhBossRong.LECH_Y_NO_DAN);
        short[] dich = aim
                ? new short[]{
                    mucTieu.x,
                    (short) ChickenKichThuocNhanVat
                            .layTamThanNguoiChoiY(mucTieu.y)
                }
                : chonDiemNgauNhienNgoaiBanDo(dauX, dauY, banDo);
        short dichX = dich[0];
        short dichY = dich[1];

        short[][] duong = taoDuongDanXuyenDiaHinh(
                dauX, dauY, dichX, dichY, windX, windY);
        VaCham vaCham = timVaChamNhanVat(
                duong[0], duong[1], boss, cacNguoiChoi);
        if (vaCham != null) {
            duong = catTaiVaCham(duong[0], duong[1], vaCham);
        }

        int satThuong = vaCham == null
                ? 0
                : Math.max(1, boss.tanCong - vaCham.mucTieu.giap);
        short goc = gocToiMucTieu(dauX, dauY, dichX, dichY);
        Map<ChickenChienBinh, Integer> satThuongTheoMucTieu =
                new LinkedHashMap<>();
        if (vaCham != null && satThuong > 0) {
            satThuongTheoMucTieu.put(vaCham.mucTieu, satThuong);
        }
        return new ChickenKetQuaDan(
                CauHinhBossRong.LOAI_DAN_DAC_BIET,
                dauX,
                dauY,
                goc,
                CauHinhBossRong.LUC_HIEN_THI_DAN,
                CauHinhBossRong.LUC_HIEN_THI_DAN,
                new short[][]{
                    duong[0],
                    duong[0].clone()
                },
                new short[][]{
                    duong[1],
                    duong[1].clone()
                },
                satThuongTheoMucTieu
        );
    }

    /**
     * Chon mot huong ngau nhien, sau do keo diem cuoi ra ngoai bien map.
     *
     * <p>Client cho BigBoss Rong luon tao hieu ung no type 60 khi doc het
     * duong dan. Neu diem cuoi nam trong map va trung vao dat, hinh anh se
     * trong nhu dan bi dia hinh chan du server khong he cat quỹ dao. Ket
     * thuc ngoai map giu dung y nghia "xuyen dia hinh" cho cac phat truot.</p>
     */
    private static short[] chonDiemNgauNhienNgoaiBanDo(
            short dauX,
            short dauY,
            ChickenQuanLyBanDo banDo
    ) {
        int rong = banDo == null ? 1_000 : Math.max(1, banDo.getWidth());
        int cao = banDo == null ? 800 : Math.max(1, banDo.getHeight());
        int minX = Math.min(
                CauHinhBossRong.LE_DIEM_NGAU_NHIEN_X,
                Math.max(0, rong - 1));
        int maxX = Math.max(
                minX + 1,
                rong - CauHinhBossRong.LE_DIEM_NGAU_NHIEN_X);
        int minY = Math.min(
                CauHinhBossRong.LE_DIEM_NGAU_NHIEN_Y,
                Math.max(0, cao - 1));
        int maxY = Math.max(
                minY + 1,
                cao - CauHinhBossRong.LE_DIEM_NGAU_NHIEN_Y);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int diemHuongX = dauX;
        int diemHuongY = dauY;
        for (int lan = 0; lan < 8; lan++) {
            diemHuongX = random.nextInt(minX, maxX);
            diemHuongY = random.nextInt(minY, maxY);
            if (Math.hypot(diemHuongX - dauX, diemHuongY - dauY)
                    >= CauHinhBossRong.KHOANG_CACH_DIEM_NGAU_NHIEN_TOI_THIEU) {
                break;
            }
        }

        double dx = diemHuongX - dauX;
        double dy = diemHuongY - dauY;
        if (Math.abs(dx) < 0.0001D && Math.abs(dy) < 0.0001D) {
            dx = 1.0D;
        }

        double heSoChamBien = Double.POSITIVE_INFINITY;
        if (dx > 0.0D) {
            heSoChamBien = Math.min(
                    heSoChamBien, (rong - 1.0D - dauX) / dx);
        } else if (dx < 0.0D) {
            heSoChamBien = Math.min(
                    heSoChamBien, (0.0D - dauX) / dx);
        }
        if (dy > 0.0D) {
            heSoChamBien = Math.min(
                    heSoChamBien, (cao - 1.0D - dauY) / dy);
        } else if (dy < 0.0D) {
            heSoChamBien = Math.min(
                    heSoChamBien, (0.0D - dauY) / dy);
        }
        if (!Double.isFinite(heSoChamBien) || heSoChamBien < 0.0D) {
            heSoChamBien = 1.0D;
        }

        double doDaiHuong = Math.max(1.0D, Math.hypot(dx, dy));
        double heSoNgoaiBien = heSoChamBien
                + CauHinhBossRong.LE_KET_THUC_NGOAI_BAN_DO / doDaiHuong;
        int dichX = gioiHanShort(
                (int) Math.round(dauX + dx * heSoNgoaiBien));
        int dichY = gioiHanShort(
                (int) Math.round(dauY + dy * heSoNgoaiBien));
        return new short[]{(short) dichX, (short) dichY};
    }

    private static int gioiHanShort(int giaTri) {
        return Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, giaTri));
    }

    /**
     * Khong doc collision map trong ham nay: dan Rong xuyen moi do day
     * dia hinh va chi dung khi cham hitbox muc tieu.
     */
    private static short[][] taoDuongDanXuyenDiaHinh(
            short dauX,
            short dauY,
            short dichX,
            short dichY,
            byte windX,
            byte windY
    ) {
        int khoangCach = Math.max(
                1,
                (int) Math.round(Math.hypot(dichX - dauX, dichY - dauY))
        );
        int soDiem = Math.max(
                CauHinhBossRong.SO_DIEM_DUONG_DAN_TOI_THIEU,
                Math.min(
                        CauHinhBossRong.SO_DIEM_DUONG_DAN_TOI_DA,
                        khoangCach
                                / CauHinhBossRong.KHOANG_CACH_MOI_DIEM_DAN
                                + 2)
        );
        short[] xs = new short[soDiem];
        short[] ys = new short[soDiem];

        for (int i = 0; i < soDiem; i++) {
            double t = (double) i / (double) (soDiem - 1);
            double cong = Math.sin(Math.PI * t);
            xs[i] = (short) Math.round(
                    dauX
                            + (dichX - dauX) * t
                            + windX * cong
                                    * CauHinhBossRong.HE_SO_GIO_DUONG_DAN
            );
            ys[i] = (short) Math.round(
                    dauY
                            + (dichY - dauY) * t
                            + windY * cong
                                    * CauHinhBossRong.HE_SO_GIO_DUONG_DAN
                            - cong * CauHinhBossRong.DO_CONG_DUONG_DAN
            );
        }
        return new short[][]{xs, ys};
    }

    private static VaCham timVaChamNhanVat(
            short[] xs,
            short[] ys,
            ChickenChienBinh nguoiBan,
            ChickenChienBinh[] cacMucTieu
    ) {
        boolean daRoiHitboxNguoiBan = false;
        int soDiem = Math.min(xs.length, ys.length);
        for (int i = 1; i < soDiem; i++) {
            int x1 = xs[i - 1];
            int y1 = ys[i - 1];
            int x2 = xs[i];
            int y2 = ys[i];
            int soBuoc = Math.max(
                    1,
                    Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1))
            );
            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                double t = (double) buoc / (double) soBuoc;
                int x = (int) Math.round(x1 + (x2 - x1) * t);
                int y = (int) Math.round(y1 + (y2 - y1) * t);
                if (!daRoiHitboxNguoiBan) {
                    daRoiHitboxNguoiBan = !trungHitbox(
                            nguoiBan, nguoiBan, x, y);
                    if (!daRoiHitboxNguoiBan) {
                        continue;
                    }
                }
                if (cacMucTieu == null) {
                    continue;
                }
                for (ChickenChienBinh mucTieu : cacMucTieu) {
                    if (!laMucTieuHopLe(mucTieu)) {
                        continue;
                    }
                    if (trungHitbox(nguoiBan, mucTieu, x, y)) {
                        return new VaCham(
                                i, (short) x, (short) y, mucTieu);
                    }
                }
            }
        }
        return null;
    }

    private static boolean laMucTieuHopLe(ChickenChienBinh mucTieu) {
        return mucTieu != null
                && !mucTieu.chet
                && mucTieu.hp > 0;
    }

    private static boolean trungHitbox(
            ChickenChienBinh nguoiBan,
            ChickenChienBinh mucTieu,
            int x,
            int y
    ) {
        if (mucTieu == null) {
            return false;
        }
        if (!mucTieu.bot) {
            return ChickenKichThuocNhanVat.trungNguoiChoi(
                    x, y, mucTieu.x, mucTieu.y);
        }
        if (mucTieu == nguoiBan) {
            return DiChuyenBossRong.trungBossRong(
                    x, y, mucTieu.x, mucTieu.y);
        }
        return ChickenKichThuocNhanVat.trungBoss(
                x, y, mucTieu.x, mucTieu.y);
    }

    private static short[][] catTaiVaCham(
            short[] xs,
            short[] ys,
            VaCham vaCham
    ) {
        int doDai = Math.max(
                2,
                Math.min(xs.length, vaCham.chiSoDoan + 1)
        );
        short[] ketQuaX = Arrays.copyOf(xs, doDai);
        short[] ketQuaY = Arrays.copyOf(ys, doDai);
        ketQuaX[doDai - 1] = vaCham.x;
        ketQuaY[doDai - 1] = vaCham.y;
        return new short[][]{ketQuaX, ketQuaY};
    }

    private static short gocToiMucTieu(int x1, int y1, int x2, int y2) {
        int goc = (int) Math.round(
                Math.toDegrees(Math.atan2(-(y2 - y1), x2 - x1))
        );
        while (goc < 0) {
            goc += 360;
        }
        return (short) (goc % 360);
    }

    private static final class VaCham {
        private final int chiSoDoan;
        private final short x;
        private final short y;
        private final ChickenChienBinh mucTieu;

        private VaCham(
                int chiSoDoan,
                short x,
                short y,
                ChickenChienBinh mucTieu
        ) {
            this.chiSoDoan = chiSoDoan;
            this.x = x;
            this.y = y;
            this.mucTieu = mucTieu;
        }
    }
}
