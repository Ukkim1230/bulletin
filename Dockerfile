FROM openjdk:17-jdk-slim

WORKDIR /app

# Gradle wrapper와 설정 파일들 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src src

# Gradle 실행 권한 부여
RUN chmod +x ./gradlew

# Java 환경 변수 설정
ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH=$JAVA_HOME/bin:$PATH

# 빌드 실행
RUN ./gradlew clean build -x test --no-daemon

# 업로드 디렉토리 생성
RUN mkdir -p uploads/bulletin-images uploads/sheet-music uploads/small-groups logs

# JAR 파일 실행
EXPOSE 8080
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-ec2}

# Docker에서 실행 시 사용 (EC2 환경)
CMD ["sh", "-c", "export PORT=${PORT:-8080} && echo 'Starting with PORT='$PORT && echo 'Profile='$SPRING_PROFILES_ACTIVE && java -Xms512m -Xmx1024m -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-ec2} -jar build/libs/bulletin-0.0.1-SNAPSHOT.jar"]
