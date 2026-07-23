package com.chicken.vatpham;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenMauThuocTinhVatPham;

public class ChickenThuocTinhVatPham {
    public byte kichHoat;
    public boolean isCompareOption;
    public int num;
    public ChickenMauThuocTinhVatPham optionTemplate;
    public int thamSo;

    public ChickenThuocTinhVatPham(int optionTemplateId, int thamSo) {
        this.thamSo = thamSo;
        this.optionTemplate = ChickenQuanLyMayChu.iOptionTemplates.get(optionTemplateId);
    }
}

