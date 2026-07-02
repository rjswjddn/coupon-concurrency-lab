package com.couponconcurrencylab.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * 발급 주체. 쿠폰을 받아가는 사용자.
 *
 * <p>순수 도메인 모델(JPA 무관). 영속성 매핑은 persistence 계층의 엔티티가 담당한다.
 */
@Getter
public class Member {

    private final Long id;
    private final String name;
    private final String email;

    @Builder
    private Member(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /** 새 멤버를 생성한다(아직 식별자 없음). */
    public static Member create(String name, String email) {
        return Member.builder()
                .name(name)
                .email(email)
                .build();
    }
}
