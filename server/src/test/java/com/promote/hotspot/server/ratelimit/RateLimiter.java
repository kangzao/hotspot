package com.promote.hotspot.server.ratelimit;

public interface RateLimiter {

    boolean isOverLimit();

    int currentQPS();

    boolean visit();
}