package kr.hhplus.be.server.interfaces.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.item.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static kr.hhplus.be.server.interfaces.item.ItemRequest.*;
import static kr.hhplus.be.server.interfaces.item.ItemResponse.*;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController implements ItemApiSpec {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    @Override
    public ResponseEntity<ItemDetailResponse> getItem(@PathVariable @Positive long itemId) {

        ItemDetailResponse response = ItemDetailResponse.from(itemService.findById(itemId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    @Override
    public ResponseEntity<List<PopularItemDetailResponse>> getPopularItems() {

        List<PopularItemDetailResponse> response = itemService.findPopularItems().stream()
                .map(PopularItemDetailResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{itemId}")
    @Override
    public ResponseEntity<ItemDetailResponse> updateItem(@PathVariable @Positive long itemId,
                                                         @Valid @RequestBody ItemUpdateRequest request) {

        ItemDetailResponse response = ItemDetailResponse.from(itemService.updateItem(itemId, request.toCommand()));

        return ResponseEntity.ok(response);
    }
}
