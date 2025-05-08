package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.PopularItem;
import kr.hhplus.be.server.domain.item.PopularItemStatistics;
import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class PopularItemRepositoryImplTest {

    @Autowired
    private PopularItemRepositoryImpl popularItemRepository;

    @Autowired
    private PopularItemJpaRepository popularItemJpaRepository;

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
    }

    @Test
    void 인기_상품_저장() {

        // given
        List<PopularItemStatistics> items = List.of(ItemFixtures.상품식별자로_인기_상품_통계_생성(1L));

        // when
        List<PopularItemStatistics> result = popularItemRepository.savePopularItems(items);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItemId()).isEqualTo(1L);
    }

    @Test
    void 최근_3일간_1일전부터_3일전까지_주문된_판매량_수가_가장_많은_상위_5개_인기_상품_조회() {

        // given
        List<Item> items = itemJpaRepository.saveAll(List.of(
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성()
        ));

        for (int i = 0; i < items.size(); i++) {
            PopularItemStatistics today = ItemFixtures.상품식별자와_주문날짜와_주문수량으로_인기_상품_통계_생성(items.get(i).getId(), LocalDate.now(), 100 * i);
            PopularItemStatistics yesterday = ItemFixtures.상품식별자와_주문날짜와_주문수량으로_인기_상품_통계_생성(items.get(i).getId(), LocalDate.now().minusDays(1), 100 * i);
            PopularItemStatistics recent = ItemFixtures.상품식별자와_주문날짜와_주문수량으로_인기_상품_통계_생성(items.get(i).getId(), LocalDate.now().minusDays(3), 100 * i);
            PopularItemStatistics old = ItemFixtures.상품식별자와_주문날짜와_주문수량으로_인기_상품_통계_생성(items.get(i).getId(), LocalDate.now().minusDays(4), 100 * i);
            popularItemJpaRepository.saveAll(List.of(today, yesterday, recent, old));
        }

        // when
        List<PopularItem> result = popularItemRepository.findPopularItems();

        // then
        assertThat(result.get(0).itemId()).isEqualTo(items.get(6).getId());
        assertThat(result.get(0).orderCount()).isEqualTo(1200);
        assertThat(result.get(1).itemId()).isEqualTo(items.get(5).getId());
        assertThat(result.get(1).orderCount()).isEqualTo(1000);
        assertThat(result.get(2).itemId()).isEqualTo(items.get(4).getId());
        assertThat(result.get(2).orderCount()).isEqualTo(800);
        assertThat(result.get(3).itemId()).isEqualTo(items.get(3).getId());
        assertThat(result.get(3).orderCount()).isEqualTo(600);
        assertThat(result.get(4).itemId()).isEqualTo(items.get(2).getId());
        assertThat(result.get(4).orderCount()).isEqualTo(400);
    }
}
