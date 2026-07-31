package com.chicken.phong.boss.trandau.rong;

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
import com.chicken.chien.ChickenMayMan;
import com.chicken.chien.ChickenNguCanhLaySung;
import com.chicken.chien.ChickenNapDanServer;
import com.chicken.chien.ChickenLoatBanUltronServer;
import com.chicken.chien.ChickenPhatBanServer;
import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.chien.ChickenQuanLyCongThucSung;
import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.chien.ChickenThoiGianHoatAnhDan;
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
import com.chicken.phong.boss.trandau.ChickenLuatVaChamPhongBoss;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Trận riêng của map 55 - Boss Rồng.
 * Boss Rồng dùng BigBoss native type=2, bay và luân phiên bắn xa hoặc kẹp/thả người chơi.
 */
public final class BossRong extends ChickenQuanLyChien {
    private static final int SO_SLOT = 9;
    private static final int SO_SLOT_NGUOI_CHOI = 8;
    private static final byte PHE_NGUOI_CHOI_THANG =
            ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THANG;
    private static final byte PHE_BOSS_THANG =
            ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THUA;

    private final SanhChoBoss sanh;
    private final ChickenQuanLyBanDo banDo;
    private final ChickenChienBinh[] chienBinhs = new ChickenChienBinh[SO_SLOT];
    private final CauHinhBossRong.CauHinh[] cauHinhBoss =
            new CauHinhBossRong.CauHinh[SO_SLOT];
    /** Thời gian nạp hiện tại của từng ghế/slot; lúc vào trận đều bằng 0. */
    private final int[] napDan = new int[SO_SLOT];
    private final long[] thuTuHanhDongNapDan = new long[SO_SLOT];
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
    private long boDemThuTuHanhDongNapDan;
    private ScheduledFuture<?> tacVuHetLuot;
    private ScheduledFuture<?> tacVuChoKetThucBan;
    private int slotDangChoKetThucBan = -1;
    private long phienDangChoKetThucBan = -1L;
    private int napDanSauPhatDangCho;
    private long thoiDiemSomNhatXacNhanKetThucBan;
    private final Set<Integer> nguoiChoiDaXacNhanHoatAnh = new HashSet<>();

