package com.chicken.chien;

import com.chicken.chiso.ChickenKichThuocNhanVat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Xu ly mot phat ban thuong hoan toan tren server.
 *
 * Lop nay dung chung cho PvP va boss: tao du so quy dao theo sung, quet hitbox
 * tung pixel, cat duong dan tai muc tieu dau tien va cong damage tung vien.
 */
public final class ChickenPhatBanServer {
    private ChickenPhatBanServer() {
    }

    public interface BoLocMucTieu {
        boolean chapNhan(ChickenChienBinh nguoiBan, ChickenChienBinh mucTieu);

        default boolean trungHitbox(
                ChickenChienBinh mucTieu,
                int danX,
                int danY
        ) {
            return mucTieu.bot
                    ? ChickenKichThuocNhanVat.trungBoss(
                            danX, danY, mucTieu.x, mucTieu.y)
                    : ChickenKichThuocNhanVat.trungNguoiChoi(
                            danX, danY, mucTieu.x, mucTieu.y);
        }

        default int nuaRongHitbox(ChickenChienBinh mucTieu) {
            return mucTieu.bot
                    ? ChickenKichThuocNhanVat.BOSS_NUA_RONG
                    : ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
        }

        default int lechTrenHitbox(ChickenChienBinh mucTieu) {
            return mucTieu.bot
                    ? ChickenKichThuocNhanVat.BOSS_LECH_TREN
                    : ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_TREN;
        }

        default int lechDuoiHitbox(ChickenChienBinh mucTieu) {
            return mucTieu.bot
                    ? ChickenKichThuocNhanVat.BOSS_LECH_DUOI
                    : ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_DUOI;
        }
    }

