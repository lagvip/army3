package com.chicken.mang;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.bando.ChickenDuLieuBanDo;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mohinh.ChickenNguoiDung;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.IChickenDichVuGame;
import com.chicken.mang.IChickenXuLyTin;
import com.chicken.mang.IChickenPhien;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.mang.ChickenXuLyTin;
import com.chicken.nhapvai.ChickenBanDoRPG;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChickenPhien
implements IChickenPhien {
    private final byte[] khoa = new byte[]{0};
    public Channel kenh;
    public int ma;
    public ChickenNguoiDung user;
    private IChickenXuLyTin boXuLyTin;
    private IChickenDichVuGame dichVu;
    protected boolean daKetNoi;
    protected boolean dangNhap;
    private byte curR;
    private byte curW;
    protected String phienBan;
    protected byte loaiKhach;
    protected byte mucPhong;
    protected byte nhaCungCap;
    protected boolean heThongXong;
    protected int svReceived_clSended;
    protected int svSended_clReceived;
    protected List<ChickenTinNhan> vResendMessage = new ArrayList<ChickenTinNhan>();
    public long timeConnected;
    public static final HashMap<String, Count> sessions = new HashMap();
    private String maPhien;
    private volatile boolean kichHoat = true;
    private ScheduledFuture<?> tacVuGiuKetNoi;

    public ChickenPhien(Channel kenh, int ma) {
        this.kenh = kenh;
        this.ma = ma;
        this.datBoXuLy(new ChickenXuLyTin(this));
        this.datDichVu(new ChickenDichVuGame(this));
    }

    public ChickenTinNhan thuGiaiMaTin(ByteBuf in) {
        if (!in.isReadable()) {
            return null;
        }
        in.markReaderIndex();
        try {
            int kichThuoc;
            if (!in.isReadable()) {
                return null;
            }
            byte cmd = in.readByte();
            if (this.daKetNoi) {
                cmd = this.docKhoa(cmd);
            }
            if (this.daKetNoi) {
                if (in.readableBytes() < 2) {
                    in.resetReaderIndex();
                    return null;
                }
                byte b1 = in.readByte();
                byte b2 = in.readByte();
                kichThuoc = (this.docKhoa(b1) & 0xFF) << 8 | this.docKhoa(b2) & 0xFF;
            } else {
                if (in.readableBytes() < 2) {
                    in.resetReaderIndex();
                    return null;
                }
                kichThuoc = in.readUnsignedShort();
            }
            if (in.readableBytes() < kichThuoc) {
                in.resetReaderIndex();
                return null;
            }
            byte[] duLieu = new byte[kichThuoc];
            in.readBytes(duLieu);
            if (this.daKetNoi) {
                for (int i = 0; i < duLieu.length; ++i) {
                    duLieu[i] = this.docKhoa(duLieu[i]);
                }
            }
            return new ChickenTinNhan(cmd, duLieu);
        }
        catch (Exception e) {
            in.resetReaderIndex();
            return null;
        }
    }

    public void maHoaTin(ChickenTinNhan m, ByteBuf out) {
        byte[] duLieu = m.layDuLieu();
        byte b = m.layLenh();
        if (this.daKetNoi) {
            out.writeByte((int)this.ghiKhoa(b));
        } else {
            out.writeByte((int)b);
        }
        if (this.laTinLon(b)) {
            this.maHoaTinLon(duLieu, out);
            m.donDep();
            return;
        }
        if (duLieu != null) {
            int kichThuoc = duLieu.length;
            if (this.daKetNoi) {
                out.writeByte((int)this.ghiKhoa((byte)(kichThuoc >> 8)));
                out.writeByte((int)this.ghiKhoa((byte)(kichThuoc & 0xFF)));
                byte[] encrypted = new byte[duLieu.length];
                for (int i = 0; i < duLieu.length; ++i) {
                    encrypted[i] = this.ghiKhoa(duLieu[i]);
                }
                out.writeBytes(encrypted);
            } else {
                out.writeByte(kichThuoc >> 8);
                out.writeByte(kichThuoc & 0xFF);
                out.writeBytes(duLieu);
            }
        } else if (this.daKetNoi) {
            out.writeByte((int)this.ghiKhoa((byte)0));
            out.writeByte((int)this.ghiKhoa((byte)0));
        } else {
            out.writeByte(0);
            out.writeByte(0);
        }
        if (!ChickenPhien.laTinDacBiet(m)) {
            ++this.svSended_clReceived;
        }
        m.donDep();
    }

    private void maHoaTinLon(byte[] duLieu, ByteBuf out) {
        int kichThuoc = duLieu.length;
        out.writeByte(kichThuoc >> 24);
        out.writeByte(kichThuoc >> 16);
        out.writeByte(kichThuoc >> 8);
        out.writeByte(kichThuoc & 0xFF);
        out.writeBytes(duLieu);
    }

    public void khiNhanTin(ChickenTinNhan tin) {
        if (!this.kichHoat) {
            tin.donDep();
            return;
        }
        try {
            if (tin.layLenh() == -27) {
                this.guiKhoa();
            } else if (tin.layLenh() == -127) {
                this.dongBo(tin);
            } else {
                if (!ChickenPhien.laTinDacBiet(tin)) {
                    ++this.svReceived_clSended;
                }
                this.boXuLyTin.khiCoTin(tin);
            }
        }
        catch (Exception e) {
            ChickenQuanLyMayChu.log("Error handling message from " + String.valueOf(this) + ": " + e.getMessage());
            this.dongTin();
        }
        finally {
            tin.donDep();
        }
    }

    public void khiKenhNgat() {
        if (this.kichHoat) {
            this.dongTin();
        }
    }

    private void dongBo(ChickenTinNhan ms) throws IOException {
        byte loai = ms.boDoc().readByte();
        if (loai == 0) {
            String oldSessionId = ms.boDoc().readUTF();
            int clSended = ms.boDoc().readInt();
            int clReceived = ms.boDoc().readInt();
            Count c = sessions.get(oldSessionId);
            if (c == null) {
                this.guiMaPhien(0);
                return;
            }
            System.out.println(c.svReceived_clSended);
            this.svReceived_clSended = c.svReceived_clSended;
            this.svSended_clReceived = c.svSended_clReceived;
            this.vResendMessage = new ArrayList<ChickenTinNhan>(c.vResendMessage);
            sessions.remove(this.maPhien);
            this.guiMaPhien(1);
            if (clReceived != this.svSended_clReceived) {
                int num3 = this.vResendMessage.size() - (this.svReceived_clSended - clReceived);
                if (num3 < 0) {
                    num3 = 0;
                }
                this.guiLaiTinTu(num3);
            }
        } else if (loai == 2) {
            this.heThongXong = true;
            this.svSended_clReceived = 0;
            this.svReceived_clSended = 0;
            this.vResendMessage.clear();
        }
    }

    public void datLoaiKhach(ChickenTinNhan mss) throws IOException {
        this.loaiKhach = mss.boDoc().readByte();
        byte mucPhongYeuCau = mss.boDoc().readByte();
        if (mucPhongYeuCau < 1 || mucPhongYeuCau > 4) {
            throw new IOException("Muc phong client khong hop le: " + mucPhongYeuCau);
        }
        this.mucPhong = mucPhongYeuCau;
        System.out.println(mucPhong);
        this.phienBan = mss.boDoc().readUTF();
        ((ChickenDichVuGame)this.dichVu).hienTaiXuong();
    }

    protected void guiVaChamBanDo() throws IOException {
        ChickenTinNhan ms = new ChickenTinNhan(92);
        DataOutputStream ds = ms.boGhi();
        ds.writeShort(ChickenDuLieuBanDo.undestroyTile.length);
        for (int i = 0; i < ChickenDuLieuBanDo.undestroyTile.length; ++i) {
            ds.writeShort(ChickenDuLieuBanDo.undestroyTile[i]);
        }
        ds.flush();
        this.guiTin(ms);
    }

    public void taiXuong() throws IOException {
        ChickenDichVuGame sv = (ChickenDichVuGame)this.dichVu;
        sv.taiXuong();
        File[] files = new File("res/data/" + this.mucPhong + "/").listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            try (FileInputStream fis = new FileInputStream(file);){
                byte[] duLieu = fis.readAllBytes();
                sv.guiTep(file.getName().replaceAll(".png", ""), duLieu);
            }
        }
    }

    public void datNhaCungCap(ChickenTinNhan ms) throws IOException {
        this.nhaCungCap = ms.boDoc().readByte();
    }

    @Override
    public boolean dangKetNoi() {
        return this.daKetNoi && this.kichHoat && this.kenh != null && this.kenh.isActive();
    }

    @Override
    public void datBoXuLy(IChickenXuLyTin boXuLyTin) {
        this.boXuLyTin = boXuLyTin;
    }

    @Override
    public void datDichVu(IChickenDichVuGame dichVu) {
        this.dichVu = dichVu;
    }

    public IChickenDichVuGame layDichVu() {
        return this.dichVu;
    }

    @Override
    public void guiTin(ChickenTinNhan tin) {
        if (this.vResendMessage.size() < 200) {
            if (!ChickenPhien.laTinDacBiet(tin)) {
                this.vResendMessage.add(tin);
            }
        } else {
            this.vResendMessage.removeFirst();
        }
        this.dayTin(tin);
    }

    private void dayTin(ChickenTinNhan tin) {
        if (this.kenh != null && this.kenh.isActive()) {
            this.kenh.writeAndFlush((Object)tin);
        }
    }

    private static boolean laTinDacBiet(ChickenTinNhan tin) {
        return tin.layLenh() == -27 || tin.layLenh() == -127 || tin.layLenh() == -98 || tin.layLenh() == -102;
    }

    private boolean laTinLon(byte cmd) {
        return cmd == -120 || cmd == -31 || cmd == -41 || cmd == -60 || cmd == -92;
    }

    private byte docKhoa(byte b) {
        byte b2 = this.curR;
        this.curR = (byte)(b2 + 1);
        byte ketQua = (byte)(this.khoa[b2 & 0xFF] & 0xFF ^ b & 0xFF);
        if (this.curR >= this.khoa.length) {
            this.curR = (byte)(this.curR % this.khoa.length);
        }
        return ketQua;
    }

    private byte ghiKhoa(byte b) {
        byte b2 = this.curW;
        this.curW = (byte)(b2 + 1);
        byte ketQua = (byte)(this.khoa[b2 & 0xFF] & 0xFF ^ b & 0xFF);
        if (this.curW >= this.khoa.length) {
            this.curW = (byte)(this.curW % this.khoa.length);
        }
        return ketQua;
    }

    @Override
    public void close() {
        if (!this.kichHoat) {
            return;
        }
        this.kichHoat = false;
        Count c = new Count();
        c.svReceived_clSended = this.svReceived_clSended;
        c.svSended_clReceived = this.svSended_clReceived;
        c.vResendMessage = new ArrayList<ChickenTinNhan>(this.vResendMessage);
        if (this.maPhien != null) {
            sessions.put(this.maPhien, c);
        }
        try {
            if (this.user != null) {
                this.user.close();
            }
            ChickenQuanLyMayChu.disconnect(this);
            this.donMang();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void donMang() {
        this.curR = 0;
        this.curW = 0;
        this.daKetNoi = false;
        this.dangNhap = false;
        if (this.tacVuGiuKetNoi != null) {
            this.tacVuGiuKetNoi.cancel(false);
            this.tacVuGiuKetNoi = null;
        }
        if (this.kenh != null) {
            this.kenh.close();
            this.kenh = null;
        }
    }

    public String mayXa() {
        if (this.kenh == null || this.kenh.remoteAddress() == null) {
            return null;
        }
        String raw = this.kenh.remoteAddress().toString();
        if (raw.startsWith("/")) {
            raw = raw.substring(1);
        }
        int colon = raw.lastIndexOf(':');
        if (colon > 0) {
            return raw.substring(0, colon);
        }
        return raw;
    }

    public String moTa() {
        StringBuilder sb = new StringBuilder();
        sb.append('#').append(this.ma);
        String mayChu = this.mayXa();
        if (mayChu != null && !mayChu.isEmpty()) {
            sb.append(" @").append(mayChu);
        }
        if (this.user != null) {
            sb.append(" tk=").append(this.user.toString());
            if (this.user.nguoiChoi != null && this.user.nguoiChoi.ten != null) {
                sb.append(" nv=").append(this.user.nguoiChoi.ten);
            }
        }
        return sb.toString();
    }

    public String toString() {
        if (this.user != null) {
            return this.user.toString();
        }
        return "Client " + this.ma;
    }

    public void guiKhoa() throws IOException {
        this.maPhien = "nvn_" + System.currentTimeMillis();
        ChickenTinNhan ms = new ChickenTinNhan(-27);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(this.khoa.length);
        ds.writeByte(this.khoa[0]);
        for (int i = 1; i < this.khoa.length; ++i) {
            ds.writeByte(this.khoa[i] ^ this.khoa[i - 1]);
        }
        ds.writeUTF(this.maPhien);
        ds.flush();
        this.dayTin(ms);
        this.daKetNoi = true;
        this.timeConnected = System.currentTimeMillis();
        this.batGiuKetNoi();
    }

    private void batGiuKetNoi() {
        if (this.kenh != null && this.kenh.eventLoop() != null) {
            this.tacVuGiuKetNoi = this.kenh.eventLoop().scheduleAtFixedRate(() -> {
                if (this.dangKetNoi()) {
                    this.guiTin(new ChickenTinNhan(-102));
                    this.dongBoThanhTrangThai();
                }
            }, 2L, 2L, TimeUnit.SECONDS);
        }
    }

    /**
     * Client JavaME cÃ³ thá»ƒ khá»Ÿi táº¡o láº¡i MenuScr sau gÃ³i Ä‘Äƒng nháº­p vÃ  lÃ m
     * hai thanh sá»©c máº¡nh/nÄƒng lÆ°á»£ng AVG trá»Ÿ vá» 0. Äá»“ng bá»™ cÃ¹ng nhá»‹p
     * keep-alive Ä‘á»ƒ giao diá»‡n luÃ´n pháº£n Ã¡nh giÃ¡ trá»‹ tháº­t trÃªn server.
     */
    private void dongBoThanhTrangThai() {
        if (this.user == null
                || this.user.nguoiChoi == null
                || this.user.dichVu == null) {
            return;
        }
        this.user.dichVu.capNhatAvenger();
        this.user.dichVu.capNhatSucManh();
    }

    public void taiDuLieuXong() throws IOException {
        if (this.user.nguoiChoi != null) {
            this.guiThongTin();
        } else {
            this.user.dichVu.taoNhanVat();
        }
    }

    public void guiThongTin() throws IOException {
        ChickenNguoiChoi.players_id.put(this.user.nguoiChoi.ma, this.user.nguoiChoi);
        this.guiVaChamBanDo();
        this.user.dichVu.guiDoTrenNguoi();
        this.user.dichVu.guiTuiDo();
        this.user.dichVu.guiRuongDo();
        this.user.dichVu.guiBalo();
        this.user.dichVu.guiThongTin();
        ChickenBanDoRPG.vao(this.user.nguoiChoi);
        this.user.dichVu.capNhatKDVaKDA();
        this.user.dichVu.capNhatAvenger();
        this.user.dichVu.capNhatSucManh();
    }

    public void dangKy(ChickenTinNhan ms) throws IOException {
        String tenDangNhap = ms.boDoc().readUTF();
        String matKhau = ms.boDoc().readUTF();
        String usernameAo = ms.boDoc().readUTF();
        System.out.println("Yeu cau dang ky: " + tenDangNhap + " usernameAo: " + usernameAo);
    }

    public void guiLaiTinTu(int chiSo) {
        try {
            for (int i = chiSo; i < this.vResendMessage.size(); ++i) {
                ChickenTinNhan tin = this.vResendMessage.get(i);
                this.dayTin(tin);
            }
            this.guiMaPhien(2);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        this.vResendMessage.clear();
    }

    public void guiMaPhien(int loai) {
        if (this.maPhien == null) {
            this.heThongXong = true;
            this.daKetNoi = true;
            return;
        }
        try {
            ChickenTinNhan tin = new ChickenTinNhan(-127);
            tin.boGhi().write(loai);
            if (loai == 0) {
                tin.boGhi().writeUTF(this.maPhien);
                tin.boGhi().writeInt(this.svReceived_clSended);
                tin.boGhi().writeInt(this.svSended_clReceived);
                this.dayTin(tin);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void dangNhap(ChickenTinNhan ms) throws IOException {
        byte loai;
        String phienBan;
        String matKhau;
        if (this.dangNhap) {
            return;
        }
        String tenDangNhap = ms.boDoc().readUTF();
        ChickenNguoiDung us = ChickenNguoiDung.dangNhap(this, tenDangNhap, matKhau = ms.boDoc().readUTF(), phienBan = ms.boDoc().readUTF(), loai = ms.boDoc().readByte());
        if (us != null) {
            this.user = us;
            this.user.taiDuLieuNguoiChoi();
            this.user.dichVu.guiPhienBan();
        }
    }

    public void dangNhap2(ChickenTinNhan ms) throws IOException {
        String tenDangNhap = ms.boDoc().readUTF();
        ChickenNguoiDung.dangNhap2(this, tenDangNhap);
    }

    public void dangXuat() {
        this.guiTin(new ChickenTinNhan(2));
        this.dongTin();
    }

    public void dongTin() {
        if (this.kichHoat) {
            if (this.boXuLyTin != null) {
                this.boXuLyTin.khiMatKetNoi();
            }
            this.close();
        }
    }

    private static class Count {
        protected int svReceived_clSended;
        protected int svSended_clReceived;
        private List<ChickenTinNhan> vResendMessage = new ArrayList<ChickenTinNhan>();

        private Count() {
        }
    }
}
