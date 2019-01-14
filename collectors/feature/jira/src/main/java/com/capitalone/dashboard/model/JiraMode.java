package com.capitalone.dashboard.model;

public enum JiraMode {
    Board,
    Team;

    public static JiraMode fromString(String value) {
        for (JiraMode type : values()) {
            if (type.toString().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid Mode.");
    }
}
