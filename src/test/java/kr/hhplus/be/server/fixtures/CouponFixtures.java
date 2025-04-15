package kr.hhplus.be.server.fixtures;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.coupon.DiscountType;

import java.time.LocalDateTime;
import java.util.List;

public class CouponFixtures {

    public static Coupon 쿠폰명으로_쿠폰_생성(String couponName) {
        return new Coupon(1L, couponName, DiscountType.FIXED, 10000, LocalDateTime.MIN, LocalDateTime.MAX, 100, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Coupon 할인타입으로_쿠폰_생성(DiscountType discountType) {
        return new Coupon(1L, "쿠폰명", discountType, 10000, LocalDateTime.MIN, LocalDateTime.MAX, 100, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Coupon 유효시작일로_쿠폰_생성(LocalDateTime validTo) {
        return new Coupon(1L, "쿠폰명", DiscountType.FIXED, 10000, validTo, LocalDateTime.MAX, 100, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Coupon 유효종료일로_쿠폰_생성(LocalDateTime validFrom) {
        return new Coupon(1L, "쿠폰명", DiscountType.FIXED, 10000, LocalDateTime.MIN, validFrom, 100, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Coupon 할인율_할인금액으로_쿠폰_생성(int discountValue) {
        return new Coupon(1L, "쿠폰명", DiscountType.FIXED, discountValue, LocalDateTime.MIN, LocalDateTime.MAX, 100, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Coupon 발급수량으로_쿠폰_생성(int count) {
        return new Coupon(1L, "쿠폰명", DiscountType.FIXED, 10000, LocalDateTime.MIN, LocalDateTime.MAX, count, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Coupon 정상_쿠폰_생성() {
        return new Coupon(1L, "쿠폰명", DiscountType.FIXED, 10000, LocalDateTime.MIN, LocalDateTime.MAX, 10, LocalDateTime.now(), LocalDateTime.now());
    }

    public static CouponIssue 쿠폰명으로_쿠폰_발급_내역_생성(String couponName) {
        return new CouponIssue(1L, 1L, couponName, DiscountType.FIXED, 10000, 1L, LocalDateTime.MAX, false, LocalDateTime.now());
    }

    public static CouponIssue 할인타입으로_쿠폰_발급_내역_생성(DiscountType discountType) {
        return new CouponIssue(1L, 1L, "쿠폰명", discountType, 10000, 1L, LocalDateTime.MAX, false, LocalDateTime.now());
    }

    public static CouponIssue 할인율_할인금액으로_쿠폰_발급_내역_생성(int discountValue) {
        return new CouponIssue(1L, 1L, "쿠폰명", DiscountType.FIXED, discountValue, 1L, LocalDateTime.MAX, false, LocalDateTime.now());
    }

    public static CouponIssue 쿠폰식별자로_쿠폰_발급_내역_생성(long couponId) {
        return new CouponIssue(1L, couponId, "쿠폰명", DiscountType.FIXED, 10000, 1L, LocalDateTime.MAX, false, LocalDateTime.now());
    }

    public static CouponIssue 유저식별자로_쿠폰_발급_내역_생성(long userId) {
        return new CouponIssue(1L, 1L, "쿠폰명", DiscountType.FIXED, 10000, userId, LocalDateTime.MAX, false, LocalDateTime.now());
    }

    public static CouponIssue 만료일자로_쿠폰_발급_내역_생성(LocalDateTime expiredAt) {
        return new CouponIssue(1L, 1L, "쿠폰명", DiscountType.FIXED, 10000, 1L, expiredAt, false, LocalDateTime.now());
    }

    public static CouponIssue 사용한_쿠폰_발급_내역_생성() {
        return new CouponIssue(1L, 1L, "쿠폰명", DiscountType.FIXED, 10000, 1L, LocalDateTime.MAX, true, LocalDateTime.now());
    }

    public static CouponIssue 정액_할인_쿠폰_발급_내역_생성(int discountValue) {
        return new CouponIssue(1L, 1L, "쿠폰명", DiscountType.FIXED, discountValue, 1L, LocalDateTime.MAX, false, LocalDateTime.now());
    }

    public static CouponIssue 정률_할인_쿠폰_발급_내역_생성(int discountValue) {
        return new CouponIssue(1L, 1L, "쿠폰명", DiscountType.RATE, discountValue, 1L, LocalDateTime.MAX, false, LocalDateTime.now());
    }

    public static CouponIssue 정상_쿠폰_발급_내역_생성() {
        return new CouponIssue(1L, 1L, "쿠폰명1", DiscountType.FIXED, 10000, 1L, LocalDateTime.MAX, false, LocalDateTime.now());
    }

    public static List<CouponIssue> 정상_쿠폰_발급_내역_목록_생성() {
        return List.of(
                new CouponIssue(1L, 1L, "쿠폰명1", DiscountType.FIXED, 10000, 1L, LocalDateTime.MAX, false, LocalDateTime.now()),
                new CouponIssue(2L, 2L, "쿠폰명2", DiscountType.RATE, 100, 1L, LocalDateTime.MAX, false, LocalDateTime.now())
        );
    }
}
