package kr.hhplus.be.server.domain.point;

import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository {

    Point findByUserId(long userId);

    PointHistory savePointHistory(PointHistory pointHistory);
}
