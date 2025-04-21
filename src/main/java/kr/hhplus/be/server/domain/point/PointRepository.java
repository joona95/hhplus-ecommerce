package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository {

    Point findByUser(User user);

    PointHistory savePointHistory(PointHistory pointHistory);
}
