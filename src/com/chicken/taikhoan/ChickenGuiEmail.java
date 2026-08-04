package com.chicken.taikhoan;

import com.chicken.loi.ChickenQuanLyMayChu;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Gui email bao mat; moi bi mat chi duoc doc tu bien moi truong. */
public final class ChickenGuiEmail {
    private static final ExecutorService BO_GUI =
            Executors.newFixedThreadPool(2, runnable -> {
                Thread thread = new Thread(runnable, "account-email");
                thread.setDaemon(true);
                return thread;
            });

    private static Session phien;
    private static String diaChiGui;
    private static boolean sanSang;

    private ChickenGuiEmail() {
    }

    public static synchronized void khoiTao(boolean batBuoc) {
        String mayChu = bienMoiTruong("CHICKEN_SMTP_HOST");
        String taiKhoan = bienMoiTruong("CHICKEN_SMTP_USERNAME");
        String matKhau = bienMoiTruong("CHICKEN_SMTP_PASSWORD");
        diaChiGui = bienMoiTruong("CHICKEN_SMTP_FROM");
        if (mayChu == null || taiKhoan == null
                || matKhau == null || diaChiGui == null) {
            sanSang = false;
            if (batBuoc) {
                throw new IllegalStateException(
                        "Thieu cau hinh SMTP trong bien moi truong");
            }
            ChickenQuanLyMayChu.log(
                    "[BAO_MAT] SMTP chua cau hinh; OTP email dang tat");
            return;
        }

        int cong = soNguyenMoiTruong("CHICKEN_SMTP_PORT", 587);
        boolean startTls = boolMoiTruong(
                "CHICKEN_SMTP_STARTTLS", true);
        boolean ssl = boolMoiTruong("CHICKEN_SMTP_SSL", false);
        Properties thuocTinh = new Properties();
        thuocTinh.put("mail.smtp.host", mayChu);
        thuocTinh.put("mail.smtp.port", Integer.toString(cong));
        thuocTinh.put("mail.smtp.auth", "true");
        thuocTinh.put("mail.smtp.starttls.enable",
                Boolean.toString(startTls));
        thuocTinh.put("mail.smtp.starttls.required",
                Boolean.toString(startTls));
        thuocTinh.put("mail.smtp.ssl.enable", Boolean.toString(ssl));
        thuocTinh.put("mail.smtp.connectiontimeout", "10000");
        thuocTinh.put("mail.smtp.timeout", "10000");
        thuocTinh.put("mail.smtp.writetimeout", "10000");
        phien = Session.getInstance(thuocTinh, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(taiKhoan, matKhau);
            }
        });
        sanSang = true;
        ChickenQuanLyMayChu.log("[BAO_MAT] SMTP da san sang");
    }

    public static boolean sanSang() {
        return sanSang;
    }

    public static CompletableFuture<Boolean> guiMa(
            int maTaiKhoan,
            String nguoiNhan,
            String tieuDe,
            String noiDung
    ) {
        if (!sanSang || nguoiNhan == null) {
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                MimeMessage thu = new MimeMessage(phien);
                thu.setFrom(new InternetAddress(diaChiGui));
                thu.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(nguoiNhan));
                thu.setSubject(tieuDe, StandardCharsets.UTF_8.name());
                thu.setText(noiDung, StandardCharsets.UTF_8.name());
                Transport.send(thu);
                ChickenQuanLyMayChu.log(
                        "[BAO_MAT] Gui OTP thanh cong account_id="
                        + maTaiKhoan);
                return true;
            } catch (MessagingException ex) {
                ChickenQuanLyMayChu.log(
                        "[BAO_MAT] Gui OTP that bai account_id="
                        + maTaiKhoan + " loi="
                        + ex.getClass().getSimpleName());
                return false;
            }
        }, BO_GUI);
    }

    public static void dong() {
        BO_GUI.shutdown();
    }

    private static String bienMoiTruong(String ten) {
        String giaTri = System.getenv(ten);
        if (giaTri == null || giaTri.isBlank()) {
            return null;
        }
        return giaTri.trim();
    }

    private static int soNguyenMoiTruong(String ten, int macDinh) {
        String giaTri = bienMoiTruong(ten);
        if (giaTri == null) {
            return macDinh;
        }
        try {
            return Integer.parseInt(giaTri);
        } catch (NumberFormatException ex) {
            return macDinh;
        }
    }

    private static boolean boolMoiTruong(String ten, boolean macDinh) {
        String giaTri = bienMoiTruong(ten);
        return giaTri == null ? macDinh : Boolean.parseBoolean(giaTri);
    }
}
