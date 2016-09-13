package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BinaryArtifactCreateRequest {

    @NotNull
    private String artifactName;
    @NotNull
    String canonicalName;
    @NotNull
    private String artifactGroup;
    @NotNull
    private String artifactVersion;
    
    private long timestamp;
    
    // See usage of metadata in BinaryArtifactServiceImpl for defined property names
    private Map<String, Object> metadata = new HashMap<>();


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
    
    public Map<String, Object> getMetadata() {
    	return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
    	this.metadata = metadata;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
