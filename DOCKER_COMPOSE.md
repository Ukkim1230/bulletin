# Docker Compose를 사용한 로컬 개발 환경

이 파일은 로컬 개발 환경에서 PostgreSQL 데이터베이스와 함께 애플리케이션을 실행하기 위한 Docker Compose 설정입니다.

## 사용 방법

### 1. Docker Compose로 실행

```bash
# 데이터베이스와 애플리케이션 함께 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 중지
docker-compose down
```

### 2. 애플리케이션 접속

- 애플리케이션: http://localhost:8080
- PostgreSQL: localhost:5432

### 3. 환경 변수 설정

`.env` 파일을 생성하고 필요한 환경 변수를 설정하세요:

```bash
DATABASE_URL=jdbc:postgresql://postgres:5432/bulletin
DB_USERNAME=bulletin_user
DB_PASSWORD=bulletin_password
SPRING_PROFILES_ACTIVE=ec2
```

## Docker Compose 파일 구조

- `postgres`: PostgreSQL 15 데이터베이스
- `app`: Spring Boot 애플리케이션

포트:
- 8080: 애플리케이션
- 5432: PostgreSQL (호스트에서 접근 가능)

