package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.Traceability;
import com.capitalone.dashboard.status.TestResultAuditStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TestResultsAuditResponse extends AuditReviewResponse<TestResultAuditStatus> {
    private String url;
    private long lastExecutionTime;
    private Collection<TestCapability> testCapabilities;
    private String type;
    private Map featureTestResult = new HashMap();
    public Traceability traceability;

    public Collection<TestCapability> getTestCapabilities() { return testCapabilities; }

    public void setTestCapabilities(Collection<TestCapability> testCapabilities) { this.testCapabilities = testCapabilities; }

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

    public Map getFeatureTestResult() { return featureTestResult; }

    public void setFeatureTestResult(Map featureTestResult) { this.featureTestResult = featureTestResult; }

    public Traceability getTraceability() { return traceability; }

    public void setTraceability(Traceability traceability) { this.traceability = traceability; }
}