package com.chicken.phong.boss.trandau.ruarong;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.chiso.ChickenKichThuocNhanVat;
import java.util.Arrays;

/** Tạo đường đạn riêng cho Rùa máy và Rồng máy map 58. */
public final class BossRuaRongTanCong {
    /**
     * Type 1 là đạn theo vũ khí (BulletForGun). Rùa mang wp=1563 nên client
     * dùng đúng ảnh Small1563.png đã được server trả qua CMD -40.
     */
    private static final byte LOAI_DAN_RUA = 1;

    /**
     * Type 59 là bong bóng nước native của client (/item/bongbong2.png).
     * Dùng type native để không phụ thuộc vào wp và không bị mất hình.
     */
    private static final byte LOAI_DAN_RONG = 59;
    private static final byte LUC_HIEN_THI = 24;
    private static final int SO_DIEM_TOI_DA = 96;

    private BossRuaRongTanCong() {
    }

    public static ChickenKetQuaDan taoPhatBanRua(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY
    ) {
        int huong = mucTieu.x < boss.x ? -1 : 1;
        short dauX = (short) (boss.x + huong * 56);
        short dauY = (short) (boss.y - 70);
        short dichX = mucTieu.x;
        short dichY = (short) ChickenKichThuocNhanVat.layTamThanNguoiChoiY(mucTieu.y);
        short[][] duong = taoDuongDan(dauX, dauY, dichX, dichY, banDo, windX, windY, 14.0D);
        VaCham vaCham = timVaChamNguoiChoi(duong[0], duong[1], mucTieu);
        if (vaCham != null) {
            duong = catTaiVaCham(duong[0], duong[1], vaCham);
        }
        int satThuong = vaCham == null ? 0 : Math.max(1, boss.tanCong - mucTieu.giap);
        short goc = gocToiMucTieu(dauX, dauY, dichX, dichY);
        return new ChickenKetQuaDan(LOAI_DAN_RUA, dauX, dauY, goc, LUC_HIEN_THI,
                duong[0], duong[1], vaCham == null ? null : mucTieu, satThuong);
    }

    public static ChickenKetQuaDan taoPhatBanRong(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY
    ) {
        int huong = mucTieu.x < boss.x ? -1 : 1;
        short dauX = (short) (boss.x + huong * 66);
        short dauY = (short) (boss.y - 62);
        short dichX = mucTieu.x;
        short dichY = (short) ChickenKichThuocNhanVat.layTamThanNguoiChoiY(mucTieu.y);
        short[][] duong = taoDuongDan(dauX, dauY, dichX, dichY, banDo, windX, windY, 22.0D);
        VaCham vaCham = timVaChamNguoiChoi(duong[0], duong[1], mucTieu);
        if (vaCham != null) {
            duong = catTaiVaCham(duong[0], duong[1], vaCham);
        }
        int satThuong = vaCham == null ? 0 : Math.max(1, boss.tanCong - mucTieu.giap);
        short goc = gocToiMucTieu(dauX, dauY, dichX, dichY);
        return new ChickenKetQuaDan(LOAI_DAN_RONG, dauX, dauY, goc, (byte) 30,
                duong[0], duong[1], vaCham == null ? null : mucTieu, satThuong);
    }

    private static short[][] taoDuongDan(
            short dauX,
            short dauY,
            short dichX,
            short dichY,
            ChickenQuanLyBanDo banDo,
            byte windX,
            byte windY,
            double doCong
    ) {
        int khoangCach = Math.max(1, (int) Math.round(Math.hypot(dichX - dauX, dichY - dauY)));
        int soDiem = Math.max(12, Math.min(SO_DIEM_TOI_DA, khoangCach / 12 + 2));
        short[] xs = new short[soDiem];
        short[] ys = new short[soDiem];
        xs[0] = dauX;
        ys[0] = dauY;
        int doDai = 1;
        int truocX = dauX;
        int truocY = dauY;
        for (int i = 1; i < soDiem; i++) {
            double t = (double) i / (double) (soDiem - 1);
            double cong = Math.sin(Math.PI * t);
            int x = (int) Math.round(dauX + (dichX - dauX) * t + windX * cong * 0.65D);
            int y = (int) Math.round(dauY + (dichY - dauY) * t + windY * cong * 0.55D - cong * doCong);
            short[] chamMap = timVaChamBanDo(truocX, truocY, x, y, banDo);
            if (chamMap != null) {
                xs[doDai] = chamMap[0];
                ys[doDai] = chamMap[1];
                doDai++;
                break;
            }
            xs[doDai] = (short) x;
            ys[doDai] = (short) y;
            doDai++;
            truocX = x;
            truocY = y;
        }
        return new short[][]{Arrays.copyOf(xs, doDai), Arrays.copyOf(ys, doDai)};
    }

    private static short[] timVaChamBanDo(int x1, int y1, int x2, int y2, ChickenQuanLyBanDo banDo) {
        int soBuoc = Math.max(1, Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)));
        for (int i = 1; i <= soBuoc; i++) {
            double t = (double) i / (double) soBuoc;
            int x = (int) Math.round(x1 + (x2 - x1) * t);
            int y = (int) Math.round(y1 + (y2 - y1) * t);
            if (x >= 0 && y >= 0 && x < banDo.getWidth() && y < banDo.getHeight() && banDo.coVaCham((short) x, (short) y)) {
                return new short[]{(short) x, (short) y};
            }
        }
        return null;
    }

    private static VaCham timVaChamNguoiChoi(short[] xs, short[] ys, ChickenChienBinh mucTieu) {
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
                if (ChickenKichThuocNhanVat.trungNguoiChoi(x, y, mucTieu.x, mucTieu.y)) {
                    return new VaCham(i, (short) x, (short) y);
                }
            }
        }
        return null;
    }

    private static short[][] catTaiVaCham(short[] xs, short[] ys, VaCham vaCham) {
        int doDai = Math.max(2, Math.min(xs.length, vaCham.chiSoDoan + 1));
        short[] ketQuaX = Arrays.copyOf(xs, doDai);
        short[] ketQuaY = Arrays.copyOf(ys, doDai);
        ketQuaX[doDai - 1] = vaCham.x;
        ketQuaY[doDai - 1] = vaCham.y;
        return new short[][]{ketQuaX, ketQuaY};
    }

    private static short gocToiMucTieu(int x1, int y1, int x2, int y2) {
        int goc = (int) Math.round(Math.toDegrees(Math.atan2(-(y2 - y1), x2 - x1)));
        while (goc < 0) {
            goc += 360;
        }
        return (short) (goc % 360);
    }

    private static final class VaCham {
        private final int chiSoDoan;
        private final short x;
        private final short y;

        private VaCham(int chiSoDoan, short x, short y) {
            this.chiSoDoan = chiSoDoan;
            this.x = x;
            this.y = y;
        }
    }
}
