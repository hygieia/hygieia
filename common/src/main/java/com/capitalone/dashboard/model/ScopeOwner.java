package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a team in a content management system that works with features.
 *
 * Possible collectors: VersionOne PivotalTracker Rally Trello Jira
 *
 * @author kfk884
 *
 */
@Document(collection = "scope-owner")
public class ScopeOwner extends CollectorItem {
	private ObjectId collectorItemId;
	@Indexed
	private String teamId;
	@Indexed
	private String name;
	private String changeDate;
	private String assetState;
	private String isDeleted;

	public ObjectId getCollectorItemId() {
		return collectorItemId;
	}

	public void setCollectorItemId(ObjectId collectorItemId) {
		this.collectorItemId = collectorItemId;
	}

	public String getTeamId() {
		return this.teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}

	public String getAssetState() {
		return assetState;
	}

	public void setAssetState(String assetState) {
		this.assetState = assetState;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

}