    public static ChickenKetQuaDan tao(
            ChickenChienBinh nguoiBan,
            short dauNongX,
            short dauNongY,
            short goc,
            byte luc,
            byte lucPhu,
            ChickenQuanLyDanSung.DuLieuSung sung,
            byte windX,
            byte windY,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo,
            ChickenChienBinh[] cacMucTieu,
            BoLocMucTieu boLoc
    ) {
        ChickenLoatDanServer.KetQua loat = ChickenLoatDanServer.tao(
                dauNongX,
                dauNongY,
                nguoiBan.x,
                nguoiBan.y,
                goc,
                luc,
                lucPhu,
                sung,
                windX,
                windY,
                banDo
        );
        ChickenLoatDanServer.KetQua loatKhongDiaHinh = ChickenLoatDanServer.tao(
                dauNongX,
                dauNongY,
                nguoiBan.x,
                nguoiBan.y,
                goc,
                luc,
                lucPhu,
                sung,
                windX,
                windY,
                taoBanDoKhongVaCham(banDo)
        );
        short[][] cacX = loat.getCacDuongX();
        short[][] cacY = loat.getCacDuongY();
        short[][] cacXKhongDiaHinh = loatKhongDiaHinh.getCacDuongX();
        short[][] cacYKhongDiaHinh = loatKhongDiaHinh.getCacDuongY();
        int soDuong = Math.max(1, Math.min(cacX.length, cacY.length));
        Map<ChickenChienBinh, Integer> satThuongTheoMucTieu =
                new LinkedHashMap<ChickenChienBinh, Integer>();
        ChickenCauHinhSatThuongSung.HoSoSatThuong hoSoSatThuong = sung == null
                ? null
                : ChickenCauHinhSatThuongSung.theoIdSung(sung.getIdSung());
        int tanCong = nguoiBan.tanCong > 0
                ? nguoiBan.tanCong
                : 20 + (luc & 0xFF) / 2;
        boolean sieuCaoHieuUng = false;

        for (int i = 0; i < soDuong; i++) {
            short[] duongSieuCaoX = i < cacXKhongDiaHinh.length
                    ? cacXKhongDiaHinh[i] : null;
            short[] duongSieuCaoY = i < cacYKhongDiaHinh.length
                    ? cacYKhongDiaHinh[i] : null;
            Set<ChickenChienBinh> mucTieuSieuCao = timCacMucTieuSieuCao(
                    nguoiBan,
                    loat.getLoaiDan(),
                    duongSieuCaoX,
                    duongSieuCaoY,
                    cacMucTieu,
                    boLoc
            );
            if (i == 0 && !mucTieuSieuCao.isEmpty()) {
                sieuCaoHieuUng = true;
            }
            boolean xuyenNguoiCaptain = sung != null
                    && sung.getIdSung() == ChickenQuanLyCongThucSung.ID_SUNG_CAPTAIN;
            Set<ChickenChienBinh> cacMucTieuCaptainLuotQua = xuyenNguoiCaptain
                    ? timTatCaMucTieuTrung(
                            nguoiBan, cacX[i], cacY[i], cacMucTieu, boLoc)
                    : new LinkedHashSet<ChickenChienBinh>();
            VaCham vaCham = xuyenNguoiCaptain
                    ? null
                    : timVaChamDauTien(
                            nguoiBan,
                            cacX[i],
                            cacY[i],
                            cacMucTieu,
                            boLoc
                    );
            if (vaCham != null) {
                short[][] daCat = catTaiVaCham(cacX[i], cacY[i], vaCham);
                cacX[i] = daCat[0];
                cacY[i] = daCat[1];
            }
            if (hoSoSatThuong == null) {
                continue;
            }

            int soDiem = Math.min(
                    cacX[i] == null ? 0 : cacX[i].length,
                    cacY[i] == null ? 0 : cacY[i].length
            );
            if (soDiem <= 0) {
                continue;
            }
            int xNo = cacX[i][soDiem - 1];
            int yNo = cacY[i][soDiem - 1];

            if (xuyenNguoiCaptain) {
                Map<ChickenChienBinh, Integer> damageMotDuong =
                        new LinkedHashMap<ChickenChienBinh, Integer>();
                for (ChickenChienBinh mucTieu : cacMucTieuCaptainLuotQua) {
                    int damageDayDu = tinhSatThuongMoiDuong(
                            tanCong,
                            mucTieu,
                            soDuong,
                            mucTieuSieuCao.contains(mucTieu)
                    );
                    damageMotDuong.put(mucTieu, Math.max(1, damageDayDu / 2));
                }
                if (laDiemVaChamDiaHinh(xNo, yNo, banDo) && cacMucTieu != null) {
                    for (ChickenChienBinh mucTieu : cacMucTieu) {
                        if (!laMucTieuNhanNoHopLe(
                                nguoiBan, mucTieu, boLoc)) {
                            continue;
                        }
                        int damageDayDu = tinhSatThuongMoiDuong(
                                tanCong,
                                mucTieu,
                                soDuong,
                                mucTieuSieuCao.contains(mucTieu)
                        );
                        int damageNo = tinhSatThuongNoChoMucTieu(
                                hoSoSatThuong,
                                damageDayDu,
                                xNo,
                                yNo,
                                mucTieu,
                                boLoc,
                                banDo
                        );
                        int damageLuotQua = damageMotDuong.containsKey(mucTieu)
                                ? damageMotDuong.get(mucTieu)
                                : 0;
                        if (damageNo > damageLuotQua) {
                            damageMotDuong.put(mucTieu, damageNo);
                        }
                    }
                }
                for (Map.Entry<ChickenChienBinh, Integer> entry
                        : damageMotDuong.entrySet()) {
                    congSatThuong(
                            satThuongTheoMucTieu,
                            entry.getKey(),
                            entry.getValue()
                    );
                }
                continue;
            }

            if (!hoSoSatThuong.coNoTheoKhoangCach()) {
                if (vaCham != null) {
                    int damage = tinhSatThuongMoiDuong(
                            tanCong,
                            vaCham.mucTieu,
                            soDuong,
                            mucTieuSieuCao.contains(vaCham.mucTieu)
                    );
                    congSatThuong(satThuongTheoMucTieu, vaCham.mucTieu, damage);
                }
                continue;
            }

            // Khong tao vu no khi dan chi het buoc mo phong/ra ngoai map.
            boolean coDiemNo = vaCham != null
                    || laDiemVaChamDiaHinh(xNo, yNo, banDo);
            if (!coDiemNo || cacMucTieu == null) {
                continue;
            }
            for (ChickenChienBinh mucTieu : cacMucTieu) {
                if (!laMucTieuNhanNoHopLe(nguoiBan, mucTieu, boLoc)) {
                    continue;
                }
                int damageDayDuMoiDuong = tinhSatThuongMoiDuong(
                        tanCong,
                        mucTieu,
                        soDuong,
                        mucTieuSieuCao.contains(mucTieu)
                );
                int damageTheoKhoangCach =
                        tinhSatThuongNoChoMucTieu(
                                hoSoSatThuong,
                                damageDayDuMoiDuong,
                                xNo,
                                yNo,
                                mucTieu,
                                boLoc,
                                banDo
                        );
                congSatThuong(
                        satThuongTheoMucTieu,
                        mucTieu,
                        damageTheoKhoangCach
                );
            }
        }

        return new ChickenKetQuaDan(
                loat.getLoaiDan(),
                dauNongX,
                dauNongY,
                goc,
                luc,
                loat.getLucPhu(),
                cacX,
                cacY,
                satThuongTheoMucTieu,
                sieuCaoHieuUng
        );
    }

