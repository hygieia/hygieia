package com.capitalone.dashboard.model;

public enum RallyFeatureType {
	Feature(CollectorType.Feature);
	private final CollectorType collectorType;
	
	RallyFeatureType(CollectorType collectorType) {
        this.collectorType = collectorType;
    }
    public static RallyFeatureType fromString(String value) {
        for (RallyFeatureType rallyFeatureType : values()) {
            if (rallyFeatureType.toString().equalsIgnoreCase(value)) {
                return rallyFeatureType;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid RallyFeatureType.");
    }
    
    public CollectorType collectorType() {
        return collectorType;
    }
}
