# 캐시 보고서

## 1. 캐시란?

### 1-1. 정의

- 느리거나 비용이 큰 저장소(데이터베이스, 외부 API, 디스크 등)에 있는 데이터를 더 빠른 매체에 임시 저장해두고 재사용하는 기술
- 동일한 요청이 반복되거나, 데이터의 실시간성이 약간 떨어져도 괜찮고, 읽기 요청이 쓰기보다 압도적으로 많은 구조에서 효과적임
- 잘못 사용될 경우 데이터 불일치(정합성 문제), 오래된 데이터 반환, 캐시 적중률 저하에 따른 성능 저하, 캐시 서버 장애 시 서비스 영향 등의 문제 발생 가능
- 물리적으로 브라우저, CDN, 애플리케이션 내부 메모리, 외부 분산 캐시 서버(Redis, Memcached), 그리고 DB 자체의 버퍼 캐시 등 다양한 위치에 존재 가능
- 사용 목적에 따라 복수 계층으로 구성되기도 함

### 1-2. 특징

- 지연(latency) 단축
- 백엔드 부하 감소
- 통신비 및 과금 절감

### 1-3. 작동원리

1)클라이언트가 요청을 보내면, 애플리케이션에서 해당 요청에 대한 데이터를 캐시에서 조회

2)캐시에 데이터가 존재하면 (CACHE HIT), 원본 저장소 거치지 않고 캐시된 데이터 즉시 반환

3)캐시에 데이터가 없으면 (CACHE MISS), 원본 저장소에서 데이터 조회 후 응답하고 다음 요청부터는 재사용 가능하도록 캐시 저장

4)캐시는 일정 시간이 지나면 만료되거나, LRU/LFU/FIFO 같은 정책에 따라 오래된 데이터 제거하여 공간 확보

## 2. 캐시 전략

### 2-1. 읽기 전략

> 데이터를 언제, 어떻게 캐시에 불러오고 갱신할 것인가?

#### 1) Cache-Aside

- 애플리케이션이 먼저 캐시에서 데이터 조회하고, 없을 경우 원본 저장소에서 데이터 가져온 후 캐시에 저장하는 전략
- 장점
    - 구현이 간단하고 가장 널리 사용됨
    - 필요한 데이터만 캐시에 올라가기 때문에 메모리 낭비가 적음
- 단점
    - 초기 요청은 반드시 DB 조회해야 하므로 초기 응답 지연 발생 가능
    - 다수의 요청이 동시에 MISS될 경우 스탬피드 발생 가능

#### 2) Read-Through

- 캐시 계층이 DB를 프록시처럼 동작하여, 캐시에 데이터가 없으면 자동으로 DB에서 데이터 조회하고 캐시에 저장한 뒤 클라이언트에 반환하는 전략
- 장점
    - 애플리케이션은 캐시만 조회하면 되어 로직이 단순해짐
    - 캐시 일관성이 보장되기 쉬운 구조
- 단점
    - 캐시 계층에 복잡한 로직이 들어감
    - 시스템 설계에 제약 발생 가능

#### 3) Refresh-Ahead

- 데이터가 TTL로 만료되기 전에 백그라운드 스레드나 스케줄러가 미리 데이터를 갱신해 캐시를 최신 상태로 유지하는 전략
- 장점
    - TTL 만료 시점에 캐시 MISS를 방지할 수 있어 캐시 스탬피드를 막을 수 있음
- 단점
    - 실제 사용되지 않는 데이터도 미리 불러오기 때문에 메모리 낭비 발생 가능

### 2-2. 쓰기 전략

>원본 저장소와 캐시 간 데이터를 어떻게 동기화할 것인가

#### 1) Write-Through

- 데이터를 캐시에 먼저 기록하고, 동시에 DB에도 동기적으로 반영하는 전략
- 장점
    - 캐시와 DB가 항상 같은 값을 가지며, 데이터 정합성 유지하기 쉬움
- 단점
    - 쓰기 작업의 속도가 캐시와 DB 모두에 영향을 받으므로 느릴 수 있음

