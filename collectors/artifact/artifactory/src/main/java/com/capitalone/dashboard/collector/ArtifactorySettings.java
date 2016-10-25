package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the Artifactory collector.
 */
@Component
@ConfigurationProperties(prefix = "artifactory")
public class ArtifactorySettings {
    private String cron;
    private List<String> usernames;
    private List<String> apiKeys;
    private List<String> servers;
    private List<String[]> repos;
    private String endpoint;
    
    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public List<String> getApiKeys() {
        return apiKeys;
    }

    public void setApiKeys(List<String> apiKeys) {
        this.apiKeys = apiKeys;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
    
    public List<String[]> getRepos() {
        return repos;
    }

    public void setRepos(List<String[]> repos) {
        this.repos = repos;
    }
    
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
