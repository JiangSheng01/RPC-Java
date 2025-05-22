package com.jiangsheng.rpc.server.ratelimit.impl;

import com.jiangsheng.rpc.server.ratelimit.RateLimit;

public class TokenBucketRateLimit implements RateLimit {
    private static int RATE;
    private static int CAPACITY;
    private volatile int curCapacity;
    private volatile long timeStamp = System.currentTimeMillis();

    public TokenBucketRateLimit(int rate, int capacity) {
        RATE = rate;
        CAPACITY = capacity;
        curCapacity = capacity;
    }

    @Override
    public boolean getToken() {
        if (curCapacity > 0) {
            curCapacity--;
            return true;
        }

        long current = System.currentTimeMillis();

        if (current - timeStamp >= RATE) {
            if ((current - timeStamp) / RATE >= 2) {
                curCapacity += (int) ((current - timeStamp) / RATE) - 1;
            }
            if (curCapacity > CAPACITY) {
                curCapacity = CAPACITY;
            }
            timeStamp = current;
            return true;
        }
        return false;
    }
}
