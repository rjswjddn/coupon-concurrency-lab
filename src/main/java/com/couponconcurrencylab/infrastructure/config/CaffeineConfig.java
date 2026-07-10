package com.couponconcurrencylab.infrastructure.config;

import com.couponconcurrencylab.domain.CouponPolicy;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class CaffeineConfig {

    @Bean
    public Cache<Long, ReentrantLock> issueLocks() {
        return Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public Cache<Long, CouponPolicy> policyCache() {
        return Caffeine.newBuilder()
                .build();
    }
}
