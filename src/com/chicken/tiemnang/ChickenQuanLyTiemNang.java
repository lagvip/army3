package com.chicken.tiemnang;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.tienich.ChickenTienIch;
import com.chicken.vatpham.ChickenVatPham;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Quản lý điểm tiềm năng nhân vật.
 * Thứ tự packet/client: Máu, Giáp, Tấn công, May mắn, Đồng đội, Tốc độ.
 */
public final class ChickenQuanLyTiemNang {

    public static final int SO_CHI_SO = 6;
    public static final int MAU = 0;
    public static final int GIAP = 1;
    public static final int TAN_CONG = 2;
    public static final int MAY_MAN = 3;
    public static final int DONG_DOI = 4;
    public static final int TOC_DO = 5;

    private static final short MAU_MAC_DINH = 1000;
    private static final byte[] MUC_TANG = {10, 1, 1, 1, 1, 1};
    /*
     * May mắn chưa có công thức authoritative nên vẫn khóa. Đồng đội đã
     * được server chốt lúc tạo trận và chỉ buff Máu/Tấn công/Giáp.
     */
    private static final boolean[] CHI_SO_DANG_HOAT_DONG = {
        true, true, true, true, true, true
    };
    private static final int MA_VAT_PHAM_TAY_DIEM = 256;

    /** Toàn bộ cấu hình thưởng lên cấp đặt ngay trong file này. */
    public static final int DIEM_TIEM_NANG_MOI_CAP = 10;
    public static final int NGOC_TIM_MOI_CAP = 10;
    public static final int DONG_BO_THAT_BAI = Integer.MIN_VALUE;

    /**
     * Biên lưu trữ để production dùng JDBC còn testcase có thể mô phỏng lỗi
     * database mà không chạm dữ liệu thật.
     */
    public interface KhoDuLieu {

        boolean luuPhanBo(ChickenNguoiChoi nguoiChoi);

        boolean luuTayDiem(ChickenNguoiChoi nguoiChoi);
    }

    private static final KhoDuLieu KHO_JDBC = new KhoDuLieu() {
        @Override
        public boolean luuPhanBo(ChickenNguoiChoi nguoiChoi) {
            return nguoiChoi.luuTiemNangCoKetQua();
        }

        @Override
        public boolean luuTayDiem(ChickenNguoiChoi nguoiChoi) {
            return nguoiChoi.luuTiemNangVaTuiDoCoKetQua();
        }
    };
    private static volatile KhoDuLieu khoDuLieu = KHO_JDBC;
    private static volatile boolean daKhoiTaoKhoaDaMayChu;

    private ChickenQuanLyTiemNang() {
    }

    /**
     * Migration idempotent cho optimistic locking của stats_json.
     */
    public static synchronized void khoiTaoKhoaDaMayChu() {
        if (daKhoiTaoKhoaDaMayChu) {
            return;
        }
        String sql = "ALTER TABLE `players` ADD COLUMN IF NOT EXISTS "
                + "`stats_revision` BIGINT NOT NULL DEFAULT 0";
        try (Connection ketNoi = ChickenCoSoDuLieu.getConnection();
                Statement lenh = ketNoi.createStatement()) {
            lenh.execute(sql);
            daKhoiTaoKhoaDaMayChu = true;
        } catch (SQLException loi) {
            throw new IllegalStateException(
                    "Khong khoi tao duoc stats_revision", loi);
        }
    }

