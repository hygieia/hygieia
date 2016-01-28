package com.capitalone.dashboard.model;

/**
 * Defines the type of {@link EnvironmentStage}
 */
public enum PipelineStageType {
    Commit, Build, Dev, QA, Int, Perf, Prod;

    public static PipelineStageType fromString(String value) {
        for (PipelineStageType type : values()) {
            if (type.toString().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid PipelineStageType.");
    }
}
