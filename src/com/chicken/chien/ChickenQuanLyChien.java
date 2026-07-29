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
import com.chicken.phong.ChickenChoDau;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

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
    private final ChickenQuanLyBanDo map;
    private byte luotHienTai = -1;
    private boolean daKetThuc;
    private ScheduledFuture<?> tacVuBot;
    private long hanLuot;
    private ChickenHeThongGio.TrangThaiGio gioHienTai = ChickenHeThongGio.khongGio();
    private final ChickenKyNangDacBietHawk kyNangHawk;
    private final ChickenKyNangDacBietThor kyNangThor;
    private final ChickenKyNangDacBietLoki kyNangLoki;
    private final ChickenKyNangDacBietUltron kyNangUltron;
    private final ChickenKyNangDacBietIronMan kyNangIronMan;
    /** Trang thai ba LAN BAN lien tiep cua skill Ultron trong PvP. */
    private ChickenChienBinh ultronX3NguoiBan;
    private ChickenKetQuaDan ultronX3KetQua;
    private int ultronX3SoLanDaGui;
    private long ultronX3MaLoat;
    private long ultronX3XacNhanSomNhatMs;
    /** Giu lai CMD79 den som de client goc khong phai gui lai lan hai. */
    private ScheduledFuture<?> ultronX3TacVuXacNhanSom;
    private ScheduledFuture<?> ultronX3TacVuHetHan;

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
            }
        }
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiHienManHinhGameLuyenTap();
            }
        }
        this.luotHienTai = this.nguoiSongTiepTu((byte)-1);
        this.sendNextTurn();
        this.lapLichBotBan();
    }

    public synchronized void diChuyen(
            ChickenNguoiChoi nguoiChoi,
            ChickenTinNhan ms
    ) throws IOException {
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh == null || chienBinh.chet || chienBinh.chiSo != this.luotHienTai || this.daKetThuc) {
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
        if (chienBinh == null || chienBinh.chet || this.daKetThuc) {
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

    public synchronized void ban(
            ChickenNguoiChoi nguoiChoi,
            ChickenTinNhan ms
    ) throws IOException {
        ChickenChienBinh shooter = this.layChienBinh(nguoiChoi);
        if (shooter == null || shooter.chet || shooter.chiSo != this.luotHienTai
                || this.daKetThuc
                || this.kyNangThor.dangThiTrien(shooter)
                || this.kyNangLoki.dangThiTrien(shooter)) {
            return;
        }
        ChickenQuanLyDanSung.DuLieuSung sungMayChu =
                ChickenQuanLyDanSung.theoPartSung(shooter.maVuKhi);
        ChickenYeuCauBanServer.KetQua yeuCau =
                ChickenYeuCauBanServer.doc(ms, sungMayChu, shooter.avenger);
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
            if (this.kyNangIronMan.dangChoBan(shooter)) {
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
                this.phatBan(shooter, ketQua, (byte) 1);
                if (this.dongBoHulkSauPhat(shooter, ketQua)) {
                    return;
                }
                this.apDungSatThuongKetQua(ketQua);
            }
        }
        this.kyNangHawk.sauKhiBanThuong(shooter);
        if (!this.daKetThuc) {
            this.sangLuot();
        }
    }

    public synchronized boolean kichHoatKyNangIronMan(
            ChickenNguoiChoi nguoiChoi
    ) {
        return this.kyNangIronMan.kichHoat(this.layChienBinh(nguoiChoi));
    }

    public synchronized boolean kichHoatKyNangUltron(
            ChickenNguoiChoi nguoiChoi
    ) throws IOException {
        ChickenChienBinh ultron = this.layChienBinh(nguoiChoi);
        if (!this.kyNangUltron.kichHoatBanX3(ultron)) {
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
        return this.kyNangHawk.kichHoat(this.layChienBinh(nguoiChoi), chiSoMucTieu);
    }

    public synchronized void nhanLenhKyNangHawk(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        if (this.kyNangHawk == null) {
            System.out.println("[HAWK] kyNangHawk=null");
            return;
        }
        this.kyNangHawk.nhanLenh(this.layChienBinh(nguoiChoi), ms);
    }

    /** Route CMD -91 theo đúng AVG đang sử dụng. */
    public synchronized void nhanLenhKyNangDacBiet(
            ChickenNguoiChoi nguoiChoi,
            ChickenTinNhan ms
    ) throws IOException {
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh == null) {
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
        this.xuLyVaChamLoatUltron(nguoiChoi);
    }

    public synchronized void boLuot(ChickenNguoiChoi nguoiChoi)
            throws IOException {
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh != null && !chienBinh.chet
                && chienBinh.chiSo == this.luotHienTai
                && !this.daKetThuc
                && this.ultronX3KetQua == null
                && !this.kyNangThor.dangThiTrien(chienBinh)
                && !this.kyNangLoki.dangThiTrien(chienBinh)) {
            if (chienBinh.avenger == ChickenKyNangDacBietLoki.AVG_LOKI) {
                chienBinh.lokiDangChoChonMucTieu = false;
                chienBinh.lokiDaGuiMenu = false;
            }
            this.kyNangUltron.huyKhiBoLuot(chienBinh);
            this.kyNangIronMan.sauKhiBanHoacBoLuot(chienBinh);
            this.sangLuot();
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
        if (nguoiChoi != null) {
            TRAN_DAU_THEO_NGUOI_CHOI.remove(nguoiChoi.ma, this);
        }
    }

    /** Cho phép lớp trận boss riêng dọn đúng đăng ký trận mà constructor gốc đã tạo. */
    protected final void boDangKyNguoiChoi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi != null) {
            ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
            if (chienBinh != null) {
                chienBinh.daRoiTran = true;
            }
            TRAN_DAU_THEO_NGUOI_CHOI.remove(nguoiChoi.ma, this);
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
        this.huyTrangThaiLoatUltron();
        this.kyNangUltron.sauKhiDaBan(shooter);
        this.apDungSatThuongKetQua(ketQua);
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
        ChickenQuanLyCongThucSung.KiemTraBanDo kiemTraBanDo =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() {
                        return map.getWidth();
                    }

                    @Override
                    public int getHeight() {
                        return map.getHeight();
                    }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return map.coVaCham(x, y);
                    }
                };

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
        ChickenQuanLyCongThucSung.KiemTraBanDo kiemTraBanDo =
                new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                    @Override
                    public int getWidth() {
                        return map.getWidth();
                    }

                    @Override
                    public int getHeight() {
                        return map.getHeight();
                    }

                    @Override
                    public boolean coVaCham(short x, short y) {
                        return map.coVaCham(x, y);
                    }
                };

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
                        new ChickenQuanLyCongThucSung.KiemTraBanDo() {
                            @Override
                            public int getWidth() {
                                return map.getWidth();
                            }

                            @Override
                            public int getHeight() {
                                return map.getHeight();
                            }

                            @Override
                            public boolean coVaCham(short x, short y) {
                                return map.coVaCham(x, y);
                            }
                        }
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

    private synchronized void nhipBot() throws IOException {
        if (this.daKetThuc) {
            this.dungBot();
            return;
        }
        if (this.ultronX3KetQua != null) {
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
                this.phatBan(turn, ketQua, (byte)1);
                if (this.dongBoHulkSauPhat(turn, ketQua)) {
                    return;
                }
                this.apDungSatThuongKetQua(ketQua);
            }
            if (!this.daKetThuc) {
                this.sangLuot();
            }
        } else if (System.currentTimeMillis() > this.hanLuot) {
            this.sangLuot();
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

    private void apDungSatThuongKetQua(ChickenKetQuaDan ketQua) throws IOException {
        if (ketQua == null) {
            return;
        }
        for (Map.Entry<ChickenChienBinh, Integer> entry
                : ketQua.satThuongTheoMucTieu.entrySet()) {
            ChickenChienBinh mucTieu = entry.getKey();
            int satThuong = entry.getValue();
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
                TRAN_DAU_THEO_NGUOI_CHOI.remove(chienBinh.nguoiChoi.ma, this);
            }
        }
        this.wait.ketThucDau();
    }

    private void sangLuot() throws IOException {
        int slotVuaCoLuot = this.luotHienTai & 0xFF;
        if (this.luotHienTai >= 0 && this.luotHienTai < this.chienBinhs.length) {
            ChickenChienBinh vuaCoLuot = this.chienBinhs[this.luotHienTai];
            if (vuaCoLuot != null
                    && vuaCoLuot.avenger == ChickenKyNangDacBietLoki.AVG_LOKI
                    && !vuaCoLuot.lokiSkillActive) {
                vuaCoLuot.lokiDangChoChonMucTieu = false;
                if (!vuaCoLuot.lokiDaDungKyNang) {
                    vuaCoLuot.lokiDaGuiMenu = false;
                }
            }
            if (vuaCoLuot != null) {
                this.kyNangUltron.huyKhiBoLuot(vuaCoLuot);
                this.kyNangIronMan.sauKhiBanHoacBoLuot(vuaCoLuot);
                if (!vuaCoLuot.chet) {
                    this.napDan[slotVuaCoLuot] =
                            ChickenNapDanServer.layChoChienBinh(vuaCoLuot);
                }
            }
        }
        this.luotHienTai = this.timLuotTheoNapDan(slotVuaCoLuot);
        this.sendNextTurn();
    }

    private byte timLuotTheoNapDan(int sauSlot) {
        byte sanSang = this.timNguoiSanSangSau(sauSlot);
        if (sanSang >= 0) {
            return sanSang;
        }
        int nhoNhat = Integer.MAX_VALUE;
        for (int slot = 0; slot < this.chienBinhs.length; slot++) {
            ChickenChienBinh chienBinh = this.chienBinhs[slot];
            if (chienBinh != null && !chienBinh.chet && this.napDan[slot] > 0) {
                nhoNhat = Math.min(nhoNhat, this.napDan[slot]);
            }
        }
        if (nhoNhat == Integer.MAX_VALUE) {
            return -1;
        }
        for (int slot = 0; slot < this.chienBinhs.length; slot++) {
            ChickenChienBinh chienBinh = this.chienBinhs[slot];
            if (chienBinh != null && !chienBinh.chet) {
                this.napDan[slot] = Math.max(0, this.napDan[slot] - nhoNhat);
            }
        }
        return this.timNguoiSanSangSau(sauSlot);
    }

    private byte timNguoiSanSangSau(int sauSlot) {
        for (int buoc = 1; buoc <= this.chienBinhs.length; buoc++) {
            int slot = (sauSlot + buoc + this.chienBinhs.length)
                    % this.chienBinhs.length;
            ChickenChienBinh chienBinh = this.chienBinhs[slot];
            if (chienBinh != null && !chienBinh.chet && this.napDan[slot] <= 0) {
                return (byte) slot;
            }
        }
        return -1;
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
        next.quangDuongDiChuyenConLai =
                ChickenThanhDiChuyenAVG.hoiDay(next.theLucDiChuyenToiDa);
        this.gioHienTai = ChickenHeThongGio.taoGioMoi();
        this.hanLuot = System.currentTimeMillis() + TURN_SECONDS * 1000L;
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiGio(this.gioHienTai.getWindX(), this.gioHienTai.getWindY());
                chienBinh.nguoiChoi.dichVu.guiLuotDauTiep(this.luotHienTai, next.x, next.y, this.chienBinhConSong(), (byte)TURN_SECONDS);
            }
        }
        this.kyNangHawk.guiTinHieuChonMucTieuNeuCo(next);
        this.kyNangThor.guiTinHieuKyNangNeuCo(next);
        this.kyNangLoki.guiTinHieuKyNangNeuCo(next);
        this.kyNangUltron.guiTinHieuKyNangNeuCo(next);
        this.kyNangIronMan.guiTinHieuKyNangNeuCo(next);
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

        int chiSoMucTieu = ketQua.getChiSoMucTieu();
        if (chiSoMucTieu >= 0 && chiSoMucTieu < this.chienBinhs.length) {
            ChickenChienBinh mucTieu = this.chienBinhs[chiSoMucTieu];
            if (mucTieu != null && mucTieu != shooter
                    && !mucTieu.chet && mucTieu.hp > 0) {
                int satThuong = ChickenTiaLaserIronMan.tinhSatThuongNhuHawk(
                        shooter.tanCong, mucTieu.giap);
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

    private void phatBan(ChickenChienBinh shooter, ChickenKetQuaDan ketQua, byte numShoot) throws IOException {
        boolean neoDiemDauQuyDao =
                shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON;
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiKetQuaBanDau(
                        shooter.chiSo,
                        shooter.x,
                        shooter.y,
                        ketQua,
                        numShoot,
                        neoDiemDauQuyDao
                );
            }
        }
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
                        loat.getY()
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
