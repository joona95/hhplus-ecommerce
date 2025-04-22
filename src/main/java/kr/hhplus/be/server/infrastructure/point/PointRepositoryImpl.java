package kr.hhplus.be.server.infrastructure.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Repository;

@Repository
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;
    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    public PointRepositoryImpl(PointJpaRepository pointJpaRepository, PointHistoryJpaRepository pointHistoryJpaRepository) {
        this.pointJpaRepository = pointJpaRepository;
        this.pointHistoryJpaRepository = pointHistoryJpaRepository;
    }

    @Override
    public Point findByUser(User user) {
        return pointJpaRepository.findByUser(user).orElse(null);
    }

    @Override
    public PointHistory savePointHistory(PointHistory pointHistory) {
        return pointHistoryJpaRepository.save(pointHistory);
    }
}
