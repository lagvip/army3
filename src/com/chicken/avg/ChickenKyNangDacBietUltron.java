package com.chicken.avg;

import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenTrangThaiHanhDongLuot;

/**
 * Kỹ năng đặc biệt của AVG Ultron: Bắn x3.
 *
 * Khi người chơi chọn Bắn x3, server chỉ bật trạng thái kỹ năng và giữ
 * nguyên lượt hiện tại. Client tự đóng menu; phát bắn CMD 22 kế tiếp mang
 * góc/lực thật của người chơi và được chuyển thành ba tia laser hội tụ.
 */
public final class ChickenKyNangDacBietUltron {

    public static final byte AVG_ULTRON = ChickenCongThucBanUltron.AVG_ULTRON;
    public interface DieuKhienTranDau {
        boolean daKetThuc();
        byte luotHienTai();
        void guiMenuUltron(ChickenChienBinh ultron);
    }

    private final DieuKhienTranDau dieuKhien;

    public ChickenKyNangDacBietUltron(DieuKhienTranDau dieuKhien) {
        this.dieuKhien = dieuKhien;
    }

    /** Chế độ test: tới lượt Ultron thì hiện menu Bắn x3 một lần mỗi lượt. */
    public void guiTinHieuKyNangNeuCo(ChickenChienBinh ultron) {
        if (ultron == null
                || ultron.chet
                || !ChickenTrangThaiHanhDongLuot
                        .coTheKichHoatKyNang(ultron)
                || !ultron.coPhien()
                || ultron.avenger != AVG_ULTRON
                || ultron.ultronDaDungKyNang
                || ultron.ultronDangBanX3
                || ultron.ultronDaGuiMenu
                || this.dieuKhien.daKetThuc()
                || ultron.chiSo != this.dieuKhien.luotHienTai()) {
            return;
        }
        ultron.ultronDaGuiMenu = true;
        this.dieuKhien.guiMenuUltron(ultron);
        System.out.println("[ULTRON] GUI_MENU_BAN_X3 index="
                + (ultron.chiSo & 0xFF)
                + " choNguoiChoiChon=true");
    }

    /** Nhận lựa chọn duy nhất của menu generic CMD -47 (client trả lại CMD -47). */
    public synchronized boolean kichHoatBanX3(ChickenChienBinh ultron) {
        if (ultron == null
                || ultron.chet
                || ultron.avenger != AVG_ULTRON
                || ultron.ultronDaDungKyNang
                || ultron.ultronDangBanX3
                || !ultron.ultronDaGuiMenu
                || this.dieuKhien.daKetThuc()
                || ultron.chiSo != this.dieuKhien.luotHienTai()) {
            return false;
        }
        ultron.ultronDaGuiMenu = false;
        ultron.ultronDaDungKyNang = true;
        ultron.ultronDangBanX3 = true;
        if (ultron.nguoiChoi != null && ultron.nguoiChoi.dichVu != null) {
            ultron.nguoiChoi.dichVu.guiDongChoKyNangUltron();
        }
        System.out.println("[ULTRON] KICH_HOAT_BAN_X3 index="
                + (ultron.chiSo & 0xFF) + " choPhatBanThat=true");
        return true;
    }

    public boolean dangBanX3(ChickenChienBinh ultron) {
        return ultron != null
                && ultron.avenger == AVG_ULTRON
                && ultron.ultronDangBanX3;
    }

    public void sauKhiDaBan(ChickenChienBinh ultron) {
        if (ultron == null || ultron.avenger != AVG_ULTRON) {
            return;
        }
        ultron.ultronDangBanX3 = false;
        ultron.ultronDaGuiMenu = false;
    }

    public void huyKhiBoLuot(ChickenChienBinh ultron) {
        if (ultron == null || ultron.avenger != AVG_ULTRON) {
            return;
        }
        ultron.ultronDangBanX3 = false;
        ultron.ultronDaGuiMenu = false;
    }
}
