package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineResponse {
    private String name;
    private ObjectId collectorItemId;
    private Map<PipelineStageType, List<PipelineResponseCommit>> stages = new HashMap<>();

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

    public Map<PipelineStageType, List<PipelineResponseCommit>> getStages() {
        return stages;
    }

    public void setStages(Map<PipelineStageType, List<PipelineResponseCommit>> stages) {
        this.stages = stages;
    }

    public void addToStage(PipelineStageType stage, PipelineResponseCommit pipelineCommit) {
        List<PipelineResponseCommit> pipelineStage = stages.get(stage);
        if (pipelineStage == null) {
            pipelineStage = new ArrayList<>();
            stages.put(stage, pipelineStage);
        }
        pipelineStage.add(pipelineCommit);
    }
}
