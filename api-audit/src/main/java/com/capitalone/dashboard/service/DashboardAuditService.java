package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.response.DashboardReviewResponse;

public interface DashboardAuditService {

    DashboardReviewResponse getDashboardReviewResponse(String title, String type, String busServ, String busApp, long beginDate, long endDate) throws HygieiaException;

}
