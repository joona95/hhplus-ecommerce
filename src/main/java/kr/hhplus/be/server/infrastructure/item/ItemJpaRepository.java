package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
}
