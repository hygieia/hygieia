package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Represents deployable units (components) deployed to an environment.
 */
@Data
@Document(collection = "environment_components")
public class EnvironmentComponent extends BaseModel {
    /**
     * Deploy collectorItemId
     */
    private ObjectId collectorItemId;
    private String environmentName;
    private String environmentUrl;
    private String componentID;
	private String componentName;
    private String componentVersion;
    private boolean deployed;
    private long deployTime;
    private long asOfDate;

}
