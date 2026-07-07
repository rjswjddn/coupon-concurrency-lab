package com.couponconcurrencylab.infrastructure.persistence;

import com.couponconcurrencylab.domain.IssuedCoupon;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * IssuedCoupon 아그리거트의 영속성 어댑터. JPA 엔티티/매퍼를 감추고 도메인 타입만 노출한다.
 */
@Repository
@RequiredArgsConstructor
public class IssuedCouponRepository {

    private final IssuedCouponJpaRepository jpaRepository;

    public IssuedCoupon save(IssuedCoupon issuedCoupon) {
        return IssuedCouponMapper.toDomain(jpaRepository.save(IssuedCouponMapper.toEntity(issuedCoupon)));
    }

    public Optional<IssuedCoupon> findById(Long id) {
        return jpaRepository.findById(id).map(IssuedCouponMapper::toDomain);
    }

    public Boolean existsByCouponPolicyIdAndMemberId(Long couponPolicyId, Long memberId) {
        return jpaRepository.existsByCouponPolicyIdAndMemberId(couponPolicyId, memberId);
    }

    /** 정책 하나의 발급 현황 집계(전체 발급 행 수 / 발급받은 멤버 수). */
    public record IssueAggregate(long totalCount, long memberCount) {
    }

    public IssueAggregate aggregateByPolicyId(Long couponPolicyId) {
        IssuedCouponJpaRepository.IssueAggregate result = jpaRepository.aggregateByPolicyId(couponPolicyId);
        return new IssueAggregate(result.getTotalCount(), result.getMemberCount());
    }

    /** 여러 정책의 실제 발급 행 수. key=정책 ID, value=발급 행 수(없으면 미포함). */
    public Map<Long, Long> countByPolicyIds(Collection<Long> couponPolicyIds) {
        if (couponPolicyIds.isEmpty()) {
            return Map.of();
        }
        return jpaRepository.countByPolicyIds(couponPolicyIds).stream()
                .collect(Collectors.toMap(
                        IssuedCouponJpaRepository.PolicyIssuedCount::getPolicyId,
                        IssuedCouponJpaRepository.PolicyIssuedCount::getIssuedCount));
    }
}
