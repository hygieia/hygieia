package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="audit_results")
public class AuditResult extends BaseModel     {

    private ObjectId dashboardId;
    private String dashboardTitle;
    private String auditStatuses;

//    private String lineOfBusiness;
//    private String configurationItemBusServName;
//    private String configurationItemBusAppName;
//    private String busServiceOwner;
//    private String busAppOwner;


    public AuditResult(ObjectId dashboardId, String dashboardTitle, String auditStatuses){
        this.dashboardId = dashboardId;
        this.dashboardTitle = dashboardTitle;
        this.auditStatuses = auditStatuses;
    }

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

    public String getAuditStatuses() {
        return auditStatuses;
    }

    public void setAuditStatuses(String auditStatuses) {
        this.auditStatuses = auditStatuses;
    }

    public ObjectId getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(ObjectId dashboardId) {
        this.dashboardId = dashboardId;
    }
}