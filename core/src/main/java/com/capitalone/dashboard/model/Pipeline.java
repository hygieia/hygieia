package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Document containing the details of a Pipeline for a TeamDashboardCollectorItem
 */
@Document(collection="pipelines")
public class Pipeline extends BaseModel{
    /** {@link CollectorItem} teamdashboard collector item id */
    private ObjectId collectorItemId;

    /** Map of environment name and stage object*/
    private Map<String, EnvironmentStage> stages = new HashMap<>();

    /**not including this in the map above because the enum allows us to
     * use ordinals to iterate through pipeline progression*/
    private Set<Build> failedBuilds = new HashSet<>();

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

    public Set<Build> getFailedBuilds() {
        return failedBuilds;
    }

    public void setFailedBuilds(Set<Build> failedBuilds) {
        this.failedBuilds = failedBuilds;
    }

    public void addFailedBuild(Build failedBuild){
        this.getFailedBuilds().add(failedBuild);
    }
}
