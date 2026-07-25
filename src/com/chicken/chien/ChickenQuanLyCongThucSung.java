package com.chicken.chien;

import com.chicken.avg.ChickenCongThucBanUltron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Quản lý duy nhất công thức bay của từng nhóm súng.
 *
 * Hệ số vật lý được đối chiếu từ công thức client Army3:
 *  - vận tốc đầu dùng sin/cos fixed-point 1024;
 *  - gió và trọng lực được cộng dồn theo đơn vị phần trăm;
 *  - mỗi khi phần dư đạt 100 mới đổi vận tốc 1 pixel/bước.
 */
public final class ChickenQuanLyCongThucSung {

    public static final int ID_SUNG_CAPTAIN = 395;
    public static final int ID_SUNG_WINTER_SOLDIER = 396;
    public static final int ID_SUNG_HULK = 392;

    public enum KieuCongThuc {
        DAN_THUONG,
        BOOMERANG_QUAY_VE,
        LAZER_RIENG,
        ULTRON_LAZER_THANG,
    }

    public static final class CongThucSung {
        private final int idSung;
        private final byte nhomSung;
        private final KieuCongThuc kieu;
        private final int heSoGio;
        private final int trongLuc;
        private final int lucCongThem;
        private final int soBuocToiDa;

        private CongThucSung(int idSung, byte nhomSung, KieuCongThuc kieu,
                int heSoGio, int trongLuc, int lucCongThem, int soBuocToiDa) {
            this.idSung = idSung;
            this.nhomSung = nhomSung;
            this.kieu = kieu;
            this.heSoGio = heSoGio;
            this.trongLuc = trongLuc;
            this.lucCongThem = lucCongThem;
            this.soBuocToiDa = soBuocToiDa;
        }

        public int getIdSung() { return this.idSung; }
        public byte getNhomSung() { return this.nhomSung; }
        public KieuCongThuc getKieu() { return this.kieu; }
        public int getHeSoGio() { return this.heSoGio; }
        public int getTrongLuc() { return this.trongLuc; }
        public int getLucCongThem() { return this.lucCongThem; }
        public int getSoBuocToiDa() { return this.soBuocToiDa; }
        /** Khiên Captain vẫn chạm đất nhưng không bị nhân vật chặn đường bay. */
        public boolean xuyenNguoi() { return this.idSung == ID_SUNG_CAPTAIN; }
        /** Đạn Winter Soldier bỏ qua toàn bộ pixel địa hình. */
        public boolean xuyenBanDo() { return this.idSung == ID_SUNG_WINTER_SOLDIER; }
    }

    public static final class KetQuaQuyDao {
        private final short[] hienThiX;
        private final short[] hienThiY;
        private final short[] vaChamX;
        private final short[] vaChamY;

        private KetQuaQuyDao(short[] hienThiX, short[] hienThiY,
                short[] vaChamX, short[] vaChamY) {
            this.hienThiX = hienThiX;
            this.hienThiY = hienThiY;
            this.vaChamX = vaChamX;
            this.vaChamY = vaChamY;
        }

        public short[] getHienThiX() { return this.hienThiX; }
        public short[] getHienThiY() { return this.hienThiY; }
        public short[] getVaChamX() { return this.vaChamX; }
        public short[] getVaChamY() { return this.vaChamY; }
    }

    public interface KiemTraBanDo {
        int getWidth();
        int getHeight();
        boolean coVaCham(short x, short y);
    }

    private static final int FIXED_TRIG = 1024;
    private static final int MAX_STEPS = 200;
    /** Khớp biên mô phỏng client/plugin: đạn được bay ngoài cạnh map rồi có thể vòng lại. */
    public static final int BIEN_NGANG_NGOAI_MAP = 120;
    /** Độ sâu an toàn dưới đáy map dành cho quỹ đạo đạn thông thường. */
    public static final int BIEN_DUOI_NGOAI_MAP = 80;
    private static final int BIEN_NGANG_LAZER = 100;
    private static final Map<Integer, CongThucSung> THEO_ID_SUNG = new LinkedHashMap<>();

    private static final CongThucSung MAC_DINH =
            new CongThucSung(-1, (byte) 0, KieuCongThuc.DAN_THUONG,
                    80, 100, 0, MAX_STEPS);

