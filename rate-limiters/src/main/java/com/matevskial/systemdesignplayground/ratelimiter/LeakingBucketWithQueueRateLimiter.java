package com.matevskial.systemdesignplayground.ratelimiter;

import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

class LeakingBucketWithQueueRateLimiter implements RateLimiter {

    private final Queue<MonoSink<?>> queue;
    private final int capacity;
    private final Thread queueConsumerThread;
    private final LeakingBucket leakingBucket;

    public LeakingBucketWithQueueRateLimiter(LeakingBucketParameters parameters) {
        capacity = parameters.getCapacity();
        queue = new LinkedBlockingQueue<>(capacity);
        leakingBucket = new LeakingBucket(parameters.getCapacity(), parameters.getOutflowRateInSeconds());
        queueConsumerThread = new Thread(this::consumeQueue);
        queueConsumerThread.start();
    }

    @Override
    public boolean limit() {
        return false;
    }

    @Override
    public Mono<Void> submitReactive(Mono<Void> m) {
        return Mono.fromCallable(() -> {
            if (queue.size() >= capacity) {
                System.out.println("should rate-limit");
                return Mono.error(new IllegalStateException("rate limited"));
            }

            return Mono.create(queue::add).then(m);
        }).flatMap(m1 -> m1).then();
    }

    private void consumeQueue() {
        while(true) {
            boolean processed = leakingBucket.leakAndProcess();
            while (!processed) {
                processed = leakingBucket.leakAndProcess();
            }

            MonoSink<?> sink = queue.poll();
            if (sink != null) {
                sink.success();
            }
        }
    }
}
