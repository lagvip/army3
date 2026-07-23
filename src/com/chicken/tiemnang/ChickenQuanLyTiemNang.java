package com.chicken.tiemnang;

import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.tienich.ChickenTienIch;
import java.io.DataOutputStream;
import java.io.IOException;

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

    /** Toàn bộ cấu hình thưởng lên cấp đặt ngay trong file này. */
    public static final int DIEM_TIEM_NANG_MOI_CAP = 10;
    public static final int NGOC_TIM_MOI_CAP = 10;

    private ChickenQuanLyTiemNang() {
    }


    /**
     * LUỒNG 1: cộng EXP và kiểm tra level hiện tại.
     * Chỉ tính số level mới chưa từng nhận thưởng, chưa cộng thưởng tại đây.
     */
    public static KetQuaKiemTraCap congExpVaKiemTraLenCap(
            ChickenNguoiChoi nguoiChoi, int expCong) {
        if (nguoiChoi == null || expCong <= 0) {
            return KetQuaKiemTraCap.KHONG_TANG_CAP;
        }

        long expMoi = (long) nguoiChoi.kinhNghiem + expCong;
        nguoiChoi.kinhNghiem = (int) Math.min(Integer.MAX_VALUE, expMoi);

        int capCu = Math.max(0, nguoiChoi.cap);
        int capMoi = ChickenTienIch.layCap(nguoiChoi.kinhNghiem);
        nguoiChoi.cap = capMoi;

        int mocDaNhan = Math.max(0, nguoiChoi.capCaoNhatDaNhanThuong);
        int capBatDauThuong = Math.max(capCu, mocDaNhan);
        int soCapChuaNhanThuong = Math.max(0, capMoi - capBatDauThuong);

        return new KetQuaKiemTraCap(capCu, capMoi, soCapChuaNhanThuong);
    }

    /**
     * LUỒNG 2: cộng thưởng cho các level chưa từng nhận.
     * Sau khi cộng sẽ cập nhật mốc level cao nhất đã nhận và lưu ngay vào DB.
     * Vì vậy người chơi bị giảm level rồi tăng lại sẽ không nhận lại thưởng cũ.
     */
    public static KetQuaLenCap congThuongLenCapChuaNhan(
            ChickenNguoiChoi nguoiChoi, KetQuaKiemTraCap kiemTra) {
        if (nguoiChoi == null || kiemTra == null) {
            return KetQuaLenCap.KHONG_TANG_CAP;
        }

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
                    Math.max(0L, (long) nguoiChoi.point + diemTiemNangCong));
            nguoiChoi.ngoc = (int) Math.min(Integer.MAX_VALUE,
                    Math.max(0L, (long) nguoiChoi.ngoc + ngocTimCong));

            // Lưu mốc cấp cao nhất đã nhận thưởng. Mỗi level chỉ được thưởng một lần.
            nguoiChoi.capCaoNhatDaNhanThuong = Math.max(
                    nguoiChoi.capCaoNhatDaNhanThuong, kiemTra.capMoi);
        }

        // Lưu EXP, level, phần thưởng và mốc đã nhận trong cùng một lần.
        nguoiChoi.flushCache();

        return new KetQuaLenCap(
                soCapThuong, diemTiemNangCong, ngocTimCong);
    }

    public static final class KetQuaKiemTraCap {
        public static final KetQuaKiemTraCap KHONG_TANG_CAP =
                new KetQuaKiemTraCap(0, 0, 0);

        public final int capCu;
        public final int capMoi;
        public final int soCapChuaNhanThuong;

        private KetQuaKiemTraCap(int capCu, int capMoi, int soCapChuaNhanThuong) {
            this.capCu = capCu;
            this.capMoi = capMoi;
            this.soCapChuaNhanThuong = soCapChuaNhanThuong;
        }

        public boolean coCapMoiChuaNhanThuong() {
            return this.soCapChuaNhanThuong > 0;
        }
    }

    public static final class KetQuaLenCap {
        public static final KetQuaLenCap KHONG_TANG_CAP =
                new KetQuaLenCap(0, 0, 0);

        public final int soCapTang;
        public final int diemTiemNangCong;
        public final int ngocTimCong;

        private KetQuaLenCap(int soCapTang, int diemTiemNangCong, int ngocTimCong) {
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

    public static void xuLyNangCap(ChickenNguoiChoi nguoiChoi, ChickenTinNhan tinNhan)
            throws IOException {
        baoDamDuLieuHopLe(nguoiChoi);
        byte loai = tinNhan.boDoc().readByte();
        if (loai == 0) {
            guiBangTiemNang(nguoiChoi);
            return;
        }

        byte chiSo = tinNhan.boDoc().readByte();
        if (!laChiSoHopLe(chiSo)) {
            nguoiChoi.moHopThoaiOK("Có lỗi xảy ra.");
            return;
        }
        if (nguoiChoi.point <= 0) {
            nguoiChoi.moHopThoaiOK("Không đủ điểm cộng.");
            return;
        }

        int giaTriMoi = nguoiChoi.pointAdd[chiSo] + MUC_TANG[chiSo];
        nguoiChoi.pointAdd[chiSo] = (short) Math.min(Short.MAX_VALUE, giaTriMoi);
        nguoiChoi.point--;

        // Lưu ngay chỉ số vừa cộng và gửi lại bảng tiềm năng để client dùng
        // đúng giá trị mới. Các hàm tổng Máu/Tấn công/Giáp/... đọc trực tiếp
        // pointAdd nên trận kế tiếp sẽ lấy đúng chỉ số đã nâng.
        nguoiChoi.flushCache();
        guiBangTiemNang(nguoiChoi);
        nguoiChoi.dichVu.guiThongTin();
    }

    public static boolean tayDiem(ChickenNguoiChoi nguoiChoi) {
        baoDamDuLieuHopLe(nguoiChoi);
        int diemHoanLai = (nguoiChoi.pointAdd[MAU] - MAU_MAC_DINH) / MUC_TANG[MAU];
        for (int i = 1; i < SO_CHI_SO; i++) {
            diemHoanLai += nguoiChoi.pointAdd[i] / MUC_TANG[i];
        }
        nguoiChoi.point = (short) Math.min(Short.MAX_VALUE,
                Math.max(0, nguoiChoi.point + diemHoanLai));
        datMacDinh(nguoiChoi);
        return true;
    }

    public static int layGiaTri(ChickenNguoiChoi nguoiChoi, int chiSo) {
        baoDamDuLieuHopLe(nguoiChoi);
        if (!laChiSoHopLe(chiSo)) {
            return 0;
        }
        return Math.max(0, nguoiChoi.pointAdd[chiSo]);
    }

    /**
     * Lấy riêng điểm Tấn công đã cộng. Hàm tính damage của server gọi trực
     * tiếp hàm này, không phụ thuộc thứ tự tham số của hàm tổng chỉ số chung.
     */
    public static int layTanCongDaCong(ChickenNguoiChoi nguoiChoi) {
        return layGiaTri(nguoiChoi, TAN_CONG);
    }

    public static void baoDamDuLieuHopLe(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi.pointAdd == null || nguoiChoi.pointAdd.length != SO_CHI_SO) {
            short[] moi = new short[SO_CHI_SO];
            moi[MAU] = MAU_MAC_DINH;
            if (nguoiChoi.pointAdd != null) {
                System.arraycopy(nguoiChoi.pointAdd, 0, moi, 0,
                        Math.min(nguoiChoi.pointAdd.length, moi.length));
                if (moi[MAU] <= 0) {
                    moi[MAU] = MAU_MAC_DINH;
                }
            }
            nguoiChoi.pointAdd = moi;
        }
    }

    private static void guiBangTiemNang(ChickenNguoiChoi nguoiChoi) throws IOException {
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
        for (int i = 0; i < SO_CHI_SO; i++) {
            nguoiChoi.pointAdd[i] = 0;
        }
        nguoiChoi.pointAdd[MAU] = MAU_MAC_DINH;
    }

    private static boolean laChiSoHopLe(int chiSo) {
        return chiSo >= 0 && chiSo < SO_CHI_SO;
    }
}
