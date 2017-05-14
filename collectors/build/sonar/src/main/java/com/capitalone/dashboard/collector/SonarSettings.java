package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the Sonar collector.
 */
@Component
@ConfigurationProperties(prefix = "sonar")
public class SonarSettings {
    private String cron;
    private String username;
    private String password;
    private List<String> servers;
    private List<Double> versions;
    private List<String> metrics;

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

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public List<Double> getVersions() {
        return versions;
    }

    public void setVersions(List<Double> versions) {
        this.versions = versions;
    }
}
