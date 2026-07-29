package com.chicken.mohinh;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.chicken.avg.ChickenQuanLyNangLuongAVG;
import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.tiemnang.ChickenQuanLyTiemNang;
import com.chicken.tienich.ChickenTienIch;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Giao dịch phần thưởng luyện tập do server tính.
 *
 * EXP, vàng, ngọc lên cấp, điểm tiềm năng, năng lượng AVG và số trận thắng
 * được khóa/cập nhật cùng một transaction. Mã giao dịch do server phát cho
 * từng phiên khiến cùng một kết quả không thể được nhận hai lần.
 */
public final class ChickenKinhTeLuyenTap {

    interface KhoThuong {
        KetQua apDung(
                int maNguoiChoi,
                String maGiaoDich,
                int expThuong,
                int vangThuong
        ) throws SQLException;
    }

    public static final class KetQua {
        public static final KetQua THAT_BAI = new KetQua(
                false, false, 0, 0, 0, 0, 0,
                0, (short) 0, 0, 0, 0, 0, -1L);

        public final boolean thanhCong;
        public final boolean daXuLy;
        public final int expThucTe;
        public final int vangThucTe;
        public final int ngocThucTe;
        public final int expMoi;
        public final int vangMoi;
        public final int capMoi;
        public final short diemTiemNangMoi;
        public final int mocCapDaNhanMoi;
        public final int ngocMoi;
        public final int soTranThangMoi;
        public final int nangLuongAvgMoi;
        public final long phienBanThongKeMoi;

        private KetQua(
                boolean thanhCong,
                boolean daXuLy,
                int expThucTe,
                int vangThucTe,
                int ngocThucTe,
                int expMoi,
                int vangMoi,
                int capMoi,
                short diemTiemNangMoi,
                int mocCapDaNhanMoi,
                int ngocMoi,
                int soTranThangMoi,
                int nangLuongAvgMoi,
                long phienBanThongKeMoi
        ) {
            this.thanhCong = thanhCong;
            this.daXuLy = daXuLy;
            this.expThucTe = Math.max(0, expThucTe);
            this.vangThucTe = Math.max(0, vangThucTe);
            this.ngocThucTe = Math.max(0, ngocThucTe);
            this.expMoi = Math.max(0, expMoi);
            this.vangMoi = Math.max(0, vangMoi);
            this.capMoi = Math.max(0, capMoi);
            this.diemTiemNangMoi = (short) Math.max(
                    0, diemTiemNangMoi);
            this.mocCapDaNhanMoi = Math.max(0, mocCapDaNhanMoi);
            this.ngocMoi = Math.max(0, ngocMoi);
            this.soTranThangMoi = Math.max(0, soTranThangMoi);
            this.nangLuongAvgMoi = Math.max(
                    0,
                    Math.min(
                            ChickenQuanLyNangLuongAVG.NANG_LUONG_TOI_DA,
                            nangLuongAvgMoi));
            this.phienBanThongKeMoi = phienBanThongKeMoi;
        }

        static KetQua thanhCong(
                boolean daXuLy,
                int expThucTe,
                int vangThucTe,
                int ngocThucTe,
                int expMoi,
                int vangMoi,
                int capMoi,
                short diemTiemNangMoi,
                int mocCapDaNhanMoi,
                int ngocMoi,
                int soTranThangMoi,
                int nangLuongAvgMoi
        ) {
            return new KetQua(
                    true, daXuLy, expThucTe, vangThucTe, ngocThucTe,
                    expMoi, vangMoi, capMoi, diemTiemNangMoi,
                    mocCapDaNhanMoi, ngocMoi, soTranThangMoi,
                    nangLuongAvgMoi, -1L);
        }

