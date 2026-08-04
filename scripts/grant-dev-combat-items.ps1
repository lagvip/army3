param(
    [string]$Username = 'admin',
    [Parameter(Mandatory = $true)]
    [int[]]$ItemIds,
    [int]$Quantity = 99,
    [string]$Database = 'chicken3',
    [string]$MySql = 'C:\xampp\mysql\bin\mysql.exe'
)

$ErrorActionPreference = 'Stop'

if ($Username -notmatch '^[A-Za-z0-9_]{1,100}$') {
    throw 'Username contains unsupported characters.'
}
if ($Quantity -lt 1) {
    throw 'Quantity must be positive.'
}
if (-not (Test-Path -LiteralPath $MySql)) {
    throw "mysql.exe was not found at: $MySql"
}

$projectRoot = Split-Path -Parent $PSScriptRoot
$profilePath = Join-Path $projectRoot `
    'src\com\chicken\chien\ChickenCauHinhSatThuongVatPham.java'
$profileSource = Get-Content -LiteralPath $profilePath -Raw
$matches = [regex]::Matches(
    $profileSource,
    'dangKy\s*\(\s*new\s+HoSo\s*\(\s*(\d+)'
)
$completedItemIds = @(
    $matches |
        ForEach-Object { [int]$_.Groups[1].Value } |
        Sort-Object -Unique
)
if ($completedItemIds.Count -eq 0) {
    throw 'No completed combat-item damage profiles were found.'
}
$requestedItemIds = @($ItemIds | Sort-Object -Unique)
foreach ($candidateId in $requestedItemIds) {
    if ($completedItemIds -notcontains $candidateId) {
        throw "Item ID $candidateId has no completed server damage/physics profile."
    }
}

$selectSql = @"
SELECT p.id, p.inventory_revision, a.is_online, p.inventory_json
FROM players p
JOIN accounts a ON a.id = p.account_id
WHERE a.username = '$Username'
LIMIT 1;
"@
$row = & $MySql -u root -N -B --raw -D $Database -e $selectSql
if ($LASTEXITCODE -ne 0) {
    throw 'Could not read the admin inventory.'
}
if (-not $row) {
    throw "Account was not found: $Username"
}

$columns = $row -split "`t", 4
if ($columns.Count -ne 4) {
    throw 'Unexpected inventory query result.'
}
$playerId = [int]$columns[0]
$revision = [long]$columns[1]
$isOnline = [int]$columns[2]
if ($isOnline -ne 0) {
    throw "Account '$Username' is online. Log it out before granting items."
}

$inventory = @()
foreach ($parsedEntry in (ConvertFrom-Json -InputObject $columns[3])) {
    $inventory += $parsedEntry
}
$maxSlots = 100
$maxStack = 99
$usedIndexes = [System.Collections.Generic.HashSet[int]]::new()
foreach ($entry in $inventory) {
    if ($null -ne $entry.index) {
        [void]$usedIndexes.Add([int]$entry.index)
    }
}

function Get-FreeIndex {
    for ($index = 0; $index -lt $maxSlots; $index++) {
        if (-not $usedIndexes.Contains($index)) {
            [void]$usedIndexes.Add($index)
            return $index
        }
    }
    throw 'The inventory does not have enough free slots.'
}

foreach ($combatItemId in $requestedItemIds) {
    $remaining = $Quantity
    foreach ($entry in $inventory) {
        if ($remaining -le 0) {
            break
        }
        if ([int]$entry.id -ne $combatItemId) {
            continue
        }
        $current = [int]$entry.quantity
        if ($current -ge $maxStack) {
            continue
        }
        $added = [Math]::Min($maxStack - $current, $remaining)
        $entry.quantity = $current + $added
        $remaining -= $added
    }

    while ($remaining -gt 0) {
        $stack = [Math]::Min($maxStack, $remaining)
        $inventory += [pscustomobject][ordered]@{
            id = $combatItemId
            quantity = $stack
            HP = 100
            index = (Get-FreeIndex)
            options = @()
        }
        $remaining -= $stack
    }
}

$json = ConvertTo-Json -InputObject @($inventory) -Compress -Depth 20
$base64 = [Convert]::ToBase64String(
    [Text.Encoding]::UTF8.GetBytes($json)
)
$updateSql = @"
START TRANSACTION;
UPDATE players
SET inventory_json = CONVERT(FROM_BASE64('$base64') USING utf8mb4),
    inventory_revision = inventory_revision + 1
WHERE id = $playerId AND inventory_revision = $revision;
SELECT ROW_COUNT();
COMMIT;
"@
$updated = & $MySql -u root -N -B -D $Database -e $updateSql
if ($LASTEXITCODE -ne 0 -or [int]$updated -ne 1) {
    throw 'Inventory changed concurrently; no grant was applied.'
}

$summary = $requestedItemIds | ForEach-Object { "ID $_ +$Quantity" }
Write-Output ("Granted to {0}: {1}" -f $Username, ($summary -join ', '))
