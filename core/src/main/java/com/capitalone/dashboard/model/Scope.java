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

/**
 * Represents a project in a content management system that aligns features
 * under scope.
 * 
 * Possible collectors: VersionOne PivotalTracker Rally Trello Jira
 * 
 * @author kfk884
 * 
 */
@Data
@Document(collection = "scope")
public class Scope extends BaseModel {
	private ObjectId collectorId;
	@Indexed
	private String pId;
	@Indexed
	private String name;
	private String projectPath;
	private String beginDate;
	private String endDate;
	private String changeDate;
	private String assetState;
	private String isDeleted;
}
