package com.capitalone.dashboard.request;

public class TestResultAuditRequest extends AuditReviewRequest {
	
    private String jobUrl;

	public String getJobUrl() {
		return jobUrl;
	}

	public void setJobUrl(String jobUrl) {
		this.jobUrl = jobUrl;
	}

}
