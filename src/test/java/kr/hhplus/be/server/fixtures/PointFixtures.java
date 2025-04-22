package kr.hhplus.be.server.fixtures;

import kr.hhplus.be.server.domain.point.Amount;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.TransactionType;
import kr.hhplus.be.server.domain.user.User;

import java.time.LocalDateTime;

public class PointFixtures {

    public static Point 유저로_잔액_생성(User user) {
        return new Point(null, user, Amount.of(1000), LocalDateTime.now());
    }

    public static Point 금액으로_잔액_생성(Amount amount) {
        return new Point(null, UserFixtures.식별자로_유저_생성(1L), amount, LocalDateTime.now());
    }

    public static Point 금액으로_잔액_생성(int amount) {
        return new Point(null, UserFixtures.식별자로_유저_생성(1L), Amount.of(amount), LocalDateTime.now());
    }

    public static Point 유저와_금액으로_잔액_생성(User user, int amount) {
        return new Point(null, user, Amount.of(amount), LocalDateTime.now());
    }

    public static Point 식별자와_금액으로_잔액_생성(long id, int amount) {
        return new Point(id, UserFixtures.식별자로_유저_생성(1L), Amount.of(amount), LocalDateTime.now());
    }

    public static Point 식별자와_유저와_금액으로_잔액_생성(long id, User user, int amount) {
        return new Point(id, user, Amount.of(amount), LocalDateTime.now());
    }

    public static PointHistory 포인트식별자로_포인트_내역_생성(long pointId) {
        return new PointHistory(null, pointId, 1L, Amount.of(1000), TransactionType.CHARGE, LocalDateTime.now());
    }

    public static PointHistory 금액으로_포인트_내역_생성(Amount amount) {
        return new PointHistory(null, 1L, 1L, amount, TransactionType.CHARGE, LocalDateTime.now());
    }

    public static PointHistory 거래_타입으로_포인트_내역_생성(TransactionType type) {
        return new PointHistory(null, 1L, 1L, Amount.of(1000), type, LocalDateTime.now());
    }
}
