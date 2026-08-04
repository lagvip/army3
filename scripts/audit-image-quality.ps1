param(
    [string]$Root = (Join-Path $PSScriptRoot '..\res'),
    [string]$OutputDirectory = (Join-Path $PSScriptRoot '..\build\reports\image-quality')
)

$ErrorActionPreference = 'Stop'
$Root = (Resolve-Path -LiteralPath $Root).Path
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null

Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @'
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Runtime.InteropServices;

public sealed class ChickenImageMetric
{
    public string Path;
    public string Error;
    public int Width;
    public int Height;
    public long PixelCount;
    public long VisiblePixels;
    public long PartialAlphaPixels;
    public int BoundsWidth;
    public int BoundsHeight;
    public int UniqueColors;
    public double Gradient;
    public double Laplacian;
}

public static class ChickenImageScanner
{
    public static ChickenImageMetric Scan(string path)
    {
        var result = new ChickenImageMetric { Path = path, Error = "" };
        try
        {
            using (var source = new Bitmap(path))
            using (var bitmap = new Bitmap(source.Width, source.Height, PixelFormat.Format32bppArgb))
            {
                using (var graphics = Graphics.FromImage(bitmap))
                {
                    graphics.CompositingMode = System.Drawing.Drawing2D.CompositingMode.SourceCopy;
                    graphics.DrawImageUnscaled(source, 0, 0);
                }

                result.Width = bitmap.Width;
                result.Height = bitmap.Height;
                result.PixelCount = (long)bitmap.Width * bitmap.Height;
                var rect = new Rectangle(0, 0, bitmap.Width, bitmap.Height);
                var bits = bitmap.LockBits(rect, ImageLockMode.ReadOnly, PixelFormat.Format32bppArgb);
                try
                {
                    int stride = Math.Abs(bits.Stride);
                    byte[] data = new byte[stride * bitmap.Height];
                    Marshal.Copy(bits.Scan0, data, 0, data.Length);
                    var colors = new HashSet<int>();
                    int minX = bitmap.Width, minY = bitmap.Height, maxX = -1, maxY = -1;
                    double gradient = 0;
                    long gradientSamples = 0;
                    double laplacian = 0;
                    long laplacianSamples = 0;

                    Func<int, int, int> offset = (x, y) => y * stride + x * 4;
                    Func<int, int, int> luminance = (x, y) =>
                    {
                        int i = offset(x, y);
                        return (data[i + 2] * 77 + data[i + 1] * 150 + data[i] * 29) >> 8;
                    };
                    Func<int, int, int> alpha = (x, y) => data[offset(x, y) + 3];

                    for (int y = 0; y < bitmap.Height; y++)
                    {
                        for (int x = 0; x < bitmap.Width; x++)
                        {
                            int i = offset(x, y);
                            int a = data[i + 3];
                            if (a == 0) continue;
                            result.VisiblePixels++;
                            if (a < 255) result.PartialAlphaPixels++;
                            if (x < minX) minX = x;
                            if (x > maxX) maxX = x;
                            if (y < minY) minY = y;
                            if (y > maxY) maxY = y;
                            if (colors.Count < 65536)
                            {
                                int argb = (a << 24) | (data[i + 2] << 16) | (data[i + 1] << 8) | data[i];
                                colors.Add(argb);
                            }

                            int lum = luminance(x, y);
                            if (x + 1 < bitmap.Width && alpha(x + 1, y) > 0)
                            {
                                gradient += Math.Abs(lum - luminance(x + 1, y));
                                gradientSamples++;
                            }
                            if (y + 1 < bitmap.Height && alpha(x, y + 1) > 0)
                            {
                                gradient += Math.Abs(lum - luminance(x, y + 1));
                                gradientSamples++;
                            }
                            if (x > 0 && x + 1 < bitmap.Width && y > 0 && y + 1 < bitmap.Height &&
                                alpha(x - 1, y) > 0 && alpha(x + 1, y) > 0 &&
                                alpha(x, y - 1) > 0 && alpha(x, y + 1) > 0)
                            {
                                int value = Math.Abs(4 * lum - luminance(x - 1, y) - luminance(x + 1, y)
                                    - luminance(x, y - 1) - luminance(x, y + 1));
                                laplacian += value;
                                laplacianSamples++;
                            }
                        }
                    }

                    result.UniqueColors = colors.Count;
                    result.BoundsWidth = maxX >= minX ? maxX - minX + 1 : 0;
                    result.BoundsHeight = maxY >= minY ? maxY - minY + 1 : 0;
                    result.Gradient = gradientSamples > 0 ? gradient / gradientSamples : 0;
                    result.Laplacian = laplacianSamples > 0 ? laplacian / laplacianSamples : 0;
                }
                finally
                {
                    bitmap.UnlockBits(bits);
                }
            }
        }
        catch (Exception ex)
        {
            result.Error = ex.GetType().Name + ": " + ex.Message;
        }
        return result;
    }
}
'@

