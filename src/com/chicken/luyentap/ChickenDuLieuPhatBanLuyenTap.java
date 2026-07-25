package com.chicken.luyentap;

/** Dữ liệu một phát bắn luyện tập đã được kiểm tra và chuẩn hóa. */
public final class ChickenDuLieuPhatBanLuyenTap {
    public final byte loaiDan;
    public final short x;
    public final short y;
    public final short goc;
    public final byte luc;
    public final byte lucPhu;
    public final byte soPhat;

    public ChickenDuLieuPhatBanLuyenTap(byte loaiDan, short x, short y,
            short goc, byte luc, byte lucPhu, byte soPhat) {
        this.loaiDan = loaiDan;
        this.x = x;
        this.y = y;
        this.goc = goc;
        this.luc = luc;
        this.lucPhu = lucPhu;
        this.soPhat = soPhat;
    }
}