    static {
        // gun 0: CANNON, wind=80, gravity=100
        dangKyKhoang(110, 119, (byte) 0, KieuCongThuc.DAN_THUONG, 80, 100, 0);
        // gun 1: AK, wind=50, gravity=50
        dangKyKhoang(120, 129, (byte) 1, KieuCongThuc.DAN_THUONG, 50, 50, 0);
        // gun 5: MG42, wind=30, gravity=90
        dangKyKhoang(130, 139, (byte) 5, KieuCongThuc.DAN_THUONG, 30, 90, 0);
        // gun 3: CHUOI_MULTI, wind=40, gravity=90
        dangKyKhoang(140, 149, (byte) 3, KieuCongThuc.DAN_THUONG, 40, 90, 0);
        // gun 2: PROTON_MULTI, wind=80, gravity=60
        dangKyKhoang(150, 159, (byte) 2, KieuCongThuc.DAN_THUONG, 80, 60, 0);
        // gun 4: ROCKET, wind=50, gravity=80
        dangKyKhoang(160, 169, (byte) 4, KieuCongThuc.DAN_THUONG, 50, 80, 0);
        // gun 6: CHICKEN, wind=20, gravity=50
        dangKyKhoang(170, 179, (byte) 6, KieuCongThuc.DAN_THUONG, 20, 50, 0);
        // gun 8: HAMMER, wind=30, gravity=100
        dangKyKhoang(180, 189, (byte) 8, KieuCongThuc.DAN_THUONG, 30, 100, 0);
        // gun 7: BOOMERANG, wind=10, gravity=50
        dangKyKhoang(190, 199, (byte) 7, KieuCongThuc.BOOMERANG_QUAY_VE, 10, 50, 0);
        // gun 9: LASER, wind=40, gravity=70, extraPower=5
        dangKyKhoang(200, 209, (byte) 9, KieuCongThuc.LAZER_RIENG, 40, 70, 5);

        dangKy(295, (byte) 0, KieuCongThuc.DAN_THUONG, 80, 100, 0);
        dangKy(391, (byte) 1, KieuCongThuc.DAN_THUONG, 50, 50, 0);
        // CPlayer.drawBull(): gun 12 (AVG Hulk) = wind 0, gravity 100.
        // Hulk chi dung chung dang dan dao, khong dung cong thuc AT gun 0.
        dangKy(ID_SUNG_HULK, (byte) 0, KieuCongThuc.DAN_THUONG, 0, 100, 0);
        dangKy(393, (byte) 0, KieuCongThuc.DAN_THUONG, 80, 100, 0);
        dangKy(394, (byte) 1, KieuCongThuc.DAN_THUONG, 50, 50, 0);
        dangKy(ID_SUNG_CAPTAIN, (byte) 7, KieuCongThuc.BOOMERANG_QUAY_VE, 10, 50, 0);
        dangKy(ID_SUNG_WINTER_SOLDIER, (byte) 1, KieuCongThuc.DAN_THUONG, 50, 50, 0);
        dangKy(397, (byte) 3, KieuCongThuc.DAN_THUONG, 40, 90, 0);
        dangKy(398, (byte) 0, KieuCongThuc.ULTRON_LAZER_THANG, 0, 0, 0);
        dangKy(400, (byte) 3, KieuCongThuc.DAN_THUONG, 40, 90, 0);
        dangKy(401, (byte) 3, KieuCongThuc.DAN_THUONG, 40, 90, 0);
    }

    private ChickenQuanLyCongThucSung() {
    }

    private static void dangKyKhoang(int tuId, int denId, byte nhomSung,
            KieuCongThuc kieu, int heSoGio, int trongLuc, int lucCongThem) {
        for (int id = tuId; id <= denId; id++) {
            dangKy(id, nhomSung, kieu, heSoGio, trongLuc, lucCongThem);
        }
    }

    private static void dangKy(int idSung, byte nhomSung, KieuCongThuc kieu,
            int heSoGio, int trongLuc, int lucCongThem) {
        THEO_ID_SUNG.put(idSung, new CongThucSung(idSung, nhomSung, kieu,
                heSoGio, trongLuc, lucCongThem, MAX_STEPS));
    }

    public static CongThucSung theoIdSung(int idSung) {
        CongThucSung result = THEO_ID_SUNG.get(idSung);
        return result != null ? result : MAC_DINH;
    }

    public static CongThucSung theoPartSung(short partSung) {
        ChickenQuanLyDanSung.DuLieuSung duLieu = ChickenQuanLyDanSung.theoPartSung(partSung);
        return duLieu == null ? MAC_DINH : theoIdSung(duLieu.getIdSung());
    }

