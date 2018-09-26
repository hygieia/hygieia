
package com.capitalone.dashboard.model;

/**
 * Represents an issue link of a jira story.
 */
public class FeatureIssueLink {

	private String targetIssueKey;
	private String targetIssueUri;
	private String issueLinkName;
	private String issueLinkType;
	private String issueLinkDirection;

	public String getTargetIssueKey() {
		return targetIssueKey;
	}

	public void setTargetIssueKey(String targetIssueKey) {
		this.targetIssueKey = targetIssueKey;
	}

	public String getTargetIssueUri() {
		return targetIssueUri;
	}

	public void setTargetIssueUri(String targetIssueUri) {
		this.targetIssueUri = targetIssueUri;
	}

	public String getIssueLinkName() {
		return issueLinkName;
	}

	public void setIssueLinkName(String issueLinkName) {
		this.issueLinkName = issueLinkName;
	}

	public String getIssueLinkType() {
		return issueLinkType;
	}

	public void setIssueLinkType(String issueLinkType) {
		this.issueLinkType = issueLinkType;
	}

	public String getIssueLinkDirection() {
		return issueLinkDirection;
	}

	public void setIssueLinkDirection(String issueLinkDirection) {
		this.issueLinkDirection = issueLinkDirection;
	}
}
