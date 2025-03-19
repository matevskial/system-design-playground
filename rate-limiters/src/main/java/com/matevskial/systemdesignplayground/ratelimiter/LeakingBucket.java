package com.matevskial.systemdesignplayground.ratelimiter;

import java.util.concurrent.TimeUnit;

class LeakingBucket {

    private volatile long lastLeakTimestampInNanos = 0;
    private volatile long waterLevel = 0;

    private final int capacity;
    private final int leakRateInSeconds;

    public LeakingBucket(int capacity, int outflowRateInSeconds) {
        this.capacity = capacity;
        this.leakRateInSeconds = outflowRateInSeconds;
    }

    /**
     * Leaks pending processes and adds new process
     * @return true if capacity of the bucket is NOT filled after leaking and adding the new process
     */
    public synchronized boolean leakAndProcess() {
        leak();
        return process();
    }

    private void leak() {
        long currentTimeInNanos = System.nanoTime();
        if (currentTimeInNanos == 0 || currentTimeInNanos - lastLeakTimestampInNanos > TimeUnit.SECONDS.toNanos(1)) {
            long times = ((currentTimeInNanos - lastLeakTimestampInNanos) / TimeUnit.SECONDS.toNanos(1)) * leakRateInSeconds;
            waterLevel = waterLevel - times;
            if (waterLevel < 0) {
                waterLevel = 0;
            }
            lastLeakTimestampInNanos = currentTimeInNanos;
        }
    }

    private boolean process() {
        boolean processed = true;
        waterLevel++;
        if (waterLevel > capacity) {
            waterLevel = capacity;
            processed = false;
        }
        return processed;
    }
}
