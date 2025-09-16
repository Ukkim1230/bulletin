@echo off
echo ========================================
echo 청년부 모바일 주보 배포 스크립트
echo ========================================

REM 현재 시간 출력
echo 배포 시작: %date% %time%

REM 기존 컨테이너 중지 및 제거
echo.
echo 1. 기존 컨테이너 정리 중...
docker-compose down

REM 이미지 빌드
echo.
echo 2. 새 이미지 빌드 중...
docker-compose build --no-cache

REM 컨테이너 시작
echo.
echo 3. 서비스 시작 중...
docker-compose up -d

REM 서비스 상태 확인
echo.
echo 4. 서비스 상태 확인 중...
timeout 10
docker-compose ps

REM 로그 확인
echo.
echo 5. 애플리케이션 로그 확인...
docker-compose logs --tail=20 bulletin-app

echo.
echo ========================================
echo 배포 완료!
echo ========================================
echo.
echo 접속 주소:
echo - HTTP:  http://localhost
echo - 모바일: http://localhost/mobile
echo - API:   http://localhost/swagger-ui.html
echo.
echo 로그 실시간 확인: docker-compose logs -f bulletin-app
echo 서비스 중지:     docker-compose down
echo.
pause
