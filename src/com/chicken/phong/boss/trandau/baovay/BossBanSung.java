package com.chicken.phong.boss.trandau.baovay;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.chien.ChickenLoatDanServer;
import com.chicken.chien.ChickenPhatBanServer;
import com.chicken.chien.ChickenQuanLyCongThucSung;
import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.chien.ChickenToaDoDauNong;
import com.chicken.chiso.ChickenKichThuocNhanVat;
import com.chicken.phong.boss.trandau.ChickenLuatVaChamPhongBoss;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/** Logic riêng của hai boss Phiến quân đứng im và bắn súng. */
public final class BossBanSung {
    private static final byte LUC_HIEN_THI = 18;
    private static final int SO_DIEM_TOI_DA = 128;
    public static final int TY_LE_AIM_CHUAN_PHAN_TRAM = 25;

    private BossBanSung() {
    }

    public static ChickenChienBinh chonNgauNhienNguoiSong(ChickenChienBinh[] chienBinhs) {
        int dem = 0;
        for (int i = 0; i < 8 && i < chienBinhs.length; i++) {
            ChickenChienBinh chienBinh = chienBinhs[i];
            if (chienBinh != null && !chienBinh.chet && chienBinh.hp > 0 && chienBinh.coPhien()) {
                dem++;
            }
        }
        if (dem == 0) {
            return null;
        }
        int viTri = ThreadLocalRandom.current().nextInt(dem);
        for (int i = 0; i < 8 && i < chienBinhs.length; i++) {
            ChickenChienBinh chienBinh = chienBinhs[i];
            if (chienBinh != null && !chienBinh.chet && chienBinh.hp > 0 && chienBinh.coPhien()) {
                if (viTri-- == 0) {
                    return chienBinh;
                }
            }
        }
        return null;
    }

    public static ChickenKetQuaDan taoPhatBan(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY
    ) {
        return taoPhatBan(
                boss,
                mucTieu,
                new ChickenChienBinh[]{mucTieu},
                banDo,
                windX,
                windY
        );
    }

    /**
     * Tao mot lan ban cua AI bang dung cong thuc sung thuong.
     *
     * Khac voi duong cong cu cua boss bao vay, ham nay giu nguyen ID sung,
     * gio, trong luc, loai dan va toan bo cac duong dan trong mot loat. Vi du
     * sung chuoi la bon duong lech goc trong mot packet, MG la nam duong cung
     * goc; khong loai nao bi tach thanh N lan ban.
     */
    public static ChickenKetQuaDan taoPhatBanTheoCongThucSung(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenChienBinh[] cacMucTieu,
            ChickenQuanLyDanSung.DuLieuSung duLieu,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY
    ) {
        return taoPhatBanTheoCongThucSung(
                boss,
                mucTieu,
                cacMucTieu,
                duLieu,
                banDo,
                windX,
                windY,
                true
        );
    }

    public static ChickenKetQuaDan taoPhatBanTheoCongThucSung(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenChienBinh[] cacMucTieu,
            ChickenQuanLyDanSung.DuLieuSung duLieu,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY,
            boolean aimChuan
    ) {
        ChickenQuanLyCongThucSung.KiemTraBanDo kiemTra =
                taoKiemTraBanDo(banDo);
        NgamBan ngam = aimChuan
                ? timNgamTheoCongThuc(
                        boss, mucTieu, duLieu, kiemTra, windX, windY)
                : taoNgamNgauNhien();
        short[] dauNong = ChickenToaDoDauNong.layChoBoss(
                boss.x, boss.y, ngam.goc, kiemTra);
        return ChickenPhatBanServer.tao(
                boss,
                dauNong[0],
                dauNong[1],
                ngam.goc,
                ngam.luc,
                ngam.lucPhu,
                duLieu,
                windX,
                windY,
                kiemTra,
                cacMucTieu,
                new ChickenPhatBanServer.BoLocMucTieu() {
                    @Override
                    public boolean chapNhan(
                            ChickenChienBinh nguoiBan,
                            ChickenChienBinh mucTieuVaCham
                    ) {
                        return ChickenLuatVaChamPhongBoss.chapNhan(
                                nguoiBan, mucTieuVaCham);
                    }
                }
        );
    }

