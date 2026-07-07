package com.couponconcurrencylab.application;

import com.couponconcurrencylab.domain.CouponPolicy;
import com.couponconcurrencylab.domain.DiscountType;
import com.couponconcurrencylab.infrastructure.persistence.CouponPolicyRepository;
import com.couponconcurrencylab.infrastructure.persistence.IssuedCouponRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;
    private final IssuedCouponRepository issuedCouponRepository;

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

    /** 정책 + 실제 발급 행 수. 목록 화면에서 재고 카운터와 실제 발급 수를 나란히 보여주기 위한 조합. */
    public record PolicyWithIssuedCount(CouponPolicy policy, long issuedCount) {
    }

    @Transactional(readOnly = true)
    public Page<PolicyWithIssuedCount> list(String keyword, String status, Pageable pageable) {
        Page<CouponPolicy> page = couponPolicyRepository.search(keyword, status, LocalDateTime.now(), pageable);
        List<Long> ids = page.getContent().stream().map(CouponPolicy::getId).toList();
        Map<Long, Long> counts = issuedCouponRepository.countByPolicyIds(ids);
        return page.map(policy -> new PolicyWithIssuedCount(policy, counts.getOrDefault(policy.getId(), 0L)));
    }
}
