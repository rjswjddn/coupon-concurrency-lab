package com.couponconcurrencylab.web;

import com.couponconcurrencylab.application.MemberService;
import com.couponconcurrencylab.domain.Member;
import com.couponconcurrencylab.web.dto.MemberDtos;
import com.couponconcurrencylab.web.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public ResponseEntity<PageResponse<MemberDtos.Response>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, 100),
                Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(PageResponse.from(memberService.list(pageable), MemberDtos.Response::from));
    }
}
