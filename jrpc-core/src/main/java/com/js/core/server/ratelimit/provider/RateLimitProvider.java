package com.js.core.server.ratelimit.provider;

import com.js.core.server.ratelimit.RateLimit;
import com.js.core.server.ratelimit.impl.TokenBucketRateLimit;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RateLimitProvider {
    private Map<String, RateLimit> rateLimitMap = new HashMap<>();

    private static final int DEFAULT_CAPACITY = 10;
    private static final int DEFAULT_RATE = 100;
    public RateLimit getRateLimit(String interfaceName) {
        if (!rateLimitMap.containsKey(interfaceName)) {
            RateLimit rateLimit = new TokenBucketRateLimit(DEFAULT_RATE, DEFAULT_CAPACITY);
            log.info("为接口 [{}] 创建了新的限流策略: {}", interfaceName, rateLimit);
            rateLimitMap.put(interfaceName, rateLimit);
            return rateLimit;
        }
        return rateLimitMap.get(interfaceName);
    }
}
