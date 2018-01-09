package com.capitalone.dashboard.response;

import java.util.Set;

public class QualityProfileAuditResponse extends AuditReviewResponse {

	 private Set<String> commitAuthors;
	 private Set<String> qualityGateChangePerformers;
	 

	public Set<String> getQualityGateChangePerformers() {
		return qualityGateChangePerformers;
	}

	public void setQualityGateChangePerformers(Set<String> qualityGateChangePerformers) {
		this.qualityGateChangePerformers = qualityGateChangePerformers;
	}

	public Set<String> getCommitAuthors() {
		return commitAuthors;
	}

	public void setCommitAuthors(Set<String> commitAuthors) {
		this.commitAuthors = commitAuthors;
	}
    
    
    
}