    public static Map<Integer, CongThucSung> layTatCa() {
        return Collections.unmodifiableMap(THEO_ID_SUNG);
    }

    public static KetQuaQuyDao taoQuyDao(short batDauX, short batDauY,
            short goc, byte luc, short partSung, byte sucGio,
            KiemTraBanDo banDo) {
        return taoQuyDao(batDauX, batDauY, goc, luc, partSung, sucGio, (byte) 0, banDo);
    }

    public static KetQuaQuyDao taoQuyDao(short batDauX, short batDauY,
            short goc, byte luc, short partSung, byte windX, byte windY,
            KiemTraBanDo banDo) {
        return taoQuyDaoTheoCongThuc(batDauX, batDauY, goc, luc, windX, windY,
                theoPartSung(partSung), banDo);
    }

    public static KetQuaQuyDao taoQuyDaoTheoIdSung(short batDauX, short batDauY,
            short goc, byte luc, int idSung, byte sucGio,
            KiemTraBanDo banDo) {
        return taoQuyDaoTheoIdSung(batDauX, batDauY, goc, luc, idSung, sucGio, (byte) 0, banDo);
    }

    public static KetQuaQuyDao taoQuyDaoTheoIdSung(short batDauX, short batDauY,
            short goc, byte luc, int idSung, byte windX, byte windY,
            KiemTraBanDo banDo) {
        return taoQuyDaoTheoCongThuc(batDauX, batDauY, goc, luc, windX, windY,
                theoIdSung(idSung), banDo);
    }

    /**
     * Quỹ đạo viên phụ của nhóm Gà (bullet type 20).
     *
     * Client sinh viên này tại bước force2 của viên chính, vận tốc ban đầu bằng 0,
     * hệ số gió 10 và trọng lực 30. Điểm bắt đầu đã gồm độ lệch Y +8 do bên gọi
     * xác định từ quỹ đạo viên chính.
     */
    public static KetQuaQuyDao taoQuyDaoDanGaRoi(short batDauX, short batDauY,
            byte windX, byte windY, KiemTraBanDo banDo) {
        List<Short> xs = new ArrayList<>();
        List<Short> ys = new ArrayList<>();

        if (banDo.coVaCham(batDauX, batDauY)) {
            return ketQuaGiongNhau(xs, ys, batDauX, batDauY);
        }

        int x = batDauX;
        int y = batDauY;
        int dx = 0;
        int dy = 0;
        int accX = windX * 10 / 100;
        int accY = windY * 10 / 100;
        int carryX = 0;
        int carryY = 0;
        int carryGravity = 0;

        for (int step = 1; step <= MAX_STEPS; step++) {
            int nx = x + dx;
            int ny = y + dy;
            short px = (short) nx;
            short py = (short) ny;
            short[] vaCham = timVaChamDauTienTrenDoan(
                    (short) x, (short) y, px, py, banDo);
            if (vaCham != null) {
                xs.add(vaCham[0]);
                ys.add(vaCham[1]);
                break;
            }
            if (daRaKhoiBienMoPhong(nx, ny, banDo.getWidth(), banDo.getHeight())) {
                break;
            }

            xs.add(px);
            ys.add(py);
            x = nx;
            y = ny;

            carryX += accX;
            carryY += accY;
            carryGravity += 30;
            if (Math.abs(carryX) >= 100) {
                dx += carryX / 100;
                carryX %= 100;
            }
            if (Math.abs(carryY) >= 100) {
                dy += carryY / 100;
                carryY %= 100;
            }
            if (Math.abs(carryGravity) >= 100) {
                dy += carryGravity / 100;
                carryGravity %= 100;
            }
        }

        return ketQuaGiongNhau(xs, ys, batDauX, batDauY);
    }

    /**
     * Tạo một trong ba quỹ đạo con của Rìu sau bước force2.
     *
     * Khớp client pathType 8: góc con cách nhau 15 độ, đầu nòng con cách điểm
     * tách 40 px (trục Y trừ thêm 12), vận tốc chỉ bằng một nửa phát chính,
     * hệ số gió 30 và trọng lực 100.
     */
    public static KetQuaQuyDao taoQuyDaoConRiu(
            short diemTachX,
            short diemTachY,
            short nguoiBanX,
            short nguoiBanY,
            short gocGoc,
            byte luc,
            int chiSoCon,
            byte windX,
            byte windY,
            KiemTraBanDo banDo
    ) {
        int gocCon = layGocConRiu(
                diemTachX, diemTachY, nguoiBanX, nguoiBanY, gocGoc, chiSoCon);
        int cos = cos1024(gocCon);
        int sin = sin1024(gocCon);
        int batDauX = diemTachX + ((40 * cos) >> 10);
        int batDauY = diemTachY - 12 - ((40 * sin) >> 10);
        int power = Math.max(1, luc & 0xFF);
        int dx = (power * cos) >> 11;
        int dy = -((power * sin) >> 11);
        return taoDanRiuTuVanToc(
                (short) batDauX,
                (short) batDauY,
                dx,
                dy,
                windX,
                windY,
                banDo
        );
    }

