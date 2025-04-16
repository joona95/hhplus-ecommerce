package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueJpaRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private CouponIssueJpaRepository couponIssueJpaRepository;

    @BeforeEach
    void setUp() {
        couponIssueJpaRepository.deleteAll();
        couponJpaRepository.deleteAll();
    }

    @Test
    void 사용자의_보유_쿠폰_목록을_조회() {

        // given
        Coupon coupon1 = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());
        Coupon coupon2 = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());
        couponIssueJpaRepository.saveAll(List.of(
                CouponIssue.of(1L, coupon1),
                CouponIssue.of(1L, coupon2)
        ));

        // when
        ResponseEntity<UserCouponResponse[]> response = restTemplate.getForEntity(
                "/api/v1/coupons?userId=1",
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
    void 양수가_아닌_유저식별자로_보유_쿠폰_목록_조회(long userId) {

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/coupons?userId=" + userId,
                String.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 쿠폰을_발급() {

        // given
        Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());

        CouponIssueRequest request = new CouponIssueRequest(1L, coupon.getId());

        // when
        ResponseEntity<UserCouponResponse> response = restTemplate.postForEntity(
                "/api/v1/coupons",
                request,
                UserCouponResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().couponId()).isEqualTo(coupon.getId());
    }

    @ParameterizedTest
    @ValueSource(longs = {-100L, -10L, -3L, -2L, -1L, 0L})
    void 양수가_아닌_유저식별자로_쿠폰_발급(long userId) {

        // given
        CouponIssueRequest request = new CouponIssueRequest(userId, 1L);

        // when
        ResponseEntity<UserCouponResponse> response = restTemplate.postForEntity(
                "/api/v1/coupons",
                request,
                UserCouponResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(longs = {-100L, -10L, -3L, -2L, -1L, 0L})
    void 양수가_아닌_쿠폰식별자로_쿠폰_발급(long couponId) {

        // given
        CouponIssueRequest request = new CouponIssueRequest(1L, couponId);

        // when
        ResponseEntity<UserCouponResponse> response = restTemplate.postForEntity(
                "/api/v1/coupons",
                request,
                UserCouponResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}