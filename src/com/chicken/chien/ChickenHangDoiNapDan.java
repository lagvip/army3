package com.chicken.chien;

/**
 * Bộ lập lịch lượt theo nạp đạn dùng chung cho toàn bộ chế độ chiến đấu.
 *
 * <p>Khi nhiều chiến binh sẵn sàng tại cùng một mốc, người đã thực hiện
 * hành động trước phải được đi trước (FIFO). Không được phá hòa theo slot
 * đứng sau người vừa bắn vì một chiến binh nạp nhanh chen giữa sẽ làm đảo
 * thứ tự của toàn bộ nhóm có cùng tốc độ.</p>
 */
public final class ChickenHangDoiNapDan {

    @FunctionalInterface
    public interface KiemTraSlot {
        boolean hopLe(int slot);
    }

    private ChickenHangDoiNapDan() {
    }

    /**
     * Ghi lại thứ tự chiến binh vừa hoàn tất hành động và bắt đầu nạp.
     *
     * @return số thứ tự mới để trận giữ làm bộ đếm cho lần kế tiếp
     */
    public static long ghiNhanHanhDong(
            long[] thuTuHanhDong,
            int slot,
            long thuTuHienTai
    ) {
        if (thuTuHanhDong == null
                || slot < 0 || slot >= thuTuHanhDong.length) {
            return thuTuHienTai;
        }
        long thuTuMoi = thuTuHienTai + 1L;
        if (thuTuMoi <= 0L) {
            // Một trận không thể thực hiện tới Long.MAX_VALUE lượt. Nhánh này
            // chỉ giữ thứ tự hợp lệ nếu bộ đếm từng bị sửa/hỏng.
            thuTuMoi = 1L;
            for (int i = 0; i < thuTuHanhDong.length; i++) {
                thuTuHanhDong[i] = 0L;
            }
        }
        thuTuHanhDong[slot] = thuTuMoi;
        return thuTuMoi;
    }

    /**
     * Chọn slot kế tiếp theo đồng hồ nạp đạn và FIFO khi bằng nhau.
     */
    public static int timSlotTiepTheo(
            int[] napDan,
            long[] thuTuHanhDong,
            int sauSlot,
            KiemTraSlot kiemTraSlot
    ) {
        if (napDan == null || thuTuHanhDong == null
                || napDan.length != thuTuHanhDong.length
                || kiemTraSlot == null) {
            return -1;
        }

        int sanSang = timSlotSanSang(
                napDan, thuTuHanhDong, sauSlot, kiemTraSlot);
        if (sanSang >= 0) {
            return sanSang;
        }

        int nhoNhat = Integer.MAX_VALUE;
        for (int slot = 0; slot < napDan.length; slot++) {
            if (kiemTraSlot.hopLe(slot) && napDan[slot] > 0) {
                nhoNhat = Math.min(nhoNhat, napDan[slot]);
            }
        }
        if (nhoNhat == Integer.MAX_VALUE) {
            return -1;
        }
        for (int slot = 0; slot < napDan.length; slot++) {
            if (kiemTraSlot.hopLe(slot)) {
                napDan[slot] = Math.max(0, napDan[slot] - nhoNhat);
            }
        }
        return timSlotSanSang(
                napDan, thuTuHanhDong, sauSlot, kiemTraSlot);
    }

    private static int timSlotSanSang(
            int[] napDan,
            long[] thuTuHanhDong,
            int sauSlot,
            KiemTraSlot kiemTraSlot
    ) {
        int soSlot = napDan.length;
        int batDau = sauSlot < 0 ? 0 : (sauSlot + 1) % soSlot;
        int chon = -1;
        long thuTuNhoNhat = Long.MAX_VALUE;

        for (int buoc = 0; buoc < soSlot; buoc++) {
            int slot = (batDau + buoc) % soSlot;
            if (!kiemTraSlot.hopLe(slot) || napDan[slot] > 0) {
                continue;
            }
            long thuTu = Math.max(0L, thuTuHanhDong[slot]);
            if (chon < 0 || thuTu < thuTuNhoNhat) {
                chon = slot;
                thuTuNhoNhat = thuTu;
            }
        }
        return chon;
    }
}
