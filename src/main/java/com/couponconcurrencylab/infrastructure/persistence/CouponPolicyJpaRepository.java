package com.couponconcurrencylab.infrastructure.persistence;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponPolicyJpaRepository extends JpaRepository<CouponPolicyEntity, Long> {

    /**
     * 이름 키워드 + 발급 기간 상태로 정책을 검색한다.
     *
     * @param status ALL / ACTIVE(발급중) / UPCOMING(예정) / ENDED(종료)
     */
    @Query("""
            select p from CouponPolicyEntity p
            where (:keyword = '' or p.name like concat('%', :keyword, '%'))
              and (:status = 'ALL'
                   or (:status = 'ACTIVE'   and p.issueStartAt <= :now and p.issueEndAt >= :now)
                   or (:status = 'UPCOMING' and p.issueStartAt > :now)
                   or (:status = 'ENDED'    and p.issueEndAt < :now))
            """)
    Page<CouponPolicyEntity> search(@Param("keyword") String keyword,
                                    @Param("status") String status,
                                    @Param("now") LocalDateTime now,
                                    Pageable pageable);
}