    public BossRong(SanhChoBoss sanh) {
        super(null, layNguoiChoiTheoGhe(sanh),
                (byte) CauHinhBossRong.MAP_ID, false);
        this.sanh = sanh;
        this.banDo = new ChickenQuanLyBanDo(CauHinhBossRong.MAP_ID);
        this.boHenGio = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "boss-rong-P4-"
                    + (sanh == null ? -1 : sanh.getMaBan() & 0xFF));
            thread.setDaemon(true);
            return thread;
        });
        this.taoNguoiChoi(sanh);
        this.taoBoss();
        this.khoiTaoKyNangAVG();
        this.dangKyNguoiChoiTrongTran();
    }

    public static BossRong tao(SanhChoBoss sanh) {
        if (sanh == null || (sanh.getMaBanDo() & 0xFF) != CauHinhBossRong.MAP_ID) {
            return null;
        }
        return new BossRong(sanh);
    }

    @Override
    public synchronized void batDau() throws IOException {
        if (this.daBatDau || this.daKetThuc) {
            return;
        }
        this.daBatDau = true;
        for (CauHinhBossRong.CauHinh cauHinh : CauHinhBossRong.layTatCa()) {
            System.out.println("[BOSS RONG][TAO_BOSS] slot="
                    + (cauHinh.getSlot() & 0xFF)
                    + " ten=" + cauHinh.getTen()
                    + " x=" + cauHinh.getX()
                    + " y=" + cauHinh.getY()
                    + " hp=" + CauHinhBossRong.MAU_BOSS
                    + " loai=" + cauHinh.getLoai()
                    + " sungPart=" + cauHinh.getVuKhi());
        }
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiBatDauDau(
                    this.banDo.layMaBanDo(), this.chienBinhs, this.banDo.layMaNen());
            for (CauHinhBossRong.CauHinh cauHinh : CauHinhBossRong.layTatCa()) {
                nguoiNhan.nguoiChoi.dichVu.guiTaoBossRong(
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
                        CauHinhBossRong.MAU_BOSS
                );
            }
        }
        System.out.println("[BOSS RONG][BAT_DAU] P4-"
                + (this.sanh.getMaBan() & 0xFF)
                + " map=55 players=" + this.demNguoiChoiSong()
                + " bosses=1");
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
                this.luotHienTai, this.daKetThuc, chiSoTui);
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

        try (ChickenNguCanhLaySung.Phien ignored =
                ChickenNguCanhLaySung.batDauPhatBanNguoiChoi()) {
        if (shooter.avenger == ChickenKyNangDacBietIronMan.AVG_IRON_MAN
                && shooter.ironManLaserSanSang) {
            this.banLaserIronManBoss(shooter, goc);
        } else if (this.kyNangUltronBoss.dangBanX3(shooter)) {
            ChickenKetQuaDan loatUltron =
                    this.banX3UltronBoss(shooter, goc, luc);
            thoiGianHoatAnhMs = ChickenThoiGianHoatAnhDan.tinh(loatUltron);
        } else {
            ChickenKetQuaDan ketQua = this.taoPhatBanNguoiChoi(
                    shooter, loaiDan, goc, luc, lucPhu);
            if (vatPhamDangCho != null) {
                if (ketQua == null
                        || !shooter.nguoiChoi.tieuThuMotVatPhamChien(
                                vatPhamDangCho)) {
                    return;
                }
                shooter.xoaVatPhamChienDangCho();
            }
            ChickenMayMan.PhienTanCong phienMayMan =
                    ChickenMayMan.batDau(shooter, this.chienBinhs);
            phienMayMan.chuanBiPhongThuTruocPhat(
                    ketQua.satThuongTheoMucTieu.keySet());
            this.phatBan(shooter, ketQua, (byte) 1);
            thoiGianHoatAnhMs = ChickenThoiGianHoatAnhDan.tinh(ketQua);
            if (this.dongBoHulkSauPhat(shooter, ketQua)) {
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
                    this.layNapDanSauBanNguoiChoi(shooter),
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
                ChickenNapDanServer.TOI_THIEU);
        this.chuyenSangLuotTiepTheo(chienBinh.chiSo & 0xFF);
    }

    @Override
    public synchronized void kiemTraVaCham(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms)
            throws IOException {
        int soByteConLai = ms.boDoc().available();
        ChickenChienBinh chienBinh = this.layNguoiChoi(nguoiChoi);
        int slotDangCho = this.slotDangChoKetThucBan;
        boolean tinKetThucHopLe = chienBinh != null
                && slotDangCho >= 0
                && this.docTinVaChamKetThuc(ms, soByteConLai);
        while (ms.boDoc().available() > 0) {
            ms.boDoc().readByte();
        }
        if (!tinKetThucHopLe) {
            return;
        }
        this.nguoiChoiDaXacNhanHoatAnh.add(nguoiChoi.ma);
        long phien = this.phienDangChoKetThucBan;
        if (System.currentTimeMillis()
                < this.thoiDiemSomNhatXacNhanKetThucBan) {
            return;
        }
        if (!this.tatCaNguoiChoiDaXacNhanHoatAnh()) {
            return;
        }
        System.out.println("[BOSS RONG][CMD79_HIEN_THI_XONG] slotBan="
                + slotDangCho + " nguoiBao=" + chienBinh.ten);
        this.hoanTatChoKetThucPhatBan(slotDangCho, phien, false);
    }

    private boolean tatCaNguoiChoiDaXacNhanHoatAnh() {
        for (int i = 0; i < SO_SLOT_NGUOI_CHOI; i++) {
            ChickenChienBinh chienBinh = this.chienBinhs[i];
            if (chienBinh != null && chienBinh.coPhien()
                    && !this.nguoiChoiDaXacNhanHoatAnh.contains(
                            chienBinh.nguoiChoi.ma)) {
                return false;
            }
        }
        return true;
    }

    /**
     * BM cua client goc luon gui CMD 79:
     * [soDiem:byte][x:int,y:int]... sau khi vien dan cuoi da bien mat.
     * Toa do va cham chi duoc doc de kiem tra khuon dang packet, tuyet doi
     * khong duoc dung de tinh damage hay sua dia hinh tren server.
     */
    private boolean docTinVaChamKetThuc(ChickenTinNhan ms, int soByte) {
        if (ms.layLenh() != 79 || soByte < 1 || soByte > 513
                || (soByte - 1) % 8 != 0) {
            return false;
        }
        try {
            int soDiem = ms.boDoc().readUnsignedByte();
            int soDiemTheoDoDai = (soByte - 1) / 8;
            if (soDiem != soDiemTheoDoDai || soDiem > 64) {
                return false;
            }
            for (int i = 0; i < soDiem; i++) {
                ms.boDoc().readInt();
                ms.boDoc().readInt();
            }
            return ms.boDoc().available() == 0;
        } catch (IOException loi) {
            return false;
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
        System.out.println("[BOSS RONG][NGUOI_CHOI_ROI] player="
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
        com.chicken.chiso.ChickenHieuUngDongDoi.apDungChoNhomDongMinh(
                this.chienBinhs);
    }

    private void taoBoss() {
        for (CauHinhBossRong.CauHinh cauHinh : CauHinhBossRong.layTatCa()) {
            int slot = cauHinh.getSlot() & 0xFF;
            int tanCong = cauHinh.laBossBanSung()
                    ? CauHinhBossRong.layTanCongTheoSung(cauHinh.getVuKhi())
                    : 0;
            this.chienBinhs[slot] = new ChickenChienBinh(
                    cauHinh.getSlot(), cauHinh.getId(), cauHinh.getX(), cauHinh.getY(),
                    cauHinh.getTen(), cauHinh.getVuKhi(),
                    CauHinhBossRong.MAU_BOSS,
                    tanCong,
                    CauHinhBossRong.GIAP_BOSS);
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
                        return BossRong.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossRong.this.luotHienTai;
                    }

                    @Override
                    public void guiHoatAnhMuiTen(
                            ChickenChienBinh hawk,
                            short goc,
                            ChickenHoatAnhHawk.DuongDan duongDan
                    ) throws IOException {
                        BossRong.this.phatHoatAnhMuiTenHawkBoss(
                                hawk, goc, duongDan);
                    }

                    @Override
                    public void gaySatThuong(
                            ChickenChienBinh mucTieu,
                            int satThuong
                    ) throws IOException {
                        BossRong.this.gaySatThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        BossRong.this.sangLuotSauKyNangAVG();
                    }
                }
        );

        this.kyNangThorBoss = new ChickenKyNangDacBietThor(
                this.chienBinhs,
                this.banDo,
                new ChickenKyNangDacBietThor.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossRong.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossRong.this.luotHienTai;
                    }

                    @Override
                    public void guiTiaSet(
                            ChickenChienBinh thor,
                            byte loaiHieuUng,
                            short[] cacX,
                            short[] cacY
                    ) throws IOException {
                        BossRong.this.phatTiaSetThorBoss(
                                thor, loaiHieuUng, cacX, cacY);
                    }

                    @Override
                    public void gaySatThuong(
                            ChickenChienBinh mucTieu,
                            int satThuong
                    ) throws IOException {
                        BossRong.this.gaySatThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        BossRong.this.sangLuotSauKyNangAVG();
                    }
                }
        );

        this.kyNangLokiBoss = new ChickenKyNangDacBietLoki(
                this.chienBinhs,
                new ChickenKyNangDacBietLoki.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossRong.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossRong.this.luotHienTai;
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
                        BossRong.this.phatBienHinhLokiBoss(loki, mucTieu);
                    }

                    @Override
                    public void capNhatMau(ChickenChienBinh loki)
                            throws IOException {
                        BossRong.this.phatCapNhatMau(loki);
                    }
                }
        );

        this.kyNangUltronBoss = new ChickenKyNangDacBietUltron(
                new ChickenKyNangDacBietUltron.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossRong.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossRong.this.luotHienTai;
                    }

                    @Override
                    public void guiMenuUltron(ChickenChienBinh ultron) {
                        ultron.nguoiChoi.dichVu.guiChonKyNangUltron();
                    }
                }
        );
    }

    private synchronized void chuyenSangLuotTiepTheo(int sauSlot) throws IOException {
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
                CauHinhBossRong.GIAY_MOI_LUOT,
                TimeUnit.SECONDS);

        if (slotTiep >= SO_SLOT_NGUOI_CHOI) {
            this.boHenGio.schedule(() -> this.thucHienLuotBoss(slotTiep, phien),
                    CauHinhBossRong.TRE_BOSS_BAT_DAU_MS,
                    TimeUnit.MILLISECONDS);
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
            System.out.println("[BOSS RONG][HET_25_GIAY] slot=" + slot
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
        return CauHinhBossRong.NAP_DAN_SAU_HANH_DONG;
    }

    private void thucHienLuotBoss(int slot, long phien) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot) {
                return;
            }
            ChickenChienBinh boss = this.chienBinhs[slot];
            if (boss == null || boss.chet || boss.hp <= 0) {
                this.sangLuotSauBoss(slot, phien, 0);
                return;
            }
            ChickenChienBinh mucTieu = DiChuyenBossRong.timNguoiSongGanNhat(
                    boss, this.chienBinhs);
            if (mucTieu == null) {
                this.sangLuotSauBoss(
                        slot, phien, CauHinhBossRong.NAP_DAN_SAU_HANH_DONG);
                return;
            }

            boolean gapNguoi = chonGapNguoiChoLuot();
            if (gapNguoi) {
                System.out.println("[BOSS RONG][CHON_DON] kieu=GAP_THA target="
                        + mucTieu.ten);
                this.bayDenGapNguoi(boss, mucTieu, slot, phien);
            } else {
                System.out.println("[BOSS RONG][CHON_DON] kieu=BAN_TU_XA target="
                        + mucTieu.ten);
                this.bayQuanhRoiBan(boss, mucTieu, slot, phien);
            }
        }
    }

    public static boolean laChieuGapNguoi(int giaTriNgauNhienPhanTram) {
        return giaTriNgauNhienPhanTram >= 0
                && giaTriNgauNhienPhanTram
                        < CauHinhBossRong.TY_LE_GAP_NGUOI_PHAN_TRAM;
    }

    /** Server tung đúng một lần khi bắt đầu lượt của Boss Rồng. */
    public static boolean chonGapNguoiChoLuot() {
        return laChieuGapNguoi(ThreadLocalRandom.current().nextInt(100));
    }

    private void rongBanTuXa(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int slot,
            long phien
    ) {
        synchronized (this) {
            if (!this.conHieuLucLuotBoss(slot, phien, boss)) {
                return;
            }
            ChickenChienBinh mucTieuMoi = this.nguoiChoiConSong(mucTieu)
                    ? mucTieu
                    : DiChuyenBossRong.timNguoiSongGanNhat(
                            boss, this.chienBinhs);
            if (mucTieuMoi == null) {
                this.sangLuotSauBoss(
                        slot, phien, CauHinhBossRong.NAP_DAN_SAU_HANH_DONG);
                return;
            }
            byte windX = this.layWindXChoChienBinh(boss);
            byte windY = this.layWindYChoChienBinh(boss);
            boolean aim = BossRongTanCong.chonAimChoCaLoat();
            ChickenKetQuaDan ketQua = BossRongTanCong.taoPhatBanLua(
                    boss,
                    mucTieuMoi,
                    this.chienBinhs,
                    this.banDo,
                    windX,
                    windY,
                    aim);
            int tongSatThuong =
                    BossRongTanCong.tinhTongSatThuongLoat(ketQua.satThuong);
            System.out.println("[BOSS RONG][BAN_LOAT] target=" + mucTieuMoi.ten
                    + " cheDo=" + (aim ? "AIM" : "NGAU_NHIEN")
                    + " soVien=" + CauHinhBossRong.SO_VIEN_DAN_DAC_BIET
                    + " damageMoiVien=" + ketQua.satThuong
                    + " tongDamage=" + tongSatThuong
                    + " windX=" + windX + " windY=" + windY);
            this.phatLoatHaiVienRong(
                    boss, slot, phien, ketQua);
        }
    }

    private void bayQuanhRoiBan(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int slot,
            long phien
    ) {
        synchronized (this) {
            if (!this.conHieuLucLuotBoss(slot, phien, boss)
                    || !this.nguoiChoiConSong(mucTieu)) {
                this.sangLuotSauBoss(
                        slot, phien, CauHinhBossRong.NAP_DAN_SAU_HANH_DONG);
                return;
            }
            short xCu = boss.x;
            short yCu = boss.y;
            short[] diemDen = DiChuyenBossRong.chonDiemBayQuanhNguoi(
                    boss, mucTieu, this.banDo);
            boss.x = diemDen[0];
            boss.y = diemDen[1];
            try {
                this.phatDiChuyenBoss(boss);
            } catch (IOException loi) {
                System.err.println("[BOSS RONG][LOI_GUI_BAY_QUANH] "
                        + loi.getMessage());
            }
            long thoiGianBay = DiChuyenBossRong.tinhThoiGianBayMs(
                    xCu, yCu, boss.x, boss.y)
                    + CauHinhBossRong.TRE_DU_PHONG_BAY_QUANH_TRUOC_BAN_MS;
            System.out.println("[BOSS RONG][BAY_QUANH] target=" + mucTieu.ten
                    + " tu=" + xCu + "," + yCu
                    + " den=" + boss.x + "," + boss.y
                    + " animationMs=" + thoiGianBay);
            this.boHenGio.schedule(
                    () -> this.rongBanTuXa(boss, mucTieu, slot, phien),
                    thoiGianBay,
                    TimeUnit.MILLISECONDS);
        }
    }

    private void phatLoatHaiVienRong(
            ChickenChienBinh boss,
            int slot,
            long phien,
            ChickenKetQuaDan ketQua
    ) {
        synchronized (this) {
            if (!this.conHieuLucLuotBoss(slot, phien, boss)) {
                return;
            }
            ChickenMayMan.PhienTanCong phienMayMan =
                    ChickenMayMan.batDau(boss, this.chienBinhs);
            try {
                // Mot packet, hai path: client type 1 tu nhả dung hai vien
                // tren cung goc/luc. Khong gui packet thu hai khi BM dang active.
                this.phatBan(boss, ketQua, (byte) 1);
                int tongSatThuong =
                        BossRongTanCong.tinhTongSatThuongLoat(
                                ketQua.satThuong);
                if (ketQua.mucTieu != null
                        && tongSatThuong > 0
                        && !ketQua.mucTieu.chet
                        && !this.daKetThuc) {
                    // Hai vien van duoc ve rieng, nhung HP va so damage chi
                    // cap nhat mot lan sau khi cong damage ca loat.
                    tongSatThuong =
                            phienMayMan.apDung(
                                    ketQua.mucTieu, tongSatThuong);
                    this.gaySatThuong(ketQua.mucTieu, tongSatThuong);
                }
            } catch (IOException loi) {
                System.err.println("[BOSS RONG][LOI_BAN_LOAT] "
                        + loi.getMessage());
            }
            System.out.println("[BOSS RONG][BAN_LOAT_GUI] packet=1"
                    + " soVien=" + CauHinhBossRong.SO_VIEN_DAN_DAC_BIET
                    + " bulletType=" + ketQua.loaiDan
                    + " pathCount=" + ketQua.cacDuongX.length);

            if (this.daKetThuc) {
                return;
            }
            this.boHenGio.schedule(() -> this.sangLuotSauBoss(
                            slot,
                            phien,
                            CauHinhBossRong.NAP_DAN_SAU_HANH_DONG),
                    CauHinhBossRong.TRE_KET_THUC_LOAT_DAN_MS,
                    TimeUnit.MILLISECONDS);
        }
    }

    private void bayDenGapNguoi(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int slot,
            long phien
    ) {
        synchronized (this) {
            if (!this.conHieuLucLuotBoss(slot, phien, boss)
                    || !this.nguoiChoiConSong(mucTieu)) {
                this.sangLuotSauBoss(
                        slot, phien, CauHinhBossRong.NAP_DAN_SAU_HANH_DONG);
                return;
            }
            short[] diemMang = DiChuyenBossRong.chonDiemMangQua(
                    mucTieu, this.banDo);
            int yBatDauTha = diemMang[1]
                    + CauHinhBossRong.LECH_Y_NGUOI_BI_KEP;
            short[] diemTha = DiChuyenBossRong.chonDiemThaAnToan(
                    diemMang[0], yBatDauTha, this.banDo);
            short xTha = diemTha == null
                    ? diemMang[0]
                    : diemTha[0];
            short yTha = (short) (diemTha != null
                    ? diemTha[1]
                    : this.banDo.getHeight() + 24);
            boolean roiXuongVuc = diemTha == null;
            long thoiGianHoatAnh = this.tinhThoiGianGapTha(
                    boss, mucTieu,
                    diemMang[0], diemMang[1],
                    xTha, yTha);
            ChickenMayMan.PhienTanCong phienMayMan =
                    roiXuongVuc
                            ? null
                            : ChickenMayMan.batDau(
                                    boss, this.chienBinhs);

            this.phatHoatAnhGapThaBossRong(
                    boss, mucTieu,
                    diemMang[0], diemMang[1],
                    xTha, yTha);
            System.out.println("[BOSS RONG][GAP_THA] target=" + mucTieu.ten
                    + " xMang=" + diemMang[0]
                    + " yMang=" + diemMang[1]
                    + " xTha=" + xTha
                    + " yTha=" + yTha
                    + " roiVuc=" + roiXuongVuc
                    + " animationMs=" + thoiGianHoatAnh);
            this.boHenGio.schedule(() -> this.hoanTatGapThaNguoi(
                            boss, mucTieu, slot, phien,
                            diemMang[0], diemMang[1],
                            xTha, yTha, roiXuongVuc,
                            phienMayMan),
                    thoiGianHoatAnh, TimeUnit.MILLISECONDS);
        }
    }

    private void hoanTatGapThaNguoi(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int slot,
            long phien,
            short xMang,
            short yMang,
            short xTha,
            short yTha,
            boolean roiXuongVuc,
            ChickenMayMan.PhienTanCong phienMayMan
    ) {
        synchronized (this) {
            if (!this.conHieuLucLuotBoss(slot, phien, boss)
                    || !this.nguoiChoiConSong(mucTieu)) {
                this.sangLuotSauBoss(
                        slot, phien, CauHinhBossRong.NAP_DAN_SAU_HANH_DONG);
                return;
            }

            boss.x = xMang;
            boss.y = yMang;
            mucTieu.x = xTha;
            mucTieu.y = yTha;
            this.phatChotToaDoGapTha(boss, mucTieu);
            int satThuong = roiXuongVuc
                    ? Math.max(1, mucTieu.hp)
                    : Math.max(
                            1,
                            CauHinhBossRong.TAN_CONG_THA_ROI - mucTieu.giap);
            if (!roiXuongVuc && phienMayMan != null) {
                satThuong = phienMayMan.apDung(mucTieu, satThuong);
            }
            try {
                this.gaySatThuong(mucTieu, satThuong);
                System.out.println("[BOSS RONG][GAP_THA_XONG] target="
                        + mucTieu.ten + " damage=" + satThuong
                        + " x=" + mucTieu.x + " y=" + mucTieu.y);
            } catch (IOException ignored) {
            }
            if (!this.daKetThuc) {
                this.boHenGio.schedule(() -> this.sangLuotSauBoss(
                        slot, phien, CauHinhBossRong.NAP_DAN_SAU_HANH_DONG),
                        CauHinhBossRong.TRE_SAU_DAMAGE_GAP_THA_MS,
                        TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * CMD -68 chi chay animation native. Sau khi animation ket thuc, server
     * phat lai toa do authoritative de client khong khoi phuc x/y cu khi doi
     * state hoac doi luot.
     */
    private void phatChotToaDoGapTha(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu
    ) {
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            try {
                nguoiNhan.nguoiChoi.dichVu.guiCapNhatXYDau(
                        boss.chiSo, boss.x, boss.y);
            } catch (IOException loi) {
                System.err.println("[BOSS RONG][LOI_CHOT_TOA_DO_RONG] player="
                        + nguoiNhan.ten + " loi=" + loi.getMessage());
            }
            try {
                nguoiNhan.nguoiChoi.dichVu.guiCapNhatXYDau(
                        mucTieu.chiSo, mucTieu.x, mucTieu.y);
            } catch (IOException loi) {
                System.err.println("[BOSS RONG][LOI_CHOT_TOA_DO_NGUOI] player="
                        + nguoiNhan.ten + " loi=" + loi.getMessage());
            }
        }
    }

    private long tinhThoiGianGapTha(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int xMang,
            int yMang,
            int xTha,
            int yTha
    ) {
        long bayDenNguoi = DiChuyenBossRong.tinhThoiGianBayMs(
                boss.x, boss.y, mucTieu.x, mucTieu.y);
        long mangNguoi = DiChuyenBossRong.tinhThoiGianBayMs(
                mucTieu.x, mucTieu.y, xMang, yMang);
        double khoangTha = Math.hypot(
                xTha - xMang,
                yTha - (yMang
                        + CauHinhBossRong.LECH_Y_NGUOI_BI_KEP));
        long soFrameTha = Math.max(
                1L,
                (long) Math.ceil(
                        khoangTha
                                / CauHinhBossRong.TOC_DO_THA_NGUOI_MOI_FRAME)
        );
        long thoiGianTha = soFrameTha
                * CauHinhBossRong.THOI_GIAN_FRAME_CLIENT_MS;
        return bayDenNguoi
                + mangNguoi
                + CauHinhBossRong.TRE_HOAT_ANH_KEP_MANG_MS
                + thoiGianTha
                + CauHinhBossRong.TRE_DU_PHONG_GAP_THA_MS;
    }

    private boolean conHieuLucLuotBoss(
            int slot,
            long phien,
            ChickenChienBinh boss
    ) {
        return !this.daKetThuc
                && this.maPhienLuot == phien
                && (this.luotHienTai & 0xFF) == slot
                && boss != null
                && !boss.chet
                && boss.hp > 0;
    }

    private boolean nguoiChoiConSong(ChickenChienBinh nguoiChoi) {
        return nguoiChoi != null
                && !nguoiChoi.chet
                && nguoiChoi.hp > 0
                && nguoiChoi.coPhien();
    }

    private void phatHoatAnhGapThaBossRong(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            short xMang,
            short yMang,
            short xTha,
            short yTha
    ) {
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            try {
                nguoiNhan.nguoiChoi.dichVu.guiGapThaBossRong(
                        boss.chiSo,
                        xMang,
                        yMang,
                        mucTieu.chiSo,
                        xTha,
                        yTha
                );
            } catch (IOException loi) {
                System.err.println("[BOSS RONG][LOI_GUI_GAP_THA] player="
                        + nguoiNhan.ten + " loi=" + loi.getMessage());
            }
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

                        @Override
                        public boolean trungHitbox(
                                ChickenChienBinh mucTieu,
                                int danX,
                                int danY
                        ) {
                            return mucTieu.bot
                                    ? DiChuyenBossRong.trungBossRong(
                                            danX, danY,
                                            mucTieu.x, mucTieu.y)
                                    : ChickenKichThuocNhanVat
                                            .trungNguoiChoi(
                                                    danX, danY,
                                                    mucTieu.x, mucTieu.y);
                        }

                        @Override
                        public int nuaRongHitbox(
                                ChickenChienBinh mucTieu
                        ) {
                            return mucTieu.bot
                                    ? CauHinhBossRong.HITBOX_LECH_TRAI
                                    : ChickenKichThuocNhanVat
                                            .NGUOI_CHOI_NUA_RONG;
                        }

                        @Override
                        public int lechTrenHitbox(
                                ChickenChienBinh mucTieu
                        ) {
                            return mucTieu.bot
                                    ? CauHinhBossRong.HITBOX_LECH_TREN
                                    : ChickenKichThuocNhanVat
                                            .NGUOI_CHOI_LECH_TREN;
                        }

                        @Override
                        public int lechDuoiHitbox(
                                ChickenChienBinh mucTieu
                        ) {
                            return mucTieu.bot
                                    ? -CauHinhBossRong.HITBOX_LECH_DUOI
                                    : ChickenKichThuocNhanVat
                                            .NGUOI_CHOI_LECH_DUOI;
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

                    @Override
                    public boolean trungHitbox(
                            ChickenChienBinh mucTieu,
                            int danX,
                            int danY
                    ) {
                        return mucTieu.bot
                                ? DiChuyenBossRong.trungBossRong(
                                        danX, danY, mucTieu.x, mucTieu.y)
                                : ChickenKichThuocNhanVat.trungNguoiChoi(
                                        danX, danY, mucTieu.x, mucTieu.y);
                    }

                    @Override
                    public int nuaRongHitbox(ChickenChienBinh mucTieu) {
                        return mucTieu.bot
                                ? CauHinhBossRong.HITBOX_LECH_TRAI
                                : ChickenKichThuocNhanVat.NGUOI_CHOI_NUA_RONG;
                    }

                    @Override
                    public int lechTrenHitbox(ChickenChienBinh mucTieu) {
                        return mucTieu.bot
                                ? CauHinhBossRong.HITBOX_LECH_TREN
                                : ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_TREN;
                    }

                    @Override
                    public int lechDuoiHitbox(ChickenChienBinh mucTieu) {
                        return mucTieu.bot
                                ? -CauHinhBossRong.HITBOX_LECH_DUOI
                                : ChickenKichThuocNhanVat.NGUOI_CHOI_LECH_DUOI;
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

    private ChickenKetQuaDan banX3UltronBoss(
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
        ChickenKetQuaDan ketQua =
                ChickenLoatBanUltronServer.tao(
                        shooter, dauNong[0], dauNong[1], goc, luc,
                        this.banDo, this.chienBinhs,
                        new ChickenLoatBanUltronServer.BoLocMucTieu() {
                            @Override
                            public boolean chapNhan(
                                    ChickenChienBinh nguoiBan,
                                    ChickenChienBinh mucTieu
                            ) {
                                return ChickenLuatVaChamPhongBoss.chapNhan(
                                        nguoiBan, mucTieu);
                            }

                            @Override
                            public boolean trungHitbox(
                                    ChickenChienBinh mucTieu,
                                    int danX,
                                    int danY
                            ) {
                                return mucTieu.bot
                                        ? DiChuyenBossRong.trungBossRong(
                                                danX, danY,
                                                mucTieu.x, mucTieu.y)
                                        : ChickenKichThuocNhanVat
                                                .trungNguoiChoi(
                                                        danX, danY,
                                                        mucTieu.x,
                                                        mucTieu.y);
                            }
                        });

        ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(shooter, this.chienBinhs);
        phienMayMan.chuanBiPhongThuTruocPhat(
                ketQua.satThuongTheoMucTieu.keySet());
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiLoatLaserUltronDau(
                    shooter.chiSo,
                    shooter.x,
                    shooter.y,
                    goc,
                    luc,
                    ketQua.cacDuongX,
                    ketQua.cacDuongY,
                    phienMayMan.powDaKichHoat()
            );
        }

        for (Map.Entry<ChickenChienBinh, Integer> entry
                : ketQua.satThuongTheoMucTieu.entrySet()) {
            if (!this.daKetThuc && !entry.getKey().chet) {
                this.gaySatThuong(
                        entry.getKey(),
                        phienMayMan.apDung(entry.getKey(), entry.getValue()));
            }
        }

        System.out.println("[BOSS RONG][ULTRON_X3] player="
                + shooter.ten + " goc=" + goc
                + " soVien=3 quyDaoDocLap=true");
        return ketQua;
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
                                    ? DiChuyenBossRong.trungBossRong(
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
                    System.err.println("[BOSS RONG][LOI_KET_THUC] "
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
        this.huyChoKetThucPhatBan();
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
        System.out.println("[BOSS RONG][KET_THUC] ketQua="
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
        if (slot < 0 || slot >= SO_SLOT
                || this.daKetThuc
                || (this.luotHienTai & 0xFF) != slot) {
            return;
        }
        this.huyTacVuHetLuot();
        this.huyChoKetThucPhatBan();
        long phien = this.maPhienLuot;
        this.slotDangChoKetThucBan = slot;
        this.phienDangChoKetThucBan = phien;
        this.napDanSauPhatDangCho = napDanSauBan;
        this.thoiDiemSomNhatXacNhanKetThucBan =
                System.currentTimeMillis()
                + ChickenThoiGianHoatAnhDan.TOI_THIEU_MS;
        long treMs = Math.max(
                ChickenThoiGianHoatAnhDan.TOI_THIEU_MS,
                Math.min(ChickenThoiGianHoatAnhDan.TOI_DA_MS,
                        thoiGianHoatAnhMs)
        );
        this.tacVuChoKetThucBan = this.boHenGio.schedule(
                () -> this.hoanTatChoKetThucPhatBan(slot, phien, true),
                treMs,
                TimeUnit.MILLISECONDS);
    }

    private void hoanTatChoKetThucPhatBan(
            int slot,
            long phien,
            boolean hetHan
    ) {
        synchronized (this) {
            if (this.daKetThuc
                    || this.slotDangChoKetThucBan != slot
                    || this.phienDangChoKetThucBan != phien
                    || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot) {
                return;
            }
            int napDanSauBan = this.napDanSauPhatDangCho;
            this.huyChoKetThucPhatBan();
            this.datNapDanSauHanhDong(slot, napDanSauBan);
            if (hetHan) {
                System.out.println("[BOSS RONG][ANIMATION_DAN_TIMEOUT] slot="
                        + slot + " phien=" + phien
                        + " serverChuyenLuot=true");
            }
            try {
                this.chuyenSangLuotTiepTheo(slot);
            } catch (IOException loi) {
                System.err.println("[BOSS RONG][LOI_CHUYEN_LUOT_SAU_DAN] "
                        + loi.getMessage());
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
        this.thoiDiemSomNhatXacNhanKetThucBan = 0L;
        this.nguoiChoiDaXacNhanHoatAnh.clear();
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
        for (int i = CauHinhBossRong.SLOT_BOSS_DAU;
                i <= CauHinhBossRong.SLOT_BOSS_CUOI; i++) {
            ChickenChienBinh boss = this.chienBinhs[i];
            if (boss != null && !boss.chet && boss.hp > 0) {
                dem++;
            }
        }
        return dem;
    }

    private void phatLuot(ChickenChienBinh hienTai) throws IOException {
        byte giay = (byte) CauHinhBossRong.GIAY_MOI_LUOT;
        this.dongMenuKyNangKhiDoiLuot(this.chienBinhs);
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiGio(
                    this.gioHienTai.getWindX(), this.gioHienTai.getWindY());
            nguoiNhan.nguoiChoi.dichVu.guiLuotBossBaoVayTiep(
                    hienTai.chiSo, hienTai.x, hienTai.y,
                    this.chienBinhs, this.napDan,
                    this.thuTuHanhDongNapDan, giay);
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
        System.out.println("[BOSS RONG][LUOT] slot=" + slot
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
        System.out.println("[BOSS RONG][NAP_DAN] slot=" + slot
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
            nguoiNhan.nguoiChoi.dichVu.guiDiChuyenBigBoss(
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
                        this.banDo.getWidth(), this.banDo.getHeight(),
                        (mucTieu, tiaX, tiaY) -> mucTieu.bot
                                ? DiChuyenBossRong.trungBossRong(
                                        tiaX, tiaY, mucTieu.x, mucTieu.y)
                                : ChickenKichThuocNhanVat.trungNguoiChoi(
                                        tiaX, tiaY, mucTieu.x, mucTieu.y));
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
            nguoiNhan.nguoiChoi.dichVu.guiKetQuaBanBossBaoVay(
                    shooter.chiSo,
                    shooter.x,
                    shooter.y,
                    ketQua,
                    soPhat <= 0 ? (byte) 1 : soPhat,
                    shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON,
                    shooter.nguoiChoi != null && shooter.nguoiChoi.dangHienHieuUngPow());
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
        for (int i = 0; i < soDuong; i++) {
            short[] xs = ketQua.cacDuongX[i];
            short[] ys = ketQua.cacDuongY[i];
            int soDiem = Math.min(xs == null ? 0 : xs.length, ys == null ? 0 : ys.length);
            if (soDiem <= 0) {
                continue;
            }
            short x = xs[soDiem - 1];
            short y = ys[soDiem - 1];
            if (x >= 0 && y >= 0 && x < this.banDo.getWidth()
                    && y < this.banDo.getHeight() && this.banDo.coVaCham(x, y)) {
                this.banDo.phaDiaHinh(x, y, ketQua.loaiDan);
            }
        }
    }

    private ChickenQuanLyCongThucSung.KiemTraBanDo kiemTraBanDo() {
        return new ChickenQuanLyCongThucSung.KiemTraBanDo() {
            @Override
            public int getWidth() { return BossRong.this.banDo.getWidth(); }
            @Override
            public int getHeight() { return BossRong.this.banDo.getHeight(); }
            @Override
            public boolean coVaCham(short x, short y) {
                return BossRong.this.banDo.coVaCham(x, y);
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
