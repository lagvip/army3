package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;

/** Log riêng cho luồng phòng boss, dùng để kiểm tra trực tiếp trên Termux. */
public final class DebugSanhBoss {
    public static volatile boolean BAT = true;

    private DebugSanhBoss() {
    }

    public static void log(String buoc, ChickenNguoiChoi nguoiChoi, String chiTiet) {
        if (!BAT) {
            return;
        }
        String ten = "null";
        int ma = -1;
        if (nguoiChoi != null) {
            ma = nguoiChoi.ma;
            if (nguoiChoi.ten != null && !nguoiChoi.ten.isEmpty()) {
                ten = nguoiChoi.ten;
            }
        }
        System.out.println("[BOSS ROOM][" + buoc + "] player=" + ten
                + " id=" + ma
                + (chiTiet == null || chiTiet.isEmpty() ? "" : " " + chiTiet));
    }

    public static void log(String buoc, String chiTiet) {
        if (!BAT) {
            return;
        }
        System.out.println("[BOSS ROOM][" + buoc + "]"
                + (chiTiet == null || chiTiet.isEmpty() ? "" : " " + chiTiet));
    }
}
