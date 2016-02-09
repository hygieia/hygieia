package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineResponse {
    private String name;
    private ObjectId collectorItemId;
    private List<PipelineStageType> unmappedStages;
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

    public List<PipelineStageType> getUnmappedStages() {
        return unmappedStages;
    }

    public void setUnmappedStages(List<PipelineStageType> unmappedStages) {
        this.unmappedStages = unmappedStages;
    }

    public void addToStage(PipelineStageType stage, PipelineResponseCommit pipelineCommit) {
        List<PipelineResponseCommit> pipelineStage = stages.get(stage);
        if (pipelineStage == null) {
            pipelineStage = new ArrayList<>();
            stages.put(stage, pipelineStage);
        }
        pipelineStage.add(pipelineCommit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PipelineResponse that = (PipelineResponse) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (!collectorItemId.equals(that.collectorItemId)) return false;
        return stages.equals(that.stages);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + collectorItemId.hashCode();
        result = 31 * result + stages.hashCode();
        return result;
    }
}
