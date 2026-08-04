package com.chicken.chien;

import com.chicken.avg.ChickenThanhDiChuyenAVG;
import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chiso.ChickenKichThuocNhanVat;

/**
 * Chot toa do di chuyen chu dong o server.
 *
 * Nhan vat bay duoc phep gui dich X/Y, nhung van bi gioi han boi thanh di chuyen.
 * Nhan vat di bo chi gui huong X; Y va duong di tren dia hinh do server tinh.
 */
public final class ChickenDiChuyenServer {

    /** CPlayer.move() chi cho phep buoc len toi da 4 pixel trong mot nhip. */
    private static final int DO_CAO_BUOC_LEN_TOI_DA = 4;
    private static final int CAO_HITBOX =
            ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_TREN;
    /** CPlayer.move() cua client kiem tra va cham tai y - 5 truoc khi buoc. */
    private static final int LECH_DIEM_VA_CHAM_DI_BO_CLIENT = 5;
    /**
     * CPlayer van giu van toc ngang trong luc roi, nen co the vuot qua mot khe
     * nho giua hai manh dia hinh. Server quet tung cot ma ket luan vuc ngay o
     * cot trong dau tien se giet nguoi choi du client da dap xuong manh ke tiep.
     * Gioi han nay chi cho phep khe nho dung voi vat ly client, khong cho buoc
     * qua mot vuc rong.
     */
    private static final int KHE_NHO_CO_THE_VUOT_TOI_DA = 16;

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
        int soCotTrongLienTiep = 0;
        boolean biDiaHinhChan = false;
        boolean roiKhoiNen = false;
        while (xServer != xGioiHan) {
            short xKeTiep = (short) (xServer + huong);
            short yDiemChanClient = kepShort(
                    (int) yServer - LECH_DIEM_VA_CHAM_DI_BO_CLIENT,
                    0,
                    Math.max(0, banDo.getHeight() - 1)
            );
            // Khop dung CPlayer.move(): client chi chan buoc khi diem (x, y-5)
            // la dia hinh. Quet ca hinh chu nhat sprite tai day lam nhan vat bi
            // ket tren doc va khong the roi vao phan map vua bi duc.
            if (banDo.coVaCham(xKeTiep, yDiemChanClient)
                    || coVatCanThanTaiCot(banDo, xKeTiep, yServer)) {
                biDiaHinhChan = true;
                break;
            }
            short yKeTiep = timMatDatChoBuocKeTiep(banDo, xKeTiep, yServer);
            if (yKeTiep == Short.MIN_VALUE) {
                // Chua ket luan la vuc o cot trong dau tien: client co the roi
                // ngang qua khe nho va dap vao manh dia hinh ke tiep.
                xServer = xKeTiep;
                soBuoc++;
                soCotTrongLienTiep++;
                if (soCotTrongLienTiep > KHE_NHO_CO_THE_VUOT_TOI_DA) {
                    roiKhoiNen = true;
                    break;
                }
                continue;
            }
            xServer = xKeTiep;
            yServer = yKeTiep;
            soBuoc++;
            soCotTrongLienTiep = 0;
        }

        /*
         * Chi duoc coi la "vuot khe nho" khi da tim thay nen o dau ben kia.
         * Neu packet dung ngay giua cac cot trong, client dang roi. Danh dau
         * roi de lop tran khong gui CMD 53 voi Y cu keo nhan vat nguoc len.
         */
        if (!biDiaHinhChan && soCotTrongLienTiep > 0) {
            roiKhoiNen = true;
        }

        return new KetQua(
                xServer,
                yServer,
                Math.max(0, quangDuongConLai - soBuoc),
                biDiaHinhChan,
                roiKhoiNen
        );
    }

    private static short timMatDatChoBuocKeTiep(
            ChickenQuanLyBanDo banDo,
            short x,
            short yHienTai
    ) {
        int batDauY = Math.max(0,
                (int) yHienTai - DO_CAO_BUOC_LEN_TOI_DA);
        // CPlayer va cham bang dung cot tam x. Ban cu quet them x +/-2 va hai
        // chan x +/-8, nen mot mep bang ben canh cung bi coi la nen va server
        // keo nhan vat len cao hon hinh client.
        for (int y = batDauY; y < banDo.getHeight(); y++) {
            if (banDo.coVaCham(x, (short) y)) {
                return (short) y;
            }
        }
        return Short.MIN_VALUE;
    }

    /**
     * Chống packet đi xuyên vách bằng cách quét đúng cột X đang bước tới.
     * Không nới thành hình chữ nhật x +/-12: sprite client chỉ va chạm theo
     * cột tâm, và cách quét rộng cũ coi mép nền bên cạnh là tường rồi nâng
     * nhân vật khỏi vùng lõm.
     */
    private static boolean coVatCanThanTaiCot(
            ChickenQuanLyBanDo banDo,
            short x,
            short chanY
    ) {
        int tuY = Math.max(0, chanY - CAO_HITBOX);
        int denY = Math.min(banDo.getHeight() - 1,
                chanY - LECH_DIEM_VA_CHAM_DI_BO_CLIENT);
        for (int y = tuY; y <= denY; y++) {
            if (banDo.coVaCham(x, (short) y)) {
                return true;
            }
        }
        return false;
    }

    private static short kepShort(int giaTri, int min, int max) {
        return (short) Math.max(min, Math.min(max, giaTri));
    }

    public static final class KetQua {
        private final short x;
        private final short y;
        private final int conLai;
        private final boolean biDiaHinhChan;
        private final boolean roiKhoiNen;

        private KetQua(short x, short y, int conLai, boolean biDiaHinhChan) {
            this(x, y, conLai, biDiaHinhChan, false);
        }

        private KetQua(
                short x,
                short y,
                int conLai,
                boolean biDiaHinhChan,
                boolean roiKhoiNen
        ) {
            this.x = x;
            this.y = y;
            this.conLai = conLai;
            this.biDiaHinhChan = biDiaHinhChan;
            this.roiKhoiNen = roiKhoiNen;
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

        /** Nhan vat di bo da buoc vao cot khong con bat ky nen nao ben duoi. */
        public boolean isRoiKhoiNen() {
            return this.roiKhoiNen;
        }
    }
}
