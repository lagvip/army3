package com.chicken.avg;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.mang.ChickenTinNhan;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Chứa toàn bộ logic kỹ năng đặc biệt của AVG Hawk.
 *
 * Luồng CMD -91:
 * - Server gửi action 1 để client mở menu chọn mục tiêu.
 * - Client trả đúng 2 byte: action 1 và battleIndex của mục tiêu.
 *
 * Hoạt ảnh skill:
 * - Bắn 4 sprite /eff/muiten.png của client nối đuôi theo góc 90 độ lên trời.
 * - Sau khi loạt đầu bay khỏi màn hình, tạo 4 mũi tên trên đầu mục tiêu.
 * - 4 mũi tên lao xuống nối đuôi, sau mũi cuối mới trừ một lần sát thương cộng dồn.
 */
public final class ChickenKyNangDacBietHawk {
    public static final byte AVG_HAWK = 7;
    private static final int SO_MUI_TEN = ChickenHoatAnhHawk.SO_MUI_TEN;
    private static final ScheduledExecutorService HAWK_EXECUTOR =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "chicken-hawk-skill");
                thread.setDaemon(true);
                return thread;
            });

    public interface DieuKhienTranDau {
        boolean daKetThuc();
        byte luotHienTai();
        void guiHoatAnhMuiTen(
                ChickenChienBinh hawk,
                short goc,
                ChickenHoatAnhHawk.DuongDan duongDan
        ) throws IOException;
        void gaySatThuong(ChickenChienBinh mucTieu, int satThuong) throws IOException;
        void sangLuot() throws IOException;
    }

    private final ChickenChienBinh[] chienBinhs;
    private final ChickenQuanLyBanDo map;
    private final DieuKhienTranDau dieuKhien;
    private long maKyNang;

    public ChickenKyNangDacBietHawk(
            ChickenChienBinh[] chienBinhs,
            ChickenQuanLyBanDo map,
            DieuKhienTranDau dieuKhien
    ) {
        this.chienBinhs = chienBinhs;
        this.map = map;
        this.dieuKhien = dieuKhien;
    }

    /** Gọi sau khi nhân vật hoàn thành một lượt bắn thường. */
    public void sauKhiBanThuong(ChickenChienBinh chienBinh) {
        if (chienBinh == null || chienBinh.chet || chienBinh.avenger != AVG_HAWK) {
            return;
        }
        chienBinh.hawkSoLuotBan++;
        chienBinh.hawkDaGuiChonMucTieu = false;
        log("BAN_THUONG", chienBinh, "dem=" + chienBinh.hawkSoLuotBan);
    }

    /**
     * Chế độ test Hawk: cứ đến lượt Hawk là mở ngay danh sách chọn mục tiêu.
     * Tạm thời bỏ điều kiện số lượt bắn, nạp skill và cờ đã dùng skill.
     */
    public void guiTinHieuChonMucTieuNeuCo(ChickenChienBinh hawk) {
        if (hawk == null
                || hawk.chet
                || !hawk.coPhien()
                || hawk.avenger != AVG_HAWK
                || hawk.hawkDaDungKyNang
                || this.dieuKhien.daKetThuc()
                || hawk.chiSo != this.dieuKhien.luotHienTai()) {
            return;
        }

        hawk.hawkDaGuiChonMucTieu = false;
        if (guiDanhSachMucTieu(hawk)) {
            hawk.hawkDaGuiChonMucTieu = true;
            log("TEST_MO_MENU_NGAY", hawk, "boDieuKienNapSkill=true");
        }
    }

    /** Nhận đúng packet client Hawk: byte action, byte battleIndex mục tiêu. */
    public void nhanLenh(ChickenChienBinh nguoiDung, ChickenTinNhan ms) throws IOException {
        if (nguoiDung == null) {
            System.out.println("[HAWK] KHONG_TIM_THAY_HAWK");
            return;
        }
        if (nguoiDung.avenger != AVG_HAWK) {
            log("CMD_-91_KHONG_PHAI_HAWK", nguoiDung,
                    "avenger=" + nguoiDung.avenger);
            return;
        }
        if (nguoiDung.chet
                || nguoiDung.hawkDaDungKyNang
                || !nguoiDung.hawkDaGuiChonMucTieu
                || nguoiDung.chiSo != this.dieuKhien.luotHienTai()
                || this.dieuKhien.daKetThuc()) {
            log("CMD_-91_BO_QUA", nguoiDung,
                    "chet=" + nguoiDung.chet
                    + ", dangDungSkill=" + nguoiDung.hawkDaDungKyNang
                    + ", ketThuc=" + this.dieuKhien.daKetThuc());
            return;
        }

        int soByte = ms.boDoc().available();
        if (soByte != 2) {
            System.out.println("[HAWK] PACKET_SAI_DO_DAI bytes=" + soByte);
            return;
        }

        int action = ms.boDoc().readUnsignedByte();
        int targetIndex = ms.boDoc().readUnsignedByte();
        System.out.println("[HAWK] DOC_PACKET action=" + action
                + " targetIndex=" + targetIndex);

        if (action != 1) {
            System.out.println("[HAWK] SAI_ACTION action=" + action);
            return;
        }

        xuLyChonMucTieu(nguoiDung, targetIndex);
    }

    /** Danh sách mục tiêu dùng cho chế độ điều kiện chuẩn. */
    public ChickenChienBinh[] danhSachMucTieu(ChickenChienBinh hawk) {
        if (!coTheDungKyNang(hawk)) {
            return new ChickenChienBinh[0];
        }
        return taoMangMucTieu(hawk);
    }

    /** Kích hoạt theo điều kiện chuẩn. */
    public boolean kichHoat(ChickenChienBinh hawk, byte chiSoMucTieu) throws IOException {
        if (!coTheDungKyNang(hawk)) {
            return false;
        }
        ChickenChienBinh mucTieu = layMucTieu(hawk, chiSoMucTieu);
        if (mucTieu == null) {
            return false;
        }
        return thiTrien(hawk, mucTieu);
    }

    /** Chọn mục tiêu bằng battleIndex client gửi trực tiếp. */
    private void xuLyChonMucTieu(ChickenChienBinh hawk, int targetIndex) throws IOException {
        ChickenChienBinh mucTieu = timMucTieuTheoBattleIndex(targetIndex);
        if (mucTieu == null || mucTieu == hawk || mucTieu.chet) {
            System.out.println("[HAWK] MUC_TIEU_KHONG_HOP_LE index=" + targetIndex);
            return;
        }

        System.out.println("[HAWK] CHON_MUC_TIEU hawk=" + (hawk.chiSo & 0xFF)
                + " target=" + (mucTieu.chiSo & 0xFF));
        thiTrien(hawk, mucTieu);
    }

    private ChickenChienBinh timMucTieuTheoBattleIndex(int targetIndex) {
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && (chienBinh.chiSo & 0xFF) == targetIndex) {
                return chienBinh;
            }
        }
        return null;
    }

    /**
     * Bắt đầu skill bằng loạt bốn mũi tên bay thẳng lên. Phần rơi xuống và
     * sát thương được chạy theo lịch để không chuyển lượt trước khi animation xong.
     */
    private synchronized boolean thiTrien(
            ChickenChienBinh hawk,
            ChickenChienBinh mucTieu
    ) throws IOException {
        if (hawk == null || mucTieu == null || hawk.hawkDaDungKyNang
                || hawk.chet || mucTieu.chet || this.dieuKhien.daKetThuc()) {
            return false;
        }

        hawk.hawkDaDungKyNang = true;
        hawk.hawkDaGuiChonMucTieu = false;
        final long skillId = ++this.maKyNang;
        short dauNongX = hawk.x;
        short dauNongY = (short)Math.max(Short.MIN_VALUE, hawk.y - 24);
        ChickenHoatAnhHawk.DuongDan bayLen =
                ChickenHoatAnhHawk.taoDuongBayLen(dauNongX, dauNongY);
        this.dieuKhien.guiHoatAnhMuiTen(
                hawk,
                ChickenHoatAnhHawk.GOC_BAY_LEN,
                bayLen
        );
        log("BAY_LEN", hawk, "skillId=" + skillId + ", soMuiTen=" + SO_MUI_TEN);

        HAWK_EXECUTOR.schedule(
                () -> this.batDauLoatRoiXuong(
                        skillId,
                        hawk,
                        mucTieu
                ),
                ChickenHoatAnhHawk.THOI_GIAN_BAY_LEN_MS,
                TimeUnit.MILLISECONDS
        );
        return true;
    }

    /** Tạo loạt bốn mũi tên ở phía trên đầu mục tiêu rồi cho lao xuống. */
    private synchronized void batDauLoatRoiXuong(
            long skillId,
            ChickenChienBinh hawk,
            ChickenChienBinh mucTieu
    ) {
        if (!conHieuLuc(skillId, hawk) || mucTieu == null || mucTieu.chet) {
            ketThucSkill(skillId, hawk);
            return;
        }

        try {
            short tamThanY = (short)Math.max(Short.MIN_VALUE, mucTieu.y - 18);
            ChickenHoatAnhHawk.DuongDan laoXuong =
                    ChickenHoatAnhHawk.taoDuongLaoXuong(mucTieu.x, tamThanY);
            this.dieuKhien.guiHoatAnhMuiTen(
                    hawk,
                    ChickenHoatAnhHawk.GOC_LAO_XUONG,
                    laoXuong
            );
            log("LAO_XUONG", hawk,
                    "skillId=" + skillId + ", target=" + (mucTieu.chiSo & 0xFF));

            long thoiDiemMuiCuoiCham =
                    ChickenHoatAnhHawk.THOI_GIAN_MUI_DAU_CHAM_MUC_TIEU_MS
                    + (SO_MUI_TEN - 1L) * ChickenHoatAnhHawk.KHOANG_CACH_MUI_TEN_MS;
            HAWK_EXECUTOR.schedule(
                    () -> this.xuLySatThuongCongDon(
                            skillId,
                            hawk,
                            mucTieu
                    ),
                    thoiDiemMuiCuoiCham,
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception ex) {
            System.out.println("[HAWK] LOI_LAO_XUONG " + ex.getMessage());
            ketThucSkill(skillId, hawk);
        }
    }

    /**
     * Sau khi mũi thứ tư chạm mục tiêu mới trừ máu đúng một lần. Giá trị trừ
     * bằng tổng sát thương của cả bốn mũi, nên client chỉ nhận một lần cập nhật HP.
     */
    private synchronized void xuLySatThuongCongDon(
            long skillId,
            ChickenChienBinh hawk,
            ChickenChienBinh mucTieu
    ) {
        if (!conHieuLuc(skillId, hawk)) {
            return;
        }

        try {
            if (mucTieu != null && !mucTieu.chet && !this.dieuKhien.daKetThuc()) {
                int tamNoX = mucTieu.x;
                int tamNoY = (short) Math.max(Short.MIN_VALUE, mucTieu.y - 18);
                for (ChickenChienBinh biAnhHuong : this.chienBinhs) {
                    if (biAnhHuong == null
                            || biAnhHuong == hawk
                            || biAnhHuong.chet
                            || this.dieuKhien.daKetThuc()) {
                        continue;
                    }
                    int satThuong = ChickenSatThuongLanKyNang.tinhHawk(
                            hawk.tanCong,
                            biAnhHuong.giap,
                            tamNoX,
                            tamNoY,
                            biAnhHuong.x,
                            biAnhHuong.y,
                            biAnhHuong.bot,
                            this.map
                    );
                    if (satThuong <= 0) {
                        continue;
                    }
                    log("DAME_NO_LAN", hawk,
                            "soMui=" + SO_MUI_TEN
                            + ", target=" + biAnhHuong.ten
                            + ", damage=" + satThuong);
                    this.dieuKhien.gaySatThuong(biAnhHuong, satThuong);
                }
            }
        } catch (Exception ex) {
            System.out.println("[HAWK] LOI_SAT_THUONG " + ex.getMessage());
        } finally {
            ketThucSkill(skillId, hawk);
        }
    }

    private boolean conHieuLuc(long skillId, ChickenChienBinh hawk) {
        return skillId == this.maKyNang
                && hawk != null
                && hawk.hawkDaDungKyNang
                && !hawk.chet
                && !this.dieuKhien.daKetThuc();
    }

    /** Chỉ kết thúc một lần sau mũi tên cuối hoặc khi mục tiêu/trận đã kết thúc. */
    private synchronized void ketThucSkill(long skillId, ChickenChienBinh hawk) {
        if (skillId != this.maKyNang || hawk == null || !hawk.hawkDaDungKyNang) {
            return;
        }

        resetSauKhiDungSkill(hawk);
        if (!this.dieuKhien.daKetThuc()) {
            try {
                this.dieuKhien.sangLuot();
            } catch (IOException ex) {
                System.out.println("[HAWK] LOI_CHUYEN_LUOT " + ex.getMessage());
            }
        }
    }

    /** Dùng skill xong phải bắn thường lại từ đầu mới được chọn mục tiêu lần nữa. */
    private void resetSauKhiDungSkill(ChickenChienBinh hawk) {
        hawk.hawkSoLuotBan = 0;
        hawk.hawkDaGuiChonMucTieu = false;
        hawk.hawkDaDungKyNang = false;
        log("RESET", hawk, "dem=0");
    }

    private static void log(String suKien, ChickenChienBinh chienBinh, String chiTiet) {
        String ten = chienBinh != null && chienBinh.ten != null ? chienBinh.ten : "null";
        int chiSo = chienBinh != null ? chienBinh.chiSo : -1;
        System.out.println("[HAWK] " + suKien + " player=" + ten + " index=" + chiSo + " " + chiTiet);
    }

    private boolean guiDanhSachMucTieu(ChickenChienBinh nguoiDung) {
        if (nguoiDung == null || !nguoiDung.coPhien()) {
            return false;
        }
        boolean coMucTieu = false;
        for (ChickenChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu != null && mucTieu != nguoiDung && !mucTieu.chet) {
                coMucTieu = true;
                break;
            }
        }
        if (!coMucTieu) {
            return false;
        }
        nguoiDung.nguoiChoi.dichVu.guiChonMucTieuHawk();
        return true;
    }

    private ChickenChienBinh layMucTieu(ChickenChienBinh nguoiDung, byte chiSoMucTieu) {
        if (chiSoMucTieu < 0 || chiSoMucTieu >= this.chienBinhs.length) {
            return null;
        }
        ChickenChienBinh mucTieu = this.chienBinhs[chiSoMucTieu];
        if (mucTieu == null || mucTieu == nguoiDung || mucTieu.chet) {
            return null;
        }
        return mucTieu;
    }

    private ChickenChienBinh[] taoMangMucTieu(ChickenChienBinh nguoiDung) {
        int soLuong = 0;
        for (ChickenChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu != null && mucTieu != nguoiDung && !mucTieu.chet) {
                soLuong++;
            }
        }
        ChickenChienBinh[] ketQua = new ChickenChienBinh[soLuong];
        int viTri = 0;
        for (ChickenChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu != null && mucTieu != nguoiDung && !mucTieu.chet) {
                ketQua[viTri++] = mucTieu;
            }
        }
        return ketQua;
    }

    private boolean coTheDungKyNang(ChickenChienBinh hawk) {
        return hawk != null
                && !this.dieuKhien.daKetThuc()
                && !hawk.chet
                && hawk.chiSo == this.dieuKhien.luotHienTai()
                && hawk.avenger == AVG_HAWK
                && hawk.hawkSoLuotBan >= 1
                && !hawk.hawkDaDungKyNang;
    }
}
