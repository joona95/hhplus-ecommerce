package kr.hhplus.be.server.domain.item;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("상품을 찾을 수 없습니다.");
        });
    }

    @Transactional(readOnly = true)
    public List<PopularItem> findPopularItems() {
        return Optional.ofNullable(itemRepository.findPopularItems())
                .orElse(List.of());
    }
}
