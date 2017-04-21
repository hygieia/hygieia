package com.capitalone.dashboard.model;

import com.capitalone.dashboard.util.PipelineUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
     * Map of environment names to EnvironmentStage objects. "Build" and "Commit" are
     * treated as environments.
     * */
    @Field("stages") 
    private Map<String, EnvironmentStage> environmentStageMap = new HashMap<>();

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

    public Map<String, EnvironmentStage> getEnvironmentStageMap() {
        return environmentStageMap;
    }

    public void setEnvironmentStageMap(Map<String, EnvironmentStage> environmentStageMap) {
        this.environmentStageMap = environmentStageMap;
    }

    /**
     * Adds a commit to a given environment.  Will create a new stage if it doesn't exist.
     * @param environmentName the environment name including the pseudo environments "Build" and "Commit"
     * @param commit
     */
    public void addCommit(String environmentName, PipelineCommit commit){
        if(!this.getEnvironmentStageMap().containsKey(environmentName)){
            this.getEnvironmentStageMap().put(environmentName, new EnvironmentStage());
        }
        this.getEnvironmentStageMap().get(environmentName).getCommits().add(commit);
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
     * @param environmentName the environment name including the pseudo environments "Build" and "Commit"
     * @return
     */
    public Map<String, PipelineCommit> getCommitsByEnvironmentName(String environmentName){

        Map<String, EnvironmentStage> caseInsensitiveMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        caseInsensitiveMap.putAll(environmentStageMap);
        EnvironmentStage pipelineStage = caseInsensitiveMap.get(environmentName);
        if(pipelineStage == null) {
            return new HashMap<>();
        }

        Map<String, PipelineCommit> commitsByStage = PipelineUtils.commitSetToMap(pipelineStage.getCommits());
        return commitsByStage;
    }
}
