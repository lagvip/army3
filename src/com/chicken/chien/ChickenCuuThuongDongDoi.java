package com.chicken.chien;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Luat server-authoritative cua item Cuu thuong dong doi (ID 230). */
public final class ChickenCuuThuongDongDoi {
    public static final int ID_VAT_PHAM = 230;

    private ChickenCuuThuongDongDoi() {
    }

    /**
     * Tim dung nguoi co the hoi, khong bao gio hoi bot boss, ke dich, nguoi
     * da chet hay da roi tran. Nguoi dung duoc tinh la thanh vien cua phe.
     */
    public static List<ChickenChienBinh> layMucTieu(
            ChickenChienBinh nguoiDung,
            ChickenChienBinh[] danhSach,
            boolean chiaPheTheoSlotChanLe
    ) {
        if (nguoiDung == null || !nguoiDung.laNguoiChoiThat()
                || nguoiDung.chet || nguoiDung.hp <= 0
                || danhSach == null) {
            return Collections.emptyList();
        }
        List<ChickenChienBinh> ketQua = new ArrayList<>();
        for (ChickenChienBinh mucTieu : danhSach) {
            if (mucTieu == null || !mucTieu.laNguoiChoiThat()
                    || mucTieu.daRoiTran || mucTieu.chet
                    || mucTieu.hp <= 0 || mucTieu.hp >= mucTieu.mauToiDa) {
                continue;
            }
            if (chiaPheTheoSlotChanLe
                    && ((mucTieu.chiSo ^ nguoiDung.chiSo) & 1) != 0) {
                continue;
            }
            ketQua.add(mucTieu);
        }
        return ketQua;
    }

    /** Ap dung sau khi giao dich tru item da commit. */
    public static void apDung(List<ChickenChienBinh> mucTieu) {
        if (mucTieu == null) {
            return;
        }
        for (ChickenChienBinh chienBinh : mucTieu) {
            if (chienBinh == null || chienBinh.chet || chienBinh.hp <= 0) {
                continue;
            }
            chienBinh.hp = tinhMauSauCuuThuong(
                    chienBinh.hp, chienBinh.mauToiDa);
        }
    }

    /**
     * Hoi 50% phan HP dang thieu. Neu phan thieu la so le thi lam tron len
     * de nguoi chi thieu 1 HP van duoc hoi, dong thoi luon kep trong 0..max.
     */
    public static int tinhMauSauCuuThuong(int hp, int mauToiDa) {
        int maxHp = Math.max(1, mauToiDa);
        int hpHienTai = Math.max(0, Math.min(maxHp, hp));
        int mauDaMat = maxHp - hpHienTai;
        int mauHoi = (mauDaMat + 1) / 2;
        return hpHienTai + mauHoi;
    }
}
