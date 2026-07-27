package com.chicken.chien;

import com.chicken.avg.ChickenCongThucBanUltron;
import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chiso.ChickenKichThuocNhanVat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tao ky nang Ban x3 cua Ultron thanh ba vien server-authoritative doc lap.
 *
 * Moi vien duoc tinh lai sau khi vien truoc pha dia hinh. Vi vay loat ban co
 * dung ba quy dao/va cham nhu sung coi, thay vi nhan ba damage cua mot ket qua.
 */
public final class ChickenLoatBanUltronServer {
    public static final int SO_VIEN = 3;
    public static final byte LOAI_DAN = 0;

    private ChickenLoatBanUltronServer() {
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
    }

    public static ChickenKetQuaDan tao(
            ChickenChienBinh nguoiBan,
            short dauNongX,
            short dauNongY,
            short goc,
            byte luc,
            ChickenQuanLyBanDo banDo,
            ChickenChienBinh[] cacMucTieu,
            BoLocMucTieu boLoc
    ) {
        if (nguoiBan == null || banDo == null) {
            return new ChickenKetQuaDan(
                    LOAI_DAN, dauNongX, dauNongY, goc, luc, luc,
                    new short[0][], new short[0][],
                    new LinkedHashMap<ChickenChienBinh, Integer>());
        }

        short[][] cacX = new short[SO_VIEN][];
        short[][] cacY = new short[SO_VIEN][];
        Map<ChickenChienBinh, Integer> damage =
                new LinkedHashMap<ChickenChienBinh, Integer>();

        for (int vien = 0; vien < SO_VIEN; vien++) {
            ChickenCongThucBanUltron.DuongTia tia =
                    ChickenCongThucBanUltron.taoTiaThang(
                            dauNongX,
                            dauNongY,
                            goc,
                            banDo.getWidth(),
                            banDo.getHeight()
                    );
            VaCham vaChamBanDo = timVaChamBanDo(
                    tia.getX(), tia.getY(), banDo);
            short[][] duong = catTaiVaCham(
                    tia.getX(), tia.getY(), vaChamBanDo);

            VaCham vaChamNhanVat = timVaChamNhanVat(
                    nguoiBan, duong[0], duong[1], cacMucTieu, boLoc);
            if (vaChamNhanVat != null) {
                duong = catTaiVaCham(duong[0], duong[1], vaChamNhanVat);
                congDamage(
                        damage,
                        vaChamNhanVat.mucTieu,
                        Math.max(1, layTanCong(nguoiBan, luc)
                                - vaChamNhanVat.mucTieu.giap)
                );
            } else if (vaChamBanDo != null) {
                /* Pha ngay de vien sau duoc mo phong tren mat na dia hinh moi. */
                banDo.phaDiaHinh(
                        vaChamBanDo.hitX,
                        vaChamBanDo.hitY,
                        LOAI_DAN
                );
            }

            cacX[vien] = duong[0];
            cacY[vien] = duong[1];
        }

        return new ChickenKetQuaDan(
                LOAI_DAN,
                dauNongX,
                dauNongY,
                goc,
                luc,
                luc,
                cacX,
                cacY,
                damage
        );
    }

    private static int layTanCong(ChickenChienBinh nguoiBan, byte luc) {
        return nguoiBan.tanCong > 0
                ? nguoiBan.tanCong
                : 20 + (luc & 0xFF) / 2;
    }

    private static VaCham timVaChamBanDo(
            short[] xs,
            short[] ys,
            ChickenQuanLyBanDo banDo
    ) {
        return timVaCham(xs, ys, false, new KiemTraDiem() {
            @Override
            public ChickenChienBinh tai(int x, int y) {
                if (x < 0 || y < 0 || x >= banDo.getWidth()
                        || y >= banDo.getHeight()) {
                    return null;
                }
                return banDo.coVaCham((short) x, (short) y)
                        ? DIA_HINH : null;
            }
        });
    }

