package com.capitalone.dashboard.cloudwatch.collector;

import com.amazonaws.services.logs.AWSLogsClient;
import com.amazonaws.services.logs.model.FilterLogEventsRequest;
import com.amazonaws.services.logs.model.FilterLogEventsResult;
import com.capitalone.dashboard.cloudwatch.model.AwsLogCollectorItem;
import com.capitalone.dashboard.cloudwatch.model.CloudWatchJob;
import com.capitalone.dashboard.cloudwatch.model.Series;
import com.capitalone.dashboard.repository.AwsLogCollectorItemRepository;
import com.capitalone.dashboard.collector.CollectorTask;
import com.capitalone.dashboard.model.LogAnalysis;
import com.capitalone.dashboard.model.LogAnalysisMetric;
import com.capitalone.dashboard.repository.BaseCollectorRepository;
import com.capitalone.dashboard.repository.LogAnalysizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AwsCloudwatchLogAnalyzerTask extends CollectorTask<AwsCloudwatchLogAnalyzer>{

    private final BaseCollectorRepository<AwsCloudwatchLogAnalyzer> repository;
    private final AwsLogCollectorItemRepository jobRepo;
    private final LogAnalysizerRepository metricsRepo;
    private final AwsCloudWatchClientFactory factory;
    private final AwsCloudwatchLogAnalyzerSettings settings;

    @Autowired
    public AwsCloudwatchLogAnalyzerTask(TaskScheduler taskScheduler, BaseCollectorRepository<AwsCloudwatchLogAnalyzer> repository, AwsCloudWatchClientFactory factory, AwsLogCollectorItemRepository jobRepo,LogAnalysizerRepository metricsRepo,AwsCloudwatchLogAnalyzerSettings settings){
        super(taskScheduler,AwsCloudwatchLogAnalyzer.COLLECTOR_NAME);
        this.repository = repository;
        this.factory = factory;
        this.jobRepo = jobRepo;
        this.metricsRepo = metricsRepo;
        this.settings = settings;
        this.factory.setup(settings);
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
        int jobCount=0;
        int metricCount=0;
        for (CloudWatchJob job: jobs) {
            jobCount++;
            List<AwsLogCollectorItem> logJobItem = this.jobRepo.findByCollectorIdAndDescription(collector.getId(),job.getName());
            AwsLogCollectorItem analyzerJob = null;
            if (null==logJobItem || logJobItem.isEmpty()) {
                analyzerJob = new AwsLogCollectorItem();
                analyzerJob.setCollectorId(collector.getId());
                analyzerJob.setNiceName(job.getName());
                analyzerJob.setDescription(job.getName());
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
                    metricCount++;
                }
            }
            LogAnalysis logAnalysis = new LogAnalysis();
            logAnalysis.setCollectorItemId(analyzerJob.getId());
            logAnalysis.setName(job.getName());
            logAnalysis.setTimestamp(System.currentTimeMillis());
            logAnalysis.addMetrics(metrics);
            this.metricsRepo.save(logAnalysis);
        }
        log("job Count", System.currentTimeMillis(), jobCount);
        log("metric Count", System.currentTimeMillis(), metricCount);
    }

    private FilterLogEventsRequest buildLogEvent(Series job) {
        long currentTime = System.currentTimeMillis();
        FilterLogEventsRequest filter = new FilterLogEventsRequest();
        filter.setLogGroupName(job.getLogGroupName());
        filter.setLogStreamNames(job.getLogStreams());
        filter.setFilterPattern(job.getFilterPattern());
        filter.setEndTime(currentTime);
        filter.setStartTime(currentTime-(this.settings.getLogAnalysisPeriod()*60*1000));
        return filter;
    }
}
