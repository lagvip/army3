package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.phong.boss.trandau.CauHinhMapBoss;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class QuanLySanhChoBoss {
    public static final byte MA_PHONG_BOSS = 4;
    public static final byte SO_NGUOI_TOI_DA = 8;
    public static final int SO_SANH = 5;
    private static final int SO_KHOA_TAI_KHOAN = 256;
    private static final Object[] KHOA_TAI_KHOAN =
            new Object[SO_KHOA_TAI_KHOAN];

    private static final AtomicLong THU_TU_VAO = new AtomicLong();
    private static final Map<Integer, SanhChoBoss> SANH_THEO_NGUOI_CHOI =
            new ConcurrentHashMap<>();
    private static volatile SanhChoBoss[] sanhs = new SanhChoBoss[0];

    static {
        for (int i = 0; i < KHOA_TAI_KHOAN.length; i++) {
            KHOA_TAI_KHOAN[i] = new Object();
        }
        khoiTao();
    }

    private QuanLySanhChoBoss() {
    }

    public static synchronized void khoiTao() {
        SanhChoBoss[] danhSachMoi = new SanhChoBoss[SO_SANH];
        for (int maBan = 0; maBan < danhSachMoi.length; maBan++) {
            danhSachMoi[maBan] = new SanhChoBoss(
                    MA_PHONG_BOSS,
                    (byte) maBan,
                    (byte) CauHinhMapBoss.layMapTheoBan(maBan),
                    SO_NGUOI_TOI_DA,
                    Math.max(0, ChickenQuanLyMayChu.bossEntryGoldCost)
            );
        }
        sanhs = danhSachMoi;
        SANH_THEO_NGUOI_CHOI.clear();
        THU_TU_VAO.set(0L);
    }

    public static SanhChoBoss laySanh(int maBan) {
        SanhChoBoss[] danhSach = sanhs;
        if (maBan < 0 || maBan >= danhSach.length) {
            return null;
        }
        return danhSach[maBan];
    }

    public static SanhChoBoss[] layDanhSach() {
        return sanhs.clone();
    }

    public static SanhChoBoss timSanhCuaNguoiChoi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return null;
        }
        SanhChoBoss sanh = SANH_THEO_NGUOI_CHOI.get(nguoiChoi.ma);
        if (sanh != null && sanh.timThanhVien(nguoiChoi) != null) {
            return sanh;
        }
        SANH_THEO_NGUOI_CHOI.remove(nguoiChoi.ma);
        for (SanhChoBoss sanhChoBoss : sanhs) {
            if (sanhChoBoss != null && sanhChoBoss.timThanhVien(nguoiChoi) != null) {
                SANH_THEO_NGUOI_CHOI.put(nguoiChoi.ma, sanhChoBoss);
                return sanhChoBoss;
            }
        }
        return null;
    }

    static long taoThuTuVao() {
        return THU_TU_VAO.incrementAndGet();
    }

    static Object layKhoaTaiKhoan(int maNguoiChoi) {
        int chiSo = (maNguoiChoi ^ (maNguoiChoi >>> 16))
                & (KHOA_TAI_KHOAN.length - 1);
        return KHOA_TAI_KHOAN[chiSo];
    }

    static void ganNguoiChoi(ChickenNguoiChoi nguoiChoi, SanhChoBoss sanh) {
        if (nguoiChoi != null && sanh != null) {
            SANH_THEO_NGUOI_CHOI.put(nguoiChoi.ma, sanh);
        }
    }

    static void boGanNguoiChoi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi != null) {
            SANH_THEO_NGUOI_CHOI.remove(nguoiChoi.ma);
        }
    }

    public static void xoaNguoiChoiKhoiTatCaSanh(ChickenNguoiChoi nguoiChoi) {
        RoiSanhChoBoss.xuLyNgatKetNoi(nguoiChoi);
    }
}
