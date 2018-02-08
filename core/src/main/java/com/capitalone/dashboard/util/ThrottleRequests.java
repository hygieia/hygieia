package com.capitalone.dashboard.util;

import com.capitalone.dashboard.model.ThrottleSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ThrottleRequests {
    static final Logger LOGGER = LoggerFactory.getLogger(ThrottleRequests.class);
    /**
     *
     * Makes the thread wait for "waitTime" milliseconds if the number of "requestCount" >= "requestRateLimit"
     * in a span of rates limit time window configured in milliseconds.
     *
     * @param requestCount
     *          Indicates the number of requests made in the given time window
     * @param startTime
     *          Indicates the start time of the current time window
     * @param ts
     *          Object that encapsulates throttle setting:
     *              1. requestRateLimit            : The maximum number of requests allowed in the given time window.
     *              2. requestRateLimitTimeWindow  : The time window within which we want to limit the number of requests made.
     *              3. waitTime                    : The duration of time for which we want to wait if we have already reached the
     *                                               limit "rateLimit" within the "requestRateLimitTimeWindow"
     *
     **/
    default boolean throttleRequests (long startTime, int requestCount, ThrottleSettings ts) {
        boolean result = false;
        // Record Current Time
        long currentTime = System.currentTimeMillis();
        // Time Elapsed
        long timeElapsed = currentTime - startTime;
        if (requestCount >= ts.getRequestRateLimit()) {
            result = true;
            long requestRateLimitTimeWindow = ts.getRequestRateLimitTimeWindow();
            if (timeElapsed <= requestRateLimitTimeWindow) {
                long timeToWait = (timeElapsed < requestRateLimitTimeWindow)? ((requestRateLimitTimeWindow - timeElapsed) + ts.getWaitTime()) : ts.getWaitTime();
                try {
                    LOGGER.debug("Rates limit exceeded: timeElapsed = " +timeElapsed+ "; Rate Count = "+requestCount+ "; waiting for " + timeToWait + " milliseconds");
                    Thread.sleep(timeToWait);
                } catch (InterruptedException ie) {
                    LOGGER.error("Thread Interrupted ", ie);
                }
            }
        }
        return result;
    }
}
