package com.couponconcurrencylab.infrastructure.persistence;

import com.couponconcurrencylab.domain.CouponPolicy;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /** 이름 키워드 + 발급 기간 상태(ALL/ACTIVE/UPCOMING/ENDED)로 검색한다. */
    public Page<CouponPolicy> search(String keyword, String status, LocalDateTime now, Pageable pageable) {
        return jpaRepository.search(keyword, status, now, pageable).map(CouponPolicyMapper::toDomain);
    }

    public Optional<CouponPolicy> findByIdWithLock(Long id) {
        return jpaRepository.findByIdWithLock(id).map(CouponPolicyMapper::toDomain);
    }
}
