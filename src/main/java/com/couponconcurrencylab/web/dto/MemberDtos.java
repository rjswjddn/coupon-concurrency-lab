package com.couponconcurrencylab.web.dto;

import com.couponconcurrencylab.domain.Member;

public final class MemberDtos {

    private MemberDtos() {
    }

    public record CreateRequest(String name, String email) {
    }

    public record Response(Long id, String name, String email) {
        public static Response from(Member member) {
            return new Response(member.getId(), member.getName(), member.getEmail());
        }
    }
}
