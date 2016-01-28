package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

public class PipelineResponse {
    private String name;
    private ObjectId collectorItemId;
    private Map<PipelineStageType, EnvironmentStage> stages = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public Map<PipelineStageType, EnvironmentStage> getStages() {
        return stages;
    }

    public void setStages(Map<PipelineStageType, EnvironmentStage> stages) {
        this.stages = stages;
    }

    public void addToStage(PipelineStageType stage, PipelineCommit pipelineCommit) {
        EnvironmentStage pipelineStage = stages.get(stage);
        if (pipelineStage == null) {
            pipelineStage = new EnvironmentStage();
            stages.put(stage, pipelineStage);
        }
        pipelineStage.getCommits().add(pipelineCommit);
    }
}
