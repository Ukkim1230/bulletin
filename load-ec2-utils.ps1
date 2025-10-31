# PowerShell 프로필에 EC2 유틸리티 추가하기
# 
# 사용 방법:
# 1. PowerShell 프로필 열기:
#    notepad $PROFILE
#
# 2. 다음 줄 추가:
#    . C:\Users\Administrator\git\bulletin\ec2-utils.ps1
#
# 3. 또는 매번 수동으로 로드:
#    . .\ec2-utils.ps1

# 현재 PowerShell 세션에 유틸리티 함수 로드
. .\ec2-utils.ps1

# 설정 확인
Show-EC2Config

Write-Host ""
Write-Host "사용 가능한 명령어:" -ForegroundColor Yellow
Write-Host "  Connect-EC2           - EC2에 SSH 접속" -ForegroundColor White
Write-Host "  Build-Project         - 프로젝트 빌드" -ForegroundColor White
Write-Host "  Deploy-EC2 -Build     - 빌드 후 배포" -ForegroundColor White
Write-Host "  Get-EC2ServiceStatus  - 서비스 상태 확인" -ForegroundColor White
Write-Host "  Get-EC2Logs           - 로그 확인" -ForegroundColor White
Write-Host "  Show-EC2Help          - 전체 도움말" -ForegroundColor White
Write-Host ""

