package com.chicken.kiemthu;

import com.chicken.chien.ChickenChienBinh;
import com.chicken.chien.ChickenMayMan;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ma tran hoi quy cua May man.
 *
 * <p>Test dong bao phu cong thuc 4 trang thai. Test tinh bao dam moi diem noi
 * damage vao PvP, luyen tap, AVG va 7 map boss deu chot May man truoc packet
 * animation. Neu them nguon damage moi, so diem goi hoac quy tac se thay doi
 * va test bat buoc lap trinh vien cap nhat ma tran.</p>
 */
public final class ChickenKiemThuPhuMayMan {

    private static int soToHop;
    private static int soQuyTac;

    private ChickenKiemThuPhuMayMan() {
    }

    public static void main(String[] args) throws Exception {
        tuKiemTra();
    }

    public static void tuKiemTra() throws Exception {
        soToHop = 0;
        soQuyTac = 0;
        kiemTraBonTrangThaiDamage();
        kiemTraDanhSachDiemNoi();
        kiemTraThuTuToanBoNguon();
        kiemTraNguonKhongTuGayDamage();
        System.out.println("MAY_MAN_MATRIX_OK combinations=" + soToHop
                + " integrationRules=" + soQuyTac
                + " callSites=52");
    }

    private static void kiemTraBonTrangThaiDamage() {
        int[] cacDamage = {
            1, 2, 3, 100, 101, 10_000, Integer.MAX_VALUE
        };
        for (int congKichHoat = 0; congKichHoat <= 1; congKichHoat++) {
            for (int thuKichHoat = 0; thuKichHoat <= 1; thuKichHoat++) {
                for (int damageGoc : cacDamage) {
                    ChickenChienBinh nguoiBan = chienBinh(
                            (byte) 0, congKichHoat);
                    ChickenChienBinh mucTieu = chienBinh(
                            (byte) 1, thuKichHoat);
                    int[] soLanQuay = {0};
                    ChickenMayMan.PhienTanCong phien =
                            ChickenMayMan.batDauChoKiemThu(
                                    nguoiBan,
                                    new ChickenChienBinh[]{
                                        nguoiBan, mucTieu
                                    },
                                    gioiHan -> {
                                        soLanQuay[0]++;
                                        return 0;
                                    });
                    int thucTe = phien.apDung(mucTieu, damageGoc);
                    int mongDoi = tinhDamageMongDoi(
                            damageGoc,
                            congKichHoat != 0,
                            thuKichHoat != 0);
                    bang(mongDoi, thucTe,
                            "sai ma tran cong=" + congKichHoat
                            + " thu=" + thuKichHoat
                            + " damage=" + damageGoc);
                    int soLanQuaySauLanDau = soLanQuay[0];
                    bang(thucTe, phien.apDung(mucTieu, damageGoc),
                            "cung muc tieu doi ket qua trong mot phien");
                    bang(soLanQuaySauLanDau, soLanQuay[0],
                            "cung muc tieu bi quay lai trong mot phien");
                    soToHop++;
                }
            }
        }

        ChickenChienBinh tuBan = chienBinh((byte) 0, 1);
        ChickenMayMan.PhienTanCong phienTuBan =
                ChickenMayMan.batDauChoKiemThu(
                        tuBan,
                        new ChickenChienBinh[]{tuBan},
                        gioiHan -> 0);
        bang(100, phienTuBan.apDung(tuBan, 100),
                "tu ban co ca cong va thu khong triet tieu");
        bang(0, phienTuBan.apDung(null, 100),
                "muc tieu null tao damage");
        bang(0, phienTuBan.apDung(tuBan, 0),
                "damage 0 bi nang thanh damage duong");
        bang(0, phienTuBan.apDung(tuBan, -1),
                "damage am bi nang thanh damage duong");
        soToHop += 4;
    }

    private static int tinhDamageMongDoi(
            int damageGoc,
            boolean congKichHoat,
            boolean thuKichHoat
    ) {
        long damage = damageGoc;
        if (congKichHoat) {
            damage = Math.min(Integer.MAX_VALUE, damage * 2L);
        }
        if (thuKichHoat) {
            damage = Math.max(1L, (damage + 1L) / 2L);
        }
        return (int) Math.max(1L, Math.min(Integer.MAX_VALUE, damage));
    }

    private static ChickenChienBinh chienBinh(byte slot, int coMayMan) {
        ChickenChienBinh ketQua = new ChickenChienBinh(
                slot,
                (short) (100 + slot * 50),
                (short) 500,
                "Matrix" + slot,
                (short) 57,
                (byte) 0);
        ketQua.mayMan = coMayMan == 0 ? 0 : 1;
        return ketQua;
    }

