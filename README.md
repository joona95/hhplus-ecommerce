# HH+ E-Commerce Backend System

고동시성 환경을 가정하고 진행한 **이커머스 주문 백엔드 시스템**입니다.

분산 시스템, 동시성 제어, 캐싱 최적화, 이벤트 기반 아키텍처를 실제로 구현한 프로젝트입니다.

> **기간**: 2025.03 ~ 2025.06 | **팀**: 백엔드 1명 | **아키텍처**: Clean Architecture

---

## 🚀 핵심 기능

| 기능 | 설명 | 특징 |
|------|------|------|
| **포인트 관리** | 충전 / 조회 / 차감 | 낙관적 락으로 동시성 보장 |
| **상품 카탈로그** | 상품 조회 / 재고 관리 | Redis 캐싱, 비관적 락 |
| **선착순 쿠폰** ⭐ | 제한 수량 발급 | Redis 분산 락 |
| **주문 처리** | 다중상품 / 결제 / 할인 | 트랜잭션 일관성 보장 |
| **인기 상품** | Top 5 조회 | Redis 캐시 + 배치 갱신 |
| **데이터 연동** | Kafka 이벤트 발행 | 비동기 처리 |

---

## 🛠 기술 스택

**Framework**: Java 17, Spring Boot 3.4.1, Spring Cloud 2024.0.0

**Database**: MySQL 8.0, JPA/Hibernate, QueryDSL 5.0.0

**Cache & Lock**: Redis 7.4.2, Redisson 3.17.0

**Messaging**: Apache Kafka, Spring Kafka

**Testing**: JUnit 5, Mockito, TestContainers, K6

---

## 🏗 시스템 구조

### Clean Architecture (4계층)

```
Presentation Layer (Controllers, DTOs)
        ↓
Application Layer (Facade Services)
        ↓
Domain Layer (Business Logic, Entities)
        ↓
Infrastructure Layer (Database, Cache, Kafka)
```

**핵심 특징**:
- 도메인 로직이 프레임워크 독립적
- 계층 간 명확한 책임 분리
- 높은 유지보수성 및 테스트 용이성

### 주요 설계 패턴

| 패턴 | 사용 사례 | 효과 |
|------|---------|------|
| **Facade** | OrderFacadeService | 복잡한 비즈니스 로직 단순화 |
| **Event-Driven** | 주문 완료 이벤트 | 느슨한 결합, 확장성 |
| **Distributed Locking** | 쿠폰 발급, 재고 차감 | 레이스 컨디션 제거 |
| **Cache-Aside** | 인기 상품 조회 | DB 부하 감소, 성능 향상 |
| **Optimistic/Pessimistic Locking** | Point/Coupon | 동시성 제어 |
| **Policy Pattern** | 할인 정책 | 새 정책 추가 시 코드 변경 최소화 |

---

## 📦 빠른 시작

### Prerequisites
```bash
Docker & Docker Compose | Java 17+ | Gradle 8.x
```

### 1단계: 인프라 시작
```bash
docker-compose up -d
```

### 2단계: 빌드
```bash
./gradlew clean build
```

### 3단계: 실행
```bash
./gradlew bootRun
```

**API 문서**: http://localhost:8080/swagger-ui.html

---

