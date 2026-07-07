package com.couponconcurrencylab.web.dto;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

/** 목록 API 공용 페이지 응답. Spring Data Page 를 그대로 직렬화하지 않고 필요한 필드만 노출한다. */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static <S, T> PageResponse<T> from(Page<S> page, Function<S, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
