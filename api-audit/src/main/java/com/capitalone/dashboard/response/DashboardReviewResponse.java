package com.capitalone.dashboard.response;

import java.util.List;

public class DashboardReviewResponse extends AuditReviewResponse {
    private String dashboardTitle;
    private long repoLastUpdated;
    private long buildJobLastUpdated;

    List<PeerReviewResponse> allPeerReviewResponses;

    JobReviewResponse jobReviewResponse;

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

    public long getRepoLastUpdated() {
        return repoLastUpdated;
    }

    public void setRepoLastUpdated(long repoLastUpdated) {
        this.repoLastUpdated = repoLastUpdated;
    }

    public long getBuildJobLastUpdated() {
        return buildJobLastUpdated;
    }

    public void setBuildJobLastUpdated(long buildJobLastUpdated) {
        this.buildJobLastUpdated = buildJobLastUpdated;
    }

    public List<PeerReviewResponse> getAllPeerReviewResponses() {
        return allPeerReviewResponses;
    }

    public void setAllPeerReviewResponses(List<PeerReviewResponse> allPeerReviewResponses) {
        this.allPeerReviewResponses = allPeerReviewResponses;
    }

    public JobReviewResponse getJobReviewResponse() {
        return jobReviewResponse;
    }

    public void setJobReviewResponse(JobReviewResponse jobReviewResponse) {
        this.jobReviewResponse = jobReviewResponse;
    }
}
