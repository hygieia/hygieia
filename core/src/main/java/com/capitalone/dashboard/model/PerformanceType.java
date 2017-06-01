package com.capitalone.dashboard.model;


public enum PerformanceType {
    ApplicationPerformance(CollectorType.AppPerformance),
    InfrastructurePerformance(CollectorType.InfraPerformance);

    private final CollectorType collectorType;
    PerformanceType(CollectorType collectorType) {
        this.collectorType = collectorType;
    }

    public static PerformanceType fromString(String value) {
        for (PerformanceType performanceType : values()) {
            if (performanceType.toString().equalsIgnoreCase(value)) {
                return performanceType;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid PerformanceType.");
    }

    public CollectorType collectorType() {
        return collectorType;
    }
}
