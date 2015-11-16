package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents the status (online/offline) of a server for a given component and environment.
 */
@Document(collection = "environment_status")
public class EnvironmentStatus extends BaseModel {
    private ObjectId collectorItemId;
    private String componentID;
	private String environmentName;
    private String componentName;
    private String resourceName;
    private String parentAgentName;
    private boolean online;

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public String getComponentID() {
		return componentID;
	}

	public void setComponentID(String componentID) {
		this.componentID = componentID;
	}
	
    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getParentAgentName() {
        return parentAgentName;
    }

    public void setParentAgentName(String parentAgentName) {
        this.parentAgentName = parentAgentName;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}

