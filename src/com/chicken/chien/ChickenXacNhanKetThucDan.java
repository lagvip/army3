package com.chicken.chien;

import com.chicken.mang.ChickenTinNhan;
import java.io.IOException;

/**
 * Kiem tra khuon CMD 79 cua client.
 *
 * Cac toa do trong packet chi la du lieu animation khong tin cay va bi bo qua.
 * Server chi nhan tin hieu ket thuc khi packet co dung do dai/cau truc.
 */
public final class ChickenXacNhanKetThucDan {

    private static final int TOI_DA_DIEM = 64;
    private static final int TOI_DA_BYTE = 1 + TOI_DA_DIEM * 8;

    private ChickenXacNhanKetThucDan() {
    }

    public static boolean docVaBoQua(ChickenTinNhan ms) {
        if (ms == null || ms.layLenh() != 79) {
            return false;
        }
        try {
            int soByte = ms.boDoc().available();
            if (soByte < 1 || soByte > TOI_DA_BYTE
                    || (soByte - 1) % 8 != 0) {
                boPhanConLai(ms);
                return false;
            }
            int soDiem = ms.boDoc().readUnsignedByte();
            if (soDiem != (soByte - 1) / 8 || soDiem > TOI_DA_DIEM) {
                boPhanConLai(ms);
                return false;
            }
            for (int i = 0; i < soDiem; i++) {
                ms.boDoc().readInt();
                ms.boDoc().readInt();
            }
            return ms.boDoc().available() == 0;
        } catch (IOException loi) {
            try {
                boPhanConLai(ms);
            } catch (IOException ignored) {
            }
            return false;
        }
    }

    private static void boPhanConLai(ChickenTinNhan ms) throws IOException {
        while (ms.boDoc().available() > 0) {
            ms.boDoc().readByte();
        }
    }
}
