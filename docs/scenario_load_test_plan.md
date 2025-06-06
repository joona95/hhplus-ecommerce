# 부하 테스트 계획 보고서

## 서론

신규 패션 이커머스 오픈을 앞두었다는 가정하에 부하 테스트 계획을 작성하였습니다. 도메인 유사성과 트래픽 패턴이 비슷하다는 가정 하에 무신사를 성능 벤치마크로 삼았습니다. 언론에 공개된 무신사 DAU를 기반으로
목표를 설정하였습니다. 부하 테스트 목표는 “**평시 1.8 k TPS**(DAU 165만)~ **블프 피크 4.3 k TPS**(DAU 386만) 구간을 무중단으로 견디는가”로 설정했습니다.

테스트 목표는 위 두 구간에서 무중단으로 서비스 수준 목표(SLO) - *p95 < 500ms, 에러율 < 5 %* — 를 만족하는지 확인하고, 결과를 바탕으로 **초기 인프라 용량**과 **이벤트‑대응
Auto‑Scaling 규칙**을 설계하는 데 있습니다.

## 부하 테스트 목표 TPS & VUser

> **[목표 설정]**
> 1. 요청/유저 = 20
> - 모바일 패션몰 평균 page‑views/visit ≈ 4.8 (Statista)
> - 화면 1개당 평균 4‑5개 API 호출 → 4.8 × 4 ≈ 20
> 2. 피크 1 h 비중 = 0.20
> - Cisco Busy‑Hour 연구: 하루 트래픽 15‑20 %가 최대치
> 3. TPS/RPS
> - `피크 1h 요청 수 ÷ 3600 = 초당 요청 수`
> - 예) 평시 6.6 M 요청 ÷ 3 600 ≈ 1 833 req/s → 1 800 TPS로 반올림.

| 구분 | DAU | 요청/유저 | 피크 1h 비중 | **피크 1h 요청 수** | **TPS (=RPS)** | **필요 VU*** |
| --- | --- | --- | --- | --- | --- | --- |
| **평시** | 1.65M | 20 | 0.20 | 6.6M | **≈1800req/s** | ≈2700 |
| **블프** | 3.86M | 20 | 0.20 | 15.4M | **≈4300 req/s** | ≈6500 |

- **VU(Virtual User)** = `TPS × iterationTime(1.5 s)`

– k6가 동시에 돌리는 가상 세션 수로, *목표 TPS를 꾸준히 발사*할 만큼의 동접을 확보하기 위해 계산합니다.

## 테스트 환경

**백엔드 인프라 구성 (Docker)**

- API 서버 - 2 vCPU, 4GB Memory 단일 Docker 서버 기준
- Redis - redis:7
- MySQL - mysql:8.0
- Kafka - confluentinc/cp-kafka:latest

**테스트 실행 환경**

- 테스트 도구: K6 + grafana (Docker) + influxdb (Docker)
- 테스트 머신: Apple M3 Air (10-Core CPU / 16GB RAM)
- 테스트 실행 방법

    ```
    $ docker-compose up -d // 백엔드 인프라 + API 서버 + 테스트 도구 Docker 기반 실행
    $ k6 run --out influxdb=http://localhost:8086/k6db k6/scenario_test.js // k6 로 테스트 스크립트 실행
    ```

## 테스트 시나리오

> 핵심 로직 3종(상품 상세, 주문, 선착순 쿠폰)을 각각 가장 잘 맞는 부하‑테스트 유형에 매핑했습니다.

### 상품 상세 조회 시나리오

상품 상세 조회는 사용자가 가장 많이 누르는 엔드포인트이기 때문에 평시 1800RPS를 넘어 피크 4300RPS 까지 Load Test를 진행하여 SLO (p95 < 500 ms, 에러 < 5%) 준수할 수 있는
수준을 확인하고자 합니다.

이 수치를 기준으로 인프라 구축 시 필요한 최소/최대 파라미터의 근거로 삼고자 합니다.

#### 평시 트래픽을 견딜 수 있는 최소 수준 인프라 구축을 위한 Load Test

```
export const options = {
    executor: 'ramping-arrival-rate',
    startRate: 0,
    stages: [
        { duration: "1m", target: 2700 },
        { duration: "3m", target: 2700 },
        { duration: "1m", target: 0 }
    ],
    thresholds: {
        http_req_duration: ["p(95)<500"],  // 95% 이상의 요청이 500ms 이하 유지
        http_req_failed: ["rate<0.05"],    // 실패율 5% 미만 유지
    },
};
```

#### 피크 트래픽을 견딜 수 있게 최대 수준 오토스케일 인프라 구축을 위한 Load Test

```
export const options = {
    executor: 'ramping-arrival-rate',
    startRate: 0,
    stages: [
        { duration: "1m", target: 6500 },
        { duration: "3m", target: 6500 },
        { duration: "1m", target: 0 }
    ],
    thresholds: {
        http_req_duration: ["p(95)<500"],  // 95% 이상의 요청이 500ms 이하 유지
        http_req_failed: ["rate<0.05"],    // 실패율 5% 미만 유지
    },
};
```

### 주문 시나리오

주문의 경우, 상품 상세 조회부터 보유 쿠폰 조회, 주문까지의 플로우 기반으로 이루어지며 사이트의 주요 로직이므로 안정적인 운영이 필요한 항목이라 Load Test를 활용하여 안정성 확인 및 정상 시나리오 작동을
확인하고자 합니다.

이커머스 플랫폼의 평균 구매전환율은 5%로 평시 90RPS ~ 피크 215RPS 수준으로 동작을 확인하고자 합니다.

트랜잭션/락/주문이벤트발행에 따른 호출이 안정적으로 처리되는지를 확인하도록 하겠습니다.

#### 주문 안정성 확인을 위한 Load Test

```
export const options = {
    executor: 'ramping-arrival-rate',
    startRate: 0,
    stages: [
        { duration: "1m", target: 325 },
        { duration: "3m", target: 325 },
        { duration: "1m", target: 0 }
    ],
    thresholds: {
        http_req_duration: ["p(95)<500"],  // 95% 이상의 요청이 500ms 이하 유지
        http_req_failed: ["rate<0.05"],    // 실패율 5% 미만 유지
    },
};
```

### 선착순 쿠폰 발급 시나리오

선착순 쿠폰의 경우, 쿠폰 오픈과 동시에 사용자가 몰렸을 때 안정적으로 동작할 필요성이 있습니다.

순간적으로 몰리는 트래픽을 어느 수준까지 감당 가능한지 확인하고, 필요한 경우 이벤트 시행 전 감당 가능한 수준의 인프라로 구축하여 대비할 수 있습니다.

피크 4300RPS가 30초 안에 집중될 수 있는 것으로 보고, 동일 스파이크를 3번 반복하여 캐시/락/큐잉 시스템이 밀리지 않는지 확인하고자 합니다.

```
export const options = {
    executor: 'ramping-arrival-rate',
    startRate: 0,
    stages: [
        { duration: "1m", target: 1800 },
        { duration: "30s", target: 4300 },
        { duration: "1m", target: 1800 },
        { duration: "30s", target: 4300 },
        { duration: "1m", target: 1800 },
        { duration: "30s", target: 4300 },
        { duration: "1m", target: 1800 },
        { duration: "1m", target: 0 }
    ],
    thresholds: {
        http_req_duration: ["p(95)<500"],  // 95% 이상의 요청이 500ms 이하 유지
        http_req_failed: ["rate<0.05"],    // 실패율 5% 미만 유지
    },
};
```
