package com.chicken.chien;

import com.chicken.loi.ChickenQuanLyMayChu;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Công thức May mắn dùng chung cho toàn bộ chiến đấu.
 *
 * <p>Client chỉ nhận hiệu ứng. Tỉ lệ và sát thương luôn được server tính:
 * mỗi 10 điểm tương ứng 1%, tối đa 100%. Một lần tấn công chỉ quay một lần
 * cho phía tấn công và một lần cho mỗi mục tiêu phòng thủ.</p>
 */
public final class ChickenMayMan {

    public static final int DIEM_MOI_PHAN_TRAM = 10;
    public static final int TI_LE_TOI_DA_PHAN_TRAM = 100;
    /**
     * Chế độ test tạm thời: chỉ cần có 1 điểm May mắn là kích hoạt 100%.
     * Đổi thành false để trở lại công thức 10 điểm = 1%.
     */
    public static final boolean CHE_DO_TEST_MOT_DIEM_FULL =
            ChickenCheDoTestChienDau.MAY_MAN_MOT_DIEM_FULL;
    private static final int MAU_SO = 10_000;

    @FunctionalInterface
    public interface BoTaoSo {

        int tao(int gioiHan);
    }

    private static final BoTaoSo NGAU_NHIEN =
            gioiHan -> ThreadLocalRandom.current().nextInt(gioiHan);

    private ChickenMayMan() {
    }

    public static int tinhTiLePhanTram(int diemMayMan) {
        if (CHE_DO_TEST_MOT_DIEM_FULL) {
            return diemMayMan > 0 ? 100 : 0;
        }
        return Math.min(
                TI_LE_TOI_DA_PHAN_TRAM,
                Math.max(0, diemMayMan) / DIEM_MOI_PHAN_TRAM
        );
    }

    public static boolean kichHoat(int diemMayMan, int giaTriTuKhongDen9999) {
        int moc = tinhTiLePhanTram(diemMayMan) * 100;
        int giaTri = Math.max(0, Math.min(MAU_SO - 1, giaTriTuKhongDen9999));
        return moc > 0 && giaTri < moc;
    }

    public static PhienTanCong batDau(
            ChickenChienBinh nguoiTanCong,
            ChickenChienBinh[] chienBinhs
    ) {
        return new PhienTanCong(nguoiTanCong, chienBinhs, NGAU_NHIEN);
    }

    /** Chỉ dành cho testcase để kết quả quay May mắn có thể lặp lại. */
    public static PhienTanCong batDauChoKiemThu(
            ChickenChienBinh nguoiTanCong,
            ChickenChienBinh[] chienBinhs,
            BoTaoSo boTaoSo
    ) {
        return new PhienTanCong(nguoiTanCong, chienBinhs, boTaoSo);
    }

    public static final class PhienTanCong {

        private final ChickenChienBinh nguoiTanCong;
        private final ChickenChienBinh[] chienBinhs;
        private final BoTaoSo boTaoSo;
        private final boolean tangGapDoi;
        /** POW la he so rieng, duoc phep cong don voi May man thanh x4. */
        private final boolean powGapDoi;
        private final Map<ChickenChienBinh, Boolean> giamMotNua =
                new IdentityHashMap<>();
        private final Set<ChickenChienBinh> daPhatHieuUng =
                Collections.newSetFromMap(new IdentityHashMap<>());

        private PhienTanCong(
                ChickenChienBinh nguoiTanCong,
                ChickenChienBinh[] chienBinhs,
                BoTaoSo boTaoSo
        ) {
            this.nguoiTanCong = nguoiTanCong;
            this.chienBinhs = chienBinhs == null
                    ? new ChickenChienBinh[0] : chienBinhs;
            this.boTaoSo = boTaoSo == null ? NGAU_NHIEN : boTaoSo;
            this.powGapDoi = nguoiTanCong != null
                    && nguoiTanCong.nguoiChoi != null
                    && nguoiTanCong.nguoiChoi.tieuThuPowChoTanCong();
            this.tangGapDoi = quay(nguoiTanCong);
            /*
             * Hiệu ứng phía tấn công là động tác báo trước. Phát ngay khi
             * server đã chốt lần quay, trước packet đạn; không đợi tới lúc
             * kết toán va chạm/damage ở apDung().
             */
            if (this.tangGapDoi) {
                phatHieuUngMotLan(this.nguoiTanCong);
            }
            /*
             * Phòng thủ được quay cho toàn bộ người chơi ngay ở nhịp chuẩn bị
             * bắn. Ai kích hoạt đều bung sao trước packet đạn, kể cả phát đó
             * sau cùng không chạm vào họ. Khi kết toán chỉ dùng lại kết quả đã
             * chốt, tuyệt đối không quay thêm sau va chạm.
             */
            for (ChickenChienBinh chienBinh : this.chienBinhs) {
                chuanBiPhongThuTruocPhat(chienBinh);
            }
        }

