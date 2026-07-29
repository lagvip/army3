package com.chicken.dulieu;

import java.util.NavigableMap;
import java.util.TreeMap;

public class ChickenTieuDeCap {
    /**
     * ID level cũng chính là chỉ số mảng mà client dùng. TreeMap giữ thứ tự
     * 0..N khi đóng gói dataLevel, không phụ thuộc thứ tự bucket của HashMap.
     */
    public static final NavigableMap<Integer, ChickenTieuDeCap> levels =
            new TreeMap<Integer, ChickenTieuDeCap>();
    public int ma;
    public String ten;
    public int kinhNghiem;
    public short icon;
}
