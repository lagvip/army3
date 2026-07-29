package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.tiemnang.ChickenQuanLyTiemNang;
import com.chicken.tienich.ChickenTienIch;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Kiểm thử giao dịch boss bằng kho vàng RAM, không chạm MariaDB thật. */
public final class ChickenKinhTeBossTestSupport {
    private static KhoExpRam khoExpTam;

    private ChickenKinhTeBossTestSupport() {
    }

    public static void tuKiemTra() {
        KhoVangRam kho = new KhoVangRam();
        ChickenNguoiChoi nguoiChoi = nguoiChoi(93_001, 3_000);
        kho.datSoDu(nguoiChoi.ma, nguoiChoi.vang);
        ThanhVienBoss thanhVien = new ThanhVienBoss(
                nguoiChoi, (byte) 0, 1L, true);

        bang(ChickenKinhTeBoss.KetQuaThuPhi.THANH_CONG,
                ChickenKinhTeBoss.thuPhiVaoPhong(
                        thanhVien, 1_000, kho),
                "vao phong lan dau khong thu duoc phi");
        bang(2_000, nguoiChoi.vang,
                "phi vao phong khong tru dung 1000");
        bang(1, kho.soGiaoDich(),
                "thu phi khong ghi dung mot giao dich");

        bang(ChickenKinhTeBoss.KetQuaThuPhi.DA_THU,
                ChickenKinhTeBoss.thuPhiVaoPhong(
                        thanhVien, 1_000, kho),
                "packet vao phong lap khong bi nhan dien");
        bang(2_000, nguoiChoi.vang,
                "packet vao phong lap bi tru vang lan hai");
        bang(1, kho.soGiaoDich(),
                "packet lap lai tao them giao dich thu phi");

        bang(2_000, ChickenKinhTeBoss.traoThuongThang(
                        thanhVien, 2_000, kho),
                "thang boss khong duoc cong 2000 vang");
        bang(4_000, nguoiChoi.vang,
                "so du sau thang boss bi sai");
        bang(0, ChickenKinhTeBoss.traoThuongThang(
                        thanhVien, 2_000, kho),
                "ket qua lap lai con tra thuong");
        bang(4_000, nguoiChoi.vang,
                "ket qua lap lai cong vang lan hai");
        bang(2, kho.soGiaoDich(),
                "thu phi va thuong phai co hai khoa idempotency rieng");

        ChickenNguoiChoi thieuVang = nguoiChoi(93_002, 999);
        kho.datSoDu(thieuVang.ma, thieuVang.vang);
        ThanhVienBoss veThieuVang = new ThanhVienBoss(
                thieuVang, (byte) 1, 2L, false);
        bang(ChickenKinhTeBoss.KetQuaThuPhi.KHONG_DU_VANG,
                ChickenKinhTeBoss.thuPhiVaoPhong(
                        veThieuVang, 1_000, kho),
                "nguoi thieu vang van vao duoc phong boss");
        bang(999, thieuVang.vang,
                "thu phi that bai van lam thay doi so du");

        ChickenNguoiChoi loiDb = nguoiChoi(93_004, 5_000);
        ThanhVienBoss veLoiDb = new ThanhVienBoss(
                loiDb, (byte) 3, 4L, false);
        ChickenKinhTeBoss.KhoVang khoLoi = (ma, giaoDich, bienDong) -> {
            throw new SQLException("loi DB chu dong");
        };
        bang(ChickenKinhTeBoss.KetQuaThuPhi.LOI_LUU_TRU,
                ChickenKinhTeBoss.thuPhiVaoPhong(
                        veLoiDb, 1_000, khoLoi),
                "DB loi nhung server van cho vao phong");
        bang(5_000, loiDb.vang,
                "DB rollback nhung state RAM van bi tru");
        ThanhVienBoss veThuongLoi = new ThanhVienBoss(
                loiDb, (byte) 4, 5L, false);
        veThuongLoi.danhDauDaThuPhiVaoPhong();
        bang(0, ChickenKinhTeBoss.traoThuongThang(
                        veThuongLoi, 2_000, khoLoi),
                "DB loi nhung packet van duoc bao co thuong");
        bang(5_000, loiDb.vang,
                "DB loi nhung state RAM van duoc cong thuong");

        ChickenNguoiChoi ganTran = nguoiChoi(
                93_003, Integer.MAX_VALUE - 500);
        kho.datSoDu(ganTran.ma, ganTran.vang);
        ThanhVienBoss veGanTran = new ThanhVienBoss(
                ganTran, (byte) 2, 3L, false);
        bang(ChickenKinhTeBoss.KetQuaThuPhi.THANH_CONG,
                ChickenKinhTeBoss.thuPhiVaoPhong(
                        veGanTran, 0, kho),
                "ve test tran so chua duoc xac nhan da tra phi");
        bang(500, ChickenKinhTeBoss.traoThuongThang(
                        veGanTran, 2_000, kho),
                "thuong gan tran int khong tra bien dong thuc te");
        bang(Integer.MAX_VALUE, ganTran.vang,
                "thuong boss lam tran so vang");

        kiemTraThuPhiNhomKhiBatDau();
        kiemTraExpIdempotentVaRollback();
    }

