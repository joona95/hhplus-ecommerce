package kr.hhplus.be.server.interfaces.coupon;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.coupon.CouponFacadeService;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static kr.hhplus.be.server.interfaces.coupon.CouponRequest.*;
import static kr.hhplus.be.server.interfaces.coupon.CouponResponse.*;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponApiSpec {

    private final CouponFacadeService couponFacadeService;
    private final CouponService couponService;

    public CouponController(CouponFacadeService couponFacadeService, CouponService couponService) {
        this.couponFacadeService = couponFacadeService;
        this.couponService = couponService;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<UserCouponResponse>> getUserCoupons(User user) {

        List<UserCouponResponse> response = couponService.findByUser(user).stream()
                .map(UserCouponResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Override
    public ResponseEntity<Void> issueCoupon(User user, @RequestBody @Valid CouponIssueRequest request) {

        couponFacadeService.requestCouponIssue(user, request.toCommand());

        return ResponseEntity.ok().build();
    }
}
