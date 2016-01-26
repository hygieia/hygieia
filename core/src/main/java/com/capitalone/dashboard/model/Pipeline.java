package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

/**
 * Document containing the details of a Pipeline for a TeamDashboardCollectorItem
 */
@Document(collection="pipelines")
public class Pipeline extends BaseModel{
    private String name;
    private ObjectId collectorItemId;
    private Set<PipelineCommit> commits = new HashSet<>();

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

    public Set<PipelineCommit> getCommits() {
        return commits;
    }

    public void setCommits(Set<PipelineCommit> commits) {
        this.commits = commits;
    }
}
