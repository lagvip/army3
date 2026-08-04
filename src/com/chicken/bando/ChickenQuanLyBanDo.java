package com.chicken.bando;

import com.chicken.chien.ChickenQuanLyCongThucSung;
import com.chicken.tienich.ChickenTienIch;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ChickenQuanLyBanDo
        implements ChickenQuanLyCongThucSung.KiemTraBanDo {
    private static final short[] DEFAULT_SPAWN_X = new short[]{220, 600, 320, 720, 150, 850, 460, 980};
    private static final short[] DEFAULT_SPAWN_Y = new short[]{300, 300, 260, 260, 320, 320, 280, 280};
    private static final Map<String, HoleMask> HOLE_MASK_CACHE = new HashMap<>();
    private static final String TEP_DA_RUA = "res/icon/hole/bullrua.png";
    private static final String TEP_MANG_NHEN =
            "res/icon/hole/mangnhen.png";
    private static final int LECH_DA_RUA_X = 15;
    private static final int LECH_DA_RUA_Y = 37;
    private static final int LECH_MANG_NHEN_X = 21;
    private static final int LECH_MANG_NHEN_Y = 20;
    private static int[] daRuaArgb;
    private static int daRuaRong;
    private static int daRuaCao;
    private static int[] mangNhenArgb;
    private static int mangNhenRong;
    private static int mangNhenCao;

    private final ArrayList<MapEntry> mucs = new ArrayList<>();
    private final ArrayList<VoiRongDangHoatDong> voiRongs =
            new ArrayList<>();
    private short[] spawnX = DEFAULT_SPAWN_X;
    private short[] spawnY = DEFAULT_SPAWN_Y;
    private byte maBanDo;
    private byte maNen;
    private int chieuRong = 1200;
    private int chieuCao = 700;

    /**
     * Khop chinh xac Image.getRGB + CRes.isLand cua client Unity:
     * alpha bang 0 duoc chuyen thanh mau trang, va mau trang la rong.
     * Moi pixel con lai (ke ca vien alpha thap) deu la dia hinh.
     */
    private static boolean laPixelVaChamTheoClient(int argb) {
        int alpha = argb >>> 24;
        int rgb = argb & 0x00FFFFFF;
        return alpha != 0 && rgb != 0x00FFFFFF;
    }

    public ChickenQuanLyBanDo(int mapID) {
        this.setMapId(mapID);
    }

    public synchronized void setMapId(int mapID) {
        this.maBanDo = (byte) mapID;
        this.mucs.clear();
        this.voiRongs.clear();
        this.spawnX = DEFAULT_SPAWN_X;
        this.spawnY = DEFAULT_SPAWN_Y;
        ChickenDuLieuBanDo.MapDataEntry muc = this.findEntry(mapID);
        if (muc == null || muc.duLieu == null || muc.duLieu.length < 5) {
            return;
        }
        this.maNen = muc.bgID;
        this.chieuRong = ChickenTienIch.getShort(0, muc.duLieu);
        this.chieuCao = ChickenTienIch.getShort(2, muc.duLieu);
        this.phanTichBanDo(muc.duLieu);
    }

    public synchronized void datLaiVaCham() {
        this.setMapId(this.maBanDo & 0xFF);
    }

    public byte layMaBanDo() {
        return this.maBanDo;
    }

    public byte layMaNen() {
        return this.maNen;
    }

    public int getWidth() {
        return this.chieuRong;
    }

    public int getHeight() {
        return this.chieuCao;
    }

    /**
     * Ghi nhan cot loc authoritative. Gia tri 4 gom luot vua dat; khi luot
     * do ket thuc con dung 3 luot tiep theo, trung voi nturn=3 cua client.
     */
    public synchronized boolean themVoiRong(int tamX, int yDat) {
        if (tamX < 0 || tamX >= this.chieuRong
                || yDat < 0 || yDat >= this.chieuCao) {
            return false;
        }
        this.voiRongs.add(new VoiRongDangHoatDong(
                (short) tamX, (short) yDat, 4));
        return true;
    }

    /**
     * Gọi đúng một lần khi một lượt người chơi kết thúc. Lượt boss phải
     * truyền {@code false} để không làm giảm thời gian tồn tại của vòi rồng.
     */
    public synchronized void ketThucLuotVoiRong(boolean laLuotNguoiChoi) {
        if (!laLuotNguoiChoi) {
            return;
        }
        this.ketThucLuotVoiRong();
    }

    /** Kết thúc một lượt hợp lệ được tính cho vòi rồng. */
    public synchronized void ketThucLuotVoiRong() {
        for (int i = this.voiRongs.size() - 1; i >= 0; i--) {
            VoiRongDangHoatDong voiRong = this.voiRongs.get(i);
            voiRong.soLuotConLai--;
            if (voiRong.soLuotConLai <= 0) {
                this.voiRongs.remove(i);
            }
        }
    }

    @Override
    public synchronized short[][] layCacVoiRong() {
        short[][] ketQua = new short[this.voiRongs.size()][2];
        for (int i = 0; i < this.voiRongs.size(); i++) {
            VoiRongDangHoatDong voiRong = this.voiRongs.get(i);
            ketQua[i][0] = voiRong.tamX;
            ketQua[i][1] = voiRong.yDat;
        }
        return ketQua;
    }

    public synchronized int laySoVoiRongDangHoatDong() {
        return this.voiRongs.size();
    }

    public short laySinhX(int chiSo) {
        if (chiSo >= 0 && chiSo < this.spawnX.length) {
            return this.spawnX[chiSo];
        }
        return DEFAULT_SPAWN_X[chiSo % DEFAULT_SPAWN_X.length];
    }

    public short laySinhY(int chiSo) {
        if (chiSo >= 0 && chiSo < this.spawnY.length) {
            return this.spawnY[chiSo];
        }
        return DEFAULT_SPAWN_Y[chiSo % DEFAULT_SPAWN_Y.length];
    }

    public synchronized boolean coVaCham(short x, short y) {
        if (x < 0 || x >= this.chieuRong || y < 0) {
            return false;
        }
        if (y >= this.chieuCao) {
            return true;
        }
        for (MapEntry muc : this.mucs) {
            if (muc.coVaCham(x, y)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean coThePhaDiaHinh(short x, short y) {
        if (x < 0 || x >= this.chieuRong
                || y < 0 || y >= this.chieuCao) {
            return false;
        }
        for (MapEntry muc : this.mucs) {
            if (muc.coThePha && muc.coVaCham(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Va cham rieng cua mang nhen tam. Client kiem tra lop nay bang mot diem
     * gan chan khi di bo, vi vay server can tach no khoi hitbox than rong de
     * tranh client buoc duoc roi bi server keo nguoc lai lien tuc.
     */
    public synchronized boolean coVaChamMangNhen(short x, short y) {
        if (x < 0 || x >= this.chieuRong
                || y < 0 || y >= this.chieuCao) {
            return false;
        }
        for (MapEntry muc : this.mucs) {
            if (muc.laMangNhen && muc.coVaCham(x, y)) {
                return true;
            }
        }
        return false;
    }

    /** Kiem tra mot hitbox dang nam trong mang nhen, khong tinh dia hinh goc. */
    public synchronized boolean coMangNhenTrongVung(
            int trai,
            int tren,
            int phai,
            int duoi
    ) {
        int xDau = Math.max(0, Math.min(trai, phai));
        int xCuoi = Math.min(this.chieuRong - 1, Math.max(trai, phai));
        int yDau = Math.max(0, Math.min(tren, duoi));
        int yCuoi = Math.min(this.chieuCao - 1, Math.max(tren, duoi));
        if (xDau > xCuoi || yDau > yCuoi) {
            return false;
        }
        for (MapEntry muc : this.mucs) {
            if (!muc.laMangNhen) {
                continue;
            }
            int giaoTrai = Math.max(xDau, muc.x);
            int giaoPhai = Math.min(
                    xCuoi, muc.x + muc.chieuRong - 1);
            int giaoTren = Math.max(yDau, muc.y);
            int giaoDuoi = Math.min(
                    yCuoi, muc.y + muc.chieuCao - 1);
            for (int y = giaoTren; y <= giaoDuoi; y++) {
                for (int x = giaoTrai; x <= giaoPhai; x++) {
                    if (muc.coVaCham((short) x, (short) y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Va cham dia hinh goc va vat can khac, khong tinh mang nhen tam. */
    public synchronized boolean coVaChamKhongTinhMangNhen(short x, short y) {
        if (x < 0 || x >= this.chieuRong || y < 0) {
            return false;
        }
        if (y >= this.chieuCao) {
            return true;
        }
        // Duong di nhanh nay vua giu dung hanh vi coVaCham() cua cac ban do
        // chuyen biet, vua tranh quet lai toan bo danh sach khi diem khong nam
        // tren luoi nhen.
        if (!this.coVaChamMangNhen(x, y)) {
            return this.coVaCham(x, y);
        }
        for (MapEntry muc : this.mucs) {
            if (!muc.laMangNhen && muc.coVaCham(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cập nhật mặt nạ va chạm của server bằng đúng hình lỗ mà client dùng.
     * Màu đen đặc trong ảnh hole là vùng bị xóa; viền đỏ chỉ tạo mép lỗ và
     * không được xóa khỏi mặt nạ. Tile nằm trong undestroyTile vẫn va chạm
     * bình thường nhưng không bị phá.
     */
    public synchronized void phaDiaHinh(int tamX, int tamY, byte loaiDan) {
        int loai = loaiDan & 0xFF;
        // Type 5/36 chi dich chuyen nguoi ban; type 8 them mang nhan. Ca ba
        // nhanh Bullet.explode cua client deu khong goi CMap.makeHole(). Chan
        // them tai lop ban do de mot loi goi truc tiep trong che do moi cung
        // khong the lam terrain server lech terrain client.
        if (loai == 5 || loai == 36 || loai == 8 || loai == 13) {
            return;
        }
        if (tamX < 0 || tamY < 0 || tamX >= this.chieuRong || tamY >= this.chieuCao) {
            return;
        }
        HoleMask mask = layMatNaLoTheoLoaiDan(loaiDan & 0xFF);
        if (mask == null) {
            return;
        }
        int batDauX = tamX - mask.width / 2;
        int batDauY = tamY - mask.height / 2;
        System.out.println("[PHYSICS][SERVER_HOLE] bulletType="
                + (loaiDan & 0xFF)
                + " center=" + tamX + "," + tamY
                + " mask=" + mask.width + "x" + mask.height
                + " rect=" + batDauX + "," + batDauY
                + ".." + (batDauX + mask.width - 1) + ","
                + (batDauY + mask.height - 1));
        for (MapEntry muc : this.mucs) {
            muc.xoaTheoMatNa(batDauX, batDauY, mask);
        }
    }

    /**
     * Dong bo dia hinh theo duong dan ma client vua ve. Dan thuong chi tao
     * mot lo tai diem va cham cuoi. Rieng type 25 goi makeHole trong suot luc
     * nam trong dat, nen server quet tung doan va dao cung mot duong lien tuc.
     */
    public synchronized boolean phaDiaHinhTheoDuongDan(
            short[] xs,
            short[] ys,
            byte loaiDan
    ) {
        int soDiem = Math.min(
                xs == null ? 0 : xs.length,
                ys == null ? 0 : ys.length);
        if (soDiem <= 0) {
            return false;
        }
        int loai = loaiDan & 0xFF;
        if (loai != 25) {
            short x = xs[soDiem - 1];
            short y = ys[soDiem - 1];
            if (x < 0 || y < 0 || x >= this.chieuRong
                    || y >= this.chieuCao || !this.coVaCham(x, y)) {
                return false;
            }
            this.phaDiaHinh(x, y, loaiDan);
            return true;
        }

        boolean daPha = false;
        int xTruoc = xs[0];
        int yTruoc = ys[0];
        daPha |= this.phaDiemXuyenDatNeuCan(xTruoc, yTruoc, loaiDan);
        for (int i = 1; i < soDiem; i++) {
            int xSau = xs[i];
            int ySau = ys[i];
            int dx = xSau - xTruoc;
            int dy = ySau - yTruoc;
            int doDai = Math.max(Math.abs(dx), Math.abs(dy));
            int soMau = Math.max(1, (doDai + 5) / 6);
            for (int mau = 1; mau <= soMau; mau++) {
                int x = xTruoc + (int) Math.round((double) dx * mau / soMau);
                int y = yTruoc + (int) Math.round((double) dy * mau / soMau);
                daPha |= this.phaDiemXuyenDatNeuCan(x, y, loaiDan);
            }
            xTruoc = xSau;
            yTruoc = ySau;
        }
        return daPha;
    }

    private boolean phaDiemXuyenDatNeuCan(
            int x,
            int y,
            byte loaiDan
    ) {
        if (x < 0 || y < 0 || x >= this.chieuRong || y >= this.chieuCao
                || !this.coThePhaDiaHinh((short) x, (short) y)) {
            return false;
        }
        this.phaDiaHinh(x, y, loaiDan);
        return true;
    }

    /**
     * Thêm đúng vật cản mà client tạo khi Bullet type 61 của Boss Rùa nổ.
     * Client neo ảnh BULLRUA tại (x - 15, y - 37), nên server dùng cùng ảnh
     * và cùng tọa độ để đường đạn, di chuyển và phá địa hình không bị lệch.
     */
    public synchronized boolean themDaRua(int xVaCham, int yVaCham) {
        int[] argb = docAnhDaRua();
        if (argb == null || daRuaRong <= 0 || daRuaCao <= 0) {
            return false;
        }
        int trai = xVaCham - LECH_DA_RUA_X;
        int tren = yVaCham - LECH_DA_RUA_Y;
        this.mucs.add(new MapEntry(
                (short) trai,
                (short) tren,
                (short) daRuaRong,
                (short) daRuaCao,
                argb,
                true,
                true
        ));
        return true;
    }

    /**
     * Them dung mang nhan ma Bullet type 8 cua client tao tai diem roi.
     * Client neo anh tai (x - 21, y - 20); server dung cung anh alpha de
     * dan va di chuyen ve sau va cham trung khop, dong thoi van cho phep
     * dan khac pha mang nhan nhu mot manh dia hinh tam.
     */
    public synchronized boolean themMangNhen(int xVaCham, int yVaCham) {
        int[] argb = docAnhMangNhen();
        if (argb == null || mangNhenRong <= 0 || mangNhenCao <= 0
                || xVaCham < 0 || xVaCham >= this.chieuRong
                || yVaCham < 0 || yVaCham >= this.chieuCao) {
            return false;
        }
        this.mucs.add(new MapEntry(
                (short) (xVaCham - LECH_MANG_NHEN_X),
                (short) (yVaCham - LECH_MANG_NHEN_Y),
                (short) mangNhenRong,
                (short) mangNhenCao,
                argb,
                true,
                false,
                true
        ));
        return true;
    }

    public static synchronized boolean coTheThemMangNhen() {
        return docAnhMangNhen() != null;
    }

    /**
     * Kiểm tra phần lõi của đúng tảng đá đã ghim người chơi còn tồn tại.
     * Khi đạn phá trúng tâm đá, pixel này mất và người chơi được thoát ghim.
     */
    public synchronized boolean conDaRuaGhimTai(int xVaCham, int yVaCham) {
        int trai = xVaCham - LECH_DA_RUA_X;
        int tren = yVaCham - LECH_DA_RUA_Y;
        int yLoi = yVaCham - LECH_DA_RUA_Y / 2;
        for (MapEntry muc : this.mucs) {
            if (muc.laDaRua
                    && muc.x == (short) trai
                    && muc.y == (short) tren
                    && muc.coVaCham((short) xVaCham, (short) yLoi)) {
                return true;
            }
        }
        return false;
    }

    private ChickenDuLieuBanDo.MapDataEntry findEntry(int mapID) {
        if (ChickenDuLieuBanDo.entrys == null) {
            return null;
        }
        for (ChickenDuLieuBanDo.MapDataEntry muc : ChickenDuLieuBanDo.entrys) {
            if (muc.mapID == mapID) {
                return muc;
            }
        }
        return null;
    }

    private void phanTichBanDo(byte[] duLieu) {
        try {
            int offset = 4;
            int len = duLieu[offset++] & 0xFF;
            for (int i = 0; i < len && offset + 4 < duLieu.length; i++) {
                int brickId = duLieu[offset] & 0xFF;
                short x = (short) ChickenTienIch.getShort(offset + 1, duLieu);
                short y = (short) ChickenTienIch.getShort(offset + 3, duLieu);
                if (!ChickenDuLieuBanDo.existsMapBrick(brickId)) {
                    ChickenDuLieuBanDo.loadMapBrick(brickId);
                }
                ChickenDuLieuBanDo.MapBrickEntry brick = ChickenDuLieuBanDo.getMapBrickEntry(brickId);
                if (brick != null) {
                    // undestroyTile là tile không phá được, không phải tile vô hình.
                    // Nó vẫn phải có va chạm; chỉ cấm xóa pixel khi đạn nổ.
                    boolean coThePha = !ChickenDuLieuBanDo.isTileDestroy(brickId);
                    this.mucs.add(new MapEntry(
                            x,
                            y,
                            (short) brick.Width,
                            (short) brick.Height,
                            brick.dat,
                            coThePha
                    ));
                }
                offset += 5;
            }
            if (offset >= duLieu.length) {
                return;
            }
            int spawnCount = duLieu[offset++] & 0xFF;
            if (spawnCount <= 0 || offset + spawnCount * 4 > duLieu.length) {
                return;
            }
            this.spawnX = new short[spawnCount];
            this.spawnY = new short[spawnCount];
            for (int i = 0; i < spawnCount; i++) {
                this.spawnX[i] = (short) ChickenTienIch.getShort(offset, duLieu);
                offset += 2;
                this.spawnY[i] = (short) ChickenTienIch.getShort(offset, duLieu);
                offset += 2;
            }
        } catch (Exception ignored) {
            this.spawnX = DEFAULT_SPAWN_X;
            this.spawnY = DEFAULT_SPAWN_Y;
        }
    }

    private static HoleMask layMatNaLoTheoLoaiDan(int loaiDan) {
        String tenTep;
        switch (loaiDan) {
            case 0:
                tenTep = "h36x30.png";
                break;
            case 1:
            case 11:
            case 17:
            case 18:
            case 19:
            case 21:
            case 27:
            case 44:
            case 58:
                tenTep = "smallhole.png";
                break;
            case 2:
                // CMap.getHoleType(2) cua client tra ve index 0 (h32x26).
                // Dung smallhole (22x21) lam server con pixel nen ma client
                // da xoa, sau do CMD53 keo nguoi dang roi tro lai tren khong.
                tenTep = "h32x26.png";
                break;
            case 4:
            case 5:
            case 8:
            case 13:
            case 14:
            case 16:
            case 20:
            case 23:
            case 26:
            case 28:
            case 29:
            case 30:
            case 33:
            case 34:
            case 35:
            case 36:
            case 38:
            case 39:
            case 40:
            case 41:
            case 46:
            case 49:
            case 50:
            case 51:
            case 53:
            case 54:
            case 55:
            case 56:
                tenTep = "h32x26.png";
                break;
            case 3:
                tenTep = "h55x50.png";
                break;
            case 6:
            case 12:
                tenTep = "hrangcua.png";
                break;
            case 7:
            case 25:
            case 31:
            case 37:
            case 47:
                tenTep = "h14x12.png";
                break;
            case 9:
                tenTep = "rangehole.png";
                break;
            case 10:
                tenTep = "rocket.png";
                break;
            case 15:
            case 22:
            case 42:
            case 43:
            case 45:
            case 57:
                tenTep = "hgrenade.png";
                break;
            case 24:
            case 32:
            case 48:
            case 52:
                tenTep = "h36x30.png";
                break;
            default:
                tenTep = "h32x26.png";
                break;
        }
        synchronized (HOLE_MASK_CACHE) {
            if (HOLE_MASK_CACHE.containsKey(tenTep)) {
                return HOLE_MASK_CACHE.get(tenTep);
            }
            HoleMask mask = docMatNaLo(tenTep);
            HOLE_MASK_CACHE.put(tenTep, mask);
            return mask;
        }
    }

    private static HoleMask docMatNaLo(String tenTep) {
        try {
            BufferedImage img = ImageIO.read(new File("res/training/hole/" + tenTep));
            if (img == null) {
                return null;
            }
            int width = img.getWidth();
            int height = img.getHeight();
            int[] argb = new int[width * height];
            img.getRGB(0, 0, width, height, argb, 0, width);
            boolean[] xoa = new boolean[argb.length];
            for (int i = 0; i < argb.length; i++) {
                // Client Army__dm chỉ xóa pixel map khi pixel mask đúng màu đen.
                xoa[i] = argb[i] == 0xFF000000;
            }
            return new HoleMask(width, height, xoa);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static synchronized int[] docAnhDaRua() {
        if (daRuaArgb != null) {
            return daRuaArgb;
        }
        try {
            BufferedImage img = ImageIO.read(new File(TEP_DA_RUA));
            if (img == null || img.getWidth() <= 0 || img.getHeight() <= 0) {
                return null;
            }
            daRuaRong = img.getWidth();
            daRuaCao = img.getHeight();
            daRuaArgb = new int[daRuaRong * daRuaCao];
            img.getRGB(0, 0, daRuaRong, daRuaCao, daRuaArgb, 0, daRuaRong);
            return daRuaArgb;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static synchronized int[] docAnhMangNhen() {
        if (mangNhenArgb != null) {
            return mangNhenArgb;
        }
        try {
            BufferedImage img = ImageIO.read(new File(TEP_MANG_NHEN));
            if (img == null || img.getWidth() <= 0
                    || img.getHeight() <= 0) {
                return null;
            }
            mangNhenRong = img.getWidth();
            mangNhenCao = img.getHeight();
            mangNhenArgb = new int[mangNhenRong * mangNhenCao];
            img.getRGB(0, 0, mangNhenRong, mangNhenCao,
                    mangNhenArgb, 0, mangNhenRong);
            return mangNhenArgb;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static final class HoleMask {
        private final int width;
        private final int height;
        private final boolean[] xoa;

        private HoleMask(int width, int height, boolean[] xoa) {
            this.width = width;
            this.height = height;
            this.xoa = xoa;
        }
    }

    private static final class VoiRongDangHoatDong {
        private final short tamX;
        private final short yDat;
        private int soLuotConLai;

        private VoiRongDangHoatDong(
                short tamX,
                short yDat,
                int soLuotConLai
        ) {
            this.tamX = tamX;
            this.yDat = yDat;
            this.soLuotConLai = soLuotConLai;
        }
    }

    private static final class MapEntry {
        private final short x;
        private final short y;
        private final short chieuRong;
        private final short chieuCao;
        private final int[] argb;
        private final boolean coThePha;
        private final boolean laDaRua;
        private final boolean laMangNhen;

        private MapEntry(
                short x,
                short y,
                short chieuRong,
                short chieuCao,
                int[] argb,
                boolean coThePha
        ) {
            this(x, y, chieuRong, chieuCao, argb, coThePha, false, false);
        }

        private MapEntry(
                short x,
                short y,
                short chieuRong,
                short chieuCao,
                int[] argb,
                boolean coThePha,
                boolean laDaRua
        ) {
            this(x, y, chieuRong, chieuCao, argb,
                    coThePha, laDaRua, false);
        }

        private MapEntry(
                short x,
                short y,
                short chieuRong,
                short chieuCao,
                int[] argb,
                boolean coThePha,
                boolean laDaRua,
                boolean laMangNhen
        ) {
            this.x = x;
            this.y = y;
            this.chieuRong = chieuRong;
            this.chieuCao = chieuCao;
            // Mỗi trận phải có mặt nạ riêng; không được sửa mảng cache dùng chung.
            this.argb = argb == null ? null : argb.clone();
            this.coThePha = coThePha;
            this.laDaRua = laDaRua;
            this.laMangNhen = laMangNhen;
        }

        private boolean coVaCham(short px, short py) {
            int localX = px - this.x;
            int localY = py - this.y;
            if (localX < 0 || localY < 0 || localX >= this.chieuRong || localY >= this.chieuCao) {
                return false;
            }
            if (this.argb == null || this.argb.length <= localY * this.chieuRong + localX) {
                // Không có dữ liệu pixel thật thì không được tự coi cả hình chữ
                // nhật là vật cản vô hình.
                return false;
            }
            return laPixelVaChamTheoClient(
                    this.argb[localY * this.chieuRong + localX]);
        }

        private void xoaTheoMatNa(int maskX, int maskY, HoleMask mask) {
            if (!this.coThePha || this.argb == null || mask == null) {
                return;
            }
            int giaoTrai = Math.max(this.x, maskX);
            int giaoTren = Math.max(this.y, maskY);
            int giaoPhai = Math.min(this.x + this.chieuRong, maskX + mask.width);
            int giaoDuoi = Math.min(this.y + this.chieuCao, maskY + mask.height);
            if (giaoTrai >= giaoPhai || giaoTren >= giaoDuoi) {
                return;
            }
            for (int py = giaoTren; py < giaoDuoi; py++) {
                int localMapY = py - this.y;
                int localMaskY = py - maskY;
                for (int px = giaoTrai; px < giaoPhai; px++) {
                    int localMaskX = px - maskX;
                    if (!mask.xoa[localMaskY * mask.width + localMaskX]) {
                        continue;
                    }
                    int localMapX = px - this.x;
                    this.argb[localMapY * this.chieuRong + localMapX] = 0;
                }
            }
        }
    }
}
