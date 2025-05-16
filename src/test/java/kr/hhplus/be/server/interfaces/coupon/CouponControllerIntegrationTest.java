package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueJpaRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.store.RedisStoreRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.support.RedisCleanup;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

import static kr.hhplus.be.server.interfaces.coupon.CouponRequest.*;
import static kr.hhplus.be.server.interfaces.coupon.CouponResponse.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CouponControllerIntegrationTest {

    private static final String COUPON_STOCK_KEY_PREFIX = "coupon-stock:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private RedisStoreRepository redisStoreRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private RedisCleanup redisCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
        redisCleanup.flushAll();
    }

    @Nested
    class 사용자_보유_쿠폰_목록_조회 {

        @Test
        void 사용자의_보유_쿠폰_목록을_정상적으로_조회() {

            // given
            User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
            Coupon coupon1 = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());
            Coupon coupon2 = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());
            couponIssueJpaRepository.saveAll(List.of(
                    CouponIssue.of(user.getId(), coupon1),
                    CouponIssue.of(user.getId(), coupon2)
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", String.valueOf(user.getId()));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<UserCouponResponse[]> response = restTemplate.exchange(
                    "/api/v1/coupons",
                    HttpMethod.GET,
                    entity,
                    UserCouponResponse[].class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody()).isNotEmpty();
            assertThat(response.getBody()[0].couponId()).isEqualTo(coupon1.getId());
            assertThat(response.getBody()[1].couponId()).isEqualTo(coupon2.getId());
        }

        @ParameterizedTest
        @ValueSource(longs = {-100L, -10L, -3L, -2L, -1L, 0L})
        void 존재하지_않는_유저식별자로_보유_쿠폰_목록_조회_시_500_예외_발생(long userId) {

            //given
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", String.valueOf(userId));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/coupons",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Nested
    class 쿠폰_발급 {

        @Test
        void 정상적으로_쿠폰_발급() {

            // given
            User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
            Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());
            redisStoreRepository.setAtomicLong(COUPON_STOCK_KEY_PREFIX + coupon.getId(), 1);

            CouponIssueRequest request = new CouponIssueRequest(coupon.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", String.valueOf(user.getId()));
            HttpEntity<CouponIssueRequest> entity = new HttpEntity<>(request, headers);

            // when
            ResponseEntity<UserCouponResponse> response = restTemplate.exchange(
                    "/api/v1/coupons",
                    HttpMethod.POST,
                    entity,
                    UserCouponResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @ParameterizedTest
        @ValueSource(longs = {-100L, -10L, -3L, -2L, -1L, 0L})
        void 양수가_아닌_쿠폰식별자로_쿠폰_발급(long couponId) {

            // given
            User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
            CouponIssueRequest request = new CouponIssueRequest(couponId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", String.valueOf(user.getId()));
            HttpEntity<CouponIssueRequest> entity = new HttpEntity<>(request, headers);

            // when
            ResponseEntity<UserCouponResponse> response = restTemplate.exchange(
                    "/api/v1/coupons",
                    HttpMethod.POST,
                    entity,
                    UserCouponResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}