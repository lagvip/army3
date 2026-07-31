package com.chicken.mang;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Chuyển quỹ đạo laser đầy đủ của server sang dạng hiển thị mà bulletType 49
 * của client Army3 chờ:
 *
 * <pre>
 * các điểm viên kim cương bay tới đỉnh + điểm va chạm cuối + vx/vy của tia
 * </pre>
 *
 * Server vẫn giữ quỹ đạo đầy đủ để tính va chạm và sát thương. Việc rút gọn
 * này chỉ dành cho packet hiển thị, nhờ đó viên kim cương dừng ở đỉnh thay vì
 * tiếp tục chạy dọc theo tia laser.
 */
final class ChickenDuongDanLaserClient {

    static final byte LOAI_DAN_LASER = 49;

    static final class DuLieu {
        final short[] x;
        final short[] y;
        final byte buocX;
        final byte buocY;

        private DuLieu(short[] x, short[] y, byte buocX, byte buocY) {
            this.x = x;
            this.y = y;
            this.buocX = buocX;
            this.buocY = buocY;
        }
    }

    private ChickenDuongDanLaserClient() {
    }

    static DuLieu tao(short[] duongX, short[] duongY,
            short xMacDinh, short yMacDinh) {
        return tao(duongX, duongY, xMacDinh, yMacDinh, false);
    }

    /**
     * {@code neoDiemDauTaiNhanVat} chi dung cho animation AVG cu can diem neo
     * tai than nhan vat. Sung laser thuong phai giu diem dau nong that; neu
     * thay bang toa do chan, client se ve tia lech khoi duong va cham server.
     */
    static DuLieu tao(short[] duongX, short[] duongY,
            short xMacDinh, short yMacDinh,
            boolean neoDiemDauTaiNhanVat) {
        int soDiem = Math.min(
                duongX == null ? 0 : duongX.length,
                duongY == null ? 0 : duongY.length
        );
        if (soDiem <= 0) {
            return new DuLieu(
                    new short[]{xMacDinh},
                    new short[]{yMacDinh},
                    (byte) 0,
                    (byte) 0
            );
        }

        soDiem = Math.min(Short.MAX_VALUE, soDiem);
        int chiSoDinh = 0;
        for (int i = 1; i < soDiem; i++) {
            if (duongY[i] < duongY[chiSoDinh]) {
                chiSoDinh = i;
            }
        }

        /*
         * Nếu viên đạn va địa hình trước hoặc ngay tại đỉnh thì chưa có đoạn
         * laser. Vẫn phải gửi hai byte 0 vì client luôn đọc chúng với type 49.
         */
        if (chiSoDinh >= soDiem - 1) {
            short[] x = new short[soDiem];
            short[] y = new short[soDiem];
            System.arraycopy(duongX, 0, x, 0, soDiem);
            System.arraycopy(duongY, 0, y, 0, soDiem);
            if (neoDiemDauTaiNhanVat) {
                x[0] = xMacDinh;
                y[0] = yMacDinh;
            }
            return new DuLieu(x, y, (byte) 0, (byte) 0);
        }

        /*
         * Bullet 49 dừng ở length - 4 trên client PC, còn một số bản Java ME
         * dừng ở length - 2. Chèn hai bản sao của đỉnh trước điểm va chạm để
         * cả hai nhánh đều dừng đúng tại viên kim cương:
         *
         *   [..., đỉnh, đỉnh, đỉnh, điểm va chạm]
         *         ^ length-4   ^ length-2
         */
        short[] x = new short[chiSoDinh + 4];
        short[] y = new short[chiSoDinh + 4];
        System.arraycopy(duongX, 0, x, 0, chiSoDinh + 1);
        System.arraycopy(duongY, 0, y, 0, chiSoDinh + 1);
        x[x.length - 3] = duongX[chiSoDinh];
        y[y.length - 3] = duongY[chiSoDinh];
        x[x.length - 2] = duongX[chiSoDinh];
        y[y.length - 2] = duongY[chiSoDinh];
        x[x.length - 1] = duongX[soDiem - 1];
        y[y.length - 1] = duongY[soDiem - 1];

        int chiSoBuocDau = 1;
        while (chiSoBuocDau <= chiSoDinh
                && duongX[chiSoBuocDau] == duongX[0]
                && duongY[chiSoBuocDau] == duongY[0]) {
            chiSoBuocDau++;
        }
        int dxTia = chiSoBuocDau <= chiSoDinh
                ? duongX[chiSoBuocDau] - duongX[0]
                : duongX[chiSoDinh] - duongX[0];
        int dyTia = chiSoBuocDau <= chiSoDinh
                ? duongY[chiSoBuocDau] - duongY[0]
                : duongY[chiSoDinh] - duongY[0];
        int buocX = giuKhacKhongSauChiaDoi(
                kepByteCoDau(dxTia), duongX[chiSoDinh] - duongX[0]);
        int buocY = giuKhacKhongSauChiaDoi(
                kepByteCoDau(dyTia), duongY[chiSoDinh] - duongY[0]);

        /*
         * Client gốc chỉ bật paintLazerGirl khi cả hai byte đều khác 0.
         * Giữ một dịch chuyển 1px cho góc đúng dọc/ngang để tia vẫn xuất hiện;
         * tọa độ kết thúc vẫn do server gửi nên không ảnh hưởng va chạm.
         */
        if (buocX == 0 && buocY != 0) {
            buocX = x[x.length - 1] >= x[0] ? 2 : -2;
        }
        if (buocY == 0 && buocX != 0) {
            buocY = y[chiSoDinh] <= y[0] ? -2 : 2;
        }

        /*
         * Client dung diem dau lam muc tieu animation bay cua AVG. Chi neo ban
         * sao gui qua mang; quy dao vat ly server va diem va cham khong doi.
         */
        if (neoDiemDauTaiNhanVat) {
            x[0] = xMacDinh;
            y[0] = yMacDinh;
        }
        return new DuLieu(x, y, (byte) buocX, (byte) buocY);
    }