## 🔌 API 엔드포인트

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/v1/points/{userId}` | 잔액 조회 |
| POST | `/api/v1/points` | 잔액 충전 |
| GET | `/api/v1/items/{itemId}` | 상품 조회 |
| GET | `/api/v1/items/popular` | 인기 상품 Top 5 |
| POST | `/api/v1/coupons/issue` | 쿠폰 발급 |
| GET | `/api/v1/coupons` | 쿠폰 목록 |
| POST | `/api/v1/orders` | 주문 생성 |

자세한 내용: [docs/api.md](docs/api.md)

---

## 🧪 테스트

### 테스트 구성
- **54개** 테스트 파일
- **92개+** 테스트 메서드
- **~75%** 코드 커버리지

| 유형 | 개수 | 기술 |
|------|------|------|
| Unit Test | 28개 | Mockito |
| Integration Test | 14개 | @SpringBootTest + Testcontainers |
| Concurrency Test | 7개 | ExecutorService, CountDownLatch |
| Cache Test | 4개 | @MockitoSpyBean |
| Event Test | 3개 | Kafka, Awaitility |

### 테스트 실행
```bash
./gradlew test                              # 모든 테스트
./gradlew test --tests "*ConcurrencyTest"  # 동시성 테스트만
./gradlew test --tests "PointServiceIntegrationTest"  # 특정 테스트
```

---

## 📊 성능 

### 캐싱 효과 분석

**상품 상세 조회 (부하 테스트 결과)**

| 시나리오 | P95 지연 | 처리량 (RPS) | 효과 |
|---------|---------|-----------|------|
| 캐시 미적용 (100만 균등 랜덤) | 7.48s | 882 | - |
| **캐시 적용** (상위 1000 중복 조회) | **1.67s** | **2,991** | **60% 개선 / 2.5배 증가** |

**테스트 검증** (`ItemCacheTest.java`):
- ✅ 동일 상품 3회 조회 → DB 접근 1회만 수행
- ✅ 캐시 무효화(업데이트) 시 자동 갱신
- ✅ 인기 상품 3회 조회 → 쿼리 1회만 수행

### 인프라 스펙 결정

**부하 테스트** (상품 상세 조회, 2700 VU)

| 환경 | 실패율 | 평균 RPS |
|------|--------|----------|
| 2CPU/4GB | 66.6% | 620 |
| **4CPU/8GB** | **0.02%** | **882** |

**결론**: 안정적 운영을 위해 **최소 4CPU/8GB 필수**

자세한 분석: [docs/cache.md](docs/cache.md) | [docs/scenario_load_test_result.md](docs/scenario_load_test_result.md)

---

## 📚 주요 문서

### 📖 요구사항 & 설계
| 문서 | 내용 |
|------|------|
| [requirements.md](docs/requirements.md) | 프로젝트 요구사항 명세 |
| [api.md](docs/api.md) | REST API 상세 명세서 |
| [erd.md](docs/erd.md) | 데이터베이스 스키마 |

### 🏗 아키텍처 & 성능
| 문서 | 내용 |
|------|------|
| [concurrency.md](docs/concurrency.md) | 동시성 제어 전략 |
| [cache.md](docs/cache.md) | 캐싱 전략 및 구현 |
| [kafka.md](docs/kafka.md) | 이벤트 기반 아키텍처 |
| [sequence_diagram.md](docs/sequence_diagram.md) | 요청 흐름도 |

### 📈 성능 & 테스트
| 문서 | 내용 |
|------|------|
| [scenario_load_test_plan.md](docs/scenario_load_test_plan.md) | 부하 테스트 계획 |
| [scenario_load_test_result.md](docs/scenario_load_test_result.md) | 부하 테스트 분석 |
| [report.md](docs/report.md) | 인기 상품 성능 분석 |

### 💻 개발 가이드
| 문서 | 내용 |
|------|------|
| [CLAUDE.md](CLAUDE.md) | AI 기반 개발 & 테스트 자동화 |

---

## 📁 프로젝트 구조

```
src/main/java/kr/hhplus/be/server/
├── domain/              # 비즈니스 로직
│   ├── coupon/         # 쿠폰 도메인
│   ├── point/          # 포인트 도메인
│   ├── item/           # 상품 도메인
│   ├── order/          # 주문 도메인
│   └── user/           # 사용자 도메인
│
├── application/        # 사용 사례 조정
│   ├── coupon/
│   ├── point/
│   ├── item/
│   └── order/
│
├── infrastructure/     # 기술 구현
│   ├── coupon/
│   ├── point/
│   ├── item/
│   ├── order/
│   ├── lock/          # 분산 락
│   └── support/       # 필터, 인터셉터
│
├── interfaces/        # REST API
│   ├── coupon/
│   ├── point/
│   ├── item/
│   └── order/
│
└── config/           # Spring 설정
    ├── auth/
    ├── cache/
    ├── jpa/
    ├── kafka/
    └── redis/

src/test/java/       # 테스트 (54 files)
├── domain/
├── infrastructure/
├── interfaces/
└── fixtures/        # 테스트 데이터 팩토리

docs/                # 기술 문서 (10+)
k6/                  # 부하 테스트 시나리오
```
