package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of a Continuous Integration build execution. Typically produces binary artifacts.
 * Often triggered by one or more SCM commits.
 *
 * Possible collectors:
 *  Hudson (in scope)
 *  Team City
 *  TFS
 *  Go
 *  Bamboo
 *  TravisCI
 *
 */
@Document(collection="builds")
public class Build extends BaseModel {
    private ObjectId collectorItemId;
    private long timestamp;

    private String number;
    private String buildUrl;
    private long startTime;
    private long endTime;
    private long duration;
    private BuildStatus buildStatus;
    private String startedBy;
    private String log;
    private List<RepoBranch> codeRepos = new ArrayList<>();
    private List<SCM> sourceChangeSet = new ArrayList<>();

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public void setSourceChangeSet(List<SCM> sourceChangeSet) {
        this.sourceChangeSet = sourceChangeSet;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBuildUrl() {
        return buildUrl;
    }

    public void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public BuildStatus getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(BuildStatus buildStatus) {
        this.buildStatus = buildStatus;
    }

    public String getStartedBy() {
        return startedBy;
    }

    public void setStartedBy(String startedBy) {
        this.startedBy = startedBy;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public List<SCM> getSourceChangeSet() {
        return sourceChangeSet;
    }

    public void addSourceChangeSet(SCM scm) {
        getSourceChangeSet().add(scm);
    }

    public List<RepoBranch> getCodeRepos() {
        return codeRepos;
    }

    public void setCodeRepos(List<RepoBranch> codeRepos) {
        this.codeRepos = codeRepos;
    }

}
