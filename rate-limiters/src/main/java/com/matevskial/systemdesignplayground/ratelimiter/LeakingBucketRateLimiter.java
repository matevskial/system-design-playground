package com.matevskial.systemdesignplayground.ratelimiter;

import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class LeakingBucketRateLimiter implements RateLimiter {

    private final Queue<MonoSink<?>> queue;
    private final int capacity;
    private final Thread queueConsumerThread;
    private final TokenBucket tokenBucket;

    public LeakingBucketRateLimiter(LeakingBucketParameters parameters) {
        capacity = parameters.getCapacity();
        queue = new LinkedBlockingQueue<>(capacity);
        tokenBucket = new TokenBucket(parameters.getOutflowRateInSeconds(), parameters.getOutflowRateInSeconds(), 1, TimeUnit.SECONDS);
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
            boolean consumed = tokenBucket.tryConsume();
            while (!consumed) {
                consumed = tokenBucket.tryConsume();
            }

            MonoSink<?> sink = queue.poll();
            if (sink != null) {
                sink.success();
            }
        }
    }
}
