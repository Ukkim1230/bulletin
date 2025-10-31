# Quick Deploy Script
# 간단하게 EC2에 배포하기

param(
    [string]$Ec2Ip = "",
    [string]$KeyPath = ""
)

# 값이 없으면 입력받기
if ([string]::IsNullOrWhiteSpace($Ec2Ip)) {
    $Ec2Ip = Read-Host "EC2 IP Address"
}

if ([string]::IsNullOrWhiteSpace($KeyPath)) {
    # 기본 경로 시도
    $defaultPath = "$env:USERPROFILE\Downloads\*.pem"
    $foundKeys = Get-ChildItem -Path "$env:USERPROFILE\Downloads" -Filter "*.pem" -ErrorAction SilentlyContinue
    
    if ($foundKeys.Count -eq 1) {
        $KeyPath = $foundKeys[0].FullName
        Write-Host "Auto-detected key file: $KeyPath" -ForegroundColor Cyan
    } elseif ($foundKeys.Count -gt 1) {
        Write-Host ""
        Write-Host "Found multiple key files:" -ForegroundColor Cyan
        for ($i = 0; $i -lt $foundKeys.Count; $i++) {
            Write-Host "  [$i] $($foundKeys[$i].FullName)" -ForegroundColor White
        }
        Write-Host ""
        $selection = Read-Host "Select file number (0-$($foundKeys.Count - 1))"
        
        if ($selection -match '^\d+$' -and [int]$selection -ge 0 -and [int]$selection -lt $foundKeys.Count) {
            $KeyPath = $foundKeys[[int]$selection].FullName
            Write-Host "[Selected] $KeyPath" -ForegroundColor Green
        } else {
            Write-Host "[ERROR] Invalid selection." -ForegroundColor Red
            exit 1
        }
    } else {
        $KeyPath = Read-Host "SSH key file (.pem) path"
    }
}

# 배포 실행
Write-Host ""
Write-Host "Starting deployment..." -ForegroundColor Green
& .\deploy-to-ec2.ps1 -Ec2Ip $Ec2Ip -KeyPath $KeyPath

