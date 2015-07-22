package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a trending history of features (story/requirement) from a content management system.
 *
 * Possible collectors: VersionOne PivotalTracker Rally Trello Jira
 *
 * @author kfk884
 *
 */
@Document(collection = "feature-history")
public class FeatureHistory extends BaseModel {
	/*
	 * Sprint data
	 */
	@Indexed
	private String sprintID;
	private String sprintBeginDate;
	private String sprintEndDate;
	/*
	 * Scope data
	 */
	@Indexed
	private String projectID;
	/*
	 * ScopeOwner data
	 */
	@Indexed
	private String teamID;
	/*
	 * Story data
	 */
	private String sStatus;
	private String sEstimate;
	private String sToDo;
	private String sAssetState;
	private String sSoftwareTesting;
	private String isDeleted;
	@Indexed
	private String changeDate;
	private String reportedDate;
	@Indexed
	private String storyID;

	public String getStoryID() {
		return storyID;
	}

	public void setStoryID(String storyID) {
		this.storyID = storyID;
	}

	public String getSprintID() {
		return sprintID;
	}

	public void setSprintID(String sprintID) {
		this.sprintID = sprintID;
	}

	public String getSprintBeginDate() {
		return sprintBeginDate;
	}

	public void setSprintBeginDate(String sprintBeginDate) {
		this.sprintBeginDate = sprintBeginDate;
	}

	public String getSprintEndDate() {
		return sprintEndDate;
	}

	public void setSprintEndDate(String sprintEndDate) {
		this.sprintEndDate = sprintEndDate;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public String getTeamID() {
		return teamID;
	}

	public void setTeamID(String teamID) {
		this.teamID = teamID;
	}

	public String getsStatus() {
		return sStatus;
	}

	public void setsStatus(String sStatus) {
		this.sStatus = sStatus;
	}

	public String getsEstimate() {
		return sEstimate;
	}

	public void setsEstimate(String sEstimate) {
		this.sEstimate = sEstimate;
	}

	public String getsToDo() {
		return sToDo;
	}

	public void setsToDo(String sToDo) {
		this.sToDo = sToDo;
	}

	public String getsAssetState() {
		return sAssetState;
	}

	public void setsAssetState(String sAssetState) {
		this.sAssetState = sAssetState;
	}

	public String getsSoftwareTesting() {
		return sSoftwareTesting;
	}

	public void setsSoftwareTesting(String sSoftwareTesting) {
		this.sSoftwareTesting = sSoftwareTesting;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}

	public String getReportedDate() {
		return reportedDate;
	}

	public void setReportedDate(String reportedDate) {
		this.reportedDate = reportedDate;
	}

}
