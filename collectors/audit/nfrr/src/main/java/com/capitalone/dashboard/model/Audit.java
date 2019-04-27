package com.capitalone.dashboard.model;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;


public class Audit {
    private AuditType type;
    private AuditStatus auditStatus;
    private DataStatus dataStatus;
    private List<String> url = new ArrayList<>();
    private Set<String> auditStatusCodes = new HashSet<>();

    private Map<String, Object> options;

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

    public Set<String> getAuditStatusCodes() {
        return auditStatusCodes;
    }

    public void setAuditStatusCodes(Set<String> auditStatusCodes) {
        this.auditStatusCodes = auditStatusCodes;
    }

    public Map<String, Object> getOptions() { return options; }

    public void setOptions(Map<String, Object> options) { this.options = options; }
}