package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueJpaRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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
    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        couponIssueJpaRepository.deleteAll();
        couponJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
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
    class 동시성_테스트 {

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
    }
}
