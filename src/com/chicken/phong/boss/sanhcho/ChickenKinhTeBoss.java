package com.chicken.phong.boss.sanhcho;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.tiemnang.ChickenQuanLyTiemNang;
import com.chicken.tienich.ChickenTienIch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Giao dịch vàng của phòng boss. Mọi số tiền đều lấy từ cấu hình server,
 * được ghi vào sổ giao dịch có khóa duy nhất rồi mới cập nhật state trong RAM.
 */
public final class ChickenKinhTeBoss {
    public enum KetQuaThuPhi {
        THANH_CONG,
        DA_THU,
        KHONG_DU_VANG,
        LOI_LUU_TRU
    }

    public static final class KetQuaThuPhiTran {
        private final KetQuaThuPhi ketQua;
        private final ThanhVienBoss thanhVienLoi;
        private final ThanhVienBoss[] thanhVienDaThuTrongLan;

        private KetQuaThuPhiTran(
                KetQuaThuPhi ketQua,
                ThanhVienBoss thanhVienLoi,
                ThanhVienBoss[] thanhVienDaThuTrongLan
        ) {
            this.ketQua = ketQua;
            this.thanhVienLoi = thanhVienLoi;
            this.thanhVienDaThuTrongLan = thanhVienDaThuTrongLan == null
                    ? new ThanhVienBoss[0]
                    : thanhVienDaThuTrongLan.clone();
        }

        public KetQuaThuPhi getKetQua() {
            return this.ketQua;
        }

        public ThanhVienBoss getThanhVienLoi() {
            return this.thanhVienLoi;
        }

        ThanhVienBoss[] getThanhVienDaThuTrongLan() {
            return this.thanhVienDaThuTrongLan.clone();
        }
    }

    static final class YeuCauVang {
        final ThanhVienBoss thanhVien;
        final int maNguoiChoi;
        final String maGiaoDich;
        final int bienDong;

        YeuCauVang(
                ThanhVienBoss thanhVien,
                String maGiaoDich,
                int bienDong
        ) {
            this.thanhVien = thanhVien;
            this.maNguoiChoi = thanhVien.getNguoiChoi().ma;
            this.maGiaoDich = maGiaoDich;
            this.bienDong = bienDong;
        }
    }

    static final class KetQuaVangNhom {
        final boolean thanhCong;
        final ThanhVienBoss thanhVienThieuVang;
        final KetQuaBienDong[] cacKetQua;

        private KetQuaVangNhom(
                boolean thanhCong,
                ThanhVienBoss thanhVienThieuVang,
                KetQuaBienDong[] cacKetQua
        ) {
            this.thanhCong = thanhCong;
            this.thanhVienThieuVang = thanhVienThieuVang;
            this.cacKetQua = cacKetQua;
        }

        static KetQuaVangNhom thanhCong(KetQuaBienDong[] cacKetQua) {
            return new KetQuaVangNhom(true, null, cacKetQua);
        }

        static KetQuaVangNhom khongDuVang(ThanhVienBoss thanhVien) {
            return new KetQuaVangNhom(false, thanhVien, null);
        }
    }

    interface KhoVang {
        KetQuaBienDong apDung(
                int maNguoiChoi,
                String maGiaoDich,
                int bienDong
        ) throws SQLException;
    }

    interface KhoVangNhom {
        KetQuaVangNhom apDungNhom(List<YeuCauVang> yeuCaus)
                throws SQLException;
    }

    interface KhoExp {
        KetQuaExp apDung(
                int maNguoiChoi,
                String maGiaoDich,
                int expCong
        ) throws SQLException;
    }

    static final class KetQuaBienDong {
        final boolean thanhCong;
        final boolean daXuLy;
        final boolean khongDuVang;
        final int soDuMoi;
        final int bienDongThucTe;

        private KetQuaBienDong(
                boolean thanhCong,
                boolean daXuLy,
                boolean khongDuVang,
                int soDuMoi,
                int bienDongThucTe
        ) {
            this.thanhCong = thanhCong;
            this.daXuLy = daXuLy;
            this.khongDuVang = khongDuVang;
            this.soDuMoi = soDuMoi;
            this.bienDongThucTe = bienDongThucTe;
        }

        static KetQuaBienDong thanhCong(int soDuMoi, int bienDongThucTe) {
            return new KetQuaBienDong(
                    true, false, false, soDuMoi, bienDongThucTe);
        }

        static KetQuaBienDong daXuLy(int soDuMoi) {
            return new KetQuaBienDong(true, true, false, soDuMoi, 0);
        }

        static KetQuaBienDong khongDuVang(int soDuHienTai) {
            return new KetQuaBienDong(
                    false, false, true, soDuHienTai, 0);
        }
    }

