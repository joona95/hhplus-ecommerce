package kr.hhplus.be.server.interfaces.item;

import kr.hhplus.be.server.domain.item.PopularItemService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCompleteEvent;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderItems;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import kr.hhplus.be.server.interfaces.item.PopularItemEventListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PopularItemEventListenerTest {

    @Mock
    private PopularItemService popularItemService;

    @InjectMocks
    private PopularItemEventListener popularItemEventListener;

    @Test
    void 주문_완료_이벤트_발행_시_인기_상품_통계_데이터_저장() {

        //given
        Order order = OrderFixtures.정상_주문_생성();
        OrderItem orderItem1 = OrderFixtures.상품식별자로_주문상품_생성(1L);
        OrderItem orderItem2 = OrderFixtures.상품식별자로_주문상품_생성(2L);
        OrderItems orderItems = new OrderItems(List.of(orderItem1, orderItem2));
        OrderInfo orderInfo = OrderInfo.of(order, orderItems);

        OrderCompleteEvent event = new OrderCompleteEvent(orderInfo);

        //when
        popularItemEventListener.handleOrderCompleteEvent(event);

        //then
        verify(popularItemService, times(1)).savePopularItemScore(orderItem1.getItemId(), orderItem1.getCount());
        verify(popularItemService, times(1)).savePopularItemScore(orderItem2.getItemId(), orderItem2.getCount());
    }
}