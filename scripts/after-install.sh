#!/bin/bash

# CodeDeploy AfterInstall Hook

set -e

APP_DIR="/opt/bulletin"
RUN_USER="${RUN_USER:-ec2-user}"

echo "AfterInstall: 파일 권한 설정 중..."

# 파일 소유권 설정
sudo chown -R $RUN_USER:$RUN_USER $APP_DIR
sudo chmod 644 $APP_DIR/app.jar

# .env 파일 처리
if [ ! -f "$APP_DIR/.env" ]; then
  if [ -f "$APP_DIR/env.template" ]; then
    sudo cp "$APP_DIR/env.template" "$APP_DIR/.env"
    sudo chown $RUN_USER:$RUN_USER "$APP_DIR/.env"
    sudo chmod 600 "$APP_DIR/.env"
    echo ".env 파일 생성됨 (env.template 기반)"
  fi
fi

# 서비스 파일 업데이트
if [ -f "$APP_DIR/bulletin.service" ]; then
  sudo sed -i "s|^User=.*|User=$RUN_USER|" "$APP_DIR/bulletin.service"
  sudo sed -i "s|^Group=.*|Group=$RUN_USER|" "$APP_DIR/bulletin.service"
  sudo sed -i "s|/opt/bulletin/bulletin-0.0.1-SNAPSHOT.jar|/opt/bulletin/app.jar|" "$APP_DIR/bulletin.service"
  sudo cp "$APP_DIR/bulletin.service" /etc/systemd/system/bulletin.service
  sudo systemctl daemon-reload
  echo "서비스 파일 업데이트 완료"
fi

echo "AfterInstall 완료"

