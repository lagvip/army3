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

    public synchronized boolean vao(ChickenNguoiChoi nguoiChoi) {
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

    public synchronized boolean roi(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null || this.players_id.get(nguoiChoi.ma) == null) {
            return false;
        }

        /*
         * Không tin nguoiChoi.chiSo ở đây. Khi code phòng đấu cũ ghi đè chiSo
         * bằng số ghế, xóa theo giá trị đó có thể xóa nhầm một người khác khỏi
         * khu RPG và để lại bản ghi ma. Tìm lại slot thật từ dữ liệu của khu.
         */
        int chiSoTrongKhu = -1;
        for (int i = 0; i < this.maxPlayer; i++) {
            ChickenNguoiChoi trongKhu = this.players_index.get(i);
            if (trongKhu == nguoiChoi
                    || (trongKhu != null && trongKhu.ma == nguoiChoi.ma)) {
                chiSoTrongKhu = i;
                break;
            }
        }
        if (chiSoTrongKhu >= 0) {
            this.players_index.remove(chiSoTrongKhu);
        }
        if (this.players_id.remove(nguoiChoi.ma) != null) {
            this.numPlayer = Math.max(0, this.numPlayer - 1);
        }
        if (nguoiChoi.zone == this) {
            nguoiChoi.chiSo = -1;
            nguoiChoi.zoneId = (byte)-1;
            nguoiChoi.zone = null;
        }
        this.datDiem();
        if (chiSoTrongKhu >= 0) {
            this.guiNguoiChoiRoiKhu(chiSoTrongKhu);
        }
        return true;
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
