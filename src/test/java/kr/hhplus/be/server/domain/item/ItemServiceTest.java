package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.fixtures.ItemFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static kr.hhplus.be.server.domain.item.ItemCommand.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ApplicationEventPublisher publisher;
    @InjectMocks
    private ItemService itemService;

    @Nested
    class 상품_상세_조회 {

        @Test
        void 상품이_존재하지_않는_경우_RuntimeException_발생() {

            //given
            when(itemRepository.findById(1L))
                    .thenReturn(Optional.empty());

            //when, then
            assertThatThrownBy(() -> itemService.findById(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("상품을 찾을 수 없습니다.");
        }

        @Test
        void 상품식별자로_상품_조회_레포지토리_1회_호출() {

            //given
            when(itemRepository.findById(1L))
                    .thenReturn(Optional.of(ItemFixtures.정상_상품_생성()));

            //when, then
            itemService.findById(1L);

            verify(itemRepository, times(1)).findById(1L);
        }
    }

    @Nested
    class 재고_차감 {

        @Test
        void 상품_조회_레포지토리_1회_호출() {

            //given
            StockDecreaseCommand command = StockDecreaseCommand.of(1L, 1);

            when(itemRepository.findByIdWithLock(1L))
                    .thenReturn(ItemFixtures.식별자로_상품_생성(1L));

            //when
            itemService.decreaseStock(command);

            //then
            verify(itemRepository, times(1)).findByIdWithLock(1L);
        }
    }
}