        static KetQua thanhCongCoPhienBan(
                boolean daXuLy,
                int expThucTe,
                int vangThucTe,
                int ngocThucTe,
                int expMoi,
                int vangMoi,
                int capMoi,
                short diemTiemNangMoi,
                int mocCapDaNhanMoi,
                int ngocMoi,
                int soTranThangMoi,
                int nangLuongAvgMoi,
                long phienBanThongKeMoi
        ) {
            return new KetQua(
                    true, daXuLy, expThucTe, vangThucTe, ngocThucTe,
                    expMoi, vangMoi, capMoi, diemTiemNangMoi,
                    mocCapDaNhanMoi, ngocMoi, soTranThangMoi,
                    nangLuongAvgMoi, phienBanThongKeMoi);
        }
    }

    private static final KhoThuong KHO_JDBC = new KhoThuongJdbc();
    private static volatile KhoThuong khoDangDung = KHO_JDBC;
    private static volatile boolean daKhoiTao;

    private ChickenKinhTeLuyenTap() {
    }

    public static synchronized void khoiTao() {
        if (daKhoiTao) {
            return;
        }
        String sql = "CREATE TABLE IF NOT EXISTS "
                + "`training_reward_transactions` ("
                + "`operation_key` varchar(80) NOT NULL,"
                + "`player_id` int(11) NOT NULL,"
                + "`exp_delta` int(11) NOT NULL,"
                + "`gold_delta` int(11) NOT NULL,"
                + "`gem_delta` int(11) NOT NULL,"
                + "`created_at` timestamp NOT NULL "
                + "DEFAULT CURRENT_TIMESTAMP,"
                + "PRIMARY KEY (`operation_key`),"
                + "KEY `idx_training_reward_player` (`player_id`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 "
                + "COLLATE=utf8mb4_unicode_ci";
        try (Connection ketNoi = ChickenCoSoDuLieu.getConnection();
                Statement lenh = ketNoi.createStatement()) {
            lenh.execute(sql);
            daKhoiTao = true;
        } catch (SQLException loi) {
            throw new IllegalStateException(
                    "Khong khoi tao duoc so thuong luyen tap", loi);
        }
    }

    public static KetQua traoThuong(
            ChickenNguoiChoi nguoiChoi,
            String maGiaoDich,
            int expThuong,
            int vangThuong
    ) {
        return traoThuong(
                nguoiChoi,
                maGiaoDich,
                expThuong,
                vangThuong,
                khoDangDung);
    }

    static KetQua traoThuong(
            ChickenNguoiChoi nguoiChoi,
            String maGiaoDich,
            int expThuong,
            int vangThuong,
            KhoThuong kho
    ) {
        if (nguoiChoi == null
                || nguoiChoi.ma <= 0
                || maGiaoDich == null
                || maGiaoDich.isBlank()
                || maGiaoDich.length() > 80
                || kho == null) {
            return KetQua.THAT_BAI;
        }
        synchronized (nguoiChoi) {
            try {
                KetQua ketQua = kho.apDung(
                        nguoiChoi.ma,
                        maGiaoDich,
                        gioiHanExpPacket(expThuong),
                        Math.max(0, vangThuong));
                if (!ketQua.thanhCong) {
                    return KetQua.THAT_BAI;
                }
                nguoiChoi.apDungTrangThaiExpDaCommit(
                        ketQua.expMoi,
                        ketQua.capMoi,
                        ketQua.mocCapDaNhanMoi,
                        ketQua.diemTiemNangMoi,
                        ketQua.phienBanThongKeMoi);
                nguoiChoi.vang = ketQua.vangMoi;
                nguoiChoi.ngoc = ketQua.ngocMoi;
                nguoiChoi.powerAvenger =
                        (byte) ketQua.nangLuongAvgMoi;
                nguoiChoi.datSoTranThangLuyenTap(
                        ketQua.soTranThangMoi);
                ChickenQuanLyMayChu.log(
                        "[KINH_TE][THUONG_LUYEN_TAP]"
                        + " playerId=" + nguoiChoi.ma
                        + " operation=" + maGiaoDich
                        + " duplicate=" + ketQua.daXuLy
                        + " exp=" + ketQua.expThucTe
                        + " gold=" + ketQua.vangThucTe
                        + " gem=" + ketQua.ngocThucTe
                        + " wins=" + ketQua.soTranThangMoi);
                return ketQua;
            } catch (SQLException | RuntimeException loi) {
                ChickenQuanLyMayChu.log(
                        "[KINH_TE][LOI_THUONG_LUYEN_TAP]"
                        + " playerId=" + nguoiChoi.ma
                        + " operation=" + maGiaoDich
                        + " error=" + loi.getClass().getSimpleName());
                return KetQua.THAT_BAI;
            }
        }
    }

