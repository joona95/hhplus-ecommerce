package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderAmountInfo;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import kr.hhplus.be.server.fixtures.PointFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.order.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.point.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.point.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class PointConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
    }

    @Test
    void 특정_유저_충전_요청이_동시에_들어왔을_때_모든_요청들이_정상적으로_반영된_유저_포인트_값을_반환() throws InterruptedException {

        //given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
        Point point = pointJpaRepository.save(PointFixtures.유저와_금액으로_잔액_생성(user, 0));

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        PointCommand.PointChargeCommand command = new PointCommand.PointChargeCommand(1000);

        AtomicInteger failureCount = new AtomicInteger();

        //when
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    pointService.charge(user, command);
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

        Optional<Point> result = pointJpaRepository.findById(point.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAmount()).isEqualTo(1000 * (threadCount - failureCount.get()));

        System.out.println("실패 횟수 : " + failureCount.get() + ", 포인트 : " + result.get().getAmount());
    }

    @Test
    void 특정_유저_사용_요청이_동시에_들어왔을_때_모든_요청들이_정상적으로_반영된_유저_포인트_값을_반환() throws InterruptedException {

        //given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
        Order order = orderJpaRepository.save(OrderFixtures.유저와_주문가격정보로_주문_생성(user, OrderAmountInfo.of(1000, 1000, 0)));
        Point point = pointJpaRepository.save(PointFixtures.유저와_금액으로_잔액_생성(user, 10000));

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        PointCommand.PointUseCommand command = PointCommand.PointUseCommand.of(order);

        AtomicInteger failureCount = new AtomicInteger();

        //when
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    pointService.use(user, command);
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

        Optional<Point> result = pointJpaRepository.findById(point.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAmount()).isEqualTo(10000 - 1000 * (threadCount - failureCount.get()));

        System.out.println("실패 횟수 : " + failureCount.get() + ", 포인트 : " + result.get().getAmount());
    }

}
