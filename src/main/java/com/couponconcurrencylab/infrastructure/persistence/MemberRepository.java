package com.couponconcurrencylab.infrastructure.persistence;

import com.couponconcurrencylab.domain.Member;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Member 아그리거트의 영속성 어댑터. JPA 엔티티/매퍼를 감추고 도메인 타입만 노출한다.
 */
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final MemberJpaRepository jpaRepository;

    public Member save(Member member) {
        return MemberMapper.toDomain(jpaRepository.save(MemberMapper.toEntity(member)));
    }

    public Optional<Member> findById(Long id) {
        return jpaRepository.findById(id).map(MemberMapper::toDomain);
    }
}
