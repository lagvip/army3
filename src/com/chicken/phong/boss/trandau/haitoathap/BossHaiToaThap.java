package com.chicken.phong.boss.trandau.haitoathap;

import com.chicken.avg.ChickenCoCheBayAVG;
import com.chicken.avg.ChickenCongThucBanUltron;
import com.chicken.avg.ChickenGocBanUltron;
import com.chicken.avg.ChickenHoatAnhHawk;
import com.chicken.avg.ChickenKyNangDacBietHawk;
import com.chicken.avg.ChickenKyNangDacBietLoki;
import com.chicken.avg.ChickenKyNangDacBietThor;
import com.chicken.avg.ChickenKyNangDacBietUltron;
import com.chicken.avg.ChickenKyNangDacBietIronMan;
import com.chicken.avg.ChickenTiaLaserIronMan;
import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenDiChuyenServer;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.chien.ChickenLoaiDanPhaDiaHinhClient;
import com.chicken.chien.ChickenMayMan;
import com.chicken.chien.ChickenNguCanhLaySung;
import com.chicken.chien.ChickenNapDanServer;
import com.chicken.chien.ChickenPhatBanServer;
import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.chien.ChickenQuanLyCongThucSung;
import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.chien.ChickenThoiGianHoatAnhDan;
import com.chicken.chien.ChickenTrongLucDiaHinhServer;
import com.chicken.chien.ChickenYeuCauBanServer;
import com.chicken.chien.ChickenYeuCauToaDoServer;
import com.chicken.chien.ChickenToaDoDauNong;
import com.chicken.chiso.ChickenKichThuocNhanVat;
import com.chicken.chiso.ChickenChiSoNguoiChoi;
import com.chicken.gio.ChickenHeThongGio;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.sanhcho.ThanhVienBoss;
import com.chicken.phong.boss.trandau.ChickenKetQuaTranBoss;
import com.chicken.chien.ChickenHangDoiNapDan;
import com.chicken.phong.boss.trandau.ChickenHoatAnhNoCamTu;
import com.chicken.phong.boss.trandau.ChickenLuatVaChamPhongBoss;
import com.chicken.phong.boss.trandau.ChickenSungShopBoss;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Trận riêng của map 51 - Boss Hai tòa tháp.
 * File này chỉ điều phối người chơi, lượt và kết quả trận. Công thức bắn nằm
 * trong BossBanSung; tìm mục tiêu/di chuyển/nổ Cảm tử nằm trong BossCamTu.
 */
public final class BossHaiToaThap extends ChickenQuanLyChien {
    private static final int SO_SLOT = 28;
    private static final int SO_SLOT_NGUOI_CHOI = 8;
    /** Người chơi và boss đều có tối đa 25 giây cho một lượt. */
    private static final int GIAY_MOI_LUOT = 25;
    private static final int TRE_BOSS_BAT_DAU_MS = 550;
    /** Cam tu chi di chuyen, khong can khoang lay da dai nhu boss ban sung. */
    private static final int TRE_CAM_TU_BAT_DAU_MS = 250;
    private static final byte PHE_NGUOI_CHOI_THANG =
            ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THANG;
    private static final byte PHE_BOSS_THANG =
            ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THUA;

    private final SanhChoBoss sanh;
    private final ChickenQuanLyBanDo banDo;
    private final ChickenChienBinh[] chienBinhs = new ChickenChienBinh[SO_SLOT];
    private final CauHinhBossHaiToaThap.CauHinh[] cauHinhBoss =
            new CauHinhBossHaiToaThap.CauHinh[SO_SLOT];
    /** Chỉ các boss phải có ngay khi mở trận; không chứa Cảm tử dự bị. */
    private final CauHinhBossHaiToaThap.CauHinh[] danhSachBossBanDau;
    /** ID sung server random mot lan cho tung Phien quan ban sung. */
    private final int[] idSungBoss = new int[SO_SLOT];
    /** Thời gian nạp hiện tại của từng ghế/slot; lúc vào trận đều bằng 0. */
    private final int[] napDan = new int[SO_SLOT];
    private final long[] thuTuHanhDongNapDan = new long[SO_SLOT];
    private final ScheduledExecutorService boHenGio;
    /** Bộ kỹ năng AVG riêng dùng chung mảng chiến binh của trận boss. */
    private ChickenKyNangDacBietHawk kyNangHawkBoss;
    private ChickenKyNangDacBietThor kyNangThorBoss;
    private ChickenKyNangDacBietLoki kyNangLokiBoss;
    private ChickenKyNangDacBietUltron kyNangUltronBoss;

    private byte luotHienTai = -1;
    private boolean daBatDau;
    private boolean daKetThuc;
    private boolean dangChoXacNhanThang;
    private long maPhienLuot;
    private long boDemThuTuHanhDongNapDan;
    private ScheduledFuture<?> tacVuHetLuot;
    private ScheduledFuture<?> tacVuChoKetThucBan;
    private int slotDangChoKetThucBan = -1;
    private long phienDangChoKetThucBan = -1L;
    private int napDanSauPhatDangCho;

