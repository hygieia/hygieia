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
    private String username;
    private String apiKey;
    private List<String> servers;
    private List<String> artifactoryEndpoints;
    private List<String> repos;
    
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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
    
    public List<String> getArtifactoryEndpoints() {
        return artifactoryEndpoints;
    }

    public void setArtifactoryEndpoints(List<String> artifactoryEndpoints) {
        this.artifactoryEndpoints = artifactoryEndpoints;
    }
    
    public List<String> getRepos() {
        return repos;
    }

    public void setRepos(List<String> repos) {
        this.repos = repos;
    }
}
