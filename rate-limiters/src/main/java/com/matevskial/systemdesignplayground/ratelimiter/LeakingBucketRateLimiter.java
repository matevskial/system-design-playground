package com.matevskial.systemdesignplayground.ratelimiter;

class LeakingBucketRateLimiter implements RateLimiter {

    public LeakingBucketRateLimiter(LeakingBucketParameters parameters) {

    }

    @Override
    public boolean limit() {
        return false;
    }
}
