package com.matevskial.systemdesignplayground.apigateway.filter.ratelimiter.tokenbucket;

import org.isomorphism.util.TokenBucket;
import org.isomorphism.util.TokenBuckets;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

class TokenBucketRateLimiterUsingLibrary implements GatewayFilter {

    private final TokenBucket tokenBucket;

    public TokenBucketRateLimiterUsingLibrary(TokenBucketRateLimiterParameters parameters) {
        tokenBucket = TokenBuckets.builder()
                .withCapacity(parameters.getCapacity())
                .withFixedIntervalRefillStrategy(parameters.getRefillAmount(), parameters.getRefillTimePeriod(), parameters.getRefillTimeUnit())
                .build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        boolean consumed = tokenBucket.tryConsume();
        if (consumed) {
            return chain.filter(exchange);
        }
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        return exchange.getResponse().setComplete();
    }
}
