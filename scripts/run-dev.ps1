# Development mode: restart the server when source or config changes.
# Stop this script with Ctrl+C in the PowerShell window that runs it.

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$jdkHome = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'

if (-not (Test-Path (Join-Path $jdkHome 'bin\java.exe'))) {
    throw "JDK 21 was not found at: $jdkHome"
}

$env:JAVA_HOME = $jdkHome
$env:Path = "$jdkHome\bin;$env:Path"

function Get-SourceStamp {
    $files = @(
        Get-ChildItem (Join-Path $projectRoot 'src') -Recurse -File -Filter '*.java'
        Get-Item (Join-Path $projectRoot 'config.conf')
        Get-Item (Join-Path $projectRoot 'build.gradle')
        Get-Item (Join-Path $projectRoot 'settings.gradle')
    )
    return ($files | Measure-Object -Property LastWriteTimeUtc -Maximum).Maximum
}

function Test-DatabasePort {
    $client = [System.Net.Sockets.TcpClient]::new()
    try {
        $connectTask = $client.ConnectAsync('127.0.0.1', 3306)
        if (-not $connectTask.Wait(1000)) {
            return $false
        }
        return $client.Connected
    }
    catch {
        return $false
    }
    finally {
        $client.Dispose()
    }
}

function Assert-DatabaseReady {
    if (-not (Test-DatabasePort)) {
        throw 'MySQL/MariaDB is not listening on 127.0.0.1:3306. Start MySQL in XAMPP, then run this script again.'
    }
}

function Start-Server {
    Assert-DatabaseReady
    Write-Host "`n[DEV] Starting server..." -ForegroundColor Cyan
    return Start-Process -FilePath 'cmd.exe' `
        -ArgumentList '/d', '/c', 'call .\gradlew.bat run --no-daemon' `
        -WorkingDirectory $projectRoot `
        -NoNewWindow `
        -PassThru
}

function Test-ProjectBuild {
    Write-Host "[DEV] Checking source before restart..." -ForegroundColor Cyan
    Push-Location $projectRoot
    try {
        & (Join-Path $projectRoot 'gradlew.bat') compileJava --no-daemon
        if ($LASTEXITCODE -ne 0) {
            Write-Host "[DEV] Build failed; keeping the current server alive. Save the fixed source to retry." -ForegroundColor Red
            return $false
        }
        return $true
    }
    finally {
        Pop-Location
    }
}

function Stop-Server([System.Diagnostics.Process]$serverProcess) {
    if ($null -eq $serverProcess -or $serverProcess.HasExited) {
        return
    }
    Write-Host "[DEV] Stopping old server..." -ForegroundColor Yellow
    & "$env:SystemRoot\System32\taskkill.exe" /PID $serverProcess.Id /T /F | Out-Null
    $serverProcess.WaitForExit()
}

$portInUse = Get-NetTCPConnection -LocalPort 19150 -State Listen -ErrorAction SilentlyContinue
if ($null -ne $portInUse) {
    throw 'Port 19150 is already in use. Stop the old server with Ctrl+C, then run this script again.'
}

$lastSourceStamp = Get-SourceStamp
$buildOk = Test-ProjectBuild
if (-not $buildOk) {
    throw 'Initial build failed. Fix the compiler errors, then run this script again.'
}
$server = Start-Server

try {
    while ($true) {
        Start-Sleep -Milliseconds 750
        $newSourceStamp = Get-SourceStamp

        if ($newSourceStamp -gt $lastSourceStamp) {
            # Editors may save related Java files one by one. Debounce, then
            # compile before stopping the healthy server so a partial save
            # cannot kill development mode.
            Start-Sleep -Milliseconds 900
            $lastSourceStamp = Get-SourceStamp
            if (Test-ProjectBuild) {
                Write-Host "[DEV] Build passed; restarting..." -ForegroundColor Green
                Stop-Server $server
                $server = Start-Server
            }
        }

        if ($server.HasExited) {
            $server.WaitForExit()
            $exitCode = $server.ExitCode
            if ($null -eq $exitCode) {
                $exitCode = 'unknown'
            }
            throw "Server exited unexpectedly with code $exitCode. Automatic crash restart was stopped to avoid an error loop."
        }
    }
}
finally {
    Stop-Server $server
}
