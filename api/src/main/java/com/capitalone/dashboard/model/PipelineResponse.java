package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

public class PipelineResponse {
    private String name;
    private ObjectId collectorItemId;
    private Map<PipelineStageType, PipelineStage> stages = new HashMap<>();

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

    public Map<PipelineStageType, PipelineStage> getStages() {
        return stages;
    }

    public void setStages(Map<PipelineStageType, PipelineStage> stages) {
        this.stages = stages;
    }

    public void addToStage(PipelineStageType stage, PipelineCommit pipelineCommit) {
        PipelineStage pipelineStage = stages.get(stage);
        if (pipelineStage == null) {
            pipelineStage = new PipelineStage();
            stages.put(stage, pipelineStage);
        }
        pipelineStage.getCommits().add(pipelineCommit);
    }
}
