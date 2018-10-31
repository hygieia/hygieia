package com.capitalone.dashboard.settings;

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
    private String gitHubWebHook;
    @Value("${githubWebhookEnabled:false}")
    private boolean githubWebhookEnabled;

    public boolean isGithubWebhookEnabled() { return githubWebhookEnabled; }

    public void setGithubWebhookEnabled(boolean githubWebhookEnabled) { this.githubWebhookEnabled = githubWebhookEnabled; }

    public String getGitHubWebHook() { return gitHubWebHook; }

    public void setGitHubWebHook(String gitHubWebHook) { this.gitHubWebHook = gitHubWebHook; }

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
}
