package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueJpaRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.support.RedisCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static kr.hhplus.be.server.domain.coupon.CouponCommand.*;
import static org.assertj.core.api.Assertions.assertThat;

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
    private RedissonClient redissonClient;

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
            RAtomicLong rAtomicLong = redissonClient.getAtomicLong(COUPON_STOCK_KEY_PREFIX + couponId);
            rAtomicLong.set(10);

            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());

            CouponIssueCommand command = new CouponIssueCommand(coupon.getId());

            // when
            couponService.requestCouponIssue(user, command);

            // then
            RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(COUPON_ISSUE_TOKEN_KEY_PREFIX + command.couponId());
            assertThat(zset.size()).isEqualTo(1);
            assertThat(zset.contains(couponId)).isTrue();

            RSet<Long> rSet = redissonClient.getSet(COUPON_ISSUE_PENDING_KEY, LongCodec.INSTANCE);
            assertThat(rSet.size()).isEqualTo(1);
            assertThat(rSet.contains(couponId)).isTrue();
        }

        @Test
        void 쿠폰_발급_토큰_수량보다_발급량이_많거나_같은_경우_발급_요청되지_않음() {

            // given
            long couponId = 1L;
            RAtomicLong rAtomicLong = redissonClient.getAtomicLong(COUPON_STOCK_KEY_PREFIX + couponId);
            rAtomicLong.set(0);

            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());

            CouponIssueCommand command = new CouponIssueCommand(coupon.getId());

            // when
            couponService.requestCouponIssue(user, command);

            // then
            RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(COUPON_ISSUE_TOKEN_KEY_PREFIX + command.couponId());
            assertThat(zset.size()).isEqualTo(0);

            RSet<Long> rSet = redissonClient.getSet(COUPON_ISSUE_PENDING_KEY, LongCodec.INSTANCE);
            assertThat(rSet.size()).isEqualTo(0);
        }
    }
}
