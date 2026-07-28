param(
    [string[]]$JarPaths = @('client\1 Aim local_1.jar')
)

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$javaHome = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
$javac = Join-Path $javaHome 'bin\javac.exe'
$java = Join-Path $javaHome 'bin\java.exe'
$sourceRoot = Join-Path $root 'client-patches\javame'
$buildRoot = Join-Path $root 'build\overlay-toggle-javame'
$helperSource = Join-Path $sourceRoot 'src\chibikun\OverlayInfoToggle.java'
$patcherSource = Join-Path $sourceRoot 'PatchOverlayInfoToggle.java'
$toolsRoot = Join-Path $root 'build\tools'
$proguardJar = Join-Path $toolsRoot 'proguard-7.9.1\lib\proguard.jar'
$cldcJar = Join-Path $toolsRoot 'cldcapi11-2.0.4.jar'
$midpJar = Join-Path $toolsRoot 'midpapi20-2.0.4.jar'

foreach ($required in @($javac, $java, $proguardJar, $cldcJar, $midpJar)) {
    if (-not (Test-Path -LiteralPath $required)) {
        throw "Khong tim thay cong cu: $required"
    }
}

New-Item -ItemType Directory -Force -Path $buildRoot | Out-Null
foreach ($relativeJar in $JarPaths) {
    $jarPath = [IO.Path]::GetFullPath((Join-Path $root $relativeJar))
    if (-not (Test-Path -LiteralPath $jarPath)) {
        throw "Khong tim thay JAR: $jarPath"
    }

    $helperBuild = Join-Path $buildRoot 'helper'
    $patcherBuild = Join-Path $buildRoot 'patcher'
    New-Item -ItemType Directory -Force -Path $helperBuild | Out-Null
    New-Item -ItemType Directory -Force -Path $patcherBuild | Out-Null

    & $javac --release 8 '-Xlint:-options' `
        -classpath $jarPath -d $helperBuild $helperSource
    if ($LASTEXITCODE -ne 0) {
        throw 'Khong bien dich duoc OverlayInfoToggle'
    }
    & $javac `
        --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED `
        -d $patcherBuild $patcherSource
    if ($LASTEXITCODE -ne 0) {
        throw 'Khong bien dich duoc PatchOverlayInfoToggle'
    }

    $backup = "$jarPath.before-overlay-toggle.bak"
    if (-not (Test-Path -LiteralPath $backup)) {
        Copy-Item -LiteralPath $jarPath -Destination $backup
    }
    $runBackup = "$jarPath.overlay-run.bak"
    Copy-Item -LiteralPath $jarPath -Destination $runBackup -Force
    try {
        $helperClass = Join-Path $helperBuild 'chibikun\OverlayInfoToggle.class'
        & $java `
            --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED `
            -classpath $patcherBuild `
            PatchOverlayInfoToggle $jarPath $helperClass
        if ($LASTEXITCODE -ne 0) {
            throw 'Va bytecode toggle that bai'
        }

        $preverified = "$jarPath.overlay-preverified.tmp.jar"
        Remove-Item -LiteralPath $preverified -ErrorAction SilentlyContinue
        & $java -jar $proguardJar `
            -injars $jarPath -outjars $preverified `
            -libraryjars $cldcJar -libraryjars $midpJar `
            -dontshrink -dontoptimize -dontobfuscate `
            -dontwarn -ignorewarnings -dontnote -keepdirectories `
            -microedition -target 1.3 -forceprocessing
        if ($LASTEXITCODE -ne 0 -or -not (Test-Path -LiteralPath $preverified)) {
            throw 'Preverify Java ME toggle that bai'
        }
        Move-Item -LiteralPath $preverified -Destination $jarPath -Force
    } catch {
        Copy-Item -LiteralPath $runBackup -Destination $jarPath -Force
        throw
    } finally {
        Remove-Item -LiteralPath $runBackup -ErrorAction SilentlyContinue
    }
}
