package com.couponconcurrencylab.infrastructure.persistence;

import com.couponconcurrencylab.domain.IssuedCoupon;
import java.util.Optional;
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
}
