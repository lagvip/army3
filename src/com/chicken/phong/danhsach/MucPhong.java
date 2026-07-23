package com.chicken.phong.danhsach;

import java.io.DataOutputStream;
import java.io.IOException;

/** Chỉ ghi byte cho từng dòng trong danh sách phòng CMD -28. */
public final class MucPhong {
    private MucPhong() {
    }

    /**
     * roomId = -1 là dấu phân cách nhóm theo đúng trình đọc native của client.
     * Client sẽ tự hiển thị tiêu đề và tự thêm dòng "Tạo khu vực".
     */
    public static void ghiTieuDe(DataOutputStream ds, String tieuDe)
            throws IOException {
        ds.writeByte(-1);
        ds.writeUTF(tieuDe);
    }

    /** Ghi một dòng dạng: current/max P<room>-<board> (mapName) money. */
    public static void ghiPhong(
            DataOutputStream ds,
            byte maPhong,
            byte maBan,
            byte maBanDo,
            int soNguoi,
            int toiDa,
            int tien
    ) throws IOException {
        ds.writeByte(maPhong);
        ds.writeByte(maBan);
        ds.writeByte(maBanDo);
        ds.writeByte(gioiHanByteKhongAm(soNguoi));
        ds.writeByte(gioiHanByteKhongAm(toiDa));
        ds.writeInt(Math.max(0, tien));
    }

    private static int gioiHanByteKhongAm(int giaTri) {
        return Math.max(0, Math.min(127, giaTri));
    }
}
