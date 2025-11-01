#!/bin/bash

# CodeDeploy BeforeInstall Hook

set -e

APP_DIR="/opt/bulletin"

echo "BeforeInstall: 디렉토리 준비 중..."

# 디렉토리 생성
sudo mkdir -p $APP_DIR/{logs,uploads/bulletin-images,uploads/sheet-music,uploads/small-groups}

# 기존 JAR 백업
if [ -f "$APP_DIR/app.jar" ]; then
  sudo cp "$APP_DIR/app.jar" "$APP_DIR/app.jar.backup.$(date +%s)"
  echo "기존 파일 백업 완료"
fi

echo "BeforeInstall 완료"

