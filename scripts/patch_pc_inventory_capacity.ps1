param(
    [string]$ClientRoot = 'D:\MobiArmy3VN-PC\MobiArmy3 VN'
)

$ErrorActionPreference = 'Stop'

$assemblyPath = Join-Path $ClientRoot 'Mobi Army 3_Data\Managed\Assembly-CSharp.dll'
$cecilPath = Join-Path $ClientRoot 'BepInEx\core\Mono.Cecil.dll'

if (-not (Test-Path -LiteralPath $assemblyPath)) {
    throw "Khong tim thay Assembly-CSharp.dll: $assemblyPath"
}
if (-not (Test-Path -LiteralPath $cecilPath)) {
    throw "Khong tim thay Mono.Cecil.dll: $cecilPath"
}

[void][Reflection.Assembly]::LoadFrom($cecilPath)
$assembly = [Mono.Cecil.AssemblyDefinition]::ReadAssembly($assemblyPath)
$tempPath = "$assemblyPath.inventory-capacity.tmp"
$backupPath = "$assemblyPath.before-inventory-100.bak"

try {
    $messageHandler = $assembly.MainModule.Types |
        Where-Object { $_.FullName -eq 'MessageHandler' } |
        Select-Object -First 1
    if ($null -eq $messageHandler) {
        throw 'Khong tim thay lop MessageHandler.'
    }

    $onMessage = $messageHandler.Methods |
        Where-Object { $_.Name -eq 'onMessage' -and $_.HasBody } |
        Select-Object -First 1
    if ($null -eq $onMessage) {
        throw 'Khong tim thay MessageHandler.onMessage.'
    }

    $instructions = $onMessage.Body.Instructions
    $patched = $false
    $alreadyPatched = $false

    for ($i = 2; $i -lt $instructions.Count; $i++) {
        $store = $instructions[$i]
        if ($store.OpCode.Code -ne [Mono.Cecil.Cil.Code]::Stsfld -or
                $null -eq $store.Operand -or
                $store.Operand.FullName -ne 'Item[] CPlayer::arrItemBag') {
            continue
        }

        $newArray = $instructions[$i - 1]
        $arrayLength = $instructions[$i - 2]
        if ($newArray.OpCode.Code -ne [Mono.Cecil.Cil.Code]::Newarr -or
                $newArray.Operand.FullName -ne 'Item') {
            continue
        }

        if ($arrayLength.OpCode.Code -in @(
                    [Mono.Cecil.Cil.Code]::Ldloc,
                    [Mono.Cecil.Cil.Code]::Ldloc_S,
                    [Mono.Cecil.Cil.Code]::Ldloc_0,
                    [Mono.Cecil.Cil.Code]::Ldloc_1,
                    [Mono.Cecil.Cil.Code]::Ldloc_2,
                    [Mono.Cecil.Cil.Code]::Ldloc_3
                )) {
            $alreadyPatched = $true
            break
        }

        if ($arrayLength.OpCode.Code -ne [Mono.Cecil.Cil.Code]::Ldc_I4_S -or
                [int]$arrayLength.Operand -ne 20) {
            throw "Mau lenh tao tui do da thay doi; dung va kiem tra lai de tranh va nham client."
        }

        $storeLength = $instructions[$i - 3]
        if ($storeLength.OpCode.Code -notin @(
                    [Mono.Cecil.Cil.Code]::Stloc,
                    [Mono.Cecil.Cil.Code]::Stloc_S
                )) {
            throw 'Khong tim thay bien chua so o tui do do server gui.'
        }

        $arrayLength.OpCode = [Mono.Cecil.Cil.OpCodes]::Ldloc
        $arrayLength.Operand = $storeLength.Operand
        $patched = $true
        break
    }

    if (-not $patched -and -not $alreadyPatched) {
        throw 'Khong tim thay doan khoi tao CPlayer.arrItemBag.'
    }

    if ($alreadyPatched) {
        Write-Host 'Client PC da dung kich thuoc tui do do server gui; khong can va lai.'
        return
    }

    if (-not (Test-Path -LiteralPath $backupPath)) {
        Copy-Item -LiteralPath $assemblyPath -Destination $backupPath
    }

    $assembly.Write($tempPath)
    $assembly.Dispose()
    $assembly = $null
    Move-Item -LiteralPath $tempPath -Destination $assemblyPath -Force
    Write-Host "Da va client PC: tui do se dung dung so o server gui (100)."
    Write-Host "Ban sao truoc khi va: $backupPath"
}
finally {
    if ($null -ne $assembly) {
        $assembly.Dispose()
    }
    if (Test-Path -LiteralPath $tempPath) {
        Remove-Item -LiteralPath $tempPath -Force
    }
}