    public static int layGocConRiu(
            short diemTachX,
            short diemTachY,
            short nguoiBanX,
            short nguoiBanY,
            short gocGoc,
            int chiSoCon
    ) {
        int baseAngle = chuanHoaGoc(gocGoc);
        int dxVeNguoiBan = nguoiBanX - diemTachX;
        int dyVeNguoiBan = nguoiBanY - diemTachY;
        int gocVeNguoiBan = chuanHoaGoc((short) Math.round(
                Math.toDegrees(Math.atan2(dyVeNguoiBan, dxVeNguoiBan))));
        int gocCon = baseAngle + gocVeNguoiBan;
        if (baseAngle < 90) {
            gocCon = 180 - gocCon;
        }
        gocCon += Math.max(0, Math.min(2, chiSoCon)) * 15 - 15;
        return chuanHoaGoc((short) gocCon);
    }

    private static KetQuaQuyDao taoDanRiuTuVanToc(
            short batDauX,
            short batDauY,
            int dx,
            int dy,
            byte windX,
            byte windY,
            KiemTraBanDo banDo
    ) {
        List<Short> xs = new ArrayList<>();
        List<Short> ys = new ArrayList<>();
        int x = batDauX;
        int y = batDauY;
        int accX = windX * 30 / 100;
        int accY = windY * 30 / 100;
        int carryX = 0;
        int carryY = 0;
        int carryGravity = 0;

        for (int step = 1; step <= MAX_STEPS; step++) {
            int nx = x + dx;
            int ny = y + dy;
            short px = (short) nx;
            short py = (short) ny;
            short[] vaCham = timVaChamDauTienTrenDoan(
                    (short) x, (short) y, px, py, banDo);
            if (vaCham != null) {
                xs.add(vaCham[0]);
                ys.add(vaCham[1]);
                break;
            }
            if (daRaKhoiBienMoPhong(nx, ny, banDo.getWidth(), banDo.getHeight())) {
                break;
            }

            xs.add(px);
            ys.add(py);
            x = nx;
            y = ny;
            carryX += accX;
            carryY += accY;
            carryGravity += 100;
            if (Math.abs(carryX) >= 100) {
                dx += carryX / 100;
                carryX %= 100;
            }
            if (Math.abs(carryY) >= 100) {
                dy += carryY / 100;
                carryY %= 100;
            }
            if (Math.abs(carryGravity) >= 100) {
                dy += carryGravity / 100;
                carryGravity %= 100;
            }
        }
        return ketQuaGiongNhau(xs, ys, batDauX, batDauY);
    }

    private static KetQuaQuyDao taoQuyDaoTheoCongThuc(short batDauX, short batDauY,
            short goc, byte luc, byte windX, byte windY, CongThucSung congThuc,
            KiemTraBanDo banDo) {
        switch (congThuc.getKieu()) {
            case BOOMERANG_QUAY_VE:
                return taoDanFixedPoint(batDauX, batDauY, goc, luc, windX, windY,
                        congThuc, banDo, true);
            case LAZER_RIENG:
                return taoLazerTheoClient(batDauX, batDauY, goc, luc, windX, windY,
                        congThuc, banDo);
            case ULTRON_LAZER_THANG: {
                ChickenCongThucBanUltron.DuongTia tia =
                        ChickenCongThucBanUltron.taoTiaThang(
                                batDauX,
                                batDauY,
                                goc,
                                banDo.getWidth(),
                                banDo.getHeight()
                        );
                return new KetQuaQuyDao(
                        tia.getX(),
                        tia.getY(),
                        tia.getX(),
                        tia.getY()
                );
            }
            case DAN_THUONG:
            default:
                return taoDanFixedPoint(batDauX, batDauY, goc, luc, windX, windY,
                        congThuc, banDo, false);
        }
    }

