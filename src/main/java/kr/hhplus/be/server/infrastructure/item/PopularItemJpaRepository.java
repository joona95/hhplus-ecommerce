package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.PopularItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PopularItemJpaRepository extends JpaRepository<PopularItem, Long> {

    List<PopularItem> findByOrderDateBetween(LocalDate orderDateAfter, LocalDate orderDateBefore);
}
