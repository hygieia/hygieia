package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditResult;
import org.bson.types.ObjectId;

public interface AuditResultService {

    AuditResult getAuditResult(ObjectId id);
    Iterable<AuditResult> getAuditResultsByTitle(String dashboardTitle);
    Iterable<AuditResult> getAuditResults();
}
