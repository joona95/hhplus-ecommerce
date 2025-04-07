package kr.hhplus.be.server.interfaces.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.item.ItemService;
import kr.hhplus.be.server.interfaces.item.dto.ItemRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static kr.hhplus.be.server.interfaces.item.dto.ItemResponse.*;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController implements ItemApiSpec {

    @GetMapping("/{itemId}")
    @Override
    public ResponseEntity<ItemDetailResponse> getItem(@PathVariable @Positive long itemId) {
        return ResponseEntity.ok(new ItemDetailResponse(1L, "상품1", 10000, 100));
    }

    @GetMapping("/popular")
    @Override
    public ResponseEntity<List<ItemDetailResponse>> getPopularItems() {
        return ResponseEntity.ok(List.of(
                new ItemDetailResponse(1L, "상품1", 10000, 100),
                new ItemDetailResponse(2L, "상품2", 10000, 100),
                new ItemDetailResponse(3L, "상품3", 10000, 100)
        ));
    }

    @PostMapping("/popular")
    @Override
    public ResponseEntity<Void> sendPopularItemStatistics(@RequestBody @Valid ItemRequest.PopularItemStatisticsRequest request) {
        return ResponseEntity.ok().build();
    }
}
