package com.couponconcurrencylab.application;

import com.couponconcurrencylab.domain.CouponPolicy;
import com.couponconcurrencylab.domain.DiscountType;
import com.couponconcurrencylab.infrastructure.persistence.CouponPolicyRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;

    @Transactional
    public CouponPolicy create(String name, DiscountType discountType, int discountValue,
                               int totalQuantity, LocalDateTime issueStartAt, LocalDateTime issueEndAt,
                               boolean onePerMember) {
        CouponPolicy policy = CouponPolicy.create(name, discountType, discountValue, totalQuantity,
                issueStartAt, issueEndAt, onePerMember);
        return couponPolicyRepository.save(policy);
    }

    @Transactional(readOnly = true)
    public CouponPolicy get(Long id) {
        return couponPolicyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 정책이 없습니다. policyId=" + id));
    }
}
