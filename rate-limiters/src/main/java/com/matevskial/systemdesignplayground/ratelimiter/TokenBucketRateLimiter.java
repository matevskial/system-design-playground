package com.matevskial.systemdesignplayground.ratelimiter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class TokenBucketRateLimiter implements RateLimiter {

    private final TokenBucket tokenBucket;

    @Override
    public boolean limit() {
        boolean consumed = tokenBucket.tryConsume();
        return !consumed;
    }
}
