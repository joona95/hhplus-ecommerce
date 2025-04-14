package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.PointCommand.PointChargeCommand;
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
    public Point findByUserId(long userId) {
        return pointRepository.findByUserId(userId);
    }

    @Transactional
    public Point charge(PointChargeCommand command) {

        Point point = findByUserId(command.userId());
        point.charge(command.amount());

        PointHistory pointHistory = PointHistory.ofCharge(point.getId(), command.amount());
        pointRepository.savePointHistory(pointHistory);

        return point;
    }

    @Transactional
    public Point use(PointUseCommand command) {

        Point point = findByUserId(command.userId());
        point.use(command.amount());

        PointHistory pointHistory = PointHistory.ofUse(point.getId(), command.orderId(), command.amount());
        pointRepository.savePointHistory(pointHistory);

        return point;
    }
}
