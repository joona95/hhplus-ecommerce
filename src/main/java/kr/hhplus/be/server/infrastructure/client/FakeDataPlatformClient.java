package kr.hhplus.be.server.infrastructure.client;

import kr.hhplus.be.server.application.client.DataPlatformClient;
import kr.hhplus.be.server.domain.order.OrderInfo;
import org.springframework.stereotype.Component;

@Component
public class FakeDataPlatformClient implements DataPlatformClient {

    @Override
    public void sendOrderData(OrderInfo orderInfo) {
        System.out.println("주문 데이터 전송:" + orderInfo);
    }
}
