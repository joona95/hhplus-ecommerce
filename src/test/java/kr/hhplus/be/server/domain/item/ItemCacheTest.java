package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.support.RedisCleanup;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import static kr.hhplus.be.server.domain.item.ItemCommand.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
public class ItemCacheTest {

    @Autowired
    private ItemService itemService;

    @MockitoSpyBean
    private ItemJpaRepository itemJpaRepository;

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
    void 상품_상세_조회_시_여러_번_호출해도_DB_1회_조회() {

        //given
        Item item = itemJpaRepository.save(ItemFixtures.정상_상품_생성());

        //when
        itemService.findById(item.getId());
        itemService.findById(item.getId());
        itemService.findById(item.getId());

        //then
        verify(itemJpaRepository, times(1)).findById(item.getId());
    }

    @Test
    void 상품_정보_수정_시_캐시_무효화_후_DB_저장_값_조회() {

        //given
        Item item = itemJpaRepository.save(ItemFixtures.정상_상품_생성());

        ItemUpdateCommand command = ItemUpdateCommand.of("상품명수정", 50000, 500);

        itemService.findById(item.getId()); // DB 접근 후 캐시 저장

        //when
        itemService.updateItem(item.getId(), command); // 캐시 무효화
        Item result = itemService.findById(item.getId()); // DB 재접근

        //then
        verify(itemJpaRepository, times(3)).findById(item.getId());

        assertThat(result.getItemName()).isEqualTo("상품명수정");
        assertThat(result.getPrice()).isEqualTo(50000);
        assertThat(result.getStockCount()).isEqualTo(500);
    }
}
