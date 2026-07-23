package com.chicken.vatpham;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.tienich.ChickenDuLieuJson;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.util.Vector;

public class ChickenVatPham {
    public ChickenMauVatPham mau;
    public int ma;
    public int soLuong;
    public int HP;
    public int chiSo;
    public Vector itemOptions = new Vector();
    public byte nSocket;
    public boolean isSocketing;
    public long socketFinishTime;
    public int nGem;

    public ChickenVatPham(int ma) {
        this.ma = ma;
        this.mau = ChickenQuanLyMayChu.itemTemplates.get(ma);
        this.HP = 100;
        this.soLuong = 1;
        this.nSocket = 0;
        this.isSocketing = false;
    }

    public ChickenVatPham(JSONObject doiTuong) {
        this.tai(doiTuong);
    }

    public boolean isTypeBody() {
        return this.mau.loai >= 0 && this.mau.loai < 6;
    }

    public void tai(JSONObject doiTuong) {
        ChickenDuLieuJson parse = new ChickenDuLieuJson(doiTuong);
        this.ma = parse.getInt("id");
        this.mau = ChickenQuanLyMayChu.itemTemplates.get(this.ma);
        this.soLuong = parse.getInt("quantity");
        this.HP = parse.getInt("HP");
        this.chiSo = parse.getByte("index");
        if (parse.containsKey("options")) {
            JSONArray jArr = parse.getJSONArray("options");
            for (int i = 0; i < jArr.size(); ++i) {
                ChickenDuLieuJson d = new ChickenDuLieuJson((JSONObject)jArr.get(i));
                int ma = d.getInt("id");
                int thamSo = d.getInt("param");
                if (ma == 15) {
                    this.isSocketing = true;
                    this.socketFinishTime = thamSo * 1000;
                } else if (ma == 16) {
                    this.nSocket = (byte)(this.nSocket + 1);
                    if (thamSo != 0) {
                        ++this.nGem;
                    }
                }
                this.itemOptions.add(new ChickenThuocTinhVatPham(ma, thamSo));
            }
        }
    }

    public JSONObject toJSONObject() {
        JSONObject doiTuong = new JSONObject();
        doiTuong.put("id", this.ma);
        doiTuong.put("quantity", this.soLuong);
        doiTuong.put("HP", this.HP);
        doiTuong.put("index", this.chiSo);
        JSONArray thuocTinhs = new JSONArray();
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            ChickenThuocTinhVatPham op = (ChickenThuocTinhVatPham)this.itemOptions.get(i);
            JSONObject option = new JSONObject();
            option.put("id", op.optionTemplate.ma);
            option.put("param", op.thamSo);
            thuocTinhs.add((Object)option);
        }
        doiTuong.put("options", thuocTinhs);
        return doiTuong;
    }

    public int getParamById(int ma) {
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            ChickenThuocTinhVatPham o = (ChickenThuocTinhVatPham)this.itemOptions.get(i);
            if (o.optionTemplate.ma != ma) continue;
            return o.thamSo;
        }
        return -1;
    }
}

