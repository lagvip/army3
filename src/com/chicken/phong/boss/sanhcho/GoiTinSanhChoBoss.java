package com.chicken.phong.boss.sanhcho;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.mang.ChickenTinNhan;
import java.io.DataOutputStream;
import java.io.IOException;

public final class GoiTinSanhChoBoss {
    /* Giao diện dùng đúng mã P4; chuỗi packet phải giống phòng PvP native. */
    private static final byte MA_PHONG_GIAO_DIEN = QuanLySanhChoBoss.MA_PHONG_BOSS;

    private GoiTinSanhChoBoss() {
    }

    public static void guiMoSanh(ChickenNguoiChoi nguoiNhan, SanhChoBoss sanh)
            throws IOException {
        if (!coTheGui(nguoiNhan) || sanh == null) {
            DebugSanhBoss.log("HUY_GUI_SANH", nguoiNhan, "lyDo=nguoiNhan_hoac_sanh_null");
            return;
        }

        byte coTrangThaiClient = (byte) (nguoiNhan.trainingSuccess == 1 ? 0 : 1);
        DebugSanhBoss.log("BAT_DAU_GUI_SANH", nguoiNhan,
                "P4-" + (sanh.getMaBan() & 0xFF)
                + " map=" + (sanh.getMaBanDo() & 0xFF)
                + " soNguoi=" + sanh.getSoNguoi());
        DebugSanhBoss.log("KIEM_TRA_CO_MO_PHONG_CHO", nguoiNhan,
                "trainingSuccessServer=" + nguoiNhan.trainingSuccess
                + " byteCMD3=" + coTrangThaiClient
                + " MyMidlet.aDuKien=" + (coTrangThaiClient == 0)
                + (coTrangThaiClient == 0
                        ? " ketQua=CHO_PHEP_CLIENT_MO_SANH"
                        : " ketQua=CLIENT_SE_CHAN_MO_SANH"));

        // Không gửi CMD -6 ở đây. Trong client, -6 là lệnh RANDOM MAP
        // và sẽ đưa người chơi vào màn hình loading chứ không mở phòng chờ.
        guiThongTinSanh(nguoiNhan, sanh);
        guiBanDoSanh(nguoiNhan, sanh);
        guiTatCaThanhVien(nguoiNhan, sanh);
        guiGiaPhong(nguoiNhan, sanh);
        guiTatCaTrangThaiSanSang(nguoiNhan, sanh);

        DebugSanhBoss.log("HOAN_TAT_GUI_SANH", nguoiNhan,
                "daGui=76,75,8,19 dangChoClientYeuCauCMD126=true");
    }

    public static void guiThongTinSanh(ChickenNguoiChoi nguoiNhan, SanhChoBoss sanh)
            throws IOException {
        if (!coTheGui(nguoiNhan) || sanh == null) {
            return;
        }
        String tenKhuVuc = "Khu vực " + ((sanh.getMaBan() & 0xFF) + 1);
        DebugSanhBoss.log("GUI_CMD_76", nguoiNhan,
                "roomUI=" + (MA_PHONG_GIAO_DIEN & 0xFF)
                + " board=" + (sanh.getMaBan() & 0xFF)
                + " roomLevel=4 danhSachMap=Boss50_55_58");
        nguoiNhan.dichVu.guiThongTinChoDau(
                MA_PHONG_GIAO_DIEN,
                sanh.getMaBan(),
                tenKhuVuc,
                (byte) 4
        );
    }

    public static void guiBanDoSanh(ChickenNguoiChoi nguoiNhan, SanhChoBoss sanh)
            throws IOException {
        if (!coTheGui(nguoiNhan) || sanh == null) {
            return;
        }
        DebugSanhBoss.log("GUI_CMD_75", nguoiNhan,
                "map=" + (sanh.getMaBanDo() & 0xFF));
        nguoiNhan.dichVu.guiChonBanDoDau(sanh.getMaBanDo());
    }

    /** Gửi map đang chọn cho toàn bộ thành viên trong sảnh boss. */
    public static void guiCapNhatBanDo(SanhChoBoss sanh) throws IOException {
        if (sanh == null) {
            return;
        }
        for (ThanhVienBoss thanhVien : sanh.chupThanhVien()) {
            if (thanhVien != null && thanhVien.getNguoiChoi() != null) {
                guiBanDoSanh(thanhVien.getNguoiChoi(), sanh);
            }
        }
    }

    public static void guiThanhVien(
            ChickenNguoiChoi nguoiNhan,
            ThanhVienBoss thanhVien,
            SanhChoBoss sanh
    ) throws IOException {
        if (!coTheGui(nguoiNhan) || thanhVien == null || sanh == null) {
            return;
        }
        ChickenNguoiChoi joined = thanhVien.getNguoiChoi();
        if (joined == null) {
            return;
        }
        ThanhVienBoss chuPhong = sanh.getChuPhong();
        ChickenNguoiChoi nguoiChuPhong = chuPhong != null
                ? chuPhong.getNguoiChoi()
                : joined;

        joined.chiSo = thanhVien.getGhe() & 0xFF;
        joined.pointSeat = thanhVien.getGhe();
        DebugSanhBoss.log("GUI_CMD_8_THANH_VIEN", nguoiNhan,
                "joined=" + joined.ten
                + " joinedId=" + joined.ma
                + " ghe=" + (thanhVien.getGhe() & 0xFF)
                + " chuId=" + nguoiChuPhong.ma);
        nguoiNhan.dichVu.guiNguoiChoiVaoDau(
                joined,
                nguoiChuPhong,
                MA_PHONG_GIAO_DIEN,
                sanh.getMaBan()
        );
    }