    public static boolean laCheDoAimChuan(int giaTriNgauNhien) {
        return giaTriNgauNhien >= 0
                && giaTriNgauNhien < TY_LE_AIM_CHUAN_PHAN_TRAM;
    }

    public static boolean chonCheDoAimChuan() {
        return laCheDoAimChuan(
                ThreadLocalRandom.current().nextInt(100));
    }

    public static ChickenKetQuaDan taoPhatBan(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenChienBinh[] cacMucTieu,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY
    ) {
        ChickenQuanLyDanSung.DuLieuSung duLieu =
                ChickenQuanLyDanSung.theoPartSung(boss.maVuKhi);
        return taoPhatBan(
                boss, mucTieu, cacMucTieu, duLieu,
                banDo, windX, windY);
    }

    /**
     * Bản dùng ID súng đã được server chốt. Cần thiết khi nhiều template có
     * thể dùng chung part; không được suy ngược ID từ sprite client.
     */
    public static ChickenKetQuaDan taoPhatBan(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenChienBinh[] cacMucTieu,
            ChickenQuanLyDanSung.DuLieuSung duLieu,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY
    ) {
        byte loaiDan = duLieu == null ? (byte) 1 : duLieu.getLoaiDan();
        int mucTieuY = ChickenKichThuocNhanVat.layTamThanNguoiChoiY(mucTieu.y);
        short goc = gocToiMucTieu(boss.x, boss.y - 18, mucTieu.x, mucTieuY);
        ChickenQuanLyCongThucSung.KiemTraBanDo kiemTra = new ChickenQuanLyCongThucSung.KiemTraBanDo() {
            @Override
            public int getWidth() { return banDo.getWidth(); }
            @Override
            public int getHeight() { return banDo.getHeight(); }
            @Override
            public boolean coVaCham(short x, short y) { return banDo.coVaCham(x, y); }
        };
        short[] dauNong = ChickenToaDoDauNong.layChoBoss(boss.x, boss.y, goc, kiemTra);
        goc = gocToiMucTieu(dauNong[0], dauNong[1], mucTieu.x, mucTieuY);
        dauNong = ChickenToaDoDauNong.layChoBoss(boss.x, boss.y, goc, kiemTra);

        short[][] duongDan = taoDuongDanCong(
                dauNong[0], dauNong[1], mucTieu.x, (short) mucTieuY,
                banDo, windX, windY);
        VaChamNhanVat vaCham = timVaChamNhanVat(
                duongDan[0], duongDan[1], boss, cacMucTieu);
        if (vaCham != null) {
            duongDan = catTaiVaCham(duongDan[0], duongDan[1], vaCham);
        }
        int satThuong = vaCham == null
                ? 0
                : Math.max(1, boss.tanCong - vaCham.mucTieu.giap);
        return new ChickenKetQuaDan(
                loaiDan,
                dauNong[0],
                dauNong[1],
                goc,
                LUC_HIEN_THI,
                duongDan[0],
                duongDan[1],
                vaCham == null ? null : vaCham.mucTieu,
                satThuong
        );
    }

    public static int laySoVien(ChickenChienBinh boss) {
        ChickenQuanLyDanSung.DuLieuSung duLieu =
                ChickenQuanLyDanSung.theoPartSung(boss.maVuKhi);
        return laySoVien(duLieu);
    }

    public static int laySoVien(
            ChickenQuanLyDanSung.DuLieuSung duLieu) {
        return duLieu == null ? 1 : Math.max(1, duLieu.getSoVienMoiLoat() & 0xFF);
    }

    public static int layKhoangCachVienMs(ChickenChienBinh boss) {
        ChickenQuanLyDanSung.DuLieuSung duLieu =
                ChickenQuanLyDanSung.theoPartSung(boss.maVuKhi);
        return layKhoangCachVienMs(duLieu);
    }

    public static int layKhoangCachVienMs(
            ChickenQuanLyDanSung.DuLieuSung duLieu) {
        return duLieu == null ? 80 : Math.max(10, duLieu.getKhoangCachVienMs());
    }

