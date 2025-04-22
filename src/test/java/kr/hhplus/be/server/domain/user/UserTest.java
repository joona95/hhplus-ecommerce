package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.fixtures.UserFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Nested
    class 유저_생성 {

        @ParameterizedTest
        @NullAndEmptySource
        void 로그인_아이디가_빈_값일_때_IllegalArgumentException_발생(String loginId) {

            //when, then
            assertThatThrownBy(() -> UserFixtures.로그인_아이디로_유저_생성(loginId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("로그인 아이디를 입력해주세요.");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 비밀번호가_빈_값일_때_IllegalArgumentException_발생(String password) {

            //when, then
            assertThatThrownBy(() -> UserFixtures.비밀번호로_유저_생성(password))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("비밀번호를 입력해주세요.");
        }
    }
}