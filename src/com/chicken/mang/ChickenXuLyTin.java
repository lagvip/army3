package com.chicken.mang;

import com.chicken.cuahang.ChickenCuaHang;
import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.dichvu.ChickenQuanLyXepHang;
import com.chicken.dichvu.ChickenQuanLyThanhTich;
import com.chicken.dichvu.ChickenQuanLyBietDoi;
import com.chicken.dichvu.ChickenQuanLyBanBe;
import com.chicken.phong.ChickenQuanLyPhong;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.npc.chihuy.XuLyMenuChiHuy;
import com.chicken.thoat.ChickenThoatTran;
import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.loi.ChickenQuanLyMayChu;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChickenXuLyTin
implements IChickenXuLyTin {
    private static final ScheduledExecutorService BO_GUI_NGUYEN_LIEU_BOSS =
            Executors.newScheduledThreadPool(2, r -> {
                Thread thread = new Thread(r, "boss-resource");
                thread.setDaemon(true);
                return thread;
            });
    private final ChickenPhien khach;

    public ChickenXuLyTin(ChickenPhien khach) {
        this.khach = khach;
    }

    @Override
    public void khiCoTin(ChickenTinNhan mss) {
        if (mss != null) {
            int cmd = mss.layLenh();
            try {
                if (!this.khach.choPhepXuLyLenh(
                        cmd, System.currentTimeMillis())) {
                    return;
                }
                if (!this.khach.coNguoiChoiDaDangNhap()
                        && canNguoiChoiDaDangNhap(cmd)) {
                    this.khach.ghiNhanPacketLoi(
                            cmd, new IllegalStateException("Chua dang nhap"));
                    return;
                }
                if (this.chanLenhChuyenSceneTreTrongTranBoss(cmd)) {
                    return;
                }
                switch (cmd) {
                    case 1:
                        this.khach.dangNhap(mss);
                        break;
                    case 2:
                    case -4:
                        this.khach.dangXuat();
                        break;
                    case -58:
                        this.khach.dangNhap2(mss);
                        break;
                    case -98:
                        this.khach.user.nguoiChoi.banDoRPG(mss);
                        break;
                    case 29:
                        ChickenQuanLyBanBe.xuLy(this.khach.user.nguoiChoi, mss);
                        break;
                    case 32:
                        ChickenQuanLyBanBe.themBan(this.khach.user.nguoiChoi, mss);
                        break;
                    case 33:
                        ChickenQuanLyBanBe.xoaBanReq(this.khach.user.nguoiChoi, mss);
                        break;
                    case 36:
                        ChickenQuanLyBanBe.timKiem(this.khach.user.nguoiChoi, mss);
                        break;
                    case -57:
                        ChickenQuanLyXepHang.guiMenuTop(this.khach.user.nguoiChoi);
                        break;
                    case -14:
                        ChickenQuanLyXepHang.bangXepHang(this.khach.user.nguoiChoi, mss);
                        break;
                    case -108:
                        ChickenQuanLyBietDoi.yeuCauThongTin(this.khach.user.nguoiChoi, mss);
                        break;
                    case -105:
                        ChickenQuanLyBietDoi.yeuCauThanhVien(this.khach.user.nguoiChoi, mss);
                        break;
                    case -103:
                        ChickenQuanLyBietDoi.quanLyClan(this.khach.user.nguoiChoi, mss);
                        break;
                    case -113:
                        ChickenQuanLyBietDoi.timKiem(this.khach.user.nguoiChoi, mss);
                        break;
                    case -104:
                        ChickenQuanLyBietDoi.thamGia(this.khach.user.nguoiChoi, mss);
                        break;
                    case -106:
                        ChickenQuanLyBietDoi.tinNhanClan(this.khach.user.nguoiChoi, mss);
                        break;
                    case -110:
                        ChickenQuanLyBietDoi.quanLyThanhVien(this.khach.user.nguoiChoi, mss);
                        break;
                    case -111:
                        ChickenQuanLyBietDoi.moiClan(this.khach.user.nguoiChoi, mss);
                        break;
                    case -109:
                        ChickenQuanLyBietDoi.roiClan(this.khach.user.nguoiChoi);
                        break;
                    case -117:
                        ChickenQuanLyBietDoi.topClan(this.khach.user.nguoiChoi);
                        break;
                    case -12:
                        ChickenQuanLyBietDoi.shopBietDoi(this.khach.user.nguoiChoi, mss);
                        break;
                    case -118:
                        ChickenQuanLyBietDoi.xuLyShopClan(this.khach.user.nguoiChoi, mss);
                        break;
                    case -119:
                        ChickenQuanLyBietDoi.trangThaiClan(this.khach.user.nguoiChoi);
                        break;
                    case -28:
                    case 6:
                        ChickenQuanLyPhong.yeuCauDanhSachPhong(this.khach.user.nguoiChoi);
                        break;
                    case 7:
                        ChickenQuanLyPhong.yeuCauDanhSachBan(this.khach.user.nguoiChoi, mss);
                        break;
                    case 8:
                        ChickenQuanLyPhong.vaoBan(this.khach.user.nguoiChoi, mss);
                        break;
                    case 15:
                        ChickenThoatTran.xuLy(this.khach.user.nguoiChoi);
                        break;
                    case 18:
                        ChickenQuanLyPhong.datMatKhau(this.khach.user.nguoiChoi, mss);
                        break;
                    case 71:
                        ChickenQuanLyPhong.doiPhe(this.khach.user.nguoiChoi);
                        break;
                    case 16:
                        ChickenQuanLyPhong.sanSang(this.khach.user.nguoiChoi, mss);
                        break;
                    case 20:
                        if (mss.layDuLieu().length != 0) {
                            this.khach.ghiNhanPacketLoi(
                                    cmd, new IllegalArgumentException(
                                            "CMD 20 khong co payload"));
                            break;
                        }
                        ChickenQuanLyPhong.batDau(this.khach.user.nguoiChoi);
                        break;
                    case 23:
                        ChickenQuanLyPhong.dauKiemTraVaCham(this.khach.user.nguoiChoi, mss);
                        break;
                    case -47:
                    case 49: {
                        int soByteLuaChon = mss.boDoc().available();
                        /*
                         * Nút » native gọi GameService.skipTurn() và gửi CMD 49
                         * không payload. CMD 49 có payload vẫn được giữ cho các
                         * client cũ từng dùng nó làm lựa chọn menu generic.
                         */
                        if (laLenhBoLuot(cmd, soByteLuaChon)) {
                            ChickenNguoiChoi nguoiChoi =
                                    this.khach.user.nguoiChoi;
                            if (nguoiChoi != null && nguoiChoi.inTraining) {
                                nguoiChoi.boLuotLuyenTap();
                            } else {
                                ChickenQuanLyPhong.boLuot(nguoiChoi);
                            }
                            break;
                        }
                        /*
                         * Menu generic CMD -47 của client gốc trả lại chính CMD -47
                         * kèm một byte chỉ số mục đã chọn. Plugin PC dùng hàm
                         * getItem(type, id), vì vậy gửi đúng hai byte 0,0.
                         * Bản cũ bắt nhầm CMD 49 nên bấm "Bắn x3" không bao giờ
                         * vào logic Ultron.
                         *
                         * Giữ case 49 để tương thích với client khác, nhưng client
                         * hiện tại dùng -47.
                         */
                        if (soByteLuaChon != 1 && soByteLuaChon != 2) {
                            break;
                        }
                        int luaChon;
                        if (soByteLuaChon == 2) {
                            // Client PC dung GameService.getItem(type, id) de gui lai
                            // menu generic. Chi chap nhan dung cap 0,0 cua nut skill;
                            // moi ket qua van duoc skill server kiem tra lai ben duoi.
                            int loaiMenuPc = mss.boDoc().readUnsignedByte();
                            luaChon = mss.boDoc().readUnsignedByte();
                            if (loaiMenuPc != 0 || luaChon != 0) {
                                break;
                            }
                        } else {
                            luaChon = mss.boDoc().readUnsignedByte();
                        }
                        ChickenNguoiChoi nguoiChoi = this.khach.user.nguoiChoi;
                        if (XuLyMenuChiHuy.xuLyLuaChon(nguoiChoi, luaChon)) {
                            break;
                        }
                        if (luaChon != 0) {
                            break;
                        }

                        if (nguoiChoi != null
                                && nguoiChoi.inTraining
                                ) {
                            if (nguoiChoi.kichHoatKyNangIronManLuyenTap()
                                    || nguoiChoi.kichHoatKyNangUltronLuyenTap()) {
                                break;
                            }
                        }

                        ChickenQuanLyChien tranDau =
                                ChickenQuanLyChien.timTranDauCuaNguoiChoi(nguoiChoi);
                        if (tranDau != null) {
                            if (!tranDau.kichHoatKyNangIronMan(nguoiChoi)) {
                                tranDau.kichHoatKyNangUltron(nguoiChoi);
                            }
                        }
                        break;
                    }
                    case 53:
                        ChickenQuanLyPhong.dauCapNhatXY(this.khach.user.nguoiChoi, mss);
                        break;
                    case 75:
                        ChickenQuanLyPhong.chonBanDo(this.khach.user.nguoiChoi, mss);
                        break;
                    case 5:
                        this.khach.user.nguoiChoi.chatTo(mss);
                        break;
                    case 58:
                        this.khach.datNhaCungCap(mss);
                        break;
                    case 114:
                        this.khach.datLoaiKhach(mss);
                        break;
                    case -102:
                        this.khach.guiTin(new ChickenTinNhan(-102));
                        break;
                    case -60:
                        this.khach.taiXuong();
                        break;
                    case -41:
                        this.khach.user.dichVu.yeuCauIcon(mss);
                        break;
                    case -38:
                        this.khach.user.dichVu.guiBanDo();
                        break;
                    case -37:
                        this.khach.taiDuLieuXong();
                        break;
                    case -32:
                        this.khach.user.dichVu.guiVatPham();
                        break;
                    case -71:
                        this.khach.dangKy(mss);
                        break;
                    case -99:
                        this.khach.user.taoNhanVat(mss);
                        break;
                    case -31:
                        this.khach.user.dichVu.guiDuLieu();
                        break;
                    case 103:
                        if (mss.boDoc().available() != 0) {
                            this.khach.ghiNhanPacketLoi(
                                    cmd, new IllegalArgumentException(
                                            "CMD 103 khong co payload"));
                            break;
                        }
                        this.khach.user.nguoiChoi.xemCuaHang(ChickenCuaHang.SHOP_EQUIP);
                        break;
                    case -43:
                        this.khach.user.nguoiChoi.requestTab(mss);
                        break;
                    case 72:
                        this.khach.user.nguoiChoi.yeuCauMuaVatPham(mss);
                        break;
                    case 26:
                        int kichThuocCmd26 = mss.boDoc().available();
                        if (kichThuocCmd26 == 1) {
                            int giaTriCmd26 = mss.boDoc().readUnsignedByte();
                            if (giaTriCmd26 == 100) {
                                this.khach.user.nguoiChoi
                                        .kichHoatPowTrongTran();
                            } else {
                                this.khach.user.nguoiChoi.doiSungTrongTran(
                                        giaTriCmd26);
                            }
                            break;
                        }
                        if (kichThuocCmd26 != 2) {
                            this.khach.ghiNhanPacketLoi(
                                    cmd, new IllegalArgumentException(
                                            "Sai kich thuoc CMD 26"));
                            break;
                        }
                        int loaiDungVatPham = mss.layDuLieu()[1] & 0xFF;
                        if (loaiDungVatPham != 0 && loaiDungVatPham != 1) {
                            this.khach.ghiNhanPacketLoi(
                                    cmd, new IllegalArgumentException(
                                            "Loai CMD 26 khong hop le"));
                            break;
                        }
                        this.khach.user.nguoiChoi.dungVatPham(mss);
                        break;
                    case -44:
                        if (mss.boDoc().available() != 2) {
                            this.khach.ghiNhanPacketLoi(
                                    cmd, new IllegalArgumentException(
                                            "Sai kich thuoc CMD -44"));
                            break;
                        }
                        int loaiChuyenVatPham = mss.layDuLieu()[0] & 0xFF;
                        if (loaiChuyenVatPham != 0
                                && loaiChuyenVatPham != 1
                                && loaiChuyenVatPham != 4
                                && loaiChuyenVatPham != 5
                                && loaiChuyenVatPham != 6
                                && loaiChuyenVatPham != 7) {
                            this.khach.ghiNhanPacketLoi(
                                    cmd, new IllegalArgumentException(
                                            "Loai CMD -44 khong hop le"));
                            break;
                        }
                        this.khach.user.nguoiChoi.chuyenVatPham(mss);
                        break;
                    case -25:
                        this.khach.user.nguoiChoi.thucHien(mss);
                        break;
                    case -48:
                        this.khach.user.nguoiChoi.yeuCauBanVatPham(mss);
                        break;
                    case -33:
                        if (mss.boDoc().available() != 0) {
                            this.khach.ghiNhanPacketLoi(
                                    cmd, new IllegalArgumentException(
                                            "CMD -33 khong co payload"));
                            break;
                        }
                        this.khach.user.nguoiChoi.xemCuaHang(ChickenCuaHang.SHOP_ITEM);
                        break;
                    case -46:
                        this.khach.user.nguoiChoi.nangCapNhanVat(mss);
                        break;
                    case 88:
                        ChickenQuanLyThanhTich.xuLy(this.khach.user, mss);
                        break;
                    case -126:
                        this.khach.user.nguoiChoi.viewPlayerInfo(mss);
                        break;
                    case 126: {
                        ChickenNguoiChoi nguoiChoi = this.khach.user.nguoiChoi;
                        if (mss.boDoc().available() != 2) {
                            this.khach.ghiNhanPacketLoi(
                                    cmd, new IllegalArgumentException(
                                            "Sai kich thuoc CMD 126"));
                            break;
                        }
                        short materialId = mss.boDoc().readShort();
                        SanhChoBoss sanhBoss =
                                QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);

                        if (sanhBoss != null) {
                            /*
                             * Client gửi CMD 126 ngay trong lúc còn đang phân tích
                             * CMD 75. Nếu server local trả ảnh tức thì, client có thể
                             * nhận ảnh trước khi nó gán xong tổng số ảnh cần chờ, làm
                             * màn hình phòng chờ không bao giờ được kích hoạt. Chỉ
                             * trì hoãn phản hồi ảnh terrain cho sảnh boss để tránh
                             * race này; PvP và các chế độ cũ không bị thay đổi.
                             */
                            long treMs = this.khach.datLichGuiNguyenLieuBoss(
                                    System.currentTimeMillis());
                            if (treMs < 0L) {
                                this.khach.ghiNhanPacketLoi(
                                        cmd, new IllegalStateException(
                                                "Hang doi resource boss day"));
                                break;
                            }
                            BO_GUI_NGUYEN_LIEU_BOSS.schedule(() -> {
                                try {
                                    if (this.khach.conKichHoat()
                                            && this.khach.coNguoiChoiDaDangNhap()
                                            && QuanLySanhChoBoss
                                                .timSanhCuaNguoiChoi(
                                                    this.khach.user.nguoiChoi)
                                                != null) {
                                        this.khach.user.dichVu
                                                .yeuCauNguyenLieu(materialId);
                                    }
                                } catch (Exception loi) {
                                    ChickenQuanLyMayChu.log(
                                            "Loi gui resource boss "
                                            + this.khach.moTa()
                                            + " materialId="
                                            + (materialId & 0xFFFF)
                                            + " loi="
                                            + loi.getClass().getSimpleName());
                                } finally {
                                    if (this.khach
                                            .hoanTatGuiNguyenLieuBoss()) {
                                        this.moManHinhBossSauKhiTaiXong();
                                    }
                                }
                            }, treMs, TimeUnit.MILLISECONDS);
                            break;
                        }

                        this.khach.user.dichVu.yeuCauNguyenLieu(materialId);
                        break;
                    }
                    case -40:
                        this.khach.user.dichVu.yeuCauDanLuyenTap(mss);
                        break;
                    case 83:
                        if (mss.layDuLieu().length != 0) {
                            this.khach.ghiNhanPacketLoi(
                                    cmd, new IllegalArgumentException(
                                            "CMD 83 khong co payload"));
                            break;
                        }
                        this.khach.user.nguoiChoi.vaoLuyenTap();
                        break;
                    case 21:
                        if (this.khach.user.nguoiChoi.inTraining) {
                            this.khach.user.nguoiChoi.handleTrainingMove(mss);
                        } else {
                            ChickenQuanLyPhong.dauDiChuyen(this.khach.user.nguoiChoi, mss);
                        }
                        break;
                    case 84:
                        this.khach.user.nguoiChoi.xuLyBanLuyenTap(mss);
                        break;
                    case 22:
                        if (this.khach.user.nguoiChoi.inTraining) {
                            this.khach.user.nguoiChoi.xuLyBanLuyenTap(mss);
                        } else {
                            ChickenQuanLyPhong.dauBan(this.khach.user.nguoiChoi, mss);
                        }
                        break;
                    case 79:
                        if (this.khach.user.nguoiChoi.inTraining) {
                            this.khach.user.nguoiChoi.xuLyVaChamLuyenTap(mss);
                        } else {
                            ChickenQuanLyPhong.dauKiemTraVaCham(this.khach.user.nguoiChoi, mss);
                        }
                        break;
                    case 91:
                    case -91: {
                        ChickenNguoiChoi nguoiChoi = this.khach.user.nguoiChoi;
                        // Luyện tập không tạo ChickenQuanLyChien. CMD -91 phải đi
                        // thẳng vào phiên luyện tập, nếu không luôn báo không tìm thấy trận.
                        if (nguoiChoi != null && nguoiChoi.inTraining) {
                            nguoiChoi.xuLyCmd91KyNangDacBietLuyenTap(mss);
                            break;
                        }

                        ChickenQuanLyChien tranDau =
                                ChickenQuanLyChien.timTranDauCuaNguoiChoi(nguoiChoi);
                        if (tranDau == null) {
                            break;
                        }

                        tranDau.nhanLenhKyNangDacBiet(nguoiChoi, mss);
                        break;
                    }
                    case -92:
                        this.khach.user.nguoiChoi.handleTrainingHoleRequest(mss);
                        break;
                    case -67: {
                        ChickenNguoiChoi nguoiChoi =
                                this.khach.user.nguoiChoi;
                        SanhChoBoss sanhBoss =
                                QuanLySanhChoBoss.timSanhCuaNguoiChoi(
                                        nguoiChoi);
                        if (dangTrongTranBoss(sanhBoss)) {
                            /*
                             * Client gui -67 khi anh dan cua CMD 20 da xong.
                             * Anh terrain CMD 126 co hang doi rieng, nen chi
                             * mo GameScr khi ca ACK nay va hang doi deu xong.
                             */
                            ChickenQuanLyMayChu.log(
                                    "[BOSS ROOM][CLIENT_SAN_SANG_VAO_TRAN]"
                                    + " playerId=" + nguoiChoi.ma
                                    + " room="
                                    + (sanhBoss.getMaBan() & 0xFF)
                                    + " state="
                                    + sanhBoss.getTrangThai());
                            if (this.khach
                                    .xacNhanSanSangMoManHinhBoss()) {
                                nguoiChoi.dichVu
                                        .guiHienManHinhGameLuyenTap();
                            }
                            break;
                        }
                        nguoiChoi.handleTrainingClientReady();
                        break;
                    }
                    default:
                        this.khach.ghiNhanPacketLoi(
                                cmd, new IllegalArgumentException(
                                        "Lenh khong duoc ho tro"));
                        break;
                }
            }
            catch (Exception ex) {
                this.khach.ghiNhanPacketLoi(cmd, ex);
            }
        }
    }

    private void moManHinhBossSauKhiTaiXong() {
        try {
            if (!this.khach.conKichHoat()
                    || !this.khach.coNguoiChoiDaDangNhap()) {
                return;
            }
            ChickenNguoiChoi nguoiChoi =
                    this.khach.user.nguoiChoi;
            SanhChoBoss sanh =
                    QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
            if (!dangTrongTranBoss(sanh)) {
                return;
            }
            ChickenQuanLyMayChu.log(
                    "[BOSS ROOM][TAI_XONG_MO_GAME]"
                    + " playerId=" + nguoiChoi.ma
                    + " room=" + (sanh.getMaBan() & 0xFF)
                    + " state=" + sanh.getTrangThai());
            nguoiChoi.dichVu.guiHienManHinhGameLuyenTap();
        } catch (Exception loi) {
            ChickenQuanLyMayChu.log(
                    "Loi mo GameScr boss sau khi tai resource "
                    + this.khach.moTa()
                    + " loi=" + loi.getClass().getSimpleName());
        }
    }

    /**
     * Client goc co the gui tre lenh mo danh sach/phong trong luc scene boss
     * dang tai. Khong duoc chay handler vi packet phan hoi se day client ve
     * sanh 8 nguoi trong khi tran server van song. Day chi la bo qua y dinh
     * sai scene; khong tinh packet loi va khong ngat ket noi.
     */
    private boolean chanLenhChuyenSceneTreTrongTranBoss(int cmd) {
        ChickenNguoiChoi nguoiChoi = this.khach.coNguoiChoiDaDangNhap()
                ? this.khach.user.nguoiChoi : null;
        SanhChoBoss sanh =
                QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);
        if (!dangTrongTranBoss(sanh)
                || !laLenhChuyenScenePhong(cmd)) {
            return false;
        }
        ChickenQuanLyMayChu.log(
                "[BOSS ROOM][BO_QUA_LENH_CHUYEN_SCENE_TRE]"
                + " playerId=" + nguoiChoi.ma
                + " cmd=" + cmd
                + " room=" + (sanh.getMaBan() & 0xFF)
                + " state=" + sanh.getTrangThai());
        return true;
    }

    static boolean dangTrongTranBoss(SanhChoBoss sanh) {
        if (sanh == null) {
            return false;
        }
        SanhChoBoss.TrangThai trangThai = sanh.getTrangThai();
        return trangThai == SanhChoBoss.TrangThai.DANG_BAT_DAU
                || trangThai == SanhChoBoss.TrangThai.DANG_CHIEN;
    }

    static boolean laLenhChuyenScenePhong(int cmd) {
        return switch (cmd) {
            case -98, -28, 6, 7, 8, 16, 18, 20, 71, 75, 83 -> true;
            default -> false;
        };
    }

    static boolean laLenhBoLuot(int cmd, int soBytePayload) {
        return cmd == 49 && soBytePayload == 0;
    }

    private static boolean canNguoiChoiDaDangNhap(int cmd) {
        return cmd != 1
                && cmd != -58
                && cmd != -71
                && cmd != 58
                && cmd != 114
                && cmd != -102
                && cmd != -60;
    }

    @Override
    public void khiKetNoiLoi() {
        System.out.println("Client " + this.khach.ma + ": Kết nối thất bại!");
    }

    @Override
    public void khiMatKetNoi() {
        System.out.println("Client " + this.khach.ma + ": Mất kết nối!");
    }

    @Override
    public void khiKetNoiThanhCong() {
        System.out.println("Client " + this.khach.ma + ": Kết nối thành công!");
    }
}
