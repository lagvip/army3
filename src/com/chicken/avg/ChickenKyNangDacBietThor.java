package com.chicken.avg;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenMayMan;
import com.chicken.mang.ChickenTinNhan;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Kỹ năng đặc biệt của AVG Thor.
 *
 * Client native dùng CMD -91:
 * - action 3: mở lựa chọn "Sấm sét" của Thor.
 * - client trả action 3 và battleIndex của chính Thor.
 * - action 4: hiển thị hiệu ứng sét tại danh sách tọa độ.
 */
public final class ChickenKyNangDacBietThor {
    public static final byte AVG_THOR = 3;
    private static final byte LOAI_HIEU_UNG_SET = 0;
    private static final int LECH_TIA_GAN = 40;
    private static final int LECH_TIA_XA = 80;
    private static final long THOI_GIAN_HIEU_UNG_MS = 650L;

    private static final ScheduledExecutorService THOR_EXECUTOR =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "chicken-thor-skill");
                thread.setDaemon(true);
                return thread;
            });

    public interface DieuKhienTranDau {
        boolean daKetThuc();
        byte luotHienTai();
        void guiTiaSet(
                ChickenChienBinh thor,
                byte loaiHieuUng,
                short[] cacX,
                short[] cacY
        ) throws IOException;
        void gaySatThuong(ChickenChienBinh mucTieu, int satThuong) throws IOException;
        void sangLuot() throws IOException;
    }

    private final ChickenChienBinh[] chienBinhs;
    private final ChickenQuanLyBanDo map;
    private final DieuKhienTranDau dieuKhien;
    private long maKyNang;

    public ChickenKyNangDacBietThor(
            ChickenChienBinh[] chienBinhs,
            ChickenQuanLyBanDo map,
            DieuKhienTranDau dieuKhien
    ) {
        this.chienBinhs = chienBinhs;
        this.map = map;
        this.dieuKhien = dieuKhien;
    }

    /** Chế độ test: tới lượt Thor thì hiện lựa chọn Sấm sét. */
    public void guiTinHieuKyNangNeuCo(ChickenChienBinh thor) {
        if (thor == null
                || thor.chet
                || !thor.coPhien()
                || thor.avenger != AVG_THOR
                || thor.thorDaDungKyNang
                || this.dieuKhien.daKetThuc()) {
            return;
        }

        thor.thorDaGuiMenu = true;
        thor.nguoiChoi.dichVu.guiChonKyNangThor();
        log("TEST_MO_MENU", thor, "action=3");
    }

    /** Nhận packet client: action 3, battleIndex của chính Thor. */
    public synchronized void nhanLenh(
            ChickenChienBinh thor,
            ChickenTinNhan ms
    ) throws IOException {
        if (thor == null || ms == null) {
            System.out.println("[THOR] KHONG_TIM_THAY_THOR");
            return;
        }
        if (thor.avenger != AVG_THOR
                || thor.chet
                || thor.thorDaDungKyNang
                || !thor.thorDaGuiMenu
                || thor.chiSo != this.dieuKhien.luotHienTai()
                || this.dieuKhien.daKetThuc()) {
            log("CMD_-91_BO_QUA", thor,
                    "avenger=" + thor.avenger
                    + ", chet=" + thor.chet
                    + ", active=" + thor.thorDaDungKyNang
                    + ", luot=" + this.dieuKhien.luotHienTai());
            return;
        }

        int soByte = ms.boDoc().available();
        if (soByte != 2) {
            System.out.println("[THOR] PACKET_SAI_DO_DAI bytes=" + soByte);
            return;
        }

        int action = ms.boDoc().readUnsignedByte();
        int selfIndex = ms.boDoc().readUnsignedByte();
        System.out.println("[THOR] DOC_PACKET action=" + action
                + " selfIndex=" + selfIndex);

        if (action != 3) {
            System.out.println("[THOR] SAI_ACTION action=" + action);
            return;
        }
        if ((thor.chiSo & 0xFF) != selfIndex) {
            System.out.println("[THOR] SAI_SELF_INDEX expected="
                    + (thor.chiSo & 0xFF) + " actual=" + selfIndex);
            return;
        }

        thiTrien(thor);
    }

    public boolean dangThiTrien(ChickenChienBinh chienBinh) {
        return chienBinh != null
                && chienBinh.avenger == AVG_THOR
                && chienBinh.thorDaDungKyNang;
    }

    private synchronized void thiTrien(ChickenChienBinh thor) throws IOException {
        if (thor == null || thor.thorDaDungKyNang || this.dieuKhien.daKetThuc()) {
            return;
        }

        thor.thorDaDungKyNang = true;
        thor.thorDaGuiMenu = false;
        final long skillId = ++this.maKyNang;

        short[] cacX = taoBonViTriX(thor.x);
        short[] cacY = taoBonDiemVaChamY(this.map, cacX, thor.y);
        ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(thor, this.chienBinhs);
        this.dieuKhien.guiTiaSet(thor, LOAI_HIEU_UNG_SET, cacX, cacY);
        log("PHAT_SET", thor,
                "x=" + cacX[0] + "," + cacX[1] + "," + cacX[2] + "," + cacX[3]);

        gaySatThuongTheoTungTia(thor, cacX, cacY, phienMayMan);

        // Đồng bộ mặt nạ va chạm phía server sau mỗi lần sét đánh.
        // Nếu không cập nhật, lần dùng sau server vẫn tìm đúng điểm va chạm cũ
        // trong khi client đã khoét lỗ ở đó, nên tia sét không thể phá tiếp lớp map bên dưới.
        phaDiaHinhTheoTungTia(this.map, cacX, cacY);

        THOR_EXECUTOR.schedule(
                () -> ketThucKyNang(skillId, thor),
                THOI_GIAN_HIEU_UNG_MS,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Mỗi tia được tính va chạm riêng. Thor luôn bị loại khỏi danh sách mục tiêu.
     * Một mục tiêu đứng trúng nhiều tia sẽ nhận tổng sát thương của số tia trúng.
     */
    private void gaySatThuongTheoTungTia(
            ChickenChienBinh thor,
            short[] cacX,
            short[] cacY,
            ChickenMayMan.PhienTanCong phienMayMan
    ) throws IOException {
        for (ChickenChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu == null
                    || mucTieu == thor
                    || mucTieu.chet
                    || this.dieuKhien.daKetThuc()) {
                continue;
            }

            int tongSatThuong = ChickenSatThuongLanKyNang.tinhThor(
                    thor.tanCong,
                    mucTieu.giap,
                    cacX,
                    cacY,
                    mucTieu.x,
                    mucTieu.y,
                    mucTieu.bot,
                    this.map
            );
            if (tongSatThuong <= 0) {
                continue;
            }
            tongSatThuong = phienMayMan.apDung(mucTieu, tongSatThuong);

            log("NO_LAN_SET", thor,
                    "target=" + mucTieu.ten
                    + ", damage=" + tongSatThuong);
            this.dieuKhien.gaySatThuong(mucTieu, tongSatThuong);
        }
    }

    private synchronized void ketThucKyNang(long skillId, ChickenChienBinh thor) {
        if (skillId != this.maKyNang || thor == null || !thor.thorDaDungKyNang) {
            return;
        }

        thor.thorDaDungKyNang = false;
        thor.thorDaGuiMenu = false;
        log("KET_THUC", thor, "skillId=" + skillId);

        if (!this.dieuKhien.daKetThuc()) {
            try {
                this.dieuKhien.sangLuot();
            } catch (IOException ex) {
                System.out.println("[THOR] LOI_CHUYEN_LUOT " + ex.getMessage());
            }
        }
    }

    public static short[] taoBonViTriX(short thorX) {
        return new short[]{
            kepShort((int)thorX - LECH_TIA_XA),
            kepShort((int)thorX - LECH_TIA_GAN),
            kepShort((int)thorX + LECH_TIA_GAN),
            kepShort((int)thorX + LECH_TIA_XA)
        };
    }

    /**
     * Tìm điểm va chạm riêng cho từng tia sét. Mỗi tia quét thẳng xuống từ
     * độ cao của Thor tại đúng cột X của nó và dừng ở pixel địa hình đầu tiên.
     * Vì vậy nếu bên cạnh Thor là khoảng trống, tia vẫn tiếp tục đánh xuống
     * nền thấp hơn thay vì dùng chung Y với Thor.
     */
    public static short[] taoBonDiemVaChamY(
            ChickenQuanLyBanDo map,
            short[] cacX,
            short thorY
    ) {
        int soTia = cacX == null ? 0 : cacX.length;
        short[] ketQua = new short[soTia];
        for (int i = 0; i < soTia; i++) {
            ketQua[i] = timDiemVaChamTheoCot(map, cacX[i], thorY);
        }
        return ketQua;
    }

    /**
     * Trả về Y va chạm đầu tiên dưới Thor tại một cột X. Nếu cột đó không có
     * địa hình nào, tia vẫn kéo xuống sát đáy map để hiệu ứng kết thúc riêng.
     */
    public static short timDiemVaChamTheoCot(
            ChickenQuanLyBanDo map,
            short x,
            short thorY
    ) {
        if (map == null || map.getHeight() <= 0) {
            return kepShort((int)thorY - 18);
        }

        int batDauY = Math.max(0, Math.min(map.getHeight() - 1, (int)thorY - 18));
        for (int y = batDauY; y < map.getHeight(); y++) {
            if (map.coVaCham(x, (short)y)) {
                return (short)y;
            }
        }

        return (short)Math.min(Short.MAX_VALUE, Math.max(0, map.getHeight() - 1));
    }

    /** Giữ tương thích cho các chỗ cũ chưa truyền map. */
    public static short[] taoBonViTriY(short chanY) {
        short y = kepShort((int)chanY - 18);
        return new short[]{y, y, y, y};
    }

    /**
     * Khoét địa hình độc lập tại điểm va chạm của từng tia. Chỉ khoét khi điểm
     * đó thực sự còn là pixel địa hình; nhờ vậy mỗi lần dùng skill tiếp theo
     * sẽ dò xuống lớp map mới ở bên dưới thay vì lặp lại điểm va chạm cũ.
     */
    public static void phaDiaHinhTheoTungTia(
            ChickenQuanLyBanDo map,
            short[] cacX,
            short[] cacY
    ) {
        if (map == null || cacX == null || cacY == null) {
            return;
        }
        int soTia = Math.min(cacX.length, cacY.length);
        for (int i = 0; i < soTia; i++) {
            short x = cacX[i];
            short y = cacY[i];
            if (x < 0 || y < 0 || x >= map.getWidth() || y >= map.getHeight()) {
                continue;
            }
            if (!map.coVaCham(x, y)) {
                continue;
            }
            map.phaDiaHinh(x, y, LOAI_HIEU_UNG_SET);
        }
    }

    private static short kepShort(int giaTri) {
        if (giaTri < Short.MIN_VALUE) {
            return Short.MIN_VALUE;
        }
        if (giaTri > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        }
        return (short)giaTri;
    }

    private static void log(String suKien, ChickenChienBinh thor, String chiTiet) {
        String ten = thor != null && thor.ten != null ? thor.ten : "null";
        int chiSo = thor != null ? thor.chiSo & 0xFF : -1;
        System.out.println("[THOR] " + suKien
                + " player=" + ten
                + " index=" + chiSo
                + " " + chiTiet);
    }
}
