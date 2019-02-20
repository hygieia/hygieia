package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class Audit {
    private AuditType type;
    private AuditStatus auditStatus;
    private DataStatus dataStatus;
    private List<String> url = new ArrayList<>();
    private List<String> auditStatusCodes = new ArrayList<>();
    private Map traceability;

    public AuditType getType() {
        return type;
    }

    public void setType(AuditType type) {
        this.type = type;
    }

    public AuditStatus getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(AuditStatus auditStatus) {
        this.auditStatus = auditStatus;
    }

    public DataStatus getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(DataStatus dataStatus) {
        this.dataStatus = dataStatus;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public List<String> getAuditStatusCodes() {
        return auditStatusCodes;
    }

    public void setAuditStatusCodes(List<String> auditStatusCodes) {
        this.auditStatusCodes = auditStatusCodes;
    }

    public Map getTraceability() {
        return traceability;
    }

    public void setTraceability(Map traceability) {
        this.traceability = traceability;
    }
}