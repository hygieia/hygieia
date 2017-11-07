package com.capitalone.dashboard.response;

import java.util.Collection;

import com.capitalone.dashboard.model.TestCapability;

public class TestResultsResponse extends AuditReviewResponse  {
	
	 private Collection<TestCapability> testCapabilities;
	 
	public Collection<TestCapability> getTestCapabilities() {
		return testCapabilities;
	}

	public void setTestCapabilities(Collection<TestCapability> testCapabilities) {
		this.testCapabilities = testCapabilities;
	}
	
	

}
