package com.capitalone.dashboard;

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
    private String peerReviewContexts;
    private String peerReviewApprovalText;
    private String serviceAccountOU;
    @Value("${maxDaysRangeForQuery:60}") // 60 days max
    private long maxDaysRangeForQuery;
    private boolean logRequest;
    
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

    public String getPeerReviewContexts() {
        return peerReviewContexts;
    }

    public void setPeerReviewContexts(String peerReviewContexts) {
        this.peerReviewContexts = peerReviewContexts;
    }

    public boolean isLogRequest() {
        return logRequest;
    }

    public void setLogRequest(boolean logRequest) {
        this.logRequest = logRequest;
    }

    public long getMaxDaysRangeForQuery() {
        return maxDaysRangeForQuery;
    }

    public void setMaxDaysRangeForQuery(long maxDaysRangeForQuery) {
        this.maxDaysRangeForQuery = maxDaysRangeForQuery;
    }

    public String getPeerReviewApprovalText() {
        return peerReviewApprovalText;
    }

    public void setPeerReviewApprovalText(String peerReviewApprovalText) {
        this.peerReviewApprovalText = peerReviewApprovalText;
    }

    public String getServiceAccountOU() {
        return serviceAccountOU;
    }

    public void setServiceAccountOU(String serviceAccountOU) {
        this.serviceAccountOU = serviceAccountOU;
    }
}
