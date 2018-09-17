
package com.capitalone.dashboard.model;

import com.atlassian.jira.rest.client.api.domain.IssueLinkType;
import java.net.URI;

/**
 * Represents a feature issue link of a jira story.
 */
public class FeatureIssueLink extends BaseModel {

	private String targetIssueKey;
	private IssueLinkType issueLinkType;
	private URI targetIssueUri;

	public void setTargetIssueKey(String targetIssueKey) {
		this.targetIssueKey = targetIssueKey;
	}

	public void setIssueLinkType(IssueLinkType issueLinkType) {
		this.issueLinkType = issueLinkType;
	}

	public void setTargetIssueUri(URI targetIssueUri) {
		this.targetIssueUri = targetIssueUri;
	}

}
