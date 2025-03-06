package com.matevskial.systemdesignplayground.apigateway.filter.ratelimiter.tokenbucket;

import com.matevskial.systemdesignplayground.ratelimiter.core.RateLimiter;
import com.matevskial.systemdesignplayground.ratelimiter.tokenbucket.TokenBucketRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
class TokenBucketRateLimiterUsingCustomImplementation implements GatewayFilter {

    private final RateLimiter rateLimiter;

    public TokenBucketRateLimiterUsingCustomImplementation(TokenBucketRateLimiterParameters parameters) {
        rateLimiter = new TokenBucketRateLimiter();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("[{}] Filter executing", this.getClass().getSimpleName());
        boolean limited = rateLimiter.limit();
        if (limited) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
