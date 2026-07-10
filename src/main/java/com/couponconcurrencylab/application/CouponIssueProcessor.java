package com.couponconcurrencylab.application;

import com.couponconcurrencylab.domain.CouponPolicy;
import com.couponconcurrencylab.domain.IssuedCoupon;
import com.couponconcurrencylab.infrastructure.persistence.CouponPolicyRepository;
import com.couponconcurrencylab.infrastructure.persistence.IssuedCouponRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 쿠폰 발급의 영속화(정책 조회, 발급 저장)를 담당한다.
 */
@Component
@RequiredArgsConstructor
public class CouponIssueProcessor {

    private final CouponPolicyRepository couponPolicyRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    @Transactional(readOnly = true)
    public CouponPolicy loadPolicy(Long policyId) {
        return couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 정책이 없습니다. policyId=" + policyId));
    }

    @Transactional
    public IssuedCoupon saveIssued(Long policyId, Long memberId, boolean soldOut) {
        if (soldOut) {
            CouponPolicy policy = couponPolicyRepository.findById(policyId)
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰 정책이 없습니다. policyId=" + policyId));
            policy.outStock();
            couponPolicyRepository.save(policy);
        }

        if (issuedCouponRepository.existsByCouponPolicyIdAndMemberId(policyId, memberId)) {
            throw new IllegalStateException(
                    "이미 쿠폰을 발급 받았습니다. policyId=" + policyId + ", memberId=" + memberId);
        }

        IssuedCoupon issued = IssuedCoupon.issue(memberId, policyId, LocalDateTime.now());
        IssuedCoupon saved;

        try {
            saved = issuedCouponRepository.save(issued);
        } catch (DataIntegrityViolationException dve) {
            throw new IllegalStateException(
                    "이미 쿠폰을 발급 받았습니다. policyId=" + policyId + ", memberId=" + memberId, dve);
        }

        return saved;
    }
}
