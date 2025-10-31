@echo off
REM 긴급 보안 조치 스크립트 (Windows)

echo ==========================================
echo 긴급 보안 조치: Git 히스토리 정리
echo ==========================================
echo.
echo ⚠️  주의: 이 작업은 Git 히스토리를 재작성합니다!
echo.

REM 1. 현재 상태 확인
echo 1. 현재 remote 확인...
git remote -v
echo.

REM 2. 백업 브랜치 생성
echo 2. 백업 브랜치 생성...
git branch backup-before-cleanup
echo 백업 완료: backup-before-cleanup
echo.

REM 3. application.yml 히스토리에서 제거
echo 3. Git 히스토리에서 application.yml 제거...
git filter-branch --force --index-filter "git rm --cached --ignore-unmatch src/main/resources/application.yml" --prune-empty --tag-name-filter cat -- --all

REM 4. 리팩토링
echo 4. Git 리팩토링...
git reflog expire --expire=now --all
git gc --prune=now --aggressive
echo.

echo ==========================================
echo 히스토리 정리 완료!
echo ==========================================
echo.
echo 다음 단계:
echo 1. git push origin --force --all
echo 2. git push origin --force --tags
echo.
echo ⚠️  강제 푸시는 신중하게 진행하세요!

