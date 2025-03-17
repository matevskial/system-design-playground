package com.matevskial.systemdesignplayground.ratelimiter;

import reactor.core.publisher.Mono;

public interface RateLimiter {
    boolean limit();
    Mono<Void> submitReactive(Mono<Void> m);
}
