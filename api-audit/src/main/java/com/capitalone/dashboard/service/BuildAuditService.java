package com.capitalone.dashboard.service;

import com.capitalone.dashboard.response.JobReviewResponse;

public interface BuildAuditService {

    JobReviewResponse getBuildJobReviewResponse(String jobUrl, String jobName, long beginDt, long endDt);
}
