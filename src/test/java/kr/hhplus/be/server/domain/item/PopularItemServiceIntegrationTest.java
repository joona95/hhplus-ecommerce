package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderItemStatistics;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import kr.hhplus.be.server.infrastructure.item.PopularItemJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class PopularItemServiceIntegrationTest {

    @Autowired
    private PopularItemService popularItemService;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
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

            OrderItemStatistics orderItemStatistics = new OrderItemStatistics(orderItems);

            // when
            List<PopularItemStatistics> result = popularItemService.createPopularItems(orderItemStatistics);

            // then
            assertThat(result).hasSize(2);
        }
    }
}
