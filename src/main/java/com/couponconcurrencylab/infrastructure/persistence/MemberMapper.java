package com.couponconcurrencylab.infrastructure.persistence;

import com.couponconcurrencylab.domain.Member;

/**
 * Member 도메인 <-> MemberEntity 매핑.
 */
public final class MemberMapper {

    private MemberMapper() {
    }

    public static Member toDomain(MemberEntity entity) {
        return Member.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .build();
    }

    public static MemberEntity toEntity(Member domain) {
        return MemberEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .email(domain.getEmail())
                .build();
    }
}
