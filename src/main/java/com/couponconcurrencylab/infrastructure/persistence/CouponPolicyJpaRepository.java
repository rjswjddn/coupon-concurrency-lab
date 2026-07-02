package com.couponconcurrencylab.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponPolicyJpaRepository extends JpaRepository<CouponPolicyEntity, Long> {
}
