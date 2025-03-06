package com.matevskial.systemdesignplayground.ratelimiter;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
class TokenBucket {

    private final long tokenCapacity;
    private final long tokenRefillAmount;
    private final long tokenRefillTimePeriodInNanos;

    private volatile long tokens = 0;
    private volatile long lastRefillTimestampInNanos = 0;

    public TokenBucket(long tokenCapacity, long tokenRefillAmount, long tokenRefillTimePeriod, TimeUnit tokenRefillTimeUnit) {
        this.tokenCapacity = tokenCapacity;
        this.tokenRefillAmount = tokenRefillAmount;
        this.tokenRefillTimePeriodInNanos = tokenRefillTimeUnit.toNanos(tokenRefillTimePeriod);
    }

    public synchronized boolean tryConsume() {
        refill();
        if (tokens > 0) {
            tokens--;
            if (tokens < 0) {
                tokens = 0;
            }
            return true;
        }
        return false;
    }

    private void refill() {
        long currentTimeInNanos = System.nanoTime();
        if (lastRefillTimestampInNanos == 0 || currentTimeInNanos - lastRefillTimestampInNanos >= tokenRefillTimePeriodInNanos) {
            long times = (currentTimeInNanos - lastRefillTimestampInNanos) / tokenRefillTimePeriodInNanos;
            tokens = tokens + times * tokenRefillAmount;
            if (tokens > tokenCapacity) {
                tokens = tokenCapacity;
            }
            lastRefillTimestampInNanos = currentTimeInNanos;
        }
    }
}
