package com.couponconcurrencylab.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 애그리거트 루트. 쿠폰 발급 규칙과 재고를 보유한다.
 *
 * <p>순수 도메인 모델(JPA 무관). 영속성 매핑은 persistence 계층의 엔티티가 담당한다.
 */
@Getter
public class CouponPolicy {

    private final Long id;
    private final String name;
    private final DiscountType discountType;
    private final int discountValue;
    private final int totalQuantity;
    private int stockQuantity;
    private final LocalDateTime issueStartAt;
    private final LocalDateTime issueEndAt;
    private final boolean onePerMember;

    @Builder
    private CouponPolicy(Long id, String name, DiscountType discountType, int discountValue,
                         int totalQuantity, int stockQuantity,
                         LocalDateTime issueStartAt, LocalDateTime issueEndAt, boolean onePerMember) {
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

    /** 새 정책을 생성한다. 최초 재고 = 총 수량. */
    public static CouponPolicy create(String name, DiscountType discountType, int discountValue,
                                      int totalQuantity, LocalDateTime issueStartAt,
                                      LocalDateTime issueEndAt, boolean onePerMember) {
        return CouponPolicy.builder()
                .name(name)
                .discountType(discountType)
                .discountValue(discountValue)
                .totalQuantity(totalQuantity)
                .stockQuantity(totalQuantity)
                .issueStartAt(issueStartAt)
                .issueEndAt(issueEndAt)
                .onePerMember(onePerMember)
                .build();
    }

    /** now 가 발급 기간(issueStartAt ~ issueEndAt) 안에 있는지. */
    public boolean isWithinIssuePeriod(LocalDateTime now) {
        return !now.isBefore(issueStartAt) && !now.isAfter(issueEndAt);
    }

    /** 남은 재고가 있는지. */
    public boolean hasStock() {
        return stockQuantity > 0;
    }

    /** 재고를 1 차감한다. 재고가 없으면 예외. */
    public void decreaseStock() {
        if (!hasStock()) {
            throw new IllegalStateException("재고가 모두 소진되었습니다. policyId=" + id);
        }
        stockQuantity--;
    }

    /** 재고를 1 되돌린다. */
    public void increaseStock() {
        stockQuantity++;
    }

    public void outStock() {
        this.stockQuantity = 0;
    }

    /** 원래 가격에 이 정책의 할인을 적용한 최종 가격(0 미만이면 0). */
    public int applyDiscount(int originalPrice) {
        int discounted = switch (discountType) {
            case AMOUNT -> originalPrice - discountValue;
            case RATE -> originalPrice - (originalPrice * discountValue / 100);
        };
        return Math.max(discounted, 0);
    }
}
