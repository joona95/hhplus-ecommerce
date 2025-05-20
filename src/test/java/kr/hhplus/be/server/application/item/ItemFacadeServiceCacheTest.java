package kr.hhplus.be.server.application.item;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.item.PopularItemStatisticsJpaRepository;
import kr.hhplus.be.server.infrastructure.item.PopularItemQuerydslRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.support.RedisCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
public class ItemFacadeServiceCacheTest {

    @Autowired
    private ItemFacadeService itemFacadeService;

    @Autowired
    private PopularItemStatisticsJpaRepository popularItemStatisticsJpaRepository;

    @MockitoSpyBean
    private ItemJpaRepository itemJpaRepository;

    @MockitoSpyBean
    private PopularItemQuerydslRepository popularItemQuerydslRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private RedisCleanup redisCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
        redisCleanup.flushAll();
    }

    @Test
    void 인기_상품_목록_조회_시_여러_번_호출해도_인기_상품_DB_1회_조회와_상품_DB_5회_조회() {

        // given
        List<Item> items = itemJpaRepository.saveAll(List.of(
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성(),
                ItemFixtures.정상_상품_생성()
        ));

        for (int i = 0; i < items.size(); i++) {
            popularItemStatisticsJpaRepository.saveAll(List.of(
                    ItemFixtures.상품식별자와_주문날짜와_주문수량으로_인기_상품_통계_생성(items.get(i).getId(), LocalDate.now().minusDays(1), 100),
                    ItemFixtures.상품식별자와_주문날짜와_주문수량으로_인기_상품_통계_생성(items.get(i).getId(), LocalDate.now().minusDays(1), 100)
            ));
        }

        //when
        itemFacadeService.findPopularItemDetails();
        itemFacadeService.findPopularItemDetails();
        itemFacadeService.findPopularItemDetails();

        //then
        verify(popularItemQuerydslRepository, times(1)).findPopularItems();
        verify(itemJpaRepository, times(5)).findById(any());
    }
}
