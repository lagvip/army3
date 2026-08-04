param(
    [string]$CacheRoot = 'C:\Users\Admin\AppData\LocalLow\00bobrp0_utf\ Mobi Army3 3_9_1_1',
    [string]$OutputRoot = (Join-Path $PSScriptRoot '..\build\staging\unity-small-cache')
)

$ErrorActionPreference = 'Stop'

if (-not (Test-Path -LiteralPath $CacheRoot -PathType Container)) {
    throw "Khong tim thay cache client: $CacheRoot"
}

$outputPath = [IO.Path]::GetFullPath($OutputRoot)
New-Item -ItemType Directory -Force -Path $outputPath | Out-Null

$pngSignature = [byte[]](0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
$manifest = [System.Collections.Generic.List[object]]::new()

function Test-PngSignature {
    param([string]$Path)

    $stream = [IO.File]::OpenRead($Path)
    try {
        if ($stream.Length -lt $pngSignature.Length) {
            return $false
        }

        foreach ($expected in $pngSignature) {
            if ($stream.ReadByte() -ne $expected) {
                return $false
            }
        }
        return $true
    }
    finally {
        $stream.Dispose()
    }
}

$cacheFiles = @(Get-ChildItem -LiteralPath $CacheRoot -Force -File | Where-Object {
    $_.Name -match '^(?<Scale>[1-4])Small(?<Id>\d+)$'
})

foreach ($file in $cacheFiles) {
    $match = [regex]::Match($file.Name, '^(?<Scale>[1-4])Small(?<Id>\d+)$')
    $scale = [int]$match.Groups['Scale'].Value
    $id = [int]$match.Groups['Id'].Value

    if (-not (Test-PngSignature -Path $file.FullName)) {
        $manifest.Add([pscustomobject]@{
            Scale = $scale
            Id = $id
            Source = $file.FullName
            Output = ''
            Bytes = $file.Length
            Sha256 = ''
            Status = 'not-png'
        })
        continue
    }

    $scaleDirectory = Join-Path $outputPath $scale
    New-Item -ItemType Directory -Force -Path $scaleDirectory | Out-Null
    $destination = Join-Path $scaleDirectory "Small$id.png"
    Copy-Item -LiteralPath $file.FullName -Destination $destination -Force

    $manifest.Add([pscustomobject]@{
        Scale = $scale
        Id = $id
        Source = $file.FullName
        Output = $destination
        Bytes = $file.Length
        Sha256 = (Get-FileHash -LiteralPath $destination -Algorithm SHA256).Hash
        Status = 'extracted'
    })
}

$manifestPath = Join-Path $outputPath 'manifest.csv'
$manifest | Sort-Object Scale, Id | Export-Csv -LiteralPath $manifestPath -NoTypeInformation -Encoding UTF8

$extracted = @($manifest | Where-Object Status -eq 'extracted')
Write-Host "Cache files found: $($cacheFiles.Count)"
Write-Host "PNG files extracted: $($extracted.Count)"
Write-Host "Output: $outputPath"
Write-Host "Manifest: $manifestPath"

