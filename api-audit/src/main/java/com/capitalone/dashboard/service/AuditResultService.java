package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditResult;

public interface AuditResultService {
    AuditResult findByDashboardTitle(String dashboardTitle);
    Iterable<AuditResult> all();

}
