package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Represents the status (online/offline) of a server for a given component and environment.
 */
@Data
@Document(collection = "environment_status")
public class EnvironmentStatus extends BaseModel {
    private ObjectId collectorItemId;
    private String componentID;
	private String environmentName;
    private String componentName;
    private String resourceName;
    private String parentAgentName;
    private boolean online;
}

