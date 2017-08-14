package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.AuditStatus;

import java.util.EnumSet;
import java.util.Set;

public class AuditReviewResponse {
    private Set<AuditStatus> auditStatuses = EnumSet.noneOf(AuditStatus.class);

    public void addAuditStatus(AuditStatus status) {
        auditStatuses.add(status);
    }

    public Set<AuditStatus> getAuditStatuses() {
        return auditStatuses;
    }
}
