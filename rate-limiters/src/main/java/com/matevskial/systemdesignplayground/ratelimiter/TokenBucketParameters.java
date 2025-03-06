package com.matevskial.systemdesignplayground.ratelimiter;

import lombok.Builder;
import lombok.Value;

import java.util.concurrent.TimeUnit;

@Value
@Builder
public class TokenBucketParameters {
    int capacity;
    int refillAmount;
    int refillTimePeriod;
    TimeUnit refillTimeUnit;
}
