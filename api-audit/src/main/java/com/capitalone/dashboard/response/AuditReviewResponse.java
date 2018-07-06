package com.capitalone.dashboard.response;

import java.util.SortedSet;
import java.util.TreeSet;

public class AuditReviewResponse<T> {
    private SortedSet<T> auditStatuses = new TreeSet<>();

    private String errorMessage;

    private long lastUpdated;

    public void addAuditStatus(T status) {
        auditStatuses.add(status);
    }

    public SortedSet<T> getAuditStatuses() {
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
