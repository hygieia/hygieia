package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.status.TestResultAuditStatus;

import java.util.Collection;

public class TestResultsAuditResponse extends AuditReviewResponse<TestResultAuditStatus>  {
	
	 private Collection<TestCapability> testCapabilities;
	 
	public Collection<TestCapability> getTestCapabilities() {
		return testCapabilities;
	}

	public void setTestCapabilities(Collection<TestCapability> testCapabilities) {
		this.testCapabilities = testCapabilities;
	}
	
	

}
