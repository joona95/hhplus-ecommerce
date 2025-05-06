package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.item.PopularItemJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static kr.hhplus.be.server.domain.item.ItemCommand.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private PopularItemJpaRepository popularItemJpaRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
    }

    @Nested
    class 재고_차감 {

        @Test
        void 재고_차감_성공() {

            // given
            Item item = ItemFixtures.재고로_상품_생성(Stock.of(10));

            itemJpaRepository.save(item);

            StockDecreaseCommand command = StockDecreaseCommand.of(item.getId(), 1);

            // when
            Item result = itemService.decreaseStock(command);

            // then
            assertThat(result.getStock()).isEqualTo(9); // 재고 감소 확인
        }
    }

    @Nested
    class 인기_상품_통계_생성 {

        @Test
        void 인기_상품_통계_생성_시_상품식별자별로_그룹핑하여_생성() {

            // given
            List<OrderItem> orderItems = List.of(
                    OrderFixtures.상품식별자로_주문상품_생성(1L),
                    OrderFixtures.상품식별자로_주문상품_생성(1L),
                    OrderFixtures.상품식별자로_주문상품_생성(2L)
            );

            // when
            List<PopularItem> result = itemService.createPopularItemStatistics(orderItems);

            // then
            assertThat(result).hasSize(2);
        }
    }
}
