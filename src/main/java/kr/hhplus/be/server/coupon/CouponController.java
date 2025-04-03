package kr.hhplus.be.server.coupon;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.coupon.dto.CouponIssueRequest;
import kr.hhplus.be.server.coupon.dto.CouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponApi {

    @GetMapping
    @Override
    public ResponseEntity<List<CouponResponse>> getUserCoupons(@RequestParam @Positive long userId) {
        return null;
    }

    @PostMapping
    @Override
    public ResponseEntity<CouponResponse> issueCoupon(@RequestBody @Valid CouponIssueRequest request) {
        return null;
    }
}
