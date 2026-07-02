package com.couponconcurrencylab.domain;

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
 * 애그리거트 루트. 쿠폰 발급 규칙을 보유하는, 거의 안 바뀌는 메타데이터.
 *
 * <p>재고(stockQuantity)는 이 정책 row 안의 컬럼으로 둔다.
 */
@Entity
@Table(name = "coupon_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscountType discountType;

    /** 정액이면 할인 금액(원), 정률이면 할인율(%). */
    @Column(nullable = false)
    private int discountValue;

    /** 선착순 총 수량. 최초 재고의 기준값(불변). */
    @Column(nullable = false)
    private int totalQuantity;

    /** 남은 재고. 발급 시 차감된다. */
    @Column(nullable = false)
    private int stockQuantity;

    /** 발급 시작 시각. */
    @Column(nullable = false)
    private LocalDateTime issueStartAt;

    /** 발급 종료 시각. */
    @Column(nullable = false)
    private LocalDateTime issueEndAt;

    /** 1인 1매 여부. true 면 한 멤버당 하나만 발급 가능. */
    @Column(nullable = false)
    private boolean onePerMember;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private CouponPolicy(String name, DiscountType discountType, int discountValue,
                         int totalQuantity, LocalDateTime issueStartAt, LocalDateTime issueEndAt,
                         boolean onePerMember) {
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.totalQuantity = totalQuantity;
        this.stockQuantity = totalQuantity; // 최초 재고 = 총 수량
        this.issueStartAt = issueStartAt;
        this.issueEndAt = issueEndAt;
        this.onePerMember = onePerMember;
    }
}
