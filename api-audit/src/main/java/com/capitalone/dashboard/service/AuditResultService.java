package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditResult;
import org.bson.types.ObjectId;

public interface AuditResultService {

    AuditResult findById(ObjectId id);
    Iterable<AuditResult> findByDashboardTitle(String dashboardTitle);
    Iterable<AuditResult> all();
}
