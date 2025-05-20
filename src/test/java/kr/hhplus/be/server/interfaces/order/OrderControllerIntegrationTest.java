package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.point.Amount;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.TransactionType;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.fixtures.PointFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueJpaRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.point.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.point.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static kr.hhplus.be.server.interfaces.order.OrderRequest.*;
import static kr.hhplus.be.server.interfaces.order.OrderResponse.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
    }

    @Test
    void 주문을_정상적으로_생성() {

        // given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());

        Item item = itemJpaRepository.save(ItemFixtures.정상_상품_생성());

        Point point = pointJpaRepository.save(PointFixtures.유저와_금액으로_잔액_생성(user, 20000));

        Coupon coupon = couponJpaRepository.save(CouponFixtures.정액_할인_쿠폰_생성(1000));
        couponIssueJpaRepository.save(CouponIssue.of(user.getId(), coupon));

        OrderCreateRequest request = new OrderCreateRequest(
                coupon.getId(),
                List.of(new OrderItemCreateRequest(item.getId(), 2))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", String.valueOf(user.getId()));
        HttpEntity<OrderCreateRequest> entity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<OrderDetailResponse> response = restTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                entity,
                OrderDetailResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().totalAmount()).isEqualTo(19000);
        assertThat(response.getBody().discountAmount()).isEqualTo(1000);
        assertThat(response.getBody().itemTotalAmount()).isEqualTo(20000);

        assertThat(pointJpaRepository.findByUser(point.getUser()).get().getAmount()).isEqualTo(1000);

        assertThat(pointHistoryJpaRepository.findAll()).hasSize(1);
        assertThat(pointHistoryJpaRepository.findAll().get(0).getType()).isEqualTo(TransactionType.USE);
        assertThat(pointHistoryJpaRepository.findAll().get(0).getOrderId()).isEqualTo(response.getBody().orderId());
        assertThat(pointHistoryJpaRepository.findAll().get(0).getAmount()).isEqualTo(Amount.of(19000));
    }

    @ParameterizedTest
    @NullSource
    void 상품_정보_없이_주문_시_400_예외_발생(List<OrderItemCreateRequest> items) {

        // given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());

        OrderCreateRequest request = new OrderCreateRequest(
                1L,
                items
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", String.valueOf(user.getId()));
        HttpEntity<OrderCreateRequest> entity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<OrderDetailResponse> response = restTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                entity,
                OrderDetailResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(longs = {-100L, -10L, -3L, -2L, -1L, 0L})
    void 양수가_아닌_상품식별자로_주문_시_400_예외_발생(long itemId) {

        // given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());

        OrderCreateRequest request = new OrderCreateRequest(
                1L,
                List.of(new OrderItemCreateRequest(itemId, 2))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", String.valueOf(user.getId()));
        HttpEntity<OrderCreateRequest> entity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<OrderDetailResponse> response = restTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                entity,
                OrderDetailResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -10, -3, -2, -1, 0})
    void 양수가_아닌_주문수량으로_주문_시_400_예외_발생(int count) {

        // given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());

        OrderCreateRequest request = new OrderCreateRequest(
                1L,
                List.of(new OrderItemCreateRequest(1L, count))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", String.valueOf(user.getId()));
        HttpEntity<OrderCreateRequest> entity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<OrderDetailResponse> response = restTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                entity,
                OrderDetailResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(longs = {-100L, -10L, -3L, -2L, -1L, 0L})
    void 존재하지_않는_유저식별자로_주문_시_500_예외_발생(long userId) {

        // given
        OrderCreateRequest request = new OrderCreateRequest(
                1L,
                List.of(new OrderItemCreateRequest(1L, 2))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-USER-ID", String.valueOf(userId));
        HttpEntity<OrderCreateRequest> entity = new HttpEntity<>(request, headers);


        // when
        ResponseEntity<OrderDetailResponse> response = restTemplate.exchange(
                "/api/v1/orders",
                HttpMethod.POST,
                entity,
                OrderDetailResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}