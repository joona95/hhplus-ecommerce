package kr.hhplus.be.server.domain.item;

import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("상품을 찾을 수 없습니다.");
        });
    }
}
