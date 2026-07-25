package com.chicken.mang;

import com.chicken.cuahang.ChickenCuaHang;
import com.chicken.chien.ChickenQuanLyChien;
import com.chicken.dichvu.ChickenQuanLyXepHang;
import com.chicken.dichvu.ChickenQuanLyThanhTich;
import com.chicken.dichvu.ChickenQuanLyBietDoi;
import com.chicken.dichvu.ChickenQuanLyBanBe;
import com.chicken.phong.ChickenQuanLyPhong;
import com.chicken.phong.boss.sanhcho.DebugSanhBoss;
import com.chicken.phong.boss.sanhcho.QuanLySanhChoBoss;
import com.chicken.phong.boss.sanhcho.SanhChoBoss;
import com.chicken.npc.chihuy.XuLyMenuChiHuy;
import com.chicken.thoat.ChickenThoatTran;
import com.chicken.mohinh.ChickenNguoiChoi;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChickenXuLyTin
implements IChickenXuLyTin {
    private final ChickenPhien khach;

    public ChickenXuLyTin(ChickenPhien khach) {
        this.khach = khach;
    }

    @Override
    public void khiCoTin(ChickenTinNhan mss) {
        if (mss != null) {
            try {
                int cmd = mss.layLenh();
                int soByte = 0;
                try {
                    soByte = mss.boDoc().available();
                } catch (Exception ignored) {
                }
                String tenNguoiChoi = "chua_dang_nhap";
                try {
                    if (this.khach.user != null && this.khach.user.nguoiChoi != null
                            && this.khach.user.nguoiChoi.ten != null) {
                        tenNguoiChoi = this.khach.user.nguoiChoi.ten;
                    }
                } catch (Exception ignored) {
                }
                System.out.println("[CLIENT ACTION] client=" + this.khach.ma
                        + " player=" + tenNguoiChoi
                        + " cmd=" + cmd
                        + " bytes=" + soByte);
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
                        ChickenQuanLyPhong.batDau(this.khach.user.nguoiChoi);
                        break;
                    case 23:
                        ChickenQuanLyPhong.dauKiemTraVaCham(this.khach.user.nguoiChoi, mss);
                        break;
                    case -47:
                    case 49: {
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
                        int soByteLuaChon = mss.boDoc().available();
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
                        this.khach.user.nguoiChoi.xemCuaHang(ChickenCuaHang.SHOP_EQUIP);
                        break;
                    case -43:
                        this.khach.user.nguoiChoi.requestTab(mss);
                        break;
                    case 72:
                        this.khach.user.nguoiChoi.yeuCauMuaVatPham(mss);
                        break;
                    case 26:
                        this.khach.user.nguoiChoi.dungVatPham(mss);
                        break;
                    case -44:
                        this.khach.user.nguoiChoi.chuyenVatPham(mss);
                        break;
                    case -25:
                        this.khach.user.nguoiChoi.thucHien(mss);
                        break;
                    case -48:
                        this.khach.user.nguoiChoi.yeuCauBanVatPham(mss);
                        break;
                    case -33:
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
                        short materialId = mss.boDoc().readShort();
                        SanhChoBoss sanhBoss =
                                QuanLySanhChoBoss.timSanhCuaNguoiChoi(nguoiChoi);

                        if (sanhBoss != null) {
                            DebugSanhBoss.log("NHAN_CMD_126", nguoiChoi,
                                    "P4-" + (sanhBoss.getMaBan() & 0xFF)
                                    + " map=" + (sanhBoss.getMaBanDo() & 0xFF)
                                    + " materialId=" + (materialId & 0xFFFF));

                            /*
                             * Client gửi CMD 126 ngay trong lúc còn đang phân tích
                             * CMD 75. Nếu server local trả ảnh tức thì, client có thể
                             * nhận ảnh trước khi nó gán xong tổng số ảnh cần chờ, làm
                             * màn hình phòng chờ không bao giờ được kích hoạt. Chỉ
                             * trì hoãn phản hồi ảnh terrain cho sảnh boss để tránh
                             * race này; PvP và các chế độ cũ không bị thay đổi.
                             */
                            try {
                                Thread.sleep(250L);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                            DebugSanhBoss.log("GUI_CMD_126_SAU_DELAY", nguoiChoi,
                                    "materialId=" + (materialId & 0xFFFF)
                                    + " delayMs=250");
                        }

                        this.khach.user.dichVu.yeuCauNguyenLieu(materialId);
                        break;
                    }
                    case -40:
                        this.khach.user.dichVu.yeuCauDanLuyenTap(mss);
                        break;
                    case 83:
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
                        System.out.println("[SKILL] NHAN_CMD_-91 player="
                                + (nguoiChoi != null ? nguoiChoi.ten : "null")
                                + " bytes=" + mss.boDoc().available()
                                + " inTraining=" + (nguoiChoi != null && nguoiChoi.inTraining));

                        // Luyện tập không tạo ChickenQuanLyChien. CMD -91 phải đi
                        // thẳng vào phiên luyện tập, nếu không luôn báo không tìm thấy trận.
                        if (nguoiChoi != null && nguoiChoi.inTraining) {
                            System.out.println("[SKILL] ROUTE_LUYEN_TAP player=" + nguoiChoi.ten);
                            nguoiChoi.xuLyCmd91KyNangDacBietLuyenTap(mss);
                            break;
                        }

                        ChickenQuanLyChien tranDau =
                                ChickenQuanLyChien.timTranDauCuaNguoiChoi(nguoiChoi);
                        if (tranDau == null) {
                            System.out.println("[SKILL] KHONG_TIM_THAY_TRAN_DAU player="
                                    + (nguoiChoi != null ? nguoiChoi.ten : "null"));
                            break;
                        }

                        tranDau.nhanLenhKyNangDacBiet(nguoiChoi, mss);
                        break;
                    }
                    case -92:
                        this.khach.user.nguoiChoi.handleTrainingHoleRequest(mss);
                        break;
                    case -67:
                        this.khach.user.nguoiChoi.handleTrainingClientReady();
                        break;
                    default:
                        if (mss.layLenh() != -98) {
                            System.out.println("CMD: " + mss.layLenh());
                        }
                        break;
                }
            }
            catch (Exception ex) {
                Logger.getLogger(ChickenXuLyTin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
