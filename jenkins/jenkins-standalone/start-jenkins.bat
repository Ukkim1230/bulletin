@echo off
set "JAVA_HOME=C:\work\jdk-17.0.2"
set "JENKINS_HOME=C:\Users\Administrator\git\bulletin\jenkins\jenkins-standalone\jenkins-home"
echo Jenkins 시작 중...
echo - Jenkins Home: %JENKINS_HOME%
echo - Java Home: %JAVA_HOME%
echo - 포트: 8080
echo - 접속 URL: http://localhost:8080
echo.
echo Jenkins를 중지하려면 Ctrl+C를 누르세요.
echo.
"%JAVA_HOME%\bin\java" -jar jenkins.war --httpPort=8080
