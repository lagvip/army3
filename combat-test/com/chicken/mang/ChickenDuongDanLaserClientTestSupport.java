package com.chicken.mang;

/** Hoi quy packet laser: hinh client phai trung duong va cham server. */
public final class ChickenDuongDanLaserClientTestSupport {
    private ChickenDuongDanLaserClientTestSupport() {
    }

    public static void tuKiemTra() {
        short[] x = {(short) 831, (short) 800, (short) 760, (short) 256};
        short[] y = {(short) 351, (short) 340, (short) 351, (short) 351};

        ChickenDuongDanLaserClient.DuLieu sungThuong =
                ChickenDuongDanLaserClient.tao(
                        x, y, (short) 849, (short) 381);
        bang(831, sungThuong.x[0],
                "laser thuong bi neo xuong chan nguoi ban");
        bang(351, sungThuong.y[0],
                "laser thuong sai cao do dau nong");
        bang(256, sungThuong.x[sungThuong.x.length - 1],
                "laser hien thi khong giu diem va cham server");
        bang(351, sungThuong.y[sungThuong.y.length - 1],
                "laser hien thi sai Y diem va cham server");

        ChickenDuongDanLaserClient.DuLieu avgCanNeo =
                ChickenDuongDanLaserClient.tao(
                        x, y, (short) 849, (short) 381, true);
        bang(849, avgCanNeo.x[0],
                "animation AVG khong con neo vao nhan vat");
        bang(381, avgCanNeo.y[0],
                "animation AVG sai Y neo nhan vat");
    }

    private static void bang(int mongDoi, int thucTe, String thongBao) {
        if (mongDoi != thucTe) {
            throw new AssertionError(
                    thongBao + ": mongDoi=" + mongDoi + " thucTe=" + thucTe);
        }
    }
}
