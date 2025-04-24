# 동시성 이슈 보고서

## 1. 동시성 이슈란?

### 1-1. 정의

- 동시성 이슈는 여러 트랜잭션이나 프로세스가 같은 데이터에 동시에 접근하고 수정하려고 할 때 발생할 수 있는 문제입니다.

- 동시성 문제 발생 시 데이터 충돌, 데이터 불일치 등의 문제가 발생할 수 있습니다.
  - 경쟁 조건: 두 개 이상의 트랜잭션이 서로 경쟁하면서 데이터에 대해 잘못된 수정이 이루어지거나 하나의 트랜잭션만 성공하여 결함이 발생할 수 있습니다.
  - 갱신 손실: 두 트랜잭션이 같은 데이터를 수정할 때 한 트랜잭션의 수정 내용이 다른 트랜잭션에 의해 덮어쓰여질 수 있습니다.

- 공유 자원 점유에 대한 문제로, 주로 멀티스레드 환경이나 분산 시스템에서 많이 나타납니다.

### 1-2. 해결방법

- 트랜잭션 격리 수준 설정
- 낙관/비관적 락 사용
- 원자적 처리 보장: 트랜잭션을 하나의 단위로 처리하여 중간에 문제 발생 시 모두 롤백

<br>

## 2. 낙관/비관적 락이란?

### 2-1. 낙관적 락

#### 1) 개념

- 물리적 락의 개념이 아니라 논리적 개념의 락입니다.
- 트랜잭션 시작 시 다른 트랜잭션이 해당 데이터를 수정하지 않을 것이라는 가정하에 작업 진행 후, 커밋 시점에 충돌 여부를 확인합니다.
- 버전 관리 방식을 주로 사용하며, 데이터가 변경되었는지 확인합니다.

#### 2) 장단점

- 장점
  - 물리적인 락을 사용하지 않아 성능이 좋음
- 단점
  - 충돌 발생 시 재시도 구현이 필요함

#### 3) 동작 원리

① 트랜잭션이 데이터를 읽고 수정 작업 수행

② 데이터의 버전을 읽고, 트랜잭션에서 버전을 확인하고 처리

③ 트랜잭션이 커밋될 때 DB는 해당 데이터가 다른 트랜잭션에 의해 수정되었는지 확인

④ 버전이 달라졌다면 충돌이 발생한 것으로 간주하고 예외를 발생 (재시도 로직 구현)

#### 4) 사용 방법 (JPA 기준)

```java
@Entity
public class Point {
  @Version
  private Long version;
}
```
- JPA가 제공하는 기본적인 기능으로, `@Version` 필드만 추가하면 됨
- JPA가 내부적으로 version 컬럼 확인하여 충돌 발생할 경우 예외 발생시킴

#### 5) 사용 상황

- 충돌 가능성은 있지만 상대적으로 낮은 확률이고 성능을 중시하는 작업에 적합합니다.
- 여러 요청이 들어왔을 때, 1개의 요청만 성공하고 나머지는 실패해도 괜찮은 상황일 때 적합합니다.
- 충돌 가능성이 높아지면 실패 시 재시도하는 부하가 많아질 수 있습니다.


### 2-2. 비관적 락

#### 1) 개념

- DB 차원에서 물리적으로 락을 거는 개념입니다.
- 충돌이 발생할 수 있다는 가정을 기반으로 작업을 시작할 때부터 다른 트랜잭션의 접근을 차단합니다.
- 락을 먼저 걸고 트랜잭션을 실행하는 방식으로, 데이터에 대한 락을 강제하여 다른 트랜잭션이 데이터를 수정하지 못하도록 합니다.

#### 2) 장단점

- 장점
  - 수정에 대한 충돌 없이 동시성 문제를 예방할 수 있음
- 단점
  - 락 유지 시간이 길어질 수록 성능 저하 가능성이 높아짐
  - 데드락 발생 가능성이 있음

#### 3) 동작 원리

① 트랜잭션이 데이터를 읽을 때 락을 시도

② 다른 트랜잭션이 해당 데이터에 접근한 경우, 대기하거나 트랜잭션이 끝날 때까지 차단

③ 트랜잭션이 끝나면 락을 해제

④ 트랜잭션 간에 데이터 충돌이 일어날 수 없게 하여 순서대로 진행

#### 4) 사용 방법 (JPA 기준)

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT c FROM Coupon c WHERE c.id = :couponId")
Optional<Coupon> findByIdWithLock(Long couponId);

