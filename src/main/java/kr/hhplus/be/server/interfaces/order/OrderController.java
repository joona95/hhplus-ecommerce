package kr.hhplus.be.server.interfaces.order;

import jakarta.validation.Valid;
import kr.hhplus.be.server.interfaces.order.dto.OrderItemResponse;
import kr.hhplus.be.server.interfaces.order.dto.OrderRequest;
import kr.hhplus.be.server.interfaces.order.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderApiSpec {

    @PostMapping
    @Override
    public ResponseEntity<OrderResponse> order(@RequestBody @Valid OrderRequest request) {

        List<OrderItemResponse> orderItems = List.of(
                new OrderItemResponse(1L, "상품1", 10000, 1),
                new OrderItemResponse(2L, "상품2", 10000, 2),
                new OrderItemResponse(3L, "상품3", 10000, 1)
        );

        return ResponseEntity.ok(
                new OrderResponse(1L, "COMPLETE", 1L, orderItems, 30000, 10000, "2025-04-01")
        );
    }
}
