# MSA 전환 배포 단위 분리와 분산 트랜잭션 대응

## 1. 기존 모놀리식 애플리케이션 배경

```java
@Transactional
public OrderCreateResult placeOrder(…) {
    // ① 재고 차감
    items = … itemService.decreaseStock(…);
    // ② 주문 생성
    orderInfo = orderService.createOrder(…);
    // ③ 쿠폰 적용
    if (command.couponId() != null) { … }
    // ④ 포인트 차감
    pointService.use(…);
    // ⑤ 주문 완료 이벤트 발행
    orderEventPublisher.send(new OrderCompleteEvent(orderInfo));
    return …;
}
```

기존 모놀리식 애플리케이션은 하나의 파사드 서비스를 두고 `@Transactional` 메서드로 주문, 재고, 쿠폰, 포인트 차감, 이벤트 발행까지 모두 처리했습니다.

이 구조는 구현이 단순하고, 같은 트랜잭션으로 묶여 하나의 서비스에서 오류가 생기면 전체 롤백이 일어나면서 DB 정합성을 위한 추가 작업이 필요 없다는 장점이 있습니다.

그러나, 기존 시스템은 하나의 서비스에 각각 다른 서비스들이 합쳐져 있어서 여러 단점이 존재합니다.

- **스케일링 한계**: 모든 비즈니스 로직이 하나의 JVM과 DB 트랜잭션에 묶여 있어, 특정 기능만 증설하기 어렵고
- **배포·운영 복잡도**: 작은 수정 하나도 전체를 배포해야 하며 장애가 전체 시스템으로 확산되고
- **기술 스택 분리 불가**: 재고·쿠폰·포인트 같은 서로 다른 속성의 기능을 각각 최적 기술로 운영하기 힘듭니다.

따라서 **서비스 간 독립 배포, 독립 확장, 장애 격리**를 위해 MSA(마이크로서비스) 아키텍처로 전환할 필요성이 있습니다.


## 2. MSA 전환 방향 및 기대 효과

기존 파사드 서비스 하나에 모든 로직이 몰려 있던 구조를, 각 도메인별로 책임을 분리하여 독립된 마이크로서비스로 나누고자 합니다.

각 서비스는 자신의 데이터베이스와 배포·확장 단위를 가지며, 서로는 가벼운 API 호출 또는 이벤트 메시지로만 통신하도록 합니다.

재고·쿠폰·포인트 차감 등은 **이벤트 드리븐(Choreography)** 방식으로, 각 서비스가 도메인 이벤트를 발행 및 구독을 하여 아래와 같은 흐름으로 가져가고자 합니다.

- **도메인별 서비스 분리**
  1. **재고 차감 서비스**
      - 주문 요청이 들어오면 재고 서비스로 재고 차감 서비스 호출
      - 성공 시 재고 차감 이벤트 발행
  2. **주문 생성 서비스**
      - 재고 차감 완료 이벤트 수신 → 주문 생성 서비스 호출
      - 주문 생성 후 주문 생성 이벤트 발행
  3. **쿠폰 서비스**
      - 주문 생성 이벤트 수신 → 쿠폰 적용 서비스 호출
      - 성공 시 쿠폰 적용 이벤트 발행
  4. **포인트 서비스**
      - 쿠폰 적용 이벤트 수신 → 포인트 차감 서비스 호출
      - 성공 시 결제 완료 이벤트 발행
  5. **결제 완료 → 통계·알림**
      - 결제 완료 이벤트 수신 후 최종 검증 → 주문 완료 이벤트 발행 (주문 상태 변경 등 작업)
      - 이 이벤트를 통해 **데이터 플랫폼 전송**, **인기 상품 통계 집계** 등의 후속 작업 수행
      

## 3. 보상 시나리오

위와 같이 도메인 서비스별로 분리하여 분산 트랜잭션을 진행하였을 때의 가장 큰 문제는, 한 서비스에서 실패가 일어나더라도 자동으로 전체 롤백이 진행되지 않는다는 점입니다.

