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

function Start-Server {
    Write-Host "`n[DEV] Starting server..." -ForegroundColor Cyan
    return Start-Process -FilePath 'cmd.exe' `
        -ArgumentList '/d', '/c', 'call .\gradlew.bat run --no-daemon' `
        -WorkingDirectory $projectRoot `
        -NoNewWindow `
        -PassThru
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
$server = Start-Server

try {
    while ($true) {
        Start-Sleep -Milliseconds 750
        $newSourceStamp = Get-SourceStamp

        if ($newSourceStamp -gt $lastSourceStamp) {
            # Give the editor time to finish writing all files before compiling.
            Start-Sleep -Milliseconds 500
            $lastSourceStamp = Get-SourceStamp
            Write-Host "[DEV] Change detected; restarting..." -ForegroundColor Green
            Stop-Server $server
            $server = Start-Server
        }

        if ($server.HasExited) {
            Write-Host '[DEV] Server stopped; starting again...' -ForegroundColor Yellow
            $server = Start-Server
        }
    }
}
finally {
    Stop-Server $server
}
