# 멀티 스테이지 빌드를 사용하여 최적화된 이미지 생성
FROM gradle:8.3-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 설정 파일들 복사
COPY build.gradle settings.gradle ./
COPY gradle gradle

# 의존성 다운로드 (캐시 활용)
RUN gradle dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드
RUN gradle bootJar --no-daemon

# 실행 이미지 생성
FROM eclipse-temurin:17-jre

# 메타데이터 설정
LABEL maintainer="youth-ministry"
LABEL description="청년부 모바일 주보 시스템"

# 작업 디렉토리 설정
WORKDIR /app

# 시간대 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# wget 설치 (헬스체크용)
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# 애플리케이션 사용자 생성 (보안)
RUN groupadd -r bulletin && useradd -r -g bulletin bulletin

# 빌드된 JAR 파일 복사 및 이름 확인
COPY --from=builder /app/build/libs/ ./
RUN ls -la && find . -name "*.jar" -exec mv {} app.jar \;

# 파일 권한 설정
RUN chown bulletin:bulletin app.jar

# 사용자 변경
USER bulletin

# 포트 노출
EXPOSE 80

# 헬스체크 추가
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# JVM 옵션 설정
ENV JAVA_OPTS="-Xmx512m -Xms256m -server -XX:+UseG1GC"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
