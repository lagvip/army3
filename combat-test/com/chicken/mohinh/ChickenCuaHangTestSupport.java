package com.chicken.mohinh;

import com.chicken.loi.ChickenQuanLyMayChu;
import com.chicken.mang.ChickenDichVuGame;
import com.chicken.mang.ChickenPhien;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.vatpham.ChickenMauVatPham;
import com.chicken.vatpham.ChickenVatPham;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Hoi quy protocol va tinh tien server-authoritative cua shop. */
public final class ChickenCuaHangTestSupport {
    private static final int MA_VAT_PHAM_TEST = 32_000;

    private ChickenCuaHangTestSupport() {
    }

    public static void tuKiemTra() throws Exception {
        ChickenMauVatPham cu =
                ChickenQuanLyMayChu.itemTemplates.get(MA_VAT_PHAM_TEST);
        ChickenMauVatPham mau = new ChickenMauVatPham(
                (short) MA_VAT_PHAM_TEST,
                (byte) 6,
                (byte) 0,
                "Vat pham shop test",
                "",
                (byte) 0,
                0,
                (short) 0,
                (short) 0,
                false);
        mau.buyGold = 100;
        ChickenQuanLyMayChu.itemTemplates.put(MA_VAT_PHAM_TEST, mau);

        try {
            DichVuBatPacket dichVu =
                    new DichVuBatPacket(new ChickenPhien(null, 97_072));
            ChickenNguoiChoi nguoiChoi =
                    new ChickenNguoiChoi(dichVu);
            nguoiChoi.vang = 1_000;
            dichVu.datNguoiChoi(nguoiChoi);

            nguoiChoi.yeuCauMuaVatPham(
                    tinMua((byte) 0, MA_VAT_PHAM_TEST, 3));
            dung(nguoiChoi.vang == 700,
                    "server khong tinh tong gia theo so luong");
            dung(nguoiChoi.itemBag[0] != null
                            && nguoiChoi.itemBag[0].soLuong == 3,
                    "server khong them dung so luong");
            dung(dichVu.coLenh(105)
                            && dichVu.coLenh(-35)
                            && dichVu.coLenh(45),
                    "mua thanh cong khong cap nhat UI");

            int vangTruoc = nguoiChoi.vang;
            int soLuongTruoc = nguoiChoi.itemBag[0].soLuong;
            dichVu.xoaPacket();
            nguoiChoi.yeuCauMuaVatPham(
                    new ChickenTinNhan(
                            (byte) 72,
                            new byte[]{0, 0, 1}));
            dung(nguoiChoi.vang == vangTruoc
                            && nguoiChoi.itemBag[0].soLuong
                                    == soLuongTruoc,
                    "packet sai do dai van sua kinh te");
            dung(dichVu.coLenh(10),
                    "packet sai khong dong InfoDlg dang xoay");

            nguoiChoi.vang = 20_000;
            vangTruoc = nguoiChoi.vang;
            nguoiChoi.yeuCauMuaVatPham(
                    tinMua((byte) 0, MA_VAT_PHAM_TEST, 97));
            dung(nguoiChoi.vang == vangTruoc,
                    "vuot gioi han chong vat pham van tru tien");
        } finally {
            if (cu == null) {
                ChickenQuanLyMayChu.itemTemplates.remove(MA_VAT_PHAM_TEST);
            } else {
                ChickenQuanLyMayChu.itemTemplates.put(
                        MA_VAT_PHAM_TEST, cu);
            }
        }
    }

    private static ChickenTinNhan tinMua(
            byte loai, int ma, int soLuong) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(bo);
        ds.writeByte(loai);
        ds.writeShort(ma);
        ds.writeByte(soLuong);
        ds.flush();
        return new ChickenTinNhan((byte) 72, bo.toByteArray());
    }

    private static void dung(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }

    private static final class DichVuBatPacket
            extends ChickenDichVuGame {
        private final List<Integer> cacLenh = new ArrayList<>();

        private DichVuBatPacket(ChickenPhien phien) {
            super(phien);
        }

        @Override
        public void guiTin(ChickenTinNhan tin) {
            this.cacLenh.add((int) tin.layLenh());
        }

        private boolean coLenh(int lenh) {
            return this.cacLenh.contains(lenh);
        }

        private void xoaPacket() {
            this.cacLenh.clear();
        }
    }
}
