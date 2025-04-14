package kr.hhplus.be.server.domain.item;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository {

    Optional<Item> findById(long id);

    List<PopularItem> findPopularItems();

    List<Item> findByIdIn(List<Long> ids);

    List<PopularItem> savePopularItems(List<PopularItem> popularItems);
}
