package com.capitalone.dashboard.model;

/**
 * Enumerates the possible {@link CodeQuality} types.
 */
public enum CodeQualityType {
    StaticAnalysis(CollectorType.CodeQuality),
    SecurityAnalysis(CollectorType.StaticSecurityScan);

    private final CollectorType collectorType;
    CodeQualityType(CollectorType collectorType) {
        this.collectorType = collectorType;
    }

    public static CodeQualityType fromString(String value) {
        for (CodeQualityType qualityType : values()) {
            if (qualityType.toString().equalsIgnoreCase(value)) {
                return qualityType;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid CodeQualityType.");
    }

    public CollectorType collectorType() {
        return collectorType;
    }
}
