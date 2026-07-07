package com.couponconcurrencylab.web;

import com.couponconcurrencylab.application.CouponPolicyService;
import com.couponconcurrencylab.domain.CouponPolicy;
import com.couponconcurrencylab.web.dto.CouponPolicyDtos;
import com.couponconcurrencylab.web.dto.PageResponse;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /** 발급 기간 상태 필터로 허용하는 값. */
    private static final Set<String> STATUS_FILTERS = Set.of("ALL", "ACTIVE", "UPCOMING", "ENDED");

    @GetMapping
    public ResponseEntity<PageResponse<CouponPolicyDtos.ListItem>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String normalized = status.toUpperCase();
        if (!STATUS_FILTERS.contains(normalized)) {
            throw new IllegalArgumentException("지원하지 않는 상태 필터입니다. status=" + status);
        }
        var pageable = PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, 100),
                Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(PageResponse.from(
                couponPolicyService.list(keyword.trim(), normalized, pageable),
                CouponPolicyDtos.ListItem::from));
    }
}
