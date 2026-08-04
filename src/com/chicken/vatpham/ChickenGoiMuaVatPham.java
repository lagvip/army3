package com.chicken.vatpham;

/**
 * So vat pham server cap cho mot goi mua trong shop.
 *
 * Client chi gui so goi muon mua; gia va so luong thuc nhan deu do server
 * quyet dinh tu ID mau vat pham.
 */
public final class ChickenGoiMuaVatPham {
    public static final int ID_NGUNG_GIO = 225;
    public static final int ID_DAN_TRAI_PHA = 231;
    public static final int ID_DAN_LAZER = 235;
    public static final int ID_DAN_VOI_RONG = 236;
    public static final int ID_CHUOT_GAN_BOM = 237;
    public static final int ID_TEN_LUA_X4 = 238;
    public static final int ID_DAN_XUYEN_DAT = 239;

    private ChickenGoiMuaVatPham() {
    }

    public static int soLuongNhanMoiGoi(int idVatPham) {
        switch (idVatPham) {
            case ID_NGUNG_GIO:
                return 3;
            case ID_DAN_VOI_RONG:
            case ID_DAN_XUYEN_DAT:
                return 2;
            case ID_DAN_TRAI_PHA:
            case ID_DAN_LAZER:
            case ID_CHUOT_GAN_BOM:
            case ID_TEN_LUA_X4:
                return 5;
            default:
                return 1;
        }
    }
}
