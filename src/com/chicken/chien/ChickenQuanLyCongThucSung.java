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
        dangKy(392, (byte) 0, KieuCongThuc.DAN_THUONG, 80, 100, 0);
        dangKy(393, (byte) 0, KieuCongThuc.DAN_THUONG, 80, 100, 0);
        dangKy(394, (byte) 1, KieuCongThuc.DAN_THUONG, 50, 50, 0);
        dangKy(395, (byte) 7, KieuCongThuc.BOOMERANG_QUAY_VE, 10, 50, 0);
        dangKy(396, (byte) 1, KieuCongThuc.DAN_THUONG, 50, 50, 0);
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

    private static KetQuaQuyDao taoQuyDaoTheoCongThuc(short batDauX, short batDauY,
            short goc, byte luc, byte windX, byte windY, CongThucSung congThuc,
            KiemTraBanDo banDo) {
        switch (congThuc.getKieu()) {
            case BOOMERANG_QUAY_VE:
                return taoDanFixedPoint(batDauX, batDauY, goc, luc, windX, windY,
                        congThuc, banDo, true);
            case LAZER_RIENG:
                return taoLazerRieng(batDauX, batDauY, goc, luc, windX, windY,
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
     * Công thức riêng của lazer:
     * 1) tính ngầm điểm cao nhất theo góc/lực, không hiển thị cục lazer đầu;
     * 2) chỉ hiển thị tia lazer từ đầu nòng tới điểm cao nhất;
     * 3) tại điểm cao nhất, tia đảo hướng và đi xuống với đúng góc bắn ban đầu;
     *    ví dụ 20 độ phản xuống 20 độ, 50 độ phản xuống 50 độ,
     *    90 độ đi thẳng lên sẽ phản thẳng xuống;
     * 4) chỉ đoạn tia phản lại mới được xét va chạm.
     */
    private static KetQuaQuyDao taoLazerRieng(short batDauX, short batDauY,
            short goc, byte luc, byte windX, byte windY, CongThucSung congThuc,
            KiemTraBanDo banDo) {
        int angle = chuanHoaGoc(goc);
        int power = Math.max(1, luc & 0xFF) + congThuc.getLucCongThem();
        int dx = (power * cos1024(angle)) >> 10;
        int dy = -((power * sin1024(angle)) >> 10);
        int x = batDauX;
        int y = batDauY;
        int accX = windX * congThuc.getHeSoGio() / 100;
        int accY = windY * congThuc.getHeSoGio() / 100;
        int carryX = 0;
        int carryY = 0;
        int carryGravity = 0;

        short dinhX = batDauX;
        short dinhY = batDauY;
        int minY = batDauY;

        // Chỉ tính ngầm đường bay để tìm điểm cao nhất.
        // Không đưa các tọa độ này vào mảng hiển thị nên cục lazer đầu đã bị bỏ.
        boolean daDenDinh = false;
        for (int step = 0; step < congThuc.getSoBuocToiDa() && !daDenDinh; step++) {
            int nx = x + dx;
            int ny = y + dy;
            short px = (short) nx;
            short py = (short) ny;

            if (raNgoai(px, py, banDo)) {
                break;
            }

            x = nx;
            y = ny;
            if (ny < minY) {
                minY = ny;
                dinhX = px;
                dinhY = py;
            }

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

            if (dy >= 0) {
                daDenDinh = true;
            }
        }

        // Chỉ dùng tia lazer thứ hai: đi nhanh từ đầu nòng tới điểm cao nhất.
        // Đoạn này chỉ hiển thị, không va chạm và không gây sát thương.
        short[][] tiaNoi = taoDoanThangKhongVaCham(
                batDauX, batDauY, dinhX, dinhY, 8.0D);

        // Điểm C luôn nằm chính giữa A và B theo phương ngang, đồng thời
        // hai đoạn lazer có cùng độ dài: AC = BC.
        // Vì B nằm cùng độ cao với A nên B là ảnh đối xứng của A qua
        // đường thẳng đứng đi qua C:
        //      xB = 2*xC - xA, yB = yA.
        // Khi C càng gần ngay trên A thì B càng gần A; nếu C nằm đúng
        // trên A (góc 90 độ) thì B trùng A và tia dội thẳng xuống.
        int diemBXInt = 2 * (int) dinhX - (int) batDauX;
        short diemBX = (short) Math.max(Short.MIN_VALUE,
                Math.min(Short.MAX_VALUE, diemBXInt));
        short diemBY = batDauY;

        // B chỉ dùng làm điểm xác định hướng dội từ C, không phải điểm kết thúc.
        // Tia phản xạ được phép đi xa hơn độ dài AC và toàn bộ phần kéo dài
        // vẫn xét va chạm map, va chạm nhân vật và sát thương.
        List<Short> tiaBeX = new ArrayList<>();
        List<Short> tiaBeY = new ArrayList<>();
        List<Short> vaChamBeX = new ArrayList<>();
        List<Short> vaChamBeY = new ArrayList<>();

        double huongX = (double) diemBX - dinhX;
        double huongY = (double) diemBY - dinhY;
        double doDaiHuong = Math.hypot(huongX, huongY);
        if (doDaiHuong < 0.0001D) {
            // Trường hợp C nằm thẳng trên A: tia dội thẳng xuống.
            huongX = 0.0D;
            huongY = 1.0D;
            doDaiHuong = 1.0D;
        }

        double buocX = huongX / doDaiHuong * 6.0D;
        double buocY = huongY / doDaiHuong * 6.0D;
        double hienTaiX = dinhX;
        double hienTaiY = dinhY;
        short truocX = dinhX;
        short truocY = dinhY;

        // Không giới hạn BC bằng AC. Tia tiếp tục tới khi chạm vật cản,
        // ra ngoài bản đồ hoặc đạt giới hạn bước an toàn của công thức súng.
        for (int i = 0; i < congThuc.getSoBuocToiDa(); i++) {
            hienTaiX += buocX;
            hienTaiY += buocY;
            short px = (short) Math.round(hienTaiX);
            short py = (short) Math.round(hienTaiY);

            if (raNgoai(px, py, banDo)) {
                break;
            }

            short[] vaCham = timVaChamDauTienTrenDoan(
                    truocX, truocY, px, py, banDo);
            if (vaCham != null) {
                tiaBeX.add(vaCham[0]);
                tiaBeY.add(vaCham[1]);
                vaChamBeX.add(vaCham[0]);
                vaChamBeY.add(vaCham[1]);
                break;
            }

            tiaBeX.add(px);
            tiaBeY.add(py);
            vaChamBeX.add(px);
            vaChamBeY.add(py);
            truocX = px;
            truocY = py;
        }

        short[] hienThiX = noiMang(tiaNoi[0], doiMang(tiaBeX));
        short[] hienThiY = noiMang(tiaNoi[1], doiMang(tiaBeY));

        // Mảng va chạm bao phủ toàn bộ tia phản lại C -> B.
        short[] vaChamX = doiMang(vaChamBeX);
        short[] vaChamY = doiMang(vaChamBeY);
        return new KetQuaQuyDao(hienThiX, hienThiY, vaChamX, vaChamY);
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

            if (raNgoai(px, py, banDo)) {
                break;
            }

            short[] vaCham = timVaChamDauTienTrenDoan((short)x, (short)y, px, py, banDo);
            if (vaCham != null) {
                xs.add(vaCham[0]);
                ys.add(vaCham[1]);
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

    private static short[][] taoDoanThangKhongVaCham(short x1, short y1,
            short x2, short y2, double khoangCachDiem) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length < 1.0D) {
            return new short[][]{new short[]{x1}, new short[]{y1}};
        }
        double buoc = Math.max(1.0D, khoangCachDiem);
        int count = Math.max(1, (int) Math.ceil(length / buoc));
        short[] xs = new short[count + 1];
        short[] ys = new short[count + 1];
        for (int i = 0; i <= count; i++) {
            double t = (double) i / count;
            xs[i] = (short) Math.round(x1 + dx * t);
            ys[i] = (short) Math.round(y1 + dy * t);
        }
        return new short[][]{xs, ys};
    }

    private static short[] noiMang(short[] a, short[] b) {
        short[] result = new short[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
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

    private static boolean raNgoai(short x, short y, KiemTraBanDo banDo) {
        /*
         * Y âm chỉ có nghĩa là viên đạn đang bay phía trên phần ảnh map.
         * Không được kết thúc quỹ đạo tại mép trên vì client sẽ coi điểm cuối
         * đó là một va chạm/nổ giữa không trung. Chỉ kết thúc khi đạn ra khỏi
         * cạnh trái/phải hoặc rơi xuống dưới map.
         */
        return x < 0 || x >= banDo.getWidth() || y >= banDo.getHeight();
    }

    private static int chuanHoaGoc(short goc) {
        int result = goc % 360;
        return result < 0 ? result + 360 : result;
    }
}
