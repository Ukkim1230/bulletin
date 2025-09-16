@echo off
echo Jenkins 프로세스를 찾아 중지합니다...
taskkill /f /im java.exe 2>nul
echo Jenkins가 중지되었습니다.
pause
