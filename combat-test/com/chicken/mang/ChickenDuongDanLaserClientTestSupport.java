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

        short[] lechGioX = {(short) 100, (short) 120, (short) 137,
            (short) 163, (short) 189};
        short[] lechGioY = {(short) 100, (short) 88, (short) 82,
            (short) 91, (short) 100};
        ChickenDuongDanLaserClient.DuLieu tiaPhai =
                ChickenDuongDanLaserClient.tao(
                        lechGioX, lechGioY, (short) 100, (short) 100);
        bang(26, tiaPhai.buocX,
                "laser van lay dx doan parabol thay vi tia that");
        bang(-9, tiaPhai.buocY,
                "laser van lay dy doan parabol thay vi tia that");

        short[] lechGioTraiX = {(short) 300, (short) 282, (short) 267,
            (short) 241, (short) 215};
        ChickenDuongDanLaserClient.DuLieu tiaTrai =
                ChickenDuongDanLaserClient.tao(
                        lechGioTraiX, lechGioY, (short) 300, (short) 100);
        bang(-26, tiaTrai.buocX,
                "laser trai bi dao sai huong X");
        bang(-9, tiaTrai.buocY,
                "laser trai bi dao sai huong Y");

        /*
         * Dinh phang xuat hien that trong cong thuc laser: buoc 18,0 van la
         * parabol; ngay sau do tia that doi sang 29,6. Adapter phai chon diem
         * cuoi cua dinh phang va gui 29,-6 cho cach ve doi dau Y cua client.
         */
        ChickenDuongDanLaserClient.DuLieu dinhPhang =
                ChickenDuongDanLaserClient.tao(
                        new short[]{299, 317, 335, 353, 371, 389, 407, 425,
                            443, 461, 479, 508, 537, 566},
                        new short[]{329, 322, 316, 311, 306, 302, 299, 296,
                            294, 293, 293, 299, 305, 311},
                        (short) 281,
                        (short) 336);
        bang(29, dinhPhang.buocX,
                "laser lay nham buoc ngang dau dinh phang");
        bang(-6, dinhPhang.buocY,
                "laser sai do doc sau dinh phang");

        ChickenDuongDanLaserClient.DuLieu tiaNgan =
                ChickenDuongDanLaserClient.tao(
                        new short[]{10, 11, 12},
                        new short[]{10, 9, 10},
                        (short) 10,
                        (short) 10);
        bang(2, tiaNgan.buocX,
                "buoc laser ngan bi chia doi thanh 0 tren client FPS cao");
        bang(-2, tiaNgan.buocY,
                "buoc Y laser ngan bi chia doi thanh 0 tren client FPS cao");
    }

    private static void bang(int mongDoi, int thucTe, String thongBao) {
        if (mongDoi != thucTe) {
            throw new AssertionError(
                    thongBao + ": mongDoi=" + mongDoi + " thucTe=" + thucTe);
        }
    }
}
