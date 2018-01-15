package com.capitalone.dashboard.response;

import com.capitalone.dashboard.status.DashboardAuditStatus;

import java.util.Collection;

public class DashboardReviewResponse extends AuditReviewResponse <DashboardAuditStatus> {
    private String dashboardTitle;
    private String businessService;
    private String businessApplication;

    private Collection<CodeReviewAuditResponseV2> codeReview;

	private Collection<BuildAuditResponse> build;

	private Collection<CodeQualityAuditResponse> codeQuality;

	private Collection<TestResultsAuditResponse> regresionTestResult;

	private Collection<PerformanceTestAuditResponse> performanceTestResult;

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

	public Collection<TestResultsAuditResponse> getRegresionTestResult() {
		return regresionTestResult;
	}

	public void setRegresionTestResult(Collection<TestResultsAuditResponse> regresionTestResult) {
		this.regresionTestResult = regresionTestResult;
	}

	public Collection<PerformanceTestAuditResponse> getPerformanceTestResult() {
		return performanceTestResult;
	}

	public void setPerformanceTestResult(Collection<PerformanceTestAuditResponse> performanceTestResult) {
		this.performanceTestResult = performanceTestResult;
	}
}
