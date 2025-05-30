# Kafka 선착순 쿠폰 발급 적용 보고서

## 서론

선착순 쿠폰 발급 기능은 다수의 사용자가가 거의 동시에 접근할 때 동시성 이슈가 발생할 가능성이 있었습니다. 처음에는 DB 비관적 락을 통해 트랜잭션 레벨에서 동시성 문제를 제어하려 하였으나, 수많은 요청이 들어오면 모든 트랜잭션이 락을 대기하면서 DB 부하 및 응답 지연이 심각해지는 문제가 있었습니다.

다음으로 단일 애플리케이션에서 여러 인스턴스로 확장한다고 가정하여, DB 부하를 줄이고 분산 환경에서 락 경합을 완화하기 위해 Redis 분산 락을 도입했습니다. 분산 락은 네트워크 지연이나 Redis 노드 장애 시 락 해제 실패로 데드락 위험이 있고, 높은 트래픽에서는 락 재시도로 인한 오버헤드가 발생할 수 있었습니다.

이를 개선하기 위해 Redis Sorted Set 자료구조를 활용한 순차 큐 방식을 도입했습니다. 요청 시점의 타임스탬프를 score로 하여 ZADD 한 후, ZRANK 로 순번이 발급 가능 수량 이하일 때만 대기 리스트에 저장하고 1~3초 주기 배치로 쿠폰 발급하는 방식입니다. 이 방법은 DB 락을 최소화하고 Redis 레벨에서 동시성 제어할 수 있었으나, 여전히 배치 지연과 Redis 부하 (Sorted Set 연산, 배치 스캔), 및 배치 예외 복구 복잡성이라는 한계가 남아 있었습니다. 

따라서, MSA 규모의 높은 트래픽과 실시간성을 요구하는 환경에서 Redis 단독 처리로는 한계가 있다고 판단하여 Kafka 기반 실시간 이벤트 발행으로 전환하기로 하였습니다.

<br>

## 기존 : Redis + DB 활용

### 요청 처리 플로우

```
1) HTTP POST /coupons/{couponId}/issue 선착순 쿠폰 발급 요청

2) ZADD coupon-issue-token:{couponId} timestamp userId Sorted Set 자료구조에 선착순 쿠폰 발급 요청 저장

3) coupon-issued:{couponId} userId 중복 발급 체크

4) ZRANK 순번과 coupon-stock:{couponId} 발급 가능 수량 비교 → 발급 가능 수량 초과 시 ZREM (쿠폰 발급 수량 제한)

5) 순번 이내 시 SADD coupon-issued:{couponId} userId (중복 발급 체크용) / LPUSH coupon-issue-pending couponId (발급 요청된 쿠폰 목록)

5) 1~3초 주기 배치: coupon-issue-pending 발급 요청된 쿠폰 목록 조회 → 중복 발급 체크 → DB 트랜잭션 쿠폰 발급내역 저장 → coupon-stock:{couponId} 발급 가능 수량 감소
```

### 문제점

- **배치 지연**(1~3초)
- **Redis 부하**(Sorted Set 연산 및 배치 스캔)
- **운영 복잡성**(배치 실패 시 상태 동기화)
- **MSA 확장성 부족**(단일 배치 스케줄러 의존)

<br>

## 변경 : Kafka + Redis + DB 활용

### 아키텍쳐 구조

```
클라이언트 → Kafka Producer → topic: coupon-issue-request(key=couponId) → Consumer Group(concurrency=P) → Redis(Set+Counter) + DB 트랜잭션
```

- **Producer**: HTTP 요청 직후 `KafkaTemplate.send("coupon-issue-request", couponId, userId)`
- **Topic**: 파티션 6, 복제팩터 3 (key 기반 파티셔닝으로 couponId별 순차성 보장)
- **Consumer**: `@KafkaListener(concurrency=6)`, Set + Counter 조합으로 중복 발급 체크 및 쿠폰 발급 수량 제한 처리
- **Redis**: `SADD`로 중복 발급 차단, `Counter`로 쿠폰 발급 수량 제한
- **DB**: 트랜재션 내 재고 차감 및 발급 이력 저장

