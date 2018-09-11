
package com.capitalone.dashboard.model;

/**
 * Represents a feature issue link of a jira story.
 */
public class FeatureIssueLink extends BaseModel {

	private String issueLinkID;
	private String issueLinkType;
	private String issueLinkName;
	private String linkedIssueStoryNumber;
	private String linkedIssueStoryType;


	public String getIssueLinkID() {
		return issueLinkID;
	}

	public void setIssueLinkID(String issueLinkID) {
		this.issueLinkID = issueLinkID;
	}

	public String getIssueLinkType() {
		return issueLinkType;
	}

	public void setIssueLinkType(String issueLinkType) {
		this.issueLinkType = issueLinkType;
	}

	public String getIssueLinkName() {
		return issueLinkName;
	}

	public void setIssueLinkName(String issueLinkName) {
		this.issueLinkName = issueLinkName;
	}

	public String getLinkedIssueStoryNumber() {
		return linkedIssueStoryNumber;
	}

	public void setLinkedIssueStoryNumber(String linkedIssueStoryNumber) {
		this.linkedIssueStoryNumber = linkedIssueStoryNumber;
	}

	public String getLinkedIssueStoryType() {
		return linkedIssueStoryType;
	}

	public void setLinkedIssueStoryType(String linkedIssueStoryType) {
		this.linkedIssueStoryType = linkedIssueStoryType;
	}
}
