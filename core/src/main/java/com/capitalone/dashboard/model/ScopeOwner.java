package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Represents a team in a content management system that works with features.
 *
 * Possible collectors: VersionOne PivotalTracker Rally Trello Jira
 *
 * @author kfk884
 *
 */
@Data
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

}
