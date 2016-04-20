/*************************DA-BOARD-LICENSE-START*********************************
 * Copyright 2014 CapitalOne, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************DA-BOARD-LICENSE-END*********************************/

package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.List;

/**
 * Represents a feature (story/requirement) of a component.
 * 
 * Possible collectors: VersionOne PivotalTracker Rally Trello Jira
 * 
 * @author kfk884
 * 
 */
@Data
@Document(collection = "feature")
public class Feature extends BaseModel {
	private ObjectId collectorId;
	/*
	 * Story data
	 */
	@Indexed
	private String sId;
	private String sNumber;
	private String sName;
	private String sStatus;
	private String sState;
	private String sEstimate;
	@Indexed
	private String changeDate;
	private String isDeleted;

	/*
	 * Owner data
	 */
	private List<String> sOwnersID;
	private List<String> sOwnersIsDeleted;
	private List<String> sOwnersChangeDate;
	private List<String> sOwnersState;
	private List<String> sOwnersUsername;
	private List<String> sOwnersFullName;
	private List<String> sOwnersShortName;

	/*
	 * ScopeOwner data
	 */
	private String sTeamIsDeleted;
	private String sTeamAssetState;
	private String sTeamChangeDate;
	private String sTeamName;
	@Indexed
	private String sTeamID;

	/*
	 * Sprint data
	 */
	private String sSprintIsDeleted;
	private String sSprintChangeDate;
	private String sSprintAssetState;
	@Indexed
	private String sSprintEndDate;
	@Indexed
	private String sSprintBeginDate;
	private String sSprintName;
	@Indexed
	private String sSprintID;

	/*
	 * Epic data
	 */
	private String sEpicIsDeleted;
	private String sEpicChangeDate;
	private String sEpicAssetState;
	private String sEpicType;
	private String sEpicEndDate;
	private String sEpicBeginDate;
	private String sEpicName;
	private String sEpicNumber;
	private String sEpicID;

	/*
	 * Scope data
	 */
	private String sProjectPath;
	private String sProjectIsDeleted;
	private String sProjectState;
	private String sProjectChangeDate;
	private String sProjectEndDate;
	private String sProjectBeginDate;
	private String sProjectName;
	private String sProjectID;

}
