package com.capitalone.dashboard.settings;

import com.capitalone.dashboard.webhook.settings.WebHookSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class ApiSettings {
    /**
     * TODO The property name 'key' is too vague. This key is used only for encryption. Would suggest to rename it to
     * encryptionKey to be specific. For now (for backwards compatibility) keeping it as it was.
     */
    private String key;
    @Value("${corsEnabled:false}")
    private boolean corsEnabled;
    private String corsWhitelist;
    private boolean logRequest;
    @Value("${pageSize:10}")
    private int pageSize;
    @Value("${lookupDashboardForBuildDataCreate:false}")
    private boolean lookupDashboardForBuildDataCreate;

    @Value("${gitToolName:Github}")
    private String gitToolName;

    private WebHookSettings webHook;

    private String capturePattern;

    public WebHookSettings getWebHook() {
        return webHook;
    }

    public void setWebHook(WebHookSettings webHook) {
        this.webHook = webHook;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public boolean isCorsEnabled() {
        return corsEnabled;
    }

    public void setCorsEnabled(boolean corsEnabled) {
        this.corsEnabled = corsEnabled;
    }

    public String getCorsWhitelist() {
        return corsWhitelist;
    }

    public void setCorsWhitelist(String corsWhitelist) {
        this.corsWhitelist = corsWhitelist;
    }

    public boolean isLogRequest() {
        return logRequest;
    }

    public void setLogRequest(boolean logRequest) {
        this.logRequest = logRequest;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isLookupDashboardForBuildDataCreate() { return lookupDashboardForBuildDataCreate; }

    public void setLookupDashboardForBuildDataCreate(boolean lookupDashboardForBuildDataCreate) {
        this.lookupDashboardForBuildDataCreate = lookupDashboardForBuildDataCreate;
    }

    public String getGitToolName() {
        return gitToolName;
    }

    public void setGitToolName(String gitToolName) {
        this.gitToolName = gitToolName;
    }

    public String getCapturePattern() {
        return capturePattern;
    }

    public void setCapturePattern(String capturePattern) {
        this.capturePattern = capturePattern;
    }
}
