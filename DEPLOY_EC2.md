# AWS EC2 배포 가이드

이 문서는 교회 모바일 주보 시스템을 AWS EC2에 배포하는 방법을 설명합니다.

## 사전 요구사항

1. **AWS EC2 인스턴스**
   - Amazon Linux 2 또는 Ubuntu 20.04 이상
   - 최소 2GB RAM 권장
   - SSH 접근 가능

2. **데이터베이스**
   - PostgreSQL 12 이상 (RDS 또는 EC2에 설치)
   - 또는 로컬 PostgreSQL 설치

3. **포트 설정**
   - EC2 Security Group에서 포트 8080 열기 (HTTP)
   - 필요시 포트 443 (HTTPS) 열기

## 1단계: EC2 인스턴스 준비

### 1.1 EC2 인스턴스에 접속

```bash
ssh -i your-key.pem ec2-user@your-ec2-ip
```

### 1.2 필수 패키지 설치

**Amazon Linux 2의 경우:**
```bash
sudo yum update -y
sudo yum install -y java-17-amazon-corretto git
```

**Ubuntu의 경우:**
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk git
```

### 1.3 PostgreSQL 설치 및 설정 (로컬 DB 사용 시)

```bash
# Amazon Linux 2
sudo yum install -y postgresql15-server
sudo postgresql-setup initdb
sudo systemctl enable postgresql
sudo systemctl start postgresql

# 데이터베이스 및 사용자 생성
sudo -u postgres psql << EOF
CREATE DATABASE bulletin;
CREATE USER bulletin_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE bulletin TO bulletin_user;
\q
EOF
```

## 2단계: 프로젝트 배포

### 2.1 프로젝트 클론

```bash
cd /opt
sudo git clone https://github.com/your-repo/bulletin.git
sudo chown -R ec2-user:ec2-user bulletin
cd bulletin
```

### 2.2 환경 변수 설정

```bash
# env.template을 기반으로 .env 파일 생성
cp env.template .env
nano .env  # 또는 vi .env
```

`.env` 파일 수정 예시:
```bash
# 데이터베이스 설정
DATABASE_URL=jdbc:postgresql://localhost:5432/bulletin
DB_USERNAME=bulletin_user
DB_PASSWORD=your_secure_password

# Cloudinary 설정
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# 애플리케이션 설정
APP_URL=http://your-domain.com  # 또는 EC2 퍼블릭 IP
PORT=8080

# 프로필 설정
SPRING_PROFILES_ACTIVE=ec2
```

### 2.3 배포 스크립트 실행

```bash
# 배포 스크립트에 실행 권한 부여
chmod +x deploy.sh

# 배포 실행 (root 권한 필요)
sudo ./deploy.sh
```

또는 수동으로 배포:

```bash
# 빌드
./gradlew clean build -x test --no-daemon

# JAR 파일 복사
sudo mkdir -p /opt/bulletin
sudo cp build/libs/bulletin-0.0.1-SNAPSHOT.jar /opt/bulletin/
sudo cp .env /opt/bulletin/

# 업로드 디렉토리 생성
sudo mkdir -p /opt/bulletin/uploads/{bulletin-images,sheet-music,small-groups}
sudo mkdir -p /opt/bulletin/logs
sudo chown -R ec2-user:ec2-user /opt/bulletin
```

### 2.4 systemd 서비스 설정

```bash
# 서비스 파일 복사
sudo cp bulletin.service /etc/systemd/system/

# 서비스 활성화 및 시작
sudo systemctl daemon-reload
sudo systemctl enable bulletin
sudo systemctl start bulletin

# 서비스 상태 확인
sudo systemctl status bulletin
```

## 3단계: Nginx 리버스 프록시 설정 (선택사항)

HTTPS 및 도메인 연결을 위해 Nginx를 사용할 수 있습니다.

### 3.1 Nginx 설치

```bash
# Amazon Linux 2
sudo amazon-linux-extras install nginx1 -y

