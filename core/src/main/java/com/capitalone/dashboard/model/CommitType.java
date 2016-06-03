package com.capitalone.dashboard.model;

public enum CommitType {
    Merge,
    New;

    public static CommitType fromString(String value) {
        for (CommitType commitType : values()) {
            if (commitType.toString().equalsIgnoreCase(value)) {
                return commitType;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid Commit Type");
    }
}


