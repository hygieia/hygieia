package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Bean to hold settings specific to the Audit Collector.
 */
@Component
@ConfigurationProperties(prefix = "nfrr")
public class AuditSettings {

    private String cron;
    private boolean saveLog;
    private List<String> servers;
    private List<String> environments;
    private List<String> usernames;
    private List<String> apiKeys;
    private String dockerLocalHostIP; //null if not running in docker on http://localhost
    private int days;

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public boolean isSaveLog() {
        return saveLog;
    }

    public void setSaveLog(boolean saveLog) {
        this.saveLog = saveLog;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
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

    public List<String> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<String> environments) {
        this.environments = environments;
    }

    //Docker NATs the real host localhost to 10.0.2.2 when running in docker
    //as localhost is stored in the JSON payload from jenkins we need
    //this hack to fix the addresses
    public String getDockerLocalHostIP() {

        //we have to do this as spring will return NULL if the value is not set vs and empty string
        String localHostOverride = "";
        if (dockerLocalHostIP != null) {
            localHostOverride = dockerLocalHostIP;
        }
        return localHostOverride;
    }

    public void setDockerLocalHostIP(String dockerLocalHostIP) {
        this.dockerLocalHostIP = dockerLocalHostIP;
    }
}