    private static void kiemTraThuPhiNhomKhiBatDau() {
        KhoVangRam kho = new KhoVangRam();
        ChickenNguoiChoi nguoiMot = nguoiChoi(93_020, 3_000);
        ChickenNguoiChoi nguoiHai = nguoiChoi(93_021, 2_000);
        kho.datSoDu(nguoiMot.ma, nguoiMot.vang);
        kho.datSoDu(nguoiHai.ma, nguoiHai.vang);
        ThanhVienBoss veMot = new ThanhVienBoss(
                nguoiMot, (byte) 0, 20L, true);
        ThanhVienBoss veHai = new ThanhVienBoss(
                nguoiHai, (byte) 1, 21L, false);

        ChickenKinhTeBoss.datKhoVangNhomChoKiemThu(kho);
        try {
            ChickenKinhTeBoss.KetQuaThuPhiTran ketQua =
                    ChickenKinhTeBoss.thuPhiBatDauTran(
                            new ThanhVienBoss[]{veMot, veHai}, 1_000);
            bang(ChickenKinhTeBoss.KetQuaThuPhi.THANH_CONG,
                    ketQua.getKetQua(),
                    "bat dau tran khong thu phi ca doi");
            bang(2_000, nguoiMot.vang,
                    "nguoi mot bi tru sai phi luc bat dau");
            bang(1_000, nguoiHai.vang,
                    "nguoi hai bi tru sai phi luc bat dau");
            bang(1_000, veMot.getPhiDaThu(),
                    "ve khong luu dung phi thuc te");
            bang(1_000, veHai.getPhiDaThu(),
                    "ve dong doi khong luu dung phi thuc te");

            bang(ChickenKinhTeBoss.KetQuaThuPhi.DA_THU,
                    ChickenKinhTeBoss.thuPhiBatDauTran(
                            new ThanhVienBoss[]{veMot, veHai}, 1_000)
                            .getKetQua(),
                    "bam bat dau lap lai bi thu phi lan hai");
            bang(2, kho.soGiaoDich(),
                    "thu phi nhom lap tao them giao dich");

            bang(true, ChickenKinhTeBoss.hoanPhiBatDauTran(ketQua),
                    "khoi tao tran loi nhung khong hoan phi");
            bang(3_000, nguoiMot.vang,
                    "hoan phi khong tra du nguoi mot");
            bang(2_000, nguoiHai.vang,
                    "hoan phi khong tra du nguoi hai");
            bang(false, veMot.isDaThuPhiVaoPhong(),
                    "hoan phi nhung ve van bi danh dau da thu");

            ChickenNguoiChoi duVang = nguoiChoi(93_022, 5_000);
            ChickenNguoiChoi thieuVang = nguoiChoi(93_023, 999);
            kho.datSoDu(duVang.ma, duVang.vang);
            kho.datSoDu(thieuVang.ma, thieuVang.vang);
            ThanhVienBoss veDu = new ThanhVienBoss(
                    duVang, (byte) 2, 22L, true);
            ThanhVienBoss veThieu = new ThanhVienBoss(
                    thieuVang, (byte) 3, 23L, false);
            ChickenKinhTeBoss.KetQuaThuPhiTran thieu =
                    ChickenKinhTeBoss.thuPhiBatDauTran(
                            new ThanhVienBoss[]{veDu, veThieu}, 1_000);
            bang(ChickenKinhTeBoss.KetQuaThuPhi.KHONG_DU_VANG,
                    thieu.getKetQua(),
                    "mot nguoi thieu vang nhung tran van bat dau");
            bang(5_000, duVang.vang,
                    "giao dich nhom khong rollback nguoi du vang");
            bang(999, thieuVang.vang,
                    "giao dich nhom lam doi so du nguoi thieu");
            bang(false, veDu.isDaThuPhiVaoPhong(),
                    "rollback nhung ve nguoi du van bi danh dau");
        } finally {
            ChickenKinhTeBoss.datKhoVangNhomChoKiemThu(null);
        }
    }

