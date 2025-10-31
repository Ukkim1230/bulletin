@echo off
chcp 65001 >nul 2>&1
REM Windows EC2 Deployment Script
REM Usage: deploy-to-ec2.bat <ec2-ip> [key-path]

setlocal enabledelayedexpansion

REM Check arguments
if "%1"=="" (
    echo [ERROR] EC2 IP is required!
    echo Usage: deploy-to-ec2.bat ^<ec2-ip^> [key-path]
    echo Example: deploy-to-ec2.bat 1.2.3.4 C:\Users\Administrator\Downloads\bulletin-key.pem
    exit /b 1
) else (
    set EC2_IP=%1
)

if "%2"=="" (
    set KEY_PATH=C:\Users\Administrator\Downloads\bulletin-key.pem
) else (
    set KEY_PATH=%2
)

set USER=ec2-user
set APP_DIR=/opt/bulletin

echo ==========================================
echo EC2 Deployment Start
echo ==========================================
echo EC2 IP: %EC2_IP%
echo Key File: %KEY_PATH%
echo ==========================================

REM SSH key file check
if not exist "%KEY_PATH%" (
    echo [ERROR] SSH key file not found: %KEY_PATH%
    echo Please check the key file path.
    exit /b 1
)

REM Local build
echo.
echo [1/4] Building project locally...
call gradlew.bat clean build -x test --no-daemon
if errorlevel 1 (
    echo [ERROR] Build failed!
    exit /b 1
)
echo [OK] Build successful

REM JAR file check
if not exist "build\libs\bulletin-0.0.1-SNAPSHOT.jar" (
    echo [ERROR] JAR file not found!
    exit /b 1
)

REM Upload JAR file to EC2
echo.
echo [2/4] Uploading JAR file to EC2...
scp -i "%KEY_PATH%" build\libs\bulletin-0.0.1-SNAPSHOT.jar %USER%@%EC2_IP%:/tmp/bulletin.jar
if errorlevel 1 (
    echo [ERROR] File upload failed!
    echo Please check:
    echo   1. EC2 IP is correct
    echo   2. SSH key file path is correct
    echo   3. Security Group allows SSH (port 22)
    exit /b 1
)
echo [OK] File upload successful

REM Deploy on EC2
echo.
echo [3/4] Deploying on EC2...
ssh -i "%KEY_PATH%" %USER%@%EC2_IP% "sudo mkdir -p %APP_DIR% && sudo cp /tmp/bulletin.jar %APP_DIR%/bulletin-0.0.1-SNAPSHOT.jar && sudo chown ec2-user:ec2-user %APP_DIR%/bulletin-0.0.1-SNAPSHOT.jar && sudo systemctl restart bulletin"
if errorlevel 1 (
    echo [ERROR] Deployment failed!
    exit /b 1
)
echo [OK] Deployment successful

REM Check service status
echo.
echo [4/4] Checking service status...
timeout /t 3 /nobreak >nul
ssh -i "%KEY_PATH%" %USER%@%EC2_IP% "sudo systemctl status bulletin --no-pager -l"

echo.
echo ==========================================
echo Deployment Complete!
echo ==========================================
echo Application URL: http://%EC2_IP%:8080
echo.

endlocal

