package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.PointCommand.PointChargeCommand;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.domain.point.PointCommand.*;

@Service
public class PointService {

    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Transactional(readOnly = true)
    public Point findByUser(User user) {
        return pointRepository.findByUser(user);
    }

    @Transactional
    public Point charge(User user, PointChargeCommand command) {

        Point point = findByUser(user);
        point.charge(command.amount());

        PointHistory pointHistory = PointHistory.ofCharge(point.getId(), command.amount());
        pointRepository.savePointHistory(pointHistory);

        return point;
    }

    @Transactional
    public Point use(User user, PointUseCommand command) {

        Point point = findByUser(user);
        point.use(command.amount());

        PointHistory pointHistory = PointHistory.ofUse(point.getId(), command.orderId(), command.amount());
        pointRepository.savePointHistory(pointHistory);

        return point;
    }
}
