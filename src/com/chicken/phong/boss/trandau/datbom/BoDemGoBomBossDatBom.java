package com.chicken.phong.boss.trandau.datbom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks four consecutive completed turns spent inside a timed bomb's
 * interaction radius.
 * Player positions are supplied by authoritative server state.
 */
public final class BoDemGoBomBossDatBom {
    public static final int SO_LUOT_CAN_GO = 4;
    public static final int PHAN_TRAM_MOI_LUOT = 100 / SO_LUOT_CAN_GO;
    /** Ban kinh tuong tac tron; ap dung nhu nhau cho nguoi di bo va AVG bay. */
    public static final int BAN_KINH_GO_BOM = 48;
    private static final long BINH_PHUONG_BAN_KINH_GO_BOM =
            (long) BAN_KINH_GO_BOM * BAN_KINH_GO_BOM;

    private final Map<Byte, TrangThai> trangThaiTheoBom =
            new LinkedHashMap<>();

    public synchronized void dangKyBom(byte id, int x, int y) {
        this.trangThaiTheoBom.put(id, new TrangThai(id, x, y));
    }

    public synchronized void capNhatViTriBom(byte id, int x, int y) {
        TrangThai trangThai = this.trangThaiTheoBom.get(id);
        if (trangThai != null) {
            trangThai.x = x;
            trangThai.y = y;
        }
    }

    public synchronized List<ViTriBom> chupViTriBom() {
        ArrayList<ViTriBom> ketQua =
                new ArrayList<>(this.trangThaiTheoBom.size());
        for (TrangThai trangThai : this.trangThaiTheoBom.values()) {
            ketQua.add(new ViTriBom(
                    trangThai.id, trangThai.x, trangThai.y));
        }
        return Collections.unmodifiableList(ketQua);
    }

    public synchronized KetQuaKetThucLuot ketThucLuot(
            List<ViTriNguoiChoi> nguoiChoiSong
    ) {
        List<ViTriNguoiChoi> viTris = nguoiChoiSong == null
                ? Collections.emptyList()
                : nguoiChoiSong;
        ArrayList<TienDo> thayDoi = new ArrayList<>();
        ArrayList<Byte> daGo = new ArrayList<>();

        for (TrangThai trangThai
                : new ArrayList<>(this.trangThaiTheoBom.values())) {
            boolean coNguoiDungTrenBom = false;
            for (ViTriNguoiChoi viTri : viTris) {
                if (viTri == null) {
                    continue;
                }
                long dx = (long) viTri.x - trangThai.x;
                long dy = (long) viTri.y - trangThai.y;
                if (dx * dx + dy * dy <= BINH_PHUONG_BAN_KINH_GO_BOM) {
                    coNguoiDungTrenBom = true;
                    break;
                }
            }

            int cu = trangThai.soLuotLienTiep;
            trangThai.soLuotLienTiep = coNguoiDungTrenBom
                    ? Math.min(SO_LUOT_CAN_GO, cu + 1)
                    : 0;
            if (trangThai.soLuotLienTiep != cu) {
                int phanTram = Math.min(
                        100,
                        trangThai.soLuotLienTiep * PHAN_TRAM_MOI_LUOT);
                thayDoi.add(new TienDo(trangThai.id, phanTram));
            }
            if (trangThai.soLuotLienTiep >= SO_LUOT_CAN_GO) {
                this.trangThaiTheoBom.remove(trangThai.id);
                daGo.add(trangThai.id);
            }
        }
        return new KetQuaKetThucLuot(thayDoi, daGo);
    }

    public synchronized void xoaBom(byte id) {
        this.trangThaiTheoBom.remove(id);
    }

    public synchronized int demBomDangTheoDoi() {
        return this.trangThaiTheoBom.size();
    }

    public synchronized void xoaTatCa() {
        this.trangThaiTheoBom.clear();
    }

    public static final class ViTriNguoiChoi {
        private final int x;
        private final int y;

        public ViTriNguoiChoi(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static final class ViTriBom {
        private final byte id;
        private final int x;
        private final int y;

        private ViTriBom(byte id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public byte getId() {
            return this.id;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }
    }

    public static final class TienDo {
        private final byte id;
        private final int phanTram;

        private TienDo(byte id, int phanTram) {
            this.id = id;
            this.phanTram = phanTram;
        }

        public byte getId() {
            return this.id;
        }

        public int getPhanTram() {
            return this.phanTram;
        }
    }

    public static final class KetQuaKetThucLuot {
        private final List<TienDo> tienDoThayDoi;
        private final List<Byte> bomDaGo;

        private KetQuaKetThucLuot(
                List<TienDo> tienDoThayDoi,
                List<Byte> bomDaGo
        ) {
            this.tienDoThayDoi =
                    Collections.unmodifiableList(tienDoThayDoi);
            this.bomDaGo = Collections.unmodifiableList(bomDaGo);
        }

        public List<TienDo> getTienDoThayDoi() {
            return this.tienDoThayDoi;
        }

        public List<Byte> getBomDaGo() {
            return this.bomDaGo;
        }
    }

    private static final class TrangThai {
        private final byte id;
        private int x;
        private int y;
        private int soLuotLienTiep;

        private TrangThai(byte id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }
}
