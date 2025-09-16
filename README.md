# 교회 모바일 주보 시스템

Spring Boot와 Thymeleaf를 사용한 교회 모바일 주보 관리 시스템입니다.

## 주요 기능

### 📱 모바일 최적화
- 반응형 웹 디자인
- 모바일 전용 UI
- 터치 친화적 인터페이스
- 자동 새로고침 기능

### ⛪ 교회 주보 관리
- **예배 정보**: 예배 시간, 설교자, 설교 제목, 본문 말씀
- **찬양 정보**: 찬송가, CCM, 특송 관리
- **공지사항**: 일반/중요 공지사항, 기간별 표시
- **성경구절**: 주간/일일 말씀 관리
- **기도제목**: 카테고리별 기도제목 관리
- **교회 행사**: 다가오는 행사 및 일정 관리

### 🔧 기술적 특징
- REST API 제공
- Swagger UI 통합 (API 문서화)
- JPA/Hibernate 사용
- H2 인메모리 DB (개발용)
- MySQL 지원 (운영용)
- Spring Security 적용

## 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Data JPA**
- **Spring Security**
- **H2 Database** (개발용)
- **MySQL** (운영용)

### Frontend
- **Thymeleaf** (템플릿 엔진)
- **Bootstrap 5.3** (UI 프레임워크)
- **Font Awesome** (아이콘)
- **JavaScript** (AJAX 통신)

### 도구
- **Maven** (빌드 도구)
- **Swagger/OpenAPI 3** (API 문서화)
- **Lombok** (코드 간소화)

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/church/bulletin/
│   │   ├── BulletinApplication.java          # 메인 애플리케이션
│   │   ├── config/                           # 설정 클래스
│   │   │   ├── SecurityConfig.java          # 보안 설정
│   │   │   └── DataInitializer.java         # 초기 데이터 설정
│   │   ├── controller/                       # 컨트롤러
│   │   │   ├── api/BulletinApiController.java  # REST API
│   │   │   └── web/BulletinWebController.java  # 웹 컨트롤러
│   │   ├── entity/                          # JPA 엔티티
│   │   │   ├── WorshipService.java          # 예배 정보
│   │   │   ├── PraiseSong.java              # 찬양 정보
│   │   │   ├── Announcement.java            # 공지사항
│   │   │   ├── BibleVerse.java              # 성경구절
│   │   │   ├── PrayerRequest.java           # 기도제목
│   │   │   ├── ChurchEvent.java             # 교회 행사
│   │   │   └── AdminUser.java               # 관리자 계정
│   │   ├── repository/                      # JPA 리포지토리
│   │   └── service/                         # 비즈니스 로직
│   │       └── BulletinService.java
│   └── resources/
│       ├── application.yml                  # 애플리케이션 설정
│       └── templates/                       # Thymeleaf 템플릿
│           ├── index.html                   # 메인 페이지
│           └── mobile.html                  # 모바일 페이지
└── database_schema.sql                      # 데이터베이스 스키마
```

## 실행 방법

### 1. 사전 요구사항
- Java 17 이상
- Maven 3.6 이상

### 2. 프로젝트 클론 및 실행
```bash
# 프로젝트 디렉토리로 이동
cd bulletin

# Maven을 사용한 빌드 및 실행
mvn spring-boot:run
```

### 3. 애플리케이션 접속
- **메인 페이지**: http://localhost:8080
- **모바일 페이지**: http://localhost:8080/mobile
- **API 문서**: http://localhost:8080/swagger-ui.html
- **H2 콘솔**: http://localhost:8080/h2-console (개발용)

## API 엔드포인트

### 주보 정보 조회
- `GET /api/bulletin/today` - 오늘의 주보 조회
- `GET /api/bulletin/date/{date}` - 특정 날짜 주보 조회

### 예배 정보
- `GET /api/bulletin/worship/recent` - 최근 예배 정보
- `GET /api/bulletin/worship/upcoming` - 다가오는 예배 정보

### 공지사항
- `GET /api/bulletin/announcements` - 현재 공지사항
- `GET /api/bulletin/announcements/important` - 중요 공지사항

### 기타
- `GET /api/bulletin/bible-verse/weekly` - 주간 성경구절
- `GET /api/bulletin/prayer-requests` - 기도제목
- `GET /api/bulletin/events/upcoming` - 다가오는 행사

## 데이터베이스 설정

### 개발 환경 (H2)
기본적으로 H2 인메모리 데이터베이스를 사용하며, 애플리케이션 시작 시 샘플 데이터가 자동으로 생성됩니다.

### 운영 환경 (MySQL)
`application.yml`에서 `spring.profiles.active=prod` 설정 후 MySQL 연결 정보를 환경변수로 설정:

```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export SERVER_PORT=8080
```

## 커스터마이징

### 1. 교회 정보 수정
- `src/main/resources/templates/` 디렉토리의 HTML 파일에서 교회명, 색상 등 수정

### 2. 추가 기능 구현
- Entity 클래스 추가
- Repository 인터페이스 구현  
- Service 로직 추가
- Controller API 추가

### 3. UI 테마 변경
- Bootstrap 변수 오버라이드
- CSS 커스텀 스타일 추가

## 라이센스

이 프로젝트는 MIT 라이센스 하에 제공됩니다.

## 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

**교회 모바일 주보 시스템**으로 더욱 편리하고 현대적인 교회 소통을 경험해보세요! 🙏
