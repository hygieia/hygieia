package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Collector;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Base class for Collector task implementation which provides subclasses with
 * the following:
 *
 * <ol>
 *     <li>Creates a Collector instance the first time the collector runs.</li>
 *     <li>Uses TaskScheduler to schedule the job based on the provided cron when the process starts.</li>
 *     <li>Saves the last execution time on the collector when the collection run finishes.</li>
 *     <li>Sets the collector online/offline when the collector process starts/stops</li>
 * </ol>
 *
 * @param <T> Class that extends Collector
 */
@Component
public abstract class CollectorTask<T extends Collector> implements Runnable {

    private final TaskScheduler taskScheduler;
    private final String collectorName;

    @Autowired
    protected CollectorTask(TaskScheduler taskScheduler, String collectorName) {
        this.taskScheduler = taskScheduler;
        this.collectorName = collectorName;
    }

    @Override
    public final void run() {
        T collector = getCollectorRepository().findByName(collectorName);
        if (collector == null) {
            // Register new collector
            collector = getCollectorRepository().save(getCollector());
        }

        if (collector.isEnabled()) {
            // Do collection run
            collect(collector);

            // Update lastUpdate timestamp in Collector
            collector.setLastExecuted(System.currentTimeMillis());
            getCollectorRepository().save(collector);
        }
    }

    @PostConstruct
    public void onStartup() {
        taskScheduler.schedule(this, new CronTrigger(getCron()));
        setOnline(true);
    }

    @PreDestroy
    public void onShutdown() {
        setOnline(false);
    }

    public abstract T getCollector();
    public abstract BaseCollectorRepository<T> getCollectorRepository();
    public abstract String getCron();
    public abstract void collect(T collector);

    private void setOnline(boolean online) {
        T collector = getCollectorRepository().findByName(collectorName);
        if (collector != null) {
            collector.setOnline(online);
            getCollectorRepository().save(collector);
        }
    }
}
