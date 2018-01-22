package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.AuditType;

import java.util.Set;

public class DashboardAuditRequest extends AuditReviewRequest {
    private String title;
    private String businessService;
    private String businessApplication;
    private Set<AuditType> auditType;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Set<AuditType> getAuditType() {
        return auditType;
    }

    public void setAuditType(Set<AuditType> auditType) {
        this.auditType = auditType;
    }
}