    /**
     * Cộng EXP và kiểm tra level hiện tại. Chưa trả thưởng tại đây.
     */
    public static KetQuaKiemTraCap congExpVaKiemTraLenCap(
            ChickenNguoiChoi nguoiChoi,
            int expCong
    ) {
        if (nguoiChoi == null || expCong <= 0) {
            return KetQuaKiemTraCap.KHONG_TANG_CAP;
        }
        synchronized (nguoiChoi) {
            int capCu = Math.max(0, nguoiChoi.cap);
            long expMoi = (long) nguoiChoi.layKinhNghiem() + expCong;
            nguoiChoi.datKinhNghiemVaCanBangTrongBoNho(
                    (int) Math.min(Integer.MAX_VALUE, expMoi));

            int capMoi = ChickenTienIch.layCap(
                    nguoiChoi.layKinhNghiem());
            nguoiChoi.cap = capMoi;

            int mocDaNhan =
                    Math.max(0, nguoiChoi.capCaoNhatDaNhanThuong);
            int capBatDauThuong = Math.max(capCu, mocDaNhan);
            int soCapChuaNhanThuong =
                    Math.max(0, capMoi - capBatDauThuong);
            return new KetQuaKiemTraCap(
                    capCu, capMoi, soCapChuaNhanThuong);
        }
    }

    /**
     * Trả thưởng cho các level chưa từng nhận.
     */
    public static KetQuaLenCap congThuongLenCapChuaNhan(
            ChickenNguoiChoi nguoiChoi,
            KetQuaKiemTraCap kiemTra
    ) {
        if (nguoiChoi == null || kiemTra == null) {
            return KetQuaLenCap.KHONG_TANG_CAP;
        }
        synchronized (nguoiChoi) {
            int soCapThuong = Math.max(0, kiemTra.soCapChuaNhanThuong);
            int diemTiemNangCong = 0;
            int ngocTimCong = 0;

            if (soCapThuong > 0) {
                baoDamDuLieuHopLe(nguoiChoi);
                diemTiemNangCong = nhanAnToan(
                        soCapThuong, DIEM_TIEM_NANG_MOI_CAP);
                ngocTimCong = nhanAnToan(
                        soCapThuong, NGOC_TIM_MOI_CAP);

                nguoiChoi.point = (short) Math.min(Short.MAX_VALUE,
                        Math.max(0L,
                                (long) nguoiChoi.point + diemTiemNangCong));
                nguoiChoi.ngoc = (int) Math.min(Integer.MAX_VALUE,
                        Math.max(0L,
                                (long) nguoiChoi.ngoc + ngocTimCong));
                nguoiChoi.capCaoNhatDaNhanThuong = Math.max(
                        nguoiChoi.capCaoNhatDaNhanThuong, kiemTra.capMoi);
            }

            /*
             * Diem tiem nang bam level hien tai, khong bam moc thuong cao
             * nhat. Vi vay nguoi choi tung tut cap se duoc phuc hoi dung phan
             * diem khi tang level tro lai, nhung khong nhan lai ngoc tim.
             */
            canBangTongDiemTrongBoNho(
                    nguoiChoi, Math.max(0, nguoiChoi.cap));

            /*
             * flushCache nay được bao bởi transaction. EXP, mốc thưởng,
             * điểm và ngọc không còn có thể commit nửa chừng.
             */
            nguoiChoi.flushCache();
            return new KetQuaLenCap(
                    soCapThuong, diemTiemNangCong, ngocTimCong);
        }
    }

    public static final class KetQuaKiemTraCap {

        public static final KetQuaKiemTraCap KHONG_TANG_CAP
                = new KetQuaKiemTraCap(0, 0, 0);

        public final int capCu;
        public final int capMoi;
        public final int soCapChuaNhanThuong;

        private KetQuaKiemTraCap(
                int capCu,
                int capMoi,
                int soCapChuaNhanThuong
        ) {
            this.capCu = capCu;
            this.capMoi = capMoi;
            this.soCapChuaNhanThuong = soCapChuaNhanThuong;
        }

        public boolean coCapMoiChuaNhanThuong() {
            return this.soCapChuaNhanThuong > 0;
        }
    }

    public static final class KetQuaLenCap {

        public static final KetQuaLenCap KHONG_TANG_CAP
                = new KetQuaLenCap(0, 0, 0);

        public final int soCapTang;
        public final int diemTiemNangCong;
        public final int ngocTimCong;

