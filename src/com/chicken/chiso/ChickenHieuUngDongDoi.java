package com.chicken.chiso;

import com.chicken.chien.ChickenChienBinh;
import com.chicken.mohinh.ChickenNguoiChoi;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Hieu ung chi so Dong doi do server tinh khi tao tran.
 *
 * <p>Khi tran bat dau, moi phe chot muc diem Dong doi cao nhat cua mot
 * thanh vien va dung muc do cho ca phe. Moi diem tang 0,163% Mau,
 * Tan cong va Giap. Snapshot nay khong bi thu hoi khi nguoi co diem
 * cao nhat chet hoac roi tran.
 */
public final class ChickenHieuUngDongDoi {

    private static final long MAU_SO_MOI_DIEM = 163L;
    private static final long MAU_SO_CO_SO = 100_000L;

    private ChickenHieuUngDongDoi() {
    }

    /**
     * Ap dung dung mot lan cho mot nhom nguoi choi cung phe (phong boss).
     * Bot luyen tap va boss khong duoc tinh la dong doi.
     *
     * @return so chien binh da duoc chot trang thai hieu ung
     */
    public static int apDungChoNhomDongMinh(ChickenChienBinh[] chienBinhs) {
        return apDungChoPhe(chienBinhs, 0, false);
    }

    /**
     * PvP chi kich hoat khi co mot nguoi choi that khac cung phe.
     * Giao thuc phong hien tai xep phe theo bit thap cua chi so slot:
     * slot chan mot phe, slot le mot phe.
     */
    public static int apDungChoPvpTheoPhe(ChickenChienBinh[] chienBinhs) {
        if (chienBinhs == null) {
            return 0;
        }
        return apDungChoPhe(chienBinhs, 0, true)
                + apDungChoPhe(chienBinhs, 1, true);
    }

    /**
     * Nhan mot chi so goc voi (1 + diem * 0,00163), dung so nguyen va long
     * de ket qua xac dinh, khong tran so khi du lieu lon.
     */
    public static int tinhChiSoSauThuong(int chiSoGoc, int diemDongDoi) {
        long goc = Math.max(0L, (long) chiSoGoc);
        long diem = Math.max(0L, (long) diemDongDoi);
        long heSo = MAU_SO_CO_SO + diem * MAU_SO_MOI_DIEM;
        long ketQua;
        if (goc == 0L || heSo <= 0L) {
            ketQua = 0L;
        } else if (goc > (long) Integer.MAX_VALUE * MAU_SO_CO_SO / heSo) {
            ketQua = Integer.MAX_VALUE;
        } else {
            ketQua = goc * heSo / MAU_SO_CO_SO;
        }
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, ketQua));
    }

    private static int apDungChoPhe(
            ChickenChienBinh[] chienBinhs,
            int phe,
            boolean locTheoPhe
    ) {
        if (chienBinhs == null) {
            return 0;
        }

        Set<ChickenNguoiChoi> nguoiChois = Collections.newSetFromMap(
                new IdentityHashMap<ChickenNguoiChoi, Boolean>());
        int diemCaoNhat = 0;
        for (ChickenChienBinh chienBinh : chienBinhs) {
            if (!thuocPhe(chienBinh, phe, locTheoPhe)) {
                continue;
            }
            nguoiChois.add(chienBinh.nguoiChoi);
            diemCaoNhat = Math.max(
                    diemCaoNhat,
                    ChickenChiSoNguoiChoi.tinhDongDoi(
                            chienBinh.nguoiChoi));
        }
        if (nguoiChois.size() < 2) {
            return 0;
        }

        int daApDung = 0;
        for (ChickenChienBinh chienBinh : chienBinhs) {
            if (!thuocPhe(chienBinh, phe, locTheoPhe)) {
                continue;
            }
            if (chienBinh.apDungThuongDongDoi(diemCaoNhat)) {
                daApDung++;
            }
        }
        return daApDung;
    }

    private static boolean thuocPhe(
            ChickenChienBinh chienBinh,
            int phe,
            boolean locTheoPhe
    ) {
        return chienBinh != null
                && chienBinh.laNguoiChoiThat()
                && (!locTheoPhe || (chienBinh.chiSo & 1) == phe);
    }
}
