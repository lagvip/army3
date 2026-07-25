package com.chicken.avg;

import com.chicken.chien.ChickenChienBinh;

/** Trang thai skill laser nguc Iron Man: chon skill roi ngam va ban mot lan. */
public final class ChickenKyNangDacBietIronMan {

    public static final byte AVG_IRON_MAN = ChickenTiaLaserIronMan.AVG_IRON_MAN;

    public interface DieuKhienTranDau {
        boolean daKetThuc();
        byte luotHienTai();
        void guiMenuIronMan(ChickenChienBinh ironMan);
    }

    private final DieuKhienTranDau dieuKhien;

    public ChickenKyNangDacBietIronMan(DieuKhienTranDau dieuKhien) {
        this.dieuKhien = dieuKhien;
    }

    public void guiTinHieuKyNangNeuCo(ChickenChienBinh ironMan) {
        if (ironMan == null || ironMan.chet || !ironMan.coPhien()
                || ironMan.avenger != AVG_IRON_MAN
                || ironMan.ironManDaDungKyNang
                || ironMan.ironManLaserSanSang
                || ironMan.ironManDaGuiMenu
                || this.dieuKhien.daKetThuc()
                || ironMan.chiSo != this.dieuKhien.luotHienTai()) {
            return;
        }
        ironMan.ironManDaGuiMenu = true;
        this.dieuKhien.guiMenuIronMan(ironMan);
    }

    public synchronized boolean kichHoat(ChickenChienBinh ironMan) {
        if (ironMan == null || ironMan.chet
                || ironMan.avenger != AVG_IRON_MAN
                || ironMan.ironManDaDungKyNang
                || ironMan.ironManLaserSanSang
                || !ironMan.ironManDaGuiMenu
                || this.dieuKhien.daKetThuc()
                || ironMan.chiSo != this.dieuKhien.luotHienTai()) {
            return false;
        }
        ironMan.ironManDaGuiMenu = false;
        ironMan.ironManDaDungKyNang = true;
        ironMan.ironManLaserSanSang = true;
        if (ironMan.coPhien()) {
            ironMan.nguoiChoi.dichVu.guiDongChoKyNangUltron();
            ironMan.nguoiChoi.dichVu.guiTrangThaiNgamLaserIronMan(true);
        }
        return true;
    }

    public boolean dangChoBan(ChickenChienBinh ironMan) {
        return ironMan != null
                && ironMan.avenger == AVG_IRON_MAN
                && ironMan.ironManLaserSanSang;
    }

    public void sauKhiBanHoacBoLuot(ChickenChienBinh ironMan) {
        xoaTrangThaiChoBan(ironMan);
    }

    public static void guiTinHieuTrongTran(
            ChickenChienBinh ironMan,
            boolean daKetThuc,
            byte luotHienTai
    ) {
        if (ironMan == null || ironMan.chet || !ironMan.coPhien()
                || ironMan.avenger != AVG_IRON_MAN
                || ironMan.ironManDaDungKyNang
                || ironMan.ironManLaserSanSang
                || ironMan.ironManDaGuiMenu
                || daKetThuc
                || ironMan.chiSo != luotHienTai) {
            return;
        }
        ironMan.ironManDaGuiMenu = true;
        ironMan.nguoiChoi.dichVu.guiChonKyNangIronMan();
    }

    public static boolean kichHoatTrongTran(
            ChickenChienBinh ironMan,
            boolean daKetThuc,
            byte luotHienTai
    ) {
        if (ironMan == null || ironMan.chet
                || ironMan.avenger != AVG_IRON_MAN
                || ironMan.ironManDaDungKyNang
                || ironMan.ironManLaserSanSang
                || !ironMan.ironManDaGuiMenu
                || daKetThuc
                || ironMan.chiSo != luotHienTai) {
            return false;
        }
        ironMan.ironManDaGuiMenu = false;
        ironMan.ironManDaDungKyNang = true;
        ironMan.ironManLaserSanSang = true;
        if (ironMan.coPhien()) {
            ironMan.nguoiChoi.dichVu.guiDongChoKyNangUltron();
            ironMan.nguoiChoi.dichVu.guiTrangThaiNgamLaserIronMan(true);
        }
        return true;
    }

    public static void xoaTrangThaiChoBan(ChickenChienBinh ironMan) {
        if (ironMan == null || ironMan.avenger != AVG_IRON_MAN) {
            return;
        }
        boolean dangSanSang = ironMan.ironManLaserSanSang;
        ironMan.ironManLaserSanSang = false;
        ironMan.ironManDaGuiMenu = false;
        if (dangSanSang && ironMan.coPhien()) {
            ironMan.nguoiChoi.dichVu.guiTrangThaiNgamLaserIronMan(false);
        }
    }
}
