package com.chicken.chien;

import com.chicken.chiso.ChickenKichThuocNhanVat;
import com.chicken.avg.ChickenKyNangDacBietHawk;
import com.chicken.avg.ChickenKyNangDacBietThor;
import com.chicken.avg.ChickenKyNangDacBietLoki;
import com.chicken.avg.ChickenKyNangDacBietUltron;
import com.chicken.avg.ChickenCongThucBanUltron;
import com.chicken.avg.ChickenGocBanUltron;
import com.chicken.avg.ChickenHoatAnhHawk;
import com.chicken.avg.ChickenCoCheBayAVG;
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
    private final ChickenChoDau wait;
    private final ChickenChienBinh[] chienBinhs = new ChickenChienBinh[MAX_FIGHTERS];
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

    public ChickenQuanLyChien(ChickenChoDau wait, ChickenNguoiChoi[] nguoiChois, byte maBanDo) {
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
        this.kyNangHawk = new ChickenKyNangDacBietHawk(this.chienBinhs,
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

    public void diChuyen(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh == null || chienBinh.chet || chienBinh.chiSo != this.luotHienTai || this.daKetThuc) {
            return;
        }
        short xYeuCau = ms.boDoc().readShort();
        short yYeuCau = ms.boDoc().readShort();
        short xMucTieu;
        short yMucTieu;
        if (ChickenCoCheBayAVG.coTheBay(chienBinh.avenger)) {
            // AVG bay đi tự do theo X/Y và xuyên địa hình; chỉ giữ trong khung map.
            xMucTieu = ChickenCoCheBayAVG.gioiHanToaDoTrongMap(
                    xYeuCau, this.map.getWidth());
            yMucTieu = ChickenCoCheBayAVG.gioiHanToaDoTrongMap(
                    yYeuCau, this.map.getHeight());
        } else {
            xMucTieu = this.kepShort(xYeuCau, 0, this.map.getWidth());
            yMucTieu = this.kepShort(yYeuCau, 0, this.map.getHeight());
        }

        ChickenThanhDiChuyenAVG.KetQuaDiChuyen ketQuaDiChuyen =
                ChickenThanhDiChuyenAVG.gioiHan(
                        chienBinh.x,
                        chienBinh.y,
                        xMucTieu,
                        yMucTieu,
                        chienBinh.quangDuongDiChuyenConLai
                );
        chienBinh.x = ketQuaDiChuyen.getX();
        chienBinh.y = ketQuaDiChuyen.getY();
        chienBinh.quangDuongDiChuyenConLai = ketQuaDiChuyen.getConLai();
        this.phatDiChuyen(chienBinh);
    }

    public void capNhatXY(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh == null || this.daKetThuc) {
            return;
        }
        short x = ms.boDoc().readShort();
        short y = ms.boDoc().readShort();
        if (ChickenCoCheBayAVG.coTheBay(chienBinh.avenger)) {
            chienBinh.x = ChickenCoCheBayAVG.gioiHanToaDoTrongMap(
                    x, this.map.getWidth());
            chienBinh.y = ChickenCoCheBayAVG.gioiHanToaDoTrongMap(
                    y, this.map.getHeight());
        } else {
            chienBinh.x = this.kepShort(x, 0, this.map.getWidth());
            chienBinh.y = this.kepShort(y, 0, this.map.getHeight());
        }
        this.phatCapNhatXY(chienBinh);
    }

    public void ban(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        ChickenChienBinh shooter = this.layChienBinh(nguoiChoi);
        if (shooter == null || shooter.chet || shooter.chiSo != this.luotHienTai
                || this.daKetThuc
                || this.kyNangThor.dangThiTrien(shooter)
                || this.kyNangLoki.dangThiTrien(shooter)) {
            return;
        }
        if (shooter.avenger == ChickenKyNangDacBietLoki.AVG_LOKI
                && shooter.lokiDangChoChonMucTieu) {
            shooter.lokiDangChoChonMucTieu = false;
            shooter.lokiDaGuiMenu = false;
        }
        byte loaiDan = ms.boDoc().readByte();
        short x = ms.boDoc().readShort();
        short y = ms.boDoc().readShort();
        short goc = ms.boDoc().readShort();
        if (shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON) {
            goc = ChickenGocBanUltron.chuanHoa(goc);
        }
        byte luc = ms.boDoc().readByte();
        if (loaiDan == 17 || loaiDan == 19) {
            ms.boDoc().readByte();
        }
        byte numShoot = ms.boDoc().readByte();
        if (luc <= 0) {
            luc = 10;
        }
        if (luc > 30) {
            luc = 30;
        }
        if (ChickenCoCheBayAVG.coTheBay(shooter.avenger)) {
            shooter.x = ChickenCoCheBayAVG.gioiHanToaDoTrongMap(
                    x, this.map.getWidth());
            shooter.y = ChickenCoCheBayAVG.gioiHanToaDoTrongMap(
                    y, this.map.getHeight());
        } else {
            shooter.x = this.kepShort(x, 0, this.map.getWidth());
            shooter.y = this.kepShort(y, 0, this.map.getHeight());
        }
        if (shooter.avenger == ChickenKyNangDacBietUltron.AVG_ULTRON) {
            shooter.ultronGocNgamHienTai = this.chuanHoaGocUltron(goc);
            shooter.ultronLucNgamHienTai = luc;
            shooter.ultronDaCoGocNgam = true;
        }

        if (this.kyNangUltron.dangBanX3(shooter)) {
            this.banX3Ultron(shooter, goc, luc);
            this.kyNangUltron.sauKhiDaBan(shooter);
        } else {
            ChickenKetQuaDan ketQua = this.xuLyPhatBan(
                    shooter,
                    this.layLoaiDanAnToan(loaiDan),
                    goc,
                    luc
            );
            this.phatBan(shooter, ketQua, numShoot);
            if (ketQua.mucTieu != null && ketQua.satThuong > 0) {
                this.satThuong(ketQua.mucTieu, ketQua.satThuong);
            }
        }
        this.kyNangHawk.sauKhiBanThuong(shooter);
        if (!this.daKetThuc) {
            this.sangLuot();
        }
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
        /*
         * Client gửi lựa chọn CMD -47 rồi tự bật InfoDlg.showWait().
         * CMD -67 chỉ đóng trạng thái chờ và đưa client về màn hình trận,
         * không phát lại lượt, không đổi turnId, góc ngắm hoặc lực bắn.
         */
        if (ultron.nguoiChoi != null && ultron.nguoiChoi.dichVu != null) {
            ultron.nguoiChoi.dichVu.guiHienManHinhGameLuyenTap();
        }
        System.out.println("[ULTRON] DA_CHON_BAN_X3 index="
                + (ultron.chiSo & 0xFF)
                + " choPhatBanThat=true dongChoCmd=-67 khongGuiLaiLuot=true");
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

    public void kiemTraVaCham(ChickenNguoiChoi nguoiChoi, ChickenTinNhan ms) throws IOException {
        while (ms.boDoc().available() > 0) {
            ms.boDoc().readByte();
        }
    }

    public void boLuot(ChickenNguoiChoi nguoiChoi) throws IOException {
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh != null && !chienBinh.chet
                && chienBinh.chiSo == this.luotHienTai
                && !this.daKetThuc
                && !this.kyNangThor.dangThiTrien(chienBinh)
                && !this.kyNangLoki.dangThiTrien(chienBinh)) {
            if (chienBinh.avenger == ChickenKyNangDacBietLoki.AVG_LOKI) {
                chienBinh.lokiDangChoChonMucTieu = false;
                chienBinh.lokiDaGuiMenu = false;
            }
            this.kyNangUltron.huyKhiBoLuot(chienBinh);
            this.sangLuot();
        }
    }

    public void khiNguoiChoiRoi(ChickenNguoiChoi nguoiChoi) {
        ChickenChienBinh chienBinh = this.layChienBinh(nguoiChoi);
        if (chienBinh != null) {
            chienBinh.chet = true;
            chienBinh.hp = 0;
        }
        if (nguoiChoi != null) {
            TRAN_DAU_THEO_NGUOI_CHOI.remove(nguoiChoi.ma, this);
        }
    }

    /** Cho phép lớp trận boss riêng dọn đúng đăng ký trận mà constructor gốc đã tạo. */
    protected final void boDangKyNguoiChoi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi != null) {
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

    private void banX3Ultron(
            ChickenChienBinh shooter,
            short goc,
            byte luc
    ) throws IOException {
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
                            shooter.x, shooter.y, goc, kiemTraBanDo);
        }

        /*
         * Phân tích lại theo đúng yêu cầu người dùng:
         * - Chỉ tia giữa là tia thật, tính va chạm và sát thương.
         * - Hai tia ngoài chỉ là hiệu ứng thị giác.
         * - Cả 3 tia phải chụm về cùng một điểm cuối.
         *
         * Vì vậy phải tìm điểm kết thúc của tia giữa trước, rồi mới dựng hai
         * tia phụ hội tụ về đúng điểm đó. Không được cắt hai tia phụ theo map /
         * người riêng lẻ, nếu không chúng sẽ kết thúc ở ba vị trí khác nhau.
         */
        ChickenCongThucBanUltron.DuongTia tiaGiuaDayDu =
                ChickenCongThucBanUltron.taoTiaThang(
                        dauNong[0],
                        dauNong[1],
                        goc,
                        this.map.getWidth(),
                        this.map.getHeight()
                );
        short[][] tiaGiuaSauMap = this.catDuongTiaTaiVaChamBanDo(
                tiaGiuaDayDu.getX(),
                tiaGiuaDayDu.getY()
        );
        short[] tiaGiuaX = tiaGiuaSauMap[0];
        short[] tiaGiuaY = tiaGiuaSauMap[1];

        Map<ChickenChienBinh, Integer> tongSatThuong =
                new LinkedHashMap<ChickenChienBinh, Integer>();
        VaChamTiaUltron vaCham = this.timVaChamTiaUltron(
                shooter,
                tiaGiuaX,
                tiaGiuaY
        );
        if (vaCham != null) {
            short[][] daCat = this.catDuongTiaTaiVaCham(
                    tiaGiuaX,
                    tiaGiuaY,
                    vaCham
            );
            tiaGiuaX = daCat[0];
            tiaGiuaY = daCat[1];
            ChickenChienBinh mucTieu = vaCham.mucTieu;
            int tanCong = shooter.tanCong > 0 ? shooter.tanCong : 20;
            int satThuong = Math.max(1, tanCong - mucTieu.giap);
            tongSatThuong.put(mucTieu, satThuong);
        }

        short diemCuoiX = this.layGiaTriCuoi(tiaGiuaX, dauNong[0]);
        short diemCuoiY = this.layGiaTriCuoi(tiaGiuaY, dauNong[1]);

        ChickenCongThucBanUltron.LoatBaTia loatHienThi =
                ChickenCongThucBanUltron.taoBaTiaHoiTuTaiDiemCuoi(
                        dauNong[0],
                        dauNong[1],
                        goc,
                        diemCuoiX,
                        diemCuoiY,
                        this.map.getWidth(),
                        this.map.getHeight()
                );
        short[][] hienThiX = loatHienThi.getX();
        short[][] hienThiY = loatHienThi.getY();
        hienThiX[1] = tiaGiuaX;
        hienThiY[1] = tiaGiuaY;

        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiLoatLaserUltronDau(
                        shooter.chiSo,
                        shooter.x,
                        shooter.y,
                        goc,
                        luc,
                        hienThiX,
                        hienThiY
                );
            }
        }

        for (Map.Entry<ChickenChienBinh, Integer> entry
                : tongSatThuong.entrySet()) {
            if (this.daKetThuc) {
                break;
            }
            this.satThuong(entry.getKey(), entry.getValue());
        }

        System.out.println("[ULTRON] BAN_X3 shooter="
                + (shooter.chiSo & 0xFF)
                + " goc=" + goc
                + " impactX=" + diemCuoiX
                + " impactY=" + diemCuoiY
                + " tiaThat=1 tiaHieuUng=2 hoiTu=1Diem");
    }

    private ChickenKetQuaDan xuLyPhatBan(
            ChickenChienBinh shooter,
            byte loaiDan,
            short goc,
            byte luc
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
                            shooter.x, shooter.y, goc, kiemTraBanDo);
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
                                    this.layIdSung(shooter.maVuKhi)),
                            ChickenHeThongGio.layWindYChoSung(
                                    this.gioHienTai,
                                    this.layIdSung(shooter.maVuKhi)),
                            kiemTraBanDo
                    );
            hienThiX = quyDao.getHienThiX();
            hienThiY = quyDao.getHienThiY();
            vaChamX = quyDao.getVaChamX();
            vaChamY = quyDao.getVaChamY();
        }

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
        } else {
            mucTieu = this.timMucTieuTrung(
                    shooter,
                    vaChamX,
                    vaChamY
            );
        }

        int satThuong = 0;
        if (mucTieu != null) {
            int tanCong = shooter.tanCong > 0
                    ? shooter.tanCong
                    : 20 + luc / 2;
            satThuong = Math.max(1, tanCong - mucTieu.giap);
        }

        return new ChickenKetQuaDan(
                loaiDan,
                dauNong[0],
                dauNong[1],
                goc,
                luc,
                hienThiX,
                hienThiY,
                mucTieu,
                satThuong
        );
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
                if (ketQua.mucTieu != null && ketQua.satThuong > 0) {
                    this.satThuong(ketQua.mucTieu, ketQua.satThuong);
                }
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
                     * Quét từng pixel và cộng 4px theo bề dày hiển thị của tia.
                     * Khoảng cách được tính tới vùng thân, không tính tên/súng.
                     */
                    double khoangCach = mucTieu.bot
                            ? ChickenKichThuocNhanVat.khoangCachDenBoss(
                                    danX, danY, mucTieu.x, mucTieu.y)
                            : ChickenKichThuocNhanVat.khoangCachDenNguoiChoi(
                                    danX, danY, mucTieu.x, mucTieu.y);
                    if (khoangCach <= 4.0D) {
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
        int alive = 0;
        byte pheThang = 0;
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && !chienBinh.chet) {
                alive++;
                pheThang = (byte)(chienBinh.chiSo % 2);
            }
        }
        if (alive > 1) {
            return;
        }
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
            }
        }
        this.luotHienTai = this.nguoiSongTiepTu(this.luotHienTai);
        this.sendNextTurn();
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
        ChickenChienBinh next = this.chienBinhs[this.luotHienTai];
        // Client tự xóa phần thanh đã dùng khi nhận CMD 24; server hồi cùng lúc.
        next.quangDuongDiChuyenConLai =
                ChickenThanhDiChuyenAVG.hoiDay(next.avenger);
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
        for (ChickenChienBinh chienBinh : this.chienBinhs) {
            if (chienBinh != null && chienBinh.coPhien()) {
                chienBinh.nguoiChoi.dichVu.guiKetQuaBanDau(shooter.chiSo, ketQua, numShoot);
            }
        }
    }

    /**
     * Phát đúng một loạt Hawk gồm bốn quỹ đạo. Bullet type 9 của client tự
     * dựng bốn mũi Small1879 từ bốn đường này; soPhat luôn là 1 nên sau mũi
     * thứ tư client dừng hẳn, không khởi động thêm loạt mới.
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
