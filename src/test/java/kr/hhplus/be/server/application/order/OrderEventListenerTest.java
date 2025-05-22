package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.client.DataPlatformClient;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCompleteEvent;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderItems;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderEventListenerTest {

    @Mock
    private DataPlatformClient dataPlatformClient;

    @InjectMocks
    private OrderEventListener orderEventListener;

    @Test
    void 주문_완료_이벤트_발행_시_데이터_플랫폼에_데이터_전송() {

        //given
        Order order = OrderFixtures.정상_주문_생성();
        OrderItems orderItems = new OrderItems(List.of(OrderFixtures.정상_주문상품_생성()));
        OrderInfo orderInfo = OrderInfo.of(order, orderItems);

        OrderCompleteEvent event = new OrderCompleteEvent(orderInfo);

        //when
        orderEventListener.handleOrderCompleteEvent(event);

        //then
        verify(dataPlatformClient, times(1)).sendOrderData(event.orderInfo());
    }
}
