package com.chicken.phong.boss.trandau.rua;

import com.chicken.phong.boss.trandau.ruarong.BossRuaRongTanCong;

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
import java.util.concurrent.TimeUnit;

/**
 * Trận riêng của map 54 - Boss Rùa.
 * Boss Rùa dùng hình BigBoss native của client, đuổi người gần nhất,
 * gây sát thương khi chạm rồi bắn viên đạn riêng như Rùa ở map 58.
 */
public final class BossRua extends ChickenQuanLyChien {
    private static final int SO_SLOT = 9;
    private static final int SO_SLOT_NGUOI_CHOI = 8;
    /** Người chơi và boss đều có tối đa 25 giây cho một lượt. */
    private static final int GIAY_MOI_LUOT = 25;
    /**
     * CMD24 đã chỉ được phát sau khi animation đạn người chơi kết thúc. Chỉ
     * cần một nhịp ngắn để client áp dụng lượt mới trước khi nhận CMD21.
     */
    private static final int TRE_BOSS_BAT_DAU_MS = 100;
    private static final byte PHE_NGUOI_CHOI_THANG =
            ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THANG;
    private static final byte PHE_BOSS_THANG =
            ChickenKetQuaTranBoss.CLIENT_NGUOI_CHOI_THUA;

    private final SanhChoBoss sanh;
    private final ChickenQuanLyBanDo banDo;
    private final ChickenChienBinh[] chienBinhs = new ChickenChienBinh[SO_SLOT];
    private final CauHinhBossRua.CauHinh[] cauHinhBoss =
            new CauHinhBossRua.CauHinh[SO_SLOT];
    /** Thời gian nạp hiện tại của từng ghế/slot; lúc vào trận đều bằng 0. */
    private final int[] napDan = new int[SO_SLOT];
    private final long[] thuTuHanhDongNapDan = new long[SO_SLOT];
    /** Số lượt đã hành động của từng Rùa trong riêng trận này. */
    private final int[] soLuotRua = new int[SO_SLOT];
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