#### 2) Write-Behind

- 캐시에만 데이터를 기록하고, 일정 시간 또는 조건이 충족되면 배치로 DB에 반영하는 전략
- 장점
    - 고속의 쓰기 성능을 제공함
    - 대량의 데이터를 일괄 처리 가능함
- 단점
    - 캐시가 날아가거나 장애가 발생하면 데이터 유실 가능
    - 정합성 확보가 어려울 수 있음

#### 3) Write-Around

- 데이터를 캐시에 저장하지 않고 바로 DB에만 기록하고, 이후 해당 데이터가 조회되면 그 때 캐시에 로드되는 전략
- 장점
    - 자주 조회되지 않는 데이터가 캐시에 저장되지 않아 메모리 낭비 줄일 수 있음
- 단점
    - 최근에 쓰여진 데이터를 즉시 조회할 경우 캐시 MISS가 발생해 응답 속도 느려질 수 있음

#### 4) Invalidate-on-Write

- DB에 데이터를 쓰는 동시에 해당 캐시 키를 삭제(무효화)하는 전략
- 장점
    - 단순하면서도 캐시가 오래된 데이터 반환하지 않도록 막을 수 있음
- 단점
    - 삭제 후 다음 요청이 들어올 때까지 캐시가 비어 있으므로 첫 요청은 느려질 수 있음

### 2-3. 일관성 전략

>캐시된 데이터의 신선도 유지와 저장소 정합성을 어떻게 유지할 것인가?

#### 1) TTL(Time To Live)

- 캐시에 저장된 데이터에 유효시간(TTL)을 설정하고 시간이 지나면 자동으로 삭제되도록 하는 전략
- 장점
    - 캐시가 자동으로 갱신되며 오래된 데이터가 남아있지 않도록 함
- 단점
    - TTL을 너무 짧게 설정하면 캐시 HIT가 줄어들고, 너무 길게 설정하면 오래된 데이터가 반환될 수 있음

#### 2) LRU (Least Recently Used) / LFU (Least Frequently Used) / FIFO (First In First Out)

- 메모리 부족 시 오래된 키(LRU), 사용 빈도가 낮은 키(LFU), 또는 먼저 들어온 키(FIFO)를 제거하는 전략
- 장점
    - 메모리 사용량을 제한할 수 있어 시스템 안정성 높음
- 단점
    - 중요한 데이터가 예상치 못하게 제거될 수 있음

#### 3) Manual Invalidate

- 애플리케이션에서 DB를 변경한 후 해당 캐시를 수동으로 삭제하는 전략
- 장점
    - 언제 무효화할지 개발자가 명확히 제어 가능
- 단점
    - 무효화를 잊어먹거나 예외처리를 놓치면 정합성 문제 발생 가능

#### 4) Pub/Sub Invalidate

- 데이터 변경이 발생한 서버에서 Redis Pub/Sub 등을 이용해 무효화 이벤트를 발행하고 구독 중인 서비스들이 캐시를 함께 삭제하는 전략
- 장점
    - 분산 캐시 환경에서 캐시 일관성을 효율적으로 맞출 수 있음
- 단점
    - Pub/Sub 시스템이 동작하지 않으면 일부 캐시가 무효화되지 않을 수 있음

#### 5) Version Token

- 캐시 데이터와 함께 버전 값을 저장하고 조회 시 버전이 일치하는지 확인해 정합성 유지하는 전략
- 장점
    - 데이터 신선도를 정밀하게 제어 가능
- 단점
    - 캐시 조회 로직이 복잡함
    - 버전 추가 관리 비용이 발생함

#### 6) 분산락 + Double-Check

- 캐시 MISS 시 많은 요청이 동시에 DB를 조회하는 현상(Cache Stampede)를 막기 위해, 락을 이용해 한 요청만 DB를 조회하고, 나머지는 기다리거나 재시도하게 하는 전략
- 장점
    - 스탬피드 문제를 효과적으로 방지 가능
- 단점
    - 락 획득/해제 비용이 발생함
    - 락 풀림 시 재시도 로직이 복잡해질 수 있음
 
