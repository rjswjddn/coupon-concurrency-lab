package com.couponconcurrencylab.web;

import com.couponconcurrencylab.application.CouponIssueService;
import com.couponconcurrencylab.domain.IssuedCoupon;
import com.couponconcurrencylab.web.dto.CouponIssueDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupon-policies/{policyId}/issues")
@RequiredArgsConstructor
public class CouponIssueController {

    private final CouponIssueService couponIssueService;

    @PostMapping
    public ResponseEntity<CouponIssueDtos.Response> issue(
            @PathVariable Long policyId,
            @RequestBody CouponIssueDtos.IssueRequest request) {
        IssuedCoupon issued = couponIssueService.issue(policyId, request.memberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CouponIssueDtos.Response.from(issued));
    }

    /** 발급 현황(재고 카운터 + 실제 발급 행 수 + 중복 발급 수) 조회. */
    @GetMapping("/stats")
    public ResponseEntity<CouponIssueDtos.StatsResponse> stats(@PathVariable Long policyId) {
        return ResponseEntity.ok(CouponIssueDtos.StatsResponse.from(couponIssueService.stats(policyId)));
    }
}
