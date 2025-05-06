package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class ItemConcurrencyTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
    }

    @Test
    void 재고_차감_시_동시_요청이_들어온_경우_요청이_들어온_만큼_재고_차감_발생() throws InterruptedException {

        //given
        Item item = itemJpaRepository.save(ItemFixtures.재고로_상품_생성(Stock.of(20)));

        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        List<ItemCommand.StockDecreaseCommand> commands = List.of(ItemCommand.StockDecreaseCommand.of(item.getId(), 1));

        AtomicInteger failureCount = new AtomicInteger();

        //when
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    itemService.decreaseStocks(commands);
                } catch (Exception e) {
                    failureCount.getAndIncrement();
                }
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();
        long endTime = System.currentTimeMillis();

        //then
        System.out.println("실행 시간 == " + (endTime - startTime) + "ms");

        Optional<Item> result = itemJpaRepository.findById(item.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getStock()).isEqualTo(failureCount.get());

        System.out.println("실패 횟수 : " + failureCount.get() + ", 재고 : " + result.get().getStock());
    }
}
