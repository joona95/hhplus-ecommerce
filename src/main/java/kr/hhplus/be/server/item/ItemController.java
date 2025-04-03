package kr.hhplus.be.server.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.item.dto.ItemResponse;
import kr.hhplus.be.server.item.dto.PopularItemStatisticsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController implements ItemApi {

    @GetMapping("/{itemId}")
    @Override
    public ResponseEntity<ItemResponse> getItem(@PathVariable @Positive long itemId) {
        return null;
    }

    @GetMapping("/popular")
    @Override
    public ResponseEntity<List<ItemResponse>> getPopularItems() {
        return null;
    }

    @PostMapping("/popular")
    @Override
    public ResponseEntity<Void> sendPopularItemStatistics(@RequestBody @Valid PopularItemStatisticsRequest request) {
        return null;
    }
}
