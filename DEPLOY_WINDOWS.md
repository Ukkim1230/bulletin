# Windows에서 EC2 배포 가이드

Windows CMD 또는 PowerShell에서 EC2 인스턴스를 관리하고 배포하는 방법을 설명합니다.

## 사전 준비

### 1. AWS CLI 설치 (선택사항)

AWS CLI를 사용하면 EC2 인스턴스를 Windows에서 직접 관리할 수 있습니다.

**다운로드 및 설치:**
1. https://aws.amazon.com/cli/ 에서 AWS CLI 설치
2. 또는 Chocolatey 사용:
   ```powershell
   choco install awscli
   ```

**설정:**
```cmd
aws configure
# AWS Access Key ID 입력
# AWS Secret Access Key 입력
# Default region name 입력 (예: ap-northeast-2)
# Default output format 입력 (json)
```

### 2. SSH 클라이언트 설치

Windows에서 SSH를 사용하기 위한 옵션:

**옵션 A: OpenSSH (Windows 10/11 기본 포함)**
- Windows 10 버전 1809 이상 또는 Windows 11 사용 시 기본 제공
- PowerShell에서 바로 사용 가능

**옵션 B: Git Bash**
- Git for Windows 설치 시 함께 설치됨
- https://git-scm.com/download/win

**옵션 C: PuTTY**
- 전통적인 Windows SSH 클라이언트
- https://www.putty.org/

## 방법 1: PowerShell/CMD에서 직접 SSH 접속

### 1.1 SSH 키 파일 준비

`.pem` 파일을 EC2 인스턴스 생성 시 다운로드한 위치에 저장합니다.
예: `C:\Users\Administrator\Downloads\bulletin-key.pem`

**권한 설정 (PowerShell):**
```powershell
# SSH 키 파일 권한 설정 (보안상 필요)
icacls C:\Users\Administrator\Downloads\bulletin-key.pem /inheritance:r
icacls C:\Users\Administrator\Downloads\bulletin-key.pem /grant:r "%username%:R"
```

### 1.2 SSH 접속

**PowerShell에서:**
```powershell
# Amazon Linux 2의 경우
ssh -i "C:\Users\Administrator\Downloads\bulletin-key.pem" ec2-user@your-ec2-ip

# Ubuntu의 경우
ssh -i "C:\Users\Administrator\Downloads\bulletin-key.pem" ubuntu@your-ec2-ip
```

**CMD에서:**
```cmd
# PowerShell 실행
powershell -Command "ssh -i 'C:\Users\Administrator\Downloads\bulletin-key.pem' ec2-user@your-ec2-ip"
```

### 1.3 프로젝트 배포

SSH 접속 후 EC2 인스턴스에서 배포 스크립트 실행:

```bash
# 프로젝트 클론 (처음만)
cd /opt
sudo git clone https://github.com/your-repo/bulletin.git
sudo chown -R ec2-user:ec2-user bulletin
cd bulletin

# 환경 변수 설정
cp env.template .env
sudo nano .env  # 또는 vi .env

# 배포 실행
chmod +x deploy.sh
sudo ./deploy.sh
```

## 방법 2: Windows에서 배포 스크립트 작성 (SCP 사용)

Windows에서 파일을 업로드하고 원격으로 실행하는 스크립트를 만들 수 있습니다.

### 2.1 배포 스크립트 작성 (PowerShell)

`deploy-to-ec2.ps1` 파일 생성:

```powershell
# EC2 배포 스크립트 (PowerShell)
param(
    [Parameter(Mandatory=$true)]
    [string]$Ec2Ip,
    
    [Parameter(Mandatory=$true)]
    [string]$KeyPath,
    
    [Parameter(Mandatory=$false)]
    [string]$User = "ec2-user"
)

$ErrorActionPreference = "Stop"

Write-Host "==========================================" -ForegroundColor Green
Write-Host "EC2 배포 시작" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

# SSH 키 경로 확인
if (-not (Test-Path $KeyPath)) {
    Write-Host "SSH 키 파일을 찾을 수 없습니다: $KeyPath" -ForegroundColor Red
    exit 1
}

# 로컬에서 빌드
Write-Host "로컬에서 프로젝트 빌드 중..." -ForegroundColor Yellow
./gradlew.bat clean build -x test --no-daemon
if ($LASTEXITCODE -ne 0) {
    Write-Host "빌드 실패!" -ForegroundColor Red
    exit 1
}

# JAR 파일을 EC2로 전송
Write-Host "JAR 파일을 EC2로 전송 중..." -ForegroundColor Yellow
scp -i $KeyPath build/libs/bulletin-0.0.1-SNAPSHOT.jar ${User}@${Ec2Ip}:/tmp/bulletin.jar

# 배포 스크립트 실행
Write-Host "EC2에서 배포 실행 중..." -ForegroundColor Yellow
ssh -i $KeyPath ${User}@${Ec2Ip} @"
sudo mkdir -p /opt/bulletin
sudo cp /tmp/bulletin.jar /opt/bulletin/
sudo chown ec2-user:ec2-user /opt/bulletin/bulletin.jar
sudo systemctl restart bulletin
sudo systemctl status bulletin --no-pager
"@

Write-Host "==========================================" -ForegroundColor Green
Write-Host "배포 완료!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
```

**사용 방법:**
```powershell
.\deploy-to-ec2.ps1 -Ec2Ip "your-ec2-ip" -KeyPath "C:\Users\Administrator\Downloads\bulletin-key.pem"
```

### 2.2 배포 스크립트 작성 (배치 파일)

`deploy-to-ec2.bat` 파일 생성:

