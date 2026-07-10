package com.couponconcurrencylab.application;

import com.couponconcurrencylab.domain.CouponPolicy;
import com.couponconcurrencylab.domain.IssuedCoupon;
import com.couponconcurrencylab.infrastructure.persistence.CouponPolicyRepository;
import com.couponconcurrencylab.infrastructure.persistence.IssuedCouponRepository;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponIssueService {

    private final CouponPolicyRepository couponPolicyRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final Cache<Long, ReentrantLock> issueLocks;
    private final Cache<Long, CouponPolicy> policyCache;
    private final CouponIssueProcessor processor;

    /**
     * 쿠폰 정책을 검증하고, 재고를 확보한 뒤, 쿠폰을 발급한다.
     */
    public IssuedCoupon issue(Long policyId, Long memberId) {
        CouponPolicy policy = policyCache.get(policyId, processor::loadPolicy);

        if (!policy.isWithinIssuePeriod(LocalDateTime.now())) {
            throw new IllegalStateException("발급 기간이 아닙니다. policyId=" + policyId);
        }

        int remaining = reserveStock(policy);
        try {
            return processor.saveIssued(policyId, memberId, remaining == 0);
        } catch (RuntimeException e) {
            restoreStock(policy);
            throw e;
        }
    }

    /** 재고를 1 확보하고 남은 재고를 반환한다. 남은 재고가 없으면 예외. */
    private int reserveStock(CouponPolicy policy) {
        ReentrantLock lock = issueLocks.get(policy.getId(), key -> new ReentrantLock());
        lock.lock();
        try {
            policy.decreaseStock();
            return policy.getStockQuantity();
        } finally {
            lock.unlock();
        }
    }

    /** 확보했던 재고를 1 되돌린다. */
    private void restoreStock(CouponPolicy policy) {
        ReentrantLock lock = issueLocks.get(policy.getId(), key -> new ReentrantLock());
        lock.lock();
        try {
            policy.increaseStock();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 정책 하나의 발급 현황.
     *
     * @param issuedCount 실제 발급된 쿠폰 행 수 (재고 카운터에서 파생한 값이 아니라 count 집계)
     * @param duplicateIssuedCount 같은 멤버에게 2매 이상 발급된 초과분 합계
     */
    public record IssueStats(CouponPolicy policy, long issuedCount, long duplicateIssuedCount) {
    }

    @Transactional(readOnly = true)
    public IssueStats stats(Long policyId) {
        CouponPolicy policy = couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 정책이 없습니다. policyId=" + policyId));

        IssuedCouponRepository.IssueAggregate aggregate = issuedCouponRepository.aggregateByPolicyId(policyId);
        // 전체 발급 행 수 - 발급받은 멤버 수 = 1인 1매를 넘긴 초과 발급분
        long duplicated = aggregate.totalCount() - aggregate.memberCount();
        return new IssueStats(policy, aggregate.totalCount(), duplicated);
    }
}
