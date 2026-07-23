package com.chicken.bando;

import com.chicken.tienich.ChickenTienIch;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class ChickenDuLieuBanDo {
    public static ArrayList<MapDataEntry> entrys;
    public static ArrayList<MapBrickEntry> brickEntrys;
    public static final short[] undestroyTile;

    public static boolean isTileDestroy(int ma) {
        for (int i = 0; i < undestroyTile.length; ++i) {
            if (ma != undestroyTile[i]) continue;
            return true;
        }
        return false;
    }

    public static MapBrickEntry getMapBrickEntry(int ma) {
        for (MapBrickEntry me : brickEntrys) {
            if (me.ma != ma) continue;
            return me;
        }
        return null;
    }

    public static synchronized void loadMapBrick(int ma) {
        if (existsMapBrick(ma)) {
            return;
        }
        try {
            BufferedImage img = docAnhMapBrick(ma);
            if (img == null) {
                throw new IOException("Không tìm thấy ảnh map brick: " + ma + ".png");
            }
            int W = img.getWidth();
            int H = img.getHeight();
            int[] argb = new int[W * H];
            img.getRGB(0, 0, W, H, argb, 0, W);
            brickEntrys.add(new MapBrickEntry(ma, argb, W, H));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Đọc ảnh va chạm map không phụ thuộc thư mục hiện tại khi chạy server.
     * Termux có thể khởi động JAR từ thư mục khác nên đường dẫn tương đối
     * res/icon/map trước đây làm nhân vật mới không dựng được map luyện tập.
     */
    private static BufferedImage docAnhMapBrick(int ma) throws Exception {
        String duongDan = "res/icon/map/" + ma + ".png";
        File[] ungVien = new File[]{
            new File(duongDan),
            new File(System.getProperty("user.dir", "."), duongDan),
            new File("Chicken_lt", duongDan),
            new File("../Chicken_lt", duongDan)
        };
        for (File tep : ungVien) {
            if (tep.isFile()) {
                BufferedImage img = ImageIO.read(tep);
                if (img != null) {
                    return img;
                }
            }
        }

        try {
            URI viTriMa = ChickenDuLieuBanDo.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI();
            File thuMucMa = new File(viTriMa);
            if (thuMucMa.isFile()) {
                thuMucMa = thuMucMa.getParentFile();
            }
            for (int i = 0; thuMucMa != null && i < 4; i++, thuMucMa = thuMucMa.getParentFile()) {
                File tep = new File(thuMucMa, duongDan);
                if (tep.isFile()) {
                    BufferedImage img = ImageIO.read(tep);
                    if (img != null) {
                        return img;
                    }
                }
                File tepTrongProject = new File(thuMucMa, "Chicken_lt/" + duongDan);
                if (tepTrongProject.isFile()) {
                    BufferedImage img = ImageIO.read(tepTrongProject);
                    if (img != null) {
                        return img;
                    }
                }
            }
        }
        catch (Exception ignored) {
        }

        String[] taiNguyen = new String[]{
            "/" + duongDan,
            "/icon/map/" + ma + ".png"
        };
        for (String taiNguyenPath : taiNguyen) {
            try (InputStream in = ChickenDuLieuBanDo.class.getResourceAsStream(taiNguyenPath)) {
                if (in != null) {
                    BufferedImage img = ImageIO.read(in);
                    if (img != null) {
                        return img;
                    }
                }
            }
        }
        return null;
    }

    public static boolean existsMapBrick(int ma) {
        for (MapBrickEntry me : brickEntrys) {
            if (me.ma != ma) continue;
            return true;
        }
        return false;
    }

    static {
        undestroyTile = new short[]{70, 71, 73, 74, 75, 77, 78, 79, 97, 121, 122, 123, 124, 130, 131, 132, 135, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168};
    }

    public static final class MapBrickEntry {
        public int ma;
        public int[] dat;
        public int Width;
        public int Height;

        MapBrickEntry(int ma, int[] dat, int W, int H) {
            this.ma = ma;
            this.dat = dat;
            this.Width = W;
            this.Height = H;
        }
    }

    public static final class MapDataEntry {
        public short backGroundID;
        public byte bgID;
        public byte[] duLieu;
        public short iconID;
        public boolean isCheckFilter;
        public boolean isWaterClass;
        public int mapH;
        public byte mapID;
        public String mapName;
        public int mapW;
        public short[] values = new short[5];
        public short water_class;
        public short yBackGround;
        public short yCloud;
        public short yWater;

        public MapDataEntry(byte[] duLieu, byte mapID, String mapName, short icon, byte bgID) {
            this.duLieu = duLieu;
            this.mapID = mapID;
            this.bgID = bgID;
            this.iconID = icon;
            this.mapName = mapName;
            this.mapW = ChickenTienIch.getShort(0, duLieu) / 24;
            this.mapH = ChickenTienIch.getShort(2, duLieu) / 24;
            System.out.println("map ID= " + mapID + " mapName= " + mapName + " bgID= " + bgID);
            this.khoiTao();
        }

        public void khoiTao() {
            this.backGroundID = this.values[0];
            this.yCloud = this.values[2];
            this.yBackGround = this.values[1];
            this.water_class = this.values[4];
            this.yWater = this.values[3];
            this.isWaterClass = this.water_class != -1;
        }
    }
}

