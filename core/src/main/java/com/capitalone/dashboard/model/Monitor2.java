package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/*
Represents the getters and setters for the Aws Server Status object.
 */
@Document(collection = "monitor2")
public class Monitor2 extends BaseModel {
    private String name;
    private String url;
    private int status;
    private ObjectId dashboardId;
    private long lastUpdated;
    private String applicationName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }

    public int getStatus() { return this.status; }

    public void setStatus(int status) { this.status = status; }

    public ObjectId getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(ObjectId dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
