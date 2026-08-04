package com.chicken.mohinh;

import com.chicken.chiso.ChickenKichThuocNhanVat;
import com.chicken.avg.ChickenHoatAnhHawk;
import com.chicken.avg.ChickenKyNangDacBietThor;
import com.chicken.avg.ChickenKyNangDacBietLoki;
import com.chicken.avg.ChickenKyNangDacBietUltron;
import com.chicken.avg.ChickenKyNangDacBietIronMan;
import com.chicken.avg.ChickenTiaLaserIronMan;
import com.chicken.avg.ChickenCongThucBanUltron;
import com.chicken.avg.ChickenGocBanUltron;
import com.chicken.avg.ChickenQuanLyNangLuongAVG;
import com.chicken.avg.ChickenCoCheBayAVG;
import com.chicken.avg.ChickenCoCheHulk;
import com.chicken.avg.ChickenSatThuongLanKyNang;
import com.chicken.avg.ChickenThanhDiChuyenAVG;
import com.chicken.chien.ChickenYeuCauToaDoServer;
import com.chicken.chien.ChickenDiChuyenServer;
import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenDieuKienVaoTran;
import com.chicken.chien.ChickenCheDoTestChienDau;
import com.chicken.chien.ChickenKetQuaDan;
import com.chicken.chien.ChickenLoaiDanPhaDiaHinhClient;
import com.chicken.chien.ChickenMayMan;
import com.chicken.chien.ChickenCongThucVatPhamChien;
import com.chicken.chien.ChickenPhatBanServer;
import com.chicken.chien.ChickenPhatBanVatPhamServer;
import com.chicken.chien.ChickenHieuUngVatPhamChien;
import com.chicken.chien.ChickenYeuCauBanServer;
import com.chicken.chien.ChickenTrangThaiHanhDongLuot;
import com.chicken.chien.ChickenCuuThuongBanThan;
import com.chicken.chien.ChickenCuuThuongDongDoi;
import com.chicken.chien.ChickenDiChuyenX2;
import com.chicken.chien.ChickenNgungGio;

import static com.chicken.luyentap.ChickenCauHinhLuyenTap.*;

