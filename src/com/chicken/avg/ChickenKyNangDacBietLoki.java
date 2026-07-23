package com.chicken.avg;

import com.chicken.chien.ChickenChienBinh;
import com.chicken.mang.ChickenTinNhan;
import java.io.IOException;

/**
 * Kỹ năng đặc biệt của AVG Loki.
 *
 * Luồng native CMD -91 của client:
 * - server gửi action 5: hiện menu có mục "Giả dạng".
 * - bấm "Giả dạng": client tự mở danh sách nhân vật trong map.
 * - chọn mục tiêu: client gửi action 0 và battleIndex mục tiêu.
 * - server gửi action 0, lokiIndex, targetIndex: client copy tên hiển thị,
 *   ngoại hình, AVG, hình súng và thanh máu của mục tiêu sang Loki.
 *
 * Server chỉ copy hp/máu tối đa. Vũ khí thật, tấn công, giáp, may mắn,
 * tốc độ và kỹ năng của Loki không thay đổi.
 */
public final class ChickenKyNangDacBietLoki {
    public static final byte AVG_LOKI = 4;
    public interface DieuKhienTranDau {
        boolean daKetThuc();
        byte luotHienTai();
        void guiMenuLoki(ChickenChienBinh loki);
        void guiChonMucTieuLoki(ChickenChienBinh loki);
        void guiBienHinh(ChickenChienBinh loki, ChickenChienBinh mucTieu) throws IOException;
        void capNhatMau(ChickenChienBinh loki) throws IOException;
    }

    private final ChickenChienBinh[] chienBinhs;
    private final DieuKhienTranDau dieuKhien;

    public ChickenKyNangDacBietLoki(
            ChickenChienBinh[] chienBinhs,
            DieuKhienTranDau dieuKhien
    ) {
        this.chienBinhs = chienBinhs;
        this.dieuKhien = dieuKhien;
    }

    /** Chế độ test: tới lượt Loki thì hiện menu Giả dạng một lần. */
    public void guiTinHieuKyNangNeuCo(ChickenChienBinh loki) {
        if (loki == null
                || loki.chet
                || !loki.coPhien()
                || loki.avenger != AVG_LOKI
                || loki.lokiDaDungKyNang
                || this.dieuKhien.daKetThuc()
                || !coMucTieuHopLe(loki)) {
            return;
        }

        loki.lokiDaGuiMenu = true;
        this.dieuKhien.guiMenuLoki(loki);
        log("TEST_MO_MENU", loki, "action=5 label=Gia_dang");
    }

    public synchronized void nhanLenh(
            ChickenChienBinh loki,
            ChickenTinNhan ms
    ) throws IOException {
        if (loki == null || ms == null) {
            System.out.println("[LOKI] KHONG_TIM_THAY_LOKI");
            return;
        }
        if (loki.avenger != AVG_LOKI
                || loki.chet
                || loki.lokiDaDungKyNang
                || loki.chiSo != this.dieuKhien.luotHienTai()
                || this.dieuKhien.daKetThuc()) {
            log("CMD_-91_BO_QUA", loki,
                    "avenger=" + loki.avenger
                    + ", chet=" + loki.chet
                    + ", daDung=" + loki.lokiDaDungKyNang
                    + ", luot=" + this.dieuKhien.luotHienTai());
            return;
        }

        int soByte = ms.boDoc().available();
        if (soByte < 2) {
            System.out.println("[LOKI] PACKET_THIEU bytes=" + soByte);
            return;
        }

        int action = ms.boDoc().readUnsignedByte();
        int battleIndex = ms.boDoc().readUnsignedByte();
        log("DOC_PACKET", loki,
                "action=" + action + ", battleIndex=" + battleIndex);

        // Menu action 5 của client tự mở danh sách. Khi người chơi chọn
        // một nhân vật, client gửi thẳng action 0 + battleIndex mục tiêu.
        if (action == 0) {
            xuLyChonMucTieuTrucTiep(loki, battleIndex);
            return;
        }

        // Giữ tương thích với luồng cũ action 2 -> action 1.
        if (action == 2) {
            xuLyMoDanhSachMucTieu(loki, battleIndex);
            return;
        }
        if (action == 1) {
            xuLyChonMucTieu(loki, battleIndex);
            return;
        }

        System.out.println("[LOKI] SAI_ACTION action=" + action);
    }

