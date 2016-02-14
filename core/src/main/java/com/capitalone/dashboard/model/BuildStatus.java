package com.capitalone.dashboard.model;

/**
 * Enumeration of valid build statuses.
 */
public enum BuildStatus {
    Success,
    Failure,
    Unstable,
    Aborted,
    InProgress,
    Unknown;

    public static BuildStatus fromString(String value) {
        for (BuildStatus buildStatus : values()) {
            if (buildStatus.toString().equalsIgnoreCase(value)) {
                return buildStatus;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid BuildStatus.");
    }
}
