@echo off
setlocal enabledelayedexpansion

echo ========================================
echo 청년부 모바일 주보 Jenkins 설정
echo ========================================

REM Docker 설치 확인
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker가 설치되지 않았습니다.
    echo https://www.docker.com/products/docker-desktop 에서 설치해주세요.
    pause
    exit /b 1
)

echo.
echo 1. Jenkins 컨테이너 시작 중...
cd jenkins
docker-compose -f docker-compose-jenkins.yml up -d jenkins

echo.
echo 2. Jenkins 초기화 대기 중... (약 1-2분 소요)
timeout 60

echo.
echo 3. Jenkins 초기 관리자 비밀번호 확인 중...
echo ========================================
echo Jenkins 초기 관리자 비밀번호:
echo ========================================
docker exec jenkins-master cat /var/jenkins_home/secrets/initialAdminPassword
echo ========================================

echo.
echo 4. Jenkins 접속 정보:
echo - URL: http://localhost:8080
echo - 초기 설정을 위해 위의 비밀번호를 사용하세요
echo.

echo 5. Jenkins 플러그인 설치 권장 목록:
echo - Git
echo - Pipeline
echo - Docker Pipeline
echo - Gradle
echo - HTML Publisher
echo - Test Results Analyzer
echo - Blue Ocean (선택사항)
echo.

echo 6. 추가 설정 가이드:
echo.
echo [1단계] 브라우저에서 http://localhost:8080 접속
echo [2단계] 위에 표시된 초기 비밀번호 입력
echo [3단계] "Install suggested plugins" 선택
echo [4단계] 관리자 계정 생성
echo [5단계] Jenkins URL 확인 (http://localhost:8080)
echo [6단계] 새 파이프라인 작업 생성:
echo          - "New Item" 클릭
echo          - "Pipeline" 선택
echo          - 이름: "youth-bulletin-pipeline"
echo          - Pipeline script from SCM 선택 (Git 사용시)
echo          - 또는 Pipeline script 직접 입력
echo.

echo 7. 유용한 명령어:
echo - Jenkins 로그 확인: docker logs jenkins-master
echo - Jenkins 재시작: docker restart jenkins-master
echo - Jenkins 중지: docker-compose -f docker-compose-jenkins.yml down
echo - 전체 정리: docker-compose -f docker-compose-jenkins.yml down -v
echo.

set /p OPEN_BROWSER="브라우저를 자동으로 열까요? (y/N): "
if /i "%OPEN_BROWSER%"=="y" (
    start http://localhost:8080
)

echo.
echo ========================================
echo Jenkins 설치 완료!
echo ========================================
echo.
echo 다음 단계:
echo 1. http://localhost:8080 에서 Jenkins 초기 설정
echo 2. 파이프라인 작업 생성
echo 3. GitHub 연동 설정 (선택사항)
echo 4. 첫 번째 빌드 실행
echo.
pause