    static final class KetQuaExp {
        final boolean thanhCong;
        final boolean daXuLy;
        final int expThucTe;
        final int expMoi;
        final int capMoi;
        final int mocCapDaNhanMoi;
        final short diemTiemNangMoi;
        final int ngocMoi;

        private KetQuaExp(
                boolean thanhCong,
                boolean daXuLy,
                int expThucTe,
                int expMoi,
                int capMoi,
                int mocCapDaNhanMoi,
                short diemTiemNangMoi,
                int ngocMoi
        ) {
            this.thanhCong = thanhCong;
            this.daXuLy = daXuLy;
            this.expThucTe = expThucTe;
            this.expMoi = expMoi;
            this.capMoi = capMoi;
            this.mocCapDaNhanMoi = mocCapDaNhanMoi;
            this.diemTiemNangMoi = diemTiemNangMoi;
            this.ngocMoi = ngocMoi;
        }

        static KetQuaExp thanhCong(
                boolean daXuLy,
                int expThucTe,
                int expMoi,
                int capMoi,
                int mocCapDaNhanMoi,
                short diemTiemNangMoi,
                int ngocMoi
        ) {
            return new KetQuaExp(
                    true, daXuLy, expThucTe, expMoi, capMoi,
                    mocCapDaNhanMoi, diemTiemNangMoi, ngocMoi);
        }
    }

    private static final KhoVangJdbc KHO_VANG_JDBC =
            new KhoVangJdbc();
    private static final KhoExp KHO_EXP_JDBC =
            new KhoExpJdbc();
    private static volatile KhoExp khoExpDangDung = KHO_EXP_JDBC;
    private static volatile KhoVangNhom khoVangNhomDangDung =
            KHO_VANG_JDBC;
    private static volatile boolean daKhoiTao;

    private ChickenKinhTeBoss() {
    }

    public static synchronized void khoiTao() {
        if (daKhoiTao) {
            return;
        }
        String sqlVang = "CREATE TABLE IF NOT EXISTS `boss_gold_transactions` ("
                + "`operation_key` varchar(80) NOT NULL,"
                + "`player_id` int(11) NOT NULL,"
                + "`gold_delta` int(11) NOT NULL,"
                + "`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (`operation_key`),"
                + "KEY `idx_boss_gold_player` (`player_id`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 "
                + "COLLATE=utf8mb4_unicode_ci";
        String sqlExp = "CREATE TABLE IF NOT EXISTS `boss_exp_transactions` ("
                + "`operation_key` varchar(80) NOT NULL,"
                + "`player_id` int(11) NOT NULL,"
                + "`exp_delta` int(11) NOT NULL,"
                + "`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (`operation_key`),"
                + "KEY `idx_boss_exp_player` (`player_id`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 "
                + "COLLATE=utf8mb4_unicode_ci";
        try (Connection ketNoi = ChickenCoSoDuLieu.getConnection();
                Statement lenh = ketNoi.createStatement()) {
            lenh.execute(sqlVang);
            lenh.execute(sqlExp);
            daKhoiTao = true;
        } catch (SQLException loi) {
            throw new IllegalStateException(
                    "Khong khoi tao duoc so giao dich vang boss", loi);
        }
    }

    public static KetQuaThuPhi thuPhiVaoPhong(
            ThanhVienBoss thanhVien,
            int phiVaoPhong
    ) {
        return thuPhiVaoPhong(thanhVien, phiVaoPhong, KHO_VANG_JDBC);
    }

    static KetQuaThuPhi thuPhiVaoPhong(
            ThanhVienBoss thanhVien,
            int phiVaoPhong,
            KhoVang khoVang
    ) {
        if (thanhVien == null || thanhVien.getNguoiChoi() == null
                || khoVang == null) {
            return KetQuaThuPhi.LOI_LUU_TRU;
        }
        synchronized (thanhVien) {
            if (thanhVien.isDaThuPhiVaoPhong()) {
                return KetQuaThuPhi.DA_THU;
            }
            ChickenNguoiChoi nguoiChoi = thanhVien.getNguoiChoi();
            int phi = Math.max(0, phiVaoPhong);
            if (phi == 0) {
                thanhVien.danhDauDaThuPhiVaoPhong(0);
                return KetQuaThuPhi.THANH_CONG;
            }
            synchronized (nguoiChoi) {
                try {
                    KetQuaBienDong ketQua = khoVang.apDung(
                            nguoiChoi.ma,
                            thanhVien.getMaGiaoDichThuPhi(),
                            -phi
                    );
                    if (ketQua.khongDuVang) {
                        return KetQuaThuPhi.KHONG_DU_VANG;
                    }
                    if (!ketQua.thanhCong) {
                        return KetQuaThuPhi.LOI_LUU_TRU;
                    }
                    nguoiChoi.vang = ketQua.soDuMoi;
                    thanhVien.danhDauDaThuPhiVaoPhong(phi);
                    capNhatVangChoClient(nguoiChoi);
                    DebugSanhBoss.log("THU_PHI_VAO_PHONG", nguoiChoi,
                            "phi=" + phi
                            + " daXuLy=" + ketQua.daXuLy
                            + " soDu=" + ketQua.soDuMoi);
                    return ketQua.daXuLy
                            ? KetQuaThuPhi.DA_THU
                            : KetQuaThuPhi.THANH_CONG;
                } catch (SQLException | RuntimeException loi) {
                    DebugSanhBoss.log("LOI_THU_PHI_VAO_PHONG", nguoiChoi,
                            "loi=" + loi.getClass().getSimpleName());
                    return KetQuaThuPhi.LOI_LUU_TRU;
                }
            }
        }
    }