## 3. 캐시 적용 시나리오

이커머스 프로젝트 기능 중 인기 상품 목록 조회와 상품 상세 조회에 캐시 적용을 하고자 했습니다.

인기 상품 목록 조회와 상품 상세 조회는 모두 읽기 요청이 빈번하고, 실시간성이 강하지 않으며, 동일 데이터를 여러 사용자가 반복조회하는 구조를 가지고 있어, 캐시를 적용하면 응답 속도 향상 및 백엔드 부하 감소에 효과적일 것으로 판단했습니다.

### 3-1. 상품 상세 조회

#### 1) 캐시 적용 이유

- 많은 사용자가 동일한 상품을 반복해서 조회함
- 상품 정보는 자주 수정되지 않으며, 수정 시 캐시 삭제(Evict)로 정합성 유지 가능

#### 2) 캐싱 전략

- 상품 상세 데이터는 자주 변경되지 않고, 변경이 발생했을 때 캐시만 무효화하면 충분히 일관성을 유지할 수 있음
- Cache-Aside + TTL + Evict-On-Write(Invalidate-On-Write)
    - 사용자 요청 시 캐시에 없으면 DB 조회 후 캐시에 저장
    - TTL 설정으로 자동 만료 처리
    - 상품 정보 수정 시 캐시를 명시적으로 삭제하여 데이터 정합성 유지

#### 3) 캐시 구현

- 상품 상세 조회 시 캐시 저장

```java
	  @Cacheable(value = "cache:item", key = "#id")
    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }
```

- 상품 정보 수정 시 캐시 무효화

```java
@CacheEvict(value = "cache:item", key = "#itemId")
public Item updateItem(Long itemId, ItemUpdateCommand command) {
	
		Item item = findById(itemId);
		
		item.update(command);

    return itemRepository.save(item);
}
```

#### 4) 캐시 테스트

- 상품 상세 조회 캐시 저장 테스트

```java
		@Test
    void 상품_상세_조회_시_여러_번_호출해도_DB_1회_조회() {

        //given
        Item item = itemJpaRepository.save(ItemFixtures.정상_상품_생성());

        //when
        itemService.findById(item.getId());
        itemService.findById(item.getId());
        itemService.findById(item.getId());

        //then
        verify(itemJpaRepository, times(1)).findById(item.getId());
    }
```

상품상세조회를 3번 요청했을 때, 캐시 설정 후 최초에 DB 1회 접근 후 캐시에서 정보를 가져오는지를 테스트해보도록 하겠습니다.

<img width="1209" alt="Image" src="https://github.com/user-attachments/assets/ad5f295a-5d11-4727-9b99-dd64bdb35694" />

캐시 설정 전 상품 상세 조회를 3회 요청하는 경우 DB 접근이 3회 일어나는 것을 볼 수 있습니다.

<img width="904" alt="Image" src="https://github.com/user-attachments/assets/03437acb-88c6-4802-8b52-5206eb96f8cc" />

캐시 설정 후에는 상품 상세 조회를 3회 요청하는 경우 DB 접근이 1회 일어나고, 기존보다 더 빠른 속도로 테스트 수행이 된 것을 볼 수 있습니다.


- 상품 정보 수정 캐시 무효화 테스트

```java
		@Test
    void 상품_정보_수정_시_캐시_무효화_후_DB_저장_값_조회() {

        //given
        Item item = itemJpaRepository.save(ItemFixtures.정상_상품_생성());

        ItemUpdateCommand command = ItemUpdateCommand.of("상품명수정", 50000, 500);

        itemService.findById(item.getId()); // DB 접근 후 캐시 저장

        //when
        itemService.updateItem(item.getId(), command); // 캐시 무효화
        Item result = itemService.findById(item.getId()); // DB 재접근

        //then
        verify(itemJpaRepository, times(3)).findById(item.getId());

        assertThat(result.getItemName()).isEqualTo("상품명수정");
        assertThat(result.getPrice()).isEqualTo(50000);
        assertThat(result.getStockCount()).isEqualTo(500);
    }
```

