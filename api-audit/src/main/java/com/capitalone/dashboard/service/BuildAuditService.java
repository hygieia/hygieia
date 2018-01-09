package com.capitalone.dashboard.service;

import com.capitalone.dashboard.response.BuildAuditResponse;

public interface BuildAuditService {

    BuildAuditResponse getBuildJobReviewResponse(String jobUrl, String jobName, long beginDt, long endDt);
}