    public static int gioiHanExpPacket(int exp) {
        return Math.max(0, Math.min(Short.MAX_VALUE, exp));
    }

    static void datKhoChoKiemThu(KhoThuong kho) {
        khoDangDung = kho == null ? KHO_JDBC : kho;
    }

    private static final class KhoThuongJdbc implements KhoThuong {
        @Override
        public KetQua apDung(
                int maNguoiChoi,
                String maGiaoDich,
                int expThuong,
                int vangThuong
        ) throws SQLException {
            try (Connection ketNoi = ChickenCoSoDuLieu.getConnection()) {
                boolean tuDongCommitCu = ketNoi.getAutoCommit();
                ketNoi.setAutoCommit(false);
                try {
                    TrangThai trangThai =
                            docVaKhoa(ketNoi, maNguoiChoi);
                    if (daCoGiaoDich(
                            ketNoi, maGiaoDich, maNguoiChoi)) {
                        ketNoi.commit();
                        return trangThai.toKetQua(true);
                    }

                    int expCu = docSoNguyen(
                            trangThai.stats, "exp", 0);
                    int capCu = ChickenTienIch.layCap(expCu);
                    int mocCu = Math.max(
                            capCu,
                            docSoNguyen(
                                    trangThai.stats,
                                    "rewardedLevel",
                                    capCu));
                    int expMoi = congAnToan(expCu, expThuong);
                    int expThucTe = expMoi - expCu;
                    int capMoi = ChickenTienIch.layCap(expMoi);
                    int soCapThuong = Math.max(
                            0, capMoi - Math.max(capCu, mocCu));
                    int ngocThucTe = nhanAnToan(
                            soCapThuong,
                            ChickenQuanLyTiemNang.NGOC_TIM_MOI_CAP);
                    int ngocMoi = congAnToan(
                            trangThai.ngoc, ngocThucTe);
                    ngocThucTe = ngocMoi - trangThai.ngoc;

                    int diemDaPhanBo =
                            docSoDiemDaPhanBo(trangThai.stats);
                    int tongDiemTheoCap = nhanAnToan(
                            capMoi,
                            ChickenQuanLyTiemNang
                                    .DIEM_TIEM_NANG_MOI_CAP);
                    if (diemDaPhanBo > tongDiemTheoCap) {
                        throw new SQLException(
                                "Diem tiem nang da phan bo vuot level");
                    }
                    short diemMoi = (short) Math.min(
                            Short.MAX_VALUE,
                            tongDiemTheoCap - diemDaPhanBo);
                    int vangMoi = congAnToan(
                            trangThai.vang, vangThuong);
                    int vangThucTe = vangMoi - trangThai.vang;
                    int soTranThangMoi = congAnToan(
                            docSoNguyen(
                                    trangThai.stats,
                                    "trainingWins",
                                    0),
                            1);
                    int nangLuongMoi = tinhNangLuongSauThang(
                            docSoNguyen(
                                    trangThai.stats,
                                    "avenger",
                                    ChickenQuanLyNangLuongAVG
                                            .NANG_LUONG_TOI_DA));
                    int mocMoi = Math.max(mocCu, capMoi);

                    trangThai.stats.put("exp", expMoi);
                    trangThai.stats.put("rewardedLevel", mocMoi);
                    trangThai.stats.put("point", diemMoi);
                    trangThai.stats.put(
                            "trainingWins", soTranThangMoi);
                    trangThai.stats.put("avenger", nangLuongMoi);
                    long phienBanMoi = trangThai.phienBanThongKe + 1L;

                    try (PreparedStatement capNhat =
                            ketNoi.prepareStatement(
                                    "UPDATE `players` SET "
                                    + "`gold` = ?, `gem` = ?, "
                                    + "`stats_json` = ?, "
                                    + "`stats_revision` = ? "
                                    + "WHERE `id` = ?")) {
                        capNhat.setInt(1, vangMoi);
                        capNhat.setInt(2, ngocMoi);
                        capNhat.setString(
                                3, trangThai.stats.toJSONString());
                        capNhat.setLong(4, phienBanMoi);
                        capNhat.setInt(5, maNguoiChoi);
                        if (capNhat.executeUpdate() != 1) {
                            throw new SQLException(
                                    "Thuong luyen tap khong cap nhat "
                                    + "dung mot player");
                        }
                    }
                    try (PreparedStatement ghiSo =
                            ketNoi.prepareStatement(
                                    "INSERT INTO "
                                    + "`training_reward_transactions` "
                                    + "(`operation_key`, `player_id`, "
                                    + "`exp_delta`, `gold_delta`, "
                                    + "`gem_delta`) "
                                    + "VALUES (?, ?, ?, ?, ?)")) {
                        ghiSo.setString(1, maGiaoDich);
                        ghiSo.setInt(2, maNguoiChoi);
                        ghiSo.setInt(3, expThucTe);
                        ghiSo.setInt(4, vangThucTe);
                        ghiSo.setInt(5, ngocThucTe);
                        ghiSo.executeUpdate();
                    }
                    ketNoi.commit();
                    return KetQua.thanhCongCoPhienBan(
                            false,
                            expThucTe,
                            vangThucTe,
                            ngocThucTe,
                            expMoi,
                            vangMoi,
                            capMoi,
                            diemMoi,
                            mocMoi,
                            ngocMoi,
                            soTranThangMoi,
                            nangLuongMoi,
                            phienBanMoi);
                } catch (SQLException | RuntimeException loi) {
                    ketNoi.rollback();
                    throw loi;
                } finally {
                    ketNoi.setAutoCommit(tuDongCommitCu);
                }
            }
        }

