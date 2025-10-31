# AWS EC2 완전 초보자 가이드

이 가이드는 AWS EC2를 처음 사용하는 분들을 위한 단계별 설명서입니다.

## 목차
1. [AWS 계정 만들기](#1-aws-계정-만들기)
2. [EC2 인스턴스 생성하기](#2-ec2-인스턴스-생성하기)
3. [EC2 인스턴스 접속하기](#3-ec2-인스턴스-접속하기)
4. [기본 명령어 배우기](#4-기본-명령어-배우기)
5. [애플리케이션 배포하기](#5-애플리케이션-배포하기)
6. [방화벽 설정하기](#6-방화벽-설정하기)
7. [문제 해결](#7-문제-해결)

---

## 1. AWS 계정 만들기

### 1.1 AWS 가입
1. https://aws.amazon.com 접속
2. "AWS 계정 만들기" 클릭
3. 이메일, 비밀번호, 신용카드 정보 입력
4. **주의**: 프리티어 사용 시에도 신용카드 등록 필요 (무료 사용량 초과 시에만 과금)

### 1.2 AWS 콘솔 접속
1. https://console.aws.amazon.com 접속
2. 이메일과 비밀번호로 로그인

### 1.3 리전 선택
- 우측 상단에서 리전 선택 (예: `ap-northeast-2` - 서울)
- 리전마다 가격이 다를 수 있습니다

---

## 2. EC2 인스턴스 생성하기

### 2.1 EC2 대시보드 접속
1. AWS 콘솔에서 검색창에 "EC2" 입력
2. "EC2" 클릭하여 EC2 대시보드로 이동

### 2.2 인스턴스 시작
1. 왼쪽 메뉴에서 "인스턴스" 클릭
2. "인스턴스 시작" 버튼 클릭

### 2.3 1단계: 이름 및 태그
- **이름**: 예) `bulletin-server` (원하는 이름 입력)

### 2.4 2단계: 애플리케이션 및 OS 이미지 (AMI) 선택
- **Amazon Linux 2023** 또는 **Amazon Linux 2** 선택 (프리티어 사용 가능)
- 또는 **Ubuntu Server 22.04 LTS** 선택 가능

### 2.5 3단계: 인스턴스 유형 선택
- **t2.micro** 또는 **t3.micro** 선택 (프리티어 사용 가능)
- **무료 체험 사용 가능** 표시 확인

### 2.6 4단계: 키 페어 생성 또는 선택
1. "새 키 페어 생성" 클릭
2. **키 페어 이름** 입력: 예) `bulletin-key`
3. "키 페어 유형": `RSA` 선택
4. "프라이빗 키 파일 형식": `.pem` 선택
5. "키 페어 생성" 클릭
6. **중요**: `.pem` 파일이 자동으로 다운로드됩니다. 이 파일을 안전한 곳에 보관하세요!

### 2.7 5단계: 네트워크 설정
1. **보안 그룹 이름**: 예) `bulletin-sg`
2. **설명**: 예) `Security group for bulletin application`

**인바운드 보안 그룹 규칙 추가**:
- **SSH (포트 22)**:
  - 유형: SSH
  - 소스: 내 IP (또는 특정 IP 주소)
  - 설명: SSH 접속용

- **HTTP (포트 80)**:
  - 유형: HTTP
  - 소스: 어디서나 (0.0.0.0/0)
  - 설명: 웹 접속용

- **커스텀 TCP (포트 8080)**:
  - 유형: 커스텀 TCP
  - 포트 범위: 8080
  - 소스: 어디서나 (0.0.0.0/0)
  - 설명: 애플리케이션 포트

3. "인바운드 보안 그룹 규칙 추가" 버튼을 클릭하여 위 규칙들을 추가

### 2.8 6단계: 스토리지 구성
- 기본값 (8GB gp3) 유지 (프리티어: 30GB까지 무료)

### 2.9 7단계: 고급 세부 정보
- 기본값 유지

### 2.10 인스턴스 시작
1. 오른쪽 하단의 "인스턴스 시작" 버튼 클릭
2. "인스턴스 보기" 클릭

### 2.11 인스턴스 상태 확인
- 상태가 "실행 중"으로 변경될 때까지 대기 (1-2분 소요)
- **퍼블릭 IPv4 주소**를 메모해두세요! 예: `54.123.45.67`

---

## 3. EC2 인스턴스 접속하기

### 3.1 Windows에서 접속 (PowerShell)

#### 방법 1: PowerShell에서 직접 접속
```powershell
# 키 파일이 있는 폴더로 이동
cd C:\Users\Administrator\Downloads

# SSH 접속
ssh -i "bulletin-key.pem" ec2-user@your-ec2-ip

# 예시:
ssh -i "bulletin-key.pem" ec2-user@54.123.45.67
```

**첫 접속 시 확인 메시지**:
```
The authenticity of host '54.123.45.67' can't be established.
Are you sure you want to continue connecting (yes/no/[fingerprint])? 
```
→ `yes` 입력하고 Enter

#### 방법 2: PuTTY 사용 (Windows 전용)
1. PuTTY 다운로드: https://www.putty.org/
2. PuTTYgen으로 `.pem` 파일을 `.ppk` 파일로 변환
3. PuTTY에서 연결 설정:
   - 호스트 이름: `ec2-user@your-ec2-ip`
   - Connection → SSH → Auth → Credentials → Private key file: `.ppk` 파일 선택

### 3.2 Mac/Linux에서 접속
```bash
# 키 파일 권한 설정 (처음 한 번만)
chmod 400 bulletin-key.pem

# SSH 접속
ssh -i bulletin-key.pem ec2-user@your-ec2-ip
```

### 3.3 접속 성공 확인
다음과 같은 메시지가 보이면 성공입니다:
```
       __|  __|_  )
       _|  (     /   Amazon Linux 2023 AMI
      ___|\___|___|

https://aws.amazon.com/amazon-linux-2023/
[ec2-user@ip-xxx-xxx-xxx-xxx ~]$
```

---

## 4. 기본 명령어 배우기

### 4.1 현재 위치 확인
```bash
pwd
# 출력: /home/ec2-user
```

### 4.2 파일 목록 보기
```bash
ls          # 현재 폴더 파일 목록
ls -l       # 자세한 정보 포함
ls -la      # 숨김 파일 포함
```

### 4.3 폴더 이동
```bash
cd /opt                    # /opt 폴더로 이동
cd ..                      # 상위 폴더로 이동
cd ~                       # 홈 디렉토리로 이동
```

### 4.4 폴더 만들기
```bash
mkdir bulletin            # bulletin 폴더 생성
mkdir -p /opt/bulletin     # 여러 단계 폴더 생성
```

### 4.5 파일 편집
```bash
# nano 편집기 사용 (초보자 추천)
nano filename.txt

# 편집 후:
# Ctrl + O: 저장
# Ctrl + X: 종료
```

### 4.6 파일 내용 보기
```bash
cat filename.txt           # 전체 내용 보기
head filename.txt          # 처음 10줄 보기
tail filename.txt         # 마지막 10줄 보기
tail -f filename.txt      # 실시간으로 보기 (로그 확인용)
```

### 4.7 파일 복사/이동/삭제
```bash
cp file1.txt file2.txt    # 복사
mv file1.txt file2.txt    # 이동 또는 이름 변경
rm file.txt               # 파일 삭제
rm -r folder/             # 폴더 삭제
```

### 4.8 권한 설정
```bash
chmod +x script.sh        # 실행 권한 부여
chmod 400 key.pem         # 읽기 전용 (키 파일)
sudo command              # 관리자 권한으로 실행
```

### 4.9 시스템 정보 확인
```bash
# 시스템 정보
uname -a

# 메모리 사용량
free -h

# 디스크 사용량
df -h

# CPU 사용률
top
# 나가기: q

# 네트워크 연결 확인
ping google.com
# 중지: Ctrl + C
```

---

## 5. 애플리케이션 배포하기

### 5.1 필수 패키지 설치

#### Amazon Linux 2023 / Amazon Linux 2
```bash
# 시스템 업데이트
sudo yum update -y

# Java 설치
sudo yum install -y java-17-amazon-corretto

# Git 설치
sudo yum install -y git

# Java 버전 확인
java -version
```

#### Ubuntu
```bash
# 시스템 업데이트
sudo apt update

# Java 설치
sudo apt install -y openjdk-17-jdk

# Git 설치
sudo apt install -y git

# Java 버전 확인
java -version
```

### 5.2 프로젝트 클론
```bash
# /opt 디렉토리로 이동
cd /opt

# 프로젝트 클론
sudo git clone https://github.com/your-repo/bulletin.git

# 소유권 변경
sudo chown -R ec2-user:ec2-user bulletin

# 프로젝트 폴더로 이동
cd bulletin
```

### 5.3 환경 변수 설정
```bash
# env.template을 .env로 복사
cp env.template .env

# 파일 편집
nano .env
```

`.env` 파일 내용 수정:
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/bulletin
DB_USERNAME=bulletin_user
DB_PASSWORD=your_password_here
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
APP_URL=http://your-ec2-ip:8080
PORT=8080
SPRING_PROFILES_ACTIVE=ec2
```

### 5.4 배포 스크립트 실행
```bash
# 배포 스크립트에 실행 권한 부여
chmod +x deploy.sh

# 배포 실행
sudo ./deploy.sh
```

### 5.5 서비스 상태 확인
```bash
# 서비스 상태 확인
sudo systemctl status bulletin

# 서비스 시작
sudo systemctl start bulletin

# 서비스 중지
sudo systemctl stop bulletin

# 서비스 재시작
sudo systemctl restart bulletin

# 서비스 자동 시작 설정
sudo systemctl enable bulletin
```

### 5.6 로그 확인
```bash
# 서비스 로그 실시간 확인
sudo journalctl -u bulletin -f

# 최근 50줄만 보기
sudo journalctl -u bulletin -n 50

# 애플리케이션 로그
tail -f /opt/bulletin/logs/bulletin.log
```

---

## 6. 방화벽 설정하기

### 6.1 보안 그룹 확인 및 수정
1. AWS 콘솔 → EC2 → 인스턴스 선택
2. "보안" 탭 → 보안 그룹 클릭
3. "인바운드 규칙" 탭 → "인바운드 규칙 편집" 클릭
4. 필요한 포트 추가:
   - **8080** (애플리케이션)
   - **80** (HTTP)
   - **443** (HTTPS, 선택사항)

### 6.2 로컬 방화벽 설정 (필요시)

#### Amazon Linux 2
```bash
# 방화벽 상태 확인
sudo firewall-cmd --state

# 포트 열기
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

#### Ubuntu
```bash
# 방화벽 활성화
sudo ufw enable

# 포트 열기
sudo ufw allow 8080/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# 상태 확인
sudo ufw status
```

---

## 7. 문제 해결

### 7.1 SSH 접속이 안 될 때

**문제**: `Permission denied (publickey)`

**해결 방법**:
```bash
# Windows PowerShell에서 키 파일 권한 확인
icacls "C:\path\to\key.pem"

# 권한 설정
icacls "C:\path\to\key.pem" /inheritance:r
icacls "C:\path\to\key.pem" /grant:r "%username%:R"
```

**문제**: `Connection timed out`

**해결 방법**:
1. AWS 콘솔 → Security Group → 인바운드 규칙 확인
2. SSH (22) 포트가 열려있는지 확인
3. 소스를 "내 IP"로 설정했는지 확인

### 7.2 애플리케이션이 시작되지 않을 때

```bash
# 1. 로그 확인
sudo journalctl -u bulletin -n 50

# 2. Java 설치 확인
java -version

# 3. 포트 사용 확인
sudo netstat -tlnp | grep 8080

# 4. 환경 변수 확인
cat /opt/bulletin/.env

# 5. 서비스 파일 확인
cat /etc/systemd/system/bulletin.service
```

### 7.3 웹 페이지에 접속이 안 될 때

1. **보안 그룹 확인**:
   - AWS 콘솔 → Security Group → 포트 8080 열려있는지 확인

2. **서비스 상태 확인**:
   ```bash
   sudo systemctl status bulletin
   ```

3. **포트 확인**:
   ```bash
   sudo netstat -tlnp | grep 8080
   ```

4. **방화벽 확인**:
   ```bash
   sudo firewall-cmd --list-all  # Amazon Linux 2
   sudo ufw status               # Ubuntu
   ```

### 7.4 디스크 공간 부족

```bash
# 디스크 사용량 확인
df -h

# 큰 파일 찾기
du -sh /* | sort -h

# 로그 파일 정리
sudo journalctl --vacuum-time=7d  # 7일 이상된 로그 삭제
```

### 7.5 메모리 부족

```bash
# 메모리 사용량 확인
free -h

# 실행 중인 프로세스 확인
top

# 필요없는 프로세스 종료
sudo kill -9 <PID>
```

---

## 8. 유용한 팁

### 8.1 세션 끊김 방지 (tmux 사용)
```bash
# tmux 설치
sudo yum install -y tmux  # Amazon Linux
sudo apt install -y tmux  # Ubuntu

# tmux 세션 시작
tmux

# 세션에서 나가기 (세션은 유지됨)
Ctrl + B, D

# 세션 다시 접속
tmux attach
```

### 8.2 파일 업로드 (로컬 → EC2)
```bash
# Windows PowerShell에서
scp -i "key.pem" local-file.txt ec2-user@ec2-ip:/home/ec2-user/

# 예시: JAR 파일 업로드
scp -i "bulletin-key.pem" build/libs/bulletin-0.0.1-SNAPSHOT.jar ec2-user@54.123.45.67:/tmp/
```

### 8.3 파일 다운로드 (EC2 → 로컬)
```bash
# Windows PowerShell에서
scp -i "key.pem" ec2-user@ec2-ip:/path/to/file.txt ./

# 예시: 로그 파일 다운로드
scp -i "bulletin-key.pem" ec2-user@54.123.45.67:/opt/bulletin/logs/bulletin.log ./
```

### 8.4 자동 백업 설정
```bash
# crontab 편집
crontab -e

# 매일 새벽 2시에 백업 (예시)
0 2 * * * /opt/bulletin/backup.sh
```

---

## 9. 다음 단계

1. **도메인 연결**: Route 53을 사용하여 도메인 연결
2. **HTTPS 설정**: Let's Encrypt 또는 AWS Certificate Manager 사용
3. **모니터링**: CloudWatch로 로그 및 메트릭 모니터링
4. **백업**: RDS 또는 EBS 스냅샷 설정
5. **로드 밸런서**: 여러 인스턴스 사용 시 ELB 설정

---

## 10. 참고 자료

- **AWS 공식 문서**: https://docs.aws.amazon.com/ec2/
- **AWS 프리티어**: https://aws.amazon.com/ko/free/
- **AWS 요금 계산기**: https://calculator.aws/

---

## 자주 묻는 질문 (FAQ)

### Q: EC2 인스턴스를 중지하면 데이터가 사라지나요?
A: 루트 볼륨이 EBS인 경우 데이터는 유지됩니다. 단, 인스턴스를 종료(terminate)하면 데이터가 삭제됩니다.

### Q: 프리티어는 언제까지 무료인가요?
A: 계정 생성 후 12개월 동안 무료입니다. 일부 서비스는 평생 무료입니다.

### Q: SSH 키 파일을 잃어버렸어요.
A: 새 키 페어를 만들고 인스턴스를 재생성해야 합니다. 또는 AWS Systems Manager Session Manager를 사용할 수 있습니다.

### Q: 인스턴스 비용을 줄이려면?
A: 
- 사용하지 않는 인스턴스 중지
- 적절한 인스턴스 타입 선택
- Reserved Instance 사용
- Spot Instance 고려

---

**이제 EC2를 사용할 준비가 되었습니다!** 🚀

