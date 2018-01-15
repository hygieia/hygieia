package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.response.DashboardReviewResponse;

import java.util.Set;

public interface DashboardAuditService {

    DashboardReviewResponse getDashboardReviewResponse(String dashboardTitle, String dashboardType, String businessService, String businessApp, long beginDate, long endDate, Set<AuditType> auditTypes) throws AuditException;
}
