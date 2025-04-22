package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.order.Order;
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

        PointHistory pointHistory = PointHistory.ofCharge(point, command.amount());
        pointRepository.savePointHistory(pointHistory);

        return point;
    }

    @Transactional
    public Point use(User user, PointUseCommand command) {

        Order order = command.order();

        Point point = findByUser(user);
        point.use(order.getTotalAmount());

        PointHistory pointHistory = PointHistory.ofUse(point, order);
        pointRepository.savePointHistory(pointHistory);

        return point;
    }
}
