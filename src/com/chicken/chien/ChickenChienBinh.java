package com.chicken.chien;

import com.chicken.mohinh.ChickenNguoiChoi;
import com.chicken.avg.ChickenThanhDiChuyenAVG;
import com.chicken.avg.ChickenCoCheBayAVG;

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
    public int tanCong;
    public int giap;
    public int mayMan;
    public int tocDo;
    /** Thể lực di chuyển tối đa do server chốt từ option 26 khi vào trận. */
    public int theLucDiChuyenToiDa;
    /** Quãng đường chủ động còn lại trong lượt hiện tại. */
    public int quangDuongDiChuyenConLai;
    /** Mốc chống spam CMD 53; chỉ dùng ở server, không nhận từ client. */
    public long lanDongBoToaDoGanNhat;
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
        this.avenger = ChickenCoCheBayAVG.layAvengerTuTrangBi(nguoiChoi);
        this.duocPhepBay = ChickenCoCheBayAVG.laIdBayDuocPhep(this.avenger);
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
        return this.nguoiChoi != null && this.nguoiChoi.dichVu != null;
    }

    /** Phân biệt người chơi thật với bot luyện tập và boss do server tạo. */
    public boolean laNguoiChoiThat() {
        return !this.bot && this.nguoiChoi != null;
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
                ChickenThanhDiChuyenAVG.hoiDay(this.theLucDiChuyenToiDa)
        );
    }

    public byte phanTramMau() {
        if (this.mauToiDa <= 0) {
            return 0;
        }
        return (byte)Math.max(0, Math.min(100, this.hp * 100 / this.mauToiDa));
    }
}
