package com.couponconcurrencylab.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCouponEntity, Long> {
    Boolean existsByCouponPolicyIdAndMemberId(Long couponPolicyId, Long memberId);
}
