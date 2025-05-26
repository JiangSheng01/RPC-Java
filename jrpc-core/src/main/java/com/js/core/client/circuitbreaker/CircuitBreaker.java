package com.js.core.client.circuitbreaker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CircuitBreaker {
    @Getter
    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private AtomicInteger failureCount = new AtomicInteger(0);
    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger requestCount = new AtomicInteger(0);
    private final int failureThreshold;
    private final double halfOpenSuccessRate;
    private final long resetTimePeriod;
    private long lastFailureTime = 0;

    public CircuitBreaker(int failureThreshold, double halfOpenSuccessRate, long resetTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.resetTimePeriod = resetTimePeriod;
    }

    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();
        switch (state) {
            case OPEN:
                if (currentTime - lastFailureTime > resetTimePeriod) {
                    state = CircuitBreakerState.HALF_OPEN;
                    resetCounts();
                    log.info("熔断已解除，进入半开启状态，允许请求通过");
                    return true;
                }
                log.warn("熔断生效中，拒绝请求！");
                return false;
            case HALF_OPEN:
                requestCount.incrementAndGet();
                log.info("当前为半开启状态，计数请求");
                return true;
            case CLOSED:
            default:
                log.info("当前为正常状态，允许请求通过");
                return true;
        }
    }

    public synchronized void recordSuccess() {
        if (state == CircuitBreakerState.HALF_OPEN) {
            successCount.incrementAndGet();
            if (successCount.get() >= halfOpenSuccessRate * requestCount.get()) {
                state = CircuitBreakerState.CLOSED;
                resetCounts();
            }
        } else {
            resetCounts();
        }
    }

    public synchronized void recordFailure() {
        failureCount.incrementAndGet();
        System.out.println("记录失败！！！！！！！失败次数：" + failureCount.get());
        lastFailureTime = System.currentTimeMillis();
        if (state == CircuitBreakerState.HALF_OPEN) {
            state = CircuitBreakerState.OPEN;
            lastFailureTime = System.currentTimeMillis();
        } else if (failureCount.get() >= failureThreshold) {
            state = CircuitBreakerState.OPEN;
        }
    }

    public void resetCounts() {
        failureCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }
}

enum CircuitBreakerState {
    //关闭，开启，半开启
    CLOSED, OPEN, HALF_OPEN
}