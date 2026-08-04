# Bao mat tai khoan Chicken LT

## Trang thai hien tai

- Mat khau duoc bam bang bcrypt; mat khau cu duoc nang cap khi server khoi dong.
- Dang nhap sai bi gioi han theo IP va khoa tam tai khoan.
- Dang ky bat buoc username, mat khau, email va so dien thoai hop le.
- Ma khoi phuc phien la 256-bit ngau nhien, dung mot lan va het han sau 120 giay.
- OTP chi luu HMAC-SHA256 trong DB, het han sau 10 phut, toi da 5 lan thu.
- Dat lai mat khau thu hoi OTP con lai va ngat phien dang online.
- Doi mat khau thu hoi OTP va vo hieu hoa kha nang khoi phuc phien cu.
- SMTP, OTP pepper va TLS khong duoc luu trong source control.

## Khoi tao database

Voi database cu, chay mot lan:

```powershell
Get-Content .\sql\chicken_lt_auth_hardening.sql -Raw |
  C:\xampp\mysql\bin\mysql.exe -u chicken chicken3
```

Luon backup database truoc khi migration. File `sql/chicken3.sql` da gom schema
moi cho lan cai dat sach.

## Cau hinh email va OTP

Dat cac bien moi truong tren may chay server, khong ghi vao `config.conf`:

- `CHICKEN_SMTP_HOST`
- `CHICKEN_SMTP_PORT` (mac dinh 587)
- `CHICKEN_SMTP_USERNAME`
- `CHICKEN_SMTP_PASSWORD`
- `CHICKEN_SMTP_FROM`
- `CHICKEN_SMTP_STARTTLS` (mac dinh true)
- `CHICKEN_SMTP_SSL` (mac dinh false)
- `CHICKEN_OTP_PEPPER` (toi thieu 32 byte ngau nhien)

Neu dung Gmail tren may Windows de test, bat xac minh hai buoc tren tai khoan
Google, tao App Password rieng cho Chicken LT, sau do chay:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\configure-gmail-smtp.ps1
```

Script hoi Gmail va App Password trong terminal, khong in App Password va
khong dat bi mat vao source/Git. Khong dung mat khau dang nhap Gmail thong
thuong cho `CHICKEN_SMTP_PASSWORD`.

Production nen dat DB qua `CHICKEN_DB_HOST`, `CHICKEN_DB_USER`,
`CHICKEN_DB_PASSWORD`, `CHICKEN_DB_NAME`. Bien moi truong duoc uu tien hon
gia tri trong `config.conf`; khong commit mat khau production vao Git.

Thu gui email va xac minh email cua hai tai khoan quan tri truoc. Sau do moi bat:

```text
account-email-verification-required: true
account-admin-mfa-required: true
```

Neu bat hai co nay ma SMTP hoac pepper khong hop le, server se dung khoi dong
de tranh khoa tai khoan nua voi.

## TLS khi dua server len mang

Localhost hien de `network-tls-enabled: false` de client cu van test duoc. Khi
dua len Internet:

1. Dung domain tro toi server va certificate hop le cua domain do.
2. Dat PEM certificate tai `config/tls/server.crt` va private key tai
   `config/tls/server.key` (hoac doi hai duong dan trong `config.conf`).
3. Bat `network-tls-enabled: true` tren server.
4. Dat `MyMidlet.USE_TLS = true` trong client Unity va ket noi bang domain,
   khong dung IP neu certificate khong co IP trong SAN.

Client Unity dung kho tin cay certificate cua he dieu hanh; khong co callback
bo qua certificate loi. Client JAR cu chua ho tro TLS se khong ket noi vao cong
TLS nay, vi vay can nang cap JAR hoac tach cong local cu truoc khi bat production.

## Nguyen tac van hanh

- Khong log mat khau, OTP, session token, dia chi email day du hay private key.
- Khong dat pepper bang mat khau DB; moi moi truong dung mot pepper rieng.
- Backup DB va private key rieng, ma hoa backup, gioi han quyen doc.
- Khi nghi lo mat pepper/private key: thay khoa, thu hoi tat ca OTP/phien va
  cap certificate moi.
- Khong bat MFA admin cho den khi ca hai admin co email that da xac minh.
