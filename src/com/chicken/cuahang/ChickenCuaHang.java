package com.chicken.cuahang;

import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.cuahang.ChickenTrang;
import java.util.ArrayList;

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

    public void themTab(String tabName, ArrayList<ChickenMauVatPham> vatPhams) {
        this.shopTabName.add(tabName);
        ArrayList<ChickenTrang> tab = new ArrayList<ChickenTrang>();
        int num = vatPhams.size();
        int t = 0;
        while (num > 0) {
            int temp;
            int n = num > 20 ? 20 : num;
            ChickenTrang page = new ChickenTrang();
            for (int i = temp = t * 20; i < temp + n; ++i) {
                page.vatPhams.add(vatPhams.get(i));
            }
            tab.add(page);
            num -= n;
            ++t;
        }
        this.tabs.add(tab);
    }
}

