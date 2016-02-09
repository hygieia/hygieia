package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Collector implementation for Feature that stores system configuration
 * settings required for source system data connection (e.g., API tokens, etc.)
 *
 * @author KFK884
 */
public class ScopeOwnerCollectorItem extends CollectorItem {
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

        ScopeOwnerCollectorItem that = (ScopeOwnerCollectorItem) o;
        return getTeamId().equals(that.getTeamId()) && getTeamId().equals(that.getTeamId());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }
}
