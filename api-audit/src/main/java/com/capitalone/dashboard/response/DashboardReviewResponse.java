package com.capitalone.dashboard.response;

import java.util.List;

public class DashboardReviewResponse extends AuditReviewResponse {
    private String dashboardTitle;

    List<List<PeerReviewResponse>> allPeerReviewResponses;

    JobReviewResponse jobReviewResponse;
    
	StaticAnalysisResponse staticAnalysisResponse;
	
	CodeQualityProfileValidationResponse codeQualityProfileValidationResponse;
	
	TestResultsResponse testResultsResponse;


	public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

    public List<List<PeerReviewResponse>> getAllPeerReviewResponses() {
        return allPeerReviewResponses;
    }

    public void setAllPeerReviewResponses(List<List<PeerReviewResponse>> allPeerReviewResponses) {
        this.allPeerReviewResponses = allPeerReviewResponses;
    }

    public JobReviewResponse getJobReviewResponse() {
        return jobReviewResponse;
    }

    public void setJobReviewResponse(JobReviewResponse jobReviewResponse) {
        this.jobReviewResponse = jobReviewResponse;
    }
    
    
	public StaticAnalysisResponse getStaticAnalysisResponse() {
			return staticAnalysisResponse;
	}

	public void setStaticAnalysisResponse(StaticAnalysisResponse staticAnalysisResponse) {
		this.staticAnalysisResponse = staticAnalysisResponse;
	}
	
	public CodeQualityProfileValidationResponse getCodeQualityProfileValidationResponse() {
		return codeQualityProfileValidationResponse;
	}

	public void setCodeQualityProfileValidationResponse(
			CodeQualityProfileValidationResponse codeQualityProfileValidationResponse) {
		this.codeQualityProfileValidationResponse = codeQualityProfileValidationResponse;
	}
	
	public TestResultsResponse getTestResultsResponse() {
		return testResultsResponse;
	}

	public void setTestResultsResponse(TestResultsResponse testResultsResponse) {
		this.testResultsResponse = testResultsResponse;
	}

	
}