    public BossRua(SanhChoBoss sanh) {
        super(null, layNguoiChoiTheoGhe(sanh),
                (byte) CauHinhBossRua.MAP_ID, false);
        this.sanh = sanh;
        this.banDo = new ChickenQuanLyBanDo(CauHinhBossRua.MAP_ID);
        this.boHenGio = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "boss-rua-P4-"
                    + (sanh == null ? -1 : sanh.getMaBan() & 0xFF));
            thread.setDaemon(true);
            return thread;
        });
        this.taoNguoiChoi(sanh);
        this.taoBoss();
        this.khoiTaoKyNangAVG();
        this.dangKyNguoiChoiTrongTran();
    }

    public static BossRua tao(SanhChoBoss sanh) {
        if (sanh == null || (sanh.getMaBanDo() & 0xFF) != CauHinhBossRua.MAP_ID) {
            return null;
        }
        return new BossRua(sanh);
    }

    @Override
    public synchronized void batDau() throws IOException {
        if (this.daBatDau || this.daKetThuc) {
            return;
        }
        this.daBatDau = true;
        for (CauHinhBossRua.CauHinh cauHinh : CauHinhBossRua.layTatCa()) {
            System.out.println("[BOSS RUA][TAO_BOSS] slot="
                    + (cauHinh.getSlot() & 0xFF)
                    + " ten=" + cauHinh.getTen()
                    + " x=" + cauHinh.getX()
                    + " y=" + cauHinh.getY()
                    + " hp=" + CauHinhBossRua.MAU_BOSS
                    + " loai=" + cauHinh.getLoai()
                    + " sungPart=" + cauHinh.getVuKhi());
        }
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiBatDauDau(
                    this.banDo.layMaBanDo(), this.chienBinhs, this.banDo.layMaNen());
            for (CauHinhBossRua.CauHinh cauHinh : CauHinhBossRua.layTatCa()) {
                nguoiNhan.nguoiChoi.dichVu.guiTaoBossRua(
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
                        CauHinhBossRua.MAU_BOSS
                );
            }
        }
        System.out.println("[BOSS RUA][BAT_DAU] P4-"
                + (this.sanh.getMaBan() & 0xFF)
                + " map=50 players=" + this.demNguoiChoiSong()
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
        if (ChieuBossRua.dangBiDaRuaGhim(chienBinh, this.banDo)) {
            nguoiChoi.dichVu.guiDiChuyenDau(
                    chienBinh.chiSo, chienBinh.x, chienBinh.y);
            return;
        }
        short xCu = chienBinh.x;
        short yCu = chienBinh.y;
        ChickenDiChuyenServer.KetQua ketQua = ChickenDiChuyenServer.xuLy(
                this.banDo, chienBinh.x, chienBinh.y,
                yeuCau.getX(), yeuCau.getY(),
                chienBinh.quangDuongDiChuyenConLai,
                ChickenCoCheBayAVG.coTheBay(chienBinh));
        chienBinh.x = ketQua.getX();
        chienBinh.y = ketQua.getY();
        chienBinh.quangDuongDiChuyenConLai = ketQua.getConLai();
        if (chienBinh.x != xCu || chienBinh.y != yCu) {
            this.phatDiChuyenNguoiChoi(chienBinh);
        } else {
            nguoiChoi.dichVu.guiDiChuyenDau(
                    chienBinh.chiSo, chienBinh.x, chienBinh.y);
        }
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
        if (ChieuBossRua.dangBiDaRuaGhim(chienBinh, this.banDo)) {
            if (ChickenYeuCauToaDoServer.doc(ms) != null) {
                this.phatDiChuyenNguoiChoi(chienBinh);
            }
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
        if (com.chicken.avg.ChickenCoCheHulk.laHulk(shooter.avenger)
                && ChieuBossRua.dangBiDaRuaGhim(shooter, this.banDo)) {
            this.phatDiChuyenNguoiChoi(shooter);
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
            int napDanSauBan = this.layNapDanSauBanNguoiChoi(shooter);
            this.choServerKetThucPhatBan(
                    shooter, napDanSauBan, thoiGianHoatAnhMs);
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
                < this.thoiDiemSomNhatXacNhanKetThucBan) {
            return;
        }
        if (!this.tatCaNguoiChoiDaXacNhanHoatAnh()) {
            return;
        }
        System.out.println("[BOSS RUA][CMD79_HIEN_THI_XONG] slotBan="
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
     * Client không gửi CMD 23 cho đạn của BigBoss. Khi viên cuối biến mất,
     * BM.removeBullet gửi CMD 79: [soDiem][x:int,y:int]... Dữ liệu tọa độ bị
     * bỏ qua hoàn toàn; packet chỉ là tín hiệu animation đã kết thúc.
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
        System.out.println("[BOSS RUA][NGUOI_CHOI_ROI] player="
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
        if (!this.coTheNhanLenhNguoiChoi(chienBinh)) {
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
        for (CauHinhBossRua.CauHinh cauHinh : CauHinhBossRua.layTatCa()) {
            int slot = cauHinh.getSlot() & 0xFF;
            int tanCong = CauHinhBossRua.layTanCongTheoSung(cauHinh.getVuKhi());
            this.chienBinhs[slot] = new ChickenChienBinh(
                    cauHinh.getSlot(), cauHinh.getId(), cauHinh.getX(), cauHinh.getY(),
                    cauHinh.getTen(), cauHinh.getVuKhi(),
                    CauHinhBossRua.MAU_BOSS, tanCong, 0);
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
                        return BossRua.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossRua.this.luotHienTai;
                    }

                    @Override
                    public void guiHoatAnhMuiTen(
                            ChickenChienBinh hawk,
                            short goc,
                            ChickenHoatAnhHawk.DuongDan duongDan
                    ) throws IOException {
                        BossRua.this.phatHoatAnhMuiTenHawkBoss(
                                hawk, goc, duongDan);
                    }

                    @Override
                    public void gaySatThuong(
                            ChickenChienBinh mucTieu,
                            int satThuong
                    ) throws IOException {
                        BossRua.this.gaySatThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        BossRua.this.sangLuotSauKyNangAVG();
                    }
                }
        );

        this.kyNangThorBoss = new ChickenKyNangDacBietThor(
                this.chienBinhs,
                this.banDo,
                new ChickenKyNangDacBietThor.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossRua.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossRua.this.luotHienTai;
                    }

                    @Override
                    public void guiTiaSet(
                            ChickenChienBinh thor,
                            byte loaiHieuUng,
                            short[] cacX,
                            short[] cacY
                    ) throws IOException {
                        BossRua.this.phatTiaSetThorBoss(
                                thor, loaiHieuUng, cacX, cacY);
                    }

                    @Override
                    public void gaySatThuong(
                            ChickenChienBinh mucTieu,
                            int satThuong
                    ) throws IOException {
                        BossRua.this.gaySatThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        BossRua.this.sangLuotSauKyNangAVG();
                    }
                }
        );

        this.kyNangLokiBoss = new ChickenKyNangDacBietLoki(
                this.chienBinhs,
                new ChickenKyNangDacBietLoki.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossRua.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossRua.this.luotHienTai;
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
                        BossRua.this.phatBienHinhLokiBoss(loki, mucTieu);
                    }

                    @Override
                    public void capNhatMau(ChickenChienBinh loki)
                            throws IOException {
                        BossRua.this.phatCapNhatMau(loki);
                    }
                }
        );

        this.kyNangUltronBoss = new ChickenKyNangDacBietUltron(
                new ChickenKyNangDacBietUltron.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return BossRua.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return BossRua.this.luotHienTai;
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
        if (slotTiep < SO_SLOT_NGUOI_CHOI
                && this.xuLyDocDauLuot(hienTai)) {
            if (!this.daKetThuc) {
                this.chuyenSangLuotTiepTheo(slotTiep);
            }
            return;
        }
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
            System.out.println("[BOSS RUA][HET_25_GIAY] slot=" + slot
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
        return CauHinhBossRua.NAP_DAN_SAU_HANH_DONG;
    }

    private void thucHienLuotBoss(int slot, long phien) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot) {
                return;
            }
            ChickenChienBinh boss = this.chienBinhs[slot];
            if (boss == null || boss.chet) {
                this.sangLuotSauBoss(slot, phien, 0);
                return;
            }
            ChickenChienBinh mucTieu = DiChuyenBossRua.timNguoiSongGanNhat(
                    boss, this.chienBinhs);
            int slotMucTieu = mucTieu == null ? -1 : mucTieu.chiSo & 0xFF;
            int huongX = mucTieu == null ? 0 : DiChuyenBossRua.layHuongX(boss, mucTieu);
            int soLuot = this.tangVaLaySoLuotRua(slot);
            ChieuBossRua.LoaiChieu chieu = ChieuBossRua.chonChoLuot(soLuot);
            System.out.println("[BOSS RUA][CHON_CHIEU] slot=" + slot
                    + " luotRua=" + soLuot + " chieu=" + chieu);
            this.thucHienRua(
                    boss, slot, phien,
                    CauHinhBossRua.QUANG_DUONG_MOI_LUOT,
                    slotMucTieu, huongX, chieu, false);
        }
    }

    private void thucHienRua(
            ChickenChienBinh boss,
            int slot,
            long phien,
            int quangDuongConLai,
            int slotMucTieu,
            int huongXKhoa
    ) {
        this.thucHienRua(
                boss, slot, phien, quangDuongConLai,
                slotMucTieu, huongXKhoa,
                ChieuBossRua.LoaiChieu.BAN_THUONG, false);
    }

    private void thucHienRua(
            ChickenChienBinh boss,
            int slot,
            long phien,
            int quangDuongConLai,
            int slotMucTieu,
            int huongXKhoa,
            ChieuBossRua.LoaiChieu chieu,
            boolean daDiChuyen
    ) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien || boss.chet) {
                this.sangLuotSauBoss(slot, phien, 0);
                return;
            }
            ChickenChienBinh mucTieu = this.layNguoiChoiSongTheoSlot(slotMucTieu);
            if (mucTieu == null) {
                mucTieu = DiChuyenBossRua.timNguoiSongGanNhat(boss, this.chienBinhs);
                slotMucTieu = mucTieu == null ? -1 : mucTieu.chiSo & 0xFF;
                huongXKhoa = mucTieu == null ? 0 : DiChuyenBossRua.layHuongX(boss, mucTieu);
            }
            if (mucTieu == null) {
                this.sangLuotSauBoss(slot, phien, CauHinhBossRua.NAP_DAN_SAU_HANH_DONG);
                return;
            }

            /*
             * Server tính toàn bộ đích di chuyển ngay trong một lượt xử lý.
             * Client chỉ cần một CMD21 với đích cuối và tự chạy animation.
             * Không hẹn giờ 70 ms cho từng bước 9 px vì các bước trung gian
             * không hề được gửi cho client và chỉ tạo khoảng đứng im giả.
             */
            short xTruocDiChuyen = boss.x;
            short yTruocDiChuyen = boss.y;
            boolean coDiChuyen = daDiChuyen;
            int conLai = Math.max(0, quangDuongConLai);
            int baoHiem = 0;
            while (conLai > 0 && baoHiem++ < 256) {
                short[] buoc = DiChuyenBossRua.tinhBuocTiepTheo(
                        boss, mucTieu, conLai, huongXKhoa, this.banDo);
                int daDi = (int) Math.round(
                        Math.hypot(buoc[0] - boss.x, buoc[1] - boss.y));
                if (daDi <= 0) {
                    break;
                }
                int daChayNgang = Math.abs(buoc[0] - boss.x);
                boss.x = kepShort(
                        buoc[0],
                        DiChuyenBossRua.NUA_RONG_HITBOX,
                        this.banDo.getWidth() - 1
                                - DiChuyenBossRua.NUA_RONG_HITBOX);
                boss.y = kepShort(
                        buoc[1], 0, this.banDo.getHeight() + 32);
                coDiChuyen = true;
                if (DiChuyenBossRua.daRoiKhoiMap(boss, this.banDo)) {
                    this.ketThucRuaRoiKhoiMap(boss, slot, phien);
                    return;
                }
                conLai = Math.max(0, conLai - daChayNgang);
            }

            while (baoHiem++ < 512) {
                if (DiChuyenBossRua.daRoiKhoiMap(boss, this.banDo)) {
                    this.ketThucRuaRoiKhoiMap(boss, slot, phien);
                    return;
                }
                short[] buocRoi = DiChuyenBossRua.tinhBuocRoiThangDung(
                        boss, this.banDo);
                if (buocRoi[1] == boss.y) {
                    break;
                }
                boss.y = kepShort(
                        buocRoi[1], 0, this.banDo.getHeight() + 40);
                coDiChuyen = true;
            }

            boss.y = DiChuyenBossRua.chuanHoaYChanClient(
                    boss.x, boss.y, this.banDo);
            this.phatDiChuyenBossNeuCan(boss, coDiChuyen);
            this.tungChieuRuaSauDiChuyen(
                    boss, mucTieu, slot, phien, chieu,
                    coDiChuyen, xTruocDiChuyen, yTruocDiChuyen);
        }
    }

    private void tungChieuRuaSauDiChuyen(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int slot,
            long phien,
            ChieuBossRua.LoaiChieu chieu,
            boolean coDiChuyen,
            short xCu,
            short yCu
    ) {
        long treMs = coDiChuyen
                ? DiChuyenBossRua.tinhThoiGianHoatAnhMs(
                        xCu, yCu, boss.x, boss.y)
                : 0L;
        if (treMs <= 0L) {
            this.tungChieuRua(boss, mucTieu, slot, phien, chieu);
            return;
        }
        System.out.println("[BOSS RUA][CHO_DI_CHUYEN_XONG] slot=" + slot
                + " from=" + xCu + "," + yCu
                + " to=" + boss.x + "," + boss.y
                + " delayMs=" + treMs);
        short xDich = boss.x;
        short yDich = boss.y;
        this.boHenGio.schedule(
                () -> this.hoanTatDiChuyenVaTungChieuRua(
                        boss, mucTieu, slot, phien, chieu, xDich, yDich),
                treMs,
                TimeUnit.MILLISECONDS
        );
    }

    private void hoanTatDiChuyenVaTungChieuRua(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int slot,
            long phien,
            ChieuBossRua.LoaiChieu chieu,
            short xDich,
            short yDich
    ) {
        synchronized (this) {
            if (!this.conHieuLucLuotBossRua(slot, phien, boss)
                    || boss.x != xDich || boss.y != yDich) {
                return;
            }
            try {
                this.phatChotDiChuyenBoss(boss);
            } catch (IOException loi) {
                System.err.println("[BOSS RUA][LOI_CHOT_DI_CHUYEN] slot="
                        + slot + " phien=" + phien + " loi=" + loi.getMessage());
            }
            this.tungChieuRua(boss, mucTieu, slot, phien, chieu);
        }
    }

    private boolean conHieuLucLuotBossRua(
            int slot,
            long phien,
            ChickenChienBinh boss
    ) {
        return !this.daKetThuc
                && this.maPhienLuot == phien
                && (this.luotHienTai & 0xFF) == slot
                && slot >= 0
                && slot < this.chienBinhs.length
                && this.chienBinhs[slot] == boss
                && boss != null
                && !boss.chet
                && boss.hp > 0;
    }

    private void ketThucRuaRoiKhoiMap(
            ChickenChienBinh boss,
            int slot,
            long phien
    ) {
        try {
            this.phatDiChuyenBoss(boss);
            boss.hp = 0;
            boss.chet = true;
            this.phatCapNhatMau(boss);
        } catch (IOException ignored) {
        }
        this.sangLuotSauBoss(slot, phien, 0);
    }

    private void phatDiChuyenBossNeuCan(
            ChickenChienBinh boss,
            boolean daDiChuyen
    ) {
        if (!daDiChuyen) {
            return;
        }
        try {
            this.phatDiChuyenBoss(boss);
        } catch (IOException ignored) {
        }
    }

    private int tangVaLaySoLuotRua(int slot) {
        if (slot < 0 || slot >= this.soLuotRua.length) {
            return 1;
        }
        if (this.soLuotRua[slot] < Integer.MAX_VALUE) {
            this.soLuotRua[slot]++;
        }
        return Math.max(1, this.soLuotRua[slot]);
    }

    private void tungChieuRua(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int slot,
            long phien,
            ChieuBossRua.LoaiChieu chieu
    ) {
        if (chieu == ChieuBossRua.LoaiChieu.DAM_DA) {
            this.damDaRua(boss, mucTieu, slot, phien);
        } else {
            this.banDanRua(boss, mucTieu, slot, phien);
        }
    }

    private void damDaRua(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int slot,
            long phien
    ) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot
                    || boss == null || boss.chet) {
                return;
            }
            if (mucTieu == null || mucTieu.chet || mucTieu.hp <= 0
                    || !mucTieu.coPhien()) {
                mucTieu = DiChuyenBossRua.timNguoiSongGanNhat(
                        boss, this.chienBinhs);
            }
            if (mucTieu == null) {
                this.sangLuotSauBoss(
                        slot, phien, CauHinhBossRua.NAP_DAN_SAU_HANH_DONG);
                return;
            }

            short xVaCham = mucTieu.x;
            short yVaCham = mucTieu.y;
            if (!this.banDo.themDaRua(xVaCham, yVaCham)) {
                System.err.println("[BOSS RUA][DAM_DA_BO_QUA] "
                        + "khong nap duoc res/icon/hole/bullrua.png");
                this.banDanRua(boss, mucTieu, slot, phien);
                return;
            }

            int satThuong = ChieuBossRua.tinhSatThuongDamDa(boss, mucTieu);
            satThuong = ChickenMayMan.batDau(boss, this.chienBinhs)
                    .apDung(mucTieu, satThuong);
            this.phatDamDaRua(
                    boss, xVaCham, yVaCham,
                    mucTieu.chiSo, mucTieu.x, mucTieu.y);
            ChieuBossRua.ghimMucTieu(mucTieu, xVaCham, yVaCham);
            try {
                this.gaySatThuong(mucTieu, satThuong);
            } catch (IOException ignored) {
            }
            System.out.println("[BOSS RUA][DAM_DA] target=" + mucTieu.ten
                    + " damage=" + satThuong
                    + " rock=" + xVaCham + "," + yVaCham
                    + " pinned=true");
            if (!this.daKetThuc) {
                this.sangLuotSauBoss(
                        slot, phien, CauHinhBossRua.NAP_DAN_SAU_HANH_DONG);
            }
        }
    }

    private void phatDamDaRua(
            ChickenChienBinh boss,
            short xVaCham,
            short yVaCham,
            byte slotMucTieu,
            short xMucTieuMoi,
            short yMucTieuMoi
    ) {
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            try {
                nguoiNhan.nguoiChoi.dichVu.guiDamDaBossRua(
                        boss.chiSo,
                        xVaCham,
                        yVaCham,
                        slotMucTieu,
                        xMucTieuMoi,
                        yMucTieuMoi
                );
            } catch (IOException ignored) {
            }
        }
    }

    private ChickenChienBinh layNguoiChoiSongTheoSlot(int slot) {
        if (slot < 0 || slot >= SO_SLOT_NGUOI_CHOI) {
            return null;
        }
        ChickenChienBinh nguoiChoi = this.chienBinhs[slot];
        if (nguoiChoi == null || nguoiChoi.chet || nguoiChoi.hp <= 0
                || !nguoiChoi.coPhien()) {
            return null;
        }
        return nguoiChoi;
    }

    private void ruaTanCong(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int slot,
            long phien
    ) {
        ChickenKetQuaDan ketQua = null;
        ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(boss, this.chienBinhs);
        try {
            ketQua = BossRuaRongTanCong.taoPhatBanRua(
                    boss,
                    mucTieu,
                    this.chienBinhs,
                    this.banDo,
                    this.layWindXChoChienBinh(boss),
                    this.layWindYChoChienBinh(boss)
            );
            // Gửi đường đạn trước khi sát thương va chạm có thể kết thúc trận.
            // Nếu không, ở trận solo client chỉ thấy Rùa lao vào mà không thấy bắn.
            this.phatBan(boss, ketQua, (byte) 1);
            System.out.println("[BOSS RUA][BAN_DAN_SAU_VA_CHAM] target=" + mucTieu.ten
                    + " damage=" + ketQua.satThuong
                    + " bulletPart=" + boss.maVuKhi);
        } catch (IOException | RuntimeException loi) {
            System.err.println("[BOSS RUA][LOI_BAN_DAN_SAU_VA_CHAM] "
                    + loi.getClass().getSimpleName() + ": " + loi.getMessage());
            loi.printStackTrace(System.err);
        }
        try {
            int satThuongCham = phienMayMan.apDung(
                    mucTieu, CauHinhBossRua.SAT_THUONG_CHAM);
            this.gaySatThuong(mucTieu, satThuongCham);
            System.out.println("[BOSS RUA][TAN_CONG] target=" + mucTieu.ten
                    + " damage=" + satThuongCham);
        } catch (IOException ignored) {
        }
        if (!this.daKetThuc && ketQua != null && ketQua.mucTieu != null
                && !ketQua.mucTieu.chet && ketQua.satThuong > 0) {
            try {
                int satThuongDan = phienMayMan.apDung(
                        ketQua.mucTieu, ketQua.satThuong);
                this.gaySatThuong(ketQua.mucTieu, satThuongDan);
            } catch (IOException ignored) {
            }
        }
        if (!this.daKetThuc) {
            this.choServerKetThucPhatBan(
                    boss,
                    CauHinhBossRua.NAP_DAN_SAU_HANH_DONG,
                    ChickenThoiGianHoatAnhDan.tinh(ketQua));
        }
    }

    /** Pha bắn luôn diễn ra sau khi Rùa kết thúc di chuyển/va chạm. */
    private void banDanRua(
            ChickenChienBinh boss,
            ChickenChienBinh mucTieu,
            int slot,
            long phien
    ) {
        synchronized (this) {
            if (this.daKetThuc || this.maPhienLuot != phien
                    || (this.luotHienTai & 0xFF) != slot
                    || boss == null || boss.chet) {
                return;
            }
            if (mucTieu == null || mucTieu.chet || mucTieu.hp <= 0
                    || !mucTieu.coPhien()) {
                mucTieu = DiChuyenBossRua.timNguoiSongGanNhat(boss, this.chienBinhs);
            }
            if (mucTieu == null) {
                this.sangLuotSauBoss(
                        slot, phien, CauHinhBossRua.NAP_DAN_SAU_HANH_DONG);
                return;
            }

            ChickenKetQuaDan ketQua = null;
            try {
                ketQua = BossRuaRongTanCong.taoPhatBanRua(
                        boss,
                        mucTieu,
                        this.chienBinhs,
                        this.banDo,
                        this.layWindXChoChienBinh(boss),
                        this.layWindYChoChienBinh(boss)
                );
                ChickenMayMan.PhienTanCong phienMayMan =
                        ChickenMayMan.batDau(boss, this.chienBinhs);
                this.phatBan(boss, ketQua, (byte) 1);
                if (ketQua.mucTieu != null && ketQua.satThuong > 0) {
                    int satThuongDan = phienMayMan.apDung(
                            ketQua.mucTieu, ketQua.satThuong);
                    this.gaySatThuong(ketQua.mucTieu, satThuongDan);
                    if (DocBossRua.apDung(
                            boss, ketQua.mucTieu, satThuongDan)) {
                        this.phatHieuUngDoc(
                                boss.chiSo, ketQua.mucTieu.chiSo);
                        System.out.println("[BOSS RUA][NHIEM_DOC] target="
                                + ketQua.mucTieu.ten
                                + " damageMoiLuot="
                                + ketQua.mucTieu.satThuongDocBossRuaMoiLuot);
                    }
                }
                System.out.println("[BOSS RUA][BAN_DAN] target=" + mucTieu.ten
                        + " damage=" + ketQua.satThuong
                        + " bulletType=" + ketQua.loaiDan);
            } catch (IOException | RuntimeException loi) {
                System.err.println("[BOSS RUA][LOI_BAN_DAN] "
                        + loi.getClass().getSimpleName() + ": " + loi.getMessage());
                loi.printStackTrace(System.err);
            } finally {
                // Một packet lỗi không được phép làm chết luồng AI và khóa vĩnh viễn lượt đấu.
                if (!this.daKetThuc) {
                    this.choServerKetThucPhatBan(
                            boss,
                            CauHinhBossRua.NAP_DAN_SAU_HANH_DONG,
                            ChickenThoiGianHoatAnhDan.tinh(ketQua));
                }
            }
        }
    }

    private boolean xuLyDocDauLuot(ChickenChienBinh mucTieu)
            throws IOException {
        int satThuong = DocBossRua.laySatThuongDauLuot(mucTieu);
        if (satThuong <= 0) {
            return false;
        }
        int slotNguonDoc = mucTieu.slotGayDocBossRua & 0xFF;
        ChickenChienBinh nguonDoc =
                slotNguonDoc < this.chienBinhs.length
                        ? this.chienBinhs[slotNguonDoc] : null;
        ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(nguonDoc, this.chienBinhs);
        int satThuongMayMan = phienMayMan.apDung(mucTieu, satThuong);
        this.phatHieuUngDoc(
                mucTieu.slotGayDocBossRua, mucTieu.chiSo);
        int hpCu = mucTieu.hp;
        this.gaySatThuong(mucTieu, satThuongMayMan);
        System.out.println("[BOSS RUA][DOC_DAU_LUOT] target="
                + mucTieu.ten
                + " damage=" + Math.max(0, hpCu - mucTieu.hp)
                + " hpConLai=" + mucTieu.hp);
        return mucTieu.chet || this.daKetThuc;
    }

    private void phatHieuUngDoc(byte slotNguon, byte slotMucTieu)
            throws IOException {
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiTrungDoc(
                    slotNguon, slotMucTieu);
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

        System.out.println("[BOSS RUA][ULTRON_X3] player="
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
                    System.err.println("[BOSS RUA][LOI_KET_THUC] "
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
        System.out.println("[BOSS RUA][KET_THUC] ketQua="
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
                System.out.println("[BOSS RUA][ANIMATION_DAN_TIMEOUT] slot="
                        + slot + " phien=" + phien
                        + " serverChuyenLuot=true");
            }
            try {
                this.chuyenSangLuotTiepTheo(slot);
            } catch (IOException ignored) {
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
        for (int i = CauHinhBossRua.SLOT_BOSS_DAU;
                i <= CauHinhBossRua.SLOT_BOSS_CUOI; i++) {
            ChickenChienBinh boss = this.chienBinhs[i];
            if (boss != null && !boss.chet && boss.hp > 0) {
                dem++;
            }
        }
        return dem;
    }

    private void phatLuot(ChickenChienBinh hienTai) throws IOException {
        byte giay = (byte) GIAY_MOI_LUOT;
        int slot = hienTai.chiSo & 0xFF;
        this.dongMenuKyNangKhiDoiLuot(this.chienBinhs);
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiGio(
                    this.gioHienTai.getWindX(), this.gioHienTai.getWindY());
            nguoiNhan.nguoiChoi.dichVu.guiLuotBossBaoVayTiep(
                    hienTai.chiSo, hienTai.x, hienTai.y,
                    this.chienBinhs, this.napDan,
                    this.thuTuHanhDongNapDan, giay);
        }
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
        System.out.println("[BOSS RUA][LUOT] slot=" + slot
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
        System.out.println("[BOSS RUA][NAP_DAN] slot=" + slot
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
            nguoiNhan.nguoiChoi.dichVu.guiDiChuyenBossRua(
                    boss.chiSo, boss.x, boss.y);
        }
    }

    private void phatChotDiChuyenBoss(ChickenChienBinh boss) throws IOException {
        for (ChickenChienBinh nguoiNhan : this.nguoiChoiConPhien()) {
            nguoiNhan.nguoiChoi.dichVu.guiHoanTatDiChuyenBossRua(
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
            public int getWidth() { return BossRua.this.banDo.getWidth(); }
            @Override
            public int getHeight() { return BossRua.this.banDo.getHeight(); }
            @Override
            public boolean coVaCham(short x, short y) {
                return BossRua.this.banDo.coVaCham(x, y);
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