    /**
     * Thu phí của toàn bộ đội đúng lúc bắt đầu trận. Kho JDBC khóa và cập nhật
     * tất cả tài khoản trong cùng một transaction: một người thiếu vàng hoặc
     * một lệnh DB lỗi thì không thành viên nào bị trừ.
     */
    public static KetQuaThuPhiTran thuPhiBatDauTran(
            ThanhVienBoss[] danhSach,
            int phiMoiNguoi
    ) {
        int phi = Math.max(0, phiMoiNguoi);
        List<ThanhVienBoss> canThu = new ArrayList<>();
        if (danhSach != null) {
            for (ThanhVienBoss thanhVien : danhSach) {
                if (thanhVien != null
                        && thanhVien.getNguoiChoi() != null
                        && !thanhVien.isDaThuPhiVaoPhong()) {
                    canThu.add(thanhVien);
                }
            }
        }
        canThu.sort(Comparator.comparingInt(
                thanhVien -> thanhVien.getNguoiChoi().ma));
        if (canThu.isEmpty()) {
            return new KetQuaThuPhiTran(
                    KetQuaThuPhi.DA_THU, null, new ThanhVienBoss[0]);
        }
        if (phi == 0) {
            for (ThanhVienBoss thanhVien : canThu) {
                thanhVien.danhDauDaThuPhiVaoPhong(0);
            }
            return new KetQuaThuPhiTran(
                    KetQuaThuPhi.THANH_CONG,
                    null,
                    canThu.toArray(new ThanhVienBoss[0]));
        }

        List<YeuCauVang> yeuCaus = new ArrayList<>(canThu.size());
        for (ThanhVienBoss thanhVien : canThu) {
            yeuCaus.add(new YeuCauVang(
                    thanhVien,
                    thanhVien.getMaGiaoDichThuPhi(),
                    -phi));
        }
        KetQuaVangNhom ketQuaNhom;
        try {
            ketQuaNhom = khoVangNhomDangDung.apDungNhom(yeuCaus);
        } catch (SQLException | RuntimeException loi) {
            DebugSanhBoss.log("LOI_THU_PHI_BAT_DAU_TRAN",
                    canThu.get(0).getNguoiChoi(),
                    "loi=" + loi.getClass().getSimpleName());
            return new KetQuaThuPhiTran(
                    KetQuaThuPhi.LOI_LUU_TRU,
                    null,
                    new ThanhVienBoss[0]);
        }
        if (!ketQuaNhom.thanhCong) {
            return new KetQuaThuPhiTran(
                    KetQuaThuPhi.KHONG_DU_VANG,
                    ketQuaNhom.thanhVienThieuVang,
                    new ThanhVienBoss[0]);
        }
        for (int i = 0; i < canThu.size(); i++) {
            ThanhVienBoss thanhVien = canThu.get(i);
            ChickenNguoiChoi nguoiChoi = thanhVien.getNguoiChoi();
            KetQuaBienDong ketQua = ketQuaNhom.cacKetQua[i];
            nguoiChoi.vang = ketQua.soDuMoi;
            thanhVien.danhDauDaThuPhiVaoPhong(phi);
            try {
                capNhatVangChoClient(nguoiChoi);
            } catch (RuntimeException loi) {
                DebugSanhBoss.log("LOI_GUI_SO_DU_SAU_THU_PHI",
                        nguoiChoi,
                        "loi=" + loi.getClass().getSimpleName());
            }
            DebugSanhBoss.log("THU_PHI_BAT_DAU_TRAN", nguoiChoi,
                    "phi=" + phi
                    + " daXuLy=" + ketQua.daXuLy
                    + " soDu=" + ketQua.soDuMoi);
        }
        return new KetQuaThuPhiTran(
                KetQuaThuPhi.THANH_CONG,
                null,
                canThu.toArray(new ThanhVienBoss[0]));
    }