```
- JPA `@Lock` 사용하여 설정 가능
- JPARepository 의 기본 메소드에서는 락을 사용할 수 없으므로 커스텀 메소드로 `@Query` 와 함께 락 적용 필요
- 트랜잭션에서 락 사용하므로 `@Transactional` 함께 사용 필요

#### 5) 사용 상황

- 충돌이 잦아 동시성 문제가 발생할 가능성이 큰 경우 사용하기에 적합합니다.
- 트랜잭션 처리의 순서가 중요한 작업에 적합합니다.
- 실패 처리보다는 반드시 성공해야 하는 경우에 사용하기 적합합니다.
- 그러나 락을 얻기 위해 시도한 순서와 락을 얻는 순서가 보장되지는 않아, 공정성 락에 대한 고려가 필요합니다.


<br>

## 3. 시나리오별 동시성 이슈 분석

### 3-1. 상품 재고 차감

#### 1) 문제 식별

여러 사용자가 동시에 같은 상품을 구매하는 경우, 재고보다 더 많은 수량이 구매되는 문제가 발생할 수 있습니다.

#### 2) 분석

```java
@Nested
class 동시성_테스트 {

  @Test
  void 재고_차감_시_동시_요청이_들어온_경우_요청이_들어온_만큼_재고_차감_발생() throws InterruptedException {

    //given
    Item item = itemJpaRepository.save(ItemFixtures.재고로_상품_생성(Stock.of(20)));

    int threadCount = 20;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch countDownLatch = new CountDownLatch(threadCount);

    List<StockDecreaseCommand> commands = List.of(StockDecreaseCommand.of(item.getId(), 1));

    AtomicInteger failureCount = new AtomicInteger();

    //when
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < threadCount; i++) {
      executorService.execute(() -> {
        try {
          itemService.decreaseStocks(commands);
        } catch (Exception e) {
          failureCount.getAndIncrement();
        }
        countDownLatch.countDown();
      });
    }

    countDownLatch.await();
    long endTime = System.currentTimeMillis();

    //then
    System.out.println("실행 시간 == " + (endTime - startTime) + "ms");

    Optional<Item> result = itemJpaRepository.findById(item.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getStock()).isEqualTo(failureCount.get());

    System.out.println("실패 횟수 : " + failureCount.get() + ", 재고 : " + result.get().getStock());
  }
}
```

<img src="/docs/img/stock_decrease_1.png" />

- 재고가 20개인 상품의 1개 재고 차감 요청이 동시에 20개가 들어왔을 경우 모든 요청이 동시에 반영되는지 확인해볼 수 있습니다.

- 결과적으로 모든 요청이 반영되어 재고가 0개가 되길 기대하였으나 13개로 나오면서 모든 재고 차감 요청이 반영되지 않은 것을 볼 수 있습니다.

#### 2-1) 낙관적 락

```java
@Entity
@Getter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    @Embedded
    private Stock stock;

    private int price;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
