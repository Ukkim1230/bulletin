#!/bin/bash

# CodeBuild/CodePipeline에서 EC2에 직접 배포하는 스크립트

set -e

APP_DIR="/opt/bulletin"
SERVICE_NAME="bulletin"
RUN_USER="${RUN_USER:-ec2-user}"

echo "=========================================="
echo "EC2 배포 스크립트 시작"
echo "=========================================="

# JAR 파일 찾기
JAR_FILE=$(ls -1 build/libs/*SNAPSHOT*.jar | head -n1)

if [ -z "$JAR_FILE" ]; then
  echo "❌ JAR 파일을 찾을 수 없습니다!"
  exit 1
fi

echo "✅ JAR 파일 발견: $JAR_FILE"

# 디렉토리 생성
sudo mkdir -p $APP_DIR/{logs,uploads/bulletin-images,uploads/sheet-music,uploads/small-groups}

# 기존 JAR 백업
if [ -f "$APP_DIR/app.jar" ]; then
  sudo cp "$APP_DIR/app.jar" "$APP_DIR/app.jar.backup.$(date +%s)"
  echo "📦 기존 파일 백업 완료"
fi

# JAR 파일 복사
sudo cp "$JAR_FILE" $APP_DIR/app.jar
sudo chmod 644 $APP_DIR/app.jar
sudo chown -R $RUN_USER:$RUN_USER $APP_DIR

echo "✅ 파일 복사 완료"

# 서비스 재시작
if [ -f /etc/systemd/system/bulletin.service ]; then
  sudo systemctl restart $SERVICE_NAME
  echo "✅ 서비스 재시작 완료"
else
  echo "⚠️ 서비스 파일이 없습니다. 수동으로 설정하세요."
fi

echo "=========================================="
echo "배포 완료"
echo "=========================================="

