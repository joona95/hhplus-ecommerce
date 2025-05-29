package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.interfaces.item.PopularItemEventListener;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.ItemService;
import kr.hhplus.be.server.domain.item.Stock;
import kr.hhplus.be.server.domain.order.OrderEventPublisher;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.fixtures.PointFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.item.ItemJpaRepository;
import kr.hhplus.be.server.infrastructure.point.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.support.RedisCleanup;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import kr.hhplus.be.server.interfaces.order.OrderEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.List;

import static kr.hhplus.be.server.application.order.OrderFacadeCommand.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Testcontainers
public class OrderFacadeServiceTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private PointService pointService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private OrderEventPublisher orderEventPublisher;
    @Autowired
    private OrderFacadeService orderFacadeService;
    @MockitoSpyBean
    private OrderEventListener orderEventListener;
    @MockitoSpyBean
    private PopularItemEventListener popularItemEventListener;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ItemJpaRepository itemJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;

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
    void 주문_완료_시_데이터플랫폼_데이터_전송_및_인기상품_통계_데이터_저장() {

        //given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
        Item item = itemJpaRepository.save(ItemFixtures.재고로_상품_생성(Stock.of(10)));
        Point point = pointJpaRepository.save(PointFixtures.유저와_금액으로_잔액_생성(user, 100000));

        OrderCreateFacadeCommand command = OrderCreateFacadeCommand.of(null, List.of(
                new OrderItemCreateFacadeCommand(item.getId(), 10)
        ));

        //when
        orderFacadeService.placeOrder(user, command);

        //then
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    verify(orderEventListener, times(1)).handleOrderCompleteEvent(any());
                    verify(popularItemEventListener, times(1)).handleOrderCompleteEvent(any());
                });

    }
}
