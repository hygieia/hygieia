package com.capitalone.dashboard.cloudwatch.collector;

import com.capitalone.dashboard.cloudwatch.model.CloudWatchJob;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevegal on 16/06/2018.
 */
@Component
@ConfigurationProperties(prefix = "cloudwatch")
public class AwsCloudwatchLogAnalyzerSettings {

    private String cron;

    private List<CloudWatchJob> jobs = new ArrayList<>();

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public void addJob(CloudWatchJob job) {
        this.jobs.add(job);
    }
}