    /**
     * PathType 9 của client: đoạn đầu bay fixed-point bằng lực bắn +5, gió 40
     * và trọng lực 70. Ngay sau đỉnh, đạn đổi sang tia thẳng lực 30 và không
     * còn chịu gió/trọng lực. Cả hai đoạn đều hiển thị và xét va chạm.
     */
    private static KetQuaQuyDao taoLazerTheoClient(short batDauX, short batDauY,
            short goc, byte luc, byte windX, byte windY, CongThucSung congThuc,
            KiemTraBanDo banDo) {
        List<Short> xs = new ArrayList<>();
        List<Short> ys = new ArrayList<>();

        int angle = chuanHoaGoc(goc);
        int lucBan = Math.max(1, Math.min(30, luc & 0xFF));
        int lucDoanDau = lucBan + congThuc.getLucCongThem();
        int dx = (lucDoanDau * cos1024(angle)) >> 10;
        int dy = -((lucDoanDau * sin1024(angle)) >> 10);
        int x = batDauX;
        int y = batDauY;
        int accX = windX * congThuc.getHeSoGio() / 100;
        int accY = windY * congThuc.getHeSoGio() / 100;
        int gravity = congThuc.getTrongLuc();
        int carryX = 0;
        int carryY = 0;
        int carryGravity = 0;
        boolean daDoiHuong = false;

        for (int step = 1; step <= congThuc.getSoBuocToiDa(); step++) {
            int nx = x + dx;
            int ny = y + dy;
            short px = (short) nx;
            short py = (short) ny;

            short[] vaCham = timVaChamDauTienTrenDoan(
                    (short) x, (short) y, px, py, banDo);
            if (vaCham != null) {
                xs.add(vaCham[0]);
                ys.add(vaCham[1]);
                break;
            }

            if (raNgoaiLazer(nx, ny, banDo)) {
                break;
            }

            xs.add(px);
            ys.add(py);

            carryX += accX;
            carryY += accY;
            carryGravity += gravity;
            if (Math.abs(carryX) >= 100) {
                dx += carryX / 100;
                carryX %= 100;
            }
            if (Math.abs(carryY) >= 100) {
                dy += carryY / 100;
                carryY %= 100;
            }
            if (Math.abs(carryGravity) >= 100) {
                dy += carryGravity / 100;
                carryGravity %= 100;
            }

            int dyBuocTiep = dy
                    + (carryY + accY) / 100
                    + (carryGravity + gravity) / 100;
            if (!daDoiHuong && dy >= 0 && dyBuocTiep > 0) {
                int gocDoiHuong = (int) Math.round(Math.toDegrees(
                        Math.atan2(batDauY - ny, nx - batDauX)));
                gocDoiHuong = chuanHoaGoc((short) gocDoiHuong);
                dx = (30 * cos1024(gocDoiHuong)) >> 10;
                dy = (30 * sin1024(gocDoiHuong)) >> 10;

                while (dx != 0 && Math.abs(dx) < 15) {
                    dx *= 2;
                    dy *= 2;
                }

                accX = 0;
                accY = 0;
                gravity = 0;
                carryX = 0;
                carryY = 0;
                carryGravity = 0;
                daDoiHuong = true;
            }

            x = nx;
            y = ny;
        }

        return ketQuaGiongNhau(xs, ys, batDauX, batDauY);
    }


