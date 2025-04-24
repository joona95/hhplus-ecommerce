package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.PopularItem;
import kr.hhplus.be.server.fixtures.ItemFixtures;
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
    private PopularItemJpaRepository popularItemJpaRepository;

    @BeforeEach
    void setUp() {
        popularItemJpaRepository.deleteAll();
        itemJpaRepository.deleteAll();
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
    void 여러_상품_ID로_조회() {

        // given
        List<Item> savedItems = itemJpaRepository.saveAll(List.of(ItemFixtures.정상_상품_생성(), ItemFixtures.정상_상품_생성()));
        List<Long> ids = savedItems.stream().map(Item::getId).toList();

        // when
        List<Item> result = itemRepository.findByIdInWithLock(ids);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void 인기_상품_저장() {

        // given
        List<PopularItem> items = List.of(ItemFixtures.상품식별자로_인기_상품_생성(1L));

        // when
        List<PopularItem> result = itemRepository.savePopularItems(items);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItemId()).isEqualTo(1L);
    }

    @Test
    void 최근_3일간_1일전부터_3일전까지_주문된_인기_상품_조회() {

        // given
        PopularItem today = ItemFixtures.주문날짜로_인기_상품_생성(LocalDate.now());
        PopularItem yesterday = ItemFixtures.주문날짜로_인기_상품_생성(LocalDate.now().minusDays(1));
        PopularItem recent = ItemFixtures.주문날짜로_인기_상품_생성(LocalDate.now().minusDays(3));
        PopularItem old = ItemFixtures.주문날짜로_인기_상품_생성(LocalDate.now().minusDays(4));
        popularItemJpaRepository.saveAll(List.of(today, yesterday, recent, old));

        // when
        List<PopularItem> result = itemRepository.findPopularItems();

        // then
        assertThat(result).contains(yesterday);
        assertThat(result).contains(recent);
        assertThat(result).doesNotContain(today);
        assertThat(result).doesNotContain(old);
    }
}