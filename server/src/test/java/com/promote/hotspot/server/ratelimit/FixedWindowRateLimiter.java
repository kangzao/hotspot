package com.promote.hotspot.server.ratelimit;

import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter implements RateLimiter {

    private Long lastVisitAt = System.currentTimeMillis();

    private static final int DEFAULT_ALLOWED_VISIT_PER_SECOND = 5;

    private final int maxVisitPerSecond;

    private AtomicInteger count;

    public FixedWindowRateLimiter(int maxVisitPerSecond) {
        this.maxVisitPerSecond = maxVisitPerSecond;
        this.count = new AtomicInteger();
    }

    public FixedWindowRateLimiter() {
        this(DEFAULT_ALLOWED_VISIT_PER_SECOND);
    }

    @Override
    public boolean isOverLimit() {
        return count.get() >= maxVisitPerSecond;
    }

    @Override
    public int currentQPS() {
        return count.get();
    }

    @Override
    public boolean visit() {
        long now = System.currentTimeMillis();
        synchronized (lastVisitAt) {
            if (now - lastVisitAt > 1000) {
                lastVisitAt = now;
                System.out.println(currentQPS());
                count.set(1);
            }
        }
        //如果没有超过限流
        if (!isOverLimit()) {
            count.incrementAndGet();
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        RateLimiter rateLimiter = new FixedWindowRateLimiter();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    rateLimiter.visit();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    rateLimiter.visit();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}