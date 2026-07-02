package com.couponconcurrencylab.web.dto;

import com.couponconcurrencylab.domain.CouponPolicy;
import com.couponconcurrencylab.domain.DiscountType;
import java.time.LocalDateTime;

public final class CouponPolicyDtos {

    private CouponPolicyDtos() {
    }

    public record CreateRequest(
            String name,
            DiscountType discountType,
            int discountValue,
            int totalQuantity,
            LocalDateTime issueStartAt,
            LocalDateTime issueEndAt,
            boolean onePerMember) {
    }

    public record Response(
            Long id,
            String name,
            DiscountType discountType,
            int discountValue,
            int totalQuantity,
            int stockQuantity,
            LocalDateTime issueStartAt,
            LocalDateTime issueEndAt,
            boolean onePerMember) {

        public static Response from(CouponPolicy policy) {
            return new Response(
                    policy.getId(),
                    policy.getName(),
                    policy.getDiscountType(),
                    policy.getDiscountValue(),
                    policy.getTotalQuantity(),
                    policy.getStockQuantity(),
                    policy.getIssueStartAt(),
                    policy.getIssueEndAt(),
                    policy.isOnePerMember());
        }
    }
}
