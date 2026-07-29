package com.chicken.phong.boss.trandau.datbom;

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
import com.chicken.chien.ChickenNguCanhLaySung;
import com.chicken.chien.ChickenNapDanServer;
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
import com.chicken.phong.boss.trandau.ChickenHoatAnhNoCamTu;
import com.chicken.phong.boss.trandau.ChickenLuatVaChamPhongBoss;
import com.chicken.phong.boss.trandau.baovay.BossBanSung;
import com.chicken.phong.boss.trandau.baovay.BossCamTu;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Trận riêng của map 53 - Boss Đặt bom.
 * Phiến quân đứng cố định tại vị trí spawn và tấn công bằng súng đang cầm.
 * Số Phiến quân tăng theo mốc người chơi: 1→4, 2→6, 4→7 và 6→8.
 * Lượt, gió và kỹ năng AVG dùng chung cơ chế phòng boss.
 */
public final class BossDatBom extends ChickenQuanLyChien {
    private static final int SO_SLOT = 16;
    private static final int SO_SLOT_NGUOI_CHOI = 8;
    /** Người chơi và boss đều có tối đa 25 giây cho một lượt. */
    private static final int GIAY_MOI_LUOT = 25;
    private static final int TRE_BOSS_BAT_DAU_MS = 550;
    private static final int BOM_NUA_RONG = 7;
    private static final byte PHE_NGUOI_CHOI_THANG =
            ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THANG;
    private static final byte PHE_BOSS_THANG =
            ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THUA;

    private final SanhChoBoss sanh;
    private final ChickenQuanLyBanDo banDo;
    private final ChickenChienBinh[] chienBinhs = new ChickenChienBinh[SO_SLOT];
    private final CauHinhBossDatBom.CauHinh[] cauHinhBoss =
            new CauHinhBossDatBom.CauHinh[SO_SLOT];
    private final CauHinhBossDatBom.CauHinh[] danhSachBoss;
    private final int soNguoiThuc;
    /** ID template súng do server random một lần khi tạo trận, không lấy từ client. */
    private final int[] idSungBoss = new int[SO_SLOT];
    /** Thời gian nạp hiện tại của từng ghế/slot; lúc vào trận đều bằng 0. */
    private final int[] napDan = new int[SO_SLOT];
    /** Timed bombs are derived only from the server's one-based global turn. */
    private final BoDemBomBossDatBom boDemBom = new BoDemBomBossDatBom();
    private final BoDemGoBomBossDatBom boDemGoBom =
            new BoDemGoBomBossDatBom();
    private int chiSoBossDatBomTiepTheo;
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
    /** Actual one-based turns started in this match; separate from action IDs. */
    private long tongLuotDaBatDau;
    private long maPhienLuot;
    private ScheduledFuture<?> tacVuHetLuot;
    private ScheduledFuture<?> tacVuChoKetThucBan;
    private int slotDangChoKetThucBan = -1;
    private long phienDangChoKetThucBan = -1L;
    private int napDanSauPhatDangCho;
    private long thoiDiemSomNhatXacNhanKetThucBan;
    private final Set<Integer> nguoiChoiDaXacNhanHoatAnh = new HashSet<>();

