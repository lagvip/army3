package com.chicken.vatpham;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.tienich.ChickenDuLieuJson;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class ChickenVatPham {
    public static final int DO_BEN_TOI_DA = 100;
    public static final int SO_SOCKET_TOI_DA = 3;

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
        this.HP = DO_BEN_TOI_DA;
        this.soLuong = 1;
        this.nSocket = 0;
        this.isSocketing = false;
    }

    public ChickenVatPham(JSONObject doiTuong) {
        this.tai(doiTuong);
    }

    public boolean isTypeBody() {
        return this.mau != null && this.mau.loai >= 0 && this.mau.loai < 6;
    }

    public void tai(JSONObject doiTuong) {
        ChickenDuLieuJson parse = new ChickenDuLieuJson(doiTuong);
        this.ma = parse.getInt("id");
        this.mau = ChickenQuanLyMayChu.itemTemplates.get(this.ma);
        this.soLuong = parse.getInt("quantity");
        // Do ben hien la co che vo han. Khong tin gia tri HP cu trong DB va
        // khong cho du lieu import tao trang bi hong/qua 100 do ben.
        this.HP = DO_BEN_TOI_DA;
        this.chiSo = parse.getByte("index");
        if (parse.containsKey("options")) {
            JSONArray jArr = parse.getJSONArray("options");
            Set<Integer> optionDaCo = new HashSet<Integer>();
            for (int i = 0; i < jArr.size(); ++i) {
                ChickenDuLieuJson d = new ChickenDuLieuJson((JSONObject)jArr.get(i));
                int ma = d.getInt("id");
                int thamSo = d.getInt("param");
                if (ma != 15 && ma != 16) {
                    Integer thamSoMau = this.layThamSoOptionMau(ma);
                    if (thamSoMau == null) {
                        continue;
                    }
                    // Chi so goc do template server quyet dinh. JSON instance
                    // khong duoc tu nang param de tao do hack.
                    thamSo = thamSoMau;
                }
                ChickenThuocTinhVatPham option =
                        new ChickenThuocTinhVatPham(ma, thamSo);
                if (option.optionTemplate == null || thamSo < 0) {
                    continue;
                }
                if (ma != 16 && !optionDaCo.add(ma)) {
                    continue;
                }
                if (ma == 15) {
                    this.isSocketing = true;
                    this.socketFinishTime = (long) thamSo * 1000L;
                } else if (ma == 16) {
                    if (this.nSocket >= SO_SOCKET_TOI_DA) {
                        continue;
                    }
                    this.nSocket = (byte)(this.nSocket + 1);
                    if (thamSo != 0) {
                        ChickenMauVatPham mauNgoc =
                                ChickenQuanLyMayChu.itemTemplates.get(thamSo);
                        if (mauNgoc == null || mauNgoc.loai != 12) {
                            // Giu lo da duc nhung bo vien ngoc khong hop le.
                            thamSo = 0;
                            option = new ChickenThuocTinhVatPham(ma, 0);
                        } else {
                            ++this.nGem;
                        }
                    }
                }
                this.itemOptions.add(option);
            }
        }
    }

    private Integer layThamSoOptionMau(int maOption) {
        if (this.mau == null || this.mau.thuocTinhs == null) {
            return null;
        }
        for (Object doiTuong : this.mau.thuocTinhs) {
            if (!(doiTuong instanceof ChickenThuocTinhVatPham)) {
                continue;
            }
            ChickenThuocTinhVatPham option =
                    (ChickenThuocTinhVatPham) doiTuong;
            if (option.optionTemplate != null
                    && option.optionTemplate.ma == maOption) {
                return Math.max(0, option.thamSo);
            }
        }
        return null;
    }

    public JSONObject toJSONObject() {
        JSONObject doiTuong = new JSONObject();
        doiTuong.put("id", this.ma);
        doiTuong.put("quantity", this.soLuong);
        this.HP = DO_BEN_TOI_DA;
        doiTuong.put("HP", DO_BEN_TOI_DA);
        doiTuong.put("index", this.chiSo);
        JSONArray thuocTinhs = new JSONArray();
        Set<Integer> optionDaGhi = new HashSet<Integer>();
        int soSocketDaGhi = 0;
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            ChickenThuocTinhVatPham op = (ChickenThuocTinhVatPham)this.itemOptions.get(i);
            if (op == null || op.optionTemplate == null) {
                continue;
            }
            int maOption = op.optionTemplate.ma;
            int thamSo = op.thamSo;
            if (maOption == 16) {
                if (soSocketDaGhi >= SO_SOCKET_TOI_DA) {
                    continue;
                }
                soSocketDaGhi++;
                if (thamSo != 0) {
                    ChickenMauVatPham mauNgoc =
                            ChickenQuanLyMayChu.itemTemplates.get(thamSo);
                    if (mauNgoc == null || mauNgoc.loai != 12) {
                        thamSo = 0;
                    }
                }
            } else {
                if (!optionDaGhi.add(maOption)) {
                    continue;
                }
                if (maOption != 15) {
                    Integer thamSoMau = this.layThamSoOptionMau(maOption);
                    if (thamSoMau == null) {
                        continue;
                    }
                    thamSo = thamSoMau;
                }
            }
            if (thamSo < 0) {
                continue;
            }
            JSONObject option = new JSONObject();
            option.put("id", maOption);
            option.put("param", thamSo);
            thuocTinhs.add((Object)option);
        }
        doiTuong.put("options", thuocTinhs);
        return doiTuong;
    }

    public int getParamById(int ma) {
        for (int i = 0; i < this.itemOptions.size(); ++i) {
            ChickenThuocTinhVatPham o = (ChickenThuocTinhVatPham)this.itemOptions.get(i);
            if (o == null || o.optionTemplate == null
                    || o.optionTemplate.ma != ma) continue;
            return o.thamSo;
        }
        return -1;
    }
}
