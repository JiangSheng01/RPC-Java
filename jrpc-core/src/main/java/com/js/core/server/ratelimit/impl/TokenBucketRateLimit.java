package com.js.core.server.ratelimit.impl;

import cn.hutool.captcha.generator.MathGenerator;
import com.js.core.server.ratelimit.RateLimit;

public class TokenBucketRateLimit implements RateLimit {
    private final int rate;
    private final int capacity;
    private volatile int curCapacity;
    private volatile long lastTimestamp;

    public TokenBucketRateLimit(int rate, int capacity) {
        this.rate = rate;
        this.capacity = capacity;
        curCapacity = capacity;
        lastTimestamp = System.currentTimeMillis();
    }

    @Override
    public boolean getToken() {
        synchronized (this) {
            if (curCapacity > 0) {
                curCapacity--;
                return true;
            }

            long currentTimestamp = System.currentTimeMillis();

            if (currentTimestamp - lastTimestamp > rate) {
                int generatedTokens = (int) ((currentTimestamp - lastTimestamp) / rate);
                if (generatedTokens > 1) {
                    curCapacity = Math.min(capacity, curCapacity + generatedTokens - 1);
                }
                lastTimestamp = currentTimestamp;
                return true;
            }
            return false;
        }
    }
}
