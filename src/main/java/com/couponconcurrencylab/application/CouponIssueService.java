package com.couponconcurrencylab.application;

import com.couponconcurrencylab.domain.CouponPolicy;
import com.couponconcurrencylab.domain.IssuedCoupon;
import com.couponconcurrencylab.infrastructure.persistence.CouponPolicyRepository;
import com.couponconcurrencylab.infrastructure.persistence.IssuedCouponRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponPolicyRepository couponPolicyRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    @Transactional
    public IssuedCoupon issue(Long policyId, Long memberId) {
        CouponPolicy policy = couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 정책이 없습니다. policyId=" + policyId));

        LocalDateTime now = LocalDateTime.now();
        if (!policy.isWithinIssuePeriod(now)) {
            throw new IllegalStateException("발급 기간이 아닙니다. policyId=" + policyId);
        }

        policy.decreaseStock();
        couponPolicyRepository.save(policy);

        IssuedCoupon issued = IssuedCoupon.issue(memberId, policyId, now);
        return issuedCouponRepository.save(issued);
    }
}
