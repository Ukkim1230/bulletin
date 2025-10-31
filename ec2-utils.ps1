# PowerShell에서 EC2 배포를 위한 유틸리티 함수들
# 사용법: . .\ec2-utils.ps1 (스크립트를 현재 세션에 로드)

# 설정 (사용자 환경에 맞게 수정)
$script:EC2_IP = "your-ec2-ip-here"
$script:KEY_PATH = "C:\Users\Administrator\Downloads\bulletin-key.pem"
$script:EC2_USER = "ec2-user"
$script:APP_DIR = "/opt/bulletin"

# EC2에 SSH로 접속
function Connect-EC2 {
    param(
        [string]$Ip = $script:EC2_IP,
        [string]$KeyPath = $script:KEY_PATH,
        [string]$User = $script:EC2_USER
    )
    
    if (-not (Test-Path $KeyPath)) {
        Write-Host "[오류] SSH 키 파일을 찾을 수 없습니다: $KeyPath" -ForegroundColor Red
        return
    }
    
    Write-Host "EC2 인스턴스에 연결 중... ($User@$Ip)" -ForegroundColor Cyan
    ssh -i $KeyPath "${User}@${Ip}"
}

# 프로젝트 빌드
function Build-Project {
    param(
        [switch]$SkipTests
    )
    
    Write-Host "프로젝트 빌드 중..." -ForegroundColor Yellow
    if ($SkipTests) {
        .\gradlew.bat clean build -x test --no-daemon
    } else {
        .\gradlew.bat clean build --no-daemon
    }
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[완료] 빌드 성공" -ForegroundColor Green
    } else {
        Write-Host "[오류] 빌드 실패" -ForegroundColor Red
    }
}

# EC2에 배포
function Deploy-EC2 {
    param(
        [string]$Ip = $script:EC2_IP,
        [string]$KeyPath = $script:KEY_PATH,
        [string]$User = $script:EC2_USER,
        [string]$AppDir = $script:APP_DIR,
        [switch]$Build
    )
    
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "EC2 배포 시작" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    
    # SSH 키 파일 확인
    if (-not (Test-Path $KeyPath)) {
        Write-Host "[오류] SSH 키 파일을 찾을 수 없습니다: $KeyPath" -ForegroundColor Red
        return
    }
    
    # 빌드 (필요시)
    if ($Build) {
        Build-Project -SkipTests
        if ($LASTEXITCODE -ne 0) {
            Write-Host "[오류] 빌드 실패로 배포를 중단합니다." -ForegroundColor Red
            return
        }
    }
    
    # JAR 파일 확인
    $jarPath = "build\libs\bulletin-0.0.1-SNAPSHOT.jar"
    if (-not (Test-Path $jarPath)) {
        Write-Host "[오류] JAR 파일을 찾을 수 없습니다. 먼저 빌드하세요." -ForegroundColor Red
        Write-Host "사용법: Deploy-EC2 -Build" -ForegroundColor Yellow
        return
    }
    
    # JAR 파일 전송
    Write-Host "JAR 파일을 EC2로 전송 중..." -ForegroundColor Yellow
    scp -i $KeyPath $jarPath "${User}@${Ip}:/tmp/bulletin.jar"
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[오류] 파일 전송 실패!" -ForegroundColor Red
        return
    }
    
    # 배포 실행
    Write-Host "EC2에서 배포 실행 중..." -ForegroundColor Yellow
    $deployCommand = @"
sudo mkdir -p $AppDir
sudo cp /tmp/bulletin.jar $AppDir/bulletin-0.0.1-SNAPSHOT.jar
sudo chown ec2-user:ec2-user $AppDir/bulletin-0.0.1-SNAPSHOT.jar
sudo systemctl restart bulletin
"@
    
    ssh -i $KeyPath "${User}@${Ip}" $deployCommand
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[완료] 배포 성공!" -ForegroundColor Green
        Write-Host "애플리케이션 URL: http://$Ip`:8080" -ForegroundColor Cyan
    } else {
        Write-Host "[오류] 배포 실패!" -ForegroundColor Red
    }
}

# EC2 서비스 상태 확인
function Get-EC2ServiceStatus {
    param(
        [string]$Ip = $script:EC2_IP,
        [string]$KeyPath = $script:KEY_PATH,
        [string]$User = $script:EC2_USER
    )
    
    Write-Host "EC2 서비스 상태 확인 중..." -ForegroundColor Yellow
    ssh -i $KeyPath "${User}@${Ip}" "sudo systemctl status bulletin --no-pager -l"
}

