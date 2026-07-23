package com.chicken.nhapvai;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mang.ChickenTinNhan;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChickenKhu {
    public byte zoneId;
    public int pts;
    public int numPlayer;
    public int maxPlayer;
    public HashMap<Integer, ChickenNguoiChoi> players_index = new HashMap();
    public HashMap<Integer, ChickenNguoiChoi> players_id = new HashMap();

    public ChickenKhu(int ma) {
        this.zoneId = (byte)ma;
        this.maxPlayer = 24;
    }

    public boolean vao(ChickenNguoiChoi nguoiChoi) {
        if (this.players_id.get(nguoiChoi.ma) == null) {
            for (int i = 0; i < this.maxPlayer; ++i) {
                if (this.players_index.get(i) != null) continue;
                this.players_index.put(i, nguoiChoi);
                this.players_id.put(nguoiChoi.ma, nguoiChoi);
                nguoiChoi.chiSo = i;
                nguoiChoi.zoneId = this.zoneId;
                nguoiChoi.zone = this;
                ++this.numPlayer;
                this.datDiem();
                this.guiNguoiChoiTrongKhu(nguoiChoi);
                nguoiChoi.dichVu.guiNhanVatPhu();
                return true;
            }
        }
        return false;
    }

    public boolean roi(ChickenNguoiChoi nguoiChoi) {
        if (this.players_id.get(nguoiChoi.ma) != null) {
            this.players_index.remove(nguoiChoi.chiSo);
            this.players_id.remove(nguoiChoi.ma);
            --this.numPlayer;
            int chiSo = nguoiChoi.chiSo;
            nguoiChoi.chiSo = -1;
            nguoiChoi.zoneId = (byte)-1;
            nguoiChoi.zone = null;
            this.datDiem();
            this.guiNguoiChoiRoiKhu(chiSo);
            return true;
        }
        return false;
    }

    public void datDiem() {
        this.pts = this.numPlayer > 20 ? 2 : (this.numPlayer > 15 ? 1 : 0);
    }

    public void guiTatCaNguoiChoi(ChickenTinNhan ms) {
        for (ChickenNguoiChoi nguoiChoi : this.players_id.values()) {
            nguoiChoi.dichVu.guiTin(ms);
        }
    }

    public void guiNguoiChoiTrongKhu(ChickenNguoiChoi nguoiChoi) {
        try {
            for (ChickenNguoiChoi pl : this.players_id.values()) {
                pl.dichVu.vaoCho(nguoiChoi);
            }
            for (ChickenNguoiChoi pl : this.players_id.values()) {
                if (pl == nguoiChoi) continue;
                nguoiChoi.dichVu.vaoCho(pl);
            }
        }
        catch (IOException ex) {
            Logger.getLogger(ChickenKhu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void guiNguoiChoiRoiKhu(int chiSo) {
        for (ChickenNguoiChoi pl : this.players_id.values()) {
            pl.dichVu.roi(chiSo);
        }
    }
}

