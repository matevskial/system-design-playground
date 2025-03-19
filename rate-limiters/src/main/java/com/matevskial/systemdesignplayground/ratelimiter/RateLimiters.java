package com.matevskial.systemdesignplayground.ratelimiter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RateLimiters {

    public static RateLimiter tokenBucket(TokenBucketParameters tokenBucketParameters) {
        TokenBucket tokenBucket = new TokenBucket(
                tokenBucketParameters.getCapacity(),
                tokenBucketParameters.getRefillAmount(),
                tokenBucketParameters.getRefillTimePeriod(),
                tokenBucketParameters.getRefillTimeUnit()
        );
        return new TokenBucketRateLimiter(tokenBucket);
    }

    public static RateLimiter leakingBucket(LeakingBucketParameters leakingBucketParameters) {
        if (leakingBucketParameters.getType() == LeakingBucketType.QUEUE) {
            return new LeakingBucketWithQueueRateLimiter(leakingBucketParameters);
        }
        LeakingBucket leakingBucket = new LeakingBucket(leakingBucketParameters.getCapacity(), leakingBucketParameters.getOutflowRateInSeconds());
        return new LeakingBucketNoQueueRateLimiter(leakingBucket);
    }
}
