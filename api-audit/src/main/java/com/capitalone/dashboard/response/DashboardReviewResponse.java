package com.capitalone.dashboard.response;

import java.util.Collection;
import java.util.List;

public class DashboardReviewResponse extends AuditReviewResponse {
    private String dashboardTitle;
    private String businessService;
    private String businessApplication;

    Collection<CodeReviewAuditResponseV2> codeReviewAuditResponse;

	Collection<BuildAuditResponse> buildAuditResponse;

	Collection<CodeQualityAuditResponse> codeQualityAuditResponse;

	Collection<QualityProfileAuditResponse> qualityProfileAuditResponse;

	Collection<TestResultsResponse> testResultsResponse;

	public String getBusinessService() {
		return businessService;
	}

	public void setBusinessService(String businessService) {
		this.businessService = businessService;
	}

	public String getBusinessApplication() {
		return businessApplication;
	}

	public void setBusinessApplication(String businessApplication) {
		this.businessApplication = businessApplication;
	}

	public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

	public Collection<CodeReviewAuditResponseV2> getCodeReviewAuditResponse() {
		return codeReviewAuditResponse;
	}

	public void setCodeReviewAuditResponse(Collection<CodeReviewAuditResponseV2> codeReviewAuditResponse) {
		this.codeReviewAuditResponse = codeReviewAuditResponse;
	}

	public Collection<BuildAuditResponse> getBuildAuditResponse() {
		return buildAuditResponse;
	}

	public void setBuildAuditResponse(Collection<BuildAuditResponse> buildAuditResponse) {
		this.buildAuditResponse = buildAuditResponse;
	}

	public Collection<CodeQualityAuditResponse> getCodeQualityAuditResponse() {
		return codeQualityAuditResponse;
	}

	public void setCodeQualityAuditResponse(Collection<CodeQualityAuditResponse> codeQualityAuditResponse) {
		this.codeQualityAuditResponse = codeQualityAuditResponse;
	}

	public Collection<QualityProfileAuditResponse> getQualityProfileAuditResponse() {
		return qualityProfileAuditResponse;
	}

	public void setQualityProfileAuditResponse(Collection<QualityProfileAuditResponse> qualityProfileAuditResponse) {
		this.qualityProfileAuditResponse = qualityProfileAuditResponse;
	}

	public Collection<TestResultsResponse> getTestResultsResponse() {
		return testResultsResponse;
	}

	public void setTestResultsResponse(Collection<TestResultsResponse> testResultsResponse) {
		this.testResultsResponse = testResultsResponse;
	}
}
