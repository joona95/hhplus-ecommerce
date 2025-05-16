package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueJpaRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.store.RedisStoreRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.support.RedisCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static kr.hhplus.be.server.domain.coupon.CouponCommand.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
public class CouponServiceIntegrationTest {

    private static final String COUPON_STOCK_KEY_PREFIX = "coupon-stock:";
    private static final String COUPON_ISSUE_TOKEN_KEY_PREFIX = "coupon-issue-token:";
    private static final String COUPON_ISSUE_PENDING_KEY = "coupon-issue-pending";

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private CouponIssueJpaRepository couponIssueJpaRepository;

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
    class 쿠폰_발급 {

        @Test
        void 정상적으로_쿠폰_발급() {

            // given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());

            // when
            couponService.issueCoupon(user.getId(), coupon);

            // then
            assertThat(couponIssueJpaRepository.findAll()).hasSize(1);
            assertThat(couponIssueJpaRepository.findAll().get(0).getUserId()).isEqualTo(user.getId());
            assertThat(couponIssueJpaRepository.findAll().get(0).getCouponId()).isEqualTo(coupon.getId());
        }
    }

    @Nested
    class 쿠폰_발급_토큰_요청 {

        @Test
        void 정상적으로_쿠폰_발급_토큰_등록() {

            // given
            long couponId = 1L;
            redisStoreRepository.setAtomicLong(COUPON_STOCK_KEY_PREFIX + couponId, 10L);

            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());

            CouponIssueCommand command = new CouponIssueCommand(coupon.getId());

            // when
            couponService.requestCouponIssue(user, command);

            // then
            String couponIssueKey = COUPON_ISSUE_TOKEN_KEY_PREFIX + couponId;
            assertThat(redisStoreRepository.getSortedSetSize(couponIssueKey)).isEqualTo(1);
            assertThat(redisStoreRepository.popSortedSetBatch(couponIssueKey, Integer.MAX_VALUE)
                    .contains(user.getId()))
                    .isTrue();

            Set<Long> pendingCouponIds = redisStoreRepository.popSetAll(COUPON_ISSUE_PENDING_KEY);
            assertThat(pendingCouponIds).hasSize(1);
            assertThat(pendingCouponIds.contains(couponId)).isTrue();
        }

        @Test
        void 쿠폰_발급_토큰_수량보다_발급량이_많은_경우_토큰_제거_및_RuntimeException_발생() {

            // given
            long couponId = 1L;
            redisStoreRepository.setAtomicLong(COUPON_STOCK_KEY_PREFIX + couponId, 1);

            User user1 = UserFixtures.식별자로_유저_생성(1L);
            User user2 = UserFixtures.식별자로_유저_생성(2L);
            Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());

            CouponIssueCommand command = new CouponIssueCommand(coupon.getId());

            couponService.requestCouponIssue(user1, command);

            // when, then
            assertThatThrownBy(() -> couponService.requestCouponIssue(user2, command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("쿠폰 발급 가능한 수량을 초과하였습니다.");

            String couponIssueKey = COUPON_ISSUE_TOKEN_KEY_PREFIX + command.couponId();
            assertThat(redisStoreRepository.getSortedSetSize(couponIssueKey)).isEqualTo(1);
            List<Long> couponIssueUserIds = redisStoreRepository.popSortedSetBatch(couponIssueKey, Integer.MAX_VALUE);
            assertThat(couponIssueUserIds.contains(user1.getId())).isTrue();
            assertThat(couponIssueUserIds.contains(user2.getId())).isFalse();

            Set<Long> pendingCouponIds = redisStoreRepository.popSetAll(COUPON_ISSUE_PENDING_KEY);
            assertThat(pendingCouponIds).hasSize(1);
            assertThat(pendingCouponIds.contains(couponId)).isTrue();
        }

        @Test
        void 동일_유저가_중복_쿠폰_발급_요청_시_한_번만_토큰_발급() {

            // given
            long couponId = 1L;
            redisStoreRepository.setAtomicLong(COUPON_STOCK_KEY_PREFIX + couponId, 1);

            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());

            CouponIssueCommand command = new CouponIssueCommand(coupon.getId());

            couponService.requestCouponIssue(user, command);

            // when
            couponService.requestCouponIssue(user, command);

            // then
            String couponIssueKey = COUPON_ISSUE_TOKEN_KEY_PREFIX + couponId;
            assertThat(redisStoreRepository.getSortedSetSize(couponIssueKey)).isEqualTo(1);
            assertThat(redisStoreRepository.popSortedSetBatch(couponIssueKey, Integer.MAX_VALUE)
                    .contains(user.getId()))
                    .isTrue();

            Set<Long> pendingCouponIds = redisStoreRepository.popSetAll(COUPON_ISSUE_PENDING_KEY);
            assertThat(pendingCouponIds).hasSize(1);
            assertThat(pendingCouponIds.contains(couponId)).isTrue();
        }
    }
}
