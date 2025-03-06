package com.matevskial.systemdesignplayground.ratelimiter.tokenbucket;

import com.matevskial.systemdesignplayground.ratelimiter.core.RateLimiter;

public class TokenBucketRateLimiter implements RateLimiter {

    @Override
    public boolean limit() {
        return false;
    }
}
