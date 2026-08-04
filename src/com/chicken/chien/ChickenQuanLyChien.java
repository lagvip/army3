package com.chicken.chien;

import com.chicken.chiso.ChickenKichThuocNhanVat;
import com.chicken.chiso.ChickenHieuUngDongDoi;
import com.chicken.avg.ChickenKyNangDacBietHawk;
import com.chicken.avg.ChickenKyNangDacBietThor;
import com.chicken.avg.ChickenKyNangDacBietLoki;
import com.chicken.avg.ChickenKyNangDacBietUltron;
import com.chicken.avg.ChickenKyNangDacBietIronMan;
import com.chicken.avg.ChickenCongThucBanUltron;
import com.chicken.avg.ChickenGocBanUltron;
import com.chicken.avg.ChickenHoatAnhHawk;
import com.chicken.avg.ChickenTiaLaserIronMan;
import com.chicken.avg.ChickenCoCheBayAVG;
import com.chicken.avg.ChickenCoCheHulk;
import com.chicken.avg.ChickenThanhDiChuyenAVG;

import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.gio.ChickenHeThongGio;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.phong.ChickenChoDau;
import com.chicken.loi.ChickenQuanLyMayChu;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;

public class ChickenQuanLyChien {
    /** Lưu trận theo mã người chơi thật, không phụ thuộc cùng một instance object. */
    private static final Map<Integer, ChickenQuanLyChien> TRAN_DAU_THEO_NGUOI_CHOI = new ConcurrentHashMap<>();

