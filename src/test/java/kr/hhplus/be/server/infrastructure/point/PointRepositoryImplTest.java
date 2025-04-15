package kr.hhplus.be.server.infrastructure.point;

import kr.hhplus.be.server.domain.point.Amount;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.fixtures.PointFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class PointRepositoryImplTest {

    @Autowired
    private PointRepositoryImpl pointRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @BeforeEach
    void setUp() {
        pointHistoryJpaRepository.deleteAll();
        pointJpaRepository.deleteAll();
    }

    @Test
    void 유저_식별자로_잔액_조회() {

        // given
        Point point = PointFixtures.유저식별자로_잔액_생성(1L);
        pointJpaRepository.save(point);

        // when
        Point result = pointRepository.findByUserId(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(point);
    }

    @Test
    void 포인트_내역_저장() {

        // given
        PointHistory pointHistory = PointFixtures.금액으로_포인트_내역_생성(Amount.of(2000));

        // when
        PointHistory result = pointRepository.savePointHistory(pointHistory);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result).isEqualTo(pointHistory);
        assertThat(pointHistoryJpaRepository.findAll()).hasSize(1);
    }
}