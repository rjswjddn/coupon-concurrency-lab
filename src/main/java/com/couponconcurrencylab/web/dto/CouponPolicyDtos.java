package com.couponconcurrencylab.web.dto;

import com.couponconcurrencylab.application.CouponPolicyService;
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

    /** 목록용 항목. 정책 정보에 실제 발급 행 수(issuedCount)를 더해 내려준다. */
    public record ListItem(
            Long id,
            String name,
            DiscountType discountType,
            int discountValue,
            int totalQuantity,
            int stockQuantity,
            long issuedCount,
            LocalDateTime issueStartAt,
            LocalDateTime issueEndAt,
            boolean onePerMember) {

        public static ListItem from(CouponPolicyService.PolicyWithIssuedCount item) {
            CouponPolicy policy = item.policy();
            return new ListItem(
                    policy.getId(),
                    policy.getName(),
                    policy.getDiscountType(),
                    policy.getDiscountValue(),
                    policy.getTotalQuantity(),
                    policy.getStockQuantity(),
                    item.issuedCount(),
                    policy.getIssueStartAt(),
                    policy.getIssueEndAt(),
                    policy.isOnePerMember());
        }
    }
}