        private TrangThai docVaKhoa(
                Connection ketNoi,
                int maNguoiChoi
        ) throws SQLException {
            try (PreparedStatement doc = ketNoi.prepareStatement(
                    "SELECT `gold`, `gem`, `stats_json`, "
                    + "`stats_revision` "
                    + "FROM `players` WHERE `id` = ? FOR UPDATE")) {
                doc.setInt(1, maNguoiChoi);
                try (ResultSet ketQua = doc.executeQuery()) {
                    if (!ketQua.next()) {
                        throw new SQLException(
                                "Khong tim thay player id="
                                + maNguoiChoi);
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
                    return new TrangThai(
                            stats,
                            Math.max(0, ketQua.getInt("gold")),
                            Math.max(0, ketQua.getInt("gem")),
                            Math.max(0L,
                                    ketQua.getLong("stats_revision")));
                }
            }
        }

        private boolean daCoGiaoDich(
                Connection ketNoi,
                String maGiaoDich,
                int maNguoiChoi
        ) throws SQLException {
            try (PreparedStatement doc = ketNoi.prepareStatement(
                    "SELECT `player_id` FROM "
                    + "`training_reward_transactions` "
                    + "WHERE `operation_key` = ? FOR UPDATE")) {
                doc.setString(1, maGiaoDich);
                try (ResultSet ketQua = doc.executeQuery()) {
                    if (!ketQua.next()) {
                        return false;
                    }
                    if (ketQua.getInt("player_id")
                            != maNguoiChoi) {
                        throw new SQLException(
                                "Ma thuong luyen tap trung "
                                + "khac nguoi choi");
                    }
                    return true;
                }
            }
        }
    }

