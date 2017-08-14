package com.capitalone.dashboard.model;

public enum ConfigHistOperationType {
    CREATED,
    CHANGED;

    public static ConfigHistOperationType fromString(String value) {
        for (ConfigHistOperationType opType : values()) {
            if (opType.toString().equalsIgnoreCase(value)) {
                return opType;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid operation Type");
    }
}
