package com.chicken;

import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.tienich.ChickenTienIch;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChickenMayChu {
    public static void main(String[] args) {
        System.out.println("Chicken LT ChickenMayChu starting...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown ChickenMayChu!");
            ChickenQuanLyMayChu.dung();
        }));
        ChickenQuanLyMayChu.khoiTao();
        ChickenQuanLyMayChu.batDau();
    }

    public static void createItem() throws IOException {
        ChickenQuanLyMayChu.khoiTao();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ChickenTienIch.layTep("cache/dataItem")));
        byte vcItem = dis.readByte();
        System.out.println(vcItem);
        int len = dis.readByte();
        for (int i = 0; i < len; ++i) {
            try {
                String ten = dis.readUTF();
                byte loai = dis.readByte();
                PreparedStatement stmt = ChickenCoSoDuLieu.getConnection().prepareStatement("INSERT INTO `item_options`(`name`, `type`) VALUES (?, ?);");
                stmt.setString(1, ten);
                stmt.setInt(2, loai);
                stmt.execute();
                continue;
            }
            catch (SQLException ex) {
                Logger.getLogger(ChickenMayChu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        int len2 = dis.readShort();
        for (int i = 0; i < len2; ++i) {
            try {
                byte loai = dis.readByte();
                byte gioiTinh = dis.readByte();
                String ten = dis.readUTF();
                String desc = dis.readUTF();
                byte cap = dis.readByte();
                byte require = dis.readByte();
                short iconId = dis.readShort();
                short part = dis.readShort();
                PreparedStatement stmt = ChickenCoSoDuLieu.getConnection().prepareStatement(
                        "INSERT INTO `items`(`name`, `type`, `gender`, `description`, `level`, `strength_required`, `icon`, `part_id`, `buy_gold`, `buy_gem`, `options`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, 0, '[]');");
                stmt.setString(1, ten);
                stmt.setInt(2, loai);
                stmt.setInt(3, gioiTinh);
                stmt.setString(4, desc);
                stmt.setInt(5, cap);
                stmt.setInt(6, require);
                stmt.setInt(7, iconId);
                stmt.setInt(8, part);
                stmt.execute();
                continue;
            }
            catch (SQLException ex) {
                Logger.getLogger(ChickenMayChu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void readDataMap() throws IOException {
        ChickenQuanLyMayChu.khoiTao();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ChickenTienIch.layTep("cache/dataMap")));
        int numMap = dis.readUnsignedByte();
        for (int i = 0; i < numMap; ++i) {
            try {
                int ma = dis.readUnsignedByte();
                short len = dis.readShort();
                byte[] map = new byte[len];
                dis.read(map);
                String mapName = dis.readUTF();
                short iconID = dis.readShort();
                byte maNen = dis.readByte();
                ChickenTienIch.luuTep("res/map/" + ma, map);
                PreparedStatement stmt = ChickenCoSoDuLieu.getConnection().prepareStatement("INSERT INTO `game_maps`(`id`, `name`, `icon`, `background`) VALUES (?, ?, ?, ?);");
                stmt.setInt(1, ma);
                stmt.setString(2, mapName);
                stmt.setInt(3, iconID);
                stmt.setInt(4, maNen);
                stmt.execute();
                continue;
            }
            catch (SQLException ex) {
                Logger.getLogger(ChickenMayChu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void readDataLevel() throws IOException {
        ChickenQuanLyMayChu.khoiTao();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ChickenTienIch.layTep("cache/dataLevel")));
        int numMap = dis.readUnsignedByte();
        for (int i = 0; i < numMap; ++i) {
            try {
                String ten = dis.readUTF();
                int kinhNghiem = dis.readInt();
                short icon = dis.readShort();
                PreparedStatement stmt = ChickenCoSoDuLieu.getConnection().prepareStatement(
                        "INSERT INTO `caption_levels`(`id`, `name`, `exp`, `icon`) VALUES (?, ?, ?, ?);");
                stmt.setInt(1, i + 1);
                stmt.setString(2, ten);
                stmt.setInt(3, kinhNghiem);
                stmt.setInt(4, icon);
                stmt.execute();
                continue;
            }
            catch (SQLException ex) {
                Logger.getLogger(ChickenMayChu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void readDataPart() throws IOException {
        ChickenQuanLyMayChu.khoiTao();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ChickenTienIch.layTep("cache/dataPart")));
        int numMap = dis.readShort();
        for (int i = 0; i < numMap; ++i) {
            try {
                byte loai = dis.readByte();
                int num = ChickenMayChu.getNumber(loai);
                JSONArray jArr = new JSONArray();
                for (int j = 0; j < num; ++j) {
                    short ma = dis.readShort();
                    byte dx = dis.readByte();
                    byte dy = dis.readByte();
                    JSONObject doiTuong = new JSONObject();
                    doiTuong.put("id", ma);
                    doiTuong.put("dx", dx);
                    doiTuong.put("dy", dy);
                    jArr.add((Object)doiTuong);
                }
                PreparedStatement stmt = ChickenCoSoDuLieu.getConnection().prepareStatement(
                        "INSERT INTO `avatar_parts`(`type`, `part_data`) VALUES (?, ?);");
                stmt.setInt(1, loai);
                stmt.setString(2, jArr.toJSONString());
                stmt.execute();
                continue;
            }
            catch (SQLException ex) {
                Logger.getLogger(ChickenMayChu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void readDataImage() throws IOException {
        ChickenQuanLyMayChu.khoiTao();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ChickenTienIch.layTep("cache/dataImage")));
        int numMap = dis.readShort();
        for (int i = 0; i < numMap; ++i) {
            try {
                int ma = dis.readUnsignedByte();
                short x = dis.readShort();
                short y = dis.readShort();
                short w = dis.readShort();
                short h = dis.readShort();
                PreparedStatement stmt = ChickenCoSoDuLieu.getConnection().prepareStatement("INSERT INTO `sprite_images`(`image_id`, `x`, `y`, `width`, `height`) VALUES (?, ?, ?, ?, ?);");
                stmt.setInt(1, ma);
                stmt.setInt(2, x);
                stmt.setInt(3, y);
                stmt.setInt(4, w);
                stmt.setInt(5, h);
                stmt.execute();
                continue;
            }
            catch (SQLException ex) {
                Logger.getLogger(ChickenMayChu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static int getNumber(int loai) {
        return switch (loai) {
            case 0 -> 4;
            case 1, 2 -> 10;
            case 3 -> 7;
            case 4 -> 2;
            case 5 -> 1;
            default -> 0;
        };
    }
}

