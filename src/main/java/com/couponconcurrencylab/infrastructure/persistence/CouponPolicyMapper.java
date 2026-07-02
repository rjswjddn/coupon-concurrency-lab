package com.couponconcurrencylab.infrastructure.persistence;

import com.couponconcurrencylab.domain.CouponPolicy;

/**
 * CouponPolicy 도메인 <-> CouponPolicyEntity 매핑.
 */
public final class CouponPolicyMapper {

    private CouponPolicyMapper() {
    }

    public static CouponPolicy toDomain(CouponPolicyEntity entity) {
        return CouponPolicy.builder()
                .id(entity.getId())
                .name(entity.getName())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .totalQuantity(entity.getTotalQuantity())
                .stockQuantity(entity.getStockQuantity())
                .issueStartAt(entity.getIssueStartAt())
                .issueEndAt(entity.getIssueEndAt())
                .onePerMember(entity.isOnePerMember())
                .build();
    }

    public static CouponPolicyEntity toEntity(CouponPolicy domain) {
        return CouponPolicyEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .discountType(domain.getDiscountType())
                .discountValue(domain.getDiscountValue())
                .totalQuantity(domain.getTotalQuantity())
                .stockQuantity(domain.getStockQuantity())
                .issueStartAt(domain.getIssueStartAt())
                .issueEndAt(domain.getIssueEndAt())
                .onePerMember(domain.isOnePerMember())
                .build();
    }
}