        private KetQuaLenCap(
                int soCapTang,
                int diemTiemNangCong,
                int ngocTimCong
        ) {
            this.soCapTang = soCapTang;
            this.diemTiemNangCong = diemTiemNangCong;
            this.ngocTimCong = ngocTimCong;
        }

        public boolean coTangCap() {
            return this.soCapTang > 0;
        }
    }

    private static int nhanAnToan(int a, int b) {
        return (int) Math.min(Integer.MAX_VALUE,
                Math.max(0L, (long) Math.max(0, a) * Math.max(0, b)));
    }

    /**
     * CMD -46:
     * - [0] mở bảng;
     * - [1, chỉ-số] cộng đúng một điểm.
     */
    public static void xuLyNangCap(
            ChickenNguoiChoi nguoiChoi,
            ChickenTinNhan tinNhan
    ) throws IOException {
        if (nguoiChoi == null || tinNhan == null) {
            throw new IllegalArgumentException(
                    "Thieu nguoi choi/packet tiem nang");
        }
        int soByte = tinNhan.boDoc().available();
        if (soByte != 1 && soByte != 2) {
            throw new IllegalArgumentException("Sai kich thuoc CMD -46");
        }
        int loai = tinNhan.boDoc().readUnsignedByte();
        if (loai == 0) {
            if (soByte != 1) {
                throw new IllegalArgumentException(
                        "Packet mo bang tiem nang co du lieu thua");
            }
            synchronized (nguoiChoi) {
                baoDamDuLieuHopLe(nguoiChoi);
                guiBangTiemNang(nguoiChoi);
            }
            return;
        }
        if (loai != 1 || soByte != 2) {
            throw new IllegalArgumentException("Sai action CMD -46");
        }
        int chiSo = tinNhan.boDoc().readUnsignedByte();
        if (tinNhan.boDoc().available() != 0 || !laChiSoHopLe(chiSo)) {
            throw new IllegalArgumentException("Sai chi so tiem nang");
        }

        synchronized (nguoiChoi) {
            baoDamDuLieuHopLe(nguoiChoi);
            if (!CHI_SO_DANG_HOAT_DONG[chiSo]) {
                nguoiChoi.moHopThoaiOK(
                        "Chỉ số này chưa mở. Hệ thống không trừ điểm.");
                return;
            }
            if (nguoiChoi.point <= 0) {
                nguoiChoi.moHopThoaiOK("Không đủ điểm cộng.");
                return;
            }

            short diemCu = nguoiChoi.point;
            short giaTriCu = nguoiChoi.pointAdd[chiSo];
            int giaTriMoi = Math.min(
                    Short.MAX_VALUE, giaTriCu + MUC_TANG[chiSo]);
            if (giaTriMoi <= giaTriCu) {
                nguoiChoi.moHopThoaiOK(
                        "Chỉ số đã đạt giới hạn, không trừ điểm.");
                return;
            }

            nguoiChoi.pointAdd[chiSo] = (short) giaTriMoi;
            nguoiChoi.point = (short) (diemCu - 1);
            if (!khoDuLieu.luuPhanBo(nguoiChoi)) {
                nguoiChoi.point = diemCu;
                nguoiChoi.pointAdd[chiSo] = giaTriCu;
                nguoiChoi.moHopThoaiOK(
                        "Không thể lưu điểm tiềm năng. Vui lòng thử lại.");
                return;
            }

            ChickenQuanLyMayChu.log(
                    "[KINH_TE][TIEM_NANG_CONG]"
                    + " playerId=" + nguoiChoi.ma
                    + " chiSo=" + chiSo
                    + " truoc=" + giaTriCu
                    + " sau=" + giaTriMoi
                    + " diemConLai=" + nguoiChoi.point);
            guiBangTiemNang(nguoiChoi);
        }
    }

