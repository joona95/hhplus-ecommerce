package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static kr.hhplus.be.server.interfaces.coupon.CouponRequest.*;
import static kr.hhplus.be.server.interfaces.coupon.CouponResponse.*;

@Tag(name = "쿠폰 API")
public interface CouponApiSpec {

    @Operation(summary = "보유 쿠폰 목록 조회", description = "유저에게 발급된 쿠폰 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                            [{
                            "couponId" : 1,
                            "couponName": "쿠폰명1",
                            "expiredAt": "2025-04-01 18:00:00",
                            "isUsed": false
                            },
                            {
                            "couponId" : 2,
                            "couponName": "쿠폰명2",
                            "expiredAt": "2025-04-01 18:00:00",
                            "isUsed": true
                            }]
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
                            """)
            }))
    })
    ResponseEntity<List<UserCouponResponse>> getUserCoupons(long userId);

    @Operation(summary = "선착순 쿠폰 발급", description = "발급 제한 수량만큼 선착순으로 유저에게 쿠폰을 발급한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "발급 성공", value = """
                            {
                            "userId" : 1,
                            "couponId": 1,
                            "couponName": "쿠폰명",
                            "expiredAt": "2025-04-01 18:00:00"
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
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "쿠폰이 존재하지 않는 경우 실패", value = """
                            {
                            "code": "404",
                            "message": "쿠폰식별자에 해당하는 쿠폰이 존재하지 않습니다."
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
                    @ExampleObject(name = "쿠폰 유효 기간 만료로 인한 실패", value = """
                            {
                            "code": "500",
                            "message": "쿠폰 발급 유효 기간이 만료하였습니다."
                            }
                            """),
                    @ExampleObject(name = "쿠폰 잔여 수량 없어서 실패", value = """
                            {
                            "code": "500",
                            "message": "선착순 쿠폰의 잔여 수량이 남지 않았습니다."
                            }
                            """)
            }))
    })
    ResponseEntity<UserCouponResponse> issueCoupon(CouponIssueRequest request);


}