    private static void kiemTraExpIdempotentVaRollback() {
        ChickenNguoiChoi nguoiChoi = nguoiChoi(93_010, 0);
        nguoiChoi.datKinhNghiemVaCanBangTrongBoNho(10_000);
        nguoiChoi.cap = ChickenTienIch.layCap(
                nguoiChoi.layKinhNghiem());
        nguoiChoi.capCaoNhatDaNhanThuong = nguoiChoi.cap;
        nguoiChoi.point = 12;
        nguoiChoi.ngoc = 20;
        ThanhVienBoss thanhVien = new ThanhVienBoss(
                nguoiChoi, (byte) 0, 10L, true);
        thanhVien.danhDauDaThuPhiVaoPhong();

        KhoExpRam khoExp = new KhoExpRam();
        khoExp.datTrangThai(nguoiChoi);
        int expBanDau = nguoiChoi.layKinhNghiem();
        bang(1_234, ChickenKinhTeBoss.traoExpHaBoss(
                        thanhVien, 1_234, khoExp),
                "EXP boss lan dau khong duoc cong");
        bang(expBanDau + 1_234, nguoiChoi.layKinhNghiem(),
                "EXP DB thanh cong nhung RAM khong dong bo");
        bang(0, ChickenKinhTeBoss.traoExpHaBoss(
                        thanhVien, 1_234, khoExp),
                "ket qua lap lai con cong EXP");
        bang(1, khoExp.soGiaoDich(),
                "EXP lap lai tao them giao dich");

        kiemTraExpBossLenLaiCapCu();

        ChickenNguoiChoi loiDb = nguoiChoi(93_011, 0);
        loiDb.datKinhNghiemVaCanBangTrongBoNho(5_000);
        ThanhVienBoss veLoi = new ThanhVienBoss(
                loiDb, (byte) 1, 11L, false);
        veLoi.danhDauDaThuPhiVaoPhong();
        ChickenKinhTeBoss.KhoExp khoLoi =
                (ma, giaoDich, exp) -> {
                    throw new SQLException("loi DB EXP chu dong");
                };
        bang(0, ChickenKinhTeBoss.traoExpHaBoss(
                        veLoi, 500, khoLoi),
                "DB loi nhung server van bao co EXP");
        bang(5_000, loiDb.layKinhNghiem(),
                "DB rollback nhung RAM van bi cong EXP");

        ChickenNguoiChoi chuaTraPhi = nguoiChoi(93_012, 0);
        ThanhVienBoss veMienPhi = new ThanhVienBoss(
                chuaTraPhi, (byte) 2, 12L, false);
        KhoExpRam khoMienPhi = new KhoExpRam();
        khoMienPhi.datTrangThai(chuaTraPhi);
        bang(0, ChickenKinhTeBoss.traoExpHaBoss(
                        veMienPhi, 500, khoMienPhi),
                "thanh vien chua tra phi van nhan EXP");
        bang(0, khoMienPhi.soGiaoDich(),
                "ve chua tra phi van ghi giao dich EXP");

        ChickenNguoiChoi rotMang = nguoiChoi(93_013, 0);
        rotMang.datKinhNghiemVaCanBangTrongBoNho(2_000);
        ThanhVienBoss veRotMang = new ThanhVienBoss(
                rotMang, (byte) 3, 13L, false);
        veRotMang.danhDauDaThuPhiVaoPhong();
        veRotMang.danhDauNgatKetNoi();
        KhoExpRam khoRotMang = new KhoExpRam();
        khoRotMang.datTrangThai(rotMang);
        bang(0, ChickenKinhTeBoss.traoExpHaBoss(
                        veRotMang, 300, khoRotMang),
                "rot mang trong tran van nhan EXP");
        bang(0, khoRotMang.soGiaoDich(),
                "ve rot mang van ghi giao dich EXP");
        SanhChoBoss sanh = new SanhChoBoss(
                (byte) 4, (byte) 4, (byte) 54, (byte) 8, 1_000);
        bang(true, sanh.themThanhVien(veRotMang),
                "khong them duoc ve rot mang vao sanh test");
        sanh.donThanhVienNgatKetNoiSauTran();
        bang(0, sanh.getSoNguoi(),
                "ve rot mang khong duoc don sau khi phat thuong");
        bang(SanhChoBoss.TrangThai.DANG_CHO, sanh.getTrangThai(),
                "phong rong sau ket qua khong duoc reset");

        ChickenNguoiChoi taiDau = nguoiChoi(93_014, 5_000);
        ThanhVienBoss veTaiDau = new ThanhVienBoss(
                taiDau, (byte) 0, 14L, true);
        veTaiDau.danhDauDaThuPhiVaoPhong();
        veTaiDau.danhDauDaNhanExpHaBoss();
        veTaiDau.danhDauDaNhanThuongThang();
        String khoaPhiCu = veTaiDau.getMaGiaoDichThuPhi();
        SanhChoBoss sanhTaiDau = new SanhChoBoss(
                (byte) 4, (byte) 0, (byte) 54, (byte) 8, 1_000);
        bang(true, sanhTaiDau.themThanhVien(veTaiDau),
                "khong them duoc thanh vien vao test tai dau");
        sanhTaiDau.setTrangThai(SanhChoBoss.TrangThai.DA_KET_THUC);
        sanhTaiDau.chuanBiTaiDauSauKetQua();
        bang(SanhChoBoss.TrangThai.DANG_CHO, sanhTaiDau.getTrangThai(),
                "phong boss ket thuc khong mo lai de tai dau");
        ThanhVienBoss veTaiDauMoi = sanhTaiDau.timThanhVien(taiDau);
        bang(true, veTaiDauMoi != null,
                "tai dau lam mat thanh vien con ket noi");
        bang(false, veTaiDauMoi == veTaiDau,
                "tai dau tai su dung object ve cua tran cu");
        bang(false, veTaiDauMoi.isDaThuPhiVaoPhong(),
                "ve tran cu bi tai su dung mien phi cho tran moi");
        bang(false, veTaiDauMoi.isDaNhanExpHaBoss(),
                "co EXP tran cu khong duoc reset khi tai dau");
        bang(false, veTaiDauMoi.isDaNhanThuongThang(),
                "co thuong tran cu khong duoc reset khi tai dau");
        bang(false, khoaPhiCu.equals(veTaiDauMoi.getMaGiaoDichThuPhi()),
                "tai dau van dung lai khoa giao dich tran cu");
    }

