package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.fixtures.ItemFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PopularItemServiceTest {

    @Mock
    private PopularItemRepository popularItemRepository;

    @InjectMocks
    private PopularItemService popularItemService;

    @Nested
    class 인기_상품_목록_조회 {

        @Test
        void 인기_상품_목록_조회_값이_없을_경우_빈_리스트_반환() {

            //given
            when(popularItemRepository.findPopularItems())
                    .thenReturn(null);

            //when
            List<PopularItem> result = popularItemService.findPopularItems();

            //then
            assertThat(result).isEqualTo(List.of());
        }

        @Test
        void 인기_상품_목록_조회_레포지토리_1회_호출() {

            //given
            when(popularItemRepository.findPopularItems())
                    .thenReturn(List.of());

            //when
            popularItemService.findPopularItems();

            //then
            verify(popularItemRepository, times(1)).findPopularItems();
        }
    }
}
