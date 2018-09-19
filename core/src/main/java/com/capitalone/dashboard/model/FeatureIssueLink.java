
package com.capitalone.dashboard.model;

import com.atlassian.jira.rest.client.api.domain.IssueLinkType;

import java.net.URI;

/**
 * Represents a feature issue link of a jira story.
 */
public class FeatureIssueLink extends BaseModel {

	private String targetIssueKey;
	private URI targetIssueUri;
	private IssueLinkType issueLinkType;

	public String getTargetIssueKey() {
		return targetIssueKey;
	}

	public void setTargetIssueKey(String targetIssueKey) {
		this.targetIssueKey = targetIssueKey;
	}

	public URI getTargetIssueUri() {
		return targetIssueUri;
	}

	public void setTargetIssueUri(URI targetIssueUri) {
		this.targetIssueUri = targetIssueUri;
	}

	public IssueLinkType getIssueLinkType() {
		return issueLinkType;
	}

	public void setIssueLinkType(IssueLinkType issueLinkType) {
		this.issueLinkType = issueLinkType;
	}
}