상품 정보 수정을 요청하는 경우, 캐시 무효화가 되는 것을 확인하고자 합니다. 최초에 상품 조회 시 DB 1회 접근 후 캐시에 저장한 상태에서, 상품 정보 수정 요청을 하여 캐시 무효화를 진행합니다. 상품 정보 수정 요청 시 캐시 무효화가 되어 이후 다시 상품 조회 시 DB 1회 재접근하는지 살펴보고자 합니다.

상품 상세 조회 시 캐시 저장이 되고 있는 상태에서 상품 정보 수정을 했을 경우, 다시 상품 상세 조회 시 DB 저장된 값을 가져오는지 확인하고자 합니다.

<img width="935" alt="Image" src="https://github.com/user-attachments/assets/06572473-9b13-4340-9268-28f556b61886" />

캐시 무효화 설정 전 상품 정보 수정 요청이 들어가더라도, 상품 조회로 캐시가 저장될 때 1회와 수정을 위한 조회 시 1회로 총 2회의 DB 접근이 일어나는 것을 볼 수 있습니다.

<img width="522" alt="Image" src="https://github.com/user-attachments/assets/b8285551-08e3-4a38-b1e0-9bae4a7d2196" />

그리고 상품 정보 수정 요청 이후 상품 상세 조회 시 수정된 값이 아니라 이전에 캐시에 저장된 정보를 가져오는 것을 볼 수 있습니다.

<img width="1096" alt="Image" src="https://github.com/user-attachments/assets/681b26da-9d5c-4293-a7d4-b345b972b329" />

캐시 무효화 설정 후에는 상품 정보 수정 요청이 들어간 후, 상품 조회 시 DB 1회 접근하여 저장된 캐시가 무효화되어 DB 접근이 1회 다시 일어나는 것을 볼 수 있습니다. 

그리고 상품 정보 수정 요청 이후 상품 상세 조회 시 수정된 값을 찾아오는 것을 볼 수 있습니다.


### 3-2. 인기 상품 목록 조회

#### 1) 캐시 적용 이유

- 홈 화면, 검색 화면 등에서 자주 노출되어 요청 빈도가 매우 높음
- 조회 결과가 자주 바뀌지 않고 일정 주기로만 갱신됨

#### 2) 캐싱 전략

- 인기 상품 목록은 실시간성이 높지 않고, 정해진 주기로만 갱신되므로 매 요청마다 최신 데이터를 유지할 필요가 없음
- Cache-Aside + TTL
    - 사용자 요청 시 캐시에 데이터가 없으면 DB 조회 후 캐시에 저장하는 방식
    - TTL(Time-To-Live)을 활용해 일정 시간 후 자동으로 만료되도록 설정함
    - 상품 가격 정보 등이 수정된 경우 반영될 필요가 있으므로 상품 식별자만 가지고 Item 정보는 따로 조회하도록 처리

#### 3) 캐시 구현

```java
		@Cacheable(value = "cache:popular-items", key = "'cache:popular-items'")
    public List<PopularItem> findPopularItems() {
        return Optional.ofNullable(itemRepository.findPopularItems())
                       .orElse(List.of());
    }
```

- 상품 식별자를 정보로 가지는 인기 상품 목록 정보를 캐시에 저장합니다.

```java
public class ItemFacadeService {

		private final PopularItemService popularItemService;
		private final ItemService itemService;

    public List<PopularItemDetail> findPopularItemDetails() {
        return findPopularItems().stream()
                .map(p -> PopularItemDetail.of(p, itemService.findById(p.itemId())))
                .toList();
    }
}
```

- 상품 가격 정보 등이 수정된 경우 반영될 필요가 있으므로 상품 식별자만 가지고 Item 정보는 따로 itemService.findById(itemId) 로 조회하도록 처리하였습니다.
- 인기 상품 목록 수만큼 findById 요청이 들어간다고 했을 때, 캐시에 해당 itemId 의 상품 정보가 없는 경우 DB 조회가 상품 목록 수만큼 요청이 들어가 N+1문제 발생 가능성이 있습니다.
    - 그러나 인기 상품 목록 조회의 경우, 현재 5개로 제한이 되어 있고 추후로도 갯수 제한이 있을 가능성이 높기 때문에 N+1문제가 성능에 영향을 줄 정도가 아니라고 판단되어 이렇게 유지하려고 합니다.
