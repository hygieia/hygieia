package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.collector.CollectorTask;
import com.capitalone.dashboard.model.Collector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectorTaskTests {

    @Mock private TaskScheduler taskScheduler;
    @Mock private BaseCollectorRepository<Collector> baseCollectorRepository;

    private CollectorTask<Collector> task;

    private static final String COLLECTOR_NAME = "Test Collector";

    @Before
    public void init() {
        task = new TestCollectorTask();
    }

    @Test
    public void run_collectorNotRegistered_savesNewCollector() {
        Collector c = new Collector();
        when(baseCollectorRepository.findByName(COLLECTOR_NAME)).thenReturn(null);
        when(baseCollectorRepository.save(any(Collector.class))).thenReturn(c);
        task.run();
        verify(baseCollectorRepository).save(any(Collector.class));
    }

    @Test
    public void run_enabled() {
    	Collector c = new Collector();
        c.setEnabled(true);
        long prevLastExecuted = c.getLastExecuted();
        when(baseCollectorRepository.findByName(COLLECTOR_NAME)).thenReturn(c);
        when(baseCollectorRepository.save(any(Collector.class))).thenReturn(c);
        task.run();

        assertThat(c.getLastExecuted(), greaterThan(prevLastExecuted));
        verify(baseCollectorRepository, times(1)).save(c);
    }

    @Test
    public void run_disabled() {
    	Collector c =  new Collector();
        c.setEnabled(false);
        when(baseCollectorRepository.findByName(COLLECTOR_NAME)).thenReturn(c);
        when(baseCollectorRepository.save(any(Collector.class))).thenReturn(c);
        task.run();

        verify(baseCollectorRepository, never()).save(c);
    }

    @Test
    public void onStartup() {
        Collector c = new Collector();
        c.setOnline(false);
        when(baseCollectorRepository.findByName(COLLECTOR_NAME)).thenReturn(c);
        task.onStartup();

        assertThat(c.isOnline(), is(true));
        verify(baseCollectorRepository, times(1)).save(c);
        verify(taskScheduler).schedule(any(TestCollectorTask.class), any(CronTrigger.class));
    }

    @Test
    public void onShutdown() {
        Collector c = new Collector();
        c.setOnline(true);
        when(baseCollectorRepository.findByName(COLLECTOR_NAME)).thenReturn(c);

        task.onShutdown();

        assertThat(c.isOnline(), is(false));
        verify(baseCollectorRepository, times(1)).save(c);
    }

    private final class TestCollectorTask extends CollectorTask<Collector> {

        public TestCollectorTask() {
            super(taskScheduler, COLLECTOR_NAME);
        }

        @Override
        public Collector getCollector() {
            return new Collector();
        }

        @Override
        public BaseCollectorRepository<Collector> getCollectorRepository() {
            return baseCollectorRepository;
        }

        @Override
        public String getCron() {
            return "0 * * * * *";
        }

        @Override
        public void collect(Collector collector) {

        }
    }
}
