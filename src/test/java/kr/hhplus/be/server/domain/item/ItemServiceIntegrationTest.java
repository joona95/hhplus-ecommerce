package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.item.PopularItemJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

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

    @BeforeEach
    void setUp() {
        popularItemJpaRepository.deleteAll();
        itemJpaRepository.deleteAll();
    }

    @Test
    void 재고_차감_성공() {

        // given
        Item item1 = ItemFixtures.재고로_상품_생성(Stock.of(10));
        Item item2 = ItemFixtures.재고로_상품_생성(Stock.of(10));

        itemJpaRepository.saveAll(List.of(item1, item2));

        List<ItemCommand.StockDecreaseCommand> commands = List.of(
                ItemCommand.StockDecreaseCommand.of(item1.getId(), 1),
                ItemCommand.StockDecreaseCommand.of(item2.getId(), 2)
        );

        // when
        List<Item> result = itemService.decreaseStocks(commands);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStock()).isEqualTo(9); // 재고 감소 확인
        assertThat(result.get(1).getStock()).isEqualTo(8); // 재고 감소 확인
    }

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
