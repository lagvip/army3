package com.chicken.chien;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.avg.ChickenThanhDiChuyenAVG;
import com.chicken.avg.ChickenCoCheBayAVG;
import com.chicken.chiso.ChickenHieuUngDongDoi;
import com.chicken.chiso.ChickenChiSoNguoiChoi;
import com.chicken.vatpham.ChickenVatPham;
import com.chicken.vatpham.ChickenYeuCauCapVatPham;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class ChickenChienBinh {
    public final ChickenNguoiChoi nguoiChoi;
    public final byte chiSo;
    public final boolean bot;
    public String ten;
    public final int ma;
    /** Bộ chiến đấu có thể đổi trong trận khi Loki giả dạng người chơi khác. */
    public short maVuKhi;
    public byte avenger;
    /** Quyền bay do server cấp từ trang bị thật; không nhận từ packet client. */
    public boolean duocPhepBay;
    public short x;
    public short y;
    public int hp;
    public int mauToiDa;
    public boolean chet;
    /**
     * Đã rời hẳn trận đấu nhưng phiên đăng nhập vẫn còn hoạt động.
     *
     * Tách cờ này khỏi {@link #chet}: người chết vẫn cần nhận diễn biến trận,
     * còn người đã bấm thoát thì tuyệt đối không được nhận packet trận đấu nữa.
     */
    public boolean daRoiTran;
    public int tanCong;
    public int giap;
    public int mayMan;
    public int tocDo;
    /** Chan viec phat packet/khoi tao lap lam nhan buff Dong doi nhieu lan. */
    private boolean daApDungThuongDongDoi;
    private int diemDongDoiDaApDung;
    /** Sung va cac o sung Balo chi ton tai trong snapshot tran, khong ghi vao kho do. */
    private ChickenVatPham sungDangCamTrongTran;
    private final Map<Integer, ChickenVatPham> sungTheoOTrongBalo =
            new HashMap<Integer, ChickenVatPham>();
    /**
     * Vat pham chien dau duoc khoa theo vi tri hien thi trong Balo luc vao
     * tran. Client chi gui vi tri nay; ID, itemUsed va icon luon do snapshot
     * server suy ra.
     */
    private final Map<Integer, VatPhamChienTrongTran> vatPhamTheoOTrongBalo =
            new HashMap<Integer, VatPhamChienTrongTran>();
    /** So lan dung tung item trong snapshot tran; khong lay tu packet client. */
    private final Map<Integer, Integer> soLanDungVatPhamTrongTran =
            new HashMap<Integer, Integer>();
    /** Vat pham tao dan da duoc server duyet, dang cho CMD 22 that. */
    private VatPhamChienTrongTran vatPhamChienDangCho;
    /**
     * Mot luot chi duoc chon mot loai hanh dong dac biet: vat pham hoac POW.
     * Co nay la state authoritative theo luot, khong bao gio doc tu client.
     */
    private byte hanhDongDacBietTrongLuot;
    private static final byte HANH_DONG_DAC_BIET_KHONG = 0;
    private static final byte HANH_DONG_DAC_BIET_VAT_PHAM = 1;
    private static final byte HANH_DONG_DAC_BIET_POW = 2;
    /** Thể lực di chuyển tối đa do server chốt từ option 26 khi vào trận. */
    public int theLucDiChuyenToiDa;
    /** Quãng đường chủ động còn lại trong lượt hiện tại. */
    public int quangDuongDiChuyenConLai;
    /** Buff item 223 do server giu trong snapshot tran, khong doc tu client. */
    private boolean diChuyenX2DaKichHoat;
    /** Mốc chống spam CMD 53; chỉ dùng ở server, không nhận từ client. */
    public long lanDongBoToaDoGanNhat;
    /**
     * Bị tảng đá của Boss Rùa ghim tại chỗ. Tọa độ neo chỉ do server đặt khi
     * phát CMD -68; client không được tự khai trạng thái này.
     */
    public boolean biDaRuaGhim;
    public short xDaRuaGhim;
    public short yDaRuaGhim;
    /** Trạng thái độc do đạn Rùa gây ra; toàn bộ damage được server lưu và tính. */
    public boolean biDocBossRua;
    public int satThuongDocBossRuaMoiLuot;
    public byte slotGayDocBossRua = -1;
    public int hawkSoLuotBan;
    public boolean hawkDaDungKyNang;
    public boolean hawkDaGuiChonMucTieu;
    public boolean thorDaDungKyNang;
    public boolean thorDaGuiMenu;
    public boolean lokiDaDungKyNang;
    public boolean lokiDaGuiMenu;
    public boolean lokiDangChoChonMucTieu;
    public boolean lokiSkillActive;
    public boolean ultronDaDungKyNang;
    public boolean ultronDaGuiMenu;
    public boolean ultronDangBanX3;
    public short ultronGocNgamHienTai = 45;
    public byte ultronLucNgamHienTai = 30;
    public boolean ultronDaCoGocNgam;
    public boolean ironManDaDungKyNang;
    public boolean ironManDaGuiMenu;
    public boolean ironManLaserSanSang;

    public ChickenChienBinh(ChickenNguoiChoi nguoiChoi, byte chiSo, short x, short y) {
        this.nguoiChoi = nguoiChoi;
        this.chiSo = chiSo;
        this.bot = false;
        this.ten = nguoiChoi.ten;
        this.ma = nguoiChoi.ma;
        /*
         * avenger la state do server suy ra luc nap/doi trang bi. Dung truc
         * tiep de client nhan dung animation bay cua Iron Man/Ultron.
         */
        this.avenger = ChickenCoCheBayAVG.layAvengerTuTrangBi(nguoiChoi);
        this.duocPhepBay = ChickenCoCheBayAVG.laIdBayDuocPhep(this.avenger);
        this.khoiTaoSungTrongTran(nguoiChoi);
        this.maVuKhi = layVuKhiHienThiTrongTran(nguoiChoi);
        this.x = x;
        this.y = y;
        this.mauToiDa = Math.max(100, nguoiChoi.layTongMauHienTai());
        this.tanCong = Math.max(0, nguoiChoi.layTongTanCongHienTai());
        this.giap = Math.max(0, nguoiChoi.layTongGiapHienTai());
        this.mayMan = Math.max(0, nguoiChoi.layTongMayManHienTai());
        this.tocDo = Math.max(0, nguoiChoi.layTongTocDoHienTai());
        this.theLucDiChuyenToiDa = ChickenThanhDiChuyenAVG.quangDuongToiDa(nguoiChoi);
        this.quangDuongDiChuyenConLai = ChickenThanhDiChuyenAVG.hoiDay(
                this.theLucDiChuyenToiDa);
        this.hp = this.mauToiDa;
    }

    public ChickenChienBinh(byte chiSo, short x, short y, String ten, short maVuKhi, byte avenger) {
        this.nguoiChoi = null;
        this.chiSo = chiSo;
        this.bot = true;
        this.ten = ten;
        this.ma = -9000 - chiSo;
        this.maVuKhi = maVuKhi;
        this.avenger = avenger;
        this.duocPhepBay = ChickenCoCheBayAVG.laIdBayDuocPhep(avenger);
        this.x = x;
        this.y = y;
        this.mauToiDa = 160;
        this.tanCong = 20;
        this.giap = 0;
        this.mayMan = 0;
        this.tocDo = 0;
        this.theLucDiChuyenToiDa =
                ChickenThanhDiChuyenAVG.quangDuongToiDaTheoAvenger(this.avenger);
        this.quangDuongDiChuyenConLai = ChickenThanhDiChuyenAVG.hoiDay(
                this.theLucDiChuyenToiDa);
        this.hp = this.mauToiDa;
    }

    /** Tạo chiến binh boss có chỉ số cố định cho các trận boss riêng. */
    public ChickenChienBinh(
            byte chiSo,
            int ma,
            short x,
            short y,
            String ten,
            short maVuKhi,
            int mauToiDa,
            int tanCong,
            int giap
    ) {
        this.nguoiChoi = null;
        this.chiSo = chiSo;
        this.bot = true;
        this.ten = ten;
        this.ma = ma;
        this.maVuKhi = maVuKhi;
        this.avenger = 0;
        this.duocPhepBay = false;
        this.x = x;
        this.y = y;
        this.mauToiDa = Math.max(1, mauToiDa);
        this.hp = this.mauToiDa;
        this.tanCong = Math.max(0, tanCong);
        this.giap = Math.max(0, giap);
        this.mayMan = 0;
        this.tocDo = 0;
        this.theLucDiChuyenToiDa = 0;
        this.quangDuongDiChuyenConLai = 0;
    }

    private static short layVuKhiHienThiTrongTran(ChickenNguoiChoi nguoiChoi) {
        if (nguoiChoi == null) {
            return (short) -1;
        }
        /*
         * Mọi bộ AVG (391-398), không riêng Ultron, đều ẩn sprite súng bằng
         * cách đặt wp=-1. ID vật lý/collision vẫn phải lấy từ itemBody[5];
         * nếu dùng wp thì Captain thành súng -1 và không bao giờ vào nhánh
         * xuyên người, Winter Soldier cũng không vào nhánh xuyên địa hình.
         */
        short partSungDangTrangBi = nguoiChoi.layMaHinhSungDangTrangBi();
        return partSungDangTrangBi >= 0
                ? partSungDangTrangBi
                : nguoiChoi.wp;
    }

    public boolean coPhien() {
        return !this.daRoiTran
                && this.nguoiChoi != null
                && this.nguoiChoi.dichVu != null;
    }

    /** Phân biệt người chơi thật với bot luyện tập và boss do server tạo. */
    public boolean laNguoiChoiThat() {
        return !this.bot && this.nguoiChoi != null;
    }

    /**
     * Chot buff Dong doi tren snapshot chi so chien dau.
     *
     * <p>{@code diemDongDoi} la muc cao nhat cua phe da duoc server chot
     * luc tao tran, khong nhat thiet la diem rieng cua chien binh nay.
     *
     * <p>Chi Mau, Tan cong va Giap thay doi. HP hien tai duoc cong dung phan
     * Mau toi da tang them, nen goi sau khi da mat mau cung khong hoi day mau.
     */
    public boolean apDungThuongDongDoi(int diemDongDoi) {
        if (this.daApDungThuongDongDoi || !this.laNguoiChoiThat()) {
            return false;
        }
        this.daApDungThuongDongDoi = true;
        this.diemDongDoiDaApDung = Math.max(0, diemDongDoi);

        int mauCu = Math.max(1, this.mauToiDa);
        int mauMoi = Math.max(1,
                ChickenHieuUngDongDoi.tinhChiSoSauThuong(
                        mauCu, diemDongDoi));
        int mauCongThem = Math.max(0, mauMoi - mauCu);
        this.mauToiDa = mauMoi;
        this.hp = (int) Math.min(
                (long) this.mauToiDa,
                Math.max(0L, (long) this.hp + mauCongThem));
        this.tanCong = ChickenHieuUngDongDoi.tinhChiSoSauThuong(
                this.tanCong, diemDongDoi);
        this.giap = ChickenHieuUngDongDoi.tinhChiSoSauThuong(
                this.giap, diemDongDoi);
        return true;
    }

    private void khoiTaoSungTrongTran(ChickenNguoiChoi nguoiChoi) {
        this.sungDangCamTrongTran = nguoiChoi == null
                ? null : nguoiChoi.laySungTrangBiMayChu();
        this.sungTheoOTrongBalo.clear();
        this.vatPhamTheoOTrongBalo.clear();
        this.soLanDungVatPhamTrongTran.clear();
        this.vatPhamChienDangCho = null;
        this.hanhDongDacBietTrongLuot = HANH_DONG_DAC_BIET_KHONG;
        if (nguoiChoi == null || nguoiChoi.itemBalo == null
                || nguoiChoi.itemBag == null) {
            return;
        }
        // CMD 26 cua client gui vi tri hien thi trong Balo (0..4), khong gui
        // index that trong itemBag. Snapshot tran vi the phai duoc khoa theo
        // viTriBalo; itemBalo[viTriBalo] chi dung de tim vat pham ban dau.
        for (int viTriBalo = 0;
                viTriBalo < nguoiChoi.itemBalo.length;
                viTriBalo++) {
            int chiSoTui = nguoiChoi.itemBalo[viTriBalo];
            if (chiSoTui < 0 || chiSoTui >= nguoiChoi.itemBag.length) {
                continue;
            }
            ChickenVatPham sung = nguoiChoi.itemBag[chiSoTui];
            if (ChickenNguoiChoi.laSungThuongDuocPhepTrongBalo(sung)
                    && nguoiChoi.datCapSuDungVatPham(sung)) {
                this.sungTheoOTrongBalo.put(viTriBalo, sung);
                continue;
            }
            if (sung != null && sung.mau != null && sung.soLuong > 0
                    && nguoiChoi.datCapSuDungVatPham(sung)
                    && ChickenCongThucVatPhamChien.khopMauVatPham(
                            sung.mau)) {
                ChickenCongThucVatPhamChien.CauHinh cauHinh =
                        ChickenCongThucVatPhamChien.theoIdVatPham(sung.ma);
                this.vatPhamTheoOTrongBalo.put(
                        viTriBalo,
                        new VatPhamChienTrongTran(
                                viTriBalo,
                                chiSoTui,
                                sung.ma,
                                sung.mau.iconID,
                                sung.mau.strRequire,
                                cauHinh
                        )
                );
            }
        }
    }

    public ChickenVatPham laySungDangCamTrongTran() {
        return this.sungDangCamTrongTran;
    }

    public ChickenVatPham laySungTrongOTrongBalo(int viTriBalo) {
        return this.sungTheoOTrongBalo.get(viTriBalo);
    }

    public VatPhamChienTrongTran layVatPhamChienTrongOTrongBalo(
            int viTriBalo
    ) {
        return this.vatPhamTheoOTrongBalo.get(viTriBalo);
    }

    public VatPhamChienTrongTran layVatPhamChienDangCho() {
        return this.vatPhamChienDangCho;
    }

    /**
     * Chon vat pham chi thay doi state tran, chua tru kho. Vat pham chi bi
     * tru sau khi CMD 22 hop le duoc server chap nhan.
     */
    public boolean chonVatPhamChienTrongTran(int viTriBalo) {
        VatPhamChienTrongTran vatPham =
                this.vatPhamTheoOTrongBalo.get(viTriBalo);
        if (!this.laNguoiChoiThat() || vatPham == null
                || !this.coTheDungVatPhamChienTrongTran(vatPham)) {
            return false;
        }
        if (this.vatPhamChienDangCho != null) {
            return this.vatPhamChienDangCho == vatPham;
        }
        if (this.hanhDongDacBietTrongLuot
                != HANH_DONG_DAC_BIET_KHONG) {
            return false;
        }
        this.vatPhamChienDangCho = vatPham;
        this.hanhDongDacBietTrongLuot = HANH_DONG_DAC_BIET_VAT_PHAM;
        return true;
    }

    public void xoaVatPhamChienDangCho() {
        this.vatPhamChienDangCho = null;
    }

    /** Hoan tac lua chon neu giao dich item tuc thoi khong commit duoc. */
    public void huyChonVatPhamChienTrongLuot(
            VatPhamChienTrongTran vatPham
    ) {
        if (this.vatPhamChienDangCho == vatPham
                && this.hanhDongDacBietTrongLuot
                        == HANH_DONG_DAC_BIET_VAT_PHAM) {
            this.vatPhamChienDangCho = null;
            this.hanhDongDacBietTrongLuot = HANH_DONG_DAC_BIET_KHONG;
        }
    }

    /** POW chi duoc khoa neu luot nay chua chon bat ky vat pham/POW nao. */
    public boolean danhDauKichHoatPowTrongLuot() {
        if (!this.laNguoiChoiThat()
                || this.hanhDongDacBietTrongLuot
                        != HANH_DONG_DAC_BIET_KHONG
                || this.vatPhamChienDangCho != null) {
            return false;
        }
        this.hanhDongDacBietTrongLuot = HANH_DONG_DAC_BIET_POW;
        return true;
    }

    /** Hoan tac quyen POW neu thanh POW khong du dieu kien de kich hoat. */
    public void huyDanhDauPowTrongLuot() {
        if (this.hanhDongDacBietTrongLuot == HANH_DONG_DAC_BIET_POW) {
            this.hanhDongDacBietTrongLuot = HANH_DONG_DAC_BIET_KHONG;
        }
    }

    public boolean daChonVatPhamTrongLuot() {
        return this.hanhDongDacBietTrongLuot
                == HANH_DONG_DAC_BIET_VAT_PHAM;
    }

    /** Chi goi khi server thuc su ket thuc luot. */
    public void datLaiHanhDongDacBietTrongLuot() {
        this.vatPhamChienDangCho = null;
        this.hanhDongDacBietTrongLuot = HANH_DONG_DAC_BIET_KHONG;
    }

    public boolean coTheDungVatPhamChienTrongTran(
            VatPhamChienTrongTran vatPham
    ) {
        if (vatPham == null || vatPham.getCauHinh() == null) {
            return false;
        }
        if (!this.laNguoiChoiThat()
                || !ChickenYeuCauCapVatPham.datYeuCau(
                        this.nguoiChoi.cap,
                        vatPham.getCapYeuCau())) {
            return false;
        }
        ChickenCauHinhSatThuongVatPham.HoSo hoSo =
                ChickenCauHinhSatThuongVatPham.theoIdVatPham(
                        vatPham.getIdVatPham());
        if (hoSo == null) {
            return false;
        }
        Integer daDung = this.soLanDungVatPhamTrongTran.get(
                vatPham.getIdVatPham());
        return (daDung == null ? 0 : daDung.intValue())
                < hoSo.getSoLanToiDaMoiTran();
    }

    /**
     * Co item da dung it nhat mot lan nhung van con quota trong tran. Client
     * cu an icon sau lan dung, nen dau luot sau server can gui lai Balo.
     */
    public boolean coVatPhamChienCanHienLai() {
        for (VatPhamChienTrongTran vatPham
                : this.vatPhamTheoOTrongBalo.values()) {
            Integer daDung = this.soLanDungVatPhamTrongTran.get(
                    vatPham.getIdVatPham());
            if (daDung != null && daDung.intValue() > 0
                    && this.coTheDungVatPhamChienTrongTran(vatPham)) {
                return true;
            }
        }
        return false;
    }

    public boolean danhDauDaDungVatPhamChienTrongTran(
            VatPhamChienTrongTran vatPham
    ) {
        if (!this.coTheDungVatPhamChienTrongTran(vatPham)) {
            return false;
        }
        int idVatPham = vatPham.getIdVatPham();
        Integer daDung = this.soLanDungVatPhamTrongTran.get(idVatPham);
        this.soLanDungVatPhamTrongTran.put(
                idVatPham, (daDung == null ? 0 : daDung.intValue()) + 1);
        return true;
    }

    public int layNapDanSauKhiDungVatPham(
            VatPhamChienTrongTran vatPham
    ) {
        ChickenCauHinhSatThuongVatPham.HoSo hoSo = vatPham == null
                ? null : ChickenCauHinhSatThuongVatPham.theoIdVatPham(
                        vatPham.getIdVatPham());
        return hoSo == null ? -1 : hoSo.getNapDanSauDung();
    }

    public boolean coDiChuyenX2() {
        return this.diChuyenX2DaKichHoat;
    }

    /**
     * Chi ChickenDiChuyenX2 goi sau khi giao dich item da commit. Phan thanh
     * da tieu khong duoc hoan: chi nhan doi quang duong con lai.
     */
    void kichHoatDiChuyenX2() {
        this.diChuyenX2DaKichHoat = true;
        this.quangDuongDiChuyenConLai =
                ChickenDiChuyenX2.nhanDoiAnToan(
                        this.quangDuongDiChuyenConLai);
    }

    public int layQuangDuongDiChuyenToiDaTrongLuot() {
        int coBan = ChickenThanhDiChuyenAVG.hoiDay(
                this.theLucDiChuyenToiDa);
        return this.diChuyenX2DaKichHoat
                ? ChickenDiChuyenX2.nhanDoiAnToan(coBan)
                : coBan;
    }

    public void hoiDayQuangDuongDiChuyen() {
        this.quangDuongDiChuyenConLai =
                this.layQuangDuongDiChuyenToiDaTrongLuot();
    }

    /**
     * Danh sach resource sung ma client co the cam trong snapshot tran nay.
     * Client cu chi tao BulletForGun cho cac part nam trong CMD 20.
     */
    public short[] layMaHinhSungCoTheCamTrongTran() {
        LinkedHashSet<Short> cacMa = new LinkedHashSet<>();
        themMaHinhSung(cacMa, this.sungDangCamTrongTran);
        for (ChickenVatPham sung : this.sungTheoOTrongBalo.values()) {
            themMaHinhSung(cacMa, sung);
        }
        short[] ketQua = new short[cacMa.size()];
        int i = 0;
        for (short ma : cacMa) {
            ketQua[i++] = ma;
        }
        return ketQua;
    }

    private static void themMaHinhSung(
            LinkedHashSet<Short> cacMa,
            ChickenVatPham sung
    ) {
        if (ChickenNguoiChoi.laSungThuongDuocPhepTrongBalo(sung)
                && sung.mau.part > 0) {
            cacMa.add(sung.mau.part);
        }
    }

    /**
     * Doi sung tren snapshot tran. Tra ve khau vua cat vao o Balo, hoac null
     * neu packet chon o khong hop le. Inventory that tuyet doi khong bi sua.
     */
    public ChickenVatPham doiSungTrongTran(int viTriBalo) {
        if (!this.laNguoiChoiThat()
                || !ChickenNguoiChoi.laSungThuongDuocPhepTrongBalo(
                        this.sungDangCamTrongTran)
                || !this.nguoiChoi.datCapSuDungVatPham(
                        this.sungDangCamTrongTran)) {
            return null;
        }
        ChickenVatPham sungMoi = this.sungTheoOTrongBalo.get(viTriBalo);
        if (!ChickenNguoiChoi.laSungThuongDuocPhepTrongBalo(sungMoi)
                || !this.nguoiChoi.datCapSuDungVatPham(sungMoi)) {
            return null;
        }
        ChickenVatPham sungCu = this.sungDangCamTrongTran;
        this.sungTheoOTrongBalo.put(viTriBalo, sungCu);
        this.sungDangCamTrongTran = sungMoi;
        this.vatPhamChienDangCho = null;
        this.maVuKhi = sungMoi.mau.part;
        int tanCongMoi = Math.max(0,
                ChickenChiSoNguoiChoi.tinhTanCongVoiSung(
                        this.nguoiChoi, sungMoi));
        this.tanCong = this.daApDungThuongDongDoi
                ? ChickenHieuUngDongDoi.tinhChiSoSauThuong(
                        tanCongMoi, this.diemDongDoiDaApDung)
                : tanCongMoi;
        return sungCu;
    }

    public static final class VatPhamChienTrongTran {
        private final int viTriBalo;
        private final int chiSoTui;
        private final int idVatPham;
        private final short icon;
        private final int capYeuCau;
        private final ChickenCongThucVatPhamChien.CauHinh cauHinh;

        private VatPhamChienTrongTran(
                int viTriBalo,
                int chiSoTui,
                int idVatPham,
                short icon,
                int capYeuCau,
                ChickenCongThucVatPhamChien.CauHinh cauHinh
        ) {
            this.viTriBalo = viTriBalo;
            this.chiSoTui = chiSoTui;
            this.idVatPham = idVatPham;
            this.icon = icon;
            this.capYeuCau = capYeuCau;
            this.cauHinh = cauHinh;
        }

        public int getViTriBalo() {
            return this.viTriBalo;
        }

        public int getChiSoTui() {
            return this.chiSoTui;
        }

        public int getIdVatPham() {
            return this.idVatPham;
        }

        public short getIcon() {
            return this.icon;
        }

        public int getCapYeuCau() {
            return this.capYeuCau;
        }

        public ChickenCongThucVatPhamChien.CauHinh getCauHinh() {
            return this.cauHinh;
        }
    }

    /**
     * Sao chép bộ nhân vật chiến đấu của một người chơi thật cho Loki.
     *
     * Không sao chép phiên đăng nhập, mã tài khoản, battle index, vị trí hay
     * trạng thái lượt; các dữ liệu đó vẫn thuộc người điều khiển Loki. Các cờ
     * menu/đang thi triển cũng không được chép để không phát lại action cũ.
     */
    public void saoChepBoChienDauTu(ChickenChienBinh mucTieu) {
        if (mucTieu == null || !mucTieu.laNguoiChoiThat()) {
            throw new IllegalArgumentException("Chi duoc sao chep nguoi choi that");
        }

        this.ten = mucTieu.ten;
        this.maVuKhi = mucTieu.maVuKhi;
        this.avenger = mucTieu.avenger;
        this.duocPhepBay = mucTieu.duocPhepBay;
        this.mauToiDa = Math.max(1, mucTieu.mauToiDa);
        this.hp = Math.max(0, Math.min(this.mauToiDa, mucTieu.hp));
        this.chet = this.hp <= 0;
        this.tanCong = Math.max(0, mucTieu.tanCong);
        this.giap = Math.max(0, mucTieu.giap);
        this.mayMan = Math.max(0, mucTieu.mayMan);
        this.tocDo = Math.max(0, mucTieu.tocDo);
        this.theLucDiChuyenToiDa = Math.max(0, mucTieu.theLucDiChuyenToiDa);

        // Sao chép tiến độ/cooldown skill của bộ AVG mục tiêu, nhưng không chép
        // token menu hay trạng thái action đang chạy của kết nối khác.
        this.hawkSoLuotBan = Math.max(0, mucTieu.hawkSoLuotBan);
        this.hawkDaDungKyNang = mucTieu.hawkDaDungKyNang;
        this.hawkDaGuiChonMucTieu = false;
        this.thorDaDungKyNang = mucTieu.thorDaDungKyNang;
        this.thorDaGuiMenu = false;
        this.ultronDaDungKyNang = mucTieu.ultronDaDungKyNang;
        this.ultronDaGuiMenu = false;
        this.ultronDangBanX3 = false;
        this.ultronGocNgamHienTai = mucTieu.ultronGocNgamHienTai;
        this.ultronLucNgamHienTai = mucTieu.ultronLucNgamHienTai;
        this.ultronDaCoGocNgam = mucTieu.ultronDaCoGocNgam;
        this.ironManDaDungKyNang = mucTieu.ironManDaDungKyNang;
        this.ironManDaGuiMenu = false;
        this.ironManLaserSanSang = false;

        // Không hoàn lại quãng đường Loki đã tiêu trong lượt hiện tại.
        this.quangDuongDiChuyenConLai = Math.min(
                Math.max(0, this.quangDuongDiChuyenConLai),
                this.layQuangDuongDiChuyenToiDaTrongLuot()
        );
    }

    public byte phanTramMau() {
        if (this.mauToiDa <= 0) {
            return 0;
        }
        return (byte)Math.max(0, Math.min(100, this.hp * 100 / this.mauToiDa));
    }
}
