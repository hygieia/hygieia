package com.capitalone.dashboard.model;

public enum DataStatus {
    ERROR,
    NA,
    OK,
    NOT_CONFIGURED,
    NO_DATA;
    
    public static DataStatus fromString(String value) {
        for (DataStatus auditType : values()) {
            if (auditType.toString().equalsIgnoreCase(value)) {
                return auditType;
            }
        }
        throw new IllegalArgumentException(value + " is not a Data Status Type");
    }
}