    /**
     * Ghi đúng nhánh TYPE SHOOT = 0 của client Unity: điểm đầu là short tuyệt
     * đối, điểm giữa là byte delta, điểm cuối là short tuyệt đối rồi tới
     * dXLaser/dYLaser.
     */
    static void ghiNen(DataOutputStream ds, DuLieu laser) throws IOException {
        int soDiem = Math.min(laser.x.length, laser.y.length);
        if (soDiem <= 0) {
            throw new IOException("Quỹ đạo laser rỗng");
        }

        ds.writeShort(soDiem);
        ds.writeShort(laser.x[0]);
        ds.writeShort(laser.y[0]);
        if (soDiem == 1) {
            return;
        }

        for (int i = 1; i < soDiem - 1; i++) {
            int dx = laser.x[i] - laser.x[i - 1];
            int dy = laser.y[i] - laser.y[i - 1];
            if (dx < Byte.MIN_VALUE || dx > Byte.MAX_VALUE
                    || dy < Byte.MIN_VALUE || dy > Byte.MAX_VALUE) {
                throw new IOException(
                        "Bước quỹ đạo laser vượt byte tại " + i
                                + ": dx=" + dx + ", dy=" + dy);
            }
            ds.writeByte(dx);
            ds.writeByte(dy);
        }

        ds.writeShort(laser.x[soDiem - 1]);
        ds.writeShort(laser.y[soDiem - 1]);
        ds.writeByte(laser.buocX);
        ds.writeByte(laser.buocY);
    }

    private static int giuKhacKhongSauChiaDoi(int giaTri, int huongMacDinh) {
        if (giaTri == 1) {
            return 2;
        }
        if (giaTri == -1) {
            return -2;
        }
        if (giaTri == 0 && huongMacDinh != 0) {
            return huongMacDinh > 0 ? 2 : -2;
        }
        return giaTri;
    }

    private static int kepByteCoDau(int giaTri) {
        return Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, giaTri));
    }
}
