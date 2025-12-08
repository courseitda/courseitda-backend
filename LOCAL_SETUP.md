# 로컬 개발 환경 설정 가이드

## 사전 요구사항

### 필수 소프트웨어

- Docker Desktop (최신 버전)
- Docker Compose V2
- Java 21
- Gradle 8.x

### 환경 변수 설정

- 프로젝트 루트에 `.env` 파일을 생성하고 필요한 환경 변수를 설정하세요.
- `.env.example` 파일을 참고하거나, 자세한 내용은 notion에서 확인하세요.

### Docker Compose 구조

본 프로젝트는 3개의 Docker Compose 파일로 서비스가 분리되어 있습니다:

- `docker-compose.db.yml` - MySQL 데이터베이스
- `docker-compose.app.yml` - Spring Boot 애플리케이션
- `docker-compose.monitoring.yml` - 모니터링 스택 (Grafana, Prometheus, Loki)

---

## 빠른 시작

### 전체 실행 프로세스

```bash
# 1. 애플리케이션 빌드
./gradlew clean build

# 2. 기존 컨테이너 중지
docker compose -f docker-compose.db.yml \
               -f docker-compose.app.yml \
               -f docker-compose.monitoring.yml \
               down

# 3. Docker 이미지 빌드
docker compose -f docker-compose.app.yml build --no-cache

# 4. 전체 서비스 실행
docker compose -f docker-compose.db.yml \
               -f docker-compose.app.yml \
               -f docker-compose.monitoring.yml \
               up -d

# 5. 애플리케이션 로그 확인
docker logs courseitda-spring-app -f
```

실행 성공 시 로그에서 `Started CourseitdaApplication in X.XXX seconds` 메시지를 확인할 수 있습니다.

---

## 단계별 상세 가이드

### 1. 애플리케이션 빌드

```bash
# 테스트 포함
./gradlew clean build

# 테스트 제외 (빠른 빌드)
./gradlew clean build -x test
```

빌드 완료 후 `build/libs/courseitda-spring.jar` 파일이 생성됩니다.

### 2. 컨테이너 중지

```bash
docker compose -f docker-compose.db.yml \
               -f docker-compose.app.yml \
               -f docker-compose.monitoring.yml \
               down

# 데이터베이스 초기화가 필요한 경우 (볼륨 삭제)
docker compose -f docker-compose.db.yml \
               -f docker-compose.app.yml \
               -f docker-compose.monitoring.yml \
               down -v
```

### 3. Docker 이미지 빌드

```bash
# 캐시 없이 빌드 (권장)
docker compose -f docker-compose.app.yml build --no-cache

# 캐시 사용 (빠른 빌드)
docker compose -f docker-compose.app.yml build
```

### 4. 서비스 실행

```bash
docker compose -f docker-compose.db.yml \
               -f docker-compose.app.yml \
               -f docker-compose.monitoring.yml \
               up -d
```

### 5. 실행 확인

**컨테이너 상태:**

```bash
docker ps
```

**애플리케이션 로그:**

```bash
docker logs courseitda-spring-app -f
```

**헬스체크:**

```bash
curl http://localhost:8080/actuator/health
# 응답: {"status":"UP"}
```

---

## 트러블슈팅

### JAR 파일 없음

Docker 빌드 시 JAR 파일을 찾을 수 없는 경우:

```bash
./gradlew clean build
ls build/libs/*.jar
docker compose -f docker-compose.app.yml build --no-cache
```

### 포트 충돌

```bash
# 포트 사용 중인 프로세스 확인 및 종료
lsof -i :8080
kill -9 <PID>

# 또는 docker-compose.app.yml에서 포트 변경
# ports:
#   - "8081:8080"
```

### MySQL 연결 실패

MySQL 컨테이너가 완전히 시작되기까지 대기 후 애플리케이션 재시작:

```bash
docker logs courseitda-mysql
docker restart courseitda-spring-app
```

### 환경 변수 누락

`.env` 파일이 존재하고 필수 환경 변수가 모두 설정되었는지 확인:

```bash
ls -la .env
cat .env
```

### 코드 변경사항이 반영되지 않는 경우

이미지 캐시를 완전히 제거하고 재빌드:

```bash
docker compose -f docker-compose.db.yml \
               -f docker-compose.app.yml \
               -f docker-compose.monitoring.yml \
               down
docker rmi courseitda-backend-spring-app
./gradlew clean build
docker compose -f docker-compose.app.yml build --no-cache
docker compose -f docker-compose.db.yml \
               -f docker-compose.app.yml \
               -f docker-compose.monitoring.yml \
               up -d
```

---

## 서비스 접속 정보

| 서비스            | URL                            | 설명                   |
|----------------|--------------------------------|----------------------|
| **API 서버**     | http://localhost:8080          | Spring Boot REST API |
| **Actuator**   | http://localhost:8080/actuator | 헬스체크 및 메트릭           |
| **Grafana**    | http://localhost:3001          | 모니터링 대시보드            |
| **Prometheus** | http://localhost:9090          | 메트릭 수집               |
| **Loki**       | http://localhost:3100          | 로그 집계                |

---

## 개발 워크플로우

### 코드 변경 후 재배포

```bash
./gradlew clean build
docker compose -f docker-compose.db.yml \
               -f docker-compose.app.yml \
               -f docker-compose.monitoring.yml \
               down
docker compose -f docker-compose.app.yml build --no-cache
docker compose -f docker-compose.db.yml \
               -f docker-compose.app.yml \
               -f docker-compose.monitoring.yml \
               up -d
docker logs courseitda-spring-app -f
```

### 애플리케이션만 빠르게 재시작

```bash
docker restart courseitda-spring-app
docker logs courseitda-spring-app -f
```

---

## 주의사항

- **데이터 삭제**: `down -v` 옵션 사용 시 데이터베이스 볼륨이 함께 삭제됩니다.
- **환경 변수**: `.env` 파일은 반드시 `.gitignore`에 포함되어야 합니다.
- **빌드 순서**: Gradle 빌드를 먼저 실행한 후 Docker 이미지를 빌드해야 합니다.
- **캐시 이슈**: 코드 변경이 반영되지 않으면 `--no-cache` 옵션을 사용하세요.
