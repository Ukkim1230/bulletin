# EC2 배포 설정 도우미 (PowerShell)
# 사용법: .\setup-ec2-deploy.ps1

Write-Host "==========================================" -ForegroundColor Green
Write-Host "EC2 Deployment Setup Helper" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""

# EC2 IP 입력
$ec2Ip = Read-Host "Enter EC2 Instance IP Address"
if ([string]::IsNullOrWhiteSpace($ec2Ip)) {
    Write-Host "[ERROR] EC2 IP is required!" -ForegroundColor Red
    exit 1
}

# 키 파일 경로 입력
Write-Host ""
Write-Host "Enter SSH key file (.pem) path:" -ForegroundColor Yellow
Write-Host "Example: C:\Users\Administrator\Downloads\bulletin-key.pem" -ForegroundColor Gray
$keyPath = Read-Host "Key file path"

# 키 파일이 없으면 찾기
if (-not (Test-Path $keyPath)) {
    Write-Host ""
    Write-Host "[WARNING] File not found at specified path. Search for it? (Y/N)" -ForegroundColor Yellow
    $search = Read-Host
    
    if ($search -eq "Y" -or $search -eq "y") {
        Write-Host "Searching for .pem files in Downloads folder..." -ForegroundColor Cyan
        $foundKeys = Get-ChildItem -Path "$env:USERPROFILE\Downloads" -Filter "*.pem" -ErrorAction SilentlyContinue
        
        if ($foundKeys.Count -gt 0) {
            Write-Host ""
            Write-Host "Found key files:" -ForegroundColor Cyan
            for ($i = 0; $i -lt $foundKeys.Count; $i++) {
                Write-Host "  [$i] $($foundKeys[$i].FullName)" -ForegroundColor White
            }
            Write-Host ""
            $selection = Read-Host "Select file number (0-$($foundKeys.Count - 1))"
            
            if ($selection -match '^\d+$' -and [int]$selection -ge 0 -and [int]$selection -lt $foundKeys.Count) {
                $keyPath = $foundKeys[[int]$selection].FullName
                Write-Host "[Selected] $keyPath" -ForegroundColor Green
            } else {
                Write-Host "[ERROR] Invalid selection." -ForegroundColor Red
                exit 1
            }
        } else {
            Write-Host "[ERROR] No key files found." -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "[ERROR] Key file is required." -ForegroundColor Red
        exit 1
    }
}

# 키 파일 확인
if (-not (Test-Path $keyPath)) {
    Write-Host "[ERROR] SSH key file not found: $keyPath" -ForegroundColor Red
    exit 1
}

# 사용자 이름 확인
$user = Read-Host "EC2 username (default: ec2-user) [Press Enter for default]"
if ([string]::IsNullOrWhiteSpace($user)) {
    $user = "ec2-user"
}

# 설정 확인
Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Configuration" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "EC2 IP: $ec2Ip" -ForegroundColor White
Write-Host "Key File: $keyPath" -ForegroundColor White
Write-Host "User: $user" -ForegroundColor White
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

$confirm = Read-Host "Proceed with deployment using these settings? (Y/N)"
if ($confirm -ne "Y" -and $confirm -ne "y") {
    Write-Host "Cancelled." -ForegroundColor Yellow
    exit 0
}

# 배포 스크립트 실행
Write-Host ""
Write-Host "Starting deployment..." -ForegroundColor Green
Write-Host ""

& .\deploy-to-ec2.ps1 -Ec2Ip $ec2Ip -KeyPath $keyPath -User $user

