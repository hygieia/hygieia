package com.capitalone.dashboard.request;

import org.bson.types.ObjectId;

public class BinaryArtifactSearchRequest {

    private String artifactName;

    private String artifactGroup;

    private String artifactVersion;
    
    private String artifactModule;
    
    private String artifactClassifier;
    
    private String artifactExtension;

    private ObjectId buildId;


    public String getArtifactName() {
        return artifactName;
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

    public ObjectId getBuildId() {
        return buildId;
    }

    public void setBuildId(ObjectId buildId) {
        this.buildId = buildId;
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
