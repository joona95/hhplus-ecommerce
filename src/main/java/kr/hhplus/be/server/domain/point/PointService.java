package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.PointCommand.PointChargeCommand;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.domain.point.PointCommand.*;

@Service
public class PointService {

    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    public Point findByUserId(long userId) {
        return pointRepository.findByUserId(userId);
    }

    public Point charge(PointChargeCommand command) {

        Point point = findByUserId(command.userId());
        point.charge(command.amount());

        PointHistory pointHistory = PointHistory.ofCharge(point.getId(), command.amount());
        pointRepository.savePointHistory(pointHistory);

        return point;
    }

    public Point use(PointUseCommand command) {

        Point point = findByUserId(command.userId());
        point.use(command.amount());

        PointHistory pointHistory = PointHistory.ofUse(point.getId(), command.amount());
        pointRepository.savePointHistory(pointHistory);

        return point;
    }
}
