package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.PopularItemStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopularItemStatisticsJpaRepository extends JpaRepository<PopularItemStatistics, Long> {
}
