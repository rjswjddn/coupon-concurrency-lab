package com.couponconcurrencylab.domain;

/**
 * 할인 방식. 정액(원 단위) / 정률(퍼센트).
 */
public enum DiscountType {
    AMOUNT, // discountValue = 할인 금액(원)
    RATE    // discountValue = 할인율(%)
}