        /**
         * Áp dụng May mắn lên damage cuối của một mục tiêu.
         * Tấn công x2 trước, phòng thủ giảm nửa sau; kết quả luôn ít nhất 1.
         */
        public int apDung(ChickenChienBinh mucTieu, int satThuongGoc) {
            if (mucTieu == null || satThuongGoc <= 0) {
                return 0;
            }
            long satThuong = satThuongGoc;
            if (this.powGapDoi) {
                satThuong = Math.min(Integer.MAX_VALUE, satThuong * 2L);
            }
            if (this.tangGapDoi) {
                satThuong = Math.min(Integer.MAX_VALUE, satThuong * 2L);
            }
            boolean phongThuKichHoat = this.giamMotNua.computeIfAbsent(
                    mucTieu, this::quay);
            if (phongThuKichHoat) {
                satThuong = Math.max(1L, (satThuong + 1L) / 2L);
                phatHieuUngMotLan(mucTieu);
            }
            return (int) Math.max(1L, Math.min(Integer.MAX_VALUE, satThuong));
        }

        /**
         * Chốt và phát hiệu ứng phòng thủ trước packet đạn cho đúng những mục
         * tiêu mà quỹ đạo authoritative của server đã xác định sẽ nhận damage.
         */
        public void chuanBiPhongThuTruocPhat(
                Iterable<ChickenChienBinh> cacMucTieu
        ) {
            if (cacMucTieu == null) {
                return;
            }
            for (ChickenChienBinh mucTieu : cacMucTieu) {
                chuanBiPhongThuTruocPhat(mucTieu);
            }
        }

        public void chuanBiPhongThuTruocPhat(
                ChickenChienBinh mucTieu
        ) {
            if (mucTieu == null || this.giamMotNua.containsKey(mucTieu)) {
                return;
            }
            boolean kichHoat = quay(mucTieu);
            this.giamMotNua.put(mucTieu, kichHoat);
            if (kichHoat) {
                phatHieuUngMotLan(mucTieu);
            }
        }

        public boolean tanCongDaKichHoat() {
            return this.tangGapDoi;
        }

        public boolean powDaKichHoat() {
            return this.powGapDoi;
        }

        public boolean phongThuDaKichHoat(ChickenChienBinh mucTieu) {
            return Boolean.TRUE.equals(this.giamMotNua.get(mucTieu));
        }

        private boolean quay(ChickenChienBinh chienBinh) {
            if (chienBinh == null || chienBinh.chet || chienBinh.mayMan <= 0) {
                return false;
            }
            int giaTri = this.boTaoSo.tao(MAU_SO);
            return kichHoat(chienBinh.mayMan, giaTri);
        }

        private void phatHieuUngMotLan(ChickenChienBinh chienBinh) {
            if (chienBinh == null || !this.daPhatHieuUng.add(chienBinh)) {
                return;
            }
            for (ChickenChienBinh nguoiNhan : this.chienBinhs) {
                if (nguoiNhan != null
                        && nguoiNhan.coPhien()
                        && nguoiNhan.nguoiChoi.dichVu != null) {
                    try {
                        nguoiNhan.nguoiChoi.dichVu.guiHieuUngMayMan(
                                chienBinh.chiSo);
                    } catch (Exception loi) {
                        ChickenQuanLyMayChu.log(
                                "[MAY_MAN][LOI_HIEU_UNG] effectSlot="
                                + (chienBinh.chiSo & 0xFF)
                                + " receiverSlot="
                                + (nguoiNhan.chiSo & 0xFF)
                                + " loi="
                                + loi.getClass().getSimpleName());
                    }
                }
            }
        }
    }
}
