package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.fixtures.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Nested
    class 유저식별자로_유저_조회 {

        @Test
        void 유저식별자로_유저_조회_레포지토리_1회_호출() {

            //given
            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(UserFixtures.정상_유저_생성()));

            //when
            userService.findById(1L);

            //then
            verify(userRepository, times(1)).findById(1L);

        }

        @Test
        void 유저가_존재하지_않는_경우_RuntimeException_발생() {

            //given
            when(userRepository.findById(1L))
                    .thenReturn(Optional.empty());

            //when, then
            assertThatThrownBy(() -> userService.findById(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("해당하는 유저를 찾을 수 없습니다.");
        }
    }
}