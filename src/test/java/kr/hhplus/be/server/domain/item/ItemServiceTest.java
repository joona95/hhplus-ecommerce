package kr.hhplus.be.server.domain.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
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

            verify(itemRepository, times(1)).findById(1L);
        }
    }
}