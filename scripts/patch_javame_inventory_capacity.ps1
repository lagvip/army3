param(
    [string]$ClientDirectory = (Join-Path $PSScriptRoot '..\client')
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem

$oldPattern = [byte[]](0x3E, 0x10, 0x14, 0xBD)
$newPattern = [byte[]](0x3E, 0x15, 0x03, 0xBD)

function Find-PatternOffsets([byte[]]$bytes, [byte[]]$pattern) {
    $offsets = [System.Collections.Generic.List[int]]::new()
    for ($i = 0; $i -le $bytes.Length - $pattern.Length; $i++) {
        $matches = $true
        for ($j = 0; $j -lt $pattern.Length; $j++) {
            if ($bytes[$i + $j] -ne $pattern[$j]) {
                $matches = $false
                break
            }
        }
        if ($matches) {
            $offsets.Add($i)
        }
    }
    return $offsets
}

$jars = Get-ChildItem -LiteralPath $ClientDirectory -Filter '*.jar' -File
if ($jars.Count -eq 0) {
    throw "Khong tim thay client JavaME trong: $ClientDirectory"
}

foreach ($jar in $jars) {
    $backupPath = "$($jar.FullName).before-inventory-100.bak"
    if (-not (Test-Path -LiteralPath $backupPath)) {
        Copy-Item -LiteralPath $jar.FullName -Destination $backupPath
    }

    $zip = [System.IO.Compression.ZipFile]::Open(
        $jar.FullName,
        [System.IO.Compression.ZipArchiveMode]::Update
    )
    try {
        $entry = $zip.GetEntry('chibikun/MessageHandler.class')
        if ($null -eq $entry) {
            throw "Khong tim thay chibikun/MessageHandler.class trong $($jar.Name)"
        }

        $memory = [System.IO.MemoryStream]::new()
        $input = $entry.Open()
        try {
            $input.CopyTo($memory)
        }
        finally {
            $input.Dispose()
        }
        $bytes = $memory.ToArray()

        $oldOffsets = @(Find-PatternOffsets $bytes $oldPattern)
        $newOffsets = @(Find-PatternOffsets $bytes $newPattern)
        if ($oldOffsets.Count -eq 0 -and $newOffsets.Count -eq 1) {
            Write-Host "$($jar.Name): da dung kich thuoc tui do do server gui."
            continue
        }
        if ($oldOffsets.Count -ne 1 -or $newOffsets.Count -ne 0) {
            throw "$($jar.Name): mau bytecode khong dung duy nhat; dung de tranh va nham."
        }

        $offset = $oldOffsets[0]
        $bytes[$offset + 1] = 0x15 # iload
        $bytes[$offset + 2] = 0x03 # local bien chua so o server gui

        $output = $entry.Open()
        try {
            $output.SetLength(0)
            $output.Write($bytes, 0, $bytes.Length)
        }
        finally {
            $output.Dispose()
        }
        Write-Host "$($jar.Name): da va tui do theo kich thuoc server (100)."
    }
    finally {
        $zip.Dispose()
    }
}
