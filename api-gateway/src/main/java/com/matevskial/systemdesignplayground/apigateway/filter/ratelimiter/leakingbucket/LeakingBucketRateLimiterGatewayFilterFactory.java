package com.matevskial.systemdesignplayground.apigateway.filter.ratelimiter.leakingbucket;

import com.matevskial.systemdesignplayground.ratelimiter.LeakingBucketType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class LeakingBucketRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<LeakingBucketRateLimiterGatewayFilterFactory.Config> {

    public LeakingBucketRateLimiterGatewayFilterFactory() {
        super(LeakingBucketRateLimiterGatewayFilterFactory.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        LeakingBucketRateLimiterParameters parameters = LeakingBucketRateLimiterParameters.builder()
                .capacity(config.getCapacity())
                .outflowRateInSeconds(config.getOutflowRateInSeconds())
                .type(config.getType())
                .build();

        if (parameters.getType() == LeakingBucketType.QUEUE) {
            return new LeakingBucketRateLimiterUsingQueueImplementation(parameters);
        }

        return new LeakingBucketRateLimiterUsingNonQueueImplementation(parameters);
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Config {
        private int capacity;
        private int outflowRateInSeconds;
        private LeakingBucketType type;
    }
}
