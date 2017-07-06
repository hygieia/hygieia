package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.SCM;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A request to create a BinaryArtifact.
 *
 */
public class BinaryArtifactCreateRequest {

    @NotNull
    private String artifactName;
    @NotNull
    String canonicalName;
    @NotNull
    private String artifactGroup;
    @NotNull
    private String artifactVersion;
    private String artifactModule;
    private String artifactClassifier;
    private String artifactExtension;
    
    // May be null if comes in from rest call outside of jenkins
    private String buildId;
    
    private long timestamp;
    
    // Used by the jenkins plugin
	// May be null if comes in from rest call outside of jenkins
    private List<SCM> sourceChangeSet = new ArrayList<>();
    
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
    
    public Map<String, Object> getMetadata() {
    	return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
    	this.metadata = metadata;
    }

	public String getArtifactModule() {
		return artifactModule;
	}

	public void setArtifactModule(String artifactModule) {
		this.artifactModule = artifactModule;
	}

	public String getArtifactClassifier() {
		return artifactClassifier;
	}

	public void setArtifactClassifier(String artifactClassifier) {
		this.artifactClassifier = artifactClassifier;
	}

	public String getArtifactExtension() {
		return artifactExtension;
	}

	public void setArtifactExtension(String artifactExtension) {
		this.artifactExtension = artifactExtension;
	}
}
