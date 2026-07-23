package com.chicken.bando;

import com.chicken.tienich.ChickenTienIch;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class ChickenQuanLyBanDo {
    private static final short[] DEFAULT_SPAWN_X = new short[]{220, 600, 320, 720, 150, 850, 460, 980};
    private static final short[] DEFAULT_SPAWN_Y = new short[]{300, 300, 260, 260, 320, 320, 280, 280};
    private static final Map<String, HoleMask> HOLE_MASK_CACHE = new HashMap<>();

    private final ArrayList<MapEntry> mucs = new ArrayList<>();
    private short[] spawnX = DEFAULT_SPAWN_X;
    private short[] spawnY = DEFAULT_SPAWN_Y;
    private byte maBanDo;
    private byte maNen;
    private int chieuRong = 1200;
    private int chieuCao = 700;

    public ChickenQuanLyBanDo(int mapID) {
        this.setMapId(mapID);
    }

    public synchronized void setMapId(int mapID) {
        this.maBanDo = (byte) mapID;
        this.mucs.clear();
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

    /**
     * Cập nhật mặt nạ va chạm của server bằng đúng hình lỗ mà client dùng.
     * Màu đen đặc trong ảnh hole là vùng bị xóa; viền đỏ chỉ tạo mép lỗ và
     * không được xóa khỏi mặt nạ. Tile nằm trong undestroyTile vẫn va chạm
     * bình thường nhưng không bị phá.
     */
    public synchronized void phaDiaHinh(int tamX, int tamY, byte loaiDan) {
        if (tamX < 0 || tamY < 0 || tamX >= this.chieuRong || tamY >= this.chieuCao) {
            return;
        }
        HoleMask mask = layMatNaLoTheoLoaiDan(loaiDan & 0xFF);
        if (mask == null) {
            return;
        }
        int batDauX = tamX - mask.width / 2;
        int batDauY = tamY - mask.height / 2;
        for (MapEntry muc : this.mucs) {
            muc.xoaTheoMatNa(batDauX, batDauY, mask);
        }
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
                tenTep = "smallhole.png";
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

    private static final class MapEntry {
        private final short x;
        private final short y;
        private final short chieuRong;
        private final short chieuCao;
        private final int[] argb;
        private final boolean coThePha;

        private MapEntry(
                short x,
                short y,
                short chieuRong,
                short chieuCao,
                int[] argb,
                boolean coThePha
        ) {
            this.x = x;
            this.y = y;
            this.chieuRong = chieuRong;
            this.chieuCao = chieuCao;
            // Mỗi trận phải có mặt nạ riêng; không được sửa mảng cache dùng chung.
            this.argb = argb == null ? null : argb.clone();
            this.coThePha = coThePha;
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
            int alpha = this.argb[localY * this.chieuRong + localX] >>> 24;
            // Pixel trong suốt hoặc viền mờ của ảnh map không phải vật cản.
            // Chỉ phần địa hình đủ đặc mới được dùng làm mặt nạ va chạm.
            return alpha >= 128;
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
