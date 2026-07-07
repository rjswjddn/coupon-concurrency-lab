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

    /**
     *  쿠폰 발급 비즈니스 로직
     *  1. 쿠폰 정책 조회 및 검증
     *  2. 쿠폰 발급 기간 검증
     *  3. 쿠폰 중복 발금 검증
     *  4. 쿠폰 재고 검증 및 차감
     *  5. 발급 쿠폰 저장
     */
    @Transactional
    public IssuedCoupon issue(Long policyId, Long memberId) {
        CouponPolicy policy = couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 정책이 없습니다. policyId=" + policyId));

        LocalDateTime now = LocalDateTime.now();
        if (!policy.isWithinIssuePeriod(now)) {
            throw new IllegalStateException("발급 기간이 아닙니다. policyId=" + policyId);
        }

        if (issuedCouponRepository.existsByCouponPolicyIdAndMemberId(policyId, memberId)) {
            throw new IllegalStateException("이미 쿠폰을 발급 받았습니다. policyId=" + policyId + ", memberId=" + memberId);
        }

        policy.decreaseStock();
        couponPolicyRepository.save(policy);

        IssuedCoupon issued = IssuedCoupon.issue(memberId, policyId, now);
        return issuedCouponRepository.save(issued);
    }
}
