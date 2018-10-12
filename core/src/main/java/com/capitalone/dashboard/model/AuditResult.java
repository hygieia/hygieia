package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "audit_results")
public class AuditResult extends BaseModel {

    private ObjectId dashboardId;
    private String dashboardTitle;
    private String lineOfBusiness;
    private String configItemBusServName;
    private String configItemBusAppName;
    private String configItemBusServOwner;
    private String configItemBusAppOwner;
    private String auditType;
    private String auditTypeStatus;
    private String auditStatus;
    private String url;
    private String auditDetails;
    private long timestamp;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public AuditResult(ObjectId dashboardId, String dashboardTitle, String lineOfBusiness, String configItemBusServName,
                       String configItemBusAppName, String configItemBusServOwner, String configItemBusAppOwner, String auditType,
                       String auditTypeStatus, String auditStatus, String auditDetails, String url, long timestamp) {
        this.dashboardId = dashboardId;
        this.dashboardTitle = dashboardTitle;
        this.lineOfBusiness = lineOfBusiness;
        this.configItemBusServName = configItemBusServName;
        this.configItemBusAppName = configItemBusAppName;
        this.configItemBusServOwner = configItemBusServOwner;
        this.configItemBusAppOwner = configItemBusAppOwner;
        this.auditType = auditType;
        this.auditTypeStatus = auditTypeStatus;
        this.auditStatus = auditStatus;
        this.url = url;
        this.auditDetails = auditDetails;
        this.timestamp = timestamp;
    }

    public ObjectId getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(ObjectId dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public void setDashboardTitle(String dashboardTitle) {
        this.dashboardTitle = dashboardTitle;
    }

    public String getLineOfBusiness() {
        return lineOfBusiness;
    }

    public void setLineOfBusiness(String lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }

    public String getConfigItemBusServName() {
        return configItemBusServName;
    }

    public void setConfigItemBusServName(String configItemBusServName) {
        this.configItemBusServName = configItemBusServName;
    }

    public String getConfigItemBusAppName() {
        return configItemBusAppName;
    }

    public void setConfigItemBusAppName(String configItemBusAppName) {
        this.configItemBusAppName = configItemBusAppName;
    }

    public String getConfigItemBusServOwner() {
        return configItemBusServOwner;
    }

    public void setConfigItemBusServOwner(String configItemBusServOwner) {
        this.configItemBusServOwner = configItemBusServOwner;
    }

    public String getConfigItemBusAppOwner() {
        return configItemBusAppOwner;
    }

    public void setConfigItemBusAppOwner(String configItemBusAppOwner) {
        this.configItemBusAppOwner = configItemBusAppOwner;
    }

    public String getAuditType() {
        return auditType;
    }

    public void setAuditType(String auditType) {
        this.auditType = auditType;
    }

    public String getAuditTypeStatus() {
        return auditTypeStatus;
    }

    public void setAuditTypeStatus(String auditTypeStatus) {
        this.auditTypeStatus = auditTypeStatus;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuditDetails() {
        return auditDetails;
    }

    public void setAuditDetails(String auditDetails) {
        this.auditDetails = auditDetails;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}