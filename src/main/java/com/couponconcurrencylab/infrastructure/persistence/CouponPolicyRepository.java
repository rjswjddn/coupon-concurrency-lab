package com.couponconcurrencylab.infrastructure.persistence;

import com.couponconcurrencylab.domain.CouponPolicy;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * CouponPolicy 아그리거트의 영속성 어댑터. JPA 엔티티/매퍼를 감추고 도메인 타입만 노출한다.
 */
@Repository
@RequiredArgsConstructor
public class CouponPolicyRepository {

    private final CouponPolicyJpaRepository jpaRepository;

    public CouponPolicy save(CouponPolicy couponPolicy) {
        return CouponPolicyMapper.toDomain(jpaRepository.save(CouponPolicyMapper.toEntity(couponPolicy)));
    }

    public Optional<CouponPolicy> findById(Long id) {
        return jpaRepository.findById(id).map(CouponPolicyMapper::toDomain);
    }
}
