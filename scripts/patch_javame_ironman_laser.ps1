param(
    [string[]]$JarPaths = @(
        'client\1 Aim local_1.jar',
        'client\Chicken_LT_Local_JavaME.jar',
        'client\Chicken_LT_Local_JavaME_x1.jar'
    )
)

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$javaHome = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
$javac = Join-Path $javaHome 'bin\javac.exe'
$java = Join-Path $javaHome 'bin\java.exe'
$sourceRoot = Join-Path $root 'client-patches\javame'
$buildRoot = Join-Path $root 'build\ironman-laser-javame'
$helperSource = Join-Path $sourceRoot 'src\chibikun\IronManLaserVfx.java'
$patcherSource = Join-Path $sourceRoot 'PatchIronManLaser.java'
$toolsRoot = Join-Path $root 'build\tools'
$proguardVersion = '7.9.1'
$proguardDir = Join-Path $toolsRoot "proguard-$proguardVersion"
$proguardZip = Join-Path $toolsRoot "proguard-$proguardVersion.zip"
$proguardJar = Join-Path $proguardDir 'lib\proguard.jar'
$cldcJar = Join-Path $toolsRoot 'cldcapi11-2.0.4.jar'
$midpJar = Join-Path $toolsRoot 'midpapi20-2.0.4.jar'

if (-not (Test-Path -LiteralPath $javac)) {
    throw "Khong tim thay javac Java 21: $javac"
}

New-Item -ItemType Directory -Force -Path $buildRoot | Out-Null
New-Item -ItemType Directory -Force -Path $toolsRoot | Out-Null
if (-not (Test-Path -LiteralPath $proguardJar)) {
    if (-not (Test-Path -LiteralPath $proguardZip)) {
        Invoke-WebRequest `
            -Uri "https://github.com/Guardsquare/proguard/releases/download/v$proguardVersion/proguard-$proguardVersion.zip" `
            -OutFile $proguardZip
    }
    Expand-Archive -LiteralPath $proguardZip -DestinationPath $toolsRoot -Force
}
if (-not (Test-Path -LiteralPath $cldcJar)) {
    Invoke-WebRequest `
        -Uri 'https://repo1.maven.org/maven2/org/microemu/cldcapi11/2.0.4/cldcapi11-2.0.4.jar' `
        -OutFile $cldcJar
}
if (-not (Test-Path -LiteralPath $midpJar)) {
    Invoke-WebRequest `
        -Uri 'https://repo1.maven.org/maven2/org/microemu/midpapi20/2.0.4/midpapi20-2.0.4.jar' `
        -OutFile $midpJar
}

foreach ($relativeJar in $JarPaths) {
    $jarPath = [IO.Path]::GetFullPath((Join-Path $root $relativeJar))
    if (-not (Test-Path -LiteralPath $jarPath)) {
        throw "Khong tim thay JAR: $jarPath"
    }

    $helperBuild = Join-Path $buildRoot 'helper'
    New-Item -ItemType Directory -Force -Path $helperBuild | Out-Null
    & $javac `
        --release 8 `
        '-Xlint:-options' `
        -classpath $jarPath `
        -d $helperBuild `
        $helperSource
    if ($LASTEXITCODE -ne 0) {
        throw "Khong bien dich duoc IronManLaserVfx cho $jarPath"
    }

    $patcherBuild = Join-Path $buildRoot 'patcher'
    New-Item -ItemType Directory -Force -Path $patcherBuild | Out-Null
    & $javac `
        --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED `
        -d $patcherBuild `
        $patcherSource
    if ($LASTEXITCODE -ne 0) {
        throw 'Khong bien dich duoc PatchIronManLaser'
    }

    $backup = "$jarPath.before-ironman-laser.bak"
    if (-not (Test-Path -LiteralPath $backup)) {
        Copy-Item -LiteralPath $jarPath -Destination $backup
    }
    $runBackup = "$jarPath.ironman-run.bak"
    Copy-Item -LiteralPath $jarPath -Destination $runBackup -Force

    try {
        $helperClass = Join-Path $helperBuild 'chibikun\IronManLaserVfx.class'
        & $java `
            --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED `
            -classpath $patcherBuild `
            PatchIronManLaser `
            $jarPath `
            $helperClass
        if ($LASTEXITCODE -ne 0) {
            throw "Va bytecode JAR that bai: $jarPath"
        }

        # ASM khong sinh StackMap CLDC. ProGuard -microedition preverify lai
        # toan bo JAR de J2ME Loader/Java ME verifier chap nhan class da sua.
        $preverified = "$jarPath.ironman-preverified.tmp.jar"
        Remove-Item -LiteralPath $preverified -ErrorAction SilentlyContinue
        & $java `
            -jar $proguardJar `
            -injars $jarPath `
            -outjars $preverified `
            -libraryjars $cldcJar `
            -libraryjars $midpJar `
            -dontshrink `
            -dontoptimize `
            -dontobfuscate `
            -dontwarn `
            -ignorewarnings `
            -dontnote `
            -keepdirectories `
            -microedition `
            -target 1.3 `
            -forceprocessing
        if ($LASTEXITCODE -ne 0 -or -not (Test-Path -LiteralPath $preverified)) {
            throw "Preverify Java ME that bai: $jarPath"
        }
        Move-Item -LiteralPath $preverified -Destination $jarPath -Force
    } catch {
        Copy-Item -LiteralPath $runBackup -Destination $jarPath -Force
        throw
    } finally {
        Remove-Item -LiteralPath $runBackup -ErrorAction SilentlyContinue
    }
}
