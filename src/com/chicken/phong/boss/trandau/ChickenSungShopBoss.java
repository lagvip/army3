package com.chicken.phong.boss.trandau;

import com.chicken.chien.ChickenQuanLyDanSung;
import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenThuocTinhVatPham;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Nguon du lieu server-authoritative cho sung shop ma AI boss duoc phep dung. */
public final class ChickenSungShopBoss {
    private static final int ID_AVG_DAU = 391;
    private static final int ID_AVG_CUOI = 398;
    private static final int[] SUNG_DU_PHONG = {110, 120, 160};

    private ChickenSungShopBoss() {
    }

    /** Random mot sung dang ban trong shop, co mapping dan va khong phai AVG. */
    public static ChickenQuanLyDanSung.DuLieuSung chonNgauNhienKhongAvg() {
        List<ChickenQuanLyDanSung.DuLieuSung> ungViens =
                layDanhSachKhongAvg(ChickenQuanLyMayChu.itemTemplates);
        if (ungViens.isEmpty()) {
            for (int idSung : SUNG_DU_PHONG) {
                ChickenQuanLyDanSung.DuLieuSung duLieu =
                        ChickenQuanLyDanSung.theoIdSung(idSung);
                if (duLieu != null) {
                    ungViens.add(duLieu);
                }
            }
        }
        if (ungViens.isEmpty()) {
            return null;
        }
        return ungViens.get(
                ThreadLocalRandom.current().nextInt(ungViens.size()));
    }

    public static List<ChickenQuanLyDanSung.DuLieuSung> layDanhSachKhongAvg(
            Map<Integer, ChickenMauVatPham> itemTemplates
    ) {
        if (itemTemplates == null || itemTemplates.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<ChickenQuanLyDanSung.DuLieuSung> ketQua =
                new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>(itemTemplates.keySet());
        Collections.sort(ids);
        for (int idSung : ids) {
            ChickenMauVatPham mau = itemTemplates.get(idSung);
            if (!laSungShopKhongAvg(mau)) {
                continue;
            }
            ChickenQuanLyDanSung.DuLieuSung duLieu =
                    ChickenQuanLyDanSung.theoIdSung(idSung);
            if (duLieu != null) {
                ketQua.add(duLieu);
            }
        }
        return ketQua;
    }

    public static boolean laSungShopKhongAvg(ChickenMauVatPham mau) {
        if (mau == null || mau.loai != 5
                || (mau.buyGold <= 0 && mau.buyGem <= 0)) {
            return false;
        }
        int idSung = mau.ma & 0xFFFF;
        return idSung < ID_AVG_DAU || idSung > ID_AVG_CUOI;
    }

    /** Option 1 cua dung ID sung da random; khong suy nguoc qua part sprite. */
    public static int layTanCongTheoId(int idSung, int giaTriDuPhong) {
        int tanCong = layOption(layMauSung(idSung), 1);
        return tanCong > 0 ? tanCong : giaTriDuPhong;
    }

    /** Option 14 cua dung ID sung da random, dung de xep thu tu luot. */
    public static int layNapDanTheoId(int idSung, int giaTriDuPhong) {
        int napDan = layOption(layMauSung(idSung), 14);
        return napDan > 0 ? napDan : giaTriDuPhong;
    }

    private static ChickenMauVatPham layMauSung(int idSung) {
        return ChickenQuanLyMayChu.itemTemplates == null
                ? null
                : ChickenQuanLyMayChu.itemTemplates.get(idSung);
    }

    private static int layOption(ChickenMauVatPham mau, int maOption) {
        if (mau == null || mau.thuocTinhs == null) {
            return 0;
        }
        int tong = 0;
        for (Object doiTuong : mau.thuocTinhs) {
            if (!(doiTuong instanceof ChickenThuocTinhVatPham)) {
                continue;
            }
            ChickenThuocTinhVatPham option =
                    (ChickenThuocTinhVatPham) doiTuong;
            if (option.optionTemplate != null
                    && option.optionTemplate.ma == maOption) {
                tong += Math.max(0, option.thamSo);
            }
        }
        return tong;
    }
}
