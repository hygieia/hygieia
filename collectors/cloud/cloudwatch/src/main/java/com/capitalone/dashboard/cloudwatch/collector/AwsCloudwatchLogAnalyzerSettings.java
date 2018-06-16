package com.capitalone.dashboard.cloudwatch.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by stevegal on 16/06/2018.
 */
@Component
@ConfigurationProperties(prefix = "cloudwatch")
public class AwsCloudwatchLogAnalyzerSettings {

    private String cron;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
}
