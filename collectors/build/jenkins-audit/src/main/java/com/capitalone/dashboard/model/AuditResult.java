package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="audit_results")
public class AuditResult extends BaseModel     {

    private ObjectId dashboardId;

    private String dashboardTitle;

    private String auditResult;

    public AuditResult(ObjectId dashboardId, String dashboardTitle, String auditResult){
        this.dashboardId = dashboardId;
        this.dashboardTitle = dashboardTitle;
        this.auditResult = auditResult;
    }

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public ObjectId getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(ObjectId dashboardId) {
        this.dashboardId = dashboardId;
    }
}