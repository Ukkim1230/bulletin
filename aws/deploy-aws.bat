@echo off
setlocal enabledelayedexpansion

echo ========================================
echo 청년부 모바일 주보 AWS 배포 스크립트
echo ========================================

REM AWS CLI 설치 확인
aws --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: AWS CLI가 설치되지 않았습니다.
    echo https://aws.amazon.com/cli/ 에서 설치해주세요.
    pause
    exit /b 1
)

REM Docker 설치 확인
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker가 설치되지 않았습니다.
    echo https://www.docker.com/products/docker-desktop 에서 설치해주세요.
    pause
    exit /b 1
)

REM Terraform 설치 확인
terraform --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Terraform이 설치되지 않았습니다.
    echo https://www.terraform.io/downloads.html 에서 설치해주세요.
    pause
    exit /b 1
)

echo.
echo 1. AWS 자격 증명 확인 중...
aws sts get-caller-identity
if %errorlevel% neq 0 (
    echo ERROR: AWS 자격 증명이 설정되지 않았습니다.
    echo 'aws configure' 명령어로 설정해주세요.
    pause
    exit /b 1
)

echo.
echo 2. 변수 설정...
set /p AWS_REGION="AWS 리전을 입력하세요 (기본값: ap-northeast-2): "
if "%AWS_REGION%"=="" set AWS_REGION=ap-northeast-2

set /p DB_PASSWORD="데이터베이스 비밀번호를 입력하세요: "
if "%DB_PASSWORD%"=="" (
    echo ERROR: 데이터베이스 비밀번호는 필수입니다.
    pause
    exit /b 1
)

echo.
echo 3. AWS 계정 ID 가져오기...
for /f "tokens=*" %%i in ('aws sts get-caller-identity --query Account --output text') do set AWS_ACCOUNT_ID=%%i
echo AWS 계정 ID: %AWS_ACCOUNT_ID%

echo.
echo 4. Terraform 초기화...
cd aws\terraform
terraform init

echo.
echo 5. Terraform 계획 확인...
terraform plan -var="aws_region=%AWS_REGION%" -var="db_password=%DB_PASSWORD%"

echo.
set /p CONTINUE="배포를 계속하시겠습니까? (y/N): "
if /i not "%CONTINUE%"=="y" (
    echo 배포가 취소되었습니다.
    pause
    exit /b 0
)

echo.
echo 6. Terraform 적용 중...
terraform apply -var="aws_region=%AWS_REGION%" -var="db_password=%DB_PASSWORD%" -auto-approve

if %errorlevel% neq 0 (
    echo ERROR: Terraform 배포에 실패했습니다.
    pause
    exit /b 1
)

echo.
echo 7. ECR 리포지토리 URL 가져오기...
for /f "tokens=*" %%i in ('terraform output -raw ecr_repository_url') do set ECR_URL=%%i
echo ECR URL: %ECR_URL%

echo.
echo 8. ECR 로그인...
aws ecr get-login-password --region %AWS_REGION% | docker login --username AWS --password-stdin %ECR_URL%

echo.
echo 9. Docker 이미지 빌드...
cd ..\..
docker build -t youth-bulletin .

echo.
echo 10. Docker 이미지 태깅...
docker tag youth-bulletin:latest %ECR_URL%:latest

echo.
echo 11. Docker 이미지 푸시...
docker push %ECR_URL%:latest

echo.
echo 12. ECS 서비스 업데이트...
cd aws\terraform
for /f "tokens=*" %%i in ('terraform output -raw ecs_cluster_name') do set CLUSTER_NAME=%%i
for /f "tokens=*" %%i in ('terraform output -raw ecs_service_name') do set SERVICE_NAME=%%i

aws ecs update-service --cluster %CLUSTER_NAME% --service %SERVICE_NAME% --force-new-deployment --region %AWS_REGION%

echo.
echo 13. 배포 상태 확인...
aws ecs wait services-stable --cluster %CLUSTER_NAME% --services %SERVICE_NAME% --region %AWS_REGION%

echo.
echo ========================================
echo 배포 완료!
echo ========================================

echo.
echo 애플리케이션 접속 정보:
terraform output application_urls

echo.
echo 유용한 명령어:
terraform output useful_commands

echo.
echo 로그 확인: 
for /f "tokens=*" %%i in ('terraform output -raw cloudwatch_log_group') do echo aws logs tail %%i --follow --region %AWS_REGION%

echo.
pause
