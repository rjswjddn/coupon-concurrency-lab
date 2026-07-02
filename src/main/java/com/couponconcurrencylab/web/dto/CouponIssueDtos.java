package com.couponconcurrencylab.web.dto;

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
}
