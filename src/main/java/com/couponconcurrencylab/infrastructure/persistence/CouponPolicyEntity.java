package com.couponconcurrencylab.infrastructure.persistence;

import com.couponconcurrencylab.domain.DiscountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

/**
 * CouponPolicy 도메인의 영속성 매핑 전용 엔티티. 도메인 행위는 갖지 않는다.
 */
@Entity
@Table(name = "coupon_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscountType discountType;

    @Column(nullable = false)
    private int discountValue;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    private LocalDateTime issueStartAt;

    @Column(nullable = false)
    private LocalDateTime issueEndAt;

    @Column(nullable = false)
    private boolean onePerMember;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private CouponPolicyEntity(Long id, String name, DiscountType discountType, int discountValue,
                               int totalQuantity, int stockQuantity, LocalDateTime issueStartAt,
                               LocalDateTime issueEndAt, boolean onePerMember) {
        this.id = id;
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.totalQuantity = totalQuantity;
        this.stockQuantity = stockQuantity;
        this.issueStartAt = issueStartAt;
        this.issueEndAt = issueEndAt;
        this.onePerMember = onePerMember;
    }
}
