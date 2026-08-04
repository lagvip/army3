param(
    [string]$PrimaryAssetRoot = 'D:\Chicken_lt\Chicken_lt\build\staging\may-tinh-embedded-assets-20260802\Assets',
    [string]$UnityResourceRoot = 'D:\Chicken_lt\Chicken_lt\build\staging\may-tinh-unity-project-20260802\ExportedProject\Assets\Resources',
    [string]$RuntimeCacheRoot = 'C:\Users\Admin\AppData\LocalLow\00bobrp0_utf\ Mobi Army3 3_9_1_1',
    [string]$SplitSmallRoot = 'D:\Chicken_lt\Chicken_lt\build\staging\may-tinh-small-atlases',
    [string]$OutputRoot = 'D:\Chicken_lt\Chicken_lt\build\staging\may-tinh-complete-res'
)

$ErrorActionPreference = 'Stop'

function Copy-Tree {
    param(
        [string]$Source,
        [string]$Destination
    )

    if (-not (Test-Path -LiteralPath $Source -PathType Container)) {
        throw "Khong tim thay nguon: $Source"
    }
    New-Item -ItemType Directory -Force -Path $Destination | Out-Null
    Get-ChildItem -LiteralPath $Source -Force | ForEach-Object {
        Copy-Item -LiteralPath $_.FullName -Destination $Destination -Recurse -Force
    }
}

$outputPath = [IO.Path]::GetFullPath($OutputRoot)
New-Item -ItemType Directory -Force -Path $outputPath | Out-Null

# Primary Content giữ toàn bộ texture/audio/font/text/mesh/shader AssetRipper đọc được.
Copy-Tree -Source $PrimaryAssetRoot -Destination (Join-Path $outputPath '01-primary-assets')

# Unity Project export giữ lại đường dẫn Resources gốc, ví dụ res/x1/item/... thay vì tên phẳng.
Copy-Tree -Source $UnityResourceRoot -Destination (Join-Path $outputPath '02-unity-resources-by-path')

# Cache runtime gồm Big*, background b*, clan, thời tiết và các file dữ liệu nhận từ server.
Copy-Tree -Source $RuntimeCacheRoot -Destination (Join-Path $outputPath '03-runtime-cache')

# Các Small<ID> đã cắt từ Big0-Big4 theo dataImage.
Copy-Tree -Source $SplitSmallRoot -Destination (Join-Path $outputPath '04-small-from-atlas')

$manifest = foreach ($file in Get-ChildItem -LiteralPath $outputPath -Recurse -File | Where-Object Name -ne 'manifest.csv') {
    $relative = $file.FullName.Substring($outputPath.Length).TrimStart('\') -replace '\\', '/'
    $section = ($relative -split '/')[0]
    [pscustomobject]@{
        Section = $section
        RelativePath = $relative
        Bytes = $file.Length
        Sha256 = (Get-FileHash -LiteralPath $file.FullName -Algorithm SHA256).Hash
    }
}

$manifestPath = Join-Path $outputPath 'manifest.csv'
$manifest | Sort-Object Section, RelativePath | Export-Csv -LiteralPath $manifestPath -NoTypeInformation -Encoding UTF8

$summary = $manifest | Group-Object Section | ForEach-Object {
    [pscustomobject]@{
        Section = $_.Name
        Files = $_.Count
        Bytes = ($_.Group | Measure-Object Bytes -Sum).Sum
    }
}

$summary | Format-Table -AutoSize
Write-Host "Total files: $($manifest.Count)"
Write-Host "Output: $outputPath"
Write-Host "Manifest: $manifestPath"
