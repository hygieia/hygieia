package com.capitalone.dashboard.cloudwatch.collector;

import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import com.capitalone.dashboard.cloudwatch.model.AwsLogCollectorItem;
import com.capitalone.dashboard.cloudwatch.model.CloudWatchJob;
import com.capitalone.dashboard.cloudwatch.model.Series;
import com.capitalone.dashboard.cloudwatch.repository.AwsLogCollectorItemRepository;
import com.capitalone.dashboard.collector.CollectorTask;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.LogAnalysis;
import com.capitalone.dashboard.model.LogAnalysisMetric;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.LogAnalysizerRepository;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import java.time.chrono.ChronoPeriod;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevegal on 16/06/2018.
 */
@Component
public class AwsCloudwatchLogAnalyzerTask extends CollectorTask<AwsCloudwatchLogAnalyzer>{

    private final BaseCollectorRepository<AwsCloudwatchLogAnalyzer> repository;
    private final AwsLogCollectorItemRepository jobRepo;
    private final LogAnalysizerRepository metricsRepo;
    private final AwsCloudWatchClientFactory factory;
    private final AwsCloudwatchLogAnalyzerSettings settings;

    public AwsCloudwatchLogAnalyzerTask(TaskScheduler taskScheduler, BaseCollectorRepository<AwsCloudwatchLogAnalyzer> repository, AwsCloudWatchClientFactory factory, AwsLogCollectorItemRepository jobRepo,LogAnalysizerRepository metricsRepo,AwsCloudwatchLogAnalyzerSettings settings){
        super(taskScheduler,"AwsCloudwatchAnalyzerTask");
        this.repository = repository;
        this.factory = factory;
        this.jobRepo = jobRepo;
        this.metricsRepo = metricsRepo;
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
        List<CloudWatchJob> jobs = this.settings.getJobs();
        AWSLogsClient client = this.factory.getInstance();
        for (CloudWatchJob job: jobs) {
            List<AwsLogCollectorItem> logJobItem = this.jobRepo.findByName(job.getName());
            AwsLogCollectorItem analyzerJob = null;
            if (null==logJobItem || logJobItem.isEmpty()) {
                analyzerJob = new AwsLogCollectorItem();
                analyzerJob.setName(job.getName());
                analyzerJob.setCollectorId(collector.getId());
                this.jobRepo.save(analyzerJob);
            } else {
                analyzerJob = logJobItem.get(0);
            }
            List<LogAnalysisMetric> metrics = new ArrayList<>();
            for (Series series : job.getSeries()) {
                FilterLogEventsResult filterLogEventsResult = client.filterLogEvents(buildLogEvent(series));
                if (null!=filterLogEventsResult ) {
                    int numberOfEvents = filterLogEventsResult.getEvents().size();
                    LogAnalysisMetric metric = new LogAnalysisMetric();
                    metric.setName(series.getName());
                    metric.setValue(numberOfEvents);
                    metrics.add(metric);
                }
            }
            LogAnalysis logAnalysis = new LogAnalysis();
            logAnalysis.setCollectorItemId(analyzerJob.getId());
            logAnalysis.setName(job.getName());
            logAnalysis.setTimestamp(System.currentTimeMillis());
            logAnalysis.addMetrics(metrics);
            this.metricsRepo.save(logAnalysis);
        }
    }

    private FilterLogEventsRequest buildLogEvent(Series job) {
        long currentTime = System.currentTimeMillis();
        FilterLogEventsRequest filter = new FilterLogEventsRequest();
        filter.setLogGroupName(job.getLogGroupName());
        filter.setLogStreamNames(job.getLogStreams());
        filter.setFilterPattern(job.getFilterPattern());
        filter.setEndTime(currentTime);
        filter.setStartTime(currentTime-(5*60*1000));
        return filter;
    }
}