    public BossHaiToaThap(SanhChoBoss sanh) {
        super(null, layNguoiChoiTheoGhe(sanh),
                (byte) CauHinhBossHaiToaThap.MAP_ID, false);
        this.sanh = sanh;
        this.banDo = new ChickenQuanLyBanDo(CauHinhBossHaiToaThap.MAP_ID);
        this.boHenGio = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "boss-hai-toa-thap-P4-"
                    + (sanh == null ? -1 : sanh.getMaBan() & 0xFF));
            thread.setDaemon(true);
            return thread;
        });
        Arrays.fill(this.idSungBoss, -1);
        this.taoNguoiChoi(sanh);
        this.danhSachBossBanDau =
                CauHinhBossHaiToaThap.layBanDauChoSoNguoi(
                        this.demNguoiChoiSong());
        this.taoBoss();
        this.khoiTaoKyNangAVG();
        this.dangKyNguoiChoiTrongTran();
    }

    public static BossHaiToaThap tao(SanhChoBoss sanh) {
        if (sanh == null || (sanh.getMaBanDo() & 0xFF) != CauHinhBossHaiToaThap.MAP_ID) {
            return null;
        }
        return new BossHaiToaThap(sanh);
    }

    @Override
    public synchronized void batDau() throws IOException {
        if (this.daBatDau || this.daKetThuc) {
            return;
        }
        this.daBatDau = true;
        for (CauHinhBossHaiToaThap.CauHinh cauHinh : this.danhSachBossBanDau) {
            int slot = cauHinh.getSlot() & 0xFF;
            ChickenChienBinh boss = this.chienBinhs[slot];
            ChickenQuanLyDanSung.DuLieuSung duLieu =
                    this.layDuLieuSungBoss(slot, boss);
            System.out.println("[BOSS HAI TOA THAP][TAO_BOSS] slot="
                    + slot
                    + " ten=" + cauHinh.getTen()
                    + " x=" + cauHinh.getX()
                    + " y=" + cauHinh.getY()
                    + " hp=" + CauHinhBossHaiToaThap.MAU_BOSS
                    + " loai=" + cauHinh.getLoai()
                    + " idSung=" + this.idSungBoss[slot]
                    + " tenSung="
                    + (duLieu == null ? "none" : duLieu.getTenSung())
                    + " sungPart=" + (boss == null ? -1 : boss.maVuKhi)
                    + " napDan=" + this.layNapDanBoss(slot));
        }
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiBatDauDau(
                    this.banDo.layMaBanDo(), this.chienBinhs, this.banDo.layMaNen());
            nguoiNhan.nguoiChoi.dichVu.guiBaloTrongTran(nguoiNhan);
            for (CauHinhBossHaiToaThap.CauHinh cauHinh : this.danhSachBossBanDau) {
                int slot = cauHinh.getSlot() & 0xFF;
                ChickenChienBinh boss = this.chienBinhs[slot];
                nguoiNhan.nguoiChoi.dichVu.guiTaoBossBaoVay(
                        cauHinh.getSlot(),
                        cauHinh.getId(),
                        cauHinh.getTen(),
                        cauHinh.getHead(),
                        cauHinh.getLeg(),
                        cauHinh.getBody(),
                        cauHinh.getHat(),
                        cauHinh.getWing(),
                        boss == null ? cauHinh.getVuKhi() : boss.maVuKhi,
                        cauHinh.getX(),
                        cauHinh.getY(),
                        CauHinhBossHaiToaThap.MAU_BOSS
                );
            }
        }
        System.out.println("[BOSS HAI TOA THAP][BAT_DAU] P4-"
                + (this.sanh.getMaBan() & 0xFF)
                + " map=51 players=" + this.demNguoiChoiSong()
                + " bossesBanDau=" + this.danhSachBossBanDau.length
                + " camTuToiDa=16");
        this.dongBoTatCaPow(this.chienBinhs);
        this.chuyenSangLuotTiepTheo(-1);
    }

    @Override
    public synchronized void diChuyen(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms)
            throws IOException {
        ChickenChienBinh chienBinh = this.layNguoiChoi(nguoiChoi);
        if (!this.coTheNhanLenhNguoiChoi(chienBinh)) {
            return;
        }
        ChickenYeuCauToaDoServer.ToaDo yeuCau = ChickenYeuCauToaDoServer.doc(ms);
        if (yeuCau == null) {
            return;
        }
        ChickenDiChuyenServer.KetQua ketQua = ChickenDiChuyenServer.xuLy(
                this.banDo, chienBinh.x, chienBinh.y,
                yeuCau.getX(), yeuCau.getY(),
                chienBinh.quangDuongDiChuyenConLai,
                ChickenCoCheBayAVG.coTheBay(chienBinh));
        chienBinh.x = ketQua.getX();
        chienBinh.y = ketQua.getY();
        chienBinh.quangDuongDiChuyenConLai = ketQua.getConLai();
        this.phatDiChuyenNguoiChoi(chienBinh);
    }

    @Override
    public synchronized void capNhatXY(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms)
            throws IOException {
        ChickenChienBinh chienBinh = this.layNguoiChoi(nguoiChoi);
        if (chienBinh == null || chienBinh.chet || this.daKetThuc) {
            return;
        }
        if (!ChickenYeuCauToaDoServer.choPhepDongBo(
                chienBinh, System.currentTimeMillis())) {
            return;
        }
        ChickenYeuCauToaDoServer.KetQuaDongBo ketQua =
                ChickenYeuCauToaDoServer.dongBoThuDong(
                        ms, this.banDo, chienBinh.x, chienBinh.y,
                        ChickenCoCheBayAVG.coTheBay(chienBinh));
        if (ketQua == null) {
            return;
        }
        if (ketQua.isDaRoi()) {
            chienBinh.x = ketQua.getX();
            chienBinh.y = ketQua.getY();
            this.phatDiChuyenNguoiChoi(chienBinh);
            return;
        }
        nguoiChoi.dichVu.guiCapNhatXYLuyenTap(
                chienBinh.chiSo, chienBinh.x, chienBinh.y);
    }

    @Override
    public synchronized boolean doiSungTrongTran(
            ChickenNguoiChoi nguoiChoi, int chiSoTui) throws IOException {
        ChickenChienBinh chienBinh = this.layNguoiChoi(nguoiChoi);
        if (!this.coTheNhanLenhNguoiChoi(chienBinh)) {
            return false;
        }
        return this.doiSungChoChienBinh(chienBinh, this.chienBinhs,
                this.luotHienTai, this.daKetThuc, chiSoTui, false);
    }

    @Override
    public synchronized boolean kichHoatPow(ChickenNguoiChoi nguoiChoi)
            throws IOException {
        return this.kichHoatPowChoChienBinh(
                this.layNguoiChoi(nguoiChoi), this.chienBinhs,
                this.luotHienTai, this.daKetThuc);
    }

    @Override
    public synchronized void ban(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms)
            throws IOException {
        ChickenChienBinh shooter = this.layNguoiChoi(nguoiChoi);
        if (!this.coTheNhanLenhNguoiChoi(shooter)
                || this.kyNangThorBoss.dangThiTrien(shooter)
                || this.kyNangLokiBoss.dangThiTrien(shooter)) {
            return;
        }
        if (shooter.avenger == ChickenKyNangDacBietLoki.AVG_LOKI
                && shooter.lokiDangChoChonMucTieu) {
            shooter.lokiDangChoChonMucTieu = false;
            shooter.lokiDaGuiMenu = false;
        }

        ChickenQuanLyDanSung.DuLieuSung duLieuSung =
                ChickenQuanLyDanSung.theoPartSung(shooter.maVuKhi);
        ChickenChienBinh.VatPhamChienTrongTran vatPhamDangCho =
                shooter.layVatPhamChienDangCho();
        ChickenYeuCauBanServer.KetQua yeuCau =
                vatPhamDangCho == null
                        ? ChickenYeuCauBanServer.doc(
                                ms, duLieuSung, shooter.avenger)
                        : ChickenYeuCauBanServer.docVatPham(
                                ms, vatPhamDangCho.getCauHinh());
        if (yeuCau == null) {
            return;
        }
        byte loaiDan = yeuCau.getLoaiDan();
        short goc = yeuCau.getGoc();
        byte luc = yeuCau.getLuc();
        byte lucPhu = yeuCau.getLucPhu();
        long thoiGianHoatAnhMs =
                ChickenThoiGianHoatAnhDan.HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        int napDanVatPham = -1;

        try (ChickenNguCanhLaySung.Phien ignored =
                ChickenNguCanhLaySung.batDauPhatBanNguoiChoi()) {
        if (shooter.avenger == ChickenKyNangDacBietIronMan.AVG_IRON_MAN
                && shooter.ironManLaserSanSang) {
            this.banLaserIronManBoss(shooter, goc);
        } else if (this.kyNangUltronBoss.dangBanX3(shooter)) {
            this.banX3UltronBoss(shooter, goc, luc);
        } else {
            ChickenKetQuaDan ketQua = this.taoPhatBanNguoiChoi(
                    shooter, loaiDan, goc, luc, lucPhu);
            if (vatPhamDangCho != null) {
                napDanVatPham = ketQua == null ? -1
                        : this.tieuThuVaDanhDauVatPhamChien(
                                shooter, vatPhamDangCho,
                                ketQua, this.banDo);
                if (napDanVatPham < 0) {
                    return;
                }
            }
            ChickenMayMan.PhienTanCong phienMayMan =
                    ChickenMayMan.batDau(shooter, this.chienBinhs);
            phienMayMan.chuanBiPhongThuTruocPhat(
                    ketQua.satThuongTheoMucTieu.keySet());
            this.phatBan(shooter, ketQua, (byte) 1);
            this.apDungDichChuyenVatPhamSauPhatBan(
                    shooter, vatPhamDangCho, ketQua, this.banDo);
            if (vatPhamDangCho != null) {
                shooter.xoaVatPhamChienDangCho();
            }
            thoiGianHoatAnhMs = ChickenThoiGianHoatAnhDan.tinh(ketQua);
            if (vatPhamDangCho == null
                    && this.dongBoHulkSauPhat(shooter, ketQua)) {
                return;
            }
            this.phaDiaHinhNeuCan(ketQua);
            for (Map.Entry<ChickenChienBinh, Integer> entry
                    : ketQua.satThuongTheoMucTieu.entrySet()) {
                if (!this.daKetThuc && entry.getKey() != null
                        && !entry.getKey().chet && entry.getValue() > 0) {
                    this.gaySatThuong(
                            entry.getKey(),
                            phienMayMan.apDung(entry.getKey(), entry.getValue()));
                }
            }
        }

        // Dù chọn Bắn x3 hay bắn thường, lượt này đã kết thúc nên phải xóa
        // cờ menu để lượt sau Ultron có thể mở lại đúng trạng thái.
        }
        this.kyNangUltronBoss.sauKhiDaBan(shooter);
        ChickenKyNangDacBietIronMan.xoaTrangThaiChoBan(shooter);
        this.kyNangHawkBoss.sauKhiBanThuong(shooter);
        if (!this.daKetThuc) {
            this.choServerKetThucPhatBan(
                    shooter,
                    napDanVatPham > 0 ? napDanVatPham
                            : this.layNapDanSauBanNguoiChoi(shooter),
                    thoiGianHoatAnhMs);
        }
    }

    @Override
    public synchronized void boLuot(ChickenNguoiChoi nguoiChoi) throws IOException {
        ChickenChienBinh chienBinh = this.layNguoiChoi(nguoiChoi);
        if (!this.coTheNhanLenhNguoiChoi(chienBinh)
                || this.kyNangThorBoss.dangThiTrien(chienBinh)
                || this.kyNangLokiBoss.dangThiTrien(chienBinh)) {
            return;
        }
        if (chienBinh.avenger == ChickenKyNangDacBietLoki.AVG_LOKI) {
            chienBinh.lokiDangChoChonMucTieu = false;
            chienBinh.lokiDaGuiMenu = false;
        }
        this.kyNangUltronBoss.huyKhiBoLuot(chienBinh);
        ChickenKyNangDacBietIronMan.xoaTrangThaiChoBan(chienBinh);
        chienBinh.xoaVatPhamChienDangCho();
        this.datNapDanSauHanhDong(
                chienBinh.chiSo & 0xFF,
                ChickenNapDanServer.layKhiKhongTaoPhatDan());
        this.chuyenSangLuotTiepTheo(chienBinh.chiSo & 0xFF);
    }

    @Override
    public synchronized void kiemTraVaCham(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms)
            throws IOException {
        while (ms.boDoc().available() > 0) {
            ms.boDoc().readByte();
        }
    }

    @Override
    public synchronized void khiNguoiChoiRoi(ChickenNguoiChoi nguoiChoi) {
        ChickenChienBinh chienBinh = this.layNguoiChoi(nguoiChoi);
        if (chienBinh == null) {
            this.boDangKyNguoiChoi(nguoiChoi);
            return;
        }
        boolean dangDenLuot = (chienBinh.chiSo == this.luotHienTai);
        chienBinh.hp = 0;
        chienBinh.chet = true;
        this.boDangKyNguoiChoi(nguoiChoi);
        try {
            this.phatCapNhatMau(chienBinh);
            if (this.kiemTraKetThuc()) {
                return;
            }
            if (dangDenLuot) {
                this.chuyenSangLuotTiepTheo(chienBinh.chiSo & 0xFF);
            }
        } catch (IOException ignored) {
        }
        System.out.println("[BOSS HAI TOA THAP][NGUOI_CHOI_ROI] player="
                + (nguoiChoi == null ? "null" : nguoiChoi.ten)
                + " tinhLaChet=true");
    }

    @Override
    public synchronized boolean kichHoatKyNangIronMan(
            ChickenNguoiChoi nguoiChoi
    ) {
        return ChickenKyNangDacBietIronMan.kichHoatTrongTran(
                this.layNguoiChoi(nguoiChoi),
                this.daKetThuc,
                this.luotHienTai
        );
    }

    @Override
    public synchronized boolean kichHoatKyNangUltron(
            ChickenNguoiChoi nguoiChoi
    ) throws IOException {
        ChickenChienBinh ultron = this.layNguoiChoi(nguoiChoi);
        return this.kyNangUltronBoss != null
                && this.kyNangUltronBoss.kichHoatBanX3(ultron);
    }

    @Override
    public synchronized void nhanLenhKyNangDacBiet(
            ChickenNguoiChoi nguoiChoi,
            ChickenTinNhan ms
    ) throws IOException {
        ChickenChienBinh chienBinh = this.layNguoiChoi(nguoiChoi);
        if (chienBinh == null
                || chienBinh.chet
                || chienBinh.chiSo != this.luotHienTai
                || this.daKetThuc) {
            return;
        }
        if (chienBinh.avenger == ChickenKyNangDacBietThor.AVG_THOR) {
            this.kyNangThorBoss.nhanLenh(chienBinh, ms);
            return;
        }
        if (chienBinh.avenger == ChickenKyNangDacBietLoki.AVG_LOKI) {
            this.kyNangLokiBoss.nhanLenh(chienBinh, ms);
            return;
        }
        this.kyNangHawkBoss.nhanLenh(chienBinh, ms);
    }

    @Override
    public synchronized void xuLyCmd91Hawk(
            ChickenNguoiChoi nguoiChoi,
            ChickenTinNhan ms
    ) throws IOException {
        this.nhanLenhKyNangDacBiet(nguoiChoi, ms);
    }

    @Override
    public synchronized void dungBot() {
        if (this.daKetThuc && this.boHenGio.isShutdown()) {
            return;
        }
        this.daKetThuc = true;
        this.huyTacVuHetLuot();
        this.huyChoKetThucPhatBan();
        for (ChickenChienBinh chienBinh : this.nguoiChoiConPhien()) {
            this.boDangKyNguoiChoi(chienBinh.nguoiChoi);
        }
        this.boHenGio.shutdownNow();
    }

    public synchronized ChickenChienBinh[] chupChienBinh() {
        return this.chienBinhs.clone();
    }

    public synchronized int[] chupIdSungBoss() {
        return this.idSungBoss.clone();
    }

    private void taoNguoiChoi(SanhChoBoss sanh) {
        if (sanh == null) {
            return;
        }
        for (ThanhVienBoss thanhVien : sanh.chupThanhVien()) {
            if (thanhVien == null || thanhVien.getNguoiChoi() == null) {
                continue;
            }
            int ghe = thanhVien.getGhe() & 0xFF;
            if (ghe < 0 || ghe >= SO_SLOT_NGUOI_CHOI) {
                continue;
            }
            ChickenChienBinh chienBinh = new ChickenChienBinh(
                    thanhVien.getNguoiChoi(), (byte) ghe,
                    this.banDo.laySinhX(ghe), this.banDo.laySinhY(ghe));
            this.chienBinhs[ghe] = chienBinh;
        }
        com.chicken.chiso.ChickenHieuUngDongDoi.apDungChoNhomDongMinh(
                this.chienBinhs);
    }

    private void taoBoss() {
        for (CauHinhBossHaiToaThap.CauHinh cauHinh
                : this.danhSachBossBanDau) {
            this.taoMotBoss(cauHinh);
        }
    }

    private ChickenChienBinh taoMotBoss(
            CauHinhBossHaiToaThap.CauHinh cauHinh
    ) {
        if (cauHinh == null) {
            return null;
        }
        int slot = cauHinh.getSlot() & 0xFF;
        if (slot < SO_SLOT_NGUOI_CHOI || slot >= SO_SLOT) {
            return null;
        }
        ChickenQuanLyDanSung.DuLieuSung duLieu = cauHinh.laBossBanSung()
                ? ChickenSungShopBoss.chonNgauNhienKhongAvg()
                : null;
        if (duLieu == null && cauHinh.laBossBanSung()) {
            duLieu = ChickenQuanLyDanSung.theoPartSung(
                    cauHinh.getVuKhi());
        }
        int idSung = duLieu == null ? -1 : duLieu.getIdSung();
        short partSung = duLieu == null
                ? cauHinh.getVuKhi()
                : duLieu.getPartSung();
        int tanCongDuPhong =
                CauHinhBossHaiToaThap.layTanCongTheoSung(partSung);
        int tanCong = cauHinh.laBossBanSung()
                ? ChickenSungShopBoss.layTanCongTheoId(
                        idSung, tanCongDuPhong)
                : 0;
        this.idSungBoss[slot] = idSung;
        ChickenChienBinh boss = new ChickenChienBinh(
                cauHinh.getSlot(), cauHinh.getId(), cauHinh.getX(), cauHinh.getY(),
                cauHinh.getTen(), partSung,
                CauHinhBossHaiToaThap.MAU_BOSS, tanCong, 0);
        this.chienBinhs[slot] = boss;
        this.cauHinhBoss[slot] = cauHinh;
        this.napDan[slot] = 0;
        return boss;
    }

    /**
     * Moi Cam tu dau tien (slot 10-17) chi co dung mot quan du bi. Quan du bi
     * dung slot rieng 20-27 de packet tao moi khong ghi de animation chet cua
     * quan cu, va luon xuat hien tai diem spawn goc thay vi toa do luc chet.
     */
    private synchronized ChickenChienBinh xuatHienCamTuDuBiNeuCo(
            ChickenChienBinh camTuDaChet
    ) {
        if (camTuDaChet == null || !camTuDaChet.bot || !camTuDaChet.chet
                || camTuDaChet.hp > 0 || this.daKetThuc) {
            return null;
        }
        int slotGoc = camTuDaChet.chiSo & 0xFF;
        CauHinhBossHaiToaThap.CauHinh cauHinhDuBi =
                CauHinhBossHaiToaThap.layCamTuDuBiTheoSlotGoc(slotGoc);
        if (cauHinhDuBi == null) {
            return null;
        }
        int slotDuBi = cauHinhDuBi.getSlot() & 0xFF;
        if (slotDuBi < 0 || slotDuBi >= this.chienBinhs.length
                || this.chienBinhs[slotDuBi] != null) {
            return null;
        }

        ChickenChienBinh camTuDuBi = this.taoMotBoss(cauHinhDuBi);
        if (camTuDuBi == null) {
            return null;
        }
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            try {
                nguoiNhan.nguoiChoi.dichVu.guiTaoCamTuDuBiNgay(
                        cauHinhDuBi.getSlot(),
                        cauHinhDuBi.getId(),
                        cauHinhDuBi.getTen(),
                        cauHinhDuBi.getHead(),
                        cauHinhDuBi.getLeg(),
                        cauHinhDuBi.getBody(),
                        cauHinhDuBi.getHat(),
                        cauHinhDuBi.getWing(),
                        camTuDuBi.maVuKhi,
                        cauHinhDuBi.getX(),
                        cauHinhDuBi.getY(),
                        CauHinhBossHaiToaThap.MAU_BOSS);
            } catch (IOException loi) {
                System.err.println(
                        "[BOSS HAI TOA THAP][CAM_TU_DU_BI_LOI_GUI] slotGoc="
                                + slotGoc + " slotDuBi=" + slotDuBi
                                + " loi=" + loi.getMessage());
            }
        }
        System.out.println("[BOSS HAI TOA THAP][CAM_TU_DU_BI] slotGoc="
                + slotGoc + " slotDuBi=" + slotDuBi
                + " x=" + camTuDuBi.x + " y=" + camTuDuBi.y);
        return camTuDuBi;
    }

    private ChickenQuanLyDanSung.DuLieuSung layDuLieuSungBoss(
            int slot,
            ChickenChienBinh boss
    ) {
        if (slot >= 0 && slot < this.idSungBoss.length) {
            ChickenQuanLyDanSung.DuLieuSung theoId =
                    ChickenQuanLyDanSung.theoIdSung(
                            this.idSungBoss[slot]);
            if (theoId != null) {
                return theoId;
            }
        }
        return boss == null
                ? null
                : ChickenQuanLyDanSung.theoPartSung(boss.maVuKhi);
    }

    private int layNapDanBoss(int slot) {
        if (slot < 0 || slot >= this.idSungBoss.length) {
            return 100;
        }
        ChickenChienBinh boss = this.chienBinhs[slot];
        int duPhong = boss == null
                ? 100
                : CauHinhBossHaiToaThap.layNapDanTheoSung(
                        boss.maVuKhi);
        return ChickenSungShopBoss.layNapDanTheoId(
                this.idSungBoss[slot], duPhong);
    }

    private void khoiTaoKyNangAVG() {
        this.kyNangHawkBoss = new ChickenKyNangDacBietHawk(
                this.chienBinhs,
                this.banDo,
                new ChickenKyNangDacBietHawk.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossHaiToaThap.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossHaiToaThap.this.luotHienTai;
                    }

                    @Override
                    public void guiHoatAnhMuiTen(
                            ChickenChienBinh hawk,
                            short goc,
                            ChickenHoatAnhHawk.DuongDan duongDan
                    ) throws IOException {
                        BossHaiToaThap.this.phatHoatAnhMuiTenHawkBoss(
                                hawk, goc, duongDan);
                    }

                    @Override
                    public void gaySatThuong(
                            ChickenChienBinh mucTieu,
                            int satThuong
                    ) throws IOException {
                        BossHaiToaThap.this.gaySatThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        BossHaiToaThap.this.sangLuotSauKyNangAVG();
                    }
                }
        );

        this.kyNangThorBoss = new ChickenKyNangDacBietThor(
                this.chienBinhs,
                this.banDo,
                new ChickenKyNangDacBietThor.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossHaiToaThap.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossHaiToaThap.this.luotHienTai;
                    }

                    @Override
                    public void guiTiaSet(
                            ChickenChienBinh thor,
                            byte loaiHieuUng,
                            short[] cacX,
                            short[] cacY
                    ) throws IOException {
                        BossHaiToaThap.this.phatTiaSetThorBoss(
                                thor, loaiHieuUng, cacX, cacY);
                    }

                    @Override
                    public void gaySatThuong(
                            ChickenChienBinh mucTieu,
                            int satThuong
                    ) throws IOException {
                        BossHaiToaThap.this.gaySatThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        BossHaiToaThap.this.sangLuotSauKyNangAVG();
                    }
                }
        );

        this.kyNangLokiBoss = new ChickenKyNangDacBietLoki(
                this.chienBinhs,
                new ChickenKyNangDacBietLoki.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossHaiToaThap.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossHaiToaThap.this.luotHienTai;
                    }

                    @Override
                    public void guiMenuLoki(ChickenChienBinh loki) {
                        loki.nguoiChoi.dichVu.guiChonKyNangLoki();
                    }

                    @Override
                    public void guiChonMucTieuLoki(ChickenChienBinh loki) {
                        loki.nguoiChoi.dichVu.guiChonMucTieuLoki();
                    }

                    @Override
                    public void guiBienHinh(
                            ChickenChienBinh loki,
                            ChickenChienBinh mucTieu
                    ) throws IOException {
                        BossHaiToaThap.this.phatBienHinhLokiBoss(loki, mucTieu);
                    }

                    @Override
                    public void capNhatMau(ChickenChienBinh loki)
                            throws IOException {
                        BossHaiToaThap.this.phatCapNhatMau(loki);
                    }
                }
        );

        this.kyNangUltronBoss = new ChickenKyNangDacBietUltron(
                new ChickenKyNangDacBietUltron.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossHaiToaThap.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossHaiToaThap.this.luotHienTai;
                    }

                    @Override
                    public void guiMenuUltron(ChickenChienBinh ultron) {
                        ultron.nguoiChoi.dichVu.guiChonKyNangUltron();
                    }
                }
        );
    }

    private synchronized void chuyenSangLuotTiepTheo(int sauSlot) throws IOException {
        this.banDo.ketThucLuotVoiRong(
                sauSlot >= 0 && sauSlot < SO_SLOT_NGUOI_CHOI
                        && this.chienBinhs[sauSlot] != null
                        && this.chienBinhs[sauSlot].nguoiChoi != null);
        if (sauSlot >= 0 && sauSlot < this.chienBinhs.length) {
            com.chicken.chien.ChickenTrangThaiHanhDongLuot.ketThucLuot(
                    this.chienBinhs[sauSlot]);
        }
        if (sauSlot >= 0 && sauSlot < this.chienBinhs.length) {
            this.huyPowSauLuot(this.chienBinhs[sauSlot], this.chienBinhs);
        }
        if (this.daKetThuc || this.kiemTraKetThuc()) {
            return;
        }
        this.huyChoKetThucPhatBan();
        this.huyTacVuHetLuot();
        int slotTiep = this.timSlotTheoNapDan(sauSlot);
        if (slotTiep < 0) {
            this.ketThuc(false);
            return;
        }
        this.luotHienTai = (byte) slotTiep;
        this.maPhienLuot++;
        ChickenChienBinh hienTai = this.chienBinhs[slotTiep];
        if (slotTiep < SO_SLOT_NGUOI_CHOI) {
            hienTai.hoiDayQuangDuongDiChuyen();
        }

        // Mỗi lượt đổi gió đúng một lần; toàn bộ loạt đạn trong lượt dùng chung gió.
        this.gioHienTai = ChickenHeThongGio.taoGioMoi();
        this.phatLuot(hienTai);

        long phien = this.maPhienLuot;

        // Mọi lượt đều có bộ đếm 25 giây. Nếu người chơi không bắn hoặc
        // một tác vụ boss bị kẹt, server vẫn tự cộng nạp đạn và chuyển lượt.
        this.tacVuHetLuot = this.boHenGio.schedule(
                () -> this.xuLyHetThoiGianLuot(slotTiep, phien),
                GIAY_MOI_LUOT,
                TimeUnit.SECONDS);

        if (slotTiep >= SO_SLOT_NGUOI_CHOI) {
            CauHinhBossHaiToaThap.CauHinh cauHinhTiep =
                    this.cauHinhBoss[slotTiep];
            long treBatDauMs = cauHinhTiep != null && cauHinhTiep.laCamTu()
                    ? TRE_CAM_TU_BAT_DAU_MS
                    : TRE_BOSS_BAT_DAU_MS;
            this.boHenGio.schedule(() -> this.thucHienLuotBoss(slotTiep, phien),
                    treBatDauMs, TimeUnit.MILLISECONDS);
        }
    }

    private void xuLyHetThoiGianLuot(int slot, long phien) {
        synchronized (this) {
            if (this.daKetThuc
                    || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot) {
                return;
            }
            ChickenChienBinh chienBinh = this.chienBinhs[slot];
            if (slot < SO_SLOT_NGUOI_CHOI && chienBinh != null) {
                this.kyNangUltronBoss.huyKhiBoLuot(chienBinh);
                chienBinh.lokiDangChoChonMucTieu = false;
                chienBinh.lokiDaGuiMenu = false;
            }
            int napDanSauHetGio = this.layNapDanKhiHetThoiGian(slot, chienBinh);
            if (napDanSauHetGio > 0) {
                this.datNapDanSauHanhDong(slot, napDanSauHetGio);
            }
            System.out.println("[BOSS HAI TOA THAP][HET_25_GIAY] slot=" + slot
                    + " ten=" + (chienBinh == null ? "null" : chienBinh.ten)
                    + " napDanMoi=" + napDanSauHetGio
                    + " tuDongChuyenLuot=true");
            try {
                this.chuyenSangLuotTiepTheo(slot);
            } catch (IOException ignored) {
            }
        }
    }

    private int layNapDanKhiHetThoiGian(int slot, ChickenChienBinh chienBinh) {
        if (slot < SO_SLOT_NGUOI_CHOI) {
            return ChickenNapDanServer.layKhiKhongTaoPhatDan();
        }
        CauHinhBossHaiToaThap.CauHinh cauHinh = slot >= 0 && slot < this.cauHinhBoss.length
                ? this.cauHinhBoss[slot] : null;
        if (cauHinh != null && cauHinh.laCamTu()) {
            return BossCamTu.NAP_DAN_SAU_DI_CHUYEN;
        }
        return chienBinh == null
                ? 100
                : this.layNapDanBoss(slot);
    }

    private void thucHienLuotBoss(int slot, long phien) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot) {
                return;
            }
            ChickenChienBinh boss = this.chienBinhs[slot];
            CauHinhBossHaiToaThap.CauHinh cauHinh = this.cauHinhBoss[slot];
            if (boss == null || boss.chet || cauHinh == null) {
                this.sangLuotSauBoss(slot, phien, 0);
                return;
            }
            if (cauHinh.laBossBanSung()) {
                this.thucHienBossBanSung(boss, slot, phien);
            } else {
                ChickenChienBinh mucTieu =
                        BossCamTu.timNguoiSongGanNhat(boss, this.chienBinhs);
                int slotMucTieu = mucTieu == null ? -1 : mucTieu.chiSo & 0xFF;
                int huongXKhoa = mucTieu == null
                        ? 0 : BossCamTu.layHuongX(boss, mucTieu);
                this.thucHienCamTu(
                        boss,
                        slot,
                        phien,
                        slotMucTieu,
                        huongXKhoa);
            }
        }
    }

    private void thucHienBossBanSung(ChickenChienBinh boss, int slot, long phien) {
        ChickenChienBinh mucTieu = BossBanSung.chonNgauNhienNguoiSong(this.chienBinhs);
        if (mucTieu == null) {
            this.sangLuotSauBoss(slot, phien,
                    this.layNapDanBoss(slot));
            return;
        }
        ChickenQuanLyDanSung.DuLieuSung duLieu =
                this.layDuLieuSungBoss(slot, boss);
        boolean aimChuan =
                com.chicken.phong.boss.trandau.baovay.BossBanSung
                        .chonCheDoAimChuan();
        ChickenKetQuaDan ketQua =
                com.chicken.phong.boss.trandau.baovay.BossBanSung
                        .taoPhatBanTheoCongThucSung(
                                boss,
                                mucTieu,
                                this.chienBinhs,
                                duLieu,
                                this.banDo,
                                this.layWindXChoChienBinh(boss),
                                this.layWindYChoChienBinh(boss),
                                aimChuan
                        );
        System.out.println("[BOSS HAI TOA THAP][BOSS_BAN] boss=" + boss.ten
                + " target=" + mucTieu.ten
                + " idSung=" + this.idSungBoss[slot]
                + " tenSung="
                + (duLieu == null ? "unknown" : duLieu.getTenSung())
                + " sungPart=" + boss.maVuKhi
                + " loaiDan=" + (ketQua.loaiDan & 0xFF)
                + " soDuong=" + ketQua.cacDuongX.length
                + " soLanBan=1"
                + " cheDo=" + (aimChuan ? "AIM_CHUAN" : "BAN_BUA"));
        long thoiGianHoatAnhMs = ChickenThoiGianHoatAnhDan.tinh(ketQua);
        if (!this.daKetThuc) {
            this.boHenGio.schedule(() -> this.sangLuotSauBoss(
                            slot, phien, this.layNapDanBoss(slot)),
                    thoiGianHoatAnhMs, TimeUnit.MILLISECONDS);
        }
        ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(boss, this.chienBinhs);
        try {
            this.phatBan(boss, ketQua, (byte) 1);
            this.phaDiaHinhNeuCan(ketQua);
            for (Map.Entry<ChickenChienBinh, Integer> entry
                    : ketQua.satThuongTheoMucTieu.entrySet()) {
                if (!this.daKetThuc && entry.getKey() != null
                        && !entry.getKey().chet && entry.getValue() > 0) {
                    this.gaySatThuong(
                            entry.getKey(),
                            phienMayMan.apDung(entry.getKey(), entry.getValue()));
                }
            }
        } catch (Exception loi) {
            System.err.println("[BOSS HAI TOA THAP][LOI_XU_LY_PHAT_BAN] slot="
                    + slot + " ten=" + boss.ten
                    + " idSung=" + this.idSungBoss[slot]
                    + " loi=" + loi.getClass().getSimpleName()
                    + ": " + loi.getMessage());
        }
    }

    private void thucHienCamTu(
            ChickenChienBinh camTu,
            int slot,
            long phien,
            int slotMucTieu,
            int huongXKhoa
    ) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot || camTu.chet) {
                this.sangLuotSauBoss(slot, phien, 0);
                return;
            }
            ChickenChienBinh mucTieu = this.layNguoiChoiSongTheoSlot(slotMucTieu);
            if (mucTieu == null) {
                mucTieu = BossCamTu.timNguoiSongGanNhat(
                        camTu, this.chienBinhs);
                slotMucTieu = mucTieu == null ? -1 : mucTieu.chiSo & 0xFF;
                huongXKhoa = mucTieu == null ? 0 : BossCamTu.layHuongX(camTu, mucTieu);
            }
            if (mucTieu == null) {
                this.sangLuotSauBoss(slot, phien, BossCamTu.NAP_DAN_SAU_DI_CHUYEN);
                return;
            }
            if (BossCamTu.daChamNguoiChoi(camTu, mucTieu)) {
                this.noCamTu(camTu, mucTieu, slot, phien);
                return;
            }
            if (BossCamTu.daCanTrucX(camTu, mucTieu)) {
                // Không gửi CMD 21 chỉ để đổi Y: client sẽ ép look=0
                // và làm Cảm tử xoay qua lại dù không hề chạy ngang.
                System.out.println("[BOSS HAI TOA THAP][CAM_TU_DOI_CUNG_TRUC_X] boss="
                        + camTu.ten + " target=" + mucTieu.ten
                        + " x=" + camTu.x
                        + " bossY=" + camTu.y
                        + " targetY=" + mucTieu.y);
                this.sangLuotSauBoss(
                        slot, phien, BossCamTu.NAP_DAN_SAU_DI_CHUYEN);
                return;
            }
            short[] buoc = BossCamTu.tinhBuocTiepTheo(
                    camTu, mucTieu, BossCamTu.QUANG_DUONG_MOI_LUOT,
                    huongXKhoa, this.banDo);
            if (buoc == null || buoc.length < 2
                    || !BossCamTu.laDiemDenClientCoTheKetThuc(
                            camTu, this.banDo, buoc[0], buoc[1])) {
                System.err.println("[BOSS HAI TOA THAP][CAM_TU_BO_WAYPOINT_KHONG_HOP_LE] boss="
                        + camTu.ten + " from=" + camTu.x + "," + camTu.y
                        + " to=" + (buoc == null || buoc.length < 2
                                ? "null" : buoc[0] + "," + buoc[1]));
                this.sangLuotSauBoss(
                        slot, phien, BossCamTu.NAP_DAN_SAU_DI_CHUYEN);
                return;
            }
            short xTruoc = camTu.x;
            short yTruoc = camTu.y;
            int daDi = (int) Math.round(Math.hypot(
                    buoc[0] - xTruoc, buoc[1] - yTruoc));
            if (daDi <= 0) {
                this.sangLuotSauBoss(slot, phien, BossCamTu.NAP_DAN_SAU_DI_CHUYEN);
                return;
            }
            camTu.x = kepShort(
                    buoc[0],
                    ChickenKichThuocNhanVat.BOSS_NUA_RONG,
                    this.banDo.getWidth() - 1
                            - ChickenKichThuocNhanVat.BOSS_NUA_RONG);
            // Cho phép Y vượt đáy một đoạn để trọng lực kết luận rơi khỏi map.
            camTu.y = kepShort(
                    buoc[1],
                    0,
                    this.banDo.getHeight() + 32);
            try {
                this.phatDiChuyenBoss(camTu);
            } catch (IOException ignored) {
            }
            long thoiGianHoatAnhDiChuyenMs =
                    BossCamTu.tinhThoiGianHoatAnhDiChuyen(
                            xTruoc, yTruoc, camTu.x, camTu.y);
            System.out.println("[BOSS HAI TOA THAP][CAM_TU_DI_CHUYEN] boss="
                    + camTu.ten
                    + " from=" + xTruoc + "," + yTruoc
                    + " to=" + camTu.x + "," + camTu.y
                    + " distance=" + daDi
                    + " animationMs=" + thoiGianHoatAnhDiChuyenMs);
            if (BossCamTu.daRoiKhoiMap(camTu, this.banDo)) {
                this.xuLyCamTuRoiKhoiMap(camTu, slot, phien);
                return;
            }
            if (BossCamTu.daChamNguoiChoi(camTu, mucTieu)) {
                final int slotMucTieuSauDiChuyen = slotMucTieu;
                // CMD 22 khong duoc chen vao khi client van dang noi suy
                // CMD 21; server cho du thoi gian de CMD 22 khong cat animation.
                this.boHenGio.schedule(
                        () -> this.hoanTatCamTuSauDiChuyen(
                                camTu, slot, phien, slotMucTieuSauDiChuyen),
                        thoiGianHoatAnhDiChuyenMs,
                        TimeUnit.MILLISECONDS);
                return;
            }
            // Client goc chi cho moi Cam tu mot lan chay trong mot luot.
            // Neu dia hinh chan truoc moc 36 px thi ket thuc tai do, khong
            // phat them packet "di bu" lam chong animation va keo dai luot.
            this.boHenGio.schedule(() -> this.sangLuotSauBoss(
                            slot, phien, BossCamTu.NAP_DAN_SAU_DI_CHUYEN),
                    thoiGianHoatAnhDiChuyenMs, TimeUnit.MILLISECONDS);
        }
    }

    private void hoanTatCamTuSauDiChuyen(
            ChickenChienBinh camTu,
            int slot,
            long phien,
            int slotMucTieu
    ) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot
                    || camTu == null || camTu.chet || camTu.hp <= 0) {
                return;
            }
            ChickenChienBinh mucTieu =
                    this.layNguoiChoiSongTheoSlot(slotMucTieu);
            if (mucTieu == null || !BossCamTu.daChamNguoiChoi(camTu, mucTieu)) {
                mucTieu = BossCamTu.timNguoiSongGanNhat(
                        camTu, this.chienBinhs);
            }
            if (mucTieu != null && BossCamTu.daChamNguoiChoi(camTu, mucTieu)) {
                this.noCamTu(camTu, mucTieu, slot, phien);
            } else {
                this.sangLuotSauBoss(
                        slot, phien, BossCamTu.NAP_DAN_SAU_DI_CHUYEN);
            }
        }
    }

    private ChickenChienBinh layNguoiChoiSongTheoSlot(int slot) {
        if (slot < 0 || slot >= SO_SLOT_NGUOI_CHOI) {
            return null;
        }
        ChickenChienBinh nguoiChoi = this.chienBinhs[slot];
        if (nguoiChoi == null
                || nguoiChoi.chet
                || nguoiChoi.hp <= 0
                || !nguoiChoi.coPhien()) {
            return null;
        }
        return nguoiChoi;
    }

    private boolean trongTamNoCamTu(
            ChickenChienBinh camTu,
            ChickenChienBinh nguoiChoi
    ) {
        return nguoiChoi != null
                && !nguoiChoi.chet
                && nguoiChoi.hp > 0
                && nguoiChoi.coPhien()
                && BossCamTu.trongPhamViKichNo(
                        camTu,
                        nguoiChoi,
                        CauHinhBossHaiToaThap.BAN_KINH_NO_CAM_TU);
    }

    private void xuLyCamTuRoiKhoiMap(
            ChickenChienBinh camTu,
            int slot,
            long phien
    ) {
        try {
            camTu.hp = 0;
            camTu.chet = true;
            this.phatCapNhatMau(camTu);
            System.out.println("[BOSS HAI TOA THAP][CAM_TU_ROI_MAP] boss="
                    + camTu.ten + " x=" + camTu.x + " y=" + camTu.y
                    + " apDungTrongLuc=true");
        } catch (IOException ignored) {
        }
        this.xuatHienCamTuDuBiNeuCo(camTu);
        if (!this.daKetThuc) {
            this.boHenGio.schedule(
                    () -> this.sangLuotSauBoss(slot, phien, 0),
                    120,
                    TimeUnit.MILLISECONDS);
        }
    }

    private void noCamTu(
            ChickenChienBinh camTu,
            ChickenChienBinh mucTieu,
            int slot,
            long phien
    ) {
        int soMucTieuTrung = 0;
        long thoiGianHoatAnhNo =
                ChickenThoiGianHoatAnhDan.HIEU_UNG_KHONG_CO_QUY_DAO_MS;
        ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(camTu, this.chienBinhs);
        try {
            ChickenKetQuaDan hieuUngNo = ChickenHoatAnhNoCamTu.tao(
                    camTu,
                    mucTieu,
                    CauHinhBossHaiToaThap.SAT_THUONG_CAM_TU);
            thoiGianHoatAnhNo = ChickenThoiGianHoatAnhDan.tinh(hieuUngNo);
            this.phatBan(camTu, hieuUngNo, (byte) 1);
            this.banDo.phaDiaHinh(
                    camTu.x,
                    camTu.y,
                    CauHinhBossHaiToaThap.LOAI_DAN_HIEU_UNG_NO_CAM_TU);
        } catch (IOException loi) {
            System.err.println("[BOSS HAI TOA THAP][CAM_TU_LOI_HIEU_UNG] boss="
                    + camTu.ten + " loi=" + loi.getMessage());
        }
        for (int i = 0; i < SO_SLOT_NGUOI_CHOI; i++) {
            ChickenChienBinh nguoiTrongTamNo = this.chienBinhs[i];
            if (!this.trongTamNoCamTu(camTu, nguoiTrongTamNo)) {
                continue;
            }
            int satThuongTheoKhoangCach =
                    BossCamTu.tinhSatThuongNoTheoKhoangCach(
                            camTu,
                            nguoiTrongTamNo,
                            CauHinhBossHaiToaThap.SAT_THUONG_CAM_TU,
                            CauHinhBossHaiToaThap.BAN_KINH_NO_CAM_TU,
                            CauHinhBossHaiToaThap.PHAN_TRAM_DAMAGE_RIA_CAM_TU);
            if (satThuongTheoKhoangCach <= 0) {
                continue;
            }
            try {
                this.gaySatThuong(
                        nguoiTrongTamNo,
                        phienMayMan.apDung(
                                nguoiTrongTamNo,
                                satThuongTheoKhoangCach));
                soMucTieuTrung++;
            } catch (IOException loi) {
                System.err.println("[BOSS HAI TOA THAP][CAM_TU_LOI_DAMAGE] boss="
                        + camTu.ten + " target=" + nguoiTrongTamNo.ten
                        + " loi=" + loi.getMessage());
            }
        }
        camTu.hp = 0;
        camTu.chet = true;
        try {
            this.phatCapNhatMau(camTu);
        } catch (IOException loi) {
            System.err.println("[BOSS HAI TOA THAP][CAM_TU_LOI_CAP_NHAT_MAU] boss="
                    + camTu.ten + " loi=" + loi.getMessage());
        }
        this.xuatHienCamTuDuBiNeuCo(camTu);
        System.out.println("[BOSS HAI TOA THAP][CAM_TU_NO] boss=" + camTu.ten
                + " targetKichNo=" + mucTieu.ten + " damage="
                + CauHinhBossHaiToaThap.SAT_THUONG_CAM_TU
                + " banKinh="
                + CauHinhBossHaiToaThap.BAN_KINH_NO_CAM_TU
                + " damageRia="
                + CauHinhBossHaiToaThap.PHAN_TRAM_DAMAGE_RIA_CAM_TU + "%"
                + " soMucTieuTrung=" + soMucTieuTrung
                + " bossTuChet=true");
        if (!this.daKetThuc) {
            this.boHenGio.schedule(() -> this.sangLuotSauBoss(slot, phien, 0),
                    thoiGianHoatAnhNo, TimeUnit.MILLISECONDS);
        }
    }

    private void sangLuotSauBoss(int slot, long phien, int napDanSauHanhDong) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot) {
                return;
            }
            if (napDanSauHanhDong > 0) {
                this.datNapDanSauHanhDong(slot, napDanSauHanhDong);
            }
            try {
                this.chuyenSangLuotTiepTheo(slot);
            } catch (IOException ignored) {
            }
        }
    }

    private ChickenKetQuaDan taoPhatBanNguoiChoi(
            ChickenChienBinh shooter, byte loaiDan, short goc, byte luc, byte lucPhu
    ) {
        ChickenChienBinh.VatPhamChienTrongTran vatPham =
                shooter == null ? null : shooter.layVatPhamChienDangCho();
        if (vatPham != null) {
            ChickenQuanLyCongThucSung.KiemTraBanDo kiemTra =
                    this.kiemTraBanDo();
            return this.taoPhatBanVatPhamChoCheDo(
                    shooter,
                    vatPham.getCauHinh(),
                    goc,
                    luc,
                    ChickenHeThongGio.layWindXChoItem(
                            this.gioHienTai, vatPham.getIdVatPham()),
                    ChickenHeThongGio.layWindYChoItem(
                            this.gioHienTai, vatPham.getIdVatPham()),
                    kiemTra,
                    this.chienBinhs,
                    new ChickenPhatBanServer.BoLocMucTieu() {
                        @Override
                        public boolean chapNhan(
                                ChickenChienBinh nguoiBan,
                                ChickenChienBinh mucTieu
                        ) {
                            return ChickenLuatVaChamPhongBoss.chapNhan(
                                    nguoiBan, mucTieu);
                        }
                    }
            );
        }
        if (shooter != null
                && shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON) {
            return this.taoPhatBanUltronThuong(shooter, loaiDan, goc, luc);
        }
        ChickenQuanLyCongThucSung.KiemTraBanDo kiemTra = this.kiemTraBanDo();
        short[] dauNong = ChickenToaDoDauNong.layChoNguoiChoi(
                shooter.x, shooter.y, goc, shooter.maVuKhi, kiemTra);
        ChickenQuanLyDanSung.DuLieuSung sung =
                ChickenQuanLyDanSung.theoPartSung(shooter.maVuKhi);
        return ChickenPhatBanServer.tao(
                shooter, dauNong[0], dauNong[1], goc, luc, lucPhu, sung,
                this.layWindXChoChienBinh(shooter),
                this.layWindYChoChienBinh(shooter),
                kiemTra, this.chienBinhs,
                new ChickenPhatBanServer.BoLocMucTieu() {
                    @Override
                    public boolean chapNhan(
                            ChickenChienBinh nguoiBan, ChickenChienBinh mucTieu) {
                        return ChickenLuatVaChamPhongBoss.chapNhan(
                                nguoiBan, mucTieu);
                    }
                });
    }

    private ChickenKetQuaDan taoPhatBanUltronThuong(
            ChickenChienBinh shooter,
            byte loaiDan,
            short goc,
            byte luc
    ) {
        goc = ChickenGocBanUltron.chuanHoa(goc);
        short[] dauNong = ChickenGocBanUltron.layDiemBatDauDuongCan(
                shooter.x,
                shooter.y,
                goc,
                this.banDo.getWidth(),
                this.banDo.getHeight()
        );
        ChickenCongThucBanUltron.DuongTia tia =
                ChickenCongThucBanUltron.taoTiaThang(
                        dauNong[0],
                        dauNong[1],
                        goc,
                        this.banDo.getWidth(),
                        this.banDo.getHeight()
                );
        short[][] dungTaiMap = this.catTiaTaiVaChamBanDo(
                tia.getX(), tia.getY());
        short[] xs = dungTaiMap[0];
        short[] ys = dungTaiMap[1];
        VaChamBoss vaCham = this.timBossTrung(xs, ys);
        ChickenChienBinh mucTieu = null;
        if (vaCham != null) {
            mucTieu = vaCham.boss;
            short[][] daCat = catTaiVaCham(
                    xs, ys, vaCham.chiSoDoan, vaCham.x, vaCham.y);
            xs = daCat[0];
            ys = daCat[1];
        }
        int satThuong = mucTieu == null
                ? 0
                : Math.max(1, shooter.tanCong - mucTieu.giap);
        return new ChickenKetQuaDan(
                loaiDan,
                dauNong[0],
                dauNong[1],
                goc,
                luc,
                xs,
                ys,
                mucTieu,
                satThuong
        );
    }

    private void banX3UltronBoss(
            ChickenChienBinh shooter,
            short goc,
            byte luc
    ) throws IOException {
        goc = ChickenGocBanUltron.chuanHoa(goc);
        short[] dauNong = ChickenGocBanUltron.layDiemBatDauDuongCan(
                shooter.x,
                shooter.y,
                goc,
                this.banDo.getWidth(),
                this.banDo.getHeight()
        );
        ChickenCongThucBanUltron.DuongTia tiaGiuaDayDu =
                ChickenCongThucBanUltron.taoTiaThang(
                        dauNong[0],
                        dauNong[1],
                        goc,
                        this.banDo.getWidth(),
                        this.banDo.getHeight()
                );
        short[][] dungTaiMap = this.catTiaTaiVaChamBanDo(
                tiaGiuaDayDu.getX(), tiaGiuaDayDu.getY());
        short[] tiaGiuaX = dungTaiMap[0];
        short[] tiaGiuaY = dungTaiMap[1];

        VaChamBoss vaCham = this.timBossTrung(tiaGiuaX, tiaGiuaY);
        ChickenChienBinh mucTieu = null;
        if (vaCham != null) {
            mucTieu = vaCham.boss;
            short[][] daCat = catTaiVaCham(
                    tiaGiuaX,
                    tiaGiuaY,
                    vaCham.chiSoDoan,
                    vaCham.x,
                    vaCham.y
            );
            tiaGiuaX = daCat[0];
            tiaGiuaY = daCat[1];
        }

        short diemCuoiX = tiaGiuaX[tiaGiuaX.length - 1];
        short diemCuoiY = tiaGiuaY[tiaGiuaY.length - 1];
        ChickenCongThucBanUltron.LoatBaTia loat =
                ChickenCongThucBanUltron.taoBaTiaHoiTuTaiDiemCuoi(
                        dauNong[0],
                        dauNong[1],
                        goc,
                        diemCuoiX,
                        diemCuoiY,
                        this.banDo.getWidth(),
                        this.banDo.getHeight()
                );
        short[][] hienThiX = loat.getX();
        short[][] hienThiY = loat.getY();
        hienThiX[1] = tiaGiuaX;
        hienThiY[1] = tiaGiuaY;
        ChickenKetQuaDan loatMayChu =
                com.chicken.chien.ChickenLoatBanUltronServer.tao(
                        shooter, dauNong[0], dauNong[1], goc, luc,
                        this.banDo, this.chienBinhs,
                        (nguoiBan, dich) ->
                                ChickenLuatVaChamPhongBoss.chapNhan(
                                        nguoiBan, dich));
        hienThiX = loatMayChu.cacDuongX;
        hienThiY = loatMayChu.cacDuongY;

        ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(shooter, this.chienBinhs);
        phienMayMan.chuanBiPhongThuTruocPhat(
                loatMayChu.satThuongTheoMucTieu.keySet());
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiLoatLaserUltronDau(
                    shooter.chiSo,
                    shooter.x,
                    shooter.y,
                    goc,
                    luc,
                    hienThiX,
                    hienThiY,
                    phienMayMan.powDaKichHoat()
            );
        }

        for (Map.Entry<ChickenChienBinh, Integer> entry
                : loatMayChu.satThuongTheoMucTieu.entrySet()) {
            if (!this.daKetThuc && !entry.getKey().chet) {
                this.gaySatThuong(
                        entry.getKey(),
                        phienMayMan.apDung(entry.getKey(), entry.getValue()));
            }
        }

        System.out.println("[BOSS HAI TOA THAP][ULTRON_X3] player="
                + shooter.ten + " goc=" + goc
                + " soVien=3 quyDaoDocLap=true");
    }

    private short[][] catTiaTaiVaChamBanDo(short[] xs, short[] ys) {
        if (xs == null || ys == null) {
            return new short[][]{new short[0], new short[0]};
        }
        int soDiem = Math.min(xs.length, ys.length);
        if (soDiem < 2) {
            return new short[][]{
                Arrays.copyOf(xs, soDiem),
                Arrays.copyOf(ys, soDiem)
            };
        }
        for (int i = 1; i < soDiem; i++) {
            int x1 = xs[i - 1];
            int y1 = ys[i - 1];
            int x2 = xs[i];
            int y2 = ys[i];
            int soBuoc = Math.max(
                    1, Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)));
            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                double tiLe = (double) buoc / (double) soBuoc;
                short x = (short) Math.round(x1 + (x2 - x1) * tiLe);
                short y = (short) Math.round(y1 + (y2 - y1) * tiLe);
                if (x >= 0 && y >= 0
                        && x < this.banDo.getWidth()
                        && y < this.banDo.getHeight()
                        && this.banDo.coVaCham(x, y)) {
                    return catTaiVaCham(xs, ys, i, x, y);
                }
            }
        }
        return new short[][]{
            Arrays.copyOf(xs, soDiem),
            Arrays.copyOf(ys, soDiem)
        };
    }

    private VaChamBoss timBossTrung(short[] xs, short[] ys) {
        if (xs == null || ys == null) {
            return null;
        }
        int soDiem = Math.min(xs.length, ys.length);
        for (int i = 1; i < soDiem; i++) {
            int x1 = xs[i - 1];
            int y1 = ys[i - 1];
            int x2 = xs[i];
            int y2 = ys[i];
            int soBuoc = Math.max(1, Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)));
            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                double t = (double) buoc / (double) soBuoc;
                int x = (int) Math.round(x1 + (x2 - x1) * t);
                int y = (int) Math.round(y1 + (y2 - y1) * t);
                for (ChickenChienBinh mucTieu : this.chienBinhs) {
                    if (mucTieu != null && !mucTieu.chet
                            && mucTieu.hp > 0
                            && (mucTieu.bot
                                    ? ChickenKichThuocNhanVat.trungBoss(
                                            x, y, mucTieu.x, mucTieu.y)
                                    : ChickenKichThuocNhanVat.trungNguoiChoi(
                                            x, y, mucTieu.x, mucTieu.y))) {
                        return new VaChamBoss(
                                mucTieu, i, (short) x, (short) y);
                    }
                }
            }
        }
        return null;
    }

    private synchronized void gaySatThuong(ChickenChienBinh mucTieu, int satThuong)
            throws IOException {
        if (mucTieu == null || mucTieu.chet || satThuong <= 0 || this.daKetThuc) {
            return;
        }
        int hpTruoc = Math.max(0, mucTieu.hp);
        mucTieu.hp = Math.max(0, mucTieu.hp - satThuong);
        if (mucTieu.hp == 0) {
            mucTieu.chet = true;
        }
        this.phatCapNhatMau(mucTieu);
        this.ghiNhanPowSauSatThuong(mucTieu, hpTruoc, this.chienBinhs);
        this.xuatHienCamTuDuBiNeuCo(mucTieu);
        this.kiemTraKetThuc();
    }

    private synchronized boolean kiemTraKetThuc() throws IOException {
        if (this.daKetThuc) {
            return true;
        }
        ChickenKetQuaTranBoss.KetQua ketQua =
                ChickenKetQuaTranBoss.danhGia(
                        this.demNguoiChoiSong(), this.demBossSong());
        if (ketQua == ChickenKetQuaTranBoss.KetQua.NGUOI_CHOI_THUA) {
            this.ketThuc(false);
            return true;
        }
        if (ketQua == ChickenKetQuaTranBoss.KetQua.NGUOI_CHOI_THANG) {
            this.henXacNhanNguoiChoiThang();
            return true;
        }
        return false;
    }

    private void henXacNhanNguoiChoiThang() {
        if (this.dangChoXacNhanThang || this.daKetThuc) {
            return;
        }
        this.dangChoXacNhanThang = true;
        this.boHenGio.schedule(() -> {
            synchronized (this) {
                this.dangChoXacNhanThang = false;
                if (this.daKetThuc) {
                    return;
                }
                ChickenKetQuaTranBoss.KetQua ketQua =
                        ChickenKetQuaTranBoss.danhGia(
                                this.demNguoiChoiSong(), this.demBossSong());
                try {
                    if (ketQua == ChickenKetQuaTranBoss.KetQua.NGUOI_CHOI_THUA) {
                        this.ketThuc(false);
                    } else if (ketQua
                            == ChickenKetQuaTranBoss.KetQua.NGUOI_CHOI_THANG) {
                        this.ketThuc(true);
                    }
                } catch (IOException loi) {
                    System.err.println("[BOSS HAI TOA THAP][LOI_KET_THUC] "
                            + loi.getMessage());
                }
            }
        }, ChickenKetQuaTranBoss.TRE_XAC_NHAN_THANG_MS,
                TimeUnit.MILLISECONDS);
    }

    private synchronized void ketThuc(boolean nguoiChoiThang) throws IOException {
        if (this.daKetThuc) {
            return;
        }
        int expHaBoss = ChickenKetQuaTranBoss.layExpHaBoss(
                this.demBossSong());
        this.dangChoXacNhanThang = false;
        this.daKetThuc = true;
        this.huyTacVuHetLuot();
        this.sanh.setTrangThai(SanhChoBoss.TrangThai.DA_KET_THUC);
        for (ThanhVienBoss thanhVien : this.sanh.chupThanhVien()) {
            if (thanhVien == null || thanhVien.getNguoiChoi() == null) {
                continue;
            }
            ChickenKetQuaTranBoss.traoThuongVaGuiKetQua(
                    thanhVien, expHaBoss, nguoiChoiThang);
            this.boDangKyNguoiChoi(thanhVien.getNguoiChoi());
        }
        this.sanh.chuanBiTaiDauSauKetQua();
        this.boHenGio.shutdown();
        System.out.println("[BOSS HAI TOA THAP][KET_THUC] ketQua="
                + (nguoiChoiThang ? "NGUOI_CHOI_THANG" : "BOSS_THANG"));
    }

    /**
     * Chọn lượt theo thời gian nạp đạn.
     * - Vào trận toàn bộ slot có nạp đạn 0 nên vẫn đi theo thứ tự ghế.
     * - Sau hành động, riêng slot đó được cộng thời gian nạp.
     * - Khi không còn ai ở 0, trừ đồng loạt mức nhỏ nhất rồi chọn người sẵn sàng.
     */
    private int timSlotTheoNapDan(int sauSlot) {
        return ChickenHangDoiNapDan.timSlotTiepTheo(
                this.napDan,
                this.thuTuHanhDongNapDan,
                sauSlot,
                this::hopLeChoLuot);
    }

    private boolean hopLeChoLuot(int slot) {
        if (slot < 0 || slot >= SO_SLOT) {
            return false;
        }
        ChickenChienBinh chienBinh = this.chienBinhs[slot];
        return chienBinh != null && !chienBinh.chet && chienBinh.hp > 0
                && (slot >= SO_SLOT_NGUOI_CHOI || chienBinh.coPhien());
    }

    private boolean coTheNhanLenhNguoiChoi(ChickenChienBinh chienBinh) {
        return chienBinh != null && !chienBinh.chet && !this.daKetThuc
                && (chienBinh.chiSo == this.luotHienTai)
                && this.slotDangChoKetThucBan < 0;
    }

    private void choServerKetThucPhatBan(
            ChickenChienBinh shooter,
            int napDanSauBan,
            long thoiGianHoatAnhMs
    ) {
        int slot = shooter == null ? -1 : shooter.chiSo & 0xFF;
        if (slot < 0 || slot >= SO_SLOT || this.daKetThuc
                || (this.luotHienTai & 0xFF) != slot) {
            return;
        }
        this.huyChoKetThucPhatBan();
        long phien = this.maPhienLuot;
        this.slotDangChoKetThucBan = slot;
        this.phienDangChoKetThucBan = phien;
        this.napDanSauPhatDangCho = napDanSauBan;
        long treMs = Math.max(
                ChickenThoiGianHoatAnhDan.TOI_THIEU_MS,
                Math.min(ChickenThoiGianHoatAnhDan.TOI_DA_MS, thoiGianHoatAnhMs));
        this.tacVuChoKetThucBan = this.boHenGio.schedule(
                () -> this.hoanTatChoKetThucPhatBan(slot, phien),
                treMs,
                TimeUnit.MILLISECONDS);
    }

    private void hoanTatChoKetThucPhatBan(int slot, long phien) {
        synchronized (this) {
            if (this.daKetThuc || this.slotDangChoKetThucBan != slot
                    || this.phienDangChoKetThucBan != phien
                    || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot) {
                return;
            }
            int napDanSauBan = this.napDanSauPhatDangCho;
            this.huyChoKetThucPhatBan();
            this.datNapDanSauHanhDong(slot, napDanSauBan);
            try {
                this.chuyenSangLuotTiepTheo(slot);
            } catch (Exception loi) {
                System.err.println("[BOSS HAI TOA THAP][LOI_CHUYEN_LUOT_SAU_DAN] slot="
                        + slot + " phien=" + phien + " loi=" + loi.getMessage());
            }
        }
    }

    private void huyChoKetThucPhatBan() {
        if (this.tacVuChoKetThucBan != null) {
            this.tacVuChoKetThucBan.cancel(false);
            this.tacVuChoKetThucBan = null;
        }
        this.slotDangChoKetThucBan = -1;
        this.phienDangChoKetThucBan = -1L;
        this.napDanSauPhatDangCho = 0;
    }

    private ChickenChienBinh layNguoiChoi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return null;
        }
        for (int i = 0; i < SO_SLOT_NGUOI_CHOI; i++) {
            ChickenChienBinh chienBinh = this.chienBinhs[i];
            if (chienBinh != null && chienBinh.nguoiChoi != null
                    && (chienBinh.nguoiChoi == nguoiChoi
                    || chienBinh.nguoiChoi.ma == nguoiChoi.ma)) {
                return chienBinh;
            }
        }
        return null;
    }

    private ChickenChienBinh[] nguoiChoiConPhien() {
        ChickenChienBinh[] tam = new ChickenChienBinh[SO_SLOT_NGUOI_CHOI];
        int dem = 0;
        for (int i = 0; i < SO_SLOT_NGUOI_CHOI; i++) {
            ChickenChienBinh chienBinh = this.chienBinhs[i];
            if (chienBinh != null && chienBinh.coPhien()) {
                tam[dem++] = chienBinh;
            }
        }
        return Arrays.copyOf(tam, dem);
    }

    private int demNguoiChoiSong() {
        int dem = 0;
        for (int i = 0; i < SO_SLOT_NGUOI_CHOI; i++) {
            ChickenChienBinh chienBinh = this.chienBinhs[i];
            if (chienBinh != null && !chienBinh.chet && chienBinh.hp > 0
                    && chienBinh.coPhien()) {
                dem++;
            }
        }
        return dem;
    }

    private int demBossSong() {
        int dem = 0;
        for (int i = CauHinhBossHaiToaThap.SLOT_BOSS_DAU;
                i <= CauHinhBossHaiToaThap.SLOT_BOSS_CUOI; i++) {
            ChickenChienBinh boss = this.chienBinhs[i];
            if (boss != null && !boss.chet && boss.hp > 0) {
                dem++;
            }
        }
        return dem;
    }

    private void phatLuot(ChickenChienBinh hienTai) throws IOException {
        byte giay = (byte) GIAY_MOI_LUOT;
        this.dongMenuKyNangKhiDoiLuot(this.chienBinhs);
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiGio(
                    this.gioHienTai.getWindX(), this.gioHienTai.getWindY());
            nguoiNhan.nguoiChoi.dichVu.guiLuotBossBaoVayTiep(
                    hienTai.chiSo, hienTai.x, hienTai.y,
                    this.chienBinhs, this.napDan,
                    this.thuTuHanhDongNapDan, giay);
        }
        this.guiLaiBaloChoNguoiBatDauLuot(hienTai);
        int slot = hienTai.chiSo & 0xFF;
        if (slot < SO_SLOT_NGUOI_CHOI) {
            if (hienTai.avenger
                    == ChickenKyNangDacBietIronMan.AVG_IRON_MAN) {
                hienTai.ironManDaDungKyNang = false;
                hienTai.ironManDaGuiMenu = false;
                hienTai.ironManLaserSanSang = false;
            }
            this.kyNangHawkBoss.guiTinHieuChonMucTieuNeuCo(hienTai);
            this.kyNangThorBoss.guiTinHieuKyNangNeuCo(hienTai);
            this.kyNangLokiBoss.guiTinHieuKyNangNeuCo(hienTai);
            this.kyNangUltronBoss.guiTinHieuKyNangNeuCo(hienTai);
            ChickenKyNangDacBietIronMan.guiTinHieuTrongTran(
                    hienTai, this.daKetThuc, this.luotHienTai);
        }
        System.out.println("[BOSS HAI TOA THAP][LUOT] slot=" + slot
                + " ten=" + hienTai.ten
                + " napDan=" + this.napDan[slot]
                + " windX=" + this.gioHienTai.getWindX()
                + " windY=" + this.gioHienTai.getWindY()
                + " loai=" + (slot < 8 ? "NGUOI_CHOI" : "BOSS"));
    }

    private void datNapDanSauHanhDong(int slot, int giaTri) {
        if (slot < 0 || slot >= this.napDan.length) {
            return;
        }
        this.napDan[slot] = Math.max(1, Math.min(65_535, giaTri));
        this.boDemThuTuHanhDongNapDan =
                ChickenHangDoiNapDan.ghiNhanHanhDong(
                        this.thuTuHanhDongNapDan,
                        slot,
                        this.boDemThuTuHanhDongNapDan);
        ChickenChienBinh chienBinh = this.chienBinhs[slot];
        System.out.println("[BOSS HAI TOA THAP][NAP_DAN] slot=" + slot
                + " ten=" + (chienBinh == null ? "null" : chienBinh.ten)
                + " giaTri=" + this.napDan[slot]);
    }

    private int layNapDanSauBanNguoiChoi(ChickenChienBinh chienBinh) {
        return ChickenNapDanServer.layChoChienBinh(chienBinh);
    }

    private int layIdSung(ChickenChienBinh chienBinh) {
        if (chienBinh == null) {
            return -1;
        }
        int slot = chienBinh.chiSo & 0xFF;
        if (slot >= SO_SLOT_NGUOI_CHOI
                && slot < this.idSungBoss.length
                && this.idSungBoss[slot] >= 0) {
            return this.idSungBoss[slot];
        }
        ChickenQuanLyDanSung.DuLieuSung duLieu =
                ChickenQuanLyDanSung.theoPartSung(chienBinh.maVuKhi);
        return duLieu == null ? -1 : duLieu.getIdSung();
    }

    private byte layWindXChoChienBinh(ChickenChienBinh chienBinh) {
        return ChickenHeThongGio.layWindXChoSung(
                this.gioHienTai, this.layIdSung(chienBinh));
    }

    private byte layWindYChoChienBinh(ChickenChienBinh chienBinh) {
        return ChickenHeThongGio.layWindYChoSung(
                this.gioHienTai, this.layIdSung(chienBinh));
    }

    private synchronized void sangLuotSauKyNangAVG() throws IOException {
        if (this.daKetThuc) {
            return;
        }
        int slot = this.luotHienTai & 0xFF;
        if (slot < 0 || slot >= SO_SLOT_NGUOI_CHOI) {
            return;
        }
        ChickenChienBinh nguoiDung = this.chienBinhs[slot];
        if (nguoiDung == null || nguoiDung.chet) {
            this.chuyenSangLuotTiepTheo(slot);
            return;
        }
        this.datNapDanSauHanhDong(
                slot,
                this.layNapDanSauBanNguoiChoi(nguoiDung)
        );
        this.chuyenSangLuotTiepTheo(slot);
    }

    private void phatHoatAnhMuiTenHawkBoss(
            ChickenChienBinh hawk,
            short goc,
            ChickenHoatAnhHawk.DuongDan duongDan
    ) throws IOException {
        if (hawk == null || duongDan == null
                || duongDan.getX() == null || duongDan.getY() == null
                || duongDan.getX().length == 0
                || duongDan.getY().length == 0) {
            return;
        }
        ChickenHoatAnhHawk.LoatDuongDan loat =
                ChickenHoatAnhHawk.taoLoatBonMuiNoiDuoi(duongDan);
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiLoatMuiTenHawkDau(
                    hawk.chiSo,
                    ChickenHoatAnhHawk.LOAI_DAN_MUI_TEN,
                    hawk.x,
                    hawk.y,
                    goc,
                    ChickenHoatAnhHawk.LUC_HIEN_THI,
                    loat.getX(),
                    loat.getY(),
                    hawk.nguoiChoi != null && hawk.nguoiChoi.dangHienHieuUngPow()
            );
        }
    }

    private void phatTiaSetThorBoss(
            ChickenChienBinh thor,
            byte loaiHieuUng,
            short[] cacX,
            short[] cacY
    ) throws IOException {
        if (thor == null || cacX == null || cacY == null) {
            return;
        }
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiTiaSetThor(
                    thor.chiSo, loaiHieuUng, cacX, cacY);
        }
    }

    private void phatBienHinhLokiBoss(
            ChickenChienBinh loki,
            ChickenChienBinh mucTieu
    ) {
        if (loki == null || mucTieu == null) {
            return;
        }
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiBienHinhLoki(
                    loki.chiSo, mucTieu.chiSo);
        }
    }

    private void phatDiChuyenNguoiChoi(ChickenChienBinh moved) throws IOException {
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiDiChuyenDau(moved.chiSo, moved.x, moved.y);
        }
    }

    private void phatDiChuyenBoss(ChickenChienBinh boss) throws IOException {
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiDiChuyenBossBaoVay(
                    boss.chiSo, boss.x, boss.y);
        }
    }

    private void banLaserIronManBoss(
            ChickenChienBinh shooter,
            short goc
    ) throws IOException {
        ChickenTiaLaserIronMan.KetQua tia =
                ChickenTiaLaserIronMan.taoTrongTran(
                        shooter, this.chienBinhs, goc,
                        this.banDo.getWidth(), this.banDo.getHeight());
        ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(shooter, this.chienBinhs);
        int chiSoMucTieuMayMan = tia.getChiSoMucTieu();
        if (chiSoMucTieuMayMan >= 0
                && chiSoMucTieuMayMan < this.chienBinhs.length) {
            phienMayMan.chuanBiPhongThuTruocPhat(
                    this.chienBinhs[chiSoMucTieuMayMan]);
        }
        ChickenTiaLaserIronMan.phatHienThiTrongTran(
                shooter, this.chienBinhs, goc, tia);
        int chiSoMucTieu = tia.getChiSoMucTieu();
        if (chiSoMucTieu >= 0 && chiSoMucTieu < this.chienBinhs.length) {
            ChickenChienBinh mucTieu = this.chienBinhs[chiSoMucTieu];
            if (mucTieu != null && mucTieu != shooter
                    && !mucTieu.chet && mucTieu.hp > 0) {
                this.gaySatThuong(
                        mucTieu,
                        phienMayMan.apDung(
                                mucTieu,
                                ChickenTiaLaserIronMan.tinhSatThuongNhuHawk(
                                        shooter.tanCong, mucTieu.giap))
                );
            }
        }
    }

    private void phatBan(ChickenChienBinh shooter, ChickenKetQuaDan ketQua, byte soPhat)
            throws IOException {
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            try {
                this.guiLaiVatPhamChienTruocPhatBan(
                        shooter, nguoiNhan.nguoiChoi);
                nguoiNhan.nguoiChoi.dichVu.guiKetQuaBanBossBaoVay(
                        shooter.chiSo, shooter.x, shooter.y, ketQua,
                        soPhat <= 0 ? (byte) 1 : soPhat,
                        shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON,
                        shooter.nguoiChoi != null
                                && shooter.nguoiChoi.dangHienHieuUngPow());
            } catch (IOException loi) {
                System.err.println("[BOSS HAI TOA THAP][GUI_PHAT_BAN_LOI] nguoiNhan="
                        + nguoiNhan.ten + " loi=" + loi.getMessage());
            }
        }
    }

    private boolean dongBoHulkSauPhat(ChickenChienBinh shooter, ChickenKetQuaDan ketQua)
            throws IOException {
        if (!com.chicken.avg.ChickenCoCheHulk.apDungViTriCuoi(
                shooter, ketQua, this.banDo.getWidth(), this.banDo.getHeight())) {
            return false;
        }
        int hpTruoc = Math.max(0, shooter.hp);
        shooter.hp = 0;
        shooter.chet = true;
        this.phatCapNhatMau(shooter);
        this.ghiNhanPowSauSatThuong(shooter, hpTruoc, this.chienBinhs);
        return this.kiemTraKetThuc();
    }

    private void phatCapNhatMau(ChickenChienBinh mucTieu) throws IOException {
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiCapNhatMauDau(
                    mucTieu.chiSo, mucTieu.hp, mucTieu.phanTramMau(),
                    mucTieu.chet ? (byte) 2 : (byte) 0);
        }
    }

    private void phaDiaHinhNeuCan(ChickenKetQuaDan ketQua) {
        int soDuong = Math.min(ketQua.cacDuongX.length, ketQua.cacDuongY.length);
        boolean daPhaDiaHinh = false;
        for (int i = 0; i < soDuong; i++) {
            int loaiDanTaoLo = ChickenLoaiDanPhaDiaHinhClient
                    .layLoaiDanTaoLo(ketQua.loaiDan, i);
            if (loaiDanTaoLo == ChickenLoaiDanPhaDiaHinhClient.KHONG_PHA_DIA_HINH) {
                continue;
            }
            short[] xs = ketQua.cacDuongX[i];
            short[] ys = ketQua.cacDuongY[i];
            daPhaDiaHinh |= this.banDo.phaDiaHinhTheoDuongDan(
                    xs, ys, (byte) loaiDanTaoLo);
        }
        if (daPhaDiaHinh) {
            ChickenTrongLucDiaHinhServer.dongBoYSauPhaDiaHinh(
                    this.banDo, this.chienBinhs,
                    ChickenCoCheBayAVG::coTheBay);
        }
    }

    private ChickenQuanLyCongThucSung.KiemTraBanDo kiemTraBanDo() {
        return this.banDo;
    }

    private void huyTacVuHetLuot() {
        if (this.tacVuHetLuot != null) {
            this.tacVuHetLuot.cancel(false);
            this.tacVuHetLuot = null;
        }
    }

    private static short[][] catTaiVaCham(
            short[] xs, short[] ys, int chiSoDoan, short hitX, short hitY
    ) {
        int soDiem = Math.min(xs.length, ys.length);
        int doDai = Math.max(2, Math.min(soDiem, chiSoDoan + 1));
        short[] ketQuaX = Arrays.copyOf(xs, doDai);
        short[] ketQuaY = Arrays.copyOf(ys, doDai);
        ketQuaX[doDai - 1] = hitX;
        ketQuaY[doDai - 1] = hitY;
        return new short[][]{ketQuaX, ketQuaY};
    }

    private static short kepShort(int giaTri, int nhoNhat, int lonNhat) {
        return (short) Math.max(nhoNhat, Math.min(lonNhat, giaTri));
    }

    private static ChickenNguoiChoi[] layNguoiChoiTheoGhe(SanhChoBoss sanh) {
        ChickenNguoiChoi[] ketQua = new ChickenNguoiChoi[SO_SLOT_NGUOI_CHOI];
        if (sanh == null) {
            return ketQua;
        }
        for (ThanhVienBoss thanhVien : sanh.chupThanhVien()) {
            if (thanhVien == null || thanhVien.getNguoiChoi() == null) {
                continue;
            }
            int ghe = thanhVien.getGhe() & 0xFF;
            if (ghe >= 0 && ghe < ketQua.length) {
                ketQua[ghe] = thanhVien.getNguoiChoi();
            }
        }
        return ketQua;
    }

    private static final class VaChamBoss {
        private final ChickenChienBinh boss;
        private final int chiSoDoan;
        private final short x;
        private final short y;

        private VaChamBoss(ChickenChienBinh boss, int chiSoDoan, short x, short y) {
            this.boss = boss;
            this.chiSoDoan = chiSoDoan;
            this.x = x;
            this.y = y;
        }
    }
}
