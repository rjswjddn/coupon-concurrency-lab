package com.couponconcurrencylab.web;

import com.couponconcurrencylab.application.MemberService;
import com.couponconcurrencylab.domain.Member;
import com.couponconcurrencylab.web.dto.MemberDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberDtos.Response> create(@RequestBody MemberDtos.CreateRequest request) {
        Member member = memberService.create(request.name(), request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(MemberDtos.Response.from(member));
    }
}
