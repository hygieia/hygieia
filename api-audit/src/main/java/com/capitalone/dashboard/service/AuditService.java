package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.CollItemCfgHist;
import com.capitalone.dashboard.model.CollectorItem;
import com.capitalone.dashboard.model.CollectorType;
import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.model.GitRequest;
import com.capitalone.dashboard.response.DashboardReviewResponse;
import com.capitalone.dashboard.response.JobReviewResponse;
import com.capitalone.dashboard.response.PeerReviewResponse;

import java.util.List;

public interface AuditService {

    List<PeerReviewResponse> getPeerReviewResponses(List<GitRequest> gitRequests);

    List<GitRequest> getPullRequests(String repo, String branch, long beginDt, long endDt);

    List<Commit> getCommitsBySha (String scmRevisionNumber);

    String getJobEnvironment(String instanceUrl, String jobName);

    List<CollItemCfgHist> getCollItemCfgHist(String jobUrl, String jobName, long beginDt, long endDt);

    JobReviewResponse getBuildJobReviewResponse(String jobUrl, String jobName, long beginDt, long endDt);

    DashboardReviewResponse getDashboardReviewResponse(String title, String type, String busServ, String busApp, long beginDate, long endDate) throws HygieiaException;

    List<CollectorItem> getCollectorItems(Dashboard dashboard, String widgetName, CollectorType collectorType);
}