    /**
     * Hoàn điểm và trừ vật phẩm 256 trong cùng transaction.
     */
    public static boolean tayDiemBangVatPham(
            ChickenNguoiChoi nguoiChoi,
            int chiSoTui
    ) throws IOException {
        if (nguoiChoi == null || nguoiChoi.itemBag == null
                || chiSoTui < 0 || chiSoTui >= nguoiChoi.itemBag.length) {
            return false;
        }
        synchronized (nguoiChoi) {
            baoDamDuLieuHopLe(nguoiChoi);
            ChickenVatPham vatPham = nguoiChoi.itemBag[chiSoTui];
            if (vatPham == null || vatPham.ma != MA_VAT_PHAM_TAY_DIEM
                    || vatPham.soLuong <= 0) {
                return false;
            }

            int diemHoanLai = tinhDiemHoanLai(nguoiChoi);
            if (diemHoanLai <= 0) {
                nguoiChoi.moHopThoaiOK(
                        "Bạn chưa cộng điểm nào, vật phẩm không bị trừ.");
                return false;
            }
            long diemSauTay = (long) nguoiChoi.point + diemHoanLai;
            if (diemSauTay > Short.MAX_VALUE) {
                nguoiChoi.moHopThoaiOK(
                        "Không thể tẩy điểm vì điểm hoàn lại vượt giới hạn.");
                return false;
            }

            short diemCu = nguoiChoi.point;
            short[] chiSoCu = Arrays.copyOf(
                    nguoiChoi.pointAdd, nguoiChoi.pointAdd.length);
            int soLuongCu = vatPham.soLuong;

            nguoiChoi.point = (short) diemSauTay;
            datMacDinh(nguoiChoi);
            if (vatPham.soLuong == 1) {
                nguoiChoi.itemBag[chiSoTui] = null;
            } else {
                vatPham.soLuong--;
            }

            if (!khoDuLieu.luuTayDiem(nguoiChoi)) {
                nguoiChoi.point = diemCu;
                nguoiChoi.pointAdd = chiSoCu;
                vatPham.soLuong = soLuongCu;
                nguoiChoi.itemBag[chiSoTui] = vatPham;
                nguoiChoi.moHopThoaiOK(
                        "Không thể lưu tẩy điểm. Vật phẩm chưa bị trừ.");
                return false;
            }

            int soLuongConLai = nguoiChoi.itemBag[chiSoTui] == null
                    ? 0 : nguoiChoi.itemBag[chiSoTui].soLuong;
            ChickenQuanLyMayChu.log(
                    "[KINH_TE][TIEM_NANG_TAY]"
                    + " playerId=" + nguoiChoi.ma
                    + " diemHoan=" + diemHoanLai
                    + " diemMoi=" + nguoiChoi.point
                    + " itemConLai=" + soLuongConLai);
            nguoiChoi.dichVu.capNhatTuiDo(chiSoTui, soLuongConLai);
            guiBangTiemNang(nguoiChoi);
            nguoiChoi.moHopThoaiOK("Tẩy điểm thành công.");
            return true;
        }
    }

    public static int layGiaTri(ChickenNguoiChoi nguoiChoi, int chiSo) {
        baoDamDuLieuHopLe(nguoiChoi);
        if (nguoiChoi == null || !laChiSoHopLe(chiSo)) {
            return 0;
        }
        return Math.max(0, nguoiChoi.pointAdd[chiSo]);
    }

    public static int layTanCongDaCong(ChickenNguoiChoi nguoiChoi) {
        return layGiaTri(nguoiChoi, TAN_CONG);
    }

