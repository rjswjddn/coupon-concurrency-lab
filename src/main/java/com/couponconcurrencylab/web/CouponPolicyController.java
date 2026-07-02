package com.couponconcurrencylab.web;

import com.couponconcurrencylab.application.CouponPolicyService;
import com.couponconcurrencylab.domain.CouponPolicy;
import com.couponconcurrencylab.web.dto.CouponPolicyDtos;
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
@RequestMapping("/api/coupon-policies")
@RequiredArgsConstructor
public class CouponPolicyController {

    private final CouponPolicyService couponPolicyService;

    @PostMapping
    public ResponseEntity<CouponPolicyDtos.Response> create(
            @RequestBody CouponPolicyDtos.CreateRequest request) {
        CouponPolicy policy = couponPolicyService.create(
                request.name(),
                request.discountType(),
                request.discountValue(),
                request.totalQuantity(),
                request.issueStartAt(),
                request.issueEndAt(),
                request.onePerMember());
        return ResponseEntity.status(HttpStatus.CREATED).body(CouponPolicyDtos.Response.from(policy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponPolicyDtos.Response> get(@PathVariable Long id) {
        return ResponseEntity.ok(CouponPolicyDtos.Response.from(couponPolicyService.get(id)));
    }
}