import com.chicken.chien.ChickenQuanLyCongThucSung;
import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.chien.ChickenCauHinhSatThuongSung;
import com.chicken.chien.ChickenTinhSatThuongNo;
import com.chicken.chien.ChickenSieuCao;
import com.chicken.chien.ChickenLoatDanServer;
import com.chicken.chien.ChickenNguCanhLaySung;
import com.chicken.chien.ChickenNapDanServer;
import com.chicken.chien.ChickenThoiGianHoatAnhDan;
import com.chicken.chien.ChickenXacNhanKetThucDan;
import com.chicken.chiso.ChickenChiSoNguoiChoi;
import com.chicken.gio.ChickenHeThongGio;
import com.chicken.bando.ChickenQuanLyBanDo;
import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenGoiMuaVatPham;
import com.chicken.vatpham.ChickenYeuCauCapVatPham;
import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.chien.ChickenQuanLyDanSung.DuLieuSung;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.nhapvai.ChickenBanDoRPG;
import com.chicken.nhapvai.ChickenKhu;
import com.chicken.phong.ChickenQuanLyPhong;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.trandau.ChickenSungShopBoss;
import com.chicken.chien.ChickenHangDoiNapDan;
import com.chicken.phong.boss.trandau.baovay.BossBanSung;
import com.chicken.npc.chihuy.XuLyMenuChiHuy;
import com.chicken.cuahang.ChickenTrang;
import com.chicken.cuahang.ChickenCuaHang;
import com.chicken.tienich.ChickenTienIch;
import com.chicken.npc.ChickenXuLyNpc;
import com.chicken.npc.chihuy.NpcChiHuy;
import com.chicken.luyentap.ChickenLuyenTapBan;
import com.chicken.luyentap.ChickenLuyenTapToaDo;
import com.chicken.luyentap.ChickenDuLieuPhatBanLuyenTap;
import com.chicken.luyentap.ChickenXuLyBanLuyenTap;
import com.chicken.luyentap.ChickenAiLuyenTap;
import com.chicken.tiemnang.ChickenQuanLyTiemNang;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChickenNguoiChoi {
    public static final int SO_O_TUI_DO = 100;
    /** Balô chiến đấu của client chỉ hỗ trợ tối đa 5 ô (các cấp 2-5 ô). */
    public static final int SO_O_BALO_TOI_DA = 5;
    /** Thuốc tẩy điểm là vật phẩm hệ thống, chỉ được giữ trong túi đồ. */
    public static final int MA_THUOC_TAY_DIEM = 256;
    public static final int SO_O_RUONG_DO = 100;
    private static final ScheduledExecutorService TRAINING_BOT_EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "training-bot");
        thread.setDaemon(true);
        return thread;
    });
    /** Khớp byte 25 giây trong packet CMD 24 của luyện tập. */
    private static final long TRAINING_PLAYER_TURN_TIMEOUT_MS = 25_000L;
    private static final int HAWK_SO_MUI_TEN = ChickenHoatAnhHawk.SO_MUI_TEN;
    private static final byte AVG_HAWK = 7;
    private static final byte AVG_THOR = ChickenKyNangDacBietThor.AVG_THOR;
    private static final byte AVG_LOKI = ChickenKyNangDacBietLoki.AVG_LOKI;
    private static final byte AVG_ULTRON = ChickenKyNangDacBietUltron.AVG_ULTRON;
    private static final byte AVG_IRON_MAN =
            ChickenKyNangDacBietIronMan.AVG_IRON_MAN;
    /** Khớp vùng ngắm đất dưới chân của gun 15 trong plugin PC. */
    private static final byte THOR_LOAI_HIEU_UNG_SET = 0;
    private static final long THOR_THOI_GIAN_HIEU_UNG_MS = 650L;
    enum TrainingBossState {
        IDLE,
        TELEPORTING,
        AIMING,
        SHOOTING,
        DEAD,
        ROUND_END
    }

    private static final class TrainingCharacterHit {
        static final int PLAYER_TARGET = -1;

        final int botIndex;
        final int segmentIndex;
        final short hitX;
        final short hitY;

        TrainingCharacterHit(
                int botIndex,
                int segmentIndex,
                short hitX,
                short hitY
        ) {
            this.botIndex = botIndex;
            this.segmentIndex = segmentIndex;
            this.hitX = hitX;
            this.hitY = hitY;
        }

        boolean laNguoiChoi() {
            return this.botIndex == PLAYER_TARGET;
        }
    }
    public static HashMap<Integer, ChickenNguoiChoi> players_id = new HashMap();
    public int ma;
    public String ten;
    public int vang;
    public int ngoc;
    private int kinhNghiem;
    /**
     * Phiên bản optimistic-lock của stats_json. Mọi ghi thống kê phải tăng
     * đúng một đơn vị; dữ liệu RAM cũ không được phép ghi đè server khác.
     */
    private long phienBanThongKe;
    /** Optimistic-lock cho toan bo tui/trang bi/balo/ruong. */
    private long phienBanKhoVatPham;
    public int cup;
    public int cap;
    /** Cấp cao nhất đã được nhận thưởng tiềm năng và ngọc tím. */
    public int capCaoNhatDaNhanThuong;
    public int clan = -1;
    public byte clanRole;
    public byte power;
    /** Thanh POW chi ton tai trong tran, tuyet doi khong ghi vao stats_json. */
    private transient int powTrongTran;
    /** Tong HP thuc da mat ke tu lan reset POW gan nhat. */
    private transient long satThuongTichLuyChoPow;
    /** Da bam POW trong luot; mat ke ca khi bo luot, chi ap dung cho mot hanh dong. */
    private transient boolean powDaKichHoat;
    /** Client dang hien vong POW; chi tat khi hanh dong/luot da ket thuc. */
    private transient boolean powDangHienHieuUng;
    public byte busyHammer;
    public byte nHammer;
    public byte trainingSuccess;
    public boolean inTraining;
    public int trainingHits;
    /** Trạng thái riêng của một phiên luyện tập; tách khỏi dữ liệu nhân vật chính. */
    private final transient ChickenPhienLuyenTap trainingSession = new ChickenPhienLuyenTap(
            TRAINING_MAP_ID,
            TRAINING_BOT_COUNT,
            TRAINING_PLAYER_MAX_HP,
            TRAINING_BOSS_BASE_HP,
            TRAINING_PLAYER_INDEX
    );
    private long lastTrainingFire;
    /** Chặn client gửi lại lệnh vào luyện tập khi hộp kết quả đang đóng/trả sảnh. */
    private volatile boolean trainingReturningToLobby;
    public short[] pointAdd;
    public short point;
    public byte zoneId = (byte)-1;
    public ChickenKhu zone;
    public short x;
    public short y;
    public short head;
    public short hat;
    public short body;
    public short leg;
    public short wp;
    public short wing;
    public ChickenVatPham[] itemBag = new ChickenVatPham[SO_O_TUI_DO];
    public ChickenVatPham[] itemBody = new ChickenVatPham[6];
    public int[] itemBalo = new int[0];
    public ChickenVatPham[] itemBox = new ChickenVatPham[SO_O_RUONG_DO];
    /** Snapshot doi sung rieng cua luyen tap, khong duoc ghi vao inventory. */
    private transient ChickenVatPham sungDangCamLuyenTap;
    private final transient Map<Integer, ChickenVatPham> sungTheoOTrongBaloLuyenTap =
            new HashMap<Integer, ChickenVatPham>();
    public boolean isReady;
    public byte pointSeat;
    public int chiSo = -1;
    public ChickenDichVuGame dichVu;
    public int kill = 1;
    public int chet;
    public int assist;
    public int daNhanThanhTich;
    public byte powerAvenger;
    public byte avenger;
    private ChickenCuaHang store;

    public ChickenNguoiChoi(ChickenDichVuGame dichVu) {
        this.dichVu = dichVu;
    }

    public synchronized void khoiTaoPowTrongTran() {
        this.powTrongTran = ChickenCheDoTestChienDau.POW_LUON_DAY ? 100 : 0;
        this.satThuongTichLuyChoPow = 0L;
        this.powDaKichHoat = false;
        this.powDangHienHieuUng = false;
    }

    public synchronized int layPowTrongTran() {
        return this.powTrongTran;
    }

    public synchronized boolean dangHienHieuUngPow() {
        return this.powDangHienHieuUng;
    }

    /**
     * Nap POW theo HP thuc bi tru. Mat tong cong 50% HP toi da dau tran thi day.
     * Tra ve true khi gia tri hien thi da thay doi de caller phat CMD 113.
     */
    public synchronized boolean ghiNhanSatThuongChoPow(
            int hpThucDaMat,
            int mauToiDaTrongTran
    ) {
        if (hpThucDaMat <= 0 || mauToiDaTrongTran <= 0
                || this.powTrongTran >= 100) {
            return false;
        }
        long mocDay = Math.max(1L, ((long) mauToiDaTrongTran + 1L) / 2L);
        this.satThuongTichLuyChoPow = Math.min(
                mocDay,
                this.satThuongTichLuyChoPow + (long) hpThucDaMat);
        int powMoi = (int) Math.min(
                100L,
                this.satThuongTichLuyChoPow * 100L / mocDay);
        if (powMoi == this.powTrongTran) {
            return false;
        }
        this.powTrongTran = powMoi;
        return true;
    }

    /** Server chot kich hoat: bam la mat thanh ngay, ke ca sau do bo luot. */
    public synchronized boolean kichHoatPowNoiBo() {
        if (this.powTrongTran < 100 || this.powDaKichHoat) {
            return false;
        }
        this.powTrongTran = ChickenCheDoTestChienDau.POW_LUON_DAY ? 100 : 0;
        this.satThuongTichLuyChoPow = 0L;
        this.powDaKichHoat = true;
        this.powDangHienHieuUng = true;
        return true;
    }

    /**
     * Phien damage dau tien cua hanh dong se chot POW x2. Ket qua duoc snapshot
     * trong ChickenMayMan.PhienTanCong nen van ap dung cho du loat/du muc tieu.
     */
    public synchronized boolean tieuThuPowChoTanCong() {
        if (!this.powDaKichHoat) {
            return false;
        }
        this.powDaKichHoat = false;
        return true;
    }

    /**
     * Ket thuc POW khi doi luot. Tra ve true neu can gui lai CMD 113 de client
     * tat vong xoay; co nay tach khoi powDaKichHoat vi damage da snapshot POW
     * ngay luc bat dau phat ban.
     */
    public synchronized boolean huyPowDaKichHoat() {
        boolean canTatHieuUng = this.powDangHienHieuUng;
        this.powDaKichHoat = false;
        this.powDangHienHieuUng = false;
        return canTatHieuUng;
    }

    private void ghiNhanSatThuongPowLuyenTap(int hpTruoc) throws IOException {
        int hpThucDaMat = Math.max(
                0,
                hpTruoc - Math.max(0, this.trainingSession.trainingPlayerHp));
        if (this.ghiNhanSatThuongChoPow(
                hpThucDaMat,
                this.trainingSession.trainingPlayerMaxHp)
                && !this.dangHienHieuUngPow()) {
            this.dichVu.guiCapNhatPow(
                    TRAINING_PLAYER_INDEX, this.layPowTrongTran());
        }
    }

    public synchronized int layKinhNghiem() {
        return this.kinhNghiem;
    }

    public synchronized long layPhienBanThongKe() {
        return this.phienBanThongKe;
    }

    public synchronized long layPhienBanKhoVatPham() {
        return this.phienBanKhoVatPham;
    }

    public synchronized void napPhienBanKhoVatPham(long phienBan) {
        this.phienBanKhoVatPham = Math.max(0L, phienBan);
    }

    /**
     * Chỉ dùng khi nạp nguyên trạng thái authoritative từ database.
     */
    public synchronized void napTrangThaiTiemNangTuKho(
            int exp,
            int mocCapDaNhan,
            short diemConLai,
            short[] diemDaPhanBo,
            long phienBan
    ) {
        this.kinhNghiem = Math.max(0, exp);
        this.cap = ChickenTienIch.layCap(this.kinhNghiem);
        this.capCaoNhatDaNhanThuong = Math.max(0, mocCapDaNhan);
        this.point = (short) Math.max(0, diemConLai);
        this.pointAdd = diemDaPhanBo == null
                ? null : Arrays.copyOf(diemDaPhanBo, diemDaPhanBo.length);
        this.phienBanThongKe = Math.max(0L, phienBan);
        ChickenQuanLyTiemNang.baoDamDuLieuHopLe(this);
    }

    /**
     * Cập nhật EXP trong RAM luôn đi kèm cân bằng level và tổng điểm.
     * Không cung cấp setter EXP rời để code mới không thể bỏ quên invariant.
     */
    public synchronized int datKinhNghiemVaCanBangTrongBoNho(int expMoi) {
        this.kinhNghiem = Math.max(0, expMoi);
        this.cap = ChickenTienIch.layCap(this.kinhNghiem);
        return ChickenQuanLyTiemNang
                .canBangTongDiemTheoExpTrongBoNho(this);
    }

    /**
     * Áp dụng kết quả đã commit bằng transaction khóa hàng database.
     */
    public synchronized void apDungTrangThaiExpDaCommit(
            int expMoi,
            int capMoi,
            int mocCapDaNhanMoi,
            short diemConLaiMoi,
            long phienBanMoi
    ) {
        this.kinhNghiem = Math.max(0, expMoi);
        int capTinhLai = ChickenTienIch.layCap(this.kinhNghiem);
        this.cap = capTinhLai;
        if (capMoi != capTinhLai) {
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT][SAI_CAP_EXP]"
                    + " playerId=" + this.ma
                    + " capKetQua=" + capMoi
                    + " capServer=" + capTinhLai);
        }
        this.capCaoNhatDaNhanThuong =
                Math.max(capTinhLai, mocCapDaNhanMoi);
        this.point = (short) Math.max(0, diemConLaiMoi);
        this.phienBanThongKe = Math.max(
                this.phienBanThongKe, phienBanMoi);
        ChickenQuanLyTiemNang.baoDamDuLieuHopLe(this);
        ChickenQuanLyTiemNang.canBangTongDiemTheoExpTrongBoNho(this);
    }

    public float layKD() {
        return (float)this.kill / (float)this.chet;
    }

    public float layKDA() {
        return (float)(this.kill + this.assist) / (float)this.chet;
    }

    public static ChickenNguoiChoi layNguoiChoiTheoMa(int ma) {
        return players_id.get(ma);
    }

    public static void xoa(int ma) {
        players_id.remove(ma);
    }

    public static void guiMayChu(ChickenTinNhan ms) {
        for (ChickenNguoiChoi pl : players_id.values()) {
            if (pl == null) continue;
            pl.dichVu.guiTin(ms);
        }
    }

    public void nangCapNhanVat(ChickenTinNhan ms) throws IOException {
        if (this.tuChoiThayDoiKinhTeTrongTran()) {
            return;
        }
        ChickenQuanLyTiemNang.xuLyNangCap(this, ms);
    }

    public void banDoRPG(ChickenTinNhan ms) throws IOException {
        byte b = ms.boDoc().readByte();
        switch (b) {
            case 2: {
                this.diChuyen(ms);
                break;
            }
            case 3: {
                this.chat(ms);
                break;
            }
            case 7: {
                this.moKhu();
                break;
            }
            case 8: {
                this.doiKhu(ms);
                break;
            }
            case 11: {
                this.moMenu(ms);
                break;
            }
            default: {
                System.out.println("b: " + b);
                break;
            }
        }
    }

    public void moMenu(ChickenTinNhan ms) throws IOException {
        short npcId = ms.boDoc().readShort();
        ChickenXuLyNpc.mo(this, npcId);
    }

    public void npcDaiUy() {
        NpcChiHuy.mo(this);
    }

    public void doiKhu(ChickenTinNhan ms) throws IOException {
        byte zone = ms.boDoc().readByte();
        QuanLySanhChoBoss.xoaNguoiChoiKhoiTatCaSanh(this);
        ChickenQuanLyPhong.roiBanCho(this);
        ChickenBanDoRPG.roi(this);
        ChickenBanDoRPG.vao(zone, this);
    }

    public int layOTrongTuiDo() {
        int number = 0;
        for (ChickenVatPham vatPham : this.itemBag) {
            if (vatPham != null) continue;
            ++number;
        }
        return number;
    }

    private boolean chiSoTuiDoHopLe(int chiSo) {
        return chiSo >= 0 && chiSo < this.itemBag.length;
    }

    private boolean chiSoRuongDoHopLe(int chiSo) {
        return chiSo >= 0 && chiSo < this.itemBox.length;
    }

    private boolean chiSoTrangBiHopLe(int chiSo) {
        return chiSo >= 0 && chiSo < this.itemBody.length;
    }

    private boolean chiSoBaloHopLe(int chiSo) {
        return chiSo >= 0 && chiSo < this.itemBalo.length;
    }

    public static boolean laVatPhamDuocPhepTrongBalo(
            ChickenVatPham vatPham) {
        return vatPham != null && vatPham.mau != null
                && (laSungThuongDuocPhepTrongBalo(vatPham)
                || (vatPham.mau.loai == 10
                && vatPham.ma != MA_THUOC_TAY_DIEM));
    }

    /** Dieu kien cap authoritative; khong doc cap hay quyen dung tu packet. */
    public boolean datCapSuDungVatPham(ChickenVatPham vatPham) {
        return ChickenYeuCauCapVatPham.datYeuCau(this.cap, vatPham);
    }

    public boolean datCapSuDungVatPham(ChickenMauVatPham mau) {
        return ChickenYeuCauCapVatPham.datYeuCau(this.cap, mau);
    }

    private boolean coTheGanVatPhamVaoBalo(ChickenVatPham vatPham) {
        return laVatPhamDuocPhepTrongBalo(vatPham)
                && this.datCapSuDungVatPham(vatPham);
    }

    public static boolean laSungAVG(ChickenVatPham vatPham) {
        return vatPham != null && vatPham.ma >= 391 && vatPham.ma <= 398;
    }

    /** Sung thuong duoc doi trong tran phai co mapping dan va cau hinh nap dan server. */
    public static boolean laSungThuongDuocPhepTrongBalo(
            ChickenVatPham vatPham) {
        return vatPham != null && vatPham.mau != null
                && vatPham.mau.loai == 5
                && !laSungAVG(vatPham)
                && vatPham.HP > 0
                && ChickenQuanLyDanSung.theoSungDangTrangBi(vatPham) != null
                && ChickenNapDanServer.coCauHinhNapDanHopLe(vatPham);
    }

    static int layDungLuongBaloHopLe(ChickenVatPham balo) {
        if (balo == null || balo.mau == null || balo.mau.loai != 4) {
            return -1;
        }
        int sucChua = balo.getParamById(13);
        return sucChua > 0 && sucChua <= SO_O_BALO_TOI_DA
                ? sucChua : -1;
    }

    /**
     * Tao trang thai balo moi truoc khi doi trang bi. Chi khi tao thanh cong
     * caller moi duoc phep sua itemBag/itemBody, tranh trang thai doi nua chung
     * khi balo moi nho hon balo cu.
     */
    private int[] taoBaloSauKhiDoi(int sucChuaMoi) {
        if (sucChuaMoi <= 0 || sucChuaMoi > SO_O_BALO_TOI_DA) {
            return null;
        }
        int[] ketQua = new int[sucChuaMoi];
        Arrays.fill(ketQua, -1);
        boolean[] daCo = new boolean[this.itemBag.length];
        int viTriMoi = 0;
        if (this.itemBalo == null) {
            return ketQua;
        }
        for (int chiSoVatPham : this.itemBalo) {
            if (!this.chiSoTuiDoHopLe(chiSoVatPham)
                    || this.itemBag[chiSoVatPham] == null
                    || !this.coTheGanVatPhamVaoBalo(
                            this.itemBag[chiSoVatPham])
                    || this.itemBag[chiSoVatPham].chiSo != chiSoVatPham
                    || daCo[chiSoVatPham]) {
                continue;
            }
            if (viTriMoi >= ketQua.length) {
                return null;
            }
            daCo[chiSoVatPham] = true;
            ketQua[viTriMoi++] = chiSoVatPham;
        }
        return ketQua;
    }

    public int layOTrongBalo() {
        int number = 0;
        for (int chiSo : this.itemBalo) {
            if (chiSo != -1) continue;
            ++number;
        }
        return number;
    }

    public int layOTrongRuong() {
        int number = 0;
        for (ChickenVatPham vatPham : this.itemBox) {
            if (vatPham != null) continue;
            ++number;
        }
        return number;
    }

    public synchronized void thucHien(ChickenTinNhan ms) throws IOException {
        if (this.tuChoiThayDoiKinhTeTrongTran()) {
            return;
        }
        if (ms == null || ms.boDoc().available() != 5) {
            this.startOKDlg2("Dữ liệu bán vật phẩm không hợp lệ.");
            return;
        }
        ms.boDoc().readByte();
        int ma = ms.boDoc().readInt();
        if (ma < 11000) {
            this.startOKDlg2("Dữ liệu bán vật phẩm không hợp lệ.");
            return;
        }
        int chiSo = ma - 11000;
        if (!this.chiSoTuiDoHopLe(chiSo)) {
            this.startOKDlg2("Dữ liệu bán vật phẩm không hợp lệ.");
            return;
        }
        ChickenVatPham vatPham = this.itemBag[chiSo];
        if (vatPham == null || vatPham.mau == null
                || vatPham.soLuong < 1 || vatPham.soLuong > 99) {
            this.startOKDlg2("Bán vật phẩm thất bại.");
            return;
        }
        if (this.vatPhamCoTrongBalo(vatPham)) {
            this.startOKDlg2("Vật phẩm đã gắn vào Balo.");
            return;
        }

        long donGia = tinhGiaBanMotVatPham(vatPham.mau);
        long tongGia = Math.min(
                Integer.MAX_VALUE,
                donGia * (long) vatPham.soLuong);
        int vangCu = this.vang;
        int ngocCu = this.ngoc;
        String tuiCu = this.taoJsonTuiDo().toJSONString();
        ChickenVatPham[] banSaoTui =
                Arrays.copyOf(this.itemBag, this.itemBag.length);

        this.vang = (int) Math.min(
                Integer.MAX_VALUE, (long) this.vang + tongGia);
        this.itemBag[chiSo] = null;
        if (!this.luuGiaoDichShopCoKetQua(vangCu, ngocCu, tuiCu)) {
            this.vang = vangCu;
            this.ngoc = ngocCu;
            this.itemBag = banSaoTui;
            this.startOKDlg2("Không thể lưu giao dịch. Vui lòng thử lại.");
            return;
        }
        this.dichVu.capNhat();
        this.dichVu.capNhatTuiDo(chiSo, 0);
        ChickenQuanLyMayChu.log(
                "[KINH_TE] Ban vat pham playerId=" + this.ma
                + " itemId=" + vatPham.ma
                + " quantity=" + vatPham.soLuong
                + " totalGold=" + tongGia
                + " balance=" + this.vang);
        this.startOKDlg2("Bán vật phẩm thành công.");
    }

    public synchronized void yeuCauBanVatPham(
            ChickenTinNhan ms) throws IOException {
        if (this.tuChoiThayDoiKinhTeTrongTran()) {
            return;
        }
        if (ms == null || ms.boDoc().available() != 1) {
            this.startOKDlg2("Dữ liệu bán vật phẩm không hợp lệ.");
            return;
        }
        int chiSo = ms.boDoc().readUnsignedByte();
        if (!this.chiSoTuiDoHopLe(chiSo)) {
            this.startOKDlg2("Dữ liệu bán vật phẩm không hợp lệ.");
            return;
        }
        ChickenVatPham vatPham = this.itemBag[chiSo];
        if (vatPham != null && vatPham.mau != null
                && vatPham.soLuong >= 1 && vatPham.soLuong <= 99) {
            if (this.vatPhamCoTrongBalo(vatPham)) {
                this.startOKDlg2("Vật phẩm đã gắn vào Balo.");
                return;
            }
            long vang = tinhGiaBanMotVatPham(vatPham.mau)
                    * (long) vatPham.soLuong;
            ChickenTinNhan mss = new ChickenTinNhan(-25);
            DataOutputStream ds = mss.boGhi();
            ds.writeInt(11000 + chiSo);
            ds.writeUTF("Bạn có chắc muốn bán " + vatPham.mau.ten
                    + " với giá " + ChickenTienIch.dinhDangTien(vang)
                    + " Vàng");
            ds.flush();
            this.dichVu.guiTin(mss);
        } else {
            this.startOKDlg2("Bạn không có vật phẩm này.");
        }
    }

    public synchronized void yeuCauMuaVatPham(
            ChickenTinNhan ms) throws IOException {
        if (this.tuChoiThayDoiKinhTeTrongTran()) {
            return;
        }
        /*
         * Client native gui 4 byte:
         * loai tien (1) + ma vat pham (2) + so luong (1).
         * Luon phan hoi khi sai packet de InfoDlg cua client khong quay mai.
         */
        if (ms == null || ms.boDoc().available() != 4) {
            this.startOKDlg2("Dữ liệu mua vật phẩm không hợp lệ.");
            return;
        }
        byte loai = ms.boDoc().readByte();
        int ma = ms.boDoc().readUnsignedShort();
        int soLuong = ms.boDoc().readUnsignedByte();
        if (soLuong < 1 || soLuong > 99) {
            this.startOKDlg2("Số lượng mua không hợp lệ.");
            return;
        }

        ChickenMauVatPham vatPham =
                ChickenQuanLyMayChu.itemTemplates.get(ma);
        if (vatPham == null) {
            this.startOKDlg2("Có lỗi xảy ra.");
            return;
        }
        if (!ChickenCuaHang.coBanTrongCuaHang(ma, loai)) {
            this.startOKDlg2("Vật phẩm không được bán trong cửa hàng.");
            return;
        }

        if (!this.datCapSuDungVatPham(vatPham)) {
            this.startOKDlg2("Trình độ không đạt yêu cầu.");
            return;
        }

        int heSoNhanKhiMua =
                ChickenGoiMuaVatPham.soLuongNhanMoiGoi(ma);
        long soLuongNhanLong = (long) soLuong * heSoNhanKhiMua;
        if (soLuongNhanLong < 1L || soLuongNhanLong > Integer.MAX_VALUE) {
            this.startOKDlg2("Số lượng nhận không hợp lệ.");
            return;
        }
        int soLuongNhan = (int) soLuongNhanLong;

        int donGia;
        if (loai == 0 && vatPham.buyGold > 0) {
            donGia = vatPham.buyGold;
        } else if (loai == 1 && vatPham.buyGem > 0) {
            donGia = vatPham.buyGem;
        } else {
            this.moHopThoaiOK("Có lỗi xảy ra.");
            return;
        }

        long tongGia = (long) donGia * soLuong;
        if (tongGia > Integer.MAX_VALUE) {
            this.startOKDlg2("Giá mua không hợp lệ.");
            return;
        }
        if (loai == 0 && tongGia > this.vang) {
            this.startOKDlg2("Bạn không đủ vàng.");
            return;
        }
        if (loai == 1 && tongGia > this.ngoc) {
            this.startOKDlg2("Bạn không đủ ngọc.");
            return;
        }

        boolean coTheXepChong = vatPham.loai > 5;
        int soOCan;
        if (coTheXepChong) {
            long sucChuaTrongChongCu = 0L;
            for (ChickenVatPham dangCo : this.itemBag) {
                if (dangCo == null || dangCo.ma != ma) {
                    continue;
                }
                if (dangCo.soLuong < 1 || dangCo.soLuong > 99) {
                    this.startOKDlg2(
                            "Dữ liệu vật phẩm trong túi không hợp lệ.");
                    return;
                }
                sucChuaTrongChongCu += 99 - dangCo.soLuong;
            }
            long conLaiCanTaoChong = Math.max(
                    0L, (long) soLuongNhan - sucChuaTrongChongCu);
            soOCan = (int) ((conLaiCanTaoChong + 98L) / 99L);
        } else {
            soOCan = soLuongNhan;
        }
        if (this.layOTrongTuiDo() < soOCan) {
            this.startOKDlg2("Túi đã đầy.");
            return;
        }

        int vangCu = this.vang;
        int ngocCu = this.ngoc;
        String tuiCu = this.taoJsonTuiDo().toJSONString();
        ChickenVatPham[] banSaoTui =
                Arrays.copyOf(this.itemBag, this.itemBag.length);
        int[] soLuongCu = new int[this.itemBag.length];
        for (int i = 0; i < this.itemBag.length; i++) {
            soLuongCu[i] = this.itemBag[i] == null
                    ? 0 : this.itemBag[i].soLuong;
        }

        if (loai == 0) {
            this.vang -= (int) tongGia;
        } else {
            this.ngoc -= (int) tongGia;
        }

        int conLai = soLuongNhan;
        if (coTheXepChong) {
            for (ChickenVatPham dangCo : this.itemBag) {
                if (conLai <= 0) {
                    break;
                }
                if (dangCo == null || dangCo.ma != ma
                        || dangCo.soLuong >= 99) {
                    continue;
                }
                int themVaoChong = Math.min(
                        conLai, 99 - dangCo.soLuong);
                dangCo.soLuong += themVaoChong;
                conLai -= themVaoChong;
            }
        }
        for (int i = 0; i < this.itemBag.length && conLai > 0; i++) {
            if (this.itemBag[i] != null) {
                continue;
            }
            ChickenVatPham them = new ChickenVatPham(ma);
            them.HP = ChickenVatPham.DO_BEN_TOI_DA;
            them.itemOptions = new Vector(vatPham.thuocTinhs);
            them.chiSo = i;
            them.soLuong = coTheXepChong
                    ? Math.min(99, conLai) : 1;
            this.itemBag[i] = them;
            conLai -= them.soLuong;
        }

        if (!this.luuGiaoDichShopCoKetQua(vangCu, ngocCu, tuiCu)) {
            for (int i = 0; i < banSaoTui.length; i++) {
                if (banSaoTui[i] != null) {
                    banSaoTui[i].soLuong = soLuongCu[i];
                }
            }
            this.vang = vangCu;
            this.ngoc = ngocCu;
            this.itemBag = banSaoTui;
            this.startOKDlg2("Không thể lưu giao dịch. Vui lòng thử lại.");
            return;
        }
        this.dichVu.capNhat();
        this.dichVu.guiTuiDo();
        ChickenQuanLyMayChu.log(
                "[KINH_TE] Mua vat pham playerId=" + this.ma
                + " itemId=" + ma
                + " requestedQuantity=" + soLuong
                + " grantedQuantity=" + soLuongNhan
                + " currency=" + (loai == 0 ? "gold" : "gem")
                + " total=" + tongGia
                + " balance=" + (loai == 0 ? this.vang : this.ngoc));
        this.moHopThoaiOK("Bạn mua thành công " + vatPham.ten
                + " x" + soLuongNhan);
    }

    private static long tinhGiaBanMotVatPham(
            ChickenMauVatPham vatPham) {
        if (vatPham.buyGold > 0) {
            return Math.max(1L, vatPham.buyGold / 2L);
        }
        if (vatPham.buyGem > 0) {
            return Math.max(1L, (long) vatPham.buyGem * 100L);
        }
        return 1L;
    }

    /**
     * Khóa hàng người chơi và commit tiền + túi trong cùng transaction.
     * Giá trị cũ được đối chiếu để một tiến trình server khác không thể bị
     * ghi đè bởi snapshot RAM đã lỗi thời.
     */
    protected boolean luuGiaoDichShopCoKetQua(
            int vangCu,
            int ngocCu,
            String tuiCu
    ) {
        try (java.sql.Connection conn =
                     ChickenCoSoDuLieu.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String tuiTrongKho;
                int vangTrongKho;
                int ngocTrongKho;
                long phienBanKhoTrongDb;
                try (java.sql.PreparedStatement doc =
                             conn.prepareStatement(
                                     "SELECT `gold`, `gem`, "
                                     + "`inventory_json`, `inventory_revision` "
                                     + "FROM `players` "
                                     + "WHERE `id` = ? LIMIT 1 FOR UPDATE;")) {
                    doc.setInt(1, this.ma);
                    try (java.sql.ResultSet ketQua = doc.executeQuery()) {
                        if (!ketQua.next()) {
                            conn.rollback();
                            return false;
                        }
                        vangTrongKho = ketQua.getInt("gold");
                        ngocTrongKho = ketQua.getInt("gem");
                        tuiTrongKho =
                                ketQua.getString("inventory_json");
                        phienBanKhoTrongDb = ketQua.getLong(
                                "inventory_revision");
                    }
                }
                if (vangTrongKho != vangCu
                        || ngocTrongKho != ngocCu
                        || phienBanKhoTrongDb != this.phienBanKhoVatPham
                        || !cungNoiDungTuiDo(tuiTrongKho, tuiCu)) {
                    conn.rollback();
                    ChickenQuanLyMayChu.log(
                            "[BAO_MAT][XUNG_DOT_SHOP]"
                            + " playerId=" + this.ma
                            + " expectedGold=" + vangCu
                            + " dbGold=" + vangTrongKho
                            + " expectedGem=" + ngocCu
                            + " dbGem=" + ngocTrongKho);
                    return false;
                }
                try (java.sql.PreparedStatement ghi =
                             conn.prepareStatement(
                                     "UPDATE `players` SET `gold` = ?, "
                                     + "`gem` = ?, `inventory_json` = ?, "
                                     + "`inventory_revision` = "
                                     + "`inventory_revision` + 1 "
                                     + "WHERE `id` = ? "
                                     + "AND `inventory_revision` = ? "
                                     + "LIMIT 1;")) {
                    ghi.setInt(1, this.vang);
                    ghi.setInt(2, this.ngoc);
                    ghi.setString(
                            3, this.taoJsonTuiDo().toJSONString());
                    ghi.setInt(4, this.ma);
                    ghi.setLong(5, phienBanKhoTrongDb);
                    if (ghi.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }
                conn.commit();
                this.phienBanKhoVatPham = phienBanKhoTrongDb + 1L;
                return true;
            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackLoi) {
                    ex.addSuppressed(rollbackLoi);
                }
                throw ex;
            }
        } catch (SQLException | RuntimeException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(
                    Level.SEVERE,
                    "Loi giao dich shop player=" + this.ma,
                    ex);
            return false;
        }
    }

    private static boolean cungNoiDungTuiDo(
            String benTrai,
            String benPhai
    ) {
        try {
            Object trai = JSON.parse(
                    benTrai == null || benTrai.isBlank()
                            ? "[]" : benTrai);
            Object phai = JSON.parse(
                    benPhai == null || benPhai.isBlank()
                            ? "[]" : benPhai);
            return Objects.equals(trai, phai);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public void datTrangBiChoNhanVat(ChickenVatPham vatPham) {
        if (vatPham == null || vatPham.mau == null) {
            return;
        }
        if (!this.datCapSuDungVatPham(vatPham)) {
            return;
        }
        vatPham.HP = ChickenVatPham.DO_BEN_TOI_DA;
        /*
         * JSON trang bi khong duoc phep phu thuoc thu tu phan tu. Neu o vu
         * khi dang la AVG, moi lan dong bo mot mon khac van phai giu bo AVG
         * lam hinh dang va quyen server, khong reset ve nhan vat di bo.
         */
        ChickenVatPham vuKhiDangTrangBi =
                this.itemBody != null && this.itemBody.length > 5
                        ? this.itemBody[5] : null;
        if (vuKhiDangTrangBi != null
                && vuKhiDangTrangBi != vatPham
                && this.datCapSuDungVatPham(vuKhiDangTrangBi)
                && vuKhiDangTrangBi.ma >= 391
                && vuKhiDangTrangBi.ma <= 398) {
            this.datTrangBiChoNhanVat(vuKhiDangTrangBi);
            return;
        }
        int ma = vatPham.ma;
        this.avenger = 0;
        if (ma == 391) {
            this.head = (short)204;
            this.body = (short)205;
            this.leg = (short)206;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = 1;
        } else if (ma == 392) {
            this.head = (short)220;
            this.body = (short)221;
            this.leg = (short)222;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)2;
        } else if (ma == 393) {
            this.head = (short)219;
            this.body = (short)217;
            this.leg = (short)218;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)3;
        } else if (ma == 394) {
            this.head = (short)198;
            this.body = (short)211;
            this.leg = (short)212;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)4;
        } else if (ma == 395) {
            this.head = (short)197;
            this.body = (short)207;
            this.leg = (short)208;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)5;
        } else if (ma == 396) {
            this.head = (short)203;
            this.body = (short)213;
            this.leg = (short)214;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)6;
        } else if (ma == 397) {
            this.head = (short)202;
            this.body = (short)215;
            this.leg = (short)216;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)7;
        } else if (ma == 398) {
            this.head = (short)199;
            this.body = (short)209;
            this.leg = (short)210;
            this.wp = (short)-1;
            this.hat = (short)-1;
            this.wing = (short)-1;
            this.avenger = (byte)8;
        } else {
            ChickenVatPham t = this.itemBody[5];
            if (t == null || t.ma < 391 || t.ma > 400) {
                byte loai = vatPham.mau.loai;
                short part = vatPham.mau.part;
                if (loai == 0) {
                    this.head = part;
                } else if (loai == 1) {
                    this.leg = part;
                } else if (loai == 2) {
                    this.body = part;
                } else if (loai == 3) {
                    this.hat = part;
                } else if (loai == 4) {
                    this.wing = part;
                } else if (loai == 5) {
                    this.wp = part;
                }
            }
        }
    }

    public boolean vatPhamCoTrongBalo(ChickenVatPham vatPham) {
        if (vatPham == null || this.itemBalo == null) {
            return false;
        }
        for (int chiSo : this.itemBalo) {
            if (this.chiSoTuiDoHopLe(chiSo)
                    && this.itemBag[chiSo] == vatPham) {
                return true;
            }
        }
        return false;
    }

    public void xoaThamChieuBaloTheoOTrongTui(int chiSoTui) {
        if (this.itemBalo == null) {
            return;
        }
        for (int i = 0; i < this.itemBalo.length; i++) {
            if (this.itemBalo[i] == chiSoTui) {
                this.itemBalo[i] = -1;
            }
        }
    }

    private boolean baloDangHopLe() {
        int sucChua = this.itemBody != null && this.itemBody.length > 4
                && this.itemBody[4] != null
                ? layDungLuongBaloHopLe(this.itemBody[4]) : 0;
        if (this.itemBalo == null || sucChua < 0
                || this.itemBalo.length != sucChua) {
            return false;
        }
        boolean[] daCo = new boolean[this.itemBag.length];
        for (int chiSoTui : this.itemBalo) {
            if (chiSoTui == -1) {
                continue;
            }
            if (!this.chiSoTuiDoHopLe(chiSoTui)
                    || daCo[chiSoTui]
                    || !laVatPhamDuocPhepTrongBalo(
                            this.itemBag[chiSoTui])
                    || this.itemBag[chiSoTui].chiSo != chiSoTui) {
                return false;
            }
            daCo[chiSoTui] = true;
        }
        return true;
    }

    public synchronized void dungVatPham(ChickenTinNhan ms) throws IOException {
        if (this.tuChoiThayDoiKinhTeTrongTran()) {
            return;
        }
        if (ms == null || ms.boDoc().available() != 2) {
            return;
        }
        int chiSo = ms.boDoc().readUnsignedByte();
        byte loai = ms.boDoc().readByte();
        if (loai != 0 && loai != 1) {
            return;
        }
        if (loai == 1) {
                if (!this.chiSoTuiDoHopLe(chiSo)) {
                    return;
                }
                ChickenVatPham vatPham = this.itemBag[chiSo];
                if (vatPham != null && vatPham.soLuong > 0) {
                    byte t = vatPham.mau.loai;
                    int ma = vatPham.ma;
                    if (t == 12) {
                        this.startOKDlg2("Bạn có muốn nhập 5 viên ngọc này, hãy vào menu Bắt dầu -> ghép ngọc");
                        return;
                    }
                    if (t <= 5) {
                        Vector<String> vector = new Vector<String>();
                        if (vatPham.nSocket < 3) {
                            vector.add("Đục lỗ");
                        }
                        if (vatPham.nGem < vatPham.nSocket) {
                            vector.add("Đính ngọc");
                        }
                        if (vatPham.nGem > 0) {
                            vector.add("Tháo ngọc");
                        }
                        this.dichVu.moDanhSach("Bạn muốn làm gì?", vector);
                    } else if (ma == 256) {
                        if (!this.datCapSuDungVatPham(vatPham)) {
                            this.moHopThoaiOK(
                                    "Trình độ không đạt yêu cầu.");
                            return;
                        }
                        ChickenQuanLyTiemNang.tayDiemBangVatPham(
                                this, chiSo);
                    } else if (vatPham.mau.loai == 11) {
                        this.startOKDlg2("Không thể sử dụng.");
                    } else {
                        this.startOKDlg2("Không thể sử dụng.");
                    }
                } else {
                    this.startOKDlg2("Không tìm thấy vật phẩm này. Vui lòng đăng nhập lại để kiểm tra.");
                }
        } else {
                if (!this.chiSoTrangBiHopLe(chiSo)) {
                    return;
                }
                ChickenVatPham vatPham = this.itemBody[chiSo];
                if (vatPham != null) {
                    Vector<String> vector = new Vector<String>();
                    if (vatPham.nSocket < 3) {
                        vector.add("Đục lỗ");
                    }
                    if (vatPham.nGem < vatPham.nSocket) {
                        vector.add("Đính ngọc");
                    }
                    if (vatPham.nGem > 0) {
                        vector.add("Tháo ngọc");
                    }
                    this.dichVu.moDanhSach("Bạn muốn làm gì?", vector);
                } else {
                    this.startOKDlg2("Không tìm thấy vật phẩm này. Vui lòng đăng nhập lại để kiểm tra.");
                }
        }
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    public synchronized void chuyenVatPham(ChickenTinNhan ms) throws IOException {
        if (this.tuChoiThayDoiKinhTeTrongTran()) {
            return;
        }
        if (ms == null || ms.boDoc().available() != 2) {
            return;
        }
        ChickenVatPham vatPham;
        byte loai = ms.boDoc().readByte();
        int chiSo = ms.boDoc().readUnsignedByte();
        TrangThaiKhoVatPham trangThaiCu = TrangThaiKhoVatPham.chup(this);
        if (loai == 4) {
            if (!this.chiSoTuiDoHopLe(chiSo)) return;
            ChickenVatPham item2 = this.itemBag[chiSo];
            if (item2 == null || item2.mau == null) return;
            if (this.vatPhamCoTrongBalo(item2)) {
                this.moHopThoaiOK("Vật phẩm đã gắn vào Balo.");
                return;
            }
            if (!this.datCapSuDungVatPham(item2)) {
                this.moHopThoaiOK("Trình độ không đạt yêu cầu.");
                return;
            }
            byte t = item2.mau.loai;
            if (t > 5) {
                this.moHopThoaiOK("Trang bị không phù hợp.");
                return;
            }
            if (t == 5
                    && (ChickenQuanLyDanSung.theoMauSung(item2.mau) == null
                    || !ChickenNapDanServer.coCauHinhNapDanHopLe(item2))) {
                this.moHopThoaiOK("Súng chưa có đủ cấu hình chiến đấu.");
                return;
            }
            int[] baloMoi = null;
            if (t == 4) {
                int sucChuaMoi = layDungLuongBaloHopLe(item2);
                if (sucChuaMoi < 0) {
                    this.moHopThoaiOK("Dữ liệu balô không hợp lệ.");
                    return;
                }
                baloMoi = this.taoBaloSauKhiDoi(sucChuaMoi);
                if (baloMoi == null) {
                    this.moHopThoaiOK(
                            "Balô mới không đủ chỗ cho các vật phẩm đang gắn.");
                    return;
                }
            }
            if (t == 5 && this.isFlyAvenger() && this.y > 360) {
                this.moHopThoaiOK("Không thể thay đổi trang phục khi đang ở dưới đất.");
                return;
            }
            this.y = (short)360;
            ChickenVatPham item3 = this.itemBody[t];
            item2.HP = ChickenVatPham.DO_BEN_TOI_DA;
            item2.chiSo = t;
            this.itemBody[t] = item2;
            if (item3 == null) {
                this.itemBag[chiSo] = null;
            } else {
                item3.HP = ChickenVatPham.DO_BEN_TOI_DA;
                item3.chiSo = chiSo;
                this.itemBag[chiSo] = item3;
            }
            if (t == 4) {
                this.itemBalo = baloMoi;
            }
            if (!this.hoanTatChuyenVatPham(trangThaiCu)) {
                return;
            }
            if (t == 4) {
                this.dichVu.guiBalo();
            }
            if (t == 5) {
                for (ChickenVatPham ite : this.itemBody) {
                    this.datTrangBiChoNhanVat(ite);
                }
            } else {
                this.datTrangBiChoNhanVat(item2);
            }
            this.dichVu.guiTuiDo();
            this.dichVu.guiDoTrenNguoi();
            this.dichVu.doiTrangBi();
            if (this.zone != null) {
                Iterator<ChickenNguoiChoi> iterator = this.zone.players_id.values().iterator();
                while (iterator.hasNext()) {
                    ChickenNguoiChoi p = iterator.next();
                    if (p.equals(this)) continue;
                    p.dichVu.vaoCho(this);
                }
            }
            return;
        }
        if (loai == 5) {
            if (!this.chiSoTrangBiHopLe(chiSo)) return;
            ChickenVatPham item4 = this.itemBody[chiSo];
            if (item4 == null) return;
            byte t = item4.mau.loai;
            if (t != 0 && t != 4) {
                this.moHopThoaiOK("Không thể tháo trang bị này.");
                return;
            }
            int n = this.layOTrongTuiDo();
            if (n == 0) {
                this.moHopThoaiOK("Túi đồ đã đầy.");
                return;
            }
            this.themVatPhamVaoTui(item4);
            this.itemBody[chiSo] = null;
            if (t == 0) {
                this.head = 0;
            } else {
                this.itemBalo = new int[0];
                this.wing = 0;
            }
            if (!this.hoanTatChuyenVatPham(trangThaiCu)) {
                return;
            }
            if (t == 4) {
                this.dichVu.guiBalo();
            }
            this.dichVu.guiTuiDo();
            this.dichVu.guiDoTrenNguoi();
            if (this.itemBody[5] != null) {
                this.datTrangBiChoNhanVat(this.itemBody[5]);
            }
            this.dichVu.doiTrangBi();
            if (this.zone != null) {
                Iterator<ChickenNguoiChoi> playerIt = this.zone.players_id.values().iterator();
                while (playerIt.hasNext()) {
                    ChickenNguoiChoi p = playerIt.next();
                    if (p.equals(this)) continue;
                    p.dichVu.vaoCho(this);
                }
            }
            return;
        }
        if (loai == 1) {
            if (!this.chiSoTuiDoHopLe(chiSo)) return;
            ChickenVatPham item5 = this.itemBag[chiSo];
            if (item5 == null) return;
            if (this.vatPhamCoTrongBalo(item5)) {
                this.moHopThoaiOK("Vật phẩm đã gắn vào Balo.");
                return;
            }
            int slotNull = this.layOTrongRuong();
            if (slotNull == 0) {
                this.moHopThoaiOK("Rương đã đầy.");
                return;
            }
            this.themVatPhamVaoRuong(item5);
            this.itemBag[chiSo] = null;
            if (!this.hoanTatChuyenVatPham(trangThaiCu)) {
                return;
            }
            this.dichVu.guiTuiDo();
            return;
        }
        if (loai == 6) {
            if (!this.chiSoTuiDoHopLe(chiSo)) return;
            vatPham = this.itemBag[chiSo];
            if (!this.coTheGanVatPhamVaoBalo(vatPham)
                    || vatPham.chiSo != chiSo) {
                this.moHopThoaiOK("Không thể cho vật phẩm này vào balo.");
                return;
            }
            if (this.vatPhamCoTrongBalo(vatPham)) {
                this.moHopThoaiOK("Vat pham da co trong Balo.");
                return;
            }
            int n = this.layOTrongBalo();
            if (n == 0) {
                this.moHopThoaiOK("Balo đã đầy.");
                return;
            }
        } else {
            if (loai != 0) {
                if (loai != 7) return;
                if (!this.chiSoBaloHopLe(chiSo)) return;
                this.itemBalo[chiSo] = -1;
                if (!this.hoanTatChuyenVatPham(trangThaiCu)) {
                    return;
                }
                this.dichVu.guiBalo();
                return;
            }
            if (!this.chiSoRuongDoHopLe(chiSo)) return;
            ChickenVatPham item6 = this.itemBox[chiSo];
            if (item6 == null) return;
            byte t = item6.mau.loai;
            int n = this.layOTrongTuiDo();
            if (n == 0) {
                this.moHopThoaiOK("Túi đã đầy.");
                return;
            }
            this.themVatPhamVaoTui(item6);
            this.itemBox[chiSo] = null;
            if (!this.hoanTatChuyenVatPham(trangThaiCu)) {
                return;
            }
            this.dichVu.guiRuongDo();
            return;
        }
        for (int i = 0; i < this.itemBalo.length; ++i) {
            if (this.itemBalo[i] != -1) continue;
            this.itemBalo[i] = chiSo;
            break;
        }
        if (!this.hoanTatChuyenVatPham(trangThaiCu)) {
            return;
        }
        this.dichVu.guiBalo();
    }

    private boolean hoanTatChuyenVatPham(TrangThaiKhoVatPham trangThaiCu) {
        if (this.luuKhoVatPhamCoKetQua()) {
            return true;
        }
        trangThaiCu.khoiPhuc(this);
        this.startOKDlg2(
                "Không thể lưu thay đổi trang bị. Trạng thái cũ đã được khôi phục.");
        try {
            this.dichVu.guiTuiDo();
            this.dichVu.guiDoTrenNguoi();
            this.dichVu.guiBalo();
            this.dichVu.guiRuongDo();
            this.dichVu.doiTrangBi();
        } catch (IOException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(
                    Level.WARNING,
                    "Khong the dong bo UI sau khi rollback trang bi player="
                            + this.ma,
                    ex);
        }
        return false;
    }

    /**
     * Bon kho item duoc ghi bang mot cau UPDATE, nen DB chi co trang thai cu
     * hoac trang thai moi; khong the luu tui moi nhung trang bi cu.
     */
    protected boolean luuKhoVatPhamCoKetQua() {
        try {
            if (!this.baloDangHopLe()) {
                throw new IllegalStateException(
                        "Invariant balo khong hop le player=" + this.ma);
            }
            JSONArray body = this.taoJsonTrangBi();
            JSONArray bag = this.taoJsonTuiDo();
            JSONArray balo = this.taoJsonBalo();
            JSONArray box = this.taoJsonRuongDo();
            long phienBanCu = this.phienBanKhoVatPham;
            try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE `players` SET `equipped_json` = ?, "
                     + "`inventory_json` = ?, `pocket_json` = ?, "
                     + "`storage_json` = ?, `inventory_revision` = "
                     + "`inventory_revision` + 1 WHERE `id` = ? "
                     + "AND `inventory_revision` = ? LIMIT 1;")) {
            stmt.setString(1, body.toJSONString());
            stmt.setString(2, bag.toJSONString());
            stmt.setString(3, balo.toJSONString());
            stmt.setString(4, box.toJSONString());
            stmt.setInt(5, this.ma);
            stmt.setLong(6, phienBanCu);
            if (stmt.executeUpdate() != 1) {
                ChickenQuanLyMayChu.log(
                        "[BAO_MAT][XUNG_DOT_KHO] playerId=" + this.ma
                        + " expectedRevision=" + phienBanCu
                        + " action=chuyen_vat_pham");
                return false;
            }
            this.phienBanKhoVatPham = phienBanCu + 1L;
            return true;
            }
        } catch (SQLException | RuntimeException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(
                    Level.SEVERE,
                    "Loi luu kho vat pham player=" + this.ma,
                    ex);
            return false;
        }
    }

    private JSONArray taoJsonTrangBi() {
        JSONArray ketQua = new JSONArray();
        for (ChickenVatPham vatPham : this.itemBody) {
            if (vatPham != null) {
                ketQua.add(vatPham.toJSONObject());
            }
        }
        return ketQua;
    }

    private JSONArray taoJsonBalo() {
        JSONArray ketQua = new JSONArray();
        for (int chiSo : this.itemBalo) {
            ketQua.add(chiSo);
        }
        return ketQua;
    }

    private JSONArray taoJsonRuongDo() {
        JSONArray ketQua = new JSONArray();
        for (ChickenVatPham vatPham : this.itemBox) {
            if (vatPham != null) {
                ketQua.add(vatPham.toJSONObject());
            }
        }
        return ketQua;
    }

    private static final class TrangThaiKhoVatPham {
        private final ChickenVatPham[] tui;
        private final ChickenVatPham[] trangBi;
        private final int[] balo;
        private final ChickenVatPham[] ruong;
        private final IdentityHashMap<ChickenVatPham, int[]> giaTriVatPham;
        private final short head;
        private final short leg;
        private final short body;
        private final short hat;
        private final short wing;
        private final short wp;
        private final short y;
        private final byte avenger;

        private TrangThaiKhoVatPham(ChickenNguoiChoi nguoiChoi) {
            this.tui = nguoiChoi.itemBag.clone();
            this.trangBi = nguoiChoi.itemBody.clone();
            this.balo = nguoiChoi.itemBalo == null
                    ? new int[0] : nguoiChoi.itemBalo.clone();
            this.ruong = nguoiChoi.itemBox.clone();
            this.giaTriVatPham = new IdentityHashMap<>();
            this.chupVatPham(this.tui);
            this.chupVatPham(this.trangBi);
            this.chupVatPham(this.ruong);
            this.head = nguoiChoi.head;
            this.leg = nguoiChoi.leg;
            this.body = nguoiChoi.body;
            this.hat = nguoiChoi.hat;
            this.wing = nguoiChoi.wing;
            this.wp = nguoiChoi.wp;
            this.y = nguoiChoi.y;
            this.avenger = nguoiChoi.avenger;
        }

        static TrangThaiKhoVatPham chup(ChickenNguoiChoi nguoiChoi) {
            return new TrangThaiKhoVatPham(nguoiChoi);
        }

        private void chupVatPham(ChickenVatPham[] danhSach) {
            for (ChickenVatPham vatPham : danhSach) {
                if (vatPham != null && !this.giaTriVatPham.containsKey(vatPham)) {
                    this.giaTriVatPham.put(vatPham,
                            new int[]{vatPham.chiSo, vatPham.soLuong});
                }
            }
        }

        void khoiPhuc(ChickenNguoiChoi nguoiChoi) {
            nguoiChoi.itemBag = this.tui.clone();
            nguoiChoi.itemBody = this.trangBi.clone();
            nguoiChoi.itemBalo = this.balo.clone();
            nguoiChoi.itemBox = this.ruong.clone();
            for (Map.Entry<ChickenVatPham, int[]> entry
                    : this.giaTriVatPham.entrySet()) {
                ChickenVatPham vatPham = entry.getKey();
                int[] giaTri = entry.getValue();
                vatPham.chiSo = giaTri[0];
                vatPham.soLuong = giaTri[1];
                vatPham.HP = ChickenVatPham.DO_BEN_TOI_DA;
            }
            nguoiChoi.head = this.head;
            nguoiChoi.leg = this.leg;
            nguoiChoi.body = this.body;
            nguoiChoi.hat = this.hat;
            nguoiChoi.wing = this.wing;
            nguoiChoi.wp = this.wp;
            nguoiChoi.y = this.y;
            nguoiChoi.avenger = this.avenger;
        }
    }

    public int soVatPhamTrongBalo() {
        int number = 0;
        if (this.itemBalo != null) {
            for (int chiSo : this.itemBalo) {
                if (chiSo == -1) continue;
                ++number;
            }
        }
        return number;
    }

    public boolean themVatPhamVaoTui(ChickenVatPham vatPham) {
        try {
            if (vatPham == null || vatPham.mau == null) {
                return false;
            }
            vatPham.HP = ChickenVatPham.DO_BEN_TOI_DA;
            int i;
            byte loai = vatPham.mau.loai;
            if (loai > 5) {
                for (i = 0; i < this.itemBag.length; ++i) {
                    if (this.itemBag[i] == null || this.itemBag[i].ma != vatPham.ma) continue;
                    this.itemBag[i].soLuong += vatPham.soLuong;
                    this.dichVu.capNhatTuiDo(i, this.itemBag[i].soLuong);
                    return true;
                }
            }
            for (i = 0; i < this.itemBag.length; ++i) {
                if (this.itemBag[i] != null) continue;
                vatPham.chiSo = i;
                this.itemBag[i] = vatPham;
                this.dichVu.guiTuiDo();
                return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void themVatPhamVaoRuong(ChickenVatPham vatPham) {
        try {
            if (vatPham == null || vatPham.mau == null) {
                return;
            }
            vatPham.HP = ChickenVatPham.DO_BEN_TOI_DA;
            int i;
            byte loai = vatPham.mau.loai;
            if (loai > 5) {
                for (i = 0; i < this.itemBox.length; ++i) {
                    if (this.itemBox[i] == null || this.itemBox[i].ma != vatPham.ma) continue;
                    this.itemBox[i].soLuong += vatPham.soLuong;
                    this.dichVu.capNhatRuongDo(i, this.itemBox[i].soLuong);
                    return;
                }
            }
            for (i = 0; i < this.itemBox.length; ++i) {
                if (this.itemBox[i] != null) continue;
                vatPham.chiSo = i;
                this.itemBox[i] = vatPham;
                this.dichVu.guiRuongDo();
                return;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Chi cho phep sua trang bi/kinh te o ngoai tran. Chi so chien dau duoc
     * chup luc bat dau, nen doi vu khi giua tran se tao cap vu khi/tai luc gia.
     */
    private boolean tuChoiThayDoiKinhTeTrongTran() {
        if (!this.inTraining
                && ChickenQuanLyChien.timTranDauCuaNguoiChoi(this) == null) {
            return false;
        }
        this.startOKDlg2("Không thể thay đổi trang bị hoặc kinh tế khi đang chiến đấu.");
        return true;
    }

    public void moKhu() throws IOException {
        ChickenTinNhan mss = new ChickenTinNhan(-98);
        DataOutputStream ds = mss.boGhi();
        ds.writeByte(7);
        ds.writeByte(ChickenBanDoRPG.zones.size());
        for (ChickenKhu z : ChickenBanDoRPG.zones) {
            ds.writeByte(z.zoneId);
            ds.writeByte(z.pts);
            ds.writeByte(z.numPlayer);
            ds.writeByte(z.maxPlayer);
        }
        ds.flush();
        this.dichVu.guiTin(mss);
    }

    public void chat(ChickenTinNhan ms) throws IOException {
        String noiDung = ms.boDoc().readUTF();
        ChickenTinNhan mss = new ChickenTinNhan(-98);
        DataOutputStream ds = mss.boGhi();
        ds.writeByte(3);
        ds.writeByte(this.chiSo);
        ds.writeUTF(noiDung);
        ds.flush();
        this.zone.guiTatCaNguoiChoi(mss);
    }

    public void diChuyen(ChickenTinNhan ms) throws IOException {
        this.x = ms.boDoc().readShort();
        this.y = ms.boDoc().readShort();
        if (!this.isFlyAvenger() && this.y != 360) {
            this.y = (short)360;
        }
        ChickenTinNhan mss = new ChickenTinNhan(-98);
        DataOutputStream ds = mss.boGhi();
        ds.writeByte(2);
        ds.writeByte(this.chiSo);
        ds.writeShort(this.x);
        ds.writeShort(this.y);
        ds.flush();
        this.zone.guiTatCaNguoiChoi(mss);
    }

    public void xemCuaHang(ChickenCuaHang store) throws IOException {
        if (store == null) {
            this.startOKDlg2("Cửa hàng không hợp lệ.");
            return;
        }
        this.store = store;
        this.dichVu.xemCuaHang(this.store);
    }

    public void removeItem(int chiSo, int soLuong) {
        if (!this.chiSoTuiDoHopLe(chiSo) || soLuong <= 0) {
            return;
        }
        try {
            ChickenVatPham vatPham = this.itemBag[chiSo];
            if (vatPham != null) {
                vatPham.soLuong -= soLuong;
                if (vatPham.soLuong > 0) {
                    this.itemBag[chiSo].soLuong = vatPham.soLuong;
                    this.dichVu.capNhatTuiDo(chiSo, vatPham.soLuong);
                } else {
                    this.itemBag[chiSo] = null;
                    this.xoaThamChieuBaloTheoOTrongTui(chiSo);
                    this.dichVu.capNhatTuiDo(chiSo, 0);
                }
            } else {
                this.dichVu.capNhatTuiDo(chiSo, 0);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Xac nhan snapshot vat pham tran van tro dung stack authoritative trong
     * tui. Khong tra cuu bang ID/itemUsed do client gui.
     */
    public synchronized boolean coVatPhamChienSanSang(
            ChickenChienBinh.VatPhamChienTrongTran snapshot
    ) {
        if (snapshot == null
                || !this.chiSoTuiDoHopLe(snapshot.getChiSoTui())) {
            return false;
        }
        ChickenVatPham vatPham = this.itemBag[snapshot.getChiSoTui()];
        return vatPham != null
                && vatPham.chiSo == snapshot.getChiSoTui()
                && vatPham.ma == snapshot.getIdVatPham()
                && vatPham.soLuong > 0
                && vatPham.mau != null
                && vatPham.mau.loai == 10;
    }

    /**
     * Tru dung mot vat pham chien dau va ghi ca bon kho bang optimistic
     * revision. Neu DB that bai, kho RAM duoc rollback truoc khi caller tao
     * phat ban, nen reconnect/packet lap khong the nhan doi gia tri.
     */
    public synchronized boolean tieuThuMotVatPhamChien(
            ChickenChienBinh.VatPhamChienTrongTran snapshot
    ) {
        if (!this.coVatPhamChienSanSang(snapshot)) {
            return false;
        }
        TrangThaiKhoVatPham trangThaiCu = TrangThaiKhoVatPham.chup(this);
        int chiSo = snapshot.getChiSoTui();
        ChickenVatPham vatPham = this.itemBag[chiSo];
        vatPham.soLuong--;
        int conLai = vatPham.soLuong;
        if (conLai <= 0) {
            this.itemBag[chiSo] = null;
            this.xoaThamChieuBaloTheoOTrongTui(chiSo);
            conLai = 0;
        }
        if (!this.luuKhoVatPhamCoKetQua()) {
            trangThaiCu.khoiPhuc(this);
            return false;
        }
        try {
            this.dichVu.capNhatTuiDo(chiSo, conLai);
            this.dichVu.guiBalo();
        } catch (IOException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(
                    Level.WARNING,
                    "Da tru vat pham nhung khong dong bo duoc UI player="
                            + this.ma + " item=" + snapshot.getIdVatPham(),
                    ex);
        }
        ChickenQuanLyMayChu.log(
                "[KINH_TE][DUNG_VAT_PHAM_CHIEN] playerId=" + this.ma
                + " itemId=" + snapshot.getIdVatPham()
                + " bagIndex=" + chiSo
                + " remaining=" + conLai);
        return true;
    }

    public synchronized void updateGold(int vang) {
        long soDuMoi = (long) this.vang + vang;
        this.vang = (int) Math.max(
                0L, Math.min(Integer.MAX_VALUE, soDuMoi));
        this.dichVu.capNhat();
    }

    public void updateGem(int ngoc) {
        this.ngoc += ngoc;
        this.dichVu.capNhat();
    }

    public void requestTab(ChickenTinNhan ms) throws IOException {
        if (this.store == null) {
            this.startOKDlg2("Bạn chưa mở cửa hàng.");
            return;
        }
        if (ms == null || ms.boDoc().available() != 2) {
            this.startOKDlg2("Dữ liệu trang cửa hàng không hợp lệ.");
            return;
        }
        byte chiSo = ms.boDoc().readByte();
        byte page = ms.boDoc().readByte();
        if (chiSo < 0 || page < 0 || chiSo >= this.store.tabs.size() || page >= this.store.tabs.get(chiSo).size()) {
            this.moHopThoaiOK("Co loi xay ra.");
            return;
        }
        ms = new ChickenTinNhan(-43);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(chiSo);
        ds.writeByte(page);
        ArrayList<ChickenTrang> pages = this.store.tabs.get(chiSo);
        ds.writeByte(pages.size());
        ChickenTrang p = pages.get(page);
        ds.writeByte(p.vatPhams.size());
        for (ChickenMauVatPham t : p.vatPhams) {
            ds.writeShort(t.ma);
            ds.writeInt(t.buyGold);
            ds.writeInt(t.buyGem);
            int numberOption = t.thuocTinhs.size();
            ds.writeByte(numberOption);
            for (int b = 0; b < numberOption; ++b) {
                ChickenThuocTinhVatPham option = (ChickenThuocTinhVatPham)t.thuocTinhs.get(b);
                ds.writeByte(option.optionTemplate.ma);
                ds.writeShort(option.thamSo);
            }
        }
        ds.flush();
        this.dichVu.guiTin(ms);
    }

    public void moHopThoaiOK(String noiDung) {
        try {
            this.dichVu.moHopThoaiOK(noiDung);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startOKDlg2(String noiDung) {
        try {
            this.dichVu.baoLoiTien(noiDung);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isFlyAvenger() {
        return ChickenCoCheBayAVG.coTrangBiBayHopLe(this);
    }

    public static void onChatFromToAllPlayer(String ten, String noiDung) {
        try {
            ChickenTinNhan mss = new ChickenTinNhan(5);
            DataOutputStream ds = mss.boGhi();
            ds.writeInt(-1);
            ds.writeUTF(ten);
            ds.writeUTF(noiDung);
            ds.flush();
            ChickenNguoiChoi.guiMayChu(mss);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void chatTo(ChickenTinNhan ms) throws IOException {
        int ma = ms.boDoc().readInt();
        String noiDung = ms.boDoc().readUTF();
        if (ma == -1) {
            if (this.ngoc < 10) {
                this.moHopThoaiOK("Bạn không đủ ngọc để chat thế giới.");
                return;
            }
            this.updateGem(-10);
            ChickenNguoiChoi.onChatFromToAllPlayer(this.ten, noiDung);
        } else {
            ChickenNguoiChoi pl = ChickenNguoiChoi.layNguoiChoiTheoMa(ma);
            if (pl != null) {
                ChickenTinNhan mss = new ChickenTinNhan(5);
                DataOutputStream ds = mss.boGhi();
                ds.writeInt(this.ma);
                ds.writeUTF(this.ten);
                ds.writeUTF(noiDung);
                ds.flush();
                pl.dichVu.guiTin(mss);
            }
        }
    }

    public void viewPlayerInfo(ChickenTinNhan ms) throws IOException {
        int ma = ms.boDoc().readInt();
        ChickenNguoiChoi pl = ChickenNguoiChoi.layNguoiChoiTheoMa(ma);
        if (pl != null) {
            ChickenTinNhan mss = new ChickenTinNhan(-126);
            DataOutputStream ds = mss.boGhi();
            ds.writeInt(pl.ma);
            ds.writeUTF(pl.ten);
            ds.writeShort(pl.head);
            ds.writeShort(pl.hat);
            ds.writeShort(pl.body);
            ds.writeShort(pl.leg);
            ds.writeShort(pl.wing);
            ds.writeShort(pl.wp);
            ds.writeInt(pl.layKinhNghiem());
            ds.writeByte(1);
            ds.writeShort(0);
            ds.flush();
            this.dichVu.guiTin(mss);
        }
    }

    public synchronized void flushCache() {
        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection()) {
            conn.setAutoCommit(false);
            long phienBanCu = this.phienBanThongKe;
            long phienBanKhoCu = this.phienBanKhoVatPham;
            try {
            try (java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE `players` SET `gold` = ?, `cup` = ?, `gem` = ? WHERE `id` = ? LIMIT 1;")) {
                stmt.setInt(1, this.vang);
                stmt.setInt(2, this.cup);
                stmt.setInt(3, this.ngoc);
                stmt.setInt(4, this.ma);
                stmt.execute();
            }
            JSONObject duLieu = this.taoJsonThongKe();
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE `players` SET `stats_json` = ?, "
                    + "`stats_revision` = `stats_revision` + 1 "
                    + "WHERE `id` = ? AND `stats_revision` = ? LIMIT 1;")) {
                stmt.setString(1, duLieu.toJSONString());
                stmt.setInt(2, this.ma);
                stmt.setLong(3, phienBanCu);
                if (stmt.executeUpdate() != 1) {
                    throw new SQLException(
                            "Xung dot stats_revision player=" + this.ma);
                }
            }
            if (!this.baloDangHopLe()) {
                throw new SQLException(
                        "Invariant balo khong hop le player=" + this.ma);
            }
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE `players` SET `equipped_json` = ?, "
                    + "`inventory_json` = ?, `pocket_json` = ?, "
                    + "`storage_json` = ?, `inventory_revision` = "
                    + "`inventory_revision` + 1 WHERE `id` = ? "
                    + "AND `inventory_revision` = ? LIMIT 1;")) {
                stmt.setString(1, this.taoJsonTrangBi().toJSONString());
                stmt.setString(2, this.taoJsonTuiDo().toJSONString());
                stmt.setString(3, this.taoJsonBalo().toJSONString());
                stmt.setString(4, this.taoJsonRuongDo().toJSONString());
                stmt.setInt(5, this.ma);
                stmt.setLong(6, phienBanKhoCu);
                if (stmt.executeUpdate() != 1) {
                    throw new SQLException(
                            "Xung dot inventory_revision player=" + this.ma);
                }
            }
            conn.commit();
            this.phienBanThongKe = phienBanCu + 1L;
            this.phienBanKhoVatPham = phienBanKhoCu + 1L;
            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackLoi) {
                    ex.addSuppressed(rollbackLoi);
                }
                throw ex;
            }
        }
        catch (SQLException | RuntimeException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Lưu riêng stats_json sau khi cộng một điểm. Handler chỉ xác nhận thành
     * công sau khi câu UPDATE này đã commit.
     */
    public synchronized boolean luuTiemNangCoKetQua() {
        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE `players` SET `stats_json` = ?, "
                     + "`stats_revision` = `stats_revision` + 1 "
                     + "WHERE `id` = ? AND `stats_revision` = ? LIMIT 1;")) {
            long phienBanCu = this.phienBanThongKe;
            stmt.setString(1, this.taoJsonThongKe().toJSONString());
            stmt.setInt(2, this.ma);
            stmt.setLong(3, phienBanCu);
            if (stmt.executeUpdate() != 1) {
                ChickenQuanLyMayChu.log(
                        "[BAO_MAT][XUNG_DOT_STATS]"
                        + " playerId=" + this.ma
                        + " expectedRevision=" + phienBanCu
                        + " action=tiem_nang");
                return false;
            }
            this.phienBanThongKe = phienBanCu + 1L;
            return true;
        } catch (SQLException | RuntimeException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(
                    Level.SEVERE, "Loi luu diem tiem nang player=" + this.ma,
                    ex);
            return false;
        }
    }

    /**
     * Tẩy điểm thay đổi cả stats_json và inventory_json. Hai dữ liệu phải
     * commit cùng nhau để không thể giữ vật phẩm nhưng vẫn nhận điểm hoàn.
     */
    public synchronized boolean luuTiemNangVaTuiDoCoKetQua() {
        if (!this.baloDangHopLe()) {
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT][BALO_KHONG_HOP_LE] playerId=" + this.ma
                    + " action=tay_diem");
            return false;
        }
        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection()) {
            conn.setAutoCommit(false);
            long phienBanCu = this.phienBanThongKe;
            long phienBanKhoCu = this.phienBanKhoVatPham;
            try {
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE `players` SET `stats_json` = ?, "
                        + "`stats_revision` = `stats_revision` + 1 "
                        + "WHERE `id` = ? AND `stats_revision` = ? LIMIT 1;")) {
                    stmt.setString(1, this.taoJsonThongKe().toJSONString());
                    stmt.setInt(2, this.ma);
                    stmt.setLong(3, phienBanCu);
                    if (stmt.executeUpdate() != 1) {
                        conn.rollback();
                        ChickenQuanLyMayChu.log(
                                "[BAO_MAT][XUNG_DOT_STATS]"
                                + " playerId=" + this.ma
                                + " expectedRevision=" + phienBanCu
                                + " action=tay_diem");
                        return false;
                    }
                }
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE `players` SET `inventory_json` = ?, "
                        + "`pocket_json` = ?, `inventory_revision` = "
                        + "`inventory_revision` + 1 WHERE `id` = ? "
                        + "AND `inventory_revision` = ? LIMIT 1;")) {
                    stmt.setString(1, this.taoJsonTuiDo().toJSONString());
                    stmt.setString(2, this.taoJsonBalo().toJSONString());
                    stmt.setInt(3, this.ma);
                    stmt.setLong(4, phienBanKhoCu);
                    if (stmt.executeUpdate() != 1) {
                        conn.rollback();
                        return false;
                    }
                }
                conn.commit();
                this.phienBanThongKe = phienBanCu + 1L;
                this.phienBanKhoVatPham = phienBanKhoCu + 1L;
                return true;
            } catch (SQLException | RuntimeException ex) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackLoi) {
                    ex.addSuppressed(rollbackLoi);
                }
                throw ex;
            }
        } catch (SQLException | RuntimeException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(
                    Level.SEVERE, "Loi luu tay diem player=" + this.ma, ex);
            return false;
        }
    }

    private JSONObject taoJsonThongKe() {
        ChickenQuanLyTiemNang.baoDamDuLieuHopLe(this);
        JSONObject duLieu = new JSONObject();
        duLieu.put("power", this.power);
        duLieu.put("avenger", ChickenQuanLyNangLuongAVG.layNangLuong(this));
        duLieu.put("kill", this.kill);
        duLieu.put("dead", this.chet);
        duLieu.put("assist", this.assist);
        duLieu.put("trainingSuccess", this.trainingSuccess);
        duLieu.put("trainingWins", this.trainingSession.trainingWins);
        duLieu.put("achievementClaims", this.daNhanThanhTich);
        duLieu.put("busyHammer", this.busyHammer);
        duLieu.put("nHammer", this.nHammer);
        duLieu.put("exp", this.layKinhNghiem());
        duLieu.put("rewardedLevel", this.capCaoNhatDaNhanThuong);
        duLieu.put("point", this.point);
        JSONArray pointAdds = new JSONArray();
        for (short giaTri : this.pointAdd) {
            pointAdds.add(giaTri);
        }
        duLieu.put("pointAdd", pointAdds);
        return duLieu;
    }

    private JSONArray taoJsonTuiDo() {
        JSONArray bag = new JSONArray();
        for (ChickenVatPham vatPham : this.itemBag) {
            if (vatPham != null) {
                bag.add(vatPham.toJSONObject());
            }
        }
        return bag;
    }

    public void close() {
        XuLyMenuChiHuy.xoaTrangThai(this);
        this.roiLuyenTap();
        QuanLySanhChoBoss.xoaNguoiChoiKhoiTatCaSanh(this);
        ChickenQuanLyPhong.roiBanCho(this);
        if (this.zone != null && this.zoneId >= 0) {
            ChickenBanDoRPG.roi(this);
        }
        ChickenNguoiChoi.xoa(this.ma);
        this.flushCache();
    }

    public synchronized void roiLuyenTap() {
        if (this.dichVu != null) {
            this.dichVu.guiDongMenuKyNangDacBiet();
        }
        this.inTraining = false;
        this.sungDangCamLuyenTap = null;
        this.sungTheoOTrongBaloLuyenTap.clear();
        this.trainingSession.trainingVatPhamChienBinh = null;
        this.khoiTaoPowTrongTran();
        this.dungVongBotLuyenTap();
        this.xoaTrangThaiPhatBanNguoiChoi();
        ChickenQuanLyPhienLuyenTap.ketThucPhien(this.trainingSession, TRAINING_PLAYER_INDEX);
        this.lastTrainingFire = 0L;
        this.isReady = false;
        this.chiSo = -1;
        this.pointSeat = 0;
    }

    public synchronized void vaoLuyenTap() {
        try {
            if (this.trainingReturningToLobby || this.inTraining) {
                return;
            }
            SanhChoBoss sanhBoss =
                    QuanLySanhChoBoss.timSanhCuaNguoiChoi(this);
            SanhChoBoss.TrangThai trangThaiBoss = sanhBoss == null
                    ? null : sanhBoss.getTrangThai();
            if (ChickenQuanLyChien.timTranDauCuaNguoiChoi(this) != null
                    || trangThaiBoss
                            == SanhChoBoss.TrangThai.DANG_BAT_DAU
                    || trangThaiBoss == SanhChoBoss.TrangThai.DANG_CHIEN) {
                ChickenQuanLyMayChu.log(
                        "[BAO_MAT] Tu choi tao luyen tap khi dang trong tran"
                        + " playerId=" + this.ma
                        + " bossState=" + trangThaiBoss);
                return;
            }
            if (!ChickenQuanLyNangLuongAVG.kiemTraChoVaoTran(
                    this,
                    "luyện tập boss"
            )) {
                return;
            }
            String loiDieuKien = ChickenDieuKienVaoTran.layLoi(this);
            if (loiDieuKien != null) {
                this.tuChoiVaoLuyenTap(loiDieuKien);
                return;
            }
            ChickenVatPham sung = this.laySungDangTrangBiHopLe();
            int tongTanCong = this.layTongTanCongHienTai();
            int napDanNguoiChoi = this.layThoiGianNapDanNguoiChoi(sung);
            DuLieuSung duLieuSungBoss =
                    ChickenSungShopBoss.chonNgauNhienKhongAvg();
            int idSungBoss = duLieuSungBoss == null
                    ? -1 : duLieuSungBoss.getIdSung();
            short partSungBoss = duLieuSungBoss == null
                    ? (short) -1 : duLieuSungBoss.getPartSung();
            int tanCongBoss = ChickenSungShopBoss.layTanCongTheoId(
                    idSungBoss, TRAINING_BOSS_FALLBACK_DAMAGE);
            int napDanBoss = ChickenSungShopBoss.layNapDanTheoId(
                    idSungBoss, 0);
            if (tongTanCong <= 0 || napDanNguoiChoi <= 0
                    || duLieuSungBoss == null || partSungBoss < 0
                    || tanCongBoss <= 0 || napDanBoss <= 0) {
                this.tuChoiVaoLuyenTap(
                        "Dữ liệu tấn công hoặc nạp đạn của súng không hợp lệ.");
                return;
            }
            if (!ChickenQuanLyNangLuongAVG.tieuHaoKhiBatDauTran(
                    this,
                    "luyện tập boss"
            )) {
                return;
            }
            QuanLySanhChoBoss.xoaNguoiChoiKhoiTatCaSanh(this);
            ChickenQuanLyPhong.roiBanCho(this);
            if (this.zone != null && this.zoneId >= 0) {
                ChickenBanDoRPG.roi(this);
            }
            this.roiLuyenTap();
            final long phienMoi = ChickenQuanLyPhienLuyenTap.batDauPhien(
                    this.trainingSession,
                    napDanNguoiChoi,
                    napDanBoss,
                    idSungBoss,
                    partSungBoss,
                    tanCongBoss,
                    this.tinhMauToiDaBoss(),
                    TRAINING_PLAYER_INDEX
            );
            this.trainingSuccess = 1;
            this.isReady = true;
            this.chiSo = 0;
            this.pointSeat = 0;
            this.inTraining = true;
            this.khoiTaoPowTrongTran();
            this.khoiTaoSungLuyenTap(sung);
            this.trainingSession.trainingWind = ChickenHeThongGio.taoGioMoi();
            this.resetTrainingRoundState(false);
            this.trainingSession.trainingVatPhamChienBinh =
                    new ChickenChienBinh(
                            this,
                            TRAINING_PLAYER_INDEX,
                            this.trainingSession.trainingPlayerX,
                            this.trainingSession.trainingPlayerY
                    );
            short trainingWeaponResource = sung.mau.part;
            short trainingWeaponDisplay = this.avenger == 0 ? sung.mau.part : (short)-1;
            short bossWeaponResource = partSungBoss;
            ChickenQuanLyMayChu.log(
                    "[LUYEN_TAP][CHON_SUNG_BOT] playerId=" + this.ma
                    + " idSung=" + idSungBoss
                    + " tenSung=" + duLieuSungBoss.getTenSung()
                    + " part=" + bossWeaponResource
                    + " tanCong=" + tanCongBoss
                    + " napDan=" + napDanBoss);
            if (phienMoi != this.trainingSession.trainingSessionId || !this.inTraining) {
                return;
            }
            this.dichVu.guiDonNhanVatAoLuyenTap(this.ma);
            this.dichVu.guiThongTinLuyenTap();
            this.dichVu.guiChonBanDoLuyenTap(TRAINING_MAP_ID);
            this.dichVu.guiNguoiChoiLuyenTap(TRAINING_PLAYER_INDEX, this.ma, this.ten, this.head, this.leg, this.body,
                    this.hat, this.wing, trainingWeaponDisplay, this.avenger, this.ma, this.clan, this.layKinhNghiem());
            for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
                this.dichVu.guiNguoiChoiLuyenTap((byte)(i + 1), -9999 - i, this.layTenBossLuyenTap(i), TRAINING_BOT_HEADS[i], TRAINING_BOT_LEGS[i], TRAINING_BOT_BODIES[i],
                        TRAINING_BOT_HATS[i], TRAINING_BOT_WINGS[i], bossWeaponResource, TRAINING_BOT_AVENGERS[i], this.ma, -1, 0);
            }
            this.dichVu.guiGio(
                    this.trainingSession.trainingWind.getWindX(),
                    this.trainingSession.trainingWind.getWindY()
            );
            this.dichVu.guiBatDauLuyenTap(TRAINING_MAP_ID, this.trainingSession.trainingMap.layMaNen(), trainingWeaponResource, this.trainingSession.trainingPlayerX, this.trainingSession.trainingPlayerY,
                    this.trainingSession.trainingPlayerHp, this.trainingSession.trainingPlayerMaxHp, this.trainingSession.trainingBotX, this.trainingSession.trainingBotY,
                    this.trainingSession.trainingBotHp, this.trainingSession.trainingBossMaxHp, new short[]{bossWeaponResource},
                    this.avenger, TRAINING_BOT_AVENGERS);
            this.dichVu.guiBaloLuyenTap(
                    this.trainingSession.trainingVatPhamChienBinh);
            this.dichVu.guiCapNhatPow(
                    TRAINING_PLAYER_INDEX, this.layPowTrongTran());
        }
        catch (Exception ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Client chuyển sang WaitingScr trước khi gửi CMD 83. Chỉ gửi hộp thoại
     * sẽ để client kẹt ở màn hình chờ; gửi lại bản ghi RPG của chính người
     * chơi trước để client khôi phục GameScrRPG, sau đó mới hiện lý do.
     */
    private void tuChoiVaoLuyenTap(String lyDo) {
        ChickenQuanLyMayChu.log(
                "[BAT_DAU][TU_CHOI_TRANG_BI] mode=LuyenTap"
                + " playerId=" + this.ma
                + " zoneId=" + this.zoneId);
        try {
            if (this.dichVu != null && this.zone != null
                    && this.zone.players_id.get(this.ma) == this) {
                this.dichVu.vaoCho(this);
            }
        } catch (IOException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName())
                    .log(Level.WARNING,
                            "Không thể khôi phục màn RPG sau khi từ chối luyện tập",
                            ex);
        }
        this.startOKDlg2(lyDo);
    }

    public void handleTrainingMove(ChickenTinNhan ms) throws IOException {
        if (!this.inTraining || this.trainingSession.trainingCurrentTurn != TRAINING_PLAYER_INDEX || this.trainingSession.trainingWaitingShotEnd || this.trainingSession.trainingBotAnimating) {
            return;
        }
        ChickenYeuCauToaDoServer.ToaDo yeuCau = ChickenYeuCauToaDoServer.doc(ms);
        if (yeuCau == null) {
            return;
        }
        boolean avgBay = this.isFlyAvenger();
        if (yeuCau.getX() < 0
                || yeuCau.getX() >= this.trainingSession.trainingMap.getWidth()) {
            // Packet X nam ngoai map la du lieu gia/malformed, khong duoc bien
            // thanh hanh dong "chay toi mep vuc" roi xu nguoi choi chet.
            this.dichVu.guiCapNhatXYLuyenTap(
                    TRAINING_PLAYER_INDEX,
                    this.trainingSession.trainingPlayerX,
                    this.trainingSession.trainingPlayerY);
            return;
        }
        ChickenDiChuyenServer.KetQua ketQuaDiChuyen =
                ChickenDiChuyenServer.xuLy(
                        this.trainingSession.trainingMap,
                        this.trainingSession.trainingPlayerX,
                        this.trainingSession.trainingPlayerY,
                        yeuCau.getX(),
                        yeuCau.getY(),
                        this.trainingSession.trainingMoveRemaining,
                        avgBay
                );
        short xDuocPhep = ketQuaDiChuyen.getX();
        short yDuocPhep = ketQuaDiChuyen.getY();
        this.trainingSession.trainingMoveRemaining = ketQuaDiChuyen.getConLai();

        if (!avgBay && ketQuaDiChuyen.isRoiKhoiNen()) {
            ChickenQuanLyMayChu.log(
                    "[LUYEN_TAP][ROI_VUC] playerId=" + this.ma
                    + " xCu=" + this.trainingSession.trainingPlayerX
                    + " yCu=" + this.trainingSession.trainingPlayerY
                    + " xMep=" + xDuocPhep
                    + " clientX=" + yeuCau.getX()
                    + " clientY=" + yeuCau.getY());
            this.trainingSession.trainingPlayerX = xDuocPhep;
            this.trainingSession.trainingPlayerY = yDuocPhep;
            this.xuLyNguoiChoiRoiMapLuyenTap();
            return;
        }
        if (!avgBay && this.daRoiKhoiMapLuyenTap(xDuocPhep, yDuocPhep)) {
            this.xuLyNguoiChoiRoiMapLuyenTap();
            return;
        }
        if (!avgBay) {
            xDuocPhep = this.kepShort(
                    xDuocPhep, 0, this.trainingSession.trainingMap.getWidth());
            yDuocPhep = this.kepShort(
                    yDuocPhep, 0, this.trainingSession.trainingMap.getHeight());
        }
        this.trainingSession.trainingPlayerX = xDuocPhep;
        this.trainingSession.trainingPlayerY = yDuocPhep;
        this.dichVu.guiCapNhatXYLuyenTap(
                TRAINING_PLAYER_INDEX,
                this.trainingSession.trainingPlayerX,
                this.trainingSession.trainingPlayerY
        );
    }

    /**
     * Bỏ lượt luyện tập từ CMD 49 rỗng. Client chỉ gửi ý định; server tự kiểm
     * tra lượt, trạng thái animation và tự tính lại hàng đợi nạp đạn.
     */
    public synchronized void boLuotLuyenTap() throws IOException {
        this.ketThucLuotLuyenTapKhongTaoDan(false);
    }

    private void ketThucLuotLuyenTapKhongTaoDan(boolean doHetGio)
            throws IOException {
        if (!this.inTraining
                || this.trainingSession.trainingCurrentTurn
                    != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingWaitingShotEnd
                || this.trainingSession.trainingBotAnimating
                || this.trainingSession.trainingBossState
                    == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState
                    == TrainingBossState.ROUND_END
                || (!doHetGio
                    && (this.trainingSession.trainingHawkSkillActive
                        || this.trainingSession.trainingThorSkillActive
                        || this.trainingSession.trainingLokiSkillActive))) {
            return;
        }
        // Một phát đã được server tạo thì không được đổi thành bỏ lượt chỉ vì
        // animation client kéo dài. Task chốt phát bắn sẽ tự chuyển lượt.
        if (this.trainingSession.trainingLastShotTurnId
                == this.trainingSession.trainingTurnId) {
            return;
        }

        if (doHetGio) {
            // Hủy mọi menu/skill mới chỉ được chọn nhưng chưa tạo phát đánh.
            // Tăng ID để callback cũ không thể gây damage sau khi đã qua lượt.
            this.trainingSession.trainingHawkSkillId++;
            this.trainingSession.trainingHawkSkillActive = false;
            this.trainingSession.trainingThorSkillId++;
            this.trainingSession.trainingThorSkillActive = false;
            this.trainingSession.trainingLokiSkillId++;
            this.trainingSession.trainingLokiSkillActive = false;
            this.trainingSession.trainingLokiDaDungKyNang = false;
        }
        this.trainingSession.trainingLokiDangChoChonMucTieu = false;
        this.trainingSession.trainingUltronDangBanX3 = false;
        if (this.trainingSession.trainingVatPhamChienBinh != null) {
            this.trainingSession.trainingVatPhamChienBinh
                    .xoaVatPhamChienDangCho();
        }
        if (this.trainingSession.trainingIronManLaserSanSang) {
            this.trainingSession.trainingIronManLaserSanSang = false;
            this.dichVu.guiTrangThaiNgamLaserIronMan(false);
        }
        this.xoaTrangThaiPhatBanNguoiChoi();
        this.chuyenLuotTheoNapDan(
                TRAINING_PLAYER_INDEX,
                ChickenNapDanServer.layKhiKhongTaoPhatDan());
    }

    public synchronized void xuLyBanLuyenTap(ChickenTinNhan ms) throws IOException {
        if (!this.inTraining) {
            return;
        }
        if (this.trainingSession.trainingCurrentTurn != TRAINING_PLAYER_INDEX) {
            this.guiTrangThaiLuotLuyenTap();
            return;
        }
        if (this.avenger == AVG_LOKI
                && this.trainingSession.trainingLokiDangChoChonMucTieu) {
            this.trainingSession.trainingLokiDangChoChonMucTieu = false;
        }
        long now = System.currentTimeMillis();
        if (this.trainingSession.trainingWaitingShotEnd) {
            if (now - this.lastTrainingFire <= 3000L) {
                return;
            }
            if (this.trainingSession.trainingPlayerResolveTask != null) {
                this.trainingSession.trainingPlayerResolveTask.cancel(false);
                this.trainingSession.trainingPlayerResolveTask = null;
            }
            this.guiDongBoToaDoSauPhaDiaHinhLuyenTap();
            this.xoaTrangThaiPhatBanNguoiChoi();
            this.trainingSession.trainingLastShotTurnId = -1L;
        }
        if (this.trainingSession.trainingLastShotTurnId == this.trainingSession.trainingTurnId) {
            return;
        }
        if (this.trainingSession.trainingBotAnimating) {
            this.cancelTrainingBotTask();
            if (this.trainingSession.trainingBotReturnTask != null) {
                this.trainingSession.trainingBotReturnTask.cancel(false);
                this.trainingSession.trainingBotReturnTask = null;
            }
            this.trainingSession.trainingBotAnimating = false;
            this.trainingSession.trainingActiveBotIndex = -1;
        }
        if (this.trainingSession.trainingBossState == TrainingBossState.DEAD || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END) {
            return;
        }
        if (this.trainingSession.trainingBossState != TrainingBossState.IDLE) {
            this.cancelTrainingBotTask();
            if (this.trainingSession.trainingBotReturnTask != null) {
                this.trainingSession.trainingBotReturnTask.cancel(false);
                this.trainingSession.trainingBotReturnTask = null;
            }
            this.trainingSession.trainingBotAnimating = false;
            this.trainingSession.trainingActiveBotIndex = -1;
            this.trainingSession.trainingBossState = TrainingBossState.IDLE;
        }
        if (now - this.lastTrainingFire < 250L) {
            return;
        }
        // Server tự đọc đúng súng đang trang bị. Không dùng ID/loại đạn/số viên
        // do client gửi lên làm nguồn tin cậy.
        ChickenVatPham sung = this.laySungDangTrangBiHopLe();
        if (sung == null) {
            this.moHopThoaiOK("Súng đang trang bị không còn hợp lệ.");
            return;
        }
        ChickenChienBinh chienBinhVatPham =
                this.trainingSession.trainingVatPhamChienBinh;
        ChickenChienBinh.VatPhamChienTrongTran vatPhamDangCho =
                chienBinhVatPham == null
                        ? null : chienBinhVatPham.layVatPhamChienDangCho();
        if (vatPhamDangCho != null) {
            ChickenYeuCauBanServer.KetQua yeuCauVatPham =
                    ChickenYeuCauBanServer.docVatPham(
                            ms, vatPhamDangCho.getCauHinh());
            if (yeuCauVatPham == null) {
                this.moHopThoaiOK(
                        "Du lieu nem vat pham khong hop le.");
                this.guiTrangThaiLuotLuyenTap();
                return;
            }
            int tanCongVatPham = this.layTongTanCongHienTai();
            int napDanVatPham =
                    this.layThoiGianNapDanNguoiChoi(sung);
            if (tanCongVatPham <= 0 || napDanVatPham <= 0) {
                this.moHopThoaiOK(
                        "Chi so chien dau hien tai khong hop le.");
                return;
            }
            if (!this.isFlyAvenger()
                    && this.daRoiKhoiMapLuyenTap(
                            this.trainingSession.trainingPlayerX,
                            this.trainingSession.trainingPlayerY)) {
                this.xuLyNguoiChoiRoiMapLuyenTap();
                return;
            }
            this.xuLyPhatBanVatPhamLuyenTap(
                    vatPhamDangCho,
                    yeuCauVatPham,
                    tanCongVatPham,
                    napDanVatPham,
                    now);
            return;
        }
        DuLieuSung duLieuDan = ChickenQuanLyDanSung.theoSungDangTrangBi(sung);
        if (duLieuDan == null) {
            this.moHopThoaiOK("Server chưa có công thức cho súng đang trang bị.");
            return;
        }
        final int idSungMayChu = duLieuDan.getIdSung();
        final ChickenCauHinhSatThuongSung.HoSoSatThuong hoSoSatThuong =
                ChickenCauHinhSatThuongSung.theoIdSung(idSungMayChu);
        if (hoSoSatThuong == null) {
            this.moHopThoaiOK("Server chua co cau hinh damage cho sung dang trang bi.");
            return;
        }
        ChickenDuLieuPhatBanLuyenTap phatBan = this.docPhatBanLuyenTap(
                ms,
                duLieuDan.getLoaiDan()
        );
        if (phatBan == null) {
            this.moHopThoaiOK("Không đọc được dữ liệu phát bắn. Hãy thử bắn lại.");
            this.guiTrangThaiLuotLuyenTap();
            return;
        }

        short goc = this.chuanHoaGocBan(phatBan.goc);
        if (this.avenger == AVG_IRON_MAN) {
            goc = ChickenTiaLaserIronMan.chuanHoaGoc(goc);
        }
        if (!ChickenQuanLyCongThucSung.laGocBanHopLeChoSung(
                goc, idSungMayChu)) {
            this.moHopThoaiOK("Súng laser chỉ được bắn từ góc 20 đến 160 độ.");
            this.guiTrangThaiLuotLuyenTap();
            return;
        }
        goc = ChickenQuanLyCongThucSung.chuanHoaGocTheoIdSung(
                goc, idSungMayChu);

        // X/Y trong packet bắn không được ghi đè state. AVG bay cũng phải gửi
        // packet di chuyển hợp lệ trước; quỹ đạo luôn xuất phát từ tọa độ server.

        int satThuong = this.layTongTanCongHienTai();
        int napDan = this.layThoiGianNapDanNguoiChoi(sung);
        if (satThuong <= 0 || napDan <= 0) {
            this.moHopThoaiOK("Súng đang trang bị không còn hợp lệ.");
            return;
        }
        this.trainingSession.trainingPlayerReloadTime = napDan;
        this.trainingSession.trainingLastShotTurnId = this.trainingSession.trainingTurnId;
        this.lastTrainingFire = now;
        byte luc = phatBan.luc;
        if (luc <= 0) {
            luc = 10;
        }
        if (luc > 30) {
            luc = 30;
        }
        byte lucPhu = phatBan.lucPhu;
        if (lucPhu <= 0) {
            lucPhu = 1;
        }
        if (lucPhu > 30) {
            lucPhu = 30;
        }
        if (!this.isFlyAvenger()
                && this.daRoiKhoiMapLuyenTap(
                        this.trainingSession.trainingPlayerX,
                        this.trainingSession.trainingPlayerY)) {
            this.xuLyNguoiChoiRoiMapLuyenTap();
            return;
        }
        if (this.avenger == AVG_IRON_MAN
                && this.trainingSession.trainingIronManLaserSanSang) {
            this.xuLyPhatLaserIronManLuyenTap(
                    goc,
                    satThuong,
                    this.trainingSession.trainingPlayerX,
                    this.trainingSession.trainingPlayerY
            );
            return;
        }
        if (this.avenger == AVG_ULTRON) {
            this.trainingSession.trainingUltronGocNgamHienTai = goc;
            this.trainingSession.trainingUltronLucNgamHienTai = luc;
            this.trainingSession.trainingUltronDaCoGocNgam = true;
        }
        short[] diemBan = this.layDiemBanNguoiChoiLuyenTap(goc);
        short muzzleX = diemBan[0];
        short muzzleY = diemBan[1];
        // Hulk tu bay theo path sau khi client nhan packet. Packet ket qua van
        // phai neo o vi tri truoc luc bay, neu khong client se nhay thang toi
        // diem cuoi roi lai ve dau duong dan.
        short xNguoiBanTruocPhat = this.trainingSession.trainingPlayerX;
        short yNguoiBanTruocPhat = this.trainingSession.trainingPlayerY;
        boolean hulkLaNguoiDan = ChickenCoCheHulk.laHulk(this.avenger);
        boolean banX3Ultron = this.avenger == AVG_ULTRON
                && this.trainingSession.trainingUltronDangBanX3;
        if (banX3Ultron) {
            this.xuLyPhatBanX3UltronLuyenTap(
                    duLieuDan.getLoaiDan(),
                    goc,
                    luc,
                    muzzleX,
                    muzzleY,
                    satThuong,
                    xNguoiBanTruocPhat,
                    yNguoiBanTruocPhat
            );
            return;
        }
        int soVienMoiLoat = banX3Ultron
                ? 3
                : Math.max(1, duLieuDan.getSoVienMoiLoat() & 255);
        boolean danGaHaiDuong = duLieuDan.getNhomSung() == 6
                && duLieuDan.getLoaiDan() == 19;
        boolean danRiuBonDuong = duLieuDan.getNhomSung() == 8
                && duLieuDan.getLoaiDan() == 17;
        int soDuongMoiLoat = danRiuBonDuong
                ? 4
                : (danGaHaiDuong ? 2 : soVienMoiLoat);
        // ID súng, số viên và khoảng cách từng viên đều lấy từ cấu hình server.
        // Client chỉ còn cung cấp góc/lực theo định dạng packet cũ.
        // Không dùng soPhat client để tránh lệch MG/AK hoặc xử lý một phát nhiều lần.
        int soLoat = 1;
        int tongSoVien = Math.max(1, Math.min(255, soDuongMoiLoat));
        short[][] cacDuongX = new short[tongSoVien][];
        short[][] cacDuongY = new short[tongSoVien][];
        byte windXMayChu = ChickenHeThongGio.layWindXChoSung(
                this.trainingSession.trainingWind, idSungMayChu);
        byte windYMayChu = ChickenHeThongGio.layWindYChoSung(
                this.trainingSession.trainingWind, idSungMayChu);
        ChickenLoatDanServer.KetQua loatKhongDiaHinh = ChickenLoatDanServer.tao(
                muzzleX,
                muzzleY,
                this.trainingSession.trainingPlayerX,
                this.trainingSession.trainingPlayerY,
                goc,
                luc,
                lucPhu,
                duLieuDan,
                windXMayChu,
                windYMayChu,
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() {
                        return ChickenNguoiChoi.this.trainingSession.trainingMap.getWidth();
                    }

                    @Override
                    public int getHeight() {
                        return ChickenNguoiChoi.this.trainingSession.trainingMap.getHeight();
                    }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return false;
                    }

                    @Override
                    public short[][] layCacVoiRong() {
                        return ChickenNguoiChoi.this.trainingSession
                                .trainingMap.layCacVoiRong();
                    }
                }
        );
        short[][] cacDuongSieuCaoX = loatKhongDiaHinh.getCacDuongX();
        short[][] cacDuongSieuCaoY = loatKhongDiaHinh.getCacDuongY();
        boolean sieuCaoHieuUng = false;
        int soVienTuTrung = 0;
        int[] soVienTrungBot = new int[TRAINING_BOT_COUNT];
        int[] satThuongBot = new int[TRAINING_BOT_COUNT];
        int satThuongTuThan = 0;
        int satThuongMoiVien = banX3Ultron
                ? Math.max(1, satThuong)
                : Math.max(
                        1,
                        (satThuong + soDuongMoiLoat - 1) / Math.max(1, soDuongMoiLoat)
                );
        ChickenCongThucBanUltron.LoatBaTia loatUltron = banX3Ultron
                ? ChickenCongThucBanUltron.taoBaTiaHoiTu(
                        muzzleX,
                        muzzleY,
                        goc,
                        this.trainingSession.trainingMap.getWidth(),
                        this.trainingSession.trainingMap.getHeight()
                )
                : null;
        int doDaiLonNhat = 1;
        int chiSoDuong = 0;
        short diemTachRiuX = muzzleX;
        short diemTachRiuY = muzzleY;
        short[][][] cacDuongConRiu = new short[3][][];
        for (int loat = 0; loat < soLoat && chiSoDuong < tongSoVien; loat++) {
            for (int vien = 0; vien < soDuongMoiLoat && chiSoDuong < tongSoVien; vien++) {
                short gocVienDan = this.chuanHoaGocBan(
                        (short) (goc + this.layDoLechGocDanLuyenTap(
                                duLieuDan.getNhomSung(),
                                soVienMoiLoat,
                                vien))
                );
                short[][] duongDan;
                if (banX3Ultron) {
                    duongDan = new short[][]{
                        loatUltron.getX()[vien],
                        loatUltron.getY()[vien]
                    };
                } else if (this.avenger == AVG_ULTRON) {
                    /*
                     * Công thức Ultron phụ thuộc AVG, không phụ thuộc part súng
                     * client gửi hoặc mapping vật phẩm. Tia bay thẳng, bỏ hoàn
                     * toàn gió/trọng lực nhưng vẫn dừng ở địa hình hoặc nhân vật.
                     */
                    ChickenCongThucBanUltron.DuongTia tiaUltron =
                            ChickenCongThucBanUltron.taoTiaThang(
                                    muzzleX,
                                    muzzleY,
                                    goc,
                                    this.trainingSession.trainingMap.getWidth(),
                                    this.trainingSession.trainingMap.getHeight()
                            );
                    duongDan = new short[][]{
                        tiaUltron.getX(),
                        tiaUltron.getY()
                    };
                } else if (danGaHaiDuong && vien == 1) {
                    short[] duongChinhX = cacDuongX[0];
                    short[] duongChinhY = cacDuongY[0];
                    int soDiemChinh = Math.min(
                            duongChinhX == null ? 0 : duongChinhX.length,
                            duongChinhY == null ? 0 : duongChinhY.length
                    );
                    int buocTha = Math.max(
                            1,
                            Math.min(lucPhu & 255, Math.max(1, soDiemChinh))
                    );
                    lucPhu = (byte) buocTha;
                    int chiSoTha = buocTha - 1;
                    short thaX = soDiemChinh > 0 ? duongChinhX[chiSoTha] : muzzleX;
                    short thaY = soDiemChinh > 0 ? duongChinhY[chiSoTha] : muzzleY;
                    short batDauRoiY = this.kepShort(
                            thaY + 8,
                            Short.MIN_VALUE,
                            Short.MAX_VALUE
                    );
                    duongDan = this.taoDuongDanGaRoiLuyenTap(
                            thaX,
                            batDauRoiY,
                            idSungMayChu
                    );
                } else if (danRiuBonDuong && vien > 0) {
                    if (cacDuongConRiu[0] == null) {
                        // Ba nhánh tách ra đồng thời nên đều phải được tính trên
                        // cùng một mặt nạ địa hình, trước khi nhánh đầu tạo lỗ.
                        for (int i = 0; i < cacDuongConRiu.length; i++) {
                            cacDuongConRiu[i] = this.taoDuongDanConRiuLuyenTap(
                                    diemTachRiuX,
                                    diemTachRiuY,
                                    goc,
                                    luc,
                                    i,
                                    idSungMayChu
                            );
                        }
                    }
                    duongDan = cacDuongConRiu[vien - 1];
                } else {
                    duongDan = this.taoDuongDanLuyenTap(
                            muzzleX, muzzleY, gocVienDan, luc);
                }
                if (danRiuBonDuong && vien == 0) {
                    int soDiemChinh = Math.min(
                            duongDan[0] == null ? 0 : duongDan[0].length,
                            duongDan[1] == null ? 0 : duongDan[1].length
                    );
                    int buocTach = Math.max(
                            1,
                            Math.min(lucPhu & 255, Math.max(1, soDiemChinh))
                    );
                    lucPhu = (byte) buocTach;
                    if (soDiemChinh > 0) {
                        diemTachRiuX = duongDan[0][buocTach - 1];
                        diemTachRiuY = duongDan[1][buocTach - 1];
                        duongDan = new short[][]{
                            Arrays.copyOf(duongDan[0], buocTach),
                            Arrays.copyOf(duongDan[1], buocTach)
                        };
                    }
                }
                if (this.avenger == AVG_ULTRON) {
                    /*
                     * Trước hết cắt tia tại map. Sau đó mới quét nhân vật trên
                     * phần đường còn lại để mục tiêu sau tường không bị trúng.
                     */
                    duongDan = this.catDuongDanUltronTaiVaChamBanDoLuyenTap(
                            duongDan[0], duongDan[1]);
                }
                short[] duongSieuCaoX = chiSoDuong < cacDuongSieuCaoX.length
                        ? cacDuongSieuCaoX[chiSoDuong] : null;
                short[] duongSieuCaoY = chiSoDuong < cacDuongSieuCaoY.length
                        ? cacDuongSieuCaoY[chiSoDuong] : null;
                boolean sieuCaoNguoiChoi = !hulkLaNguoiDan
                        && ChickenSieuCao.laPhatSieuCaoTrungMucTieu(
                                phatBan.loaiDan,
                                duongSieuCaoX,
                                duongSieuCaoY,
                                this.trainingSession.trainingPlayerX,
                                this.trainingSession.trainingPlayerY,
                                false
                        );
                boolean[] sieuCaoBot = new boolean[TRAINING_BOT_COUNT];
                for (int botIndex = 0; botIndex < TRAINING_BOT_COUNT; botIndex++) {
                    if (this.trainingSession.trainingBotDead[botIndex]) {
                        continue;
                    }
                    sieuCaoBot[botIndex] = ChickenSieuCao.laPhatSieuCaoTrungMucTieu(
                            phatBan.loaiDan,
                            duongSieuCaoX,
                            duongSieuCaoY,
                            this.trainingSession.trainingBotX[botIndex],
                            this.trainingSession.trainingBotY[botIndex],
                            true
                    );
                    if (chiSoDuong == 0 && sieuCaoBot[botIndex]) {
                        sieuCaoHieuUng = true;
                    }
                }
                if (chiSoDuong == 0 && sieuCaoNguoiChoi) {
                    sieuCaoHieuUng = true;
                }
                boolean danCaptainXuyenNguoi =
                        idSungMayChu == ChickenQuanLyCongThucSung.ID_SUNG_CAPTAIN;
                TrainingCharacterHit vaChamNhanVat = danCaptainXuyenNguoi
                        ? null
                        : this.timVaChamNhanVatTrenDuongDanLuyenTap(
                                duongDan[0], duongDan[1], muzzleX, muzzleY,
                                !hulkLaNguoiDan, true);
                if (danCaptainXuyenNguoi) {
                    /*
                     * Gun 15 không cắt quỹ đạo tại thân người. Mỗi mục tiêu mà
                     * viên đạn lướt qua nhận 50%; nếu điểm cuối thật sự chạm
                     * terrain ngay dưới chân thì damage đầy đủ được ưu tiên.
                     */
                    boolean[] biLuotQua = this.timTatCaNhanVatBiCaptainLuotQuaLuyenTap(
                            duongDan[0], duongDan[1]);
                    int satThuongDayDu = satThuongMoiVien;
                    int satThuongMotNua = Math.max(1, satThuongDayDu / 2);
                    boolean noTrenDiaHinh =
                            this.diemCuoiLaDiaHinhLuyenTap(duongDan[0], duongDan[1]);
                    int soDiem = Math.min(duongDan[0].length, duongDan[1].length);
                    int xNo = soDiem > 0 ? duongDan[0][soDiem - 1] : muzzleX;
                    int yNo = soDiem > 0 ? duongDan[1][soDiem - 1] : muzzleY;

                    int satThuongNguoiChoi = biLuotQua[0] ? satThuongMotNua : 0;
                    if (noTrenDiaHinh) {
                        satThuongNguoiChoi = Math.max(
                                satThuongNguoiChoi,
                                this.tinhSatThuongNoNguoiChoiLuyenTap(
                                        hoSoSatThuong,
                                        xNo,
                                        yNo,
                                        satThuongDayDu
                                )
                        );
                    }
                    if (satThuongNguoiChoi > 0) {
                        soVienTuTrung++;
                        satThuongTuThan = this.congSoNguyenAnToan(
                                satThuongTuThan, satThuongNguoiChoi);
                    }

                    for (int botIndex = 0; botIndex < TRAINING_BOT_COUNT; botIndex++) {
                        if (this.trainingSession.trainingBotDead[botIndex]) {
                            continue;
                        }
                        int satThuongBoss = biLuotQua[botIndex + 1]
                                ? satThuongMotNua
                                : 0;
                        if (noTrenDiaHinh) {
                            satThuongBoss = Math.max(
                                    satThuongBoss,
                                    this.tinhSatThuongNoBossLuyenTap(
                                            hoSoSatThuong,
                                            botIndex,
                                            xNo,
                                            yNo,
                                            satThuongDayDu
                                    )
                            );
                        }
                        if (satThuongBoss > 0) {
                            soVienTrungBot[botIndex]++;
                            satThuongBot[botIndex] = this.congSoNguyenAnToan(
                                    satThuongBot[botIndex], satThuongBoss);
                        }
                    }
                } else if (vaChamNhanVat != null) {
                    /*
                     * Cả ba tia đều được cắt tại nhân vật để hiệu ứng không
                     * xuyên người. Riêng Bắn x3 chỉ tia giữa (index 1) là tia
                     * thật; hai tia ngoài không cộng hit và không gây damage.
                     */
                    duongDan = this.catDuongDanTaiVaChamNhanVatLuyenTap(
                            duongDan[0], duongDan[1], vaChamNhanVat);
                    if (hulkLaNguoiDan) {
                        this.chotDiemCuoiHulkTaiMucTieuLuyenTap(
                                duongDan, vaChamNhanVat);
                    }
                    boolean tiaGaySatThuong = !banX3Ultron || vien == 1;
                    if (tiaGaySatThuong && hoSoSatThuong.coNoTheoKhoangCach()) {
                        int soDiemVaCham = Math.min(duongDan[0].length, duongDan[1].length);
                        int xNo = soDiemVaCham > 0
                                ? duongDan[0][soDiemVaCham - 1]
                                : vaChamNhanVat.hitX;
                        int yNo = soDiemVaCham > 0
                                ? duongDan[1][soDiemVaCham - 1]
                                : vaChamNhanVat.hitY;
                        if (!hulkLaNguoiDan) {
                            int damageTuThan = this.tinhSatThuongNoNguoiChoiLuyenTap(
                                    hoSoSatThuong,
                                    xNo,
                                    yNo,
                                    sieuCaoNguoiChoi
                                            ? ChickenSieuCao.tangSatThuong(satThuongMoiVien)
                                            : satThuongMoiVien);
                            if (damageTuThan > 0) {
                                soVienTuTrung++;
                                satThuongTuThan = this.congSoNguyenAnToan(
                                        satThuongTuThan, damageTuThan);
                            }
                        }
                        for (int botIndex = 0; botIndex < TRAINING_BOT_COUNT; botIndex++) {
                            if (this.trainingSession.trainingBotDead[botIndex]) {
                                continue;
                            }
                            int damageBot = this.tinhSatThuongNoBossLuyenTap(
                                    hoSoSatThuong,
                                    botIndex,
                                    xNo,
                                    yNo,
                                    sieuCaoBot[botIndex]
                                            ? ChickenSieuCao.tangSatThuong(satThuongMoiVien)
                                            : satThuongMoiVien
                            );
                            if (damageBot > 0) {
                                soVienTrungBot[botIndex]++;
                                satThuongBot[botIndex] = this.congSoNguyenAnToan(
                                        satThuongBot[botIndex], damageBot);
                            }
                        }
                    } else if (tiaGaySatThuong && !hulkLaNguoiDan
                            && vaChamNhanVat.laNguoiChoi()) {
                        soVienTuTrung++;
                        satThuongTuThan = this.congSoNguyenAnToan(
                                satThuongTuThan,
                                sieuCaoNguoiChoi
                                        ? ChickenSieuCao.tangSatThuong(satThuongMoiVien)
                                        : satThuongMoiVien);
                    } else if (tiaGaySatThuong
                            && vaChamNhanVat.botIndex >= 0
                            && vaChamNhanVat.botIndex < TRAINING_BOT_COUNT) {
                        int satThuongVienThucTe = sieuCaoBot[vaChamNhanVat.botIndex]
                                ? ChickenSieuCao.tangSatThuong(satThuongMoiVien)
                                : satThuongMoiVien;
                        soVienTrungBot[vaChamNhanVat.botIndex]++;
                        satThuongBot[vaChamNhanVat.botIndex] = this.congSoNguyenAnToan(
                                satThuongBot[vaChamNhanVat.botIndex],
                                satThuongVienThucTe);
                    }
                } else if (this.avenger != AVG_ULTRON) {
                    int soDiemDuongDan = Math.min(duongDan[0].length, duongDan[1].length);
                    if (soDiemDuongDan > 0) {
                        int xNo = duongDan[0][soDiemDuongDan - 1];
                        int yNo = duongDan[1][soDiemDuongDan - 1];
                        boolean coNoTrenDiaHinh =
                                this.diemCuoiLaDiaHinhLuyenTap(duongDan[0], duongDan[1]);
                        int satThuongLanNguoiChoi = hulkLaNguoiDan || !coNoTrenDiaHinh
                                ? 0
                                : this.tinhSatThuongNoNguoiChoiLuyenTap(
                                        hoSoSatThuong,
                                        xNo,
                                        yNo,
                                        sieuCaoNguoiChoi
                                                ? ChickenSieuCao.tangSatThuong(satThuongMoiVien)
                                                : satThuongMoiVien);
                        if (satThuongLanNguoiChoi > 0) {
                            satThuongTuThan = this.congSoNguyenAnToan(
                                    satThuongTuThan, satThuongLanNguoiChoi);
                        }
                        for (int botIndex = 0; botIndex < TRAINING_BOT_COUNT; botIndex++) {
                            if (this.trainingSession.trainingBotDead[botIndex]) {
                                continue;
                            }
                            int satThuongLanBoss = !coNoTrenDiaHinh
                                    ? 0
                                    : this.tinhSatThuongNoBossLuyenTap(
                                            hoSoSatThuong,
                                            botIndex,
                                            xNo,
                                            yNo,
                                            sieuCaoBot[botIndex]
                                                    ? ChickenSieuCao.tangSatThuong(satThuongMoiVien)
                                                    : satThuongMoiVien
                                    );
                            if (satThuongLanBoss > 0) {
                                satThuongBot[botIndex] = this.congSoNguyenAnToan(
                                        satThuongBot[botIndex], satThuongLanBoss);
                            }
                        }
                    }
                }
                // Mỗi viên có quỹ đạo, va chạm và lỗ địa hình riêng. Sau khi
                // một viên tạo lỗ, các viên sau được kiểm tra trên mặt nạ mới.
                if (this.avenger != AVG_ULTRON) {
                    int loaiDanTaoLo =
                            ChickenLoaiDanPhaDiaHinhClient.layLoaiDanTaoLo(
                                    phatBan.loaiDan, chiSoDuong);
                    if (loaiDanTaoLo
                            != ChickenLoaiDanPhaDiaHinhClient.KHONG_PHA_DIA_HINH) {
                        this.capNhatLoDiaHinhTheoDuongDanLuyenTap(
                                duongDan[0], duongDan[1], vaChamNhanVat,
                                (byte) loaiDanTaoLo);
                    }
                }
                // Giữ nguyên quỹ đạo thật. Riêng MG 5 viên sẽ được gửi thành
                // 5 packet riêng theo thời gian, không gửi đồng thời trong cùng
                // packet vì client Army3 vẽ hai sprite nằm ngang cạnh nhau.
                short[][] duongDanHienThi = duongDan;
                cacDuongX[chiSoDuong] = duongDanHienThi[0];
                cacDuongY[chiSoDuong] = duongDanHienThi[1];
                doDaiLonNhat = Math.max(doDaiLonNhat, duongDanHienThi[0].length);
                chiSoDuong++;
            }
        }
        this.trainingSession.trainingXacNhanDanSomNhatMs =
                System.currentTimeMillis()
                + ChickenThoiGianHoatAnhDan.tinh(cacDuongX, cacDuongY);
        this.trainingSession.trainingPendingSelfHitCount = soVienTuTrung;
        this.trainingSession.trainingPendingSelfDamage = satThuongTuThan;
        System.arraycopy(
                soVienTrungBot,
                0,
                this.trainingSession.trainingPendingBotHitCounts,
                0,
                TRAINING_BOT_COUNT
        );
        System.arraycopy(
                satThuongBot,
                0,
                this.trainingSession.trainingPendingBotDamages,
                0,
                TRAINING_BOT_COUNT
        );
        this.trainingSession.trainingPendingDamagePerBullet = satThuongMoiVien;
        this.trainingSession.trainingWaitingShotEnd = true;
        final long shotId = ++this.trainingSession.trainingActiveShotId;
        this.trainingSession.trainingActiveShotResolved = false;
        this.chuanBiMayManTruocPhatLuyenTap();
        // Loại đạn và số quỹ đạo đều lấy từ súng đang trang bị. Client chỉ gửi
        // góc/lực; server không còn dùng loại đạn cũ hoặc số viên do client đoán.
        if (danGaHaiDuong || danRiuBonDuong) {
            if (cacDuongX[0] != null && cacDuongY[0] != null
                    && cacDuongX[0].length > 0 && cacDuongY[0].length > 0) {
                cacDuongX[0][0] = muzzleX;
                cacDuongY[0][0] = muzzleY;
            }
        } else {
            ChickenLuyenTapToaDo.datDiemDauTaiDauNong(
                    cacDuongX, cacDuongY, muzzleX, muzzleY);
        }
        boolean hulkRoiKhoiMap = false;
        // MG42 gửi từng viên bằng các task độc lập theo thời gian. Viên sau
        // không phụ thuộc vào việc viên trước đã bay xong hay đã va chạm.
        this.huyLoatMgDangCho();
        this.trainingSession.trainingMgBurstEndAt = 0L;
        try (ChickenNguCanhLaySung.Phien ignored =
                ChickenNguCanhLaySung.batDauPhatBanNguoiChoi()) {
        if (idSungMayChu == 130) {
            final int tongSoVienMg = Math.min(
                    ChickenLuyenTapBan.SO_VIEN_MG,
                    Math.min(cacDuongX.length, cacDuongY.length)
            );
            short[][] duongMgX = new short[tongSoVienMg][];
            short[][] duongMgY = new short[tongSoVienMg][];
            System.arraycopy(cacDuongX, 0, duongMgX, 0, tongSoVienMg);
            System.arraycopy(cacDuongY, 0, duongMgY, 0, tongSoVienMg);

            this.trainingSession.trainingMgBurstShotId = shotId;
            this.trainingSession.trainingMgBurstGunId = idSungMayChu;
            this.trainingSession.trainingMgBurstTotal = tongSoVienMg;
            this.trainingSession.trainingMgBurstSent = tongSoVienMg;
            this.trainingSession.trainingMgBurstEndAt = System.currentTimeMillis() + 200L;

            // Client gốc dùng gun=5/bulletType=11 cho MG: một đường ngắm với
            // vật lý gió 30, trọng lực 90 và năm viên bay cùng góc/lực.
            // soPhat phải là 1 vì năm viên đã nằm trọn trong NBULL; gửi 5 ở
            // cả hai trường làm client chờ thêm các loạt không tồn tại.
            this.dichVu.guiKetQuaBanLuyenTap(
                    TRAINING_PLAYER_INDEX,
                    duLieuDan.getLoaiDan(),
                    xNguoiBanTruocPhat,
                    yNguoiBanTruocPhat,
                    goc,
                    luc,
                    luc,
                    (byte) 1,
                    duongMgX,
                    duongMgY,
                    sieuCaoHieuUng,
                    this.phatBanLuyenTapDangDungPow()
            );
        } else if (banX3Ultron) {
            /*
             * Gửi một tia thật và hai tia hiệu ứng bằng bulletType 0 gốc.
             * Client sẽ không kích hoạt trạng thái bay của bulletType 2.
             */
            this.dichVu.guiLoatLaserUltronLuyenTap(
                    TRAINING_PLAYER_INDEX,
                    xNguoiBanTruocPhat,
                    yNguoiBanTruocPhat,
                    goc,
                    luc,
                    cacDuongX,
                    cacDuongY
            );
        } else {
            this.dichVu.guiKetQuaBanLuyenTap(
                    TRAINING_PLAYER_INDEX,
                    duLieuDan.getLoaiDan(),
                    xNguoiBanTruocPhat,
                    yNguoiBanTruocPhat,
                    goc,
                    luc,
                    lucPhu,
                    (byte) 1,
                    cacDuongX,
                    cacDuongY,
                    sieuCaoHieuUng,
                    this.phatBanLuyenTapDangDungPow()
            );
        }
        }
        this.dongBoToaDoBaySauKhiBanLuyenTap();
        hulkRoiKhoiMap = this.apDungViTriHulkSauPhatLuyenTap(
                cacDuongX, cacDuongY);
        if (banX3Ultron) {
            this.trainingSession.trainingUltronDangBanX3 = false;
            System.out.println("[ULTRON] BAN_X3 mode=training goc=" + goc
                    + " dichX=" + loatUltron.getDichX()
                    + " dichY=" + loatUltron.getDichY()
                    + " tiaThat=1 tiaHieuUng=2");
        }
        /*
         * Kết toán HP sớm theo kết quả va chạm server đã tính. Không chờ toàn
         * bộ animation dài vì client có thể gửi trạng thái lượt khác làm phát
         * bắn bị treo hoặc bị xóa trước khi trừ máu.
         */
        long thoiGianKetToan = Math.max(
                180L,
                Math.min(450L, doDaiLonNhat * 8L)
        );
        this.scheduleTrainingPlayerResolve(
                shotId,
                thoiGianKetToan
        );
        if (hulkRoiKhoiMap) {
            this.trainingSession.trainingPendingSelfDamage =
                    this.trainingSession.trainingPlayerHp;
        }
    }

    /**
     * Xu ly item tao dan trong luyen tap bang cung engine PvP/boss.
     * Packet client chi con goc/luc; item, bullet, damage, va cham va tieu hao
     * deu duoc suy ra tu snapshot server.
     */
    private void xuLyPhatBanVatPhamLuyenTap(
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            ChickenYeuCauBanServer.KetQua yeuCau,
            int tanCong,
            int napDan,
            long now
    ) throws IOException {
        ChickenChienBinh nguoiBan =
                this.trainingSession.trainingVatPhamChienBinh;
        if (nguoiBan == null || vatPham == null || yeuCau == null
                || nguoiBan.layVatPhamChienDangCho() != vatPham
                || !this.coVatPhamChienSanSang(vatPham)
                || !nguoiBan.coTheDungVatPhamChienTrongTran(vatPham)) {
            return;
        }
        nguoiBan.x = this.trainingSession.trainingPlayerX;
        nguoiBan.y = this.trainingSession.trainingPlayerY;
        nguoiBan.hp = this.trainingSession.trainingPlayerHp;
        nguoiBan.mauToiDa =
                this.trainingSession.trainingPlayerMaxHp;
        nguoiBan.chet = nguoiBan.hp <= 0;
        nguoiBan.tanCong = Math.max(1, tanCong);
        nguoiBan.giap = Math.max(0, this.layTongGiapHienTai());
        nguoiBan.avenger = this.avenger;
        nguoiBan.maVuKhi = this.layMaHinhSungDangTrangBi();

        ChickenChienBinh[] mucTieu =
                new ChickenChienBinh[TRAINING_BOT_COUNT + 1];
        mucTieu[0] = nguoiBan;
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            ChickenChienBinh bot = new ChickenChienBinh(
                    (byte) (i + 1),
                    -9999 - i,
                    this.trainingSession.trainingBotX[i],
                    this.trainingSession.trainingBotY[i],
                    this.layTenBossLuyenTap(i),
                    this.trainingSession.trainingBossWeaponPart,
                    this.trainingSession.trainingBossMaxHp,
                    this.trainingSession.trainingBossAttack,
                    0
            );
            bot.hp = this.trainingSession.trainingBotHp[i];
            bot.chet = this.trainingSession.trainingBotDead[i]
                    || bot.hp <= 0;
            mucTieu[i + 1] = bot;
        }

        ChickenQuanLyCongThucSung.KiemTraBanDo banDo =
                this.trainingSession.trainingMap;
        short goc = yeuCau.getGoc();
        byte luc = yeuCau.getLuc();
        short[] dauNong = this.layDiemBanNguoiChoiLuyenTap(goc);
        byte windX = ChickenHeThongGio.layWindXChoItem(
                this.trainingSession.trainingWind,
                vatPham.getIdVatPham());
        byte windY = ChickenHeThongGio.layWindYChoItem(
                this.trainingSession.trainingWind,
                vatPham.getIdVatPham());
        ChickenKetQuaDan ketQua = ChickenPhatBanVatPhamServer.tao(
                nguoiBan,
                dauNong[0],
                dauNong[1],
                goc,
                luc,
                vatPham.getCauHinh(),
                windX,
                windY,
                banDo,
                mucTieu,
                new ChickenPhatBanServer.BoLocMucTieu() {
                    @Override
                    public boolean chapNhan(
                            ChickenChienBinh shooter,
                            ChickenChienBinh target
                    ) {
                        return target != null && !target.chet;
                    }

                    @Override
                    public boolean trungHitbox(
                            ChickenChienBinh target,
                            int danX,
                            int danY
                    ) {
                        return ChickenKichThuocNhanVat.trungNguoiChoi(
                                danX, danY, target.x, target.y);
                    }

                    @Override
                    public int nuaRongHitbox(ChickenChienBinh target) {
                        return ChickenKichThuocNhanVat
                                .NGUOI_CHOI_NUA_RONG;
                    }

                    @Override
                    public int lechTrenHitbox(ChickenChienBinh target) {
                        return ChickenKichThuocNhanVat
                                .NGUOI_CHOI_LECH_TREN;
                    }

                    @Override
                    public int lechDuoiHitbox(ChickenChienBinh target) {
                        return ChickenKichThuocNhanVat
                                .NGUOI_CHOI_LECH_DUOI;
                    }
                }
        );
        if (ketQua == null) {
            this.moHopThoaiOK(
                    "Server chua ho tro vat pham nay trong chien dau.");
            return;
        }

        /*
         * Chi tru kho sau khi packet va ket qua server da hop le. Ghi DB that
         * bai thi khong phat dan va khong khoa luot.
         */
        if (!ChickenHieuUngVatPhamChien.coTheApDung(
                vatPham, ketQua,
                this.trainingSession.trainingMap)) {
            this.moHopThoaiOK(
                    "Khong the tao hieu ung vat pham tren ban do nay.");
            return;
        }
        if (!this.tieuThuMotVatPhamChien(vatPham)) {
            this.moHopThoaiOK(
                    "Vat pham khong con hoac khong the luu kho.");
            return;
        }
        if (!nguoiBan.danhDauDaDungVatPhamChienTrongTran(vatPham)) {
            throw new IllegalStateException(
                    "Vat pham vuot gioi han sau khi da tru kho");
        }
        if (!ChickenHieuUngVatPhamChien.apDung(
                vatPham, ketQua,
                this.trainingSession.trainingMap)) {
            throw new IllegalStateException(
                    "Khong ap dung duoc hieu ung vat pham da tru kho");
        }
        try {
            this.dichVu.guiBaloLuyenTap(nguoiBan);
        } catch (IOException ex) {
            ChickenQuanLyMayChu.log(
                    "[VAT_PHAM_CHIEN][LOI_DONG_BO_BALO] playerId="
                    + this.ma + " itemId=" + vatPham.getIdVatPham()
                    + " mode=training loi="
                    + ex.getClass().getSimpleName());
        }
        int napDanTheoVatPham =
                nguoiBan.layNapDanSauKhiDungVatPham(vatPham);
        this.trainingSession.trainingPendingSelfHitCount = 0;
        this.trainingSession.trainingPendingSelfDamage = 0;
        Arrays.fill(
                this.trainingSession.trainingPendingBotHitCounts, 0);
        Arrays.fill(
                this.trainingSession.trainingPendingBotDamages, 0);
        int satThuongLonNhat = 0;
        Integer tuGayThuong =
                ketQua.satThuongTheoMucTieu.get(nguoiBan);
        if (tuGayThuong != null && tuGayThuong > 0) {
            this.trainingSession.trainingPendingSelfHitCount = 1;
            this.trainingSession.trainingPendingSelfDamage =
                    tuGayThuong;
            satThuongLonNhat = tuGayThuong;
        }
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            Integer satThuong =
                    ketQua.satThuongTheoMucTieu.get(mucTieu[i + 1]);
            if (satThuong == null || satThuong <= 0) {
                continue;
            }
            this.trainingSession.trainingPendingBotHitCounts[i] = 1;
            this.trainingSession.trainingPendingBotDamages[i] =
                    satThuong;
            satThuongLonNhat =
                    Math.max(satThuongLonNhat, satThuong);
        }
        this.trainingSession.trainingPendingDamagePerBullet =
                satThuongLonNhat;
        this.trainingSession.trainingPlayerReloadTime =
                napDanTheoVatPham > 0 ? napDanTheoVatPham : napDan;
        this.trainingSession.trainingLastShotTurnId =
                this.trainingSession.trainingTurnId;
        this.lastTrainingFire = now;
        this.trainingSession.trainingWaitingShotEnd = true;
        final long shotId =
                ++this.trainingSession.trainingActiveShotId;
        this.trainingSession.trainingActiveShotResolved = false;
        final long thoiGianHoatAnh =
                ChickenThoiGianHoatAnhDan.tinh(ketQua);
        this.trainingSession.trainingXacNhanDanSomNhatMs =
                System.currentTimeMillis() + thoiGianHoatAnh;

        this.chuanBiMayManTruocPhatLuyenTap();
        short shooterX = this.trainingSession.trainingPlayerX;
        short shooterY = this.trainingSession.trainingPlayerY;
        int soDuongPhaDiaHinh = Math.min(
                ketQua.cacDuongX.length, ketQua.cacDuongY.length);
        for (int i = 0; i < soDuongPhaDiaHinh; i++) {
            int loaiDanTaoLo =
                    ChickenLoaiDanPhaDiaHinhClient.layLoaiDanTaoLo(
                            ketQua.loaiDan, i);
            if (loaiDanTaoLo
                    == ChickenLoaiDanPhaDiaHinhClient.KHONG_PHA_DIA_HINH) {
                continue;
            }
            this.capNhatLoDiaHinhTheoDuongDanLuyenTap(
                    ketQua.cacDuongX[i], ketQua.cacDuongY[i], null,
                    (byte) loaiDanTaoLo);
        }
        // Dat watchdog truoc khi gui packet: mot ket noi loi khong duoc lam
        // phat nem da tieu hao bi ket va khong bao gio chuyen luot.
        this.scheduleTrainingPlayerResolve(shotId, thoiGianHoatAnh);
        // Client tu xoa itemUsed sau khi gui CMD 22. Dat lai item ngay sat
        // packet ket qua de Hulk/AVG khong nhan nham duong dan vat pham.
        this.dichVu.guiDungVatPhamLuyenTap(
                TRAINING_PLAYER_INDEX,
                (byte) vatPham.getCauHinh().getMaSuDung(),
                vatPham.getIcon());
        this.dichVu.guiKetQuaBanLuyenTap(
                TRAINING_PLAYER_INDEX,
                ketQua.loaiDan,
                shooterX,
                shooterY,
                ketQua.goc,
                ketQua.luc,
                ketQua.lucPhu,
                (byte) 1,
                ketQua.cacDuongX,
                ketQua.cacDuongY,
                ketQua.sieuCao,
                this.phatBanLuyenTapDangDungPow()
        );
        if (ChickenHieuUngVatPhamChien
                .apDungDichChuyenSauKhiGuiPhatBan(
                        nguoiBan, vatPham, ketQua,
                        this.trainingSession.trainingMap)) {
            this.trainingSession.trainingPlayerX = nguoiBan.x;
            this.trainingSession.trainingPlayerY = nguoiBan.y;
            ChickenQuanLyMayChu.log(
                    "[LUYEN_TAP][DICH_CHUYEN] playerId=" + this.ma
                    + " x=" + nguoiBan.x + " y=" + nguoiBan.y);
        }
        nguoiBan.xoaVatPhamChienDangCho();
        ChickenQuanLyMayChu.log(
                "[LUYEN_TAP][VAT_PHAM_CHIEN] playerId=" + this.ma
                + " itemId=" + vatPham.getIdVatPham()
                + " bullet=" + (ketQua.loaiDan & 255)
                + " targets=" + ketQua.satThuongTheoMucTieu.size());
    }

    private void xuLyPhatLaserIronManLuyenTap(
            short goc,
            int tanCong,
            short shooterX,
            short shooterY
    ) throws IOException {
        short[] cacX = new short[TRAINING_BOT_COUNT];
        short[] cacY = new short[TRAINING_BOT_COUNT];
        boolean[] hopLe = new boolean[TRAINING_BOT_COUNT];
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            cacX[i] = this.trainingSession.trainingBotX[i];
            cacY[i] = this.trainingSession.trainingBotY[i];
            hopLe[i] = !this.trainingSession.trainingBotDead[i]
                    && this.trainingSession.trainingBotHp[i] > 0;
        }

        ChickenTiaLaserIronMan.KetQua ketQua =
                ChickenTiaLaserIronMan.tao(
                        shooterX,
                        (short) (shooterY
                                - ChickenTiaLaserIronMan.LECH_NGUC_SO_VOI_CHAN),
                        goc,
                        this.trainingSession.trainingMap.getWidth(),
                        this.trainingSession.trainingMap.getHeight(),
                        cacX,
                        cacY,
                        hopLe
                );

        this.trainingSession.trainingPendingSelfHitCount = 0;
        this.trainingSession.trainingPendingSelfDamage = 0;
        Arrays.fill(this.trainingSession.trainingPendingBotHitCounts, 0);
        Arrays.fill(this.trainingSession.trainingPendingBotDamages, 0);
        int mucTieu = ketQua.getChiSoMucTieu();
        int satThuong = ChickenTiaLaserIronMan.tinhSatThuongNhuHawk(
                tanCong, 0);
        if (mucTieu >= 0 && mucTieu < TRAINING_BOT_COUNT
                && hopLe[mucTieu]) {
            this.trainingSession.trainingPendingBotHitCounts[mucTieu] = 1;
            this.trainingSession.trainingPendingBotDamages[mucTieu] =
                    satThuong;
        }
        this.trainingSession.trainingPendingDamagePerBullet = satThuong;
        this.trainingSession.trainingWaitingShotEnd = true;
        long shotId = ++this.trainingSession.trainingActiveShotId;
        this.trainingSession.trainingActiveShotResolved = false;
        this.trainingSession.trainingIronManLaserSanSang = false;
        this.chuanBiMayManTruocPhatLuyenTap();

        this.dichVu.guiTiaLaserIronManLuyenTap(
                TRAINING_PLAYER_INDEX,
                shooterX,
                shooterY,
                goc,
                ketQua.getBatDauX(),
                ketQua.getBatDauY(),
                ketQua.getKetThucX(),
                ketQua.getKetThucY()
        );
        this.scheduleTrainingPlayerResolve(shotId, 300L);
        System.out.println("[IRON_MAN] BAN_LASER mode=training goc=" + goc
                + " target=" + mucTieu
                + " damage=" + (mucTieu >= 0 ? satThuong : 0)
                + " boQuaDiaHinh=true");
    }

    /** Chot vi tri Hulk tu duong dan do server tao, khong dung X/Y packet client. */
    private boolean apDungViTriHulkSauPhatLuyenTap(
            short[][] cacDuongX,
            short[][] cacDuongY
    ) {
        if (!ChickenCoCheHulk.laHulk(this.avenger)
                || cacDuongX == null || cacDuongY == null
                || cacDuongX.length == 0 || cacDuongY.length == 0) {
            return false;
        }
        short[] xs = cacDuongX[0];
        short[] ys = cacDuongY[0];
        int soDiem = Math.min(xs == null ? 0 : xs.length, ys == null ? 0 : ys.length);
        if (soDiem <= 0) {
            return false;
        }
        this.trainingSession.trainingPlayerX = xs[soDiem - 1];
        this.trainingSession.trainingPlayerY = ys[soDiem - 1];
        return ChickenCoCheHulk.daRaKhoiMap(
                xs,
                ys,
                this.trainingSession.trainingMap.getWidth(),
                this.trainingSession.trainingMap.getHeight()
        );
    }

    /**
     * Khóa lại tọa độ đang bay sau packet phát bắn. CMD 22/84 của client đổi
     * trạng thái AVG sang animation bắn; packet CMD 53 ngay sau đó chỉ đặt lại
     * X/Y hiện tại, không đổi lượt và không tìm mặt đất.
     */
    private void dongBoToaDoBaySauKhiBanLuyenTap() throws IOException {
        if (!this.inTraining
                || !this.isFlyAvenger()
                || !ChickenCoCheBayAVG.canDongBoToaDoSauKhiBan(this.avenger)) {
            return;
        }
        this.dichVu.guiCapNhatXYLuyenTap(
                TRAINING_PLAYER_INDEX,
                this.trainingSession.trainingPlayerX,
                this.trainingSession.trainingPlayerY
        );
    }

    private void xuLyPhatBanX3UltronLuyenTap(
            byte loaiDan,
            short goc,
            byte luc,
            short muzzleX,
            short muzzleY,
            int satThuong,
            short shooterX,
            short shooterY
    ) throws IOException {
        final int soPhatKyNang = 3;
        int soVienTuTrung = 0;
        int[] soVienTrungBot = new int[TRAINING_BOT_COUNT];
        int[] satThuongBot = new int[TRAINING_BOT_COUNT];
        int satThuongTuThan = 0;
        int satThuongMoiVien = Math.max(1, satThuong);
        short[][] cacDuongX = new short[soPhatKyNang][];
        short[][] cacDuongY = new short[soPhatKyNang][];
        int doDaiTia = 1;
        short diemCuoiX = muzzleX;
        short diemCuoiY = muzzleY;

        for (int phat = 0; phat < soPhatKyNang; phat++) {
            ChickenCongThucBanUltron.DuongTia tiaDayDu =
                    ChickenCongThucBanUltron.taoTiaThang(
                            muzzleX,
                            muzzleY,
                            goc,
                            this.trainingSession.trainingMap.getWidth(),
                            this.trainingSession.trainingMap.getHeight()
                    );
            short[][] duongDan = this.catDuongDanUltronTaiVaChamBanDoLuyenTap(
                    tiaDayDu.getX(), tiaDayDu.getY());
            TrainingCharacterHit vaChamNhanVat =
                    this.timVaChamNhanVatTrenDuongDanLuyenTap(
                            duongDan[0],
                            duongDan[1],
                            muzzleX,
                            muzzleY,
                            true,
                            true
                    );
            if (vaChamNhanVat != null) {
                duongDan = this.catDuongDanTaiVaChamNhanVatLuyenTap(
                        duongDan[0], duongDan[1], vaChamNhanVat);
                if (vaChamNhanVat.laNguoiChoi()) {
                    soVienTuTrung++;
                    satThuongTuThan = this.congSoNguyenAnToan(
                            satThuongTuThan, satThuongMoiVien);
                } else if (vaChamNhanVat.botIndex >= 0
                        && vaChamNhanVat.botIndex < TRAINING_BOT_COUNT) {
                    soVienTrungBot[vaChamNhanVat.botIndex]++;
                    satThuongBot[vaChamNhanVat.botIndex] =
                            this.congSoNguyenAnToan(
                                    satThuongBot[vaChamNhanVat.botIndex],
                                    satThuongMoiVien
                            );
                }
            } else {
                /* Vien sau thay dung lo ma vien truoc vua tao, giong loat coi. */
                this.capNhatLoDiaHinhTheoDuongDanLuyenTap(
                        duongDan[0], duongDan[1], null, loaiDan);
            }

            cacDuongX[phat] = duongDan[0];
            cacDuongY[phat] = duongDan[1];
            int soDiem = Math.min(duongDan[0].length, duongDan[1].length);
            doDaiTia = Math.max(doDaiTia, soDiem);
            diemCuoiX = this.layGiaTriCuoiDuongDan(duongDan[0], muzzleX);
            diemCuoiY = this.layGiaTriCuoiDuongDan(duongDan[1], muzzleY);
        }

        this.trainingSession.trainingPendingSelfHitCount = soVienTuTrung;
        this.trainingSession.trainingPendingSelfDamage = satThuongTuThan;
        System.arraycopy(
                soVienTrungBot,
                0,
                this.trainingSession.trainingPendingBotHitCounts,
                0,
                TRAINING_BOT_COUNT
        );
        System.arraycopy(
                satThuongBot,
                0,
                this.trainingSession.trainingPendingBotDamages,
                0,
                TRAINING_BOT_COUNT
        );
        this.trainingSession.trainingPendingDamagePerBullet = satThuongMoiVien;
        this.trainingSession.trainingWaitingShotEnd = true;
        final long shotId = ++this.trainingSession.trainingActiveShotId;
        this.trainingSession.trainingActiveShotResolved = false;
        // Skill la ba LAN BAN hoan chinh, khong phai mot lan ban co ba vien.
        // CMD 79 cua moi lan se mo khoa packet ban tiep theo.
        this.trainingSession.trainingUltronX3ShotId = shotId;
        this.trainingSession.trainingUltronX3VaChamDaNhan = 0;
        this.trainingSession.trainingUltronX3PhatDaGui = 1;
        this.trainingSession.trainingUltronX3LoaiDan = loaiDan;
        this.trainingSession.trainingUltronX3Goc = goc;
        this.trainingSession.trainingUltronX3Luc = luc;
        this.trainingSession.trainingUltronX3ShooterX = shooterX;
        this.trainingSession.trainingUltronX3ShooterY = shooterY;
        this.trainingSession.trainingUltronX3DuongX = cacDuongX;
        this.trainingSession.trainingUltronX3DuongY = cacDuongY;
        this.chuanBiMayManTruocPhatLuyenTap();

        this.huyLoatMgDangCho();
        this.trainingSession.trainingMgBurstEndAt = 0L;
        /* Moi packet la mot lan ban native: soPhat=1 va NBULL=1. */
        this.dichVu.guiKetQuaBanLuyenTap(
                TRAINING_PLAYER_INDEX,
                loaiDan,
                shooterX,
                shooterY,
                goc,
                luc,
                luc,
                (byte) 1,
                new short[][]{cacDuongX[0]},
                new short[][]{cacDuongY[0]},
                false,
                this.phatBanLuyenTapDangDungPow()
        );
        this.trainingSession.trainingXacNhanDanSomNhatMs =
                System.currentTimeMillis()
                + ChickenThoiGianHoatAnhDan.tinh(
                        new short[][]{cacDuongX[0]},
                        new short[][]{cacDuongY[0]});
        this.dongBoToaDoBaySauKhiBanLuyenTap();
        this.trainingSession.trainingUltronDangBanX3 = false;
        System.out.println("[ULTRON] BAN_X3 mode=training goc=" + goc
                + " impactX=" + diemCuoiX
                + " impactY=" + diemCuoiY
                + " soVien=" + soPhatKyNang + " quyDaoDocLap=true");

        long thoiGianKetToan = Math.max(
                5000L,
                Math.min(8000L, doDaiTia * 80L * soPhatKyNang + 1000L)
        );
        this.scheduleTrainingPlayerResolve(
                shotId,
                thoiGianKetToan
        );
    }

    private short layGiaTriCuoiDuongDan(short[] duong, short macDinh) {
        return duong == null || duong.length == 0 ? macDinh : duong[duong.length - 1];
    }

    /**
     * Thêm các bước đứng yên ở đầu quỹ đạo để các viên trong cùng packet
     * xuất hiện nối đuôi nhau. Mỗi viên vẫn giữ mảng quỹ đạo/object riêng.
     */
    private short[] themBuocChoDauDuongDan(short[] duongDan, int soBuocCho, short toaDoDau) {
        if (duongDan == null || duongDan.length == 0 || soBuocCho <= 0) {
            return duongDan;
        }
        short[] ketQua = new short[duongDan.length + soBuocCho];
        for (int i = 0; i < soBuocCho; i++) {
            ketQua[i] = toaDoDau;
        }
        System.arraycopy(duongDan, 0, ketQua, soBuocCho, duongDan.length);
        return ketQua;
    }

    private ChickenDuLieuPhatBanLuyenTap docPhatBanLuyenTap(
            ChickenTinNhan ms, byte loaiDanTheoSung) {
        return ChickenXuLyBanLuyenTap.docPhatBan(
                ms,
                loaiDanTheoSung,
                this.laLoaiDanLuyenTapHopLe(loaiDanTheoSung),
                this.isDoubleTrainingBullet(loaiDanTheoSung)
        );
    }

    /**
     * Kiểm tra lại hoàn toàn ở server trước mỗi viên MG42. Nếu người chơi đã
     * thoát, đổi phiên, đổi súng hoặc loạt đã đủ năm viên thì task còn lại tự dừng.
     */
    private synchronized boolean conHieuLucLoatMgLuyenTap(
            long phien, long shotId, int idSungMayChu) {
        if (!this.inTraining
                || phien != this.trainingSession.trainingSessionId
                || shotId != this.trainingSession.trainingActiveShotId
                || this.trainingSession.trainingActiveShotResolved
                || !this.trainingSession.trainingWaitingShotEnd
                || this.trainingSession.trainingMgBurstShotId != shotId
                || this.trainingSession.trainingMgBurstGunId != idSungMayChu
                || this.trainingSession.trainingMgBurstSent
                        >= this.trainingSession.trainingMgBurstTotal) {
            return false;
        }

        ChickenVatPham sungHienTai = this.laySungDangTrangBiHopLe();
        DuLieuSung duLieuHienTai = ChickenQuanLyDanSung.theoSungDangTrangBi(sungHienTai);
        return duLieuHienTai != null && duLieuHienTai.getIdSung() == idSungMayChu;
    }

    /** Gửi đúng một viên của loạt MG42 và tăng bộ đếm sau khi gửi thành công. */
    private synchronized void guiMotVienMgLuyenTap(
            long phien,
            long shotId,
            int idSungMayChu,
            int chiSoVien,
            byte loaiDan,
            short goc,
            byte luc,
            short[][] cacDuongX,
            short[][] cacDuongY
    ) throws IOException {
        if (!this.conHieuLucLoatMgLuyenTap(phien, shotId, idSungMayChu)) {
            return;
        }
        if (chiSoVien != this.trainingSession.trainingMgBurstSent
                || chiSoVien < 0
                || chiSoVien >= this.trainingSession.trainingMgBurstTotal
                || chiSoVien >= cacDuongX.length
                || chiSoVien >= cacDuongY.length) {
            return;
        }

        short[][] motDuongX = new short[][]{cacDuongX[chiSoVien]};
        short[][] motDuongY = new short[][]{cacDuongY[chiSoVien]};
        this.dichVu.guiKetQuaBanLuyenTap(
                TRAINING_PLAYER_INDEX,
                loaiDan,
                this.trainingSession.trainingPlayerX,
                this.trainingSession.trainingPlayerY,
                goc,
                luc,
                luc,
                (byte) 1,
                motDuongX,
                motDuongY,
                false,
                this.phatBanLuyenTapDangDungPow()
        );

        this.trainingSession.trainingMgBurstSent++;
        if (this.trainingSession.trainingMgBurstSent
                >= this.trainingSession.trainingMgBurstTotal) {
            // Đủ đúng 5 viên: đóng loạt ngay tại server. Không để task cũ hoặc
            // tín hiệu va chạm của từng packet khởi động tiếp một viên mới.
            this.trainingSession.trainingMgBurstSent
                    = this.trainingSession.trainingMgBurstTotal;
            ChickenLuyenTapBan.huyTasks(this.trainingSession.trainingMgBurstTasks);
            this.trainingSession.trainingMgBurstEndAt = System.currentTimeMillis() + 30L;

            // Kết thúc lượt theo bộ đếm server, không chờ client báo va chạm
            // của từng viên. Hàm resolve có kiểm tra shotId nên chỉ chạy một lần.
            this.scheduleTrainingPlayerResolve(shotId, 40L);
        }
    }

    private int layDoLechGocDanLuyenTap(
            byte nhomSung,
            int soVienMoiLoat,
            int chiSoVien
    ) {
        return ChickenXuLyBanLuyenTap.layDoLechGoc(
                nhomSung,
                soVienMoiLoat,
                chiSoVien
        );
    }

    public void xuLyVaChamLuyenTap(ChickenTinNhan ms) throws IOException {
        if (!ChickenXacNhanKetThucDan.docVaBoQua(ms)) {
            return;
        }
        if (!this.inTraining || !this.trainingSession.trainingWaitingShotEnd) {
            return;
        }
        if (this.trainingSession.trainingUltronX3ShotId
                == this.trainingSession.trainingActiveShotId) {
            long conLaiMs = this.trainingSession.trainingXacNhanDanSomNhatMs
                    - System.currentTimeMillis();
            if (conLaiMs > 0L) {
                this.henXuLyXacNhanSomUltronLuyenTap(conLaiMs);
                return;
            }
            this.xuLyXacNhanUltronX3LuyenTap();
            return;
        }
        if (System.currentTimeMillis()
                < this.trainingSession.trainingXacNhanDanSomNhatMs) {
            return;
        }
        // Client có thể báo kết thúc sau từng packet. Chỉ cho kết toán khi
        // server đã gửi đủ số viên của loạt MG42 và qua thời điểm viên cuối.
        if (this.trainingSession.trainingMgBurstShotId
                    == this.trainingSession.trainingActiveShotId
                && this.trainingSession.trainingMgBurstSent
                    < this.trainingSession.trainingMgBurstTotal) {
            return;
        }
        if (this.trainingSession.trainingMgBurstEndAt > System.currentTimeMillis()) {
            return;
        }
        this.xuLyPhatBanNguoiChoiLuyenTap(this.trainingSession.trainingActiveShotId);
    }

    private void henXuLyXacNhanSomUltronLuyenTap(long conLaiMs) {
        if (this.trainingSession.trainingUltronX3XacNhanTask != null) {
            return;
        }
        final long phien = this.trainingSession.trainingSessionId;
        final long shotId = this.trainingSession.trainingActiveShotId;
        final int phatDaGui = this.trainingSession.trainingUltronX3PhatDaGui;
        this.trainingSession.trainingUltronX3XacNhanTask =
                TRAINING_BOT_EXECUTOR.schedule(() -> {
                    synchronized (ChickenNguoiChoi.this) {
                        ChickenNguoiChoi.this.trainingSession
                                .trainingUltronX3XacNhanTask = null;
                        if (!ChickenNguoiChoi.this.inTraining
                                || ChickenNguoiChoi.this.trainingSession
                                        .trainingSessionId != phien
                                || ChickenNguoiChoi.this.trainingSession
                                        .trainingActiveShotId != shotId
                                || ChickenNguoiChoi.this.trainingSession
                                        .trainingUltronX3PhatDaGui
                                        != phatDaGui) {
                            return;
                        }
                        try {
                            ChickenNguoiChoi.this
                                    .xuLyXacNhanUltronX3LuyenTap();
                        } catch (IOException ex) {
                            Logger.getLogger(ChickenNguoiChoi.class.getName())
                                    .log(Level.SEVERE, null, ex);
                        }
                    }
                }, Math.max(1L, conLaiMs), TimeUnit.MILLISECONDS);
    }

    private void xuLyXacNhanUltronX3LuyenTap() throws IOException {
        this.trainingSession.trainingUltronX3VaChamDaNhan++;
        if (this.trainingSession.trainingUltronX3PhatDaGui < 3) {
            this.guiVienUltronX3TiepTheoLuyenTap();
            return;
        }
        this.xuLyPhatBanNguoiChoiLuyenTap(
                this.trainingSession.trainingActiveShotId);
    }

    private synchronized void scheduleTrainingPlayerResolve(long shotId, long delayMs) {
        if (this.trainingSession.trainingPlayerResolveTask != null) {
            this.trainingSession.trainingPlayerResolveTask.cancel(false);
        }
        final long phien = this.trainingSession.trainingSessionId;
        this.trainingSession.trainingPlayerResolveTask = TRAINING_BOT_EXECUTOR.schedule(() -> {
            try {
                if (!this.inTraining || phien != this.trainingSession.trainingSessionId) {
                    return;
                }
                this.xuLyPhatBanNguoiChoiLuyenTap(shotId);
            }
            catch (Exception ex) {
                Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    private synchronized void xuLyPhatBanNguoiChoiLuyenTap(long shotId) throws IOException {
        if (!this.inTraining || !this.trainingSession.trainingWaitingShotEnd
                || shotId != this.trainingSession.trainingActiveShotId
                || this.trainingSession.trainingActiveShotResolved) {
            return;
        }
        this.trainingSession.trainingActiveShotResolved = true;
        this.trainingSession.trainingXacNhanDanSomNhatMs = 0L;
        this.trainingSession.trainingMgBurstEndAt = 0L;
        if (this.trainingSession.trainingPlayerResolveTask != null) {
            this.trainingSession.trainingPlayerResolveTask.cancel(false);
            this.trainingSession.trainingPlayerResolveTask = null;
        }
        // Packet ban phai giu toa do luc keo luc de animation xuat phat dung
        // dau nong. Chi sau khi vien dan/no ket thuc moi chot Y moi xuong day ho.
        this.guiDongBoToaDoSauPhaDiaHinhLuyenTap();
        ChickenChienBinh[] mucTieuMayMan =
                this.trainingSession.trainingPendingMayManTargets;
        ChickenMayMan.PhienTanCong phienMayMan =
                this.trainingSession.trainingPendingMayMan;
        this.trainingSession.trainingPendingMayManTargets = null;
        this.trainingSession.trainingPendingMayMan = null;
        if (mucTieuMayMan == null
                || mucTieuMayMan.length < TRAINING_BOT_COUNT + 1
                || phienMayMan == null) {
            mucTieuMayMan = this.taoDanhSachMayManLuyenTap();
            phienMayMan = null;
            ChickenQuanLyMayChu.log(
                    "[LUYEN_TAP][THIEU_PHIEN_MAY_MAN] playerId=" + this.ma
                    + " shotId=" + shotId
                    + " giuDamageGoc=true");
        }
        ChickenChienBinh nguoiBanMayMan = mucTieuMayMan[0];
        if (this.trainingSession.trainingPendingSelfDamage > 0) {
            int satThuong = this.trainingSession.trainingPendingSelfDamage;
            if (phienMayMan != null) {
                satThuong = phienMayMan.apDung(
                        nguoiBanMayMan, satThuong);
            }
            int hpTruoc = this.trainingSession.trainingPlayerHp;
            this.trainingSession.trainingPlayerHp = Math.max(0, this.trainingSession.trainingPlayerHp - satThuong);
            this.ghiNhanSatThuongPowLuyenTap(hpTruoc);
            this.dichVu.guiCapNhatMauLuyenTap(
                    TRAINING_PLAYER_INDEX,
                    this.trainingSession.trainingPlayerHp,
                    this.trainingSession.trainingPlayerMaxHp,
                    this.trainingSession.trainingPlayerHp == 0 ? (byte) 2 : (byte) 0
            );
            if (this.trainingSession.trainingPlayerHp == 0) {
                this.xoaTrangThaiPhatBanNguoiChoi();
                this.xuLyNguoiChoiThuaLuyenTap();
                return;
            }
        }
        for (int botIndex = 0; botIndex < TRAINING_BOT_COUNT; botIndex++) {
            int satThuong = this.trainingSession.trainingPendingBotDamages[botIndex];
            if (satThuong <= 0 || this.trainingSession.trainingBotDead[botIndex]) {
                continue;
            }
            if (phienMayMan != null) {
                satThuong = phienMayMan.apDung(
                        mucTieuMayMan[botIndex + 1], satThuong);
            }
            if (this.trainingSession.trainingBossShield) {
                satThuong = (int) Math.max(1L, (long) satThuong * 40L / 100L);
                this.trainingSession.trainingBossShield = false;
            }
            this.trainingSession.trainingBotHp[botIndex] = Math.max(0, this.trainingSession.trainingBotHp[botIndex] - satThuong);
            this.trainingSession.trainingDummyHp = this.trainingSession.trainingBotHp[botIndex];
            if (this.trainingSession.trainingBotHp[botIndex] == 0) {
                this.trainingSession.trainingBotDead[botIndex] = true;
                this.trainingSession.trainingBossState = TrainingBossState.DEAD;
            }
            this.dichVu.guiCapNhatMauLuyenTap(
                    (byte) (botIndex + 1),
                    this.trainingSession.trainingBotHp[botIndex],
                    this.trainingSession.trainingBossMaxHp,
                    this.trainingSession.trainingBotDead[botIndex] ? (byte) 2 : (byte) 0
            );
            if (this.trainingSession.trainingBotDead[botIndex]) {
                this.xoaTrangThaiPhatBanNguoiChoi();
                this.xuLyNguoiChoiThangLuyenTap();
                return;
            }
        }
        this.dongBoToaDoBaySauKhiBanLuyenTap();
        this.xoaTrangThaiPhatBanNguoiChoi();
        this.chuyenLuotTheoNapDan(TRAINING_PLAYER_INDEX);
    }

    /**
     * Chốt lần quay và phát sao trước packet đạn. Giữ nguyên đúng các object
     * mục tiêu để lần kết toán sau animation không quay lại lần thứ hai.
     */
    private void chuanBiMayManTruocPhatLuyenTap() {
        ChickenChienBinh[] danhSach = this.taoDanhSachMayManLuyenTap();
        this.trainingSession.trainingPendingMayManTargets = danhSach;
        this.trainingSession.trainingPendingMayMan =
                ChickenMayMan.batDau(danhSach[0], danhSach);
    }

    /**
     * Cờ hình ảnh POW phải lấy từ phiên tấn công đã được server chốt, không
     * đọc lại ý định từ packet client. Cờ này chỉ điều khiển hiệu ứng đạn đỏ;
     * sát thương vẫn do ChickenMayMan.PhienTanCong tính ở server.
     */
    private boolean phatBanLuyenTapDangDungPow() {
        ChickenMayMan.PhienTanCong phien =
                this.trainingSession.trainingPendingMayMan;
        return phien != null && phien.powDaKichHoat();
    }

    private ChickenChienBinh[] taoDanhSachMayManLuyenTap() {
        ChickenChienBinh[] danhSach =
                new ChickenChienBinh[TRAINING_BOT_COUNT + 1];
        ChickenChienBinh nguoiChoiMayMan = new ChickenChienBinh(
                this,
                TRAINING_PLAYER_INDEX,
                this.trainingSession.trainingPlayerX,
                this.trainingSession.trainingPlayerY
        );
        nguoiChoiMayMan.hp = this.trainingSession.trainingPlayerHp;
        nguoiChoiMayMan.mauToiDa = this.trainingSession.trainingPlayerMaxHp;
        danhSach[0] = nguoiChoiMayMan;
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            ChickenChienBinh bot = new ChickenChienBinh(
                    (byte) (i + 1),
                    -9999 - i,
                    this.trainingSession.trainingBotX[i],
                    this.trainingSession.trainingBotY[i],
                    this.layTenBossLuyenTap(i),
                    this.trainingSession.trainingBossWeaponPart,
                    this.trainingSession.trainingBossMaxHp,
                    this.trainingSession.trainingBossAttack,
                    0
            );
            bot.hp = this.trainingSession.trainingBotHp[i];
            bot.chet = this.trainingSession.trainingBotDead[i];
            danhSach[i + 1] = bot;
        }
        return danhSach;
    }

    private void huyLoatMgDangCho() {
        ChickenLuyenTapBan.huyTasks(this.trainingSession.trainingMgBurstTasks);
        this.trainingSession.trainingMgBurstShotId = -1L;
        this.trainingSession.trainingMgBurstGunId = -1;
        this.trainingSession.trainingMgBurstTotal = 0;
        this.trainingSession.trainingMgBurstSent = 0;
    }

    private void xoaTrangThaiPhatBanNguoiChoi() {
        this.huyLoatMgDangCho();
        if (this.trainingSession.trainingUltronX3XacNhanTask != null) {
            this.trainingSession.trainingUltronX3XacNhanTask.cancel(false);
            this.trainingSession.trainingUltronX3XacNhanTask = null;
        }
        this.trainingSession.trainingMgBurstEndAt = 0L;
        this.trainingSession.trainingWaitingShotEnd = false;
        this.trainingSession.trainingPendingMayManTargets = null;
        this.trainingSession.trainingPendingMayMan = null;
        this.trainingSession.trainingPendingSelfHitCount = 0;
        this.trainingSession.trainingPendingSelfDamage = 0;
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            this.trainingSession.trainingPendingBotHitCounts[i] = 0;
            this.trainingSession.trainingPendingBotDamages[i] = 0;
        }
        this.trainingSession.trainingPendingDamagePerBullet = 0;
        this.trainingSession.trainingActiveShotResolved = true;
        this.trainingSession.trainingXacNhanDanSomNhatMs = 0L;
        this.trainingSession.trainingUltronX3ShotId = -1L;
        this.trainingSession.trainingUltronX3VaChamDaNhan = 0;
        this.trainingSession.trainingUltronX3PhatDaGui = 0;
        this.trainingSession.trainingUltronX3ShooterX = 0;
        this.trainingSession.trainingUltronX3ShooterY = 0;
        this.trainingSession.trainingUltronX3DuongX = null;
        this.trainingSession.trainingUltronX3DuongY = null;
    }

    /** Gửi viên 2/3 của Ultron sau khi client đã kết thúc viên ngay trước đó. */
    private void guiVienUltronX3TiepTheoLuyenTap() throws IOException {
        int phatThu = this.trainingSession.trainingUltronX3PhatDaGui + 1;
        short[][] cacDuongX = this.trainingSession.trainingUltronX3DuongX;
        short[][] cacDuongY = this.trainingSession.trainingUltronX3DuongY;
        int chiSo = phatThu - 1;
        if (cacDuongX == null || cacDuongY == null
                || chiSo < 0
                || chiSo >= cacDuongX.length
                || chiSo >= cacDuongY.length) {
            return;
        }
        this.dichVu.guiKetQuaBanLuyenTap(
                TRAINING_PLAYER_INDEX,
                this.trainingSession.trainingUltronX3LoaiDan,
                this.trainingSession.trainingUltronX3ShooterX,
                this.trainingSession.trainingUltronX3ShooterY,
                this.trainingSession.trainingUltronX3Goc,
                this.trainingSession.trainingUltronX3Luc,
                this.trainingSession.trainingUltronX3Luc,
                (byte) 1,
                new short[][]{cacDuongX[chiSo]},
                new short[][]{cacDuongY[chiSo]},
                false,
                this.phatBanLuyenTapDangDungPow()
        );
        this.trainingSession.trainingXacNhanDanSomNhatMs =
                System.currentTimeMillis()
                + ChickenThoiGianHoatAnhDan.tinh(
                        new short[][]{cacDuongX[chiSo]},
                        new short[][]{cacDuongY[chiSo]});
        this.trainingSession.trainingUltronX3PhatDaGui = phatThu;
        System.out.println("[ULTRON] GUI_X3_VIEN=" + phatThu
                + "/3 shotId=" + this.trainingSession.trainingActiveShotId);
    }

    public void handleTrainingHoleRequest(ChickenTinNhan ms) throws IOException {
        if (!this.inTraining || ms == null) {
            return;
        }
        DataInputStream ds = ms.boDoc();
        if (ds.available() <= 0) {
            return;
        }
        int soLo = Math.min(255, ds.readUnsignedByte());
        for (int i = 0; i < soLo; i++) {
            if (ds.available() < 7) {
                break;
            }
            ds.readShort(); // Chỉ số mảnh map; tọa độ phía sau là tọa độ toàn map.
            short xNo = ds.readShort();
            short yNo = ds.readShort();
            byte loaiDan = ds.readByte();
            if (xNo < 0 || yNo < 0 || xNo >= this.trainingSession.trainingMap.getWidth()
                    || yNo >= this.trainingSession.trainingMap.getHeight()) {
                continue;
            }
            // Server đã tự tính và phá địa hình theo quỹ đạo. Dữ liệu từ client
            // chỉ được đọc bỏ để tránh client phá map hoặc tạo va chạm giả.
        }
    }

    public void handleTrainingClientReady() throws IOException {
        this.dichVu.guiHienManHinhGameLuyenTap();
        if (this.inTraining && !this.trainingSession.trainingFirstTurnSent) {
            this.trainingSession.trainingFirstTurnSent = true;
            this.guiTrangThaiLuotLuyenTap();
            this.lapLichHetGioLuotNguoiChoiLuyenTap();
        }
    }

    public void datSoTranThangLuyenTap(int soTranThang) {
        this.trainingSession.trainingWins = Math.max(0, soTranThang);
        this.trainingSession.trainingBossMaxHp = this.tinhMauToiDaBoss();
    }

    public int laySoTranThangLuyenTap() {
        return this.trainingSession.trainingWins;
    }

    private String layTenBossLuyenTap(int botIndex) {
        return "Chicken" + Math.max(0, this.trainingSession.trainingWins + botIndex);
    }

    private int tinhMauToiDaNguoiChoiLuyenTap() {
        int tongMau = this.layTongMauHienTai();
        if (tongMau <= 0) {
            tongMau = TRAINING_PLAYER_MAX_HP;
        }
        return Math.max(1, Math.min(TRAINING_MAX_PACKET_HP, tongMau));
    }

    private int tinhMauToiDaBoss() {
        long mau = (long)TRAINING_BOSS_BASE_HP + (long)Math.max(0, this.trainingSession.trainingWins) * TRAINING_BOSS_HP_STEP;
        return (int)Math.max(TRAINING_BOSS_BASE_HP, Math.min(TRAINING_MAX_PACKET_HP, mau));
    }

    public short layMaHinhSungDangTrangBi() {
        ChickenVatPham sung = this.laySungDangTrangBiHopLe();
        return sung == null || sung.mau == null ? (short)-1 : sung.mau.part;
    }

    private ChickenVatPham laySungDangTrangBiHopLe() {
        if (this.inTraining && laSungThuongDuocPhepTrongBalo(
                this.sungDangCamLuyenTap)) {
            return this.sungDangCamLuyenTap;
        }
        return this.laySungTrangBiMayChu();
    }

    /** Sung that dang mac; khong bao gio doc ID sung tu packet client. */
    public ChickenVatPham laySungTrangBiMayChu() {
        if (this.itemBody == null || this.itemBody.length <= 5) {
            return null;
        }
        ChickenVatPham sung = this.itemBody[5];
        if (!this.trangBiHopLe(sung, 5)
                || sung.mau.loai != 5
                || !this.datCapSuDungVatPham(sung)) {
            return null;
        }
        return sung;
    }

    private void khoiTaoSungLuyenTap(ChickenVatPham sungTrangBi) {
        this.avenger = ChickenCoCheBayAVG.layAvengerTuTrangBi(this);
        this.sungDangCamLuyenTap = sungTrangBi;
        this.sungTheoOTrongBaloLuyenTap.clear();
        if (!laSungThuongDuocPhepTrongBalo(sungTrangBi)
                || this.itemBalo == null || this.itemBag == null) {
            return;
        }
        // Client gui CMD 26 bang vi tri hien thi 0..4. Khong khoa snapshot
        // bang index that cua itemBag, vi hai gia tri nay thuong khac nhau.
        for (int viTriBalo = 0;
                viTriBalo < this.itemBalo.length;
                viTriBalo++) {
            int chiSoTui = this.itemBalo[viTriBalo];
            if (chiSoTui < 0 || chiSoTui >= this.itemBag.length) {
                continue;
            }
            ChickenVatPham sung = this.itemBag[chiSoTui];
            if (laSungThuongDuocPhepTrongBalo(sung)
                    && this.datCapSuDungVatPham(sung)) {
                this.sungTheoOTrongBaloLuyenTap.put(viTriBalo, sung);
            }
        }
    }

    /** CMD 26 mot byte trong man dau la y dinh doi sung, khong phai dung item. */
    public synchronized void doiSungTrongTran(int viTriBalo)
            throws IOException {
        ChickenQuanLyChien tran =
                ChickenQuanLyChien.timTranDauCuaNguoiChoi(this);
        if (tran != null) {
            tran.doiSungTrongTran(this, viTriBalo);
            return;
        }
        this.doiSungLuyenTap(viTriBalo);
    }

    private boolean doiSungLuyenTap(int viTriBalo) throws IOException {
        if (!this.inTraining
                || this.trainingSession.trainingCurrentTurn
                    != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingWaitingShotEnd
                || this.trainingSession.trainingBotAnimating
                || this.trainingSession.trainingBossState
                    != TrainingBossState.IDLE
                || this.trainingSession.trainingHawkSkillActive
                || this.trainingSession.trainingThorSkillActive
                || this.trainingSession.trainingLokiSkillActive
                || this.trainingSession.trainingUltronDangBanX3
                || this.trainingSession.trainingIronManLaserSanSang) {
            return false;
        }
        ChickenChienBinh chienBinhVatPham =
                this.trainingSession.trainingVatPhamChienBinh;
        ChickenChienBinh.VatPhamChienTrongTran vatPham =
                chienBinhVatPham == null
                        ? null
                        : chienBinhVatPham
                                .layVatPhamChienTrongOTrongBalo(viTriBalo);
        if (vatPham != null) {
            if (vatPham.getIdVatPham()
                    == ChickenCuuThuongBanThan.ID_VAT_PHAM) {
                return this.dungCuuThuongBanThanLuyenTap(
                        chienBinhVatPham, vatPham, viTriBalo);
            }
            if (vatPham.getIdVatPham()
                    == ChickenCuuThuongDongDoi.ID_VAT_PHAM) {
                return this.dungCuuThuongLuyenTap(
                        chienBinhVatPham, vatPham, viTriBalo);
            }
            if (vatPham.getIdVatPham()
                    == ChickenDiChuyenX2.ID_VAT_PHAM) {
                return this.dungDiChuyenX2LuyenTap(
                        chienBinhVatPham, vatPham, viTriBalo);
            }
            if (vatPham.getIdVatPham()
                    == ChickenNgungGio.ID_VAT_PHAM) {
                return this.dungNgungGioLuyenTap(
                        chienBinhVatPham, vatPham, viTriBalo);
            }
            if (!this.coVatPhamChienSanSang(vatPham)
                    || !chienBinhVatPham
                            .chonVatPhamChienTrongTran(viTriBalo)) {
                return false;
            }
            this.dichVu.guiDungVatPhamLuyenTap(
                    TRAINING_PLAYER_INDEX,
                    (byte) vatPham.getCauHinh().getMaSuDung(),
                    vatPham.getIcon());
            return true;
        }
        if (this.avenger != 0
                || !laSungThuongDuocPhepTrongBalo(
                        this.sungDangCamLuyenTap)) {
            return false;
        }
        ChickenVatPham sungMoi =
                this.sungTheoOTrongBaloLuyenTap.get(viTriBalo);
        if (!laSungThuongDuocPhepTrongBalo(sungMoi)) {
            return false;
        }
        ChickenVatPham sungCu = this.sungDangCamLuyenTap;
        this.sungTheoOTrongBaloLuyenTap.put(viTriBalo, sungCu);
        this.sungDangCamLuyenTap = sungMoi;
        if (chienBinhVatPham != null) {
            chienBinhVatPham.xoaVatPhamChienDangCho();
        }
        this.trainingSession.trainingPlayerReloadTime =
                ChickenNapDanServer.layChoNguoiChoiVoiSung(this, sungMoi);
        this.dichVu.guiDoiSungTrongTran(
                TRAINING_PLAYER_INDEX,
                sungMoi.mau.part,
                sungCu.mau.iconID);
        this.dichVu.guiBaloLuyenTap(chienBinhVatPham);
        return true;
    }

    /** Item 220 hoi co dinh 300 HP cho chinh nguoi choi luyen tap. */
    private boolean dungCuuThuongBanThanLuyenTap(
            ChickenChienBinh chienBinh,
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            int viTriBalo
    ) throws IOException {
        if (chienBinh == null
                || this.trainingSession.trainingPlayerHp <= 0
                || this.trainingSession.trainingPlayerHp
                        >= this.trainingSession.trainingPlayerMaxHp
                || !this.coVatPhamChienSanSang(vatPham)
                || !chienBinh.chonVatPhamChienTrongTran(viTriBalo)) {
            return false;
        }
        if (!this.tieuThuMotVatPhamChien(vatPham)) {
            chienBinh.huyChonVatPhamChienTrongLuot(vatPham);
            return false;
        }
        if (!chienBinh.danhDauDaDungVatPhamChienTrongTran(vatPham)) {
            throw new IllegalStateException(
                    "Cuu thuong ca nhan luyen tap vuot quota sau khi tru kho");
        }
        int hpCu = this.trainingSession.trainingPlayerHp;
        this.trainingSession.trainingPlayerHp =
                ChickenCuuThuongBanThan.tinhMauSauCuuThuong(
                        this.trainingSession.trainingPlayerHp,
                        this.trainingSession.trainingPlayerMaxHp);
        this.dichVu.guiDungVatPhamLuyenTap(
                TRAINING_PLAYER_INDEX,
                (byte) vatPham.getCauHinh().getMaSuDung(),
                vatPham.getIcon());
        this.dichVu.guiCapNhatMauLuyenTap(
                TRAINING_PLAYER_INDEX,
                this.trainingSession.trainingPlayerHp,
                this.trainingSession.trainingPlayerMaxHp,
                (byte) 0);
        chienBinh.xoaVatPhamChienDangCho();
        this.dichVu.guiBaloLuyenTap(chienBinh);
        ChickenQuanLyMayChu.log(
                "[LUYEN_TAP][CUU_THUONG_BAN_THAN] playerId=" + this.ma
                + " healed="
                + (this.trainingSession.trainingPlayerHp - hpCu)
                + " hp=" + this.trainingSession.trainingPlayerHp + "/"
                + this.trainingSession.trainingPlayerMaxHp);
        return true;
    }

    /** Luyen tap khong co dong doi that, nen item 230 hoi cho chinh nguoi dung. */
    private boolean dungCuuThuongLuyenTap(
            ChickenChienBinh chienBinh,
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            int viTriBalo
    ) throws IOException {
        if (chienBinh == null
                || this.trainingSession.trainingPlayerHp <= 0
                || this.trainingSession.trainingPlayerHp
                        >= this.trainingSession.trainingPlayerMaxHp
                || !this.coVatPhamChienSanSang(vatPham)
                || !chienBinh.chonVatPhamChienTrongTran(viTriBalo)) {
            return false;
        }
        if (!this.tieuThuMotVatPhamChien(vatPham)) {
            chienBinh.huyChonVatPhamChienTrongLuot(vatPham);
            return false;
        }
        if (!chienBinh.danhDauDaDungVatPhamChienTrongTran(vatPham)) {
            throw new IllegalStateException(
                    "Cuu thuong luyen tap vuot quota sau khi tru kho");
        }
        this.trainingSession.trainingPlayerHp = Math.min(
                this.trainingSession.trainingPlayerMaxHp,
                ChickenCuuThuongDongDoi.tinhMauSauCuuThuong(
                        this.trainingSession.trainingPlayerHp,
                        this.trainingSession.trainingPlayerMaxHp));
        this.dichVu.guiDungVatPhamLuyenTap(
                TRAINING_PLAYER_INDEX,
                (byte) vatPham.getCauHinh().getMaSuDung(),
                vatPham.getIcon());
        this.dichVu.guiCapNhatMauLuyenTap(
                TRAINING_PLAYER_INDEX,
                this.trainingSession.trainingPlayerHp,
                this.trainingSession.trainingPlayerMaxHp,
                (byte) 0);
        chienBinh.xoaVatPhamChienDangCho();
        this.dichVu.guiBaloLuyenTap(chienBinh);
        ChickenQuanLyMayChu.log(
                "[LUYEN_TAP][CUU_THUONG] playerId=" + this.ma
                + " hp=" + this.trainingSession.trainingPlayerHp);
        return true;
    }

    private boolean dungDiChuyenX2LuyenTap(
            ChickenChienBinh chienBinh,
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            int viTriBalo
    ) throws IOException {
        if (chienBinh == null
                || this.trainingSession.trainingPlayerHp <= 0
                || !ChickenDiChuyenX2.coTheDung(chienBinh)
                || !this.coVatPhamChienSanSang(vatPham)
                || !chienBinh.chonVatPhamChienTrongTran(viTriBalo)) {
            return false;
        }
        if (!this.tieuThuMotVatPhamChien(vatPham)) {
            chienBinh.huyChonVatPhamChienTrongLuot(vatPham);
            return false;
        }
        if (!chienBinh.danhDauDaDungVatPhamChienTrongTran(vatPham)) {
            throw new IllegalStateException(
                    "Di chuyen x2 luyen tap vuot quota sau khi tru kho");
        }
        if (!ChickenDiChuyenX2.apDung(chienBinh)) {
            throw new IllegalStateException(
                    "Di chuyen x2 luyen tap doi state sau khi tru kho");
        }
        this.trainingSession.trainingMoveRemaining =
                ChickenDiChuyenX2.nhanDoiAnToan(
                        this.trainingSession.trainingMoveRemaining);
        this.dichVu.guiDungVatPhamLuyenTap(
                TRAINING_PLAYER_INDEX,
                (byte) vatPham.getCauHinh().getMaSuDung(),
                vatPham.getIcon());
        chienBinh.xoaVatPhamChienDangCho();
        this.dichVu.guiBaloLuyenTap(chienBinh);
        ChickenQuanLyMayChu.log(
                "[LUYEN_TAP][DI_CHUYEN_X2] playerId=" + this.ma
                + " remaining="
                + this.trainingSession.trainingMoveRemaining);
        return true;
    }

    private boolean dungNgungGioLuyenTap(
            ChickenChienBinh chienBinh,
            ChickenChienBinh.VatPhamChienTrongTran vatPham,
            int viTriBalo
    ) throws IOException {
        if (chienBinh == null
                || !ChickenNgungGio.coTheDung(chienBinh)
                || !this.coVatPhamChienSanSang(vatPham)
                || !chienBinh.chonVatPhamChienTrongTran(viTriBalo)) {
            return false;
        }
        if (!this.tieuThuMotVatPhamChien(vatPham)) {
            chienBinh.huyChonVatPhamChienTrongLuot(vatPham);
            return false;
        }
        if (!chienBinh.danhDauDaDungVatPhamChienTrongTran(vatPham)) {
            throw new IllegalStateException(
                    "Ngung gio luyen tap vuot quota sau khi tru kho");
        }

        this.trainingSession.trainingWind =
                ChickenNgungGio.taoTrangThaiKhongGio();
        chienBinh.xoaVatPhamChienDangCho();
        this.dichVu.guiDungVatPhamLuyenTap(
                TRAINING_PLAYER_INDEX,
                (byte) vatPham.getCauHinh().getMaSuDung(),
                vatPham.getIcon());
        this.dichVu.guiGio((byte) 0, (byte) 0);
        this.dichVu.guiBaloLuyenTap(chienBinh);
        ChickenQuanLyMayChu.log(
                "[LUYEN_TAP][NGUNG_GIO] playerId=" + this.ma
                + " windX=0 windY=0");
        return true;
    }

    private int layQuangDuongDiChuyenLuyenTapToiDa() {
        int coBan = ChickenThanhDiChuyenAVG.hoiDay(
                ChickenThanhDiChuyenAVG.quangDuongToiDa(this));
        ChickenChienBinh chienBinh =
                this.trainingSession.trainingVatPhamChienBinh;
        return chienBinh != null && chienBinh.coDiChuyenX2()
                ? ChickenDiChuyenX2.nhanDoiAnToan(coBan)
                : coBan;
    }

    public ChickenVatPham laySungTrongOTrongBaloLuyenTap(int viTriBalo) {
        return this.inTraining
                ? this.sungTheoOTrongBaloLuyenTap.get(viTriBalo) : null;
    }

    /** Resource sung co the doi trong snapshot luyen tap hien tai. */
    public synchronized short[] layMaHinhSungCoTheCamLuyenTap() {
        java.util.LinkedHashSet<Short> cacMa =
                new java.util.LinkedHashSet<>();
        if (laSungThuongDuocPhepTrongBalo(
                this.sungDangCamLuyenTap)) {
            cacMa.add(this.sungDangCamLuyenTap.mau.part);
        }
        for (ChickenVatPham sung :
                this.sungTheoOTrongBaloLuyenTap.values()) {
            if (laSungThuongDuocPhepTrongBalo(sung)
                    && sung.mau.part > 0) {
                cacMa.add(sung.mau.part);
            }
        }
        short[] ketQua = new short[cacMa.size()];
        int i = 0;
        for (short ma : cacMa) {
            ketQua[i++] = ma;
        }
        return ketQua;
    }

    private boolean trangBiHopLe(ChickenVatPham vatPham, int viTri) {
        return vatPham != null && vatPham.mau != null
                && vatPham.chiSo == viTri && vatPham.mau.loai == viTri;
    }

    public int layTongMauHienTai() {
        return ChickenChiSoNguoiChoi.tinhMau(this);
    }

    public int layTongTanCongHienTai() {
        return ChickenChiSoNguoiChoi.tinhTanCongVoiSung(
                this, this.laySungDangTrangBiHopLe());
    }

    public int layTongGiapHienTai() {
        return ChickenChiSoNguoiChoi.tinhGiap(this);
    }

    public int layTongMayManHienTai() {
        return ChickenChiSoNguoiChoi.tinhMayMan(this);
    }

    public int layTongDongDoiHienTai() {
        return ChickenChiSoNguoiChoi.tinhDongDoi(this);
    }

    public int layTongTocDoHienTai() {
        return ChickenChiSoNguoiChoi.tinhTocDo(this);
    }

    /**
     * Tổng chỉ số = chỉ số mặc định/tiềm năng + toàn bộ thuộc tính của đồ đang mặc
     * + thuộc tính ngọc ép trên từng món. Thuộc tính instance được ưu tiên; thuộc
     * tính gốc từ template chỉ được bổ sung khi instance không có để tránh cộng đôi.
     */
    private int tinhTongChiSo(int optionCongThang, int optionPhanTram,
            int chiSoTiemNang, int optionTheoLoaiSung) {
        long coDinh = this.layChiSoTiemNang(chiSoTiemNang);
        long phanTram = 0L;

        if (this.itemBody != null) {
            for (int i = 0; i < this.itemBody.length; i++) {
                ChickenVatPham trangBi = this.itemBody[i];
                if (!this.trangBiHopLe(trangBi, i)) {
                    continue;
                }
                long[] cong = this.layChiSoTuTrangBi(
                        trangBi,
                        optionCongThang,
                        optionPhanTram,
                        optionTheoLoaiSung
                );
                coDinh += cong[0];
                phanTram += cong[1];
            }
        }

        if (coDinh < 0L) {
            coDinh = 0L;
        }
        long tong = coDinh + coDinh * Math.max(0L, phanTram) / 100L;
        return (int)Math.min(Integer.MAX_VALUE, Math.max(0L, tong));
    }

    private long layChiSoTiemNang(int chiSo) {
        return ChickenQuanLyTiemNang.layGiaTri(this, chiSo);
    }

    /**
     * Kiểm tra một món đang mặc và cộng đủ:
     * 1) option hiện tại của item; 2) option gốc còn thiếu trong template;
     * 3) ngọc nằm trong các option Socket (id 16).
     */
    private long[] layChiSoTuTrangBi(ChickenVatPham trangBi,
            int optionCongThang, int optionPhanTram, int optionTheoLoaiSung) {
        long coDinh = 0L;
        long phanTram = 0L;
        java.util.HashSet<Integer> optionDaCo = new java.util.HashSet<Integer>();

        if (trangBi.itemOptions != null) {
            long[] tuItem = this.layChiSoTuDanhSachThuocTinh(
                    trangBi.itemOptions,
                    optionCongThang,
                    optionPhanTram,
                    optionTheoLoaiSung,
                    true,
                    optionDaCo,
                    false
            );
            coDinh += tuItem[0];
            phanTram += tuItem[1];
        }

        if (trangBi.mau != null && trangBi.mau.thuocTinhs != null) {
            long[] tuMau = this.layChiSoTuDanhSachThuocTinh(
                    trangBi.mau.thuocTinhs,
                    optionCongThang,
                    optionPhanTram,
                    optionTheoLoaiSung,
                    false,
                    optionDaCo,
                    true
            );
            coDinh += tuMau[0];
            phanTram += tuMau[1];
        }

        return new long[]{coDinh, phanTram};
    }

    private Vector layThuocTinhHieuLuc(ChickenVatPham vatPham) {
        if (vatPham == null) {
            return null;
        }
        if (vatPham.itemOptions != null && !vatPham.itemOptions.isEmpty()) {
            return vatPham.itemOptions;
        }
        return vatPham.mau != null ? vatPham.mau.thuocTinhs : null;
    }

    private long[] layChiSoTuDanhSachThuocTinh(Vector danhSach,
            int optionCongThang, int optionPhanTram,
            int optionTheoLoaiSung, boolean docNgoc,
            java.util.Set<Integer> optionDaCo, boolean chiDocOptionConThieu) {
        long coDinh = 0L;
        long phanTram = 0L;
        if (danhSach == null) {
            return new long[]{0L, 0L};
        }

        for (Object doiTuong : danhSach) {
            if (!(doiTuong instanceof ChickenThuocTinhVatPham)) {
                continue;
            }
            ChickenThuocTinhVatPham thuocTinh = (ChickenThuocTinhVatPham)doiTuong;
            if (thuocTinh.optionTemplate == null) {
                continue;
            }

            int maThuocTinh = thuocTinh.optionTemplate.ma;
            if (chiDocOptionConThieu && optionDaCo != null && optionDaCo.contains(maThuocTinh)) {
                continue;
            }
            if (!chiDocOptionConThieu && optionDaCo != null && maThuocTinh != 16) {
                optionDaCo.add(maThuocTinh);
            }

            int thamSo = Math.max(0, thuocTinh.thamSo);
            if (maThuocTinh == optionCongThang) {
                coDinh += thamSo;
            } else if (maThuocTinh == optionPhanTram
                    || maThuocTinh == 18
                    || (optionTheoLoaiSung >= 0 && maThuocTinh == optionTheoLoaiSung)) {
                phanTram += thamSo;
            } else if (docNgoc && maThuocTinh == 16 && thamSo > 0) {
                ChickenMauVatPham mauNgoc = ChickenQuanLyMayChu.itemTemplates.get(thamSo);
                if (mauNgoc != null && mauNgoc.loai == 12) {
                    long[] congNgoc = this.layChiSoTuDanhSachThuocTinh(
                            mauNgoc.thuocTinhs,
                            optionCongThang,
                            optionPhanTram,
                            optionTheoLoaiSung,
                            false,
                            null,
                            false
                    );
                    coDinh += congNgoc[0];
                    phanTram += congNgoc[1];
                }
            }
        }
        return new long[]{coDinh, phanTram};
    }

    /**
     * Trả về tổng tất cả option hiệu lực trên đồ đang mặc để các phần khác có thể
     * kiểm tra trực tiếp. Key là ID option, value là tổng tham số sau khi gộp đồ và ngọc.
     */
    public java.util.Map<Integer, Long> layThuocTinhDoDangMac() {
        java.util.LinkedHashMap<Integer, Long> ketQua = new java.util.LinkedHashMap<Integer, Long>();
        if (this.itemBody == null) {
            return ketQua;
        }
        for (int i = 0; i < this.itemBody.length; i++) {
            ChickenVatPham trangBi = this.itemBody[i];
            if (!this.trangBiHopLe(trangBi, i)) {
                continue;
            }
            java.util.HashSet<Integer> optionDaCo = new java.util.HashSet<Integer>();
            this.congTatCaOption(ketQua, trangBi.itemOptions, optionDaCo, false, true);
            if (trangBi.mau != null) {
                this.congTatCaOption(ketQua, trangBi.mau.thuocTinhs, optionDaCo, true, false);
            }
        }
        return ketQua;
    }

    private void congTatCaOption(java.util.Map<Integer, Long> ketQua, Vector danhSach,
            java.util.Set<Integer> optionDaCo, boolean chiDocOptionConThieu, boolean docNgoc) {
        if (danhSach == null) {
            return;
        }
        for (Object doiTuong : danhSach) {
            if (!(doiTuong instanceof ChickenThuocTinhVatPham)) {
                continue;
            }
            ChickenThuocTinhVatPham thuocTinh = (ChickenThuocTinhVatPham)doiTuong;
            if (thuocTinh.optionTemplate == null) {
                continue;
            }
            int ma = thuocTinh.optionTemplate.ma;
            if (chiDocOptionConThieu && optionDaCo.contains(ma)) {
                continue;
            }
            if (!chiDocOptionConThieu && ma != 16) {
                optionDaCo.add(ma);
            }
            int thamSo = Math.max(0, thuocTinh.thamSo);
            if (ma == 16 && docNgoc && thamSo > 0) {
                ChickenMauVatPham mauNgoc = ChickenQuanLyMayChu.itemTemplates.get(thamSo);
                if (mauNgoc != null && mauNgoc.loai == 12) {
                    this.congTatCaOption(ketQua, mauNgoc.thuocTinhs,
                            new java.util.HashSet<Integer>(), false, false);
                }
                continue;
            }
            Long cu = ketQua.get(ma);
            ketQua.put(ma, (cu == null ? 0L : cu.longValue()) + thamSo);
        }
    }

    /** CMD 26 gia tri 100 cua client Angry/POW. */
    public synchronized void kichHoatPowTrongTran() throws IOException {
        if (this.inTraining) {
            ChickenChienBinh chienBinhTrongLuot =
                    this.trainingSession.trainingVatPhamChienBinh;
            if (this.trainingSession.trainingCurrentTurn
                        != TRAINING_PLAYER_INDEX
                    || this.trainingSession.trainingWaitingShotEnd
                    || this.trainingSession.trainingBotAnimating
                    || this.trainingSession.trainingBossState
                        == TrainingBossState.DEAD
                    || this.trainingSession.trainingBossState
                        == TrainingBossState.ROUND_END
                    || chienBinhTrongLuot == null
                    || !chienBinhTrongLuot
                            .danhDauKichHoatPowTrongLuot()) {
                return;
            }
            if (!this.kichHoatPowNoiBo()) {
                chienBinhTrongLuot.huyDanhDauPowTrongLuot();
                return;
            }
            this.dichVu.guiKichHoatHieuUngPow(TRAINING_PLAYER_INDEX);
            return;
        }
        ChickenQuanLyChien tran =
                ChickenQuanLyChien.timTranDauCuaNguoiChoi(this);
        if (tran != null) {
            tran.kichHoatPow(this);
        }
    }

    private int layOptionTanCongTheoLoaiSung(ChickenMauVatPham sung) {
        if (sung == null) {
            return -1;
        }
        switch (sung.gioiTinh) {
            case 0:
                return 21;
            case 1:
                return 22;
            case 5:
                return 23;
            case 3:
                return 24;
            case 2:
                return 25;
            default:
                return -1;
        }
    }

    private int layThoiGianNapDanNguoiChoi(ChickenVatPham sung) {
        return ChickenNapDanServer.layChoNguoiChoiVoiSung(this, sung);
    }

    private int layThoiGianNapDan(ChickenVatPham sung) {
        return sung == null ? -1 : this.layThoiGianNapDan(this.layThuocTinhHieuLuc(sung));
    }

    private int layThoiGianNapDan(ChickenMauVatPham sung) {
        return sung == null ? -1 : this.layThoiGianNapDan(sung.thuocTinhs);
    }

    private int layThoiGianNapDan(Vector danhSach) {
        if (danhSach == null) {
            return -1;
        }
        for (Object doiTuong : danhSach) {
            if (!(doiTuong instanceof ChickenThuocTinhVatPham)) {
                continue;
            }
            ChickenThuocTinhVatPham thuocTinh = (ChickenThuocTinhVatPham)doiTuong;
            if (thuocTinh.optionTemplate != null && thuocTinh.optionTemplate.ma == 14 && thuocTinh.thamSo > 0) {
                return thuocTinh.thamSo;
            }
        }
        return -1;
    }

    private short chuanHoaGocBan(short goc) {
        if (this.avenger == AVG_ULTRON) {
            return ChickenGocBanUltron.chuanHoa(goc);
        }
        int ketQua = goc % 360;
        if (ketQua < 0) {
            ketQua += 360;
        }
        return (short) ketQua;
    }

    private short kepShort(int giaTri, int nhoNhat, int lonNhat) {
        int v = giaTri;
        if (v < nhoNhat) {
            v = nhoNhat;
        }
        if (v > lonNhat) {
            v = lonNhat;
        }
        return (short)v;
    }

    private boolean isDoubleTrainingBullet(byte loaiDan) {
        return loaiDan == 17 || loaiDan == 19;
    }

    private boolean laLoaiDanLuyenTapHopLe(byte loaiDan) {
        switch (loaiDan) {
            case 0:
            case 1:
            case 2:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 13:
            case 17:
            case 19:
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
            case 49:
            case 57:
            case 58:
                return true;
            default:
                return false;
        }
    }

    private short[] layDiemBanNguoiChoiLuyenTap(short goc) {
        if (this.avenger == AVG_ULTRON) {
            return ChickenGocBanUltron.layDiemBatDauDuongCan(
                    this.trainingSession.trainingPlayerX,
                    this.trainingSession.trainingPlayerY,
                    goc,
                    this.trainingSession.trainingMap.getWidth(),
                    this.trainingSession.trainingMap.getHeight()
            );
        }
        short trucSungX = this.trainingSession.trainingPlayerX;
        short trucSungY = this.kepShort(
                (short)(this.trainingSession.trainingPlayerY - TRAINING_PLAYER_GUN_PIVOT_Y),
                0,
                this.trainingSession.trainingMap.getHeight()
        );
        return this.layDiemDauNongLuyenTap(
                trucSungX,
                trucSungY,
                goc,
                this.layDoDaiDauNongTheoClient()
        );
    }

    /**
     * CPlayer của client dùng đầu nòng 40 px cho mọi súng, trừ Proton (gun 2)
     * chỉ dài 20 px. Nếu server tạo quỹ đạo từ điểm khác, cả góc và độ lệch
     * do gió đều nhìn như sai dù công thức vật lý đã đúng.
     */
    private int layDoDaiDauNongTheoClient() {
        ChickenVatPham sung = this.laySungDangTrangBiHopLe();
        ChickenQuanLyDanSung.DuLieuSung duLieu =
                ChickenQuanLyDanSung.theoSungDangTrangBi(sung);
        if (duLieu != null && duLieu.getIdSung() >= 150 && duLieu.getIdSung() <= 159) {
            return 20;
        }
        return TRAINING_PLAYER_BARREL_LENGTH;
    }

    /**
     * Tính đầu nòng bằng vector góc bắn.
     *
     * Hệ tọa độ map có Y tăng xuống dưới nên thành phần Y phải trừ sin(góc).
     * Ví dụ: góc 0 bắn sang phải, 90 bắn lên, 180 bắn trái và 270/-90
     * bắn xuống. Nếu đầu nòng vướng địa hình, thu ngắn dần theo đúng trục
     * nòng súng thay vì đẩy thẳng tọa độ Y lên làm lệch góc.
     */
    private short[] layDiemDauNongLuyenTap(
            short trucSungX,
            short trucSungY,
            short goc,
            int doDaiNong
    ) {
        return ChickenLuyenTapToaDo.layDiemDauNong(
                this.trainingSession.trainingMap,
                trucSungX,
                trucSungY,
                goc,
                doDaiNong
        );
    }

    private short[][] taoDuongDanLuyenTap(short muzzleX, short muzzleY, short goc, byte luc) {
        ChickenVatPham sung = this.laySungDangTrangBiHopLe();
        ChickenQuanLyDanSung.DuLieuSung duLieuSung =
                ChickenQuanLyDanSung.theoSungDangTrangBi(sung);
        if (duLieuSung == null) {
            return this.taoDuongDanMacDinhLuyenTap(muzzleX, muzzleY, goc, luc);
        }

        ChickenQuanLyCongThucSung.KetQuaQuyDao quyDao =
                ChickenQuanLyCongThucSung.taoQuyDaoTheoIdSung(
                        muzzleX,
                        muzzleY,
                        goc,
                        luc,
                        duLieuSung.getIdSung(),
                        ChickenHeThongGio.layWindXChoSung(this.trainingSession.trainingWind, duLieuSung.getIdSung()),
                        ChickenHeThongGio.layWindYChoSung(this.trainingSession.trainingWind, duLieuSung.getIdSung()),
                        this.trainingSession.trainingMap
                );
        return new short[][]{quyDao.getHienThiX(), quyDao.getHienThiY()};
    }

    private short[][] taoDuongDanGaRoiLuyenTap(
            short batDauX,
            short batDauY,
            int idSung
    ) {
        ChickenQuanLyCongThucSung.KetQuaQuyDao quyDao =
                ChickenQuanLyCongThucSung.taoQuyDaoDanGaRoi(
                        batDauX,
                        batDauY,
                        ChickenHeThongGio.layWindXChoSung(
                                this.trainingSession.trainingWind, idSung),
                        ChickenHeThongGio.layWindYChoSung(
                                this.trainingSession.trainingWind, idSung),
                        this.trainingSession.trainingMap
                );
        return new short[][]{quyDao.getHienThiX(), quyDao.getHienThiY()};
    }

    private short[][] taoDuongDanConRiuLuyenTap(
            short diemTachX,
            short diemTachY,
            short goc,
            byte luc,
            int chiSoCon,
            int idSung
    ) {
        ChickenQuanLyCongThucSung.KetQuaQuyDao quyDao =
                ChickenQuanLyCongThucSung.taoQuyDaoConRiu(
                        diemTachX,
                        diemTachY,
                        this.trainingSession.trainingPlayerX,
                        this.trainingSession.trainingPlayerY,
                        goc,
                        luc,
                        chiSoCon,
                        ChickenHeThongGio.layWindXChoSung(
                                this.trainingSession.trainingWind, idSung),
                        ChickenHeThongGio.layWindYChoSung(
                                this.trainingSession.trainingWind, idSung),
                        this.trainingSession.trainingMap
                );
        return new short[][]{quyDao.getHienThiX(), quyDao.getHienThiY()};
    }

    private short[][] taoDuongDanMacDinhLuyenTap(short muzzleX, short muzzleY, short goc, byte luc) {
        final int maxPoints = 128;
        short[] xs = new short[maxPoints];
        short[] ys = new short[maxPoints];
        double rad = Math.toRadians(this.chuanHoaGocBan(goc));
        double speed = Math.max(8, luc) * 0.85D;
        double gravity = 0.33D;

        int doDai = 1;
        xs[0] = muzzleX;
        ys[0] = muzzleY;
        int truocX = muzzleX;
        int truocY = muzzleY;

        for (int step = 1; step < maxPoints; step++) {
            double t = step;
            int rawX = (int)Math.round(muzzleX + Math.cos(rad) * speed * t);
            int rawY = (int)Math.round(muzzleY - Math.sin(rad) * speed * t + gravity * t * t);

            // Chỉ dừng khi từng pixel thật sự chạm pixel địa hình có alpha.
            short[] vaChamBanDo = this.timVaChamBanDoTrenDoanLuyenTap(
                    truocX, truocY, rawX, rawY);
            if (vaChamBanDo != null) {
                xs[doDai] = vaChamBanDo[0];
                ys[doDai] = vaChamBanDo[1];
                doDai++;
                break;
            }

            if (this.daRaKhoiBienMoPhongLuyenTap(rawX, rawY)) {
                xs[doDai] = this.kepShort(rawX, Short.MIN_VALUE, Short.MAX_VALUE);
                ys[doDai] = this.kepShort(rawY, Short.MIN_VALUE, Short.MAX_VALUE);
                doDai++;
                break;
            }

            // Y âm nghĩa là đạn đang bay phía trên map, không phải va chạm.
            xs[doDai] = this.kepShort(rawX, Short.MIN_VALUE, Short.MAX_VALUE);
            ys[doDai] = this.kepShort(rawY, Short.MIN_VALUE, Short.MAX_VALUE);
            doDai++;
            truocX = rawX;
            truocY = rawY;
        }
        return this.trimTrainingPath(xs, ys, doDai);
    }

    private boolean daRaKhoiBienMoPhongLuyenTap(int x, int y) {
        return ChickenQuanLyCongThucSung.daRaKhoiBienMoPhong(
                x,
                y,
                this.trainingSession.trainingMap.getWidth(),
                this.trainingSession.trainingMap.getHeight()
        );
    }

    private short[] timVaChamBanDoTrenDoanLuyenTap(
            int batDauX,
            int batDauY,
            int ketThucX,
            int ketThucY
    ) {
        int dx = ketThucX - batDauX;
        int dy = ketThucY - batDauY;
        int soBuoc = Math.max(Math.abs(dx), Math.abs(dy));
        if (soBuoc <= 0) {
            return null;
        }
        for (int buoc = 1; buoc <= soBuoc; buoc++) {
            double tiLe = (double)buoc / (double)soBuoc;
            int x = (int)Math.round(batDauX + dx * tiLe);
            int y = (int)Math.round(batDauY + dy * tiLe);
            if (x < 0 || y < 0 || x >= this.trainingSession.trainingMap.getWidth()
                    || y >= this.trainingSession.trainingMap.getHeight()) {
                continue;
            }
            if (this.trainingSession.trainingMap.coVaCham((short)x, (short)y)) {
                return new short[]{(short)x, (short)y};
            }
        }
        return null;
    }

    private void capNhatLoDiaHinhTheoDuongDanLuyenTap(
            short[] xs,
            short[] ys,
            TrainingCharacterHit vaChamNhanVat,
            byte loaiDan
    ) throws IOException {
        if ((loaiDan & 0xFF) == 25) {
            if (this.trainingSession.trainingMap.phaDiaHinhTheoDuongDan(
                    xs, ys, loaiDan)) {
                this.capNhatToaDoTheoNenMoiSauPhaDiaHinhLuyenTap();
            }
            return;
        }
        int xNo;
        int yNo;
        if (vaChamNhanVat != null) {
            xNo = vaChamNhanVat.hitX;
            yNo = vaChamNhanVat.hitY;
        } else {
            int soDiem = Math.min(xs == null ? 0 : xs.length, ys == null ? 0 : ys.length);
            if (soDiem <= 0) {
                return;
            }
            xNo = xs[soDiem - 1];
            yNo = ys[soDiem - 1];
            if (xNo < 0 || yNo < 0 || xNo >= this.trainingSession.trainingMap.getWidth()
                    || yNo >= this.trainingSession.trainingMap.getHeight()
                    || !this.trainingSession.trainingMap.coVaCham((short) xNo, (short) yNo)) {
                return;
            }
        }
        this.trainingSession.trainingMap.phaDiaHinh(xNo, yNo, loaiDan);

        /*
         * Mặt nạ va chạm đã thay đổi nên phải tính lại chân đứng ngay lập tức.
         * Y trong game tăng theo chiều đi xuống: nền bị thủng thì Y mới lớn hơn
         * Y cũ. Chỉ gửi packet khi tọa độ thật sự thay đổi.
         */
        this.capNhatToaDoTheoNenMoiSauPhaDiaHinhLuyenTap();
    }

    /**
     * Cập nhật tọa độ Y của người chơi và boss theo mặt nền còn lại sau khi map
     * bị phá. Hàm này chỉ hạ nhân vật xuống nền thấp hơn; trường hợp không còn
     * nền sẽ được vòng trọng lực hiện có tiếp tục xử lý cho tới khi rơi khỏi map.
     */
    private void capNhatToaDoTheoNenMoiSauPhaDiaHinhLuyenTap() throws IOException {
        if (!this.inTraining) {
            return;
        }

        /*
         * AVG bay giữ nguyên Y khi map bị khoét. Bản cũ luôn tìm nền phía dưới
         * rồi ghi đè trainingPlayerY, nên Ultron đang lơ lửng bị kéo xuống ngay
         * sau khi một phát bắn làm thay đổi mặt nạ địa hình.
         */
        if (!this.isFlyAvenger()) {
            short yNenNguoiChoi = ChickenLuyenTapToaDo.timMatDatTaiHoacThapHon(
                    this.trainingSession.trainingMap,
                    this.trainingSession.trainingPlayerX,
                    this.trainingSession.trainingPlayerY,
                    this::thanNhanVatThongThoang
            );
            if (yNenNguoiChoi != Short.MIN_VALUE
                    && yNenNguoiChoi > this.trainingSession.trainingPlayerY) {
                this.trainingSession.trainingPlayerY = yNenNguoiChoi;
                this.trainingSession.trainingTerrainPositionSyncPending = true;
            }
        }

        for (int botIndex = 0; botIndex < TRAINING_BOT_COUNT; botIndex++) {
            if (this.trainingSession.trainingBotDead[botIndex]) {
                continue;
            }

            // Không thay đổi Y giữa animation bắn. Packet 84 của client ghi
            // thẳng x/y vào boss; đổi Y giữa lúc tạo quỹ đạo sẽ làm boss giật.
            if (this.trainingSession.trainingBotAnimating
                    && this.trainingSession.trainingActiveBotIndex == botIndex
                    && this.trainingSession.trainingBossState == TrainingBossState.SHOOTING) {
                continue;
            }

            short bossX = this.trainingSession.trainingBotX[botIndex];
            short bossYCũ = this.trainingSession.trainingBotY[botIndex];
            short yNenBoss = this.timNenOnDinhChoBossLuyenTap(
                    bossX,
                    bossYCũ
            );

            /*
             * Nếu cột giữa bị khoét nhưng vùng hai chân vẫn còn nền đỡ thì giữ
             * boss tại chỗ. Không được coi đây là rơi vực.
             */
            if (yNenBoss == Short.MIN_VALUE) {
                if (this.viTriBossDangDungHopLe(botIndex)) {
                    continue;
                }

                // Boss thật sự mất nền: hạ một bước ngay và gửi lại cả X/Y.
                short yBossMoi = this.kepShort(
                        bossYCũ + 6,
                        Short.MIN_VALUE,
                        Short.MAX_VALUE
                );
                this.trainingSession.trainingBotY[botIndex] = yBossMoi;
                this.trainingSession.trainingDummyX = bossX;
                this.trainingSession.trainingDummyY = yBossMoi;
                this.trainingSession.trainingTerrainPositionSyncPending = true;
                continue;
            }
            if (yNenBoss <= bossYCũ) {
                continue;
            }

            this.trainingSession.trainingBotY[botIndex] = yNenBoss;
            this.trainingSession.trainingDummyY = yNenBoss;
            this.trainingSession.trainingTerrainPositionSyncPending = true;
        }
    }

    /**
     * Dong bo toa do do server tinh sau khi packet dan cuoi da ket thuc.
     * Gui ca nguoi choi va cac bot trong mot dot de client khong giu Y cu cua
     * bat ky muc tieu nao. Client chi hien thi; toa do van do server quyet dinh.
     */
    private void guiDongBoToaDoSauPhaDiaHinhLuyenTap() throws IOException {
        if (!this.inTraining
                || !this.trainingSession.trainingTerrainPositionSyncPending) {
            return;
        }
        this.trainingSession.trainingTerrainPositionSyncPending = false;
        this.dichVu.guiCapNhatXYLuyenTap(
                TRAINING_PLAYER_INDEX,
                this.trainingSession.trainingPlayerX,
                this.trainingSession.trainingPlayerY
        );
        for (int botIndex = 0; botIndex < TRAINING_BOT_COUNT; botIndex++) {
            if (this.trainingSession.trainingBotDead[botIndex]) {
                continue;
            }
            this.dichVu.guiCapNhatXYLuyenTap(
                    (byte) (botIndex + 1),
                    this.trainingSession.trainingBotX[botIndex],
                    this.trainingSession.trainingBotY[botIndex]
            );
        }
    }

    private int tinhSatThuongNoNguoiChoiLuyenTap(
            ChickenCauHinhSatThuongSung.HoSoSatThuong hoSo,
            int xNo,
            int yNo,
            int satThuongGoc
    ) {
        return ChickenTinhSatThuongNo.tinhSatThuongChoNhanVat(
                hoSo,
                satThuongGoc,
                xNo,
                yNo,
                this.trainingSession.trainingPlayerX,
                this.trainingSession.trainingPlayerY,
                false,
                this.kiemTraBanDoLuyenTap()
        );
    }

    private int tinhSatThuongNoBossLuyenTap(
            ChickenCauHinhSatThuongSung.HoSoSatThuong hoSo,
            int botIndex,
            int xNo,
            int yNo,
            int satThuongGoc
    ) {
        return ChickenTinhSatThuongNo.tinhSatThuongChoNhanVat(
                hoSo,
                satThuongGoc,
                xNo,
                yNo,
                this.trainingSession.trainingBotX[botIndex],
                this.trainingSession.trainingBotY[botIndex],
                true,
                this.kiemTraBanDoLuyenTap()
        );
    }

    private ChickenQuanLyCongThucSung.KiemTraBanDo kiemTraBanDoLuyenTap() {
        return this.trainingSession.trainingMap;
    }

    private boolean diemTrungNguoiChoiLuyenTap(int pointX, int pointY) {
        // Chỉ kiểm tra vùng cơ thể. Sprite súng có thể chìa ra ngoài hai bên
        // nhưng không thuộc hitbox, vì vậy đạn đi qua súng và chỉ trúng người.
        return ChickenKichThuocNhanVat.trungNguoiChoi(
                pointX,
                pointY,
                this.trainingSession.trainingPlayerX,
                this.trainingSession.trainingPlayerY
        );
    }

    /**
     * Cắt tia Ultron tại pixel địa hình đầu tiên trong luyện tập. Quét từng
     * pixel giữa các điểm quỹ đạo để không bỏ qua tường hoặc nền mỏng.
     */
    private short[][] catDuongDanUltronTaiVaChamBanDoLuyenTap(
            short[] xs,
            short[] ys
    ) {
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
                        || danX >= this.trainingSession.trainingMap.getWidth()
                        || danY >= this.trainingSession.trainingMap.getHeight()) {
                    continue;
                }
                if (this.trainingSession.trainingMap.coVaCham(
                        (short) danX, (short) danY)) {
                    TrainingCharacterHit vaChamBanDo = new TrainingCharacterHit(
                            Integer.MIN_VALUE,
                            i,
                            (short) danX,
                            (short) danY
                    );
                    return this.catDuongDanTaiVaChamNhanVatLuyenTap(
                            xs, ys, vaChamBanDo);
                }
            }
        }
        return new short[][]{xs, ys};
    }

    private TrainingCharacterHit timVaChamNhanVatTrenDuongDanLuyenTap(
            short[] xs,
            short[] ys,
            short muzzleX,
            short muzzleY,
            boolean kiemTraNguoiChoi,
            boolean kiemTraBoss
    ) {
        int soDiem = Math.min(xs.length, ys.length);
        if (soDiem < 2) {
            return null;
        }
        for (int i = 1; i < soDiem; i++) {
            int batDauX = xs[i - 1];
            int batDauY = ys[i - 1];
            int ketThucX = xs[i];
            int ketThucY = ys[i];
            int soBuoc = Math.max(1,
                    Math.max(Math.abs(ketThucX - batDauX), Math.abs(ketThucY - batDauY)));
            int buocBatDau = i == 1 ? 0 : 1;
            for (int buoc = buocBatDau; buoc <= soBuoc; buoc++) {
                double tiLe = (double) buoc / (double) soBuoc;
                int x = (int) Math.round(batDauX + (ketThucX - batDauX) * tiLe);
                int y = (int) Math.round(batDauY + (ketThucY - batDauY) * tiLe);
                /*
                 * Không suy đoán hitbox từ AVG của người đang ở luyện tập.
                 * Cách cũ nới 4px cho Ultron, vì thế khi người chơi dùng
                 * Ultron thì ngay cả đạn của boss bắn vào họ cũng bị đổi vùng
                 * va chạm. Tia laser chỉ khác quỹ đạo/hiệu ứng, còn hitbox
                 * luôn là vùng thân chuẩn như tất cả súng khác.
                 */
                boolean trungNguoiChoi = this.diemTrungNguoiChoiLuyenTap(x, y);
                if (kiemTraNguoiChoi && trungNguoiChoi) {
                    return new TrainingCharacterHit(
                            TrainingCharacterHit.PLAYER_TARGET,
                            i,
                            (short) x,
                            (short) y);
                }
                if (kiemTraBoss) {
                    for (int botIndex = 0; botIndex < TRAINING_BOT_COUNT; botIndex++) {
                        if (this.trainingSession.trainingBotDead[botIndex]) {
                            continue;
                        }
                        boolean trungBoss = this.diemTrungBossLuyenTap(
                                botIndex, x, y);
                        if (trungBoss) {
                            return new TrainingCharacterHit(
                                    botIndex,
                                    i,
                                    (short) x,
                                    (short) y);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Phần tử 0 là người chơi, các phần tử i+1 là boss i. Mỗi nhân vật chỉ
     * được đánh dấu một lần dù khiên Captain đi qua nhiều pixel trong hitbox.
     */
    private boolean[] timTatCaNhanVatBiCaptainLuotQuaLuyenTap(
            short[] xs,
            short[] ys
    ) {
        boolean[] ketQua = new boolean[TRAINING_BOT_COUNT + 1];
        if (xs == null || ys == null) {
            return ketQua;
        }
        int soDiem = Math.min(xs.length, ys.length);
        for (int i = 1; i < soDiem; i++) {
            int x1 = xs[i - 1];
            int y1 = ys[i - 1];
            int x2 = xs[i];
            int y2 = ys[i];
            int soBuoc = Math.max(1, Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)));
            for (int buoc = 1; buoc <= soBuoc; buoc++) {
                double tiLe = (double) buoc / (double) soBuoc;
                int danX = (int) Math.round(x1 + (x2 - x1) * tiLe);
                int danY = (int) Math.round(y1 + (y2 - y1) * tiLe);
                if (!ketQua[0] && this.diemTrungNguoiChoiLuyenTap(danX, danY)) {
                    ketQua[0] = true;
                }
                for (int botIndex = 0; botIndex < TRAINING_BOT_COUNT; botIndex++) {
                    if (!ketQua[botIndex + 1]
                            && !this.trainingSession.trainingBotDead[botIndex]
                            && this.diemTrungBossLuyenTap(botIndex, danX, danY)) {
                        ketQua[botIndex + 1] = true;
                    }
                }
            }
        }
        return ketQua;
    }

    private boolean diemCuoiLaDiaHinhLuyenTap(short[] xs, short[] ys) {
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
                && x < this.trainingSession.trainingMap.getWidth()
                && y < this.trainingSession.trainingMap.getHeight()
                && this.trainingSession.trainingMap.coVaCham(x, y);
    }

    private short[][] taoDuongDanThangLuyenTap(short batDauX, short batDauY, short targetX, short targetY) {
        int dx = targetX - batDauX;
        int dy = targetY - batDauY;
        int steps = Math.max(8, Math.min(36, Math.max(Math.abs(dx), Math.abs(dy)) / 16));
        short[] xs = new short[steps];
        short[] ys = new short[steps];
        for (int i = 0; i < steps; i++) {
            double t = steps == 1 ? 1.0D : (double)i / (double)(steps - 1);
            xs[i] = (short)Math.round(batDauX + dx * t);
            ys[i] = (short)Math.round(batDauY + dy * t);
        }
        return new short[][]{xs, ys};
    }

    private short[][] trimTrainingPath(short[] xs, short[] ys, int len) {
        len = Math.max(1, Math.min(len, xs.length));
        short[] trimX = new short[len];
        short[] trimY = new short[len];
        System.arraycopy(xs, 0, trimX, 0, len);
        System.arraycopy(ys, 0, trimY, 0, len);
        return new short[][]{trimX, trimY};
    }

    private boolean diemTrungBossLuyenTap(int botIndex, int pointX, int pointY) {
        return ChickenKichThuocNhanVat.trungBoss(
                pointX,
                pointY,
                this.trainingSession.trainingBotX[botIndex],
                this.trainingSession.trainingBotY[botIndex]
        );
    }

    private short[][] catDuongDanTaiVaChamNhanVatLuyenTap(
            short[] xs,
            short[] ys,
            TrainingCharacterHit vaCham
    ) {
        int doDai = Math.max(2, Math.min(xs.length, vaCham.segmentIndex + 1));
        short[] ketQuaX = new short[doDai];
        short[] ketQuaY = new short[doDai];
        int soDiemGiuLai = doDai - 1;
        System.arraycopy(xs, 0, ketQuaX, 0, soDiemGiuLai);
        System.arraycopy(ys, 0, ketQuaY, 0, soDiemGiuLai);
        ketQuaX[doDai - 1] = vaCham.hitX;
        ketQuaY[doDai - 1] = vaCham.hitY;
        return new short[][]{ketQuaX, ketQuaY};
    }

    private void chotDiemCuoiHulkTaiMucTieuLuyenTap(
            short[][] duongDan,
            TrainingCharacterHit vaCham
    ) {
        if (duongDan == null || duongDan.length < 2 || vaCham == null) {
            return;
        }
        short mucTieuX;
        short mucTieuY;
        if (vaCham.laNguoiChoi()) {
            mucTieuX = this.trainingSession.trainingPlayerX;
            mucTieuY = this.trainingSession.trainingPlayerY;
        } else if (vaCham.botIndex >= 0
                && vaCham.botIndex < TRAINING_BOT_COUNT) {
            mucTieuX = this.trainingSession.trainingBotX[vaCham.botIndex];
            mucTieuY = this.trainingSession.trainingBotY[vaCham.botIndex];
        } else {
            return;
        }
        ChickenCoCheHulk.datDiemCuoiTaiChanMucTieu(
                duongDan[0], duongDan[1], mucTieuX, mucTieuY);
    }

    private boolean diemLuyenTapTrungBia(int pointX, int pointY) {
        return this.layBotTrungDiemLuyenTap(pointX, pointY) >= 0;
    }

    private int layBotTrungDiemLuyenTap(int pointX, int pointY) {
        int botGanNhat = -1;
        int khoangCachNhoNhat = Integer.MAX_VALUE;
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            if (this.trainingSession.trainingBotDead[i]) {
                continue;
            }
            int dx = pointX - this.trainingSession.trainingBotX[i];
            int dy = pointY - this.trainingSession.trainingBotY[i];
            int khoangCach = dx * dx + dy * dy;
            if (khoangCach < khoangCachNhoNhat) {
                khoangCachNhoNhat = khoangCach;
                botGanNhat = i;
            }
        }
        return khoangCachNhoNhat <= 60 * 60 ? botGanNhat : -1;
    }

    private int layBotLuyenTapSongGanNhat() {
        int best = -1;
        int bestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            if (this.trainingSession.trainingBotDead[i]) {
                continue;
            }
            int dx = this.trainingSession.trainingBotX[i] - this.trainingSession.trainingPlayerX;
            int dy = this.trainingSession.trainingBotY[i] - this.trainingSession.trainingPlayerY;
            int distance = dx * dx + dy * dy;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return best;
    }

    private synchronized void chuyenLuotTheoNapDan(byte benVuaBan) throws IOException {
        this.chuyenLuotTheoNapDan(benVuaBan, -1);
    }

    private synchronized void chuyenLuotTheoNapDan(
            byte benVuaBan,
            int napDanSauHanhDong
    ) throws IOException {
        if (!this.inTraining || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END) {
            return;
        }

        if (!this.capNhatTrongLucSauLuotLuyenTap(benVuaBan)) {
            return;
        }
        this.chuyenLuotTheoNapDanKhongCapNhatTrongLuc(
                benVuaBan,
                napDanSauHanhDong);
    }

    private synchronized void chuyenLuotTheoNapDanKhongCapNhatTrongLuc(
            byte benVuaBan
    ) throws IOException {
        this.chuyenLuotTheoNapDanKhongCapNhatTrongLuc(benVuaBan, -1);
    }

    private synchronized void chuyenLuotTheoNapDanKhongCapNhatTrongLuc(
            byte benVuaBan,
            int napDanSauHanhDong
    ) throws IOException {
        if (!this.inTraining
                || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END
                || this.trainingSession.trainingBossState == TrainingBossState.DEAD) {
            return;
        }
        this.trainingSession.trainingMap.ketThucLuotVoiRong(
                benVuaBan == TRAINING_PLAYER_INDEX);
        if (benVuaBan == TRAINING_PLAYER_INDEX) {
            this.huyLichHetGioLuotNguoiChoiLuyenTap();
            ChickenTrangThaiHanhDongLuot.ketThucLuot(
                    this.trainingSession.trainingVatPhamChienBinh);
            if (this.huyPowDaKichHoat()) {
                this.dichVu.guiCapNhatPow(
                        TRAINING_PLAYER_INDEX, this.layPowTrongTran());
            }
            this.trainingSession.trainingPlayerReload =
                    napDanSauHanhDong >= 0
                            ? Math.max(
                                    ChickenNapDanServer.TOI_THIEU,
                                    napDanSauHanhDong)
                            : this.trainingSession.trainingPlayerReloadTime;
        } else {
            this.trainingSession.trainingBossReload =
                    napDanSauHanhDong >= 0
                            ? Math.max(
                                    ChickenNapDanServer.TOI_THIEU,
                                    napDanSauHanhDong)
                            : this.trainingSession.trainingBossReloadTime;
        }
        long[] thuTuLuyenTap = {
                this.trainingSession.trainingPlayerReloadOrder,
                this.trainingSession.trainingBossReloadOrder
        };
        this.trainingSession.trainingReloadOrderCounter =
                ChickenHangDoiNapDan.ghiNhanHanhDong(
                        thuTuLuyenTap,
                        benVuaBan,
                        this.trainingSession.trainingReloadOrderCounter);
        this.trainingSession.trainingPlayerReloadOrder =
                thuTuLuyenTap[TRAINING_PLAYER_INDEX];
        this.trainingSession.trainingBossReloadOrder =
                thuTuLuyenTap[TRAINING_BOSS_INDEX];
        int[] napDanLuyenTap = {
                this.trainingSession.trainingPlayerReload,
                this.trainingSession.trainingBossReload
        };
        int slotTiep = ChickenHangDoiNapDan.timSlotTiepTheo(
                napDanLuyenTap,
                thuTuLuyenTap,
                benVuaBan,
                slot -> slot == TRAINING_PLAYER_INDEX
                        || slot == TRAINING_BOSS_INDEX);
        this.trainingSession.trainingPlayerReload = napDanLuyenTap[0];
        this.trainingSession.trainingBossReload = napDanLuyenTap[1];
        this.trainingSession.trainingCurrentTurn = (byte) slotTiep;
        this.trainingSession.trainingBossState = TrainingBossState.IDLE;
        this.trainingSession.trainingTurnId++;
        if (this.trainingSession.trainingCurrentTurn == TRAINING_PLAYER_INDEX) {
            this.trainingSession.trainingMoveRemaining =
                    this.layQuangDuongDiChuyenLuyenTapToiDa();
            if (this.avenger == AVG_IRON_MAN) {
                this.trainingSession.trainingIronManDaDungKyNang = false;
                this.trainingSession.trainingIronManLaserSanSang = false;
                this.trainingSession.trainingIronManMenuTurnId = -1L;
            }
        }
        this.trainingSession.trainingWind = ChickenHeThongGio.taoGioMoi();
        this.guiTrangThaiLuotLuyenTap();
        if (this.trainingSession.trainingCurrentTurn == TRAINING_BOSS_INDEX) {
            this.scheduleTrainingBotShot(500L);
        } else {
            this.lapLichHetGioLuotNguoiChoiLuyenTap();
        }
    }

    private void guiTrangThaiLuotLuyenTap() throws IOException {
        this.dichVu.guiDongMenuKyNangDacBiet();
        this.dichVu.guiGio(
                this.trainingSession.trainingWind.getWindX(),
                this.trainingSession.trainingWind.getWindY()
        );
        short x = this.trainingSession.trainingCurrentTurn == TRAINING_PLAYER_INDEX ? this.trainingSession.trainingPlayerX : this.trainingSession.trainingBotX[0];
        short y = this.trainingSession.trainingCurrentTurn == TRAINING_PLAYER_INDEX ? this.trainingSession.trainingPlayerY : this.trainingSession.trainingBotY[0];
        this.dichVu.guiLuotLuyenTapTiep(this.trainingSession.trainingCurrentTurn, x, y, this.trainingSession.trainingPlayerReload, this.trainingSession.trainingBossReload);
        if (this.trainingSession.trainingCurrentTurn
                == TRAINING_PLAYER_INDEX
                && this.trainingSession.trainingVatPhamChienBinh != null
                && this.trainingSession.trainingVatPhamChienBinh
                        .coVatPhamChienCanHienLai()) {
            this.dichVu.guiBaloLuyenTap(
                    this.trainingSession.trainingVatPhamChienBinh);
        }
        this.guiMenuHawkLuyenTapNeuCo();
        this.guiMenuThorLuyenTapNeuCo();
        this.guiMenuLokiLuyenTapNeuCo();
        this.guiMenuUltronLuyenTapNeuCo();
        this.guiMenuIronManLuyenTapNeuCo();
    }

    /** Chế độ test: cứ tới lượt người chơi trong luyện tập thì mở menu Hawk một lần. */
    private void guiMenuHawkLuyenTapNeuCo() {
        if (!this.inTraining
                || this.avenger != AVG_HAWK
                || this.trainingSession.trainingCurrentTurn != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingBossState == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END
                || this.trainingSession.trainingHawkSkillActive
                || this.trainingSession.trainingHawkMenuTurnId == this.trainingSession.trainingTurnId) {
            return;
        }

        boolean coMucTieu = false;
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            if (!this.trainingSession.trainingBotDead[i]
                    && this.trainingSession.trainingBotHp[i] > 0) {
                coMucTieu = true;
                break;
            }
        }
        if (!coMucTieu) {
            return;
        }

        this.trainingSession.trainingHawkMenuTurnId = this.trainingSession.trainingTurnId;
        this.dichVu.guiChonMucTieuHawk();
        System.out.println("[HAWK] TEST_MO_MENU_NGAY mode=training player="
                + this.ten + " turnId=" + this.trainingSession.trainingTurnId
                + " avenger=" + this.avenger);
    }

    /** Chế độ test: tới lượt Thor thì hiện lựa chọn Sấm sét đúng một lần. */
    private void guiMenuThorLuyenTapNeuCo() {
        if (!this.inTraining
                || this.avenger != AVG_THOR
                || this.trainingSession.trainingCurrentTurn != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingBossState == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END
                || this.trainingSession.trainingWaitingShotEnd
                || this.trainingSession.trainingThorSkillActive
                || this.trainingSession.trainingThorMenuTurnId == this.trainingSession.trainingTurnId) {
            return;
        }

        this.trainingSession.trainingThorMenuTurnId = this.trainingSession.trainingTurnId;
        this.dichVu.guiChonKyNangThor();
        System.out.println("[THOR] TEST_MO_MENU mode=training player="
                + this.ten + " turnId=" + this.trainingSession.trainingTurnId);
    }

    /** Chế độ test: tới lượt Loki thì hiện menu Giả dạng đúng một lần. */
    private void guiMenuLokiLuyenTapNeuCo() {
        if (!this.inTraining
                || this.avenger != AVG_LOKI
                || !this.coMucTieuNguoiChoiLokiLuyenTap()
                || this.trainingSession.trainingCurrentTurn != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingBossState == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END
                || this.trainingSession.trainingWaitingShotEnd
                || this.trainingSession.trainingLokiSkillActive
                || this.trainingSession.trainingLokiDaDungKyNang
                || this.trainingSession.trainingLokiMenuTurnId
                    == this.trainingSession.trainingTurnId) {
            return;
        }

        boolean coMucTieu = false;
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            if (!this.trainingSession.trainingBotDead[i]
                    && this.trainingSession.trainingBotHp[i] > 0) {
                coMucTieu = true;
                break;
            }
        }
        if (!coMucTieu) {
            return;
        }

        this.trainingSession.trainingLokiMenuTurnId =
                this.trainingSession.trainingTurnId;
        this.dichVu.guiChonKyNangLoki();
        System.out.println("[LOKI] TEST_MO_MENU mode=training player="
                + this.ten + " turnId=" + this.trainingSession.trainingTurnId);
    }

    /** Chế độ test: tới lượt Ultron thì hiện menu Bắn x3 một lần. */
    private void guiMenuUltronLuyenTapNeuCo() {
        if (!this.inTraining
                || this.avenger != AVG_ULTRON
                || this.trainingSession.trainingCurrentTurn != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingBossState == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END
                || this.trainingSession.trainingWaitingShotEnd
                || this.trainingSession.trainingUltronDaDungKyNang
                || this.trainingSession.trainingUltronDangBanX3
                || this.trainingSession.trainingUltronMenuTurnId
                    == this.trainingSession.trainingTurnId) {
            return;
        }
        this.trainingSession.trainingUltronMenuTurnId =
                this.trainingSession.trainingTurnId;
        this.dichVu.guiChonKyNangUltron();
        System.out.println("[ULTRON] GUI_MENU mode=training turnId="
                + this.trainingSession.trainingTurnId
                + " choNguoiChoiChon=true");
    }

    private void guiMenuIronManLuyenTapNeuCo() {
        if (!this.inTraining
                || this.avenger != AVG_IRON_MAN
                || this.trainingSession.trainingCurrentTurn
                    != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingBossState
                    == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState
                    == TrainingBossState.ROUND_END
                || this.trainingSession.trainingWaitingShotEnd
                || this.trainingSession.trainingIronManDaDungKyNang
                || this.trainingSession.trainingIronManLaserSanSang
                || this.trainingSession.trainingIronManMenuTurnId
                    == this.trainingSession.trainingTurnId) {
            return;
        }
        this.trainingSession.trainingIronManMenuTurnId =
                this.trainingSession.trainingTurnId;
        this.dichVu.guiChonKyNangIronMan();
    }

    public synchronized boolean kichHoatKyNangIronManLuyenTap()
            throws IOException {
        if (!this.inTraining
                || this.avenger != AVG_IRON_MAN
                || this.trainingSession.trainingCurrentTurn
                    != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingBossState
                    == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState
                    == TrainingBossState.ROUND_END
                || this.trainingSession.trainingWaitingShotEnd
                || this.trainingSession.trainingIronManDaDungKyNang
                || this.trainingSession.trainingIronManLaserSanSang
                || !this.coTheKichHoatKyNangLuyenTap()
                || this.trainingSession.trainingIronManMenuTurnId
                    != this.trainingSession.trainingTurnId) {
            return false;
        }
        this.trainingSession.trainingIronManDaDungKyNang = true;
        this.trainingSession.trainingIronManLaserSanSang = true;
        this.dichVu.guiDongChoKyNangUltron();
        this.dichVu.guiTrangThaiNgamLaserIronMan(true);
        return true;
    }

    /**
     * Client generic menu trả CMD -47 với lựa chọn 0. Client tự đóng menu sau
     * khi gửi lựa chọn, nên chỉ bật trạng thái kỹ năng và chờ CMD 22 thật.
     * Không gửi lại CMD 24 của cùng lượt vì packet lượt trùng khiến client bị
     * kẹt vòng xoay hoặc ngắt kết nối.
     */
    public synchronized boolean kichHoatKyNangUltronLuyenTap()
            throws IOException {
        if (!this.inTraining
                || this.avenger != AVG_ULTRON
                || this.trainingSession.trainingCurrentTurn
                    != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingBossState
                    == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState
                    == TrainingBossState.ROUND_END
                || this.trainingSession.trainingWaitingShotEnd
                || this.trainingSession.trainingUltronDaDungKyNang
                || this.trainingSession.trainingUltronDangBanX3
                || !this.coTheKichHoatKyNangLuyenTap()
                || this.trainingSession.trainingUltronMenuTurnId
                    != this.trainingSession.trainingTurnId) {
            return false;
        }

        this.trainingSession.trainingUltronDaDungKyNang = true;
        this.trainingSession.trainingUltronDangBanX3 = true;
        this.dichVu.guiDongChoKyNangUltron();

        /*
         * Không gọi guiTrangThaiLuotLuyenTap() tại đây. Hàm đó phát lại cả
         * CMD 25 và CMD 24 trong khi client đang thoát menu -47, gây kẹt xoay.
         * Giữ nguyên lượt/turnId và chờ packet bắn CMD 22 tiếp theo.
         */
        System.out.println("[ULTRON] DA_CHON_BAN_X3 mode=training"
                + " choPhatBanThat=true khongGuiCmd=-67"
                + " khongGuiLaiLuot=true turnId="
                + this.trainingSession.trainingTurnId);
        return true;
    }

    /** Route CMD -91 theo AVG đang mặc trong luyện tập. */
    public synchronized void xuLyCmd91KyNangDacBietLuyenTap(ChickenTinNhan ms) throws IOException {
        if (!this.coTheKichHoatKyNangLuyenTap()) {
            return;
        }
        if (this.avenger == AVG_THOR) {
            this.xuLyCmd91ThorLuyenTap(ms);
            return;
        }
        if (this.avenger == AVG_LOKI) {
            this.xuLyCmd91LokiLuyenTap(ms);
            return;
        }
        this.xuLyCmd91HawkLuyenTap(ms);
    }

    private boolean coTheKichHoatKyNangLuyenTap() {
        ChickenChienBinh chienBinh =
                this.trainingSession.trainingVatPhamChienBinh;
        return chienBinh == null
                || ChickenTrangThaiHanhDongLuot
                        .coTheKichHoatKyNang(chienBinh);
    }

    /** Nhận action 0 trực tiếp từ danh sách Giả dạng của Loki. */
    private synchronized void xuLyCmd91LokiLuyenTap(ChickenTinNhan ms)
            throws IOException {
        if (!this.inTraining || ms == null || this.avenger != AVG_LOKI) {
            System.out.println("[LOKI] BO_QUA_LUYEN_TAP inTraining="
                    + this.inTraining + " avenger=" + this.avenger);
            return;
        }
        if (!this.coMucTieuNguoiChoiLokiLuyenTap()) {
            System.out.println("[LOKI] KHONG_CO_MUC_TIEU_NGUOI_CHOI mode=training");
            return;
        }

        int soByte = ms.boDoc().available();
        if (soByte < 2) {
            System.out.println("[LOKI] PACKET_THIEU mode=training bytes=" + soByte);
            return;
        }

        int action = ms.boDoc().readUnsignedByte();
        int battleIndex = ms.boDoc().readUnsignedByte();
        System.out.println("[LOKI] DOC_PACKET mode=training action="
                + action + " battleIndex=" + battleIndex);

        if (this.trainingSession.trainingCurrentTurn != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingLokiMenuTurnId
                    != this.trainingSession.trainingTurnId
                || this.trainingSession.trainingBossState == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END
                || this.trainingSession.trainingLokiDaDungKyNang
                || this.trainingSession.trainingLokiSkillActive) {
            System.out.println("[LOKI] SAI_TRANG_THAI_LUYEN_TAP turn="
                    + this.trainingSession.trainingCurrentTurn
                    + " daDung=" + this.trainingSession.trainingLokiDaDungKyNang);
            return;
        }

        // Với menu native action 5, client tự mở danh sách nhân vật.
        // Sau khi chọn, client gửi thẳng action 0 + battleIndex mục tiêu.
        if (action == 0) {
            this.trainingSession.trainingLokiDangChoChonMucTieu = false;
        } else if (action == 2) {
            // Tương thích luồng cũ: action 2 mới mở danh sách bằng action 1.
            if (battleIndex != (TRAINING_PLAYER_INDEX & 0xFF)) {
                System.out.println("[LOKI] SAI_SELF_INDEX mode=training index="
                        + battleIndex);
                return;
            }
            this.trainingSession.trainingLokiDangChoChonMucTieu = true;
            this.dichVu.guiChonMucTieuLoki();
            System.out.println("[LOKI] MO_DANH_SACH_MUC_TIEU mode=training");
            return;
        } else if (action != 1
                || !this.trainingSession.trainingLokiDangChoChonMucTieu) {
            System.out.println("[LOKI] SAI_ACTION mode=training action=" + action);
            return;
        }

        int botIndex = battleIndex - 1;
        if (botIndex < 0 || botIndex >= TRAINING_BOT_COUNT
                || this.trainingSession.trainingBotDead[botIndex]
                || this.trainingSession.trainingBotHp[botIndex] <= 0) {
            this.trainingSession.trainingLokiDangChoChonMucTieu = false;
            this.trainingSession.trainingWaitingShotEnd = false;
            System.out.println("[LOKI] MUC_TIEU_KHONG_HOP_LE mode=training index="
                    + battleIndex);
            return;
        }

        this.trainingSession.trainingLokiSkillId++;
        this.trainingSession.trainingLokiDangChoChonMucTieu = false;
        this.trainingSession.trainingLokiDaDungKyNang = true;

        // Biến hình chỉ là kỹ năng phụ, không phải phát bắn.
        // Không khóa lượt và không ghi nhận lượt bắn đã kết thúc.
        this.trainingSession.trainingLokiSkillActive = true;
        this.trainingSession.trainingWaitingShotEnd = false;

        // Client action 0 copy cả tên hiển thị, ngoại hình, AVG và hình súng.
        // Server chỉ copy máu; tên tài khoản, súng thật và chỉ số vẫn là Loki.
        this.trainingSession.trainingPlayerMaxHp = Math.max(
                1, this.trainingSession.trainingBossMaxHp);
        this.trainingSession.trainingPlayerHp = Math.max(
                0, Math.min(
                        this.trainingSession.trainingPlayerMaxHp,
                        this.trainingSession.trainingBotHp[botIndex]
                )
        );

        this.dichVu.guiBienHinhLoki(
                TRAINING_PLAYER_INDEX,
                (byte)(botIndex + 1)
        );
        this.dichVu.guiCapNhatMauLuyenTap(
                TRAINING_PLAYER_INDEX,
                this.trainingSession.trainingPlayerHp,
                this.trainingSession.trainingPlayerMaxHp,
                (byte)0
        );
        System.out.println("[LOKI] BIEN_HINH mode=training target="
                + (botIndex + 1)
                + " hp=" + this.trainingSession.trainingPlayerHp
                + " maxHp=" + this.trainingSession.trainingPlayerMaxHp
                + " tenHienThi=" + this.layTenBossLuyenTap(botIndex)
                + " giuDenHetTran=true");

        // Gửi xong biến hình là mở lại điều khiển ngay trong chính lượt hiện tại.
        this.trainingSession.trainingLokiSkillActive = false;
        this.trainingSession.trainingWaitingShotEnd = false;
        System.out.println("[LOKI] KET_THUC_BIEN_HINH mode=training"
                + " khongChuyenLuot=true vanDuocBan=true");
    }

    /** Luyện tập hiện chỉ có bot server, Loki không được phép giả dạng bot. */
    private boolean coMucTieuNguoiChoiLokiLuyenTap() {
        return false;
    }

    /** Nhận CMD -91 của Hawk khi người chơi đang ở luyện tập. */
    public synchronized void xuLyCmd91HawkLuyenTap(ChickenTinNhan ms) throws IOException {
        if (!this.inTraining || ms == null) {
            System.out.println("[HAWK] BO_QUA_LUYEN_TAP inTraining=" + this.inTraining);
            return;
        }
        if (this.avenger != AVG_HAWK) {
            System.out.println("[HAWK] CMD_-91_KHONG_PHAI_HAWK mode=training avenger="
                    + this.avenger);
            return;
        }
        int soByte = ms.boDoc().available();
        if (soByte < 2) {
            System.out.println("[HAWK] PACKET_THIEU mode=training bytes=" + soByte);
            return;
        }

        int action = ms.boDoc().readUnsignedByte();
        int targetIndex = ms.boDoc().readUnsignedByte();
        System.out.println("[HAWK] DOC_PACKET mode=training action="
                + action + " targetIndex=" + targetIndex);

        if (action != 1) {
            System.out.println("[HAWK] SAI_ACTION mode=training action=" + action);
            return;
        }
        if (this.trainingSession.trainingCurrentTurn != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingHawkMenuTurnId
                    != this.trainingSession.trainingTurnId
                || this.trainingSession.trainingWaitingShotEnd
                || this.trainingSession.trainingBotAnimating
                || this.trainingSession.trainingHawkSkillActive
                || this.trainingSession.trainingBossState == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END) {
            System.out.println("[HAWK] SAI_TRANG_THAI_LUYEN_TAP turn="
                    + this.trainingSession.trainingCurrentTurn
                    + " waiting=" + this.trainingSession.trainingWaitingShotEnd
                    + " active=" + this.trainingSession.trainingHawkSkillActive);
            return;
        }

        int botIndex = targetIndex - 1;
        if (botIndex < 0 || botIndex >= TRAINING_BOT_COUNT
                || this.trainingSession.trainingBotDead[botIndex]
                || this.trainingSession.trainingBotHp[botIndex] <= 0) {
            System.out.println("[HAWK] MUC_TIEU_KHONG_HOP_LE mode=training index="
                    + targetIndex);
            return;
        }

        final long phien = this.trainingSession.trainingSessionId;
        final long skillId = ++this.trainingSession.trainingHawkSkillId;
        this.trainingSession.trainingHawkSkillActive = true;
        this.trainingSession.trainingWaitingShotEnd = true;
        this.trainingSession.trainingLastShotTurnId = this.trainingSession.trainingTurnId;
        System.out.println("[HAWK] CHON_MUC_TIEU mode=training hawk=0 target="
                + targetIndex + " skillId=" + skillId);
        final ChickenChienBinh[] danhSachMayMan =
                this.taoDanhSachMayManLuyenTap();
        final ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(danhSachMayMan[0], danhSachMayMan);

        short[] dauNong = this.layDiemBanNguoiChoiLuyenTap(
                ChickenHoatAnhHawk.GOC_BAY_LEN
        );
        ChickenHoatAnhHawk.DuongDan bayLen = ChickenHoatAnhHawk.taoDuongBayLen(
                dauNong[0],
                dauNong[1]
        );
        ChickenHoatAnhHawk.LoatDuongDan loatBayLen =
                ChickenHoatAnhHawk.taoLoatBonMuiNoiDuoi(bayLen);
        this.dichVu.guiKetQuaBanLuyenTap(
                TRAINING_PLAYER_INDEX,
                ChickenHoatAnhHawk.LOAI_DAN_MUI_TEN,
                this.trainingSession.trainingPlayerX,
                this.trainingSession.trainingPlayerY,
                ChickenHoatAnhHawk.GOC_BAY_LEN,
                ChickenHoatAnhHawk.LUC_HIEN_THI,
                ChickenHoatAnhHawk.LUC_HIEN_THI,
                ChickenHoatAnhHawk.SO_PHAT_MOT_LOAT,
                loatBayLen.getX(),
                loatBayLen.getY(),
                false,
                phienMayMan.powDaKichHoat()
        );
        System.out.println("[HAWK] BAY_LEN mode=training skillId=" + skillId
                + " soMuiTen=" + HAWK_SO_MUI_TEN
                + " image=/eff/muiten.png");

        TRAINING_BOT_EXECUTOR.schedule(
                () -> this.batDauLoatMuiTenHawkRoiXuongLuyenTap(
                        phien,
                        skillId,
                        botIndex,
                        danhSachMayMan,
                        phienMayMan
                ),
                ChickenHoatAnhHawk.THOI_GIAN_BAY_LEN_MS,
                TimeUnit.MILLISECONDS
        );
    }

    private synchronized void batDauLoatMuiTenHawkRoiXuongLuyenTap(
            long phien,
            long skillId,
            int botIndex,
            ChickenChienBinh[] danhSachMayMan,
            ChickenMayMan.PhienTanCong phienMayMan
    ) {
        if (!this.inTraining
                || phien != this.trainingSession.trainingSessionId
                || skillId != this.trainingSession.trainingHawkSkillId
                || !this.trainingSession.trainingHawkSkillActive
                || botIndex < 0
                || botIndex >= TRAINING_BOT_COUNT
                || this.trainingSession.trainingBotDead[botIndex]) {
            return;
        }

        try {
            short targetX = this.trainingSession.trainingBotX[botIndex];
            short targetY = (short)Math.max(
                    Short.MIN_VALUE,
                    this.trainingSession.trainingBotY[botIndex] - 18
            );
            ChickenHoatAnhHawk.DuongDan laoXuong =
                    ChickenHoatAnhHawk.taoDuongLaoXuong(targetX, targetY);
            ChickenHoatAnhHawk.LoatDuongDan loatLaoXuong =
                    ChickenHoatAnhHawk.taoLoatBonMuiNoiDuoi(laoXuong);
            this.dichVu.guiKetQuaBanLuyenTap(
                    TRAINING_PLAYER_INDEX,
                    ChickenHoatAnhHawk.LOAI_DAN_MUI_TEN,
                    this.trainingSession.trainingPlayerX,
                    this.trainingSession.trainingPlayerY,
                    ChickenHoatAnhHawk.GOC_LAO_XUONG,
                    ChickenHoatAnhHawk.LUC_HIEN_THI,
                    ChickenHoatAnhHawk.LUC_HIEN_THI,
                    ChickenHoatAnhHawk.SO_PHAT_MOT_LOAT,
                    loatLaoXuong.getX(),
                    loatLaoXuong.getY(),
                    false,
                    phienMayMan.powDaKichHoat()
            );
            System.out.println("[HAWK] LAO_XUONG mode=training skillId=" + skillId
                    + " target=" + (botIndex + 1)
                    + " image=/eff/muiten.png");

            long thoiDiemMuiCuoiCham =
                    ChickenHoatAnhHawk.THOI_GIAN_MUI_DAU_CHAM_MUC_TIEU_MS
                    + (HAWK_SO_MUI_TEN - 1L) * ChickenHoatAnhHawk.KHOANG_CACH_MUI_TEN_MS;
            TRAINING_BOT_EXECUTOR.schedule(
                    () -> this.xuLySatThuongHawkCongDonLuyenTap(
                            phien,
                            skillId,
                            botIndex,
                            danhSachMayMan,
                            phienMayMan
                    ),
                    thoiDiemMuiCuoiCham,
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception ex) {
            this.trainingSession.trainingHawkSkillActive = false;
            this.trainingSession.trainingWaitingShotEnd = false;
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Sau mũi thứ tư mới trừ một lần tổng sát thương của cả loạt. */
    private synchronized void xuLySatThuongHawkCongDonLuyenTap(
            long phien,
            long skillId,
            int botIndex,
            ChickenChienBinh[] danhSachMayMan,
            ChickenMayMan.PhienTanCong phienMayMan
    ) {
        if (!this.inTraining
                || phien != this.trainingSession.trainingSessionId
                || skillId != this.trainingSession.trainingHawkSkillId
                || !this.trainingSession.trainingHawkSkillActive
                || botIndex < 0
                || botIndex >= TRAINING_BOT_COUNT
                || this.trainingSession.trainingBotDead[botIndex]) {
            return;
        }

        try {
            int tamNoX = this.trainingSession.trainingBotX[botIndex];
            int tamNoY = this.trainingSession.trainingBotY[botIndex] - 18;
            int tanCong = Math.max(1, this.layTongTanCongHienTai());
            for (int biAnhHuong = 0;
                    biAnhHuong < TRAINING_BOT_COUNT;
                    biAnhHuong++) {
                if (this.trainingSession.trainingBotDead[biAnhHuong]
                        || this.trainingSession.trainingBotHp[biAnhHuong] <= 0) {
                    continue;
                }
                int satThuong = ChickenSatThuongLanKyNang.tinhHawk(
                        tanCong,
                        0,
                        tamNoX,
                        tamNoY,
                        this.trainingSession.trainingBotX[biAnhHuong],
                        this.trainingSession.trainingBotY[biAnhHuong],
                        true,
                        this.trainingSession.trainingMap
                );
                if (satThuong <= 0) {
                    continue;
                }
                satThuong = phienMayMan.apDung(
                        danhSachMayMan[biAnhHuong + 1], satThuong);
                this.trainingSession.trainingBotHp[biAnhHuong] = Math.max(
                        0,
                        this.trainingSession.trainingBotHp[biAnhHuong] - satThuong
                );
                this.trainingSession.trainingDummyHp =
                        this.trainingSession.trainingBotHp[biAnhHuong];
                boolean chet = this.trainingSession.trainingBotHp[biAnhHuong] <= 0;
                if (chet) {
                    this.trainingSession.trainingBotDead[biAnhHuong] = true;
                }
                this.dichVu.guiCapNhatMauLuyenTap(
                        (byte) (biAnhHuong + 1),
                        this.trainingSession.trainingBotHp[biAnhHuong],
                        this.trainingSession.trainingBossMaxHp,
                        chet ? (byte) 2 : (byte) 0
                );
                System.out.println("[HAWK] DAME_NO_LAN mode=training soMui="
                        + HAWK_SO_MUI_TEN
                        + " target=" + (biAnhHuong + 1)
                        + " damage=" + satThuong
                        + " hpCon="
                        + this.trainingSession.trainingBotHp[biAnhHuong]);
            }

            this.trainingSession.trainingHawkSkillActive = false;
            this.trainingSession.trainingWaitingShotEnd = false;
            if (this.layTongBossHpConSong() <= 0) {
                this.trainingSession.trainingBossState = TrainingBossState.DEAD;
                this.xuLyNguoiChoiThangLuyenTap();
                return;
            }
            this.chuyenLuotTheoNapDan(TRAINING_PLAYER_INDEX);
        } catch (Exception ex) {
            this.trainingSession.trainingHawkSkillActive = false;
            this.trainingSession.trainingWaitingShotEnd = false;
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Nhận action 3 của client Thor và tạo bốn tia sét quanh bản thân. */
    private synchronized void xuLyCmd91ThorLuyenTap(ChickenTinNhan ms) throws IOException {
        if (!this.inTraining || ms == null || this.avenger != AVG_THOR) {
            System.out.println("[THOR] BO_QUA_LUYEN_TAP inTraining="
                    + this.inTraining + " avenger=" + this.avenger);
            return;
        }

        int soByte = ms.boDoc().available();
        if (soByte < 2) {
            System.out.println("[THOR] PACKET_THIEU mode=training bytes=" + soByte);
            return;
        }

        int action = ms.boDoc().readUnsignedByte();
        int selfIndex = ms.boDoc().readUnsignedByte();
        System.out.println("[THOR] DOC_PACKET mode=training action="
                + action + " selfIndex=" + selfIndex);

        if (action != 3 || selfIndex != (TRAINING_PLAYER_INDEX & 0xFF)) {
            System.out.println("[THOR] PACKET_KHONG_HOP_LE action="
                    + action + " selfIndex=" + selfIndex);
            return;
        }
        if (this.trainingSession.trainingCurrentTurn != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingThorMenuTurnId
                    != this.trainingSession.trainingTurnId
                || this.trainingSession.trainingWaitingShotEnd
                || this.trainingSession.trainingBotAnimating
                || this.trainingSession.trainingThorSkillActive
                || this.trainingSession.trainingBossState == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END) {
            System.out.println("[THOR] SAI_TRANG_THAI_LUYEN_TAP turn="
                    + this.trainingSession.trainingCurrentTurn
                    + " waiting=" + this.trainingSession.trainingWaitingShotEnd
                    + " active=" + this.trainingSession.trainingThorSkillActive);
            return;
        }

        final long phien = this.trainingSession.trainingSessionId;
        final long skillId = ++this.trainingSession.trainingThorSkillId;
        this.trainingSession.trainingThorSkillActive = true;
        this.trainingSession.trainingWaitingShotEnd = true;
        this.trainingSession.trainingLastShotTurnId = this.trainingSession.trainingTurnId;

        short[] cacX = ChickenKyNangDacBietThor.taoBonViTriX(
                this.trainingSession.trainingPlayerX);
        short[] cacY = ChickenKyNangDacBietThor.taoBonDiemVaChamY(
                this.trainingSession.trainingMap,
                cacX,
                this.trainingSession.trainingPlayerY);
        ChickenChienBinh[] danhSachMayMan =
                this.taoDanhSachMayManLuyenTap();
        ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(danhSachMayMan[0], danhSachMayMan);
        this.dichVu.guiTiaSetThor(
                TRAINING_PLAYER_INDEX,
                THOR_LOAI_HIEU_UNG_SET,
                cacX,
                cacY
        );
        System.out.println("[THOR] PHAT_SET mode=training skillId=" + skillId
                + " x=" + cacX[0] + "," + cacX[1] + "," + cacX[2] + "," + cacX[3]);

        this.gaySatThuongThorLuyenTap(
                cacX, cacY, danhSachMayMan, phienMayMan);
        if (this.trainingSession.trainingBossState == TrainingBossState.ROUND_END) {
            return;
        }

        // Phải cập nhật mặt nạ map của server sau từng lần sét đánh.
        // Lần dùng kế tiếp sẽ dò đúng lớp địa hình mới nằm thấp hơn.
        ChickenKyNangDacBietThor.phaDiaHinhTheoTungTia(
                this.trainingSession.trainingMap, cacX, cacY);
        this.capNhatToaDoTheoNenMoiSauPhaDiaHinhLuyenTap();

        TRAINING_BOT_EXECUTOR.schedule(
                () -> this.ketThucKyNangThorLuyenTap(phien, skillId),
                THOR_THOI_GIAN_HIEU_UNG_MS,
                TimeUnit.MILLISECONDS
        );
    }

    /** Mỗi vị trí sét tính va chạm và sát thương riêng; Thor không nằm trong vòng lặp này. */
    private void gaySatThuongThorLuyenTap(
            short[] cacX,
            short[] cacY,
            ChickenChienBinh[] danhSachMayMan,
            ChickenMayMan.PhienTanCong phienMayMan
    ) throws IOException {
        int tanCong = Math.max(1, this.layTongTanCongHienTai());
        for (int botIndex = 0; botIndex < TRAINING_BOT_COUNT; botIndex++) {
            if (this.trainingSession.trainingBotDead[botIndex]
                    || this.trainingSession.trainingBotHp[botIndex] <= 0) {
                continue;
            }

            int tongSatThuong = ChickenSatThuongLanKyNang.tinhThor(
                    tanCong,
                    0,
                    cacX,
                    cacY,
                    this.trainingSession.trainingBotX[botIndex],
                    this.trainingSession.trainingBotY[botIndex],
                    true,
                    this.trainingSession.trainingMap
            );
            if (tongSatThuong <= 0) {
                continue;
            }
            tongSatThuong = phienMayMan.apDung(
                    danhSachMayMan[botIndex + 1], tongSatThuong);
            this.trainingSession.trainingBotHp[botIndex] = Math.max(
                    0,
                    this.trainingSession.trainingBotHp[botIndex] - tongSatThuong
            );
            this.trainingSession.trainingDummyHp = this.trainingSession.trainingBotHp[botIndex];
            boolean chet = this.trainingSession.trainingBotHp[botIndex] <= 0;
            if (chet) {
                this.trainingSession.trainingBotDead[botIndex] = true;
            }
            this.dichVu.guiCapNhatMauLuyenTap(
                    (byte)(botIndex + 1),
                    this.trainingSession.trainingBotHp[botIndex],
                    this.trainingSession.trainingBossMaxHp,
                    chet ? (byte)2 : (byte)0
            );
            System.out.println("[THOR] NO_LAN_SET mode=training target="
                    + (botIndex + 1)
                    + " damage=" + tongSatThuong
                    + " hpCon=" + this.trainingSession.trainingBotHp[botIndex]);
        }

        if (this.layTongBossHpConSong() <= 0) {
            this.trainingSession.trainingThorSkillActive = false;
            this.trainingSession.trainingWaitingShotEnd = false;
            this.trainingSession.trainingBossState = TrainingBossState.DEAD;
            this.xuLyNguoiChoiThangLuyenTap();
        }
    }

    private synchronized void ketThucKyNangThorLuyenTap(long phien, long skillId) {
        if (!this.inTraining
                || phien != this.trainingSession.trainingSessionId
                || skillId != this.trainingSession.trainingThorSkillId
                || !this.trainingSession.trainingThorSkillActive) {
            return;
        }

        this.trainingSession.trainingThorSkillActive = false;
        this.trainingSession.trainingWaitingShotEnd = false;
        try {
            this.guiDongBoToaDoSauPhaDiaHinhLuyenTap();
            this.chuyenLuotTheoNapDan(TRAINING_PLAYER_INDEX);
        } catch (IOException ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized void scheduleTrainingBotShot(long delayMs) {
        if (!this.inTraining || this.trainingSession.trainingCurrentTurn != TRAINING_BOSS_INDEX
                || this.trainingSession.trainingBossState == TrainingBossState.DEAD || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END) {
            return;
        }
        this.cancelTrainingBotTask();
        final long phien = this.trainingSession.trainingSessionId;
        this.trainingSession.trainingBotTask = TRAINING_BOT_EXECUTOR.schedule(() -> this.runTrainingBotTurn(phien), delayMs, TimeUnit.MILLISECONDS);
    }

    private synchronized void scheduleTrainingBotAction(long phien, Runnable action, long delayMs) {
        if (!this.inTraining || phien != this.trainingSession.trainingSessionId) {
            return;
        }
        this.cancelTrainingBotTask();
        this.trainingSession.trainingBotTask = TRAINING_BOT_EXECUTOR.schedule(action, delayMs, TimeUnit.MILLISECONDS);
    }

    private synchronized void cancelTrainingBotTask() {
        if (this.trainingSession.trainingBotTask != null) {
            this.trainingSession.trainingBotTask.cancel(false);
            this.trainingSession.trainingBotTask = null;
        }
    }

    private synchronized void huyLichHetGioLuotNguoiChoiLuyenTap() {
        if (this.trainingSession.trainingPlayerTurnTimeoutTask != null) {
            this.trainingSession.trainingPlayerTurnTimeoutTask.cancel(false);
            this.trainingSession.trainingPlayerTurnTimeoutTask = null;
        }
    }

    private synchronized void lapLichHetGioLuotNguoiChoiLuyenTap() {
        this.huyLichHetGioLuotNguoiChoiLuyenTap();
        if (!this.inTraining
                || this.trainingSession.trainingCurrentTurn
                    != TRAINING_PLAYER_INDEX
                || this.trainingSession.trainingBossState
                    == TrainingBossState.DEAD
                || this.trainingSession.trainingBossState
                    == TrainingBossState.ROUND_END) {
            return;
        }
        final long phien = this.trainingSession.trainingSessionId;
        final long maLuot = this.trainingSession.trainingTurnId;
        this.trainingSession.trainingPlayerTurnTimeoutTask =
                TRAINING_BOT_EXECUTOR.schedule(
                        () -> this.xuLyHetGioLuotNguoiChoiLuyenTap(
                                phien, maLuot),
                        TRAINING_PLAYER_TURN_TIMEOUT_MS,
                        TimeUnit.MILLISECONDS);
    }

    private void xuLyHetGioLuotNguoiChoiLuyenTap(
            long phien,
            long maLuot
    ) {
        synchronized (this) {
            this.trainingSession.trainingPlayerTurnTimeoutTask = null;
            if (!this.inTraining
                    || phien != this.trainingSession.trainingSessionId
                    || maLuot != this.trainingSession.trainingTurnId
                    || this.trainingSession.trainingCurrentTurn
                        != TRAINING_PLAYER_INDEX
                    || this.trainingSession.trainingBossState
                        == TrainingBossState.DEAD
                    || this.trainingSession.trainingBossState
                        == TrainingBossState.ROUND_END) {
                return;
            }
            try {
                this.ketThucLuotLuyenTapKhongTaoDan(true);
            } catch (IOException ex) {
                Logger.getLogger(ChickenNguoiChoi.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }

    private synchronized void dungVongBotLuyenTap() {
        this.cancelTrainingBotTask();
        this.huyLichHetGioLuotNguoiChoiLuyenTap();
        if (this.trainingSession.trainingBotReturnTask != null) {
            this.trainingSession.trainingBotReturnTask.cancel(false);
            this.trainingSession.trainingBotReturnTask = null;
        }
        if (this.trainingSession.trainingPlayerResolveTask != null) {
            this.trainingSession.trainingPlayerResolveTask.cancel(false);
            this.trainingSession.trainingPlayerResolveTask = null;
        }
        if (this.trainingSession.trainingGravityTask != null) {
            this.trainingSession.trainingGravityTask.cancel(false);
            this.trainingSession.trainingGravityTask = null;
        }
        if (this.trainingSession.trainingFallDeathTask != null) {
            this.trainingSession.trainingFallDeathTask.cancel(false);
            this.trainingSession.trainingFallDeathTask = null;
        }
        this.trainingSession.trainingBotAnimating = false;
        this.trainingSession.trainingActiveBotIndex = -1;
    }

    private void runTrainingBotTurn(long phien) {
        try {
            synchronized (this) {
                this.trainingSession.trainingBotTask = null;
                if (!this.inTraining || phien != this.trainingSession.trainingSessionId || this.trainingSession.trainingCurrentTurn != TRAINING_BOSS_INDEX
                        || this.trainingSession.trainingWaitingShotEnd || this.trainingSession.trainingBotAnimating
                        || this.trainingSession.trainingBossState == TrainingBossState.DEAD || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END) {
                    return;
                }
                int botIndex = this.botLuyenTapTiep();
                if (botIndex < 0) {
                    return;
                }
                this.trainingSession.trainingActiveBotIndex = botIndex;
                this.trainingSession.trainingBossPendingReloadAfterAction = -1;
                // Không xử chết boss chỉ vì nền ngay dưới chân vừa bị đục.
                // Trọng lực đã được xử lý ở cuối lượt trước: boss sẽ rơi xuống
                // mặt đất thấp hơn nếu còn nền, hoặc chỉ chết khi rơi khỏi map.
                this.trainingSession.trainingBotAnimating = true;
                int khoangCach = this.khoangCachBossNguoiChoi(botIndex);
                ChickenAiLuyenTap.HanhDongGan hanhDong =
                        ChickenAiLuyenTap.chonHanhDongGan(khoangCach);
                if (hanhDong
                        == ChickenAiLuyenTap.HanhDongGan.DICH_CHUYEN) {
                    this.trainingSession.trainingBossState =
                            TrainingBossState.TELEPORTING;
                    this.scheduleTrainingBotAction(
                            phien,
                            () -> this.runTrainingBossTeleport(
                                    phien, botIndex),
                            TRAINING_BOSS_AIM_DELAY_MS);
                } else {
                    this.scheduleTrainingBossAim(phien, botIndex);
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void scheduleTrainingBossAim(long phien, int botIndex) {
        this.trainingSession.trainingBossState = TrainingBossState.AIMING;
        this.scheduleTrainingBotAction(phien, () -> this.runTrainingBossShot(phien, botIndex), TRAINING_BOSS_AIM_DELAY_MS);
    }

    private void runTrainingBossTeleport(long phien, int botIndex) {
        try {
            synchronized (this) {
                this.trainingSession.trainingBotTask = null;
                if (!this.inTraining
                        || phien != this.trainingSession.trainingSessionId
                        || this.trainingSession.trainingCurrentTurn
                                != TRAINING_BOSS_INDEX
                        || this.trainingSession.trainingBossState
                                != TrainingBossState.TELEPORTING
                        || this.trainingSession.trainingBotDead[botIndex]) {
                    return;
                }
                ChickenKetQuaDan ketQua =
                        this.taoPhatDichChuyenBotLuyenTap(botIndex);
                if (ketQua == null) {
                    // Khong co diem dat an toan: bot ban sung thuong, khong
                    // tieu hao luot vao toa do ngoai map.
                    this.scheduleTrainingBossAim(phien, botIndex);
                    return;
                }

                this.trainingSession.trainingBossState =
                        TrainingBossState.SHOOTING;
                short bossXCu = this.trainingSession.trainingBotX[botIndex];
                short bossYCu = this.trainingSession.trainingBotY[botIndex];
                this.dichVu.guiDungVatPhamLuyenTap(
                        (byte) (botIndex + 1),
                        (byte) 1,
                        (short) TRAINING_BOSS_TELEPORT_ICON);
                this.dichVu.guiKetQuaBanLuyenTap(
                        (byte) (botIndex + 1),
                        ketQua.loaiDan,
                        bossXCu,
                        bossYCu,
                        ketQua.goc,
                        ketQua.luc,
                        ketQua.lucPhu,
                        (byte) 1,
                        ketQua.cacDuongX,
                        ketQua.cacDuongY,
                        false);

                int chiSoCuoi = ketQua.duongX.length - 1;
                short dichX = ketQua.duongX[chiSoCuoi];
                short dichY = ketQua.duongY[chiSoCuoi];
                this.trainingSession.trainingBotX[botIndex] = dichX;
                this.trainingSession.trainingBotY[botIndex] = dichY;
                this.trainingSession.trainingDummyX = dichX;
                this.trainingSession.trainingDummyY = dichY;
                this.trainingSession.trainingBossPendingReloadAfterAction =
                        TRAINING_BOSS_TELEPORT_RELOAD;
                ChickenQuanLyMayChu.log(
                        "[LUYEN_TAP][BOT_DICH_CHUYEN] playerId=" + this.ma
                        + " bot=" + botIndex
                        + " from=" + bossXCu + "," + bossYCu
                        + " to=" + dichX + "," + dichY
                        + " goc=" + ketQua.goc
                        + " luc=" + (ketQua.luc & 255));

                if (this.trainingSession.trainingBotReturnTask != null) {
                    this.trainingSession.trainingBotReturnTask.cancel(false);
                }
                this.trainingSession.trainingBotReturnTask =
                        TRAINING_BOT_EXECUTOR.schedule(
                                () -> this.finishTrainingBotShot(phien),
                                ChickenThoiGianHoatAnhDan.tinh(ketQua),
                                TimeUnit.MILLISECONDS);
            }
        } catch (Exception ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    private ChickenKetQuaDan taoPhatDichChuyenBotLuyenTap(int botIndex) {
        ChickenCongThucVatPhamChien.CauHinh cauHinh =
                ChickenCongThucVatPhamChien.theoIdVatPham(221);
        if (cauHinh == null) {
            return null;
        }
        short bossX = this.trainingSession.trainingBotX[botIndex];
        short bossY = this.trainingSession.trainingBotY[botIndex];

        ChickenChienBinh bot = new ChickenChienBinh(
                (byte) (botIndex + 1),
                -9999 - botIndex,
                bossX,
                bossY,
                this.layTenBossLuyenTap(botIndex),
                this.trainingSession.trainingBossWeaponPart,
                this.trainingSession.trainingBossMaxHp,
                this.trainingSession.trainingBossAttack,
                0);
        bot.hp = this.trainingSession.trainingBotHp[botIndex];
        ChickenQuanLyCongThucSung.KiemTraBanDo banDo =
                this.trainingSession.trainingMap;
        byte windX = ChickenHeThongGio.layWindXChoItem(
                this.trainingSession.trainingWind, 221);
        byte windY = ChickenHeThongGio.layWindYChoItem(
                this.trainingSession.trainingWind, 221);

        // Ban bua: goc va luc khong phu thuoc toa do nguoi choi. Server chi
        // loai diem dap ngoai map/khong co nen de bot khong tu chet vi AI.
        for (int lan = 0; lan < 40; lan++) {
            short goc = (short) java.util.concurrent.ThreadLocalRandom
                    .current().nextInt(15, 166);
            byte luc = (byte) java.util.concurrent.ThreadLocalRandom.current()
                    .nextInt(10, 31);
            int huong = goc <= 90 ? 1 : -1;
            short dauNongX = (short) (bossX + huong * 12);
            short dauNongY = (short) (bossY - 24);
            ChickenKetQuaDan ketQua = ChickenPhatBanVatPhamServer.tao(
                    bot,
                    dauNongX,
                    dauNongY,
                    goc,
                    luc,
                    cauHinh,
                    windX,
                    windY,
                    banDo,
                    new ChickenChienBinh[0],
                    (shooter, target) -> false);
            if (ketQua == null || ketQua.duongX.length == 0) {
                continue;
            }
            int cuoi = ketQua.duongX.length - 1;
            short x = ketQua.duongX[cuoi];
            short y = ketQua.duongY[cuoi];
            if (Math.abs(x - bossX) < 24
                    || !this.viTriDungLuyenTapAnToan(x, y)) {
                continue;
            }
            return ketQua;
        }
        return null;
    }

    private void runTrainingBossShot(long phien, int botIndex) {
        try {
            synchronized (this) {
                this.trainingSession.trainingBotTask = null;
                if (!this.inTraining || phien != this.trainingSession.trainingSessionId || this.trainingSession.trainingCurrentTurn != TRAINING_BOSS_INDEX
                        || this.trainingSession.trainingBossState != TrainingBossState.AIMING || this.trainingSession.trainingBotDead[botIndex]) {
                    return;
                }
                this.trainingSession.trainingBossState = TrainingBossState.SHOOTING;
                this.trainingSession.trainingBossPendingReloadAfterAction = -1;
                long thoiGianHoatAnhMs =
                        this.botLuyenTapBanTra(botIndex);
                if (!this.inTraining || phien != this.trainingSession.trainingSessionId || this.trainingSession.trainingCurrentTurn != TRAINING_BOSS_INDEX
                        || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END) {
                    return;
                }
                if (this.trainingSession.trainingBotReturnTask != null) {
                    this.trainingSession.trainingBotReturnTask.cancel(false);
                }
                this.trainingSession.trainingBotReturnTask =
                        TRAINING_BOT_EXECUTOR.schedule(
                                () -> this.finishTrainingBotShot(phien),
                                thoiGianHoatAnhMs,
                                TimeUnit.MILLISECONDS);
            }
        }
        catch (Exception ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void finishTrainingBotShot(long phien) {
        try {
            synchronized (this) {
                this.trainingSession.trainingBotReturnTask = null;
                if (!this.inTraining || phien != this.trainingSession.trainingSessionId || this.trainingSession.trainingCurrentTurn != TRAINING_BOSS_INDEX) {
                    this.trainingSession.trainingBotAnimating = false;
                    return;
                }
                this.trainingSession.trainingBotAnimating = false;
                this.trainingSession.trainingActiveBotIndex = -1;
                this.trainingSession.trainingBossState = TrainingBossState.IDLE;
                int napDanSauHanhDong = this.trainingSession
                        .trainingBossPendingReloadAfterAction;
                this.trainingSession.trainingBossPendingReloadAfterAction = -1;

                // Packet dan cua bot giu Y luc bat dau ban. Sau khi hoat anh
                // ket thuc moi chot cac Y da thay doi do lo dia hinh.
                this.guiDongBoToaDoSauPhaDiaHinhLuyenTap();
                // Animation đã xong mới cập nhật nền/Y, tránh boss giật khi bắn.
                if (!this.capNhatTrongLucSauLuotLuyenTap(TRAINING_BOSS_INDEX)) {
                    return;
                }
                this.chuyenLuotTheoNapDanKhongCapNhatTrongLuc(
                        TRAINING_BOSS_INDEX,
                        napDanSauHanhDong
                );
            }
        }
        catch (Exception ex) {
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private long botLuyenTapBanTra(int botIndex) throws IOException {
        this.trainingSession.trainingBossShield = false;
        this.trainingSession.trainingBossPowerShot = false;
        DuLieuSung duLieuDanBoss = ChickenQuanLyDanSung.theoIdSung(
                this.trainingSession.trainingBossWeaponId);
        if (duLieuDanBoss == null
                || duLieuDanBoss.getPartSung()
                        != this.trainingSession.trainingBossWeaponPart
                || this.trainingSession.trainingBossAttack <= 0) {
            ChickenQuanLyMayChu.log(
                    "[LUYEN_TAP][SUNG_BOT_KHONG_HOP_LE] playerId=" + this.ma
                    + " idSung=" + this.trainingSession.trainingBossWeaponId
                    + " part=" + this.trainingSession.trainingBossWeaponPart);
            return TRAINING_BOSS_FALLBACK_SHOT_DELAY_MS;
        }

        /*
         * Packet 84 của client gán trực tiếp hai tọa độ này vào nhân vật bắn.
         * Vì vậy trước khi gửi packet phải ép boss xuống đúng nền hiện tại,
         * không được dùng Y còn sót lại từ lần spawn hoặc bước rơi trước.
         */
        short bossX = this.trainingSession.trainingBotX[botIndex];
        short bossYCu = this.trainingSession.trainingBotY[botIndex];
        short yNenTruocKhiBan = ChickenLuyenTapToaDo.timMatDatTaiHoacThapHon(
                this.trainingSession.trainingMap,
                bossX,
                bossYCu,
                this::thanNhanVatThongThoang
        );
        if (yNenTruocKhiBan != Short.MIN_VALUE
                && yNenTruocKhiBan >= bossYCu) {
            this.trainingSession.trainingBotY[botIndex] = yNenTruocKhiBan;
            this.trainingSession.trainingDummyX = bossX;
            this.trainingSession.trainingDummyY = yNenTruocKhiBan;
            if (yNenTruocKhiBan != bossYCu) {
                this.dichVu.guiCapNhatXYLuyenTap(
                        (byte) (botIndex + 1),
                        bossX,
                        yNenTruocKhiBan
                );
            }
        }
        short bossY = this.trainingSession.trainingBotY[botIndex];
        ChickenChienBinh boss = new ChickenChienBinh(
                (byte) (botIndex + 1),
                -9999 - botIndex,
                bossX,
                bossY,
                this.layTenBossLuyenTap(botIndex),
                this.trainingSession.trainingBossWeaponPart,
                this.trainingSession.trainingBossMaxHp,
                this.trainingSession.trainingBossAttack,
                0
        );
        boss.hp = this.trainingSession.trainingBotHp[botIndex];

        /*
         * Dùng chính snapshot chỉ số người chơi của server làm mục tiêu.
         * BossBanSung sẽ áp dụng hitbox người chơi, giáp thật, gió, trọng lực,
         * số viên và toàn bộ công thức đặc biệt của ID súng đã random.
         */
        ChickenChienBinh mucTieu = new ChickenChienBinh(
                this,
                TRAINING_PLAYER_INDEX,
                this.trainingSession.trainingPlayerX,
                this.trainingSession.trainingPlayerY
        );
        mucTieu.hp = this.trainingSession.trainingPlayerHp;
        mucTieu.mauToiDa = this.trainingSession.trainingPlayerMaxHp;
        byte windX = ChickenHeThongGio.layWindXChoSung(
                this.trainingSession.trainingWind,
                duLieuDanBoss.getIdSung());
        byte windY = ChickenHeThongGio.layWindYChoSung(
                this.trainingSession.trainingWind,
                duLieuDanBoss.getIdSung());
        ChickenKetQuaDan ketQua =
                BossBanSung.taoPhatBanTheoCongThucSung(
                        boss,
                        mucTieu,
                        new ChickenChienBinh[]{mucTieu},
                        duLieuDanBoss,
                        this.trainingSession.trainingMap,
                        windX,
                        windY,
                        true
                );
        if (ketQua == null
                || ketQua.cacDuongX.length == 0
                || ketQua.cacDuongY.length == 0) {
            ChickenQuanLyMayChu.log(
                    "[LUYEN_TAP][KHONG_TAO_DUOC_DAN_BOT] playerId=" + this.ma
                    + " idSung=" + duLieuDanBoss.getIdSung());
            return TRAINING_BOSS_FALLBACK_SHOT_DELAY_MS;
        }

        int soDuong = Math.min(
                ketQua.cacDuongX.length,
                ketQua.cacDuongY.length);
        for (int i = 0; i < soDuong; i++) {
            int loaiDanTaoLo =
                    ChickenLoaiDanPhaDiaHinhClient.layLoaiDanTaoLo(
                            ketQua.loaiDan, i);
            if (loaiDanTaoLo
                    != ChickenLoaiDanPhaDiaHinhClient.KHONG_PHA_DIA_HINH) {
                this.capNhatLoDiaHinhTheoDuongDanLuyenTap(
                        ketQua.cacDuongX[i], ketQua.cacDuongY[i], null,
                        (byte) loaiDanTaoLo);
            }
        }
        int tongSatThuongNguoiChoi = Math.max(
                0,
                ketQua.satThuongTheoMucTieu.getOrDefault(mucTieu, 0));
        ChickenMayMan.PhienTanCong phienMayMan =
                ChickenMayMan.batDau(
                        boss,
                        new ChickenChienBinh[]{mucTieu});
        tongSatThuongNguoiChoi =
                phienMayMan.apDung(mucTieu, tongSatThuongNguoiChoi);

        // Y đã được khóa từ trước khi tạo quỹ đạo; packet bắn dùng đúng
        // tọa độ nền hiện tại và không tự dịch lại đường đạn giữa animation.
        short bossXHienTai = bossX;
        short bossYHienTai = bossY;

        this.trainingSession.trainingDummyX = bossXHienTai;
        this.trainingSession.trainingDummyY = bossYHienTai;

        // Chốt máu ngay từ kết quả va chạm server trước khi gửi animation.
        // Như vậy hễ đường đạn boss đã đi vào đúng hitbox người chơi thì phát
        // bắn đó luôn trừ máu, không phụ thuộc các packet hiển thị phía sau.
        boolean nguoiChoiBiHa = false;
        if (tongSatThuongNguoiChoi > 0) {
            int hpTruoc = this.trainingSession.trainingPlayerHp;
            this.trainingSession.trainingPlayerHp = Math.max(
                    0,
                    this.trainingSession.trainingPlayerHp - tongSatThuongNguoiChoi
            );
            this.ghiNhanSatThuongPowLuyenTap(hpTruoc);
            nguoiChoiBiHa = this.trainingSession.trainingPlayerHp == 0;
        }

        this.dichVu.guiKetQuaBanLuyenTap(
                (byte) (botIndex + 1),
                ketQua.loaiDan,
                bossXHienTai,
                bossYHienTai,
                ketQua.goc,
                ketQua.luc,
                ketQua.lucPhu,
                (byte) 1,
                ketQua.cacDuongX,
                ketQua.cacDuongY,
                ketQua.sieuCao
        );
        ChickenQuanLyMayChu.log(
                "[LUYEN_TAP][BOT_BAN] playerId=" + this.ma
                + " idSung=" + duLieuDanBoss.getIdSung()
                + " tenSung=" + duLieuDanBoss.getTenSung()
                + " soDuong=" + soDuong
                + " damage=" + tongSatThuongNguoiChoi
                + " napDan=" + this.trainingSession.trainingBossReloadTime);
        this.dichVu.guiCapNhatXYLuyenTap(
                (byte) (botIndex + 1),
                bossXHienTai,
                bossYHienTai
        );
        if (tongSatThuongNguoiChoi <= 0) {
            return ChickenThoiGianHoatAnhDan.tinh(ketQua);
        }
        this.dichVu.guiCapNhatMauLuyenTap(
                TRAINING_PLAYER_INDEX,
                this.trainingSession.trainingPlayerHp,
                this.trainingSession.trainingPlayerMaxHp,
                nguoiChoiBiHa ? (byte) 2 : (byte) 0
        );
        if (nguoiChoiBiHa) {
            this.xuLyNguoiChoiThuaLuyenTap();
        }
        return ChickenThoiGianHoatAnhDan.tinh(ketQua);
    }

    private int botLuyenTapTiep() {
        for (int step = 1; step <= TRAINING_BOT_COUNT; step++) {
            int chiSo = (this.trainingSession.trainingBotTurn + step + TRAINING_BOT_COUNT) % TRAINING_BOT_COUNT;
            if (!this.trainingSession.trainingBotDead[chiSo]) {
                this.trainingSession.trainingBotTurn = chiSo;
                return chiSo;
            }
        }
        return -1;
    }

    private int khoangCachBossNguoiChoi(int botIndex) {
        return Math.abs(this.trainingSession.trainingBotX[botIndex] - this.trainingSession.trainingPlayerX);
    }

    /**
     * Tìm nền ổn định cho boss trên toàn bộ vùng hai chân, không chỉ đúng cột
     * X ở giữa. Địa hình bị khoét lệch 1-2 pixel vẫn có thể còn nền đỡ ở mép
     * chân, vì vậy không được chuyển boss sang trạng thái rơi vực ngay.
     */
    private short timNenOnDinhChoBossLuyenTap(short bossX, short bossY) {
        int[] lechX = new int[]{0, -4, 4, -8, 8, -12, 12};
        short nenTotNhat = Short.MIN_VALUE;
        int doRoiNhoNhat = Integer.MAX_VALUE;

        for (int dx : lechX) {
            short xKiemTra = this.kepShort(
                    bossX + dx,
                    0,
                    Math.max(0, this.trainingSession.trainingMap.getWidth() - 1)
            );
            short nen = ChickenLuyenTapToaDo.timMatDatTaiHoacThapHon(
                    this.trainingSession.trainingMap,
                    xKiemTra,
                    bossY,
                    this::thanNhanVatThongThoang
            );
            if (nen == Short.MIN_VALUE || nen < bossY) {
                continue;
            }

            int doRoi = nen - bossY;
            // Chỉ nhận nền gần dưới chân. Nền quá xa sẽ do vòng rơi xử lý.
            if (doRoi <= TRAINING_BOSS_MAX_GROUND_STEP
                    && doRoi < doRoiNhoNhat) {
                doRoiNhoNhat = doRoi;
                nenTotNhat = nen;
            }
        }
        return nenTotNhat;
    }

    /**
     * Cập nhật trọng lực cho người chơi và toàn bộ boss sau mỗi lượt.
     *
     * @return true nếu trận vẫn tiếp tục; false nếu một bên đã rơi khỏi map
     *         và quy trình thắng/thua đã được xử lý.
     */
    private boolean capNhatTrongLucSauLuotLuyenTap(byte benVuaBan) throws IOException {
        if (!this.inTraining) {
            return false;
        }

        if (!this.isFlyAvenger()
                && !this.coNenDoDuoiChanLuyenTap(
                        this.trainingSession.trainingPlayerX,
                        this.trainingSession.trainingPlayerY)) {
            short matDatThapHon = this.timMatDatThapHonLuyenTap(
                    this.trainingSession.trainingPlayerX, this.trainingSession.trainingPlayerY);
            if (matDatThapHon == Short.MIN_VALUE) {
                this.xuLyNguoiChoiRoiMapLuyenTap();
                return false;
            }
            if (matDatThapHon != this.trainingSession.trainingPlayerY) {
                this.trainingSession.trainingPlayerY = matDatThapHon;
                this.dichVu.guiCapNhatXYLuyenTap(
                        TRAINING_PLAYER_INDEX,
                        this.trainingSession.trainingPlayerX,
                        this.trainingSession.trainingPlayerY
                );
            }
        }

        for (int botIndex = 0; botIndex < TRAINING_BOT_COUNT; botIndex++) {
            if (this.trainingSession.trainingBotDead[botIndex]) {
                continue;
            }
            short bossX = this.trainingSession.trainingBotX[botIndex];
            short bossY = this.trainingSession.trainingBotY[botIndex];

            /*
             * Kiểm tra nền trên toàn vùng hai chân. Cột giữa có thể vừa bị phá
             * nhưng mép chân vẫn đứng được, nên không được cho boss rơi ngay.
             */
            short matDatMoi = this.timNenOnDinhChoBossLuyenTap(bossX, bossY);
            int dayMap = this.trainingSession.trainingMap.getHeight();
            int yTiepTheo;

            if (matDatMoi != Short.MIN_VALUE) {
                yTiepTheo = matDatMoi;
            } else if (this.viTriBossDangDungHopLe(botIndex)) {
                // Vẫn còn ít nhất hai điểm nền đỡ quanh chân: giữ nguyên Y.
                yTiepTheo = bossY;
                matDatMoi = bossY;
            } else {
                // Chỉ rơi khi toàn bộ vùng chân thật sự không còn nền.
                yTiepTheo = bossY + 12;
            }

            short yBossMoi = this.kepShort(
                    yTiepTheo, Short.MIN_VALUE, Short.MAX_VALUE);
            if (yBossMoi != bossY) {
                this.trainingSession.trainingBotY[botIndex] = yBossMoi;
                this.trainingSession.trainingDummyX = bossX;
                this.trainingSession.trainingDummyY = yBossMoi;
                this.dichVu.guiCapNhatXYLuyenTap(
                        (byte) (botIndex + 1), bossX, yBossMoi);
            }

            if (matDatMoi == Short.MIN_VALUE && yTiepTheo > dayMap + 24) {
                this.trainingSession.trainingBotHp[botIndex] = 0;
                this.trainingSession.trainingDummyHp = 0;
                this.trainingSession.trainingBotDead[botIndex] = true;
                this.trainingSession.trainingBossState = TrainingBossState.DEAD;
                this.dichVu.guiCapNhatMauLuyenTap(
                        (byte) (botIndex + 1), 0,
                        this.trainingSession.trainingBossMaxHp, (byte) 2);
                this.xuLyNguoiChoiThangLuyenTap();
                return false;
            }

            if (matDatMoi == Short.MIN_VALUE) {
                this.lapLichTrongLucLuyenTap(benVuaBan);
                return false;
            }
        }
        return true;
    }

    private synchronized void lapLichTrongLucLuyenTap(byte benVuaBan) {
        if (this.trainingSession.trainingGravityTask != null) {
            this.trainingSession.trainingGravityTask.cancel(false);
        }
        final long phien = this.trainingSession.trainingSessionId;
        this.trainingSession.trainingGravityTask = TRAINING_BOT_EXECUTOR.schedule(() -> {
            synchronized (this) {
                try {
                    this.trainingSession.trainingGravityTask = null;
                    if (!this.inTraining || phien != this.trainingSession.trainingSessionId) {
                        return;
                    }
                    this.chuyenLuotTheoNapDan(benVuaBan);
                }
                catch (Exception ex) {
                    Logger.getLogger(ChickenNguoiChoi.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        }, 70L, TimeUnit.MILLISECONDS);
    }

    /**
     * Chỉ coi nhân vật còn đứng khi cả chân trái và chân phải đều có nền sát
     * bên dưới. Một pixel map lẻ ở giữa hoặc mép lỗ không giữ boss lơ lửng.
     */
    private boolean coNenDoDuoiChanLuyenTap(short x, short footY) {
        return ChickenLuyenTapToaDo.coNenDoDuoiHaiChan(
                this.trainingSession.trainingMap, x, footY);
    }

    /**
     * Tìm đúng hàng nền thấp hơn gần nhất đủ đỡ cả hai chân. Phần tọa độ được
     * tách riêng trong ChickenLuyenTapToaDo.
     */
    private short timMatDatThapHonLuyenTap(short x, short footY) {
        return ChickenLuyenTapToaDo.timMatDatThapHon(
                this.trainingSession.trainingMap,
                x,
                footY,
                this::thanNhanVatThongThoang
        );
    }

    private boolean viTriBossDangDungHopLe(int botIndex) {
        short bossX = this.trainingSession.trainingBotX[botIndex];
        short bossY = this.trainingSession.trainingBotY[botIndex];

        // Khi boss đang đứng, không dùng điều kiện di bộ quá chặt. Một lỗ nhỏ
        // ngay dưới tâm chân từng làm tinhYDiBoLuyenTap() trả về không hợp lệ
        // và boss bị xử chết dù hai mép chân vẫn còn nền đỡ.
        int soDiemCoNenDo = 0;
        int[] lechChan = new int[]{-14, -9, -4, 4, 9, 14};
        for (int lechX : lechChan) {
            short matDat = this.timMatDatTaiX(
                    this.kepShort((short)(bossX + lechX), 0, this.trainingSession.trainingMap.getWidth()),
                    (short)Math.max(0, bossY - 12)
            );
            if (matDat != Short.MIN_VALUE
                    && matDat >= bossY
                    && matDat - bossY <= 4) {
                soDiemCoNenDo++;
            }
        }

        // Chỉ cần còn ít nhất hai điểm đỡ ở vùng hai chân thì boss vẫn đứng.
        // Boss chỉ bị tính rơi/chết khi phần lớn vùng chân đã mất nền thật sự.
        return soDiemCoNenDo >= 2 && this.thanNhanVatThongThoang(bossX, bossY);
    }

    private boolean viTriDungLuyenTapAnToan(short x, short footY) {
        if (x < 40 || x > this.trainingSession.trainingMap.getWidth() - 40 || footY < 0 || footY >= this.trainingSession.trainingMap.getHeight()) {
            return false;
        }
        short giua = this.timMatDatTaiX(x, (short)Math.max(0, footY - 32));
        short trai = this.timMatDatTaiX((short)(x - 9), (short)Math.max(0, footY - 32));
        short phai = this.timMatDatTaiX((short)(x + 9), (short)Math.max(0, footY - 32));
        if (giua == Short.MIN_VALUE || Math.abs(giua - footY) > 4) {
            return false;
        }
        boolean coChanTrai = trai != Short.MIN_VALUE && Math.abs(trai - giua) <= 8;
        boolean coChanPhai = phai != Short.MIN_VALUE && Math.abs(phai - giua) <= 8;
        return coChanTrai && coChanPhai && this.thanNhanVatThongThoang(x, giua);
    }

    private boolean thanNhanVatThongThoang(short x, short footY) {
        int[] dxs = new int[]{-10, 0, 10};
        int[] dys = new int[]{-8, -22, -36, -48};
        for (int dx : dxs) {
            for (int dy : dys) {
                if (this.laNenLuyenTap(x + dx, footY + dy)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void bossLuyenTapDungVatPhamNeuCan(int botIndex) throws IOException {
        this.trainingSession.trainingBotTurnCount++;
        if (this.trainingSession.trainingBotHp[botIndex] <= this.trainingSession.trainingBossMaxHp / 3 && this.trainingSession.trainingBotTurnCount % 2 == 1) {
            this.trainingSession.trainingBotHp[botIndex] =
                    ChickenCuuThuongDongDoi.tinhMauSauCuuThuong(
                            this.trainingSession.trainingBotHp[botIndex],
                            this.trainingSession.trainingBossMaxHp);
            this.trainingSession.trainingDummyHp = this.trainingSession.trainingBotHp[botIndex];
            this.dichVu.guiDungVatPhamLuyenTap((byte)(botIndex + 1), (byte)10, (short)0);
            this.dichVu.guiCapNhatMauLuyenTap((byte)(botIndex + 1), this.trainingSession.trainingBotHp[botIndex], this.trainingSession.trainingBossMaxHp, (byte)0);
            return;
        }
        if (this.trainingSession.trainingBotTurnCount % 3 == 0) {
            this.trainingSession.trainingBossShield = true;
            this.dichVu.guiDungVatPhamLuyenTap((byte)(botIndex + 1), (byte)0, (short)0);
            return;
        }
        if (this.trainingSession.trainingBotTurnCount % 2 == 0) {
            this.trainingSession.trainingBossPowerShot = true;
            this.dichVu.guiDungVatPhamLuyenTap((byte)(botIndex + 1), (byte)5, (short)0);
        }
    }

    private short gocToiMucTieu(short batDauX, short batDauY, short targetX, short targetY) {
        double radians = Math.atan2(batDauY - targetY, targetX - batDauX);
        int degrees = (int)Math.round(Math.toDegrees(radians));
        if (degrees < 0) {
            degrees += 360;
        }
        return (short)degrees;
    }

    private synchronized void resetTrainingRoundState(boolean tangBossHp) {
        this.trainingSession.trainingTerrainPositionSyncPending = false;
        if (tangBossHp) {
            this.trainingSession.trainingWins++;
            this.trainingSession.trainingBossMaxHp = this.tinhMauToiDaBoss();
        }
        this.trainingSession.trainingMap.setMapId(TRAINING_MAP_ID);
        this.trainingSession.trainingPlayerMaxHp = this.tinhMauToiDaNguoiChoiLuyenTap();
        this.trainingSession.trainingPlayerHp = this.trainingSession.trainingPlayerMaxHp;
        this.trainingSession.trainingMoveRemaining =
                this.layQuangDuongDiChuyenLuyenTapToiDa();
        this.xoaTrangThaiPhatBanNguoiChoi();
        ChickenQuanLyPhienLuyenTap.datTrangThaiChoVongMoi(
                this.trainingSession,
                TRAINING_PLAYER_INDEX
        );
        short[] viTriNguoiChoi = this.timViTriXuatHienAnToan(this.trainingSession.trainingMap.laySinhX(0), this.trainingSession.trainingMap.laySinhY(0));
        short[] viTriBoss = this.timViTriXuatHienAnToan(this.trainingSession.trainingMap.laySinhX(1), this.trainingSession.trainingMap.laySinhY(1));
        this.trainingSession.trainingPlayerX = viTriNguoiChoi[0];
        this.trainingSession.trainingPlayerY = viTriNguoiChoi[1];
        this.trainingSession.trainingDummyX = viTriBoss[0];
        this.trainingSession.trainingDummyY = viTriBoss[1];
        this.trainingSession.trainingDummyHp = this.trainingSession.trainingBossMaxHp;
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            this.trainingSession.trainingBotHp[i] = this.trainingSession.trainingBossMaxHp;
            this.trainingSession.trainingBotDead[i] = false;
            this.trainingSession.trainingBotX[i] = this.trainingSession.trainingDummyX;
            this.trainingSession.trainingBotY[i] = this.trainingSession.trainingDummyY;
        }
    }

    private short[] timViTriXuatHienAnToan(short goiYX, short goiYY) {
        int[] offsets = new int[]{0, 24, -24, 48, -48, 72, -72, 96, -96, 120, -120, 160, -160, 200, -200};
        for (int offset : offsets) {
            short x = this.kepShort((short)(goiYX + offset), 40, this.trainingSession.trainingMap.getWidth() - 40);
            short y = this.timMatDatTaiX(x, (short)Math.max(0, goiYY - 100));
            if (y != Short.MIN_VALUE && this.viTriDungLuyenTapAnToan(x, y)) {
                return new short[]{x, y};
            }
        }
        for (int x = 40; x <= this.trainingSession.trainingMap.getWidth() - 40; x += 16) {
            short y = this.timMatDatTaiX((short)x, (short)Math.max(0, goiYY - 140));
            if (y != Short.MIN_VALUE && this.viTriDungLuyenTapAnToan((short)x, y)) {
                return new short[]{(short)x, y};
            }
        }
        throw new IllegalStateException("Không tìm thấy vị trí xuất hiện an toàn trên map luyện tập.");
    }

    private short tinhYDiBoLuyenTap(short x, short goiY) {
        // Chỉ tìm nền từ chân hiện tại trở xuống. Trước đây quét từ Y-48 có
        // thể bắt trúng mảnh map nằm phía trên và kéo boss bay lên trước khi bắn.
        short groundY = this.timMatDatTaiXTuYTroXuong(x, goiY);
        if (groundY == Short.MIN_VALUE
                || groundY < goiY
                || groundY - goiY > TRAINING_BOSS_MAX_GROUND_STEP
                || !this.viTriDungLuyenTapAnToan(x, groundY)) {
            return Short.MIN_VALUE;
        }
        return groundY;
    }

    private short timMatDatTaiXTuYTroXuong(short x, short batDauY) {
        int clampedX = Math.max(0, Math.min(this.trainingSession.trainingMap.getWidth() - 1, x));
        int start = Math.max(0, batDauY);
        for (int py = start; py < this.trainingSession.trainingMap.getHeight(); py++) {
            if (this.laNenLuyenTap(clampedX, py)) {
                return (short) py;
            }
        }
        return Short.MIN_VALUE;
    }

    private short timMatDatTaiX(short x, short goiY) {
        int clampedX = Math.max(0, Math.min(this.trainingSession.trainingMap.getWidth(), x));
        int start = Math.max(0, goiY - 32);
        for (int py = start; py < this.trainingSession.trainingMap.getHeight(); py++) {
            if (this.laNenLuyenTap(clampedX, py)) {
                return (short)py;
            }
        }
        return Short.MIN_VALUE;
    }

    private short timMatDatLuyenTap(short x, short goiY) {
        short giua = this.timMatDatTaiX(x, goiY);
        if (giua != Short.MIN_VALUE) {
            return giua;
        }
        short trai = this.timMatDatTaiX((short)(x - 10), goiY);
        if (trai != Short.MIN_VALUE) {
            return trai;
        }
        return this.timMatDatTaiX((short)(x + 10), goiY);
    }

    private boolean laNenLuyenTap(int x, int y) {
        if (x < 0 || y < 0 || x > this.trainingSession.trainingMap.getWidth() || y > this.trainingSession.trainingMap.getHeight()) {
            return false;
        }
        return this.trainingSession.trainingMap.coVaCham((short)x, (short)y);
    }

    private boolean daRoiKhoiMapLuyenTap(short x, short y) {
        if (this.isFlyAvenger()) {
            return false;
        }
        int rong = this.trainingSession.trainingMap.getWidth();
        int cao = this.trainingSession.trainingMap.getHeight();
        if (x < -40 || x > rong + 40 || y < -120 || y >= cao - 2) {
            return true;
        }
        if (y < 0) {
            return false;
        }
        short matDat = this.timMatDatLuyenTap(this.kepShort(x, 0, rong), y);
        return matDat == Short.MIN_VALUE && y > cao / 2;
    }

    private int layTongBossHpConSong() {
        int tong = 0;
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            if (!this.trainingSession.trainingBotDead[i]) {
                tong += this.trainingSession.trainingBotHp[i];
            }
        }
        return tong;
    }

    private void guiKhoiTaoLaiVongLuyenTap() throws IOException {
        this.dichVu.guiDatLaiHoLuyenTap();
        this.dichVu.guiCapNhatXYLuyenTap(TRAINING_PLAYER_INDEX, this.trainingSession.trainingPlayerX, this.trainingSession.trainingPlayerY);
        this.dichVu.guiCapNhatMauLuyenTap(TRAINING_PLAYER_INDEX, this.trainingSession.trainingPlayerHp, this.trainingSession.trainingPlayerMaxHp, (byte)0);
        for (int i = 0; i < TRAINING_BOT_COUNT; i++) {
            this.dichVu.guiCapNhatXYLuyenTap((byte)(i + 1), this.trainingSession.trainingBotX[i], this.trainingSession.trainingBotY[i]);
            this.dichVu.guiCapNhatMauLuyenTap((byte)(i + 1), this.trainingSession.trainingBotHp[i], this.trainingSession.trainingBossMaxHp, (byte)0);
        }
        this.guiTrangThaiLuotLuyenTap();
        this.lapLichHetGioLuotNguoiChoiLuyenTap();
    }

    private void xuLyNguoiChoiThangLuyenTap() throws IOException {
        if (!this.inTraining || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END) {
            return;
        }
        this.trainingSession.trainingBossState = TrainingBossState.ROUND_END;
        this.dungVongBotLuyenTap();
        /*
         * Đóng menu skill trước CMD 50. Nếu gửi sau CMD 50, client có thể
         * hiểu packet đóng menu là thao tác đóng luôn bảng kết quả.
         */
        this.dichVu.guiDongMenuKyNangDacBiet();
        ChickenKinhTeLuyenTap.KetQua ketQua =
                ChickenKinhTeLuyenTap.traoThuong(
                        this,
                        this.trainingSession.trainingRewardOperationKey,
                        ChickenQuanLyMayChu.trainingExpReward,
                        ChickenQuanLyMayChu.trainingGoldReward);
        if (!ketQua.thanhCong) {
            /*
             * Không công bố phần thưởng khi database chưa commit. Trạng thái
             * ROUND_END vẫn khóa action, tránh gửi lại đòn kết liễu để thử
             * nhân thưởng.
             */
            this.dichVu.guiKetThucDau((byte) 1, 0, 0, 0);
            this.dichVu.moHopThoaiOK(
                    "Không thể lưu phần thưởng luyện tập. "
                    + "EXP và vàng chưa bị thay đổi, hãy thử lại sau.");
            return;
        }

        this.trainingSession.trainingBossMaxHp =
                this.tinhMauToiDaBoss();
        /*
         * CMD 50 chỉ hiển thị kết quả; client không được tự cộng tài sản.
         * Gửi đúng delta đã commit để bảng Thắng khớp với database. Không gửi
         * CMD 3 ngay sau đó vì CMD 3 sẽ đóng bảng kết quả.
         */
        this.dichVu.guiKetThucDau(
                (byte) 1,
                ketQua.expThucTe,
                ketQua.vangThucTe,
                ketQua.ngocThucTe);
    }

    private int congSoNguyenAnToan(int giaTriHienTai, int giaTriCong) {
        long ketQua = (long)giaTriHienTai + Math.max(0, giaTriCong);
        return (int)Math.min(Integer.MAX_VALUE, Math.max(0L, ketQua));
    }

    private int nhanSoNguyenAnToan(int giaTri, int heSo) {
        long ketQua = (long)Math.max(0, giaTri) * Math.max(0, heSo);
        return (int)Math.min(Integer.MAX_VALUE, ketQua);
    }

    private void traVeSanhSauLuyenTap() {
        final long phienSauKhiRoi = this.trainingSession.trainingSessionId;
        TRAINING_BOT_EXECUTOR.schedule(() -> {
            synchronized (this) {
                this.thucHienTraVeSanhSauLuyenTap(phienSauKhiRoi);
            }
        }, TRAINING_RETURN_LOBBY_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Trả về sảnh ngay, chỉ dùng khi người chơi chủ động bấm Thoát rồi xác nhận OK.
     * Không chờ thời gian hiển thị kết quả thắng/thua.
     */
    private void traVeSanhSauLuyenTapNgay() {
        this.thucHienTraVeSanhSauLuyenTap(this.trainingSession.trainingSessionId);
    }

    /** Thực hiện chung thao tác đưa người chơi luyện tập về sảnh. */
    private void thucHienTraVeSanhSauLuyenTap(long phienSauKhiRoi) {
        try {
            if (this.inTraining || this.trainingSession.trainingSessionId != phienSauKhiRoi) {
                return;
            }
            this.isReady = false;
            this.chiSo = -1;
            this.pointSeat = 0;
            this.x = 100;
            this.y = 360;
            /*
             * CMD 3 làm client chuyển sang trạng thái chờ. Phải gửi nó trước
             * CMD -98 (vào khu RPG), vì CMD -98 của chính người chơi là gói
             * cuối cùng gọi GameScrRPG.show() và đóng vòng xoay. Thứ tự cũ
             * vào khu trước rồi mới CMD 3 khiến client kẹt màn hình chờ khi
             * thoát luyện tập.
             */
            // -42 trong tran la Balo snapshot. Phuc hoi Balo persistent truoc
            // khi client roi scene de khong mang sung ao ra sanh/tran sau.
            this.dichVu.guiBalo();
            this.dichVu.guiThongTin();
            if (this.zone == null || this.zoneId < 0) {
                ChickenBanDoRPG.vao(this);
            }
            this.trainingReturningToLobby = false;
        }
        catch (Exception ex) {
            this.trainingReturningToLobby = false;
            Logger.getLogger(ChickenNguoiChoi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void xuLyNguoiChoiThuaLuyenTap() throws IOException {
        if (!this.inTraining || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END) {
            return;
        }
        this.trainingSession.trainingBossState = TrainingBossState.ROUND_END;
        this.dungVongBotLuyenTap();

        /*
         * Client luyện tập đang diễn giải giá trị phe thắng ngược với chỉ số nhân vật.
         * Gửi giá trị 0 để client hiện đúng chữ "Thua", sau đó kết thúc luyện tập và trả người chơi về sảnh.
         */
        this.dichVu.guiKetThucDau((byte) 0, 0, 0, 0);
        this.trainingReturningToLobby = true;
        // CMD 50 chi mo bang ket qua. Giu nguyen scene cho den khi nguoi choi
        // bam OK; client se gui CMD 15 va thoatLuyenTapVeSanh() moi don phien.
    }

    private synchronized void xuLyNguoiChoiRoiMapLuyenTap() {
        if (!this.inTraining
                || this.trainingSession.trainingBossState == TrainingBossState.ROUND_END
                || this.trainingSession.trainingFallDeathTask != null) {
            return;
        }
        /*
         * Server da xac dinh cot hien tai khong con nen, nhung client can mot
         * khoang ngan de ve nhan vat roi qua day map. Chot HP ngay tai mep vuc
         * tao cam giac chet khi van con dung tren nen.
         */
        this.trainingSession.trainingWaitingShotEnd = true;
        final long phien = this.trainingSession.trainingSessionId;
        this.trainingSession.trainingFallDeathTask = TRAINING_BOT_EXECUTOR.schedule(
                () -> {
                    synchronized (this) {
                        this.trainingSession.trainingFallDeathTask = null;
                        if (!this.inTraining
                                || phien != this.trainingSession.trainingSessionId
                                || this.trainingSession.trainingBossState
                                        == TrainingBossState.ROUND_END) {
                            return;
                        }
                        try {
                            int hpTruoc = this.trainingSession.trainingPlayerHp;
                            this.trainingSession.trainingPlayerHp = 0;
                            this.ghiNhanSatThuongPowLuyenTap(hpTruoc);
                            this.dichVu.guiCapNhatMauLuyenTap(
                                    TRAINING_PLAYER_INDEX,
                                    0,
                                    this.trainingSession.trainingPlayerMaxHp,
                                    (byte) 2);
                            this.xuLyNguoiChoiThuaLuyenTap();
                        } catch (IOException ex) {
                            Logger.getLogger(ChickenNguoiChoi.class.getName())
                                    .log(Level.SEVERE, null, ex);
                        }
                    }
                },
                900L,
                TimeUnit.MILLISECONDS);
    }


    public synchronized void thoatLuyenTapVeSanh() {
        if (!this.inTraining) {
            return;
        }
        this.trainingReturningToLobby = true;
        this.roiLuyenTap();
        this.traVeSanhSauLuyenTapNgay();
    }

}
