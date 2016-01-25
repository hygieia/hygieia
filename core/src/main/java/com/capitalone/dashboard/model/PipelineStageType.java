package com.capitalone.dashboard.model;

/**
 * Defines the type of {@link PipelineStage}
 */
public enum PipelineStageType {
    Commit, Build;

    public static PipelineStageType fromString(String value) {
        for (PipelineStageType type : values()) {
            if (type.toString().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid PipelineStageType.");
    }
}