    private static final class TrangThai {
        final JSONObject stats;
        final int vang;
        final int ngoc;
        final long phienBanThongKe;

        TrangThai(
                JSONObject stats,
                int vang,
                int ngoc,
                long phienBanThongKe
        ) {
            this.stats = stats;
            this.vang = vang;
            this.ngoc = ngoc;
            this.phienBanThongKe = phienBanThongKe;
        }

        KetQua toKetQua(boolean daXuLy) throws SQLException {
            int exp = Math.max(
                    0, docSoNguyen(this.stats, "exp", 0));
            int cap = ChickenTienIch.layCap(exp);
            int moc = Math.max(
                    cap,
                    docSoNguyen(
                            this.stats, "rewardedLevel", cap));
            short diem = (short) Math.min(
                    Short.MAX_VALUE,
                    Math.max(
                            0,
                            docSoNguyen(this.stats, "point", 0)));
            int wins = Math.max(
                    0,
                    docSoNguyen(
                            this.stats, "trainingWins", 0));
            int nangLuong = ChickenQuanLyNangLuongAVG
                    .LUON_DAY_NANG_LUONG
                    ? ChickenQuanLyNangLuongAVG.NANG_LUONG_TOI_DA
                    : Math.max(
                            0,
                            Math.min(
                                    ChickenQuanLyNangLuongAVG
                                            .NANG_LUONG_TOI_DA,
                                    docSoNguyen(
                                            this.stats,
                                            "avenger",
                                            ChickenQuanLyNangLuongAVG
                                                    .NANG_LUONG_TOI_DA)));
            return KetQua.thanhCongCoPhienBan(
                    daXuLy, 0, 0, 0, exp, this.vang,
                    cap, diem, moc, this.ngoc, wins, nangLuong,
                    this.phienBanThongKe);
        }
    }

    private static int tinhNangLuongSauThang(int hienTai) {
        if (ChickenQuanLyNangLuongAVG.LUON_DAY_NANG_LUONG) {
            return ChickenQuanLyNangLuongAVG.NANG_LUONG_TOI_DA;
        }
        return Math.min(
                ChickenQuanLyNangLuongAVG.NANG_LUONG_TOI_DA,
                Math.max(0, hienTai)
                + ChickenQuanLyNangLuongAVG.HOI_KHI_THANG_BOSS);
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
                    "Gia tri " + khoa
                    + " trong stats_json khong hop le",
                    loi);
        }
    }

    private static int docSoDiemDaPhanBo(
            JSONObject stats
    ) throws SQLException {
        JSONArray phanBo = stats.getJSONArray("pointAdd");
        if (phanBo == null
                || phanBo.size() < ChickenQuanLyTiemNang.SO_CHI_SO) {
            throw new SQLException(
                    "pointAdd trong stats_json khong hop le");
        }
        long tong = 0;
        for (int i = 0;
                i < ChickenQuanLyTiemNang.SO_CHI_SO;
                i++) {
            int giaTri;
            try {
                giaTri = Integer.parseInt(
                        phanBo.get(i).toString());
            } catch (RuntimeException loi) {
                throw new SQLException(
                        "pointAdd khong phai so tai vi tri " + i,
                        loi);
            }
            if (i == ChickenQuanLyTiemNang.MAU) {
                tong += Math.max(0, giaTri - 1000) / 10;
            } else {
                tong += Math.max(0, giaTri);
            }
        }
        return (int) Math.min(Integer.MAX_VALUE, tong);
    }

    private static int congAnToan(int hienTai, int giaTriCong) {
        return (int) Math.min(
                Integer.MAX_VALUE,
                Math.max(0L, (long) Math.max(0, hienTai)
                        + Math.max(0, giaTriCong)));
    }

    private static int nhanAnToan(int a, int b) {
        return (int) Math.min(
                Integer.MAX_VALUE,
                Math.max(
                        0L,
                        (long) Math.max(0, a)
                        * Math.max(0, b)));
    }
}