    /**
     * Hoàn toàn bộ phí vừa thu nếu khởi tạo trận thất bại sau bước thanh toán.
     * Hoàn phí cũng là một transaction nhóm và có khóa idempotency riêng.
     */
    public static boolean hoanPhiBatDauTran(KetQuaThuPhiTran ketQuaThuPhi) {
        if (ketQuaThuPhi == null) {
            return true;
        }
        ThanhVienBoss[] danhSach =
                ketQuaThuPhi.getThanhVienDaThuTrongLan();
        List<YeuCauVang> yeuCaus = new ArrayList<>();
        for (ThanhVienBoss thanhVien : danhSach) {
            if (thanhVien == null
                    || thanhVien.getNguoiChoi() == null
                    || !thanhVien.isDaThuPhiVaoPhong()
                    || thanhVien.getPhiDaThu() <= 0) {
                continue;
            }
            yeuCaus.add(new YeuCauVang(
                    thanhVien,
                    thanhVien.getMaGiaoDichHoanPhi(),
                    thanhVien.getPhiDaThu()));
        }
        yeuCaus.sort(Comparator.comparingInt(yeuCau -> yeuCau.maNguoiChoi));
        if (yeuCaus.isEmpty()) {
            for (ThanhVienBoss thanhVien : danhSach) {
                if (thanhVien != null) {
                    thanhVien.xoaDanhDauDaThuPhiVaoPhong();
                }
            }
            return true;
        }
        KetQuaVangNhom ketQuaNhom;
        try {
            ketQuaNhom = khoVangNhomDangDung.apDungNhom(yeuCaus);
        } catch (SQLException | RuntimeException loi) {
            DebugSanhBoss.log("LOI_HOAN_PHI_BAT_DAU_TRAN",
                    yeuCaus.get(0).thanhVien.getNguoiChoi(),
                    "loi=" + loi.getClass().getSimpleName());
            return false;
        }
        if (!ketQuaNhom.thanhCong) {
            return false;
        }
        for (int i = 0; i < yeuCaus.size(); i++) {
            YeuCauVang yeuCau = yeuCaus.get(i);
            ChickenNguoiChoi nguoiChoi =
                    yeuCau.thanhVien.getNguoiChoi();
            nguoiChoi.vang = ketQuaNhom.cacKetQua[i].soDuMoi;
            yeuCau.thanhVien.xoaDanhDauDaThuPhiVaoPhong();
            try {
                capNhatVangChoClient(nguoiChoi);
            } catch (RuntimeException loi) {
                DebugSanhBoss.log("LOI_GUI_SO_DU_SAU_HOAN_PHI",
                        nguoiChoi,
                        "loi=" + loi.getClass().getSimpleName());
            }
            DebugSanhBoss.log("HOAN_PHI_BAT_DAU_TRAN", nguoiChoi,
                    "soDu=" + nguoiChoi.vang);
        }
        return true;
    }

    /**
     * @return số vàng thực tế vừa cộng; bằng 0 nếu thua, đã nhận hoặc DB lỗi.
     */
    public static int traoThuongThang(ThanhVienBoss thanhVien) {
        return traoThuongThang(
                thanhVien,
                Math.max(0, ChickenQuanLyMayChu.bossWinGoldReward),
                KHO_VANG_JDBC
        );
    }

    static int traoThuongThang(
            ThanhVienBoss thanhVien,
            int vangThuong,
            KhoVang khoVang
    ) {
        if (thanhVien == null || thanhVien.getNguoiChoi() == null
                || khoVang == null) {
            return 0;
        }
        synchronized (thanhVien) {
            if (thanhVien.isDaNgatKetNoi()) {
                DebugSanhBoss.log("TU_CHOI_THUONG_NGAT_KET_NOI",
                        thanhVien.getNguoiChoi(), "loai=vang");
                return 0;
            }
            if (!thanhVien.isDaThuPhiVaoPhong()) {
                DebugSanhBoss.log("TU_CHOI_THUONG_CHUA_TRA_PHI",
                        thanhVien.getNguoiChoi(), "loai=vang");
                return 0;
            }
            if (thanhVien.isDaNhanThuongThang()) {
                return 0;
            }
            ChickenNguoiChoi nguoiChoi = thanhVien.getNguoiChoi();
            int thuong = Math.max(0, vangThuong);
            if (thuong == 0) {
                thanhVien.danhDauDaNhanThuongThang();
                return 0;
            }
            synchronized (nguoiChoi) {
                try {
                    KetQuaBienDong ketQua = khoVang.apDung(
                            nguoiChoi.ma,
                            thanhVien.getMaGiaoDichThuongThang(),
                            thuong
                    );
                    if (!ketQua.thanhCong) {
                        return 0;
                    }
                    nguoiChoi.vang = ketQua.soDuMoi;
                    thanhVien.danhDauDaNhanThuongThang();
                    capNhatVangChoClient(nguoiChoi);
                    DebugSanhBoss.log("TRA_THUONG_THANG", nguoiChoi,
                            "thuong=" + ketQua.bienDongThucTe
                            + " daXuLy=" + ketQua.daXuLy
                            + " soDu=" + ketQua.soDuMoi);
                    return ketQua.daXuLy ? 0 : ketQua.bienDongThucTe;
                } catch (SQLException | RuntimeException loi) {
                    DebugSanhBoss.log("LOI_TRA_THUONG_THANG", nguoiChoi,
                            "loi=" + loi.getClass().getSimpleName());
                    return 0;
                }
            }
        }
    }

