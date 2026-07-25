package com.chicken.chien;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.luyentap.ChickenLuyenTapToaDo;
import com.chicken.mang.ChickenTinNhan;

/**
 * Doc y dinh toa do tu client, nhung chi server moi duoc quyet dinh toa do that.
 *
 * CMD 21 mang dich den cua thao tac di chuyen chu dong. CMD 53 chi la thong bao
 * client da ket thuc animation roi; hai toa do trong CMD 53 khong phai bang chung
 * ve vi tri va tuyet doi khong duoc ghi thang vao trang thai tran.
 */
public final class ChickenYeuCauToaDoServer {

    private static final int DO_DAI_PACKET_TOA_DO = 4;
    private static final long KHOANG_CACH_CMD_53_MS = 100L;
    private static final int[] MAU_X_THAN = new int[]{-10, 0, 10};
    private static final int[] MAU_Y_THAN = new int[]{-8, -22, -36};

    private ChickenYeuCauToaDoServer() {
    }

    /** Chi chap nhan dung hai signed short, khong nhan packet thieu hoac co byte du. */
    public static ToaDo doc(ChickenTinNhan tinNhan) {
        if (tinNhan == null) {
            return null;
        }
        byte[] duLieu = tinNhan.layDuLieu();
        if (duLieu == null || duLieu.length != DO_DAI_PACKET_TOA_DO) {
            return null;
        }
        return new ToaDo(docShort(duLieu, 0), docShort(duLieu, 2));
    }

    /** Giới hạn riêng CMD 53 để packet giả không ép server quét map và broadcast liên tục. */
    public static boolean choPhepDongBo(ChickenChienBinh chienBinh, long hienTaiMs) {
        if (chienBinh == null) {
            return false;
        }
        synchronized (chienBinh) {
            long daQua = hienTaiMs - chienBinh.lanDongBoToaDoGanNhat;
            if (chienBinh.lanDongBoToaDoGanNhat != 0L
                    && daQua >= 0L && daQua < KHOANG_CACH_CMD_53_MS) {
                return false;
            }
            chienBinh.lanDongBoToaDoGanNhat = hienTaiMs;
            return true;
        }
    }

    /**
     * Xu ly CMD 53 theo map va toa do dang luu tren server.
     *
     * X/Y trong packet chi duoc doc de kiem tra cau truc protocol. X server luon
     * duoc giu nguyen; Y chi co the roi xuong mat dat that nam ben duoi. Nhan vat
     * bay khong chiu trong luc nen giu nguyen ca X/Y. Lenh thu dong nay duoc phep
     * den ngoai luot de xu ly nguoi bi pha nen; vi no khong the di chuyen ngang
     * hoac bay len nen khong tao duoc loi the cho client sua.
     */
    public static KetQuaDongBo dongBoThuDong(
            ChickenTinNhan tinNhan,
            ChickenQuanLyBanDo banDo,
            short xServer,
            short yServer,
            boolean duocPhepBay
    ) {
        if (doc(tinNhan) == null || banDo == null) {
            return null;
        }
        if (duocPhepBay) {
            return new KetQuaDongBo(xServer, yServer, false, false);
        }
        if (xServer < 0 || xServer >= banDo.getWidth()
                || yServer < 0 || yServer >= banDo.getHeight()) {
            return new KetQuaDongBo(xServer, yServer, false, true);
        }

        short matDat = ChickenLuyenTapToaDo.timMatDatTaiHoacThapHon(
                banDo,
                xServer,
                yServer,
                (x, y) -> thanThongThoang(banDo, x, y)
        );
        if (matDat == Short.MIN_VALUE) {
            return new KetQuaDongBo(xServer, yServer, false, true);
        }
        return new KetQuaDongBo(
                xServer,
                matDat,
                matDat != yServer,
                false
        );
    }

    private static boolean thanThongThoang(
            ChickenQuanLyBanDo banDo,
            short x,
            short chanY
    ) {
        for (int dx : MAU_X_THAN) {
            for (int dy : MAU_Y_THAN) {
                int px = x + dx;
                int py = chanY + dy;
                if (px >= 0 && px < banDo.getWidth()
                        && py >= 0 && py < banDo.getHeight()
                        && banDo.coVaCham((short) px, (short) py)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static short docShort(byte[] duLieu, int viTri) {
        return (short) (((duLieu[viTri] & 0xFF) << 8)
                | (duLieu[viTri + 1] & 0xFF));
    }

    public static final class ToaDo {
        private final short x;
        private final short y;

        private ToaDo(short x, short y) {
            this.x = x;
            this.y = y;
        }

        public short getX() {
            return this.x;
        }

        public short getY() {
            return this.y;
        }
    }

    public static final class KetQuaDongBo {
        private final short x;
        private final short y;
        private final boolean daRoi;
        private final boolean khongCoNen;

        private KetQuaDongBo(short x, short y, boolean daRoi, boolean khongCoNen) {
            this.x = x;
            this.y = y;
            this.daRoi = daRoi;
            this.khongCoNen = khongCoNen;
        }

        public short getX() {
            return this.x;
        }

        public short getY() {
            return this.y;
        }

        public boolean isDaRoi() {
            return this.daRoi;
        }

        public boolean isKhongCoNen() {
            return this.khongCoNen;
        }
    }
}