$pngFiles = @(Get-ChildItem -LiteralPath $Root -Recurse -File -Filter '*.png' | Sort-Object FullName)
$metrics = [System.Collections.Generic.List[object]]::new()
$index = 0
foreach ($file in $pngFiles) {
    $index++
    if (($index % 500) -eq 0) {
        Write-Host "Scanned $index / $($pngFiles.Count)"
    }
    $metric = [ChickenImageScanner]::Scan($file.FullName)
    $relative = $file.FullName.Substring($Root.Length).TrimStart('\') -replace '\\', '/'
    $partialAlphaPercent = if ($metric.VisiblePixels -gt 0) {
        [math]::Round(100 * $metric.PartialAlphaPixels / [double]$metric.VisiblePixels, 3)
    } else { 0 }
    $visiblePercent = if ($metric.PixelCount -gt 0) {
        [math]::Round(100 * $metric.VisiblePixels / [double]$metric.PixelCount, 3)
    } else { 0 }
    $metrics.Add([pscustomobject]@{
        RelativePath = $relative
        Width = $metric.Width
        Height = $metric.Height
        Pixels = $metric.PixelCount
        VisiblePixels = $metric.VisiblePixels
        VisiblePercent = $visiblePercent
        PartialAlphaPercent = $partialAlphaPercent
        BoundsWidth = $metric.BoundsWidth
        BoundsHeight = $metric.BoundsHeight
        UniqueColors = $metric.UniqueColors
        Gradient = [math]::Round($metric.Gradient, 3)
        Laplacian = [math]::Round($metric.Laplacian, 3)
        Error = $metric.Error
    })
}

$allCsv = Join-Path $OutputDirectory 'all-images.csv'
$metrics | Export-Csv -LiteralPath $allCsv -NoTypeInformation -Encoding UTF8

$itemPattern = '^icon/item/([1-4])/Small(\d+)\.png$'
$itemRows = @($metrics | Where-Object { $_.RelativePath -match $itemPattern } | ForEach-Object {
    [pscustomobject]@{
        Scale = [int]$Matches[1]
        Id = [int]$Matches[2]
        Metric = $_
    }
})

$itemNamesByIcon = @{}
$sqlPath = Join-Path (Split-Path -Parent $Root) 'sql\chicken3.sql'
if (Test-Path -LiteralPath $sqlPath) {
    foreach ($line in Get-Content -LiteralPath $sqlPath -Encoding UTF8) {
        $match = [regex]::Match(
            $line,
            "^\((?<itemId>\d+), '(?<name>(?:''|[^'])*)',\s*-?\d+,\s*-?\d+,\s*'(?:''|[^'])*',\s*-?\d+,\s*(?<icon>\d+),"
        )
        if (-not $match.Success) { continue }
        $icon = [int]$match.Groups['icon'].Value
        $label = $match.Groups['itemId'].Value + ':' + ($match.Groups['name'].Value -replace "''", "'")
        if (-not $itemNamesByIcon.ContainsKey($icon)) {
            $itemNamesByIcon[$icon] = [System.Collections.Generic.List[string]]::new()
        }
        $itemNamesByIcon[$icon].Add($label)
    }
}

function Get-ScaleEvidence {
    param($Base, $Current, [int]$Scale)
    if (-not $Base -or -not $Current -or [double]$Base.Gradient -le 0) { return $null }
    $colorGain = [double]$Current.UniqueColors / [math]::Max(1, [double]$Base.UniqueColors)
    $detailGain = $Scale * [double]$Current.Gradient / [math]::Max(0.001, [double]$Base.Gradient)
    $isLowSourceCopy = [double]$Current.VisiblePixels -ge 16 -and
        [double]$Current.PartialAlphaPercent -lt 0.5 -and
        $colorGain -le 2.5 -and
        $detailGain -le 1.25
    return [pscustomobject]@{
        ColorGain = [math]::Round($colorGain, 3)
        DetailGain = [math]::Round($detailGain, 3)
        IsLowSourceCopy = $isLowSourceCopy
    }
}

$issues = [System.Collections.Generic.List[object]]::new()
$familyQuality = [System.Collections.Generic.List[object]]::new()
foreach ($metric in $metrics) {
    if ($metric.Error) {
        $issues.Add([pscustomobject]@{ Severity = 'critical'; Type = 'unreadable'; Id = ''; Scale = ''; Path = $metric.RelativePath; Detail = $metric.Error })
    } elseif ($metric.VisiblePixels -eq 0) {
        $issues.Add([pscustomobject]@{ Severity = 'high'; Type = 'fully-transparent'; Id = ''; Scale = ''; Path = $metric.RelativePath; Detail = 'No visible pixel' })
    } elseif ($metric.Width -le 1 -or $metric.Height -le 1) {
        $issues.Add([pscustomobject]@{ Severity = 'medium'; Type = 'tiny'; Id = ''; Scale = ''; Path = $metric.RelativePath; Detail = "$($metric.Width)x$($metric.Height)" })
    }
}

foreach ($group in ($itemRows | Group-Object Id)) {
    $byScale = @{}
    foreach ($row in $group.Group) { $byScale[$row.Scale] = $row.Metric }
    $base = $byScale[1]
    foreach ($scale in 1..4) {
        if (-not $byScale.ContainsKey($scale)) {
            $issues.Add([pscustomobject]@{ Severity = 'high'; Type = 'missing-scale'; Id = $group.Name; Scale = $scale; Path = "icon/item/$scale/Small$($group.Name).png"; Detail = 'Missing counterpart' })
            continue
        }
        if ($base -and $scale -gt 1) {
            $current = $byScale[$scale]
            $expectedWidth = $base.Width * $scale
            $expectedHeight = $base.Height * $scale
            $widthDelta = [math]::Abs($current.Width - $expectedWidth)
            $heightDelta = [math]::Abs($current.Height - $expectedHeight)
            $relativeDelta = [math]::Max(
                $widthDelta / [double][math]::Max(1, $expectedWidth),
                $heightDelta / [double][math]::Max(1, $expectedHeight)
            )
            if ($relativeDelta -gt 0.10) {
                $issues.Add([pscustomobject]@{ Severity = 'high'; Type = 'dimension-mismatch'; Id = $group.Name; Scale = $scale; Path = $current.RelativePath; Detail = "Expected ${expectedWidth}x${expectedHeight}, actual $($current.Width)x$($current.Height)" })
            } elseif ($relativeDelta -gt 0.03 -or $widthDelta -gt 3 -or $heightDelta -gt 3) {
                $issues.Add([pscustomobject]@{ Severity = 'review'; Type = 'dimension-variance'; Id = $group.Name; Scale = $scale; Path = $current.RelativePath; Detail = "Expected ${expectedWidth}x${expectedHeight}, actual $($current.Width)x$($current.Height)" })
            }
        }
    }

    if ($base -and $base.VisiblePixels -ge 16) {
        $x2Evidence = if ($byScale.ContainsKey(2)) { Get-ScaleEvidence $base $byScale[2] 2 } else { $null }
        $x3Evidence = if ($byScale.ContainsKey(3)) { Get-ScaleEvidence $base $byScale[3] 3 } else { $null }
        $classification = 'ok'
        if ($x2Evidence -and $x3Evidence -and $x2Evidence.IsLowSourceCopy -and $x3Evidence.IsLowSourceCopy) {
            $classification = 'high-confidence-low-resolution-source'
        } elseif (($x2Evidence -and $x2Evidence.IsLowSourceCopy) -or ($x3Evidence -and $x3Evidence.IsLowSourceCopy)) {
            $classification = 'review-low-resolution-source'
        }

        if ($classification -ne 'ok') {
            $itemNames = if ($itemNamesByIcon.ContainsKey([int]$group.Name)) {
                $itemNamesByIcon[[int]$group.Name] -join '; '
            } else { '' }
            $familyQuality.Add([pscustomobject]@{
                Severity = if ($classification -eq 'high-confidence-low-resolution-source') { 'high' } else { 'review' }
                Type = $classification
                Id = [int]$group.Name
                Items = $itemNames
                Scale1 = if ($byScale[1]) { $byScale[1].RelativePath } else { '' }
                Scale2 = if ($byScale[2]) { $byScale[2].RelativePath } else { '' }
                Scale3 = if ($byScale[3]) { $byScale[3].RelativePath } else { '' }
                Scale4 = if ($byScale[4]) { $byScale[4].RelativePath } else { '' }
                X2ColorGain = if ($x2Evidence) { $x2Evidence.ColorGain } else { '' }
                X2DetailGain = if ($x2Evidence) { $x2Evidence.DetailGain } else { '' }
                X3ColorGain = if ($x3Evidence) { $x3Evidence.ColorGain } else { '' }
                X3DetailGain = if ($x3Evidence) { $x3Evidence.DetailGain } else { '' }
                Reason = 'The x2/x3 variants add almost no real detail over x1; likely enlarged from a low-resolution source.'
            })
        }
    }
}

$issuesCsv = Join-Path $OutputDirectory 'issues.csv'
$issues | Sort-Object Severity, Type, Path | Export-Csv -LiteralPath $issuesCsv -NoTypeInformation -Encoding UTF8
$familyCsv = Join-Path $OutputDirectory 'low-resolution-families.csv'
$familyQuality | Sort-Object Severity, Id | Export-Csv -LiteralPath $familyCsv -NoTypeInformation -Encoding UTF8

$highFamilies = @($familyQuality | Where-Object Severity -eq 'high')
$reviewFamilies = @($familyQuality | Where-Object Severity -eq 'review')
$dimensionIssues = @($issues | Where-Object Type -eq 'dimension-mismatch')
$scale4NativeRange = @($dimensionIssues | Where-Object {
    $_.Scale -eq 4 -and [int]$_.Id -ge 1978 -and [int]$_.Id -le 2082
})

$summary = @(
    '# Chicken LT image quality audit'
    ''
    ('- Root: `' + $Root + '`')
    "- PNG files scanned: $($metrics.Count)"
    "- Unreadable PNG files: $(@($issues | Where-Object Type -eq 'unreadable').Count)"
    "- High-confidence low-resolution families: $($highFamilies.Count)"
    "- Families requiring manual review: $($reviewFamilies.Count)"
    "- Missing item scale variants: $(@($issues | Where-Object Type -eq 'missing-scale').Count)"
    "- Scale variants with a dimension error above 10 percent: $($dimensionIssues.Count)"
    "- Small1978-Small2082 x4 files that still have x1 dimensions: $($scale4NativeRange.Count)"
    "- Fully transparent images: $(@($issues | Where-Object Type -eq 'fully-transparent').Count) (Small1514 at all four scales is used as a 1x1 placeholder)"
    ''
    '## Main findings'
    ''
    '- A family is high confidence when both x2 and x3 fail to add proportional detail over x1. RPG-7/Small29 and AT4/Small194 match this pattern.'
    '- Laser Delta/Small1213 is not flagged: its x2/x3 variants add colors, edge alpha, and real detail.'
    '- Small2090 (large Tet gift box) only has x1; x2, x3, and x4 are missing.'
    '- Low-color pixel art is not automatically marked bad without cross-scale evidence.'
    ''
    '## Detailed files'
    ''
    '- `all-images.csv`'
    '- `issues.csv`'
    '- `low-resolution-families.csv`'
    ''
    'Review entries are not automatic failures and should be checked visually before replacement.'
)
$summary | Set-Content -LiteralPath (Join-Path $OutputDirectory 'summary.md') -Encoding UTF8

Write-Host "Audit complete: $OutputDirectory"