    private static ChickenQuanLyCongThucSung.KiemTraBanDo taoBanDoKhongVaCham(
            final ChickenQuanLyCongThucSung.KiemTraBanDo banDo
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
                return false;
            }
        };
    }

    private static Set<ChickenChienBinh> timCacMucTieuSieuCao(
            ChickenChienBinh nguoiBan,
            byte loaiDan,
            short[] xsKhongDiaHinh,
            short[] ysKhongDiaHinh,
            ChickenChienBinh[] cacMucTieu,
            BoLocMucTieu boLoc
    ) {
        Set<ChickenChienBinh> ketQua = new LinkedHashSet<ChickenChienBinh>();
        if (cacMucTieu == null) {
            return ketQua;
        }
        for (ChickenChienBinh mucTieu : cacMucTieu) {
            if (!laMucTieuHopLe(nguoiBan, mucTieu, boLoc)) {
                continue;
            }
            if (ChickenSieuCao.laPhatSieuCaoTrungMucTieu(
                    loaiDan,
                    xsKhongDiaHinh,
                    ysKhongDiaHinh,
                    mucTieu.x,
                    mucTieu.y,
                    (danX, danY) -> trungHitbox(
                            boLoc, mucTieu, danX, danY)
            )) {
                ketQua.add(mucTieu);
            }
        }
        return ketQua;
    }

    private static VaCham timVaChamDauTien(
            ChickenChienBinh nguoiBan,
            short[] xs,
            short[] ys,
            ChickenChienBinh[] cacMucTieu,
            BoLocMucTieu boLoc
    ) {
        if (xs == null || ys == null || cacMucTieu == null) {
            return null;
        }
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
                double tiLe = (double) buoc / (double) soBuoc;
                int danX = (int) Math.round(x1 + (x2 - x1) * tiLe);
                int danY = (int) Math.round(y1 + (y2 - y1) * tiLe);
                for (ChickenChienBinh mucTieu : cacMucTieu) {
                    if (!laMucTieuHopLe(
                            nguoiBan, mucTieu, boLoc)) {
                        continue;
                    }
                    boolean trung = trungHitbox(
                            boLoc, mucTieu, danX, danY);
                    if (trung) {
                        return new VaCham(
                                mucTieu,
                                i,
                                (short) danX,
                                (short) danY
                        );
                    }
                }
            }
        }
        return null;
    }

    private static Set<ChickenChienBinh> timTatCaMucTieuTrung(
            ChickenChienBinh nguoiBan,
            short[] xs,
            short[] ys,
            ChickenChienBinh[] cacMucTieu,
            BoLocMucTieu boLoc
    ) {
        Set<ChickenChienBinh> ketQua = new LinkedHashSet<ChickenChienBinh>();
        if (xs == null || ys == null || cacMucTieu == null) {
            return ketQua;
        }
        for (ChickenChienBinh mucTieu : cacMucTieu) {
            if (laMucTieuHopLe(nguoiBan, mucTieu, boLoc)
                    && duongDanTrungMucTieu(
                            xs, ys, mucTieu, boLoc)) {
                ketQua.add(mucTieu);
            }
        }
        return ketQua;
    }

    private static boolean duongDanTrungMucTieu(
            short[] xs,
            short[] ys,
            ChickenChienBinh mucTieu,
            BoLocMucTieu boLoc
    ) {
        int soDiem = Math.min(xs.length, ys.length);
        for (int i = 1; i < soDiem; i++) {
            int x1 = xs[i - 1];
            int y1 = ys[i - 1];
            int x2 = xs[i];
            int y2 = ys[i];
            int soBuoc = Math.max(1, Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)));
            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                double tiLe = (double) buoc / (double) soBuoc;
                int danX = (int) Math.round(x1 + (x2 - x1) * tiLe);
                int danY = (int) Math.round(y1 + (y2 - y1) * tiLe);
                boolean trung = trungHitbox(
                        boLoc, mucTieu, danX, danY);
                if (trung) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int tinhSatThuongNoChoMucTieu(
            ChickenCauHinhSatThuongSung.HoSoSatThuong hoSo,
            int satThuongGoc,
            int xNo,
            int yNo,
            ChickenChienBinh mucTieu,
            BoLocMucTieu boLoc,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        if (hoSo == null || mucTieu == null || satThuongGoc <= 0) {
            return 0;
        }
        int nuaRong = boLoc == null
                ? (mucTieu.bot
                        ? ChickenKichThuocNhanVat.BOSS_NUA_RONG
                        : ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG)
                : boLoc.nuaRongHitbox(mucTieu);
        int lechTren = boLoc == null
                ? (mucTieu.bot
                        ? ChickenKichThuocNhanVat.BOSS_LECH_TREN
                        : ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_TREN)
                : boLoc.lechTrenHitbox(mucTieu);
        int lechDuoi = boLoc == null
                ? (mucTieu.bot
                        ? ChickenKichThuocNhanVat.BOSS_LECH_DUOI
                        : ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_DUOI)
                : boLoc.lechDuoiHitbox(mucTieu);
        int dichX = Math.max(
                mucTieu.x - nuaRong,
                Math.min(mucTieu.x + nuaRong, xNo));
        int dichY = Math.max(
                mucTieu.y - lechTren,
                Math.min(mucTieu.y - lechDuoi, yNo));
        double khoangCach = Math.hypot(xNo - dichX, yNo - dichY);
        if (ChickenTinhSatThuongNo.tinhPhanTramTheoKhoangCach(
                hoSo, khoangCach) <= 0) {
            return 0;
        }
        int phanTramQuaDiaHinh =
                ChickenTinhSatThuongNo.tinhPhanTramQuaDiaHinh(
                        hoSo,
                        xNo,
                        yNo,
                        mucTieu.x,
                        mucTieu.y,
                        nuaRong,
                        lechTren,
                        lechDuoi,
                        banDo,
                        khoangCach
                );
        return ChickenTinhSatThuongNo.tinhSatThuong(
                hoSo, satThuongGoc, khoangCach, phanTramQuaDiaHinh);
    }

    private static boolean trungHitbox(
            BoLocMucTieu boLoc,
            ChickenChienBinh mucTieu,
            int danX,
            int danY
    ) {
        if (boLoc != null) {
            return boLoc.trungHitbox(mucTieu, danX, danY);
        }
        return mucTieu.bot
                ? ChickenKichThuocNhanVat.trungBoss(
                        danX, danY, mucTieu.x, mucTieu.y)
                : ChickenKichThuocNhanVat.trungNguoiChoi(
                        danX, danY, mucTieu.x, mucTieu.y);
    }

    private static boolean laMucTieuHopLe(
            ChickenChienBinh nguoiBan,
            ChickenChienBinh mucTieu,
            BoLocMucTieu boLoc
    ) {
        return mucTieu != null
                && !mucTieu.chet
                && mucTieu.hp > 0
                /*
                 * Khong tu quyet dinh loai nguoi ban tai tang va cham.
                 * PvP tu choi self-hit trong BoLoc cua PvP; cac phong boss
                 * chu dong chap nhan de dong bo voi luyen tap/friendly fire.
                 * Khi khong co BoLoc van giu mac dinh an toan la bo qua minh.
                 */
                && (mucTieu != nguoiBan || boLoc != null)
                && (boLoc == null || boLoc.chapNhan(nguoiBan, mucTieu));
    }

    /**
     * Vu no co the gay damage lai nguoi ban neu BoLoc cua che do cho phep.
     */
    private static boolean laMucTieuNhanNoHopLe(
            ChickenChienBinh nguoiBan,
            ChickenChienBinh mucTieu,
            BoLocMucTieu boLoc
    ) {
        return mucTieu != null
                && !mucTieu.chet
                && mucTieu.hp > 0
                && (boLoc == null || boLoc.chapNhan(nguoiBan, mucTieu));
    }

    private static boolean laDiemVaChamDiaHinh(
            int x,
            int y,
            ChickenQuanLyCongThucSung.KiemTraBanDo banDo
    ) {
        return banDo != null
                && x >= 0
                && y >= 0
                && x < banDo.getWidth()
                && y < banDo.getHeight()
                && banDo.coVaCham((short) x, (short) y);
    }

    private static int tinhSatThuongMoiDuong(
            int tanCong,
            ChickenChienBinh mucTieu,
            int soDuong,
            boolean laSieuCao
    ) {
        int satThuongDayDu = Math.max(1, tanCong - mucTieu.giap);
        int satThuongMoiDuong = Math.max(
                1,
                (satThuongDayDu + soDuong - 1) / soDuong
        );
        return laSieuCao
                ? ChickenSieuCao.tangSatThuong(satThuongMoiDuong)
                : satThuongMoiDuong;
    }

    private static short[][] catTaiVaCham(
            short[] xs,
            short[] ys,
            VaCham vaCham
    ) {
        int soDiem = Math.min(xs.length, ys.length);
        int doDai = Math.max(2, Math.min(soDiem, vaCham.chiSoDoan + 1));
        short[] ketQuaX = Arrays.copyOf(xs, doDai);
        short[] ketQuaY = Arrays.copyOf(ys, doDai);
        ketQuaX[doDai - 1] = vaCham.x;
        ketQuaY[doDai - 1] = vaCham.y;
        return new short[][]{ketQuaX, ketQuaY};
    }

    private static void congSatThuong(
            Map<ChickenChienBinh, Integer> damage,
            ChickenChienBinh mucTieu,
            int satThuong
    ) {
        if (satThuong <= 0) {
            return;
        }
        int hienTai = damage.containsKey(mucTieu) ? damage.get(mucTieu) : 0;
        long tong = (long) hienTai + satThuong;
        damage.put(
                mucTieu,
                tong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) tong
        );
    }

    private static final class VaCham {
        private final ChickenChienBinh mucTieu;
        private final int chiSoDoan;
        private final short x;
        private final short y;

        private VaCham(
                ChickenChienBinh mucTieu,
                int chiSoDoan,
                short x,
                short y
        ) {
            this.mucTieu = mucTieu;
            this.chiSoDoan = chiSoDoan;
            this.x = x;
            this.y = y;
        }
    }
}