```batch
@echo off
setlocal enabledelayedexpansion

echo ==========================================
echo EC2 배포 시작
echo ==========================================

set EC2_IP=your-ec2-ip-here
set KEY_PATH=C:\Users\Administrator\Downloads\bulletin-key.pem
set USER=ec2-user

REM 로컬 빌드
echo 로컬에서 프로젝트 빌드 중...
call gradlew.bat clean build -x test --no-daemon
if errorlevel 1 (
    echo 빌드 실패!
    exit /b 1
)

REM JAR 파일 전송
echo JAR 파일을 EC2로 전송 중...
scp -i "%KEY_PATH%" build\libs\bulletin-0.0.1-SNAPSHOT.jar %USER%@%EC2_IP%:/tmp/bulletin.jar

REM EC2에서 배포 실행
echo EC2에서 배포 실행 중...
ssh -i "%KEY_PATH%" %USER%@%EC2_IP% "sudo mkdir -p /opt/bulletin && sudo cp /tmp/bulletin.jar /opt/bulletin/ && sudo chown ec2-user:ec2-user /opt/bulletin/bulletin.jar && sudo systemctl restart bulletin"

echo ==========================================
echo 배포 완료!
echo ==========================================
```

## 방법 3: WinSCP + PuTTY 사용 (GUI 방식)

### 3.1 WinSCP 설치

1. https://winscp.net/ 에서 WinSCP 다운로드 및 설치
2. 세션 설정:
   - 호스트 이름: EC2 퍼블릭 IP
   - 사용자 이름: ec2-user (Amazon Linux) 또는 ubuntu (Ubuntu)
   - 비밀번호: (키 파일 사용)
   - 고급 > SSH > 인증 > 개인 키 파일: `.pem` 파일 선택

### 3.2 파일 업로드 및 실행

1. WinSCP로 연결 후 JAR 파일 업로드
2. PuTTY로 SSH 접속하여 배포 명령 실행

## 방법 4: AWS Systems Manager Session Manager 사용

AWS Systems Manager를 사용하면 SSH 키 없이 EC2에 접속할 수 있습니다.

### 4.1 IAM 역할 설정

EC2 인스턴스에 다음 정책이 있는 IAM 역할 연결:
- `AmazonSSMManagedInstanceCore`

### 4.2 AWS CLI로 접속

```cmd
aws ssm start-session --target i-1234567890abcdef0 --region ap-northeast-2
```

### 4.3 배포 실행

접속 후 일반적인 리눅스 명령어 사용 가능:

```bash
cd /opt/bulletin
sudo git pull origin main
sudo ./deploy.sh
```

## 추천 워크플로우

### 개발 중 (Windows)
```cmd
# 로컬에서 개발 및 테스트
gradlew.bat bootRun

# 빌드
gradlew.bat clean build -x test
```

### 배포 시 (Windows CMD/PowerShell)
```powershell
# 방법 1: SSH로 접속하여 배포
ssh -i "C:\path\to\key.pem" ec2-user@your-ec2-ip
# EC2에서:
cd /opt/bulletin
sudo git pull origin main
sudo ./deploy.sh

# 방법 2: 배포 스크립트 사용
.\deploy-to-ec2.ps1 -Ec2Ip "your-ec2-ip" -KeyPath "C:\path\to\key.pem"
```

## 팁

### 1. SSH Config 파일 사용 (PowerShell)

`C:\Users\Administrator\.ssh\config` 파일 생성:

```
Host bulletin-ec2
    HostName your-ec2-ip
    User ec2-user
    IdentityFile C:\Users\Administrator\Downloads\bulletin-key.pem
```

**사용:**
```powershell
ssh bulletin-ec2
```

### 2. PowerShell 프로필에 함수 추가

`notepad $PROFILE` 실행 후:

```powershell
function Connect-BulletinEC2 {
    ssh -i "C:\Users\Administrator\Downloads\bulletin-key.pem" ec2-user@your-ec2-ip
}

function Deploy-Bulletin {
    param([string]$Ec2Ip = "your-ec2-ip")
    ssh -i "C:\Users\Administrator\Downloads\bulletin-key.pem" ec2-user@$Ec2Ip "cd /opt/bulletin && sudo git pull && sudo ./deploy.sh"
}
```

**사용:**
```powershell
Connect-BulletinEC2
Deploy-Bulletin
```

### 3. Visual Studio Code Remote SSH 확장

VS Code를 사용하는 경우:
1. Remote - SSH 확장 설치
2. SSH Config 파일 설정
3. VS Code에서 직접 EC2 폴더 열기 및 편집 가능

## 문제 해결

### SSH 접속 오류

**"Permission denied (publickey)" 오류:**
```powershell
# SSH 키 파일 권한 확인
icacls "C:\path\to\key.pem"
# 권한 설정
icacls "C:\path\to\key.pem" /inheritance:r
icacls "C:\path\to\key.pem" /grant:r "%username%:R"
```

### SCP 명령어를 찾을 수 없음

OpenSSH가 설치되어 있는지 확인:
```powershell
Get-WindowsCapability -Online | Where-Object Name -like 'OpenSSH*'
# 설치되어 있지 않으면:
Add-WindowsCapability -Online -Name OpenSSH.Client~~~~0.0.1.0
```

## 참고 자료

- [AWS CLI 사용 가이드](https://docs.aws.amazon.com/cli/latest/userguide/)
- [Windows에서 OpenSSH 사용](https://docs.microsoft.com/windows-server/administration/openssh/openssh_install_firstuse)
- [AWS Systems Manager Session Manager](https://docs.aws.amazon.com/systems-manager/latest/userguide/session-manager.html)

