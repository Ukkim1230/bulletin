# AWS 콘솔을 통한 EC2 배포 체크리스트

## 1. EC2 인스턴스 생성

### 인스턴스 설정
- ✅ AMI: Amazon Linux 2 또는 Ubuntu 22.04
- ✅ 인스턴스 타입: t2.micro 또는 t3.small (프리티어 가능)
- ✅ 키 페어: 새로 생성하거나 기존 키 페어 선택 (.pem 파일 다운로드)
- ✅ 보안 그룹: 
  - SSH (22) - 내 IP에서만 허용
  - HTTP (80) - 모든 IP 또는 특정 IP
  - HTTPS (443) - 선택사항
  - 커스텀 TCP (8080) - 애플리케이션 포트

## 2. EC2 인스턴스 접속

### SSH 접속
```bash
ssh -i "your-key.pem" ec2-user@your-ec2-ip
```

## 3. 필수 패키지 설치

### Amazon Linux 2
```bash
sudo yum update -y
sudo yum install -y java-17-amazon-corretto git postgresql15-server
```

### Ubuntu
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk git postgresql postgresql-contrib
```

## 4. PostgreSQL 설정 (로컬 DB 사용 시)

```bash
sudo postgresql-setup initdb
sudo systemctl enable postgresql
sudo systemctl start postgresql

sudo -u postgres psql << EOF
CREATE DATABASE bulletin;
CREATE USER bulletin_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE bulletin TO bulletin_user;
\q
EOF
```

## 5. 프로젝트 배포

### 방법 A: Git을 통한 배포
```bash
cd /opt
sudo git clone https://github.com/your-repo/bulletin.git
sudo chown -R ec2-user:ec2-user bulletin
cd bulletin

# 환경 변수 설정
cp env.template .env
sudo nano .env  # 실제 값으로 수정

# 배포 실행
chmod +x deploy.sh
sudo ./deploy.sh
```

### 방법 B: 파일 직접 업로드
1. AWS 콘솔 → EC2 → 인스턴스 선택
2. "연결" → "SSH 클라이언트" 탭
3. SCP를 사용하여 파일 전송:

```bash
# 로컬에서 실행
scp -i "your-key.pem" build/libs/bulletin-0.0.1-SNAPSHOT.jar ec2-user@your-ec2-ip:/tmp/
```

## 6. 환경 변수 설정

`.env` 파일에 다음 내용 설정:
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/bulletin
DB_USERNAME=bulletin_user
DB_PASSWORD=your_password
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
APP_URL=http://your-ec2-ip:8080
PORT=8080
SPRING_PROFILES_ACTIVE=ec2
```

## 7. 서비스 시작

```bash
sudo systemctl start bulletin
sudo systemctl enable bulletin
sudo systemctl status bulletin
```

## 8. 방화벽 설정 확인

```bash
# 보안 그룹에서 포트 8080 열려있는지 확인
# 또는 로컬 방화벽 확인
sudo firewall-cmd --list-all  # Amazon Linux 2
sudo ufw status              # Ubuntu
```

## 9. 애플리케이션 접속 테스트

브라우저에서: `http://your-ec2-ip:8080`

## 10. 로그 확인

```bash
# 서비스 로그
sudo journalctl -u bulletin -f

# 애플리케이션 로그
tail -f /opt/bulletin/logs/bulletin.log
```

## 문제 해결

### 포트가 열리지 않는 경우
- AWS 콘솔 → EC2 → Security Groups → 인바운드 규칙 확인
- 포트 8080 (또는 80) 추가

### 서비스가 시작되지 않는 경우
```bash
sudo journalctl -u bulletin -n 50
```

### 데이터베이스 연결 오류
```bash
# PostgreSQL 서비스 확인
sudo systemctl status postgresql

# 연결 테스트
psql -h localhost -U bulletin_user -d bulletin
```

## 유용한 AWS 콘솔 링크

- EC2 대시보드: https://console.aws.amazon.com/ec2/
- RDS (PostgreSQL 사용 시): https://console.aws.amazon.com/rds/
- CloudWatch (로그 모니터링): https://console.aws.amazon.com/cloudwatch/

## 다음 단계

1. 도메인 연결 (Route 53 사용)
2. HTTPS 설정 (Let's Encrypt 또는 ACM)
3. 자동 백업 설정 (RDS 또는 EBS 스냅샷)
4. 모니터링 설정 (CloudWatch)

