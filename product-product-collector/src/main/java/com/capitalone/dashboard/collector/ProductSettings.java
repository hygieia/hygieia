package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "product")
public class ProductSettings {

    private int commitDateThreshold;
    private String cron;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getCommitDateThreshold() {
        return commitDateThreshold;
    }

    public void setCommitDateThreshold(int commitDateThreshold) {
        this.commitDateThreshold = commitDateThreshold;
    }
}
