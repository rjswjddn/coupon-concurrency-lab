package com.couponconcurrencylab.infrastructure.persistence;

import com.couponconcurrencylab.domain.CouponStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * IssuedCoupon 도메인의 영속성 매핑 전용 엔티티. 도메인 행위는 갖지 않는다.
 *
 * <p>다른 애그리거트는 FK 컬럼(member_id, coupon_policy_id)으로만 참조한다.
 */
@Entity
@Table(
        name = "issued_coupon",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_DUPLICATED_COUPON_POLICY",
                        columnNames = {"member_id", "coupon_policy_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssuedCouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "coupon_policy_id", nullable = false)
    private Long couponPolicyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponStatus status;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column
    private LocalDateTime usedAt;

    @Builder
    private IssuedCouponEntity(Long id, Long memberId, Long couponPolicyId, CouponStatus status,
                               LocalDateTime issuedAt, LocalDateTime usedAt) {
        this.id = id;
        this.memberId = memberId;
        this.couponPolicyId = couponPolicyId;
        this.status = status;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
    }
}
