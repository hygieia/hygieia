package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Represents a trending history of features (story/requirement) from a content management system.
 *
 * Possible collectors: VersionOne PivotalTracker Rally Trello Jira
 *
 * @author kfk884
 *
 */
@Data
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
}