    private static void kiemTraDanhSachDiemNoi() throws Exception {
        Map<String, Integer> mongDoi = new LinkedHashMap<>();
        mongDoi.put("src/com/chicken/avg/ChickenKyNangDacBietHawk.java", 1);
        mongDoi.put("src/com/chicken/avg/ChickenKyNangDacBietThor.java", 1);
        mongDoi.put("src/com/chicken/chien/ChickenQuanLyChien.java", 4);
        mongDoi.put("src/com/chicken/mohinh/ChickenNguoiChoi.java", 4);
        mongDoi.put("src/com/chicken/phong/boss/trandau/baovay/BossBaoVay.java", 5);
        mongDoi.put("src/com/chicken/phong/boss/trandau/haitoathap/BossHaiToaThap.java", 5);
        mongDoi.put("src/com/chicken/phong/boss/trandau/khicau/BossKhiCau.java", 5);
        mongDoi.put("src/com/chicken/phong/boss/trandau/datbom/BossDatBom.java", 5);
        mongDoi.put("src/com/chicken/phong/boss/trandau/rua/BossRua.java", 7);
        mongDoi.put("src/com/chicken/phong/boss/trandau/rong/BossRong.java", 5);
        mongDoi.put("src/com/chicken/phong/boss/trandau/ruarong/BossRuaRong.java", 10);

        int tong = 0;
        for (Map.Entry<String, Integer> entry : mongDoi.entrySet()) {
            String source = doc(entry.getKey());
            int thucTe = dem(source, "ChickenMayMan.batDau");
            bang(entry.getValue(), thucTe,
                    "so diem noi May man thay doi o " + entry.getKey());
            tong += thucTe;
        }
        bang(52, tong, "tong diem noi May man khong khop ma tran");
    }

