package kr.hhplus.be.server.infrastructure.item;

import jakarta.transaction.Transactional;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ItemRepositoryImplTest {

    @Autowired
    private ItemRepositoryImpl itemRepository;

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
    }

    @Test
    void 상품_ID_조회() {

        // given
        Item item = itemJpaRepository.save(ItemFixtures.정상_상품_생성());

        // when
        Optional<Item> result = itemRepository.findById(item.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(item);
    }

    @Test
    @Transactional
    void 상품_ID로_조회() {

        // given
        Item savedItem = itemJpaRepository.save(ItemFixtures.정상_상품_생성());

        // when
        Item result = itemRepository.findByIdWithLock(savedItem.getId());

        // then
        assertThat(result).isEqualTo(savedItem);
    }
}