    public BossDatBom(SanhChoBoss sanh) {
        super(null, layNguoiChoiTheoGhe(sanh),
                (byte) CauHinhBossDatBom.MAP_ID, false);
        this.sanh = sanh;
        this.banDo = new ChickenQuanLyBanDo(CauHinhBossDatBom.MAP_ID);
        this.soNguoiThuc = demNguoiChoiTrongSanh(sanh);
        this.danhSachBoss = CauHinhBossDatBom.layChoSoNguoi(
                this.soNguoiThuc);
        this.boHenGio = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "boss-dat-bom-P4-"
                    + (sanh == null ? -1 : sanh.getMaBan() & 0xFF));
            thread.setDaemon(true);
            return thread;
        });
        Arrays.fill(this.idSungBoss, -1);
        this.taoNguoiChoi(sanh);
        this.taoBoss();
        this.khoiTaoKyNangAVG();
        this.dangKyNguoiChoiTrongTran();
    }

    public static BossDatBom tao(SanhChoBoss sanh) {
        if (sanh == null || (sanh.getMaBanDo() & 0xFF) != CauHinhBossDatBom.MAP_ID) {
            return null;
        }
        return new BossDatBom(sanh);
    }

    @Override
    public synchronized void batDau() throws IOException {
        if (this.daBatDau || this.daKetThuc) {
            return;
        }
        this.daBatDau = true;
        for (CauHinhBossDatBom.CauHinh cauHinh : this.danhSachBoss) {
            int slot = cauHinh.getSlot() & 0xFF;
            ChickenChienBinh boss = this.chienBinhs[slot];
            ChickenQuanLyDanSung.DuLieuSung duLieu =
                    this.layDuLieuSungBoss(slot, boss);
            System.out.println("[BOSS DAT BOM][TAO_BOSS] slot="
                    + slot
                    + " ten=" + cauHinh.getTen()
                    + " x=" + cauHinh.getX()
                    + " y=" + cauHinh.getY()
                    + " hp=" + CauHinhBossDatBom.MAU_BOSS
                    + " loai=" + cauHinh.getLoai()
                    + " idSung=" + this.idSungBoss[slot]
                    + " tenSung="
                    + (duLieu == null ? "unknown" : duLieu.getTenSung())
                    + " sungPart="
                    + (boss == null ? -1 : boss.maVuKhi)
                    + " napDan=" + this.layNapDanBoss(slot));
        }
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiBatDauDau(
                    this.banDo.layMaBanDo(), this.chienBinhs, this.banDo.layMaNen());
            for (CauHinhBossDatBom.CauHinh cauHinh : this.danhSachBoss) {
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
                        CauHinhBossDatBom.MAU_BOSS
                );
            }
        }
        System.out.println("[BOSS DAT BOM][BAT_DAU] P4-"
                + (this.sanh.getMaBan() & 0xFF)
                + " map=53 players=" + this.demNguoiChoiSong()
                + " playersReal=" + this.soNguoiThuc
                + " bosses=" + this.danhSachBoss.length);
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
                    this.gaySatThuong(entry.getKey(), entry.getValue());
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
        this.datNapDanSauHanhDong(
                chienBinh.chiSo & 0xFF,
                this.layNapDanSauBanNguoiChoi(chienBinh));
        this.chuyenSangLuotTiepTheo(chienBinh.chiSo & 0xFF);
    }

    @Override
    public synchronized void kiemTraVaCham(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms)
            throws IOException {
        int soByteConLai = ms.boDoc().available();
        ChickenChienBinh chienBinh = this.layNguoiChoi(nguoiChoi);
        if (chienBinh == null) {
            while (ms.boDoc().available() > 0) {
                ms.boDoc().readByte();
            }
            return;
        }
        int slotDangCho = this.slotDangChoKetThucBan;
        boolean tinKetThucHopLe = slotDangCho >= 0
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
                < this.thoiDiemSomNhatXacNhanKetThucBan
                || !this.tatCaNguoiChoiDaXacNhanHoatAnh()) {
            return;
        }
        System.out.println("[BOSS DAT BOM][CMD79_HIEN_THI_XONG] slotBan="
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
        } catch (IOException ignored) {
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
        System.out.println("[BOSS DAT BOM][NGUOI_CHOI_ROI] player="
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
        try {
            this.xoaBomConLaiTrenClient();
        } catch (IOException ignored) {
        }
        this.daKetThuc = true;
        this.huyChoKetThucPhatBan();
        this.huyTacVuHetLuot();
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
    }

    private void taoBoss() {
        for (CauHinhBossDatBom.CauHinh cauHinh : this.danhSachBoss) {
            int slot = cauHinh.getSlot() & 0xFF;
            ChickenQuanLyDanSung.DuLieuSung duLieu =
                    CauHinhBossDatBom.chonSungShopKhongAvg();
            if (duLieu == null) {
                duLieu = ChickenQuanLyDanSung.theoPartSung(
                        cauHinh.getVuKhi());
            }
            int idSung = duLieu == null ? -1 : duLieu.getIdSung();
            short partSung = duLieu == null
                    ? cauHinh.getVuKhi()
                    : duLieu.getPartSung();
            int tanCong = CauHinhBossDatBom.layTanCongTheoIdSung(idSung);
            this.idSungBoss[slot] = idSung;
            this.chienBinhs[slot] = new ChickenChienBinh(
                    cauHinh.getSlot(), cauHinh.getId(), cauHinh.getX(), cauHinh.getY(),
                    cauHinh.getTen(), partSung,
                    CauHinhBossDatBom.MAU_BOSS, tanCong, 0);
            this.cauHinhBoss[slot] = cauHinh;
        }
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
        return CauHinhBossDatBom.layNapDanTheoIdSung(
                this.idSungBoss[slot]);
    }

    private void khoiTaoKyNangAVG() {
        this.kyNangHawkBoss = new ChickenKyNangDacBietHawk(
                this.chienBinhs,
                this.banDo,
                new ChickenKyNangDacBietHawk.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossDatBom.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossDatBom.this.luotHienTai;
                    }

                    @Override
                    public void guiHoatAnhMuiTen(
                            ChickenChienBinh hawk,
                            short goc,
                            ChickenHoatAnhHawk.DuongDan duongDan
                    ) throws IOException {
                        BossDatBom.this.phatHoatAnhMuiTenHawkBoss(
                                hawk, goc, duongDan);
                    }

                    @Override
                    public void gaySatThuong(
                            ChickenChienBinh mucTieu,
                            int satThuong
                    ) throws IOException {
                        BossDatBom.this.gaySatThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        BossDatBom.this.sangLuotSauKyNangAVG();
                    }
                }
        );

        this.kyNangThorBoss = new ChickenKyNangDacBietThor(
                this.chienBinhs,
                this.banDo,
                new ChickenKyNangDacBietThor.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossDatBom.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossDatBom.this.luotHienTai;
                    }

                    @Override
                    public void guiTiaSet(
                            ChickenChienBinh thor,
                            byte loaiHieuUng,
                            short[] cacX,
                            short[] cacY
                    ) throws IOException {
                        BossDatBom.this.phatTiaSetThorBoss(
                                thor, loaiHieuUng, cacX, cacY);
                    }

                    @Override
                    public void gaySatThuong(
                            ChickenChienBinh mucTieu,
                            int satThuong
                    ) throws IOException {
                        BossDatBom.this.gaySatThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        BossDatBom.this.sangLuotSauKyNangAVG();
                    }
                }
        );

        this.kyNangLokiBoss = new ChickenKyNangDacBietLoki(
                this.chienBinhs,
                new ChickenKyNangDacBietLoki.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossDatBom.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossDatBom.this.luotHienTai;
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
                        BossDatBom.this.phatBienHinhLokiBoss(loki, mucTieu);
                    }

                    @Override
                    public void capNhatMau(ChickenChienBinh loki)
                            throws IOException {
                        BossDatBom.this.phatCapNhatMau(loki);
                    }
                }
        );

        this.kyNangUltronBoss = new ChickenKyNangDacBietUltron(
                new ChickenKyNangDacBietUltron.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossDatBom.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossDatBom.this.luotHienTai;
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
        this.huyChoKetThucPhatBan();
        int slotTiep = this.timSlotTheoNapDan(sauSlot);
        if (slotTiep < 0) {
            this.ketThuc(false);
            return;
        }
        long soThuTuLuotMoi = this.tongLuotDaBatDau + 1L;
        if (!this.xuLyBomKhiBatDauLuot(soThuTuLuotMoi)) {
            return;
        }
        // Chi huy watchdog cua luot cu sau khi toan bo xu ly bom da thanh
        // cong. Neu CMD109/logic bom nem loi, watchdog cu van con de tran
        // khong bi ket vinh vien tai slot vua ban.
        this.huyTacVuHetLuot();
        this.tongLuotDaBatDau = soThuTuLuotMoi;
        this.luotHienTai = (byte) slotTiep;
        this.maPhienLuot++;
        ChickenChienBinh hienTai = this.chienBinhs[slotTiep];
        if (slotTiep < SO_SLOT_NGUOI_CHOI) {
            hienTai.quangDuongDiChuyenConLai =
                    ChickenThanhDiChuyenAVG.hoiDay(hienTai.theLucDiChuyenToiDa);
        }

        // Mỗi lượt đổi gió đúng một lần; toàn bộ loạt đạn trong lượt dùng chung gió.
        this.gioHienTai = ChickenHeThongGio.taoGioMoi();
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
        // Trang thai va hai tac vu bao hiem da duoc tao truoc khi gui packet.
        // Loi mang cua mot client khong con co the lam mat luot server.
        this.phatLuot(hienTai);
    }

    /**
     * CMD 109 protocol used by the native client:
     * action 0 = create (id, x, y, turns), action 1 = explode, action 2 =
     * defuse progress, action 3 = remove, action 4 = update remaining turns.
     */
    private boolean xuLyBomKhiBatDauLuot(long soThuTuLuot) throws IOException {
        if (soThuTuLuot > 1) {
            this.xuLyGoBomSauLuotHoanThanh();
        }
        BoDemBomBossDatBom.KetQuaBatDauLuot ketQua =
                this.boDemBom.batDauLuot(soThuTuLuot);

        for (BoDemBomBossDatBom.Bom bom : ketQua.getBomDaCapNhat()) {
            this.phatCapNhatLuotBom(bom);
        }
        for (BoDemBomBossDatBom.Bom bom : ketQua.getBomDaNo()) {
            this.boDemGoBom.xoaBom(bom.getId());
            this.phatNoBom(bom);
            this.ketLieuToanBoNguoiChoiBangBom(bom);
            if (this.daKetThuc) {
                return false;
            }
        }

        BoDemBomBossDatBom.Bom bomMoi = ketQua.getBomMoi();
        if (bomMoi != null) {
            ChickenChienBinh nguoiDat = this.chonBossDatBomConSong();
            if (nguoiDat == null) {
                return !this.kiemTraKetThuc();
            }
            this.phatTaoBom(bomMoi, nguoiDat.x, nguoiDat.y);
            this.boDemGoBom.dangKyBom(
                    bomMoi.getId(), nguoiDat.x, nguoiDat.y);
            System.out.println("[BOSS DAT BOM][CMD109_DAT] luot="
                    + soThuTuLuot
                    + " id=" + (bomMoi.getId() & 0xFF)
                    + " x=" + nguoiDat.x
                    + " y=" + nguoiDat.y
                    + " demNguoc=" + bomMoi.getLuotConLai()
                    + " nguoiDat=" + nguoiDat.ten);
        }
        return true;
    }

    private void xuLyGoBomSauLuotHoanThanh() throws IOException {
        this.capNhatBomRoiTheoDiaHinh();
        ArrayList<BoDemGoBomBossDatBom.ViTriNguoiChoi> viTris =
                new ArrayList<>();
        for (int slot = 0; slot < SO_SLOT_NGUOI_CHOI; slot++) {
            ChickenChienBinh nguoiChoi = this.chienBinhs[slot];
            if (nguoiChoi != null && !nguoiChoi.chet
                    && nguoiChoi.hp > 0 && nguoiChoi.coPhien()) {
                viTris.add(new BoDemGoBomBossDatBom.ViTriNguoiChoi(
                        nguoiChoi.x, nguoiChoi.y));
            }
        }

        BoDemGoBomBossDatBom.KetQuaKetThucLuot ketQua =
                this.boDemGoBom.ketThucLuot(viTris);
        for (BoDemGoBomBossDatBom.TienDo tienDo
                : ketQua.getTienDoThayDoi()) {
            this.phatTinBom(GiaoThucBomBossDatBom.capNhatTienDoGo(
                    tienDo.getId(), tienDo.getPhanTram()));
            System.out.println("[BOSS DAT BOM][CMD109_GO] id="
                    + (tienDo.getId() & 0xFF)
                    + " phanTram=" + tienDo.getPhanTram());
        }
        for (byte id : ketQua.getBomDaGo()) {
            if (!this.boDemBom.xoaBom(id)) {
                continue;
            }
            this.phatXoaBom(id);
            System.out.println("[BOSS DAT BOM][CMD109_GO_XONG] id="
                    + (id & 0xFF)
                    + " soLuot="
                    + BoDemGoBomBossDatBom.SO_LUOT_CAN_GO);
        }
    }

    private void capNhatBomRoiTheoDiaHinh() {
        for (BoDemGoBomBossDatBom.ViTriBom bom
                : this.boDemGoBom.chupViTriBom()) {
            int yMoi = this.timMatDatChoBom(bom.getX(), bom.getY());
            if (yMoi == bom.getY()) {
                continue;
            }
            this.boDemGoBom.capNhatViTriBom(
                    bom.getId(), bom.getX(), yMoi);
            System.out.println("[BOSS DAT BOM][BOM_ROI] id="
                    + (bom.getId() & 0xFF)
                    + " x=" + bom.getX()
                    + " yCu=" + bom.getY()
                    + " yMoi=" + yMoi);
        }
    }

    private int timMatDatChoBom(int x, int yHienTai) {
        int batDauY = Math.max(0, yHienTai);
        int ketThucY = this.banDo.getHeight();
        for (int y = batDauY; y < ketThucY; y++) {
            for (int dx = -BOM_NUA_RONG; dx < BOM_NUA_RONG; dx++) {
                int px = x + dx;
                if (px >= 0 && px < this.banDo.getWidth()
                        && this.banDo.coVaCham((short) px, (short) y)) {
                    return y;
                }
            }
        }
        return this.banDo.getHeight() + 32;
    }

    private ChickenChienBinh chonBossDatBomConSong() {
        if (this.danhSachBoss.length == 0) {
            return null;
        }
        for (int thu = 0; thu < this.danhSachBoss.length; thu++) {
            int chiSo = Math.floorMod(
                    this.chiSoBossDatBomTiepTheo++,
                    this.danhSachBoss.length);
            int slot = this.danhSachBoss[chiSo].getSlot() & 0xFF;
            ChickenChienBinh boss = this.chienBinhs[slot];
            if (boss != null && !boss.chet && boss.hp > 0) {
                return boss;
            }
        }
        return null;
    }

    private void ketLieuToanBoNguoiChoiBangBom(
            BoDemBomBossDatBom.Bom bom
    ) throws IOException {
        int soNguoiBiHa = 0;
        for (int slot = 0; slot < SO_SLOT_NGUOI_CHOI; slot++) {
            ChickenChienBinh mucTieu = this.chienBinhs[slot];
            if (mucTieu == null || mucTieu.chet
                    || mucTieu.hp <= 0 || !mucTieu.coPhien()) {
                continue;
            }
            // Timed bomb is explicitly fatal: armor and HP above 6000 do not
            // turn the server-authoritative result into a client-side guess.
            mucTieu.hp = 0;
            mucTieu.chet = true;
            this.phatCapNhatMau(mucTieu);
            soNguoiBiHa++;
        }
        System.out.println("[BOSS DAT BOM][CMD109_NO] id="
                + (bom.getId() & 0xFF)
                + " damageHienThi="
                + CauHinhBossDatBom.SAT_THUONG_BOM_HEN_GIO
                + " boQuaGiap=true ketLieu=true soNguoiBiHa="
                + soNguoiBiHa);
        this.kiemTraKetThuc();
    }

    private void phatTaoBom(
            BoDemBomBossDatBom.Bom bom,
            int x,
            int y
    ) throws IOException {
        this.phatTinBom(GiaoThucBomBossDatBom.taoBom(
                bom.getId(), x, y, bom.getLuotConLai()));
    }

    private void phatCapNhatLuotBom(
            BoDemBomBossDatBom.Bom bom
    ) throws IOException {
        this.phatTinBom(GiaoThucBomBossDatBom.capNhatLuot(
                bom.getId(), bom.getLuotConLai()));
    }

    private void phatNoBom(BoDemBomBossDatBom.Bom bom) throws IOException {
        this.phatTinBom(GiaoThucBomBossDatBom.noBom(bom.getId()));
    }

    private void phatXoaBom(BoDemBomBossDatBom.Bom bom) throws IOException {
        this.phatXoaBom(bom.getId());
    }

    private void phatXoaBom(byte id) throws IOException {
        this.phatTinBom(GiaoThucBomBossDatBom.xoaBom(id));
    }

    private void phatTinBom(ChickenTinNhan tin) throws IOException {
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            try {
                nguoiNhan.nguoiChoi.dichVu.guiTin(tin);
            } catch (Exception loi) {
                // A broken client must not stop the authoritative countdown,
                // block turn progression, or prevent teammates receiving it.
                System.err.println("[BOSS DAT BOM][LOI_GUI_CMD109] player="
                        + nguoiNhan.ten
                        + " loi=" + loi.getClass().getSimpleName());
            }
        }
    }

    private void xoaBomConLaiTrenClient() throws IOException {
        for (BoDemBomBossDatBom.Bom bom
                : this.boDemBom.chupBomDangHoatDong()) {
            this.phatXoaBom(bom);
        }
        this.boDemBom.xoaTatCa();
        this.boDemGoBom.xoaTatCa();
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
            System.out.println("[BOSS DAT BOM][HET_25_GIAY] slot=" + slot
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
        CauHinhBossDatBom.CauHinh cauHinh = slot >= 0 && slot < this.cauHinhBoss.length
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
            CauHinhBossDatBom.CauHinh cauHinh = this.cauHinhBoss[slot];
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
                    this.layNapDanBoss(slot));
            return;
        }
        ChickenQuanLyDanSung.DuLieuSung duLieu =
                this.layDuLieuSungBoss(slot, boss);
        boolean aimChuan = BossBanSung.chonCheDoAimChuan();
        ChickenKetQuaDan ketQua =
                BossBanSung.taoPhatBanTheoCongThucSung(
                        boss,
                        mucTieu,
                        this.chienBinhs,
                        duLieu,
                        this.banDo,
                        this.layWindXChoChienBinh(boss),
                        this.layWindYChoChienBinh(boss),
                        aimChuan
                );
        System.out.println("[BOSS DAT BOM][BOSS_BAN] boss=" + boss.ten
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
        // Lap lich ket thuc truoc khi gui CMD22/cap nhat dia hinh. Neu mot
        // buoc hien thi nem RuntimeException, luot van co duong thoat.
        this.choServerKetThucPhatBan(
                boss,
                this.layNapDanBoss(slot),
                thoiGianHoatAnhMs);
        try {
            this.phatBan(boss, ketQua, (byte) 1);
            this.phaDiaHinhNeuCan(ketQua);
            for (Map.Entry<ChickenChienBinh, Integer> entry
                    : ketQua.satThuongTheoMucTieu.entrySet()) {
                if (!this.daKetThuc && entry.getKey() != null
                        && !entry.getKey().chet && entry.getValue() > 0) {
                    this.gaySatThuong(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception loi) {
            System.err.println("[BOSS DAT BOM][LOI_XU_LY_PHAT_BAN] slot="
                    + slot + " ten=" + boss.ten
                    + " loaiDan=" + (ketQua.loaiDan & 0xFF)
                    + " loi=" + loi.getClass().getSimpleName()
                    + ": " + loi.getMessage());
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
            if (this.daTrongPhamViNo(camTu, mucTieu)) {
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
            if (this.daTrongPhamViNo(camTu, mucTieu)) {
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

    private boolean daTrongPhamViNo(
            ChickenChienBinh datBom,
            ChickenChienBinh mucTieu
    ) {
        if (datBom == null || mucTieu == null) {
            return false;
        }
        long dx = (long) mucTieu.x - datBom.x;
        long dy = (long) mucTieu.y - datBom.y;
        long banKinh = CauHinhBossDatBom.BAN_KINH_KICH_NO;
        return dx * dx + dy * dy <= banKinh * banKinh;
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
            System.out.println("[BOSS DAT BOM][CAM_TU_ROI_MAP] boss="
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
            int satThuongThuc = Math.max(
                    1,
                    CauHinhBossDatBom.SAT_THUONG_CAM_TU - mucTieu.giap
            );
            ChickenKetQuaDan hieuUngNo = ChickenHoatAnhNoCamTu.tao(
                    camTu, mucTieu, satThuongThuc);
            this.phatBan(camTu, hieuUngNo, (byte) 1);
            this.banDo.phaDiaHinh(
                    camTu.x, camTu.y, ChickenHoatAnhNoCamTu.LOAI_DAN);
            this.gaySatThuong(mucTieu, satThuongThuc);
            camTu.hp = 0;
            camTu.chet = true;
            this.phatCapNhatMau(camTu);
            System.out.println("[BOSS DAT BOM][CAM_TU_NO] boss=" + camTu.ten
                    + " target=" + mucTieu.ten + " damage="
                    + satThuongThuc
                    + " damageGoc=" + CauHinhBossDatBom.SAT_THUONG_CAM_TU
                    + " giap=" + mucTieu.giap
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
            ChickenChienBinh shooter, byte loaiDan, short goc, byte luc, byte lucPhu
    ) {
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

        System.out.println("[BOSS DAT BOM][ULTRON_X3] player="
                + shooter.ten + " goc=" + goc
                + " soVien=3 quyDaoDocLap=true");
        return loatMayChu;
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
                    System.err.println("[BOSS DAT BOM][LOI_KET_THUC] "
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
        try {
            this.xoaBomConLaiTrenClient();
        } catch (IOException loi) {
            this.boDemBom.xoaTatCa();
            System.err.println("[BOSS DAT BOM][LOI_XOA_CMD109] "
                    + loi.getMessage());
        }
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
        System.out.println("[BOSS DAT BOM][KET_THUC] ketQua="
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
        this.huyChoKetThucPhatBan();
        long phien = this.maPhienLuot;
        this.slotDangChoKetThucBan = slot;
        this.phienDangChoKetThucBan = phien;
        this.napDanSauPhatDangCho = napDanSauBan;
        long treMs = Math.max(
                ChickenThoiGianHoatAnhDan.TOI_THIEU_MS,
                Math.min(
                        ChickenThoiGianHoatAnhDan.TOI_DA_MS,
                        thoiGianHoatAnhMs
                )
        );
        // CMD79 chi la tin hieu client da ve xong. Client khong duoc phep
        // tua luot truoc ca quy dao va phan duoi VFX ma server da tinh.
        this.thoiDiemSomNhatXacNhanKetThucBan =
                System.currentTimeMillis() + treMs;
        this.tacVuChoKetThucBan = this.boHenGio.schedule(
                () -> this.hoanTatChoKetThucPhatBan(slot, phien, true),
                treMs,
                TimeUnit.MILLISECONDS
        );
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
                System.out.println(
                        "[BOSS DAT BOM][ANIMATION_DAN_TIMEOUT] slot="
                        + slot + " phien=" + phien
                        + " serverChuyenLuot=true");
            }
            try {
                this.chuyenSangLuotTiepTheo(slot);
            } catch (Exception loi) {
                System.err.println("[BOSS DAT BOM][LOI_CHUYEN_LUOT_SAU_DAN] slot="
                        + slot + " phien=" + phien
                        + " loi=" + loi.getClass().getSimpleName()
                        + ": " + loi.getMessage());
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
        for (int i = CauHinhBossDatBom.SLOT_BOSS_DAU;
                i <= CauHinhBossDatBom.SLOT_BOSS_CUOI; i++) {
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
            try {
                nguoiNhan.nguoiChoi.dichVu.guiGio(
                        this.gioHienTai.getWindX(), this.gioHienTai.getWindY());
                nguoiNhan.nguoiChoi.dichVu.guiLuotBossBaoVayTiep(
                        hienTai.chiSo, hienTai.x, hienTai.y,
                        this.chienBinhs, this.napDan, giay);
            } catch (Exception loi) {
                System.err.println("[BOSS DAT BOM][LOI_GUI_LUOT] player="
                        + nguoiNhan.ten + " slotMoi="
                        + (hienTai.chiSo & 0xFF)
                        + " loi=" + loi.getClass().getSimpleName());
            }
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
        System.out.println("[BOSS DAT BOM][LUOT] slot=" + slot
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
        System.out.println("[BOSS DAT BOM][NAP_DAN] slot=" + slot
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
                    soPhat <= 0 ? (byte) 1 : soPhat,
                    shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON);
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
            if (x >= 0 && y >= 0 && x < this.banDo.getWidth()
                    && y < this.banDo.getHeight() && this.banDo.coVaCham(x, y)) {
                this.banDo.phaDiaHinh(x, y, ketQua.loaiDan);
            }
        }
    }

    private ChickenQuanLyCongThucSung.KiemTraBanDo kiemTraBanDo() {
        return new ChickenQuanLyCongThucSung.KiemTraBanDo() {
            @Override
            public int getWidth() { return BossDatBom.this.banDo.getWidth(); }
            @Override
            public int getHeight() { return BossDatBom.this.banDo.getHeight(); }
            @Override
            public boolean coVaCham(short x, short y) {
                return BossDatBom.this.banDo.coVaCham(x, y);
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

    private static int demNguoiChoiTrongSanh(SanhChoBoss sanh) {
        if (sanh == null) {
            return 0;
        }
        int dem = 0;
        for (ThanhVienBoss thanhVien : sanh.chupThanhVien()) {
            if (thanhVien != null && thanhVien.getNguoiChoi() != null) {
                dem++;
            }
        }
        return dem;
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
