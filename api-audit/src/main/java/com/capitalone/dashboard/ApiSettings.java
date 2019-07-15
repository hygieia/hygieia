package com.capitalone.dashboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

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
    private List<String> serviceAccountOU;
    private String commitLogIgnoreAuditRegEx;
    @Value("${maxDaysRangeForQuery:60}") // 60 days max
    private long maxDaysRangeForQuery;
    private boolean logRequest;
    private String featureIDPattern;
    @Value("${traceabilityThreshold:80.0}")
    private double traceabilityThreshold;
    @Value("${testResultThreshold:95.0}")
    private double testResultThreshold;
    private List<String> validStoryStatus;
    @Value("${testResultSuccessPriority:Low}")
    public String testResultSuccessPriority;
    @Value("${testResultFailurePriority:High}")
    public String testResultFailurePriority;
    @Value("${testResultSkippedPriority:High}")
    public String testResultSkippedPriority;
    private String serviceAccountRegEx;
    @Value("${highSecurityVulnerabilitiesAge:0}")
    private int highSecurityVulnerabilitiesAge;
    @Value("${criticalSecurityVulnerabilitiesAge:0}")
    private int criticalSecurityVulnerabilitiesAge;
    @Value("${highLicenseVulnerabilitiesAge:0}")
    private int highLicenseVulnerabilitiesAge;
    @Value("${criticalLicenseVulnerabilitiesAge:0}")
    private int criticalLicenseVulnerabilitiesAge;
    private String buildStageRegEx;

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

    public List<String> getServiceAccountOU() {
        return serviceAccountOU;
    }

    public void setServiceAccountOU(List<String> serviceAccountOU) {
        this.serviceAccountOU = serviceAccountOU;
    }

    public String getFeatureIDPattern() {
        return featureIDPattern;
    }

    public void setFeatureIDPattern(String featureIDPattern) {
        this.featureIDPattern = featureIDPattern;
    }

    public double getTraceabilityThreshold() {
        return traceabilityThreshold;
    }

    public void setTraceabilityThreshold(double traceabilityThreshold) {
        this.traceabilityThreshold = traceabilityThreshold;
    }

    public List<String> getValidStoryStatus() {
        return validStoryStatus;
    }

    public void setValidStoryStatus(List<String> validStoryStatus) {
        this.validStoryStatus = validStoryStatus;
    }

    public String getCommitLogIgnoreAuditRegEx() {
        return commitLogIgnoreAuditRegEx;
    }

    public void setCommitLogIgnoreAuditRegEx(String commitLogIgnoreAuditRegEx) {
        this.commitLogIgnoreAuditRegEx = commitLogIgnoreAuditRegEx;
    }

    public String getServiceAccountRegEx() {
        return serviceAccountRegEx;
    }

    public void setServiceAccountRegEx(String serviceAccountRegEx) {
        this.serviceAccountRegEx = serviceAccountRegEx;
    }

    public void setTestResultSuccessPriority(String testResultSuccessPriority) {
        this.testResultSuccessPriority = testResultSuccessPriority;
    }

    public String getTestResultSuccessPriority() {
        return testResultSuccessPriority;
    }

    public void setTestResultFailurePriority(String testResultFailurePriority) {
        this.testResultFailurePriority = testResultFailurePriority;
    }

    public void setTestResultSkippedPriority(String testResultSkippedPriority) {
        this.testResultSkippedPriority = testResultSkippedPriority;
    }

    public String getTestResultFailurePriority() {
        return testResultFailurePriority;
    }

    public String getTestResultSkippedPriority() {
        return testResultSkippedPriority;
    }

    public double getTestResultThreshold() {
        return testResultThreshold;
    }

    public void setTestResultThreshold(double testResultThreshold) {
        this.testResultThreshold = testResultThreshold;
    }

    public int getHighSecurityVulnerabilitiesAge() {
        return highSecurityVulnerabilitiesAge;
    }

    public void setHighSecurityVulnerabilitiesAge(int highSecurityVulnerabilitiesAge) {
        this.highSecurityVulnerabilitiesAge = highSecurityVulnerabilitiesAge;
    }

    public int getCriticalSecurityVulnerabilitiesAge() {
        return criticalSecurityVulnerabilitiesAge;
    }

    public void setCriticalSecurityVulnerabilitiesAge(int criticalSecurityVulnerabilitiesAge) {
        this.criticalSecurityVulnerabilitiesAge = criticalSecurityVulnerabilitiesAge;
    }

    public int getHighLicenseVulnerabilitiesAge() {
        return highLicenseVulnerabilitiesAge;
    }

    public void setHighLicenseVulnerabilitiesAge(int highLicenseVulnerabilitiesAge) {
        this.highLicenseVulnerabilitiesAge = highLicenseVulnerabilitiesAge;
    }

    public int getCriticalLicenseVulnerabilitiesAge() {
        return criticalLicenseVulnerabilitiesAge;
    }

    public void setCriticalLicenseVulnerabilitiesAge(int criticalLicenseVulnerabilitiesAge) {
        this.criticalLicenseVulnerabilitiesAge = criticalLicenseVulnerabilitiesAge;
    }

    public String getBuildStageRegEx() {
        return buildStageRegEx;
    }

    public void setBuildStageRegEx(String buildStageRegEx) {
        this.buildStageRegEx = buildStageRegEx;
    }


}