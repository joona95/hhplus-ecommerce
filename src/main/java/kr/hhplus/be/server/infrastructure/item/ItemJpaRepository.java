package kr.hhplus.be.server.infrastructure.item;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.id = :id")
    Item findByIdWithLock(@Param("id") long id);
}
