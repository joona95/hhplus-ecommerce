package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderAmountInfo;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueJpaRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static kr.hhplus.be.server.domain.coupon.CouponCommand.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class CouponServiceIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private CouponIssueJpaRepository couponIssueJpaRepository;

    @BeforeEach
    void setUp() {
        couponIssueJpaRepository.deleteAll();
        couponJpaRepository.deleteAll();
    }

    @Nested
    class 쿠폰_발급 {

        @Test
        void 정상적으로_쿠폰_발급() {

            // given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());

            CouponIssueCommand command = new CouponIssueCommand(coupon.getId());

            // when
            CouponIssue couponIssue = couponService.issueCoupon(user, command);

            // then
            assertThat(couponIssue).isNotNull();
            assertThat(couponIssue.getCouponId()).isEqualTo(coupon.getId());
        }

    }

    @Nested
    class 쿠폰_할인_적용 {

        @Test
        void 정액_할인_쿠폰_적용() {

            // given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Order order = OrderFixtures.주문가격정보로_주문_생성(OrderAmountInfo.of(10000, 10000, 0));
            Coupon coupon = couponJpaRepository.save(CouponFixtures.정액_할인_쿠폰_생성(1000));
            couponIssueJpaRepository.save(CouponIssue.of(user, coupon));

            CouponApplyCommand command = new CouponApplyCommand(order, coupon.getId());

            // when
            int discountAmount = couponService.applyCoupon(command);

            // then
            assertThat(discountAmount).isEqualTo(1000);
        }

        @Test
        void 정률_할인_쿠폰_적용() {

            // given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Order order = OrderFixtures.주문가격정보로_주문_생성(OrderAmountInfo.of(10000, 10000, 0));
            Coupon coupon = couponJpaRepository.save(CouponFixtures.정률_할인_쿠폰_생성(10));
            couponIssueJpaRepository.save(CouponIssue.of(user, coupon));

            CouponApplyCommand command = new CouponApplyCommand(order, coupon.getId());

            // when
            int discountAmount = couponService.applyCoupon(command);

            // then
            assertThat(discountAmount).isEqualTo(1000);
        }
    }

}