```

- Item 내부에서 재고에 대한 정보를 가지고 있으므로, 재고에 대한 조회 및 차감에서 동시성 이슈를 제어하기 위해서는 Item 내에 @Version 을 추가하여 낙관적 락을 적용할 수 있습니다.

<img src="/docs/img/stock_decrease_2.png" />

- 동시성 테스트를 진행하였을 경우, `ObjectOptimisticLockingFailureException` 이 발생하는 것을 볼 수 있습니다.

<img src="/docs/img/stock_decrease_3.png" />

- 결과를 살펴보면 실패 횟수만큼 재고가 남아있고, 정상적으로 요청 반영된 값들은 전부 재고 차감되어 동시성 문제는 해결된 것을 볼 수 있습니다.

#### 2-2) 비관적 락

```java
public interface ItemJpaRepository extends JpaRepository<Item, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.id in :ids")
    List<Item> findAllByIdWithLock(@Param("ids") List<Long> ids);
}
```

- Item 내부에서 재고에 대한 정보를 가지고 있으므로, 재고 차감을 위해 JPARepository로 Item 조회 시에 @Lock 을 걸어 비관적 락을 적용할 수 있습니다.

<img src="/docs/img/stock_decrease_4.png" />

- 20개의 재고에서 20회 1개씩 재고 차감 요청이 전부 반영되어 재고가 0이 된 것을 살펴볼 수 있습니다.

#### 3) 해결

- 재고 차감의 경우, 비관적 락을 사용하여 동시성 이슈를 방지하는 것이 좋을 것 같습니다.

  - 이벤트 같은 상황이 발생했을 경우 동시에 수많은 주문 요청이 발생할 가능성이 있어서 충돌이 잦아질 수 있는 것을 가정할 수 있습니다.

  - 주문 요청이 동시에 들어왔을 때 한 명만 성공하고 다른 사람들은 다 실패 후 재시도 처리하면 부하가 많아질 수 있을 것 같습니다.

  - 재고 차감의 경우도 먼저 요청한 사람의 요청이 먼저 처리되는 편이 합리적입니다. 낙관적 락을 사용하게 되면 한 명 성공 후 다른 사람들이 다 실패한 사이 들어온 요청이 먼저 반영될 수 있습니다.

#### 4) 대안

- 만약 이벤트 없이 평상 시에 특정 상품에 주문 요청이 몰리지 않는 상황이라면 낙관적 락을 사용하는 것도 괜찮을 것 같습니다.

- 순차적으로 주문 요청 처리를 해야한다면 Redis, 메세지큐 등 분산락 환경도 고려해볼 수 있을 것 같습니다.

### 3-2. 선착순 쿠폰 발급

#### 1) 문제 식별

- 동시에 여러 유저가 정해진 수량보다 많은 인원이 쿠폰 발급받는 현상 발생 가능합니다.
- 동일 유저가 중복으로 쿠폰 발급받는 현상 발생 가능합니다.

#### 2) 분석

```java
@Test
void 특정_쿠폰_발급_요청이_동시에_들어오는_경우_쿠폰_발급_개수가_요청_수_만큼_차감() throws InterruptedException {

  //given
  Coupon coupon = couponJpaRepository.save(CouponFixtures.발급수량으로_쿠폰_생성(20));

  int threadCount = 20;
  ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
  CountDownLatch countDownLatch = new CountDownLatch(threadCount);

  CouponIssueCommand command = CouponIssueCommand.of(coupon.getId());

  AtomicInteger failureCount = new AtomicInteger();

  //when
  long startTime = System.currentTimeMillis();
  for (int i = 0; i < threadCount; i++) {
    executorService.execute(() -> {
      User user = userJpaRepository.save(UserFixtures.정상_유저_생성());

      try {
        couponService.issueCoupon(user, command);
      } catch (ObjectOptimisticLockingFailureException e) {
        failureCount.incrementAndGet();
      }
      countDownLatch.countDown();
    });
  }

  countDownLatch.await();
  long endTime = System.currentTimeMillis();

  //then
  System.out.println("실행 시간 == " + (endTime - startTime) + "ms");

  Optional<Coupon> result = couponJpaRepository.findById(coupon.getId());

  assertThat(result).isPresent();
  assertThat(result.get().getCount()).isEqualTo(failureCount.get());

  System.out.println("실패 횟수 : " + failureCount.get() + ", 수량 : " + result.get().getCount());
}
```

<img src="/docs/img/coupon_count_decrease_1.png">

- 쿠폰 발급 가능 수량이 20개일 때, 20개의 쿠폰 발급 요청이 동시에 들어왔을 경우에 쿠폰 발급 가능 수량 차감 요청이 모두 반영되는지 살펴볼 수 있습니다.

- 그러나 결과적으로 쿠폰 발급 수량이 0개가 아니라 1개 남으면서 쿠폰 발급 요청에 따라 쿠폰 발급 수량 차감이 모두 이루어지지 않아 동시성 이슈가 발생하고 있습니다.

#### 2-1) 낙관적 락

```java
@Entity
@Getter
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponName;

    @Enumerated
    private DiscountType discountType;

    private int discountValue;

    private LocalDateTime validTo;

    private LocalDateTime validFrom;

    private int count;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
