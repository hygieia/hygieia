package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class CollectorTaskTest {
    @Mock private TaskScheduler taskScheduler;
    @Mock private CollectorTask<Collector> mockCollector;

    private static final String COLLECTOR_NAME = "Test Collector";
    private CollectorTask<Collector> collector;

    @Before
    public void init() {
        collector = new CollectorTaskTest.TestCollectorTask();
    }

    @Test
    public void throttleRequestsTest_ratelimit_exceeded() {
        long startTime = System.currentTimeMillis() - 500;
        int requestCount = 5;
        long waitTime = 500;
        int requestRateLimit = 3;
        long requestRateLimitTimeWindow = 1000;

        CollectorTask<Collector> collectorSpy = Mockito.spy(collector);
        boolean result = collectorSpy.throttleRequests(startTime, requestCount,
                                                waitTime, requestRateLimit,
                                                requestRateLimitTimeWindow);
        assertEquals(true, result);
        verify(collectorSpy, times(1)).sleep(Mockito.anyLong());
    }

    @Test
    public void throttleRequestsTest_ratelimit_not_exceeded() {
        long startTime = System.currentTimeMillis() - 500;
        int requestCount = 2;
        long waitTime = 500;
        int requestRateLimit = 3;
        long requestRateLimitTimeWindow = 1000;

        CollectorTask<Collector> collectorSpy = Mockito.spy(collector);
        boolean result = collectorSpy.throttleRequests(startTime, requestCount,
                waitTime, requestRateLimit,
                requestRateLimitTimeWindow);
        assertEquals(false, result);
        verify(collectorSpy, times(0)).sleep(Mockito.anyLong());
    }

    @Test
    public void throttleRequestsTest_ratelimit_exceeded_with_timeWindow_greaterThan_rateLimitTimeWindow() {
        long startTime = System.currentTimeMillis() - 2000;
        int requestCount = 5;
        long waitTime = 500;
        int requestRateLimit = 3;
        long requestRateLimitTimeWindow = 1000;

        CollectorTask<Collector> collectorSpy = Mockito.spy(collector);
        boolean result = collectorSpy.throttleRequests(startTime, requestCount,
                waitTime, requestRateLimit,
                requestRateLimitTimeWindow);
        assertEquals(true, result);
        verify(collectorSpy, times(0)).sleep(Mockito.anyLong());
    }

    private class TestCollectorTask extends CollectorTask<Collector> {

        public TestCollectorTask() {
            super(taskScheduler, COLLECTOR_NAME);
        }

        @Override
        public Collector getCollector() { return null; }

        @Override
        public BaseCollectorRepository<Collector> getCollectorRepository() { return null; }

        @Override
        public String getCron() { return null; }

        @Override
        public void collect(Collector collector) {}
    }
}