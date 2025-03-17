package com.matevskial.systemdesignplayground.apigateway.filter.ratelimiter.leakingbucket;

import com.matevskial.systemdesignplayground.ratelimiter.LeakingBucketParameters;
import com.matevskial.systemdesignplayground.ratelimiter.RateLimiter;
import com.matevskial.systemdesignplayground.ratelimiter.RateLimiters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
class LeakingBucketRateLimiterUsingCustomImplementation implements GatewayFilter {

    private final RateLimiter rateLimiter;

    public LeakingBucketRateLimiterUsingCustomImplementation(LeakingBucketRateLimiterParameters parameters) {
        rateLimiter = RateLimiters.leakingBucket(LeakingBucketParameters.builder()
                .capacity(parameters.getCapacity())
                .outflowRateInSeconds(parameters.getOutflowRateInSeconds())
                .build()
        );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("[{}] Filter executing", this.getClass().getSimpleName());

        Mono<Void> rateLimited = rateLimiter.submitReactive(chain.filter(exchange));

        return rateLimited.onErrorResume(IllegalStateException.class, e -> {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        });

//        if (limited) {
//            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
//            return exchange.getResponse().setComplete();
//        }

//        return chain.filter(exchange);
    }
}
