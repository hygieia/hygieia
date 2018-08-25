package com.capitalone.dashboard.request;

import org.bson.types.ObjectId;

public class TeamInventoryRequest {
	
	 private ObjectId componentId;
	 private String teamId;
	 private String teamName;


	public ObjectId getComponentId() {
		return componentId;
	}

	public void setComponentId(ObjectId componentId) {
		this.componentId = componentId;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}


}