    public boolean dangThiTrien(ChickenChienBinh chienBinh) {
        return chienBinh != null
                && chienBinh.avenger == AVG_LOKI
                && chienBinh.lokiSkillActive;
    }

    private void xuLyMoDanhSachMucTieu(
            ChickenChienBinh loki,
            int selfIndex
    ) {
        if ((loki.chiSo & 0xFF) != selfIndex || !coMucTieuHopLe(loki)) {
            log("SAI_SELF_INDEX", loki, "actual=" + selfIndex);
            return;
        }

        loki.lokiDangChoChonMucTieu = true;
        this.dieuKhien.guiChonMucTieuLoki(loki);
        log("MO_DANH_SACH_MUC_TIEU", loki, "action=1");
    }

    private void xuLyChonMucTieuTrucTiep(
            ChickenChienBinh loki,
            int targetIndex
    ) throws IOException {
        ChickenChienBinh mucTieu = timMucTieu(targetIndex);
        if (mucTieu == null || mucTieu == loki || mucTieu.chet) {
            log("MUC_TIEU_KHONG_HOP_LE", loki, "target=" + targetIndex);
            return;
        }

        thiTrien(loki, mucTieu);
    }

    private void xuLyChonMucTieu(
            ChickenChienBinh loki,
            int targetIndex
    ) throws IOException {
        if (!loki.lokiDangChoChonMucTieu) {
            log("CHUA_MO_DANH_SACH", loki, "target=" + targetIndex);
            return;
        }

        ChickenChienBinh mucTieu = timMucTieu(targetIndex);
        if (mucTieu == null || mucTieu == loki || mucTieu.chet) {
            log("MUC_TIEU_KHONG_HOP_LE", loki, "target=" + targetIndex);
            return;
        }

        thiTrien(loki, mucTieu);
    }

    private synchronized void thiTrien(
            ChickenChienBinh loki,
            ChickenChienBinh mucTieu
    ) throws IOException {
        if (loki == null
                || mucTieu == null
                || loki.lokiDaDungKyNang
                || this.dieuKhien.daKetThuc()) {
            return;
        }

        loki.lokiDaDungKyNang = true;
        loki.lokiDaGuiMenu = false;
        loki.lokiDangChoChonMucTieu = false;
        // Chỉ đánh dấu đang xử lý trong lúc gửi dữ liệu biến hình.
        // Kỹ năng này không phải phát bắn và không làm mất lượt hiện tại.
        loki.lokiSkillActive = true;

        // Copy tên hiển thị và máu trong phạm vi trận. Tên tài khoản thật,
        // vũ khí thật và toàn bộ chỉ số chiến đấu của Loki vẫn không đổi.
        loki.ten = mucTieu.ten;
        loki.mauToiDa = Math.max(1, mucTieu.mauToiDa);
        loki.hp = Math.max(0, Math.min(loki.mauToiDa, mucTieu.hp));
        loki.chet = loki.hp <= 0;

        this.dieuKhien.guiBienHinh(loki, mucTieu);
        this.dieuKhien.capNhatMau(loki);
        log("BIEN_HINH", loki,
                "target=" + (mucTieu.chiSo & 0xFF)
                + ", hp=" + loki.hp
                + ", maxHp=" + loki.mauToiDa
                + ", tenHienThi=" + loki.ten
                + ", weaponThat=" + loki.maVuKhi
                + ", avengerThat=" + loki.avenger);

        // Kết thúc trạng thái xử lý ngay nhưng giữ nguyên lượt của Loki.
        // Không gọi sangLuot(), không tính đây là một lượt bắn.
        loki.lokiSkillActive = false;
        log("KET_THUC", loki,
                "giuBienHinhDenHetTran=true, khongChuyenLuot=true, vanDuocBan=true");
    }

    private boolean coMucTieuHopLe(ChickenChienBinh loki) {
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh != loki && !chienBinh.chet) {
                return true;
            }
        }
        return false;
    }

    private ChickenChienBinh timMucTieu(int targetIndex) {
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null
                    && (chienBinh.chiSo & 0xFF) == targetIndex) {
                return chienBinh;
            }
        }
        return null;
    }

    private static void log(
            String suKien,
            ChickenChienBinh loki,
            String chiTiet
    ) {
        String ten = loki != null && loki.ten != null ? loki.ten : "null";
        int chiSo = loki != null ? loki.chiSo & 0xFF : -1;
        System.out.println("[LOKI] " + suKien
                + " player=" + ten
                + " index=" + chiSo
                + " " + chiTiet);
    }
}
