package com.matevskial.systemdesignplayground.apigateway.filter.ratelimiter.tokenbucket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TokenBucketRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenBucketRateLimiterGatewayFilterFactory.Config> {

    public TokenBucketRateLimiterGatewayFilterFactory() {
        super(TokenBucketRateLimiterGatewayFilterFactory.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        TokenBucketRateLimiterParameters parameters = TokenBucketRateLimiterParameters.builder()
                .capacity(config.getCapacity())
                .refillAmount(config.getRefillAmount())
                .refillTimePeriod(config.getRefillTimePeriod())
                .refillTimeUnit(config.getRefillTimeUnit())
                .build();
        return switch (config.getKind()) {
            case USING_LIBRARY, CUSTOM -> new TokenBucketRateLimiterUsingLibrary(parameters);
        };
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Config {
        private Kind kind = Kind.USING_LIBRARY;
        private int capacity;
        private int refillAmount;
        private int refillTimePeriod;
        private TimeUnit refillTimeUnit;
    }

    public enum Kind {
        USING_LIBRARY,
        CUSTOM
    }
}
