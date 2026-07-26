package com.chicken.phong.boss.trandau.baovay;

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
import com.chicken.avg.ChickenThanhDiChuyenAVG;
import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenDiChuyenServer;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.chien.ChickenPhatBanServer;
import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.chien.ChickenQuanLyCongThucSung;
import com.chicken.chien.ChickenQuanLyDanSung;
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
 * Trận riêng của map 50 - Boss Bao vây.
 * File này chỉ điều phối người chơi, lượt và kết quả trận. Công thức bắn nằm
 * trong BossBanSung; tìm mục tiêu/di chuyển/nổ Cảm tử nằm trong BossCamTu.
 */
public final class BossBaoVay extends ChickenQuanLyChien {
    private static final int SO_SLOT = 13;
    private static final int SO_SLOT_NGUOI_CHOI = 8;
    /** Người chơi và boss đều có tối đa 25 giây cho một lượt. */
    private static final int GIAY_MOI_LUOT = 25;
    private static final int TRE_BOSS_BAT_DAU_MS = 550;
    private static final byte PHE_NGUOI_CHOI_THANG =
            ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THANG;
    private static final byte PHE_BOSS_THANG =
            ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THUA;

    private final SanhChoBoss sanh;
    private final ChickenQuanLyBanDo banDo;
    private final ChickenChienBinh[] chienBinhs = new ChickenChienBinh[SO_SLOT];
    private final CauHinhBossBaoVay.CauHinh[] cauHinhBoss =
            new CauHinhBossBaoVay.CauHinh[SO_SLOT];
    /** Thời gian nạp hiện tại của từng ghế/slot; lúc vào trận đều bằng 0. */
    private final int[] napDan = new int[SO_SLOT];
    private final ScheduledExecutorService boHenGio;
    /** Mỗi lượt tạo một trạng thái gió mới giống cơ chế luyện tập. */
    private ChickenHeThongGio.TrangThaiGio gioHienTai =
            ChickenHeThongGio.khongGio();

    /** Bộ kỹ năng AVG riêng dùng đúng mảng 13 chiến binh của trận boss. */
    private ChickenKyNangDacBietHawk kyNangHawkBoss;
    private ChickenKyNangDacBietThor kyNangThorBoss;
    private ChickenKyNangDacBietLoki kyNangLokiBoss;
    private ChickenKyNangDacBietUltron kyNangUltronBoss;

    private byte luotHienTai = -1;
    private boolean daBatDau;
    private boolean daKetThuc;
    private boolean dangChoXacNhanThang;
    private long maPhienLuot;
    private ScheduledFuture<?> tacVuHetLuot;

