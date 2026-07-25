package com.chicken.chien;

import com.chicken.avg.ChickenThanhDiChuyenAVG;
import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chiso.ChickenKichThuocNhanVat;
import com.chicken.luyentap.ChickenLuyenTapToaDo;

/**
 * Chot toa do di chuyen chu dong o server.
 *
 * Nhan vat bay duoc phep gui dich X/Y, nhung van bi gioi han boi thanh di chuyen.
 * Nhan vat di bo chi gui huong X; Y va duong di tren dia hinh do server tinh.
 */
public final class ChickenDiChuyenServer {

    private static final int DO_CAO_BUOC_LEN_TOI_DA = 8;
    private static final int NUA_RONG_HITBOX =
            ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
    private static final int CAO_HITBOX =
            ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_TREN;
    /** Phan chan client co the chen vao mat doc ma khong bi coi la xuyen tuong. */
    private static final int VUNG_CHAN_BAM_DOC = 12;

    private ChickenDiChuyenServer() {
    }

    public static KetQua xuLy(
            ChickenQuanLyBanDo banDo,
            short xHienTai,
            short yHienTai,
            short xClient,
            short yClient,
            int quangDuongConLai,
            boolean duocPhepBay
    ) {
        if (banDo == null) {
            return new KetQua(
                    xHienTai, yHienTai, Math.max(0, quangDuongConLai), false);
        }
        int xToiDa = Math.max(0, banDo.getWidth() - 1);
        int yToiDa = Math.max(0, banDo.getHeight() - 1);
        short xMucTieu = kepShort(xClient, 0, xToiDa);

        if (duocPhepBay) {
            short yMucTieu = kepShort(yClient, 0, yToiDa);
            ChickenThanhDiChuyenAVG.KetQuaDiChuyen ketQua =
                    ChickenThanhDiChuyenAVG.gioiHan(
                            xHienTai, yHienTai, xMucTieu, yMucTieu,
                            quangDuongConLai);
            return new KetQua(
                    ketQua.getX(), ketQua.getY(), ketQua.getConLai(), false);
        }

        // Nhan vat di bo khong duoc quyet dinh Y. Gioi han dich X truoc, sau do
        // server lan theo tung cot dia hinh de khong the nhay qua tuong/ho sau.
        ChickenThanhDiChuyenAVG.KetQuaDiChuyen gioiHanNgang =
                ChickenThanhDiChuyenAVG.gioiHan(
                        xHienTai, yHienTai, xMucTieu, yHienTai,
                        quangDuongConLai);
        short xGioiHan = gioiHanNgang.getX();
        int huong = Integer.compare(xGioiHan, xHienTai);
        if (huong == 0) {
            return new KetQua(
                    xHienTai, yHienTai, Math.max(0, quangDuongConLai), false);
        }

        short xServer = xHienTai;
        short yServer = yHienTai;
        int soBuoc = 0;
        boolean biDiaHinhChan = false;
        while (xServer != xGioiHan) {
            short xKeTiep = (short) (xServer + huong);
            short yKeTiep = timMatDatChoBuocKeTiep(banDo, xKeTiep, yServer);
            if (yKeTiep == Short.MIN_VALUE) {
                biDiaHinhChan = true;
                break;
            }
            xServer = xKeTiep;
            yServer = yKeTiep;
            soBuoc++;
        }

        return new KetQua(
                xServer,
                yServer,
                Math.max(0, quangDuongConLai - soBuoc),
                biDiaHinhChan
        );
    }

    private static short timMatDatChoBuocKeTiep(
            ChickenQuanLyBanDo banDo,
            short x,
            short yHienTai
    ) {
        short batDauY = kepShort(
                (int) yHienTai - DO_CAO_BUOC_LEN_TOI_DA,
                0,
                Math.max(0, banDo.getHeight() - 1)
        );
        short matDatGanNhat = ChickenLuyenTapToaDo.timMatDatTaiHoacThapHon(
                banDo,
                x,
                batDauY,
                (toaDoX, chanY) -> true
        );
        if (matDatGanNhat == Short.MIN_VALUE
                || !thanThongThoang(banDo, x, matDatGanNhat)) {
            return Short.MIN_VALUE;
        }
        return matDatGanNhat;
    }

    private static boolean thanThongThoang(
            ChickenQuanLyBanDo banDo,
            short x,
            short chanY
    ) {
        // Quet kin phan than theo hitbox client x +/-12, y-36. Khong quet 12 px
        // sat chan: day la vung sprite chen vao mat doc khi nhan vat bam dia hinh.
        // Tuong mong nam cao hon vung chan van bi chan du chi day dung 1 pixel.
        for (int px = x - NUA_RONG_HITBOX; px <= x + NUA_RONG_HITBOX; px++) {
            for (int py = chanY - CAO_HITBOX;
                    py < chanY - VUNG_CHAN_BAM_DOC;
                    py++) {
                if (px >= 0 && px < banDo.getWidth()
                        && py >= 0 && py < banDo.getHeight()
                        && banDo.coVaCham((short) px, (short) py)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static short kepShort(int giaTri, int min, int max) {
        return (short) Math.max(min, Math.min(max, giaTri));
    }

    public static final class KetQua {
        private final short x;
        private final short y;
        private final int conLai;
        private final boolean biDiaHinhChan;

        private KetQua(short x, short y, int conLai, boolean biDiaHinhChan) {
            this.x = x;
            this.y = y;
            this.conLai = conLai;
            this.biDiaHinhChan = biDiaHinhChan;
        }

        public short getX() {
            return this.x;
        }

        public short getY() {
            return this.y;
        }

        public int getConLai() {
            return this.conLai;
        }

        public boolean isBiDiaHinhChan() {
            return this.biDiaHinhChan;
        }
    }
}