    public static void guiTatCaThanhVien(ChickenNguoiChoi nguoiNhan, SanhChoBoss sanh)
            throws IOException {
        if (sanh == null) {
            return;
        }
        for (ThanhVienBoss thanhVien : sanh.chupThanhVien()) {
            if (thanhVien != null) {
                guiThanhVien(nguoiNhan, thanhVien, sanh);
            }
        }
    }

    public static void guiThanhVienMoiChoNguoiConLai(
            SanhChoBoss sanh,
            ThanhVienBoss thanhVienMoi
    ) throws IOException {
        if (sanh == null || thanhVienMoi == null) {
            return;
        }
        ChickenNguoiChoi nguoiMoi = thanhVienMoi.getNguoiChoi();
        for (ThanhVienBoss thanhVien : sanh.chupThanhVien()) {
            if (thanhVien == null || thanhVien.getNguoiChoi() == nguoiMoi) {
                continue;
            }
            guiThanhVien(thanhVien.getNguoiChoi(), thanhVienMoi, sanh);
        }
    }

    public static void guiCapNhatChuPhong(SanhChoBoss sanh) throws IOException {
        if (sanh == null) {
            return;
        }
        for (ThanhVienBoss thanhVien : sanh.chupThanhVien()) {
            if (thanhVien == null) {
                continue;
            }
            ChickenNguoiChoi nguoiNhan = thanhVien.getNguoiChoi();
            guiThongTinSanh(nguoiNhan, sanh);
            guiBanDoSanh(nguoiNhan, sanh);
            guiTatCaThanhVien(nguoiNhan, sanh);
            guiTatCaTrangThaiSanSang(nguoiNhan, sanh);
            guiGiaPhong(nguoiNhan, sanh);
        }
    }

    public static void guiCapNhatSanSang(
            SanhChoBoss sanh,
            ThanhVienBoss thanhVien
    ) throws IOException {
        if (sanh == null || thanhVien == null || thanhVien.getNguoiChoi() == null) {
            return;
        }
        for (ThanhVienBoss nguoiNhan : sanh.chupThanhVien()) {
            if (nguoiNhan != null) {
                guiSanSang(
                        nguoiNhan.getNguoiChoi(),
                        thanhVien.getNguoiChoi().ma,
                        thanhVien.isSanSang()
                );
            }
        }
    }

    public static void guiTatCaTrangThaiSanSang(
            ChickenNguoiChoi nguoiNhan,
            SanhChoBoss sanh
    ) throws IOException {
        if (sanh == null) {
            return;
        }
        for (ThanhVienBoss thanhVien : sanh.chupThanhVien()) {
            if (thanhVien != null && !thanhVien.isChuPhong()) {
                guiSanSang(
                        nguoiNhan,
                        thanhVien.getNguoiChoi().ma,
                        thanhVien.isSanSang()
                );
            }
        }
    }

    private static void guiSanSang(
            ChickenNguoiChoi nguoiNhan,
            int maNguoiChoi,
            boolean sanSang
    ) throws IOException {
        if (!coTheGui(nguoiNhan)) {
            return;
        }
        nguoiNhan.dichVu.guiSanSangDau(maNguoiChoi, sanSang);
    }

    public static void guiNguoiChoiRoi(
            ChickenNguoiChoi nguoiNhan,
            int maNguoiRoi,
            int maChuPhongMoi
    ) throws IOException {
        if (!coTheGui(nguoiNhan)) {
            return;
        }
        nguoiNhan.dichVu.guiRoiDau(maNguoiRoi, maChuPhongMoi);
    }

    public static void guiGiaPhong(ChickenNguoiChoi nguoiNhan, SanhChoBoss sanh)
            throws IOException {
        if (!coTheGui(nguoiNhan) || sanh == null) {
            return;
        }
        /*
         * Client dùng byte cuối CMD 19 để gán lại dh.b (roomLevel).
         * Nếu gửi 0 ở đây thì giá trị 4 từ CMD 76 bị ghi đè, nên khi bấm
         * ảnh map client sẽ hiện danh sách map solo. Phòng boss bắt buộc
         * phải giữ roomLevel = 4 để client lọc các map có tên bắt đầu Boss.
         */
        DebugSanhBoss.log("GUI_CMD_19", nguoiNhan,
                "gia=" + sanh.getGiaHienThi()
                + " roomLevel=4 giuDanhSachMapBoss=true");
        nguoiNhan.dichVu.guiTienDau(sanh.getGiaHienThi(), (byte) 4);
    }


    public static void guiDoiPhe(
            ChickenNguoiChoi nguoiNhan,
            int maNguoiChoi,
            byte gheMoi
    ) throws IOException {
        if (!coTheGui(nguoiNhan)) {
            return;
        }
        ChickenTinNhan ms = new ChickenTinNhan(71);
        DataOutputStream ds = ms.boGhi();
        ds.writeInt(maNguoiChoi);
        ds.writeByte(gheMoi);
        ds.flush();
        nguoiNhan.dichVu.guiTin(ms);
    }

    public static void guiYeuCauNhapMatKhau(ChickenNguoiChoi nguoiNhan)
            throws IOException {
        if (!coTheGui(nguoiNhan)) {
            return;
        }
        ChickenTinNhan ms = new ChickenTinNhan(-28);
        DataOutputStream ds = ms.boGhi();
        ds.writeByte(1);
        ds.flush();
        nguoiNhan.dichVu.guiTin(ms);
    }

    private static boolean coTheGui(ChickenNguoiChoi nguoiChoi) {
        return nguoiChoi != null && nguoiChoi.dichVu != null;
    }
}
