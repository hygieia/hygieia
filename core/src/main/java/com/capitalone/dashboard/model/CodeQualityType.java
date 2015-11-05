package com.capitalone.dashboard.model;

/**
 * Enumerates the possible {@link CodeQuality} types.
 */
public enum CodeQualityType {
    StaticAnalysis,
    SecurityAnalysis;

    public static CodeQualityType fromString(String value) {
        for (CodeQualityType qualityType : values()) {
            if (qualityType.toString().equalsIgnoreCase(value)) {
                return qualityType;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid CodeQualityType.");
    }
}
