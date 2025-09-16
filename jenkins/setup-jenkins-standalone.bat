@echo off
setlocal enabledelayedexpansion

echo ========================================
echo 청년부 모바일 주보 Jenkins 설치 (Standalone)
echo ========================================

REM Java 설치 확인
set "JAVA_HOME=C:\work\jdk-17.0.2"
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo ERROR: Java가 %JAVA_HOME%에 설치되지 않았습니다.
    pause
    exit /b 1
)

echo Java 확인 완료: %JAVA_HOME%

REM Jenkins 다운로드 디렉토리 생성
if not exist "jenkins-standalone" mkdir jenkins-standalone
cd jenkins-standalone

REM Jenkins WAR 파일 다운로드 (최신 LTS 버전)
echo.
echo 1. Jenkins WAR 파일 다운로드 중...
if not exist "jenkins.war" (
    echo Jenkins WAR 파일을 다운로드하고 있습니다...
    powershell -Command "Invoke-WebRequest -Uri 'https://get.jenkins.io/war-stable/latest/jenkins.war' -OutFile 'jenkins.war'"
    if %errorlevel% neq 0 (
        echo ERROR: Jenkins 다운로드 실패
        echo 수동으로 다운로드하세요: https://get.jenkins.io/war-stable/latest/jenkins.war
        pause
        exit /b 1
    )
) else (
    echo Jenkins WAR 파일이 이미 존재합니다.
)

REM Jenkins 홈 디렉토리 설정
set "JENKINS_HOME=%CD%\jenkins-home"
if not exist "%JENKINS_HOME%" mkdir "%JENKINS_HOME%"

echo.
echo 2. Jenkins 환경 설정...
echo Jenkins Home: %JENKINS_HOME%
echo Java Home: %JAVA_HOME%

REM Jenkins 시작 스크립트 생성
echo.
echo 3. Jenkins 시작 스크립트 생성...
(
echo @echo off
echo set "JAVA_HOME=%JAVA_HOME%"
echo set "JENKINS_HOME=%JENKINS_HOME%"
echo echo Jenkins 시작 중...
echo echo - Jenkins Home: %%JENKINS_HOME%%
echo echo - Java Home: %%JAVA_HOME%%
echo echo - 포트: 8080
echo echo - 접속 URL: http://localhost:8080
echo echo.
echo echo Jenkins를 중지하려면 Ctrl+C를 누르세요.
echo echo.
echo "%%JAVA_HOME%%\bin\java" -jar jenkins.war --httpPort=8080
) > start-jenkins.bat

REM Jenkins 중지 스크립트 생성
(
echo @echo off
echo echo Jenkins 프로세스를 찾아 중지합니다...
echo taskkill /f /im java.exe 2^>nul
echo echo Jenkins가 중지되었습니다.
echo pause
) > stop-jenkins.bat

echo.
echo 4. Jenkins 실행 중...
echo ========================================
echo Jenkins 정보:
echo - 포트: 8080
echo - 홈 디렉토리: %JENKINS_HOME%
echo - 접속 URL: http://localhost:8080
echo ========================================

REM Jenkins 시작
echo.
echo Jenkins를 시작합니다... (초기화에 1-2분 소요)
echo.
start "Jenkins Server" cmd /k "start-jenkins.bat"

REM 초기화 대기
echo 초기화 대기 중...
timeout 30

echo.
echo 5. 초기 관리자 비밀번호 확인...
echo ========================================
if exist "%JENKINS_HOME%\secrets\initialAdminPassword" (
    echo Jenkins 초기 관리자 비밀번호:
    type "%JENKINS_HOME%\secrets\initialAdminPassword"
    echo.
) else (
    echo 초기 비밀번호 파일을 찾을 수 없습니다.
    echo Jenkins가 완전히 시작된 후 다음 파일을 확인하세요:
    echo %JENKINS_HOME%\secrets\initialAdminPassword
)
echo ========================================

echo.
echo 6. Jenkins 설정 가이드:
echo.
echo [1단계] 브라우저에서 http://localhost:8080 접속
echo [2단계] 위에 표시된 초기 비밀번호 입력
echo [3단계] "Install suggested plugins" 선택 후 대기
echo [4단계] 관리자 계정 생성
echo [5단계] Jenkins URL 확인 (http://localhost:8080)
echo [6단계] 새 파이프라인 작업 생성:
echo          - "New Item" 클릭
echo          - 이름: "youth-bulletin-pipeline" 입력
echo          - "Pipeline" 선택 후 OK
echo          - Pipeline 섹션에서 "Pipeline script" 선택
echo          - ../Jenkinsfile 내용을 복사해서 붙여넣기
echo          - Save 클릭
echo [7단계] "Build Now" 클릭하여 첫 번째 빌드 실행
echo.

echo 7. 추천 플러그인 설치:
echo - Git
echo - Pipeline
echo - Gradle
echo - HTML Publisher
echo - Test Results Analyzer
echo.

echo 8. 유용한 명령어:
echo - Jenkins 재시작: stop-jenkins.bat 실행 후 start-jenkins.bat 실행
echo - Jenkins 중지: stop-jenkins.bat 실행
echo - 로그 확인: Jenkins 콘솔 창 확인
echo.

set /p OPEN_BROWSER="브라우저를 자동으로 열까요? (y/N): "
if /i "%OPEN_BROWSER%"=="y" (
    timeout 5
    start http://localhost:8080
)

echo.
echo ========================================
echo Jenkins 설치 완료!
echo ========================================
echo.
echo 현재 상태:
echo - Jenkins 서버: 실행 중 (별도 창)
echo - 접속 주소: http://localhost:8080
echo - 홈 디렉토리: %JENKINS_HOME%
echo.
echo 다음 단계:
echo 1. 브라우저에서 Jenkins 초기 설정
echo 2. 파이프라인 작업 생성 및 설정
echo 3. 첫 번째 빌드 실행
echo.
pause
