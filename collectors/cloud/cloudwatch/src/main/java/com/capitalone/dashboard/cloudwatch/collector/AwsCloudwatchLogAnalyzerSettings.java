package com.capitalone.dashboard.cloudwatch.collector;

import com.amazonaws.regions.Regions;
import com.capitalone.dashboard.cloudwatch.model.CloudWatchJob;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "cloudwatch")
public class AwsCloudwatchLogAnalyzerSettings {

    private String cron;

    private String profile;

    private String proxyHost;
    private String proxyPort;
    private String nonProxy;

    private Regions region;

    private int logAnalysisPeriod;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

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

    public List<CloudWatchJob> getJobs() {
        return jobs;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getNonProxy() {
        return nonProxy;
    }

    public void setNonProxy(String nonProxy) {
        this.nonProxy = nonProxy;
    }

    public Regions getRegion() {
        return region;
    }

    public void setRegion(Regions region) {
        this.region = region;
    }

    public int getLogAnalysisPeriod() {
        return logAnalysisPeriod;
    }

    public void setLogAnalysisPeriod(int logAnalysisPeriod) {
        this.logAnalysisPeriod = logAnalysisPeriod;
    }
}
