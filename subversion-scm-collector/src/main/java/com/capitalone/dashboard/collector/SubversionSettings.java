package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the Subversion collector.
 */
@Component
@ConfigurationProperties(prefix = "subversion")
public class SubversionSettings {

    private String cron;
    private String username;
    private String password;
    private int commitThresholdDays;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCommitThresholdDays() {
        return commitThresholdDays;
    }

    public void setCommitThresholdDays(int commitThresholdDays) {
        this.commitThresholdDays = commitThresholdDays;
    }
}
