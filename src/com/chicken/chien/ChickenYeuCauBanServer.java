package com.chicken.chien;

import com.chicken.avg.ChickenGocBanUltron;
import com.chicken.avg.ChickenKyNangDacBietIronMan;
import com.chicken.avg.ChickenKyNangDacBietUltron;
import com.chicken.avg.ChickenTiaLaserIronMan;
import com.chicken.mang.ChickenTinNhan;

/**
 * Doc intent ban tu client, nhung suy ra loai dan va so vien tu sung server.
 * Toa do, loai dan va so phat trong packet cu chi duoc doc bo de giu protocol.
 */
public final class ChickenYeuCauBanServer {
    private ChickenYeuCauBanServer() {
    }

    public static KetQua doc(
            ChickenTinNhan tinNhan,
            ChickenQuanLyDanSung.DuLieuSung sung,
            byte avenger
    ) {
        if (tinNhan == null || sung == null) {
            return null;
        }
        byte[] duLieu = tinNhan.layDuLieu();
        boolean danHaiLuc = sung.getLoaiDan() == 17 || sung.getLoaiDan() == 19;
        int doDaiChinhXac = danHaiLuc ? 10 : 9;
        if (duLieu == null || duLieu.length != doDaiChinhXac) {
            return null;
        }

        int viTri = 0;
        viTri++; // Loai dan client.
        viTri += 2; // X client.
        viTri += 2; // Y client.
        short goc = docShort(duLieu, viTri);
        viTri += 2;
        int luc = duLieu[viTri++] & 0xFF;
        int lucPhu = luc;
        if (danHaiLuc) {
            lucPhu = duLieu[viTri++] & 0xFF;
        }
        viTri++; // So phat client.
        if (viTri != duLieu.length) {
            return null;
        }

        if (avenger == ChickenKyNangDacBietUltron.AVG_ULTRON) {
            goc = ChickenGocBanUltron.chuanHoa(goc);
        } else if (avenger == ChickenKyNangDacBietIronMan.AVG_IRON_MAN) {
            goc = ChickenTiaLaserIronMan.chuanHoaGoc(goc);
        } else {
            goc = chuanHoaGoc(goc);
        }

        return new KetQua(
                sung.getLoaiDan(),
                sung.getSoVienMoiLoat(),
                goc,
                (byte) kep(luc, 1, 30),
                (byte) kep(lucPhu, 1, 30)
        );
    }

    private static short docShort(byte[] duLieu, int viTri) {
        return (short) (((duLieu[viTri] & 0xFF) << 8)
                | (duLieu[viTri + 1] & 0xFF));
    }

    private static short chuanHoaGoc(short goc) {
        int ketQua = goc % 360;
        return (short) (ketQua < 0 ? ketQua + 360 : ketQua);
    }

    private static int kep(int giaTri, int nhoNhat, int lonNhat) {
        return Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }

    public static final class KetQua {
        private final byte loaiDan;
        private final byte soVienMoiLoat;
        private final short goc;
        private final byte luc;
        private final byte lucPhu;

        private KetQua(
                byte loaiDan,
                byte soVienMoiLoat,
                short goc,
                byte luc,
                byte lucPhu
        ) {
            this.loaiDan = loaiDan;
            this.soVienMoiLoat = soVienMoiLoat;
            this.goc = goc;
            this.luc = luc;
            this.lucPhu = lucPhu;
        }

        public byte getLoaiDan() { return this.loaiDan; }
        public byte getSoVienMoiLoat() { return this.soVienMoiLoat; }
        public short getGoc() { return this.goc; }
        public byte getLuc() { return this.luc; }
        public byte getLucPhu() { return this.lucPhu; }
    }
}
