package com.capitalone.dashboard.service;

import java.io.IOException;
import java.util.List;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollItemCfgHist;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.response.*;

public interface AuditService {

    List<PeerReviewResponse> getPeerReviewResponses(List<GitRequest> gitRequests, List<Commit> commits, String scmUrl, String scmBranch);

    List<GitRequest> getPullRequests(String repo, String branch, long beginDt, long endDt);

    List<Commit> getCommits(String repo, String branch, long beginDt, long endDt);

    String getJobEnvironment(String instanceUrl, String jobName);

    List<CollItemCfgHist> getCollItemCfgHist(String jobUrl, String jobName, long beginDt, long endDt);

    JobReviewResponse getBuildJobReviewResponse(String jobUrl, String jobName, long beginDt, long endDt);

    DashboardReviewResponse getDashboardReviewResponse(String title, String type, String busServ, String busApp, long beginDate, long endDate) throws HygieiaException;

    List<CollectorItem> getCollectorItems(Dashboard dashboard, String widgetName, CollectorType collectorType);

    boolean isGitRepoConfigured(String url,String branch);

//    List<CollectorItem> getAllRepos();
    
    List<StaticAnalysisResponse> getCodeQualityAudit(String artifactGroup, String artifactName, String artifactVersion) throws IOException, HygieiaException;
    
    CodeQualityProfileValidationResponse getQualityGateValidationDetails(String repoUrl,String repoBranch,String artifactGroup, String artifactName, String artifactVersion, long beginDate, long endDate) throws HygieiaException;
    
    TestResultsResponse getTestResultExecutionDetails(String jobUrl,long beginDt, long endDt) throws HygieiaException;

    PerfReviewResponse getresultsBycomponetAndTime(String businessComp, long from, long to);
}
