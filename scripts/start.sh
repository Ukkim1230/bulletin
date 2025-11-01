#!/bin/bash

# CodeDeploy ApplicationStart Hook

set -e

SERVICE_NAME="bulletin"

echo "ApplicationStart: 서비스 시작 중..."

# 서비스 활성화 및 시작
sudo systemctl enable $SERVICE_NAME 2>/dev/null || true
sudo systemctl restart $SERVICE_NAME

# 서비스 시작 확인 (최대 30초 대기)
for i in {1..30}; do
  if sudo systemctl is-active --quiet $SERVICE_NAME; then
    echo "✅ 서비스가 정상적으로 시작되었습니다 (${i}초)"
    break
  fi
  if [ $i -eq 30 ]; then
    echo "❌ 서비스 시작 실패 (타임아웃)"
    sudo journalctl -u $SERVICE_NAME -n 50 --no-pager
    exit 1
  fi
  sleep 1
done

echo "ApplicationStart 완료"

