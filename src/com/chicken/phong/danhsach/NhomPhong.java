package com.chicken.phong.danhsach;

/**
 * Cấu hình một tiêu đề nhóm trong danh sách khu vực native của client.
 *
 * Client tự thêm dòng "Tạo khu vực" ngay sau mỗi tiêu đề được gửi bằng
 * roomId = -1, vì vậy server chỉ cần khai báo tiêu đề và các mã phòng thuộc
 * nhóm đó.
 */
public final class NhomPhong {
    private final String tieuDe;
    private final byte[] maPhongThuong;
    private final boolean coPhongBoss;

    public NhomPhong(String tieuDe, byte[] maPhongThuong, boolean coPhongBoss) {
        this.tieuDe = tieuDe;
        this.maPhongThuong = maPhongThuong == null
                ? new byte[0]
                : maPhongThuong.clone();
        this.coPhongBoss = coPhongBoss;
    }

    public String getTieuDe() {
        return this.tieuDe;
    }

    public byte[] getMaPhongThuong() {
        return this.maPhongThuong.clone();
    }

    public boolean coPhongBoss() {
        return this.coPhongBoss;
    }
}
