package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.coupon.CouponCacheRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponIssueJpaRepository;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class CouponConcurrencyTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private CouponCacheRepository couponCacheRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
    }

    @Test
    void 특정_쿠폰_발급_요청이_동시에_들어오는_경우_쿠폰_발급_개수가_요청_수_만큼_큐에_삽입() throws InterruptedException {

        //given
        Coupon coupon = couponJpaRepository.save(CouponFixtures.발급수량으로_쿠폰_생성(20));
        CouponCommand.CouponIssueCommand command = new CouponCommand.CouponIssueCommand(coupon.getId());
        couponCacheRepository.saveCouponStock(coupon.getId(), 20);

        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        AtomicInteger failureCount = new AtomicInteger();

        //when
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                User user = userJpaRepository.save(UserFixtures.정상_유저_생성());

                try {
                    couponService.requestCouponIssue(user, command);
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

        long result = couponCacheRepository.popIssueTokenUserIds(coupon, Integer.MAX_VALUE).size();

        assertThat(result).isEqualTo(20);

        System.out.println("실패 횟수 : " + failureCount.get() + ", 수량 : " + result);
    }

    @Test
    void 특정_유저의_같은_쿠폰_발급_요청이_동시에_들어오는_경우_중복_발급_발생() throws InterruptedException {

        //given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
        Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        AtomicInteger failureCount = new AtomicInteger();

        //when
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    couponService.issueCoupon(user.getId(), coupon);
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
        assertThat(result.get(0).getUserId()).isEqualTo(user.getId());

        System.out.println("실패 횟수 : " + failureCount.get());
    }
}
