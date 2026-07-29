package com.chicken.mohinh;

import com.chicken.dichvu.ChickenQuanLyBietDoi;
import com.chicken.avg.ChickenQuanLyNangLuongAVG;

import com.chicken.loi.ChickenCoSoDuLieu;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mang.ChickenPhien;
import com.chicken.tienich.ChickenDuLieuJson;
import com.chicken.tienich.ChickenTienIch;
import com.chicken.tiemnang.ChickenQuanLyTiemNang;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.io.DataOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChickenNguoiDung {
    public static final ConcurrentHashMap<String, ChickenNguoiDung> users = new ConcurrentHashMap<>();
    private ChickenPhien khach;
    public ChickenDichVuGame dichVu;
    private int user_id;
    private String tenDangNhap;
    private String matKhau;
    private boolean ban;
    public ChickenNguoiChoi nguoiChoi;
    private static final int[] ID_TEMPLATE_BALO = new int[]{85, 90, 95, 100, 105};
    private static final int[] ID_TEMPLATE_BODY = new int[]{35, 40, 45, 50, 55};
    private static final int[] ID_TEMPLATE_LEG = new int[]{10, 15, 20, 25, 30};
    private static final int[] ID_TEMPLATE_WEAPON = new int[]{110, 120, 130, 140, 150, 160, 190, 200};
    private static final int[] ID_TEMPLATE_HEAD = new int[]{0, 1, 2, 3, 4};
    private static final int[] ID_TEMPLATE_HAT = new int[]{60, 65, 70, 75, 80};
    private static final String DEFAULT_STATS_JSON = "{\"power\":100,\"avenger\":100,\"kill\":0,\"dead\":1,\"assist\":0,\"trainingSuccess\":1,\"trainingWins\":0,\"busyHammer\":0,\"nHammer\":2,\"exp\":1000,\"rewardedLevel\":2,\"point\":20,\"pointAdd\":[1000,0,0,0,0,0]}";

    public ChickenNguoiDung(ChickenPhien khach, ChickenDichVuGame dichVu) {
        this.khach = khach;
        this.dichVu = dichVu;
    }

    public static ChickenNguoiDung timNguoiDungTheoTen(String ten) {
        return users.get(ten);
    }

    public static ChickenNguoiDung dangNhap(ChickenPhien s, String tenDangNhap, String matKhau, String phienBan, byte loai) {
        ChickenNguoiDung us = new ChickenNguoiDung(s, (ChickenDichVuGame)s.layDichVu());
        try {
            if (tenDangNhap.startsWith("nvn_") && matKhau.equals("a")) {
                matKhau = "";
            }
            try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `accounts` WHERE `username` = ? AND `password` = ? LIMIT 1;")) {
                stmt.setString(1, tenDangNhap);
                stmt.setString(2, matKhau);
                ResultSet res = stmt.executeQuery();
                if (res != null && res.next()) {
                    us.user_id = res.getInt("id");
                    us.ban = res.getBoolean("is_banned");
                    if (us.ban) {
                        us.dichVu.moHopThoaiOK("Tai khoan da bi khoa.");
                        res.close();
                        return null;
                    }
                    us.tenDangNhap = res.getString("username");
                    us.matKhau = res.getString("password");
                    res.close();
                    ChickenNguoiDung user = users.putIfAbsent(us.tenDangNhap, us);
                    if (user != null) {
                        us.dichVu.moHopThoaiOK("Tai khoan dang duoc su dung.");
                        user.khach.guiMaPhien(0);
                        return null;
                    }
                    return us;
                }
                if (res != null) {
                    res.close();
                }
            }
            us.dichVu.moHopThoaiOK("Tai khoan hoac mat khau khong chinh xac.");
        }
        catch (Exception ex) {
            try {
                us.dichVu.moHopThoaiOK("Loi dang nhap.");
            }
            catch (Exception exception) {
                }
        }
        return null;
    }

    public static void dangNhap2(ChickenPhien s, String tenDangNhap) {
        if (tenDangNhap.isEmpty()) {
            try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO `accounts`(`username`, `password`, `is_banned`, `is_online`) VALUES (?,?,?,?);")) {
                String user = "nvn_" + System.currentTimeMillis();
                stmt.setString(1, user);
                stmt.setString(2, "");
                stmt.setInt(3, 0);
                stmt.setInt(4, 0);
                stmt.execute();
                ChickenDichVuGame dichVu = (ChickenDichVuGame)s.layDichVu();
                dichVu.taoNguoiDungAo(user);
            }
            catch (SQLException ex) {
                Logger.getLogger(ChickenNguoiDung.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void taoNhanVat(ChickenTinNhan ms) {
        try {
            String ten = ms.boDoc().readUTF();
            short head = ms.boDoc().readShort();
            short leg = ms.boDoc().readShort();
            short body = ms.boDoc().readShort();
            short wing = ms.boDoc().readShort();
            short weapon = ms.boDoc().readShort();
            short hat = ms.boDoc().readShort();
            System.out.println("head: " + head);
            System.out.println("body: " + body);
            System.out.println("leg: " + leg);
            System.out.println("wing: " + wing);
            System.out.println("wp: " + weapon);
            System.out.println("hat: " + hat);
            if (ten.equals("")) {
                this.dichVu.moHopThoaiOK("Tên không hợp lệ!");
                return;
            }
            try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `name` = ?;")) {
                stmt.setString(1, ten);
                try (ResultSet res = stmt.executeQuery()) {
                    if (res.next()) {
                        this.dichVu.moHopThoaiOK("Tên nhân vật đã tồn tại.");
                        return;
                    }
                }
                try (PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO `players` (`account_id`, `name`, `gold`, `cup`, `gem`, `stats_json`, `inventory_json`, `equipped_json`, `pocket_json`, `storage_json`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                    stmt2.setInt(1, this.user_id);
                    stmt2.setString(2, ten);
                    stmt2.setInt(3, 1000000);
                    stmt2.setInt(4, 0);
                    stmt2.setInt(5, 1000);
                    stmt2.setString(6, DEFAULT_STATS_JSON);
                    stmt2.setString(7, "[]");
                    stmt2.setString(8, "[]");
                    stmt2.setString(9, "[]");
                    stmt2.setString(10, "[]");
                    stmt2.executeUpdate();
                }
                try (PreparedStatement stmt3 = conn.prepareStatement("SELECT * FROM `players` WHERE `account_id` = ? LIMIT 1;")) {
                    stmt3.setInt(1, this.user_id);
                    try (ResultSet res2 = stmt3.executeQuery()) {
                        if (res2.next()) {
                            this.nguoiChoi = new ChickenNguoiChoi(this.dichVu);
                            this.nguoiChoi.ma = res2.getInt("id");
                            this.nguoiChoi.ten = ten;
                            this.nguoiChoi.vang = res2.getInt("gold");
                            this.nguoiChoi.ngoc = res2.getInt("gem");
                            this.nguoiChoi.cup = res2.getInt("cup");
                            this.nguoiChoi.x = (short)70;
                            this.nguoiChoi.y = (short)360;
                            this.nguoiChoi.head = head;
                            this.nguoiChoi.hat = hat;
                            this.nguoiChoi.leg = leg;
                            this.nguoiChoi.body = body;
                            this.nguoiChoi.wing = wing;
                            this.nguoiChoi.wp = weapon;
                            JSONObject stats = (JSONObject)JSON.parse(res2.getString("stats_json"));
                            ChickenDuLieuJson p = new ChickenDuLieuJson(stats);
                            int expTai = p.getInt("exp");
                            int capTai = ChickenTienIch.layCap(expTai);
                            int mocTai = p.containsKey("rewardedLevel")
                                    ? Math.max(0, p.getInt("rewardedLevel"))
                                    : capTai;
                            JSONArray jArr = p.getJSONArray("pointAdd");
                            short[] diemTai = new short[6];
                            for (int i = 0; i < 6; ++i) {
                                diemTai[i] = Short.parseShort(
                                        jArr.get(i).toString());
                            }
                            this.nguoiChoi.napTrangThaiTiemNangTuKho(
                                    expTai, mocTai, p.getShort("point"),
                                    diemTai, res2.getLong("stats_revision"));
                            this.nguoiChoi.trainingSuccess = p.getByte("trainingSuccess");
                            this.nguoiChoi.datSoTranThangLuyenTap(p.containsKey("trainingWins") ? p.getInt("trainingWins") : 0);
                            this.nguoiChoi.busyHammer = p.getByte("busyHammer");
                            this.nguoiChoi.nHammer = p.getByte("nHammer");
                            this.nguoiChoi.kill = p.getInt("kill");
                            this.nguoiChoi.chet = p.getInt("dead");
                            this.nguoiChoi.assist = p.getInt("assist");
                this.nguoiChoi.daNhanThanhTich = p.containsKey("achievementClaims") ? p.getInt("achievementClaims") : 0;
                            this.nguoiChoi.powerAvenger = ChickenQuanLyNangLuongAVG.chuanHoaGiaTri(
                                    p.containsKey("avenger")
                                            ? p.getInt("avenger")
                                            : ChickenQuanLyNangLuongAVG.NANG_LUONG_TOI_DA
                            );
                            this.nguoiChoi.power = p.getByte("power");
                            int headId = -1;
                            int legId = -1;
                            int bodyId = -1;
                            int wingId = -1;
                            int maVuKhi = -1;
                            int hatId = -1;
                            for (int ma : ID_TEMPLATE_BODY) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == body) {
                                    bodyId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_LEG) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == leg) {
                                    legId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_WEAPON) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == weapon) {
                                    maVuKhi = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_BALO) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == wing) {
                                    wingId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_HEAD) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == head) {
                                    headId = t.ma;
                                    break;
                                }
                            }
                            for (int ma : ID_TEMPLATE_HAT) {
                                ChickenMauVatPham t = ChickenQuanLyMayChu.itemTemplates.get(ma);
                                if (t.part == hat) {
                                    hatId = t.ma;
                                    break;
                                }
                            }
                            ChickenVatPham vatPham = new ChickenVatPham(headId);
                            vatPham.chiSo = vatPham.mau.loai;
                            vatPham.itemOptions = vatPham.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[vatPham.chiSo] = vatPham;
                            ChickenVatPham item2 = new ChickenVatPham(legId);
                            item2.chiSo = item2.mau.loai;
                            item2.itemOptions = item2.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item2.chiSo] = item2;
                            ChickenVatPham item3 = new ChickenVatPham(bodyId);
                            item3.chiSo = item3.mau.loai;
                            item3.itemOptions = item3.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item3.chiSo] = item3;
                            ChickenVatPham item4 = new ChickenVatPham(wingId);
                            item4.chiSo = item4.mau.loai;
                            item4.itemOptions = item4.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item4.chiSo] = item4;
                            int thamSo = item4.getParamById(13);
                            this.nguoiChoi.itemBalo = new int[thamSo];
                            for (int i = 0; i < thamSo; ++i) {
                                this.nguoiChoi.itemBalo[i] = -1;
                            }
                            ChickenVatPham item5 = new ChickenVatPham(maVuKhi);
                            item5.chiSo = item5.mau.loai;
                            item5.itemOptions = item5.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item5.chiSo] = item5;
                            ChickenVatPham item6 = new ChickenVatPham(hatId);
                            item6.chiSo = item6.mau.loai;
                            item6.itemOptions = item6.mau.thuocTinhs;
                            this.nguoiChoi.itemBody[item6.chiSo] = item6;
                            this.nguoiChoi.dichVu.datNguoiChoi(this.nguoiChoi);
                            this.khach.guiThongTin();
                        } else {
                            this.dichVu.moHopThoaiOK("Có lỗi xảy ra.");
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            Logger.getLogger(ChickenNguoiDung.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void taiDuLieuNguoiChoi() {
        try (java.sql.Connection conn = ChickenCoSoDuLieu.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `players` WHERE `account_id` = ? LIMIT 1;")) {
            stmt.setInt(1, this.user_id);
            ResultSet res = stmt.executeQuery();
            if (res != null && res.next()) {
                this.nguoiChoi = new ChickenNguoiChoi(this.dichVu);
                this.nguoiChoi.ma = res.getInt("id");
                this.nguoiChoi.ten = res.getString("name");
                this.nguoiChoi.vang = res.getInt("gold");
                this.nguoiChoi.ngoc = res.getInt("gem");
                this.nguoiChoi.cup = res.getInt("cup");
                this.nguoiChoi.x = (short)70;
                this.nguoiChoi.y = (short)360;
                this.nguoiChoi.head = (short)-1;
                this.nguoiChoi.hat = (short)-1;
                this.nguoiChoi.leg = (short)-1;
                this.nguoiChoi.body = (short)-1;
                this.nguoiChoi.wing = (short)-1;
                this.nguoiChoi.wp = (short)-1;
                ChickenDuLieuJson p = new ChickenDuLieuJson((JSONObject)JSON.parse(res.getString("stats_json")));
                int expTai = p.getInt("exp");
                int capTai = ChickenTienIch.layCap(expTai);
                int mocTai = p.containsKey("rewardedLevel")
                        ? Math.max(0, p.getInt("rewardedLevel"))
                        : capTai;
                JSONArray jArr = p.getJSONArray("pointAdd");
                short[] diemTai = new short[6];
                for (int i = 0; i < 6; ++i) {
                    diemTai[i] = Short.parseShort(
                            jArr.get(i).toString());
                }
                this.nguoiChoi.napTrangThaiTiemNangTuKho(
                        expTai, mocTai, p.getShort("point"),
                        diemTai, res.getLong("stats_revision"));
                this.nguoiChoi.trainingSuccess = p.getByte("trainingSuccess");
                this.nguoiChoi.datSoTranThangLuyenTap(p.containsKey("trainingWins") ? p.getInt("trainingWins") : 0);
                this.nguoiChoi.busyHammer = p.getByte("busyHammer");
                this.nguoiChoi.nHammer = p.getByte("nHammer");
                this.nguoiChoi.kill = p.getInt("kill");
                this.nguoiChoi.chet = p.getInt("dead");
                this.nguoiChoi.assist = p.getInt("assist");
                this.nguoiChoi.daNhanThanhTich = p.containsKey("achievementClaims") ? p.getInt("achievementClaims") : 0;
                this.nguoiChoi.powerAvenger = ChickenQuanLyNangLuongAVG.chuanHoaGiaTri(
                                    p.containsKey("avenger")
                                            ? p.getInt("avenger")
                                            : ChickenQuanLyNangLuongAVG.NANG_LUONG_TOI_DA
                            );
                this.nguoiChoi.power = p.getByte("power");
                JSONArray bags = (JSONArray)JSON.parse(res.getString("inventory_json"));
                for (int i = 0; i < bags.size(); ++i) {
                    ChickenVatPham vatPham = new ChickenVatPham((JSONObject)bags.get(i));
                    if (vatPham.chiSo >= 0
                            && vatPham.chiSo < this.nguoiChoi.itemBag.length
                            && this.nguoiChoi.itemBag[vatPham.chiSo] == null) {
                        this.nguoiChoi.itemBag[vatPham.chiSo] = vatPham;
                    } else {
                        Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                                Level.WARNING,
                                "Bo qua vat pham tui co chi so khong hop le/bi trung: player={0}, index={1}",
                                new Object[]{this.nguoiChoi.ma, vatPham.chiSo}
                        );
                    }
                }
                JSONArray bodys = (JSONArray)JSON.parse(res.getString("equipped_json"));
                for (int i = 0; i < bodys.size(); ++i) {
                    ChickenVatPham vatPham = new ChickenVatPham((JSONObject)bodys.get(i));
                    if (vatPham.chiSo < 0
                            || vatPham.chiSo >= this.nguoiChoi.itemBody.length
                            || this.nguoiChoi.itemBody[vatPham.chiSo] != null) {
                        Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                                Level.WARNING,
                                "Bo qua trang bi co chi so khong hop le/bi trung: player={0}, index={1}",
                                new Object[]{this.nguoiChoi.ma, vatPham.chiSo}
                        );
                        continue;
                    }
                    this.nguoiChoi.itemBody[vatPham.chiSo] = vatPham;
                    this.nguoiChoi.datTrangBiChoNhanVat(vatPham);
                    if (vatPham.mau.loai == 4) {
                        int thamSo = vatPham.getParamById(13);
                        this.nguoiChoi.itemBalo = new int[thamSo];
                        for (int a = 0; a < thamSo; ++a) {
                            this.nguoiChoi.itemBalo[a] = -1;
                        }
                    }
                }
                JSONArray balos = (JSONArray)JSON.parse(res.getString("pocket_json"));
                for (int i = 0; i < balos.size() && i < this.nguoiChoi.itemBalo.length; ++i) {
                    int chiSo = Integer.parseInt(balos.get(i).toString());
                    if (chiSo >= 0
                            && chiSo < this.nguoiChoi.itemBag.length
                            && this.nguoiChoi.itemBag[chiSo] != null) {
                        this.nguoiChoi.itemBalo[i] = chiSo;
                    }
                }
                JSONArray box = (JSONArray)JSON.parse(res.getString("storage_json"));
                for (int i = 0; i < box.size(); ++i) {
                    ChickenVatPham vatPham = new ChickenVatPham((JSONObject)box.get(i));
                    if (vatPham.chiSo >= 0
                            && vatPham.chiSo < this.nguoiChoi.itemBox.length
                            && this.nguoiChoi.itemBox[vatPham.chiSo] == null) {
                        this.nguoiChoi.itemBox[vatPham.chiSo] = vatPham;
                    } else {
                        Logger.getLogger(ChickenNguoiDung.class.getName()).log(
                                Level.WARNING,
                                "Bo qua vat pham ruong co chi so khong hop le/bi trung: player={0}, index={1}",
                                new Object[]{this.nguoiChoi.ma, vatPham.chiSo}
                        );
                    }
                }
                int thayDoiDiem =
                        ChickenQuanLyTiemNang.dongBoQuyenLoiTheoCapHienTai(
                                this.nguoiChoi);
                if (thayDoiDiem
                        == ChickenQuanLyTiemNang.DONG_BO_THAT_BAI) {
                    int maNguoiChoiLoi = this.nguoiChoi.ma;
                    this.nguoiChoi = null;
                    throw new SQLException(
                            "Khong the dong bo diem tiem nang player="
                            + maNguoiChoiLoi);
                }
                res.close();
                ChickenQuanLyBietDoi.taiClan(this.nguoiChoi);
                this.nguoiChoi.dichVu.datNguoiChoi(this.nguoiChoi);
                return;
            }
            res.close();
        }
        catch (SQLException ex) {
            Logger.getLogger(ChickenNguoiDung.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        if (this.tenDangNhap != null) {
            users.remove(this.tenDangNhap, this);
        }
        if (this.nguoiChoi != null) {
            this.nguoiChoi.close();
        }
    }

    public String toString() {
        return this.tenDangNhap;
    }
}
