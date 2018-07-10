package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuditException;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.DashboardType;
import com.capitalone.dashboard.response.DashboardReviewResponse;

import java.util.List;
import java.util.Set;

public interface DashboardAuditService {

    DashboardReviewResponse getDashboardReviewResponse(String dashboardTitle, DashboardType dashboardType, String businessService, String businessApp, long beginDate, long endDate, Set<AuditType> auditTypes) throws AuditException;

    List<CollectorItem> getSonarProjects(String name);
}
