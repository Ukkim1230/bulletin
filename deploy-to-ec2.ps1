# EC2 배포 스크립트 (PowerShell)
# 사용법: .\deploy-to-ec2.ps1 -Ec2Ip "your-ec2-ip" -KeyPath "C:\path\to\key.pem"

param(
    [Parameter(Mandatory=$true)]
    [string]$Ec2Ip,
    
    [Parameter(Mandatory=$true)]
    [string]$KeyPath,
    
    [Parameter(Mandatory=$false)]
    [string]$User = "ec2-user",
    
    [Parameter(Mandatory=$false)]
    [string]$AppDir = "/opt/bulletin"
)

$ErrorActionPreference = "Stop"

Write-Host "==========================================" -ForegroundColor Green
Write-Host "EC2 배포 시작" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host "EC2 IP: $Ec2Ip" -ForegroundColor Cyan
Write-Host "키 파일: $KeyPath" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Green

# SSH 키 파일 확인
if (-not (Test-Path $KeyPath)) {
    Write-Host "[오류] SSH 키 파일을 찾을 수 없습니다: $KeyPath" -ForegroundColor Red
    exit 1
}

# 로컬 빌드
Write-Host ""
Write-Host "[1/4] 로컬에서 프로젝트 빌드 중..." -ForegroundColor Yellow
& .\gradlew.bat clean build -x test --no-daemon
if ($LASTEXITCODE -ne 0) {
    Write-Host "[오류] 빌드 실패!" -ForegroundColor Red
    exit 1
}
Write-Host "[완료] 빌드 성공" -ForegroundColor Green

# JAR 파일 확인
$jarPath = "build\libs\bulletin-0.0.1-SNAPSHOT.jar"
if (-not (Test-Path $jarPath)) {
    Write-Host "[오류] JAR 파일을 찾을 수 없습니다!" -ForegroundColor Red
    exit 1
}

# JAR 파일을 EC2로 전송
Write-Host ""
Write-Host "[2/4] JAR 파일을 EC2로 전송 중..." -ForegroundColor Yellow
$scpCommand = "scp -i `"$KeyPath`" $jarPath ${User}@${Ec2Ip}:/tmp/bulletin.jar"
Invoke-Expression $scpCommand
if ($LASTEXITCODE -ne 0) {
    Write-Host "[오류] 파일 전송 실패!" -ForegroundColor Red
    exit 1
}
Write-Host "[완료] 파일 전송 성공" -ForegroundColor Green

# EC2에서 배포 실행
Write-Host ""
Write-Host "[3/4] EC2에서 배포 실행 중..." -ForegroundColor Yellow
$deployCommand = @"
sudo mkdir -p $AppDir
sudo cp /tmp/bulletin.jar $AppDir/bulletin-0.0.1-SNAPSHOT.jar
sudo chown ec2-user:ec2-user $AppDir/bulletin-0.0.1-SNAPSHOT.jar
sudo systemctl restart bulletin
"@

ssh -i $KeyPath "${User}@${Ec2Ip}" $deployCommand
if ($LASTEXITCODE -ne 0) {
    Write-Host "[오류] 배포 실행 실패!" -ForegroundColor Red
    exit 1
}
Write-Host "[완료] 배포 실행 성공" -ForegroundColor Green

# 서비스 상태 확인
Write-Host ""
Write-Host "[4/4] 서비스 상태 확인 중..." -ForegroundColor Yellow
Start-Sleep -Seconds 3
ssh -i $KeyPath "${User}@${Ec2Ip}" "sudo systemctl status bulletin --no-pager -l"

Write-Host ""
Write-Host "==========================================" -ForegroundColor Green
Write-Host "배포 완료!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host "애플리케이션 URL: http://$Ec2Ip`:8080" -ForegroundColor Cyan
Write-Host ""

