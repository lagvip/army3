package com.chicken.phong.danhsach;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mang.ChickenTinNhan;
import com.chicken.phong.ChickenChoDau;
import com.chicken.phong.ChickenPhong;
import com.chicken.phong.ChickenQuanLyPhong;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import com.chicken.phong.boss.sanhcho.DebugSanhBoss;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Tạo toàn bộ danh sách phòng native theo CMD -28.
 *
 * Thứ tự tiêu đề quyết định biểu tượng nhóm mà client tự vẽ. Không chứa xử lý
 * vào phòng, sẵn sàng hoặc bắt đầu trận.
 */
public final class DanhSachPhong {
    private static final NhomPhong[] NHOMS = new NhomPhong[]{
        new NhomPhong("PHÒNG SƠ CẤP", new byte[]{0}, false),
        new NhomPhong("PHÒNG TRUNG CẤP", new byte[]{1}, false),
        new NhomPhong("PHÒNG CAO CẤP", new byte[]{2, 3}, false),
        new NhomPhong("PHÒNG ĐẤU TRÙM", new byte[0], true),
        new NhomPhong("PHÒNG CHIẾN DỊCH", new byte[0], false)
    };

    private DanhSachPhong() {
    }

    public static void gui(ChickenNguoiChoi nguoiChoi) throws IOException {
        if (nguoiChoi == null || nguoiChoi.dichVu == null) {
            return;
        }

        ChickenTinNhan ms = new ChickenTinNhan(-28);
        DataOutputStream ds = ms.boGhi();
        ghiNoiDung(ds);
        ds.flush();
        nguoiChoi.dichVu.guiTin(ms);
        DebugSanhBoss.log("GUI_DANH_SACH_PHONG", nguoiChoi,
                "soPhongBoss=" + QuanLySanhChoBoss.SO_SANH
                + " danhSach=P4-0_den_P4-" + (QuanLySanhChoBoss.SO_SANH - 1)
                + " mapMacDinh=50");
    }


    /** Tách riêng phần ghi dữ liệu để có thể kiểm tra đúng byte client đọc. */
    public static void ghiNoiDung(DataOutputStream ds) throws IOException {
        ds.writeByte(0); // 0 = mở danh sách; 1 = client mở ô nhập mật khẩu.

        for (NhomPhong nhom : NHOMS) {
            MucPhong.ghiTieuDe(ds, nhom.getTieuDe());
            ghiPhongThuong(ds, nhom.getMaPhongThuong());
            if (nhom.coPhongBoss()) {
                ghiPhongBoss(ds);
            }
        }
    }

    private static void ghiPhongThuong(DataOutputStream ds, byte[] maPhongs)
            throws IOException {
        ChickenPhong[] phongs = ChickenQuanLyPhong.phongs;
        if (phongs == null) {
            return;
        }

        for (byte maPhong : maPhongs) {
            int index = maPhong & 0xFF;
            if (index >= phongs.length || phongs[index] == null) {
                continue;
            }
            ChickenPhong phong = phongs[index];
            for (ChickenChoDau banCho : phong.banChos) {
                if (banCho == null
                        || banCho.started
                        || banCho.laySoNguoiChoi() >= (banCho.maxPlayers & 0xFF)) {
                    continue;
                }
                MucPhong.ghiPhong(
                        ds,
                        phong.ma,
                        banCho.ma,
                        banCho.maBanDo,
                        banCho.laySoNguoiChoi(),
                        banCho.maxPlayers & 0xFF,
                        banCho.tien
                );
            }
        }
    }

    private static void ghiPhongBoss(DataOutputStream ds) throws IOException {
        for (SanhChoBoss sanh : QuanLySanhChoBoss.layDanhSach()) {
            if (sanh == null
                    || sanh.isDaBatDau()
                    || sanh.getSoNguoi() >= (sanh.getToiDa() & 0xFF)) {
                continue;
            }
            MucPhong.ghiPhong(
                    ds,
                    sanh.getMaPhong(),
                    sanh.getMaBan(),
                    sanh.getMaBanDo(),
                    sanh.getSoNguoi(),
                    sanh.getToiDa() & 0xFF,
                    sanh.getGiaHienThi()
            );
        }
    }
}
