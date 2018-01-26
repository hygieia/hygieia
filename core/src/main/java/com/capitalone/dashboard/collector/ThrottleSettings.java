package com.capitalone.dashboard.collector;

/**
 * Bean to hold throttle settings used to throttle the number
 * of requests made within a specific time window.
 */
public class ThrottleSettings {
    // Time to sleep before making another request
    private long waitTime;
    // Maximum number of requests allowed with in a specific time window.
    private int requestRateLimit;
    // Time Window for which we are throttling the number of requests made.
    private long requestRateLimitTimeWindow;

    public ThrottleSettings (int requestRateLimit, long requestRateLimitTimeWindow, long waitTime) {
        this.requestRateLimit = requestRateLimit;
        this.requestRateLimitTimeWindow = requestRateLimitTimeWindow;
        this.waitTime = waitTime;
    }

    public long getRequestRateLimitTimeWindow() {
        return requestRateLimitTimeWindow;
    }

    public void setRequestRateLimitTimeWindow(long requestRateLimitTimeWindow) {
        this.requestRateLimitTimeWindow = requestRateLimitTimeWindow;
    }

    public int getRequestRateLimit() { return requestRateLimit; }

    public void setRequestRateLimit(int requestRateLimit) { this.requestRateLimit = requestRateLimit; }

    public long getWaitTime() { return waitTime; }

    public void setWaitTime(long waitTime) { this.waitTime = waitTime; }
}

