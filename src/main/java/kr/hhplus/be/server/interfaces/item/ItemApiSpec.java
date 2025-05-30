package kr.hhplus.be.server.interfaces.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "상품 API")
public interface ItemApiSpec {

    @Operation(summary = "상품 상세 조회", description = "상품에 대한 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                            {
                            "itemId" : 1,
                            "itemName": "상품명",
                            "price": 1000,
                            "stock": 100
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
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "상품식별자에 해당하는 상품이 없어서 실패", value = """
                            {
                            "code": "404",
                            "message": "해당 상품 정보를 찾을 수 없습니다."
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
    ResponseEntity<ItemResponse.ItemDetailResponse> getItem(@PathVariable @Positive long itemId);

    @Operation(summary = "인기 상품 목록 조회", description = "데이터 플랫폼에서 최근 3일간 판매량이 가장 많았던 인기 상품 5개에 대한 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "조회 성공", value = """
                            [{
                            "itemId" : 1,
                            "itemName": "상품명",
                            "price": 1000,
                            "stock": 100
                            },
                            {
                            "itemId" : 2,
                            "itemName": "상품명2",
                            "price": 1000,
                            "stock": 100
                            },{
                            "itemId" : 3,
                            "itemName": "상품명3",
                            "price": 1000,
                            "stock": 100
                            },
                            {
                            "itemId" : 4,
                            "itemName": "상품명4",
                            "price": 1000,
                            "stock": 100
                            },
                            {
                            "itemId" : 5,
                            "itemName": "상품명5",
                            "price": 1000,
                            "stock": 100
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
            @ApiResponse(responseCode = "500", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "서버 오류로 실패", value = """
                            {
                            "code": "500",
                            "message": "서버 오류가 발생하였습니다."
                            }
                            """)
            }))
    })
    ResponseEntity<List<ItemResponse.PopularItemDetailResponse>> getPopularItems();

    @Operation(
            summary = "상품 수정",
            description = "상품 정보를 수정합니다. 전달된 값만 변경 (Patch‑like) 하며, 응답은 변경된 상세 정보를 돌려줍니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(name = "수정 성공", value = """
                    {
                      "itemId": 1,
                      "itemName": "수정된 상품명",
                      "price": 1200,
                      "stock": 95
                    }
                    """))),
            @ApiResponse(responseCode = "400", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(name = "유효하지 않은 요청값으로 실패", value = """
                    {
                      "code": "400",
                      "message": "유효하지 않은 요청값입니다."
                    }
                    """))),
            @ApiResponse(responseCode = "404", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(name = "상품을 찾을 수 없어 실패", value = """
                    {
                      "code": "404",
                      "message": "해당 상품 정보를 찾을 수 없습니다."
                    }
                    """))),
            @ApiResponse(responseCode = "500", content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(name = "서버 오류로 실패", value = """
                    {
                      "code": "500",
                      "message": "서버 오류가 발생하였습니다."
                    }
                    """)))
    })
    ResponseEntity<ItemResponse.ItemDetailResponse> updateItem(
            @PathVariable @Positive long itemId,
            @Valid @RequestBody ItemRequest.ItemUpdateRequest request
    );
}
