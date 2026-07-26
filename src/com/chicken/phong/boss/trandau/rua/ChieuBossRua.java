package com.chicken.phong.boss.trandau.rua;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.avg.ChickenCoCheBayAVG;
import com.chicken.chien.ChickenChienBinh;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Quy tắc chọn chiêu và tính sát thương riêng của Boss Rùa.
 * Bộ đếm lượt được giữ trong từng trận; class này không chứa trạng thái dùng
 * chung giữa các phòng.
 */
public final class ChieuBossRua {
    public enum LoaiChieu {
        BAN_THUONG,
        DAM_DA
    }

    private ChieuBossRua() {
    }

    public static LoaiChieu chonChoLuot(int soLuot) {
        return chonChoLuot(
                soLuot,
                ThreadLocalRandom.current().nextBoolean()
        );
    }

    /** Hàm tách riêng tham số random để testcase kiểm tra đủ cả hai nhánh. */
    public static LoaiChieu chonChoLuot(
            int soLuot,
            boolean randomChonDamDa
    ) {
        if (soLuot <= 1) {
            return LoaiChieu.BAN_THUONG;
        }
        if (soLuot == 2) {
            return LoaiChieu.DAM_DA;
        }
        return randomChonDamDa ? LoaiChieu.DAM_DA : LoaiChieu.BAN_THUONG;
    }

    public static int tinhSatThuongDamDa(
            ChickenChienBinh rua,
            ChickenChienBinh mucTieu
    ) {
        if (rua == null || mucTieu == null) {
            return 0;
        }
        return Math.max(1, rua.tanCong - Math.max(0, mucTieu.giap));
    }

    public static void ghimMucTieu(
            ChickenChienBinh mucTieu,
            short xDa,
            short yDa
    ) {
        if (mucTieu == null) {
            return;
        }
        /*
         * Iron Man/Ultron chỉ được miễn va chạm khi quyền bay đã được server
         * chốt từ trang bị thật. Không dùng riêng avenger vì client có thể fake ID.
         */
        if (ChickenCoCheBayAVG.coTheBay(mucTieu)) {
            boGhim(mucTieu);
            return;
        }
        mucTieu.biDaRuaGhim = true;
        mucTieu.xDaRuaGhim = xDa;
        mucTieu.yDaRuaGhim = yDa;
    }

    /**
     * Trạng thái ghim là authoritative ở server. Khi lõi tảng đá đã bị phá,
     * cờ được xóa tại lần kiểm tra kế tiếp và người chơi lại được di chuyển.
     */
    public static boolean dangBiDaRuaGhim(
            ChickenChienBinh mucTieu,
            ChickenQuanLyBanDo banDo
    ) {
        if (mucTieu == null || !mucTieu.biDaRuaGhim || banDo == null) {
            return false;
        }
        if (ChickenCoCheBayAVG.coTheBay(mucTieu)) {
            boGhim(mucTieu);
            return false;
        }
        if (banDo.conDaRuaGhimTai(
                mucTieu.xDaRuaGhim, mucTieu.yDaRuaGhim)) {
            return true;
        }
        boGhim(mucTieu);
        return false;
    }

    private static void boGhim(ChickenChienBinh mucTieu) {
        mucTieu.biDaRuaGhim = false;
        mucTieu.xDaRuaGhim = 0;
        mucTieu.yDaRuaGhim = 0;
    }
}