    public static ChickenQuanLyChien timTranDauCuaNguoiChoi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return null;
        }
        return TRAN_DAU_THEO_NGUOI_CHOI.get(nguoiChoi.ma);
    }

    /** Giữ tên cũ cho các luồng trận khác đang gọi, nhưng dùng chung bảng theo mã người chơi. */
    public static ChickenQuanLyChien layTranDangHoatDong(ChickenNguoiChoi nguoiChoi) {
        return timTranDauCuaNguoiChoi(nguoiChoi);
    }
    private static final ScheduledExecutorService BOT_EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "chicken-chien-bot");
        thread.setDaemon(true);
        return thread;
    });
    private static final int MAX_FIGHTERS = 8;
    private static final int TURN_SECONDS = 8;
    /** Plugin PC coi điểm chạm đất trong vòng 24 px quanh chân là bắn trúng dưới chân. */
    private final ChickenChoDau wait;
    private final ChickenChienBinh[] chienBinhs = new ChickenChienBinh[MAX_FIGHTERS];
    /** Dong ho nap dan authoritative; client chi nhan ket qua chon luot. */
    private final int[] napDan = new int[MAX_FIGHTERS];
    private final long[] thuTuHanhDongNapDan = new long[MAX_FIGHTERS];
    private long boDemThuTuHanhDongNapDan;
    private final ChickenQuanLyBanDo map;
    private byte luotHienTai = -1;
    private boolean daKetThuc;
    private ScheduledFuture<?> tacVuBot;
    private long hanLuot;
    /**
     * Mot trang thai gio duy nhat cho moi tran, ke ca cac lop boss ke thua.
     * Lop con khong duoc shadow field nay de item Ngung gio khong bi lech mode.
     */
    protected ChickenHeThongGio.TrangThaiGio gioHienTai =
            ChickenHeThongGio.khongGio();
    private final ChickenKyNangDacBietHawk kyNangHawk;
    private final ChickenKyNangDacBietThor kyNangThor;
    private final ChickenKyNangDacBietLoki kyNangLoki;
    private final ChickenKyNangDacBietUltron kyNangUltron;
    private final ChickenKyNangDacBietIronMan kyNangIronMan;
    /** Trang thai ba LAN BAN lien tiep cua skill Ultron trong PvP. */
    private ChickenChienBinh ultronX3NguoiBan;
    private ChickenKetQuaDan ultronX3KetQua;
    private ChickenMayMan.PhienTanCong ultronX3PhienMayMan;
    private int ultronX3SoLanDaGui;
    private long ultronX3MaLoat;
    private long ultronX3XacNhanSomNhatMs;
    /** Giu lai CMD79 den som de client goc khong phai gui lai lan hai. */
    private ScheduledFuture<?> ultronX3TacVuXacNhanSom;
    private ScheduledFuture<?> ultronX3TacVuHetHan;
    /**
     * Mot phat ban PvP chi duoc chot sau khi animation da ket thuc. CMD79 chi
     * la tin hieu hien thi; damage va luot tiep theo van do server giu san.
     */
    private PhatBanDangCho phatBanDangCho;
    private long maPhatBanTiepTheo;

    public ChickenQuanLyChien(
            ChickenChoDau wait,
            ChickenNguoiChoi[] nguoiChois,
            byte maBanDo
    ) {
        this(wait, nguoiChois, maBanDo, true);
    }

    /**
     * Trận boss truyền {@code false} rồi chỉ đăng ký sau khi constructor lớp
     * con đã hoàn tất. Như vậy packet đến đồng thời không thể nhìn thấy một
     * instance boss còn thiếu bản đồ, scheduler hoặc bộ kỹ năng.
     */
    protected ChickenQuanLyChien(
            ChickenChoDau wait,
            ChickenNguoiChoi[] nguoiChois,
            byte maBanDo,
            boolean dangKyNgay
    ) {
        this.wait = wait;
        this.map = new ChickenQuanLyBanDo(maBanDo);
        for (int i = 0; i < nguoiChois.length && i < this.chienBinhs.length; i++) {
            ChickenNguoiChoi nguoiChoi = nguoiChois[i];
            if (nguoiChoi == null) {
                continue;
            }
            short x = this.map.laySinhX(i);
            short y = this.map.laySinhY(i);
            nguoiChoi.khoiTaoPowTrongTran();
            this.chienBinhs[i] = new ChickenChienBinh(nguoiChoi, (byte)i, x, y);
        }
        ChickenHieuUngDongDoi.apDungChoPvpTheoPhe(this.chienBinhs);
        this.kyNangHawk = new ChickenKyNangDacBietHawk(this.chienBinhs, this.map,
                new ChickenKyNangDacBietHawk.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return ChickenQuanLyChien.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return ChickenQuanLyChien.this.luotHienTai;
                    }

                    @Override
                    public void guiHoatAnhMuiTen(
                            ChickenChienBinh hawk,
                            short goc,
                            ChickenHoatAnhHawk.DuongDan duongDan
                    ) throws IOException {
                        ChickenQuanLyChien.this.phatHoatAnhMuiTenHawk(
                                hawk,
                                goc,
                                duongDan
                        );
                    }

                    @Override
                    public void gaySatThuong(ChickenChienBinh mucTieu, int satThuong) throws IOException {
                        ChickenQuanLyChien.this.satThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        ChickenQuanLyChien.this.sangLuot();
                    }
                });
        this.kyNangThor = new ChickenKyNangDacBietThor(this.chienBinhs, this.map,
                new ChickenKyNangDacBietThor.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return ChickenQuanLyChien.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return ChickenQuanLyChien.this.luotHienTai;
                    }

                    @Override
                    public void guiTiaSet(
                            ChickenChienBinh thor,
                            byte loaiHieuUng,
                            short[] cacX,
                            short[] cacY
                    ) throws IOException {
                        ChickenQuanLyChien.this.phatTiaSetThor(
                                thor, loaiHieuUng, cacX, cacY);
                    }

                    @Override
                    public void gaySatThuong(ChickenChienBinh mucTieu, int satThuong) throws IOException {
                        ChickenQuanLyChien.this.satThuong(mucTieu, satThuong);
                    }

                    @Override
                    public void sangLuot() throws IOException {
                        ChickenQuanLyChien.this.sangLuot();
                    }
                });
        this.kyNangLoki = new ChickenKyNangDacBietLoki(this.chienBinhs,
                new ChickenKyNangDacBietLoki.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return ChickenQuanLyChien.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return ChickenQuanLyChien.this.luotHienTai;
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
                        ChickenQuanLyChien.this.phatBienHinhLoki(loki, mucTieu);
                    }

                    @Override
                    public void capNhatMau(ChickenChienBinh loki) throws IOException {
                        ChickenQuanLyChien.this.phatCapNhatMau(loki);
                    }

                });
        this.kyNangUltron = new ChickenKyNangDacBietUltron(
                new ChickenKyNangDacBietUltron.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return ChickenQuanLyChien.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return ChickenQuanLyChien.this.luotHienTai;
                    }

                    @Override
                    public void guiMenuUltron(ChickenChienBinh ultron) {
                        ultron.nguoiChoi.dichVu.guiChonKyNangUltron();
                    }
                }
        );
        this.kyNangIronMan = new ChickenKyNangDacBietIronMan(
                new ChickenKyNangDacBietIronMan.DieuKhienTranDau() {
                    @Override
                    public boolean daKetThuc() {
                        return ChickenQuanLyChien.this.daKetThuc;
                    }

                    @Override
                    public byte luotHienTai() {
                        return ChickenQuanLyChien.this.luotHienTai;
                    }

                    @Override
                    public void guiMenuIronMan(ChickenChienBinh ironMan) {
                        ironMan.nguoiChoi.dichVu.guiChonKyNangIronMan();
                    }
                }
        );
        if (dangKyNgay) {
            this.dangKyNguoiChoiTrongTran();
        }
    }

    /**
     * Công bố instance đã khởi tạo hoàn chỉnh cho router packet.
     */
    protected final void dangKyNguoiChoiTrongTran() {
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.nguoiChoi != null) {
                TRAN_DAU_THEO_NGUOI_CHOI.put(chienBinh.nguoiChoi.ma, this);
            }
        }
    }

    public void themBot(byte chiSo, String ten, short maVuKhi, byte avenger) {
        if (chiSo < 0 || chiSo >= this.chienBinhs.length || this.chienBinhs[chiSo] != null) {
            return;
        }
        this.chienBinhs[chiSo] = new ChickenChienBinh(chiSo, this.map.laySinhX(chiSo), this.map.laySinhY(chiSo), ten, maVuKhi, avenger);
    }

    public void batDau() throws IOException {
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiBatDauDau(this.map.layMaBanDo(), this.chienBinhs, this.map.layMaNen());
                chienBinh.nguoiChoi.dichVu.guiBaloTrongTran(chienBinh);
            }
        }
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiHienManHinhGameLuyenTap();
            }
        }
        this.dongBoTatCaPow(this.chienBinhs);
        this.luotHienTai = this.nguoiSongTiepTu((byte)-1);
        this.sendNextTurn();
        this.lapLichBotBan();
    }

    public synchronized void diChuyen(
            ChickenNguoiChoi nguoiChoi,
            ChickenTinNhan ms
    ) throws IOException {
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh == null || chienBinh.chet
                || chienBinh.chiSo != this.luotHienTai
                || this.daKetThuc || this.phatBanDangCho != null) {
            return;
        }
        ChickenYeuCauToaDoServer.ToaDo yeuCau =
                ChickenYeuCauToaDoServer.doc(ms);
        if (yeuCau == null) {
            return;
        }
        ChickenDiChuyenServer.KetQua ketQuaDiChuyen =
                ChickenDiChuyenServer.xuLy(
                        this.map,
                        chienBinh.x,
                        chienBinh.y,
                        yeuCau.getX(),
                        yeuCau.getY(),
                        chienBinh.quangDuongDiChuyenConLai,
                        ChickenCoCheBayAVG.coTheBay(chienBinh)
                );
        chienBinh.x = ketQuaDiChuyen.getX();
        chienBinh.y = ketQuaDiChuyen.getY();
        chienBinh.quangDuongDiChuyenConLai = ketQuaDiChuyen.getConLai();
        this.phatDiChuyen(chienBinh);
    }

    public synchronized void capNhatXY(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh == null || chienBinh.chet || this.daKetThuc
                || this.phatBanDangCho != null) {
            return;
        }
        if (!ChickenYeuCauToaDoServer.choPhepDongBo(
                chienBinh, System.currentTimeMillis())) {
            return;
        }
        ChickenYeuCauToaDoServer.KetQuaDongBo ketQua =
                ChickenYeuCauToaDoServer.dongBoThuDong(
                        ms, this.map, chienBinh.x, chienBinh.y,
                        ChickenCoCheBayAVG.coTheBay(chienBinh));
        if (ketQua == null) {
            return;
        }

        if (ketQua.isDaRoi()) {
            chienBinh.x = ketQua.getX();
            chienBinh.y = ketQua.getY();
            this.phatCapNhatXY(chienBinh);
            return;
        }

        // Packet gia khong duoc phep khuech dai thanh broadcast cho ca phong.
        // Chi gui lai toa do authoritative cho chinh client de sua prediction sai.
        nguoiChoi.dichVu.guiCapNhatXYLuyenTap(
                chienBinh.chiSo, chienBinh.x, chienBinh.y);
    }

    /** Doi sung thuong trong PvP; cac lop boss override bang mang chien binh rieng. */
    public synchronized boolean doiSungTrongTran(
            ChickenNguoiChoi nguoiChoi,
            int viTriBalo
    ) throws IOException {
        if (this.phatBanDangCho != null) {
            return false;
        }
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        return this.doiSungChoChienBinh(
                chienBinh,
                this.chienBinhs,
                this.luotHienTai,
                this.daKetThuc,
                viTriBalo,
                true);
    }

    /** Kich hoat POW trong PvP; cac tran boss override bang snapshot rieng. */
    public synchronized boolean kichHoatPow(
            ChickenNguoiChoi nguoiChoi
    ) throws IOException {
        if (this.phatBanDangCho != null) {
            return false;
        }
        return this.kichHoatPowChoChienBinh(
                this.layChienBinh(nguoiChoi),
                this.chienBinhs,
                this.luotHienTai,
                this.daKetThuc);
    }

    protected final boolean kichHoatPowChoChienBinh(
            ChickenChienBinh chienBinh,
            ChickenChienBinh[] danhSach,
            byte luot,
            boolean daKetThucTran
    ) throws IOException {
        if (chienBinh == null || chienBinh.chet || daKetThucTran
                || chienBinh.chiSo != luot || !chienBinh.laNguoiChoiThat()
                || !chienBinh.danhDauKichHoatPowTrongLuot()) {
            return false;
        }
        if (!chienBinh.nguoiChoi.kichHoatPowNoiBo()) {
            chienBinh.huyDanhDauPowTrongLuot();
            return false;
        }
        this.phatKichHoatHieuUngPow(chienBinh, danhSach);
        return true;
    }

    protected final void ghiNhanPowSauSatThuong(
            ChickenChienBinh mucTieu,
            int hpTruoc,
            ChickenChienBinh[] danhSach
    ) throws IOException {
        if (mucTieu == null || !mucTieu.laNguoiChoiThat()) {
            return;
        }
        int hpThucDaMat = Math.max(0, hpTruoc - Math.max(0, mucTieu.hp));
        if (mucTieu.nguoiChoi.ghiNhanSatThuongChoPow(
                hpThucDaMat, mucTieu.mauToiDa)
                && !mucTieu.nguoiChoi.dangHienHieuUngPow()) {
            this.phatCapNhatPow(mucTieu, danhSach);
        }
    }

    protected final void dongBoTatCaPow(
            ChickenChienBinh[] danhSach
    ) throws IOException {
        if (danhSach == null) {
            return;
        }
        for (ChickenChienBinh chienBinh : danhSach) {
            if (chienBinh != null && chienBinh.laNguoiChoiThat()) {
                this.phatCapNhatPow(chienBinh, danhSach);
            }
        }
    }

    protected final void huyPowSauLuot(
            ChickenChienBinh chienBinh,
            ChickenChienBinh[] danhSach
    ) throws IOException {
        if (chienBinh != null && chienBinh.nguoiChoi != null
                && chienBinh.nguoiChoi.huyPowDaKichHoat()) {
            this.phatCapNhatPow(chienBinh, danhSach);
        }
    }

    private void phatCapNhatPow(
            ChickenChienBinh thayDoi,
            ChickenChienBinh[] danhSach
    ) throws IOException {
        this.phatCapNhatPow(
                thayDoi,
                danhSach,
                thayDoi == null || thayDoi.nguoiChoi == null
                        ? 0 : thayDoi.nguoiChoi.layPowTrongTran());
    }

    private void phatCapNhatPow(
            ChickenChienBinh thayDoi,
            ChickenChienBinh[] danhSach,
            int pow
    ) throws IOException {
        if (thayDoi == null || thayDoi.nguoiChoi == null || danhSach == null) {
            return;
        }
        for (ChickenChienBinh nguoiNhan : danhSach) {
            if (nguoiNhan != null && nguoiNhan.coPhien()) {
                nguoiNhan.nguoiChoi.dichVu.guiCapNhatPow(
                        thayDoi.chiSo, pow);
            }
        }
    }

    private void phatKichHoatHieuUngPow(
            ChickenChienBinh thayDoi,
            ChickenChienBinh[] danhSach
    ) throws IOException {
        if (thayDoi == null || thayDoi.nguoiChoi == null || danhSach == null) {
            return;
        }
        for (ChickenChienBinh nguoiNhan : danhSach) {
            if (nguoiNhan != null && nguoiNhan.coPhien()) {
                nguoiNhan.nguoiChoi.dichVu.guiKichHoatHieuUngPow(
                        thayDoi.chiSo);
            }
        }
    }

    /**
     * Luong dung chung cho moi che do. Lop con van phai truyen luot va mang
     * chien binh authoritative cua chinh no, tranh dung nham snapshot base.
     */
    protected final boolean doiSungChoChienBinh(
            ChickenChienBinh chienBinh,
            ChickenChienBinh[] danhSach,
            byte luot,
            boolean daKetThucTran,
            int viTriBalo,
            boolean chiaPheTheoSlotChanLe
    ) throws IOException {
        if (chienBinh == null || chienBinh.chet || daKetThucTran
                || chienBinh.chiSo != luot
                || !chienBinh.laNguoiChoiThat()
                || chienBinh.ultronDangBanX3
                || chienBinh.ironManLaserSanSang
                || chienBinh.lokiSkillActive
                || chienBinh.lokiDangChoChonMucTieu) {
            return false;
        }
        ChickenChienBinh.VatPhamChienTrongTran vatPham =
                chienBinh.layVatPhamChienTrongOTrongBalo(viTriBalo);
        if (vatPham != null) {
            if (vatPham.getIdVatPham()
                    == ChickenCuuThuongBanThan.ID_VAT_PHAM) {
                return this.dungCuuThuongBanThan(
                        chienBinh, vatPham, danhSach, viTriBalo);
            }
            if (vatPham.getIdVatPham()
                    == ChickenCuuThuongDongDoi.ID_VAT_PHAM) {
                return this.dungCuuThuongDongDoi(
                        chienBinh, vatPham, danhSach,
                        chiaPheTheoSlotChanLe, viTriBalo);
            }
            if (vatPham.getIdVatPham()
                    == ChickenDiChuyenX2.ID_VAT_PHAM) {
                return this.dungDiChuyenX2(
                        chienBinh, vatPham, danhSach, viTriBalo);
            }
            if (vatPham.getIdVatPham()
                    == ChickenNgungGio.ID_VAT_PHAM) {
                return this.dungNgungGio(
                        chienBinh, vatPham, danhSach, viTriBalo);
            }
            ChickenChienBinh.VatPhamChienTrongTran dangCho =
                    chienBinh.layVatPhamChienDangCho();
            if (dangCho != null) {
                return dangCho == vatPham;
            }
            if (!chienBinh.nguoiChoi.coVatPhamChienSanSang(vatPham)
                    || !chienBinh.chonVatPhamChienTrongTran(viTriBalo)) {
                return false;
            }
            for (ChickenChienBinh nguoiNhan : danhSach) {
                if (nguoiNhan != null && nguoiNhan.coPhien()) {
                    nguoiNhan.nguoiChoi.dichVu.guiDungVatPhamLuyenTap(
                            chienBinh.chiSo,
                            (byte) vatPham.getCauHinh().getMaSuDung(),
                            vatPham.getIcon()
                    );
                }
            }
            return true;
        }
        if (chienBinh.avenger != 0) {
            return false;
        }
        ChickenVatPham sungCu = chienBinh.doiSungTrongTran(viTriBalo);
        ChickenVatPham sungMoi = chienBinh.laySungDangCamTrongTran();
        if (sungCu == null || sungMoi == null || sungCu.mau == null
                || sungMoi.mau == null) {
            return false;
        }
        for (ChickenChienBinh nguoiNhan : danhSach) {
            if (nguoiNhan == null || !nguoiNhan.coPhien()) {
                continue;
            }
            nguoiNhan.nguoiChoi.dichVu.guiDoiSungTrongTran(
                    chienBinh.chiSo,
                    sungMoi.mau.part,
                    sungCu.mau.iconID);
            // CMD -45 cap nhat mang icon Balo cuc bo theo GameScr.cb. Client
            // quan sat khong bam o nay, nen nap lai Balo cua chinh ho de khong
            // lam sai icon trong lan mo sau.
            if (nguoiNhan == chienBinh) {
                nguoiNhan.nguoiChoi.dichVu.guiBaloTrongTran(chienBinh);
            } else {
                nguoiNhan.nguoiChoi.dichVu.guiBalo();
            }
        }
        return true;
    }

    /** Item 220 chi hoi nguoi dung; dong doi chi nhan packet hien thi. */
    private boolean dungCuuThuongBanThan(
            ChickenChienBinh nguoiDung,
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            ChickenChienBinh[] danhSach,
            int viTriBalo
    ) throws IOException {
        if (!ChickenCuuThuongBanThan.coTheDung(nguoiDung)
                || !nguoiDung.nguoiChoi.coVatPhamChienSanSang(vatPham)
                || !nguoiDung.chonVatPhamChienTrongTran(viTriBalo)) {
            return false;
        }
        if (!nguoiDung.nguoiChoi.tieuThuMotVatPhamChien(vatPham)) {
            nguoiDung.huyChonVatPhamChienTrongLuot(vatPham);
            return false;
        }
        if (!nguoiDung.danhDauDaDungVatPhamChienTrongTran(vatPham)) {
            throw new IllegalStateException(
                    "Cuu thuong ca nhan vuot quota sau khi da tru kho");
        }

        int mauHoi = ChickenCuuThuongBanThan.apDung(nguoiDung);
        for (ChickenChienBinh nguoiNhan : danhSach) {
            if (nguoiNhan == null || !nguoiNhan.coPhien()) {
                continue;
            }
            nguoiNhan.nguoiChoi.dichVu.guiDungVatPhamLuyenTap(
                    nguoiDung.chiSo,
                    (byte) vatPham.getCauHinh().getMaSuDung(),
                    vatPham.getIcon());
            nguoiNhan.nguoiChoi.dichVu.guiCapNhatMauDau(
                    nguoiDung.chiSo,
                    nguoiDung.hp,
                    nguoiDung.phanTramMau(),
                    (byte) 0);
        }
        nguoiDung.xoaVatPhamChienDangCho();
        try {
            nguoiDung.nguoiChoi.dichVu.guiBaloTrongTran(nguoiDung);
        } catch (IOException ex) {
            ChickenQuanLyMayChu.log(
                    "[VAT_PHAM_CHIEN][LOI_DONG_BO_BALO] playerId="
                    + nguoiDung.nguoiChoi.ma + " itemId="
                    + vatPham.getIdVatPham() + " mode=match");
        }
        ChickenQuanLyMayChu.log(
                "[VAT_PHAM_CHIEN][CUU_THUONG_BAN_THAN] playerId="
                + nguoiDung.nguoiChoi.ma + " healed=" + mauHoi
                + " hp=" + nguoiDung.hp + "/" + nguoiDung.mauToiDa);
        return true;
    }

    /**
     * Item 230 kich hoat ngay khi chon o Balo, sau do nguoi choi van duoc ban
     * sung thuong. Vi vay chi xoa item dang cho, con khoa hanh dong dac biet
     * duoc giu den het luot de chan POW/item thu hai.
     */
    private boolean dungCuuThuongDongDoi(
            ChickenChienBinh nguoiDung,
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            ChickenChienBinh[] danhSach,
            boolean chiaPheTheoSlotChanLe,
            int viTriBalo
    ) throws IOException {
        java.util.List<ChickenChienBinh> mucTieu =
                ChickenCuuThuongDongDoi.layMucTieu(
                        nguoiDung, danhSach, chiaPheTheoSlotChanLe);
        if (mucTieu.isEmpty()
                || !nguoiDung.nguoiChoi.coVatPhamChienSanSang(vatPham)
                || !nguoiDung.chonVatPhamChienTrongTran(viTriBalo)) {
            return false;
        }
        if (!nguoiDung.nguoiChoi.tieuThuMotVatPhamChien(vatPham)) {
            nguoiDung.huyChonVatPhamChienTrongLuot(vatPham);
            return false;
        }
        if (!nguoiDung.danhDauDaDungVatPhamChienTrongTran(vatPham)) {
            throw new IllegalStateException(
                    "Cuu thuong vuot quota sau khi da tru kho");
        }

        ChickenCuuThuongDongDoi.apDung(mucTieu);
        for (ChickenChienBinh nguoiNhan : danhSach) {
            if (nguoiNhan == null || !nguoiNhan.coPhien()) {
                continue;
            }
            nguoiNhan.nguoiChoi.dichVu.guiDungVatPhamLuyenTap(
                    nguoiDung.chiSo,
                    (byte) vatPham.getCauHinh().getMaSuDung(),
                    vatPham.getIcon());
            for (ChickenChienBinh duocHoi : mucTieu) {
                nguoiNhan.nguoiChoi.dichVu.guiCapNhatMauDau(
                        duocHoi.chiSo,
                        duocHoi.hp,
                        duocHoi.phanTramMau(),
                        (byte) 0);
            }
        }
        nguoiDung.xoaVatPhamChienDangCho();
        try {
            nguoiDung.nguoiChoi.dichVu.guiBaloTrongTran(nguoiDung);
        } catch (IOException ex) {
            ChickenQuanLyMayChu.log(
                    "[VAT_PHAM_CHIEN][LOI_DONG_BO_BALO] playerId="
                    + nguoiDung.nguoiChoi.ma + " itemId="
                    + vatPham.getIdVatPham() + " mode=match");
        }
        ChickenQuanLyMayChu.log(
                "[VAT_PHAM_CHIEN][CUU_THUONG] playerId="
                + nguoiDung.nguoiChoi.ma + " targets=" + mucTieu.size());
        return true;
    }

    /**
     * Item 223 kich hoat ngay, ton tai suot tran va van cho ban sung trong
     * luot. Khoa hanh dong dac biet duoc giu de chan POW/item thu hai.
     */
    private boolean dungDiChuyenX2(
            ChickenChienBinh nguoiDung,
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            ChickenChienBinh[] danhSach,
            int viTriBalo
    ) throws IOException {
        if (!ChickenDiChuyenX2.coTheDung(nguoiDung)
                || !nguoiDung.nguoiChoi.coVatPhamChienSanSang(vatPham)
                || !nguoiDung.chonVatPhamChienTrongTran(viTriBalo)) {
            return false;
        }
        if (!nguoiDung.nguoiChoi.tieuThuMotVatPhamChien(vatPham)) {
            nguoiDung.huyChonVatPhamChienTrongLuot(vatPham);
            return false;
        }
        if (!nguoiDung.danhDauDaDungVatPhamChienTrongTran(vatPham)) {
            throw new IllegalStateException(
                    "Di chuyen x2 vuot quota sau khi da tru kho");
        }
        if (!ChickenDiChuyenX2.apDung(nguoiDung)) {
            throw new IllegalStateException(
                    "Di chuyen x2 doi state sau khi da tru kho");
        }

        for (ChickenChienBinh nguoiNhan : danhSach) {
            if (nguoiNhan != null && nguoiNhan.coPhien()) {
                nguoiNhan.nguoiChoi.dichVu.guiDungVatPhamLuyenTap(
                        nguoiDung.chiSo,
                        (byte) vatPham.getCauHinh().getMaSuDung(),
                        vatPham.getIcon());
            }
        }
        nguoiDung.xoaVatPhamChienDangCho();
        try {
            nguoiDung.nguoiChoi.dichVu.guiBaloTrongTran(nguoiDung);
        } catch (IOException ex) {
            ChickenQuanLyMayChu.log(
                    "[VAT_PHAM_CHIEN][LOI_DONG_BO_BALO] playerId="
                    + nguoiDung.nguoiChoi.ma + " itemId="
                    + vatPham.getIdVatPham() + " mode=match");
        }
        ChickenQuanLyMayChu.log(
                "[VAT_PHAM_CHIEN][DI_CHUYEN_X2] playerId="
                + nguoiDung.nguoiChoi.ma + " remaining="
                + nguoiDung.quangDuongDiChuyenConLai + " max="
                + nguoiDung.layQuangDuongDiChuyenToiDaTrongLuot());
        return true;
    }

    /**
     * Item 225 xoa gio cua chinh luot dang dien ra. Nguoi dung van duoc ban
     * sung thuong; khoa item/POW cua luot duoc giu de chan packet lap.
     */
    private boolean dungNgungGio(
            ChickenChienBinh nguoiDung,
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            ChickenChienBinh[] danhSach,
            int viTriBalo
    ) throws IOException {
        if (!ChickenNgungGio.coTheDung(nguoiDung)
                || !nguoiDung.nguoiChoi.coVatPhamChienSanSang(vatPham)
                || !nguoiDung.chonVatPhamChienTrongTran(viTriBalo)) {
            return false;
        }
        if (!nguoiDung.nguoiChoi.tieuThuMotVatPhamChien(vatPham)) {
            nguoiDung.huyChonVatPhamChienTrongLuot(vatPham);
            return false;
        }
        if (!nguoiDung.danhDauDaDungVatPhamChienTrongTran(vatPham)) {
            throw new IllegalStateException(
                    "Ngung gio vuot quota sau khi da tru kho");
        }

        this.gioHienTai = ChickenNgungGio.taoTrangThaiKhongGio();
        nguoiDung.xoaVatPhamChienDangCho();
        for (ChickenChienBinh nguoiNhan : danhSach) {
            if (nguoiNhan == null || !nguoiNhan.coPhien()) {
                continue;
            }
            nguoiNhan.nguoiChoi.dichVu.guiDungVatPhamLuyenTap(
                    nguoiDung.chiSo,
                    (byte) vatPham.getCauHinh().getMaSuDung(),
                    vatPham.getIcon());
            nguoiNhan.nguoiChoi.dichVu.guiGio((byte) 0, (byte) 0);
        }
        try {
            nguoiDung.nguoiChoi.dichVu.guiBaloTrongTran(nguoiDung);
        } catch (IOException ex) {
            ChickenQuanLyMayChu.log(
                    "[VAT_PHAM_CHIEN][LOI_DONG_BO_BALO] playerId="
                    + nguoiDung.nguoiChoi.ma + " itemId="
                    + vatPham.getIdVatPham() + " mode=match");
        }
        ChickenQuanLyMayChu.log(
                "[VAT_PHAM_CHIEN][NGUNG_GIO] playerId="
                + nguoiDung.nguoiChoi.ma + " windX=0 windY=0");
        return true;
    }

    public synchronized void ban(
            ChickenNguoiChoi nguoiChoi,
            ChickenTinNhan ms
    ) throws IOException {
        ChickenChienBinh shooter = this.layChienBinh(nguoiChoi);
        if (shooter == null || shooter.chet || shooter.chiSo != this.luotHienTai
                || this.daKetThuc
                || this.phatBanDangCho != null
                || this.kyNangThor.dangThiTrien(shooter)
                || this.kyNangLoki.dangThiTrien(shooter)) {
            return;
        }
        ChickenQuanLyDanSung.DuLieuSung sungMayChu =
                ChickenQuanLyDanSung.theoPartSung(shooter.maVuKhi);
        ChickenChienBinh.VatPhamChienTrongTran vatPhamDangCho =
                shooter.layVatPhamChienDangCho();
        ChickenYeuCauBanServer.KetQua yeuCau =
                vatPhamDangCho == null
                        ? ChickenYeuCauBanServer.doc(
                                ms, sungMayChu, shooter.avenger)
                        : ChickenYeuCauBanServer.docVatPham(
                                ms, vatPhamDangCho.getCauHinh());
        if (yeuCau == null) {
            return;
        }
        byte loaiDanMayChu = yeuCau.getLoaiDan();
        if (shooter.avenger == ChickenKyNangDacBietLoki.AVG_LOKI
                && shooter.lokiDangChoChonMucTieu) {
            shooter.lokiDangChoChonMucTieu = false;
            shooter.lokiDaGuiMenu = false;
        }
        short goc = yeuCau.getGoc();
        byte luc = yeuCau.getLuc();
        byte lucPhu = yeuCau.getLucPhu();
        // Không ghi đè vị trí server bằng tọa độ mà client nhét vào packet bắn.
        if (shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON) {
            shooter.ultronGocNgamHienTai = this.chuanHoaGocUltron(goc);
            shooter.ultronLucNgamHienTai = luc;
            shooter.ultronDaCoGocNgam = true;
        }

        try (ChickenNguCanhLaySung.Phien ignored =
                ChickenNguCanhLaySung.batDauPhatBanNguoiChoi()) {
            if (vatPhamDangCho != null) {
                ChickenKetQuaDan ketQua = this.xuLyPhatBanVatPham(
                        shooter,
                        vatPhamDangCho.getCauHinh(),
                        goc,
                        luc
                );
                int napDanVatPham = ketQua == null ? -1
                        : this.tieuThuVaDanhDauVatPhamChien(
                                shooter, vatPhamDangCho,
                                ketQua, this.map);
                if (napDanVatPham < 0) {
                    return;
                }
                ChickenMayMan.PhienTanCong phienMayMan =
                        ChickenMayMan.batDau(shooter, this.chienBinhs);
                phienMayMan.chuanBiPhongThuTruocPhat(
                        ketQua.satThuongTheoMucTieu.keySet());
                this.batDauChoKetThucPhatBan(
                        shooter, ketQua, phienMayMan, napDanVatPham);
                this.phatBan(shooter, ketQua, (byte) 1);
                this.apDungDichChuyenVatPhamSauPhatBan(
                        shooter, vatPhamDangCho, ketQua, this.map);
                shooter.xoaVatPhamChienDangCho();
                return;
            } else if (this.kyNangIronMan.dangChoBan(shooter)) {
                this.banLaserIronMan(shooter, goc);
                this.kyNangIronMan.sauKhiBanHoacBoLuot(shooter);
            } else if (this.kyNangUltron.dangBanX3(shooter)) {
                this.banX3Ultron(shooter, goc, luc);
                /*
                 * Chua sang luot: CMD 79 cua client se lan luot mo khoa lan ban
                 * thu hai va thu ba. Ket thuc loat moi tinh damage va sang luot.
                 */
                return;
            } else {
                ChickenKetQuaDan ketQua = this.xuLyPhatBan(
                        shooter, loaiDanMayChu, goc, luc, lucPhu);
                ChickenMayMan.PhienTanCong phienMayMan =
                        ChickenMayMan.batDau(shooter, this.chienBinhs);
                phienMayMan.chuanBiPhongThuTruocPhat(
                        ketQua.satThuongTheoMucTieu.keySet());
                this.batDauChoKetThucPhatBan(
                        shooter, ketQua, phienMayMan);
                this.phatBan(shooter, ketQua, (byte) 1);
                return;
            }
        }
        this.kyNangHawk.sauKhiBanThuong(shooter);
        if (!this.daKetThuc) {
            this.sangLuot();
        }
    }

    private ChickenKetQuaDan xuLyPhatBanVatPham(
            ChickenChienBinh shooter,
            ChickenCongThucVatPhamChien.CauHinh cauHinh,
            short goc,
            byte luc
    ) {
        if (shooter == null || cauHinh == null
                || ChickenCauHinhSatThuongVatPham.theoIdVatPham(
                        cauHinh.getIdVatPham()) == null) {
            return null;
        }
        ChickenQuanLyCongThucSung.KiemTraBanDo kiemTraBanDo = this.map;
        byte windX = ChickenHeThongGio.layWindXChoItem(
                this.gioHienTai, cauHinh.getIdVatPham());
        byte windY = ChickenHeThongGio.layWindYChoItem(
                this.gioHienTai, cauHinh.getIdVatPham());
        return this.taoPhatBanVatPhamChoCheDo(
                shooter,
                cauHinh,
                goc,
                luc,
                windX,
                windY,
                kiemTraBanDo,
                this.chienBinhs,
                new ChickenPhatBanServer.BoLocMucTieu() {
                    @Override
                    public boolean chapNhan(
                            ChickenChienBinh nguoiBan,
                            ChickenChienBinh mucTieu
                    ) {
                        return mucTieu != nguoiBan
                                && !(nguoiBan.bot && mucTieu.bot);
                    }
                }
        );
    }

    protected final ChickenKetQuaDan taoPhatBanVatPhamChoCheDo(
            ChickenChienBinh shooter,
            ChickenCongThucVatPhamChien.CauHinh cauHinh,
            short goc,
            byte luc,
            byte windX,
            byte windY,
            ChickenQuanLyCongThucSung.KiemTraBanDo kiemTraBanDo,
            ChickenChienBinh[] danhSach,
            ChickenPhatBanServer.BoLocMucTieu boLoc
    ) {
        if (shooter == null || cauHinh == null || kiemTraBanDo == null
                || danhSach == null || boLoc == null
                || ChickenCauHinhSatThuongVatPham.theoIdVatPham(
                        cauHinh.getIdVatPham()) == null) {
            return null;
        }
        short[] dauNong = ChickenToaDoDauNong.layChoNguoiChoi(
                shooter.x,
                shooter.y,
                goc,
                shooter.maVuKhi,
                kiemTraBanDo
        );
        return ChickenPhatBanVatPhamServer.tao(
                shooter,
                dauNong[0],
                dauNong[1],
                goc,
                luc,
                cauHinh,
                windX,
                windY,
                kiemTraBanDo,
                danhSach,
                boLoc
        );
    }

    /**
     * Consume one server-approved battle item, then lock its per-match usage
     * quota inside the synchronized match action. Returns the server reload.
     */
    protected final int tieuThuVaDanhDauVatPhamChien(
            ChickenChienBinh shooter,
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            ChickenKetQuaDan ketQua,
            ChickenQuanLyBanDo banDo
    ) {
        if (shooter == null || shooter.nguoiChoi == null || vatPham == null
                || !shooter.coTheDungVatPhamChienTrongTran(vatPham)
                || !ChickenHieuUngVatPhamChien.coTheApDung(
                        vatPham, ketQua, banDo)
                || !shooter.nguoiChoi.tieuThuMotVatPhamChien(vatPham)) {
            return -1;
        }
        if (!shooter.danhDauDaDungVatPhamChienTrongTran(vatPham)) {
            throw new IllegalStateException(
                    "Vat pham vuot gioi han sau khi da tru kho");
        }
        if (!ChickenHieuUngVatPhamChien.apDung(
                vatPham, ketQua, banDo)) {
            throw new IllegalStateException(
                    "Khong ap dung duoc hieu ung vat pham da tru kho");
        }
        try {
            /*
             * Kho that van con so luong, nhung Balo cua tran phai an item da
             * het quota de nguoi choi khong hieu nham la con dung duoc.
             */
            shooter.nguoiChoi.dichVu.guiBaloTrongTran(shooter);
        } catch (IOException ex) {
            ChickenQuanLyMayChu.log(
                    "[VAT_PHAM_CHIEN][LOI_DONG_BO_BALO] playerId="
                    + shooter.nguoiChoi.ma
                    + " itemId=" + vatPham.getIdVatPham()
                    + " mode=match loi="
                    + ex.getClass().getSimpleName());
        }
        return shooter.layNapDanSauKhiDungVatPham(vatPham);
    }

    /**
     * Goi sau packet CMD 22: giu toa do dau packet la vi tri cu, sau do moi
     * chot diem dich chuyen type 5 vao state server.
     */
    protected final void apDungDichChuyenVatPhamSauPhatBan(
            ChickenChienBinh shooter,
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            ChickenKetQuaDan ketQua,
            ChickenQuanLyBanDo banDo
    ) {
        if (ChickenHieuUngVatPhamChien
                .apDungDichChuyenSauKhiGuiPhatBan(
                        shooter, vatPham, ketQua, banDo)) {
            ChickenQuanLyMayChu.log(
                    "[VAT_PHAM_CHIEN][DICH_CHUYEN] playerId="
                    + shooter.nguoiChoi.ma + " x=" + shooter.x
                    + " y=" + shooter.y);
        }
    }

    public synchronized boolean kichHoatKyNangIronMan(
            ChickenNguoiChoi nguoiChoi
    ) {
        if (this.phatBanDangCho != null) {
            return false;
        }
        ChickenChienBinh ironMan = this.layChienBinh(nguoiChoi);
        return ChickenTrangThaiHanhDongLuot.coTheKichHoatKyNang(ironMan)
                && this.kyNangIronMan.kichHoat(ironMan);
    }

    public synchronized boolean kichHoatKyNangUltron(
            ChickenNguoiChoi nguoiChoi
    ) throws IOException {
        if (this.phatBanDangCho != null) {
            return false;
        }
        ChickenChienBinh ultron = this.layChienBinh(nguoiChoi);
        if (!ChickenTrangThaiHanhDongLuot.coTheKichHoatKyNang(ultron)
                || !this.kyNangUltron.kichHoatBanX3(ultron)) {
            return false;
        }

        /*
         * CMD -47 chỉ mang chỉ số mục đã chọn. Client tự đóng menu ngay sau
         * khi gửi lựa chọn, vì vậy tuyệt đối không phát lại CMD 24 của lượt
         * hiện tại ở đây. Gửi trùng packet lượt khi client vẫn đang thoát menu
         * làm client kẹt vòng xoay hoặc tự ngắt kết nối.
         *
         * Chỉ đánh dấu phát bắn kế tiếp là Bắn x3. CMD 22 thật của người chơi
         * sẽ mang góc/lực hiện tại và được chuyển thành ba tia.
         */
        System.out.println("[ULTRON] DA_CHON_BAN_X3 index="
                + (ultron.chiSo & 0xFF)
                + " choPhatBanThat=true khongGuiCmd=-67");
        return true;
    }

    public synchronized ChickenChienBinh[] danhSachMucTieuHawk(ChickenNguoiChoi nguoiChoi) {
        return this.kyNangHawk.danhSachMucTieu(this.layChienBinh(nguoiChoi));
    }

    public synchronized boolean kichHoatKyNangHawk(ChickenNguoiChoi nguoiChoi, byte chiSoMucTieu) throws IOException {
        if (this.phatBanDangCho != null) {
            return false;
        }
        ChickenChienBinh hawk = this.layChienBinh(nguoiChoi);
        return ChickenTrangThaiHanhDongLuot.coTheKichHoatKyNang(hawk)
                && this.kyNangHawk.kichHoat(hawk, chiSoMucTieu);
    }

    public synchronized void nhanLenhKyNangHawk(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        ChickenChienBinh hawk = this.layChienBinh(nguoiChoi);
        if (this.kyNangHawk == null || this.phatBanDangCho != null
                || !ChickenTrangThaiHanhDongLuot
                        .coTheKichHoatKyNang(hawk)) {
            System.out.println("[HAWK] kyNangHawk=null");
            return;
        }
        this.kyNangHawk.nhanLenh(hawk, ms);
    }

    /** Route CMD -91 theo đúng AVG đang sử dụng. */
    public synchronized void nhanLenhKyNangDacBiet(
            ChickenNguoiChoi nguoiChoi,
            ChickenTinNhan ms
    ) throws IOException {
        if (this.phatBanDangCho != null) {
            return;
        }
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (!ChickenTrangThaiHanhDongLuot
                .coTheKichHoatKyNang(chienBinh)) {
            return;
        }
        if (chienBinh.avenger == ChickenKyNangDacBietThor.AVG_THOR) {
            this.kyNangThor.nhanLenh(chienBinh, ms);
            return;
        }
        if (chienBinh.avenger == ChickenKyNangDacBietLoki.AVG_LOKI) {
            this.kyNangLoki.nhanLenh(chienBinh, ms);
            return;
        }
        this.kyNangHawk.nhanLenh(chienBinh, ms);
    }

    /** Tên cũ được giữ lại để không ảnh hưởng các lời gọi cũ ngoài luồng CMD -91 mới. */
    public synchronized void xuLyCmd91Hawk(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        this.nhanLenhKyNangHawk(nguoiChoi, ms);
    }

    public synchronized void kiemTraVaCham(
            ChickenNguoiChoi nguoiChoi,
            ChickenTinNhan ms
    ) throws IOException {
        if (!ChickenXacNhanKetThucDan.docVaBoQua(ms)) {
            return;
        }
        if (!this.xuLyVaChamLoatUltron(nguoiChoi)) {
            this.xuLyXacNhanKetThucPhatBan(nguoiChoi);
        }
    }

    public synchronized void boLuot(ChickenNguoiChoi nguoiChoi)
            throws IOException {
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh != null && !chienBinh.chet
                && chienBinh.chiSo == this.luotHienTai
                && !this.daKetThuc
                && this.ultronX3KetQua == null
                && this.phatBanDangCho == null
                && !this.kyNangThor.dangThiTrien(chienBinh)
                && !this.kyNangLoki.dangThiTrien(chienBinh)) {
            if (chienBinh.avenger == ChickenKyNangDacBietLoki.AVG_LOKI) {
                chienBinh.lokiDangChoChonMucTieu = false;
                chienBinh.lokiDaGuiMenu = false;
            }
            this.kyNangUltron.huyKhiBoLuot(chienBinh);
            this.kyNangIronMan.sauKhiBanHoacBoLuot(chienBinh);
            chienBinh.xoaVatPhamChienDangCho();
            this.sangLuot(ChickenNapDanServer.layKhiKhongTaoPhatDan());
        }
    }

    public synchronized void khiNguoiChoiRoi(
            ChickenNguoiChoi nguoiChoi
    ) {
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh != null && chienBinh == this.ultronX3NguoiBan) {
            this.huyTrangThaiLoatUltron();
        }
        if (chienBinh != null) {
            chienBinh.chet = true;
            chienBinh.hp = 0;
            chienBinh.daRoiTran = true;
        }
        PhatBanDangCho dangCho = this.phatBanDangCho;
        if (dangCho != null && this.daDuXacNhanKetThucDan(dangCho)) {
            long conLaiMs = dangCho.xacNhanSomNhatMs
                    - System.currentTimeMillis();
            if (conLaiMs > 0L) {
                this.henChotPhatBanSauMocToiThieu(
                        dangCho, conLaiMs);
            } else {
                try {
                    this.chotPhatBanDangCho(dangCho, false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        this.boDangKyNguoiChoi(nguoiChoi);
    }

    /** Cho phép lớp trận boss riêng dọn đúng đăng ký trận mà constructor gốc đã tạo. */
    protected final void boDangKyNguoiChoi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi != null) {
            ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
            if (chienBinh != null) {
                chienBinh.daRoiTran = true;
            }
            boolean daBoDangKy = TRAN_DAU_THEO_NGUOI_CHOI.remove(
                    nguoiChoi.ma, this);
            if (daBoDangKy && nguoiChoi.dichVu != null) {
                try {
                    // CMD -42 trong tran an item da dung khoi snapshot. Khi
                    // roi tran phai gui lai Balo persistent de scene sanh
                    // khong tiep tuc hien trang thai loc cua tran cu.
                    nguoiChoi.dichVu.guiBaloCua(nguoiChoi);
                } catch (IOException loi) {
                    System.err.println("[BALO][LOI_KHOI_PHUC_SAU_TRAN] player="
                            + nguoiChoi.ma + " loi=" + loi.getMessage());
                }
            }
        }
    }

    public void dungBot() {
        if (this.tacVuBot != null) {
            this.tacVuBot.cancel(false);
            this.tacVuBot = null;
        }
    }

    private short chuanHoaGocUltron(short goc) {
        return ChickenGocBanUltron.chuanHoa(goc);
    }

    private synchronized void banX3Ultron(
            ChickenChienBinh shooter,
            short goc,
            byte luc
    ) throws IOException {
        goc = this.chuanHoaGocUltron(goc);
        short[] dauNong = ChickenGocBanUltron.layDiemBatDauDuongCan(
                shooter.x,
                shooter.y,
                goc,
                this.map.getWidth(),
                this.map.getHeight()
        );
        ChickenKetQuaDan ketQua = ChickenLoatBanUltronServer.tao(
                shooter,
                dauNong[0],
                dauNong[1],
                goc,
                luc,
                this.map,
                this.chienBinhs,
                new ChickenLoatBanUltronServer.BoLocMucTieu() {
                    @Override
                    public boolean chapNhan(
                            ChickenChienBinh nguoiBan,
                            ChickenChienBinh mucTieu
                    ) {
                        return mucTieu != nguoiBan
                                && !(nguoiBan.bot && mucTieu.bot);
                    }
                }
        );

        this.huyTrangThaiLoatUltron();
        this.ultronX3NguoiBan = shooter;
        this.ultronX3KetQua = ketQua;
        this.ultronX3PhienMayMan =
                ChickenMayMan.batDau(shooter, this.chienBinhs);
        this.ultronX3PhienMayMan.chuanBiPhongThuTruocPhat(
                ketQua.satThuongTheoMucTieu.keySet());
        this.ultronX3SoLanDaGui = 1;
        final long maLoat = ++this.ultronX3MaLoat;
        this.phatMotLanBanUltron(shooter, ketQua, 0);
        this.ultronX3TacVuHetHan = BOT_EXECUTOR.schedule(() -> {
            synchronized (ChickenQuanLyChien.this) {
                if (ChickenQuanLyChien.this.ultronX3KetQua == null
                        || ChickenQuanLyChien.this.ultronX3MaLoat != maLoat) {
                    return;
                }
                ChickenChienBinh nguoiBan =
                        ChickenQuanLyChien.this.ultronX3NguoiBan;
                ChickenQuanLyChien.this.huyTrangThaiLoatUltron();
                ChickenQuanLyChien.this.kyNangUltron.sauKhiDaBan(nguoiBan);
                System.out.println("[ULTRON] HUY_X3_QUA_HAN shooter="
                        + (nguoiBan == null ? -1 : nguoiBan.chiSo & 0xFF));
                try {
                    if (!ChickenQuanLyChien.this.daKetThuc) {
                        ChickenQuanLyChien.this.sangLuot();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }, 20L, TimeUnit.SECONDS);

        System.out.println("[ULTRON] BAN_X3 shooter="
                + (shooter.chiSo & 0xFF)
                + " goc=" + goc
                + " lanBan=1/3 choCmd79=true");
    }

    /** Gui dung mot animation ban; moi CMD 79 moi duoc gui animation ke tiep. */
    private void phatMotLanBanUltron(
            ChickenChienBinh shooter,
            ChickenKetQuaDan loat,
            int chiSoLanBan
    ) throws IOException {
        if (loat == null
                || chiSoLanBan < 0
                || chiSoLanBan >= loat.cacDuongX.length
                || chiSoLanBan >= loat.cacDuongY.length) {
            return;
        }
        ChickenKetQuaDan motLan = new ChickenKetQuaDan(
                loat.loaiDan,
                loat.batDauX,
                loat.batDauY,
                loat.goc,
                loat.luc,
                loat.lucPhu,
                new short[][]{loat.cacDuongX[chiSoLanBan]},
                new short[][]{loat.cacDuongY[chiSoLanBan]},
                new LinkedHashMap<ChickenChienBinh, Integer>()
        );
        this.phatBan(shooter, motLan, (byte) 1);
        this.ultronX3XacNhanSomNhatMs = System.currentTimeMillis()
                + ChickenThoiGianHoatAnhDan.tinh(motLan);
    }

    private void huyTrangThaiLoatUltron() {
        if (this.ultronX3TacVuXacNhanSom != null) {
            this.ultronX3TacVuXacNhanSom.cancel(false);
            this.ultronX3TacVuXacNhanSom = null;
        }
        if (this.ultronX3TacVuHetHan != null) {
            this.ultronX3TacVuHetHan.cancel(false);
            this.ultronX3TacVuHetHan = null;
        }
        this.ultronX3NguoiBan = null;
        this.ultronX3KetQua = null;
        this.ultronX3PhienMayMan = null;
        this.ultronX3SoLanDaGui = 0;
        this.ultronX3XacNhanSomNhatMs = 0L;
    }

    private synchronized boolean xuLyVaChamLoatUltron(
            ChickenNguoiChoi nguoiChoi
    ) throws IOException {
        ChickenChienBinh nguoiGui = this.layChienBinh(nguoiChoi);
        if (this.ultronX3KetQua == null
                || this.ultronX3NguoiBan == null
                || nguoiGui != this.ultronX3NguoiBan) {
            return false;
        }
        long conLaiMs = this.ultronX3XacNhanSomNhatMs
                - System.currentTimeMillis();
        if (conLaiMs > 0L) {
            this.henXuLyXacNhanSomUltron(
                    nguoiGui, conLaiMs, this.ultronX3SoLanDaGui);
            return true;
        }
        return this.tiepTucLoatUltronSauXacNhan();
    }

    /**
     * Client goc chi gui mot CMD79 khi animation ket thuc. Neu dong ho server
     * uoc luong dai hon vai frame, khong duoc vut packet do di vi loat X3 se
     * dung vinh vien. Server giu tin hieu, nhung van chi xu ly tai moc hop le.
     */
    private void henXuLyXacNhanSomUltron(
            ChickenChienBinh nguoiGui,
            long conLaiMs,
            int soLanDaGui
    ) {
        if (this.ultronX3TacVuXacNhanSom != null) {
            return;
        }
        final long maLoat = this.ultronX3MaLoat;
        this.ultronX3TacVuXacNhanSom = BOT_EXECUTOR.schedule(() -> {
            synchronized (ChickenQuanLyChien.this) {
                ChickenQuanLyChien.this.ultronX3TacVuXacNhanSom = null;
                if (ChickenQuanLyChien.this.ultronX3KetQua == null
                        || ChickenQuanLyChien.this.ultronX3MaLoat != maLoat
                        || ChickenQuanLyChien.this.ultronX3NguoiBan != nguoiGui
                        || ChickenQuanLyChien.this.ultronX3SoLanDaGui
                                != soLanDaGui) {
                    return;
                }
                try {
                    ChickenQuanLyChien.this
                            .tiepTucLoatUltronSauXacNhan();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }, Math.max(1L, conLaiMs), TimeUnit.MILLISECONDS);
    }

    private boolean tiepTucLoatUltronSauXacNhan() throws IOException {
        if (this.ultronX3SoLanDaGui < ChickenLoatBanUltronServer.SO_VIEN) {
            int chiSoLanBan = this.ultronX3SoLanDaGui;
            this.phatMotLanBanUltron(
                    this.ultronX3NguoiBan,
                    this.ultronX3KetQua,
                    chiSoLanBan
            );
            this.ultronX3SoLanDaGui++;
            System.out.println("[ULTRON] BAN_X3 shooter="
                    + (this.ultronX3NguoiBan.chiSo & 0xFF)
                    + " lanBan=" + this.ultronX3SoLanDaGui
                    + "/3 choCmd79=true");
            return true;
        }

        ChickenChienBinh shooter = this.ultronX3NguoiBan;
        ChickenKetQuaDan ketQua = this.ultronX3KetQua;
        ChickenMayMan.PhienTanCong phienMayMan =
                this.ultronX3PhienMayMan;
        this.huyTrangThaiLoatUltron();
        this.kyNangUltron.sauKhiDaBan(shooter);
        this.apDungSatThuongKetQua(phienMayMan, ketQua);
        this.kyNangHawk.sauKhiBanThuong(shooter);
        if (!this.daKetThuc) {
            this.sangLuot();
        }
        System.out.println("[ULTRON] KET_THUC_X3 shooter="
                + (shooter.chiSo & 0xFF) + " daBanDu=3");
        return true;
    }

    private ChickenKetQuaDan xuLyPhatBan(
            ChickenChienBinh shooter,
            byte loaiDan,
            short goc,
            byte luc
    ) {
        return this.xuLyPhatBan(shooter, loaiDan, goc, luc, luc);
    }

    private ChickenKetQuaDan xuLyPhatBan(
            ChickenChienBinh shooter,
            byte loaiDan,
            short goc,
            byte luc,
            byte lucPhu
    ) {
        ChickenQuanLyCongThucSung.KiemTraBanDo kiemTraBanDo = this.map;

        short[] dauNong;
        if (!shooter.bot
                && shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON) {
            dauNong = ChickenGocBanUltron.layDiemBatDauDuongCan(
                    shooter.x,
                    shooter.y,
                    goc,
                    this.map.getWidth(),
                    this.map.getHeight()
            );
        } else {
            dauNong = shooter.bot
                    ? ChickenToaDoDauNong.layChoBoss(
                            shooter.x, shooter.y, goc, kiemTraBanDo)
                    : ChickenToaDoDauNong.layChoNguoiChoi(
                            shooter.x, shooter.y, goc, shooter.maVuKhi,
                            kiemTraBanDo);
        }

        ChickenQuanLyDanSung.DuLieuSung duLieuSungMayChu =
                ChickenQuanLyDanSung.theoPartSung(shooter.maVuKhi);
        int idSungMayChu = duLieuSungMayChu == null
                ? -1
                : duLieuSungMayChu.getIdSung();
        if (duLieuSungMayChu != null) {
            // Loại đạn là thuộc tính súng đang trang bị; byte client gửi chỉ là intent cũ.
            loaiDan = duLieuSungMayChu.getLoaiDan();
        }
        if (shooter.avenger != ChickenKyNangDacBietUltron.AVG_ULTRON
                && idSungMayChu != ChickenQuanLyCongThucSung.ID_SUNG_CAPTAIN) {
            return ChickenPhatBanServer.tao(
                    shooter,
                    dauNong[0],
                    dauNong[1],
                    goc,
                    luc,
                    lucPhu,
                    duLieuSungMayChu,
                    ChickenHeThongGio.layWindXChoSung(
                            this.gioHienTai, idSungMayChu),
                    ChickenHeThongGio.layWindYChoSung(
                            this.gioHienTai, idSungMayChu),
                    kiemTraBanDo,
                    this.chienBinhs,
                    new ChickenPhatBanServer.BoLocMucTieu() {
                        @Override
                        public boolean chapNhan(
                                ChickenChienBinh nguoiBan,
                                ChickenChienBinh mucTieu
                        ) {
                            return mucTieu != nguoiBan
                                    && !(nguoiBan.bot && mucTieu.bot);
                        }
                    }
            );
        }
        short[] hienThiX;
        short[] hienThiY;
        short[] vaChamX;
        short[] vaChamY;

        if (shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON) {
            /*
             * Công thức laser là thuộc tính của AVG Ultron. Không dựa vào
             * part súng đang hiển thị vì part có thể bị thay đổi bởi biến hình
             * hoặc dữ liệu client. Tia luôn bay thẳng, không gió và không trọng
             * lực, nhưng phải dừng tại địa hình hoặc nhân vật đầu tiên.
             */
            ChickenCongThucBanUltron.DuongTia tia =
                    ChickenCongThucBanUltron.taoTiaThang(
                            dauNong[0],
                            dauNong[1],
                            goc,
                            this.map.getWidth(),
                            this.map.getHeight()
                    );
            short[][] dungTaiBanDo = this.catDuongTiaTaiVaChamBanDo(
                    tia.getX(),
                    tia.getY()
            );
            hienThiX = dungTaiBanDo[0];
            hienThiY = dungTaiBanDo[1];
            vaChamX = hienThiX;
            vaChamY = hienThiY;
        } else {
            ChickenQuanLyCongThucSung.KetQuaQuyDao quyDao =
                    ChickenQuanLyCongThucSung.taoQuyDao(
                            dauNong[0],
                            dauNong[1],
                            goc,
                            luc,
                            shooter.maVuKhi,
                            ChickenHeThongGio.layWindXChoSung(
                                    this.gioHienTai,
                                    idSungMayChu),
                            ChickenHeThongGio.layWindYChoSung(
                                    this.gioHienTai,
                                    idSungMayChu),
                            kiemTraBanDo
                    );
            hienThiX = quyDao.getHienThiX();
            hienThiY = quyDao.getHienThiY();
            vaChamX = quyDao.getVaChamX();
            vaChamY = quyDao.getVaChamY();
        }

        Map<ChickenChienBinh, Integer> satThuongTheoMucTieu =
                new LinkedHashMap<ChickenChienBinh, Integer>();
        ChickenChienBinh mucTieu;
        if (shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON) {
            VaChamTiaUltron vaCham = this.timVaChamTiaUltron(
                    shooter,
                    vaChamX,
                    vaChamY
            );
            if (vaCham != null) {
                mucTieu = vaCham.mucTieu;
                short[][] daCat = this.catDuongTiaTaiVaCham(
                        hienThiX,
                        hienThiY,
                        vaCham
                );
                hienThiX = daCat[0];
                hienThiY = daCat[1];
            } else {
                mucTieu = null;
            }
        } else if (idSungMayChu == ChickenQuanLyCongThucSung.ID_SUNG_CAPTAIN) {
            /*
             * Khiên Captain xuyên qua thân người: mỗi nhân vật bị quỹ đạo lướt
             * qua nhận đúng 50% damage. Viên đạn vẫn bay tiếp tới địa hình; nếu
             * điểm nổ nằm dưới chân thì mục tiêu nhận damage đầy đủ, không cộng
             * chồng thêm 50% của lần lướt qua.
             */
            List<ChickenChienBinh> cacMucTieuBiLuotQua =
                    this.timTatCaMucTieuXuyenQua(shooter, vaChamX, vaChamY);
            boolean noTrenDiaHinh = this.diemCuoiLaDiaHinh(vaChamX, vaChamY);
            int xNo = this.layGiaTriCuoi(vaChamX, dauNong[0]);
            int yNo = this.layGiaTriCuoi(vaChamY, dauNong[1]);
            ChickenCauHinhSatThuongSung.HoSoSatThuong hoSoCaptain =
                    ChickenCauHinhSatThuongSung.theoIdSung(idSungMayChu);
            // Captain dung loai dan xuyen nguoi rieng, khong nam trong nhom
            // quy dao duoc tinh sieu cao.
            boolean laSieuCaoCaptain = false;

            for (ChickenChienBinh doiThu : this.chienBinhs) {
                if (doiThu == null || doiThu == shooter || doiThu.chet) {
                    continue;
                }
                int satThuongDayDu = this.tinhSatThuongPhatBanDaBietSieuCao(
                        shooter,
                        doiThu,
                        luc,
                        laSieuCaoCaptain
                );
                int satThuongThucTe = 0;
                if (cacMucTieuBiLuotQua.contains(doiThu)) {
                    satThuongThucTe = Math.max(1, satThuongDayDu / 2);
                }
                if (noTrenDiaHinh) {
                    int satThuongNo = ChickenTinhSatThuongNo.tinhSatThuongChoNhanVat(
                            hoSoCaptain,
                            satThuongDayDu,
                            xNo,
                            yNo,
                            doiThu.x,
                            doiThu.y,
                            doiThu.bot,
                            kiemTraBanDo
                    );
                    satThuongThucTe = Math.max(satThuongThucTe, satThuongNo);
                }
                if (satThuongThucTe > 0) {
                    satThuongTheoMucTieu.put(doiThu, satThuongThucTe);
                }
            }
            mucTieu = satThuongTheoMucTieu.isEmpty()
                    ? null
                    : satThuongTheoMucTieu.keySet().iterator().next();
        } else {
            mucTieu = this.timMucTieuTrung(
                    shooter,
                    vaChamX,
                    vaChamY
            );
        }

        if (mucTieu != null && satThuongTheoMucTieu.isEmpty()) {
            int satThuong = this.tinhSatThuongPhatBan(
                    shooter, mucTieu, loaiDan, hienThiX, hienThiY, luc);
            satThuongTheoMucTieu.put(mucTieu, satThuong);
        }

        return new ChickenKetQuaDan(
                loaiDan,
                dauNong[0],
                dauNong[1],
                goc,
                luc,
                hienThiX,
                hienThiY,
                satThuongTheoMucTieu
        );
    }

    private ChickenKetQuaDan xuLyPhatBanRiu(
            ChickenChienBinh shooter,
            short goc,
            byte luc,
            byte lucPhu
    ) {
        ChickenKetQuaDan danChinhDayDu = this.xuLyPhatBan(
                shooter, (byte) 17, goc, luc);
        int soDiemChinh = Math.min(
                danChinhDayDu.duongX == null ? 0 : danChinhDayDu.duongX.length,
                danChinhDayDu.duongY == null ? 0 : danChinhDayDu.duongY.length
        );
        int buocTach = Math.max(
                1,
                Math.min(lucPhu & 255, Math.max(1, soDiemChinh))
        );
        short[] duongChinhX = soDiemChinh > 0
                ? Arrays.copyOf(danChinhDayDu.duongX, buocTach)
                : new short[]{danChinhDayDu.batDauX};
        short[] duongChinhY = soDiemChinh > 0
                ? Arrays.copyOf(danChinhDayDu.duongY, buocTach)
                : new short[]{danChinhDayDu.batDauY};
        short diemTachX = duongChinhX[duongChinhX.length - 1];
        short diemTachY = duongChinhY[duongChinhY.length - 1];

        ChickenQuanLyDanSung.DuLieuSung sungMayChu =
                ChickenQuanLyDanSung.theoPartSung(shooter.maVuKhi);
        int idSungMayChu = sungMayChu == null ? -1 : sungMayChu.getIdSung();
        ChickenQuanLyCongThucSung.KiemTraBanDo kiemTraBanDo = this.map;

        short[][] cacDuongX = new short[4][];
        short[][] cacDuongY = new short[4][];
        cacDuongX[0] = duongChinhX;
        cacDuongY[0] = duongChinhY;
        for (int i = 0; i < 3; i++) {
            ChickenQuanLyCongThucSung.KetQuaQuyDao danCon =
                    ChickenQuanLyCongThucSung.taoQuyDaoConRiu(
                            diemTachX,
                            diemTachY,
                            shooter.x,
                            shooter.y,
                            goc,
                            luc,
                            i,
                            ChickenHeThongGio.layWindXChoSung(
                                    this.gioHienTai, idSungMayChu),
                            ChickenHeThongGio.layWindYChoSung(
                                    this.gioHienTai, idSungMayChu),
                            kiemTraBanDo
                    );
            cacDuongX[i + 1] = danCon.getHienThiX();
            cacDuongY[i + 1] = danCon.getHienThiY();
        }

        Map<ChickenChienBinh, Integer> satThuongTheoMucTieu =
                new LinkedHashMap<ChickenChienBinh, Integer>();
        for (int i = 0; i < cacDuongX.length; i++) {
            ChickenChienBinh mucTieu = this.timMucTieuTrung(
                    shooter, cacDuongX[i], cacDuongY[i]);
            if (mucTieu == null) {
                continue;
            }
            int satThuongDayDu = this.tinhSatThuongPhatBan(
                    shooter,
                    mucTieu,
                    (byte) 17,
                    cacDuongX[i],
                    cacDuongY[i],
                    luc
            );
            int satThuongMotNhanh = satThuongDayDu / 4
                    + (satThuongDayDu % 4 == 0 ? 0 : 1);
            this.congSatThuongMucTieu(
                    satThuongTheoMucTieu, mucTieu, satThuongMotNhanh);
        }

        return new ChickenKetQuaDan(
                (byte) 17,
                danChinhDayDu.batDauX,
                danChinhDayDu.batDauY,
                goc,
                luc,
                (byte) buocTach,
                cacDuongX,
                cacDuongY,
                satThuongTheoMucTieu
        );
    }

    private ChickenKetQuaDan xuLyPhatBanGa(
            ChickenChienBinh shooter,
            short goc,
            byte luc,
            byte lucPhu
    ) {
        ChickenKetQuaDan danChinh = this.xuLyPhatBan(
                shooter, (byte) 19, goc, luc);
        int soDiemChinh = Math.min(
                danChinh.duongX == null ? 0 : danChinh.duongX.length,
                danChinh.duongY == null ? 0 : danChinh.duongY.length
        );
        int buocTha = Math.max(
                1,
                Math.min(lucPhu & 255, Math.max(1, soDiemChinh))
        );
        int chiSoTha = buocTha - 1;
        short thaX = soDiemChinh > 0
                ? danChinh.duongX[chiSoTha]
                : danChinh.batDauX;
        short thaY = soDiemChinh > 0
                ? danChinh.duongY[chiSoTha]
                : danChinh.batDauY;
        short batDauRoiY = (short) Math.max(
                Short.MIN_VALUE,
                Math.min(Short.MAX_VALUE, thaY + 8)
        );

        ChickenQuanLyDanSung.DuLieuSung sungMayChu =
                ChickenQuanLyDanSung.theoPartSung(shooter.maVuKhi);
        int idSungMayChu = sungMayChu == null ? -1 : sungMayChu.getIdSung();
        ChickenQuanLyCongThucSung.KetQuaQuyDao danRoi =
                ChickenQuanLyCongThucSung.taoQuyDaoDanGaRoi(
                        thaX,
                        batDauRoiY,
                        ChickenHeThongGio.layWindXChoSung(
                                this.gioHienTai, idSungMayChu),
                        ChickenHeThongGio.layWindYChoSung(
                                this.gioHienTai, idSungMayChu),
                        this.map
                );

        Map<ChickenChienBinh, Integer> satThuongTheoMucTieu =
                new LinkedHashMap<ChickenChienBinh, Integer>();
        for (Map.Entry<ChickenChienBinh, Integer> entry
                : danChinh.satThuongTheoMucTieu.entrySet()) {
            int satThuongNua = entry.getValue() / 2 + entry.getValue() % 2;
            this.congSatThuongMucTieu(
                    satThuongTheoMucTieu, entry.getKey(), satThuongNua);
        }

        ChickenChienBinh mucTieuDanRoi = this.timMucTieuTrung(
                shooter,
                danRoi.getVaChamX(),
                danRoi.getVaChamY()
        );
        if (mucTieuDanRoi != null) {
            int satThuongDayDu = this.tinhSatThuongPhatBan(
                    shooter,
                    mucTieuDanRoi,
                    (byte) 19,
                    danRoi.getHienThiX(),
                    danRoi.getHienThiY(),
                    luc
            );
            int satThuongNua = satThuongDayDu / 2 + satThuongDayDu % 2;
            this.congSatThuongMucTieu(
                    satThuongTheoMucTieu, mucTieuDanRoi, satThuongNua);
        }

        return new ChickenKetQuaDan(
                (byte) 19,
                danChinh.batDauX,
                danChinh.batDauY,
                goc,
                luc,
                (byte) buocTha,
                danChinh.duongX,
                danChinh.duongY,
                danRoi.getHienThiX(),
                danRoi.getHienThiY(),
                satThuongTheoMucTieu
        );
    }

    private void congSatThuongMucTieu(
            Map<ChickenChienBinh, Integer> satThuongTheoMucTieu,
            ChickenChienBinh mucTieu,
            int satThuong
    ) {
        if (mucTieu == null || satThuong <= 0) {
            return;
        }
        int hienTai = satThuongTheoMucTieu.containsKey(mucTieu)
                ? satThuongTheoMucTieu.get(mucTieu)
                : 0;
        long tong = (long) hienTai + satThuong;
        satThuongTheoMucTieu.put(
                mucTieu,
                tong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) tong
        );
    }

    private int tinhSatThuongPhatBan(
            ChickenChienBinh shooter,
            ChickenChienBinh mucTieu,
            byte loaiDan,
            short[] duongX,
            short[] duongY,
            byte luc
    ) {
        return this.tinhSatThuongPhatBanDaBietSieuCao(
                shooter,
                mucTieu,
                luc,
                false
        );
    }

    private int tinhSatThuongPhatBanDaBietSieuCao(
            ChickenChienBinh shooter,
            ChickenChienBinh mucTieu,
            byte luc,
            boolean laSieuCao
    ) {
        int tanCong = shooter.tanCong > 0
                ? shooter.tanCong
                : 20 + luc / 2;
        int satThuong = Math.max(1, tanCong - mucTieu.giap);
        if (laSieuCao) {
            satThuong = ChickenSieuCao.tangSatThuong(satThuong);
        }
        return satThuong;
    }

    private int layIdSung(short partSung) {
        ChickenQuanLyDanSung.DuLieuSung duLieu = ChickenQuanLyDanSung.theoPartSung(partSung);
        return duLieu == null ? -1 : duLieu.getIdSung();
    }

    private byte layLoaiDanAnToan(byte loaiDan) {
        switch (loaiDan) {
            case 0:
            case 7:
            case 8:
            case 13:
            case 21:
            case 22:
            case 25:
            case 30:
            case 34:
            case 35:
            case 42:
            case 45:
            case 50:
            case 51:
            case 52:
            case 54:
            case 55:
            case 57:
            case 58:
                return loaiDan;
            default:
                return 0;
        }
    }

    private void lapLichBotBan() {
        this.dungBot();
        this.tacVuBot = BOT_EXECUTOR.scheduleWithFixedDelay(() -> {
            try {
                this.nhipBot();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 700L, 700L, TimeUnit.MILLISECONDS);
    }

    private void batDauChoKetThucPhatBan(
            ChickenChienBinh shooter,
            ChickenKetQuaDan ketQua,
            ChickenMayMan.PhienTanCong phienMayMan
    ) {
        this.batDauChoKetThucPhatBan(
                shooter, ketQua, phienMayMan, -1);
    }

    private void batDauChoKetThucPhatBan(
            ChickenChienBinh shooter,
            ChickenKetQuaDan ketQua,
            ChickenMayMan.PhienTanCong phienMayMan,
            int napDanSauHanhDong
    ) {
        if (shooter == null || ketQua == null || phienMayMan == null
                || this.phatBanDangCho != null) {
            throw new IllegalStateException(
                    "Khong the tao hai phat ban dang cho trong cung tran");
        }
        long maPhatBan = ++this.maPhatBanTiepTheo;
        long bayGio = System.currentTimeMillis();
        PhatBanDangCho dangCho = new PhatBanDangCho(
                maPhatBan,
                shooter,
                ketQua,
                phienMayMan,
                napDanSauHanhDong,
                shooter.layVatPhamChienDangCho() != null,
                bayGio + ChickenThoiGianHoatAnhDan.TOI_THIEU_MS);
        this.phatBanDangCho = dangCho;

        boolean critical = shooter.nguoiChoi != null
                && shooter.nguoiChoi.dangHienHieuUngPow();
        long thoiGianDuPhong =
                ChickenThoiGianHoatAnhDan.tinh(ketQua, critical);
        dangCho.tacVuDuPhong = BOT_EXECUTOR.schedule(() -> {
            synchronized (ChickenQuanLyChien.this) {
                if (ChickenQuanLyChien.this.phatBanDangCho != dangCho
                        || dangCho.maPhatBan != maPhatBan) {
                    return;
                }
                try {
                    ChickenQuanLyChien.this
                            .chotPhatBanDangCho(dangCho, true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }, thoiGianDuPhong, TimeUnit.MILLISECONDS);
    }

    private boolean xuLyXacNhanKetThucPhatBan(
            ChickenNguoiChoi nguoiChoi
    ) throws IOException {
        PhatBanDangCho dangCho = this.phatBanDangCho;
        ChickenChienBinh nguoiGui = this.layChienBinh(nguoiChoi);
        if (dangCho == null || nguoiGui == null || !nguoiGui.coPhien()
                || nguoiChoi == null) {
            return false;
        }
        dangCho.nguoiDaXacNhan.add(nguoiChoi.ma);
        if (!this.daDuXacNhanKetThucDan(dangCho)) {
            return true;
        }

        long conLaiMs = dangCho.xacNhanSomNhatMs
                - System.currentTimeMillis();
        if (conLaiMs > 0L) {
            this.henChotPhatBanSauMocToiThieu(dangCho, conLaiMs);
            return true;
        }
        this.chotPhatBanDangCho(dangCho, false);
        return true;
    }

    private boolean daDuXacNhanKetThucDan(
            PhatBanDangCho dangCho
    ) {
        boolean coNguoiQuanSat = false;
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh == null || !chienBinh.coPhien()
                    || chienBinh.nguoiChoi == null) {
                continue;
            }
            coNguoiQuanSat = true;
            if (!dangCho.nguoiDaXacNhan
                    .contains(chienBinh.nguoiChoi.ma)) {
                return false;
            }
        }
        return coNguoiQuanSat;
    }

    private void henChotPhatBanSauMocToiThieu(
            PhatBanDangCho dangCho,
            long conLaiMs
    ) {
        if (dangCho.tacVuXacNhanSom != null) {
            return;
        }
        dangCho.tacVuXacNhanSom = BOT_EXECUTOR.schedule(() -> {
            synchronized (ChickenQuanLyChien.this) {
                dangCho.tacVuXacNhanSom = null;
                if (ChickenQuanLyChien.this.phatBanDangCho != dangCho
                        || !ChickenQuanLyChien.this
                                .daDuXacNhanKetThucDan(dangCho)) {
                    return;
                }
                try {
                    ChickenQuanLyChien.this
                            .chotPhatBanDangCho(dangCho, false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }, Math.max(1L, conLaiMs), TimeUnit.MILLISECONDS);
    }

    private void chotPhatBanDangCho(
            PhatBanDangCho dangCho,
            boolean doHetHan
    ) throws IOException {
        if (dangCho == null || this.phatBanDangCho != dangCho) {
            return;
        }
        this.phatBanDangCho = null;
        if (dangCho.tacVuDuPhong != null) {
            dangCho.tacVuDuPhong.cancel(false);
            dangCho.tacVuDuPhong = null;
        }
        if (dangCho.tacVuXacNhanSom != null) {
            dangCho.tacVuXacNhanSom.cancel(false);
            dangCho.tacVuXacNhanSom = null;
        }
        if (this.daKetThuc) {
            return;
        }

        if (!dangCho.laPhatVatPham && this.dongBoHulkSauPhat(
                dangCho.nguoiBan, dangCho.ketQua)) {
            return;
        }
        this.phaDiaHinhNeuCan(dangCho.ketQua);
        this.apDungSatThuongKetQua(
                dangCho.phienMayMan, dangCho.ketQua);
        this.kyNangHawk.sauKhiBanThuong(dangCho.nguoiBan);
        if (!this.daKetThuc) {
            this.sangLuot(dangCho.napDanSauHanhDong);
        }
        if (doHetHan) {
            System.out.println("[SHOT][TIMEOUT] ma=" + dangCho.maPhatBan
                    + " shooter=" + (dangCho.nguoiBan.chiSo & 0xFF)
                    + " serverTuChot=true");
        }
    }

    private synchronized void nhipBot() throws IOException {
        if (this.daKetThuc) {
            this.dungBot();
            return;
        }
        if (this.ultronX3KetQua != null || this.phatBanDangCho != null) {
            return;
        }
        ChickenChienBinh turn = this.luotHienTai >= 0 && this.luotHienTai < this.chienBinhs.length ? this.chienBinhs[this.luotHienTai] : null;
        if (turn == null || turn.chet) {
            this.sangLuot();
            return;
        }
        if (turn.bot) {
            this.diChuyenBotTruocKhiBan(turn);
            ChickenChienBinh mucTieu = this.timMucTieuGanNhat(turn);
            if (mucTieu != null) {
                short goc = this.gocToiMucTieu(turn, mucTieu);
                ChickenKetQuaDan ketQua = this.xuLyPhatBan(turn, (byte)0, goc, (byte)18);
                ChickenMayMan.PhienTanCong phienMayMan =
                        ChickenMayMan.batDau(turn, this.chienBinhs);
                phienMayMan.chuanBiPhongThuTruocPhat(
                        ketQua.satThuongTheoMucTieu.keySet());
                this.phatBan(turn, ketQua, (byte)1);
                this.batDauChoKetThucPhatBan(
                        turn, ketQua, phienMayMan);
                return;
            }
            if (!this.daKetThuc) {
                this.sangLuot();
            }
        } else if (System.currentTimeMillis() > this.hanLuot) {
            // Chưa có phát đạn authoritative: kéo lực/ngắm/dùng item buff rồi
            // hết giờ đều là bỏ lượt, không được lấy nạp của khẩu súng.
            this.sangLuot(ChickenNapDanServer.layKhiKhongTaoPhatDan());
        }
    }

    private void diChuyenBotTruocKhiBan(ChickenChienBinh bot) throws IOException {
        int shift = bot.chiSo % 2 == 0 ? 28 : -28;
        bot.x = this.kepShort((short)(bot.x + shift), 40, this.map.getWidth() - 40);
        this.phatDiChuyen(bot);
    }

    private ChickenChienBinh timMucTieuGanNhat(ChickenChienBinh shooter) {
        ChickenChienBinh best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (ChickenChienBinh mucTieu : this.chienBinhs) {
            if (mucTieu == null || mucTieu == shooter || mucTieu.chet || shooter.bot && mucTieu.bot) {
                continue;
            }
            int dx = mucTieu.x - shooter.x;
            int dy = mucTieu.y - shooter.y;
            int distance = dx * dx + dy * dy;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = mucTieu;
            }
        }
        return best;
    }

    private short gocToiMucTieu(ChickenChienBinh shooter, ChickenChienBinh mucTieu) {
        double radians = Math.atan2(shooter.y - mucTieu.y, mucTieu.x - shooter.x);
        int degrees = (int)Math.round(Math.toDegrees(radians));
        if (degrees < 0) {
            degrees += 360;
        }
        return (short)degrees;
    }

    private short layGiaTriCuoi(short[] duong, short macDinh) {
        return duong == null || duong.length == 0 ? macDinh : duong[duong.length - 1];
    }

    private static final class VaChamTiaUltron {
        private final ChickenChienBinh mucTieu;
        private final int chiSoDoan;
        private final short hitX;
        private final short hitY;

        private VaChamTiaUltron(
                ChickenChienBinh mucTieu,
                int chiSoDoan,
                short hitX,
                short hitY
        ) {
            this.mucTieu = mucTieu;
            this.chiSoDoan = chiSoDoan;
            this.hitX = hitX;
            this.hitY = hitY;
        }
    }

    private static final class PhatBanDangCho {
        private final long maPhatBan;
        private final ChickenChienBinh nguoiBan;
        private final ChickenKetQuaDan ketQua;
        private final ChickenMayMan.PhienTanCong phienMayMan;
        private final int napDanSauHanhDong;
        private final boolean laPhatVatPham;
        private final long xacNhanSomNhatMs;
        private final Set<Integer> nguoiDaXacNhan = new HashSet<>();
        private ScheduledFuture<?> tacVuXacNhanSom;
        private ScheduledFuture<?> tacVuDuPhong;

        private PhatBanDangCho(
                long maPhatBan,
                ChickenChienBinh nguoiBan,
                ChickenKetQuaDan ketQua,
                ChickenMayMan.PhienTanCong phienMayMan,
                int napDanSauHanhDong,
                boolean laPhatVatPham,
                long xacNhanSomNhatMs
        ) {
            this.maPhatBan = maPhatBan;
            this.nguoiBan = nguoiBan;
            this.ketQua = ketQua;
            this.phienMayMan = phienMayMan;
            this.napDanSauHanhDong = napDanSauHanhDong;
            this.laPhatVatPham = laPhatVatPham;
            this.xacNhanSomNhatMs = xacNhanSomNhatMs;
        }
    }

    /**
     * Cắt tia Ultron tại pixel địa hình đầu tiên. Quét từng pixel giữa hai điểm
     * quỹ đạo nên tia không thể nhảy xuyên qua lớp map mỏng khi bước tia dài.
     */
    private short[][] catDuongTiaTaiVaChamBanDo(short[] xs, short[] ys) {
        if (xs == null || ys == null) {
            return new short[][]{xs, ys};
        }
        int soDiem = Math.min(xs.length, ys.length);
        if (soDiem < 2) {
            return new short[][]{xs, ys};
        }

        for (int i = 1; i < soDiem; i++) {
            int x1 = xs[i - 1];
            int y1 = ys[i - 1];
            int x2 = xs[i];
            int y2 = ys[i];
            int soBuoc = Math.max(
                    1,
                    Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1))
            );

            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                double tiLe = (double) buoc / (double) soBuoc;
                int danX = (int) Math.round(x1 + (x2 - x1) * tiLe);
                int danY = (int) Math.round(y1 + (y2 - y1) * tiLe);

                if (danX < 0 || danY < 0
                        || danX >= this.map.getWidth()
                        || danY >= this.map.getHeight()) {
                    continue;
                }
                if (this.map.coVaCham((short) danX, (short) danY)) {
                    return this.catDuongTiaTaiVaCham(
                            xs,
                            ys,
                            new VaChamTiaUltron(
                                    null,
                                    i,
                                    (short) danX,
                                    (short) danY
                            )
                    );
                }
            }
        }
        return new short[][]{xs, ys};
    }

    /**
     * Tìm va chạm đầu tiên của tia Ultron theo từng pixel trên đoạn thẳng.
     * Kết quả giữ cả tọa độ chạm để cắt phần hiển thị đúng tại thân mục tiêu.
     */
    private VaChamTiaUltron timVaChamTiaUltron(
            ChickenChienBinh shooter,
            short[] xs,
            short[] ys
    ) {
        if (xs == null || ys == null) {
            return null;
        }
        int soDiem = Math.min(xs.length, ys.length);
        if (soDiem < 2) {
            return null;
        }

        for (int i = 1; i < soDiem; i++) {
            int x1 = xs[i - 1];
            int y1 = ys[i - 1];
            int x2 = xs[i];
            int y2 = ys[i];
            int soBuoc = Math.max(
                    1,
                    Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1))
            );

            for (int buoc = i == 1 ? 0 : 1; buoc <= soBuoc; buoc++) {
                double tiLe = (double) buoc / (double) soBuoc;
                int danX = (int) Math.round(x1 + (x2 - x1) * tiLe);
                int danY = (int) Math.round(y1 + (y2 - y1) * tiLe);

                for (ChickenChienBinh mucTieu : this.chienBinhs) {
                    if (mucTieu == null
                            || mucTieu == shooter
                            || mucTieu.chet
                            || (shooter.bot && mucTieu.bot)) {
                        continue;
                    }

                    /*
                     * Tia được quét từng pixel nhưng không được nới hitbox.
                     * Mọi súng đều dùng cùng vùng thân 24x36 của client; phần
                     * sáng của laser chỉ là hiệu ứng và không được tính trúng.
                     */
                    boolean trung = mucTieu.bot
                            ? ChickenKichThuocNhanVat.trungBoss(
                                    danX, danY, mucTieu.x, mucTieu.y)
                            : ChickenKichThuocNhanVat.trungNguoiChoi(
                                    danX, danY, mucTieu.x, mucTieu.y);
                    if (trung) {
                        return new VaChamTiaUltron(
                                mucTieu,
                                i,
                                (short) danX,
                                (short) danY
                        );
                    }
                }
            }
        }
        return null;
    }

    /** Cắt quỹ đạo tại đúng pixel va chạm để laser không xuyên qua mục tiêu. */
    private short[][] catDuongTiaTaiVaCham(
            short[] xs,
            short[] ys,
            VaChamTiaUltron vaCham
    ) {
        if (xs == null || ys == null || vaCham == null) {
            return new short[][]{xs, ys};
        }

        int soDiem = Math.min(xs.length, ys.length);
        int doDai = Math.max(
                2,
                Math.min(soDiem, vaCham.chiSoDoan + 1)
        );
        short[] ketQuaX = new short[doDai];
        short[] ketQuaY = new short[doDai];

        int soDiemSaoChep = doDai - 1;
        System.arraycopy(xs, 0, ketQuaX, 0, soDiemSaoChep);
        System.arraycopy(ys, 0, ketQuaY, 0, soDiemSaoChep);
        ketQuaX[doDai - 1] = vaCham.hitX;
        ketQuaY[doDai - 1] = vaCham.hitY;
        return new short[][]{ketQuaX, ketQuaY};
    }

    private ChickenChienBinh timMucTieuTrung(ChickenChienBinh shooter, short[] xs, short[] ys) {
        if (xs == null || ys == null) {
            return null;
        }
        int soDiem = Math.min(xs.length, ys.length);
        if (soDiem < 2) {
            return null;
        }

        /*
         * Không dùng bán kính 60px quanh nhân vật. Cách cũ làm đạn bay cao
         * hoặc còn cách xa thân vẫn bị xem là trúng. Tọa độ x/y của chiến
         * binh là tâm ngang và vị trí chân, vì vậy chỉ nhận va chạm khi tâm
         * viên đạn thật sự đi vào lõi thân.
         */
        for (int i = 1; i < soDiem; i++) {
            int x1 = xs[i - 1];
            int y1 = ys[i - 1];
            int x2 = xs[i];
            int y2 = ys[i];
            int soBuoc = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
            if (soBuoc <= 0) {
                continue;
            }
            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                double tiLe = (double) buoc / (double) soBuoc;
                int danX = (int) Math.round(x1 + (x2 - x1) * tiLe);
                int danY = (int) Math.round(y1 + (y2 - y1) * tiLe);

                for (ChickenChienBinh mucTieu : this.chienBinhs) {
                    if (mucTieu == null || mucTieu == shooter || mucTieu.chet) {
                        continue;
                    }
                    boolean trung = mucTieu.bot
                            ? ChickenKichThuocNhanVat.trungBoss(danX, danY, mucTieu.x, mucTieu.y)
                            : ChickenKichThuocNhanVat.trungNguoiChoi(danX, danY, mucTieu.x, mucTieu.y);
                    if (trung) {
                        return mucTieu;
                    }
                }
            }
        }
        return null;
    }

    private List<ChickenChienBinh> timTatCaMucTieuXuyenQua(
            ChickenChienBinh shooter,
            short[] xs,
            short[] ys
    ) {
        List<ChickenChienBinh> ketQua = new ArrayList<ChickenChienBinh>();
        if (xs == null || ys == null) {
            return ketQua;
        }
        int soDiem = Math.min(xs.length, ys.length);
        for (int i = 1; i < soDiem; i++) {
            int x1 = xs[i - 1];
            int y1 = ys[i - 1];
            int x2 = xs[i];
            int y2 = ys[i];
            int soBuoc = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                double tiLe = (double) buoc / (double) soBuoc;
                int danX = (int) Math.round(x1 + (x2 - x1) * tiLe);
                int danY = (int) Math.round(y1 + (y2 - y1) * tiLe);
                for (ChickenChienBinh mucTieu : this.chienBinhs) {
                    if (mucTieu == null
                            || mucTieu == shooter
                            || mucTieu.chet
                            || ketQua.contains(mucTieu)) {
                        continue;
                    }
                    boolean trung = mucTieu.bot
                            ? ChickenKichThuocNhanVat.trungBoss(
                                    danX, danY, mucTieu.x, mucTieu.y)
                            : ChickenKichThuocNhanVat.trungNguoiChoi(
                                    danX, danY, mucTieu.x, mucTieu.y);
                    if (trung) {
                        ketQua.add(mucTieu);
                    }
                }
            }
        }
        return ketQua;
    }

    private boolean diemCuoiLaDiaHinh(short[] xs, short[] ys) {
        if (xs == null || ys == null) {
            return false;
        }
        int soDiem = Math.min(xs.length, ys.length);
        if (soDiem <= 0) {
            return false;
        }
        short x = xs[soDiem - 1];
        short y = ys[soDiem - 1];
        return x >= 0 && y >= 0
                && x < this.map.getWidth()
                && y < this.map.getHeight()
                && this.map.coVaCham(x, y);
    }

    /** Dong bo cung mat na lo ma client tao sau khi animation ket thuc. */
    private void phaDiaHinhNeuCan(ChickenKetQuaDan ketQua) {
        if (ketQua == null) {
            return;
        }
        int soDuong = Math.min(
                ketQua.cacDuongX.length, ketQua.cacDuongY.length);
        boolean daPhaDiaHinh = false;
        for (int i = 0; i < soDuong; i++) {
            int loaiDanTaoLo = ChickenLoaiDanPhaDiaHinhClient
                    .layLoaiDanTaoLo(ketQua.loaiDan, i);
            if (loaiDanTaoLo
                    == ChickenLoaiDanPhaDiaHinhClient.KHONG_PHA_DIA_HINH) {
                continue;
            }
            short[] xs = ketQua.cacDuongX[i];
            short[] ys = ketQua.cacDuongY[i];
            daPhaDiaHinh |= this.map.phaDiaHinhTheoDuongDan(
                    xs, ys, (byte) loaiDanTaoLo);
        }
        if (daPhaDiaHinh) {
            ChickenTrongLucDiaHinhServer.dongBoYSauPhaDiaHinh(
                    this.map, this.chienBinhs,
                    ChickenCoCheBayAVG::coTheBay);
        }
    }

    private void apDungSatThuongKetQua(
            ChickenMayMan.PhienTanCong phienMayMan,
            ChickenKetQuaDan ketQua
    ) throws IOException {
        if (ketQua == null) {
            return;
        }
        if (phienMayMan == null) {
            return;
        }
        for (Map.Entry<ChickenChienBinh, Integer> entry
                : ketQua.satThuongTheoMucTieu.entrySet()) {
            ChickenChienBinh mucTieu = entry.getKey();
            int satThuong = phienMayMan.apDung(mucTieu, entry.getValue());
            if (mucTieu != null && !mucTieu.chet && satThuong > 0) {
                this.satThuong(mucTieu, satThuong);
            }
        }
    }

    /** Hulk bay theo duong dan tren client, nen server cung phai chot vi tri. */
    private boolean dongBoHulkSauPhat(
            ChickenChienBinh shooter,
            ChickenKetQuaDan ketQua
    ) throws IOException {
        boolean roiKhoiMap = ChickenCoCheHulk.apDungViTriCuoi(
                shooter, ketQua, this.map.getWidth(), this.map.getHeight());
        if (!roiKhoiMap) {
            return false;
        }
        // Khong tin client bao roi map: toa do chet duoc suy ra tu quy dao server.
        this.satThuong(shooter, Math.max(1, shooter.hp));
        return this.daKetThuc;
    }

    private void satThuong(ChickenChienBinh mucTieu, int satThuong) throws IOException {
        int hpTruoc = Math.max(0, mucTieu.hp);
        mucTieu.hp -= satThuong;
        if (mucTieu.hp <= 0) {
            mucTieu.hp = 0;
            mucTieu.chet = true;
        }
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiCapNhatMauDau(mucTieu.chiSo, mucTieu.hp, mucTieu.phanTramMau(), mucTieu.chet ? (byte)2 : (byte)0);
            }
        }
        this.ghiNhanPowSauSatThuong(mucTieu, hpTruoc, this.chienBinhs);
        this.kiemTraThang();
    }

    private void kiemTraThang() throws IOException {
        boolean pheChanConSong = false;
        boolean pheLeConSong = false;
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && !chienBinh.chet) {
                if ((chienBinh.chiSo & 1) == 0) {
                    pheChanConSong = true;
                } else {
                    pheLeConSong = true;
                }
            }
        }
        if (pheChanConSong && pheLeConSong) {
            return;
        }
        byte pheThang = (byte) (pheLeConSong ? 1 : 0);
        this.daKetThuc = true;
        this.dungBot();
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                if (!chienBinh.chet) {
                    chienBinh.nguoiChoi.kill++;
                    chienBinh.nguoiChoi.cup += 1;
                } else {
                    chienBinh.nguoiChoi.chet++;
                }
                chienBinh.nguoiChoi.dichVu.guiDongMenuKyNangDacBiet();
                chienBinh.nguoiChoi.dichVu.guiKetThucDau(pheThang, 10, 100, 0);
                chienBinh.nguoiChoi.dichVu.capNhatCup((byte)0, chienBinh.nguoiChoi.cup);
                chienBinh.nguoiChoi.dichVu.capNhatKDVaKDA();
            }
        }
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.nguoiChoi != null) {
                this.boDangKyNguoiChoi(chienBinh.nguoiChoi);
            }
        }
        this.wait.ketThucDau();
    }

    private void sangLuot() throws IOException {
        this.sangLuot(-1);
    }

    /**
     * Chuyển lượt với một giá trị nạp đạn do server ấn định cho hành động hiện
     * tại. Giá trị âm giữ nguyên cách tính theo súng; bỏ lượt truyền mốc nhanh
     * nhất 250 để không phụ thuộc khẩu súng đang cầm.
     */
    private void sangLuot(int napDanSauHanhDong) throws IOException {
        int slotVuaCoLuot = this.luotHienTai & 0xFF;
        this.map.ketThucLuotVoiRong(
                slotVuaCoLuot >= 0
                        && slotVuaCoLuot < this.chienBinhs.length
                        && this.chienBinhs[slotVuaCoLuot] != null
                        && this.chienBinhs[slotVuaCoLuot].nguoiChoi != null);
        if (this.luotHienTai >= 0 && this.luotHienTai < this.chienBinhs.length) {
            ChickenChienBinh vuaCoLuot = this.chienBinhs[this.luotHienTai];
            this.huyPowSauLuot(vuaCoLuot, this.chienBinhs);
            if (vuaCoLuot != null
                    && vuaCoLuot.avenger == ChickenKyNangDacBietLoki.AVG_LOKI
                    && !vuaCoLuot.lokiSkillActive) {
                vuaCoLuot.lokiDangChoChonMucTieu = false;
                if (!vuaCoLuot.lokiDaDungKyNang) {
                    vuaCoLuot.lokiDaGuiMenu = false;
                }
            }
            if (vuaCoLuot != null) {
                ChickenTrangThaiHanhDongLuot.ketThucLuot(vuaCoLuot);
                this.kyNangUltron.huyKhiBoLuot(vuaCoLuot);
                this.kyNangIronMan.sauKhiBanHoacBoLuot(vuaCoLuot);
                if (!vuaCoLuot.chet) {
                    this.napDan[slotVuaCoLuot] =
                            napDanSauHanhDong >= 0
                                    ? Math.max(
                                            ChickenNapDanServer.TOI_THIEU,
                                            napDanSauHanhDong)
                                    : ChickenNapDanServer.layChoChienBinh(
                                            vuaCoLuot);
                    this.boDemThuTuHanhDongNapDan =
                            ChickenHangDoiNapDan.ghiNhanHanhDong(
                                    this.thuTuHanhDongNapDan,
                                    slotVuaCoLuot,
                                    this.boDemThuTuHanhDongNapDan);
                }
            }
        }
        this.luotHienTai = this.timLuotTheoNapDan(slotVuaCoLuot);
        this.sendNextTurn();
    }

    private byte timLuotTheoNapDan(int sauSlot) {
        return (byte) ChickenHangDoiNapDan.timSlotTiepTheo(
                this.napDan,
                this.thuTuHanhDongNapDan,
                sauSlot,
                slot -> {
                    ChickenChienBinh chienBinh = this.chienBinhs[slot];
                    return chienBinh != null && !chienBinh.chet;
                });
    }

    private byte nguoiSongTiepTu(byte from) {
        for (int step = 1; step <= this.chienBinhs.length; step++) {
            int chiSo = (from + step + this.chienBinhs.length) % this.chienBinhs.length;
            ChickenChienBinh chienBinh = this.chienBinhs[chiSo];
            if (chienBinh != null && !chienBinh.chet) {
                return (byte)chiSo;
            }
        }
        return -1;
    }

    private void sendNextTurn() throws IOException {
        if (this.daKetThuc || this.luotHienTai < 0) {
            return;
        }
        this.dongMenuKyNangKhiDoiLuot(this.chienBinhs);
        ChickenChienBinh next = this.chienBinhs[this.luotHienTai];
        if (next.avenger == ChickenKyNangDacBietIronMan.AVG_IRON_MAN) {
            next.ironManDaDungKyNang = false;
            next.ironManDaGuiMenu = false;
            next.ironManLaserSanSang = false;
        }
        // Client tự xóa phần thanh đã dùng khi nhận CMD 24; server hồi cùng lúc.
        next.hoiDayQuangDuongDiChuyen();
        this.gioHienTai = ChickenHeThongGio.taoGioMoi();
        this.hanLuot = System.currentTimeMillis() + TURN_SECONDS * 1000L;
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiGio(this.gioHienTai.getWindX(), this.gioHienTai.getWindY());
                chienBinh.nguoiChoi.dichVu.guiLuotDauTiep(
                        this.luotHienTai,
                        next.x,
                        next.y,
                        this.chienBinhConSong(),
                        this.napDan,
                        this.thuTuHanhDongNapDan,
                        (byte) TURN_SECONDS);
            }
        }
        this.guiLaiBaloChoNguoiBatDauLuot(next);
        this.kyNangHawk.guiTinHieuChonMucTieuNeuCo(next);
        this.kyNangThor.guiTinHieuKyNangNeuCo(next);
        this.kyNangLoki.guiTinHieuKyNangNeuCo(next);
        this.kyNangUltron.guiTinHieuKyNangNeuCo(next);
        this.kyNangIronMan.guiTinHieuKyNangNeuCo(next);
    }

    /**
     * Client cu tu an item sau khi dung trong luot. Gui lai Balo sau CMD doi
     * luot de item con quota hien lai o luot ke tiep, khong thay doi kho hay
     * quyen su dung do server quan ly.
     */
    protected final void guiLaiBaloChoNguoiBatDauLuot(
            ChickenChienBinh chienBinh
    ) throws IOException {
        if (chienBinh != null && chienBinh.coPhien()
                && chienBinh.laNguoiChoiThat()
                && chienBinh.coVatPhamChienCanHienLai()) {
            chienBinh.nguoiChoi.dichVu.guiBaloTrongTran(chienBinh);
        }
    }

    /**
     * Moi menu skill chi co hieu luc trong dung mot luot. Dong InfoDlg tren
     * client va xoa cac co "da mo menu" truoc khi phat CMD 24 cua luot moi.
     * Cac co da dung/progress skill van do tung skill quan ly.
     */
    protected final void dongMenuKyNangKhiDoiLuot(
            ChickenChienBinh[] danhSach
    ) {
        if (danhSach == null) {
            return;
        }
        for (ChickenChienBinh chienBinh : danhSach) {
            if (chienBinh == null) {
                continue;
            }
            chienBinh.hawkDaGuiChonMucTieu = false;
            chienBinh.thorDaGuiMenu = false;
            chienBinh.lokiDaGuiMenu = false;
            chienBinh.lokiDangChoChonMucTieu = false;
            chienBinh.ultronDaGuiMenu = false;
            chienBinh.ultronDangBanX3 = false;
            chienBinh.ironManDaGuiMenu = false;
            ChickenKyNangDacBietIronMan.xoaTrangThaiChoBan(chienBinh);
            if (chienBinh.coPhien()
                    && chienBinh.nguoiChoi.dichVu != null) {
                chienBinh.nguoiChoi.dichVu.guiDongMenuKyNangDacBiet();
            }
        }
    }

    private void banLaserIronMan(
            ChickenChienBinh shooter,
            short goc
    ) throws IOException {
        ChickenTiaLaserIronMan.KetQua ketQua =
                ChickenTiaLaserIronMan.taoTrongTran(
                        shooter,
                        this.chienBinhs,
                        goc,
                        this.map.getWidth(),
                        this.map.getHeight()
                );
        ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(shooter, this.chienBinhs);
        int chiSoMucTieu = ketQua.getChiSoMucTieu();
        if (chiSoMucTieu >= 0 && chiSoMucTieu < this.chienBinhs.length) {
            phienMayMan.chuanBiPhongThuTruocPhat(
                    this.chienBinhs[chiSoMucTieu]);
        }
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiTiaLaserIronManDau(
                        shooter.chiSo,
                        shooter.x,
                        shooter.y,
                        goc,
                        ketQua.getBatDauX(),
                        ketQua.getBatDauY(),
                        ketQua.getKetThucX(),
                        ketQua.getKetThucY()
                );
            }
        }

        if (chiSoMucTieu >= 0 && chiSoMucTieu < this.chienBinhs.length) {
            ChickenChienBinh mucTieu = this.chienBinhs[chiSoMucTieu];
            if (mucTieu != null && mucTieu != shooter
                    && !mucTieu.chet && mucTieu.hp > 0) {
                int satThuong = ChickenTiaLaserIronMan.tinhSatThuongNhuHawk(
                        shooter.tanCong, mucTieu.giap);
                satThuong = phienMayMan.apDung(mucTieu, satThuong);
                this.satThuong(mucTieu, satThuong);
            }
        }
        System.out.println("[IRON_MAN] BAN_LASER shooter="
                + (shooter.chiSo & 0xFF)
                + " goc=" + goc
                + " target=" + chiSoMucTieu
                + " boQuaDiaHinh=true");
    }

    private ChickenChienBinh[] chienBinhConSong() {
        return this.chienBinhs;
    }

    private void phatBienHinhLoki(
            ChickenChienBinh loki,
            ChickenChienBinh mucTieu
    ) {
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiBienHinhLoki(
                        loki.chiSo, mucTieu.chiSo);
            }
        }
    }

    private void phatCapNhatMau(ChickenChienBinh mucTieu) throws IOException {
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiCapNhatMauDau(
                        mucTieu.chiSo,
                        mucTieu.hp,
                        mucTieu.phanTramMau(),
                        mucTieu.chet ? (byte)2 : (byte)0
                );
            }
        }
    }

    private void phatDiChuyen(ChickenChienBinh moved) throws IOException {
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiDiChuyenDau(moved.chiSo, moved.x, moved.y);
            }
        }
    }

    private void phatCapNhatXY(ChickenChienBinh moved) throws IOException {
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiCapNhatXYLuyenTap(moved.chiSo, moved.x, moved.y);
            }
        }
    }

    private void phatBan(ChickenChienBinh shooter, ChickenKetQuaDan ketQua, byte numShoot) {
        boolean neoDiemDauQuyDao =
                shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON;
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                try {
                    this.guiLaiVatPhamChienTruocPhatBan(
                            shooter, chienBinh.nguoiChoi);
                    chienBinh.nguoiChoi.dichVu.guiKetQuaBanDau(
                            shooter.chiSo,
                            shooter.x,
                            shooter.y,
                            ketQua,
                            numShoot,
                            neoDiemDauQuyDao,
                            shooter.nguoiChoi != null
                                    && shooter.nguoiChoi.dangHienHieuUngPow()
                    );
                } catch (IOException ex) {
                    System.err.println("[SHOT][GUI_LOI] receiver="
                            + chienBinh.nguoiChoi.ma + " shooter="
                            + (shooter.chiSo & 0xFF) + " loai="
                            + ex.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * Client xoa CPlayer.itemUsed ngay sau khi gui y dinh ban. Phat lai CMD 26
     * sat truoc CMD 22 de client phan loai day la dan cua vat pham, khong roi
     * vao nhanh animation cua sung/AVG dang cam (dac biet Hulk gun 12).
     */
    protected final void guiLaiVatPhamChienTruocPhatBan(
            ChickenChienBinh shooter,
            ChickenNguoiChoi nguoiNhan
    ) throws IOException {
        ChickenChienBinh.VatPhamChienTrongTran vatPham =
                shooter == null ? null : shooter.layVatPhamChienDangCho();
        if (vatPham == null || nguoiNhan == null) {
            return;
        }
        nguoiNhan.dichVu.guiDungVatPhamLuyenTap(
                shooter.chiSo,
                (byte) vatPham.getCauHinh().getMaSuDung(),
                vatPham.getIcon());
    }

    /**
     * Phát đúng một loạt Hawk gồm bốn quỹ đạo. Bullet type 37 của client vẽ
     * sprite /eff/muiten.png cho từng đường; soPhat luôn là 1 nên sau mũi thứ
     * tư client dừng hẳn, không khởi động thêm loạt mới.
     */
    private void phatHoatAnhMuiTenHawk(
            ChickenChienBinh hawk,
            short goc,
            ChickenHoatAnhHawk.DuongDan duongDan
    ) throws IOException {
        if (hawk == null || duongDan == null
                || duongDan.getX() == null || duongDan.getY() == null
                || duongDan.getX().length == 0 || duongDan.getY().length == 0) {
            return;
        }

        ChickenHoatAnhHawk.LoatDuongDan loat =
                ChickenHoatAnhHawk.taoLoatBonMuiNoiDuoi(duongDan);
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiLoatMuiTenHawkDau(
                        hawk.chiSo,
                        ChickenHoatAnhHawk.LOAI_DAN_MUI_TEN,
                        hawk.x,
                        hawk.y,
                        goc,
                        ChickenHoatAnhHawk.LUC_HIEN_THI,
                        loat.getX(),
                        loat.getY(),
                        hawk.nguoiChoi != null
                                && hawk.nguoiChoi.dangHienHieuUngPow()
                );
            }
        }
    }

    /** Phát bốn tia sét Thor tới toàn bộ client trong trận. */
    private void phatTiaSetThor(
            ChickenChienBinh thor,
            byte loaiHieuUng,
            short[] cacX,
            short[] cacY
    ) throws IOException {
        if (thor == null || cacX == null || cacY == null) {
            return;
        }
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiTiaSetThor(
                        thor.chiSo, loaiHieuUng, cacX, cacY);
            }
        }
    }

    private ChickenChienBinh layChienBinh(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return null;
        }
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.nguoiChoi == nguoiChoi) {
                return chienBinh;
            }
        }
        return null;
    }

    private short kepShort(short giaTri, int nhoNhat, int lonNhat) {
        int v = giaTri;
        if (v < nhoNhat) {
            v = nhoNhat;
        }
        if (v > lonNhat) {
            v = lonNhat;
        }
        return (short)v;
    }
}
