package com.capitalone.dashboard.model;

public enum InsertStatus {
    Created,
    Updated,
    Duplicate,
    Failed,
    Unknown;

    public static InsertStatus fromString(String value) {
        for (InsertStatus status : values()) {
            if (status.toString().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid InsertStatus.");
    }
}

