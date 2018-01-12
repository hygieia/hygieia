package com.capitalone.dashboard.response;

import com.capitalone.dashboard.model.TestCapability;

import java.util.Collection;

public class TestResultsResponse extends AuditReviewResponse  {
	
	 private Collection<TestCapability> testCapabilities;
	 
	public Collection<TestCapability> getTestCapabilities() {
		return testCapabilities;
	}

	public void setTestCapabilities(Collection<TestCapability> testCapabilities) {
		this.testCapabilities = testCapabilities;
	}
	
	

}