#### Kafka 활용

```java
@Configuration
public class KafkaTopicConfig {
    //...

    @Bean
    public NewTopic couponIssueRequestTopic() {
        return TopicBuilder
                .name("coupon-issue-request")
                .partitions(6)
                .replicas(3)
                .build();
    }
}
```
```java
@Slf4j
@Component
public class CouponKafkaEventPublisher implements CouponEventPublisher {

    private static final String COUPON_ISSUE_REQUEST_TOPIC = "coupon-issue-request";
    private final KafkaTemplate<String, CouponIssueRequestEvent> kafkaTemplate;

    public CouponKafkaEventPublisher(KafkaTemplate<String, CouponIssueRequestEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(CouponIssueRequestEvent event) {
        log.info("Produce message : " + event);
        this.kafkaTemplate.send(COUPON_ISSUE_REQUEST_TOPIC, String.valueOf(event.couponId()), event);
    }
}
```
```java
@Slf4j
@Component
public class CouponEventListener {

    private final CouponService couponService;

    public CouponEventListener(CouponService couponService) {
        this.couponService = couponService;
    }

    @KafkaListener(topics = "coupon-issue-request", groupId = "coupon-issue", concurrency = "6")
    public void handleCouponIssueRequestEvent(CouponIssueRequestEvent event) {

        try {
            couponService.handleCouponIssueRequest(event.userId(), event.couponId());
            log.info("선착순 쿠폰 발급 완료");
        } catch (Exception e) {
            log.error("선착순 쿠폰 발급 실패");
        }
    }
}
```

- 요청을 토픽에 쌓아 주문 요청 트래픽이 몰릴 때도 안정적으로 처리 가능

- (key = couponId) 기반 파티셔닝으로 couponId 별 선착순 보장 가능

- 파티션 수와 컨슈머 수 조정으로 처리량 선형 확장 가능

- 디스크 로그 보존 및 오프셋 재조정으로 장애 복구 및 재실행 지원 가능

#### Redis 활용

```
    public void handleCouponIssueRequest(long userId, long couponId) {

        CouponIssueToken couponIssueToken = CouponIssueToken.of(userId, couponId);

        if (couponIssueTokenRepository.isAlreadyIssued(couponIssueToken)) {
            throw new RuntimeException("이미 쿠폰 발급 요청한 유저입니다.");
        }

        long couponStock = couponRepository.getCouponStock(couponId);
        long issuedSize = couponIssueTokenRepository.countCouponIssuedUser(couponIssueToken);
        if (couponStock < issuedSize) {
            throw new RuntimeException("쿠폰 발급 가능한 수량을 초과하였습니다.");
        }

        couponIssueTokenRepository.saveCouponIssuedUser(couponIssueToken);

        issueCoupon(userId, couponId);
    }
```

- Redis 에서 중복 발급 방지(Set) 및 발급 수량 제어(Counter) 용도로 최소화하여 사용

#### DB 활용

```java
    @Transactional
    public void issueCoupon(long userId, long couponId) {

        Coupon coupon = couponRepository.findCouponById(couponId).orElseThrow(() -> new RuntimeException("존재하지 않는 쿠폰입니다."));

        if (couponRepository.existsCouponIssueByUserIdAndCouponId(userId, coupon.getId())) {
            throw new RuntimeException("쿠폰을 이미 발급 받았습니다.");
        }
        coupon.issue();
        couponRepository.saveCouponIssue(CouponIssue.of(userId, coupon));
        couponRepository.saveCouponStock(coupon.getId(), coupon.getCount());
    }
```

- 최종 DB 트랜잭션에서 재고 차감 및 발급 이력 저장으로 일관성과 안정성을 확보

