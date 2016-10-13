package com.capitalone.dashboard.jenkins;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by plv163 on 13/10/2016.
 */
@Component
@ConfigurationProperties(prefix = "jenkins-codequality")
public class JenkinsSettings {
    private String cron;
    private List<String> servers;
    private List<String> artifactRegex = new ArrayList<>();
    private String username;
    private String apiKey;
    private String dockerLocalHostIP;

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

    public List<String> getArtifactRegex() {
        return artifactRegex;
    }

    public void setArtifactRegex(List<String> artifactRegex) {
        this.artifactRegex = artifactRegex;
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
}
