package com.capitalone.dashboard.model;

/**
 * Represents a {@link CodeQuality} metric. Each metric should have a unique name property.
 */
public class CodeQualityMetric {
    private String name;
    private Object value;
    private String formattedValue;
    private CodeQualityMetricStatus status;
    private String statusMessage;

    public CodeQualityMetric(String name) {
        this.name = name;
    }

    public CodeQualityMetric() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

    public CodeQualityMetricStatus getStatus() {
        return status;
    }

    public void setStatus(CodeQualityMetricStatus status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

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
