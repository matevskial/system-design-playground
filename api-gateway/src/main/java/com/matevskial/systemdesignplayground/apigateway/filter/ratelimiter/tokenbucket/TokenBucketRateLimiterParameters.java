package com.matevskial.systemdesignplayground.apigateway.filter.ratelimiter.tokenbucket;

import lombok.Builder;
import lombok.Value;

import java.util.concurrent.TimeUnit;

@Value
@Builder
class TokenBucketRateLimiterParameters {

    int capacity;
    int refillAmount;
    int refillTimePeriod;
    TimeUnit refillTimeUnit;
}
