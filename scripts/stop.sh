#!/bin/bash

# CodeDeploy ApplicationStop Hook

set -e

SERVICE_NAME="bulletin"

echo "ApplicationStop: 서비스 중지 중..."

# 서비스 중지 (존재하는 경우)
if sudo systemctl is-active --quiet $SERVICE_NAME; then
  sudo systemctl stop $SERVICE_NAME
  echo "서비스 중지 완료"
else
  echo "서비스가 실행 중이 아닙니다."
fi

echo "ApplicationStop 완료"

