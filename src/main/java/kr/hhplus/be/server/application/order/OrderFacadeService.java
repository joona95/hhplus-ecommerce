package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.client.DataPlatformClient;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.ItemService;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.application.order.OrderFacadeCommand.*;
import static kr.hhplus.be.server.application.order.OrderResult.*;

@Service
public class OrderFacadeService {

    private final ItemService itemService;
    private final PointService pointService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final DataPlatformClient dataPlatformClient;

    public OrderFacadeService(ItemService itemService, PointService pointService, OrderService orderService, CouponService couponService, DataPlatformClient dataPlatformClient) {
        this.itemService = itemService;
        this.pointService = pointService;
        this.orderService = orderService;
        this.couponService = couponService;
        this.dataPlatformClient = dataPlatformClient;
    }

    @Transactional
    public OrderCreateResult placeOrder(User user, OrderCreateFacadeCommand command) {

        List<Item> items = command.orderItemCommands().stream()
                .map(orderItemCommand -> itemService.decreaseStock(orderItemCommand.toStockDecreaseCommand()))
                .toList();

        OrderInfo orderInfo = orderService.createOrder(command.toOrderCreateCommand(user, items));

        if (command.couponId() != null) {
            CouponIssue couponIssue = couponService.findIssuedCoupon(command.toIssuedCouponCriteria(user));
            orderInfo.applyCoupon(couponIssue);
        }

        pointService.use(user, command.toPointUseCommand(orderInfo.order()));

        dataPlatformClient.sendOrderDate(orderInfo);

        return OrderCreateResult.from(orderInfo);
    }
}
