package com.capitalone.dashboard.model;

import com.capitalone.dashboard.util.PipelineUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

/**
 * Document containing the details of a Pipeline for a TeamDashboardCollectorItem
 */
@Document(collection="pipelines")
public class Pipeline extends BaseModel{
    /**
     * {@link CollectorItem} teamdashboard collector item id
     * */
    private ObjectId collectorItemId;

    /**
     * Map of environment name and stage object
     * */
    private Map<String, EnvironmentStage> stages = new HashMap<>();

    /**
     * not including this in the map above because the enum allows us to
     * use ordinals to iterate through pipeline progression
     * */
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

    /**
     * Adds a commit to a given stage.  Will create a new stage if it doesn't exist.
     * @param stage
     * @param commit
     */
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

    /**
     * Gets all pipeline commits as a map of scmrevision number, pipelinecommit for a given stage.
     *
     * uses a case insensitive map of the pipeline stage names due tot he way the UI currently stores mapped environments
     * with lowercase for the stage type and the canonical name
     * @param stage
     * @return
     */
    public Map<String, PipelineCommit> getCommitsByStage(String stage){

        Map<String, EnvironmentStage> caseInsensitiveMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        caseInsensitiveMap.putAll(stages);
        EnvironmentStage pipelineStage = caseInsensitiveMap.get(stage);
        if(pipelineStage == null) {
            return new HashMap<>();
        }

        Map<String, PipelineCommit> commitsByStage = PipelineUtils.commitSetToMap(pipelineStage.getCommits());
        return commitsByStage;
    }
}