    /**
     * Migration idempotent khi bảng EXP/level đổi phiên bản.
     *
     * Tổng điểm đã sở hữu gồm điểm chưa cộng và toàn bộ điểm đang nằm trong
     * sáu chỉ số. Server bù hoặc thu hồi để tổng này luôn bằng level hiện tại
     * nhân điểm mỗi cấp; không dựa vào level mà client tự khai báo.
     *
     * @return delta điểm: dương khi bù, âm khi thu hồi, 0 khi đã đúng;
     * DONG_BO_THAT_BAI nếu lưu database thất bại.
     */
    public static int dongBoQuyenLoiTheoCapHienTai(
            ChickenNguoiChoi nguoiChoi
    ) {
        if (nguoiChoi == null) {
            return 0;
        }
        synchronized (nguoiChoi) {
            baoDamDuLieuHopLe(nguoiChoi);
            int capServer = ChickenTienIch.layCap(
                    nguoiChoi.layKinhNghiem());
            short diemCu = nguoiChoi.point;
            short[] phanBoCu = Arrays.copyOf(
                    nguoiChoi.pointAdd, nguoiChoi.pointAdd.length);
            int capCu = nguoiChoi.cap;
            int mocCu = nguoiChoi.capCaoNhatDaNhanThuong;
            int mocMoi = Math.max(mocCu, capServer);
            int tongTruoc = tinhTongDiemDangSoHuu(nguoiChoi);

            nguoiChoi.cap = capServer;
            int thayDoi = canBangTongDiemTrongBoNho(
                    nguoiChoi, capServer);
            nguoiChoi.capCaoNhatDaNhanThuong = mocMoi;
            if (thayDoi == 0 && mocMoi == mocCu) {
                return 0;
            }

            if (!khoDuLieu.luuPhanBo(nguoiChoi)) {
                nguoiChoi.point = diemCu;
                nguoiChoi.pointAdd = phanBoCu;
                nguoiChoi.cap = capCu;
                nguoiChoi.capCaoNhatDaNhanThuong = mocCu;
                return DONG_BO_THAT_BAI;
            }
            ChickenQuanLyMayChu.log(
                    "[KINH_TE][TIEM_NANG_DONG_BO_CAP]"
                    + " playerId=" + nguoiChoi.ma
                    + " cap=" + capServer
                    + " tongTruoc=" + tongTruoc
                    + " thayDoi=" + thayDoi
                    + " tongSau=" + tinhTongDiemDangSoHuu(nguoiChoi)
                    + " diemConLai=" + nguoiChoi.point);
            return thayDoi;
        }
    }

    /**
     * Gan EXP do server tinh va can bang diem tiem nang trong cung mot lan luu.
     * Dung ham nay cho cac logic tru EXP trong tuong lai; khong sua truc tiep
     * kinhNghiem roi de diem tiem nang o trang thai cu.
     *
     * @return delta diem tiem nang (am neu thu hoi), hoac DONG_BO_THAT_BAI.
     */
    public static int datKinhNghiemVaDongBoTiemNang(
            ChickenNguoiChoi nguoiChoi,
            int kinhNghiemMoi
    ) {
        if (nguoiChoi == null) {
            return 0;
        }
        synchronized (nguoiChoi) {
            int expCu = nguoiChoi.layKinhNghiem();
            int capCu = nguoiChoi.cap;
            int mocCu = nguoiChoi.capCaoNhatDaNhanThuong;
            short diemCu = nguoiChoi.point;
            short[] phanBoCu = Arrays.copyOf(
                    nguoiChoi.pointAdd, nguoiChoi.pointAdd.length);
            int ketQua = nguoiChoi.datKinhNghiemVaCanBangTrongBoNho(
                    kinhNghiemMoi);
            nguoiChoi.capCaoNhatDaNhanThuong = Math.max(
                    nguoiChoi.capCaoNhatDaNhanThuong, nguoiChoi.cap);

            boolean coThayDoi = expCu != nguoiChoi.layKinhNghiem()
                    || capCu != nguoiChoi.cap
                    || mocCu != nguoiChoi.capCaoNhatDaNhanThuong
                    || diemCu != nguoiChoi.point
                    || !Arrays.equals(phanBoCu, nguoiChoi.pointAdd);
            if (!coThayDoi) {
                return 0;
            }
            if (!khoDuLieu.luuPhanBo(nguoiChoi)) {
                nguoiChoi.napTrangThaiTiemNangTuKho(
                        expCu, mocCu, diemCu, phanBoCu,
                        nguoiChoi.layPhienBanThongKe());
                nguoiChoi.cap = capCu;
                return DONG_BO_THAT_BAI;
            }
            return ketQua;
        }
    }