    public static void danhDauDaTraPhi(ThanhVienBoss thanhVien) {
        if (thanhVien != null) {
            thanhVien.danhDauDaThuPhiVaoPhong(1_000);
        }
    }

    private static void kiemTraExpBossLenLaiCapCu() {
        Map<Integer, com.chicken.dulieu.ChickenTieuDeCap> bangCapCu =
                new HashMap<>(
                        com.chicken.dulieu.ChickenTieuDeCap.levels);
        try {
            com.chicken.dulieu.ChickenTieuDeCap.levels.clear();
            for (int i = 0; i <= 80; i++) {
                com.chicken.dulieu.ChickenTieuDeCap cap =
                        new com.chicken.dulieu.ChickenTieuDeCap();
                cap.ma = i;
                cap.kinhNghiem = i * 1000;
                cap.ten = "Level " + i;
                com.chicken.dulieu.ChickenTieuDeCap.levels.put(i, cap);
            }

            ChickenNguoiChoi lenLaiCapCu =
                    nguoiChoi(93_014, 0);
            lenLaiCapCu.datKinhNghiemVaCanBangTrongBoNho(70_000);
            lenLaiCapCu.cap = 70;
            lenLaiCapCu.capCaoNhatDaNhanThuong = 80;
            lenLaiCapCu.point = 700;
            lenLaiCapCu.ngoc = 321;
            ThanhVienBoss veLenLaiCapCu = new ThanhVienBoss(
                    lenLaiCapCu, (byte) 4, 14L, false);
            veLenLaiCapCu.danhDauDaThuPhiVaoPhong();
            KhoExpRam khoLenLaiCapCu = new KhoExpRam();
            khoLenLaiCapCu.datTrangThai(lenLaiCapCu);

            bang(10_000,
                    ChickenKinhTeBoss.traoExpHaBoss(
                            veLenLaiCapCu, 10_000,
                            khoLenLaiCapCu),
                    "EXP boss khong dua nguoi choi len lai Lv80");
            bang(80, lenLaiCapCu.cap,
                    "EXP boss len lai sai level");
            bang(800, (int) lenLaiCapCu.point,
                    "EXP boss len lai Lv80 van giu 700 diem");
            bang(321, lenLaiCapCu.ngoc,
                    "EXP boss len lai cap cu phat lai ngoc");
            bang(80, lenLaiCapCu.capCaoNhatDaNhanThuong,
                    "EXP boss lam sai moc thuong cao nhat");
        } finally {
            com.chicken.dulieu.ChickenTieuDeCap.levels.clear();
            com.chicken.dulieu.ChickenTieuDeCap.levels.putAll(
                    bangCapCu);
        }
    }

