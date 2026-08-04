param(
    [string]$DataImagePath = 'C:\Users\Admin\AppData\LocalLow\00bobrp0_utf\ Mobi Army3 3_9_1_1\dataImage',
    [string]$Scale1AtlasDirectory = 'D:\Chicken_lt\Chicken_lt\build\staging\may-tinh-unity-project-20260802\ExportedProject\Assets\Resources\res\x1',
    [string]$Scale2AtlasDirectory = 'C:\Users\Admin\AppData\LocalLow\00bobrp0_utf\ Mobi Army3 3_9_1_1',
    [string]$OutputRoot = (Join-Path $PSScriptRoot '..\build\staging\may-tinh-small-atlases')
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

function Read-UnsignedInt16BigEndian {
    param([IO.BinaryReader]$Reader)

    $high = $Reader.ReadByte()
    $low = $Reader.ReadByte()
    return (($high * 256) + $low)
}

function Read-Int16BigEndian {
    param([IO.BinaryReader]$Reader)

    $value = Read-UnsignedInt16BigEndian -Reader $Reader
    if ($value -ge 0x8000) {
        return $value - 0x10000
    }
    return $value
}

function Resolve-AtlasFile {
    param(
        [string]$Directory,
        [int]$Index
    )

    $withExtension = Join-Path $Directory "Big$Index.png"
    if (Test-Path -LiteralPath $withExtension -PathType Leaf) {
        return $withExtension
    }

    $withoutExtension = Join-Path $Directory "Big$Index"
    if (Test-Path -LiteralPath $withoutExtension -PathType Leaf) {
        return $withoutExtension
    }

    throw "Khong tim thay Big$Index trong $Directory"
}

function Export-SmallAtlasScale {
    param(
        [int]$Scale,
        [string]$AtlasDirectory,
        [object[]]$Entries,
        [string]$DestinationRoot
    )

    $destination = Join-Path $DestinationRoot $Scale
    New-Item -ItemType Directory -Force -Path $destination | Out-Null

    $atlases = @()
    try {
        foreach ($index in 0..4) {
            $atlasPath = Resolve-AtlasFile -Directory $AtlasDirectory -Index $index
            $atlases += [Drawing.Bitmap]::FromFile($atlasPath)
        }

        $results = [System.Collections.Generic.List[object]]::new()
        foreach ($entry in $Entries) {
            $x = $entry.X * $Scale
            $y = $entry.Y * $Scale
            $width = $entry.Width * $Scale
            $height = $entry.Height * $Scale
            $status = 'extracted'
            $output = ''

            if ($entry.Atlas -lt 0 -or $entry.Atlas -ge $atlases.Count -or
                $x -lt 0 -or $y -lt 0 -or $width -le 0 -or $height -le 0 -or
                $x + $width -gt $atlases[$entry.Atlas].Width -or
                $y + $height -gt $atlases[$entry.Atlas].Height) {
                $status = 'invalid-region'
            }
            else {
                $output = Join-Path $destination "Small$($entry.Id).png"
                $rectangle = [Drawing.Rectangle]::new($x, $y, $width, $height)
                $sprite = $atlases[$entry.Atlas].Clone(
                    $rectangle,
                    [Drawing.Imaging.PixelFormat]::Format32bppArgb
                )
                try {
                    $sprite.Save($output, [Drawing.Imaging.ImageFormat]::Png)
                }
                finally {
                    $sprite.Dispose()
                }
            }

            $results.Add([pscustomobject]@{
                Scale = $Scale
                Id = $entry.Id
                Atlas = $entry.Atlas
                X = $x
                Y = $y
                Width = $width
                Height = $height
                Output = $output
                Status = $status
            })
        }
        return $results
    }
    finally {
        foreach ($atlas in $atlases) {
            $atlas.Dispose()
        }
    }
}

foreach ($requiredPath in @($DataImagePath, $Scale1AtlasDirectory, $Scale2AtlasDirectory)) {
    if (-not (Test-Path -LiteralPath $requiredPath)) {
        throw "Khong tim thay nguon: $requiredPath"
    }
}

$stream = [IO.File]::OpenRead($DataImagePath)
$reader = [IO.BinaryReader]::new($stream)
try {
    $count = Read-UnsignedInt16BigEndian -Reader $reader
    $entries = [System.Collections.Generic.List[object]]::new()
    foreach ($id in 0..($count - 1)) {
        $entries.Add([pscustomobject]@{
            Id = $id
            Atlas = [int]$reader.ReadByte()
            X = Read-Int16BigEndian -Reader $reader
            Y = Read-Int16BigEndian -Reader $reader
            Width = Read-Int16BigEndian -Reader $reader
            Height = Read-Int16BigEndian -Reader $reader
        })
    }

    if ($stream.Position -ne $stream.Length) {
        throw "dataImage con $($stream.Length - $stream.Position) byte khong duoc doc"
    }
}
finally {
    $reader.Dispose()
    $stream.Dispose()
}

$outputPath = [IO.Path]::GetFullPath($OutputRoot)
New-Item -ItemType Directory -Force -Path $outputPath | Out-Null

$manifest = @()
$manifest += Export-SmallAtlasScale -Scale 1 -AtlasDirectory $Scale1AtlasDirectory -Entries $entries -DestinationRoot $outputPath
$manifest += Export-SmallAtlasScale -Scale 2 -AtlasDirectory $Scale2AtlasDirectory -Entries $entries -DestinationRoot $outputPath
$manifestPath = Join-Path $outputPath 'manifest.csv'
$manifest | Export-Csv -LiteralPath $manifestPath -NoTypeInformation -Encoding UTF8

$valid = @($manifest | Where-Object Status -eq 'extracted')
$invalid = @($manifest | Where-Object Status -ne 'extracted')
Write-Host "dataImage entries: $count"
Write-Host "PNG files extracted: $($valid.Count)"
Write-Host "Invalid regions: $($invalid.Count)"
Write-Host "Output: $outputPath"
Write-Host "Manifest: $manifestPath"
