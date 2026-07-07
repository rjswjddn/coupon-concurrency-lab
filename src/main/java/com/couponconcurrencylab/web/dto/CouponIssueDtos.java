package com.couponconcurrencylab.web.dto;

import com.couponconcurrencylab.application.CouponIssueService;
import com.couponconcurrencylab.domain.CouponPolicy;
import com.couponconcurrencylab.domain.CouponStatus;
import com.couponconcurrencylab.domain.IssuedCoupon;
import java.time.LocalDateTime;

public final class CouponIssueDtos {

    private CouponIssueDtos() {
    }

    public record IssueRequest(Long memberId) {
    }

    public record Response(
            Long issuedCouponId,
            Long memberId,
            Long couponPolicyId,
            CouponStatus status,
            LocalDateTime issuedAt) {

        public static Response from(IssuedCoupon issued) {
            return new Response(
                    issued.getId(),
                    issued.getMemberId(),
                    issued.getCouponPolicyId(),
                    issued.getStatus(),
                    issued.getIssuedAt());
        }
    }

    /**
     * 발급 현황 응답. 재고 카운터(stockQuantity)와 실제 발급 행 수(issuedCount)를 함께 내려
     * 화면에서 두 값을 나란히 비교할 수 있게 한다.
     */
    public record StatsResponse(
            Long policyId,
            String policyName,
            int totalQuantity,
            int stockQuantity,
            long issuedCount,
            long duplicateIssuedCount,
            boolean onePerMember) {

        public static StatsResponse from(CouponIssueService.IssueStats stats) {
            CouponPolicy policy = stats.policy();
            return new StatsResponse(
                    policy.getId(),
                    policy.getName(),
                    policy.getTotalQuantity(),
                    policy.getStockQuantity(),
                    stats.issuedCount(),
                    stats.duplicateIssuedCount(),
                    policy.isOnePerMember());
        }
    }
}
