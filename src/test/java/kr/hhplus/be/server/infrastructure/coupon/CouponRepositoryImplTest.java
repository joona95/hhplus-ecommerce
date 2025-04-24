package kr.hhplus.be.server.infrastructure.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
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
    private UserJpaRepository userJpaRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    private CouponRepositoryImpl couponRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
    }

    @Test
    @Transactional
    void 쿠폰_저장_후_조회() {

        // given
        Coupon coupon = CouponFixtures.정상_쿠폰_생성();
        couponJpaRepository.save(coupon);

        // when
        Optional<Coupon> result = couponRepository.findCouponByIdWithLock(coupon.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(coupon);
    }

    @Test
    void 쿠폰_발급_내역_저장_후_조회() {

        // given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
        CouponIssue couponIssue = couponIssueJpaRepository.save(CouponFixtures.유저로_쿠폰_발급_내역_생성(user));

        // when
        Optional<CouponIssue> result = couponRepository.findCouponIssueByUserAndCouponId(user, couponIssue.getCouponId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(couponIssue.getId());
    }

    @Test
    void 유저의_쿠폰_발급_내역_목록_조회() {

        // given
        User user = UserFixtures.식별자로_유저_생성(1L);
        List<CouponIssue> issues = List.of(CouponFixtures.유저로_쿠폰_발급_내역_생성(user));
        couponIssueJpaRepository.saveAll(issues);

        // when
        List<CouponIssue> result = couponRepository.findCouponIssueByUser(user);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void 유저의_쿠폰_발급_내역_존재여부_확인() {

        // given
        CouponIssue couponIssue = CouponFixtures.정상_쿠폰_발급_내역_생성();
        couponIssueJpaRepository.save(couponIssue);

        // when
        boolean result = couponRepository.existsCouponIssueByUserAndCouponId(
                couponIssue.getUser(), couponIssue.getCouponId());

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