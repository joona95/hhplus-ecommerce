package kr.hhplus.be.server.application.order;

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

    public OrderFacadeService(ItemService itemService, PointService pointService, OrderService orderService, CouponService couponService) {
        this.itemService = itemService;
        this.pointService = pointService;
        this.orderService = orderService;
        this.couponService = couponService;
    }

    @Transactional
    public OrderCreateResult placeOrder(User user, OrderCreateFacadeCommand command) {

        List<Item> items = itemService.decreaseStocks(command.toStockDecreaseCommands());

        OrderInfo orderInfo = orderService.createOrder(command.toOrderCreateCommand(user, items));

        if (command.couponId() != null) {
            int discountAmount = couponService.applyCoupon(command.toCouponApplyCommand(orderInfo));
            orderInfo.applyDiscount(discountAmount);
        }

        pointService.use(user, command.toPointUseCommand(orderInfo));

        return OrderCreateResult.from(orderInfo);
    }
}
