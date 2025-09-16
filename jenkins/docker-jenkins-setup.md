# 🐳 Docker로 Jenkins 설치하기

Docker를 사용해서 Jenkins를 쉽게 설치하고 실행하는 방법입니다!

## 📥 **1단계: Docker Desktop 설치**

### **Docker Desktop 다운로드 및 설치**
1. **Docker Desktop 공식 사이트** 접속: https://www.docker.com/products/docker-desktop/
2. **"Download for Windows"** 클릭
3. `Docker Desktop Installer.exe` 다운로드
4. **설치 파일 실행**:
   - "Use WSL 2 instead of Hyper-V" 체크 (권장)
   - 설치 완료 후 재부팅 필요할 수 있음

### **Docker 설치 확인**
```cmd
docker --version
docker-compose --version
```

---

## 🚀 **2단계: Jenkins Docker 컨테이너 실행**

### **방법 A: 간단한 실행**
```cmd
# Jenkins JDK17 이미지 다운로드 및 실행
docker run -d ^
  --name jenkins-youth-bulletin ^
  --restart unless-stopped ^
  -p 8080:8080 ^
  -p 50000:50000 ^
  -v jenkins_home:/var/jenkins_home ^
  jenkins/jenkins:jdk17
```

### **방법 B: Docker Compose 사용 (추천)**

**docker-compose.yml 파일 생성:**
```yaml
version: '3.8'

services:
  jenkins:
    image: jenkins/jenkins:jdk17
    container_name: jenkins-youth-bulletin
    restart: unless-stopped
    ports:
      - "8080:8080"
      - "50000:50000"
    volumes:
      # Jenkins 데이터 영구 저장
      - jenkins_home:/var/jenkins_home
      # Docker 소켓 마운트 (Docker-in-Docker)
      - /var/run/docker.sock:/var/run/docker.sock
      # 프로젝트 소스 마운트
      - ../:/workspace/bulletin
    environment:
      - JENKINS_OPTS="--httpPort=8080"
      - JAVA_OPTS="-Xmx1024m -XX:MaxPermSize=256m"
    networks:
      - jenkins-network

volumes:
  jenkins_home:
    driver: local

networks:
  jenkins-network:
    driver: bridge
```

**실행 명령:**
```cmd
# Docker Compose로 Jenkins 시작
docker-compose up -d

# 로그 확인
docker-compose logs -f jenkins

# 중지
docker-compose down
```

---

## 🔧 **3단계: Jenkins 초기 설정**

### **1. 초기 관리자 비밀번호 확인**
```cmd
# Docker 컨테이너에서 초기 비밀번호 확인
docker exec jenkins-youth-bulletin cat /var/jenkins_home/secrets/initialAdminPassword
```

### **2. 브라우저 접속**
- **URL**: http://localhost:8080
- **초기 비밀번호 입력**

### **3. 플러그인 설치**
**추천 플러그인:**
- Git
- Pipeline
- Docker Pipeline
- Gradle
- HTML Publisher
- Test Results Analyzer
- Blue Ocean (선택사항)

---

## 📋 **4단계: 청년부 주보 파이프라인 설정**

### **Docker 기반 Jenkinsfile**
```groovy
pipeline {
    agent {
        docker {
            image 'openjdk:17-jdk'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    
    environment {
        APP_NAME = 'youth-bulletin'
        DOCKER_IMAGE = "${APP_NAME}:${BUILD_NUMBER}"
        DEPLOY_PORT = '80'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '소스 코드 체크아웃 중...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo 'Gradle 빌드 시작...'
                sh './gradlew clean build -x test --no-daemon'
            }
        }
        
        stage('Test') {
            steps {
                echo '테스트 실행 중...'
                sh './gradlew test --no-daemon'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'build/test-results/test/*.xml'
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: 'Test Report'
                    ])
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                echo 'Docker 이미지 빌드 중...'
                script {
                    def app = docker.build("${DOCKER_IMAGE}")
                    docker.withRegistry('', '') {
                        app.push()
                        app.push("latest")
                    }
                }
            }
        }
        
        stage('Deploy') {
            steps {
                echo '새 컨테이너 배포 중...'
                script {
                    // 이전 컨테이너 중지 및 제거
                    sh "docker stop ${APP_NAME} || true"
                    sh "docker rm ${APP_NAME} || true"
                    
                    // 새 컨테이너 실행
                    sh """
                        docker run -d \\
                            --name ${APP_NAME} \\
                            --restart unless-stopped \\
                            -p ${DEPLOY_PORT}:80 \\
                            -e SPRING_PROFILES_ACTIVE=prod \\
                            ${DOCKER_IMAGE}
                    """
                }
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
                            sh "curl -f http://localhost:${DEPLOY_PORT}/api/bulletin/today"
                            healthCheckPassed = true
                            echo "✅ 헬스체크 성공!"
                        } catch (Exception e) {
                            retryCount++
                            echo "⚠️ 헬스체크 재시도 중... (${retryCount}/${maxRetries})"
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
        always {
            // 워크스페이스 정리
            cleanWs()
        }
    }
}
```

---

## 🛠️ **유용한 Docker 명령어**

### **Jenkins 컨테이너 관리**
```cmd
# Jenkins 컨테이너 상태 확인
docker ps | findstr jenkins

# Jenkins 로그 확인
docker logs jenkins-youth-bulletin

# Jenkins 컨테이너 재시작
docker restart jenkins-youth-bulletin

# Jenkins 컨테이너 중지
docker stop jenkins-youth-bulletin

# Jenkins 컨테이너 제거
docker rm jenkins-youth-bulletin

# Jenkins 볼륨 확인
docker volume ls | findstr jenkins
```

### **청년부 주보 애플리케이션 관리**
```cmd
# 애플리케이션 컨테이너 상태
docker ps | findstr youth-bulletin

# 애플리케이션 로그 확인
docker logs youth-bulletin

# 포트 확인
netstat -ano | findstr :80
```

---

## 🔧 **문제 해결**

### **Docker Desktop이 시작되지 않을 때**
1. **WSL 2 설치 확인**:
   ```cmd
   wsl --install
   wsl --set-default-version 2
   ```

2. **Hyper-V 활성화** (필요시):
   - Windows 기능에서 "Hyper-V" 체크
   - 재부팅

### **포트 충돌 시**
```cmd
# 포트 8080 사용 프로세스 확인
netstat -ano | findstr :8080

# Jenkins 포트 변경
docker run -p 8081:8080 jenkins/jenkins:jdk17
```

### **권한 오류 시**
- **Docker Desktop을 관리자 권한으로 실행**
- **PowerShell을 관리자 권한으로 실행**

---

## 🎉 **완료!**

### **설치 순서:**
1. ✅ **Docker Desktop 설치**
2. ✅ **Jenkins Docker 컨테이너 실행**
3. ✅ **Jenkins 초기 설정**
4. ✅ **파이프라인 생성 및 설정**
5. ✅ **첫 번째 빌드 실행**

### **최종 결과:**
- **Jenkins**: http://localhost:8080 (CI/CD 관리)
- **청년부 주보**: http://localhost/mobile (모바일 앱)
- **완전 자동화**: 코드 변경 → 자동 빌드 → 자동 배포

**Docker Desktop 설치 후 언제든 말씀하세요!** 🚀