# EC2 로그 확인
function Get-EC2Logs {
    param(
        [string]$Ip = $script:EC2_IP,
        [string]$KeyPath = $script:KEY_PATH,
        [string]$User = $script:EC2_USER,
        [int]$Lines = 50
    )
    
    Write-Host "최근 $Lines 줄의 로그를 가져오는 중..." -ForegroundColor Yellow
    ssh -i $KeyPath "${User}@${Ip}" "sudo journalctl -u bulletin -n $Lines --no-pager"
}

# EC2 서비스 재시작
function Restart-EC2Service {
    param(
        [string]$Ip = $script:EC2_IP,
        [string]$KeyPath = $script:KEY_PATH,
        [string]$User = $script:EC2_USER
    )
    
    Write-Host "EC2 서비스 재시작 중..." -ForegroundColor Yellow
    ssh -i $KeyPath "${User}@${Ip}" "sudo systemctl restart bulletin"
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[완료] 서비스 재시작 완료" -ForegroundColor Green
        Start-Sleep -Seconds 2
        Get-EC2ServiceStatus -Ip $Ip -KeyPath $KeyPath -User $User
    } else {
        Write-Host "[오류] 서비스 재시작 실패" -ForegroundColor Red
    }
}

# EC2에서 Git Pull 및 배포
function Update-EC2 {
    param(
        [string]$Ip = $script:EC2_IP,
        [string]$KeyPath = $script:KEY_PATH,
        [string]$User = $script:EC2_USER,
        [string]$AppDir = $script:APP_DIR
    )
    
    Write-Host "EC2에서 코드 업데이트 및 배포 중..." -ForegroundColor Yellow
    ssh -i $KeyPath "${User}@${Ip}" "cd $AppDir && sudo git pull origin main && sudo ./deploy.sh"
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[완료] 업데이트 및 배포 완료" -ForegroundColor Green
    } else {
        Write-Host "[오류] 업데이트 실패" -ForegroundColor Red
    }
}

# 설정 표시
function Show-EC2Config {
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host "EC2 배포 설정" -ForegroundColor Cyan
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host "EC2 IP: $script:EC2_IP" -ForegroundColor White
    Write-Host "키 파일: $script:KEY_PATH" -ForegroundColor White
    Write-Host "사용자: $script:EC2_USER" -ForegroundColor White
    Write-Host "앱 디렉토리: $script:APP_DIR" -ForegroundColor White
    Write-Host "==========================================" -ForegroundColor Cyan
}

# 도움말 표시
function Show-EC2Help {
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "EC2 배포 유틸리티 함수" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "사용 가능한 함수:" -ForegroundColor Yellow
    Write-Host "  Connect-EC2           - EC2에 SSH로 접속" -ForegroundColor White
    Write-Host "  Build-Project         - 프로젝트 빌드" -ForegroundColor White
    Write-Host "  Deploy-EC2 [-Build]   - EC2에 배포 (빌드 포함 선택)" -ForegroundColor White
    Write-Host "  Get-EC2ServiceStatus  - 서비스 상태 확인" -ForegroundColor White
    Write-Host "  Get-EC2Logs [-Lines]  - 로그 확인" -ForegroundColor White
    Write-Host "  Restart-EC2Service    - 서비스 재시작" -ForegroundColor White
    Write-Host "  Update-EC2            - Git Pull 및 배포" -ForegroundColor White
    Write-Host "  Show-EC2Config        - 설정 확인" -ForegroundColor White
    Write-Host "  Show-EC2Help          - 도움말 표시" -ForegroundColor White
    Write-Host ""
    Write-Host "예시:" -ForegroundColor Yellow
    Write-Host "  . .\ec2-utils.ps1              # 함수 로드" -ForegroundColor Gray
    Write-Host "  Connect-EC2                    # EC2 접속" -ForegroundColor Gray
    Write-Host "  Deploy-EC2 -Build              # 빌드 후 배포" -ForegroundColor Gray
    Write-Host "  Get-EC2Logs -Lines 100        # 최근 100줄 로그 확인" -ForegroundColor Gray
    Write-Host ""
    Write-Host "설정 변경:" -ForegroundColor Yellow
    Write-Host "  `$script:EC2_IP = 'your-ip'    # EC2 IP 변경" -ForegroundColor Gray
    Write-Host "  `$script:KEY_PATH = 'path'     # 키 파일 경로 변경" -ForegroundColor Gray
    Write-Host "==========================================" -ForegroundColor Green
}

# 초기 설정 안내
Write-Host ""
Write-Host "EC2 유틸리티 함수가 로드되었습니다!" -ForegroundColor Green
Write-Host "설정을 확인하려면: Show-EC2Config" -ForegroundColor Cyan
Write-Host "도움말을 보려면: Show-EC2Help" -ForegroundColor Cyan
Write-Host ""

