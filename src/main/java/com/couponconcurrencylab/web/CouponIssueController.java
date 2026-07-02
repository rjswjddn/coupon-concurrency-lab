package com.couponconcurrencylab.web;

import com.couponconcurrencylab.application.CouponIssueService;
import com.couponconcurrencylab.domain.IssuedCoupon;
import com.couponconcurrencylab.web.dto.CouponIssueDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