```

- Coupon 내에 있는 쿠폰 발급 가능 수량을 조회하고 차감하는 데서 동시성 이슈가 발생하고 있으므로, Coupon 에 `@Version` 어노테이션을 추가하여 낙관적 락을 구현할 수 있습니다.

<img src="/docs/img/coupon_count_decrease_2.png">

- 동시성 테스트를 진행하였을 경우, `ObjectOptimisticLockingFailureException` 이 발생하는 것을 볼 수 있습니다.

<img src="/docs/img/coupon_count_decrease_3.png">

- 결과를 살펴보면 실패 횟수만큼 발급 수량이 남아있고, 정상적으로 요청 반영된 값들은 전부 수량 차감되어 동시성 문제는 해결된 것을 볼 수 있습니다.

#### 2-2) 비관적 락

```java
public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :id")
    Optional<Coupon> findByIdWithLock(@Param("id") long id);
}
```

- 쿠폰 발급 가능 수량 차감 시에는 Coupon 테이블에서 가지고 있는 쿠폰 발급 수량 자원에 조회와 차감 시 동시성 이슈가 발생하고 있습니다.

<img src="/docs/img/coupon_count_decrease_4.png">

- `@Lock` 어노테이션 활용하여 비관적 락 적용 시에는 20개의 요청이 전부 반영되어 발급 가능 수량이 0개가 된 것을 볼 수 있습니다.

```java
@Test
void 특정_유저의_같은_쿠폰_발급_요청이_동시에_들어오는_경우_중복_발급_발생() throws InterruptedException {

  //given
  User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
  Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());

  int threadCount = 10;
  ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
  CountDownLatch countDownLatch = new CountDownLatch(threadCount);

  CouponIssueCommand command = CouponIssueCommand.of(coupon.getId());

  AtomicInteger failureCount = new AtomicInteger();

  //when
  long startTime = System.currentTimeMillis();
  for (int i = 0; i < threadCount; i++) {
    executorService.execute(() -> {
      try {
        couponService.issueCoupon(user, command);
      } catch (Exception e) {
        failureCount.incrementAndGet();
      }
      countDownLatch.countDown();
    });
  }
  long endTime = System.currentTimeMillis();

  countDownLatch.await();

  //then
  System.out.println("실행 시간 == " + (endTime - startTime) + "ms");

  List<CouponIssue> result = couponIssueJpaRepository.findAll();

  assertThat(result).isNotEmpty();
  assertThat(result.size()).isEqualTo(1);
  assertThat(result.get(0).getCouponId()).isEqualTo(coupon.getId());
  assertThat(result.get(0).getUser().getId()).isEqualTo(user.getId());

  System.out.println("실패 횟수 : " + failureCount.get());
}
```

<img src="/docs/img/user_coupon_issue_1.png">

- 그러나 선착순 쿠폰 발급 시, 쿠폰 발급 가능 수량 차감 외에도 중복 발급 동시성 이슈도 발생할 수 있습니다.

- 특정 유저가 동시에 쿠폰 발급 요청을 여러 번 하였을 때 발급 내역 생성 요청이 동시에 이루어지면서 중복 발급이 발생할 수 있습니다.

- 같은 유저로 특정 쿠폰 발급 동시 요청 10개가 발생했을 때, 쿠폰 발급 내역이 1개만 발생해야 하는데 3개가 발생하여 중복 발급이 되는 것을 볼 수 있습니다.

```java
@Entity
@Table(name = "coupon_issue", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "coupon_id"})
})
public class CouponIssue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long couponId;

    // ...
}
```

- 쿠폰 발급은 같은 유저가 같은 쿠폰 발급을 동시 요청하는 것이 문제이므로, CouponIssue 테이블을 제어해야 합니다.

- 낙관/비관적 락을 적용하기에는 이미 있는 데이터에 락을 거는 것이 아니라, 존재하지 않는 데이터 생성 요청을 동시에 하는 것이라 적합하지 않습니다.

- 이를 해결하기 위한 방법으로 (userId, couponId) 값에 유니크 제약조건을 추가하여 중복 발급을 방지하고자 하였습니다.

<img src="/docs/img/user_coupon_issue_2.png">

- 위와 같이 유니크 제약조건 추가 시 발급 내역 생성 시 동시 요청이 들어오는 경우, `ConstraintViolationException` 이 발생하는 것을 볼 수 있습니다.

<img src="/docs/img/user_coupon_issue_3.png">

- 결과적으로 같은 유저가 특정 쿠폰에 대해 동시 발급 요청을 하는 경우, 1회만 발급되고 나머지는 실패하는 것을 볼 수 있습니다.

#### 3) 해결

- 선착순 쿠폰 발급의 경우, 비관적 락을 사용하여 동시성 이슈를 방지하는 것이 좋을 것 같습니다.

  - 선착순 쿠폰의 경우 보통은 이벤트성으로 발생하므로 같은 쿠폰에 대한 동시 요청이 많이 발생할 수 있고 충돌이 잦을 수 있습니다.

  - 선착순 쿠폰은 먼저 요청한 사람의 요청이 먼저 처리되어야 하는데, 낙관적 락을 사용하면 한 명 성공 후 다른 사람들이 다 실패한 사이에 들어온 요청이 먼저 반영될 수 있어서 적합하지 않습니다.


#### 4) 대안

- 선착순 쿠폰은 발급 요청한 순서가 보장될 필요가 있는데 현재는 이 부분에 대한 보장이 되지 않고 있습니다.

- 비관적 락의 경우, 동시성 이슈를 해결해주지만 순서를 보장해주지는 않기 때문에 락을 걸려고 시도한 순서와 실제로 락을 얻는 순서가 달라질 수 있습니다. 이를 위해 Redis, 메세지큐 등을 활용하여 순서를 보장해줄 수 있는 방법을 고려할 필요가 있습니다.

<br>

### 3-3. 포인트 충전/차감

#### 1) 문제 식별

- 동일한 유저가 동시에 포인트 충전/차감하는 경우 정상적으로 포인트 반영이 되지 않을 수 있습니다.

#### 2) 분석

```java
@Test
void 특정_유저_충전_요청이_동시에_들어왔을_때_모든_요청들이_정상적으로_반영된_유저_포인트_값을_반환() throws InterruptedException {

    //given
    User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
    Point point = pointJpaRepository.save(PointFixtures.유저와_금액으로_잔액_생성(user, 0));

    int threadCount = 10;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch countDownLatch = new CountDownLatch(threadCount);

    PointCommand.PointChargeCommand command = new PointCommand.PointChargeCommand(1000);

    AtomicInteger failureCount = new AtomicInteger();

    //when
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < threadCount; i++) {
        executorService.execute(() -> {
            try {
                pointService.charge(user, command);
            } catch (ObjectOptimisticLockingFailureException e) {
                failureCount.incrementAndGet();
            }
            countDownLatch.countDown();
        });
    }

    countDownLatch.await();
    long endTime = System.currentTimeMillis();

    //then
    System.out.println("실행 시간 == " + (endTime - startTime) + "ms");

    Optional<Point> result = pointJpaRepository.findById(point.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getAmount()).isEqualTo(1000 * (threadCount - failureCount.get()));

    System.out.println("실패 횟수 : " + failureCount.get() + ", 포인트 : " + result.get().getAmount());
}