    /**
     * Cân bằng invariant theo EXP hiện có mà không ghi database.
     * Chỉ ChickenNguoiChoi dùng trong các API thay đổi EXP nguyên tử.
     */
    public static int canBangTongDiemTheoExpTrongBoNho(
            ChickenNguoiChoi nguoiChoi
    ) {
        if (nguoiChoi == null) {
            return 0;
        }
        synchronized (nguoiChoi) {
            baoDamDuLieuHopLe(nguoiChoi);
            int capServer = ChickenTienIch.layCap(
                    nguoiChoi.layKinhNghiem());
            nguoiChoi.cap = capServer;
            return canBangTongDiemTrongBoNho(
                    nguoiChoi, capServer);
        }
    }

    public static void baoDamDuLieuHopLe(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return;
        }
        if (nguoiChoi.point < 0) {
            nguoiChoi.point = 0;
        }
        if (nguoiChoi.pointAdd == null
                || nguoiChoi.pointAdd.length != SO_CHI_SO) {
            short[] moi = new short[SO_CHI_SO];
            moi[MAU] = MAU_MAC_DINH;
            if (nguoiChoi.pointAdd != null) {
                System.arraycopy(
                        nguoiChoi.pointAdd, 0, moi, 0,
                        Math.min(nguoiChoi.pointAdd.length, moi.length));
            }
            nguoiChoi.pointAdd = moi;
        }
        if (nguoiChoi.pointAdd[MAU] < MAU_MAC_DINH) {
            nguoiChoi.pointAdd[MAU] = MAU_MAC_DINH;
        }
        for (int i = 1; i < SO_CHI_SO; i++) {
            if (nguoiChoi.pointAdd[i] < 0) {
                nguoiChoi.pointAdd[i] = 0;
            }
        }
    }

    private static void guiBangTiemNang(ChickenNguoiChoi nguoiChoi)
            throws IOException {
        ChickenTinNhan msg = new ChickenTinNhan(-46);
        DataOutputStream ds = msg.boGhi();
        ds.writeByte(0);
        ds.writeShort(nguoiChoi.point);
        for (byte mucTang : MUC_TANG) {
            ds.writeByte(mucTang);
        }
        for (int i = 0; i < SO_CHI_SO; i++) {
            ds.writeShort(nguoiChoi.pointAdd[i]);
        }
        ds.flush();
        nguoiChoi.dichVu.guiTin(msg);
    }

    private static void datMacDinh(ChickenNguoiChoi nguoiChoi) {
        Arrays.fill(nguoiChoi.pointAdd, (short) 0);
        nguoiChoi.pointAdd[MAU] = MAU_MAC_DINH;
    }

    private static int tinhDiemHoanLai(ChickenNguoiChoi nguoiChoi) {
        long diem = Math.max(
                0, nguoiChoi.pointAdd[MAU] - MAU_MAC_DINH)
                / MUC_TANG[MAU];
        for (int i = 1; i < SO_CHI_SO; i++) {
            diem += Math.max(0, nguoiChoi.pointAdd[i]) / MUC_TANG[i];
        }
        return (int) Math.min(Integer.MAX_VALUE, diem);
    }

    private static int tinhTongDiemDangSoHuu(
            ChickenNguoiChoi nguoiChoi
    ) {
        return Math.max(0, nguoiChoi.point)
                + tinhDiemHoanLai(nguoiChoi);
    }

    /**
     * Tong diem sau ham nay bang cap * 10.
     *
     * Khi thu hoi, diem chua cong bi tru truoc. Neu van thua, cac diem da
     * phan bo bi giam theo ty le de giu gan nhat build hien tai va khong tao
     * mot lan tay diem mien phi.
     */
    private static int canBangTongDiemTrongBoNho(
            ChickenNguoiChoi nguoiChoi,
            int capServer
    ) {
        int mucTieu = nhanAnToan(
                capServer, DIEM_TIEM_NANG_MOI_CAP);
        int tongTruoc = tinhTongDiemDangSoHuu(nguoiChoi);
        if (tongTruoc < mucTieu) {
            int canBu = mucTieu - tongTruoc;
            int diemMoi = (int) Math.min(
                    Short.MAX_VALUE,
                    (long) Math.max(0, nguoiChoi.point) + canBu);
            int daBu = diemMoi - Math.max(0, nguoiChoi.point);
            nguoiChoi.point = (short) diemMoi;
            return daBu;
        }
        if (tongTruoc == mucTieu) {
            return 0;
        }

        int canThuHoi = tongTruoc - mucTieu;
        int truDiemRanh = Math.min(
                Math.max(0, nguoiChoi.point), canThuHoi);
        nguoiChoi.point = (short) (
                Math.max(0, nguoiChoi.point) - truDiemRanh);
        int conPhaiThuHoi = canThuHoi - truDiemRanh;
        if (conPhaiThuHoi > 0) {
            thuHoiDiemDaPhanBoTheoTyLe(
                    nguoiChoi, conPhaiThuHoi);
        }
        int daThuHoi = tongTruoc
                - tinhTongDiemDangSoHuu(nguoiChoi);
        return -daThuHoi;
    }

    private static void thuHoiDiemDaPhanBoTheoTyLe(
            ChickenNguoiChoi nguoiChoi,
            int soDiemCanThu
    ) {
        int[] daCong = new int[SO_CHI_SO];
        int tongDaCong = 0;
        for (int i = 0; i < SO_CHI_SO; i++) {
            int giaTriCoSo = i == MAU ? MAU_MAC_DINH : 0;
            daCong[i] = Math.max(
                    0, nguoiChoi.pointAdd[i] - giaTriCoSo)
                    / MUC_TANG[i];
            tongDaCong += daCong[i];
        }
        int canThu = Math.min(
                Math.max(0, soDiemCanThu), tongDaCong);
        if (canThu <= 0 || tongDaCong <= 0) {
            return;
        }

        int[] thuHoi = new int[SO_CHI_SO];
        long[] phanDu = new long[SO_CHI_SO];
        int daChia = 0;
        for (int i = 0; i < SO_CHI_SO; i++) {
            long tich = (long) daCong[i] * canThu;
            thuHoi[i] = (int) (tich / tongDaCong);
            phanDu[i] = tich % tongDaCong;
            daChia += thuHoi[i];
        }
        for (int conLai = canThu - daChia; conLai > 0; conLai--) {
            int viTri = -1;
            for (int i = 0; i < SO_CHI_SO; i++) {
                if (thuHoi[i] >= daCong[i]) {
                    continue;
                }
                if (viTri < 0 || phanDu[i] > phanDu[viTri]) {
                    viTri = i;
                }
            }
            if (viTri < 0) {
                break;
            }
            thuHoi[viTri]++;
            phanDu[viTri] = -1;
        }

        for (int i = 0; i < SO_CHI_SO; i++) {
            if (thuHoi[i] <= 0) {
                continue;
            }
            int giaTriCoSo = i == MAU ? MAU_MAC_DINH : 0;
            int giaTriMoi = nguoiChoi.pointAdd[i]
                    - thuHoi[i] * MUC_TANG[i];
            nguoiChoi.pointAdd[i] =
                    (short) Math.max(giaTriCoSo, giaTriMoi);
        }
    }

    private static boolean laChiSoHopLe(int chiSo) {
        return chiSo >= 0 && chiSo < SO_CHI_SO;
    }

    public static void datKhoDuLieuChoKiemThu(KhoDuLieu khoMoi) {
        khoDuLieu = khoMoi == null ? KHO_JDBC : khoMoi;
    }
}
