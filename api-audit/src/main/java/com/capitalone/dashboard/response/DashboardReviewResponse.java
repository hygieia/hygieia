package com.capitalone.dashboard.response;

import java.util.List;

public class DashboardReviewResponse extends AuditReviewResponse {
    private String dashboardTitle;

    List<PeerReviewResponse> allPeerReviewResponses;

    JobReviewResponse jobReviewResponse;

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
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
