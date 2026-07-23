package com.chicken.dichvu;

import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.tienich.ChickenTienIch;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ChickenQuanLyBietDoi {
    private static final int MAX_MEMBER = 30;
    private static final AtomicInteger nextMsgId = new AtomicInteger(1);
    private static final AtomicInteger nextInviteCode = new AtomicInteger(1000);
    private static final Map<Integer, List<ChickenTinNhanClan>> clanMessages = new ConcurrentHashMap<>();
    private static final Map<Integer, LoiMoiClan> loiMoiClan = new ConcurrentHashMap<>();

    private static class ChickenTinNhanClan {
        int id;
        byte type;
        int playerId;
        String playerName;
        byte role;
        int time;
        String text;
        byte color;
    }

    private static class LoiMoiClan {
        int clanId;
        int targetId;
        int inviterId;
        String inviterName;
    }

    public static void taiClan(ChickenNguoiChoi pl) {
        pl.clan = -1;
        pl.clanRole = 0;
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT `clan_id`, `member_role` FROM `clan_members` WHERE `player_id` = ? LIMIT 1;")) {
            stmt.setInt(1, pl.ma);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                pl.clan = rs.getInt("clan_id");
                pl.clanRole = rs.getByte("member_role");
            }
            rs.close();
        } catch (SQLException ignored) {
        }
    }

    public static void yeuCauThongTin(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        int clanId = pl.clan;
        if (ms.boDoc().available() >= 2) {
            clanId = ms.boDoc().readShort();
        }
        if (clanId <= 0 && pl.clan > 0) {
            clanId = pl.clan;
        }
        guiThongTinClan(pl, clanId);
    }

    public static void dongBoClan(ChickenNguoiChoi pl) throws IOException {
        guiThongTinClan(pl, pl.clan > 0 ? pl.clan : -1);
    }

    public static void topClan(ChickenNguoiChoi pl) throws IOException {
        System.out.println("topClan: player=" + pl.ten + " clan=" + pl.clan);
        guiThongTinClan(pl, pl.clan > 0 ? pl.clan : -1);
        guiTopClanDanhSach(pl);
        if (pl.clan <= 0) {
            guiKetQuaTimKiem(pl, "");
        }
    }

    private static void guiTopClanDanhSach(ChickenNguoiChoi pl) throws IOException {
        ChickenTinNhan out = new ChickenTinNhan(-117);
        DataOutputStream ds = out.boGhi();
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.*, (SELECT COUNT(*) FROM clan_members cm WHERE cm.clan_id = c.id) AS members, " +
                     "(SELECT p.name FROM players p WHERE p.id = c.leader_player_id LIMIT 1) AS leader_name " +
                     "FROM clans c ORDER BY c.exp DESC, c.level DESC LIMIT 20;")) {
            ResultSet rs = stmt.executeQuery();
            java.util.ArrayList<Object[]> list = new java.util.ArrayList<>();
            int rank = 1;
            while (rs.next()) {
                java.sql.Timestamp created = rs.getTimestamp("created_at");
                list.add(new Object[]{
                    rank++,
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("slogan") != null ? rs.getString("slogan") : "",
                    (short)0,
                    rs.getInt("members"),
                    MAX_MEMBER,
                    ChickenTienIch.dinhDangTien(rs.getInt("exp")),
                    created != null ? (int)(created.getTime() / 1000L) : 0,
                    rs.getString("leader_name") != null ? rs.getString("leader_name") : "",
                    rs.getByte("level"),
                    (byte)0
                });
            }
            rs.close();
            ds.writeByte(list.size());
            for (Object[] row : list) {
                ds.writeInt((Integer)row[0]);
                ds.writeInt((Integer)row[1]);
                ds.writeUTF((String)row[2]);
                ds.writeUTF((String)row[3]);
                ds.writeShort((Short)row[4]);
                ds.writeByte((Integer)row[5]);
                ds.writeByte((Integer)row[6]);
                ds.writeUTF((String)row[7]);
                ds.writeInt((Integer)row[8]);
                ds.writeUTF((String)row[9]);
                ds.writeByte((Byte)row[10]);
                ds.writeByte((Byte)row[11]);
            }
        } catch (SQLException ex) {
            ds.writeByte(0);
        }
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    public static void timKiem(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        try {
            String text = ms.boDoc().available() > 0 ? ms.boDoc().readUTF().trim() : "";
            guiKetQuaTimKiem(pl, text);
        } catch (Exception ex) {
            ChickenTinNhan out = new ChickenTinNhan(-113);
            DataOutputStream ds = out.boGhi();
            ds.writeByte(0);
            ds.flush();
            pl.dichVu.guiTin(out);
        }
    }

    private static void guiKetQuaTimKiem(ChickenNguoiChoi pl, String text) throws IOException {
        java.util.List<String[]> data = new java.util.ArrayList<>();
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.*, (SELECT COUNT(*) FROM clan_members cm WHERE cm.clan_id = c.id) AS members, " +
                     "(SELECT p.name FROM players p WHERE p.id = c.leader_player_id LIMIT 1) AS leader_name " +
                     "FROM clans c WHERE c.name LIKE ? LIMIT 20;")) {
            stmt.setString(1, text.isEmpty() ? "%" : "%" + text + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                java.sql.Timestamp created = rs.getTimestamp("created_at");
                data.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    rs.getString("slogan") != null ? rs.getString("slogan") : "",
                    String.valueOf((short)0),
                    String.valueOf(rs.getInt("exp")),
                    rs.getString("leader_name") != null ? rs.getString("leader_name") : "",
                    String.valueOf(rs.getInt("members")),
                    String.valueOf(MAX_MEMBER),
                    String.valueOf(created != null ? (int)(created.getTime() / 1000L) : 0)
                });
            }
            rs.close();
        } catch (SQLException ignored) {
        }
        ChickenTinNhan out = new ChickenTinNhan(-113);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(data.size());
        for (String[] row : data) {
            ds.writeInt(Integer.parseInt(row[0]));
            ds.writeUTF(row[1]);
            ds.writeUTF(row[2]);
            ds.writeShort(Short.parseShort(row[3]));
            ds.writeUTF(ChickenTienIch.dinhDangTien(Integer.parseInt(row[4])));
            ds.writeUTF(row[5]);
            ds.writeByte(Integer.parseInt(row[6]));
            ds.writeByte(Integer.parseInt(row[7]));
            ds.writeInt(Integer.parseInt(row[8]));
        }
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    public static void quanLyClan(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        byte action = ms.boDoc().readByte();
        if (action == 1 || action == 3) {
            byte page = ms.boDoc().readByte();
            guiDanhSachIcon(pl, page);
            return;
        }
        if (action == 2) {
            short iconId = ms.boDoc().readShort();
            String ten = ms.boDoc().readUTF();
            taoClan(pl, ten, iconId);
            return;
        }
        if (action == 4) {
            short iconId = ms.boDoc().readShort();
            String slogan = ms.boDoc().readUTF();
            doiSlogan(pl, iconId, slogan);
        }
    }

    public static void yeuCauThanhVien(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        byte page = 0;
        int clanId = pl.clan;
        int available = ms.boDoc().available();
        if (available == 4) {
            clanId = ms.boDoc().readInt();
        } else if (available >= 3) {
            page = ms.boDoc().readByte();
            clanId = ms.boDoc().readShort();
        }
        if (clanId <= 0) {
            clanId = pl.clan;
        }
        guiThanhVien(pl, clanId, page);
    }

    public static void thamGia(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        int clanId = ms.boDoc().readInt();
        byte action = ms.boDoc().readByte();
        if (action == 0) {
            if (pl.clan > 0) {
                pl.moHopThoaiOK("Bạn đã có biệt đội.");
                return;
            }
            try (Connection conn = ChickenCoSoDuLieu.getConnection();
                 PreparedStatement cnt = conn.prepareStatement("SELECT COUNT(*) AS c FROM clan_members WHERE clan_id = ?;");
                 PreparedStatement ins = conn.prepareStatement("INSERT INTO clan_members (clan_id, player_id, member_role) VALUES (?, ?, 0);")) {
                cnt.setInt(1, clanId);
                ResultSet rs = cnt.executeQuery();
                int members = rs.next() ? rs.getInt("c") : MAX_MEMBER;
                rs.close();
                if (members >= MAX_MEMBER) {
                    pl.moHopThoaiOK("Biệt đội đã đầy.");
                    return;
                }
                ins.setInt(1, clanId);
                ins.setInt(2, pl.ma);
                ins.executeUpdate();
                pl.clan = clanId;
                pl.clanRole = 0;
            } catch (SQLException ex) {
                pl.moHopThoaiOK("Không thể tham gia biệt đội.");
                return;
            }
            thongBaoThemThanhVien(clanId, pl);
            guiThongTinClan(pl, clanId);
            return;
        }
        roiClan(pl);
    }

    public static void tinNhanClan(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        if (pl.clan <= 0) {
            return;
        }
        byte type = ms.boDoc().readByte();
        if (type == 0) {
            String text = ms.boDoc().readUTF();
            if (text == null || text.trim().isEmpty()) {
                return;
            }
            ChickenTinNhanClan msg = taoChickenTinNhanChat(pl, text.trim());
            luuChickenTinNhan(pl.clan, msg);
            phatChickenTinNhanClan(pl.clan, msg);
            return;
        }
        if (type == 2) {
            int clanId = ms.boDoc().readInt();
            if (clanId <= 0) {
                clanId = pl.clan;
            }
            for (ChickenTinNhanClan msg : layChickenTinNhan(clanId)) {
                phatChickenTinNhanClanRieng(pl, msg);
            }
        }
    }

    public static void quanLyThanhVien(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        if (pl.clan <= 0 || pl.clanRole < 1) {
            pl.moHopThoaiOK("Bạn không có quyền.");
            return;
        }
        int memberId = ms.boDoc().readInt();
        byte role = ms.boDoc().readByte();
        if (memberId == pl.ma) {
            pl.moHopThoaiOK("Không thể thao tác trên chính mình.");
            return;
        }
        if (role == -1) {
            if (!xoaThanhVien(pl.clan, memberId)) {
                pl.moHopThoaiOK("Không thể xóa thành viên.");
                return;
            }
            ChickenNguoiChoi target = ChickenNguoiChoi.layNguoiChoiTheoMa(memberId);
            if (target != null) {
                target.clan = -1;
                target.clanRole = 0;
                guiThongTinClan(target, -1);
            }
            thongBaoXoaThanhVien(pl.clan, memberId);
            return;
        }
        if (role < 0 || role > 2) {
            return;
        }
        if (role == 2 && pl.clanRole != 2) {
            pl.moHopThoaiOK("Chỉ đội trưởng mới có thể chuyển quyền.");
            return;
        }
        if (!capNhatVaiTro(pl.clan, memberId, role, role == 2 ? pl.ma : -1)) {
            pl.moHopThoaiOK("Không thể cập nhật vai trò.");
            return;
        }
        if (role == 2) {
            pl.clanRole = 0;
        }
        ChickenNguoiChoi target = ChickenNguoiChoi.layNguoiChoiTheoMa(memberId);
        if (target != null) {
            target.clanRole = role;
        }
        thongBaoCapNhatThanhVien(pl.clan, memberId);
    }

    public static void moiClan(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        byte action = ms.boDoc().readByte();
        if (action == 0) {
            if (pl.clan <= 0 || pl.clanRole < 1) {
                pl.moHopThoaiOK("Bạn không có quyền mời.");
                return;
            }
            int playerId = ms.boDoc().readInt();
            ChickenNguoiChoi target = ChickenNguoiChoi.layNguoiChoiTheoMa(playerId);
            if (target == null) {
                pl.moHopThoaiOK("Người chơi không online.");
                return;
            }
            if (target.clan > 0) {
                pl.moHopThoaiOK("Người chơi đã có biệt đội.");
                return;
            }
            int code = nextInviteCode.incrementAndGet();
            LoiMoiClan invite = new LoiMoiClan();
            invite.clanId = pl.clan;
            invite.targetId = playerId;
            invite.inviterId = pl.ma;
            invite.inviterName = pl.ten;
            loiMoiClan.put(code, invite);
            ChickenTinNhan out = new ChickenTinNhan(-111);
            DataOutputStream ds = out.boGhi();
            ds.writeUTF(pl.ten);
            ds.writeInt(pl.clan);
            ds.writeInt(code);
            ds.flush();
            target.dichVu.guiTin(out);
            pl.moHopThoaiOK("Đã gửi lời mời.");
            return;
        }
        if (action == 1 || action == 2) {
            int clanId = ms.boDoc().readInt();
            int code = ms.boDoc().readInt();
            LoiMoiClan invite = loiMoiClan.remove(code);
            if (invite == null || invite.targetId != pl.ma || invite.clanId != clanId) {
                pl.moHopThoaiOK("Lời mời không hợp lệ.");
                return;
            }
            if (action == 2) {
                return;
            }
            if (pl.clan > 0) {
                pl.moHopThoaiOK("Bạn đã có biệt đội.");
                return;
            }
            try (Connection conn = ChickenCoSoDuLieu.getConnection();
                 PreparedStatement cnt = conn.prepareStatement("SELECT COUNT(*) AS c FROM clan_members WHERE clan_id = ?;");
                 PreparedStatement ins = conn.prepareStatement("INSERT INTO clan_members (clan_id, player_id, member_role) VALUES (?, ?, 0);")) {
                cnt.setInt(1, clanId);
                ResultSet rs = cnt.executeQuery();
                int members = rs.next() ? rs.getInt("c") : MAX_MEMBER;
                rs.close();
                if (members >= MAX_MEMBER) {
                    pl.moHopThoaiOK("Biệt đội đã đầy.");
                    return;
                }
                ins.setInt(1, clanId);
                ins.setInt(2, pl.ma);
                ins.executeUpdate();
                pl.clan = clanId;
                pl.clanRole = 0;
            } catch (SQLException ex) {
                pl.moHopThoaiOK("Không thể tham gia biệt đội.");
                return;
            }
            thongBaoThemThanhVien(clanId, pl);
            guiThongTinClan(pl, clanId);
        }
    }

    public static void xuLyShopClan(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        byte page = ms.boDoc().readByte();
        ChickenTinNhan out = new ChickenTinNhan(-118);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(page);
        ds.writeByte(1);
        ds.writeByte(0);
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    public static void trangThaiClan(ChickenNguoiChoi pl) throws IOException {
        ChickenTinNhan out = new ChickenTinNhan(-119);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(0);
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    public static void roiClan(ChickenNguoiChoi pl) throws IOException {
        if (pl.clan <= 0) {
            guiThongTinClan(pl, -1);
            return;
        }
        int clanId = pl.clan;
        try (Connection conn = ChickenCoSoDuLieu.getConnection()) {
            if (pl.clanRole == 2) {
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM clan_members WHERE clan_id = ?;")) {
                    del.setInt(1, clanId);
                    del.executeUpdate();
                }
                try (PreparedStatement delClan = conn.prepareStatement("DELETE FROM clans WHERE id = ? LIMIT 1;")) {
                    delClan.setInt(1, clanId);
                    delClan.executeUpdate();
                }
            } else {
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM clan_members WHERE clan_id = ? AND player_id = ? LIMIT 1;")) {
                    del.setInt(1, clanId);
                    del.setInt(2, pl.ma);
                    del.executeUpdate();
                }
            }
        } catch (SQLException ignored) {
        }
        pl.clan = -1;
        pl.clanRole = 0;
        guiThongTinClan(pl, -1);
    }

    public static void shopBietDoi(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        byte action = ms.boDoc().readByte();
        if (action == 0) {
            ChickenTinNhan out = new ChickenTinNhan(-12);
            DataOutputStream ds = out.boGhi();
            ds.writeByte(0);
            ds.flush();
            pl.dichVu.guiTin(out);
            return;
        }
        if (action == 1 && pl.clan > 0) {
            byte money = ms.boDoc().readByte();
            byte id = ms.boDoc().readByte();
            pl.moHopThoaiOK("Chưa hỗ trợ mua buff biệt đội (id=" + id + ", money=" + money + ").");
            return;
        }
        pl.moHopThoaiOK("Chưa hỗ trợ mua vật phẩm biệt đội.");
    }

    private static void taoClan(ChickenNguoiChoi pl, String ten, short iconId) throws IOException {
        if (pl.clan > 0) {
            pl.moHopThoaiOK("Bạn đã có biệt đội.");
            return;
        }
        if (ten == null || ten.trim().isEmpty()) {
            pl.moHopThoaiOK("Tên biệt đội không hợp lệ.");
            return;
        }
        if (pl.ngoc < 1) {
            pl.moHopThoaiOK("Cần 1 ngọc để tạo biệt đội.");
            return;
        }
        pl.updateGem(-1);
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO clans (name, leader_player_id, level, exp, clan_gold, slogan, icon_id) VALUES (?, ?, 1, 0, 0, ?, ?);",
                     Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ten.trim());
            stmt.setInt(2, pl.ma);
            stmt.setString(3, "");
            stmt.setShort(4, iconId);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            int clanId = keys.next() ? keys.getInt(1) : -1;
            keys.close();
            if (clanId > 0) {
                try (PreparedStatement mem = conn.prepareStatement("INSERT INTO clan_members (clan_id, player_id, member_role) VALUES (?, ?, 2);")) {
                    mem.setInt(1, clanId);
                    mem.setInt(2, pl.ma);
                    mem.executeUpdate();
                }
                pl.clan = clanId;
                pl.clanRole = 2;
                guiThongTinClan(pl, clanId);
                return;
            }
        } catch (SQLException ex) {
            pl.updateGem(1);
            pl.moHopThoaiOK("Tên biệt đội đã tồn tại.");
        }
    }

    private static void doiSlogan(ChickenNguoiChoi pl, short iconId, String slogan) throws IOException {
        if (pl.clan <= 0 || pl.clanRole < 1) {
            pl.moHopThoaiOK("Bạn không có quyền.");
            return;
        }
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE clans SET slogan = ?, icon_id = ? WHERE id = ? LIMIT 1;")) {
            stmt.setString(1, slogan != null ? slogan : "");
            stmt.setShort(2, iconId);
            stmt.setInt(3, pl.clan);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
        ChickenTinNhan out = new ChickenTinNhan(-103);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(4);
        ds.writeShort(iconId);
        ds.writeUTF(slogan != null ? slogan : "");
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    private static void guiDanhSachIcon(ChickenNguoiChoi pl, byte page) throws IOException {
        ChickenTinNhan out = new ChickenTinNhan(-103);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(1);
        ds.writeByte(page);
        ds.writeByte(1);
        ds.writeByte(3);
        ghiIconClan(ds, (short)0, "Biệt đội", 0, 0);
        ghiIconClan(ds, (short)-1, "Đỏ", 0, 0);
        ghiIconClan(ds, (short)-2, "Xanh", 0, 0);
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    private static void ghiIconClan(DataOutputStream ds, short iconId, String name, int xu, int luong) throws IOException {
        ds.writeShort(iconId);
        ds.writeUTF(name);
        ds.writeInt(xu);
        ds.writeInt(luong);
    }

    private static void guiThongTinClan(ChickenNguoiChoi pl, int clanId) throws IOException {
        if (clanId <= 0) {
            ChickenTinNhan out = new ChickenTinNhan(-108);
            DataOutputStream ds = out.boGhi();
            ds.writeInt(-1);
            ds.flush();
            pl.dichVu.guiTin(out);
            return;
        }
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.*, (SELECT p.name FROM players p WHERE p.id = c.leader_player_id LIMIT 1) AS leader_name " +
                     "FROM clans c WHERE c.id = ? LIMIT 1;")) {
            stmt.setInt(1, clanId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                ChickenTinNhan out = new ChickenTinNhan(-108);
                DataOutputStream ds = out.boGhi();
                ds.writeInt(-1);
                ds.flush();
                pl.dichVu.guiTin(out);
                rs.close();
                return;
            }
            ChickenTinNhan out = new ChickenTinNhan(-108);
            DataOutputStream ds = out.boGhi();
            ds.writeInt(clanId);
            ds.writeUTF(rs.getString("name"));
            ds.writeUTF(rs.getString("slogan") != null ? rs.getString("slogan") : "");
            ds.writeShort((short)0);
            ds.writeUTF(ChickenTienIch.dinhDangTien(rs.getInt("exp")));
            ds.writeUTF(rs.getString("leader_name") != null ? rs.getString("leader_name") : "");
            int members = demThanhVien(clanId);
            ds.writeByte(members);
            ds.writeByte(MAX_MEMBER);
            ds.writeByte(pl.clan == clanId ? pl.clanRole : (byte)0);
            ghiThanhVienVao(ds, clanId);
            ghiChickenTinNhanClanVao(ds, clanId);
            ds.writeByte(rs.getByte("level"));
            rs.close();
            ds.flush();
            pl.dichVu.guiTin(out);
        } catch (SQLException ex) {
            ChickenTinNhan out = new ChickenTinNhan(-108);
            DataOutputStream ds = out.boGhi();
            ds.writeInt(-1);
            ds.flush();
            pl.dichVu.guiTin(out);
        }
    }

    private static void guiThanhVien(ChickenNguoiChoi pl, int clanId, byte page) throws IOException {
        ChickenTinNhan out = new ChickenTinNhan(-105);
        DataOutputStream ds = out.boGhi();
        ds.writeInt(clanId);
        if (clanId <= 0) {
            ds.writeByte(0);
            ds.flush();
            pl.dichVu.guiTin(out);
            return;
        }
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.id, p.name, cm.member_role FROM clan_members cm JOIN players p ON p.id = cm.player_id " +
                     "WHERE cm.clan_id = ? ORDER BY cm.member_role DESC, p.name ASC LIMIT 30 OFFSET ?;")) {
            stmt.setInt(1, clanId);
            stmt.setInt(2, page * 30);
            ResultSet rs = stmt.executeQuery();
            java.util.ArrayList<Object[]> list = new java.util.ArrayList<>();
            while (rs.next()) {
                list.add(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getByte("member_role")});
            }
            rs.close();
            ds.writeByte(list.size());
            for (Object[] row : list) {
                int pid = (Integer)row[0];
                ChickenNguoiChoi online = ChickenNguoiChoi.layNguoiChoiTheoMa(pid);
                ds.writeInt(pid);
                ds.writeShort(online != null ? online.head : (short)-1);
                ds.writeShort(online != null ? online.leg : (short)-1);
                ds.writeShort(online != null ? online.body : (short)-1);
                ds.writeUTF((String)row[1]);
                ds.writeByte((Byte)row[2]);
                ds.writeUTF("0");
                ds.writeInt(0);
                ds.writeInt(0);
                ds.writeInt(0);
                ds.writeInt((int)(System.currentTimeMillis() / 1000L));
            }
        } catch (SQLException ex) {
            ds.writeByte(0);
        }
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    private static void ghiThanhVienVao(DataOutputStream ds, int clanId) throws IOException {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.id, p.name, cm.member_role FROM clan_members cm JOIN players p ON p.id = cm.player_id " +
                     "WHERE cm.clan_id = ? ORDER BY cm.member_role DESC LIMIT 30;")) {
            stmt.setInt(1, clanId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int pid = rs.getInt("id");
                ChickenNguoiChoi online = ChickenNguoiChoi.layNguoiChoiTheoMa(pid);
                ds.writeInt(pid);
                ds.writeShort(online != null ? online.head : (short)-1);
                ds.writeShort(online != null ? online.leg : (short)-1);
                ds.writeShort(online != null ? online.body : (short)-1);
                ds.writeUTF(rs.getString("name"));
                ds.writeByte(rs.getByte("member_role"));
                ds.writeUTF("0");
                ds.writeInt(0);
                ds.writeInt(0);
                ds.writeInt(0);
                ds.writeInt((int)(System.currentTimeMillis() / 1000L));
            }
            rs.close();
        } catch (SQLException ignored) {
        }
    }

    private static int demThanhVien(int clanId) {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS c FROM clan_members WHERE clan_id = ?;")) {
            stmt.setInt(1, clanId);
            ResultSet rs = stmt.executeQuery();
            int c = rs.next() ? rs.getInt("c") : 0;
            rs.close();
            return c;
        } catch (SQLException ex) {
            return 0;
        }
    }

    private static ChickenTinNhanClan taoChickenTinNhanChat(ChickenNguoiChoi pl, String text) {
        ChickenTinNhanClan msg = new ChickenTinNhanClan();
        msg.id = nextMsgId.getAndIncrement();
        msg.type = 0;
        msg.playerId = pl.ma;
        msg.playerName = pl.ten;
        msg.role = pl.clanRole;
        msg.time = (int)(System.currentTimeMillis() / 1000L);
        msg.text = text;
        msg.color = 0;
        return msg;
    }

    private static void luuChickenTinNhan(int clanId, ChickenTinNhanClan msg) {
        clanMessages.computeIfAbsent(clanId, k -> new ArrayList<>()).add(msg);
        List<ChickenTinNhanClan> list = clanMessages.get(clanId);
        if (list.size() > 50) {
            list.remove(0);
        }
    }

    private static List<ChickenTinNhanClan> layChickenTinNhan(int clanId) {
        List<ChickenTinNhanClan> list = clanMessages.get(clanId);
        return list != null ? list : java.util.Collections.emptyList();
    }

    private static void phatChickenTinNhanClan(int clanId, ChickenTinNhanClan msg) throws IOException {
        for (int pid : layDanhSachThanhVien(clanId)) {
            ChickenNguoiChoi online = ChickenNguoiChoi.layNguoiChoiTheoMa(pid);
            if (online != null) {
                phatChickenTinNhanClanRieng(online, msg);
            }
        }
    }

    private static void phatChickenTinNhanClanRieng(ChickenNguoiChoi pl, ChickenTinNhanClan msg) throws IOException {
        ChickenTinNhan out = new ChickenTinNhan(-106);
        DataOutputStream ds = out.boGhi();
        ghiChickenTinNhanClan(ds, msg);
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    private static void ghiChickenTinNhanClan(DataOutputStream ds, ChickenTinNhanClan msg) throws IOException {
        ds.writeByte(msg.type);
        ds.writeInt(msg.id);
        ds.writeInt(msg.playerId);
        ds.writeUTF(msg.playerName);
        ds.writeByte(msg.role);
        ds.writeInt(msg.time);
        if (msg.type == 0) {
            ds.writeUTF(msg.text);
            ds.writeByte(msg.color);
        } else if (msg.type == 1) {
            ds.writeByte(0);
            ds.writeByte(0);
            ds.writeByte(0);
        }
    }

    private static void ghiChickenTinNhanClanVao(DataOutputStream ds, int clanId) throws IOException {
        List<ChickenTinNhanClan> list = layChickenTinNhan(clanId);
        ds.writeByte(list.size());
        for (ChickenTinNhanClan msg : list) {
            ghiChickenTinNhanClan(ds, msg);
        }
    }

    private static List<Integer> layDanhSachThanhVien(int clanId) {
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT player_id FROM clan_members WHERE clan_id = ?;")) {
            stmt.setInt(1, clanId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("player_id"));
            }
            rs.close();
        } catch (SQLException ignored) {
        }
        return ids;
    }

    private static String layTenThanhVien(int playerId) {
        ChickenNguoiChoi online = ChickenNguoiChoi.layNguoiChoiTheoMa(playerId);
        if (online != null) {
            return online.ten;
        }
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM players WHERE id = ? LIMIT 1;")) {
            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
            rs.close();
        } catch (SQLException ignored) {
        }
        return "?";
    }

    private static byte layVaiTroThanhVien(int clanId, int playerId) {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT member_role FROM clan_members WHERE clan_id = ? AND player_id = ? LIMIT 1;")) {
            stmt.setInt(1, clanId);
            stmt.setInt(2, playerId);
            ResultSet rs = stmt.executeQuery();
            byte role = rs.next() ? rs.getByte("member_role") : 0;
            rs.close();
            return role;
        } catch (SQLException ex) {
            return 0;
        }
    }

    private static void thongBaoThemThanhVien(int clanId, ChickenNguoiChoi pl) throws IOException {
        ChickenTinNhan out = new ChickenTinNhan(-107);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(0);
        ghiThongTinThanhVien(ds, pl.ma, pl.ten, pl.clanRole, pl);
        ds.flush();
        phatTinChoClan(clanId, out, pl.ma);
    }

    private static void thongBaoXoaThanhVien(int clanId, int memberId) throws IOException {
        ChickenTinNhan out = new ChickenTinNhan(-107);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(1);
        ds.writeInt(memberId);
        ds.flush();
        phatTinChoClan(clanId, out, -1);
    }

    private static void thongBaoCapNhatThanhVien(int clanId, int memberId) throws IOException {
        byte role = layVaiTroThanhVien(clanId, memberId);
        ChickenNguoiChoi online = ChickenNguoiChoi.layNguoiChoiTheoMa(memberId);
        ChickenTinNhan out = new ChickenTinNhan(-107);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(2);
        ghiThongTinThanhVien(ds, memberId, layTenThanhVien(memberId), role, online);
        ds.flush();
        phatTinChoClan(clanId, out, -1);
    }

    private static void phatTinChoClan(int clanId, ChickenTinNhan out, int skipId) throws IOException {
        for (int pid : layDanhSachThanhVien(clanId)) {
            if (pid == skipId) {
                continue;
            }
            ChickenNguoiChoi online = ChickenNguoiChoi.layNguoiChoiTheoMa(pid);
            if (online != null) {
                online.dichVu.guiTin(out);
            }
        }
    }

    private static void ghiThongTinThanhVien(DataOutputStream ds, int pid, String name, byte role, ChickenNguoiChoi online) throws IOException {
        ds.writeInt(pid);
        ds.writeShort(online != null ? online.head : (short)-1);
        ds.writeShort(online != null ? online.leg : (short)-1);
        ds.writeShort(online != null ? online.body : (short)-1);
        ds.writeUTF(name);
        ds.writeByte(role);
        ds.writeUTF("0");
        ds.writeInt(0);
        ds.writeInt(0);
        ds.writeInt(0);
        ds.writeInt((int)(System.currentTimeMillis() / 1000L));
    }

    private static boolean xoaThanhVien(int clanId, int memberId) {
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM clan_members WHERE clan_id = ? AND player_id = ? LIMIT 1;")) {
            stmt.setInt(1, clanId);
            stmt.setInt(2, memberId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            return false;
        }
    }

    private static boolean capNhatVaiTro(int clanId, int memberId, byte role, int newLeaderId) {
        try (Connection conn = ChickenCoSoDuLieu.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE clan_members SET member_role = ? WHERE clan_id = ? AND player_id = ? LIMIT 1;")) {
                stmt.setByte(1, role);
                stmt.setInt(2, clanId);
                stmt.setInt(3, memberId);
                if (stmt.executeUpdate() == 0) {
                    return false;
                }
            }
            if (newLeaderId > 0) {
                try (PreparedStatement leader = conn.prepareStatement("UPDATE clans SET leader_player_id = ? WHERE id = ? LIMIT 1;")) {
                    leader.setInt(1, memberId);
                    leader.setInt(2, clanId);
                    leader.executeUpdate();
                }
                try (PreparedStatement oldLeader = conn.prepareStatement("UPDATE clan_members SET member_role = 0 WHERE clan_id = ? AND player_id = ? LIMIT 1;")) {
                    oldLeader.setInt(1, clanId);
                    oldLeader.setInt(2, newLeaderId);
                    oldLeader.executeUpdate();
                }
            }
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
}
