package kr.hhplus.be.server.application.client;

import kr.hhplus.be.server.domain.order.OrderInfo;

public interface DataPlatformClient {

    void sendOrderData(OrderInfo orderInfo);
}