@Test
void 특정_유저_사용_요청이_동시에_들어왔을_때_모든_요청들이_정상적으로_반영된_유저_포인트_값을_반환() throws InterruptedException {

  //given
  User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
  Order order = orderJpaRepository.save(OrderFixtures.유저와_주문가격정보로_주문_생성(user, OrderAmountInfo.of(1000, 1000, 0)));
  Point point = pointJpaRepository.save(PointFixtures.유저와_금액으로_잔액_생성(user, 10000));

  int threadCount = 10;
  ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
  CountDownLatch countDownLatch = new CountDownLatch(threadCount);

  PointCommand.PointUseCommand command = PointCommand.PointUseCommand.of(order);

  AtomicInteger failureCount = new AtomicInteger();

  //when
  long startTime = System.currentTimeMillis();
  for (int i = 0; i < threadCount; i++) {
    executorService.execute(() -> {
      try {
        pointService.use(user, command);
      } catch (ObjectOptimisticLockingFailureException e) {
        failureCount.incrementAndGet();
      }
      countDownLatch.countDown();
    });
  }

  countDownLatch.await();
  long endTime = System.currentTimeMillis();

  //then
  System.out.println("실행 시간 == " + (endTime - startTime) + "ms");

  Optional<Point> result = pointJpaRepository.findById(point.getId());

  assertThat(result).isPresent();
  assertThat(result.get().getAmount()).isEqualTo(10000 - 1000 * (threadCount - failureCount.get()));

  System.out.println("실패 횟수 : " + failureCount.get() + ", 포인트 : " + result.get().getAmount());
}
```

- 특정 유저의 포인트 충전/차감 요청이 동시에 20회 들어왔을 때, 모든 요청이 반영되지 않은 것을 살펴볼 수 있습니다.

<img src="/docs/img/point_charge_1.png">

- 같은 유저가 1000원씩 포인트 충전 요청을 10회 동시에 하였을 때, 10000원이 나와야 하는데 4000원만 충전되어 동시성 문제가 발생하고 있는 것을 볼 수 있습니다.

<img src="/docs/img/point_use_1.png">

- 10000원의 잔액을 가지고 있는 상황에서 같은 유저가 1000원씩 포인트 사용 요청을 10회 동시에 하였을 때, 0원이 아니라 7000원이 남아 있는 것을 보아 동시성 문제가 발생하고 있는 것을 볼 수 있습니다.

#### 2-1) 낙관적 락

```java
@Entity
@Getter
@NoArgsConstructor
public class Point {

