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

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Collector implementation for Feature that stores system configuration
 * settings required for source system data connection (e.g., API tokens, etc.)
 * 
 * @author KFK884
 */
public class TeamCollectorItem extends CollectorItem {
	@Indexed
	private static final String TEAM_ID = "teamId";
	private static final String CHANGE_DATE = "changeDate";
	private static final String ASSET_STATE = "assetState";
	private static final String IS_DELETED = "isDeleted";
	
	public String getTeamId() {
		return (String) getOptions().get(TEAM_ID);
	}
	
	public void setTeamId(String teamId) {
		getOptions().put(TEAM_ID, teamId);
	}
	
	public String getName() {
		return getDescription();
	}

	public void setName(String name) {
		setDescription(name);
	}

	public String getChangeDate() {
		return (String) getOptions().get(CHANGE_DATE);
	}

	public void setChangeDate(String changeDate) {
		getOptions().put(CHANGE_DATE, changeDate);
	}

	public String getAssetState() {
		return (String) getOptions().get(ASSET_STATE);
	}

	public void setAssetState(String assetState) {
		getOptions().put(ASSET_STATE, assetState);
	}

	public String getIsDeleted() {
		return (String) getOptions().get(IS_DELETED);
	}

	public void setIsDeleted(String isDeleted) {
		getOptions().put(IS_DELETED, isDeleted);
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TeamCollectorItem that = (TeamCollectorItem) o;
        return getTeamId().equals(that.getTeamId()) && getTeamId().equals(that.getTeamId());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }
}
