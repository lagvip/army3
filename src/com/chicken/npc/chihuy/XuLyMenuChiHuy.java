package com.chicken.npc.chihuy;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.phong.ChickenQuanLyPhong;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class XuLyMenuChiHuy {
    private static final long HET_HAN_MS = 60_000L;
    private static final Map<Integer, TrangThaiMenu> TRANG_THAI = new ConcurrentHashMap<>();

    private XuLyMenuChiHuy() {
    }

    static void ghiNhanMenu(ChickenNguoiChoi nguoiChoi, int menu) {
        if (nguoiChoi != null) {
            TRANG_THAI.put(
                    nguoiChoi.ma,
                    new TrangThaiMenu(menu, System.currentTimeMillis() + HET_HAN_MS)
            );
        }
    }

    public static boolean xuLyLuaChon(ChickenNguoiChoi nguoiChoi, int luaChon)
            throws IOException {
        if (nguoiChoi == null) {
            return false;
        }
        TrangThaiMenu trangThai = TRANG_THAI.get(nguoiChoi.ma);
        if (trangThai == null) {
            return false;
        }
        if (trangThai.hetHan < System.currentTimeMillis()) {
            TRANG_THAI.remove(nguoiChoi.ma);
            return false;
        }

        if (trangThai.menu == MenuChiHuy.MENU_CHINH) {
            if (luaChon == 0) {
                MenuChiHuy.moMenuChienDau(nguoiChoi);
            } else if (luaChon == 1) {
                TRANG_THAI.remove(nguoiChoi.ma);
                nguoiChoi.moHopThoaiOK("Shop đặc biệt đang được phát triển.");
            } else {
                TRANG_THAI.remove(nguoiChoi.ma);
            }
            return true;
        }

        if (trangThai.menu == MenuChiHuy.MENU_CHIEN_DAU) {
            TRANG_THAI.remove(nguoiChoi.ma);
            if (luaChon >= 0 && luaChon <= 3) {
                ChickenQuanLyPhong.yeuCauDanhSachPhong(nguoiChoi);
            }
            return true;
        }

        TRANG_THAI.remove(nguoiChoi.ma);
        return false;
    }

    public static void xoaTrangThai(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi != null) {
            TRANG_THAI.remove(nguoiChoi.ma);
        }
    }

    private static final class TrangThaiMenu {
        private final int menu;
        private final long hetHan;

        private TrangThaiMenu(int menu, long hetHan) {
            this.menu = menu;
            this.hetHan = hetHan;
        }
    }
}
