package com.couponconcurrencylab.infrastructure.persistence;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCouponEntity, Long> {
    Boolean existsByCouponPolicyIdAndMemberId(Long couponPolicyId, Long memberId);

    /** 정책별 발급 집계 결과(전체 행 수 / 발급받은 멤버 수). */
    interface IssueAggregate {
        long getTotalCount();

        long getMemberCount();
    }

    /** 정책 하나의 발급 현황을 집계한다. 전체 발급 행 수와 중복 제거한 멤버 수를 함께 센다. */
    @Query("""
            select count(i) as totalCount, count(distinct i.memberId) as memberCount
            from IssuedCouponEntity i
            where i.couponPolicyId = :policyId
            """)
    IssueAggregate aggregateByPolicyId(@Param("policyId") Long policyId);

    /** 정책별 발급 행 수. */
    interface PolicyIssuedCount {
        Long getPolicyId();

        long getIssuedCount();
    }

    /** 여러 정책의 실제 발급 행 수를 한 번에 집계한다(목록 화면용). */
    @Query("""
            select i.couponPolicyId as policyId, count(i) as issuedCount
            from IssuedCouponEntity i
            where i.couponPolicyId in :policyIds
            group by i.couponPolicyId
            """)
    List<PolicyIssuedCount> countByPolicyIds(@Param("policyIds") Collection<Long> policyIds);
}