- `@Cacheable` 을 사용하여 캐시 적용 시 내부 호출의 경우 작동하지 않기 때문에 사실 Item과 PopularItem 도메인이 현재로는 크게 연관성이 없어서 서비스를 분리하여 ItemFacadeService에서 조합한 결과물을 전달하기로 하였습니다.

#### 4) 캐시 테스트

- 인기 상품 목록 캐시 저장 테스트

```java
//PopularItemService
		@Test
    void 인기_상품_목록_조회_시_여러_번_호출해도_인기_상품_DB_1회() {

        // given
        List<Item> items = itemJpaRepository.saveAll(List.of(
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성()
        ));

        for (int i = 0; i < items.size(); i++) {
            popularItemStatisticsJpaRepository.saveAll(List.of(
                    ItemFixtures.상품식별자와_주문날짜와_주문수량으로_인기_상품_통계_생성(items.get(i).getId(), LocalDate.now().minusDays(1), 100),
                    ItemFixtures.상품식별자와_주문날짜와_주문수량으로_인기_상품_통계_생성(items.get(i).getId(), LocalDate.now().minusDays(1), 100)
            ));
        }

        //when
        popularItemService.findPopularItems();
        popularItemService.findPopularItems();
        popularItemService.findPopularItems();

        //then
        verify(popularItemStatisticsJpaRepository, times(1)).findPopularItems();
    }
```

인기 상품 목록 조회를 3회 요청하는 경우, 캐시 설정 후 최초에 DB 1회 접근 후 캐시에서 정보를 가져오는지를 테스트해보도록 하겠습니다.

<img width="1104" alt="Image" src="https://github.com/user-attachments/assets/8e10e334-62c6-4d4e-ac60-9b0cd9d88169" />

캐시 설정 전 인기 상품 목록 조회를 3회 요청하는 경우 DB 접근이 3회 일어나는 것을 볼 수 있습니다.

<img width="1214" alt="Image" src="https://github.com/user-attachments/assets/d57ccc36-7621-47d4-900a-07fd900d5e11" />

캐시 설정 후에는 인기 상품 목록 조회를 3회 요청하는 경우 DB 접근이 1회 일어나고, 기존보다 더 빠른 속도로 테스트 수행이 된 것을 볼 수 있습니다.


- 인기 상품 목록 조회 시 인기 상품과 상품 캐시 저장 테스트

```java
//ItemFacadeService
		@Test
    void 인기_상품_목록_조회_시_여러_번_호출해도_인기_상품_DB_1회_조회와_상품_DB_5회_조회() {

        // given
        List<Item> items = itemJpaRepository.saveAll(List.of(
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성()
        ));

        for (int i = 0; i < items.size(); i++) {
            popularItemStatisticsJpaRepository.saveAll(List.of(
                    ItemFixtures.상품식별자와_주문날짜와_주문수량으로_인기_상품_통계_생성(items.get(i).getId(), LocalDate.now().minusDays(1), 100),
                    ItemFixtures.상품식별자와_주문날짜와_주문수량으로_인기_상품_통계_생성(items.get(i).getId(), LocalDate.now().minusDays(1), 100)
            ));
        }

        //when
        itemFacadeService.findPopularItemDetails();
        itemFacadeService.findPopularItemDetails();
        itemFacadeService.findPopularItemDetails();

        //then
        verify(popularItemStatisticsJpaRepository, times(1)).findPopularItems();
        verify(itemJpaRepository, times(5)).findById(any());
    }
```

인기 상품 목록 조회를 3회 요청하는 경우, 캐시 설정 후 최초에 인기 상품 DB 1회 접근하는 것을 살펴보겠습니다. 또한, 인기 상품 조회로 얻은 상품식별자에 해당하는 상품 조회를 위해 상품  DB 5회 접근 후 캐시에서 정보를 가져오는지를 테스트해보도록 하겠습니다.

