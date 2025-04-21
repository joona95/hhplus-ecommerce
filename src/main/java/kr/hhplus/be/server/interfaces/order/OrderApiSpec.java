package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.common.auth.AuthUser;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "주문 API")
public interface OrderApiSpec {

    @Operation(summary = "주문 결제", description = "상품들에 대한 주문 및 결제를 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                            {
                            "orderId" : 1,
                            "orderStatus": "COMPLETE",
                            "couponId": 1,
                            "orderItems": [{
                            "orderItemId": 1,
                            "orderItemName": "주문상품명",
                            "sellPrice": 10000,
                            "count": 1
                            }],
                            "totalAmount": 10000,
                            "discountAmout": 5000,
                            "createdAt": "2025-04-01 18:00:00"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "유효하지 않은 요청값로 인한 실패", value = """
                            {
                            "code": "400",
                            "message": "유효하지 않은 요청값입니다."
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "유효하지 않은 사용자로 인한 실패", value = """
                            {
                            "code": "401",
                            "message": "유효하지 않은 사용자입니다."
                            }
                            """)
            })),
            @ApiResponse(responseCode = "403", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "권한이 없는 사용자로 인한 실패", value = """
                            {
                            "code": "403",
                            "message": "권한이 없는 사용자입니다."
                            }
                            """)
            })),
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "서버 오류로 실패", value = """
                            {
                            "code": "500",
                            "message": "서버 오류가 발생하였습니다."
                            }
                            """),

                    @ExampleObject(name = "재고 부족으로 실패", value = """
                            {
                            "code":"500",
                            "message":"상품 재고가 부족합니다."
                            }
                            """),
                    @ExampleObject(name = "잔액 부족으로 실패", value = """
                            {
                            "code":"500",
                            "message":"잔액이 부족합니다."
                            }
                            """)
            }))
    })
    ResponseEntity<OrderResponse.OrderDetailResponse> order(@AuthUser User user, @RequestBody @Valid OrderRequest.OrderCreateRequest request);
}
