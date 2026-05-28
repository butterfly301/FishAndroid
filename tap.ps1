param(
    [int]$GameX,
    [int]$GameY,
    [switch]$Tap,
    [switch]$Screenshot
)

<#
.SYNOPSIS
    Convert game-coordinates (1280×720) to device-coordinates and optionally tap.
.EXAMPLE
    .\tap.ps1 640 287 -Tap          # tap 关卡模式
    .\tap.ps1 640 287               # just print device coordinates
    .\tap.ps1 640 287 -Screenshot   # print coords then screenshot to verify
#>

$gameW = 1280
$gameH = 720

# --- Step 1: get view dimensions from the focused window ---
$dump = adb shell dumpsys window 2>$null | Select-String "mBounds=" | Select-Object -First 1
if (-not $dump) {
    Write-Warning "Could not find window bounds, falling back to device size."
    $sizeRaw = adb shell wm size 2>$null
    if ($sizeRaw -match '(\d+)x(\d+)') {
        $viewW = [int]$Matches[1]
        $viewH = [int]$Matches[2]
    } else {
        Write-Error "Cannot determine device resolution."
        exit 1
    }
} else {
    # mBounds=Rect(0, 0 - 1600, 900)
    if ($dump -match 'Rect\(\d+, \d+ - (\d+), (\d+)\)') {
        $viewW = [int]$Matches[1]
        $viewH = [int]$Matches[2]
    } else {
        Write-Warning "Could not parse bounds, falling back to wm size."
        $sizeRaw = adb shell wm size 2>$null
        if ($sizeRaw -match '(\d+)x(\d+)') {
            $viewW = [int]$Matches[1]
            $viewH = [int]$Matches[2]
        } else {
            Write-Error "Cannot determine device resolution."
            exit 1
        }
    }
}

# --- Step 2: calculate scale and device coordinates ---
$scaleX = $viewW / $gameW
$scaleY = $viewH / $gameH

$devX = [math]::Round($GameX * $scaleX)
$devY = [math]::Round($GameY * $scaleY)

Write-Host "Game ($GameX, $GameY) -> Device (${viewW}x${viewH}) -> ADB ($devX, $devY)" -ForegroundColor Cyan
Write-Host "  Scale: X= $($scaleX.ToString('0.000')), Y= $($scaleY.ToString('0.000'))" -ForegroundColor DarkGray

# --- Step 3: optional tap ---
if ($Tap) {
    Write-Host "  → Tapping ($devX, $devY)..." -ForegroundColor Green
    adb shell input tap $devX $devY
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✓ Done." -ForegroundColor Green
    }
}

# --- Step 4: optional screenshot verification ---
if ($Screenshot) {
    $imgPath = Join-Path $PSScriptRoot "tap_verify.png"
    Write-Host "  → Capturing screenshot..." -ForegroundColor DarkGray
    adb shell screencap -p /sdcard/tap_verify.png 2>$null
    adb pull /sdcard/tap_verify.png $imgPath 2>$null | Out-Null
    if (Test-Path $imgPath) {
        Write-Host "  → Screenshot saved: $imgPath" -ForegroundColor DarkGray
    }
}
