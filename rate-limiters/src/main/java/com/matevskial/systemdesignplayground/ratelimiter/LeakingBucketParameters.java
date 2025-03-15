package com.matevskial.systemdesignplayground.ratelimiter;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LeakingBucketParameters {
    int capacity;
    int outflowRateInSeconds;
}
