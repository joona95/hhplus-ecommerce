package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class CouponRepositoryImplTest {

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    private CouponRepositoryImpl couponRepository;

    @BeforeEach
    void setUp() {
        couponIssueJpaRepository.deleteAll();
        couponJpaRepository.deleteAll();
    }

    @Test
    void 쿠폰_저장_후_조회() {

        // given
        Coupon coupon = CouponFixtures.정상_쿠폰_생성();
        couponJpaRepository.save(coupon);

        // when
        Optional<Coupon> result = couponRepository.findCouponById(coupon.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(coupon);
    }

    @Test
    void 쿠폰_발급_내역_저장_후_조회() {

        // given
        CouponIssue couponIssue = CouponFixtures.정상_쿠폰_발급_내역_생성();
        couponIssueJpaRepository.save(couponIssue);

        // when
        Optional<CouponIssue> result = couponRepository.findByUserIdAndCouponId(
                couponIssue.getUserId(), couponIssue.getCouponId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(couponIssue);
    }

    @Test
    void 유저의_쿠폰_발급_내역_목록_조회() {

        // given
        List<CouponIssue> issues = List.of(CouponFixtures.정상_쿠폰_발급_내역_생성(), CouponFixtures.정상_쿠폰_발급_내역_생성());
        couponIssueJpaRepository.saveAll(issues);

        // when
        List<CouponIssue> result = couponRepository.findByUserId(1L);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void 유저의_쿠폰_발급_내역_존재여부_확인() {

        // given
        CouponIssue couponIssue = CouponFixtures.정상_쿠폰_발급_내역_생성();
        couponIssueJpaRepository.save(couponIssue);

        // when
        boolean result = couponRepository.existsCouponIssueByUserIdAndCouponId(
                couponIssue.getUserId(), couponIssue.getCouponId());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 쿠폰_발급_내역_저장() {

        // given
        CouponIssue couponIssue = CouponFixtures.정상_쿠폰_발급_내역_생성();

        // when
        CouponIssue result = couponRepository.saveCouponIssue(couponIssue);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result).isEqualTo(couponIssue);
    }
}