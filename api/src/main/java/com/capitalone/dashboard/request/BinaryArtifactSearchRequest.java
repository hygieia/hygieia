package com.capitalone.dashboard.request;

public class BinaryArtifactSearchRequest {

    private String artifactName;

    private String artifactGroup;

    private String artifactVersion;
    
    private String buildUrl;


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
    
    public String getBuildUrl() {
    	return buildUrl;
    }
    
    public void setBuildUrl(String buildUrl) {
    	this.buildUrl = buildUrl;
    }
}
