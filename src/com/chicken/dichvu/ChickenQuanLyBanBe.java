package com.chicken.dichvu;

import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.tienich.ChickenTienIch;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChickenQuanLyBanBe {
    private static final int MAX_FRIENDS = 60;

    public static void xuLy(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        byte action = ms.boDoc().readByte();
        if (action == 0) {
            guiDanhSach(pl);
            return;
        }
        if (action == 1 || action == 2) {
            int id = ms.boDoc().readInt();
            if (action == 1) {
                xemHoSo(pl, id);
            } else {
                xoaBan(pl, id);
            }
        }
    }

    public static void themBan(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        int friendId = ms.boDoc().readInt();
        byte ketQua = 1;
        if (friendId == pl.ma) {
            ketQua = 0;
        } else if (daLaBan(pl.ma, friendId)) {
            ketQua = 2;
        } else if (demBan(pl.ma) >= MAX_FRIENDS) {
            ketQua = 3;
        } else {
            try (Connection conn = ChickenCoSoDuLieu.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO `player_friends` (`player_id`, `friend_id`) VALUES (?, ?);")) {
                stmt.setInt(1, pl.ma);
                stmt.setInt(2, friendId);
                if (stmt.executeUpdate() > 0) {
                    ketQua = 1;
                } else {
                    ketQua = 2;
                }
            } catch (SQLException ex) {
                ketQua = 0;
            }
        }
        ChickenTinNhan out = new ChickenTinNhan(32);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(ketQua);
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    public static void xoaBanReq(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        int friendId = ms.boDoc().readInt();
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM `player_friends` WHERE `player_id` = ? AND `friend_id` = ? LIMIT 1;")) {
            stmt.setInt(1, pl.ma);
            stmt.setInt(2, friendId);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
        ChickenTinNhan out = new ChickenTinNhan(33);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(1);
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    public static void timKiem(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        String ten = ms.boDoc().readUTF().trim();
        ChickenTinNhan out = new ChickenTinNhan(36);
        DataOutputStream ds = out.boGhi();
        if (!ten.isEmpty()) {
            try (Connection conn = ChickenCoSoDuLieu.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT `id`, `name` FROM `players` WHERE `name` LIKE ? LIMIT 20;")) {
                stmt.setString(1, "%" + ten + "%");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    ds.writeInt(rs.getInt("id"));
                    ds.writeUTF(rs.getString("name"));
                }
                rs.close();
            } catch (SQLException ignored) {
            }
        }
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    private static void guiDanhSach(ChickenNguoiChoi pl) throws IOException {
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT `friend_id` FROM `player_friends` WHERE `player_id` = ?;")) {
            stmt.setInt(1, pl.ma);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("friend_id"));
            }
            rs.close();
        } catch (SQLException ignored) {
        }
        ChickenTinNhan out = new ChickenTinNhan(29);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(ids.size());
        for (int id : ids) {
            ghiHoSoBan(ds, id);
        }
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    private static void xemHoSo(ChickenNguoiChoi pl, int id) throws IOException {
        ChickenTinNhan out = new ChickenTinNhan(29);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(1);
        ghiHoSoBan(ds, id);
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    private static void xoaBan(ChickenNguoiChoi pl, int id) throws IOException {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM `player_friends` WHERE `player_id` = ? AND `friend_id` = ? LIMIT 1;")) {
            stmt.setInt(1, pl.ma);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
        guiDanhSach(pl);
    }

    private static void ghiHoSoBan(DataOutputStream ds, int id) throws IOException {
        ChickenNguoiChoi ban = ChickenNguoiChoi.layNguoiChoiTheoMa(id);
        if (ban != null) {
            ds.writeInt(ban.ma);
            ds.writeUTF(ban.ten);
            ds.writeShort(ban.head);
            ds.writeShort(ban.hat);
            ds.writeShort(ban.body);
            ds.writeShort(ban.leg);
            ds.writeShort(ban.wing);
            ds.writeShort(ban.wp);
            ds.writeInt(ban.layKinhNghiem());
            ds.writeByte(1);
            ds.writeShort(ban.clan > 0 ? (short)ban.clan : (short)0);
            return;
        }
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT p.*, cm.clan_id FROM `players` p LEFT JOIN `clan_members` cm ON cm.player_id = p.id WHERE p.id = ? LIMIT 1;")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ds.writeInt(rs.getInt("id"));
                ds.writeUTF(rs.getString("name"));
                ds.writeShort(-1);
                ds.writeShort(-1);
                ds.writeShort(-1);
                ds.writeShort(-1);
                ds.writeShort(-1);
                ds.writeShort(-1);
                ds.writeInt(0);
                ds.writeByte(0);
                int clanId = rs.getInt("clan_id");
                ds.writeShort(clanId > 0 ? (short)clanId : (short)0);
            }
            rs.close();
        } catch (SQLException ex) {
            ds.writeInt(id);
            ds.writeUTF("?");
            for (int i = 0; i < 6; ++i) {
                ds.writeShort(-1);
            }
            ds.writeInt(0);
            ds.writeByte(0);
            ds.writeShort(0);
        }
    }

    private static boolean daLaBan(int playerId, int friendId) {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM `player_friends` WHERE `player_id` = ? AND `friend_id` = ? LIMIT 1;")) {
            stmt.setInt(1, playerId);
            stmt.setInt(2, friendId);
            ResultSet rs = stmt.executeQuery();
            boolean ok = rs.next();
            rs.close();
            return ok;
        } catch (SQLException ex) {
            return false;
        }
    }

    private static int demBan(int playerId) {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS c FROM `player_friends` WHERE `player_id` = ?;")) {
            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();
            int c = rs.next() ? rs.getInt("c") : 0;
            rs.close();
            return c;
        } catch (SQLException ex) {
            return 0;
        }
    }
}
