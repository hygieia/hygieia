package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bean to hold settings specific to the Sonar collector.
 */
@Component
@ConfigurationProperties(prefix = "nexusiq")
public class NexusIQSettings {
    private String cron;
    private String username;
    private String password;
    private boolean selectStricterLicense;
    private List<String> servers;

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

    public boolean isSelectStricterLicense() {
        return selectStricterLicense;
    }

    public void setSelectStricterLicense(boolean selectStricterLicense) {
        this.selectStricterLicense = selectStricterLicense;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

}
