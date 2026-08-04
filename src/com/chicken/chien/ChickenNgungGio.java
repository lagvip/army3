package com.chicken.chien;

import com.chicken.gio.ChickenHeThongGio;

/** Luat server-authoritative cua item Ngung gio (ID 225). */
public final class ChickenNgungGio {
    public static final int ID_VAT_PHAM = 225;
    /** Một gói mua trong shop cấp ba vật phẩm dùng, đúng như mô tả client. */
    public static final int SO_LUONG_NHAN_MOI_GOI_MUA = 3;

    private ChickenNgungGio() {
    }

    public static boolean coTheDung(ChickenChienBinh nguoiDung) {
        return nguoiDung != null
                && nguoiDung.laNguoiChoiThat()
                && !nguoiDung.daRoiTran
                && !nguoiDung.chet
                && nguoiDung.hp > 0;
    }

    public static ChickenHeThongGio.TrangThaiGio taoTrangThaiKhongGio() {
        return ChickenHeThongGio.khongGio();
    }
}