    private static void kiemTraThuTuToanBoNguon() throws Exception {
        thuTu(
                "src/com/chicken/avg/ChickenKyNangDacBietHawk.java",
                "private synchronized boolean thiTrien(",
                "ChickenMayMan.batDau",
                "this.dieuKhien.guiHoatAnhMuiTen(");
        thuTu(
                "src/com/chicken/avg/ChickenKyNangDacBietThor.java",
                "private synchronized void thiTrien(",
                "ChickenMayMan.batDau",
                "this.dieuKhien.guiTiaSet(");

        String pvp = "src/com/chicken/chien/ChickenQuanLyChien.java";
        thuTu(pvp, "public synchronized void ban(",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(pvp, "private synchronized void banX3Ultron(",
                "ChickenMayMan.batDau", "this.phatMotLanBanUltron(");
        thuTu(pvp, "private synchronized void nhipBot()",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(pvp, "private void banLaserIronMan(",
                "ChickenMayMan.batDau",
                "guiTiaLaserIronManDau(");

        String luyenTap = "src/com/chicken/mohinh/ChickenNguoiChoi.java";
        thuTu(luyenTap, "public synchronized void xuLyBanLuyenTap(",
                "this.chuanBiMayManTruocPhatLuyenTap()",
                "this.dichVu.guiKetQuaBanLuyenTap(");
        thuTu(luyenTap, "private void xuLyPhatLaserIronManLuyenTap(",
                "this.chuanBiMayManTruocPhatLuyenTap()",
                "this.dichVu.guiTiaLaserIronManLuyenTap(");
        thuTu(luyenTap, "private void xuLyPhatBanX3UltronLuyenTap(",
                "this.chuanBiMayManTruocPhatLuyenTap()",
                "this.dichVu.guiKetQuaBanLuyenTap(");
        thuTu(luyenTap, "public synchronized void xuLyCmd91HawkLuyenTap(",
                "ChickenMayMan.batDau",
                "this.dichVu.guiKetQuaBanLuyenTap(");
        thuTu(luyenTap, "private synchronized void xuLyCmd91ThorLuyenTap(",
                "ChickenMayMan.batDau",
                "this.dichVu.guiTiaSetThor(");
        thuTu(luyenTap, "private long botLuyenTapBanTra(",
                "ChickenMayMan.batDau",
                "this.dichVu.guiKetQuaBanLuyenTap(");
        khongChua(luyenTap,
                "private synchronized void xuLyPhatBanNguoiChoiLuyenTap(",
                "ChickenMayMan.batDau");

        String[] bossSungCamTu = {
            "src/com/chicken/phong/boss/trandau/baovay/BossBaoVay.java",
            "src/com/chicken/phong/boss/trandau/haitoathap/BossHaiToaThap.java",
            "src/com/chicken/phong/boss/trandau/khicau/BossKhiCau.java",
            "src/com/chicken/phong/boss/trandau/datbom/BossDatBom.java"
        };
        for (String file : bossSungCamTu) {
            thuTu(file, "public synchronized void ban(",
                    "ChickenMayMan.batDau", "this.phatBan(");
            thuTu(file, "private void thucHienBossBanSung(",
                    "ChickenMayMan.batDau", "this.phatBan(");
            thuTu(file, "private void noCamTu(",
                    "ChickenMayMan.batDau", "this.phatBan(");
            String khaiBaoX3 = file.endsWith("BossDatBom.java")
                    ? "private ChickenKetQuaDan banX3UltronBoss("
                    : "private void banX3UltronBoss(";
            thuTu(file, khaiBaoX3,
                    "ChickenMayMan.batDau",
                    "guiLoatLaserUltronDau(");
            thuTu(file, "private void banLaserIronManBoss(",
                    "ChickenMayMan.batDau",
                    "ChickenTiaLaserIronMan.phatHienThiTrongTran(");
        }

        String rua = "src/com/chicken/phong/boss/trandau/rua/BossRua.java";
        thuTu(rua, "public synchronized void ban(",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(rua, "private void damDaRua(",
                "ChickenMayMan.batDau", "this.phatDamDaRua(");
        thuTu(rua, "private void ruaTanCong(",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(rua, "private void banDanRua(",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(rua, "private boolean xuLyDocDauLuot(",
                "ChickenMayMan.batDau", "this.phatHieuUngDoc(");
        thuTu(rua, "private ChickenKetQuaDan banX3UltronBoss(",
                "ChickenMayMan.batDau", "guiLoatLaserUltronDau(");
        thuTu(rua, "private void banLaserIronManBoss(",
                "ChickenMayMan.batDau",
                "ChickenTiaLaserIronMan.phatHienThiTrongTran(");

        String rong = "src/com/chicken/phong/boss/trandau/rong/BossRong.java";
        thuTu(rong, "public synchronized void ban(",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(rong, "private void phatLoatHaiVienRong(",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(rong, "private void bayDenGapNguoi(",
                "ChickenMayMan.batDau", "this.phatHoatAnhGapThaBossRong(");
        thuTu(rong, "private ChickenKetQuaDan banX3UltronBoss(",
                "ChickenMayMan.batDau", "guiLoatLaserUltronDau(");
        thuTu(rong, "private void banLaserIronManBoss(",
                "ChickenMayMan.batDau",
                "ChickenTiaLaserIronMan.phatHienThiTrongTran(");
        khongChua(rong, "private void hoanTatGapThaNguoi(",
                "ChickenMayMan.batDau");
        batBuocChua(rong, "private void hoanTatGapThaNguoi(",
                "phienMayMan.apDung");

        String ruaRong =
                "src/com/chicken/phong/boss/trandau/ruarong/BossRuaRong.java";
        thuTu(ruaRong, "public synchronized void ban(",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(ruaRong, "private void damDaRua(",
                "ChickenMayMan.batDau", "this.phatDamDaRua(");
        thuTu(ruaRong, "private void banDanRua(",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(ruaRong, "private boolean xuLyDocDauLuot(",
                "ChickenMayMan.batDau", "this.phatHieuUngDoc(");
        thuTu(ruaRong, "private void phatLoatHaiVienRong(",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(ruaRong, "private void bayDenGapNguoiRong(",
                "ChickenMayMan.batDau", "this.phatHoatAnhGapThaBossRong(");
        thuTu(ruaRong, "private void thucHienBossBanSung(",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(ruaRong, "private void noCamTu(",
                "ChickenMayMan.batDau", "this.phatBan(");
        thuTu(ruaRong, "private ChickenKetQuaDan banX3UltronBoss(",
                "ChickenMayMan.batDau", "guiLoatLaserUltronDau(");
        thuTu(ruaRong, "private void banLaserIronManBoss(",
                "ChickenMayMan.batDau",
                "ChickenTiaLaserIronMan.phatHienThiTrongTran(");
        khongChua(ruaRong, "private void hoanTatGapThaNguoiRong(",
                "ChickenMayMan.batDau");
        batBuocChua(ruaRong, "private void hoanTatGapThaNguoiRong(",
                "phienMayMan.apDung");
    }

    private static void kiemTraNguonKhongTuGayDamage() throws Exception {
        String loki = doc(
                "src/com/chicken/avg/ChickenKyNangDacBietLoki.java");
        dung(!loki.contains("gaySatThuong("),
                "Loki phat sinh damage rieng nhung chua vao ma tran May man");
        String hulk = doc("src/com/chicken/avg/ChickenCoCheHulk.java");
        dung(!hulk.contains("gaySatThuong("),
                "Hulk phat sinh damage rieng nhung chua vao ma tran May man");
        soQuyTac += 2;
    }

    private static void thuTu(
            String file,
            String methodNeedle,
            String truoc,
            String sau
    ) throws Exception {
        String thanHam = thanHam(doc(file), methodNeedle, null);
        int viTriTruoc = thanHam.indexOf(truoc);
        int viTriSau = thanHam.indexOf(sau);
        dung(viTriTruoc >= 0,
                "thieu moc May man trong " + file + " :: " + methodNeedle);
        dung(viTriSau >= 0,
                "thieu moc animation trong " + file + " :: " + methodNeedle);
        dung(viTriTruoc < viTriSau,
                "May man xuat hien sau animation trong "
                + file + " :: " + methodNeedle);
        soQuyTac++;
    }

    private static void khongChua(
            String file,
            String methodNeedle,
            String cam
    ) throws Exception {
        String thanHam = thanHam(doc(file), methodNeedle, null);
        dung(!thanHam.contains(cam),
                "callback quay lai May man sau animation trong "
                + file + " :: " + methodNeedle);
        soQuyTac++;
    }

    private static void batBuocChua(
            String file,
            String methodNeedle,
            String batBuoc
    ) throws Exception {
        String thanHam = thanHam(doc(file), methodNeedle, null);
        dung(thanHam.contains(batBuoc),
                "callback khong dung phien May man da chot trong "
                + file + " :: " + methodNeedle);
        soQuyTac++;
    }

    private static String thanHam(
            String source,
            String methodNeedle,
            String tenHam
    ) {
        int batDau = source.indexOf(methodNeedle);
        dung(batDau >= 0, "khong tim thay ham " + methodNeedle);
        int mo = source.indexOf('{', batDau);
        dung(mo >= 0, "ham khong co ngoac mo " + methodNeedle);
        int doSau = 0;
        boolean trongChuoi = false;
        boolean trongKyTu = false;
        boolean thoat = false;
        boolean commentDong = false;
        boolean commentKhoi = false;
        for (int i = mo; i < source.length(); i++) {
            char c = source.charAt(i);
            char tiep = i + 1 < source.length()
                    ? source.charAt(i + 1) : '\0';
            if (commentDong) {
                if (c == '\n') {
                    commentDong = false;
                }
                continue;
            }
            if (commentKhoi) {
                if (c == '*' && tiep == '/') {
                    commentKhoi = false;
                    i++;
                }
                continue;
            }
            if (!trongChuoi && !trongKyTu && c == '/' && tiep == '/') {
                commentDong = true;
                i++;
                continue;
            }
            if (!trongChuoi && !trongKyTu && c == '/' && tiep == '*') {
                commentKhoi = true;
                i++;
                continue;
            }
            if (trongChuoi || trongKyTu) {
                if (thoat) {
                    thoat = false;
                } else if (c == '\\') {
                    thoat = true;
                } else if (trongChuoi && c == '"') {
                    trongChuoi = false;
                } else if (trongKyTu && c == '\'') {
                    trongKyTu = false;
                }
                continue;
            }
            if (c == '"') {
                trongChuoi = true;
                continue;
            }
            if (c == '\'') {
                trongKyTu = true;
                continue;
            }
            if (c == '{') {
                doSau++;
            } else if (c == '}') {
                doSau--;
                if (doSau == 0) {
                    return source.substring(batDau, i + 1);
                }
            }
        }
        throw new AssertionError("ham khong dong ngoac " + methodNeedle);
    }

    private static String doc(String file) throws Exception {
        Path path = Path.of(file);
        dung(Files.isRegularFile(path), "thieu source de audit: " + file);
        return Files.readString(path);
    }

    private static int dem(String source, String mau) {
        int dem = 0;
        int viTri = 0;
        while ((viTri = source.indexOf(mau, viTri)) >= 0) {
            dem++;
            viTri += mau.length();
        }
        return dem;
    }

    private static void bang(int mongDoi, int thucTe, String thongBao) {
        if (mongDoi != thucTe) {
            throw new AssertionError(
                    thongBao + ": expected=" + mongDoi
                    + " actual=" + thucTe);
        }
    }

    private static void dung(boolean dieuKien, String thongBao) {
        if (!dieuKien) {
            throw new AssertionError(thongBao);
        }
    }
}
