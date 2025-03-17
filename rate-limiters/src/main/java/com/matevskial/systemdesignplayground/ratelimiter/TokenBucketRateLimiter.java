package com.matevskial.systemdesignplayground.ratelimiter;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
class TokenBucketRateLimiter implements RateLimiter {

    private final TokenBucket tokenBucket;

    @Override
    public boolean limit() {
        boolean consumed = tokenBucket.tryConsume();
        return !consumed;
    }

    @Override
    public Mono<Void> submitReactive(Mono<Void> m) {
        return Mono.empty();
    }

}