    public BossBaoVay(SanhChoBoss sanh) {
        super(null, layNguoiChoiTheoGhe(sanh),
                (byte) CauHinhBossBaoVay.MAP_ID, false);
        this.sanh = sanh;
        this.banDo = new ChickenQuanLyBanDo(CauHinhBossBaoVay.MAP_ID);
        this.boHenGio = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "boss-bao-vay-P4-"
                    + (sanh == null ? -1 : sanh.getMaBan() & 0xFF));
            thread.setDaemon(true);
            return thread;
        });
        this.taoNguoiChoi(sanh);
        this.taoBoss();
        this.khoiTaoKyNangAVG();
        this.dangKyNguoiChoiTrongTran();
    }

    public static BossBaoVay tao(SanhChoBoss sanh) {
        if (sanh == null || (sanh.getMaBanDo() & 0xFF) != CauHinhBossBaoVay.MAP_ID) {
            return null;
        }
        return new BossBaoVay(sanh);
    }

    @Override
    public synchronized void batDau() throws IOException {
        if (this.daBatDau || this.daKetThuc) {
            return;
        }
        this.daBatDau = true;
        for (CauHinhBossBaoVay.CauHinh cauHinh : CauHinhBossBaoVay.layTatCa()) {
            System.out.println("[BOSS BAO VAY][TAO_BOSS] slot="
                    + (cauHinh.getSlot() & 0xFF)
                    + " ten=" + cauHinh.getTen()
                    + " x=" + cauHinh.getX()
                    + " y=" + cauHinh.getY()
                    + " hp=" + CauHinhBossBaoVay.MAU_BOSS
                    + " loai=" + cauHinh.getLoai()
                    + " sungPart=" + cauHinh.getVuKhi());
        }
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiBatDauDau(
                    this.banDo.layMaBanDo(), this.chienBinhs, this.banDo.layMaNen());
            for (CauHinhBossBaoVay.CauHinh cauHinh : CauHinhBossBaoVay.layTatCa()) {
                nguoiNhan.nguoiChoi.dichVu.guiTaoBossBaoVay(
                        cauHinh.getSlot(),
                        cauHinh.getId(),
                        cauHinh.getTen(),
                        cauHinh.getHead(),
                        cauHinh.getLeg(),
                        cauHinh.getBody(),
                        cauHinh.getHat(),
                        cauHinh.getWing(),
                        cauHinh.getVuKhi(),
                        cauHinh.getX(),
                        cauHinh.getY(),
                        CauHinhBossBaoVay.MAU_BOSS
                );
            }
            nguoiNhan.nguoiChoi.dichVu.guiHienManHinhGameLuyenTap();
        }
        System.out.println("[BOSS BAO VAY][BAT_DAU] P4-"
                + (this.sanh.getMaBan() & 0xFF)
                + " map=50 players=" + this.demNguoiChoiSong()
                + " bosses=5");
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
        ChickenYeuCauBanServer.KetQua yeuCau =
                ChickenYeuCauBanServer.doc(ms, duLieuSung, shooter.avenger);
        if (yeuCau == null) {
            return;
        }
        byte loaiDan = yeuCau.getLoaiDan();
        short goc = yeuCau.getGoc();
        byte luc = yeuCau.getLuc();
        byte lucPhu = yeuCau.getLucPhu();

        if (shooter.avenger == ChickenKyNangDacBietIronMan.AVG_IRON_MAN
                && shooter.ironManLaserSanSang) {
            this.banLaserIronManBoss(shooter, goc);
        } else if (this.kyNangUltronBoss.dangBanX3(shooter)) {
            this.banX3UltronBoss(shooter, goc, luc);
        } else {
            ChickenKetQuaDan ketQua = this.taoPhatBanNguoiChoi(
                    shooter, loaiDan, goc, luc, lucPhu);
            this.phatBan(shooter, ketQua, (byte) 1);
            if (this.dongBoHulkSauPhat(shooter, ketQua)) {
                return;
            }
            this.phaDiaHinhNeuCan(ketQua);
            for (Map.Entry<ChickenChienBinh, Integer> entry
                    : ketQua.satThuongTheoMucTieu.entrySet()) {
                if (!this.daKetThuc
                        && entry.getKey() != null
                        && !entry.getKey().chet
                        && entry.getValue() > 0) {
                    this.gaySatThuong(entry.getKey(), entry.getValue());
                }
            }
        }

        // Dù chọn Bắn x3 hay bắn thường, lượt này đã kết thúc nên phải xóa
        // cờ menu để lượt sau Ultron có thể mở lại đúng trạng thái.
        this.kyNangUltronBoss.sauKhiDaBan(shooter);
        ChickenKyNangDacBietIronMan.xoaTrangThaiChoBan(shooter);
        this.kyNangHawkBoss.sauKhiBanThuong(shooter);
        if (!this.daKetThuc) {
            this.datNapDanSauHanhDong(
                    shooter.chiSo & 0xFF,
                    this.layNapDanSauBanNguoiChoi(shooter));
            this.chuyenSangLuotTiepTheo(shooter.chiSo & 0xFF);
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
        this.datNapDanSauHanhDong(
                chienBinh.chiSo & 0xFF,
                this.layNapDanSauBanNguoiChoi(chienBinh));
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
        System.out.println("[BOSS BAO VAY][NGUOI_CHOI_ROI] player="
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
        for (ChickenChienBinh chienBinh : this.nguoiChoiConPhien()) {
            this.boDangKyNguoiChoi(chienBinh.nguoiChoi);
        }
        this.boHenGio.shutdownNow();
    }

    public synchronized ChickenChienBinh[] chupChienBinh() {
        return this.chienBinhs.clone();
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
    }

    private void taoBoss() {
        for (CauHinhBossBaoVay.CauHinh cauHinh : CauHinhBossBaoVay.layTatCa()) {
            int slot = cauHinh.getSlot() & 0xFF;
            int tanCong = cauHinh.laBossBanSung()
                    ? CauHinhBossBaoVay.layTanCongTheoSung(cauHinh.getVuKhi())
                    : 0;
            this.chienBinhs[slot] = new ChickenChienBinh(
                    cauHinh.getSlot(), cauHinh.getId(), cauHinh.getX(), cauHinh.getY(),
                    cauHinh.getTen(), cauHinh.getVuKhi(),
                    CauHinhBossBaoVay.MAU_BOSS, tanCong, 0);
            this.cauHinhBoss[slot] = cauHinh;
        }
    }

    private void khoiTaoKyNangAVG() {
        this.kyNangHawkBoss = new ChickenKyNangDacBietHawk(
                this.chienBinhs,
                this.banDo,
                new ChickenKyNangDacBietHawk.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossBaoVay.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossBaoVay.this.luotHienTai;
                    }

                    @Override
                    public void guiHoatAnhMuiTen(
                            ChickenChienBinh hawk,
                            short goc,
                            ChickenHoatAnhHawk.DuongDan duongDan
                    ) throws IOException {
                        BossBaoVay.this.phatHoatAnhMuiTenHawkBoss(
                                hawk, goc, duongDan);
                    }

                    @Override
                    public void gaySatThuong(
                            ChickenChienBinh mucTieu,
                            int satThuong
                    ) throws IOException {
                        BossBaoVay.this.gaySatThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        BossBaoVay.this.sangLuotSauKyNangAVG();
                    }
                }
        );

        this.kyNangThorBoss = new ChickenKyNangDacBietThor(
                this.chienBinhs,
                this.banDo,
                new ChickenKyNangDacBietThor.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossBaoVay.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossBaoVay.this.luotHienTai;
                    }

                    @Override
                    public void guiTiaSet(
                            ChickenChienBinh thor,
                            byte loaiHieuUng,
                            short[] cacX,
                            short[] cacY
                    ) throws IOException {
                        BossBaoVay.this.phatTiaSetThorBoss(
                                thor, loaiHieuUng, cacX, cacY);
                    }

                    @Override
                    public void gaySatThuong(
                            ChickenChienBinh mucTieu,
                            int satThuong
                    ) throws IOException {
                        BossBaoVay.this.gaySatThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        BossBaoVay.this.sangLuotSauKyNangAVG();
                    }
                }
        );

        this.kyNangLokiBoss = new ChickenKyNangDacBietLoki(
                this.chienBinhs,
                new ChickenKyNangDacBietLoki.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossBaoVay.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossBaoVay.this.luotHienTai;
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
                        BossBaoVay.this.phatBienHinhLokiBoss(loki, mucTieu);
                    }

                    @Override
                    public void capNhatMau(ChickenChienBinh loki)
                            throws IOException {
                        BossBaoVay.this.phatCapNhatMau(loki);
                    }
                }
        );

        this.kyNangUltronBoss = new ChickenKyNangDacBietUltron(
                new ChickenKyNangDacBietUltron.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossBaoVay.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossBaoVay.this.luotHienTai;
                    }

                    @Override
                    public void guiMenuUltron(ChickenChienBinh ultron) {
                        ultron.nguoiChoi.dichVu.guiChonKyNangUltron();
                    }
                }
        );
    }

    private synchronized void chuyenSangLuotTiepTheo(int sauSlot) throws IOException {
        if (this.daKetThuc || this.kiemTraKetThuc()) {
            return;
        }
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
            hienTai.quangDuongDiChuyenConLai =
                    ChickenThanhDiChuyenAVG.hoiDay(hienTai.theLucDiChuyenToiDa);
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
            this.boHenGio.schedule(() -> this.thucHienLuotBoss(slotTiep, phien),
                    TRE_BOSS_BAT_DAU_MS, TimeUnit.MILLISECONDS);
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
            System.out.println("[BOSS BAO VAY][HET_25_GIAY] slot=" + slot
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
            return this.layNapDanSauBanNguoiChoi(chienBinh);
        }
        CauHinhBossBaoVay.CauHinh cauHinh = slot >= 0 && slot < this.cauHinhBoss.length
                ? this.cauHinhBoss[slot] : null;
        if (cauHinh != null && cauHinh.laCamTu()) {
            return BossCamTu.NAP_DAN_SAU_DI_CHUYEN;
        }
        return chienBinh == null
                ? 100
                : CauHinhBossBaoVay.layNapDanTheoSung(chienBinh.maVuKhi);
    }

    private void thucHienLuotBoss(int slot, long phien) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot) {
                return;
            }
            ChickenChienBinh boss = this.chienBinhs[slot];
            CauHinhBossBaoVay.CauHinh cauHinh = this.cauHinhBoss[slot];
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
                        BossCamTu.QUANG_DUONG_MOI_LUOT,
                        slotMucTieu,
                        huongXKhoa);
            }
        }
    }

    private void thucHienBossBanSung(ChickenChienBinh boss, int slot, long phien) {
        ChickenChienBinh mucTieu = BossBanSung.chonNgauNhienNguoiSong(this.chienBinhs);
        if (mucTieu == null) {
            this.sangLuotSauBoss(slot, phien,
                    CauHinhBossBaoVay.layNapDanTheoSung(boss.maVuKhi));
            return;
        }
        int soVien = BossBanSung.laySoVien(boss);
        int treVien = BossBanSung.layKhoangCachVienMs(boss);
        System.out.println("[BOSS BAO VAY][BOSS_BAN] boss=" + boss.ten
                + " target=" + mucTieu.ten + " soVien=" + soVien
                + " sungPart=" + boss.maVuKhi + " damageMoiVien="
                + Math.max(1, boss.tanCong - mucTieu.giap));
        this.banVienBoss(boss, mucTieu, slot, phien, 0, soVien, treVien);
    }

    private void banVienBoss(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int slot,
            long phien,
            int vien,
            int soVien,
            int treVien
    ) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien
                    || boss.chet || mucTieu.chet) {
                this.sangLuotSauBoss(slot, phien,
                        CauHinhBossBaoVay.layNapDanTheoSung(boss.maVuKhi));
                return;
            }
            ChickenKetQuaDan ketQua = BossBanSung.taoPhatBan(
                    boss, mucTieu, this.banDo,
                    this.layWindXChoChienBinh(boss),
                    this.layWindYChoChienBinh(boss));
            try {
                this.phatBan(boss, ketQua, (byte) 1);
                this.phaDiaHinhNeuCan(ketQua);
                if (ketQua.mucTieu != null && ketQua.satThuong > 0) {
                    this.gaySatThuong(ketQua.mucTieu, ketQua.satThuong);
                }
            } catch (IOException ignored) {
            }
            if (this.daKetThuc) {
                return;
            }
            if (vien + 1 >= soVien || mucTieu.chet) {
                this.boHenGio.schedule(() -> this.sangLuotSauBoss(
                                slot, phien,
                                CauHinhBossBaoVay.layNapDanTheoSung(boss.maVuKhi)),
                        180, TimeUnit.MILLISECONDS);
                return;
            }
            this.boHenGio.schedule(() -> this.banVienBoss(
                    boss, mucTieu, slot, phien, vien + 1, soVien, treVien),
                    treVien, TimeUnit.MILLISECONDS);
        }
    }

    private void thucHienCamTu(
            ChickenChienBinh camTu,
            int slot,
            long phien,
            int quangDuongConLai,
            int slotMucTieu,
            int huongXKhoa
    ) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien || camTu.chet) {
                this.sangLuotSauBoss(slot, phien, 0);
                return;
            }
            ChickenChienBinh mucTieu = this.layNguoiChoiSongTheoSlot(slotMucTieu);
            if (mucTieu == null) {
                mucTieu = BossCamTu.timNguoiSongGanNhat(camTu, this.chienBinhs);
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
            if (quangDuongConLai <= 0) {
                this.sangLuotSauBoss(slot, phien, BossCamTu.NAP_DAN_SAU_DI_CHUYEN);
                return;
            }
            short[] buoc = BossCamTu.tinhBuocTiepTheo(
                    camTu, mucTieu, quangDuongConLai,
                    huongXKhoa, this.banDo);
            int daDi = (int) Math.round(Math.hypot(buoc[0] - camTu.x, buoc[1] - camTu.y));
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
            if (BossCamTu.daRoiKhoiMap(camTu, this.banDo)) {
                this.xuLyCamTuRoiKhoiMap(camTu, slot, phien);
                return;
            }
            if (BossCamTu.daChamNguoiChoi(camTu, mucTieu)) {
                this.noCamTu(camTu, mucTieu, slot, phien);
                return;
            }
            int conLai = Math.max(0, quangDuongConLai - Math.max(1, daDi));
            final int slotMucTieuTiep = slotMucTieu;
            final int huongXKhoaTiep = huongXKhoa;
            this.boHenGio.schedule(() -> this.thucHienCamTu(
                    camTu, slot, phien, conLai,
                    slotMucTieuTiep, huongXKhoaTiep),
                    BossCamTu.TRE_MOI_BUOC_MS, TimeUnit.MILLISECONDS);
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

    private void xuLyCamTuRoiKhoiMap(
            ChickenChienBinh camTu,
            int slot,
            long phien
    ) {
        try {
            camTu.hp = 0;
            camTu.chet = true;
            this.phatCapNhatMau(camTu);
            System.out.println("[BOSS BAO VAY][CAM_TU_ROI_MAP] boss="
                    + camTu.ten + " x=" + camTu.x + " y=" + camTu.y
                    + " apDungTrongLuc=true");
        } catch (IOException ignored) {
        }
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
        try {
            short[] xs = new short[]{camTu.x, (short) (camTu.x + 1)};
            short[] ys = new short[]{camTu.y, camTu.y};
            ChickenKetQuaDan hieuUngNo = new ChickenKetQuaDan(
                    (byte) 15, camTu.x, camTu.y, (short) 0, (byte) 1,
                    xs, ys, mucTieu, CauHinhBossBaoVay.SAT_THUONG_CAM_TU);
            this.phatBan(camTu, hieuUngNo, (byte) 1);
            this.banDo.phaDiaHinh(camTu.x, camTu.y, (byte) 15);
            this.gaySatThuong(mucTieu, CauHinhBossBaoVay.SAT_THUONG_CAM_TU);
            camTu.hp = 0;
            camTu.chet = true;
            this.phatCapNhatMau(camTu);
            System.out.println("[BOSS BAO VAY][CAM_TU_NO] boss=" + camTu.ten
                    + " target=" + mucTieu.ten + " damage="
                    + CauHinhBossBaoVay.SAT_THUONG_CAM_TU
                    + " bossTuChet=true");
        } catch (IOException ignored) {
        }
        if (!this.daKetThuc) {
            this.boHenGio.schedule(() -> this.sangLuotSauBoss(slot, phien, 0),
                    250, TimeUnit.MILLISECONDS);
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
            ChickenChienBinh shooter,
            byte loaiDan,
            short goc,
            byte luc,
            byte lucPhu
    ) {
        if (shooter != null
                && shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON) {
            return this.taoPhatBanUltronThuong(shooter, loaiDan, goc, luc);
        }
        ChickenQuanLyCongThucSung.KiemTraBanDo kiemTra = this.kiemTraBanDo();
        short[] dauNong = ChickenToaDoDauNong.layChoNguoiChoi(
                shooter.x, shooter.y, goc, kiemTra);
        ChickenQuanLyDanSung.DuLieuSung sung =
                ChickenQuanLyDanSung.theoPartSung(shooter.maVuKhi);
        return ChickenPhatBanServer.tao(
                shooter,
                dauNong[0],
                dauNong[1],
                goc,
                luc,
                lucPhu,
                sung,
                this.layWindXChoChienBinh(shooter),
                this.layWindYChoChienBinh(shooter),
                kiemTra,
                this.chienBinhs,
                new ChickenPhatBanServer.BoLocMucTieu() {
                    @Override
                    public boolean chapNhan(
                            ChickenChienBinh nguoiBan,
                            ChickenChienBinh mucTieu
                    ) {
                        return !nguoiBan.bot || !mucTieu.bot;
                    }
                }
        );
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
                        (nguoiBan, dich) -> dich.bot);
        hienThiX = loatMayChu.cacDuongX;
        hienThiY = loatMayChu.cacDuongY;

        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiLoatLaserUltronDau(
                    shooter.chiSo,
                    shooter.x,
                    shooter.y,
                    goc,
                    luc,
                    hienThiX,
                    hienThiY
            );
        }

        for (Map.Entry<ChickenChienBinh, Integer> entry
                : loatMayChu.satThuongTheoMucTieu.entrySet()) {
            if (!this.daKetThuc && !entry.getKey().chet) {
                this.gaySatThuong(entry.getKey(), entry.getValue());
            }
        }

        System.out.println("[BOSS BAO VAY][ULTRON_X3] player="
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
                for (int slot = CauHinhBossBaoVay.SLOT_BOSS_DAU;
                        slot <= CauHinhBossBaoVay.SLOT_BOSS_CUOI; slot++) {
                    ChickenChienBinh boss = this.chienBinhs[slot];
                    if (boss != null && !boss.chet
                            && ChickenKichThuocNhanVat.trungBoss(x, y, boss.x, boss.y)) {
                        return new VaChamBoss(boss, i, (short) x, (short) y);
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
        mucTieu.hp = Math.max(0, mucTieu.hp - satThuong);
        if (mucTieu.hp == 0) {
            mucTieu.chet = true;
        }
        this.phatCapNhatMau(mucTieu);
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
                    System.err.println("[BOSS BAO VAY][LOI_KET_THUC] "
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
        System.out.println("[BOSS BAO VAY][KET_THUC] ketQua="
                + (nguoiChoiThang ? "NGUOI_CHOI_THANG" : "BOSS_THANG"));
    }

    /**
     * Chọn lượt theo thời gian nạp đạn.
     * - Vào trận toàn bộ slot có nạp đạn 0 nên vẫn đi theo thứ tự ghế.
     * - Sau hành động, riêng slot đó được cộng thời gian nạp.
     * - Khi không còn ai ở 0, trừ đồng loạt mức nhỏ nhất rồi chọn người sẵn sàng.
     */
    private int timSlotTheoNapDan(int sauSlot) {
        int sanSang = this.timSlotCoNapDanBangKhong(sauSlot);
        if (sanSang >= 0) {
            return sanSang;
        }

        int nhoNhat = Integer.MAX_VALUE;
        for (int slot = 0; slot < SO_SLOT; slot++) {
            if (this.hopLeChoLuot(slot) && this.napDan[slot] > 0) {
                nhoNhat = Math.min(nhoNhat, this.napDan[slot]);
            }
        }
        if (nhoNhat == Integer.MAX_VALUE) {
            return -1;
        }
        for (int slot = 0; slot < SO_SLOT; slot++) {
            if (this.hopLeChoLuot(slot)) {
                this.napDan[slot] = Math.max(0, this.napDan[slot] - nhoNhat);
            }
        }
        return this.timSlotCoNapDanBangKhong(sauSlot);
    }

    private int timSlotCoNapDanBangKhong(int sauSlot) {
        int batDau = sauSlot < 0 ? 0 : (sauSlot + 1) % SO_SLOT;
        for (int buoc = 0; buoc < SO_SLOT; buoc++) {
            int slot = (batDau + buoc) % SO_SLOT;
            if (this.hopLeChoLuot(slot) && this.napDan[slot] <= 0) {
                return slot;
            }
        }
        return -1;
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
                && (chienBinh.chiSo == this.luotHienTai);
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
        for (int i = CauHinhBossBaoVay.SLOT_BOSS_DAU;
                i <= CauHinhBossBaoVay.SLOT_BOSS_CUOI; i++) {
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
                    this.chienBinhs, this.napDan, giay);
        }
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
        System.out.println("[BOSS BAO VAY][LUOT] slot=" + slot
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
        ChickenChienBinh chienBinh = this.chienBinhs[slot];
        System.out.println("[BOSS BAO VAY][NAP_DAN] slot=" + slot
                + " ten=" + (chienBinh == null ? "null" : chienBinh.ten)
                + " giaTri=" + this.napDan[slot]);
    }

    private int layNapDanSauBanNguoiChoi(ChickenChienBinh chienBinh) {
        if (chienBinh == null || chienBinh.nguoiChoi == null) {
            return 100;
        }
        ChickenNguoiChoi nguoiChoi = chienBinh.nguoiChoi;
        ChickenVatPham sung = nguoiChoi.itemBody != null
                && nguoiChoi.itemBody.length > 5
                ? nguoiChoi.itemBody[5] : null;
        int napDanGoc = -1;
        if (sung != null) {
            // Giống luyện tập: nếu item có option riêng thì dùng đúng danh sách
            // đó; chỉ dùng option template khi item không có option riêng.
            if (sung.itemOptions != null && !sung.itemOptions.isEmpty()) {
                napDanGoc = sung.getParamById(14);
            } else if (sung.mau != null && sung.mau.thuocTinhs != null) {
                for (Object doiTuong : sung.mau.thuocTinhs) {
                    if (!(doiTuong instanceof ChickenThuocTinhVatPham)) {
                        continue;
                    }
                    ChickenThuocTinhVatPham option =
                            (ChickenThuocTinhVatPham) doiTuong;
                    if (option.optionTemplate != null
                            && option.optionTemplate.ma == 14
                            && option.thamSo > 0) {
                        napDanGoc = option.thamSo;
                        break;
                    }
                }
            }
        }
        if (napDanGoc <= 0) {
            napDanGoc = 100;
        }
        int giamNapDan = ChickenChiSoNguoiChoi.tinhGiamNapDanTuTiemNang(nguoiChoi);
        return Math.max(1, napDanGoc - giamNapDan);
    }

    private int layIdSung(ChickenChienBinh chienBinh) {
        if (chienBinh == null) {
            return -1;
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
                    loat.getY()
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
        ChickenTiaLaserIronMan.phatHienThiTrongTran(
                shooter, this.chienBinhs, goc, tia);
        int chiSoMucTieu = tia.getChiSoMucTieu();
        if (chiSoMucTieu >= 0 && chiSoMucTieu < this.chienBinhs.length) {
            ChickenChienBinh mucTieu = this.chienBinhs[chiSoMucTieu];
            if (mucTieu != null && mucTieu != shooter
                    && !mucTieu.chet && mucTieu.hp > 0) {
                this.gaySatThuong(
                        mucTieu,
                        ChickenTiaLaserIronMan.tinhSatThuongNhuHawk(
                                shooter.tanCong, mucTieu.giap)
                );
            }
        }
    }

    private void phatBan(ChickenChienBinh shooter, ChickenKetQuaDan ketQua, byte soPhat)
            throws IOException {
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiKetQuaBanBossBaoVay(
                    shooter.chiSo,
                    shooter.x,
                    shooter.y,
                    ketQua,
                    soPhat <= 0 ? (byte) 1 : soPhat);
        }
    }

    private boolean dongBoHulkSauPhat(ChickenChienBinh shooter, ChickenKetQuaDan ketQua)
            throws IOException {
        if (!com.chicken.avg.ChickenCoCheHulk.apDungViTriCuoi(
                shooter, ketQua, this.banDo.getWidth(), this.banDo.getHeight())) {
            return false;
        }
        shooter.hp = 0;
        shooter.chet = true;
        this.phatCapNhatMau(shooter);
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
        for (int i = 0; i < soDuong; i++) {
            short[] xs = ketQua.cacDuongX[i];
            short[] ys = ketQua.cacDuongY[i];
            int soDiem = Math.min(xs == null ? 0 : xs.length, ys == null ? 0 : ys.length);
            if (soDiem <= 0) {
                continue;
            }
            short x = xs[soDiem - 1];
            short y = ys[soDiem - 1];
            if (x >= 0 && y >= 0
                    && x < this.banDo.getWidth()
                    && y < this.banDo.getHeight()
                    && this.banDo.coVaCham(x, y)) {
                this.banDo.phaDiaHinh(x, y, ketQua.loaiDan);
            }
        }
    }

    private ChickenQuanLyCongThucSung.KiemTraBanDo kiemTraBanDo() {
        return new ChickenQuanLyCongThucSung.KiemTraBanDo() {
            @Override
            public int getWidth() { return BossBaoVay.this.banDo.getWidth(); }
            @Override
            public int getHeight() { return BossBaoVay.this.banDo.getHeight(); }
            @Override
            public boolean coVaCham(short x, short y) {
                return BossBaoVay.this.banDo.coVaCham(x, y);
            }
        };
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