    private static final int MAX_POINT_LIMIT = 1000000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Embedded
    private Amount amount;

    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @Version
    private Long version; 
}
```

- Point 테이블에 존재하는 금액 데이터에 대해서 동시 요청이 발생하고 있는 것으로, Point 테이블 내에 `@Version` 을 추가하여 낙관적 락을 구현할 수 있습니다.

<img src="/docs/img/point_charge_3.png">

- 1000원 포인트 충전 시도 10회 중 실패한 6회를 제외하고 4000원이 충전된 것을 보아 동시성 문제가 해소된 것을 볼 수 있습니다.

<img src="/docs/img/point_use_3.png">

- 10000원 잔액이 남아있을 때 1000원 포인트 사용 시도 10회 중 실패한 6회를 제외하고 4000원이 사용된 것을 보아 동시성 문제가 해소된 것을 볼 수 있습니다.

#### 2-2) 비관적 락

```java
public interface PointJpaRepository extends JpaRepository<Point, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Point p where p.user = :user")
    Optional<Point> findByUserWithLock(User user);
}
```

- 유저의 Point 를 조회하고 충전/차감 요청이 동시에 발생하여 동시성 이슈가 생기므로 Point 조회 시 비관적 락을 걸어 제어할 수 있습니다.

<img src="/docs/img/point_charge_2.png">

- 동시에 10회 1000원씩 같은 유저가 포인트 충전 요청 시, 실패 없이 포인트가 총 10000원 충전된 것으로 보아 동시성 문제가 해소된 것을 볼 수 있습니다.

<img src="/docs/img/point_use_2.png">

- 10000원 잔액이 남은 상황에서 동시에 1000원씩 10회 같은 유저가 포인트 사용 시, 실패 없이 포인트 잔액이 0원이 된 것을 보아 동시성 문제가 해소된 것을 볼 수 있습니다.

#### 3) 해결

- 포인트 충전/차감의 경우, 낙관적 락을 사용하는 것이 좋을 것 같습니다.

  - 동일 유저에 대해서 포인트 충전/차감이 동시에 발생하는 경우는 사실 어뷰징 유저거나 정기결제 등과 겹치는 케이스가 아니라면 잘 발생하지 않을 것 같습니다.


- 충돌이 많이 발생하지 않는다고 가정한다면, 재시도를 내부적으로 시도하는 것이 크게 부하를 주지도 않을 것 같습니다. 실패 시 고객이 재시도하게끔 한다면 유저에게 실패 경험을 안겨주게 되는데 이보다는 내부적으로 재시도를 하는 것이 더 나을 것 같습니다.

```
//spring-retry 어노테이션 사용을 위한 의존성 추가
implementation 'org.springframework.retry:spring-retry'
implementation 'org.springframework:spring-aspects'
```  

```java
@EnableRetry
@SpringBootApplication
public class ServerApplication {
  
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
  
}
```
  
```java
@Retryable(
  retryFor = {ObjectOptimisticLockingFailureException.class},
  maxAttempts = 5
)
@Transactional
public Point charge(User user, PointChargeCommand command) {

    Point point = findByUser(user);
    point.charge(command.amount());

    PointHistory pointHistory = PointHistory.ofCharge(point, command.amount());
    pointRepository.savePointHistory(pointHistory);

    return point;
}

@Retryable(
  retryFor = {ObjectOptimisticLockingFailureException.class},
  maxAttempts = 5
)
@Transactional
public Point use(User user, PointUseCommand command) {

    Order order = command.order();

    Point point = findByUser(user);
    point.use(order.getTotalAmount());

    PointHistory pointHistory = PointHistory.ofUse(point, order);
    pointRepository.savePointHistory(pointHistory);

    return point;
}
```
    
- `@Retryable` 을 사용하기 위한 의존성을 추가하고, 포인트 충전/차감 서비스 트랜잭션에 재시도 설정을 추가해줍니다.
        
<img src="/docs/img/point_charge_4.png">

<img src="/docs/img/point_use_4.png">


      

#### 4) 대안

- 돈과 관련된 부분으로 데이터 정합성이 매우 중요하다고 판단이 된다면 비관적 락을 고려해볼 수도 있을 것 같습니다.

<br>
