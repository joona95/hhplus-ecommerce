package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

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

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
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
}
