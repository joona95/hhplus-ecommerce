package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.PointFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kr.hhplus.be.server.domain.point.PointCommand.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    PointRepository pointRepository;
    @InjectMocks
    PointService pointService;

    @Nested
    class 유저_잔액_조회 {

        @Test
        void 유저_잔액_조회_레포지토리를_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(pointRepository.findByUser(user))
                    .thenReturn(PointFixtures.금액으로_잔액_생성(1000));

            //when
            pointService.findByUser(user);

            //then
            verify(pointRepository, times(1)).findByUser(user);
        }
    }

    @Nested
    class 유저_잔액_충전 {

        @Test
        void 유저_잔액_조회_레포지토리를_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(pointRepository.findByUser(user))
                    .thenReturn(PointFixtures.식별자와_금액으로_잔액_생성(1L, 1000));

            PointChargeCommand command = new PointChargeCommand(1000);

            //when
            pointService.charge(user, command);

            //then
            verify(pointRepository, times(1)).findByUser(user);
        }

        @Test
        void 포인트_내역_저장_레포지토리를_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Point point = PointFixtures.유저와_금액으로_잔액_생성(user, 1000);
            when(pointRepository.findByUser(user))
                    .thenReturn(point);

            PointChargeCommand command = new PointChargeCommand(1000);

            //when
            pointService.charge(user, command);

            //then
            verify(pointRepository, times(1)).savePointHistory(PointHistory.ofCharge(point, 1000));
        }

        @Test
        void 포인트_충전에_실패하면_포인트_내역_저장_레포지토리를_0회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(pointRepository.findByUser(user))
                    .thenReturn(PointFixtures.식별자와_금액으로_잔액_생성(1L, 1000));

            PointChargeCommand command = new PointChargeCommand(-2000);

            //when, then
            assertThatThrownBy(() -> pointService.charge(user, command))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(pointRepository, times(0)).savePointHistory(any());
        }

        @Test
        void 포인트_잔액에_충전_금액_더한_포인트_반환() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(pointRepository.findByUser(user))
                    .thenReturn(PointFixtures.식별자와_금액으로_잔액_생성(1L, 1000));

            PointChargeCommand command = new PointChargeCommand(1000);

            //when
            Point result = pointService.charge(user, command);

            //then
            assertThat(result.getAmount()).isEqualTo(2000);
        }
    }

    @Nested
    class 유저_잔액_차감 {

        @Test
        void 유저_잔액_조회_레포지토리를_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(pointRepository.findByUser(user))
                    .thenReturn(PointFixtures.식별자와_금액으로_잔액_생성(1L, 1000));

            PointUseCommand command = new PointUseCommand(1L, 1000);

            //when
            pointService.use(user, command);

            //then
            verify(pointRepository, times(1)).findByUser(user);
        }

        @Test
        void 포인트_내역_저장_레포지토리를_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(pointRepository.findByUser(user))
                    .thenReturn(PointFixtures.식별자와_금액으로_잔액_생성(1L, 1000));

            PointUseCommand command = new PointUseCommand(1L, 1000);

            //when
            pointService.use(user, command);

            //then
            verify(pointRepository, times(1)).savePointHistory(PointHistory.ofUse(1L, 1L, 1000));
        }

        @Test
        void 포인트_사용에_실패하면_포인트_내역_저장_레포지토리를_0회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(pointRepository.findByUser(user))
                    .thenReturn(PointFixtures.식별자와_금액으로_잔액_생성(1L, 1000));

            PointUseCommand command = new PointUseCommand(1L, 2000);

            //when, then
            assertThatThrownBy(() -> pointService.use(user, command))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(pointRepository, times(0)).savePointHistory(any());
        }

        @Test
        void 포인트_잔액에_사용_금액_뺀_포인트_반환() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(pointRepository.findByUser(user))
                    .thenReturn(PointFixtures.식별자와_금액으로_잔액_생성(1L, 1000));

            PointUseCommand command = new PointUseCommand(1L, 1000);

            //when
            Point result = pointService.use(user, command);

            //then
            assertThat(result.getAmount()).isEqualTo(0);
        }
    }
}