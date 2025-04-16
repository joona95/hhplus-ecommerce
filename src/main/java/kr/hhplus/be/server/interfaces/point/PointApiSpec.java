package kr.hhplus.be.server.interfaces.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "잔액 API")
public interface PointApiSpec {

    @Operation(summary = "유저 잔액 조회", description = "유저의 잔액을 조회합니다. 로그인한 사용자만 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                            {
                            "userId" : 1,
                            "amount": 1000
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
                            """)
            }))
    })
    ResponseEntity<PointResponse.UserPointResponse> getUserPoint(@RequestParam @Positive long userId);

    @Operation(summary = "유저 잔액 충전", description = "유저의 잔액을 충전합니다. 로그인한 사용자만 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "잔액 충전 성공", value = """
                            {
                            "userId": 1,
                            "amount": 1000
                            }
                            """)
            })),
            @ApiResponse(responseCode = "400", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "유효하지 않은 요청값로 인한 실패", value = """
                            {
                            "code": "400",
                            "message": "유효하지 않은 요청값입니다."
                            }
                            """),
                    @ExampleObject(name = "충전 금액 음수로 인한 실패", value = """
                            {
                            "code": "400",
                            "message": "충전 금액은 양수여야 합니다."
                            }
                            """),
                    @ExampleObject(name = "최대 한도 초과로 인한 실패", value = """
                            {
                            "code": "400",
                            "message": "충전 금액은 최대 한도를 넘을 수 없습니다."
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
    ResponseEntity<PointResponse.UserPointResponse> charge(@RequestBody @Valid PointRequest.PointChargeRequest request);
}
