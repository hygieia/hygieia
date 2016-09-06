package com.capitalone.dashboard.model;

public enum CommitType {
    NotBuilt, //maven release commits that are not built
    Merge, //github merge commits that dont show up in build change set
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


