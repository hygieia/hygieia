package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="dashboard_audit")
public class AuditStatus extends BaseModel     {

    private ObjectId dashboardId;

    private String dashboardTitle;

    private String auditStatus;

    public AuditStatus(ObjectId dashboardId, String dashboardTitle, String auditStatus){
        this.dashboardId = dashboardId;
        this.dashboardTitle = dashboardTitle;
        this.auditStatus = auditStatus;
    }

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public AuditStatus setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
        return this;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public AuditStatus setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
        return this;
    }

    public ObjectId getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(ObjectId dashboardId) {
        this.dashboardId = dashboardId;
    }
}