    public static void batKhoExpRam(ChickenNguoiChoi... nguoiChois) {
        KhoExpRam kho = new KhoExpRam();
        if (nguoiChois != null) {
            for (ChickenNguoiChoi nguoiChoi : nguoiChois) {
                if (nguoiChoi != null) {
                    kho.datTrangThai(nguoiChoi);
                }
            }
        }
        khoExpTam = kho;
        ChickenKinhTeBoss.datKhoExpChoKiemThu(kho);
    }

    public static void khoiPhucKhoExpJdbc() {
        khoExpTam = null;
        ChickenKinhTeBoss.datKhoExpChoKiemThu(null);
    }

    private static ChickenNguoiChoi nguoiChoi(int ma, int vang) {
        ChickenNguoiChoi nguoiChoi = new ChickenNguoiChoi(null);
        nguoiChoi.ma = ma;
        nguoiChoi.ten = "BossEconomy" + ma;
        nguoiChoi.vang = vang;
        return nguoiChoi;
    }

    private static void bang(Object mongDoi, Object thucTe, String thongBao) {
        if (mongDoi == null ? thucTe != null : !mongDoi.equals(thucTe)) {
            throw new AssertionError(
                    thongBao + " expected=" + mongDoi + " actual=" + thucTe);
        }
    }

    private static final class KhoVangRam
            implements ChickenKinhTeBoss.KhoVang,
            ChickenKinhTeBoss.KhoVangNhom {
        private final Map<Integer, Integer> soDu = new HashMap<>();
        private final Set<String> giaoDich = new HashSet<>();

        void datSoDu(int maNguoiChoi, int vang) {
            this.soDu.put(maNguoiChoi, Math.max(0, vang));
        }

        int soGiaoDich() {
            return this.giaoDich.size();
        }

        @Override
        public ChickenKinhTeBoss.KetQuaVangNhom apDungNhom(
                java.util.List<ChickenKinhTeBoss.YeuCauVang> yeuCaus
        ) throws SQLException {
            Map<Integer, Integer> soDuMoi = new HashMap<>(this.soDu);
            Set<String> giaoDichMoi = new HashSet<>();
            ChickenKinhTeBoss.KetQuaBienDong[] cacKetQua =
                    new ChickenKinhTeBoss.KetQuaBienDong[yeuCaus.size()];
            for (int i = 0; i < yeuCaus.size(); i++) {
                ChickenKinhTeBoss.YeuCauVang yeuCau = yeuCaus.get(i);
                Integer hienTai = soDuMoi.get(yeuCau.maNguoiChoi);
                if (hienTai == null) {
                    throw new SQLException("player test khong ton tai");
                }
                if (this.giaoDich.contains(yeuCau.maGiaoDich)) {
                    cacKetQua[i] =
                            ChickenKinhTeBoss.KetQuaBienDong.daXuLy(
                                    hienTai);
                    continue;
                }
                long tinh = (long) hienTai + yeuCau.bienDong;
                if (tinh < 0L) {
                    return ChickenKinhTeBoss.KetQuaVangNhom
                            .khongDuVang(yeuCau.thanhVien);
                }
                int moi = (int) Math.min(Integer.MAX_VALUE, tinh);
                soDuMoi.put(yeuCau.maNguoiChoi, moi);
                giaoDichMoi.add(yeuCau.maGiaoDich);
                cacKetQua[i] =
                        ChickenKinhTeBoss.KetQuaBienDong.thanhCong(
                                moi, moi - hienTai);
            }
            this.soDu.clear();
            this.soDu.putAll(soDuMoi);
            this.giaoDich.addAll(giaoDichMoi);
            return ChickenKinhTeBoss.KetQuaVangNhom.thanhCong(
                    cacKetQua);
        }

        @Override
        public ChickenKinhTeBoss.KetQuaBienDong apDung(
                int maNguoiChoi,
                String maGiaoDich,
                int bienDong
        ) throws SQLException {
            Integer hienTai = this.soDu.get(maNguoiChoi);
            if (hienTai == null) {
                throw new SQLException("player test khong ton tai");
            }
            if (this.giaoDich.contains(maGiaoDich)) {
                return ChickenKinhTeBoss.KetQuaBienDong.daXuLy(hienTai);
            }
            long soDuMoiTinh = (long) hienTai + bienDong;
            if (soDuMoiTinh < 0L) {
                return ChickenKinhTeBoss.KetQuaBienDong.khongDuVang(
                        hienTai);
            }
            int soDuMoi = (int) Math.min(
                    Integer.MAX_VALUE, soDuMoiTinh);
            int bienDongThucTe = soDuMoi - hienTai;
            this.soDu.put(maNguoiChoi, soDuMoi);
            this.giaoDich.add(maGiaoDich);
            return ChickenKinhTeBoss.KetQuaBienDong.thanhCong(
                    soDuMoi, bienDongThucTe);
        }
    }

