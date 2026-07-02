package com.couponconcurrencylab.infrastructure.persistence;

import com.couponconcurrencylab.domain.IssuedCoupon;

/**
 * IssuedCoupon 도메인 <-> IssuedCouponEntity 매핑.
 */
public final class IssuedCouponMapper {

    private IssuedCouponMapper() {
    }

    public static IssuedCoupon toDomain(IssuedCouponEntity entity) {
        return IssuedCoupon.builder()
                .id(entity.getId())
                .memberId(entity.getMemberId())
                .couponPolicyId(entity.getCouponPolicyId())
                .status(entity.getStatus())
                .issuedAt(entity.getIssuedAt())
                .usedAt(entity.getUsedAt())
                .build();
    }

    public static IssuedCouponEntity toEntity(IssuedCoupon domain) {
        return IssuedCouponEntity.builder()
                .id(domain.getId())
                .memberId(domain.getMemberId())
                .couponPolicyId(domain.getCouponPolicyId())
                .status(domain.getStatus())
                .issuedAt(domain.getIssuedAt())
                .usedAt(domain.getUsedAt())
                .build();
    }
}
