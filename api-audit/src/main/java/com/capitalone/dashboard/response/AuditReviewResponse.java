package com.capitalone.dashboard.response;

import java.util.HashSet;
import java.util.Set;

public class AuditReviewResponse<T> {
    private Set<T> auditStatuses = new HashSet<>();

    private String errorMessage;

    private long lastUpdated;

    public void addAuditStatus(T status) {
        auditStatuses.add(status);
    }

    public Set<T> getAuditStatuses() {
        return auditStatuses;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
