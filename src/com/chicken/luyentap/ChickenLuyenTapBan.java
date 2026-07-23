package com.chicken.luyentap;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Chỉ quản lý số viên, khoảng cách giữa các viên và điểm dừng của một loạt MG.
 *
 * Mỗi task chỉ chạy đúng một lần. Không dùng scheduleAtFixedRate nên một lần
 * bóp cò không thể tự lặp thành nhiều loạt vô hạn.
 */
public final class ChickenLuyenTapBan {

    public static final int SO_VIEN_MG = 5;
    public static final long KHOANG_CACH_VIEN_MG_MS = 10L;
    private static final int SO_VIEN_TOI_DA_MOI_LOAT = 32;

    @FunctionalInterface
    public interface GuiMotVienMg {
        void gui(int chiSoVien) throws IOException;
    }

    @FunctionalInterface
    public interface KiemTraLoatMg {
        boolean conHieuLuc();
    }

    private ChickenLuyenTapBan() {
    }

    /**
     * Gửi một loạt hữu hạn. Viên đầu gửi ngay, các viên sau là task một lần.
     *
     * @return độ trễ từ viên đầu đến thời điểm gửi viên cuối.
     */
    public static long guiLoatDanCoGioiHan(
            ScheduledExecutorService boHenGio,
            List<ScheduledFuture<?>> tasks,
            int tongSoVien,
            long khoangCachVienMs,
            KiemTraLoatMg kiemTra,
            GuiMotVienMg guiVien
    ) throws IOException {
        if (boHenGio == null || tasks == null || kiemTra == null || guiVien == null) {
            return 0L;
        }

        huyTasks(tasks);

        final int soVien = Math.max(1, Math.min(SO_VIEN_TOI_DA_MOI_LOAT, tongSoVien));
        final long khoangCach = Math.max(1L, khoangCachVienMs);

        if (!kiemTra.conHieuLuc()) {
            return 0L;
        }

        // Viên 0 gửi ngay. Bộ đếm thật nằm trong phiên luyện tập và được tăng
        // chỉ sau khi packet của viên đó đã gửi thành công.
        guiVien.gui(0);

        for (int i = 1; i < soVien; i++) {
            final int chiSoVien = i;
            ScheduledFuture<?> task = boHenGio.schedule(() -> {
                try {
                    if (!kiemTra.conHieuLuc()) {
                        return;
                    }
                    guiVien.gui(chiSoVien);
                } catch (IOException ex) {
                    huyTasks(tasks);
                    Logger.getLogger(ChickenLuyenTapBan.class.getName())
                            .log(Level.SEVERE, "Không gửi được viên MG thứ " + chiSoVien, ex);
                }
            }, khoangCach * i, TimeUnit.MILLISECONDS);

            synchronized (tasks) {
                tasks.add(task);
            }
        }

        return khoangCach * (soVien - 1L);
    }

    /** Hủy toàn bộ viên chưa gửi của loạt cũ. */
    public static void huyTasks(List<ScheduledFuture<?>> tasks) {
        if (tasks == null) {
            return;
        }
        synchronized (tasks) {
            for (ScheduledFuture<?> task : tasks) {
                if (task != null) {
                    task.cancel(false);
                }
            }
            tasks.clear();
        }
    }
}
