package com.capitalone.dashboard.jenkins;

import com.capitalone.dashboard.model.quality.ArtifactType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "jenkins-codequality")
public class JenkinsSettings {
    private String cron;
    private List<String> servers;
    private Map<ArtifactType, List<String>> artifactRegex = new HashMap<>();
    private String username;
    private String apiKey;
    private String dockerLocalHostIP;
    private int jobDepth;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public Map<ArtifactType, List<String>> getArtifactRegex() {
        return artifactRegex;
    }

    public void setArtifactRegex(ArtifactType type, List<String> artifactRegex) {
        this.artifactRegex.put(type, artifactRegex);
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

    public String getDockerLocalHostIP() {
        return dockerLocalHostIP;
    }

    public void setDockerLocalHostIP(String dockerLocalHostIP) {
        this.dockerLocalHostIP = dockerLocalHostIP;
    }

    public int getJobDepth() {
        return jobDepth;
    }

    public void setJobDepth(int jobDepth) {
        this.jobDepth = jobDepth;
    }
}
