package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.support.RedisCleanup;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import kr.hhplus.be.server.interfaces.coupon.CouponEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Testcontainers
@EmbeddedKafka
public class CouponFacadeServiceTest {

    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponFacadeService couponFacadeService;
    @MockitoSpyBean
    private CouponEventListener couponEventListener;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;
    @Autowired
    private RedisCleanup redisCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
        redisCleanup.flushAll();
    }

    @Test
    void 쿠폰_발급_요청_시_쿠폰_발급_처리() {

        //given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
        Coupon coupon = couponJpaRepository.save(CouponFixtures.정상_쿠폰_생성());
        CouponCommand.CouponIssueCommand command = CouponCommand.CouponIssueCommand.of(coupon.getId());

        //when
        couponFacadeService.requestCouponIssue(user, command);

        //then
        Awaitility.await()
                .atMost(Duration.ofSeconds(60))
                .untilAsserted(() -> {
                    verify(couponEventListener, times(1)).handleCouponIssueRequestEvent(any());
                });
    }
}
