package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.AuditType;
import com.capitalone.dashboard.response.DashboardReviewResponse;

import java.util.HashSet;
import java.util.List;

public interface DashboardAuditService {

    DashboardReviewResponse getDashboardReviewResponse(String dashboardTitle, String dashboardType, String businessService, String businessApp, long beginDate, long endDate, HashSet<AuditType> auditTypes) throws HygieiaException;
}