    /**
     * Cộng EXP hạ boss đúng một lần theo vé vào phòng. Toàn bộ EXP, mốc level,
     * điểm tiềm năng và ngọc tím được ghi trong cùng transaction trước khi RAM
     * và packet client được cập nhật.
     */
    public static int traoExpHaBoss(ThanhVienBoss thanhVien, int exp) {
        return traoExpHaBoss(thanhVien, exp, khoExpDangDung);
    }

    static void datKhoExpChoKiemThu(KhoExp khoExp) {
        khoExpDangDung = khoExp == null ? KHO_EXP_JDBC : khoExp;
    }

    static void datKhoVangNhomChoKiemThu(KhoVangNhom khoVangNhom) {
        khoVangNhomDangDung = khoVangNhom == null
                ? KHO_VANG_JDBC : khoVangNhom;
    }

    static int traoExpHaBoss(
            ThanhVienBoss thanhVien,
            int exp,
            KhoExp khoExp
    ) {
        if (thanhVien == null || thanhVien.getNguoiChoi() == null
                || khoExp == null) {
            return 0;
        }
        synchronized (thanhVien) {
            if (thanhVien.isDaNgatKetNoi()) {
                DebugSanhBoss.log("TU_CHOI_THUONG_NGAT_KET_NOI",
                        thanhVien.getNguoiChoi(), "loai=exp");
                return 0;
            }
            if (!thanhVien.isDaThuPhiVaoPhong()) {
                DebugSanhBoss.log("TU_CHOI_THUONG_CHUA_TRA_PHI",
                        thanhVien.getNguoiChoi(), "loai=exp");
                return 0;
            }
            if (thanhVien.isDaNhanExpHaBoss()) {
                return 0;
            }
            int expHopLe = Math.max(0, exp);
            if (expHopLe == 0) {
                thanhVien.danhDauDaNhanExpHaBoss();
                return 0;
            }
            ChickenNguoiChoi nguoiChoi = thanhVien.getNguoiChoi();
            synchronized (nguoiChoi) {
                try {
                    KetQuaExp ketQua = khoExp.apDung(
                            nguoiChoi.ma,
                            thanhVien.getMaGiaoDichExpHaBoss(),
                            expHopLe
                    );
                    if (ketQua == null || !ketQua.thanhCong) {
                        return 0;
                    }
                    nguoiChoi.kinhNghiem = ketQua.expMoi;
                    nguoiChoi.cap = ketQua.capMoi;
                    nguoiChoi.capCaoNhatDaNhanThuong =
                            ketQua.mocCapDaNhanMoi;
                    nguoiChoi.point = ketQua.diemTiemNangMoi;
                    nguoiChoi.ngoc = ketQua.ngocMoi;
                    thanhVien.danhDauDaNhanExpHaBoss();
                    DebugSanhBoss.log("TRA_EXP_HA_BOSS", nguoiChoi,
                            "exp=" + ketQua.expThucTe
                            + " daXuLy=" + ketQua.daXuLy
                            + " expMoi=" + ketQua.expMoi);
                    return ketQua.daXuLy ? 0 : ketQua.expThucTe;
                } catch (SQLException | RuntimeException loi) {
                    DebugSanhBoss.log("LOI_TRA_EXP_HA_BOSS", nguoiChoi,
                            "loi=" + loi.getClass().getSimpleName());
                    return 0;
                }
            }
        }
    }

