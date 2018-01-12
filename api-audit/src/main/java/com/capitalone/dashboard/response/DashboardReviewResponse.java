package com.capitalone.dashboard.response;

import java.util.Collection;

public class DashboardReviewResponse extends AuditReviewResponse {
    private String dashboardTitle;
    private String businessService;
    private String businessApplication;

    private Collection<CodeReviewAuditResponseV2> codeReview;

	private Collection<BuildAuditResponse> build;

	private Collection<CodeQualityAuditResponse> codeQuality;

	private Collection<TestResultsResponse> testResult;

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

	public Collection<CodeReviewAuditResponseV2> getCodeReview() {
		return codeReview;
	}

	public void setCodeReview(Collection<CodeReviewAuditResponseV2> codeReview) {
		this.codeReview = codeReview;
	}

	public Collection<BuildAuditResponse> getBuild() {
		return build;
	}

	public void setBuild(Collection<BuildAuditResponse> build) {
		this.build = build;
	}

	public Collection<CodeQualityAuditResponse> getCodeQuality() {
		return codeQuality;
	}

	public void setCodeQuality(Collection<CodeQualityAuditResponse> codeQuality) {
		this.codeQuality = codeQuality;
	}

	public Collection<TestResultsResponse> getTestResult() {
		return testResult;
	}

	public void setTestResult(Collection<TestResultsResponse> testResult) {
		this.testResult = testResult;
	}
}
