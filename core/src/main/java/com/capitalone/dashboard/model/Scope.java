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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a project in a content management system that aligns features
 * under scope.
 * 
 * Possible collectors: VersionOne PivotalTracker Rally Trello Jira
 * 
 * @author kfk884
 * 
 */
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
	
	@Transient
    private Collector collector;
	
	public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

	public ObjectId getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(ObjectId collectorId) {
        this.collectorId = collectorId;
    }
	
	public String getpId() {
		return this.pId;
	}

	public void setpId(String pId) {
		this.pId = (pId != null ? pId.trim() : "");
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = (name != null ? name.trim() : "");
	}

	public String getProjectPath() {
		return this.projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getBeginDate() {
		return this.beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getChangeDate() {
		return this.changeDate;
	}

	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}

	public String getAssetState() {
		return this.assetState;
	}

	public void setAssetState(String assetState) {
		this.assetState = assetState;
	}

	public String getIsDeleted() {
		return this.isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Scope that = (Scope) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(pId, that.pId).append(collectorId, that.collectorId).build();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(pId).append(collectorId).toHashCode();
	}

}