    private static void capNhatVangChoClient(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi != null && nguoiChoi.dichVu != null) {
            nguoiChoi.dichVu.capNhat();
        }
    }

    private static final class KhoVangJdbc
            implements KhoVang, KhoVangNhom {
        @Override
        public KetQuaVangNhom apDungNhom(List<YeuCauVang> yeuCaus)
                throws SQLException {
            if (yeuCaus == null || yeuCaus.isEmpty()) {
                return KetQuaVangNhom.thanhCong(
                        new KetQuaBienDong[0]);
            }
            try (Connection ketNoi = ChickenCoSoDuLieu.getConnection()) {
                boolean tuDongCommitCu = ketNoi.getAutoCommit();
                ketNoi.setAutoCommit(false);
                try {
                    KetQuaBienDong[] cacKetQua =
                            new KetQuaBienDong[yeuCaus.size()];
                    for (int i = 0; i < yeuCaus.size(); i++) {
                        YeuCauVang yeuCau = yeuCaus.get(i);
                        int soDuHienTai = docVaKhoaSoDu(
                                ketNoi, yeuCau.maNguoiChoi);
                        if (soDuHienTai < 0) {
                            throw new SQLException(
                                    "Khong tim thay player id="
                                            + yeuCau.maNguoiChoi);
                        }
                        if (daCoGiaoDich(
                                ketNoi,
                                yeuCau.maGiaoDich,
                                yeuCau.maNguoiChoi)) {
                            cacKetQua[i] =
                                    KetQuaBienDong.daXuLy(soDuHienTai);
                            continue;
                        }

                        long soDuTinh =
                                (long) soDuHienTai + yeuCau.bienDong;
                        if (soDuTinh < 0L) {
                            ketNoi.rollback();
                            return KetQuaVangNhom.khongDuVang(
                                    yeuCau.thanhVien);
                        }
                        int soDuMoi = (int) Math.min(
                                Integer.MAX_VALUE, soDuTinh);
                        int bienDongThucTe = soDuMoi - soDuHienTai;

                        try (PreparedStatement capNhat =
                                ketNoi.prepareStatement(
                                        "UPDATE `players` SET `gold` = ? "
                                                + "WHERE `id` = ?")) {
                            capNhat.setInt(1, soDuMoi);
                            capNhat.setInt(2, yeuCau.maNguoiChoi);
                            if (capNhat.executeUpdate() != 1) {
                                throw new SQLException(
                                        "Cap nhat gold khong dung mot dong");
                            }
                        }
                        try (PreparedStatement ghiSo =
                                ketNoi.prepareStatement(
                                        "INSERT INTO "
                                                + "`boss_gold_transactions` "
                                                + "(`operation_key`, "
                                                + "`player_id`, `gold_delta`) "
                                                + "VALUES (?, ?, ?)")) {
                            ghiSo.setString(1, yeuCau.maGiaoDich);
                            ghiSo.setInt(2, yeuCau.maNguoiChoi);
                            ghiSo.setInt(3, bienDongThucTe);
                            ghiSo.executeUpdate();
                        }
                        cacKetQua[i] =
                                KetQuaBienDong.thanhCong(
                                        soDuMoi, bienDongThucTe);
                    }
                    ketNoi.commit();
                    return KetQuaVangNhom.thanhCong(cacKetQua);
                } catch (SQLException | RuntimeException loi) {
                    ketNoi.rollback();
                    throw loi;
                } finally {
                    ketNoi.setAutoCommit(tuDongCommitCu);
                }
            }
        }

        @Override
        public KetQuaBienDong apDung(
                int maNguoiChoi,
                String maGiaoDich,
                int bienDong
        ) throws SQLException {
            try (Connection ketNoi = ChickenCoSoDuLieu.getConnection()) {
                boolean tuDongCommitCu = ketNoi.getAutoCommit();
                ketNoi.setAutoCommit(false);
                try {
                    int soDuHienTai = docVaKhoaSoDu(ketNoi, maNguoiChoi);
                    if (soDuHienTai < 0) {
                        ketNoi.rollback();
                        throw new SQLException(
                                "Khong tim thay player id=" + maNguoiChoi);
                    }
                    if (daCoGiaoDich(
                            ketNoi, maGiaoDich, maNguoiChoi)) {
                        ketNoi.commit();
                        return KetQuaBienDong.daXuLy(soDuHienTai);
                    }

                    long soDuTinh = (long) soDuHienTai + bienDong;
                    if (soDuTinh < 0L) {
                        ketNoi.rollback();
                        return KetQuaBienDong.khongDuVang(soDuHienTai);
                    }
                    int soDuMoi = (int) Math.min(
                            Integer.MAX_VALUE, soDuTinh);
                    int bienDongThucTe = soDuMoi - soDuHienTai;

                    try (PreparedStatement capNhat = ketNoi.prepareStatement(
                            "UPDATE `players` SET `gold` = ? "
                                    + "WHERE `id` = ?")) {
                        capNhat.setInt(1, soDuMoi);
                        capNhat.setInt(2, maNguoiChoi);
                        if (capNhat.executeUpdate() != 1) {
                            throw new SQLException(
                                    "Cap nhat gold khong dung mot dong");
                        }
                    }
                    try (PreparedStatement ghiSo = ketNoi.prepareStatement(
                            "INSERT INTO `boss_gold_transactions` "
                                    + "(`operation_key`, `player_id`, "
                                    + "`gold_delta`) VALUES (?, ?, ?)")) {
                        ghiSo.setString(1, maGiaoDich);
                        ghiSo.setInt(2, maNguoiChoi);
                        ghiSo.setInt(3, bienDongThucTe);
                        ghiSo.executeUpdate();
                    }
                    ketNoi.commit();
                    return KetQuaBienDong.thanhCong(
                            soDuMoi, bienDongThucTe);
                } catch (SQLException | RuntimeException loi) {
                    ketNoi.rollback();
                    throw loi;
                } finally {
                    ketNoi.setAutoCommit(tuDongCommitCu);
                }
            }
        }

        private int docVaKhoaSoDu(
                Connection ketNoi,
                int maNguoiChoi
        ) throws SQLException {
            try (PreparedStatement doc = ketNoi.prepareStatement(
                    "SELECT `gold` FROM `players` "
                            + "WHERE `id` = ? FOR UPDATE")) {
                doc.setInt(1, maNguoiChoi);
                try (ResultSet ketQua = doc.executeQuery()) {
                    return ketQua.next() ? ketQua.getInt("gold") : -1;
                }
            }
        }

        private boolean daCoGiaoDich(
                Connection ketNoi,
                String maGiaoDich,
                int maNguoiChoi
        ) throws SQLException {
            try (PreparedStatement doc = ketNoi.prepareStatement(
                    "SELECT `player_id` FROM `boss_gold_transactions` "
                            + "WHERE `operation_key` = ? FOR UPDATE")) {
                doc.setString(1, maGiaoDich);
                try (ResultSet ketQua = doc.executeQuery()) {
                    if (!ketQua.next()) {
                        return false;
                    }
                    if (ketQua.getInt("player_id") != maNguoiChoi) {
                        throw new SQLException(
                                "Ma giao dich boss trung khac nguoi choi");
                    }
                    return true;
                }
            }
        }
    }

    private static final class KhoExpJdbc implements KhoExp {
        @Override
        public KetQuaExp apDung(
                int maNguoiChoi,
                String maGiaoDich,
                int expCong
        ) throws SQLException {
            try (Connection ketNoi = ChickenCoSoDuLieu.getConnection()) {
                boolean tuDongCommitCu = ketNoi.getAutoCommit();
                ketNoi.setAutoCommit(false);
                try {
                    TrangThaiExp trangThai =
                            docVaKhoaTrangThai(ketNoi, maNguoiChoi);
                    if (daCoGiaoDichExp(
                            ketNoi, maGiaoDich, maNguoiChoi)) {
                        ketNoi.commit();
                        return trangThai.toKetQua(true, 0);
                    }

                    int expCu = docSoNguyen(
                            trangThai.stats, "exp", 0);
                    int capCu = ChickenTienIch.layCap(expCu);
                    int mocDaNhan = Math.max(0, docSoNguyen(
                            trangThai.stats, "rewardedLevel", capCu));
                    int diemCu = Math.max(0, docSoNguyen(
                            trangThai.stats, "point", 0));

                    int expMoi = (int) Math.min(
                            Integer.MAX_VALUE,
                            (long) expCu + Math.max(0, expCong));
                    int expThucTe = expMoi - expCu;
                    int capMoi = ChickenTienIch.layCap(expMoi);
                    int soCapThuong = Math.max(
                            0, capMoi - Math.max(capCu, mocDaNhan));
                    int diemCong = nhanAnToan(
                            soCapThuong,
                            ChickenQuanLyTiemNang.DIEM_TIEM_NANG_MOI_CAP);
                    int ngocCong = nhanAnToan(
                            soCapThuong,
                            ChickenQuanLyTiemNang.NGOC_TIM_MOI_CAP);
                    short diemMoi = (short) Math.min(
                            Short.MAX_VALUE, (long) diemCu + diemCong);
                    int ngocMoi = (int) Math.min(
                            Integer.MAX_VALUE,
                            (long) Math.max(0, trangThai.ngoc) + ngocCong);
                    int mocMoi = Math.max(mocDaNhan, capMoi);

                    trangThai.stats.put("exp", expMoi);
                    trangThai.stats.put("rewardedLevel", mocMoi);
                    trangThai.stats.put("point", diemMoi);
                    try (PreparedStatement capNhat = ketNoi.prepareStatement(
                            "UPDATE `players` SET `stats_json` = ?, `gem` = ? "
                                    + "WHERE `id` = ?")) {
                        capNhat.setString(1, trangThai.stats.toJSONString());
                        capNhat.setInt(2, ngocMoi);
                        capNhat.setInt(3, maNguoiChoi);
                        if (capNhat.executeUpdate() != 1) {
                            throw new SQLException(
                                    "Cap nhat EXP boss khong dung mot dong");
                        }
                    }
                    try (PreparedStatement ghiSo = ketNoi.prepareStatement(
                            "INSERT INTO `boss_exp_transactions` "
                                    + "(`operation_key`, `player_id`, "
                                    + "`exp_delta`) VALUES (?, ?, ?)")) {
                        ghiSo.setString(1, maGiaoDich);
                        ghiSo.setInt(2, maNguoiChoi);
                        ghiSo.setInt(3, expThucTe);
                        ghiSo.executeUpdate();
                    }
                    ketNoi.commit();
                    return KetQuaExp.thanhCong(
                            false, expThucTe, expMoi, capMoi,
                            mocMoi, diemMoi, ngocMoi);
                } catch (SQLException | RuntimeException loi) {
                    ketNoi.rollback();
                    throw loi;
                } finally {
                    ketNoi.setAutoCommit(tuDongCommitCu);
                }
            }
        }

        private TrangThaiExp docVaKhoaTrangThai(
                Connection ketNoi,
                int maNguoiChoi
        ) throws SQLException {
            try (PreparedStatement doc = ketNoi.prepareStatement(
                    "SELECT `stats_json`, `gem` FROM `players` "
                            + "WHERE `id` = ? FOR UPDATE")) {
                doc.setInt(1, maNguoiChoi);
                try (ResultSet ketQua = doc.executeQuery()) {
                    if (!ketQua.next()) {
                        throw new SQLException(
                                "Khong tim thay player id=" + maNguoiChoi);
                    }
                    JSONObject stats;
                    try {
                        stats = JSON.parseObject(
                                ketQua.getString("stats_json"));
                    } catch (RuntimeException loiJson) {
                        throw new SQLException(
                                "stats_json cua player khong hop le",
                                loiJson);
                    }
                    if (stats == null) {
                        throw new SQLException(
                                "stats_json cua player bi null");
                    }
                    return new TrangThaiExp(
                            stats, Math.max(0, ketQua.getInt("gem")));
                }
            }
        }

        private boolean daCoGiaoDichExp(
                Connection ketNoi,
                String maGiaoDich,
                int maNguoiChoi
        ) throws SQLException {
            try (PreparedStatement doc = ketNoi.prepareStatement(
                    "SELECT `player_id` FROM `boss_exp_transactions` "
                            + "WHERE `operation_key` = ? FOR UPDATE")) {
                doc.setString(1, maGiaoDich);
                try (ResultSet ketQua = doc.executeQuery()) {
                    if (!ketQua.next()) {
                        return false;
                    }
                    if (ketQua.getInt("player_id") != maNguoiChoi) {
                        throw new SQLException(
                                "Ma giao dich EXP boss trung khac nguoi choi");
                    }
                    return true;
                }
            }
        }
    }

    private static int docSoNguyen(
            JSONObject json,
            String khoa,
            int macDinh
    ) throws SQLException {
        Object giaTri = json.get(khoa);
        if (giaTri == null) {
            return macDinh;
        }
        try {
            return Integer.parseInt(giaTri.toString());
        } catch (NumberFormatException loi) {
            throw new SQLException(
                    "Gia tri " + khoa + " trong stats_json khong hop le",
                    loi);
        }
    }

    private static int nhanAnToan(int a, int b) {
        return (int) Math.min(
                Integer.MAX_VALUE,
                Math.max(0L, (long) Math.max(0, a) * Math.max(0, b)));
    }

    private static final class TrangThaiExp {
        final JSONObject stats;
        final int ngoc;

        TrangThaiExp(JSONObject stats, int ngoc) {
            this.stats = stats;
            this.ngoc = ngoc;
        }

        KetQuaExp toKetQua(boolean daXuLy, int expThucTe)
                throws SQLException {
            int exp = docSoNguyen(this.stats, "exp", 0);
            int cap = ChickenTienIch.layCap(exp);
            int moc = Math.max(0, docSoNguyen(
                    this.stats, "rewardedLevel", cap));
            short diem = (short) Math.min(
                    Short.MAX_VALUE,
                    Math.max(0, docSoNguyen(this.stats, "point", 0)));
            return KetQuaExp.thanhCong(
                    daXuLy, expThucTe, exp, cap, moc, diem, this.ngoc);
        }
    }
}
