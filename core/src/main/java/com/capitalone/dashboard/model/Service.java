package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * A product or service offered by an Application.
 */
@Data
@Document(collection="services")
public class Service extends BaseModel {
    private String name;
    private String applicationName;
    private ObjectId dashboardId;
    private ServiceStatus status;
    private String message;
    private long lastUpdated;
    private Set<ObjectId> dependedBy = new HashSet<>();

}
