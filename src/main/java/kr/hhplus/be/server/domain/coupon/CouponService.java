package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<CouponIssue> findByUserId(long userId) {
        return couponRepository.findByUserId(userId);
    }
}
