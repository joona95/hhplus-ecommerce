package kr.hhplus.be.server.interfaces.item;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.item.PopularItemJpaRepository;
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
    private PopularItemJpaRepository popularItemJpaRepository;

    @BeforeEach
    void setUp() {
        popularItemJpaRepository.deleteAll();
        itemJpaRepository.deleteAll();
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
        popularItemJpaRepository.saveAll(List.of(
                ItemFixtures.주문날짜로_인기_상품_생성(LocalDate.now().minusDays(1)),
                ItemFixtures.주문날짜로_인기_상품_생성(LocalDate.now().minusDays(3))
        ));

        // when
        ResponseEntity<PopularItemDetailResponse[]> response = restTemplate.getForEntity(
                "/api/v1/items/popular",
                PopularItemDetailResponse[].class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).hasSize(2);
    }
}