# Ubuntu
sudo apt install -y nginx
```

### 3.2 Nginx 설정 파일 생성

```bash
sudo nano /etc/nginx/conf.d/bulletin.conf
```

설정 내용:
```nginx
server {
    listen 80;
    server_name your-domain.com;  # 또는 EC2 퍼블릭 IP

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 파일 업로드 크기 제한 증가
    client_max_body_size 50M;
}
```

### 3.3 Nginx 시작

```bash
sudo systemctl enable nginx
sudo systemctl start nginx
sudo systemctl status nginx
```

## 4단계: 방화벽 설정

### 4.1 Security Group 설정 (AWS 콘솔)

EC2 인스턴스의 Security Group에서:
- 인바운드 규칙: 포트 80 (HTTP), 443 (HTTPS), 22 (SSH)
- 아웃바운드 규칙: 모든 트래픽 허용

### 4.2 로컬 방화벽 설정 (필요시)

```bash
# Amazon Linux 2
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=443/tcp
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload

# Ubuntu
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp
sudo ufw enable
```

## 5단계: 애플리케이션 확인

### 5.1 서비스 상태 확인

```bash
# 서비스 상태
sudo systemctl status bulletin

# 로그 확인
sudo journalctl -u bulletin -f

# 또는 애플리케이션 로그
tail -f /opt/bulletin/logs/bulletin.log
```

### 5.2 웹 브라우저에서 확인

- HTTP: `http://your-ec2-ip:8080`
- 또는 Nginx 사용 시: `http://your-domain.com`

## 6단계: 자동 배포 설정 (선택사항)

### 6.1 GitHub Actions 설정

`.github/workflows/deploy-ec2.yml` 파일 생성:

```yaml
name: Deploy to EC2

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    
    - name: Deploy to EC2
      uses: appleboy/ssh-action@master
      with:
        host: ${{ secrets.EC2_HOST }}
        username: ec2-user
        key: ${{ secrets.EC2_SSH_KEY }}
        script: |
          cd /opt/bulletin
          git pull origin main
          sudo ./deploy.sh
```

## 유용한 명령어

### 서비스 관리

```bash
# 서비스 시작
sudo systemctl start bulletin

# 서비스 중지
sudo systemctl stop bulletin

# 서비스 재시작
sudo systemctl restart bulletin

# 서비스 상태 확인
sudo systemctl status bulletin

# 서비스 로그 확인
sudo journalctl -u bulletin -f
```

### 애플리케이션 로그 확인

```bash
# 실시간 로그
tail -f /opt/bulletin/logs/bulletin.log

# 최근 100줄
tail -n 100 /opt/bulletin/logs/bulletin.log
```

### 업데이트 배포

```bash
cd /opt/bulletin
git pull origin main
sudo ./deploy.sh
```

## 문제 해결

### 서비스가 시작되지 않는 경우

1. 로그 확인:
   ```bash
   sudo journalctl -u bulletin -n 50
   ```

2. 환경 변수 확인:
   ```bash
   cat /opt/bulletin/.env
   ```

3. 데이터베이스 연결 확인:
   ```bash
   psql -h localhost -U bulletin_user -d bulletin
   ```

### 포트가 이미 사용 중인 경우

```bash
# 포트 사용 확인
sudo netstat -tlnp | grep 8080

# 프로세스 종료
sudo kill -9 <PID>
```

### 메모리 부족 문제

`bulletin.service` 파일에서 JVM 메모리 설정 조정:
```ini
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar ...
```

## 보안 권장사항

1. **환경 변수 보안**
   - `.env` 파일 권한 설정: `chmod 600 .env`
   - 민감한 정보는 AWS Secrets Manager 사용 고려

2. **HTTPS 설정**
   - Let's Encrypt를 사용한 무료 SSL 인증서 발급
   - Certbot 사용: `sudo certbot --nginx`

3. **정기 백업**
   - 데이터베이스 백업 스크립트 작성
   - S3에 자동 백업 설정

4. **모니터링**
   - CloudWatch Logs 설정
   - 알람 설정

## 참고 자료

- [Spring Boot Production Ready](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [systemd 서비스 관리](https://www.freedesktop.org/software/systemd/man/systemd.service.html)
- [Nginx 리버스 프록시](https://nginx.org/en/docs/http/ngx_http_proxy_module.html)