    private static VaCham timVaChamNhanVat(
            final ChickenChienBinh nguoiBan,
            short[] xs,
            short[] ys,
            final ChickenChienBinh[] cacMucTieu,
            final BoLocMucTieu boLoc
    ) {
        if (cacMucTieu == null) {
            return null;
        }
        return timVaCham(xs, ys, true, new KiemTraDiem() {
            @Override
            public ChickenChienBinh tai(int x, int y) {
                for (ChickenChienBinh mucTieu : cacMucTieu) {
                    if (mucTieu == null
                            || mucTieu.chet
                            || mucTieu.hp <= 0
                            || (mucTieu == nguoiBan && boLoc == null)
                            || (boLoc != null
                                    && !boLoc.chapNhan(nguoiBan, mucTieu))) {
                        continue;
                    }
                    boolean trung = boLoc != null
                            ? boLoc.trungHitbox(mucTieu, x, y)
                            : mucTieu.bot
                                    ? ChickenKichThuocNhanVat.trungBoss(
                                            x, y, mucTieu.x, mucTieu.y)
                                    : ChickenKichThuocNhanVat.trungNguoiChoi(
                                            x, y, mucTieu.x, mucTieu.y);
                    if (trung) {
                        return mucTieu;
                    }
                }
                return null;
            }
        });
    }

    private static VaCham timVaCham(
            short[] xs,
            short[] ys,
            boolean kiemTraDiemDau,
            KiemTraDiem kiemTra
    ) {
        int soDiem = Math.min(xs == null ? 0 : xs.length,
                ys == null ? 0 : ys.length);
        for (int i = 1; i < soDiem; i++) {
            int x1 = xs[i - 1];
            int y1 = ys[i - 1];
            int x2 = xs[i];
            int y2 = ys[i];
            int soBuoc = Math.max(1,
                    Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)));
            int buocDau = kiemTraDiemDau && i == 1 ? 0 : 1;
            for (int buoc = buocDau; buoc <= soBuoc; buoc++) {
                double tiLe = (double) buoc / (double) soBuoc;
                int x = (int) Math.round(x1 + (x2 - x1) * tiLe);
                int y = (int) Math.round(y1 + (y2 - y1) * tiLe);
                ChickenChienBinh mucTieu = kiemTra.tai(x, y);
                if (mucTieu != null) {
                    return new VaCham(mucTieu == DIA_HINH ? null : mucTieu,
                            i, (short) x, (short) y);
                }
            }
        }
        return null;
    }

    private static short[][] catTaiVaCham(
            short[] xs,
            short[] ys,
            VaCham vaCham
    ) {
        if (vaCham == null) {
            return new short[][]{xs, ys};
        }
        int soDiem = Math.min(xs == null ? 0 : xs.length,
                ys == null ? 0 : ys.length);
        int doDai = Math.max(2, Math.min(soDiem, vaCham.chiSoDoan + 1));
        short[] ketQuaX = Arrays.copyOf(xs, doDai);
        short[] ketQuaY = Arrays.copyOf(ys, doDai);
        ketQuaX[doDai - 1] = vaCham.hitX;
        ketQuaY[doDai - 1] = vaCham.hitY;
        return new short[][]{ketQuaX, ketQuaY};
    }

    private static void congDamage(
            Map<ChickenChienBinh, Integer> damage,
            ChickenChienBinh mucTieu,
            int satThuong
    ) {
        int hienTai = damage.containsKey(mucTieu) ? damage.get(mucTieu) : 0;
        long tong = (long) hienTai + satThuong;
        damage.put(mucTieu,
                tong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) tong);
    }

    private interface KiemTraDiem {
        ChickenChienBinh tai(int x, int y);
    }

    /* Sentinel noi bo: khong bao gio duoc dua vao ket qua damage. */
    private static final ChickenChienBinh DIA_HINH =
            new ChickenChienBinh((byte) -1, (short) 0, (short) 0,
                    "terrain", (short) 0, (byte) 0);

    private static final class VaCham {
        private final ChickenChienBinh mucTieu;
        private final int chiSoDoan;
        private final short hitX;
        private final short hitY;

        private VaCham(ChickenChienBinh mucTieu, int chiSoDoan,
                short hitX, short hitY) {
            this.mucTieu = mucTieu;
            this.chiSoDoan = chiSoDoan;
            this.hitX = hitX;
            this.hitY = hitY;
        }
    }
}