    private static NgamBan timNgamTheoCongThuc(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenQuanLyDanSung.DuLieuSung duLieu,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo,
            byte windX,
            byte windY
    ) {
        int tamMucTieuY = mucTieu.bot
                ? mucTieu.y - (ChickenKichThuocNhanVat.BOSS_LECH_TREN
                        + ChickenKichThuocNhanVat.BOSS_LECH_DUOI) / 2
                : ChickenKichThuocNhanVat.layTamThanNguoiChoiY(mucTieu.y);
        short gocTrucTiep = gocToiMucTieu(
                boss.x,
                boss.y - ChickenToaDoDauNong.BOSS_TRUC_SUNG_CACH_CHAN,
                mucTieu.x,
                tamMucTieuY
        );
        NgamBan totNhat = new NgamBan(gocTrucTiep, (byte) 18, (byte) 18);
        double diemTotNhat = chamDiemNgam(
                boss, mucTieu, duLieu, banDo, windX, windY, totNhat);

        // Quet tho tren toan bo vong tron de xu ly ca muc tieu o tren/cao hon
        // va cac cong thuc boomerang/lazer co huong bay khac dan thuong.
        for (int goc = 0; goc < 360; goc += 6) {
            for (int luc = 3; luc <= 30; luc += 3) {
                NgamBan ungVien = new NgamBan(
                        (short) goc, (byte) luc, (byte) luc);
                double diem = chamDiemNgam(
                        boss, mucTieu, duLieu, banDo, windX, windY, ungVien);
                if (diem < diemTotNhat) {
                    diemTotNhat = diem;
                    totNhat = ungVien;
                }
            }
        }

        short gocTho = totNhat.goc;
        int lucTho = totNhat.luc & 0xFF;
        for (int lechGoc = -6; lechGoc <= 6; lechGoc++) {
            short goc = chuanHoaGoc((short) (gocTho + lechGoc));
            for (int luc = Math.max(1, lucTho - 3);
                    luc <= Math.min(30, lucTho + 3); luc++) {
                NgamBan ungVien = new NgamBan(
                        goc, (byte) luc, (byte) luc);
                double diem = chamDiemNgam(
                        boss, mucTieu, duLieu, banDo, windX, windY, ungVien);
                if (diem < diemTotNhat) {
                    diemTotNhat = diem;
                    totNhat = ungVien;
                }
            }
        }
        return totNhat;
    }

