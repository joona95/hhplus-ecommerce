package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.PointFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.point.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.point.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        pointHistoryJpaRepository.deleteAll();
        pointJpaRepository.deleteAll();
    }

    @Test
    void 포인트를_충전한다() {

        // given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
        Point point = pointJpaRepository.save(PointFixtures.유저와_금액으로_잔액_생성(user, 10000));

        PointCommand.PointChargeCommand command = new PointCommand.PointChargeCommand(10000);

        // when
        Point result = pointService.charge(user, command);

        // then
        assertThat(result.getAmount()).isEqualTo(20000);

        List<PointHistory> histories = pointHistoryJpaRepository.findAll();
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getType()).isEqualTo(TransactionType.CHARGE);
        assertThat(histories.get(0).getAmount()).isEqualTo(Amount.of(10000));
    }

    @Test
    void 포인트를_사용한다() {

        // given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
        Point point = pointJpaRepository.save(PointFixtures.유저와_금액으로_잔액_생성(user, 10000));

        PointCommand.PointUseCommand command = new PointCommand.PointUseCommand(1L, 5000);

        // when
        Point result = pointService.use(user, command);

        // then
        assertThat(result.getAmount()).isEqualTo(5000);

        List<PointHistory> histories = pointHistoryJpaRepository.findAll();
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getType()).isEqualTo(TransactionType.USE);
        assertThat(histories.get(0).getAmount()).isEqualTo(Amount.of(5000));
        assertThat(histories.get(0).getOrderId()).isEqualTo(1L);
    }
}
