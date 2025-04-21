package kr.hhplus.be.server.fixtures;

import kr.hhplus.be.server.domain.user.User;

import java.time.LocalDateTime;

public class UserFixtures {

    public static User 식별자로_유저_생성(long id) {
        return new User(id, "TESTER", "1234", LocalDateTime.now(), LocalDateTime.now());
    }

    public static User 정상_유저_생성() {
        return new User(null, "TESTER", "1234", LocalDateTime.now(), LocalDateTime.now());
    }
}