    private static NgamBan taoNgamNgauNhien() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new NgamBan(
                (short) random.nextInt(360),
                (byte) random.nextInt(1, 31),
                (byte) random.nextInt(1, 31)
        );
    }

    private static double chamDiemNgam(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenQuanLyDanSung.DuLieuSung duLieu,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo,
            byte windX,
            byte windY,
            NgamBan ngam
    ) {
        short[] dauNong = ChickenToaDoDauNong.layChoBoss(
                boss.x, boss.y, ngam.goc, banDo);
        ChickenLoatDanServer.KetQua loat = ChickenLoatDanServer.tao(
                dauNong[0],
                dauNong[1],
                boss.x,
                boss.y,
                ngam.goc,
                ngam.luc,
                ngam.lucPhu,
                duLieu,
                windX,
                windY,
                banDo
        );
        double ganNhat = Double.MAX_VALUE;
        int soDuong = Math.min(
                loat.getCacDuongX().length,
                loat.getCacDuongY().length
        );
        for (int duong = 0; duong < soDuong; duong++) {
            short[] xs = loat.getCacDuongX()[duong];
            short[] ys = loat.getCacDuongY()[duong];
            int soDiem = Math.min(
                    xs == null ? 0 : xs.length,
                    ys == null ? 0 : ys.length
            );
            for (int diem = 1; diem < soDiem; diem++) {
                double khoangCach = mucTieu.bot
                        ? ChickenKichThuocNhanVat.khoangCachDenBoss(
                                xs[diem], ys[diem], mucTieu.x, mucTieu.y)
                        : ChickenKichThuocNhanVat.khoangCachDenNguoiChoi(
                                xs[diem], ys[diem], mucTieu.x, mucTieu.y);
                if (khoangCach < ganNhat) {
                    ganNhat = khoangCach;
                    if (ganNhat == 0.0D) {
                        return 0.0D;
                    }
                }
            }
        }
        return ganNhat;
    }

    private static ChickenQuanLyCongThucSung.KiemTraBanDo taoKiemTraBanDo(
            ChickenQuanLyBanDo banDo
    ) {
        return new ChickenQuanLyCongThucSung.KiemTraBanDo() {
            @Override
            public int getWidth() {
                return banDo.getWidth();
            }

            @Override
            public int getHeight() {
                return banDo.getHeight();
            }

            @Override
            public boolean coVaCham(short x, short y) {
                return banDo.coVaCham(x, y);
            }
        };
    }

    private static short chuanHoaGoc(short goc) {
        int ketQua = goc % 360;
        return (short) (ketQua < 0 ? ketQua + 360 : ketQua);
    }

    private static short[][] taoDuongDanCong(
            short dauX,
            short dauY,
            short dichX,
            short dichY,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY
    ) {
        int dx = dichX - dauX;
        int dy = dichY - dauY;
        int khoangCach = (int) Math.round(Math.hypot(dx, dy));
        int soDiemCung = Math.max(14, Math.min(64, khoangCach / 12 + 1));
        double doCaoCung = Math.max(32.0D,
                Math.min(96.0D, Math.abs(dx) * 0.18D + 20.0D));
        double dieuKhienX = (dauX + dichX) / 2.0D;
        double dieuKhienY = Math.min(dauY, dichY) - doCaoCung;
        short[] xs = new short[SO_DIEM_TOI_DA];
        short[] ys = new short[SO_DIEM_TOI_DA];
        int doDai = 1;
        xs[0] = dauX;
        ys[0] = dauY;
        int truocX = dauX;
        int truocY = dauY;

        for (int i = 1; i < soDiemCung && doDai < SO_DIEM_TOI_DA; i++) {
            double t = (double) i / (double) (soDiemCung - 1);
            double motTruT = 1.0D - t;
            // Dùng cùng cách lệch gió của boss luyện tập: gió tăng dần
            // theo bình phương tiến trình và giữ nguyên cho cả loạt đạn.
            double lechGio = t * t * 0.50D;
            int rawX = (int) Math.round(motTruT * motTruT * dauX
                    + 2.0D * motTruT * t * dieuKhienX + t * t * dichX
                    + windX * lechGio);
            int rawY = (int) Math.round(motTruT * motTruT * dauY
                    + 2.0D * motTruT * t * dieuKhienY + t * t * dichY
                    + windY * lechGio);
            short[] vaCham = timVaChamBanDo(truocX, truocY, rawX, rawY, banDo);
            if (vaCham != null) {
                xs[doDai] = vaCham[0];
                ys[doDai] = vaCham[1];
                return trim(xs, ys, doDai + 1);
            }
            if (raNgoai(rawX, rawY, banDo)) {
                xs[doDai] = (short) rawX;
                ys[doDai] = (short) rawY;
                return trim(xs, ys, doDai + 1);
            }
            xs[doDai] = (short) rawX;
            ys[doDai] = (short) rawY;
            doDai++;
            truocX = rawX;
            truocY = rawY;
        }

        int huongX = doDai >= 2 ? xs[doDai - 1] - xs[doDai - 2] : dx;
        int huongY = doDai >= 2 ? ys[doDai - 1] - ys[doDai - 2] : dy;
        double doLon = Math.max(1.0D, Math.hypot(huongX, huongY));
        double buocX = huongX / doLon * 12.0D;
        double buocY = huongY / doLon * 12.0D;
        double hienTaiX = truocX;
        double hienTaiY = truocY;
        while (doDai < SO_DIEM_TOI_DA) {
            int rawX = (int) Math.round(hienTaiX + buocX);
            int rawY = (int) Math.round(hienTaiY + buocY);
            short[] vaCham = timVaChamBanDo(truocX, truocY, rawX, rawY, banDo);
            if (vaCham != null) {
                xs[doDai] = vaCham[0];
                ys[doDai] = vaCham[1];
                doDai++;
                break;
            }
            xs[doDai] = (short) rawX;
            ys[doDai] = (short) rawY;
            doDai++;
            if (raNgoai(rawX, rawY, banDo)) {
                break;
            }
            truocX = rawX;
            truocY = rawY;
            hienTaiX = rawX;
            hienTaiY = rawY;
        }
        return trim(xs, ys, doDai);
    }

    private static short[] timVaChamBanDo(
            int x1, int y1, int x2, int y2, ChickenQuanLyBanDo banDo
    ) {
        int soBuoc = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
        for (int i = 1; i <= soBuoc; i++) {
            double t = (double) i / (double) Math.max(1, soBuoc);
            int x = (int) Math.round(x1 + (x2 - x1) * t);
            int y = (int) Math.round(y1 + (y2 - y1) * t);
            if (x >= 0 && y >= 0 && x < banDo.getWidth() && y < banDo.getHeight()
                    && banDo.coVaCham((short) x, (short) y)) {
                return new short[]{(short) x, (short) y};
            }
        }
        return null;
    }

    private static VaChamNhanVat timVaChamNhanVat(
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
            int soBuoc = Math.max(1, Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)));
            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                double t = (double) buoc / (double) soBuoc;
                int x = (int) Math.round(x1 + (x2 - x1) * t);
                int y = (int) Math.round(y1 + (y2 - y1) * t);
                if (!daRoiHitboxNguoiBan) {
                    daRoiHitboxNguoiBan = !trungHitbox(
                            nguoiBan, x, y);
                    if (!daRoiHitboxNguoiBan) {
                        continue;
                    }
                }
                if (cacMucTieu == null) {
                    continue;
                }
                for (ChickenChienBinh mucTieu : cacMucTieu) {
                    if (mucTieu == null || mucTieu.chet
                            || mucTieu.hp <= 0) {
                        continue;
                    }
                    if (trungHitbox(mucTieu, x, y)) {
                        return new VaChamNhanVat(
                                i, (short) x, (short) y, mucTieu);
                    }
                }
            }
        }
        return null;
    }

    private static boolean trungHitbox(
            ChickenChienBinh mucTieu,
            int x,
            int y
    ) {
        if (mucTieu == null) {
            return false;
        }
        return mucTieu.bot
                ? ChickenKichThuocNhanVat.trungBoss(
                        x, y, mucTieu.x, mucTieu.y)
                : ChickenKichThuocNhanVat.trungNguoiChoi(
                        x, y, mucTieu.x, mucTieu.y);
    }

    private static short[][] catTaiVaCham(short[] xs, short[] ys, VaChamNhanVat vaCham) {
        int doDai = Math.max(2, Math.min(xs.length, vaCham.chiSoDoan + 1));
        short[] ketQuaX = Arrays.copyOf(xs, doDai);
        short[] ketQuaY = Arrays.copyOf(ys, doDai);
        ketQuaX[doDai - 1] = vaCham.x;
        ketQuaY[doDai - 1] = vaCham.y;
        return new short[][]{ketQuaX, ketQuaY};
    }

    private static short[][] trim(short[] xs, short[] ys, int doDai) {
        int n = Math.max(1, Math.min(doDai, Math.min(xs.length, ys.length)));
        return new short[][]{Arrays.copyOf(xs, n), Arrays.copyOf(ys, n)};
    }

    private static boolean raNgoai(int x, int y, ChickenQuanLyBanDo banDo) {
        return x < 0 || y < 0 || x >= banDo.getWidth() || y >= banDo.getHeight();
    }

    private static short gocToiMucTieu(int x1, int y1, int x2, int y2) {
        double goc = Math.toDegrees(Math.atan2(-(y2 - y1), x2 - x1));
        int ketQua = (int) Math.round(goc);
        while (ketQua < 0) {
            ketQua += 360;
        }
        return (short) (ketQua % 360);
    }

    private static final class VaChamNhanVat {
        private final int chiSoDoan;
        private final short x;
        private final short y;
        private final ChickenChienBinh mucTieu;

        private VaChamNhanVat(
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

    private static final class NgamBan {
        private final short goc;
        private final byte luc;
        private final byte lucPhu;

        private NgamBan(short goc, byte luc, byte lucPhu) {
            this.goc = goc;
            this.luc = luc;
            this.lucPhu = lucPhu;
        }
    }
}
