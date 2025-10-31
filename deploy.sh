#!/bin/bash

# 교회 모바일 주보 시스템 EC2 배포 스크립트
# 사용법: ./deploy.sh

set -e  # 에러 발생 시 스크립트 중단

echo "=========================================="
echo "교회 모바일 주보 시스템 배포 시작"
echo "=========================================="

# 색상 정의
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 변수 설정
APP_NAME="bulletin"
APP_DIR="/opt/bulletin"
JAR_FILE="bulletin-0.0.1-SNAPSHOT.jar"
SERVICE_NAME="bulletin"

# 현재 사용자 확인
if [ "$EUID" -ne 0 ]; then 
    echo -e "${RED}이 스크립트는 root 권한으로 실행해야 합니다.${NC}"
    echo "sudo ./deploy.sh 를 사용하세요."
    exit 1
fi

# Java 설치 확인
if ! command -v java &> /dev/null; then
    echo -e "${YELLOW}Java가 설치되어 있지 않습니다. Java 17을 설치합니다...${NC}"
    yum update -y
    yum install -y java-17-amazon-corretto
fi

# Git 설치 확인
if ! command -v git &> /dev/null; then
    echo -e "${YELLOW}Git이 설치되어 있지 않습니다. Git을 설치합니다...${NC}"
    yum install -y git
fi

# 애플리케이션 디렉토리 생성
echo -e "${GREEN}애플리케이션 디렉토리를 생성합니다...${NC}"
mkdir -p $APP_DIR
mkdir -p $APP_DIR/logs
mkdir -p $APP_DIR/uploads/bulletin-images
mkdir -p $APP_DIR/uploads/sheet-music
mkdir -p $APP_DIR/uploads/small-groups

# 프로젝트 빌드
echo -e "${GREEN}프로젝트를 빌드합니다...${NC}"
cd "$(dirname "$0")"

# Gradle wrapper 실행 권한 확인
chmod +x ./gradlew

# 빌드 실행 (테스트 제외)
./gradlew clean build -x test --no-daemon

# JAR 파일 복사
echo -e "${GREEN}JAR 파일을 배포 디렉토리로 복사합니다...${NC}"
cp build/libs/$JAR_FILE $APP_DIR/

# 환경 변수 파일 확인
if [ ! -f "$APP_DIR/.env" ]; then
    echo -e "${YELLOW}.env 파일이 없습니다. env.template을 복사합니다...${NC}"
    cp env.template $APP_DIR/.env
    echo -e "${RED}중요: $APP_DIR/.env 파일을 수정하여 실제 값으로 설정하세요!${NC}"
fi

# systemd 서비스 파일 설치
echo -e "${GREEN}systemd 서비스 파일을 설치합니다...${NC}"
cp bulletin.service /etc/systemd/system/
systemctl daemon-reload

# 서비스 재시작
echo -e "${GREEN}서비스를 재시작합니다...${NC}"
systemctl enable $SERVICE_NAME
systemctl restart $SERVICE_NAME

# 서비스 상태 확인
sleep 3
if systemctl is-active --quiet $SERVICE_NAME; then
    echo -e "${GREEN}=========================================="
    echo "배포가 성공적으로 완료되었습니다!"
    echo "=========================================="
    echo "서비스 상태:"
    systemctl status $SERVICE_NAME --no-pager -l
    echo ""
    echo "로그 확인: journalctl -u $SERVICE_NAME -f"
    echo "서비스 중지: systemctl stop $SERVICE_NAME"
    echo "서비스 시작: systemctl start $SERVICE_NAME"
    echo "서비스 재시작: systemctl restart $SERVICE_NAME"
    echo -e "${NC}"
else
    echo -e "${RED}=========================================="
    echo "배포 중 오류가 발생했습니다!"
    echo "=========================================="
    echo "서비스 상태:"
    systemctl status $SERVICE_NAME --no-pager -l
    echo ""
    echo "로그 확인: journalctl -u $SERVICE_NAME -n 50"
    echo -e "${NC}"
    exit 1
fi

