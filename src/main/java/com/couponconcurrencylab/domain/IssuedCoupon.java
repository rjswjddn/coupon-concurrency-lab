package com.couponconcurrencylab.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 특정 멤버가 실제 발급받은 쿠폰 인스턴스.
 *
 * <p>순수 도메인 모델(JPA 무관). 다른 애그리거트(Member, CouponPolicy)는 객체가 아니라
 * 식별자(memberId, couponPolicyId)로 참조한다.
 */
@Getter
public class IssuedCoupon {

    private final Long id;
    private final Long memberId;
    private final Long couponPolicyId;
    private CouponStatus status;
    private final LocalDateTime issuedAt;
    private LocalDateTime usedAt;

    @Builder
    private IssuedCoupon(Long id, Long memberId, Long couponPolicyId, CouponStatus status,
                         LocalDateTime issuedAt, LocalDateTime usedAt) {
        this.id = id;
        this.memberId = memberId;
        this.couponPolicyId = couponPolicyId;
        this.status = status;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
    }

    /** 멤버에게 정책 쿠폰을 발급한 인스턴스를 생성한다. */
    public static IssuedCoupon issue(Long memberId, Long couponPolicyId, LocalDateTime issuedAt) {
        return IssuedCoupon.builder()
                .memberId(memberId)
                .couponPolicyId(couponPolicyId)
                .status(CouponStatus.ISSUED)
                .issuedAt(issuedAt)
                .build();
    }

    /** 쿠폰을 사용 처리한다. ISSUED 상태에서만 가능. */
    public void use(LocalDateTime usedAt) {
        if (status != CouponStatus.ISSUED) {
            throw new IllegalStateException("사용할 수 없는 상태입니다. status=" + status);
        }
        this.status = CouponStatus.USED;
        this.usedAt = usedAt;
    }

    /** 쿠폰을 만료 처리한다. */
    public void expire() {
        this.status = CouponStatus.EXPIRED;
    }
}
