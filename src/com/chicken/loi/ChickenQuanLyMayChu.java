package com.chicken.loi;

import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.dulieu.ChickenTieuDeCap;
import com.chicken.dulieu.ChickenBoPhan;
import com.chicken.dulieu.ChickenAnhBoPhan;
import com.chicken.dulieu.ChickenAnhNho;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.bando.ChickenDuLieuBanDo;
import com.chicken.mang.ChickenPhien;
import com.chicken.mang.kenh.ChickenMayChuNetty;
import com.chicken.phong.ChickenQuanLyPhong;
import com.chicken.nhapvai.ChickenBanDoRPG;
import com.chicken.cuahang.ChickenCuaHang;
import com.chicken.tienich.ChickenDuLieuJson;
import com.chicken.tienich.ChickenTienIch;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChickenQuanLyMayChu {
    public static final String GAME_NAME = "Chicken LT";
    private static boolean goLoi;
    protected static String mayChu;
    protected static short cong;
    protected static String mysql_host;
    protected static String mysql_user;
    protected static String mysql_pass;
    protected static String mysql_database;
    protected static int numClients;
    protected static ArrayList<ChickenPhien> clients;
    protected static ChickenMayChuNetty nettyServer;
    protected static boolean batDau;
    protected static int ma;
    public static int dailyGold;
    public static int dailyGem;
    public static int clanCreateGold;
    public static int wheelGemCost;
    public static int eventIntervalMinutes;
    public static int eventDurationMinutes;
    public static int worldTreasureIntervalMinutes;
    public static int worldBossHp;
    public static byte vBig;
    public static byte vData;
    public static byte vItem;
    public static byte vMap;
    public static int[] nBig;
    public static String[] dataSize;
    public static HashMap<Integer, ChickenMauThuocTinhVatPham> iOptionTemplates;
    public static HashMap<Integer, ChickenMauVatPham> itemTemplates;
    public static HashMap<Integer, ChickenBoPhan> parts;
    public static final ChickenCuaHang SHOP_EQUIP;
    public static final ChickenCuaHang SHOP_ITEM;
    public static byte maxElementFight;
    public static byte maxPlayers;
    protected static byte nPlayersInitRoom;
    protected static byte initMap;
    protected static byte initMapBoss;

    private static void loadDataItem() {
        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `item_options`");
                 ResultSet res = stmt.executeQuery()) {
                while (res.next()) {
                    ChickenMauThuocTinhVatPham optionTemplate = new ChickenMauThuocTinhVatPham();
                    optionTemplate.ma = res.getInt("id");
                    optionTemplate.ten = res.getString("name");
                    optionTemplate.loai = res.getInt("type");
                    iOptionTemplates.put(optionTemplate.ma, optionTemplate);
                }
            }
            ArrayList<ChickenMauVatPham> weapons = new ArrayList<ChickenMauVatPham>();
            ArrayList<ChickenMauVatPham> clothes = new ArrayList<ChickenMauVatPham>();
            ArrayList<ChickenMauVatPham> hairs = new ArrayList<ChickenMauVatPham>();
            ArrayList<ChickenMauVatPham> balos = new ArrayList<ChickenMauVatPham>();
            ArrayList<ChickenMauVatPham> ngocs = new ArrayList<ChickenMauVatPham>();
            ArrayList<ChickenMauVatPham> vatPhams = new ArrayList<ChickenMauVatPham>();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `items`");
                 ResultSet res = stmt.executeQuery()) {
                while (res.next()) {
                    int ma = res.getInt("id");
                    String ten = res.getString("name");
                    String desc = res.getString("description");
                    byte loai = res.getByte("type");
                    byte cap = res.getByte("level");
                    short icon = res.getShort("icon");
                    short part = res.getShort("part_id");
                    int require = res.getInt("strength_required");
                    byte gioiTinh = res.getByte("gender");
                    int vang = res.getInt("buy_gold");
                    int ngoc = res.getInt("buy_gem");
                    ChickenMauVatPham vatPham = new ChickenMauVatPham((short)ma, loai, gioiTinh, ten, desc, cap, require, icon, part, false);
                    JSONArray arr = (JSONArray)JSON.parse((String)res.getString("options"));
                    for (int i = 0; i < arr.size(); ++i) {
                        ChickenDuLieuJson p = new ChickenDuLieuJson((JSONObject)arr.get(i));
                        vatPham.thuocTinhs.add(new ChickenThuocTinhVatPham(p.getInt("id"), p.getInt("param")));
                    }
                    vatPham.buyGem = ngoc;
                    vatPham.buyGold = vang;
                    itemTemplates.put(ma, vatPham);
                    if (vang + ngoc <= 0) continue;
                    if (loai == 10) {
                        vatPhams.add(vatPham);
                        continue;
                    }
                    if (loai == 12) {
                        ngocs.add(vatPham);
                        continue;
                    }
                    if (loai == 1 || loai == 2) {
                        clothes.add(vatPham);
                        continue;
                    }
                    if (loai == 0 || loai == 3) {
                        hairs.add(vatPham);
                        continue;
                    }
                    if (loai == 4 || ma == 349 || ma == 399 || ma == 350 || ma == 351 || ma == 352) {
                        balos.add(vatPham);
                        continue;
                    }
                    if (loai != 5) continue;
                    weapons.add(vatPham);
                }
            }
            ChickenCuaHang.SHOP_ITEM.themTab("vật\nPhẩm", vatPhams);
            ChickenCuaHang.SHOP_ITEM.themTab("Ngọc", ngocs);
            ChickenCuaHang.SHOP_EQUIP.themTab("Giáp", clothes);
            ChickenCuaHang.SHOP_EQUIP.themTab("Nón\nTóc", hairs);
            ChickenCuaHang.SHOP_EQUIP.themTab("Hỗ\nTrợ", balos);
            ChickenCuaHang.SHOP_EQUIP.themTab("Súng", weapons);
        }
        catch (SQLException ex) {
            Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void loadDataMap() {
        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection()) {
            damBaoMapBossRuaRong(conn);
            try (PreparedStatement stmt = conn.prepareStatement(
                        "SELECT * FROM `game_maps` ORDER BY `id`");
                 ResultSet res = stmt.executeQuery()) {
                ChickenDuLieuBanDo.entrys = new ArrayList();
                ChickenDuLieuBanDo.brickEntrys = new ArrayList();
                while (res.next()) {
                    byte maBanDo = res.getByte("id");
                    String ten = res.getString("name");
                    short icon = res.getShort("icon");
                    byte background = res.getByte("background");
                    byte[] ab = ChickenTienIch.layTep("res/map/" + maBanDo);
                    if (ab == null || ab.length < 5) {
                        Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(
                                Level.WARNING,
                                "Bo qua map {0} vi thieu res/map/{0}",
                                maBanDo & 0xFF
                        );
                        continue;
                    }
                    ChickenDuLieuBanDo.MapDataEntry map =
                            new ChickenDuLieuBanDo.MapDataEntry(
                                    ab, maBanDo, ten, icon, background);
                    ChickenDuLieuBanDo.entrys.add(map);
                }
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Database cũ của người dùng chưa có map 58 nên client không thể hiện
     * "Boss Rùa x Boss Rồng" trong danh sách chọn map của phòng boss.
     * Upsert tại lúc khởi động để không bắt người dùng import lại toàn bộ SQL.
     */
    private static void damBaoMapBossRuaRong(java.sql.Connection conn)
            throws SQLException {
        if (conn == null) {
            return;
        }
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO `game_maps` (`id`, `name`, `icon`, `background`) "
                + "VALUES (58, ?, 1974, 7) "
                + "ON DUPLICATE KEY UPDATE "
                + "`name` = VALUES(`name`), "
                + "`icon` = VALUES(`icon`), "
                + "`background` = VALUES(`background`)")) {
            stmt.setString(1, "Boss Rùa x Boss Rồng");
            stmt.executeUpdate();
        }
    }

    private static void loadDataCaptionLevel() {
        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection()) {
            // Database đang chạy có thể vẫn giữ dữ liệu cũ từ trước.
            // Chỉ đổi các cấp có chữ "Phàm Nhân" thành dấu chấm, giữ nguyên cấp khác.
            try (PreparedStatement capNhat = conn.prepareStatement(
                    "UPDATE `caption_levels` SET `name` = '.' WHERE `name` LIKE ?")) {
                capNhat.setString(1, "%Phàm Nhân%");
                capNhat.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `caption_levels`");
                 ResultSet res = stmt.executeQuery()) {
            while (res.next()) {
                int kinhNghiem = res.getInt("exp");
                String ten = res.getString("name");
                short icon = res.getShort("icon");
                int ma = res.getInt("id");
                ChickenTieuDeCap cap = new ChickenTieuDeCap();
                cap.kinhNghiem = kinhNghiem;
                cap.ten = ten;
                cap.icon = icon;
                ChickenTieuDeCap.levels.put(ma, cap);
            }
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void loadDataPart() {
        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `avatar_parts`");
             ResultSet res = stmt.executeQuery()) {
            while (res.next()) {
                int ma = res.getInt("id");
                String duLieu = res.getString("part_data");
                byte loai = res.getByte("type");
                JSONArray jArr = (JSONArray)JSON.parse((String)duLieu);
                ChickenBoPhan part = new ChickenBoPhan(loai);
                for (int i = 0; i < part.pi.length; ++i) {
                    JSONObject doiTuong = (JSONObject)jArr.get(i);
                    part.pi[i] = new ChickenAnhBoPhan();
                    part.pi[i].ma = Short.parseShort(doiTuong.get("id").toString());
                    part.pi[i].dx = Byte.parseByte(doiTuong.get("dx").toString());
                    part.pi[i].dy = Byte.parseByte(doiTuong.get("dy").toString());
                }
                parts.put(ma, part);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void loadDataImage() {
        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `sprite_images`");
             ResultSet res = stmt.executeQuery()) {
            while (res.next()) {
                int ma = res.getInt("id");
                int maAnh = res.getInt("image_id");
                int x = res.getInt("x");
                int y = res.getInt("y");
                int w = res.getInt("width");
                int h = res.getInt("height");
                ChickenAnhNho small = new ChickenAnhNho();
                small.maAnh = (byte)maAnh;
                small.x = x;
                small.y = y;
                small.w = w;
                small.h = h;
                ChickenAnhNho.smallImg.put(ma, small);
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void loadConfigFile() {
        byte[] ab = ChickenTienIch.layTep("config.conf");
        if (ab == null) {
            System.out.println("Config file not found!");
            System.exit(0);
        }
        String duLieu = new String(ab);
        HashMap<String, String> configMap = new HashMap<String, String>();
        StringBuilder sbd = new StringBuilder();
        boolean bo = false;
        for (int i = 0; i <= duLieu.length(); ++i) {
            char es;
            if (i == duLieu.length() || (es = duLieu.charAt(i)) == '\n') {
                int j;
                bo = false;
                String sbf = sbd.toString().trim();
                if (sbf != null && !sbf.equals("") && sbf.charAt(0) != '#' && (j = sbf.indexOf(58)) > 0) {
                    String khoa = sbf.substring(0, j).trim();
                    String giaTri = sbf.substring(j + 1).trim();
                    configMap.put(khoa, giaTri);
                    System.out.println("config: " + khoa + "-" + giaTri);
                }
                sbd.setLength(0);
                continue;
            }
            if (es == '#') {
                bo = true;
            }
            if (bo) continue;
            sbd.append(es);
        }
        goLoi = ChickenQuanLyMayChu.cfgBool(configMap, false, "debug-mode", "debug");
        mayChu = ChickenQuanLyMayChu.cfgStr(configMap, "localhost", "server-host", "host");
        cong = ChickenQuanLyMayChu.cfgShort(configMap, (short)14445, "server-port", "post");
        mysql_host = ChickenQuanLyMayChu.cfgStr(configMap, "127.0.0.1", "database-host", "mysql-host");
        mysql_user = ChickenQuanLyMayChu.cfgStr(configMap, "root", "database-user", "mysql-user");
        mysql_pass = ChickenQuanLyMayChu.cfgStr(configMap, "", "database-password", "mysql-password");
        mysql_database = ChickenQuanLyMayChu.cfgStr(configMap, "chicken3", "database-name", "mysql-database");
        vBig = ChickenQuanLyMayChu.cfgByte(configMap, (byte)0, "client-version-big", "vBig");
        vData = ChickenQuanLyMayChu.cfgByte(configMap, (byte)0, "client-version-data", "vData");
        vItem = ChickenQuanLyMayChu.cfgByte(configMap, (byte)0, "client-version-item", "vItem");
        vMap = ChickenQuanLyMayChu.cfgByte(configMap, (byte)0, "client-version-map", "vMap");
        dailyGold = ChickenQuanLyMayChu.cfgInt(configMap, dailyGold, "reward-daily-gold", "daily-gold");
        dailyGem = ChickenQuanLyMayChu.cfgInt(configMap, dailyGem, "reward-daily-gem", "daily-gem");
        eventIntervalMinutes = ChickenQuanLyMayChu.cfgInt(configMap, eventIntervalMinutes, "event-rotation-minutes", "event-interval-minutes");
        eventDurationMinutes = ChickenQuanLyMayChu.cfgInt(configMap, eventDurationMinutes, "event-duration-minutes");
        clanCreateGold = ChickenQuanLyMayChu.cfgInt(configMap, clanCreateGold, "clan-create-cost-gold", "clan-create-gold");
        wheelGemCost = ChickenQuanLyMayChu.cfgInt(configMap, wheelGemCost, "lucky-wheel-gem-cost", "wheel-gem-cost");
        worldTreasureIntervalMinutes = ChickenQuanLyMayChu.cfgInt(configMap, worldTreasureIntervalMinutes, "world-treasure-spawn-minutes", "world-treasure-interval-minutes");
        worldBossHp = ChickenQuanLyMayChu.cfgInt(configMap, worldBossHp, "world-boss-hp");
    }

    private static String cfgStr(HashMap<String, String> map, String def, String... keys) {
        for (String khoa : keys) {
            if (map.containsKey(khoa)) {
                return map.get(khoa);
            }
        }
        return def;
    }

    private static int cfgInt(HashMap<String, String> map, int def, String... keys) {
        String v = ChickenQuanLyMayChu.cfgStr(map, null, keys);
        return v != null ? Integer.parseInt(v) : def;
    }

    private static short cfgShort(HashMap<String, String> map, short def, String... keys) {
        String v = ChickenQuanLyMayChu.cfgStr(map, null, keys);
        return v != null ? Short.parseShort(v) : def;
    }

    private static byte cfgByte(HashMap<String, String> map, byte def, String... keys) {
        String v = ChickenQuanLyMayChu.cfgStr(map, null, keys);
        return v != null ? Byte.parseByte(v) : def;
    }

    private static boolean cfgBool(HashMap<String, String> map, boolean def, String... keys) {
        String v = ChickenQuanLyMayChu.cfgStr(map, null, keys);
        return v != null ? Boolean.parseBoolean(v) : def;
    }

    public static int getOnlineCount() {
        return numClients;
    }

    public static boolean isDebug() {
        return goLoi;
    }

    private static void damBaoTaiKhoanTest() {
        String sql = "INSERT INTO `accounts` (`id`, `username`, `password`, `is_banned`, `is_online`, `is_admin`) "
                + "VALUES (?, ?, '1', 0, 0, 1) "
                + "ON DUPLICATE KEY UPDATE `username` = VALUES(`username`), `password` = '1', "
                + "`is_banned` = 0, `is_online` = 0, `is_admin` = 1";
        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection()) {
            try (PreparedStatement reset = conn.prepareStatement("UPDATE `accounts` SET `is_online` = 0")) {
                reset.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, 1);
                stmt.setString(2, "admin");
                stmt.executeUpdate();
                stmt.setInt(1, 2);
                stmt.setString(2, "admin1");
                stmt.executeUpdate();
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(Level.SEVERE, "Khong the tao tai khoan test", ex);
        }
    }

    public static void khoiTao() {
        for (int i = 0; i < 4; ++i) {
            File[] files;
            int kichThuoc = 0;
            int numberBig = 0;
            for (File file : files = new File("res/data/" + (i + 1) + "/").listFiles()) {
                kichThuoc = (int)((long)kichThuoc + file.length());
                ++numberBig;
            }
            ChickenQuanLyMayChu.dataSize[i] = ChickenTienIch.doiThanhChuoiRutGon(kichThuoc);
            ChickenQuanLyMayChu.nBig[i] = numberBig;
        }
        batDau = false;
        ChickenQuanLyMayChu.loadConfigFile();
        ChickenCoSoDuLieu.khoiTao(mysql_host, mysql_database, mysql_user, mysql_pass);
        ChickenQuanLyMayChu.damBaoTaiKhoanTest();
        ChickenQuanLyMayChu.loadDataItem();
        ChickenQuanLyMayChu.setDataItem();
        ChickenQuanLyMayChu.loadDataMap();
        ChickenQuanLyMayChu.setDataMap();
        ChickenQuanLyMayChu.loadDataCaptionLevel();
        ChickenQuanLyMayChu.setDataCaptionLevel();
        ChickenQuanLyMayChu.loadDataPart();
        ChickenQuanLyMayChu.setDataPart();
        ChickenQuanLyMayChu.loadDataImage();
        ChickenQuanLyMayChu.setDataImage();
        ChickenBanDoRPG.khoiTaoKhu();
        ChickenQuanLyPhong.khoiTao();
    }

    public static void setDataItem() {
        try {
            ByteArrayOutputStream dos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(dos);
            ds.writeByte(vItem);
            ds.writeByte(iOptionTemplates.size());
            for (ChickenMauThuocTinhVatPham option : iOptionTemplates.values()) {
                ds.writeUTF(option.ten);
                ds.writeByte(option.loai);
            }
            ds.writeShort(itemTemplates.size());
            for (ChickenMauVatPham mau : itemTemplates.values()) {
                ds.writeByte(mau.loai);
                ds.writeByte(mau.gioiTinh);
                ds.writeUTF(mau.ten);
                ds.writeUTF(mau.moTa);
                ds.writeByte(mau.cap);
                ds.writeByte(mau.strRequire);
                ds.writeShort(mau.iconID);
                ds.writeShort(mau.part);
            }
            ds.flush();
            ds.close();
            dos.close();
            byte[] ab = dos.toByteArray();
            ChickenTienIch.luuTep("cache/dataItem", ab);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setDataMap() {
        try {
            ByteArrayOutputStream dos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(dos);
            int len = ChickenDuLieuBanDo.entrys.size();
            ds.writeByte(len);
            for (int i = 0; i < len; ++i) {
                ChickenDuLieuBanDo.MapDataEntry map = ChickenDuLieuBanDo.entrys.get(i);
                ds.writeByte(map.mapID);
                ds.writeShort(map.duLieu.length);
                ds.write(map.duLieu);
                ds.writeUTF(map.mapName);
                ds.writeShort(map.iconID);
                ds.writeByte(map.bgID);
            }
            ds.flush();
            ds.close();
            dos.close();
            byte[] ab = dos.toByteArray();
            ChickenTienIch.luuTep("cache/dataMap", ab);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setDataCaptionLevel() {
        try {
            ByteArrayOutputStream dos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(dos);
            ds.writeByte(ChickenTieuDeCap.levels.size());
            for (ChickenTieuDeCap cap : ChickenTieuDeCap.levels.values()) {
                ds.writeUTF(cap.ten);
                ds.writeInt(cap.kinhNghiem);
                ds.writeShort(cap.icon);
            }
            ds.flush();
            ds.close();
            dos.close();
            byte[] ab = dos.toByteArray();
            ChickenTienIch.luuTep("cache/dataLevel", ab);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setDataPart() {
        try {
            ByteArrayOutputStream dos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(dos);
            ds.writeShort(parts.size());
            for (ChickenBoPhan part : parts.values()) {
                ds.writeByte(part.loai);
                for (ChickenAnhBoPhan p : part.pi) {
                    ds.writeShort(p.ma);
                    ds.writeByte(p.dx);
                    ds.writeByte(p.dy);
                }
            }
            ds.flush();
            ds.close();
            dos.close();
            byte[] ab = dos.toByteArray();
            ChickenTienIch.luuTep("cache/dataPart", ab);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void setDataImage() {
        try {
            ByteArrayOutputStream dos = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(dos);
            ds.writeShort(ChickenAnhNho.smallImg.size());
            for (ChickenAnhNho small : ChickenAnhNho.smallImg.values()) {
                ds.writeByte(small.maAnh);
                ds.writeShort(small.x);
                ds.writeShort(small.y);
                ds.writeShort(small.w);
                ds.writeShort(small.h);
            }
            ds.flush();
            ds.close();
            dos.close();
            byte[] ab = dos.toByteArray();
            ChickenTienIch.luuTep("cache/dataImage", ab);
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenQuanLyMayChu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static synchronized int nextClientId() {
        return ++ma;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void onClientConnected(ChickenPhien cl) {
        ArrayList<ChickenPhien> arrayList = clients;
        synchronized (arrayList) {
            clients.add(cl);
            ++numClients;
            ChickenQuanLyMayChu.logConnection("Ket noi " + cl.moTa() + " | online=" + numClients);
        }
    }

    public static void onClientLoggedIn(ChickenPhien cl) {
        ChickenQuanLyMayChu.logConnection("Dang nhap " + cl.moTa() + " | online=" + numClients);
    }

    public static void batDau() {
        System.out.println(GAME_NAME + " — Netty port=" + cong);
        try {
            clients = new ArrayList();
            ma = 0;
            numClients = 0;
            batDau = true;
            nettyServer = new ChickenMayChuNetty();
            nettyServer.batDau(mayChu, cong);
            ChickenQuanLyMayChu.log(GAME_NAME + " start OK!");
            Thread.currentThread().join();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dung() {
        if (batDau) {
            ChickenQuanLyMayChu.close();
            batDau = false;
            System.gc();
        }
    }

    protected static void close() {
        try {
            if (nettyServer != null) {
                nettyServer.dung();
                nettyServer = null;
            }
            if (clients != null) {
                while (!clients.isEmpty()) {
                    ChickenPhien c = clients.getFirst();
                    c.close();
                    --numClients;
                }
                clients = null;
            }
            ChickenCoSoDuLieu.close();
            System.out.println("ChickenMayChu stopped");
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void log(String s) {
        if (goLoi) {
            System.out.println(s);
        }
    }

    public static void logConnection(String s) {
        System.out.println("[Client] " + s);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void disconnect(ChickenPhien cl) {
        ArrayList<ChickenPhien> arrayList = clients;
        synchronized (arrayList) {
            clients.remove(cl);
            --numClients;
            ChickenQuanLyMayChu.logConnection("Ngat ket noi " + cl.moTa() + " | online=" + numClients);
        }
    }

    static {
        dailyGold = 5000;
        dailyGem = 10;
        clanCreateGold = 500000;
        wheelGemCost = 5;
        eventIntervalMinutes = 90;
        eventDurationMinutes = 30;
        worldTreasureIntervalMinutes = 8;
        worldBossHp = 50000;
        maxElementFight = 8;
        maxPlayers = 8;
        nPlayersInitRoom = 2;
        initMap = 1;
        initMapBoss = 30;
        nBig = new int[4];
        dataSize = new String[4];
        iOptionTemplates = new HashMap();
        itemTemplates = new HashMap();
        parts = new HashMap();
        SHOP_EQUIP = new ChickenCuaHang();
        SHOP_ITEM = new ChickenCuaHang();
    }
}
