package com.chicken.phong.boss.trandau;

import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.sanhcho.ThanhVienBoss;
import com.chicken.phong.boss.trandau.baovay.BossBaoVay;
import com.chicken.phong.boss.trandau.baovay.CauHinhBossBaoVay;
import com.chicken.phong.boss.trandau.haitoathap.BossHaiToaThap;
import com.chicken.phong.boss.trandau.haitoathap.CauHinhBossHaiToaThap;
import com.chicken.phong.boss.trandau.datbom.BossDatBom;
import com.chicken.phong.boss.trandau.datbom.CauHinhBossDatBom;
import com.chicken.phong.boss.trandau.khicau.BossKhiCau;
import com.chicken.phong.boss.trandau.khicau.CauHinhBossKhiCau;
import com.chicken.phong.boss.trandau.rua.BossRua;
import com.chicken.phong.boss.trandau.rua.CauHinhBossRua;
import com.chicken.phong.boss.trandau.rong.BossRong;
import com.chicken.phong.boss.trandau.rong.CauHinhBossRong;
import com.chicken.phong.boss.trandau.ruarong.BossRuaRong;
import com.chicken.phong.boss.trandau.ruarong.CauHinhBossRuaRong;
import java.io.IOException;

public final class VaoTranBoss {
    private VaoTranBoss() {
    }

    public static void xuLy(ChickenNguoiChoi nguoiChoi) throws IOException {
        SanhChoBoss sanh = QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
        if (sanh == null) {
            return;
        }

        ChickenQuanLyChien tranBoss;
        String tenTran;
        synchronized (sanh) {
            ThanhVienBoss thanhVien = sanh.timThanhVien(nguoiChoi);
            if (thanhVien == null || !thanhVien.isChuPhong()) {
                return;
            }
            if (!sanh.isDangCho()) {
                return;
            }
            if (sanh.getSoNguoi() < 1) {
                nguoiChoi.startOKDlg2("Chưa có người chơi trong phòng boss.");
                return;
            }
            if (!sanh.tatCaThanhVienDaSanSang()) {
                nguoiChoi.startOKDlg2("Vẫn còn thành viên chưa sẵn sàng.");
                return;
            }
            int mapId = sanh.getMaBanDo() & 0xFF;
            if (!CauHinhMapBoss.laMapBossHopLe(mapId)) {
                nguoiChoi.startOKDlg2("Map boss không hợp lệ.");
                return;
            }

            if (mapId == CauHinhBossBaoVay.MAP_ID) {
                tranBoss = BossBaoVay.tao(sanh);
                tenTran = "Boss Bao vây";
            } else if (mapId == CauHinhBossHaiToaThap.MAP_ID) {
                tranBoss = BossHaiToaThap.tao(sanh);
                tenTran = "Boss Hai tòa tháp";
            } else if (mapId == CauHinhBossKhiCau.MAP_ID) {
                tranBoss = BossKhiCau.tao(sanh);
                tenTran = "Boss Khí cầu";
            } else if (mapId == CauHinhBossDatBom.MAP_ID) {
                tranBoss = BossDatBom.tao(sanh);
                tenTran = "Boss Đặt bom";
            } else if (mapId == CauHinhBossRua.MAP_ID) {
                tranBoss = BossRua.tao(sanh);
                tenTran = "Boss Rùa";
            } else if (mapId == CauHinhBossRong.MAP_ID) {
                tranBoss = BossRong.tao(sanh);
                tenTran = "Boss Rồng";
            } else if (mapId == CauHinhBossRuaRong.MAP_ID) {
                tranBoss = BossRuaRong.tao(sanh);
                tenTran = "Boss Rùa x Boss Rồng";
            } else {
                nguoiChoi.startOKDlg2(
                        "Map boss này chưa được hoàn thiện."
                );
                return;
            }

            sanh.setTrangThai(SanhChoBoss.TrangThai.DANG_BAT_DAU);
            if (tranBoss == null) {
                sanh.setTrangThai(SanhChoBoss.TrangThai.DANG_CHO);
                nguoiChoi.startOKDlg2("Không thể tạo trận " + tenTran + ".");
                return;
            }
        }

        try {
            tranBoss.batDau();
            synchronized (sanh) {
                if (sanh.getTrangThai() == SanhChoBoss.TrangThai.DANG_BAT_DAU) {
                    sanh.setTrangThai(SanhChoBoss.TrangThai.DANG_CHIEN);
                }
            }
        } catch (IOException | RuntimeException ex) {
            tranBoss.dungBot();
            synchronized (sanh) {
                sanh.setTrangThai(SanhChoBoss.TrangThai.DANG_CHO);
            }
            throw ex;
        }
    }
}
