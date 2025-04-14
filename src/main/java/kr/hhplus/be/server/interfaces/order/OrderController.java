package kr.hhplus.be.server.interfaces.order;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.order.OrderFacadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kr.hhplus.be.server.interfaces.order.OrderResponse.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderApiSpec {

    private final OrderFacadeService orderFacadeService;

    public OrderController(OrderFacadeService orderFacadeService) {
        this.orderFacadeService = orderFacadeService;
    }

    @PostMapping
    @Override
    public ResponseEntity<OrderDetailResponse> order(@RequestBody @Valid OrderRequest.OrderCreateRequest request) {

        OrderDetailResponse response = OrderDetailResponse.from(
                orderFacadeService.placeOrder(request.toCommand())
        );

        return ResponseEntity.ok(response);
    }
}