    private static final class KhoExpRam
            implements ChickenKinhTeBoss.KhoExp {
        private final Map<Integer, TrangThaiExpRam> trangThais =
                new HashMap<>();
        private final Set<String> giaoDich = new HashSet<>();

        void datTrangThai(ChickenNguoiChoi nguoiChoi) {
            this.trangThais.put(
                    nguoiChoi.ma,
                    new TrangThaiExpRam(
                            nguoiChoi.layKinhNghiem(),
                            nguoiChoi.capCaoNhatDaNhanThuong,
                            nguoiChoi.point,
                            nguoiChoi.ngoc));
        }

        int soGiaoDich() {
            return this.giaoDich.size();
        }

        @Override
        public ChickenKinhTeBoss.KetQuaExp apDung(
                int maNguoiChoi,
                String maGiaoDich,
                int expCong
        ) throws SQLException {
            TrangThaiExpRam trangThai = this.trangThais.get(maNguoiChoi);
            if (trangThai == null) {
                throw new SQLException("player EXP test khong ton tai");
            }
            if (this.giaoDich.contains(maGiaoDich)) {
                return trangThai.toKetQua(true, 0);
            }
            int expCu = trangThai.exp;
            int capCu = ChickenTienIch.layCap(expCu);
            int expMoi = (int) Math.min(
                    Integer.MAX_VALUE,
                    (long) expCu + Math.max(0, expCong));
            int capMoi = ChickenTienIch.layCap(expMoi);
            int soCapThuong = Math.max(
                    0, capMoi - Math.max(capCu, trangThai.mocDaNhan));
            int ngocCong = soCapThuong
                    * ChickenQuanLyTiemNang.NGOC_TIM_MOI_CAP;
            trangThai.exp = expMoi;
            trangThai.mocDaNhan = Math.max(
                    trangThai.mocDaNhan, capMoi);
            trangThai.diem = (short) Math.min(
                    Short.MAX_VALUE,
                    (long) capMoi
                    * ChickenQuanLyTiemNang.DIEM_TIEM_NANG_MOI_CAP);
            trangThai.ngoc = (int) Math.min(
                    Integer.MAX_VALUE,
                    (long) Math.max(0, trangThai.ngoc) + ngocCong);
            this.giaoDich.add(maGiaoDich);
            return trangThai.toKetQua(
                    false, expMoi - expCu);
        }
    }

    private static final class TrangThaiExpRam {
        int exp;
        int mocDaNhan;
        short diem;
        int ngoc;

        TrangThaiExpRam(int exp, int mocDaNhan, short diem, int ngoc) {
            this.exp = Math.max(0, exp);
            this.mocDaNhan = Math.max(0, mocDaNhan);
            this.diem = (short) Math.max(0, diem);
            this.ngoc = Math.max(0, ngoc);
        }

        ChickenKinhTeBoss.KetQuaExp toKetQua(
                boolean daXuLy,
                int expThucTe
        ) {
            return ChickenKinhTeBoss.KetQuaExp.thanhCong(
                    daXuLy,
                    expThucTe,
                    this.exp,
                    ChickenTienIch.layCap(this.exp),
                    this.mocDaNhan,
                    this.diem,
                    this.ngoc);
        }
    }
}
