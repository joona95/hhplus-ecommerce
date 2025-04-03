package kr.hhplus.be.server.order;

import jakarta.validation.Valid;
import kr.hhplus.be.server.order.dto.OrderRequest;
import kr.hhplus.be.server.order.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderApi {

    @PostMapping
    @Override
    public ResponseEntity<OrderResponse> order(@RequestBody @Valid OrderRequest request) {
        return null;
    }
}
