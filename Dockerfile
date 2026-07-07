# 배포 전용 이미지. (로컬 학습 측정은 여전히 host에서 ./gradlew bootRun 으로 돈다)
# 원격 서버에 Java가 없으므로 앱을 컨테이너로 빌드/실행한다.

# ── build stage ──────────────────────────────────────────────
# gradlew 래퍼를 그대로 써서 프로젝트가 고정한 Gradle 버전으로 빌드한다.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 의존성 캐시 레이어: 빌드 스크립트/래퍼만 먼저 복사
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle build.gradle ./
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

# ── runtime stage ────────────────────────────────────────────
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
