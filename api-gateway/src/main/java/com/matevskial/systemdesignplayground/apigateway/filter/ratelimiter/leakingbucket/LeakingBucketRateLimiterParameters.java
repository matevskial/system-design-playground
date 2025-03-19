package com.matevskial.systemdesignplayground.apigateway.filter.ratelimiter.leakingbucket;

import com.matevskial.systemdesignplayground.ratelimiter.LeakingBucketType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
class LeakingBucketRateLimiterParameters {

    int capacity;
    int outflowRateInSeconds;
    LeakingBucketType type;
}
