# 🆓 무료 배포 옵션 가이드

AWS 프리티어 이후 비용이 걱정되시나요? 완전 무료로 청년부 모바일 주보를 배포할 수 있는 다양한 옵션들을 제시해드립니다!

## 🚀 추천 무료 배포 플랫폼

### 1. 🚂 **Railway** (가장 추천!)
- **장점**: Spring Boot 완벽 지원, 자동 배포, 무료 플랜 넉넉함
- **무료 한도**: 월 500시간 실행 시간, 1GB RAM
- **설정**: GitHub 연동만으로 자동 배포

```bash
# Railway 배포
railway login
railway init
railway up
```

### 2. 🟣 **Heroku** (클래식)
- **장점**: 오래된 플랫폼, 안정적, 많은 문서
- **무료 한도**: 월 550시간 (수면 모드 있음)
- **단점**: 2022년부터 유료화 (하지만 개인용은 여전히 저렴)

### 3. ▲ **Vercel** (프론트엔드 특화)
- **장점**: 무제한 대역폭, 빠른 CDN
- **단점**: 서버리스 함수만 지원 (Spring Boot 직접 지원 안함)
- **대안**: Next.js로 프론트엔드만 배포하고 API는 다른 곳에

### 4. 🐙 **GitHub Pages + GitHub Actions**
- **장점**: 완전 무료, GitHub 통합
- **단점**: 정적 사이트만 지원
- **대안**: JAMstack 아키텍처로 구성

### 5. 🔥 **Firebase Hosting**
- **장점**: Google 인프라, 빠른 CDN
- **무료 한도**: 월 10GB 저장, 월 125K 함수 호출
- **적용**: Cloud Functions로 API 구현

## 🎯 **가장 실용적인 조합**

### **옵션 A: Railway + PlanetScale (추천)**
```yaml
프론트엔드 + 백엔드: Railway
데이터베이스: PlanetScale (MySQL 호환)
비용: 완전 무료 (소규모 사용시)
```

### **옵션 B: Heroku + PostgreSQL**
```yaml
애플리케이션: Heroku
데이터베이스: Heroku Postgres (무료 플랜)
비용: 월 $7 정도 (취미용으로는 충분)
```

### **옵션 C: Docker + Oracle Cloud (무료 VM)**
```yaml
서버: Oracle Cloud Always Free VM
배포: Docker + Jenkins
데이터베이스: Oracle Autonomous DB (무료)
비용: 완전 무료 (평생!)
```

## 📋 **Railway 배포 가이드** (추천)

### 1. Railway 계정 생성
```
https://railway.app/
```

### 2. GitHub 저장소 연동
```bash
# 프로젝트를 GitHub에 푸시
git init
git add .
git commit -m "청년부 모바일 주보 초기 커밋"
git remote add origin https://github.com/your-username/youth-bulletin.git
git push -u origin main
```

### 3. Railway 프로젝트 생성
1. Railway 대시보드에서 "New Project" 클릭
2. "Deploy from GitHub repo" 선택
3. 저장소 선택
4. 자동 배포 시작!

### 4. 환경 변수 설정
```bash
SPRING_PROFILES_ACTIVE=prod
DB_URL=railway_mysql_url
DB_USERNAME=railway_user
DB_PASSWORD=railway_password
```

### 5. 도메인 설정
- Railway에서 자동으로 `https://your-app.railway.app` 도메인 제공
- 커스텀 도메인도 무료로 연결 가능

## 🗄️ **무료 데이터베이스 옵션**

### 1. **PlanetScale** (MySQL)
- 무료: 1개 DB, 1GB 저장공간
- 장점: 서버리스, 자동 스케일링

### 2. **Supabase** (PostgreSQL)
- 무료: 500MB 저장공간
- 장점: 실시간 기능, 인증 내장

### 3. **MongoDB Atlas**
- 무료: 512MB 저장공간
- 장점: NoSQL, 클라우드 네이티브

### 4. **Railway PostgreSQL**
- Railway 프로젝트에 포함
- 무료 플랜에 포함됨

## 🔧 **Jenkins 로컬 CI/CD**

### 장점
- ✅ 완전 무료
- ✅ 모든 제어권
- ✅ 커스터마이징 자유도
- ✅ 보안 (내부 네트워크)

### 설정 방법
```bash
# Jenkins 설치 및 실행
cd jenkins
setup-jenkins.bat
```

### GitHub Webhook 연동
1. GitHub 저장소 Settings → Webhooks
2. Payload URL: `http://your-server:8080/github-webhook/`
3. Content type: `application/json`
4. Events: Push, Pull request

## 💡 **비용 최적화 팁**

### 1. **수면 모드 활용**
```javascript
// 비활성 시간대 자동 수면
if (new Date().getHours() >= 2 && new Date().getHours() <= 6) {
    // 서버 수면 모드
}
```

### 2. **캐싱 활용**
- Redis 무료 플랜 활용
- CDN으로 정적 자원 서빙
- 브라우저 캐싱 최적화

### 3. **리소스 최적화**
- Docker 이미지 경량화
- 메모리 사용량 최적화
- 불필요한 기능 제거

## 🎉 **최종 추천 구성**

### **🥇 가장 추천: Railway + PlanetScale**
```yaml
비용: 완전 무료
난이도: ⭐⭐☆☆☆
성능: ⭐⭐⭐⭐⭐
확장성: ⭐⭐⭐⭐☆
```

### **🥈 안정적: Heroku + PostgreSQL**
```yaml
비용: 월 $7
난이도: ⭐⭐☆☆☆
성능: ⭐⭐⭐⭐☆
확장성: ⭐⭐⭐⭐⭐
```

### **🥉 완전 무료: Oracle Cloud + Jenkins**
```yaml
비용: 완전 무료
난이도: ⭐⭐⭐⭐☆
성능: ⭐⭐⭐⭐⭐
확장성: ⭐⭐⭐⭐⭐
```

---

## 🚀 **바로 시작하기**

### 1. Jenkins로 로컬 CI/CD
```bash
jenkins\setup-jenkins.bat
```

### 2. Railway 무료 배포
```bash
# GitHub에 코드 푸시 후
# https://railway.app 에서 프로젝트 생성
```

### 3. 현재 로컬 서버 활용
```bash
# 포트 포워딩으로 외부 접속 허용
# 또는 ngrok 사용
```

**어떤 방법을 선택하시겠어요?** 🤔

각 방법의 자세한 설정 가이드를 제공해드릴 수 있습니다!
