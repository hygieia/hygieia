package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.SCM;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class BinaryArtifactCreateRequest {

    @NotNull
    private String artifactName;
    @NotNull
    String canonicalName;
    @NotNull
    private String artifactGroup;
    @NotNull
    private String artifactVersion;
    @NotNull
    private String buildId;


    private long timestamp;

    private List<SCM> sourceChangeSet = new ArrayList<>();

    public String getArtifactName() {
        return artifactName;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public String getArtifactGroup() {
        return artifactGroup;
    }

    public void setArtifactGroup(String artifactGroup) {
        this.artifactGroup = artifactGroup;
    }

    public String getArtifactVersion() {
        return artifactVersion;
    }

    public void setArtifactVersion(String artifactVersion) {
        this.artifactVersion = artifactVersion;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<SCM> getSourceChangeSet() {
        return sourceChangeSet;
    }
}
