package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.AuditStatus;

import java.util.EnumSet;
import java.util.Set;

public class AuditReviewResponse {
    private Set<AuditStatus> auditStatuses = EnumSet.noneOf(AuditStatus.class);

    private String errorMessage = "";

    public void addAuditStatus(AuditStatus status) {
        auditStatuses.add(status);
    }

    public Set<AuditStatus> getAuditStatuses() {
        return auditStatuses;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
