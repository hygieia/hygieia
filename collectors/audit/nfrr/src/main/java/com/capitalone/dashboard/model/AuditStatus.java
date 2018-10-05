package com.capitalone.dashboard.model;

public enum AuditStatus {
    NA,
    OK,
    FAIL;

    public static AuditStatus fromString(String value) {
        for (AuditStatus auditType : values()) {
            if (auditType.toString().equalsIgnoreCase(value)) {
                return auditType;
            }
        }
        throw new IllegalArgumentException(value + " is not a Audit Type");
    }
}
