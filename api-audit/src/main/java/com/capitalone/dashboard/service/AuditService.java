package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.response.BuildAuditResponse;
import com.capitalone.dashboard.response.CodeReviewAuditResponse;
import com.capitalone.dashboard.response.QualityProfileAuditResponse;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.response.PerformaceTestAuditResponse;
import com.capitalone.dashboard.response.CodeQualityAuditResponse;
import com.capitalone.dashboard.response.TestResultsResponse;

import java.io.IOException;
import java.util.List;

public interface AuditService {

    List<CodeReviewAuditResponse> getPeerReviewResponses(CollectorItem repoItem, long beginDt, long endDt);
    List<CodeReviewAuditResponse> getPeerReviewResponses(String repo, String branch, String scmName, long beginDate, long endDate);

    BuildAuditResponse getBuildJobReviewResponse(String jobUrl, String jobName, long beginDt, long endDt);

    DashboardReviewResponse getDashboardReviewResponse(String title, String type, String busServ, String busApp, long beginDate, long endDate) throws HygieiaException;

    List<CollectorItem> getCollectorItems(Dashboard dashboard, String widgetName, CollectorType collectorType);
    
    List<CodeQualityAuditResponse> getCodeQualityAudit(String projectName, String artifactVersion) throws IOException, HygieiaException;
    
    QualityProfileAuditResponse getQualityGateValidationDetails(String repoUrl, String repoBranch, String projectName, String artifactVersion, long beginDate, long endDate) throws HygieiaException;
    
    TestResultsResponse getTestResultExecutionDetails(String jobUrl,long beginDt, long endDt) throws HygieiaException;

    PerformaceTestAuditResponse getresultsBycomponetAndTime(String businessComp, long from, long to);


}
