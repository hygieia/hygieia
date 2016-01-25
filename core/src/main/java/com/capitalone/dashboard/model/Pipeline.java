package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Document containing the details of a Pipeline for a TeamDashboardCollectorItem
 */
@Document(collection="pipelines")
public class Pipeline extends BaseModel{
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
}
