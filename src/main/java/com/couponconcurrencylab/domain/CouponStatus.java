package com.couponconcurrencylab.domain;

/**
 * 발급된 쿠폰 인스턴스의 상태.
 */
public enum CouponStatus {
    ISSUED,  // 발급됨
    USED,    // 사용됨
    EXPIRED  // 만료됨
}
