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
    /** {@link CollectorItem} teamdashboard collector item id */
    private ObjectId collectorItemId;

    /** Map of environment name and stage object*/
    private Map<String, EnvironmentStage> stages = new HashMap<>();

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public Map<String, EnvironmentStage> getStages() {
        return stages;
    }

    public void setStages(Map<String, EnvironmentStage> stages) {
        this.stages = stages;
    }

    public void addCommit(String stage, PipelineCommit commit){
        if(!this.getStages().containsKey(stage)){
            this.getStages().put(stage, new EnvironmentStage());
        }
        this.getStages().get(stage).getCommits().add(commit);
    }

}
