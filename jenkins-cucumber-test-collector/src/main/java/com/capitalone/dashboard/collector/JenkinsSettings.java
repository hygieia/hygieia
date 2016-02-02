package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Bean to hold settings specific to the Jenkins collector.
 *
 * Created by ltm688 on 2/12/15.
 */
@Component
@ConfigurationProperties(prefix = "jenkins-cucumber")
public class JenkinsSettings {

    private String cron;
    private List<String> servers;
    private String cucumberJsonRegex = "cucumber.json";
    private String username;
    private String apiKey;

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

    public String getCucumberJsonRegex() {
        return cucumberJsonRegex;
    }

    public void setCucumberJsonRegex(String cucumberJsonRegex) {
        this.cucumberJsonRegex = cucumberJsonRegex;
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
}
