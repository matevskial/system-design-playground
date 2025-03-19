package com.matevskial.systemdesignplayground.ratelimiter;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LeakingBucketNoQueueRateLimiter implements RateLimiter {

    private final LeakingBucket leakingBucket;

    @Override
    public boolean limit() {
        return !leakingBucket.leakAndProcess();
    }

    @Override
    public Mono<Void> submitReactive(Mono<Void> m) {
        return Mono.empty();
    }
}
