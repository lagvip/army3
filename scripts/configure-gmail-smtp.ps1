$ErrorActionPreference = 'Stop'

Write-Host '[SMTP] Cau hinh Gmail gui OTP cho Chicken LT.'
$email = (Read-Host 'Nhap dia chi Gmail dung de gui OTP').Trim()
try {
    $mailAddress = [System.Net.Mail.MailAddress]::new($email)
    if ($mailAddress.Address -ne $email) {
        throw 'Dia chi email khong hop le.'
    }
} catch {
    throw 'Dia chi Gmail khong hop le.'
}

$securePassword = Read-Host 'Nhap App Password 16 ky tu cua Google (khong phai mat khau Gmail)' -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
try {
    $appPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    $appPassword = $appPassword.Replace(' ', '')
    if ($appPassword.Length -ne 16) {
        throw 'App Password phai co dung 16 ky tu sau khi bo khoang trang.'
    }

    [Environment]::SetEnvironmentVariable('CHICKEN_SMTP_HOST', 'smtp.gmail.com', 'User')
    [Environment]::SetEnvironmentVariable('CHICKEN_SMTP_PORT', '587', 'User')
    [Environment]::SetEnvironmentVariable('CHICKEN_SMTP_USERNAME', $email, 'User')
    [Environment]::SetEnvironmentVariable('CHICKEN_SMTP_PASSWORD', $appPassword, 'User')
    [Environment]::SetEnvironmentVariable('CHICKEN_SMTP_FROM', $email, 'User')
    [Environment]::SetEnvironmentVariable('CHICKEN_SMTP_STARTTLS', 'true', 'User')
    [Environment]::SetEnvironmentVariable('CHICKEN_SMTP_SSL', 'false', 'User')
} finally {
    if ($passwordPointer -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    }
    $appPassword = $null
    $securePassword = $null
}

$pepper = [Environment]::GetEnvironmentVariable('CHICKEN_OTP_PEPPER', 'User')
if ([string]::IsNullOrWhiteSpace($pepper) -or [Text.Encoding]::UTF8.GetByteCount($pepper) -lt 32) {
    $buffer = New-Object byte[] 48
    $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
    try {
        $rng.GetBytes($buffer)
    } finally {
        $rng.Dispose()
    }
    $pepper = [Convert]::ToBase64String($buffer)
    [Environment]::SetEnvironmentVariable('CHICKEN_OTP_PEPPER', $pepper, 'User')
}

Write-Host '[SMTP] Da luu cau hinh vao Windows User Environment Variables.'
Write-Host '[SMTP] Hay tat server cu va chay lai scripts/run-dev.ps1.'
Write-Host '[SMTP] Khi log co dong "[BAO_MAT] SMTP da san sang", hay thu Quen mat khau.'
