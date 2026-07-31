package com.chicken.kiemthu;

import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenCheDoTestChienDau;
import com.chicken.chien.ChickenMayMan;
import com.chicken.mohinh.ChickenNguoiChoi;

/** Hoi quy cong thuc POW authoritative va kha nang cong don May man. */
public final class ChickenKiemThuPow {

    private ChickenKiemThuPow() {
    }

    public static void main(String[] args) {
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(null);
        nguoiChoi.ma = 990_113;
        nguoiChoi.ten = "PowTester";
        nguoiChoi.wp = 57;
        ChickenChienBinh nguoiTanCong = new ChickenChienBinh(
                nguoiChoi, (byte) 0, (short) 100, (short) 100);
        ChickenChienBinh mucTieuMot = new ChickenChienBinh(
                (byte) 1, (short) 200, (short) 100,
                "PowTarget1", (short) 57, (byte) 0);
        ChickenChienBinh mucTieuHai = new ChickenChienBinh(
                (byte) 2, (short) 300, (short) 100,
                "PowTarget2", (short) 57, (byte) 0);
        ChickenChienBinh[] danhSach = {
            nguoiTanCong, mucTieuMot, mucTieuHai
        };

        nguoiChoi.khoiTaoPowTrongTran();
        if (ChickenCheDoTestChienDau.POW_LUON_DAY) {
            bang(100, nguoiChoi.layPowTrongTran(),
                    "che do dev khong khoi tao day POW");
            dung(!nguoiChoi.ghiNhanSatThuongChoPow(500, 10_000),
                    "POW dev day van bao thay doi");
        } else {
            dung(nguoiChoi.ghiNhanSatThuongChoPow(4_999, 10_000),
                    "damage dau khong cap nhat POW");
            bang(99, nguoiChoi.layPowTrongTran(),
                    "POW day som truoc moc 50% HP");
            dung(nguoiChoi.ghiNhanSatThuongChoPow(1, 10_000),
                    "damage cham moc khong cap nhat POW");
            bang(100, nguoiChoi.layPowTrongTran(),
                    "POW khong day dung moc 50% HP");
            dung(!nguoiChoi.ghiNhanSatThuongChoPow(500, 10_000),
                    "POW day van bao thay doi");
        }

        dung(nguoiChoi.kichHoatPowNoiBo(),
                "khong kich hoat duoc POW day");
        bang(ChickenCheDoTestChienDau.POW_LUON_DAY ? 100 : 0,
                nguoiChoi.layPowTrongTran(),
                "kich hoat cap nhat thanh POW sai che do");
        dung(!nguoiChoi.kichHoatPowNoiBo(),
                "kich hoat trung khi thanh da rong");

        nguoiTanCong.mayMan = 0;
        mucTieuMot.mayMan = 0;
        mucTieuHai.mayMan = 0;
        ChickenMayMan.PhienTanCong phienPow =
                ChickenMayMan.batDauChoKiemThu(
                        nguoiTanCong, danhSach, gioiHan -> 9_999);
        dung(phienPow.powDaKichHoat(),
                "phien tan cong khong snapshot POW");
        bang(200, phienPow.apDung(mucTieuMot, 100),
                "POW khong x2 muc tieu dau");
        bang(200, phienPow.apDung(mucTieuHai, 100),
                "POW khong x2 toan bo muc tieu no lan");
        bang(100, ChickenMayMan.batDauChoKiemThu(
                        nguoiTanCong, danhSach, gioiHan -> 9_999)
                        .apDung(mucTieuMot, 100),
                "POW bi dung lai o hanh dong ke tiep");

        napDayVaKichHoat(nguoiChoi, 10_000);
        nguoiTanCong.mayMan = 1_000;
        ChickenMayMan.PhienTanCong phienX4 =
                ChickenMayMan.batDauChoKiemThu(
                        nguoiTanCong, danhSach, gioiHan -> 0);
        dung(phienX4.powDaKichHoat() && phienX4.tanCongDaKichHoat(),
                "POW va May man khong cung kich hoat");
        bang(400, phienX4.apDung(mucTieuMot, 100),
                "POW x2 va May man x2 khong cong don thanh x4");

        napDayVaKichHoat(nguoiChoi, 10_000);
        mucTieuMot.mayMan = 1_000;
        bang(200, ChickenMayMan.batDauChoKiemThu(
                        nguoiTanCong, danhSach, gioiHan -> 0)
                        .apDung(mucTieuMot, 100),
                "May man phong thu khong giam x4 con x2");
        mucTieuMot.mayMan = 0;

        napDayVaKichHoat(nguoiChoi, 10_000);
        nguoiChoi.huyPowDaKichHoat();
        nguoiTanCong.mayMan = 0;
        bang(100, ChickenMayMan.batDauChoKiemThu(
                        nguoiTanCong, danhSach, gioiHan -> 9_999)
                        .apDung(mucTieuMot, 100),
                "POW van con hieu luc sau bo/het luot");

        if (!ChickenCheDoTestChienDau.POW_LUON_DAY) {
            nguoiChoi.khoiTaoPowTrongTran();
            nguoiChoi.ghiNhanSatThuongChoPow(5_000, 10_001);
            bang(99, nguoiChoi.layPowTrongTran(),
                    "HP le lam tron moc 50% xuong");
            nguoiChoi.ghiNhanSatThuongChoPow(1, 10_001);
            bang(100, nguoiChoi.layPowTrongTran(),
                    "HP le khong day tai ceil(50% HP)");
        }

        napDayVaKichHoat(nguoiChoi, 10_000);
        nguoiTanCong.mayMan = 1_000;
        bang(Integer.MAX_VALUE,
                ChickenMayMan.batDauChoKiemThu(
                        nguoiTanCong, danhSach, gioiHan -> 0)
                        .apDung(mucTieuMot, 1_500_000_000),
                "POW x May man bi tran so nguyen");

        System.out.println(
                "POW_TEST_OK threshold=50% pow=2 luck=2 stack=4"
                + " multiTarget=ok skipConsumes=ok oddHp=ok overflow=ok"
                + " devPow=" + ChickenCheDoTestChienDau.POW_LUON_DAY);
    }

    private static void napDayVaKichHoat(
            ChickenNguoiChoi nguoiChoi,
            int mauToiDa
    ) {
        nguoiChoi.khoiTaoPowTrongTran();
        nguoiChoi.ghiNhanSatThuongChoPow(
                (mauToiDa + 1) / 2, mauToiDa);
        dung(nguoiChoi.kichHoatPowNoiBo(),
                "khong kich hoat duoc POW sau khi nap day");
    }

    private static void bang(int mongDoi, int thucTe, String thongBao) {
        if (mongDoi != thucTe) {
            throw new IllegalStateException(
                    thongBao + " expected=" + mongDoi + " actual=" + thucTe);
        }
    }

    private static void dung(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new IllegalStateException(thongBao);
        }
    }
}
