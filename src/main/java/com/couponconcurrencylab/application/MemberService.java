package com.couponconcurrencylab.application;

import com.couponconcurrencylab.domain.Member;
import com.couponconcurrencylab.infrastructure.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member create(String name, String email) {
        return memberRepository.save(Member.create(name, email));
    }

    @Transactional(readOnly = true)
    public Page<Member> list(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }
}
