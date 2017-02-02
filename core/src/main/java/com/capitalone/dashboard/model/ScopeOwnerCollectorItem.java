package com.capitalone.dashboard.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
	@Indexed
	private static final String PROJECT_ID = "projectId";
	private static final String CHANGE_DATE = "changeDate";
	private static final String ASSET_STATE = "assetState";
	private static final String IS_DELETED = "isDeleted";

	public String getTeamId() {
		return (String) getOptions().get(TEAM_ID);
	}

	public void setTeamId(String teamId) {
		getOptions().put(TEAM_ID, teamId);
	}

	public String getProjectId() {
		return (String) getOptions().get(PROJECT_ID);
	}

	public void setProjectId(String projectId) {
		getOptions().put(PROJECT_ID, projectId);
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
        EqualsBuilder builder = new EqualsBuilder();
        return builder.append(getTeamId(), that.getTeamId()).append(getCollectorId(), that.getCollectorId()).build();
    }

    @Override
    public int hashCode() {
    	return new HashCodeBuilder(17, 37).append(getTeamId()).append(getCollectorId()).toHashCode();
    }
}
