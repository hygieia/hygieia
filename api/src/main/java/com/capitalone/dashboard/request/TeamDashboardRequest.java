package com.capitalone.dashboard.request;

import org.bson.types.ObjectId;

/**
 * Created by jkc on 1/13/16.
 */
public class TeamDashboardRequest {

    String name;
    ObjectId dashboardId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(ObjectId dashboardId) {
        this.dashboardId = dashboardId;
    }
}