    /** Công thức cập nhật đúng kiểu client: tích lũy gió/trọng lực theo phần trăm. */
    private static KetQuaQuyDao taoDanFixedPoint(short batDauX, short batDauY,
            short goc, byte luc, byte windX, byte windY, CongThucSung congThuc,
            KiemTraBanDo banDo, boolean boomerang) {
        List<Short> xs = new ArrayList<>();
        List<Short> ys = new ArrayList<>();

        int angle = chuanHoaGoc(goc);
        int power = Math.max(1, luc & 0xFF) + congThuc.getLucCongThem();
        int cos = cos1024(angle);
        int sin = sin1024(angle);
        int dx = (power * cos) >> 10;
        int dy = -((power * sin) >> 10);
        int x = batDauX;
        int y = batDauY;

        int accX = windX * congThuc.getHeSoGio() / 100;
        int accY = windY * congThuc.getHeSoGio() / 100;
        int carryX = 0;
        int carryY = 0;
        int carryGravity = 0;
        boolean curveBackRight = dx <= 0;
        int curveState = -1;

        for (int step = 1; step <= congThuc.getSoBuocToiDa(); step++) {
            int nx = x + dx;
            int ny = y + dy;
            short px = (short) nx;
            short py = (short) ny;

            short[] vaCham = congThuc.xuyenBanDo()
                    ? null
                    : timVaChamDauTienTrenDoan((short)x, (short)y, px, py, banDo);
            if (vaCham != null) {
                xs.add(vaCham[0]);
                ys.add(vaCham[1]);
                break;
            }

            if (daRaKhoiBienMoPhong(nx, ny, banDo.getWidth(), banDo.getHeight())) {
                break;
            }

            xs.add(px);
            ys.add(py);

            carryX += accX;
            carryY += accY;
            carryGravity += congThuc.getTrongLuc();

            if (Math.abs(carryX) >= 100) {
                dx += carryX / 100;
                carryX %= 100;
            }
            if (Math.abs(carryY) >= 100) {
                dy += carryY / 100;
                carryY %= 100;
            }
            if (Math.abs(carryGravity) >= 100) {
                dy += carryGravity / 100;
                carryGravity %= 100;
            }

            // Đúng nhánh pathType 7 của client: khi đạn bắt đầu rơi,
            // vận tốc ngang tăng ngược hướng để tạo vòng quay trở lại.
            if (boomerang) {
                if (curveState == 0) {
                    dx += curveBackRight ? 1 : -1;
                    curveState++;
                } else if (curveState > 0) {
                    dx += curveBackRight ? 2 : -2;
                } else if (dy > 0) {
                    curveState++;
                }
            }

            x = nx;
            y = ny;
        }

        return ketQuaGiongNhau(xs, ys, batDauX, batDauY);
    }

    private static KetQuaQuyDao ketQuaGiongNhau(List<Short> xs, List<Short> ys,
            short batDauX, short batDauY) {
        if (xs.isEmpty()) {
            xs.add(batDauX);
            ys.add(batDauY);
        }
        short[] x = doiMang(xs);
        short[] y = doiMang(ys);
        return new KetQuaQuyDao(x, y, x, y);
    }


    /**
     * Quét từng pixel theo đúng thứ tự từ điểm cũ tới điểm mới và trả về
     * pixel địa hình đầu tiên. Nhờ vậy đạn không thể bỏ qua lớp map phía trên
     * để va vào lớp pixel phía dưới khi vận tốc/gió làm khoảng cách mỗi bước lớn.
     */
    private static short[] timVaChamDauTienTrenDoan(short x1, short y1,
            short x2, short y2, KiemTraBanDo banDo) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int soBuoc = Math.max(Math.abs(dx), Math.abs(dy));
        if (soBuoc <= 0) {
            return banDo.coVaCham(x2, y2) ? new short[]{x2, y2} : null;
        }
        for (int buoc = 1; buoc <= soBuoc; buoc++) {
            int x = x1 + (int)Math.round((double)dx * buoc / soBuoc);
            int y = y1 + (int)Math.round((double)dy * buoc / soBuoc);
            if (x < 0 || x >= banDo.getWidth() || y < 0 || y >= banDo.getHeight()) {
                continue;
            }
            if (banDo.coVaCham((short)x, (short)y)) {
                return new short[]{(short)x, (short)y};
            }
        }
        return null;
    }

    private static short[] doiMang(List<Short> list) {
        short[] result = new short[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    private static int cos1024(int angle) {
        return (int) Math.round(Math.cos(Math.toRadians(angle)) * FIXED_TRIG);
    }

    private static int sin1024(int angle) {
        return (int) Math.round(Math.sin(Math.toRadians(angle)) * FIXED_TRIG);
    }

    public static boolean daRaKhoiBienMoPhong(int x, int y, int chieuRong, int chieuCao) {
        /*
         * Ngoài phần ảnh map không phải terrain. Tiếp tục mô phỏng trong một
         * khoảng đệm để gió/boomerang có thể đưa đạn quay lại bản đồ.
         */
        return x < -BIEN_NGANG_NGOAI_MAP
                || x > chieuRong + BIEN_NGANG_NGOAI_MAP
                || y > chieuCao + BIEN_DUOI_NGOAI_MAP;
    }

    private static boolean raNgoaiLazer(int x, int y, KiemTraBanDo banDo) {
        return x < -BIEN_NGANG_LAZER
                || x > banDo.getWidth() + BIEN_NGANG_LAZER
                || y > banDo.getHeight();
    }

    private static int chuanHoaGoc(short goc) {
        int result = goc % 360;
        return result < 0 ? result + 360 : result;
    }
}
