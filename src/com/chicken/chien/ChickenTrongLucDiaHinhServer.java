package com.chicken.chien;

import com.chicken.bando.ChickenQuanLyBanDo;
import java.util.function.Predicate;

/**
 * Đồng bộ trọng lực server sau khi mặt nạ địa hình bị thay đổi.
 *
 * <p>Client Army 3 cho nhân vật rơi theo đúng cột X ở giữa chân và dừng tại
 * pixel va chạm đầu tiên. Server phải lưu cùng Y này; nếu không, packet mở lượt
 * sau sẽ gửi lại Y cũ và kéo nhân vật vừa rơi ngược lên.</p>
 */
public final class ChickenTrongLucDiaHinhServer {

    private ChickenTrongLucDiaHinhServer() {
    }

    /**
     * Hạ các chiến binh không bay xuống pixel nền đầu tiên bên dưới.
     *
     * <p>Không tự xử chết khi không còn nền. Việc rơi vực có luật kết thúc và
     * packet máu riêng theo từng chế độ; hàm này chỉ sửa tọa độ khi có một nền
     * xác định giống client.</p>
     *
     * @return số chiến binh có Y được cập nhật
     */
    public static int dongBoYSauPhaDiaHinh(
            ChickenQuanLyBanDo banDo,
            ChickenChienBinh[] chienBinhs,
            Predicate<ChickenChienBinh> boQuaTrongLuc
    ) {
        if (banDo == null || chienBinhs == null) {
            return 0;
        }
        Predicate<ChickenChienBinh> boQua = boQuaTrongLuc == null
                ? chienBinh -> false
                : boQuaTrongLuc;
        int soCapNhat = 0;
        for (ChickenChienBinh chienBinh : chienBinhs) {
            if (chienBinh == null || chienBinh.chet || chienBinh.hp <= 0
                    || boQua.test(chienBinh)) {
                continue;
            }
            short yMoi = timNenDauTienBenDuoi(
                    banDo, chienBinh.x, chienBinh.y);
            if (yMoi == Short.MIN_VALUE || yMoi <= chienBinh.y) {
                continue;
            }
            short yCu = chienBinh.y;
            chienBinh.y = yMoi;
            soCapNhat++;
            System.out.println("[PHYSICS][SERVER_FALL_SYNC] slot="
                    + (chienBinh.chiSo & 0xFF)
                    + " ten=" + chienBinh.ten
                    + " x=" + chienBinh.x
                    + " yCu=" + yCu
                    + " yMoi=" + yMoi);
        }
        return soCapNhat;
    }

    /**
     * Khớp CPlayer.fall(): kiểm tra chính điểm (x, y), rồi tăng Y từng pixel.
     */
    public static short timNenDauTienBenDuoi(
            ChickenQuanLyBanDo banDo,
            short x,
            short yHienTai
    ) {
        if (banDo == null || x < 0 || x >= banDo.getWidth()) {
            return Short.MIN_VALUE;
        }
        int batDauY = Math.max(0, yHienTai);
        for (int y = batDauY; y < banDo.getHeight(); y++) {
            if (banDo.coVaCham(x, (short) y)) {
                return (short) y;
            }
        }
        return Short.MIN_VALUE;
    }
}
