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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ChickenQuanLyXepHang {
    private static final String[] TAB_NAMES = new String[]{"Cao thủ", "Đại gia", "KDA", "Ngọc"};
    private static final String[] TAB_TITLES = new String[]{
        "Bảng xếp hạng Cao thủ",
        "Bảng xếp hạng Đại gia",
        "Bảng xếp hạng KDA",
        "Bảng xếp hạng Ngọc"
    };

    private static class DongXepHang {
        int id;
        String name;
        int cup;
        int kill;
        int dead = 1;
        int assist;
        float kda;
        int gold;
        int gem;
        int exp;
        int clanId;
        short head = -1;
        short hat = -1;
        short body = -1;
        short leg = -1;
        short wing = -1;
        short wp = -1;
        boolean online;
    }

    public static void guiSubMenu(ChickenNguoiChoi pl) throws IOException {
        ChickenTinNhan out = new ChickenTinNhan(-14);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(-1);
        ds.writeByte(TAB_TITLES.length);
        for (String title : TAB_TITLES) {
            ds.writeUTF(title);
        }
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    public static void guiMenuTop(ChickenNguoiChoi pl) throws IOException {
        ChickenTinNhan out = new ChickenTinNhan(-57);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(TAB_NAMES.length);
        for (String tab : TAB_NAMES) {
            ds.writeUTF(tab);
        }
        for (int t = 0; t < TAB_NAMES.length; ++t) {
            List<DongXepHang> list = layBangXepHang(t, 10, 0);
            ds.writeByte(list.size());
            int rank = 1;
            for (DongXepHang row : list) {
                ghiDongTop57(ds, row, rank++, t);
            }
        }
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    public static void bangXepHang(ChickenNguoiChoi pl, ChickenTinNhan ms) throws IOException {
        byte type = ms.boDoc().readByte();
        byte page = ms.boDoc().available() > 0 ? ms.boDoc().readByte() : 0;
        if (type == -1) {
            guiSubMenu(pl);
            return;
        }
        int tab = type;
        if (tab < 0) {
            tab = -tab;
        }
        if (tab >= TAB_TITLES.length) {
            return;
        }
        int pageIndex = page < 0 ? 0 : page;
        List<DongXepHang> list = layBangXepHang(tab, 10, pageIndex * 10);
        ChickenTinNhan out = new ChickenTinNhan(-14);
        DataOutputStream ds = out.boGhi();
        ds.writeByte(type);
        ds.writeByte(page);
        ds.writeUTF(TAB_TITLES[tab]);
        int stt = pageIndex * 10;
        for (DongXepHang row : list) {
            ghiDongTop14(ds, row, tab, ++stt);
        }
        ds.flush();
        pl.dichVu.guiTin(out);
    }

    private static List<DongXepHang> layBangXepHang(int tab, int limit, int offset) {
        List<DongXepHang> list = new ArrayList<>();
        try (Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.id, p.name, p.cup, p.gold, p.gem, p.stats_json, cm.clan_id " +
                     "FROM `players` p LEFT JOIN `clan_members` cm ON cm.player_id = p.id;")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DongXepHang row = new DongXepHang();
                row.id = rs.getInt("id");
                row.name = lamSachTen(rs.getString("name"));
                row.cup = rs.getInt("cup");
                row.gold = rs.getInt("gold");
                row.gem = rs.getInt("gem");
                String statsJson = rs.getString("stats_json");
                row.exp = docSoThongKe(statsJson, "exp", 0);
                row.kill = docSoThongKe(statsJson, "kill", 0);
                row.dead = Math.max(1, docSoThongKe(statsJson, "dead", 1));
                row.assist = docSoThongKe(statsJson, "assist", 0);
                row.kda = tinhKDA(row.kill, row.assist, row.dead);
                row.clanId = rs.getInt("clan_id");
                ChickenNguoiChoi online = ChickenNguoiChoi.layNguoiChoiTheoMa(row.id);
                if (online != null) {
                    row.online = true;
                    row.head = online.head;
                    row.hat = online.hat;
                    row.body = online.body;
                    row.leg = online.leg;
                    row.wing = online.wing;
                    row.wp = online.wp;
                    row.exp = online.kinhNghiem;
                    row.cup = online.cup;
                    row.kill = online.kill;
                    row.dead = Math.max(1, online.chet);
                    row.assist = online.assist;
                    row.kda = tinhKDA(row.kill, row.assist, row.dead);
                    row.gold = online.vang;
                    row.gem = online.ngoc;
                    row.clanId = online.clan > 0 ? online.clan : row.clanId;
                }
                list.add(row);
            }
            rs.close();
        } catch (SQLException ignored) {
        }
        sapXep(list, tab);
        if (offset >= list.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(offset + limit, list.size());
        return new ArrayList<>(list.subList(offset, end));
    }

    private static void sapXep(List<DongXepHang> list, int tab) {
        Comparator<DongXepHang> cmp;
        switch (tab) {
            case 1:
                cmp = Comparator.comparingInt((DongXepHang r) -> r.gold).reversed();
                break;
            case 2:
                cmp = Comparator.comparingDouble((DongXepHang r) -> r.kda).reversed();
                break;
            case 3:
                cmp = Comparator.comparingInt((DongXepHang r) -> r.gem).reversed();
                break;
            default:
                cmp = Comparator.comparingInt((DongXepHang r) -> r.exp).reversed();
                break;
        }
        list.sort(cmp);
    }

    private static int docSoThongKe(String stats, String khoa, int macDinh) {
        if (stats == null || khoa == null) {
            return macDinh;
        }
        try {
            String token = "\"" + khoa + "\"";
            int idx = stats.indexOf(token);
            if (idx < 0) {
                return macDinh;
            }
            int start = stats.indexOf(':', idx);
            if (start < 0) {
                return macDinh;
            }
            start++;
            int end = start;
            while (end < stats.length()) {
                char c = stats.charAt(end);
                if ((c >= '0' && c <= '9') || c == '-') {
                    end++;
                } else if (end == start && Character.isWhitespace(c)) {
                    start++;
                    end++;
                } else {
                    break;
                }
            }
            if (end <= start) {
                return macDinh;
            }
            return Integer.parseInt(stats.substring(start, end).trim());
        } catch (Exception ignored) {
            return macDinh;
        }
    }

    private static float tinhKDA(int kill, int assist, int dead) {
        return (float)(Math.max(0, kill) + Math.max(0, assist)) / (float)Math.max(1, dead);
    }

    private static void ghiDongTop57(DataOutputStream ds, DongXepHang row, int rank, int tab) throws IOException {
        ds.writeInt(rank);
        ds.writeInt(row.id);
        ds.writeUTF(row.name);
        ds.writeShort(row.head);
        ds.writeShort(row.hat);
        ds.writeShort(row.body);
        ds.writeShort(row.leg);
        ds.writeShort(row.wing);
        ds.writeShort(row.wp);
        ds.writeInt(0);
        ds.writeByte(row.online ? (byte)1 : (byte)0);
        ds.writeUTF(giaTriXepHang(row, tab));
        ds.writeShort(row.clanId > 0 ? (short)row.clanId : (short)0);
    }

    private static void ghiDongTop14(DataOutputStream ds, DongXepHang row, int tab, int stt) throws IOException {
        ds.writeInt(row.id);
        ds.writeUTF(row.name);
        ds.writeByte(0);
        ds.writeShort(row.clanId > 0 ? (short)row.clanId : (short)0);
        ds.writeByte(0);
        ds.writeByte(0);
        ds.writeByte(stt);
        ds.writeShort(row.head);
        ds.writeShort(row.hat);
        ds.writeShort(row.body);
        ds.writeShort(row.leg);
        ds.writeShort(row.wing);
        ds.writeUTF(giaTriXepHang(row, tab));
    }

    private static String giaTriXepHang(DongXepHang row, int tab) {
        switch (tab) {
            case 1:
                return ChickenTienIch.dinhDangTien(row.gold);
            case 2:
                return String.format(Locale.US, "%.2f", row.kda);
            case 3:
                return ChickenTienIch.dinhDangTien(row.gem);
            default:
                return ChickenTienIch.dinhDangTien(row.exp);
        }
    }
    private static String lamSachTen(String ten) {
        if (ten == null) {
            return "Không tên";
        }
        StringBuilder ketQua = new StringBuilder();
        for (int i = 0; i < ten.length(); i++) {
            char kyTu = ten.charAt(i);
            if (!Character.isISOControl(kyTu)) {
                ketQua.append(kyTu);
            }
        }
        String daLamSach = ketQua.toString().trim();
        return daLamSach.isEmpty() ? "Không tên" : daLamSach;
    }

}
