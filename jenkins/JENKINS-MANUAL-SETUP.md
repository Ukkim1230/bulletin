# 🔧 Jenkins 수동 설치 가이드

Jenkins 홈페이지에서 직접 설치하는 방법을 안내해드립니다!

## 📥 **1단계: Jenkins 다운로드**

### **방법 A: Windows Installer (추천)**
1. **Jenkins 공식 홈페이지** 접속: https://www.jenkins.io/download/
2. **"Download Jenkins"** 클릭
3. **"Windows"** 탭 선택
4. **"Download Jenkins for Windows"** 클릭
5. `jenkins.msi` 파일 다운로드

### **방법 B: WAR 파일**
1. **Jenkins 공식 홈페이지** 접속: https://www.jenkins.io/download/
2. **"Generic Java package (.war)"** 클릭
3. `jenkins.war` 파일 다운로드

---

## 🚀 **2단계: Jenkins 설치**

### **방법 A: Windows Installer 사용**

1. **다운로드한 `jenkins.msi` 실행**
2. **설치 마법사 진행**:
   - Welcome 화면에서 **"Next"**
   - 설치 경로 확인 (기본값: `C:\Program Files\Jenkins`)
   - **"Install"** 클릭
   - 설치 완료까지 대기

3. **서비스 자동 시작**:
   - 설치 완료 후 Jenkins가 Windows 서비스로 자동 등록
   - 부팅 시 자동 시작 설정됨

### **방법 B: WAR 파일 직접 실행**

1. **다운로드한 `jenkins.war`를 적당한 폴더에 저장**
   ```
   C:\jenkins\jenkins.war
   ```

2. **명령 프롬프트에서 실행**:
   ```cmd
   cd C:\jenkins
   java -jar jenkins.war --httpPort=8080
   ```

---

## 🔧 **3단계: Jenkins 초기 설정**

### **1. 브라우저 접속**
- **URL**: http://localhost:8080
- **대기**: 초기화에 1-2분 소요

### **2. 초기 관리자 비밀번호 입력**
- **Windows Installer 설치 시**:
  ```
  C:\Program Files\Jenkins\secrets\initialAdminPassword
  ```
- **WAR 파일 실행 시**:
  ```
  C:\Users\[사용자명]\.jenkins\secrets\initialAdminPassword
  ```

### **3. 플러그인 설치**
- **"Install suggested plugins"** 선택 (추천)
- 또는 **"Select plugins to install"**에서 개별 선택
- **필수 플러그인**:
  - Git
  - Pipeline
  - Gradle
  - HTML Publisher
  - Test Results Analyzer

### **4. 관리자 계정 생성**
- **사용자명**: admin
- **비밀번호**: 안전한 비밀번호 설정
- **이메일**: 관리자 이메일 주소

### **5. Jenkins URL 설정**
- **기본값**: http://localhost:8080
- **확인 후 "Save and Finish"**

---

## 📋 **4단계: 청년부 주보 파이프라인 생성**

### **1. 새 파이프라인 작업 생성**
1. Jenkins 메인 화면에서 **"New Item"** 클릭
2. **이름**: `youth-bulletin-pipeline`
3. **유형**: **"Pipeline"** 선택
4. **"OK"** 클릭

### **2. 파이프라인 설정**
1. **"Pipeline"** 섹션으로 스크롤
2. **Definition**: **"Pipeline script"** 선택
3. **Script 입력창**에 다음 내용 복사 붙여넣기:

