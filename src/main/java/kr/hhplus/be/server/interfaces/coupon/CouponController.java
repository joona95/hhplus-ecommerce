package kr.hhplus.be.server.interfaces.coupon;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.interfaces.coupon.dto.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.coupon.dto.CouponResponse;
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
public class CouponController implements CouponApiSpec {

    @GetMapping
    @Override
    public ResponseEntity<List<CouponResponse>> getUserCoupons(@RequestParam @Positive long userId) {
        return ResponseEntity.ok(List.of(
                new CouponResponse(1L, "쿠폰1", "2025-12-31", false),
                new CouponResponse(2L, "쿠폰2", "2025-12-31", true),
                new CouponResponse(3L, "쿠폰3", "2025-05-31", false)
        ));
    }

    @PostMapping
    @Override
    public ResponseEntity<CouponResponse> issueCoupon(@RequestBody @Valid CouponIssueRequest request) {
        return ResponseEntity.ok(new CouponResponse(1L, "쿠폰1", "2025-12-31", false));
    }
}