<img width="1045" alt="Image" src="https://github.com/user-attachments/assets/9cf11b7d-37c9-4265-b97e-065f4d6bd85a" />

캐시 설정 전 인기 상품 목록 조회를 3회 요청하는 경우 인기 상품 DB 접근이 3회 일어나는 것을 볼 수 있습니다. 그리고 인기 상품에 해당하는 상품 상세 조회는 캐시 적용으로 5회 접근하는 것을 살펴볼 수 있습니다.

<img width="1255" alt="Image" src="https://github.com/user-attachments/assets/3745f42f-e38c-408b-9264-0bd1ce8a3ec5" />

캐시 설정 후에는 인기 상품 목록 조회를 3회 요청하는 경우 인기 상품 DB 접근이 1회 일어나고 인기 상품에 해당하는 상품 목록 조회 5회 일어나는 것을 볼 수 있습니다. 

## 4. 캐시 성능 테스트

K6 스크립트를 활용하여 캐시 적용 전과 캐시 적용 후의 성능 비교 테스트를 진행하였습니다.

### 4-1. 상품 상세 조회

| 항목 | **캐시 적용 전** | **캐시 적용 후** | ✅ 의미 있는 변화 |
| --- | --- | --- | --- |
| 요청 수 (http_reqs) | 173,981 (2,174/s) | **439,025 (5,487/s)** | ✅ 처리량 2.5배 증가 |
| 평균 응답시간 (avg duration) | **21.31ms** | **8.46ms** | ✅ 60% 이상 감소 |
| 응답시간 p(90) | 39.35ms | 18.59ms | ✅ 고부하에서도 안정적 |
| 실패 요청률 (http_req_failed) | 0% | 0% | ✅ 안정성 동일 |
| 체크 실패 (응답 300ms 초과) | 없음 | 240건 (0.02%) | ⚠️ 약간 발생했으나 매우 낮음 |
| 처리 성공률 (checks_succeeded) | 100% | 99.97% | ⚠️ 사실상 무시 가능한 차이 |
| 평균 반복 시간 | 21.4ms | 8.5ms | ✅ 반복 속도 2.5배 개선 |
| VU 기준 최대 처리량 | 1VU당 2,174/s | 1VU당 5,487/s | ✅ VU당 효율 향상 |
| 수신 데이터량 | 31MB | 78MB | ⚠️ 더 많이 처리함에 따른 증가 |
| 송신 데이터량 | 15MB | 37MB | ⚠️ 동일 |

### 4-2. 인기 상품 목록 조회

| 항목 | **캐시 적용 전** | **캐시 적용 후** | ✅ 의미 있는 변화 |
| --- | --- | --- | --- |
| 요청 수 (http_reqs) | 302,578 (3,782/s) | **613,145 (7,664/s)** | ✅ **2배 이상 처리량 향상** |
| 평균 응답시간 (avg duration) | **12.27ms** | **6.08ms** | ✅ **절반 수준으로 단축** |
| 90퍼센타일 응답시간 (p90) | 31.03ms | **9.45ms** | ✅ 지연 요청 크게 개선 |
| 최대 응답시간 (max) | 608ms | **6.69s** (스파이크) | ⚠️ 극단값 존재하지만 빈도 낮음 |
| 실패 요청률 (http_req_failed) | 0% | 0% | ✅ 안정성 동일 |
| 300ms 초과 응답 실패 건수 | 144건 (0.02%) | 199건 (0.01%) | ⚠️ 유사 수준, 무시 가능 |
| 평균 반복 시간 | 12.31ms | 6.11ms | ✅ 반복 속도 개선 |
| 처리 성공률 (checks_succeeded) | 99.97% | 99.98% | ⚪ 무시 가능 |
| 수신 데이터량 | 38MB | 77MB | ⚠️ 처리량 증가에 따른 증가 |
| 송신 데이터량 | 27MB | 55MB | ⚠️ 동일 |