```groovy
pipeline {
    agent any
    
    environment {
        JAVA_HOME = 'C:\\work\\jdk-17.0.2'
        GRADLE_HOME = "${WORKSPACE}\\gradle"
        APP_NAME = 'youth-bulletin'
        DEPLOY_PORT = '80'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '소스 코드 체크아웃 중...'
                // Git에서 코드 가져오기 (수동으로 워크스페이스에 복사 필요)
                bat 'dir'
            }
        }
        
        stage('Build') {
            steps {
                echo 'Gradle 빌드 시작...'
                bat '''
                    set JAVA_HOME=%JAVA_HOME%
                    gradlew.bat clean build -x test --no-daemon
                '''
            }
        }
        
        stage('Test') {
            steps {
                echo '테스트 실행 중...'
                bat '''
                    set JAVA_HOME=%JAVA_HOME%
                    gradlew.bat test --no-daemon
                '''
            }
        }
        
        stage('Deploy') {
            steps {
                echo '애플리케이션 배포 중...'
                bat '''
                    @echo off
                    echo 이전 애플리케이션 중지...
                    taskkill /f /im java.exe 2>nul || echo "실행 중인 Java 프로세스가 없습니다"
                    
                    echo 새 애플리케이션 시작...
                    set JAVA_HOME=%JAVA_HOME%
                    for %%f in (build\\libs\\*.jar) do set JAR_FILE=%%f
                    
                    start "Youth Bulletin" /min "%JAVA_HOME%\\bin\\java" -jar -Dserver.port=%DEPLOY_PORT% -Dspring.profiles.active=prod "%JAR_FILE%"
                    
                    timeout 10
                    echo 배포 완료!
                '''
            }
        }
        
        stage('Health Check') {
            steps {
                echo '헬스체크 실행 중...'
                script {
                    def maxRetries = 10
                    def retryCount = 0
                    def healthCheckPassed = false
                    
                    while (retryCount < maxRetries && !healthCheckPassed) {
                        try {
                            sleep(time: 5, unit: 'SECONDS')
                            def response = bat(
                                script: "powershell -Command \"try { Invoke-WebRequest -Uri 'http://localhost:${DEPLOY_PORT}/api/bulletin/today' -UseBasicParsing -TimeoutSec 10; exit 0 } catch { exit 1 }\"",
                                returnStatus: true
                            )
                            
                            if (response == 0) {
                                healthCheckPassed = true
                                echo "✅ 헬스체크 성공!"
                            } else {
                                retryCount++
                                echo "⚠️ 헬스체크 재시도 중... (${retryCount}/${maxRetries})"
                            }
                        } catch (Exception e) {
                            retryCount++
                            echo "⚠️ 헬스체크 오류: ${e.getMessage()}"
                        }
                    }
                    
                    if (!healthCheckPassed) {
                        error "헬스체크 실패"
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo """
            🎉 배포 성공!
            
            📱 접속 주소:
            - 메인: http://localhost:${DEPLOY_PORT}/
            - 모바일: http://localhost:${DEPLOY_PORT}/mobile
            - API: http://localhost:${DEPLOY_PORT}/swagger-ui.html
            """
        }
        failure {
            echo "❌ 배포 실패! 로그를 확인하세요."
        }
    }
}
```

4. **"Save"** 클릭

---

## 🗂️ **5단계: 프로젝트 파일 연결**

### **Jenkins 워크스페이스에 프로젝트 복사**

1. **Jenkins 워크스페이스 경로 확인**:
   - Windows Installer: `C:\Program Files\Jenkins\workspace\youth-bulletin-pipeline`
   - WAR 파일: `C:\Users\[사용자명]\.jenkins\workspace\youth-bulletin-pipeline`

2. **프로젝트 파일 복사**:
   ```cmd
   # 현재 프로젝트 디렉토리에서
   xcopy /E /I "C:\Users\Administrator\git\bulletin\*" "C:\Program Files\Jenkins\workspace\youth-bulletin-pipeline"
   ```

3. **또는 Git 연동** (선택사항):
   - GitHub에 프로젝트 업로드
   - Pipeline 설정에서 "Pipeline script from SCM" 선택
   - Git 저장소 URL 입력

---

## 🚀 **6단계: 첫 번째 빌드 실행**

1. **파이프라인 페이지에서 "Build Now" 클릭**
2. **빌드 진행 상황 모니터링**
3. **빌드 로그 확인**: 빌드 번호 클릭 → "Console Output"
4. **성공 시 애플리케이션 접속**: http://localhost/mobile

---

## 🔧 **유용한 Jenkins 명령어**

### **서비스 관리 (Windows Installer 설치 시)**
```cmd
# Jenkins 서비스 시작
net start jenkins

# Jenkins 서비스 중지
net stop jenkins

# Jenkins 서비스 재시작
net stop jenkins && net start jenkins
```

### **WAR 파일 실행 시**
```cmd
# Jenkins 시작
cd C:\jenkins
java -jar jenkins.war --httpPort=8080

# Jenkins 중지
# Ctrl+C 또는 창 닫기
```

---

## 🎯 **문제 해결**

### **포트 충돌 시**
```cmd
# 포트 8080 사용 프로세스 확인
netstat -ano | findstr :8080

# 포트 변경 (WAR 파일 실행 시)
java -jar jenkins.war --httpPort=8081
```

### **Java 경로 오류 시**
1. **환경 변수 확인**: `JAVA_HOME=C:\work\jdk-17.0.2`
2. **Jenkins 시스템 설정**:
   - "Manage Jenkins" → "Global Tool Configuration"
   - "JDK" 섹션에서 Java 경로 설정

### **권한 오류 시**
- **관리자 권한으로 명령 프롬프트 실행**
- **Jenkins 서비스를 관리자 계정으로 실행**

---

## 🎉 **완료!**

이제 Jenkins CI/CD 파이프라인이 준비되었습니다!

### **다음 단계:**
1. ✅ **Jenkins 설치 및 설정**
2. ✅ **파이프라인 생성**
3. ✅ **첫 번째 빌드 실행**
4. 🔄 **GitHub 연동 (선택사항)**
5. 📱 **청년부 사용자들에게 URL 공유**

**질문이나 문제가 있으시면 언제든 말씀하세요!** 🤝