이를 해결하기 위해 취소 및 보상 시나리오가 필요합니다. 각 서비스는 실패 시 실패 이벤트를 발행하며, 자신이 발행한 서비스의 실패 이벤트를 구독해서 반대 작업을 수행할 수 있도록 처리하고자 합니다. 취소 및 보상은 주문 요청 역순의 보상 트랜잭션으로 구성하고자 합니다.

### 4.1. 주문 취소 시작 (Order Service)

- 결제 완료 이벤트 수신 후 최종 검증 시, 문제가 있는 경우 주문 취소 이벤트 발행
- 주문 서비스에서 주문 취소 이벤트 수신 시, 주문 상태를 취소 상태로 변경
- 결제 서비스에서 주문 취소 이벤트 수신 시, 결제 취소 진행

### 4.2. 포인트 환급 (Point Service)

- 주문 취소 이벤트 수신 시, 결제 취소 서비스 호출하여 취소 진행
- 포인트 결제(차감) 실패 시나 포인트 결제 취소 작업 완료 시에 쿠폰 복원 이벤트 발행

### 4.3. 쿠폰 복원 (Coupon Service)

- 쿠폰 복원 이벤트 수신 시, 쿠폰 상태를 미사용으로 롤백
- 쿠폰 사용 실패 시나 쿠폰 복원 작업 완료 시에 재고 보상 이벤트 발행

### 4.4. 재고 보상 (Item Service)

- 재고 보상 이벤트 수신 시, 재고 수량 복원 완료

### (+) 추가 고려사항

보상 트랜잭션 진행 시에 실패 이벤트 발행을 통해 주문 취소 과정을 진행할 때, 추가적으로 고려해볼 수 있는 사항들은 아래와 같다.

- **Outbox 패턴**: 이벤트 발행 시점이 DB 커밋과 정확히 일치하도록
- **Idempotency Key**: `orderId` 기준 동중복 호출 방지
- **Retry 정책**: 실패 이벤트에 대해서는 백오프 후 3회 자동 재시도
- **Dead Letter Queue**: 재시도 한계 초과 시 별도 큐에 보관, 수동 복구 프로세스 트리거


## 4. 결론

이번 MSA 전환 설계에서는 기존 모놀리식 파사드 서비스가 한 번의 트랜잭션으로 모든 비즈니스 로직을 처리하던 구조를, **재고·주문·쿠폰·포인트·통계** 로 명확히 분리된 마이크로서비스로 나누었습니다.

각 서비스는 **자체 DB와 로컬 트랜잭션**만 관리하고, 도메인 이벤트(Choreography)를 통해 느슨하게 연결함으로써 다음과 같은 이점을 확보합니다.

1. **독립 배포·확장성**
    - 재고 부하가 급증해도 Inventory Service만 스케일 아웃
    - 쿠폰 정책 변경 시 Coupon Service만 재배포
2. **장애 격리**
    - 하나의 서비스 장애가 전체 시스템으로 전파되지 않으며, 이벤트 재처리·Dead Letter Queue로 복구 가능
3. **최종적 일관성 보장**
    - Saga(이벤트 + 보상 트랜잭션) 패턴으로, 분산 환경에서도 데이터 정합성 유지
4. **운영·개발 효율성**
    - 각 도메인별로 최적의 기술 스택·데이터 모델을 선택 가능
    - 이벤트 로그와 보상 이벤트를 통해 취소·환불 절차를 투명하게 모니터링

취소 시나리오 역시 **OrderCanceled → PointRefunded → CouponReverted → StockRestored → OrderCompensationCompleted** 순서의 보상 이벤트 체인으로 구성해, 단계별 실패에서의 재시도와 모니터링 체계를 갖출 수 있습니다.

 이로써 “한 번의 실패로 전체 롤백”이라는 모놀리식의 강력함을 대체하면서, **서비스별 자율성과 유연성**, **운영 복원력**, **비즈니스 변화 대응 속도**를 획기적으로 높일 수 있습니다.
