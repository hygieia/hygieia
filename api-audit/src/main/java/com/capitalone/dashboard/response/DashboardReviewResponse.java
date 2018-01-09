package com.capitalone.dashboard.response;

import java.util.List;

public class DashboardReviewResponse extends AuditReviewResponse {
    private String dashboardTitle;

    List<List<CodeReviewAuditResponse>> allPeerReviewResponses;

    BuildAuditResponse buildAuditResponse;
    
	CodeQualityAuditResponse codeQualityAuditResponse;
	
	QualityProfileAuditResponse qualityProfileAuditResponse;
	
	TestResultsResponse testResultsResponse;


	public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

    public List<List<CodeReviewAuditResponse>> getAllPeerReviewResponses() {
        return allPeerReviewResponses;
    }

    public void setAllPeerReviewResponses(List<List<CodeReviewAuditResponse>> allPeerReviewResponses) {
        this.allPeerReviewResponses = allPeerReviewResponses;
    }

    public BuildAuditResponse getBuildAuditResponse() {
        return buildAuditResponse;
    }

    public void setBuildAuditResponse(BuildAuditResponse buildAuditResponse) {
        this.buildAuditResponse = buildAuditResponse;
    }
    
    
	public CodeQualityAuditResponse getCodeQualityAuditResponse() {
			return codeQualityAuditResponse;
	}

	public void setCodeQualityAuditResponse(CodeQualityAuditResponse codeQualityAuditResponse) {
		this.codeQualityAuditResponse = codeQualityAuditResponse;
	}
	
	public QualityProfileAuditResponse getQualityProfileAuditResponse() {
		return qualityProfileAuditResponse;
	}

	public void setQualityProfileAuditResponse(
			QualityProfileAuditResponse qualityProfileAuditResponse) {
		this.qualityProfileAuditResponse = qualityProfileAuditResponse;
	}
	
	public TestResultsResponse getTestResultsResponse() {
		return testResultsResponse;
	}

	public void setTestResultsResponse(TestResultsResponse testResultsResponse) {
		this.testResultsResponse = testResultsResponse;
	}

	
}
