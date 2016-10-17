package com.capitalone.dashboard.collecteur;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by benathmane on 23/06/16.
 */

/**
 * Bean to hold settings specific to the Gitlab collector.
 */

@Component
@ConfigurationProperties(prefix = "gitlab")
public class GitlabSettings {
    private String cron;
    private boolean saveLog = false;
    private String server;
    private String apiToken;
    private String branche;

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

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getBranche() {
        return branche;
    }

    public void setBranche(String branche) {
        this.branche = branche;
    }
}
