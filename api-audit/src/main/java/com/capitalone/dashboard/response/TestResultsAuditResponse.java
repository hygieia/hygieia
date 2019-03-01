package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.status.TestResultAuditStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TestResultsAuditResponse extends AuditReviewResponse<TestResultAuditStatus> {
    private String url;
    private long lastExecutionTime;

    private String type;

    private HashMap featureTestResult = new HashMap();

    private Collection<TestCapability> testCapabilities;

    private int totalStoryCount;

    private double threshold;

    private double percentTraceability;

    private List<HashMap> totalStories = new ArrayList<HashMap>();

    private List<String> totalCompletedStories = new ArrayList<>();

    public Collection<TestCapability> getTestCapabilities() {
        return testCapabilities;
    }

    public void setTestCapabilities(Collection<TestCapability> testCapabilities) {
        this.testCapabilities = testCapabilities;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    public void setLastExecutionTime(long lastExecutionTime) {
        this.lastExecutionTime = lastExecutionTime;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public HashMap getFeatureTesResult() { return featureTestResult; }

    public void setFeatureTestResult(HashMap featureTestResult) { this.featureTestResult = featureTestResult; }

    public List<HashMap> getTotalStories() {
        return totalStories;
    }

    public void setTotalStories(List<HashMap> totalStories) {
        this.totalStories = totalStories;
    }


    public int getTotalStoryCount() {
        return totalStoryCount;
    }

    public void setTotalStoryCount(int totalStoryCount) {
        this.totalStoryCount = totalStoryCount;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getPercentTraceability() {
        return percentTraceability;
    }

    public void setPercentTraceability(double percentTraceability) {
        this.percentTraceability = percentTraceability;
    }

    public List<String> getTotalCompletedStories() {
        return totalCompletedStories;
    }

    public void setTotalCompletedStories(List<String> totalCompletedStories) {
        this.totalCompletedStories = totalCompletedStories;
    }
}