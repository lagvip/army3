package com.chicken.chien;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.avg.ChickenThanhDiChuyenAVG;

public class ChickenChienBinh {
    public final ChickenNguoiChoi nguoiChoi;
    public final byte chiSo;
    public final boolean bot;
    public String ten;
    public final int ma;
    public final short maVuKhi;
    public final byte avenger;
    public short x;
    public short y;
    public int hp;
    public int mauToiDa;
    public boolean chet;
    public final int tanCong;
    public final int giap;
    public final int mayMan;
    public final int tocDo;
    /** Quãng đường chủ động còn lại trong lượt hiện tại. */
    public int quangDuongDiChuyenConLai;
    public int hawkSoLuotBan;
    public boolean hawkDaDungKyNang;
    public boolean hawkDaGuiChonMucTieu;
    public boolean thorDaDungKyNang;
    public boolean thorDaGuiMenu;
    public boolean lokiDaDungKyNang;
    public boolean lokiDaGuiMenu;
    public boolean lokiDangChoChonMucTieu;
    public boolean lokiSkillActive;
    public boolean ultronDaDungKyNang;
    public boolean ultronDaGuiMenu;
    public boolean ultronDangBanX3;
    public short ultronGocNgamHienTai = 45;
    public byte ultronLucNgamHienTai = 30;
    public boolean ultronDaCoGocNgam;

    public ChickenChienBinh(ChickenNguoiChoi nguoiChoi, byte chiSo, short x, short y) {
        this.nguoiChoi = nguoiChoi;
        this.chiSo = chiSo;
        this.bot = false;
        this.ten = nguoiChoi.ten;
        this.ma = nguoiChoi.ma;
        this.avenger = nguoiChoi.avenger;
        this.maVuKhi = layVuKhiHienThiTrongTran(nguoiChoi);
        this.x = x;
        this.y = y;
        this.mauToiDa = Math.max(100, nguoiChoi.layTongMauHienTai());
        this.tanCong = Math.max(0, nguoiChoi.layTongTanCongHienTai());
        this.giap = Math.max(0, nguoiChoi.layTongGiapHienTai());
        this.mayMan = Math.max(0, nguoiChoi.layTongMayManHienTai());
        this.tocDo = Math.max(0, nguoiChoi.layTongTocDoHienTai());
        this.quangDuongDiChuyenConLai = ChickenThanhDiChuyenAVG.hoiDay(this.avenger);
        this.hp = this.mauToiDa;
    }

    public ChickenChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger) {
        this.nguoiChoi = null;
        this.chiSo = chiSo;
        this.bot = true;
        this.ten = ten;
        this.ma = -9000 - chiSo;
        this.maVuKhi = maVuKhi;
        this.avenger = avenger;
        this.x = x;
        this.y = y;
        this.mauToiDa = 160;
        this.tanCong = 20;
        this.giap = 0;
        this.mayMan = 0;
        this.tocDo = 0;
        this.quangDuongDiChuyenConLai = ChickenThanhDiChuyenAVG.hoiDay(this.avenger);
        this.hp = this.mauToiDa;
    }

    /** Tạo chiến binh boss có chỉ số cố định cho các trận boss riêng. */
    public ChickenChienBinh(
            byte chiSo,
            int ma,
            short x,
            short y,
            String ten,
            short maVuKhi,
            int mauToiDa,
            int tanCong,
            int giap
    ) {
        this.nguoiChoi = null;
        this.chiSo = chiSo;
        this.bot = true;
        this.ten = ten;
        this.ma = ma;
        this.maVuKhi = maVuKhi;
        this.avenger = 0;
        this.x = x;
        this.y = y;
        this.mauToiDa = Math.max(1, mauToiDa);
        this.hp = this.mauToiDa;
        this.tanCong = Math.max(0, tanCong);
        this.giap = Math.max(0, giap);
        this.mayMan = 0;
        this.tocDo = 0;
        this.quangDuongDiChuyenConLai = 0;
    }

    private static short layVuKhiHienThiTrongTran(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return (short) -1;
        }
        /*
         * Mọi bộ AVG (391-398), không riêng Ultron, đều ẩn sprite súng bằng
         * cách đặt wp=-1. ID vật lý/collision vẫn phải lấy từ itemBody[5];
         * nếu dùng wp thì Captain thành súng -1 và không bao giờ vào nhánh
         * xuyên người, Winter Soldier cũng không vào nhánh xuyên địa hình.
         */
        short partSungDangTrangBi = nguoiChoi.layMaHinhSungDangTrangBi();
        return partSungDangTrangBi >= 0
                ? partSungDangTrangBi
                : nguoiChoi.wp;
    }

    public boolean coPhien() {
        return this.nguoiChoi != null && this.nguoiChoi.dichVu != null;
    }

    public byte phanTramMau() {
        if (this.mauToiDa <= 0) {
            return 0;
        }
        return (byte)Math.max(0, Math.min(100, this.hp * 100 / this.mauToiDa));
    }
}
