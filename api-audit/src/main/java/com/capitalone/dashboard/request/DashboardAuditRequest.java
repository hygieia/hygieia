package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.AuditType;

import java.util.Set;

public class DashboardAuditRequest extends AuditReviewRequest {
    private String dashboardTitle;
    private String dashBoardType;
    private String businessService;
    private String businessApplication;
    private Set<AuditType> auditTypes;

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

    public String getDashBoardType() {
        return dashBoardType;
    }

    public void setDashBoardType(String dashBoardType) {
        this.dashBoardType = dashBoardType;
    }

    public String getBusinessService() {
        return businessService;
    }

    public void setBusinessService(String businessService) {
        this.businessService = businessService;
    }

    public String getBusinessApplication() {
        return businessApplication;
    }

    public void setBusinessApplication(String businessApplication) {
        this.businessApplication = businessApplication;
    }

    public Set<AuditType> getAuditTypes() {
        return auditTypes;
    }

    public void setAuditTypes(Set<AuditType> auditTypes) {
        this.auditTypes = auditTypes;
    }
}
