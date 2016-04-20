package com.capitalone.dashboard.model;

import lombok.Data;

/**
 * Represents a {@link CodeQuality} metric. Each metric should have a unique name property.
 */
@Data
public class CodeQualityMetric {
    private String name;
    private Object value;
    private String formattedValue;
    private CodeQualityMetricStatus status;
    private String statusMessage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return name.equals(((CodeQualityMetric) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
