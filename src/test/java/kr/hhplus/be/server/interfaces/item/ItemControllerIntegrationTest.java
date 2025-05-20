package kr.hhplus.be.server.interfaces.item;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.item.PopularItemStatisticsJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static kr.hhplus.be.server.interfaces.item.ItemResponse.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ItemControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private PopularItemStatisticsJpaRepository popularItemStatisticsJpaRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
    }

    @Test
    void 상품을_식별자로_조회() {

        // given
        Item item = itemJpaRepository.save(ItemFixtures.정상_상품_생성());

        // when
        ResponseEntity<ItemDetailResponse> response = restTemplate.getForEntity(
                "/api/v1/items/" + item.getId(),
                ItemDetailResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(item.getId());
    }

    @ParameterizedTest
    @ValueSource(longs = {-100L, -10L, -3L, -2L, -1L, 0L})
    void 양수가_아닌_상품식별자로_상품_조회(long itemId) {

        // when
        ResponseEntity<ItemDetailResponse> response = restTemplate.getForEntity(
                "/api/v1/items/" + itemId,
                ItemDetailResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 인기_상품_목록을_조회() {

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

        // when
        ResponseEntity<PopularItemDetailResponse[]> response = restTemplate.getForEntity(
                "/api/v1/items/popular",
                PopularItemDetailResponse[].class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).hasSize(5);
        assertThat(response.getBody()[0].itemId()).isEqualTo(items.get(0).getId());
        assertThat(response.getBody()[1].itemId()).isEqualTo(items.get(1).getId());
        assertThat(response.getBody()[2].itemId()).isEqualTo(items.get(2).getId());
        assertThat(response.getBody()[3].itemId()).isEqualTo(items.get(3).getId());
        assertThat(response.getBody()[4].itemId()).isEqualTo(items.get(4).getId());
    }
}