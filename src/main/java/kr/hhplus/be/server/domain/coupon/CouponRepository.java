package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository {

    List<CouponIssue> findByUserId(long userId);
}
