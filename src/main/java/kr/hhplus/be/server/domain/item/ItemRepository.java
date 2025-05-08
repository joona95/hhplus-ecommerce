package kr.hhplus.be.server.domain.item;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository {

    Optional<Item> findById(long id);

    List<PopularItem> findPopularItems();

    Item findByIdWithLock(long id);

    List<PopularItemStatistics> savePopularItems(List<PopularItemStatistics> popularItemStatistics);

    Item saveItem(Item item);
}
