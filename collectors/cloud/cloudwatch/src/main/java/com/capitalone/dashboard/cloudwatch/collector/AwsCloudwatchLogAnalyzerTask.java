package com.capitalone.dashboard.cloudwatch.collector;

import com.capitalone.dashboard.collector.CollectorTask;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 * Created by stevegal on 16/06/2018.
 */
@Component
public class AwsCloudwatchLogAnalyzerTask extends CollectorTask<AwsCloudwatchLogAnalyzer>{

    private final BaseCollectorRepository<AwsCloudwatchLogAnalyzer> repository;
    private final AwsCloudWatchClientFactory factory;
    private final AwsCloudwatchLogAnalyzerSettings settings;

    public AwsCloudwatchLogAnalyzerTask(TaskScheduler taskScheduler, BaseCollectorRepository<AwsCloudwatchLogAnalyzer> repository, AwsCloudWatchClientFactory factory, AwsCloudwatchLogAnalyzerSettings settings){
        super(taskScheduler,"AwsCloudwatchAnalyzerTask");
        this.repository = repository;
        this.factory = factory;
        this.settings = settings;
    }

    @Override
    public AwsCloudwatchLogAnalyzer getCollector() {
        return  AwsCloudwatchLogAnalyzer.prototype();
    }

    @Override
    public BaseCollectorRepository<AwsCloudwatchLogAnalyzer> getCollectorRepository() {
        return this.repository;
    }

    @Override
    public String getCron() {
        return this.settings.getCron();
    }

    @Override
    public void collect(AwsCloudwatchLogAnalyzer collector) {

    }
}
