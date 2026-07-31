package com.chicken.cuahang;

import com.chicken.vatpham.ChickenMauVatPham;
import java.util.ArrayList;
import java.util.List;

public class ChickenCuaHang {
    public static final ChickenCuaHang SHOP_EQUIP = new ChickenCuaHang();
    public static final ChickenCuaHang SHOP_ITEM = new ChickenCuaHang();
    public static final int MAX_NUMBER_IN_PAGE = 20;
    public ArrayList<ArrayList<ChickenTrang>> tabs = new ArrayList();
    public byte typeShop;
    public ArrayList<String> shopTabName = new ArrayList();

    public void datLoaiCuaHang(byte loai) {
        this.typeShop = loai;
    }

    public synchronized void themTab(
            String tabName,
            ArrayList<ChickenMauVatPham> vatPhams
    ) {
        this.shopTabName.add(tabName == null ? "" : tabName);
        ArrayList<ChickenTrang> tab = new ArrayList<ChickenTrang>();
        List<ChickenMauVatPham> danhSach =
                vatPhams == null ? List.of() : vatPhams;
        int num = danhSach.size();
        int t = 0;
        while (num > 0) {
            int temp;
            int n = num > 20 ? 20 : num;
            ChickenTrang page = new ChickenTrang();
            for (int i = temp = t * 20; i < temp + n; ++i) {
                ChickenMauVatPham vatPham = danhSach.get(i);
                if (vatPham != null) {
                    page.vatPhams.add(vatPham);
                }
            }
            tab.add(page);
            num -= n;
            ++t;
        }
        /*
         * Client luon mo trang 0 cua moi tab. Tab rong van can mot trang rong
         * de packet xem shop khong bi IndexOutOfBoundsException.
         */
        if (tab.isEmpty()) {
            tab.add(new ChickenTrang());
        }
        this.tabs.add(tab);
    }

    public synchronized boolean coBanVatPham(int maVatPham, byte loaiTien) {
        if (loaiTien != 0 && loaiTien != 1) {
            return false;
        }
        for (ArrayList<ChickenTrang> cacTrang : this.tabs) {
            if (cacTrang == null) {
                continue;
            }
            for (ChickenTrang trang : cacTrang) {
                if (trang == null) {
                    continue;
                }
                for (ChickenMauVatPham vatPham : trang.vatPhams) {
                    if (vatPham == null || (vatPham.ma & 0xFFFF) != maVatPham) {
                        continue;
                    }
                    return loaiTien == 0
                            ? vatPham.buyGold > 0
                            : vatPham.buyGem > 0;
                }
            }
        }
        return false;
    }

    public static boolean coBanTrongCuaHang(
            int maVatPham,
            byte loaiTien
    ) {
        return SHOP_EQUIP.coBanVatPham(maVatPham, loaiTien)
                || SHOP_ITEM.coBanVatPham(maVatPham, loaiTien);
    }
}
